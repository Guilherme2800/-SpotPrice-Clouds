package com.finops.spotprice.repository;

import com.finops.spotprice.model.InstancesAzure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends JpaRepository<InstancesAzure, Long>{

}
