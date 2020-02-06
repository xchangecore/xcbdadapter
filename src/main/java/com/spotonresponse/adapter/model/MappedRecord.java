package com.spotonresponse.adapter.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Hashtable;

import com.spotonresponse.adapter.model.ConfigurationHelper;

@Data
@NoArgsConstructor
public class MappedRecord {

    static Logger logger = LoggerFactory.getLogger(MappedRecord.class);

    private Integer id;
    private String creator;

    private String title = "N/A";
    private String category = "N/A";
    private String content = "N/A";
    private String description = "N/A";
    private String index = "N/A";
    private String latitude;
    private String longitude;
    private String filter = "N/A";

    // for XchangeCore 
    private String igID = null;
    private String workProductID = null;

    // for JSON feed MD5Hah
    private String md5hash = null;

    // private String distance;
    // private String distanceFilterText;

    /* for NoSQL
     * Source -> {The name of your program}
     * SourceHost -> {Where your program is running}
     * SourceURL -> {URL to view/manage source (if applicable)}
     * SourceContact -> {Name of person or group to contact about source data if needed (if applicable)}
     * SourceEmail -> {Email of SourceContact (if applicable)}
     */
    private String source;
    private String sourceHost;
    private String sourceURL;
    private String sourceContact;
    private String sourceEmail;

    private Date lastUpdated;

    private Hashtable<String, String> descMap = new Hashtable<String, String>();

    public void put(String key, String value) {

        if (key.equalsIgnoreCase(ConfigurationHelper.FN_Title)) {
            setTitle(value);
        } else if (key.equalsIgnoreCase(ConfigurationHelper.FN_Description)) {
            setDescription(value);
        } else if (key.equalsIgnoreCase(ConfigurationHelper.FN_Category)) {
            setCategory(value);
        } else if (key.equalsIgnoreCase(ConfigurationHelper.FN_FilterName)) {
            setFilter(value);
        } else if (key.equalsIgnoreCase(ConfigurationHelper.FN_Index)) {
            setIndex(value);
        } else if (key.equalsIgnoreCase(ConfigurationHelper.FN_Content)) {
            setContent(value);
        } else if (key.equalsIgnoreCase(ConfigurationHelper.FN_Latitude)) {
            setLatitude(value);
        } else if (key.equalsIgnoreCase(ConfigurationHelper.FN_Longitude)) {
            setLongitude(value);
        } else {
            logger.error("MapperRecord.put: key: [{}], value: [{}]", key, value);
        }
    }

    public String get(String key) {

        if (key.equalsIgnoreCase(ConfigurationHelper.FN_Title)) {
            return getTitle();
        } else if (key.equalsIgnoreCase(ConfigurationHelper.FN_Description)) {
            return getDescription();
        } else if (key.equalsIgnoreCase(ConfigurationHelper.FN_Category)) {
            return getCategory();
        } else if (key.equalsIgnoreCase(ConfigurationHelper.FN_FilterName)) {
            return getTitle();
        } else if (key.equalsIgnoreCase(ConfigurationHelper.FN_Content)) {
            return getContent();
        } else {
            logger.error("MapperRecord.get: key: [{}]", key);
            return "N/A";
        }
    }
}
