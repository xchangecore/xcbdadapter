package com.spotonresponse.adapter.repo;

import com.spotonresponse.adapter.model.ConfigurationFileAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface ConfigurationFileAssociationRepository extends JpaRepository<ConfigurationFileAssociation, String> {
    boolean existsByUsername(String username);
}
