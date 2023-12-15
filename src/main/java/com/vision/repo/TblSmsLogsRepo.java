package com.vision.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision.entity.TblSmsLogs;

@Repository
public interface TblSmsLogsRepo extends JpaRepository<TblSmsLogs, Integer> {
	
	TblSmsLogs findByAni(String ani);

}
