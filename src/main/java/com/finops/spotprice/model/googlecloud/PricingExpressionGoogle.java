package com.finops.spotprice.model.googlecloud;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PricingExpressionGoogle {

	private String usageUnit;
	List<TieredRatesGoogle> tieredRates = new ArrayList<TieredRatesGoogle>();
	
}
