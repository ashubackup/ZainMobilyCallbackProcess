package com.vision.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_unsubscription")
public class TblUnsubscription {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Integer id;
	private String ani;
	private LocalDateTime dateTime;
	private LocalDate unsubDateTime;
	private String countryId;
	private String requestId;
	private String operatorId;
	private String type;
	private String subType;
	private String operator;
	private String unsubStatus;

}
