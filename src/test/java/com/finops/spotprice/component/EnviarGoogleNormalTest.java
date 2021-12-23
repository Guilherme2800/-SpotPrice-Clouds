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
import com.finops.spotprice.persistence.entity.InstanceNormalPrice;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.InstanceNormalRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;

@ExtendWith(SpringExtension.class)
class EnviarGoogleNormalTest extends SpotpriceApplicationTests{

	@InjectMocks
	private EnviarGoogleNormal envioGoogle = new EnviarGoogleNormal();

	@Mock
	private InstanceNormalRepository instanceRepositoryMock;
	
	@Mock
	private SpotRepository spotRepositoryMock;

	InstanceNormalPrice instanceNormal;


	String dataFormatada;
	
	@BeforeEach
	void setUp() {

		instanceNormal = new InstanceNormalPrice();

		instanceNormal.setCloudName("AZURE");
		instanceNormal.setDataReq("15-01-2000");
		instanceNormal.setInstanceType("kf1");
		instanceNormal.setPrice(new BigDecimal(5.33));
		instanceNormal.setRegion("russia");
		instanceNormal.setProductDescription("Teste de produto");

		// Formata a data
		DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
		OffsetDateTime dataSpot = OffsetDateTime.parse("2021-10-01T00:00:00Z");
		dataFormatada = dataSpot.format(formatarPadrao);
	}
	
	@Test
	@DisplayName("Inserir instancia normal - Sucesso")
	public void insertInstanceNormal_Sucesso() {

		boolean resultado = false;

		BDDMockito.when(instanceRepositoryMock.save(ArgumentMatchers.any())).thenReturn(instanceNormal);

		resultado = envioGoogle.insertInstancePrice(null, null, null, null, null, dataFormatada);

		Assertions.assertThat(resultado).isTrue();
	}
	
	@Test
	@DisplayName("Atualizar instancia normal - sucesso")
	public void updateInstanceNormal_Sucesso() {

		boolean resultado = false;

		BDDMockito.when(instanceRepositoryMock.save(ArgumentMatchers.any())).thenReturn(instanceNormal);

		resultado = envioGoogle.updateInstancePrice(instanceNormal, new BigDecimal(0), dataFormatada);

		Assertions.assertThat(resultado).isTrue();

	}

	@Test
	@DisplayName("Selecionar instancia normal - sucesso")
	public void selectInstanceNormal_Sucesso() {

		InstanceNormalPrice instance = null;

		BDDMockito.when(instanceRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(instanceNormal);

		instance = envioGoogle.selectInstancePrice("Google", "XF16", "Russia", "Descricao");

		Assertions.assertThat(instance).isNotNull();

	}
	
	@Test
	@DisplayName("Percorre as paginas da API da google, com instância já existindo no banco de dados - sucesso")
	public void percorrerPaginaInstanciaExiste_Sucesso() {
		
		BDDMockito.when(instanceRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(instanceNormal);
		
		BDDMockito.when(instanceRepositoryMock.save(ArgumentMatchers.any())).thenReturn(instanceNormal);
		
		boolean resultado = envioGoogle.enviar();
		
		Assertions.assertThat(resultado).isTrue();
		
		
	}
	
	@Test
	@DisplayName("Percorre as paginas da API da google, com instâncias não existindo no banco de dados - sucesso")
	public void percorrerPaginaNaoExisteInstancia_Sucesso() {
		
		SpotPrices spot = new SpotPrices();
		spot.setCloudName("Google");
		
		List<SpotPrices> spotList = new ArrayList<SpotPrices>();
		spotList.add(spot);
		
		BDDMockito.when(instanceRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(null);
		
		BDDMockito.when(spotRepositoryMock.findBySelectUsingcloudNameAndinstanceTypeAndregion(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(spotList);
		
		BDDMockito.when(instanceRepositoryMock.save(ArgumentMatchers.any())).thenReturn(instanceNormal);
		
		boolean resultado = envioGoogle.enviar();
		
		Assertions.assertThat(resultado).isTrue();
		
		
	}

}
