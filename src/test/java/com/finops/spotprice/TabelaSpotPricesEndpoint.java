package com.finops.spotprice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TabelaSpotPricesEndpoint {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void abrirTabelaReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void listagemNulaReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listarSpot?cloud=&region=&instanceType=",
				String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}

	@Test
	public void listarPorCloudReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listarSpot?cloud=google&region=&instanceType=",
				String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void listarPorRegionReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listarSpot?cloud=&region=asia-east2&instanceType=",
				String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void listarPorInstanceTypeReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listarSpot?cloud=&region=&instanceType=n2d custom vm ram",
				String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void listarPorCloudAndRegionReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listarSpot?cloud=google&region=asia-east2&instanceType=",
				String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void listarPorCloudAndInstanceTypeReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listarSpot?cloud=google&region=&instanceType=n2d custom vm ram",
				String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void listarPorCloudAndRegionAndInstanceTypeReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listarSpot?cloud=google&region=asia-east2&instanceType=n2d custom vm ram",
				String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
}
