package com.finops.spotprice.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.finops.spotprice.persistence.entity.InstanceNormalPrice;

@DataJpaTest
class InstanceNormalRepositoryTest {

	@Autowired
	InstanceNormalRepository instanceRepository;
	
	@Test
	void select_BuscarUnicaInstancia_Sucesso() {
		
		InstanceNormalPrice instance = createInstanceNormal();
		this.instanceRepository.save(instance);
		
		InstanceNormalPrice spotSalva = instanceRepository.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(instance.getCloudName(), instance.getInstanceType(), instance.getRegion(), instance.getProductDescription());
		
		assertNotNull(spotSalva);
	}
	
	@Test
	void salvar_NovaInstancia_Sucesso() {
		
		InstanceNormalPrice spotParaSalvar = createInstanceNormal();
		InstanceNormalPrice  spotSalva = this.instanceRepository.save(spotParaSalvar);
		
		Assertions.assertThat(spotSalva).isNotNull();
		Assertions.assertThat(spotSalva.getCloudName()).isNotNull();
		Assertions.assertThat(spotSalva.getDataReq()).isNotNull();
		Assertions.assertThat(spotSalva.getInstanceType()).isNotNull();
		Assertions.assertThat(spotSalva.getPrice()).isNotNull();
		Assertions.assertThat(spotSalva.getRegion()).isNotNull();
		Assertions.assertThat(spotSalva.getCod_instance()).isNotNull();
		Assertions.assertThat(spotSalva.getProductDescription()).isNotNull();
	}
	
	
	@Test
	void salvar_NovaInstancia_Erro() {
		
		InstanceNormalPrice spotParaSalvar = createInstanceNormal();
		InstanceNormalPrice  spotSalva = this.instanceRepository.save(spotParaSalvar);
		
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
		}else if (spotSalva.getCod_instance() == null) {
			Assertions.assertThat(spotSalva.getCod_instance()).isNull();
		}else if (spotSalva.getProductDescription() == null) {
			Assertions.assertThat(spotSalva.getProductDescription()).isNull();
		}
		
	}
	
	private InstanceNormalPrice createInstanceNormal() {
		
		InstanceNormalPrice instanceNormal = new InstanceNormalPrice();
		
		instanceNormal.setCloudName("AWS");
		instanceNormal.setDataReq("15-01-2000");
		instanceNormal.setInstanceType("k30");
		instanceNormal.setPrice(new BigDecimal(1.33));
		instanceNormal.setRegion("Australia");
		instanceNormal.setProductDescription("Instancia para teste");
		
		return instanceNormal;
		
	}

}
