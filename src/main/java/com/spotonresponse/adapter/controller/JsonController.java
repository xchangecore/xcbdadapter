package com.spotonresponse.adapter.controller;

import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.model.QueryResult;
import com.spotonresponse.adapter.repo.ConfigurationRepository;
import com.spotonresponse.adapter.repo.DynamoDBRepository;

import com.spotonresponse.adapter.services.JsonScheduler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class JsonController {

    @Autowired
    private DynamoDBRepository dynamoDBRepository;

    @Autowired
    private ConfigurationRepository configurationRepository;


    @GetMapping(path = "/listCSVConfigurationName", produces = "applicaiton/json")
    public String listCSVConfigurationName() {

        return new Gson().toJson(configurationRepository.listCSVConfigurationName());
    }

    @GetMapping(path = "/listConfigurationName", produces = "applicaiton/json")
    public String listConfigurationName() {

        return new Gson().toJson(configurationRepository.listConfigurationName());
    }

    @GetMapping(path = "/configuration/{name}", produces = "application/json")
    public Configuration getConfiguration(@PathVariable String name) {

        return configurationRepository.findById(name).get();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(path = "/deleteConfiguration/{name}", produces = "application/json")
    public String deleteConfiguration(@PathVariable String name) {

        Optional<Configuration> configuration = configurationRepository.findById(name);
        if (configuration.isPresent()) {
            if (configuration.get().getJson_ds() != null) {
                JsonScheduler.getInstance().removeSchedule(name);
            }
            configurationRepository.deleteById(name);
        }
        JSONObject status = new JSONObject();
        status.put("Configuration", name);
        status.put("Remove", configurationRepository.findById(name).isPresent() ? "Failure" : "Success");
        return status.toString();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(path = "/listConfiguration", produces = "application/json")
    public List<Configuration> listConfiguration() {

        return configurationRepository.findAll();
    }

    @RequestMapping(value = "/query", produces = "application/json")
    public QueryResult query(@RequestParam(value = "config", defaultValue = "xcore") String creator) {

        JSONArray resultArray = dynamoDBRepository.queryArray(creator);

        return new QueryResult(creator, resultArray.length(), resultArray);
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    public QueryResult delete(@RequestParam(value = "config", defaultValue = "xcore") String creator) {

        int count = dynamoDBRepository.removeByCreator(creator);
        return new QueryResult(creator, count, null);
    }

    @RequestMapping(value = "/test", produces = "application/json")
    public String test(@RequestParam(value = "config", defaultValue = "xcore") String configuration) {

        JSONObject o = new JSONObject();
        o.put("Configuration", configuration);
        o.put("Name", "Daniel Huang");
        return o.toString();
    }
}
