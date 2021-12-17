package com.finops.spotprice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.SpotRepository;

@Service
public class TabelaSpotPricesService {

	@Autowired
	SpotRepository spotRepository;

	public Iterable<SpotPrices> listarInstancias(String cloud, String region, String instanceType) {

		Iterable<SpotPrices> spotIt = null;

		if (cloud.length() != 0 && region.length() != 0 && instanceType.length() != 0) {
			spotIt = spotRepository.findBycloudNameAndRegionAndInstanceType(cloud, region, instanceType);
			return spotIt;
		}

		if (cloud.length() != 0 && region.length() != 0) {
			spotIt = spotRepository.findBycloudNameAndRegion(cloud, region);
			return spotIt;
		}

		if (cloud.length() != 0 && instanceType.length() != 0) {
			spotIt = spotRepository.findBycloudNameAndInstanceType(cloud, instanceType);
			return spotIt;
		}

		if (region.length() != 0) {
			spotIt = spotRepository.findByregion(region);
			return spotIt;
		}

		if (instanceType.length() != 0) {
			spotIt = spotRepository.findByinstanceType(instanceType);
			return spotIt;
		}

		if (cloud.length() != 0) {
			spotIt = spotRepository.findBycloudName(cloud);
			return spotIt;
		}

		return spotIt;
	}

}
