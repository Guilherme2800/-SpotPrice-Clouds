package com.finops.spotprice.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finops.spotprice.persistence.entity.InstanceNormalPrice;

@Repository
public interface InstanceNormalPriceRepository extends JpaRepository<InstanceNormalPrice, Long> {

}
