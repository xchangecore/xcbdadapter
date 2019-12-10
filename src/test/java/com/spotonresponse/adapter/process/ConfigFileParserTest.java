package com.spotonresponse.adapter.process;

import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.model.MappedRecordJson;
import com.spotonresponse.adapter.repo.ConfigurationRepository;
import com.spotonresponse.adapter.services.UrlReader;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ConfigFileParserTest {

    private static Logger logger = LoggerFactory.getLogger(ConfigFileParserTest.class);

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Test
    public void testConfigFileParser() {

        try {
            File[] files = new ClassPathResource("config").getFile().listFiles();
            for (File file : files) {
                String filename = file.getPath();
                String basename = FilenameUtils.getBaseName(filename);
                String extension = FilenameUtils.getExtension(filename);
                logger.debug("Filename: {}", file.getAbsolutePath());
                ConfigFileParser configFileParser = new ConfigFileParser(file.getPath(),
                                                                         new FileInputStream(file.getAbsolutePath()));
                List<Configuration> configurationList = configFileParser.getConfigurationList();
                for (Configuration configuration : configurationList) {
                    configurationRepository.save(configuration);
                    if (configuration.getJson_ds() != null) {
                        String content = new UrlReader(configuration.getJson_ds()).getContent();
                        JsonFeedParser parser = new JsonFeedParser(configuration, content);
                        List<MappedRecordJson> list = parser.getJsonRecordList();
                        list.forEach(record -> {
                            logger.debug(record.toString());
                        });
                    }
                }
            }
        } catch (Exception e) {
            // TODO
            logger.error("Exception: {}", e.getMessage());
        }
    }
}
