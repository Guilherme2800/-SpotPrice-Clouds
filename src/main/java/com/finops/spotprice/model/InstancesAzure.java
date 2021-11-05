package com.finops.spotprice.model;


import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table (name = "azure")
public class InstancesAzure implements Serializable{
	
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	Long id;
	
	@Column(name="currency_code")
	String currencyCode;
	
	@Column(name="tier_minimum_units")
	int tierMinimumUnits;
	
	@Column(name="retail_price")
	double retailPrice;
	
	@Column(name="unit_price")
	double unitPrice;
	
	@Column(name="arm_region_name")
	String armRegionName;
	
	@Column(name="location")
	String location;
	
	@Column(name="effective_start_date")
	String effectiveStartDate;
	
	@Column(name="meter_id")
	String meterId;
	
	@Column(name="meter_name")
	String meterName;
	
	@Column(name="product_id")
	String productId;
	
	@Column(name="sku_id")
	String skuId;
	
	@Column(name="product_name")
	String productName;
	
	@Column(name="sku_name")
	String skuName;
	
	@Column(name="service_name")
	String serviceName;
	
	@Column(name="service_id")
	String serviceId;
	
	@Column(name="service_family")
	String serviceFamily;
	
	@Column(name="unit_of_measure")
	String unitOfMeasure;
	
	@Column(name="typ_e")
	String type;
	
	@Column(name="is_primary_meter_region")
	boolean isPrimaryMeterRegion;
	
	@Column(name="arm_sku_name")
	String armSkuName;
	
}
