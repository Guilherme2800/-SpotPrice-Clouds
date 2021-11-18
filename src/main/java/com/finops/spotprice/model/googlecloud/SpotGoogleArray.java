package com.finops.spotprice.model.googlecloud;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SpotGoogleArray {

	List<SpotGoogle> skus = new ArrayList<SpotGoogle>();
	private String nextPageToken;

}
