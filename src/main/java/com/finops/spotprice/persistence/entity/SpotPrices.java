package com.finops.spotprice.persistence.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Entity
@Table (name = "spotprices")
public class SpotPrices {

	@ApiModelProperty("Código da instância no banco de dados.")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long cod_spot;
	
	@ApiModelProperty("Nome da plataforma de cloud.")
	@Column(name = "cloud_name")
	private String cloudName;
	
	@ApiModelProperty("Tipo da instância.")
	@Column(name = "instance_type")
	private String instanceType;
	
	@ApiModelProperty("Região onde a instância se encontra.")
	private String region;
	
	@ApiModelProperty("Descrição da instãncia Spot;")
	@Column(name = "product_description")
	private String productDescription;
	
	@ApiModelProperty("Preço da instância.")
	private BigDecimal price;
	
	@ApiModelProperty("Data de requisição do preço.")
	@Column(name = "data_req")
	private String dataReq;
	
}
