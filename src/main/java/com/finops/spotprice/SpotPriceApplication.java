package com.finops.spotprice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.finops.spotprice.controller.SpotController;
import com.finops.spotprice.repository.SpotRepository;

@SpringBootApplication
public class SpotPriceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotPriceApplication.class, args);
		
		SpotController spot = new SpotController();
		spot.enviarAzure();
	}

}
