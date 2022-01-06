package com.finops.spotprice.model;

import java.math.BigDecimal;

import javax.persistence.Column;

import com.finops.spotprice.persistence.entity.InstanceNormalPrice;

import lombok.Data;

@Data
public class InstanceNormalAws{

	private String chaveInstance;

	private String cloudName;

	private String instanceType;

	private String region;
	
	private String productDescription;

	private BigDecimal price;

	private String dataReq;

}
