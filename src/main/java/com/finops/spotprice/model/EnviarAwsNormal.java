package com.finops.spotprice.model;

import java.io.IOException;
import java.util.Map;

import com.github.wnameless.aws.pricelist.api.AWSOffer;
import com.github.wnameless.aws.pricelist.api.AWSRegion;
import com.github.wnameless.aws.pricelist.api.PriceListApi;
import com.github.wnameless.aws.pricelist.api.model.Offer;
import com.github.wnameless.aws.pricelist.api.model.OfferIndex;
import com.github.wnameless.aws.pricelist.api.model.product.OnDemandDetail;
import com.github.wnameless.aws.pricelist.api.model.product.PriceDimension;
import com.github.wnameless.aws.pricelist.api.model.product.Product;
import com.github.wnameless.aws.pricelist.api.model.product.ProductRegion;
import com.github.wnameless.aws.pricelist.api.model.product.ProductRegionIndex;
import com.github.wnameless.aws.pricelist.api.model.product.ProductVersion;

import okhttp3.logging.HttpLoggingInterceptor.Level;

public class EnviarAwsNormal {

	public static void main(String[] args) {
		
		PriceListApi.INSTANCE.setLoggingLevel(Level.BODY);
		
		// Get the OfferIndex which contains all offers of AWS
				OfferIndex offerIndex = null;
				try {
					offerIndex = OfferIndex.get();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				// Select an offer called AmazonA2I
				Offer offer = offerIndex.getOffer(AWSOffer.AmazonEC2);

				// Get all AmazonA2I products sorted by region
				ProductRegionIndex productRegionIndex = null;
				try {
					productRegionIndex = offer.getCurrentProductRegionIndex();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Select products under region eu-central-1
				ProductRegion productRegion = productRegionIndex.getProductRegion(AWSRegion.eu_central_1);

				// Iterate all products
				try {
					for (Product product : productRegion.getProductVersion().getProducts().values()) {
					  product.getSku();
					  product.getAttributes().getUsagetype();
					  product.getAttributes().getOperation();
					  
					  
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ProductVersion productVersion = null;
				OnDemandDetail product = null;
				// Browse the OnDemand term under a product SKU
				Map<String, OnDemandDetail> onDemandDetails = productVersion.getTerms().getOnDemand().get(product.getSku());
				for (OnDemandDetail onDemandDetail : onDemandDetails.values()) {
				  onDemandDetail.getSku();
				  Map<String, PriceDimension> priceDimensions = onDemandDetail.getPriceDimensions();
				  for (PriceDimension priceDimension : priceDimensions.values()) {
				    priceDimension.getPricePerUnit().get("USD");
				  }
				}
		
	}

	
}
