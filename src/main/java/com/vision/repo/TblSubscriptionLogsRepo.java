package com.vision.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision.entity.TblSubscriptionLogs;

@Repository
public interface TblSubscriptionLogsRepo extends JpaRepository<TblSubscriptionLogs, Integer>{

}
