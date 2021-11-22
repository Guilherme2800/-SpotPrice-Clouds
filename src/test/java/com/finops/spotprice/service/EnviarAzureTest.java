package com.finops.spotprice.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.finops.spotprice.SpotPriceApplicationTests;
import com.finops.spotprice.model.azurecloud.SpotAzure;
import com.finops.spotprice.model.azurecloud.SpotAzureArray;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestInstance(Lifecycle.PER_CLASS)
public class EnviarAzureTest extends SpotPriceApplicationTests{

	@Autowired
	EnviarAzure envioAzure;

	SpotAzure spot;
	PreparedStatement pstm;
	Connection conexao;

	@BeforeAll
	public void init() {
		spot = new SpotAzure();
		pstm = null;
	}

	@Test
	public void testSolicitarObjetoAzureSucesso() {
		SpotAzureArray azureArray = envioAzure.solicitarObjetoAzure(envioAzure.URL);
		assertNotNull(azureArray);
	}

	@Test
	public void testSelectSpotPricesSucesso() {
		
		conexao = ConexaoMariaDb.conectar();

		spot.setEffectiveStartDate("2021-11-01");
		spot.setLocation("FR Central");
		spot.setProductName("Virtual Machines Easv5 Series Windows");
		spot.setSkuName("E32as v5 Spot");
		spot.setUnitPrice(1.44);

		
		ResultSet result = envioAzure.selectSpotPrices(pstm, conexao, spot);
		
		ConexaoMariaDb objetoMariaDb = new ConexaoMariaDb();	
		objetoMariaDb.desconectar();
		
		try {
			assertTrue(result.next());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void testSelectSpotPricesConexaoNull() {

		spot.setEffectiveStartDate("2021-11-01");
		spot.setLocation("FR Central");
		spot.setProductName("Virtual Machines Easv5 Series Windows");
		spot.setSkuName("E32as v5 Spot");
		spot.setUnitPrice(1.44);

		assertThrows(NullPointerException.class, () -> {
			envioAzure.selectSpotPrices(pstm, conexao, spot);
		});

	}

	@Test
	public void testSelectSpotPricesSpotInexistente(){
		
		conexao = ConexaoMariaDb.conectar();

		spot.setEffectiveStartDate("2021-11-01");
		spot.setLocation("FR Central");
		spot.setProductName("Virtual Machines Easv5 Series Windows");
		spot.setSkuName("E32as v5 Spotst");
		spot.setUnitPrice(1.44);
		
		ResultSet result = envioAzure.selectSpotPrices(pstm, conexao, spot);
		
		ConexaoMariaDb objetoMariaDb = new ConexaoMariaDb();	
		objetoMariaDb.desconectar();
		
		try {
			assertFalse(result.next());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
