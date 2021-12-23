package com.finops.spotprice.model;

import lombok.Data;

@Data
public class InstanceAzure {

	private String location;

	private double unitPrice;

	private String effectiveStartDate;

	private String productName;

	private String skuName;

}
