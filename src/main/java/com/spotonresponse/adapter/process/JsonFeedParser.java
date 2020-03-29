package com.spotonresponse.adapter.process;

import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.model.MappedRecordJson;
import com.spotonresponse.adapter.model.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonFeedParser implements Parser {

    public static final String S_Features = "features";
    public static final String S_Properties = "properties";
    public static final String S_Geometry = "geometry";
    public static final String S_coordinates = "coordinates";

    private static Logger logger = LoggerFactory.getLogger(JsonFeedParser.class);

    private TheCSVParser parser = null;
    private boolean isAutoClose = false;

    public JsonFeedParser(Configuration configuration, String contentText) {

        logger.info("JsonFeedParser: Configuration: {}", configuration.getId());

        isAutoClose = configuration.isAutoClose();

        JSONObject jsonObject = new JSONObject(contentText);

        // features is array of records
        JSONArray features = (JSONArray) jsonObject.get(S_Features);
        /*
         features: [
            {
                properties -> row of data
                geometry -> latitude/longitude
            }
         ]
         */
        List<Map<String, String>> listOfRow = new ArrayList<Map<String, String>>();
        for (int i = 0; i < features.length(); i++) {

            JSONObject feature = (JSONObject) features.get(i);

            // convert properties into a row of data
            JSONObject properties = (JSONObject) feature.get(S_Properties);
            Map<String, String> rowData = Util.convertKeyValue(Util.toMap(properties));

            // convert the geometry to latitude and longitude
            JSONObject geo = (JSONObject) feature.get(S_Geometry);
            JSONArray lonLat = (JSONArray) geo.get(S_coordinates);
            rowData.put("Longitude", String.valueOf(lonLat.get(0)));
            rowData.put("Latitude", String.valueOf(lonLat.get(1)));
            listOfRow.add(rowData);
        }
        parser = new TheCSVParser(configuration, listOfRow);
    }

    public List<MappedRecordJson> getJsonRecordList() {

        return parser.getJsonRecordList();
    }

    @Override
    public boolean isAutoClose() {

        return this.isAutoClose;
    }

    @Override
    public String getId() {

        return parser.getId();
    }

    @Override
    public Map<String, MappedRecordJson> getJsonRecordMap() {

        return parser.getJsonRecordMap();
    }

    @Override
    public Set<String> getNotMatchedKeySet() {

        return parser.getNotMatchedKeySet();
    }
}
