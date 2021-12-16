package com.finops.spotprice.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table (name = "pricehistory")
@Data
public class PriceHistorySpot {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "cod_history")
	private Long codHistory;
	
	@Column (name = "cod_spot")
	private Long codSpot;
	
	private double price;
	
	@Column (name = "data_req")
	private String dataReq;

}
