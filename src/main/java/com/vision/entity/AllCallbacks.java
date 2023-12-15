package com.vision.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="all_callbacks")
public class AllCallbacks 
{
	@Id
	@GeneratedValue
	private Integer id;
	private String callback;
	private LocalDateTime dateTime;
	private String type;
	private String status;

}
