package com.finops.spotprice.component;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

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
import com.finops.spotprice.persistence.entity.PriceHistorySpot;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.PriceHistoryRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;

@ExtendWith(SpringExtension.class)
class EnviarAzureSpotTest extends SpotpriceApplicationTests {

	@InjectMocks
	private EnviarAzureSpot envioAzure = new EnviarAzureSpot();
	
	@Mock
	private SpotRepository spotRepositoryMock;

	@Mock
	private PriceHistoryRepository historyRepositoryMock;

	SpotPrices spot;

	InstanceAzure instanceAzure;

	String dataSpotFormatada;

	@BeforeEach
	void setUp() {

		spot = new SpotPrices();

		spot.setCloudName("AZURE");
		spot.setDataReq("15-01-2000");
		spot.setInstanceType("kf1");
		spot.setPrice(new BigDecimal(5.33));
		spot.setRegion("russia");
		spot.setProductDescription("Teste de produto");

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
	@DisplayName("Insere Spot - sucesso")
	public void insertSpot_Sucesso() {

		boolean resultado = false;

		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);

		resultado = envioAzure.insertSpotPrice(instanceAzure, dataSpotFormatada);

		Assertions.assertThat(resultado).isTrue();
	}
	
	@Test
	@DisplayName("Insere PriceHistory - sucesso")
	public void insertPriceHistory_Sucesso() {

		PriceHistorySpot priceHistory = new PriceHistorySpot();
		
		boolean resultado = false;
		
		BDDMockito.when(historyRepositoryMock.save(ArgumentMatchers.any())).thenReturn(priceHistory);

		resultado = envioAzure.insertPricehistory(spot);

		Assertions.assertThat(resultado).isTrue();
	}

	@Test
	@DisplayName("Atualiza Spot - sucesso")
	public void updateSpot_Sucesso() {

		boolean resultado = false;

		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);

		resultado = envioAzure.updateSpotPrice(instanceAzure, spot, dataSpotFormatada);

		Assertions.assertThat(resultado).isTrue();

	}

	@Test
	@DisplayName("Seleciona spot - sucesso")
	public void selectSpot_Sucesso() {

		SpotPrices spotResultSelect = null;

		BDDMockito.when(spotRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spot);

		spotResultSelect = envioAzure.selectSpotPrice(instanceAzure);

		Assertions.assertThat(spotResultSelect).isNotNull();

	}
	
	@Test
	@DisplayName("Solicita array de spots azure - sucesso")
	public void solicitarArrayAzure_Sucesso() {

		InstancesAzureArray azureArray;

		azureArray = envioAzure.solicitarObjetoAzure(
				"https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27");

		Assertions.assertThat(azureArray).isNotNull();
	}

	@Test
	@DisplayName("Percorre as paginas da API da azure, com instância existindo no banco de dados e priceHistory igual a NULL")
	public void percorrerPagina_InstanciaExisteEHistoryNull_Sucesso() {

		BDDMockito.when(spotRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spot);

		BDDMockito.when(historyRepositoryMock.findBySelectUsingcodSpotAndpriceAnddataReq(ArgumentMatchers.anyLong(),
				ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString())).thenReturn(null);

		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);

		boolean resultado = envioAzure.enviar();

		Assertions.assertThat(resultado).isTrue();

	}

	@Test
	@DisplayName("Percorre as paginas da API da azure, com instância não existindo no banco de dados e priceHistory igual a NULL")
	public void percorrerPagina_NaoExisteInstanciaEHistoryNull_Sucesso() {

		BDDMockito.when(spotRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(null);

		BDDMockito.when(historyRepositoryMock.findBySelectUsingcodSpotAndpriceAnddataReq(ArgumentMatchers.anyLong(),
				ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString())).thenReturn(null);

		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);

		boolean resultado = envioAzure.enviar();

		Assertions.assertThat(resultado).isTrue();

	}
	

}
