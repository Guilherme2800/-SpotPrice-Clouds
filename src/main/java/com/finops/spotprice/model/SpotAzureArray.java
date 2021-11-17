package com.finops.spotprice.model;


import java.util.ArrayList;
import java.util.List;


import lombok.Data;

@Data
public class SpotAzure{
	
	String billingCurrency;
	String CustomerEntityId;	
	String CustomerEntityType;
	String NextPageLink;	
	int Count;
	
	public List<InstancesAzure> Items = new ArrayList<InstancesAzure>();
	
}
