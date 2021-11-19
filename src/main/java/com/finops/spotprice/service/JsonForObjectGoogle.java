package com.finops.spotprice.service;

import java.lang.reflect.Type;

import org.springframework.stereotype.Service;

import com.finops.spotprice.model.googlecloud.SpotGoogleArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class JsonForObjectGoogle {

	public SpotGoogleArray converter(String url) {

		ReceberJson json = new ReceberJson();
		
		// Define a classe que o json será convertido
		Type type = new TypeToken<SpotGoogleArray>() {
		}.getType();

		// Converte a string na classe definida anteriormente
		SpotGoogleArray spot = new Gson().fromJson(json.requisitarJson(url), type);
		return spot;
	}
	
}
