package com.finops.spotprice.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.finops.spotprice.SpotPriceApplicationTests;
import com.finops.spotprice.model.azurecloud.SpotAzureArray;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JsonForObjectAzureTest extends SpotPriceApplicationTests {

	static Date data;
	static SimpleDateFormat sdf;
	static SpotAzureArray azureArray;

	@Autowired
	JsonForObjectAzure objectAzure;

	@BeforeAll
	public static void init() {

		sdf = new SimpleDateFormat("yyyy-MM");
		data = new Date(System.currentTimeMillis());
		azureArray = new SpotAzureArray();

	}

	@Test
	public void testObjetoCorreto() {

		azureArray = objectAzure.converter(
				"https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27%20and%20endswith(meterName,%20%27Spot%27)%20and%20effectiveStartDate%20eq%20"
						+ sdf.format(data) + "-01");

		assertNotNull(azureArray);

	}

	@Test
	public void testObjetoNull() {

		azureArray = objectAzure.converter(
				"https://prices.azur.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27%20and%20endswith(meterName,%20%27Spot%27)%20and%20effectiveStartDate%20eq%20"
						+ sdf.format(data) + "-01");

		assertNull(azureArray);

	}

}
