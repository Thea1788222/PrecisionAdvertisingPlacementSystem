package com.ad.tracker.repository;

import com.ad.tracker.model.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
}