package com.finops.spotprice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.finops.spotprice.repository.SpotRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.finops.spotprice.model.SpotPrices;

@RestController
public class SpotControllerAPI {


	@Autowired
	private SpotRepository repository;
	
	@GetMapping("/listar")
	public Page<SpotPrices> listar(Pageable pageable){
		return repository.findAll(pageable);
	}
	
	@GetMapping("/listar/cloud/{cloudName}")
	public Page<SpotPrices> listarCloud(@PathVariable String cloudName, Pageable pageable){
		return repository.findBycloudName(cloudName, pageable);
	}
	
	@GetMapping("/listar/tipoInstancia/{tipoInstancia}")
	public Page<SpotPrices> listarTipoInstancia(@PathVariable String tipoInstancia, Pageable pageable){
		return repository.findByinstanceType(tipoInstancia, pageable);
	}
	
	
	@GetMapping("/listar/cloud/{cloudName}/region/{region}")
	public Page<SpotPrices> listarCloudRegion(@PathVariable String cloudName, @PathVariable String region, Pageable pageable){
		return repository.findBycloudNameAndRegion(cloudName, region, pageable);
	}
	
	@GetMapping("/listar/cloud/{cloudName}/instanceType/{instanceType}")
	public Page<SpotPrices> listarCloudInstancetype(@PathVariable String cloudName, @PathVariable String instanceType, Pageable pageable){
		return repository.findBycloudNameAndInstanceType(cloudName, instanceType, pageable);
	}
	
	@GetMapping("/listar/cloud/{cloudName}/region/{region}/instanceType/{instanceType}")
	public Page<SpotPrices> listarCloudRegionInstanceType(@PathVariable String cloudName, @PathVariable String region, @PathVariable String instanceType, Pageable pageable){
		return repository.findBycloudNameAndRegionAndInstanceType(cloudName, region, instanceType, pageable);
	}
	
	
	
}
	
	
