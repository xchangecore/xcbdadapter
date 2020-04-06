package com.spotonresponse.adapter.repo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.google.gson.Gson;
import com.spotonresponse.adapter.model.MappedRecordJson;
import com.spotonresponse.adapter.model.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class DynamoDBRepository {

    public static final String S_Title = "title";
    private static final Logger logger = LogManager.getLogger(DynamoDBRepository.class);

    private DynamoDB dynamoDBClient = null;
    private static Table table = null;

    public DynamoDBRepository() {

    }

    public void init(AmazonDynamoDB dynamoDB, String dynamoDBTableName) {

        logger.info("Init: dynamoDBRepository: ... start ...");
        try {
            dynamoDBClient = new DynamoDB(dynamoDB);
            logger.debug("Setting up DynamoDB table: [{}]", dynamoDBTableName);
            table = dynamoDBClient.getTable(dynamoDBTableName);
        } catch (Throwable e) {
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

    private List<Map<String, Object>> querRecordList(final String title) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        final ItemCollection<QueryOutcome> items = query(title);
        if (items != null) {
            final Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                final Item item = (Item) iterator.next();
                list.add(item.getRawMap("item"));
            }
        }
        return list;
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

    public void updateEntries(final Set<String> notMatchedKeySet,
        final Map<String, MappedRecordJson> newMap,
        final boolean isAutoClose,
        final String title) {

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
                            logger.debug("updateEntries: autoClose: true, Index: " +
                                key +
                                " will be deleted since it's in core and it's not matched the filter");
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
        logger.debug("updateEntries: New: " +
            newList.size() +
            ", Update: " +
            updateList.size() +
            ", Delete: " +
            deleteList.size());
        if (newList.size() > 0) {
            createAllEntries(newList);
        }
        if (updateList.size() > 0) {
            for (final MappedRecordJson r : updateList) { updateEntry(r); }
        }
        if (deleteList.size() > 0) {
            for (final String key : deleteList) { deleteEntry(new AbstractMap.SimpleImmutableEntry(title, key)); }
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
            final DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(new PrimaryKey(S_Title,
                key.getKey(),
                MappedRecordJson.S_MD5HASH,
                key.getValue()));
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

    public boolean createEntry(final Map<String, Object> m) {

        if (table == null) { return false; }

        logger.debug("createEntry: ");
        String creator = (String) m.get(MappedRecordJson.S_Creator);
        String primaryKey = (String) m.get(MappedRecordJson.S_MD5HASH);
        String item = new Gson().toJson(m);

        try {
            table.putItem(new Item().withPrimaryKey(MappedRecordJson.S_MD5HASH, primaryKey, S_Title, creator)
                .withJSON("item", item));
        } catch (final Exception e) {
            logger.error("createEntry: Creator: [{}] MD5HASH: [{}]\nItem: [{}]\n Error: [{}]",
                creator,
                primaryKey,
                item,
                e.getMessage());
            return false;
        }

        return true;
    }

    public boolean createEntry(final MappedRecordJson item) {

        if (table == null) {
            return false;
        }

        logger.debug("createEntry: Creator: [{}] MD5HASH: [{}]", item.getCreator(), item.getPrimaryKey());
        try {
            table.putItem(new Item().withPrimaryKey(MappedRecordJson.S_MD5HASH,
                item.getPrimaryKey(),
                S_Title,
                item.getCreator())
                .withJSON("item", item.toString()));
        } catch (final Exception e) {
            logger.error("createEntry: Creator: [{}] MD5HASH: [{}]\nItem: [{}]\n Error: [{}]",
                item.getCreator(),
                item.getPrimaryKey(),
                item,
                e.getMessage());
            return false;
        }
        return true;
    }

    public void update(String title, Map<String, Object> updateMap) {

        // update each records
        String timestamp = new Date().toString();
        List<Map<String, Object>> list = querRecordList(title);
        for (Map<String, Object> item : list) {
            for (String key : updateMap.keySet()) {
                updateOrAdd(item, key, (String) updateMap.get(key), true);
            }
            updateOrAdd(item, "lastUpdated", timestamp, false);
            updateOrAdd(item,
                MappedRecordJson.S_MD5HASH,
                Util.ToHash((String) item.get(MappedRecordJson.S_MD5HASH)),
                false);
            String uuid = (String) item.get(MappedRecordJson.S_UUID);
            deleteEntry(new AbstractMap.SimpleImmutableEntry(title, uuid));
            createEntry(item);
        }
    }

    private String replaceInContent(String content, String oldValue, String newValue) {

        if (oldValue == null) {
            content += ":" + newValue;
        } else if (oldValue.equals(" ")) {
            content = content.replaceFirst("::", (String) ":" + newValue + ":");
        } else {
            content = content.replace(oldValue, (String) newValue);
        }
        return content;
    }

    private void updateOrAdd(Map<String, Object> m, String key, String value, boolean isContent) {

        // replace the value for the key
        String oldValue = (String) m.remove(key);
        m.put(key, value);

        if (isContent) {
            String content = (String) m.remove(MappedRecordJson.S_Content);
            m.put(MappedRecordJson.S_Content, replaceInContent(content, oldValue, value));
        }
    }
}
