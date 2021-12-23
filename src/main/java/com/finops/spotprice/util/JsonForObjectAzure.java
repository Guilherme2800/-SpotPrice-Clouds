package com.finops.spotprice.util;

import java.lang.reflect.Type;

import org.springframework.stereotype.Service;

import com.finops.spotprice.model.InstancesAzureArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class JsonForObjectAzure {
	
	public InstancesAzureArray converter(String url) {

		ReceberJson json = new ReceberJson();

		// Define a classe que o json será convertido
		Type type = new TypeToken<InstancesAzureArray>() {
		}.getType();

		// Converte a string na classe definida anteriormente
		InstancesAzureArray spot = new Gson().fromJson(json.requisitarJson(url), type);
		return spot;
	}
}
