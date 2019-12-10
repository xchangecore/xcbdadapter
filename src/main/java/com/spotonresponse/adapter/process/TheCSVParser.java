package com.spotonresponse.adapter.process;

import com.spotonresponse.adapter.model.ConfigurationHelper;
import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.model.MappedRecord;
import com.spotonresponse.adapter.model.MappedRecordJson;
import com.spotonresponse.adapter.model.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This is the base CSV parser, it will take Configuration model and rows of data which is Map<String, String>
 * It will return MappedRecord list and MappedRecordJson for NoSQL inject 
*/
public class TheCSVParser {

    public static final String S_TokenSeparator = ":";
    public static final String PatternPrefix = "(?i:.*";
    public static final String PatternPostfix = ".*)";
    public static final double Pi = 3.14159;
    public static final double Radius = 6378137.0;

    private static Logger logger = LoggerFactory.getLogger(CSVParser.class);

    private List<MappedRecordJson> mappedRecordJsonList = null;
    private final List<MappedRecord> mappedRecordList = new ArrayList<MappedRecord>();

    private Configuration configuration;
    private Map<String, String> mappingColumns;

    public TheCSVParser(Configuration configuration, List<Map<String, String>> rows) {

        this.configuration = configuration;
        mappingColumns = ConfigurationHelper.getMappingColumns(configuration.getMappingColumns());

        List<MappedRecord> recordList = new ArrayList<MappedRecord>();
        for (int i = 0; i < rows.size(); i++) {
            MappedRecord record = toRecord(rows.get(i));
            if (record != null)
                recordList.add(record);
        }

        List<MappedRecord> mismatched = new ArrayList<MappedRecord>();
        // to apply the distance filter/distance
        // - calculate the bounding box
        // - find out whether the object is within the bounding box
        if (configuration.getDistance() != null && (configuration.getDistanceFilterText() == null
                || configuration.getDistanceFilterText().equalsIgnoreCase(configuration.getFilterText()))) {
            Double[][] boundingBox = calculateBoundingBox(recordList, configuration.getDistance());
            for (MappedRecord record : recordList) {
                if (Util.IsInsideBoundingBox(boundingBox, record.getLatitude(), record.getLongitude()) == false) {
                    logger.trace("{} is outside bounding box", record);
                    mismatched.add(record);
                }
            }
            if (mismatched.size() > 0) {
                recordList.removeAll(mismatched);
            }
        }

        Map<String, MappedRecord> recordMap = new HashMap<>();
        for (MappedRecord record : recordList) {
            recordMap.put(record.getMd5hash(), record);
        }
        for (MappedRecord r : recordMap.values()) {
            mappedRecordList.add(r);
        }
    }

    public String getId() {
        return configuration.getId();
    }

    public List<MappedRecord> getRecordList() {
        return mappedRecordList;
    }

    public List<MappedRecordJson> getJsonRecordList() {

        if (mappedRecordJsonList == null) {
            mappedRecordJsonList = new ArrayList<MappedRecordJson>();
            for (MappedRecord record : mappedRecordList) {
                mappedRecordJsonList.add(new MappedRecordJson(record));
            }
        }
        return mappedRecordJsonList;
    }

    //
    // parse the row into the MappedRecordJson
    //
    private MappedRecord toRecord(Map<String, String> row) {

        boolean isFullDescription = configuration.isFullDescription();

        MappedRecord record = new MappedRecord();
        record.setCreator(configuration.getId());
        record.setLastUpdated(new Date());
        setHostAndPath(record, configuration.getJson_ds() == null ? configuration.getUri() + Configuration.S_UrlPostfix
                : configuration.getJson_ds());

        Set<String> columnNames = ConfigurationHelper.getMap(configuration).keySet();
        for (String columnName : columnNames) {
            StringBuffer sb = new StringBuffer();
            List<String> columns = ConfigurationHelper.getMap(configuration).get(columnName);
            if (columnName.equalsIgnoreCase(ConfigurationHelper.FN_Description) && !isFullDescription) {
                Set<String> emptySet = new HashSet<String>();
                for (String column : columns) {
                    String newKeyName = getMappedName(column);
                    String value = row.get(column);
                    if (value != null) {
                        record.getDescMap().put(newKeyName, value);
                    } else {
                        emptySet.add(newKeyName);
                    }
                }
                if (emptySet.size() > 0) {
                    for (String key : emptySet) {
                        record.getDescMap().put(key, "N/A");
                    }
                }
            } else {
                int isFirstColumn = 0;
                for (String column : columns) {
                    if (isFirstColumn++ > 0) {
                        sb.append(S_TokenSeparator);
                    }
                    sb.append(row.get(column));
                }
            }
            record.put(columnName, sb.toString().trim());
        }

        // check whether filter match the filter text
        String filter = record.getFilter();
        boolean isMatched = isMatchFilter(filter);
        logger.trace("Filter: [{}] Matched: [{}]", filter, isMatched ? "YES" : "NO");

        // if the filter mis-match then we don't need to continue
        if (!isMatched) {
            return null;
        }

        // fill the content with every columns
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        Collection<String> values = row.values();
        int isFirstColumn = 0;
        for (Object value : values) {
            if (isFirstColumn++ > 0) {
                sb.append(S_TokenSeparator);
            }
            sb.append(value);
        }
        sb.append("]");
        record.setContent(sb.toString());

        if (isFullDescription) {
            columnNames = row.keySet();
            for (String key : columnNames) {
                if (key.length() == 0)
                    continue;
                record.getDescMap().put(getMappedName(key), row.get(key));
            }
        }

        // TO DO to perform the prefix, suffix, distance, filter, ...
        // category.fix, category.prefix, category.suffix
        if (configuration.getCategoryFixed() != null) {
            record.setCategory(configuration.getCategoryFixed());
        } else {
            if (configuration.getCategoryPrefix() != null || configuration.getCategorySuffix() != null) {
                String category = record.getCategory();
                if (configuration.getCategoryPrefix() != null) {
                    category = configuration.getCategoryPrefix() + category;
                }
                if (configuration.getCategorySuffix() != null) {
                    category = category + configuration.getCategorySuffix();
                }
                record.setCategory(category);
            }
        }

        // title.prefix, title.suffix
        if (configuration.getTitlePrefix() != null || configuration.getTitleSuffix() != null) {
            String title = record.getTitle();
            if (configuration.getTitlePrefix() != null) {
                title = configuration.getTitlePrefix() + title;
            }
            if (configuration.getTitleSuffix() != null) {
                title = title + configuration.getTitleSuffix();
            }
            record.setTitle(title);
        }

        // set MD5Hash
        record.setMd5hash(Util.ToHash(record.getIndex()));

        return record;
    }

