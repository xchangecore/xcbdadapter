package com.spotonresponse.adapter.process;
import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.model.MappedRecordJson;
import com.spotonresponse.adapter.model.Util;
import com.spotonresponse.adapter.process.Parser;
import com.spotonresponse.adapter.process.TheCSVParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFeedParser implements Parser {
    public static final String S_Features = "features";

    public static final String S_Properties = "properties";

    public static final String S_Geometry = "geometry";

    public static final String S_coordinates = "coordinates";

    private static Logger logger = LoggerFactory.getLogger(com.spotonresponse.adapter.process.JsonFeedParser.class);

    private TheCSVParser parser = null;

    private boolean isAutoClose = false;

    public JsonFeedParser(Configuration configuration, String contentText) {
        logger.info("JsonFeedParser: Configuration: {}", configuration.getId());
        this.isAutoClose = configuration.isAutoClose();
        JSONObject jsonObject = new JSONObject(contentText);
        JSONArray features = (JSONArray)jsonObject.get("features");
        List<Map<String, String>> listOfRow = new ArrayList<>();
        for (int i = 0; i < features.length(); i++) {
            JSONObject feature = (JSONObject)features.get(i);
            JSONObject properties = (JSONObject)feature.get("properties");
            Map<String, String> rowData = Util.convertKeyValue(Util.toMap(properties));
            JSONObject geo = (JSONObject)feature.get("geometry");
            JSONArray lonLat = (JSONArray)geo.get("coordinates");
            rowData.put("Longitude", String.valueOf(lonLat.get(0)));
            rowData.put("Latitude", String.valueOf(lonLat.get(1)));
            listOfRow.add(rowData);
        }
        this.parser = new TheCSVParser(configuration, listOfRow);
    }

    public List<MappedRecordJson> getJsonRecordList() {
        return this.parser.getJsonRecordList();
    }

    public boolean isAutoClose() {
        return this.isAutoClose;
    }

    public String getId() {
        return this.parser.getId();
    }

    public Map<String, MappedRecordJson> getJsonRecordMap() {
        return this.parser.getJsonRecordMap();
    }

    public Set<String> getNotMatchedKeySet() {
        return this.parser.getNotMatchedKeySet();
    }
}