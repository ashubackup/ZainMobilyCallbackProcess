package com.vision.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision.entity.TblUnsubscription;

@Repository
public interface TblUnsubRepo extends JpaRepository<TblUnsubscription, Integer>{
	@Query(value="select * from tbl_unsubscription where ani=:ani and operatorId=:operatorId", nativeQuery=true)
	List<TblUnsubscription> findByAni(@Param("ani") String ani, @Param("operatorId") String operatorId);
}