    private void setHostAndPath(MappedRecord record, String url) {
        try {
            URL aURL = new URL(url);
            record.setSourceHost(aURL.getHost());
            record.setSourceURL(aURL.getPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private boolean isMatchFilter(String filter) {

        if (filter == null || filter.length() == 0) {
            return false;
        }

        boolean negativeExpression = configuration.getFilterText().startsWith("!");
        String filterText = negativeExpression ? configuration.getFilterText().substring(1)
                : configuration.getFilterText();
        String pattern = PatternPrefix + filterText + PatternPostfix;
        logger.trace("Filter Pattern: " + pattern);
        boolean isMatched = filter.matches(pattern);
        return isMatched && negativeExpression == false || isMatched == false && negativeExpression == true;
    }

    private String getMappedName(String name) {

        if (mappingColumns == null) {
            return name;
        }

        String mappedName = mappingColumns.get(name);
        return mappedName != null ? mappedName : name;
    }

    private Double[][] calculateBoundingBox(Collection<MappedRecord> records, String distanceText) {

        double distance = new Double(distanceText).doubleValue();

        double south = 0.0;
        double north = 0.0;
        double west = 0.0;
        double east = 0.0;
        for (MappedRecord record : records) {
            double lat = Double.parseDouble(record.getLatitude());
            north = lat > 0 ? lat > north ? lat : north : lat < north ? lat : north;
            south = lat > 0 ? lat < south ? lat : south : lat > south ? lat : south;
            double lon = Double.parseDouble(record.getLongitude());
            west = lon > 0 ? lon < west ? lon : west : lon > west ? west : lon;
            east = lon > 0 ? lon > east ? lon : east : lon < east ? east : lon;
            if (south == 0) {
                south = north;
            }
            if (north == 0) {
                north = south;
            }
            if (east == 0) {
                east = west;
            }
            if (west == 0) {
                west = east;
            }
        }

        /*
         * Earth’s radius, sphere R=6378137 offsets in meters dn = 100 de = 100
         * Coordinate offsets in radians dLat = dn/R dLon =de/(R*Cos(Pi*lat/180))
         * OffsetPosition, decimal degrees latO = lat + dLat * 180/Pi lonO = lon + dLon
         * * 180/Pi
         */
        double d = distance * 1000.0;
        double deltaLat = d / Radius * 180 / Pi;
        north += deltaLat * (north > 0 ? 1 : -1);
        south -= deltaLat * (south > 0 ? 1 : -1);
        double northDelta = d / (Radius * Math.cos(Pi * north / 180.0)) * 180.0 / Pi;
        double northWestLon = west - northDelta;
        double northEastLon = east + northDelta;
        double southDelta = d / (Radius * Math.cos(Pi * south / 180.0)) * 180.0 / Pi;
        double southWestLon = west - southDelta;
        double southEastLon = east + southDelta;
        Double[][] boundingBox = new Double[5][2];
        boundingBox[0][0] = northWestLon;
        boundingBox[0][1] = north;
        boundingBox[1][0] = northEastLon;
        boundingBox[1][1] = north;
        boundingBox[2][0] = southEastLon;
        boundingBox[2][1] = south;
        boundingBox[3][0] = southWestLon;
        boundingBox[3][1] = south;
        boundingBox[4][0] = northWestLon;
        boundingBox[4][1] = north;

        return boundingBox;
    }
}
