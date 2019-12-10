package com.spotonresponse.adapter.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class XCoreJsonTest {

    public static final String S_Features = "features";
    public static final String S_Properties = "properties";
    public static final String S_Geometry = "geometry";
    public static final String S_coordinates = "coordinates";

    @Test
    public void testXCoreJsonParse() {

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("json/xcore.json").getFile());
            System.out.println("File Path: " + file.getAbsolutePath());
            String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray features = (JSONArray) jsonObject.get(S_Features);
            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = (JSONObject) features.get(i);
                Map<String, String> record = new HashMap<String, String>();
                JSONArray properties = (JSONArray) feature.get(S_Properties);
                JSONObject geo = (JSONObject) feature.get(S_Geometry);
                String lonLat = (String) geo.get(S_coordinates);
                System.out.println("Record: [\n" + features.get(i) + "\n]");
            }
        } catch (Exception e) {
            // TODO
        }
    }
}
