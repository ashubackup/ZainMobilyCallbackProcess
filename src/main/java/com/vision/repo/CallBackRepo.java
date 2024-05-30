package com.vision.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision.entity.AllCallbacks;

@Repository
public interface CallBackRepo extends JpaRepository<AllCallbacks, Integer>{
	
	@Query(value="select * from all_callbacks where status='0' AND TYPE=:type",nativeQuery = true)
	List<AllCallbacks> findByType(@Param("type") String type);
	
	@Query(value="SELECT * FROM all_callbacks WHERE STATUS='0' AND TYPE=:type AND operator=:operator",nativeQuery = true)
	List<AllCallbacks> findByOperator(@Param("type") String type, @Param("operator") String operator);

}
