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
import com.finops.spotprice.model.Instances;

@RestController("/spot")
public class SpotControllerAPI {


	@Autowired
	private SpotRepository repository;
	
	@GetMapping("/listar")
	public List<Instances> listar(){
		return repository.findAll();
	}
	
	@GetMapping("/listar/cloud/{cloudName}")
	public List<Instances> listarCloud(@PathVariable String cloudName){
		return repository.findBycloudName(cloudName);
	}
	

	@GetMapping("/listar/regiao/{regiao}")
	public List<Instances> listarRegiao(@PathVariable String regiao){
		return repository.findByregion(regiao);
	}
	
	@GetMapping("/listar/tipoInstancia/{tipoInstancia}")
	public List<Instances> listarTipoInstancia(@PathVariable String tipoInstancia){
		return repository.findByinstanceType(tipoInstancia);
	}
	
}
	
	
