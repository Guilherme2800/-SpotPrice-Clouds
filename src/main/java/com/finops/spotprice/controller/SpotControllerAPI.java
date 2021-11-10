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
public class SpotControllerAPI {

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


}
	
	
