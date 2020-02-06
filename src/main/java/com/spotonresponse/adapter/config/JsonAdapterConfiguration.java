package com.spotonresponse.adapter.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.google.cloud.datastore.*;
import com.spotonresponse.adapter.repo.DynamoDBRepository;
import com.spotonresponse.adapter.repo.unpw.ConfigurationFileAssociationDynamoDBRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class JsonAdapterConfiguration {

    private final static String packageName = "com.spotonresponse.adapter";

    private static final Logger logger = LoggerFactory.getLogger(JsonAdapterConfiguration.class);

    @Value("${DynamoDbUUID}")
    private String DynamoDbUUID;

    private String aws_access_key_id;
    private String aws_secret_access_key;
    private String amazon_endpoint;
    private String amazon_region;
    private String db_table_name;

    @Bean
    public com.spotonresponse.adapter.model.Configuration configuration() {

        return new com.spotonresponse.adapter.model.Configuration();
    }

    @Bean
    public DynamoDBRepository dynamoDBRepository() {
        DynamoDBRepository repo = new DynamoDBRepository();

        repo.init(getDynamoDB(), db_table_name);
        return repo;
    }

    @Bean
    public AmazonDynamoDB getDynamoDB(){
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("Credentials")
                .setFilter(StructuredQuery.PropertyFilter.eq("UUID", DynamoDbUUID))
                .build();

        QueryResults<Entity> results = datastore.run(query);
        Entity entity = results.next();
        aws_access_key_id = entity.getString("username");
        aws_secret_access_key = entity.getString("password");
        amazon_endpoint = entity.getString("Endpoint");
        amazon_region = entity.getString("Region");
        db_table_name = entity.getString("TableName");

        BasicAWSCredentials credentials = new BasicAWSCredentials(aws_access_key_id, aws_secret_access_key);

        return AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(
                    credentials)).withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazon_endpoint,
                    amazon_region)).build();
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("JsonAdapter-");
        executor.initialize();

        return executor;
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("JsonAdapterTask-");
        taskScheduler.setPoolSize(10);

        return taskScheduler;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {

        HibernateJpaVendorAdapter bean = new HibernateJpaVendorAdapter();
        bean.setDatabase(Database.H2);
        bean.setGenerateDdl(true);
        bean.setShowSql(false);
        return bean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {

        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setJpaVendorAdapter(jpaVendorAdapter);
        bean.setPackagesToScan(packageName);
        return bean;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {

        return new JpaTransactionManager(emf);
    }
}
