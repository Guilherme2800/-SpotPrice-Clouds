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
import com.finops.spotprice.model.googlecloud.SpotGoogleArray;
import com.google.gson.JsonObject;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JsonForObjectGoogleTest extends SpotPriceApplicationTests {

	static SpotGoogleArray googleArray;

	@Autowired
	JsonForObjectGoogle objectGoogle;

	@BeforeAll
	public static void init() {

		googleArray = new SpotGoogleArray();

	}

	@Test
	public void testObjetoCorreto() {

		googleArray = objectGoogle.converter(
				"https://cloudbilling.googleapis.com/v1/services/6F81-5844-456A/skus?key=AIzaSyA9eUyryawCIAgta6f2TzUgVEijCmvauns");
		assertNotNull(googleArray);

	}

	@Test
	public void testObjetoNull() {

		// URL errada
		googleArray = objectGoogle.converter(
				"https://cloudbilling.googleapi.com/v1/services/6F81-5844-456A/skus?key=AIzaSyA9eUyryawCIAgta6f2TzUgVEijCmvauns");
		assertNull(googleArray);

	}

}
