package com.spotonresponse.adapter.controller.unpw;

import com.spotonresponse.adapter.controller.FileController;
import com.spotonresponse.adapter.model.unpw.ConfigurationFileAssociation;
import com.spotonresponse.adapter.repo.ConfigurationRepository;
import com.spotonresponse.adapter.repo.DynamoDBRepository;
import com.spotonresponse.adapter.repo.unpw.ConfigurationFileAssociationDynamoDBRepository;
import com.spotonresponse.adapter.repo.unpw.ConfigurationFileAssociationRepository;
import com.spotonresponse.adapter.security.unpw.JwtService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserFileController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ConfigurationFileAssociationDynamoDBRepository configurationFileAssociationRepository;

    @Autowired
    private FileController fileController;

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
    public long uploadCSVFile(@RequestParam("csvFile") MultipartFile csvFile, Principal principal) {
        ConfigurationFileAssociation configurationFileAssociation =
                configurationFileAssociationRepository.findById(principal.getName());


        return fileController
                .uploadCSVFile(csvFile, configurationFileAssociation.getConfigName())
                .getSize();
    }
}
