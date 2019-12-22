package com.spotonresponse.adapter.services.unpw;

import com.spotonresponse.adapter.model.unpw.ConfigurationFileAssociation;
import com.spotonresponse.adapter.repo.unpw.ConfigurationFileAssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationFileAssociationValidator {
    @Autowired
    private ConfigurationFileAssociationRepository configurationFileAssociationRepository;

    public ValidationErrors validate(ConfigurationFileAssociation configurationFileAssociation){
        final ValidationErrors validationErrors = new ValidationErrors();

        if(configurationFileAssociation.getConfigName().length() < 3){
            validationErrors.addError("short_username", "Username cannot be less than 3 characters");
        }

        if(configurationFileAssociation.getPassword().length() < 6){
            validationErrors.addError("short_password", "Password too short");
        }

        boolean usernameExists = configurationFileAssociationRepository
                .existsByUsername(configurationFileAssociation.getUsername());

        if(usernameExists){
            validationErrors.addError("username_exists", "The username already exists");
        }

        return validationErrors;
    }
}
