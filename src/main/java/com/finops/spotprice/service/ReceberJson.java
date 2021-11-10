package com.finops.spotprice.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ReceberJson {

	public JsonObject receberJson(String site) {

		String sURL = site;
		JsonObject jo = null;

		// Variavel de controle para repetir a ação quando ocorrer erro HTTP
		boolean semErro = true;

		// Variavel que controla quantas vezes o erro ocorreu
		int contadorErro = 0;

		// LOOP para repetir a solicitação do JSON em caso de erro
		do {

			try {
				semErro = true;

				URL url = new URL(sURL);
				URLConnection request = url.openConnection();
				request.connect();
				JsonParser jp = new JsonParser();
				JsonElement je = jp.parse(new InputStreamReader((InputStream) request.getContent()));
				jo = je.getAsJsonObject();

			} catch (MalformedURLException e) {
				e.printStackTrace();

			} catch (IOException e) {
				System.out.println("Tentando novamente");
				semErro = false;
				contadorErro++;
			}

			if (semErro == true) {
				contadorErro = 0;
			}

			if (contadorErro == 5) {
				System.out.println("A URL não está respondendo a requisição.");
				semErro = true;
			}

		} while (!semErro);

		return jo;

	}

}
