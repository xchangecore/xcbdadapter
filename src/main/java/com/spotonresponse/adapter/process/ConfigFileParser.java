package com.spotonresponse.adapter.process;

import com.spotonresponse.adapter.model.Configuration;
import com.spotonresponse.adapter.model.ConfigurationHelper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class ConfigFileParser {

    private static final Logger logger = LoggerFactory.getLogger(ConfigFileParser.class);

    private List<Configuration> configurationList = new ArrayList<Configuration>();

    public ConfigFileParser() {

        super();
    }

    public ConfigFileParser(String configFilename, InputStream configInputStream) throws Exception {

        super();

        final String creator = FilenameUtils.getBaseName(configFilename);
        logger.info("start parsing {} and creator: {}", configFilename, creator);

        int startCount = 0;
        int endCount = 0;
        String line = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(configInputStream));
            reader.mark(20480);
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.length() == 0) {
                    continue;
                }
                if (line.equalsIgnoreCase(ConfigurationHelper.FN_Configuration_Start)) {
                    startCount++;
                    continue;
                } else if (line.equalsIgnoreCase(ConfigurationHelper.FN_Configuration_End)) {
                    endCount++;
                    continue;
                }
            }
        } catch (final Exception e) {
            throw new Exception(e.getMessage());
        }

        if (startCount != endCount) {
            throw new Exception("Configuration File: " + configFilename + ": incomplete configuration block");
        }

        try {

            reader.reset();

            Configuration configuration = null;

            if (startCount == 0) {
                configuration = new Configuration();
                configuration.setId(creator);
            }

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.length() == 0) {
                    continue;
                }
                if (line.equalsIgnoreCase(ConfigurationHelper.FN_Configuration_Start)) {
                    logger.debug("Configuration: .. start ...");
                    configuration = new Configuration();
                    configuration.setId(creator);
                    continue;
                } else if (line.equalsIgnoreCase(ConfigurationHelper.FN_Configuration_End)) {
                    logger.debug("Configuration: .. end ...");
                    if (ConfigurationHelper.isValid(configuration)) {
                        configurationList.add(configuration);
                        configuration = null;
                        continue;
                    } else {
                        throw new Exception("Configuration File: " + configFilename + ": "
                                + ConfigurationHelper.getMissingAttributes(configuration));
                    }
                }
                if (configuration == null) {
                    throw new Exception("Configuration File: " + configFilename + ": Invalid format ...");
                }
                final String[] tokens = line.split(",", -1);
                if (tokens.length != 2) {
                    logger.error("Configuration File: " + configFilename + "Invalid formated Line: [" + line + "]");
                    continue;
                }

                tokens[0] = tokens[0].trim().toLowerCase();
                tokens[1] = tokens[1].trim().replaceAll("~@~", " ");
                setKeyValue(configuration, tokens);
            }
            if (configuration != null) {
                if (ConfigurationHelper.isValid(configuration)) {
                    configurationList.add(configuration);
                } else {
                    throw new Exception("Configuration File: " + configFilename + ": "
                            + ConfigurationHelper.getMissingAttributes(configuration));
                }
            }
            reader.close();
            logger.info("done parsing [{}]", configFilename);
        } catch (final Exception e) {
            throw new Exception("Configuration File: " + configFilename + ", Error: " + e.getMessage());
        }
    }

    public List<Configuration> getConfigurationList() {

        return configurationList;
    }

    public void setConfigurationList(List<Configuration> configurationList) {

        this.configurationList = configurationList;
    }

    private void setKeyValue(Configuration configuration, final String[] keyAndValue) {

        // logger.debug("key/value: [" + keyAndValue[0] + "/" + keyAndValue[1] + "]");

        if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_Category)) {
            configuration.setCategory(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_Title)) {
            configuration.setTitle(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_TitlePrefix)) {
            configuration.setTitlePrefix(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_TitleSuffix)) {
            configuration.setTitleSuffix(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_Latitude)) {
            configuration.setLatitude(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_Longitude)) {
            configuration.setLongitude(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_FilterName)) {
            configuration.setFilter(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_FilterText)) {
            configuration.setFilterText(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_Index)) {
            configuration.setIndex(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_Description)) {
            configuration.setDescription(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_URLHost)) {
            configuration.setUri(keyAndValue[1] + ConfigurationHelper.S_UrlHost);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_Username)) {
            configuration.setUsername(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_Password)) {
            configuration.setPassword(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_RedirectUrl)) {
            configuration.setRedirectUrl(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_CategoryPrefix)) {
            configuration.setCategoryPrefix(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_CategorySuffix)) {
            configuration.setCategorySuffix(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_CategoryFixed)) {
            configuration.setCategoryFixed(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_Distance)) {
            configuration.setDistance(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_DistanceFilterText)) {
            configuration.setDistanceFilterText(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_AutoClose)) {
            configuration.setAutoClose(ConfigurationHelper.getBooleanValue(keyAndValue[1]));
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_MappingColumns)) {
            configuration.setMappingColumns(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_FullDescription)) {
            configuration.setFullDescription(ConfigurationHelper.getBooleanValue(keyAndValue[1]));
        } else if (keyAndValue[0].equalsIgnoreCase(ConfigurationHelper.FN_JsonDataSource)) {
            configuration.setJson_ds(keyAndValue[1]);
        } else {
            logger.warn("Invalid Key/Value: [" + keyAndValue[0] + "/" + keyAndValue[1] + "]");
        }
    }
}