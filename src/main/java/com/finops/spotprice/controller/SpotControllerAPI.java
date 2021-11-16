package com.finops.spotprice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.finops.spotprice.repository.SpotAwsRepository;
import com.finops.spotprice.repository.SpotAzureRepository;
import com.finops.spotprice.model.InstancesAws;
import com.finops.spotprice.model.InstancesAzure;


@RestController("/spot")
public class SpotControllerAPI {

	//------AZURE------
	
	@Autowired
	private SpotAzureRepository azureRepository;

	@GetMapping("/listarAzure")
	public List<InstancesAzure> listarAzure() {
		return azureRepository.findAll();
	}

	
	//-------AWS--------

	@Autowired
	private SpotAwsRepository awsRepository;
	
	@GetMapping("/listarAws")
	public List<InstancesAws> listarAws(){
		return awsRepository.findAll();
	}

	@GetMapping("/listarAws/regiao/{Regiao}")
	public List<InstancesAws> listarAwsRegiao(@PathVariable String regiao){
		return awsRepository.findByavailabilityZone(regiao);
	}
	
	@GetMapping("/listarAws/tipoInstancia/{tipoInstancia}")
	public List<InstancesAws> listarAwsTipoInstancia(@PathVariable String tipoInstancia){
		return awsRepository.findByinstanceType(tipoInstancia);
	}
	
}
	
	
