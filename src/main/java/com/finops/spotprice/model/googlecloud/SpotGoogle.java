package com.finops.spotprice.model.googlecloud;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SpotGoogle {
	
	private String name;
	private CategoryGoogle category = new CategoryGoogle();
	private String description;
	List<PricingInfoGoogle> pricingInfo = new ArrayList<PricingInfoGoogle>();
}
