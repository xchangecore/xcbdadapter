package com.spotonresponse.adapter.repo;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.spotonresponse.adapter.model.MappedRecordJson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import java.util.*;

public class DynamoDBRepository {

    public static final String S_Title = "title";
    private static final Logger logger = LogManager.getLogger(DynamoDBRepository.class);

    private static DynamoDB dynamoDBClient = null;
    private static Table table = null;

    public DynamoDBRepository() {
    }

    public void init(final String aws_access_key_id, final String aws_secret_access_key, final String amazon_endpoint,
            final String amazon_region, final String dynamoDBTableName) {

        logger.info("Init: dynamoDBRepository: ... start ...");
        if (aws_access_key_id == null || aws_secret_access_key == null || amazon_endpoint == null
                || amazon_region == null || dynamoDBTableName == null) {
            return;
        }

        final BasicAWSCredentials credentials = new BasicAWSCredentials(aws_access_key_id, aws_secret_access_key);

        try {
            final AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials)).withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(amazon_endpoint, amazon_region))
                    .build();

            logger.debug("Setting up DynamoDB client: Region: [{}], Endpoint: [{}]", amazon_region, amazon_endpoint);
            dynamoDBClient = new DynamoDB(amazonDynamoDB);

            logger.debug("Setting up DynamoDB table: [{}]", dynamoDBTableName);
            table = dynamoDBClient.getTable(dynamoDBTableName);
        } catch (final Throwable e) {
            logger.error("DynamoDBRepository.init: failed: " + e.getMessage());
        }
    }

    public JSONArray queryArray(final String title) {

        final JSONArray resultArray = new JSONArray();

        final ItemCollection<QueryOutcome> items = query(title);
        if (items != null) {
            logger.debug("query: {}, count: {}", title, items.getAccumulatedItemCount());
            final Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                final Item item = (Item) iterator.next();
                resultArray.put(item.get("item"));
                logger.debug("query: Item: [{}]", item);
            }
        }
        return resultArray;
    }

    private ItemCollection<QueryOutcome> query(final String title) {

        if (table == null) {
            return null;
        }

        final QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("title = :v_title")
                .withValueMap(new ValueMap().with(":v_title", title));

        final ItemCollection<QueryOutcome> items = table.query(querySpec);
        return items;
    }

    private Map<String, String> queryItems(final String title) {

        final Map<String, String> map = new HashMap<String, String>();

        final ItemCollection<QueryOutcome> items = query(title);
        if (items != null) {
            final Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                final Item item = (Item) iterator.next();
                final String jsonString = item.getJSON("item");
                final Map<String, Object> m = item.getRawMap("item");
                map.put((String) m.get(MappedRecordJson.S_UUID), (String) m.get(MappedRecordJson.S_MD5HASH));
            }
        }
        return map;
    }

    public void updateEntries(final Set<String> notMatchedKeySet, final Map<String, MappedRecordJson> newMap,
            final boolean isAutoClose, final String title) {

        final List<MappedRecordJson> newList = new ArrayList<MappedRecordJson>();
        final List<MappedRecordJson> updateList = new ArrayList<MappedRecordJson>();
        final List<String> deleteList = new ArrayList<String>();

        final Map<String, String> inCore = queryItems(title);
        if (inCore.size() > 0) {
            final Set<String> inCoreKeySet = inCore.keySet();
            for (final String key : inCoreKeySet) {
                if (newMap.containsKey(key)) {
                    // if the new record has the same index key as the DynamoDB table
                    final MappedRecordJson r = newMap.remove(key);
                    if (r.getMD5Hash().equals(inCore.get(key)) == false) {
                        updateList.add(r);
                    }
                } else {
                    if (isAutoClose) {
                        logger.debug("updateEntries: autoClose: true, delete Index: " + key);
                        deleteList.add(key);
                    } else {
                        if (notMatchedKeySet.contains(key)) {
                            logger.debug("updateEntries: autoClose: true, Index: " + key
                                    + " will be deleted since it's in core and it's not matched the filter");
                            deleteList.add(key);
                        }
                    }
                }
            }
        }
        final Set<String> newKeySet = newMap.keySet();
        for (final String k : newKeySet) {
            newList.add(newMap.get(k));
        }
        logger.debug("updateEntries: New: " + newList.size() + ", Update: " + updateList.size() + ", Delete: "
                + deleteList.size());
        if (newList.size() > 0) {
            createAllEntries(newList);
        }
        if (updateList.size() > 0) {
            for (final MappedRecordJson r : updateList)
                updateEntry(r);
        }
        if (deleteList.size() > 0) {
            for (final String key : deleteList)
                deleteEntry(new AbstractMap.SimpleImmutableEntry(title, key));
        }
    }

    public int createAllEntries(final List<MappedRecordJson> recordList) {

        logger.info("createAllEntries: count#: {}", recordList.size());
        int count = 0;
        for (final MappedRecordJson record : recordList) {
            if (createEntry(record)) {
                count++;
            }
        }
        logger.info("createAllEntries: created: {}", count);
        return count;
    }

    public int removeByCreator(final String title) {

        return deleteAllEntries(title, queryHashList(title));
    }

    public List<String> queryHashList(final String title) {

        logger.info("queryHashList: {}", title);
        final List<String> hashList = new ArrayList<String>();

        if (table == null) {
            return hashList;
        }

        final QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("title = :v_title")
                .withValueMap(new ValueMap().with(":v_title", title));

        final ItemCollection<QueryOutcome> items = table.query(querySpec);
        final Iterator iterator = items.iterator();
        while (iterator.hasNext()) {
            hashList.add((String) ((Item) iterator.next()).get(MappedRecordJson.S_MD5HASH));
        }

        logger.info("queryHashList: {}, count#: {}", title, hashList.size());
        return hashList;
    }

    public void shutown() {

        logger.debug("DynamoDB.shutdown: ... start ...");
        // Don't hold a connection open to the database
        if (dynamoDBClient != null) {
            dynamoDBClient.shutdown();
        }
    }

    public int deleteAllEntries(final String creator, final List<String> hashList) {

        logger.info("deleteAllEntries: {}, counts: {}", creator, hashList.size());

        if (hashList.size() == 0) {
            return 0;
        }

        int count = 0;
        for (final String hash : hashList) {
            try {
                deleteEntry(new AbstractMap.SimpleImmutableEntry(creator, hash));
                count++;
            } catch (final Exception e) {
                // TODO continue is right thing ???
            }
        }
        logger.info("deleteAllEntries: deleted: {} ", creator, count);
        return count;
    }

    public boolean deleteEntry(final Map.Entry key) {

        if (table == null) {
            return false;
        }

        logger.debug("deleteEntry: Title: [{}] & MD5Hash: [{}]", key.getKey(), key);
        try {
            final DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(
                    new PrimaryKey(S_Title, key.getKey(), MappedRecordJson.S_MD5HASH, key.getValue()));
            table.deleteItem(deleteItemSpec);
        } catch (final Exception e) {
            logger.error("deleteEntry: Title: [{}] & MD5Hash: [{}]: Error: [{}]", key.getKey(), key, e.getMessage());
            return false;
        }
        return true;
    }

    public boolean updateEntry(final MappedRecordJson item) {

        logger.debug("updateEntry: ... start ...");
        final boolean isSuccess = this.deleteEntry(item.getMapEntry()) && this.createEntry(item);
        return isSuccess;
    }

    public boolean createEntry(final MappedRecordJson item) {

        if (table == null) {
            return false;
        }

        logger.debug("createEntry: Creator: [{}] MD5HASH: [{}]", item.getCreator(), item.getPrimaryKey());
        try {
            table.putItem(new Item().withPrimaryKey(MappedRecordJson.S_MD5HASH, item.getPrimaryKey(),
                    S_Title, item.getCreator()).withJSON("item", item.toString()));
        } catch (final Exception e) {
            logger.error("createEntry: Creator: [{}] MD5HASH: [{}]\nItem: [{}]\n Error: [{}]", item.getCreator(),
                    item.getPrimaryKey(), item, e.getMessage());
            return false;
        }
        return true;
    }
}
