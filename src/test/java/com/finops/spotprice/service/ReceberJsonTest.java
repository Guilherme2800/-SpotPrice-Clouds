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

import com.google.gson.JsonObject;

@RunWith(SpringRunner.class)
@SpringBootTest
class ReceberJsonTest extends SpotPriceApplicationTests {

	static Date data;
	static SimpleDateFormat sdf;
	static JsonObject json;

	@Autowired
	private ReceberJson receberJson;

	@BeforeAll
	public static void init() {
		
		sdf = new SimpleDateFormat("yyyy-MM");
		data = new Date(System.currentTimeMillis());
		json = null;
		
	}

	@Test
	public void testReceberJsonAzureSucesso() {

		json = receberJson.requisitarJson(
				"https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27%20and%20endswith(meterName,%20%27Spot%27)%20and%20effectiveStartDate%20eq%20"
						+ sdf.format(data) + "-01");

		assertNotNull(json);

	}

	@Test
	public void testReceberJsonAzureErro() {

		json = receberJson.requisitarJson(
				"https://prices.azur.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27%20and%20endswith(meterName,%20%27Spot%27)%20and%20effectiveStartDate%20eq%20"
						+ sdf.format(data) + "-01");

		assertNull(json);
	}

	@Test
	public void testReceberJsonGoogleSucesso() {

		json = receberJson.requisitarJson(
				"https://cloudbilling.googleapis.com/v1/services/6F81-5844-456A/skus?key=AIzaSyA9eUyryawCIAgta6f2TzUgVEijCmvauns");

		assertNotNull(json);
		
	}

	@Test
	public void testReceberJsonGoogleErro() {
		
		json = receberJson.requisitarJson(
				"https://cloudbilling.googleapi.com/v1/services/6F81-5844-456A/skus?key=AIzaSyA9eUyryawCIAgta6f2TzUgVEijCmvauns");

		assertNull(json);
		
	}

}