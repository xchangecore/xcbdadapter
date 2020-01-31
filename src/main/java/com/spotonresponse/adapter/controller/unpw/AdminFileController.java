package com.spotonresponse.adapter.controller.unpw;

import com.spotonresponse.adapter.controller.FileController;
import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.model.unpw.ConfigurationFileAssociation;
import com.spotonresponse.adapter.repo.ConfigurationRepository;
import com.spotonresponse.adapter.repo.unpw.ConfigurationFileAssociationDynamoDBRepository;
import com.spotonresponse.adapter.services.FileStorageService;
import com.spotonresponse.adapter.services.unpw.ConfigurationFileAssociationValidator;
import com.spotonresponse.adapter.services.unpw.ValidationErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/unpw/")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminFileController {

    @Autowired
    private ConfigurationFileAssociationValidator validator;

    @Autowired
    private ConfigurationFileAssociationDynamoDBRepository configFileAssocRepository;

    @Autowired
    private FileController fileController;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @GetMapping("existsByUsername")
    public boolean existsByUsername(@RequestParam  String username){
        return configFileAssocRepository.existsByUsername(username);
    }

    @GetMapping("configurations")
    public List<String> getAllConfigurations(){
        return configurationRepository.findAll().stream()
                .map(Configuration::getId)
                .collect(Collectors.toList());
    }


    @PostMapping("uploadConfig")
    public ResponseEntity<?> uploadConfigFile(@RequestParam  String username,
                                              @RequestParam  String password,
                                              @RequestParam  String configName){

        final ConfigurationFileAssociation configFileAssoc =
                new ConfigurationFileAssociation(username, password, configName);


        // validate the user input and return 400 error if the validation failed.
        final ValidationErrors validationErrors = validator.validate(configFileAssoc);

        if(validationErrors.hasErrors())
            return ResponseEntity.badRequest().body(validationErrors);

        // encrypt the password
        configFileAssoc.setPassword(passwordEncoder.encode(configFileAssoc.getPassword()));


        // persist the config file association
        configFileAssocRepository.save(configFileAssoc);

        return ResponseEntity.ok("Configuration was successfully saved.");
    }
}
