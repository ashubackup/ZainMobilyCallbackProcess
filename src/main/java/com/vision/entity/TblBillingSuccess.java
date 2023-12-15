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
@Table(name="tbl_billing_success")
public class TblBillingSuccess {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Integer id;
	private String ani;
	private LocalDateTime subDateTime;
	private LocalDateTime lastBilledDate;
	private LocalDate nextBilledDate;
	private String requestId;
	private String TransactionId;
	private String shortCode;
	private String channelId;
	private String applicationId;
	private String countryId;
	private String operatorId;
	private String price;
	private String pack;
	private String type;
	private String subType;
	private String mAct;
	private String mDeact;
	private LocalDateTime processDatetime;
	private String recordStatus;
	private String language;
	private String billingType;
	private String billingStatus;
	private String operator;
	private String status;
	

}
