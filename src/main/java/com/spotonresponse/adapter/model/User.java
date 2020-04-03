package com.spotonresponse.adapter.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class User {
    @Id
    private String username;
    private String password;
    private String type;
}
