package com.finops.spotprice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.service.SpotPricesService;

import io.swagger.annotations.ApiOperation;

@RestController
public class SpotPricesController {

	@Autowired
	private SpotPricesService spotPricesService;
	
	@ApiOperation("API responsável por listar as instâncias Spot das três Clouds, sem filtros. ")
	@GetMapping("/listar")
	public ResponseEntity<Page<SpotPrices>> listar(Pageable pageable){
		Page<SpotPrices> spotPrices = spotPricesService.listar(pageable);
		return ResponseEntity.ok(spotPrices);
	}
	
	@ApiOperation("API responsável por listar as instâncias Spot da Cloud especificada.")
	@GetMapping("/listar/cloud/{cloudName}")
	public ResponseEntity<Page<SpotPrices>> listarCloud(@PathVariable String cloudName, Pageable pageable){
		Page<SpotPrices> spotPrices = spotPricesService.listarCloud(cloudName, pageable);
		return ResponseEntity.ok(spotPrices);
	}

	@ApiOperation("API responsável por listar as instâncias Spot das Clouds trabalhadas, filtrando a região.")
	@GetMapping("/listar/region/{region}")
	public ResponseEntity<Page<SpotPrices>> listarRegion(@PathVariable String region, Pageable pageable){
		Page<SpotPrices> spotPrices = spotPricesService.listarRegion(region, pageable);
		return ResponseEntity.ok(spotPrices);
	}
	
	@ApiOperation("API responsável por listar as instâncias Spot das Clouds trabalhadas, fitrando por tipo de instância.")
	@GetMapping("/listar/instanceType/{instanceType}")
	public ResponseEntity<Page<SpotPrices>> listarInstanceType(@PathVariable String instanceType, Pageable pageable){
		Page<SpotPrices> spotPrices = spotPricesService.listarInstanceType(instanceType, pageable);
		return ResponseEntity.ok(spotPrices);
	}
	
	@ApiOperation("API responsável por listar as instâncias Spot filtrando a Cloud e a região.")
	@GetMapping("/listar/cloud/{cloudName}/region/{region}")
	public ResponseEntity<Page<SpotPrices>> listarCloudRegion(@PathVariable String cloudName, @PathVariable String region, Pageable pageable){
		Page<SpotPrices> spotPrices = spotPricesService.listarCloudRegion(cloudName, region, pageable);
		return ResponseEntity.ok(spotPrices);
	}
	
	@ApiOperation("API responsável por listar as instâncias Spot filtrando a Cloud e o tipo de instância.")
	@GetMapping("/listar/cloud/{cloudName}/instanceType/{instanceType}")
	public ResponseEntity<Page<SpotPrices>> listarCloudInstanceType(@PathVariable String cloudName, @PathVariable String instanceType, Pageable pageable){
		Page<SpotPrices> spotPrices = spotPricesService.listarCloudInstancetype(cloudName, instanceType, pageable);
		return ResponseEntity.ok(spotPrices);
	}
	
	@ApiOperation("API responsável por listar as instâncias Spot filtrando a Cloud, região e tipo da instância.")
	@GetMapping("/listar/cloud/{cloudName}/region/{region}/instanceType/{instanceType}")
	public ResponseEntity<Page<SpotPrices>> listarCloudRegionInstanceType(@PathVariable String cloudName, @PathVariable String region, @PathVariable String instanceType, Pageable pageable){
		Page<SpotPrices> spotPrices = spotPricesService.listarCloudRegionInstanceType(cloudName, region, instanceType, pageable);
		return ResponseEntity.ok(spotPrices);
	}
}
