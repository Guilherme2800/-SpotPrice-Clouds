package com.finops.spotprice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.finops.spotprice.model.EstruturaTabela;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.service.TabelaSpotPricesService;

@Controller
public class TabelaSpotPricesController {

	@Autowired
	TabelaSpotPricesService tabelaService;

	@GetMapping("/")
	public String telaTabela() {
		return "paginaPrincipal/tabelaSpot";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/listarSpot")
	public ModelAndView listar(String cloud, String region, String instanceType) {
		ModelAndView mdv = new ModelAndView("paginaPrincipal/tabelaSpot");
		Iterable<EstruturaTabela> spotIt = tabelaService.listarInstancias(cloud, region, instanceType);
		mdv.addObject("spots", spotIt);
		return mdv;
	}

}
