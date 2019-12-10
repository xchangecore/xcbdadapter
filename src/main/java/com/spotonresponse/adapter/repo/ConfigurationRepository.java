package com.spotonresponse.adapter.repo;

import com.spotonresponse.adapter.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ConfigurationRepository extends JpaRepository<Configuration, String> {

    @Query("SELECT c FROM Configuration c WHERE c.id=(:id)")
    Optional<Configuration> findById(@Param("id") String id);

    @Query("SELECT c.id FROM Configuration c WHERE c.json_ds=null")
    List<String> listCSVConfigurationName();

    @Query("SELECT c.id FROM Configuration c")
    List<String> listConfigurationName();
}
