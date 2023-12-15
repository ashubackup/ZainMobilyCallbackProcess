package com.vision.entity;

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
@Table(name="service_info")
public class ServiceInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String cpId;
	private String cpName;
	private String applicationId;
	private String application;
	private String countryId;
	private String country;
	private String operatorId;
	private String operator;
	private String apiKey;
    private String apiSecret;
    private String shortcode;
    private String pubId;
    private String adpartnername; 
    private String price;
    private String status;
    private LocalDateTime dateTime;
    private String subKey;
    private String unsubKey;
    
    

}
