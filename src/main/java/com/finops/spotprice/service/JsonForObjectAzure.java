package com.finops.spotprice.service;

import java.lang.reflect.Type;

import com.finops.spotprice.model.SpotAzureArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



public class JsonForObjectAzure {
	
	public SpotAzureArray converter(String url) {

		ReceberJson json = new ReceberJson();

		// Define a classe que o json será convertido
		Type type = new TypeToken<SpotAzureArray>() {
		}.getType();

		// Converte a string na classe definida anteriormente
		SpotAzureArray spot = new Gson().fromJson(json.receberJson(url), type);
		return spot;
	}
}
