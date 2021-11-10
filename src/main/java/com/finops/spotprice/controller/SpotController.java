package com.finops.spotprice.controller;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;

import com.finops.spotprice.repository.SpotRepository;
import com.finops.spotprice.service.EnviarAws;
import com.finops.spotprice.service.EnviarAzure;
import com.finops.spotprice.service.JsonForObjectAzure;
import com.finops.spotprice.model.InstancesAzure;
import com.finops.spotprice.model.SpotAzure;

@RestController("/spot")
public class SpotController {

	@Autowired
	private SpotRepository spotRepository;

	@GetMapping("/listarAzure")
	public List<InstancesAzure> listarAzure() {
		return spotRepository.findAll();
	}

	
	@PostMapping("/inserirAzure")
	public void inserirAzure(@RequestBody InstancesAzure spot) {
		spotRepository.save(spot);
	}

	
	public void enviarAzure() {

		EnviarAzure azure = new EnviarAzure();
		azure.enviar();

	}
	
	public void enviarAws(){
		
		int numero=0;
		
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		DescribeRegionsResult regions_response = ec2.describeRegions();

		System.out.println("Iniciando Envio da AWS...");

		for (Region region : regions_response.getRegions()) {
			System.out.println("\n--------Enviando região: " + region.getRegionName() + "----------");
			numero++;
			EnviarAws enviar = new EnviarAws();
			enviar.enviarObjeto(region.getRegionName());
			System.out.println("\n--------Conteúdo Enviado----------");
		}

		System.out.println("Finalizado Envio da AWS..." + numero);
	}

}
