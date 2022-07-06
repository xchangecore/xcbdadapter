package com.spotonresponse.adapter.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UploadHistory {
    @Id
    @GeneratedValue
    private Integer id;

    private String filename;

    private Date timestamp;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public UploadHistory() {}

    public UploadHistory(Integer id, String filename, Date timestamp) {
        this.id = id;
        this.filename = filename;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return this.id;
    }

    public String getFilename() {
        return this.filename;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }
}