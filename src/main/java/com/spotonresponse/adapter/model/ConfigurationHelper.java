package com.spotonresponse.adapter.model;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationHelper {
    public static final String FN_Configuration_Start = "configuration:start";
    public static final String FN_Configuration_End = "configuration:end";
    public static final String FN_Latitude = "latitude";
    public static final String FN_Longitude = "longitude";
    public static final String FN_Title = "title";
    public static final String FN_TitlePrefix = "title.prefix";
    public static final String FN_TitleSuffix = "title.suffix";
    public static final String FN_Category = "category";
    public static final String FN_CategoryPrefix = "category.prefix";
    public static final String FN_CategorySuffix = "category.suffix";
    public static final String FN_CategoryFixed = "category.fixed";
    public static final String FN_FilterName = "filter";
    public static final String FN_FilterText = "filter.text";
    public static final String FN_Distance = "distance";
    public static final String FN_DistanceFilterText = "distance.filter.text";
    public static final String FN_Index = "index";
    public static final String FN_Content = "content";
    public static final String FN_Description = "description";
    public static final String FN_MappingColumns = "mapping.columns";
    public static final String FN_FullDescription = "full.description";
    public static final String FN_AutoClose = "auto.close";
    public static final String FN_URLHost = "url.host";
    public static final String FN_Username = "url.username";
    public static final String FN_Password = "url.password";
    public static final String FN_RedirectUrl = "url.redirectUrl";
    public static final String urlPostfix = "/core/ws/services";
    public static final String S_Dot = "\\.";
    public static final String FN_JsonDataSource = "json_ds";
    public static final String S_UrlHost = "/xchangecore/";

    public static final String[] DefinedColumnNames = new String[] { FN_Title, FN_Category, FN_Latitude, FN_Longitude,
            FN_FilterName, FN_Index, FN_Description, FN_Content, };

    public final static Logger logger = LoggerFactory.getLogger(ConfigurationHelper.class);

    public final static boolean isValid(Configuration configuration) {

        return configuration.getLatitude() != null && configuration.getLongitude() != null
                && configuration.getFilter() != null && configuration.getFilterText() != null;
    }

    public final static String getValue(Configuration configuration, String key) {

        if (key.equals(FN_Category)) {
            return configuration.getCategory();
        } else if (key.equalsIgnoreCase(FN_Description)) {
            return configuration.getDescription();
        } else if (key.equalsIgnoreCase(FN_FilterName)) {
            return configuration.getFilter();
        } else if (key.equalsIgnoreCase(FN_Latitude)) {
            return configuration.getLatitude();
        } else if (key.equalsIgnoreCase(FN_Longitude)) {
            return configuration.getLongitude();
        } else if (key.equalsIgnoreCase(FN_Title)) {
            return configuration.getTitle();
        } else if (key.equalsIgnoreCase(FN_Category)) {
            return configuration.getCategory();
        } else if (key.equalsIgnoreCase(FN_Distance)) {
            return configuration.getDistance();
        } else if (key.equalsIgnoreCase(FN_DistanceFilterText)) {
            return configuration.getDistanceFilterText();
        } else if (key.equalsIgnoreCase(FN_Index)) {
            return configuration.getIndex();
        } else {
            return null;
        }
    }

    public final static boolean getBooleanValue(String str) {

        String trimStr = str.toLowerCase().trim();
        return trimStr.equals("true") || trimStr.equals("1");
    }

    // Based on the required attributes, return the missing attributes
    public final static String getMissingAttributes(Configuration configuration) {

        StringBuffer sb = new StringBuffer();
        if (configuration.getFilterText() == null) {
            sb.append("filter.text, ");
        }
        if (configuration.getFilter() == null) {
            sb.append("filter, ");
        }
        if (configuration.getIndex() == null) {
            sb.append("index, ");
        }
        if (configuration.getLatitude() == null) {
            sb.append("latitude, ");
        }
        if (configuration.getLongitude() == null) {
            sb.append("longitude, ");
        }
        String errorMessage = sb.toString();
        errorMessage = errorMessage.substring(0, errorMessage.lastIndexOf(", "));

        return "Missing Attribute: [ " + errorMessage + " ]";
    }

    public final static Map<String, List<String>> getMap(Configuration configuration) {

        Map<String, List<String>> theMap = new HashMap<String, List<String>>();

        if (configuration.getTitle() != null) {
            theMap.put(FN_Title, Arrays.asList(configuration.getTitle().split(S_Dot, -1)));
        }
        if (configuration.getIndex() != null) {
            theMap.put(FN_Index, Arrays.asList(configuration.getIndex().split(S_Dot, -1)));
        }
        if (configuration.getFilter() != null) {
            theMap.put(FN_FilterName, Arrays.asList(configuration.getFilter().split(S_Dot, -1)));
        }
        if (configuration.getCategory() != null) {
            theMap.put(FN_Category, Arrays.asList(configuration.getCategory().split(S_Dot, -1)));
        }
        if (configuration.getDescription() != null) {
            theMap.put(FN_Description, Arrays.asList(configuration.getDescription().split(S_Dot, -1)));
        }
        if (configuration.getLatitude() != null) {
            theMap.put(FN_Latitude, Arrays.asList(configuration.getLatitude().split(S_Dot, -1)));
        }
        if (configuration.getLongitude() != null) {
            theMap.put(FN_Longitude, Arrays.asList(configuration.getLongitude().split(S_Dot, -1)));
        }
        return theMap;
    }

    public final static Map<String, String> getMappingColumns(String mappedColumns) {

        if (mappedColumns == null) {
            return null;
        }

        Map<String, String> map = new HashMap<String, String>();

        String[] pairs = mappedColumns.split("\\.", -1);
        for (String pair : pairs) {
            String[] tokens = pair.split(":", -1);
            if (tokens.length != 2) {
                logger.error("[" + pair + "] is not valid key and value");
                continue;
            }
            map.put(tokens[0], tokens[1]);
        }

        return map;
    }

}