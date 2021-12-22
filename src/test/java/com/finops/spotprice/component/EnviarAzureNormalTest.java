package com.finops.spotprice.component;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import com.finops.spotprice.SpotpriceApplicationTests;
import com.finops.spotprice.model.InstanceAzure;
import com.finops.spotprice.persistence.entity.InstanceNormalPrice;
import com.finops.spotprice.persistence.repository.InstanceNormalRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;


@ExtendWith(SpringExtension.class)
class EnviarAzureNormalTest extends SpotpriceApplicationTests{
	
	@InjectMocks
	private EnviarAzureNormal envioAzure = new EnviarAzureNormal();
	
	@Mock
	private InstanceNormalRepository instanceRepository;
	
	@Test
	public void select() {
		
		boolean resultado = false;
		
		InstanceNormalPrice instanceNormal = new InstanceNormalPrice();
		
		instanceNormal.setCloudName("AZURE");
		instanceNormal.setDataReq("15-01-2000");
		instanceNormal.setInstanceType("kf1");
		instanceNormal.setPrice(new BigDecimal(5.33));
		instanceNormal.setRegion("russia");
		instanceNormal.setProductDescription("Teste de produto");
		
		InstanceAzure instanceAzure = new InstanceAzure();
		
		instanceAzure.setSkuName("kf1");
		instanceAzure.setLocation("russia");
		instanceAzure.setProductName("Teste de produto");
		instanceAzure.setUnitPrice(5.33);
		instanceAzure.setEffectiveStartDate("2021-10-01T00:00:00Z");
		
		// Formata a data
		DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
		OffsetDateTime dataSpot = OffsetDateTime.parse(instanceAzure.getEffectiveStartDate());
		String dataSpotFormatada = dataSpot.format(formatarPadrao);
		
		BDDMockito.when(instanceRepository.save(instanceNormal)).thenReturn(instanceNormal);
		
		resultado = envioAzure.insertInstancePrice(instanceAzure, dataSpotFormatada);
		
		assertTrue(resultado);
	}
	
}
