package com.finops.spotprice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.finops.spotprice.service.EnviarGoogle;


@SpringBootApplication
public class SpotPriceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotPriceApplication.class, args);
		
		EnviarGoogle teste = new EnviarGoogle();
		teste.enviar();
		
	}

}
