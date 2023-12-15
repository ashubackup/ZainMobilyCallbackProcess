package com.vision.repo;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.vision.entity.TblSubscription;

@Repository
public interface TblSubscriptionRepo extends JpaRepository<TblSubscription, Integer>{
	
	@Query(value="select * from tbl_subscription where ani=:ani and operatorId=:operatorId", nativeQuery=true)
	List<TblSubscription> findByAni(@Param("ani") String ani, @Param("operatorId") String operatorId);

}
