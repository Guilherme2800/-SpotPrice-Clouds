package com.finops.spotprice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.finops.spotprice.model.EstruturaTabela;
import com.finops.spotprice.service.TabelaSpotPricesService;

import io.swagger.annotations.ApiOperation;

@Controller
public class TabelaSpotPricesController {

	@Autowired
	TabelaSpotPricesService tabelaService;

	@ApiOperation("API responsável por retornar a tela inicial da tabela de preços spot.")
	@GetMapping("/")
	public String telaTabela() {
		return "paginaPrincipal/tabelaSpot";
	}

	@ApiOperation("API responsável por retornar o resultado da pesquisa a partir dos filtros inseridos.")
	@RequestMapping(method = RequestMethod.GET, value = "/listarSpot")
	public ModelAndView listar(String cloud, String region, String instanceType) {
		ModelAndView mdv = new ModelAndView("paginaPrincipal/tabelaSpot");
		Iterable<EstruturaTabela> spotIt = tabelaService.listarInstancias(cloud, region, instanceType);
		mdv.addObject("spots", spotIt);
		return mdv;
	}

}
