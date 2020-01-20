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
    public static final String S_MD5HASH = "md5hash";
    public static final String S_Index = "index";
    public static final String S_UUID = "uuid";
    private static final String[] removeEntries = { "longitude", "latitude" };
    private static Logger logger = LoggerFactory.getLogger(MappedRecordJson.class);

    public MappedRecordJson() {

    }

    public MappedRecordJson(final MappedRecord record) {

        super(new GsonBuilder().setPrettyPrinting().create().toJson(record));

        // per Jim's request to rename the index to uuid
        final Object uuid = this.get(ConfigurationHelper.FN_Index);
        this.remove(ConfigurationHelper.FN_Index);
        this.put(S_UUID, uuid);

        // per Jim's request to rename the 'title' to 'name'
        final Object name = this.get(ConfigurationHelper.FN_Title);
        this.remove(ConfigurationHelper.FN_Title);
        this.put("Name", name);

        // convert the description into key/value pair
        final Set<String> keys = record.getDescMap().keySet();
        for (final String key : keys) {
            this.put(key, record.getDescMap().get(key));
        }
        this.remove("descMap");
        this.remove(ConfigurationHelper.FN_Description);

        setWhere(record.getLatitude(), record.getLongitude());
        clearUp();

        // make sure there is no null value
        final Iterator<String> keyIter = this.keys();
        while (keyIter.hasNext()) {
            final String key = keyIter.next();
            final Object value = this.get(key);
            if (value instanceof String && value.toString().length() == 0) {
                this.put(key, " ");
            }
        }
    }

    private void clearUp() {

        for (final String key : removeEntries) {
            this.remove(key);
        }
    }

    private void setWhere(final String lat, final String lon) {

        final JSONObject where = new JSONObject();
        final JSONObject point = new JSONObject();
        point.put("pos", lat + " " + lon);
        where.put("Point", point);
        this.put("where", where);
    }

    public Map.Entry getMapEntry() {

        return new AbstractMap.SimpleImmutableEntry(getCreator(), getPrimaryKey());
    }

    // DynamoDB primary index is the title or the configuration name
    public String getCreator() {

        return (String) this.get(S_Creator);
    }

    public String getMD5Hash() {

        return (String) this.get(S_MD5HASH);
    }

    // DynamoDB secondary index is the index field of the record
    public String getPrimaryKey() {

        return (String) this.get(S_UUID);
    }

    public String getLatitude() {

        return (String) this.get(ConfigurationHelper.FN_Latitude);
    }

    public String getLongitude() {

        return (String) this.get(ConfigurationHelper.FN_Longitude);
    }
}
