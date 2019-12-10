This is adapter for SpotOnResponse and will allow upload of configuration files to process JSON and CSV files and 
put them into XCBD (XChangecore Big Data)


This is a Spring Boot project, deployment is as simple as executing the jar:
java -jar xcbdadapter-1.0.1.jar



Table Of Contents:

Each configuration's file name will be used as index for the data in NoSQL, for example, landslide.config will
generate data with Title as landslide so it can be queried easier. Each configuration will be running at the rate
of every 6 hours which is defined as cron sechedule in application.properties.

For the testing purpose, you can place the test configuration file which file name is prefixed as 'test.',
for example, 'test.landslide.config' then this configuration will be run only once after the file is updated.

To upload a configuration, you can use browser to access http://localhost/. You can use 'Configuration File Upload' tab to upload the configuration file into adapter.

Couple utilities for configuration management and upload content management:

list of the all the Configuration uploaded:
http://hostname/api/listConfiguration

you can see the uploaded configuration, for example cvs.config, using browser by
http://hostname/api/configuratiion/cvs

you can see the specific configuration file, for example cvs
http://hostname/api/configuration/cvs

you can delete a specific configuration file, for example cvs
http://hostname/api/deleteConfiguration/cvs

you can use browser to VIEW the content for the configuration file, xcore.config
http://hostname/api/query?config=xcore

you can use browser to DELETE the content for the configuration file: xcore.config
http://hostname/api/delete?config=xcore

Only the file upload is coded as web application, will integrate the rest into the web application later.
