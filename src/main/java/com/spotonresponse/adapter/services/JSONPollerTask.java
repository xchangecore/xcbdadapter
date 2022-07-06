package com.spotonresponse.adapter.services;


import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.process.JsonFeedParser;
import com.spotonresponse.adapter.repo.DynamoDBRepository;
import com.spotonresponse.adapter.services.UrlReader;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class JSONPollerTask implements Runnable {
    static Logger logger = LoggerFactory.getLogger(com.spotonresponse.adapter.services.JSONPollerTask.class);

    private DynamoDBRepository dynamoDBRepository;

    private Configuration configuration;

    public JSONPollerTask(Configuration configuration) {
        this.configuration = configuration;
        this.dynamoDBRepository = new DynamoDBRepository();
    }

    public void run() {
        MDC.put("logFileName", this.configuration.getId());
        logger.info("JSONPollerTask starts @ [{}] with URL: [{}]", new Date(), this.configuration.getJson_ds());
        if (this.configuration.getJson_ds() == null)
            System.exit(-1);
        String content = (new UrlReader(this.configuration.getJson_ds())).getContent();
        JsonFeedParser parser = new JsonFeedParser(this.configuration, content);
        this.dynamoDBRepository.updateEntries(parser.getNotMatchedKeySet(), parser
                .getJsonRecordMap(), parser
                .isAutoClose(), parser
                .getId());
    }
}

