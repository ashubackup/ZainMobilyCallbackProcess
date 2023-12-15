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
@Table(name="tbl_billing_failed")
public class TblBillingFailed {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Integer id;
	private String ani;
	private String countryId;
	private String operatorId;
	private String price;
	private String pack;
	private String subType;
	private LocalDateTime processDatetime;
	private String recordStatus;
	private String billingType;
	private String operator;

}
