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
@Table (name = "aws")
public class InstancesAws {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@Column(name = "availability_zone")
	private String availabilityZone;
	
	@Column(name = "instance_type")
	private String instanceType;
	
	@Column(name = "product_description")
	private String productDescription;
	
	@Column(name = "spot_price")
	private double spotPrice;
	
	@Column(name = "time_stamp")
	private String timeStamp;
	
}
