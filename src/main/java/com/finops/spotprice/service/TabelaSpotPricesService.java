package com.finops.spotprice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.finops.spotprice.model.EstruturaTabela;
import com.finops.spotprice.persistence.entity.InstanceNormalPrice;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.InstanceNormalPriceRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;

@Service
public class TabelaSpotPricesService {

	@Autowired
	SpotRepository spotRepository;

	@Autowired
	InstanceNormalPriceRepository instanceRepository;

	public Iterable<EstruturaTabela> listarInstancias(String cloud, String region, String instanceType) {

		Iterable<SpotPrices> spotIt = null;
		Iterable<EstruturaTabela> tabelaIt = null;

		if (cloud.length() != 0 && region.length() != 0 && instanceType.length() != 0) {
			spotIt = spotRepository.findBycloudNameAndRegionAndInstanceType(cloud, region, instanceType);
			return adicionarInstanciasNormais(spotIt);
		}

		if (cloud.length() != 0 && region.length() != 0) {
			spotIt = spotRepository.findBycloudNameAndRegion(cloud, region);
			return adicionarInstanciasNormais(spotIt);
		}

		if (cloud.length() != 0 && instanceType.length() != 0) {
			spotIt = spotRepository.findBycloudNameAndInstanceType(cloud, instanceType);
			return adicionarInstanciasNormais(spotIt);
		}

		if (region.length() != 0) {
			spotIt = spotRepository.findByregion(region);
			return adicionarInstanciasNormais(spotIt);
		}

		if (instanceType.length() != 0) {
			spotIt = spotRepository.findByinstanceType(instanceType);
			return adicionarInstanciasNormais(spotIt);
		}

		if (cloud.length() != 0) {
			spotIt = spotRepository.findBycloudName(cloud);
			return adicionarInstanciasNormais(spotIt);
		}

		return tabelaIt;
	}

	public Iterable<EstruturaTabela> adicionarInstanciasNormais(Iterable<SpotPrices> spotIt) {

		List<InstanceNormalPrice> instanceNormal = instanceRepository.findAll();
		List<EstruturaTabela> estruturaTabelaList = new ArrayList<EstruturaTabela>();

		Iterable<EstruturaTabela> tabelaIt = null;

		for (SpotPrices spotPrices : spotIt) {

			boolean possuiInstanceNormal = false;

			for (InstanceNormalPrice instanceNormalPrice : instanceNormal) {

				if (spotPrices.getCloudName().contains(instanceNormalPrice.getCloudName())
						&& spotPrices.getInstanceType().contains(instanceNormalPrice.getInstanceType())
						&& spotPrices.getRegion().contains(instanceNormalPrice.getRegion())
						&& spotPrices.getProductDescription().contains(instanceNormalPrice.getProductDescription())) {

					EstruturaTabela estruturaTabela = new EstruturaTabela();

					estruturaTabela.setCloudName(spotPrices.getCloudName());
					estruturaTabela.setInstanceType(spotPrices.getInstanceType());
					estruturaTabela.setDataReq(spotPrices.getDataReq());
					estruturaTabela.setProductDescription(spotPrices.getProductDescription());
					estruturaTabela.setRegion(spotPrices.getRegion());
					estruturaTabela.setPriceSpot(spotPrices.getPrice());
					estruturaTabela.setPriceNormal(instanceNormalPrice.getPrice());

					estruturaTabelaList.add(estruturaTabela);

					possuiInstanceNormal = true;
				}

			}

			if (!possuiInstanceNormal) {

				EstruturaTabela estruturaTabela = new EstruturaTabela();

				BigDecimal valorZerado = new BigDecimal("0");

				estruturaTabela.setCloudName(spotPrices.getCloudName());
				estruturaTabela.setInstanceType(spotPrices.getInstanceType());
				estruturaTabela.setDataReq(spotPrices.getDataReq());
				estruturaTabela.setProductDescription(spotPrices.getProductDescription());
				estruturaTabela.setRegion(spotPrices.getRegion());
				estruturaTabela.setPriceSpot(spotPrices.getPrice());

				estruturaTabelaList.add(estruturaTabela);

			}

		}

		tabelaIt = estruturaTabelaList;

		return tabelaIt;
	}

}
