package com.vision.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision.entity.TblMessage;

@Repository
public interface TblMessageRepo extends JpaRepository<TblMessage, Integer>{
	@Query(value="select * from tbl_message where status=:status",nativeQuery = true)
	TblMessage findByStatus(@Param("status") String status);

}
