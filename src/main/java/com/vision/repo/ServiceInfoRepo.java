package com.vision.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision.entity.ServiceInfo;

@Repository
public interface ServiceInfoRepo extends JpaRepository<ServiceInfo, Integer>{
	
	@Query(value="select * from service_info where status=:status",nativeQuery = true)
	ServiceInfo findByStatus(@Param("status") String status);
	

}
