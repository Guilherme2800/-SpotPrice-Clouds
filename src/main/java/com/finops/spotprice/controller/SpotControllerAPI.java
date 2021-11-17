package com.finops.spotprice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.finops.spotprice.repository.SpotRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.finops.spotprice.model.Instances;

@RestController("/spot")
public class SpotControllerAPI {


	@Autowired
	private SpotRepository repository;
	private List<Instances> instanciasCloudRegion;
	
	@GetMapping("/listar")
	public List<Instances> listar(){
		return repository.findAll();
	}
	
	@GetMapping("/listar/cloud/{cloudName}")
	public List<Instances> listarCloud(@PathVariable String cloudName){
		return repository.findBycloudName(cloudName);
	}
	
	
	@GetMapping("/listar/cloud/{cloudName}/region/{region}")
	public List<Instances> listarCloudRegion(@PathVariable String cloudName, @PathVariable String region){
		return repository.findBycloudNameAndRegion(cloudName, region);
	}
	
	@GetMapping("/listar/cloud/{cloudName}/region/{region}/instanceType/{instanceType}")
	public List<Instances> listarCloudRegionInstanceType(@PathVariable String cloudName, @PathVariable String region, @PathVariable String instanceType){
		return repository.findBycloudNameAndRegionAndInstanceType(cloudName, region, instanceType);
	}
	
	@GetMapping("/listar/cloud/{cloudName}/instanceType/{instanceType}")
	public List<Instances> listarCloudInstancetype(@PathVariable String cloudName, @PathVariable String instanceType){
		return repository.findBycloudNameAndInstanceType(cloudName, instanceType);
	}
	

	@GetMapping("/listar/tipoInstancia/{tipoInstancia}")
	public List<Instances> listarTipoInstancia(@PathVariable String tipoInstancia){
		return repository.findByinstanceType(tipoInstancia);
	}
	
}
	
	
