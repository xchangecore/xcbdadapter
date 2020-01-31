package com.spotonresponse.adapter.repo.unpw;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import com.spotonresponse.adapter.model.unpw.ConfigurationFileAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

@Repository
public class ConfigurationFileAssociationDynamoDBRepository {
    private static final String USERNAME_KEY = "username";
    private static final String CONFIG_NAME_KEY = "configName";
    private static final String PASSWORD_KEY = "password";
    private static final String TABLE_NAME = "configurationFileAssociation";

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private Table table;

    public ConfigurationFileAssociationDynamoDBRepository(AmazonDynamoDB dynamoDB){
        DynamoDB dynamoDBClient = new DynamoDB(dynamoDB);
        try {
            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withTableName(TABLE_NAME)
                    .withAttributeDefinitions(new AttributeDefinition(USERNAME_KEY, ScalarAttributeType.S))
                    .withKeySchema(new KeySchemaElement(USERNAME_KEY, KeyType.HASH))
                    .withProvisionedThroughput(new ProvisionedThroughput(
                            10L, 10L));
            this.table = dynamoDBClient.createTable(createTableRequest);
            table.waitForActive();
        } catch (AmazonServiceException e){
            this.table = dynamoDBClient.getTable(TABLE_NAME);
        } catch (Exception e){
            logger.error("Error creating/loading configuration file table: {}", e.getMessage());
        }
    }

    public boolean existsByUsername(String username){
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(USERNAME_KEY + " = :v_username")
                .withValueMap(new ValueMap().withString(
                ":v_username",
                username));

        return this.table.query(querySpec).iterator().hasNext();
    }

    public void save(ConfigurationFileAssociation configurationFileAssociation){
        table.putItem(new Item().withPrimaryKey(USERNAME_KEY,
                configurationFileAssociation.getUsername())
                .withString(PASSWORD_KEY, configurationFileAssociation.getPassword())
                .withString(CONFIG_NAME_KEY, configurationFileAssociation.getConfigName()));
    }


    public ConfigurationFileAssociation findById(String username){
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(USERNAME_KEY + " = :v_username")
                .withValueMap(new ValueMap().with(
                        ":v_username",
                        username));

        Item item = this.table.query(querySpec).iterator().next();

        ConfigurationFileAssociation configurationFileAssociation = new ConfigurationFileAssociation();
        configurationFileAssociation.setUsername(item.get(USERNAME_KEY).toString());
        configurationFileAssociation.setPassword(item.get(PASSWORD_KEY).toString());
        configurationFileAssociation.setConfigName(item.get(CONFIG_NAME_KEY).toString());

        return configurationFileAssociation;
    }

}
