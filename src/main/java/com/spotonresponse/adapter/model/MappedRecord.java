package com.spotonresponse.adapter.model;

import java.util.Date;
import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappedRecord {
    public void setId(Integer id) {
        this.id = id;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setIgID(String igID) {
        this.igID = igID;
    }

    public void setWorkProductID(String workProductID) {
        this.workProductID = workProductID;
    }

    public void setMd5hash(String md5hash) {
        this.md5hash = md5hash;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSourceHost(String sourceHost) {
        this.sourceHost = sourceHost;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public void setSourceContact(String sourceContact) {
        this.sourceContact = sourceContact;
    }

    public void setSourceEmail(String sourceEmail) {
        this.sourceEmail = sourceEmail;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setDescMap(Hashtable<String, String> descMap) {
        this.descMap = descMap;
    }

    public String toString() {
        return "MappedRecord(id=" + getId() + ", creator=" + getCreator() + ", title=" + getTitle() + ", category=" + getCategory() + ", content=" + getContent() + ", description=" + getDescription() + ", index=" + getIndex() + ", latitude=" + getLatitude() + ", longitude=" + getLongitude() + ", filter=" + getFilter() + ", igID=" + getIgID() + ", workProductID=" + getWorkProductID() + ", md5hash=" + getMd5hash() + ", source=" + getSource() + ", sourceHost=" + getSourceHost() + ", sourceURL=" + getSourceURL() + ", sourceContact=" + getSourceContact() + ", sourceEmail=" + getSourceEmail() + ", lastUpdated=" + getLastUpdated() + ", descMap=" + getDescMap() + ")";
    }

    public MappedRecord() {
        this.title = "N/A";
        this.category = "N/A";
        this.content = "N/A";
        this.description = "N/A";
        this.index = "N/A";
        this.filter = "N/A";
        this.igID = null;
        this.workProductID = null;
        this.md5hash = null;
        this.descMap = new Hashtable<>();
    }

    static Logger logger = LoggerFactory.getLogger(com.spotonresponse.adapter.model.MappedRecord.class);

    private Integer id;

    private String creator;

    private String title;

    private String category;

    private String content;

    private String description;

    private String index;

    private String latitude;

    private String longitude;

    private String filter;

    private String igID;

    private String workProductID;

    private String md5hash;

    private String source;

    private String sourceHost;

    private String sourceURL;

    private String sourceContact;

    private String sourceEmail;

    private Date lastUpdated;

    private Hashtable<String, String> descMap;

    public Integer getId() {
        return this.id;
    }

    public String getCreator() {
        return this.creator;
    }

    public String getTitle() {
        return this.title;
    }

    public String getCategory() {
        return this.category;
    }

    public String getContent() {
        return this.content;
    }

    public String getDescription() {
        return this.description;
    }

    public String getIndex() {
        return this.index;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public String getFilter() {
        return this.filter;
    }

    public String getIgID() {
        return this.igID;
    }

    public String getWorkProductID() {
        return this.workProductID;
    }

    public String getMd5hash() {
        return this.md5hash;
    }

    public String getSource() {
        return this.source;
    }

    public String getSourceHost() {
        return this.sourceHost;
    }

    public String getSourceURL() {
        return this.sourceURL;
    }

    public String getSourceContact() {
        return this.sourceContact;
    }

    public String getSourceEmail() {
        return this.sourceEmail;
    }

    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    public Hashtable<String, String> getDescMap() {
        return this.descMap;
    }

    public void put(String key, String value) {
        if (key.equalsIgnoreCase("title")) {
            setTitle(value);
        } else if (key.equalsIgnoreCase("description")) {
            setDescription(value);
        } else if (key.equalsIgnoreCase("category")) {
            setCategory(value);
        } else if (key.equalsIgnoreCase("filter")) {
            setFilter(value);
        } else if (key.equalsIgnoreCase("index")) {
            setIndex(value);
        } else if (key.equalsIgnoreCase("content")) {
            setContent(value);
        } else if (key.equalsIgnoreCase("latitude")) {
            setLatitude(value);
        } else if (key.equalsIgnoreCase("longitude")) {
            setLongitude(value);
        } else {
            logger.error("MapperRecord.put: key: [{}], value: [{}]", key, value);
        }
    }

    public String get(String key) {
        if (key.equalsIgnoreCase("title"))
            return getTitle();
        if (key.equalsIgnoreCase("description"))
            return getDescription();
        if (key.equalsIgnoreCase("category"))
            return getCategory();
        if (key.equalsIgnoreCase("filter"))
            return getTitle();
        if (key.equalsIgnoreCase("content"))
            return getContent();
        logger.error("MapperRecord.get: key: [{}]", key);
        return "N/A";
    }
}
