package com.spotonresponse.adapter.controller;

import com.spotonresponse.adapter.services.CSVToJSON;
import com.spotonresponse.adapter.services.FileStorageService;
import com.spotonresponse.adapter.services.JsonScheduler;
import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.process.CSVParser;
import com.spotonresponse.adapter.process.ConfigFileParser;
import com.spotonresponse.adapter.repo.ConfigurationRepository;
import com.spotonresponse.adapter.repo.DynamoDBRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private DynamoDBRepository dynamoDBRepository;

    @PostMapping(path = "/uploadConfig", produces = "application/json")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {

        String fileName = fileStorageService.storeFile(file);
        ConfigFileParser parser;
        try {
            logger.info("Upload file: {} ...", fileName);
            parser = new ConfigFileParser(fileName, file.getInputStream());
            List<Configuration> configurationList = parser.getConfigurationList();
            for (Configuration configuration : configurationList) {
                configurationRepository.save(configuration);
                JsonScheduler.getInstance().setSchedule(configuration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                .path(fileName).toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultiConfig")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        for (MultipartFile file : files) {
            logger.info("Uploading file: " + file.getName());
        }
        return Arrays.asList(files).stream().map(file -> uploadFile(file)).collect(Collectors.toList());
    }

    @PostMapping("/uploadCSVFile")
    public UploadFileResponse uploadCSVFile(@RequestParam("file") MultipartFile file, String csvConfiugrationName) {

        // convert MultipartFile into Map then retrieve the configuration
        Optional<Configuration> configuration = configurationRepository.findById(csvConfiugrationName);
        int rowCount = 0;
        if (configuration.isPresent()) {
            List<Map<String, Object>> rows = CSVToJSON.parse(file);
            rowCount = rows.size();
            try {
                if (rowCount == 1) {
                    // Only update the record instead of parsing the csv
                    dynamoDBRepository.update(configuration.get().getId(), rows.get(0));
                } else {
                    CSVParser parser = new CSVParser(configuration.get(), rows);
                    dynamoDBRepository.updateEntries(parser.getNotMatchedKeySet(),
                        parser.getJsonRecordMap(),
                        parser.isAutoClose(),
                        parser.getId());
                }
            } catch (Exception e) {
                // TODO Error Handling
                e.printStackTrace();
            }
        }
        // parse the map with configuration
        return new UploadFileResponse(csvConfiugrationName, "xyz", "csv", rowCount);
    }

    @PostMapping("/uploadMultiCSVFile")
    public List<UploadFileResponse> uploadMultipleCSVFiles(@RequestParam("files") MultipartFile[] files,
                                                           @RequestParam("config_name") String csvConfiugrationName) {

        return Arrays.asList(files).stream().map(file -> uploadCSVFile(file, csvConfiugrationName))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
