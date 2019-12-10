package com.spotonresponse.adapter.model;

import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MappedRecordJson extends JSONObject {

    private static final String S_Creator = "creator";
    private static final String S_MD5HASH = "md5hash";
    private static final String[] removeEntries = { "longitude", "latitude" };
    private static Logger logger = LoggerFactory.getLogger(MappedRecordJson.class);

    public MappedRecordJson() {

    }

    public MappedRecordJson(MappedRecord record) {

        super(new GsonBuilder().setPrettyPrinting().create().toJson(record));

        // per Jim's request to rename the index to uuid
        Object uuid = this.get(ConfigurationHelper.FN_Index);
        this.remove(ConfigurationHelper.FN_Index);
        this.put("uuid", uuid);

        // convert the description into key/value pair
        Set<String> keys = record.getDescMap().keySet();
        for (String key : keys) {
            this.put(key, record.getDescMap().get(key));
        }
        this.remove("descMap");
        this.remove(ConfigurationHelper.FN_Description);

        setWhere(record.getLatitude(), record.getLongitude());
        clearUp();

        // make sure there is no null value
        Iterator<String> keyIter = this.keys();
        while (keyIter.hasNext()) {
            String key = keyIter.next();
            Object value = this.get(key);
            if (value instanceof String && value.toString().length() == 0) {
                this.put(key, " ");
            }
        }
    }

    private void clearUp() {

        for (String key : removeEntries) {
            this.remove(key);
        }
    }

    private void setWhere(String lat, String lon) {

        JSONObject where = new JSONObject();
        JSONObject point = new JSONObject();
        point.put("pos", lat + " " + lon);
        where.put("Point", point);
        this.put("where", where);
    }

    public Map.Entry getMapEntry() {

        return new AbstractMap.SimpleImmutableEntry(getCreator(), getPrimaryKey());
    }

    public String getCreator() {

        return (String) this.get(S_Creator);
    }

    public String getPrimaryKey() {

        return (String) this.get(S_MD5HASH);
    }

    public String getLatitude() {

        return (String) this.get(ConfigurationHelper.FN_Latitude);
    }

    public String getLongitude() {

        return (String) this.get(ConfigurationHelper.FN_Longitude);
    }
}
