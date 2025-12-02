package com.ad.management.repository;

import com.ad.management.model.Advertiser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertiserRepository extends JpaRepository<Advertiser, Long> {
    
    @Query("SELECT a FROM Advertiser a WHERE " +
           "(:name IS NULL OR a.name LIKE %:name%)")
    Page<Advertiser> findByNameContainingIgnoreCase(
        @Param("name") String name, 
        Pageable pageable
    );
}