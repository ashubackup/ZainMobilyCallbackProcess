package com.vision.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision.entity.TblUser;
@Repository
public interface TblUserRepo extends JpaRepository<TblUser, Integer>{
	
	@Query(value="SELECT COUNT(ani) FROM tbl_user WHERE ani=:ani",nativeQuery = true)
	Integer findByAni(@Param("ani") String ani);

}
