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
    private final List<MappedRecord> notMatchedList = new ArrayList<MappedRecord>();
    private final List<MappedRecord> mappedRecordList = new ArrayList<MappedRecord>();

    private final Configuration configuration;
    private final Map<String, String> mappingColumns;

    public TheCSVParser(final Configuration configuration, final List<Map<String, String>> rows) {

        this.configuration = configuration;
        mappingColumns = ConfigurationHelper.getMappingColumns(configuration.getMappingColumns());

        final List<MappedRecord> recordList = new ArrayList<MappedRecord>();
        for (int i = 0; i < rows.size(); i++) {
            final MappedRecord record = toRecord(rows.get(i));
            if (record != null)
                recordList.add(record);
        }

        // to apply the distance filter/distance
        // - calculate the bounding box
        // - find out whether the object is within the bounding box
        if (configuration.getDistance() != null && (configuration.getDistanceFilterText() == null
                || configuration.getDistanceFilterText().equalsIgnoreCase(configuration.getFilterText()))) {
            final Double[][] boundingBox = calculateBoundingBox(recordList, configuration.getDistance());
            for (final MappedRecord record : recordList) {
                if (Util.IsInsideBoundingBox(boundingBox, record.getLatitude(), record.getLongitude()) == false) {
                    logger.trace("{} is outside bounding box", record);
                    notMatchedList.add(record);
                }
            }
            // remove all not macthed in the recordList
            if (notMatchedList.size() > 0) {
                recordList.removeAll(notMatchedList);
            }
        }

        final Map<String, MappedRecord> recordMap = new HashMap<>();
        for (final MappedRecord record : recordList) {
            recordMap.put(record.getMd5hash(), record);
        }
        for (final MappedRecord r : recordMap.values()) {
            mappedRecordList.add(r);
        }
    }

    public String getId() {
        return configuration.getId();
    }

    public List<MappedRecord> getRecordList() {
        return mappedRecordList;
    }

    public Set<String> getNotMatchedKeySet() {

        final Set<String> keySet = new HashSet<String>();
        for (final MappedRecord r : notMatchedList) {
            keySet.add(r.getIndex());
        }
        return keySet;
    }

    public List<MappedRecord> getNotMatchedList() {
        return notMatchedList;
    }

    public Map<String, MappedRecordJson> getJsonRecordMap() {

        final Map<String, MappedRecordJson> map = new HashMap<>();
        for (final MappedRecord r : mappedRecordList) {
            map.put(r.getIndex(), new MappedRecordJson(r));
        }
        return map;
    }

    public List<MappedRecordJson> getJsonRecordList() {

        if (mappedRecordJsonList == null) {
            mappedRecordJsonList = new ArrayList<MappedRecordJson>();
            for (final MappedRecord record : mappedRecordList) {
                mappedRecordJsonList.add(new MappedRecordJson(record));
            }
        }
        return mappedRecordJsonList;
    }

    //
    // parse the row into the MappedRecordJson
    //
    private MappedRecord toRecord(final Map<String, String> row) {

        final boolean isFullDescription = configuration.isFullDescription();

        final MappedRecord record = new MappedRecord();
        record.setCreator(configuration.getId());
        record.setLastUpdated(new Date());
        setHostAndPath(record, configuration.getJson_ds() == null ? configuration.getUri() + Configuration.S_UrlPostfix
                : configuration.getJson_ds());

        Set<String> columnNames = ConfigurationHelper.getMap(configuration).keySet();
        for (final String columnName : columnNames) {
            final StringBuffer sb = new StringBuffer();
            final List<String> columns = ConfigurationHelper.getMap(configuration).get(columnName);
            if (columnName.equalsIgnoreCase(ConfigurationHelper.FN_Description) && !isFullDescription) {
                final Set<String> emptySet = new HashSet<String>();
                for (final String column : columns) {
                    final String newKeyName = getMappedName(column);
                    final String value = row.get(column);
                    if (value != null) {
                        record.getDescMap().put(newKeyName, value);
                    } else {
                        emptySet.add(newKeyName);
                    }
                }
                if (emptySet.size() > 0) {
                    for (final String key : emptySet) {
                        record.getDescMap().put(key, "N/A");
                    }
                }
            } else {
                int isFirstColumn = 0;
                for (final String column : columns) {
                    if (isFirstColumn++ > 0) {
                        sb.append(S_TokenSeparator);
                    }
                    sb.append(row.get(column));
                }
            }
            record.put(columnName, sb.toString().trim());
        }

        // fill the content with every columns
        final StringBuffer sb = new StringBuffer();
        sb.append("[");
        final Collection<String> values = row.values();
        int isFirstColumn = 0;
        for (final Object value : values) {
            if (isFirstColumn++ > 0) {
                sb.append(S_TokenSeparator);
            }
            sb.append(value);
        }
        sb.append("]");
        record.setContent(sb.toString());

        if (isFullDescription) {
            columnNames = row.keySet();
            for (final String key : columnNames) {
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
        record.setMd5hash(Util.ToHash(record.getContent()));

        // check whether filter match the filter text
        final String filter = record.getFilter();
        final boolean isMatched = isMatchFilter(filter);
        logger.trace("Filter: [{}] Matched: [{}]", filter, isMatched ? "YES" : "NO");

        // if the filter mis-match then add into notMatchedList and return null
        if (!isMatched) {
            notMatchedList.add(record);
            return null;
        }

        return record;
    }

    private void setHostAndPath(final MappedRecord record, final String url) {
        try {
            final URL aURL = new URL(url);
            record.setSourceHost(aURL.getHost());
            record.setSourceURL(aURL.getPath());
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private boolean isMatchFilter(final String filter) {

        if (filter == null || filter.length() == 0) {
            return false;
        }

        final boolean negativeExpression = configuration.getFilterText().startsWith("!");
        final String filterText = negativeExpression ? configuration.getFilterText().substring(1)
                : configuration.getFilterText();
        final String pattern = PatternPrefix + filterText + PatternPostfix;
        logger.trace("Filter Pattern: " + pattern);
        final boolean isMatched = filter.matches(pattern);
        return isMatched && negativeExpression == false || isMatched == false && negativeExpression == true;
    }

    private String getMappedName(final String name) {

        if (mappingColumns == null) {
            return name;
        }

        final String mappedName = mappingColumns.get(name);
        return mappedName != null ? mappedName : name;
    }

    private Double[][] calculateBoundingBox(final Collection<MappedRecord> records, final String distanceText) {

        final double distance = new Double(distanceText).doubleValue();

        double south = 0.0;
        double north = 0.0;
        double west = 0.0;
        double east = 0.0;
        for (final MappedRecord record : records) {
            final double lat = Double.parseDouble(record.getLatitude());
            north = lat > 0 ? lat > north ? lat : north : lat < north ? lat : north;
            south = lat > 0 ? lat < south ? lat : south : lat > south ? lat : south;
            final double lon = Double.parseDouble(record.getLongitude());
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
        final double d = distance * 1000.0;
        final double deltaLat = d / Radius * 180 / Pi;
        north += deltaLat * (north > 0 ? 1 : -1);
        south -= deltaLat * (south > 0 ? 1 : -1);
        final double northDelta = d / (Radius * Math.cos(Pi * north / 180.0)) * 180.0 / Pi;
        final double northWestLon = west - northDelta;
        final double northEastLon = east + northDelta;
        final double southDelta = d / (Radius * Math.cos(Pi * south / 180.0)) * 180.0 / Pi;
        final double southWestLon = west - southDelta;
        final double southEastLon = east + southDelta;
        final Double[][] boundingBox = new Double[5][2];
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

    public boolean isAutoClose() {
        return this.configuration.isAutoClose();
    }
}
