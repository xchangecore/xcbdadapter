package com.spotonresponse.adapter.model;

import org.json.JSONArray;

public class QueryResult {

    private String configName;
    private int count;
    private JSONArray content;

    public QueryResult(String configName, int count, JSONArray content) {

        this.configName = configName;
        this.count = count;
        if (content == null) {
            this.content = new JSONArray();
        } else {
            this.content = content;
        }
    }

    public String getConfigName() {

        return configName;
    }

    public int getCount() {

        return count;
    }

    public JSONArray getContent() {

        return content;
    }
}
