package com.spotonresponse.adapter.repo;

import com.spotonresponse.adapter.model.Configuration;
import org.assertj.core.api.OptionalAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ConfigurationRepositoryTest {

    @Autowired
    ConfigurationRepository configurationRepository;

    @Test
    public void testSaveOneThenGetIt() {

        Configuration configuration = new Configuration();
        configuration.setId("CVS");
        configuration.setCategory("CVS-Category");
        configurationRepository.save(configuration);
        configuration = new Configuration();
        configuration.setId("Costco");
        configurationRepository.save(configuration);

        Optional<Configuration> config = configurationRepository.findById("CVS");
        List<String> idList = configurationRepository.listCSVConfigurationName();
        List<Configuration> configurationList = configurationRepository.findAll();
    }

    @Test
    public void testListOfConfiguration() {

        List<Configuration> configurationList = configurationRepository.findAll();
    }
}
