package com.finops.spotprice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.SpotRepository;


@Service
public class SpotPricesService {

	@Autowired
	private SpotRepository repository;
	
	public Page<SpotPrices> listar(Pageable pageable){
		return repository.findAll(pageable);
	}
	
	public Page<SpotPrices> listarCloud(String cloudName, Pageable pageable){
		return repository.findBycloudName(cloudName, pageable);
	}
	
	public Page<SpotPrices> listarRegion(String region, Pageable pageable){
		return repository.findByregion(region, pageable);
	}
	
	public Page<SpotPrices> listarInstanceType(String instanceType, Pageable pageable){
		return repository.findByinstanceType(instanceType, pageable);
	}
	
	public Page<SpotPrices> listarCloudRegion(String cloudName, String region, Pageable pageable){
		return repository.findBycloudNameAndRegion(cloudName, region, pageable);
	}
	
	public Page<SpotPrices> listarCloudInstancetype(String cloudName, String instanceType, Pageable pageable){
		return repository.findBycloudNameAndInstanceType(cloudName, instanceType, pageable);
	}
	
	public Page<SpotPrices> listarCloudRegionInstanceType(String cloudName, String region, String instanceType, Pageable pageable){
		return repository.findBycloudNameAndRegionAndInstanceType(cloudName, region, instanceType, pageable);
	}
	
	
	
}
	
	
