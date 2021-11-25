package com.finops.spotprice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.finops.spotprice.model.SpotPrices;

@Repository
public interface SpotRepository extends JpaRepository<SpotPrices, Long>{

	// Query SQL
	@Query (value = "select * from spotprices where cloud_name = 'google' and instance_type = ? and region = ? and product_description = ?", nativeQuery = true)
	SpotPrices findBySelectUsinginstanceTypeAndregionAndProductDescription(String instanceType, String region, String ProductDescription);
	
	
	
	// API REST
   List<SpotPrices> findByregion(String region);
   List<SpotPrices> findByinstanceType(String instanceType);
   List<SpotPrices> findBycloudName(String cloudName);
   List<SpotPrices> findBycloudNameAndInstanceType(String cloudName, String instanceType);
   List<SpotPrices> findBycloudNameAndRegion(String cloudName, String region);
   List<SpotPrices> findBycloudNameAndRegionAndInstanceType(String cloudName, String region, String instanceType);
}
