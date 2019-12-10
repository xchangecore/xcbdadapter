package com.spotonresponse.adapter.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UploadHistory {
    @Id
    @GeneratedValue
    private Integer id;
    private String filename;
    private Date timestamp;
}