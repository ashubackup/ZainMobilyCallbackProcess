package com.vision.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision.entity.TblBillingSuccess;

@Repository
public interface TblBillingSuccessRepo extends JpaRepository<TblBillingSuccess, Integer>{
	@Query(value="select * from tbl_billing_success where ani=:ani and operatorId=:operatorId", nativeQuery=true)
	List<TblBillingSuccess> findByAni(@Param("ani") String ani, @Param("operatorId") String operatorId);

}
