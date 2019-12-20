package com.spotonresponse.adapter.controller.unpw;

import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.model.MappedRecordJson;
import com.spotonresponse.adapter.process.CSVParser;
import com.spotonresponse.adapter.repo.ConfigurationRepository;
import com.spotonresponse.adapter.repo.DynamoDBRepository;
import com.spotonresponse.adapter.security.unpw.ConfigUserDetailsService;
import com.spotonresponse.adapter.security.unpw.JwtService;
import com.spotonresponse.adapter.services.CSVToJSON;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

import static com.spotonresponse.adapter.model.ConfigurationHelper.logger;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserFileController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ConfigUserDetailsService configUserDetailsService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private DynamoDBRepository dynamoDBRepository;

    @PostMapping("/api/unpw/authenticate")
    public ResponseEntity<String> authenticate(@RequestParam String username,
                                       @RequestParam String password){

        val usernamePasswordToken =
                new UsernamePasswordAuthenticationToken(username, password);

        try {
            authenticationManager.authenticate(usernamePasswordToken);
            return ResponseEntity.ok(jwtService.generateToken(username));
        } catch (Exception e){
            return ResponseEntity.status(401).body("Bad Credentials");
        }
    }

    @PostMapping("/api/unpw/user/uploadCSV")
    public int uploadCSVFile(@RequestParam("csvFile") MultipartFile csvFile, Principal principal){
        List<Configuration> configurationList = configurationRepository.findByUsername(principal.getName());
        int csvRecordSize = 0;
        try{
            for(Configuration configuration : configurationList){
                CSVParser parser = new CSVParser(configuration, CSVToJSON.parse(csvFile));
                List<MappedRecordJson> recordList = parser.getJsonRecordList();

                if(recordList.size() > csvRecordSize){
                    csvRecordSize = recordList.size();
                }

                logger.info("record count: {}", recordList.size());
                dynamoDBRepository.removeByCreator(parser.getId());
                dynamoDBRepository.createAllEntries(recordList);
                logger.info("... done ...");
            }
        } catch (Exception e){
            logger.error(e.getMessage());
        }

        return csvRecordSize;
    }
}
