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

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.SpotPrice;
import com.finops.spotprice.SpotpriceApplicationTests;
import com.finops.spotprice.persistence.entity.PriceHistorySpot;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.PriceHistoryRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;

@ExtendWith(SpringExtension.class)
class EnviarAwsSpotTest extends SpotpriceApplicationTests {

	@InjectMocks
	private EnviarAwsSpot envioAws = new EnviarAwsSpot();

	@Mock
	private SpotRepository spotRepositoryMock;

	@Mock
	private PriceHistoryRepository historyRepositoryMock;

	SpotPrices spot;

	SpotPrice spotAws;

	String dataFormatada;

	@BeforeEach
	void setUp() {

		spot = new SpotPrices();
		spot.setCloudName("AZURE");
		spot.setDataReq("15-01-2000");
		spot.setInstanceType("kf1");
		spot.setPrice(new BigDecimal(5.33));
		spot.setRegion("russia");
		spot.setProductDescription("Teste de produto");

		spotAws = new SpotPrice();
		spotAws.setProductDescription("teste de produto");
		spotAws.setAvailabilityZone("Russia");
		spotAws.setInstanceType("kf1");
		spotAws.setSpotPrice("8.3");

		// Formata a data
		DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
		OffsetDateTime dataSpot = OffsetDateTime.parse("2021-10-01T00:00:00Z");
		dataFormatada = dataSpot.format(formatarPadrao);
	}

	@Test
	@DisplayName("Insere Spot - sucesso")
	public void insertSpot_Sucesso() {

		boolean resultado = false;

		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);

		resultado = envioAws.insertSpotPrice(spotAws, dataFormatada, "Russia");

		Assertions.assertThat(resultado).isTrue();
	}

	@Test
	@DisplayName("Insere PriceHistory - sucesso")
	public void insertPriceHistory_Sucesso() {

		boolean resultado = false;

		PriceHistorySpot priceHistory = new PriceHistorySpot();

		BDDMockito.when(historyRepositoryMock.save(ArgumentMatchers.any())).thenReturn(priceHistory);

		resultado = envioAws.insertPricehistory(spot);

		Assertions.assertThat(resultado).isTrue();
	}

	@Test
	@DisplayName("Seleciona spot - sucesso")
	public void selectSpot_Sucesso() {

		SpotPrices spotSalva = null;

		BDDMockito.when(spotRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spot);

		spotSalva = envioAws.selectSpotPrice(spotAws, "russia");
		
		System.out.println(spotSalva);

		Assertions.assertThat(spotSalva).isNotNull();

	}

//	@Test
//	@DisplayName("Seleciona priceHistory - sucesso")
//	public void selectPriceHistory_Sucesso() {
//
//		PriceHistorySpot priceHistory = new PriceHistorySpot();
//		
//		PriceHistorySpot priceHistorySave = null;
//
//		BDDMockito.when(historyRepositoryMock.findBySelectUsingcodSpotAndpriceAnddataReq(ArgumentMatchers.anyLong(), 
//				ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString())).thenReturn(priceHistory);
//		
//		priceHistorySave = envioAws.selectPriceHistory(spot);
//		
//		System.out.println(priceHistorySave);
//		
//		Assertions.assertThat(priceHistorySave).isNotNull();
//
//	}

	@Test
	@DisplayName("Atualiza Spot - sucesso")
	public void updateSpot_Sucesso() {

		boolean resultado = false;

		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);

		resultado = envioAws.updateSpotPrice(spotAws, spot, dataFormatada);

		Assertions.assertThat(resultado).isTrue();

	}

	
	@Test
	@DisplayName("Percorre as paginas da AWS, com instância existindo no banco de dados e priceHistory igual a NULL")
	public void percorrerPagina_InstanciaExisteEHistoryNull_Sucesso() {
		
		BDDMockito.when(spotRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spot);
		
		BDDMockito.when(historyRepositoryMock.findBySelectUsingcodSpotAndpriceAnddataReq(ArgumentMatchers.anyLong(),
				ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString())).thenReturn(null);
		
		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);
		
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		DescribeRegionsResult regions_response = ec2.describeRegions();
		
		boolean resultado = envioAws.enviarParaBanco(regions_response.getRegions().get(0).getRegionName());
		
		Assertions.assertThat(resultado).isTrue();
		
		
	}
	
	@Test
	@DisplayName("Percorre as paginas da AWS, com instância não existindo no banco de dados")
	public void percorrerPagina_NaoExisteInstancia_Sucesso() {

		BDDMockito.when(spotRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(null);

		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		DescribeRegionsResult regions_response = ec2.describeRegions();
		
		boolean resultado = envioAws.enviarParaBanco(regions_response.getRegions().get(0).getRegionName());

		Assertions.assertThat(resultado).isTrue();

	}
	
}
