package com.finops.spotprice.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class EstruturaTabela {

	private String cloudName;

	private String instanceType;

	private String region;

	private String productDescription;

	private BigDecimal priceSpot;
	
	private BigDecimal priceNormal;

	private String dataReq;

}
