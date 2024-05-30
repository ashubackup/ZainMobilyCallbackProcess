package com.vision.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision.entity.ServiceInfo;

@Repository
public interface ServiceInfoRepo extends JpaRepository<ServiceInfo, Integer>{
	
	@Query(value="SELECT * FROM service_info WHERE operatorId=:operatorId AND applicationId=:applicationId",nativeQuery = true)
	ServiceInfo findByOperatorid(@Param("operatorId") String operatorId, @Param("applicationId") String applicationId);
	

}
