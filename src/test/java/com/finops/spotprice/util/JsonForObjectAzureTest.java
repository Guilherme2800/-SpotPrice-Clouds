package com.finops.spotprice.util;

import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.finops.spotprice.SpotpriceApplicationTests;
import com.finops.spotprice.model.InstancesAzureArray;

@RunWith(SpringRunner.class)
@SpringBootTest
class JsonForObjectAzureTest extends SpotpriceApplicationTests {

	private final String URL = "https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27";

	private JsonForObjectAzure jsonForAzure = new JsonForObjectAzure();

	@Test
	public void receberObjetoAzureArray_Sucesso() {

		InstancesAzureArray azureArray = jsonForAzure.converter(URL);

		assertNotNull(azureArray);
	}
	
	@Test
	public void receberObjetoAzureArray_Erro() {

		String url = "url errada";
		
		InstancesAzureArray azureArray = jsonForAzure.converter(url);

		assertNull(azureArray);
	}

}
