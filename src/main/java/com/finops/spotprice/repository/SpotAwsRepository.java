package com.finops.spotprice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import com.finops.spotprice.model.InstancesAws;

@Repository
public interface SpotAwsRepository extends JpaRepository<InstancesAws, Long>{

   List<InstancesAws> findByavailabilityZone(String availabilityZone);
   List<InstancesAws> findByinstanceType(String instanceType);
	
}
