package com.spotonresponse.adapter.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import com.spotonresponse.adapter.model.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

public class JsonScheduler {

    private final static Logger logger = LoggerFactory.getLogger(JsonScheduler.class);

    private String cronSchedule;
    private ThreadPoolTaskScheduler taskScheduler;

    private final static Map<String, ScheduledFuture> scheduleMap = new HashMap<String, ScheduledFuture>();
    private static JsonScheduler _instance = null;

    private JsonScheduler() {
    }

    public static JsonScheduler getInstance() {
        if (_instance == null)
            _instance = new JsonScheduler();
        return _instance;
    }

    public void removeSchedule(String name) {

        ScheduledFuture schedule = scheduleMap.remove(name);
        if (schedule != null) {

            logger.info("Cancel schedule for {}", name);
            schedule.cancel(true);
        }
    }

    public void setSchedule(Configuration configuration) {

        if (configuration == null || configuration.getJson_ds() == null)
            return;
        String name = configuration.getId();
        logger.info("setSchedule: name: [{}}]", name);
        removeSchedule(name);

        if (name.startsWith("test.")) {
            Date currentTimestamp = new Date();
            long oneSecondLater = currentTimestamp.getTime() + 10000;
            logger.info("current time: [{}], scheduled time: [{}]", currentTimestamp, new Date(oneSecondLater));
            logger.info("Start JSON poller Thread for [{}], URL: [{}]", configuration.getId(),
                    configuration.getJson_ds());
            scheduleMap.put(name, taskScheduler.schedule(new JSONPollerTask(configuration), new Date(oneSecondLater)));
        } else {
            logger.info("Start JSON poller Thread: ID: [{}], URL: [{}], schedule: [{}]", configuration.getId(),
                    configuration.getJson_ds(), cronSchedule);
            scheduleMap.put(configuration.getId(),
                    taskScheduler.schedule(new JSONPollerTask(configuration), new CronTrigger(cronSchedule)));
        }
    }

    /**
     * @return the cronSchedule
     */
    public String getCronSchedule() {
        return cronSchedule;
    }

    /**
     * @return the taskScheduler
     */
    public ThreadPoolTaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    /**
     * @param taskScheduler the taskScheduler to set
     */
    public void setTaskScheduler(ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * @param cronSchedule the cronSchedule to set
     */
    public void setCronSchedule(String cronSchedule) {
        this.cronSchedule = cronSchedule;
    }
}