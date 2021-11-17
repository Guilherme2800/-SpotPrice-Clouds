package com.finops.spotprice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finops.spotprice.model.Instances;

@Repository
public interface SpotRepository extends JpaRepository<Instances, Long>{

   List<Instances> findByregion(String region);
   List<Instances> findByinstanceType(String instanceType);
   List<Instances> findBycloudName(String cloudName);
   List<Instances> findBycloudNameAndInstanceType(String cloudName, String instanceType);
   List<Instances> findBycloudNameAndRegion(String cloudName, String region);
   List<Instances> findBycloudNameAndRegionAndInstanceType(String cloudName, String region, String instanceType);
}
