package com.ad.tracker.repository;

import com.ad.tracker.model.AdImpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdImpressionRepository extends JpaRepository<AdImpression, Long> {
}