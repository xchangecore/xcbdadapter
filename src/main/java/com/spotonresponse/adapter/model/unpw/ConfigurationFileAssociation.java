package com.spotonresponse.adapter.model.unpw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConfigurationFileAssociation {
    @Id
    private String username;
    private String password;
    private String configName;
}
