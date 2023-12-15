package com.vision.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision.entity.TblBillingFailed;
@Repository
public interface TblBillingFailedRepo extends JpaRepository<TblBillingFailed, Integer>{

}
