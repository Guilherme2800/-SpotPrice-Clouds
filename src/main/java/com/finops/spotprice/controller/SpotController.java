package com.finops.spotprice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.finops.spotprice.repository.SpotRepository;
import com.finops.spotprice.model.InstancesAzure;
import com.finops.spotprice.model.SpotAzure;

@RestController("/spot")
public class SpotController {

	@Autowired
	private SpotRepository spotRepository;
	
	@GetMapping("/listarAzure")
	public List<InstancesAzure> listarAzure(){
		return spotRepository.findAll();
	}
	
	@PostMapping("/inserirAzure")
	public void inserirAzure(@RequestBody InstancesAzure spot){
		spotRepository.save(spot);
	}
	
}
