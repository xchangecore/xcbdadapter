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

    public static final String S_MD5HASH = "md5hash";
    public static final String S_Title = "title";
    private static final Logger logger = LogManager.getLogger(DynamoDBRepository.class);

    private static DynamoDB dynamoDBClient = null;
    private static Table table = null;

    public DynamoDBRepository() { }

    public void init(String aws_access_key_id, String aws_secret_access_key, String amazon_endpoint, String amazon_region, String dynamoDBTableName) {

        logger.info("Init: dynamoDBRepository: ... start ...");
        if (aws_access_key_id == null || aws_secret_access_key == null || amazon_endpoint == null ||
            amazon_region == null || dynamoDBTableName == null) {
            return;
        }

        BasicAWSCredentials credentials = new BasicAWSCredentials(aws_access_key_id, aws_secret_access_key);

        try {
            AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(
                credentials)).withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazon_endpoint,
                                                                                                   amazon_region)).build();

            logger.debug("Setting up DynamoDB client: Region: [{}], Endpoint: [{}]", amazon_region, amazon_endpoint);
            dynamoDBClient = new DynamoDB(amazonDynamoDB);

            logger.debug("Setting up DynamoDB table: [{}]", dynamoDBTableName);
            table = dynamoDBClient.getTable(dynamoDBTableName);
        } catch (Throwable e) {
            logger.error("DynamoDBRepository.init: failed: " + e.getMessage());
        }
    }

    public JSONArray query(String title) {

        if (table == null) {
            return new JSONArray();
        }

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("title = :v_title").withValueMap(new ValueMap().with(
            ":v_title",
            title));

        ItemCollection<QueryOutcome> items = table.query(querySpec);
        logger.debug("query: {}, count: {}", title, items.getAccumulatedItemCount());
        Iterator iterator = items.iterator();
        JSONArray resultArray = new JSONArray();
        while (iterator.hasNext()) {
            Item item = (Item) iterator.next();
            resultArray.put(item.get("item"));
            logger.debug("query: Item: [{}]", item);
        }

        return resultArray;
    }

    public int createAllEntries(List<MappedRecordJson> recordList) {

        logger.info("createAllEntries: count#: {}", recordList.size());
        int count = 0;
        for (MappedRecordJson record : recordList) {
            if (createEntry(record)) {
                count++;
            }
        }
        logger.info("createAllEntries: created: {}", count);
        return count;
    }

    public int removeByCreator(String title) {

        return deleteAllEntries(title, queryHashList(title));
    }

    public List<String> queryHashList(String title) {

        logger.info("queryHashList: {}", title);
        List<String> hashList = new ArrayList<String>();

        if (table == null) {
            return hashList;
        }

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("title = :v_title").withValueMap(new ValueMap().with(
            ":v_title",
            title));

        ItemCollection<QueryOutcome> items = table.query(querySpec);
        Iterator iterator = items.iterator();
        while (iterator.hasNext()) {
            hashList.add((String) ((Item) iterator.next()).get(S_MD5HASH));
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

    public int deleteAllEntries(String creator, List<String> hashList) {

        logger.info("deleteAllEntries: {}, counts: {}", creator, hashList.size());

        if (hashList.size() == 0) {
            return 0;
        }

        int count = 0;
        for (String hash : hashList) {
            try {
                deleteEntry(new AbstractMap.SimpleImmutableEntry(creator, hash));
                count++;
            } catch (Exception e) {
                // TODO continue is right thing ???
            }
        }
        logger.info("deleteAllEntries: deleted: {} ", creator, count);
        return count;
    }

    public boolean deleteEntry(Map.Entry key) {

        if (table == null) {
            return false;
        }

        logger.debug("deleteEntry: Title: [{}] & MD5Hash: [{}]", key.getKey(), key);
        try {
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(new PrimaryKey(S_Title,
                                                                                               key.getKey(),
                                                                                               S_MD5HASH,
                                                                                               key.getValue()));
            table.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            logger.error("deleteEntry: Title: [{}] & MD5Hash: [{}]: Error: [{}]", key.getKey(), key, e.getMessage());
            return false;
        }
        return true;
    }

    public boolean updateEntry(MappedRecordJson item) {

        logger.debug("updateEntry: ... start ...");
        boolean isSuccess = this.deleteEntry(item.getMapEntry()) && this.createEntry(item);
        return isSuccess;
    }

    public boolean createEntry(MappedRecordJson item) {

        if (table == null) {
            return false;
        }

        logger.debug("createEntry: Creator: [{}] MD5HASH: [{}]", item.getCreator(), item.getPrimaryKey());
        try {
            table.putItem(new Item().withPrimaryKey(S_MD5HASH,
                                                    item.getPrimaryKey(),
                                                    S_Title,
                                                    item.getCreator()).withJSON("item", item.toString()));
        } catch (Exception e) {
            logger.error("createEntry: Creator: [{}] MD5HASH: [{}]\nItem: [{}]\n Error: [{}]",
                         item.getCreator(),
                         item.getPrimaryKey(),
                         item,
                         e.getMessage());
            return false;
        }
        return true;
    }
}
