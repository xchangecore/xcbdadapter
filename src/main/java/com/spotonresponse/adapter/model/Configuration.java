package com.spotonresponse.adapter.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "configuration")
public class Configuration {
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

    public static final String S_UrlPostfix = "/core/ws/services";

    public static final String S_Dot = "\\.";

    public static final String FN_JsonDataSource = "json_ds";

    public static final String S_UrlHost = "/xchangecore/";

    @Id
    private String id;

    private String title;

    private String titlePrefix;

    private String titleSuffix;

    private String category;

    private String filter;

    private String filterText;

    private String distance;

    private String distanceFilterText;

    private String latitude;

    private String longitude;

    private String categoryPrefix;

    private String categorySuffix;

    private String categoryFixed;

    private String json_ds;

    private String uri;

    private String username;

    private String password;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String mappingColumns;

    @Column(columnDefinition = "VARCHAR(65536)")
    private String description;

    @Column(columnDefinition = "VARCHAR(65536)")
    private String index;

    private boolean autoClose;

    private boolean fullDescription;

    private boolean enableXCore;

    private String redirectUrl;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitlePrefix(String titlePrefix) {
        this.titlePrefix = titlePrefix;
    }

    public void setTitleSuffix(String titleSuffix) {
        this.titleSuffix = titleSuffix;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDistanceFilterText(String distanceFilterText) {
        this.distanceFilterText = distanceFilterText;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setCategoryPrefix(String categoryPrefix) {
        this.categoryPrefix = categoryPrefix;
    }

    public void setCategorySuffix(String categorySuffix) {
        this.categorySuffix = categorySuffix;
    }

    public void setCategoryFixed(String categoryFixed) {
        this.categoryFixed = categoryFixed;
    }

    public void setJson_ds(String json_ds) {
        this.json_ds = json_ds;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMappingColumns(String mappingColumns) {
        this.mappingColumns = mappingColumns;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    public void setFullDescription(boolean fullDescription) {
        this.fullDescription = fullDescription;
    }

    public void setEnableXCore(boolean enableXCore) {
        this.enableXCore = enableXCore;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }


    public Configuration() {
        this.titlePrefix = null;
        this.titleSuffix = null;
        this.distance = "";
        this.distanceFilterText = "";
        this.categoryPrefix = null;
        this.categorySuffix = null;
        this.categoryFixed = null;
        this.description = "title.category";
        this.index = "title.category.latitude.longitude";
        this.autoClose = true;
        this.fullDescription = false;
        this.enableXCore = false;
        this.redirectUrl = "http://www.google.com";
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTitlePrefix() {
        return this.titlePrefix;
    }

    public String getTitleSuffix() {
        return this.titleSuffix;
    }

    public String getCategory() {
        return this.category;
    }

    public String getFilter() {
        return this.filter;
    }

    public String getFilterText() {
        return this.filterText;
    }

    public String getDistance() {
        return this.distance;
    }

    public String getDistanceFilterText() {
        return this.distanceFilterText;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public String getCategoryPrefix() {
        return this.categoryPrefix;
    }

    public String getCategorySuffix() {
        return this.categorySuffix;
    }

    public String getCategoryFixed() {
        return this.categoryFixed;
    }

    public String getJson_ds() {
        return this.json_ds;
    }

    public String getUri() {
        return this.uri;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getMappingColumns() {
        return this.mappingColumns;
    }

    public String getDescription() {
        return this.description;
    }

    public String getIndex() {
        return this.index;
    }

    public boolean isAutoClose() {
        return this.autoClose;
    }

    public boolean isFullDescription() {
        return this.fullDescription;
    }

    public boolean isEnableXCore() {
        return this.enableXCore;
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }
}
