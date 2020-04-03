package com.spotonresponse.adapter.services;

import java.util.List;

import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.repo.ConfigurationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationSetupService implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationSetupService.class);

    @Value("${jsonpoller.cron.schedule}")
    private String cronSchedule;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * This event is executed as late as conceivably possible to indicate that the
     * application is ready to service requests. - initialize Directory Monitor -
     * initialize the Configuration files - initilize JSON poller if the json_ds
     * existed
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        logger.info("ConfigurationSetupService: ... init ...");
        JsonScheduler.getInstance().setCronSchedule(cronSchedule);
        JsonScheduler.getInstance().setTaskScheduler(taskScheduler);
        List<Configuration> configurationList = configurationRepository.findAll();
        for (Configuration configuration : configurationList) {
            JsonScheduler.getInstance().setSchedule(configuration);
        }
        logger.info("ConfigurationSetupService: ... done ...");

        return;
    }
}
