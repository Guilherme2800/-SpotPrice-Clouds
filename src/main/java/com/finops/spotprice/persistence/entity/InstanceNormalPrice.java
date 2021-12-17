package com.finops.spotprice.persistence.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table (name = "instanceprices")
public class InstanceNormalPrice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long cod_instance;
	
	@Column(name = "cloud_name")
	private String cloudName;
	
	@Column(name = "instance_type")
	private String instanceType;
	
	private String region;

	@Column(name = "product_description")
	private String productDescription;
	
	private BigDecimal price;
	
	@Column(name = "data_req")
	private String dataReq;
	
}
