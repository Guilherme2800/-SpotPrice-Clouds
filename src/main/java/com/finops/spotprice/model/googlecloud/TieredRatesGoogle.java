package com.finops.spotprice.model.googlecloud;

import lombok.Data;

@Data
public class TieredRatesGoogle {

	private int startUsageAmount;
	private UnitPriceGoogle unitPrice = new UnitPriceGoogle();
	
}
