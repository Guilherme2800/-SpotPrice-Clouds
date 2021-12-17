package com.finops.spotprice.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.finops.spotprice.persistence.entity.InstanceNormalPrice;
import com.finops.spotprice.persistence.entity.SpotPrices;

@Repository
public interface InstanceNormalPriceRepository extends JpaRepository<InstanceNormalPrice, Long> {

	// Query SQL
		@Query(value = "select * from instanceprices where cloud_name = ? and instance_type = ? and region = ? and product_description = ?", nativeQuery = true)
		InstanceNormalPrice findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(String cloudName,
				String instanceType, String region, String ProductDescription);
	
}
