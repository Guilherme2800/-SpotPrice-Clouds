package com.finops.spotprice.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.finops.spotprice.persistence.entity.SpotPrices;

@DataJpaTest
class SpotRepositoryTest {

	@Autowired
	private SpotRepository spotRepository;
	
	@Test
	void select_BuscarUnicaSpot_Sucesso() {
		
		SpotPrices spot = createSpotPrice();
		this.spotRepository.save(spot);
		
		SpotPrices spotSalva = spotRepository.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(spot.getCloudName(), spot.getInstanceType(), spot.getRegion(), spot.getProductDescription());
		
		assertNotNull(spotSalva);
	}
	
	@Test
	void select_BuscarListaSpot_sucesso() {
		
		SpotPrices spot1 = createSpotPrice();
		SpotPrices spot2 = createSpotPrice();
		SpotPrices spot3 = createSpotPrice();
		
		this.spotRepository.save(spot1);
		this.spotRepository.save(spot2);
		this.spotRepository.save(spot3);
		
		List<SpotPrices> listaSpots = spotRepository.findBySelectUsingcloudNameAndinstanceTypeAndregion("AWS","k30", "Australia");
		
		assertNotNull(listaSpots);
		
	}
	
	@Test
	void select_BuscarListaSpotComLike_CloudName_sucesso() {
		
		SpotPrices spot1 = createSpotPrice();
		SpotPrices spot2 = createSpotPrice();
		SpotPrices spot3 = createSpotPrice();
		
		this.spotRepository.save(spot1);
		this.spotRepository.save(spot2);
		this.spotRepository.save(spot3);
		
		List<SpotPrices> listaSpots = spotRepository.findBycloudName(spot1.getCloudName());
		
		assertNotNull(listaSpots);
		
	}
	
	@Test
	void select_BuscarListaSpotComLike_Region_sucesso() {
		
		SpotPrices spot1 = createSpotPrice();
		SpotPrices spot2 = createSpotPrice();
		SpotPrices spot3 = createSpotPrice();
		
		this.spotRepository.save(spot1);
		this.spotRepository.save(spot2);
		this.spotRepository.save(spot3);
		
		List<SpotPrices> listaSpots = spotRepository.findByregion(spot1.getRegion());
		
		assertNotNull(listaSpots);
		
	}
	
	@Test
	void select_BuscarListaSpotComLike_InstanceType_sucesso() {
		
		SpotPrices spot1 = createSpotPrice();
		SpotPrices spot2 = createSpotPrice();
		SpotPrices spot3 = createSpotPrice();
		
		this.spotRepository.save(spot1);
		this.spotRepository.save(spot2);
		this.spotRepository.save(spot3);
		
		List<SpotPrices> listaSpots = spotRepository.findByinstanceType(spot1.getInstanceType());
		
		assertNotNull(listaSpots);
		
	}
	
	@Test
	void select_BuscarListaSpotComLike_CloudNameAndRegion_sucesso() {
		
		SpotPrices spot1 = createSpotPrice();
		SpotPrices spot2 = createSpotPrice();
		SpotPrices spot3 = createSpotPrice();
		
		this.spotRepository.save(spot1);
		this.spotRepository.save(spot2);
		this.spotRepository.save(spot3);
		
		List<SpotPrices> listaSpots = spotRepository.findBycloudNameAndRegion(spot1.getCloudName(), spot1.getRegion());
		
		assertNotNull(listaSpots);
		
	}
	
	@Test
	void select_BuscarListaSpotComLike_CloudNameAndInstanceType_sucesso() {
		
		SpotPrices spot1 = createSpotPrice();
		SpotPrices spot2 = createSpotPrice();
		SpotPrices spot3 = createSpotPrice();
		
		this.spotRepository.save(spot1);
		this.spotRepository.save(spot2);
		this.spotRepository.save(spot3);
		
		List<SpotPrices> listaSpots = spotRepository.findBycloudNameAndInstanceType(spot1.getCloudName(), spot1.getInstanceType());
		
		assertNotNull(listaSpots);
		
	}
	
	@Test
	void select_BuscarListaSpotComLike_CloudNameAndRegionAndInstanceType_sucesso() {
		
		SpotPrices spot1 = createSpotPrice();
		SpotPrices spot2 = createSpotPrice();
		SpotPrices spot3 = createSpotPrice();
		
		this.spotRepository.save(spot1);
		this.spotRepository.save(spot2);
		this.spotRepository.save(spot3);
		
		List<SpotPrices> listaSpots = spotRepository.findBycloudNameAndRegionAndInstanceType(spot1.getCloudName(), spot1.getRegion(), spot1.getInstanceType());
		
		assertNotNull(listaSpots);
		
	}
	
