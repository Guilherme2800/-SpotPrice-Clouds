package com.finops.spotprice.service;

import com.finops.spotprice.model.googlecloud.SpotGoogleArray;

public class Test {

	public void enviar() {
		
		JsonForObjectGoogle json = new JsonForObjectGoogle();
		
		SpotGoogleArray spotGoogle = json.converter("https://cloudbilling.googleapis.com/v1/services/6F81-5844-456A/skus?key=AIzaSyA9eUyryawCIAgta6f2TzUgVEijCmvauns");
		
		System.out.println(spotGoogle.getSkus().get(0));
		
	}
	
}
