package com.finops.spotprice.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.finops.spotprice.controller.SpotPricesController;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.SpotRepository;

@ExtendWith(SpringExtension.class)
class SpotPricesServiceTest {

	@InjectMocks
	private SpotPricesService spotService;

	@Mock
	private SpotRepository spotRepositoryMock;

	@BeforeEach
	void setUp() {
		PageImpl<SpotPrices> spotPage = new PageImpl<>(createSpot());
		BDDMockito.when(spotRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(spotPage);

		PageImpl<SpotPrices> spotPageCloud = new PageImpl<>(createSpot());
		BDDMockito.when(spotRepositoryMock.findBycloudName(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
				.thenReturn(spotPageCloud);

		PageImpl<SpotPrices> spotPageRegion = new PageImpl<>(createSpot());
		BDDMockito.when(spotRepositoryMock.findByregion(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
				.thenReturn(spotPageRegion);

		PageImpl<SpotPrices> spotPageInstanceType = new PageImpl<>(createSpot());
		BDDMockito.when(spotRepositoryMock.findByinstanceType(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
				.thenReturn(spotPageInstanceType);

		PageImpl<SpotPrices> spotPageCloudRegion = new PageImpl<>(createSpot());
		BDDMockito.when(spotRepositoryMock.findBycloudNameAndRegion(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.any())).thenReturn(spotPageCloudRegion);

		PageImpl<SpotPrices> spotPageCloudInstanceType = new PageImpl<>(createSpot());
		BDDMockito.when(spotRepositoryMock.findBycloudNameAndInstanceType(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(spotPageCloudInstanceType);

		PageImpl<SpotPrices> spotPageCloudRegionInstanceType = new PageImpl<>(createSpot());
		BDDMockito
				.when(spotRepositoryMock.findBycloudNameAndRegionAndInstanceType(ArgumentMatchers.anyString(),
						ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any()))
				.thenReturn(spotPageCloudRegionInstanceType);
	}

	@Test
	void listarGeral_Sucesso() {

		Page<SpotPrices> spotPageResult = spotService.listar(PageRequest.of(1, 1));

		Assertions.assertThat(spotPageResult).isNotNull();

		Assertions.assertThat(spotPageResult.toList()).isNotNull();

		Assertions.assertThat(spotPageResult.toList().get(0).getCloudName()).isEqualTo("AWS");
	}

	@Test
	void listarCloud_Sucesso() {

		Page<SpotPrices> spotPageResult = spotService.listarCloud("Azure", null);

		System.out.println(spotPageResult);

		Assertions.assertThat(spotPageResult).isNotNull();

		Assertions.assertThat(spotPageResult.toList()).isNotNull();

	}

	@Test
	void listarRegion_Sucesso() {

		Page<SpotPrices> spotPageResult = spotService.listarRegion("EU", null);

		Assertions.assertThat(spotPageResult).isNotNull();

		Assertions.assertThat(spotPageResult.toList()).isNotNull();

	}

	@Test
	void listarInstanceType_Sucesso() {

		Page<SpotPrices> spotPageResult = spotService.listarInstanceType("XL16", null);

		Assertions.assertThat(spotPageResult).isNotNull();

		Assertions.assertThat(spotPageResult.toList()).isNotNull();

	}

	@Test
	void listarCloudAndRegion_Sucesso() {

		Page<SpotPrices> spotPageResult = spotService.listarCloudRegion("AZURE", "EU", null);

		Assertions.assertThat(spotPageResult).isNotNull();

		Assertions.assertThat(spotPageResult.toList()).isNotNull();

	}

	@Test
	void listarCloudAndInstanceType_Sucesso() {

		Page<SpotPrices> spotPageResult = spotService.listarCloudInstancetype("AZURE", "XL16", null);

		Assertions.assertThat(spotPageResult).isNotNull();

		Assertions.assertThat(spotPageResult.toList()).isNotNull();

	}

	@Test
	void listarCloudAndRegionAndInstanceType_Sucesso() {

		Page<SpotPrices> spotPageResult = spotService.listarCloudRegionInstanceType("AZURE", "EU", "XL16", null)
				;

		Assertions.assertThat(spotPageResult).isNotNull();

		Assertions.assertThat(spotPageResult.toList()).isNotNull();

	}

	private List<SpotPrices> createSpot() {

		List<SpotPrices> spotList = new ArrayList<SpotPrices>();

		SpotPrices spot1 = new SpotPrices();
		spot1.setCloudName("AWS");
		spot1.setCod_spot(new Long(1));
		spot1.setDataReq("2000-15-10");
		spot1.setInstanceType("KF1");
		spot1.setPrice(new BigDecimal(1.33));
		spot1.setProductDescription("descricao");
		spot1.setRegion("Russia");

		SpotPrices spot2 = new SpotPrices();
		spot2.setCloudName("AZURE");
		spot2.setCod_spot(new Long(1));
		spot2.setDataReq("2000-15-10");
		spot2.setInstanceType("KF1");
		spot2.setPrice(new BigDecimal(1.33));
		spot2.setProductDescription("descricao");
		spot2.setRegion("Russia");

		SpotPrices spot3 = new SpotPrices();
		spot3.setCloudName("GOOGLE");
		spot3.setCod_spot(new Long(1));
		spot3.setDataReq("2000-15-10");
		spot3.setInstanceType("KF1");
		spot3.setPrice(new BigDecimal(1.33));
		spot3.setProductDescription("descricao");
		spot3.setRegion("Russia");

		spotList.add(spot1);
		spotList.add(spot2);
		spotList.add(spot3);

		return spotList;
	}


}