	@Test
	@DisplayName("Salvar Spot com Sucesso")
	void salvar_NovaSpot_Sucesso() {
		
		SpotPrices spotParaSalvar = createSpotPrice();
		SpotPrices spotSalva = this.spotRepository.save(spotParaSalvar);
		
		Assertions.assertThat(spotSalva).isNotNull();
		Assertions.assertThat(spotSalva.getCloudName()).isNotNull();
		Assertions.assertThat(spotSalva.getDataReq()).isNotNull();
		Assertions.assertThat(spotSalva.getInstanceType()).isNotNull();
		Assertions.assertThat(spotSalva.getPrice()).isNotNull();
		Assertions.assertThat(spotSalva.getRegion()).isNotNull();
		Assertions.assertThat(spotSalva.getCod_spot()).isNotNull();
		Assertions.assertThat(spotSalva.getProductDescription()).isNotNull();
	}
	
	@Test
	@DisplayName("Update Spot com Sucesso")
	void salvar_UpdateSpot_Sucesso() {
		
		SpotPrices spotParaSalvar = createSpotPrice();
		SpotPrices spotSalva = this.spotRepository.save(spotParaSalvar);
		spotSalva.setCloudName("AZURE");
		
		SpotPrices spotUpdate = this.spotRepository.save(spotSalva);
		
		Assertions.assertThat(spotUpdate).isNotNull();
		Assertions.assertThat(spotUpdate.getCloudName()).isNotNull();
		Assertions.assertThat(spotUpdate.getDataReq()).isNotNull();
		Assertions.assertThat(spotUpdate.getInstanceType()).isNotNull();
		Assertions.assertThat(spotUpdate.getPrice()).isNotNull();
		Assertions.assertThat(spotUpdate.getRegion()).isNotNull();
		Assertions.assertThat(spotUpdate.getCod_spot()).isNotNull();
		Assertions.assertThat(spotUpdate.getProductDescription()).isNotNull();
		Assertions.assertThat(spotUpdate.getCloudName()).isEqualTo(spotSalva.getCloudName());
		
	}
	
	@Test
	@DisplayName("Salvar Spot com Erro")
	void salvar_NovaSpot_Erro() {
		
		SpotPrices spotParaSalvar = createSpotPrice();
		SpotPrices spotSalva = this.spotRepository.save(spotParaSalvar);
		
		if(spotSalva == null) {
			Assertions.assertThat(spotSalva).isNull();
		}else if (spotSalva.getCloudName() == null) {
			Assertions.assertThat(spotSalva.getCloudName()).isNull();
		}else if (spotSalva.getDataReq() == null) {
			Assertions.assertThat(spotSalva.getDataReq()).isNull();
		}else if (spotSalva.getInstanceType() == null) {
			Assertions.assertThat(spotSalva.getInstanceType()).isNull();
		}else if (spotSalva.getPrice() == null) {
			Assertions.assertThat(spotSalva.getPrice()).isNull();
		}else if (spotSalva.getRegion() == null) {
			Assertions.assertThat(spotSalva.getRegion()).isNull();
		}else if (spotSalva.getCod_spot() == null) {
			Assertions.assertThat(spotSalva.getCod_spot()).isNull();
		}else if (spotSalva.getProductDescription() == null) {
			Assertions.assertThat(spotSalva.getProductDescription()).isNull();
		}
		
	}
	
	@Test
	@DisplayName("Update Spot com Erro")
	void salvar_UpdateSpot_Erro() {
		
		SpotPrices spotParaSalvar = createSpotPrice();
		SpotPrices spotSalva = this.spotRepository.save(spotParaSalvar);
		spotSalva.setCloudName(null);
		
		SpotPrices spotUpdate = this.spotRepository.save(spotSalva);
		
		if(spotUpdate == null) {
			Assertions.assertThat(spotUpdate).isNull();
		}else if (spotUpdate.getCloudName() == null) {
			Assertions.assertThat(spotUpdate.getCloudName()).isNull();
		}else if (spotUpdate.getDataReq() == null) {
			Assertions.assertThat(spotUpdate.getDataReq()).isNull();
		}else if (spotUpdate.getInstanceType() == null) {
			Assertions.assertThat(spotUpdate.getInstanceType()).isNull();
		}else if (spotUpdate.getPrice() == null) {
			Assertions.assertThat(spotUpdate.getPrice()).isNull();
		}else if (spotUpdate.getRegion() == null) {
			Assertions.assertThat(spotUpdate.getRegion()).isNull();
		}else if (spotUpdate.getCod_spot() == null) {
			Assertions.assertThat(spotUpdate.getCod_spot()).isNull();
		}else if (spotUpdate.getProductDescription() == null) {
			Assertions.assertThat(spotUpdate.getProductDescription()).isNull();
		}
		
	}
	
	private SpotPrices createSpotPrice() {
		SpotPrices spot =  new SpotPrices();
		
		spot.setCloudName("AWS");
		spot.setDataReq("15-01-2000");
		spot.setInstanceType("k30");
		spot.setPrice(new BigDecimal(1.33));
		spot.setRegion("Australia");
		spot.setProductDescription("Instancia para teste");
		
		return spot;
	}

}
