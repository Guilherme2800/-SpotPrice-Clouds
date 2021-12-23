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
import com.finops.spotprice.persistence.entity.PriceHistorySpot;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.PriceHistoryRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;

@ExtendWith(SpringExtension.class)
class EnviarGoogleSpotTest extends SpotpriceApplicationTests{

	@InjectMocks
	private EnviarGoogleSpot envioGoogle = new EnviarGoogleSpot();

	@Mock
	private PriceHistoryRepository historyRepositoryMock;
	
	@Mock
	private SpotRepository spotRepositoryMock;

	SpotPrices spot;

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

		// Formata a data
		DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
		OffsetDateTime dataSpot = OffsetDateTime.parse("2021-10-01T00:00:00Z");
		dataFormatada = dataSpot.format(formatarPadrao);
	}
	
	@Test
	@DisplayName("Seleciona spot - sucesso")
	public void selectSpot_Sucesso() {

		SpotPrices spotSalva = null;

		BDDMockito.when(spotRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spot);

		spotSalva = envioGoogle.selectSpotPrices("Google", "XF16", "Russia", "Descricao");

		Assertions.assertThat(spotSalva).isNotNull();

	}

	
	@Test
	@DisplayName("Insere Spot - sucesso")
	public void insertSpot_Sucesso() {
		
		boolean resultado = false;

		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);

		resultado = envioGoogle.insertSpotPrice(null, null, null, null, null, dataFormatada);

		Assertions.assertThat(resultado).isTrue();
	}
	
	@Test
	@DisplayName("Insere PriceHistory - sucesso")
	public void insertPriceHistory_Sucesso() {

		PriceHistorySpot priceHistory = new PriceHistorySpot();
		
		boolean resultado = false;
		
		BDDMockito.when(historyRepositoryMock.save(ArgumentMatchers.any())).thenReturn(priceHistory);

		resultado = envioGoogle.insertPriceHistory(spot);

		Assertions.assertThat(resultado).isTrue();
	}
	
	@Test
	@DisplayName("Atualiza Spot - sucesso")
	public void updateSpot_Sucesso() {

		boolean resultado = false;

		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);

		resultado = envioGoogle.updateSpotPrice(spot, new BigDecimal(0), dataFormatada);

		Assertions.assertThat(resultado).isTrue();

	}

	
	@Test
	@DisplayName("Percorre as paginas da API da google, com instância existindo no banco de dados e priceHistory igual a NULL")
	public void percorrerPagina_InstanciaExisteEHistoryNull_Sucesso() {
		
		BDDMockito.when(spotRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spot);
		
		BDDMockito.when(historyRepositoryMock.findBySelectUsingcodSpotAndpriceAnddataReq(ArgumentMatchers.anyLong(),
				ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString())).thenReturn(null);
		
		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);
		
		boolean resultado = envioGoogle.enviar();
		
		Assertions.assertThat(resultado).isTrue();
		
		
	}

	@Test
	@DisplayName("Percorre as paginas da API da google, com instância não existindo no banco de dados")
	public void percorrerPagina_NaoExisteInstancia_Sucesso() {

		BDDMockito.when(spotRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(null);

		BDDMockito.when(spotRepositoryMock.save(ArgumentMatchers.any())).thenReturn(spot);

		boolean resultado = envioGoogle.enviar();

		Assertions.assertThat(resultado).isTrue();

	}

}
