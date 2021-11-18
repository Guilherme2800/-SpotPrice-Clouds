package com.finops.spotprice.model.googlecloud;

import lombok.Data;

@Data
public class UnitPriceGoogle {

	private String currencyCode;
	private int nanos;
	private int units;
	
}
