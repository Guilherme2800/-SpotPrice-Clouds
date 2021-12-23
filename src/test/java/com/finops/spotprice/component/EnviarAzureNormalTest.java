package com.finops.spotprice.component;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.finops.spotprice.SpotpriceApplicationTests;
import com.finops.spotprice.model.InstanceAzure;
import com.finops.spotprice.model.InstancesAzureArray;
import com.finops.spotprice.persistence.entity.InstanceNormalPrice;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.InstanceNormalRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;
import com.finops.spotprice.util.JsonForObjectAzure;

@ExtendWith(SpringExtension.class)
class EnviarAzureNormalTest extends SpotpriceApplicationTests {

	@InjectMocks
	private EnviarAzureNormal envioAzure = new EnviarAzureNormal();

	@Mock
	private InstanceNormalRepository instanceRepositoryMock;
	
	@Mock
	private SpotRepository spotRepositoryMock;

	InstanceNormalPrice instanceNormal;
	InstanceAzure instanceAzure;

	String dataSpotFormatada;
	
	@BeforeEach
	void setUp() {

		instanceNormal = new InstanceNormalPrice();

		instanceNormal.setCloudName("AZURE");
		instanceNormal.setDataReq("15-01-2000");
		instanceNormal.setInstanceType("kf1");
		instanceNormal.setPrice(new BigDecimal(5.33));
		instanceNormal.setRegion("russia");
		instanceNormal.setProductDescription("Teste de produto");

		instanceAzure = new InstanceAzure();

		instanceAzure.setSkuName("kf1");
		instanceAzure.setLocation("russia");
		instanceAzure.setProductName("Teste de produto");
		instanceAzure.setUnitPrice(5.33);
		instanceAzure.setEffectiveStartDate("2021-10-01T00:00:00Z");

		// Formata a data
		DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
		OffsetDateTime dataSpot = OffsetDateTime.parse(instanceAzure.getEffectiveStartDate());
		dataSpotFormatada = dataSpot.format(formatarPadrao);
	}

	@Test
	@DisplayName("Inserir instancia normal - Sucesso")
	public void insertInstanceNormal_Sucesso() {

		boolean resultado = false;

		BDDMockito.when(instanceRepositoryMock.save(ArgumentMatchers.any())).thenReturn(instanceNormal);

		resultado = envioAzure.insertInstancePrice(instanceAzure, dataSpotFormatada);

		Assertions.assertThat(resultado).isTrue();
	}
	
	@Test
	@DisplayName("Atualizar instancia normal - sucesso")
	public void updateInstanceNormal_Sucesso() {

		boolean resultado = false;

		BDDMockito.when(instanceRepositoryMock.save(ArgumentMatchers.any())).thenReturn(instanceNormal);

		resultado = envioAzure.updateInstancePrice(instanceAzure, instanceNormal, dataSpotFormatada);

		Assertions.assertThat(resultado).isTrue();

	}

	@Test
	@DisplayName("Selecionar instancia normal - sucesso")
	public void selectInstanceNormal_Sucesso() {

		InstanceNormalPrice instance = null;

		BDDMockito.when(instanceRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(instanceNormal);

		instance = envioAzure.selectInstancePrice(instanceAzure);

		Assertions.assertThat(instance).isNotNull();

	}

	@Test
	@DisplayName("Solicitar Array de instâncias Azure - sucesso")
	public void solicitarArrayAzure_Sucesso() {

		InstancesAzureArray azureArray;

		azureArray = envioAzure.solicitarObjetoAzure(
				"https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27");

		Assertions.assertThat(azureArray).isNotNull();
	}
	
	@Test
	@DisplayName("Percorre as paginas da API da azure, com instância já existindo no banco de dados - sucesso")
	public void percorrerPagina_InstanciaExiste_Sucesso() {
		
		BDDMockito.when(instanceRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(instanceNormal);
		
		BDDMockito.when(instanceRepositoryMock.save(ArgumentMatchers.any())).thenReturn(instanceNormal);
		
		boolean resultado = envioAzure.enviar();
		
		Assertions.assertThat(resultado).isTrue();
		
		
	}
	
	@Test
	@DisplayName("Percorre as paginas da API da azure, com instâncias não existindo no banco de dados - sucesso")
	public void percorrerPagina_NaoExisteInstancia_Sucesso() {
		
		SpotPrices spot = new SpotPrices();
		spot.setCloudName("AWS");
		
		List<SpotPrices> spotList = new ArrayList<SpotPrices>();
		spotList.add(spot);
		
		BDDMockito.when(instanceRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(null);
		
		BDDMockito.when(spotRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregion(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(spotList);
		
		BDDMockito.when(instanceRepositoryMock.save(ArgumentMatchers.any())).thenReturn(instanceNormal);
		
		boolean resultado = envioAzure.enviar();
		
		Assertions.assertThat(resultado).isTrue();
		
		
	}
}
