package com.finops.spotprice.model;

import lombok.Data;

@Data
public class SpotAzure {

	private String location;

	private double unitPrice;

	private String effectiveStartDate;

	private String productName;

	private String skuName;

}
