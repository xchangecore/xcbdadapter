package com.spotonresponse.adapter.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
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
    private String titlePrefix = null;
    private String titleSuffix = null;
    private String category;
    private String filter;
    private String filterText;
    private String distance = "";
    private String distanceFilterText = "";
    private String latitude;
    private String longitude;
    private String categoryPrefix = null;
    private String categorySuffix = null;
    private String categoryFixed = null;
    private String json_ds;
    private String uri;
    private String username;
    private String password;
    @Column(columnDefinition = "VARCHAR(4096)")
    private String mappingColumns;
    @Column(columnDefinition = "VARCHAR(65536)")
    private String description = "title.category";
    @Column(columnDefinition = "VARCHAR(65536)")
    private String index = "title.category.latitude.longitude";
    private boolean autoClose = true;
    private boolean fullDescription = false;
    private boolean enableXCore = false;
    private String redirectUrl = "http://www.google.com";
}
