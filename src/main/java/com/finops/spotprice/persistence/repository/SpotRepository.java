package com.finops.spotprice.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.finops.spotprice.persistence.entity.SpotPrices;

@Repository
public interface SpotRepository extends JpaRepository<SpotPrices, Long> {

	// Query SQL
	@Query(value = "select * from spotprices where cloud_name = ? and instance_type = ? and region = ? and product_description = ?", nativeQuery = true)
	SpotPrices findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription(String cloudName,
			String instanceType, String region, String ProductDescription);

	// Query SQL
	@Query(value = "select * from spotprices where cloud_name = ? and instance_type = ? and region = ?", nativeQuery = true)
	List<SpotPrices> findBySelectUsingcloudNameAndinstanceTypeAndregion(String cloudName, String instanceType, String region);

	// API REST
	Page<SpotPrices> findBycloudName(String cloudName, Pageable pageable);

	Page<SpotPrices> findByregion(String region, Pageable pageable);

	Page<SpotPrices> findByinstanceType(String instanceType, Pageable pageable);

	Page<SpotPrices> findBycloudNameAndRegion(String cloudName, String region, Pageable pageable);

	Page<SpotPrices> findBycloudNameAndInstanceType(String cloudName, String instanceType, Pageable pageable);

	Page<SpotPrices> findBycloudNameAndRegionAndInstanceType(String cloudName, String region, String instanceType,
			Pageable pageable);

	// Thymeleaf
	@Query(value = "select * from spotprices where cloud_name like %?% ", nativeQuery = true)
	List<SpotPrices> findBycloudName(String cloudName);

	@Query(value = "select * from spotprices where region like %?% ", nativeQuery = true)
	List<SpotPrices> findByregion(String region);

	@Query(value = "select * from spotprices where instance_type like %?% ", nativeQuery = true)
	List<SpotPrices> findByinstanceType(String instanceType);

	@Query(value = "select * from spotprices where cloud_name like %?% and region like %?%", nativeQuery = true)
	List<SpotPrices> findBycloudNameAndRegion(String cloudName, String region);

	@Query(value = "select * from spotprices where cloud_name like %?% and instance_type like %?%", nativeQuery = true)
	List<SpotPrices> findBycloudNameAndInstanceType(String cloudName, String instanceType);

	@Query(value = "select * from spotprices where cloud_name like %?% and region like %?% and instance_type like %?%", nativeQuery = true)
	List<SpotPrices> findBycloudNameAndRegionAndInstanceType(String cloudName, String region, String instanceType);

}
