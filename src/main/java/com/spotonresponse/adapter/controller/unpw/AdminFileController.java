package com.spotonresponse.adapter.controller.unpw;

import com.spotonresponse.adapter.controller.FileController;
import com.spotonresponse.adapter.controller.UploadFileResponse;
import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.model.ConfigurationFileAssociation;
import com.spotonresponse.adapter.process.ConfigFileParser;
import com.spotonresponse.adapter.repo.ConfigurationFileAssociationRepository;
import com.spotonresponse.adapter.repo.ConfigurationRepository;
import com.spotonresponse.adapter.services.FileStorageService;
import com.spotonresponse.adapter.services.JsonScheduler;
import com.spotonresponse.adapter.services.unpw.ConfigurationFileAssociationValidator;
import com.spotonresponse.adapter.services.unpw.ValidationErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

import static com.spotonresponse.adapter.model.ConfigurationHelper.logger;

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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @PostMapping("uploadConfig")
    public ResponseEntity<?> uploadConfigFile(@RequestParam  String username,
                                              @RequestParam  String password,
                                              @RequestParam  MultipartFile configFile){

        final ConfigurationFileAssociation configFileAssoc =
                new ConfigurationFileAssociation(username, password, configFile.getName());


        // validate the user input and return 400 error if the validation failed.
        final ValidationErrors validationErrors = validator.validate(configFileAssoc);

        if(validationErrors.hasErrors())
            return ResponseEntity.badRequest().body(validationErrors);

        // encrypt the password
        configFileAssoc.setPassword(passwordEncoder.encode(configFileAssoc.getPassword()));


        // persist the config file association.
        configFileAssocRepository.save(configFileAssoc);


        String fileName = fileStorageService.storeFile(configFile);
        ConfigFileParser parser;
        try {
            logger.info("Upload file: {} ...", fileName);
            parser = new ConfigFileParser(fileName, configFile.getInputStream());
            List<Configuration> configurationList = parser.getConfigurationList();
            for (Configuration configuration : configurationList) {
                // set the username/password for each config instance found.
                configuration.setUsername(username);
                configuration.setPassword(configFileAssoc.getPassword());

                configurationRepository.save(configuration);
                JsonScheduler.getInstance().setSchedule(configuration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                .path(fileName).toUriString();

        final UploadFileResponse response = new UploadFileResponse(fileName, fileDownloadUri, configFile.getContentType(), configFile.getSize());

        return ResponseEntity.ok(response);
    }
}
