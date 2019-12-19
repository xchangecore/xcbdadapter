package com.spotonresponse.adapter.controller.unpw;

import com.spotonresponse.adapter.controller.FileController;
import com.spotonresponse.adapter.controller.UploadFileResponse;
import com.spotonresponse.adapter.model.ConfigurationFileAssociation;
import com.spotonresponse.adapter.repo.ConfigurationFileAssociationRepository;
import com.spotonresponse.adapter.services.unpw.ConfigurationFileAssociationValidator;
import com.spotonresponse.adapter.services.unpw.ValidationErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/unpw/")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminFileController {

    @Autowired
    private ConfigurationFileAssociationValidator validator;

    @Autowired
    private ConfigurationFileAssociationRepository configFileAssocRepository;

    @Autowired
    private FileController fileController;


    @PostMapping("uploadConfig")
    public ResponseEntity<?> uploadConfigFile(String username, String password, MultipartFile configFile){

        final ConfigurationFileAssociation configFileAssoc =
                new ConfigurationFileAssociation(username, password, configFile.getName());


        // validate the user input and return 400 error if the validation failed.
        final ValidationErrors validationErrors = validator.validate(configFileAssoc);

        if(validationErrors.hasErrors())
            return ResponseEntity.badRequest().body(validationErrors);


        // persist the config file association.
        configFileAssocRepository.save(configFileAssoc);

        // delegate the upload to the file controller
        final UploadFileResponse response =  fileController.uploadFile(configFile);

        return ResponseEntity.ok(response);
    }
}
