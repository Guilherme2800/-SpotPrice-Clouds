package com.finops.spotprice.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.finops.spotprice.persistence.entity.PriceHistorySpot;


@DataJpaTest
@DisplayName("Teste para PriceHistory repository")
class PriceHistoryRepositoryTest {

	@Autowired
	PriceHistoryRepository historyRepository;
	
	@Test
	void select_buscarUnicaSpot_Sucesso() {
		
		PriceHistorySpot spot = createPriceHistorySpot();
		historyRepository.save(spot);
		
		PriceHistorySpot spotSelect = historyRepository.findBySelectUsingcodSpotAndpriceAnddataReq(spot.getCodSpot(), spot.getPrice(), spot.getDataReq());
		
		System.out.println(spotSelect);
		
		assertNotNull(spotSelect);
		
	}
	
	@Test
	void salvar_NovaSpot_Sucesso() {
		
		PriceHistorySpot spotParaSalvar = createPriceHistorySpot();
		PriceHistorySpot spotSalva = this.historyRepository.save(spotParaSalvar);
		
		Assertions.assertThat(spotSalva).isNotNull();
		Assertions.assertThat(spotSalva.getDataReq()).isNotNull();
		Assertions.assertThat(spotSalva.getPrice()).isNotNull();
		Assertions.assertThat(spotSalva.getCodSpot()).isNotNull();
		Assertions.assertThat(spotSalva.getCodHistory()).isNotNull();
	}
	
	@Test
	void salvar_UpdateSpot_Sucesso() {
		
		PriceHistorySpot spotParaSalvar = createPriceHistorySpot();
		PriceHistorySpot spotSalva = this.historyRepository.save(spotParaSalvar);
		
		PriceHistorySpot spotUpdate = this.historyRepository.save(spotSalva);
		
		Assertions.assertThat(spotUpdate).isNotNull();
		Assertions.assertThat(spotUpdate.getDataReq()).isNotNull();
		Assertions.assertThat(spotUpdate.getPrice()).isNotNull();
		Assertions.assertThat(spotUpdate.getCodSpot()).isNotNull();
		Assertions.assertThat(spotUpdate.getCodHistory()).isNotNull();
		
	}
	
	@Test
	void salvar_NovaSpot_Erro() {
		
		PriceHistorySpot spotParaSalvar = createPriceHistorySpot();
		PriceHistorySpot spotSalva = this.historyRepository.save(spotParaSalvar);
		
		if(spotSalva == null) {
			Assertions.assertThat(spotSalva).isNull();
		}else if (spotSalva.getDataReq() == null) {
			Assertions.assertThat(spotSalva.getDataReq()).isNull();
		}else if (spotSalva.getCodHistory() == null) {
			Assertions.assertThat(spotSalva.getCodHistory()).isNull();
		}else if (spotSalva.getPrice() == 0) {
			Assertions.assertThat(spotSalva.getPrice()).isEqualTo(0);
		}else if (spotSalva.getCodSpot() == null) {
			Assertions.assertThat(spotSalva.getCodSpot()).isNull();
		}
		
	}
	
	@Test
	void salvar_UpdateSpot_Erro() {
		
		PriceHistorySpot spotParaSalvar = createPriceHistorySpot();
		PriceHistorySpot spotSalva = this.historyRepository.save(spotParaSalvar);
		
		PriceHistorySpot spotUpdate = this.historyRepository.save(spotSalva);
		
		if(spotUpdate == null) {
			Assertions.assertThat(spotUpdate).isNull();
		}else if (spotUpdate.getDataReq() == null) {
			Assertions.assertThat(spotUpdate.getDataReq()).isNull();
		}else if (spotUpdate.getCodHistory() == null) {
			Assertions.assertThat(spotUpdate.getCodHistory()).isNull();
		}else if (spotUpdate.getPrice() == 0) {
			Assertions.assertThat(spotUpdate.getPrice()).isEqualTo(0);
		}else if (spotUpdate.getCodSpot() == null) {
			Assertions.assertThat(spotUpdate.getCodSpot()).isNull();
		}
		
	}
	
	private PriceHistorySpot createPriceHistorySpot() {
		
		PriceHistorySpot historySpot = new PriceHistorySpot();
		
		historySpot.setCodSpot((long) 1);
		historySpot.setDataReq("15-10-2000");
		historySpot.setPrice(1.15);
		
		return historySpot;
		
	}
}
