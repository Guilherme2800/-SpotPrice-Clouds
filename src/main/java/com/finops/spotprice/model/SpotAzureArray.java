package com.finops.spotprice.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SpotAzureArray {
	
	private String billingCurrency;
	private String CustomerEntityId;	
	private String CustomerEntityType;
	private String NextPageLink;	
	private int Count;
	
	public List<SpotAzure> Items = new ArrayList<SpotAzure>();
	
}
