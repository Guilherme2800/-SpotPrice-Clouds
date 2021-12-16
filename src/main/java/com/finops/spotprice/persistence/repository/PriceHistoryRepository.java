package com.finops.spotprice.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.finops.spotprice.persistence.entity.PriceHistory;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long>{

	@Query ( value = "select * from pricehistory where cod_spot = ? and price = ? and data_req = ?", nativeQuery = true)
	PriceHistory findBySelectUsingcodSpotAndpriceAnddataReq(Long codSpot, double price, String dataReq);
	
}
