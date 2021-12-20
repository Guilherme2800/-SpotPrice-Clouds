package com.finops.spotprice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.SpotRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpotPriceEndpointTest extends SpotpriceApplicationTests{

	@Autowired
	private TestRestTemplate restTemplate;
	
	@LocalServerPort
	private int port;
	
	@Test
	public void listarTodasCloudReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listar", String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void ListarComFiltroCloudNameReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listar/cloud/aws", String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void ListarComFiltroRegionTypeReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listar/region/eu-north-1", String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void ListarComFiltroInstanceTypeReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listar/instanceType/c5n.4xlarge", String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void ListarComFiltroCloudAndRegionTypeReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listar/cloud/aws/region/eu-north-1", String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void ListarComFiltroCloudAndInstanceTypeReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listar/cloud/aws/instanceType/c5n.4xlarge", String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	@Test
	public void ListarComFiltroCloudAndRegionAndInstanceTypeReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.getForEntity("/listar/cloud/aws/region/eu-north-1/instanceType/c5n.4xlarge", String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("200"));
	}
	
	
	@Test
	public void listarTodasCloudReturnStatusCode404() {
		ResponseEntity<String> response = restTemplate.getForEntity("/lista", String.class);
		String statusCode = response.getStatusCode().toString();
		assertTrue(statusCode.contains("404"));
	}
	
}
