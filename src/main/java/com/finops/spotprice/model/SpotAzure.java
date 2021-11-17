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
@Table(name = "spotprices")
public class InstancesAzure {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long cod_spot;

	@Column(name = "region")
	String location;

	@Column(name = "price")
	double unitPrice;

	@Column(name = "data_req")
	String effectiveStartDate;

	@Column(name = "product_description")
	String productName;

	@Column(name = "instance_type")
	String skuName;

	/*
	 * String currencyCode;
	 * 
	 * int tierMinimumUnits;
	 * 
	 * double retailPrice;
	 * 
	 * String armRegionName;
	 * 
	 * String meterId;
	 * 
	 * String meterName;
	 * 
	 * String productId;
	 * 
	 * String skuId;
	 * 
	 * String serviceName;
	 * 
	 * String serviceId;
	 * 
	 * String serviceFamily;
	 * 
	 * String unitOfMeasure;
	 * 
	 * String type;
	 * 
	 * boolean isPrimaryMeterRegion;
	 * 
	 * String armSkuName;
	 * 
	 */
}
