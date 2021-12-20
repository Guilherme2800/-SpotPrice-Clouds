package com.finops.spotprice.util;

import java.lang.reflect.Type;

import org.springframework.stereotype.Service;

import com.finops.spotprice.model.SpotAzureArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class JsonForObjectAzure {
	
	public SpotAzureArray converter(String url) {

		ReceberJson json = new ReceberJson();

		// Define a classe que o json será convertido
		Type type = new TypeToken<SpotAzureArray>() {
		}.getType();

		// Converte a string na classe definida anteriormente
		SpotAzureArray spot = new Gson().fromJson(json.requisitarJson(url), type);
		return spot;
	}
}
