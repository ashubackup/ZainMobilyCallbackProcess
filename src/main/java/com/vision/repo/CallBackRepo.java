package com.vision.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision.entity.AllCallbacks;

@Repository
public interface CallBackRepo extends JpaRepository<AllCallbacks, Integer>{
	
	@Query(value="select * from all_callbacks where status=:status",nativeQuery = true)
	List<AllCallbacks> findByStatus(@Param("status") String status);

}
