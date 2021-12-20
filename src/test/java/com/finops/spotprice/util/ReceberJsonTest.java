package com.finops.spotprice.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.finops.spotprice.SpotpriceApplicationTests;
import com.google.gson.JsonObject;

@RunWith(SpringRunner.class)
@SpringBootTest
class ReceberJsonTest extends SpotpriceApplicationTests{

	JsonObject jsonObjeto;
	
	ReceberJson jsonClass = new ReceberJson();
	
	@Test
	public void receberJson_Sucesso() {
		
		String URL = "https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27";
		
		jsonObjeto = jsonClass.requisitarJson(URL);
		
		assertNotNull(jsonObjeto);
		
	}
	
	@Test
	public void receberJson_formatoUrlIncorreto_Erro() {
		
		String URL = "Url Errada";
		
		jsonObjeto = jsonClass.requisitarJson(URL);
		
		assertNull(jsonObjeto);
		
	}
	
	@Test
	public void receberJson_urlSemResponder_Erro() {
		
		String URL = "https://essaUrlNaoResponde.com.br";
		
		jsonObjeto = jsonClass.requisitarJson(URL);
		
		assertNull(jsonObjeto);
		
	}
	
}
