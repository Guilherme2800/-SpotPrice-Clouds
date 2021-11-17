package com.finops.spotprice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table (name = "spotprices")
public class Instances {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long cod_spot;
	
	@Column(name = "region")
	private String region;
	
	@Column(name = "cloud_name")
	private String cloudName;
	
	@Column(name = "instance_type")
	private String instanceType;
	
	@Column(name = "product_description")
	private String productDescription;
	
	@Column(name = "price")
	private double price;
	
	@Column(name = "data_req")
	private String dataReq;
	
}
