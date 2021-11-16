package com.finops.spotprice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.finops.spotprice.service.UpdateCloud;


@SpringBootApplication
public class SpotPriceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotPriceApplication.class, args);
		//UpdateCloud testes = new UpdateCloud();
		//testes.correrRegioes();
		
	}

}
