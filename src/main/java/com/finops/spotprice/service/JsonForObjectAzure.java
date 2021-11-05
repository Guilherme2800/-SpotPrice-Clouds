package com.finops.spotprice.service;

import java.lang.reflect.Type;

import com.finops.spotprice.model.SpotAzure;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



public class JsonForObjectAzure {
	
	public SpotAzure converter(String url) {

		ReceberJson json = new ReceberJson();

		// Define a classe que o json será convertido
		Type type = new TypeToken<SpotAzure>() {
		}.getType();

		// Converte a string na classe definida anteriormente
		SpotAzure spot = new Gson().fromJson(json.receberJson(url), type);
		return spot;
	}
}
