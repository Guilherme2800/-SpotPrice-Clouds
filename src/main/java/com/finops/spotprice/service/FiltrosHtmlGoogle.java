package com.finops.spotprice.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FiltrosHtmlGoogle {

	private List<String> iframe = new ArrayList<String>();
	private List<String> machine = new ArrayList<String>();

	public List<String> buscarIframe(URL url) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		String inputLine;

		while ((inputLine = in.readLine()) != null) {

			if (inputLine.contains("machine_types")) {

				// Pula duas linhas
				inputLine = in.readLine();
				inputLine = in.readLine();

				if (inputLine.contains("<iframe")) {
					String regex = "\"([^\"]*)\""; // regex com um grupo entre aspas
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(inputLine); // linha é a variável que contém a linha que foi lida
																	// do
																	// arquivo
					if (matcher.find()) {
						String iframeUrl = matcher.group(1); // obtém o grupo lido da regex
						iframe.add("https://cloud.google.com" + iframeUrl);
						// machine.add(nome);
					}
				} else {

					// Pula duas linhas
					inputLine = in.readLine();
					inputLine = in.readLine();

					if (inputLine.contains("<iframe")) {
						String regex = "\"([^\"]*)\""; // regex com um grupo entre aspas
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(inputLine); // linha é a variável que contém a linha que foi
																		// lida do
																		// arquivo
						if (matcher.find()) {
							String iframeUrl = matcher.group(1); // obtém o grupo lido da regex
							iframe.add("https://cloud.google.com" + iframeUrl);
							// machine.add(nome);
						}

					}

				}

			}

		}

		in.close();
		return iframe;
	}

	public List<String> instanceTypes(String pagina) {

		String inputLine;
		URL url = null;

		try {
			url = new URL(pagina);

			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			while ((inputLine = in.readLine()) != null) {

				if (inputLine.contains("<tr>")) {
					// Pula uma linha
					inputLine = in.readLine();

					if (!inputLine.contains("<a") && !inputLine.contains("<strong")) {

						if (inputLine.contains("<tr>")) {
							inputLine = in.readLine();
						}

						String nome = (String) inputLine.replaceAll("<td>", "").replaceAll("</td>", "")
								.replaceAll("  ", "").replaceAll("-", " ");
						machine.add(nome);

					}

				}

			}

			in.close();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return machine;
	}

	public List<String> precoInstanciaString(String pagina) {

		List<String> price = new ArrayList<String>();
		List<String> aux = new ArrayList<String>();

		String inputLine;
		URL url = null;

		try {
			url = new URL(pagina);

			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			while ((inputLine = in.readLine()) != null) {

				if (inputLine.contains("<tr>")) {
					inputLine = in.readLine();

					if (!inputLine.contains("<a") && !inputLine.contains("<strong")) {

						// Pula 6 linhas
						for (int i = 0; i < 6; i++) {
							inputLine = in.readLine();
						}
						while (!inputLine.contains("</tr>")) {
							inputLine = in.readLine();

							if (inputLine.contains("hourly")) {

								aux.add(inputLine.replaceAll("-hourly", "").replaceAll(" ", ""));

							}

							if (inputLine.contains("</td>")) {
								inputLine = in.readLine();

								if (inputLine.contains("<td")) {
									aux.removeAll(aux);
								}

							}

						}

						price.addAll(aux);

					}

				}

			}
			in.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return price;
	}

	public Double obterPrecoConvertido(String conteudo) {

		String regex = "\"([^\"]*)\"";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(conteudo);

		if (matcher.find()) {
			String con = (String) matcher.group(1);

			if (con.contains("otavail")) {
				return 0.0;
			} else if (con.length() > 10) {
				return 0.0;
			} else {
				try {
					con = (String) matcher.group(1).subSequence(1, 8);
				} catch (java.lang.StringIndexOutOfBoundsException e) {
					return 0.0;
				}

				return Double.parseDouble(con);
			}

		}

		return 0.0;
	}

	public String region(String linha) {

		int indice = linha.indexOf("=");
		String abreviacaoRegion = (String) linha.subSequence(0, indice);

		switch (abreviacaoRegion) {

		case "io":
			return "Iowa (us-central1)";

		case "ore":
			return "Oregon (us-west1)";

		case "slc":
			return "Salt Lake City (us-west3)";

		case "la":
			return "Los Angeles (us-west2)";

		case "lv":
			return "Las Vegas (us-west4)";

		case "nv":
			return "Nothern Virginia (us-east4)";

		case "sc":
			return "Soth Carolina (us-east1)";

		case "mtreal":
			return "Montreal (northamerica-northeast1)";

		case "tor":
			return "Toronto (nortamerica-northeast2)";

		case "spaulo":
			return "São Paulo (southamerica-east)";

		case "sant":
			return "Satiago (southamerica-west1)";

		case "eu":
			return "Belgium (europe-west1)";

		case "fi":
			return "Finland (europe-north1)";

		case "lon":
			return "London (europe-west2)";

		case "ffurt":
			return "Frankfurt (europe-west3)";

		case "nether":
			return "Netherlands (europe-west4)";

		case "zur":
			return "Zurich (europe-west6)";

		case "wsaw":
			return "Warsaw (europe-central2)";

		case "mbai":
			return "Mumbai (asia-south1)";

		case "del":
			return "Delhi (asia-south2)";

		case "sg":
			return "Singapore (asia-southeast1)";

		case "jk":
			return "Jakarta (asia-southeast2)";

		case "syd":
			return "Sydney (australia-southeast1)";

		case "mel":
			return "Melbourn (australia-southeast2)";

		case "tw":
			return "Taiwan (asia-east1)";

		case "hk":
			return "Hong Kong (asia-east2)";

		case "ja":
			return "Tokyo (asia-northeast1)";

		case "osa":
			return "Osaka (asia-northeast2)";

		case "kr":
			return "Seoul (asia-northeast3)";

		default:
			return abreviacaoRegion;
		}

	}
	
}
