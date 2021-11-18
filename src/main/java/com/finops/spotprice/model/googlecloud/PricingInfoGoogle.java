package com.finops.spotprice.model.googlecloud;

import lombok.Data;

@Data
public class PricingInfoGoogle {

	private String summary;
	private PricingExpressionGoogle pricingExpression = new PricingExpressionGoogle();
	private String effectiveTime;
	
}
