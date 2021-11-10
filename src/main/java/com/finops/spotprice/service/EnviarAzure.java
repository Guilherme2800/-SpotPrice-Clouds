package com.finops.spotprice.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finops.spotprice.model.InstancesAzure;
import com.finops.spotprice.model.SpotAzure;
import com.finops.spotprice.repository.SpotRepository;

@Component
public class EnviarAzure {
	
	@Autowired
	private SpotRepository spotRepository; 

	public void enviar() {
		
		JsonForObjectAzure converter = new JsonForObjectAzure();

		Date data = new Date(System.currentTimeMillis());
		SimpleDateFormat formatarDate = new SimpleDateFormat("yyyy-MM");

		SpotAzure azureSpot;

		azureSpot = converter.converter(
				"https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27%20and%20endswith(meterName,%20%27Spot%27)%20and%20effectiveStartDate%20eq%20"
						+ formatarDate.format(data) + "-01");
		
		for (InstancesAzure instanciaAzure : azureSpot.getItems()) {

			if (instanciaAzure.getRetailPrice() != 0) {
				spotRepository.save(instanciaAzure);
			}

		}
		
	}
	
}
