package com.spotonresponse.adapter.services;

import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.model.MappedRecordJson;
import com.spotonresponse.adapter.process.JsonFeedParser;
import com.spotonresponse.adapter.repo.DynamoDBRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;
import java.util.List;

public class JSONPollerTask implements Runnable {

    static Logger logger = LoggerFactory.getLogger(JSONPollerTask.class);

    private DynamoDBRepository dynamoDBRepository;
    private Configuration configuration;

    public JSONPollerTask(Configuration configuration) {

        this.configuration = configuration;
        this.dynamoDBRepository = new DynamoDBRepository();
    }

    @Override
    public void run() {

        MDC.put("logFileName", this.configuration.getId());

        logger.info("JSONPollerTask starts @ [{}] with URL: [{}]", new Date(), configuration.getJson_ds());
        if (configuration.getJson_ds() == null) {
            // TODO fatal error
            System.exit(-1);
        }

        String content = new UrlReader(configuration.getJson_ds()).getContent();

        // use the input stream to generate records
        List<MappedRecordJson> recordList = new JsonFeedParser(this.configuration, content).getJsonRecordList();

        logger.info("record count: {}", recordList.size());
        dynamoDBRepository.removeByCreator(configuration.getId());
        dynamoDBRepository.createAllEntries(recordList);
        logger.info("... done ...");
    }
}
