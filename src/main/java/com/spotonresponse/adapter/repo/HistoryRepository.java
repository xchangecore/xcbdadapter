package com.spotonresponse.adapter.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import com.spotonresponse.adapter.model.UploadHistory;

@Repository
@Transactional
public interface HistoryRepository extends JpaRepository<UploadHistory, Integer> {

    /*
     * @Query("SELECT c FROM Configuration c WHERE c.id=(:id)")
     * Optional<Configuration> findById(@Param("id") String id);
     * 
     * @Query("SELECT c.id FROM Configuration c") List<String> listIds();
     */
}
