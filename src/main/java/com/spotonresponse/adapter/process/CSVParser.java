package com.spotonresponse.adapter.process;

import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.model.MappedRecord;
import com.spotonresponse.adapter.model.MappedRecordJson;
import com.spotonresponse.adapter.model.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVParser {

    public static final String S_TokenSeparator = ":";
    public static final String PatternPrefix = "(?i:.*";
    public static final String PatternPostfix = ".*)";
    public static final double Pi = 3.14159;
    public static final double Radius = 6378137.0;

    private static Logger logger = LoggerFactory.getLogger(CSVParser.class);

    private TheCSVParser parser = null;

    public CSVParser(Configuration configuration, List<Map<String, Object>> rows) {

        List<Map<String, String>> listOfRow = new ArrayList<Map<String, String>>();
        for (Map<String, Object> row : rows) {
            listOfRow.add(Util.convertKeyValue(row));
        }
        parser = new TheCSVParser(configuration, listOfRow);
    }

    public String getId() {
        return parser.getId();
    }

    public List<MappedRecord> getRecordList() {
        return parser.getRecordList();
    }

    public List<MappedRecordJson> getJsonRecordList() {

        return parser.getJsonRecordList();
    }
}
