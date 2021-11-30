package com.finops.spotprice.model.googlecloud;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testando {

	static List<String> iframe = new ArrayList<String>();
	static List<String> machine = new ArrayList<String>();
	static int i;

	public void getPage(URL url, File file) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		String inputLine;

		while ((inputLine = in.readLine()) != null) {

			if (inputLine.contains("machine_types")) {
				int posEnd = inputLine.indexOf("\"");

				// String nome = (String) inputLine.subSequence(8, 15);

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

//				// Imprime página no console
//				if (inputLine.contains("<iframe")) {
//					String regex = "\"([^\"]*)\""; // regex com um grupo entre aspas
//					Pattern pattern = Pattern.compile(regex);
//					Matcher matcher = pattern.matcher(inputLine); // linha é a variável que contém a linha que foi lida
//																	// do
//																	// arquivo
//					if (matcher.find()) {
//						String iframeUrl = matcher.group(1); // obtém o grupo lido da regex
//						iframe.add("https://cloud.google.com" + iframeUrl);
//					}
//				}
				// System.out.println(inputLine);

				// Grava pagina no arquivo
				out.write(inputLine);
				out.newLine();
			}
			
		}

		
		in.close();
		out.flush();
		out.close();
	}

	public static void main(String[] args) {
		URL url = null;
		File file = new File("D:\\test\\page2.html");
		try {
			url = new URL("https://cloud.google.com/compute/all-pricing");
			new Testando().getPage(url, file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<String> price = precoInstancia();

		try {
			instanceTypes();
			
			int quantidadePrecos = price.size() / machine.size();
			int indicePrecos = 0;
			System.out.println("--------TABELA DE PREÇOS------");
			for (String maquina : machine) {
				System.out.println("Maquina " + maquina);
				
				for(int i = 0; i < quantidadePrecos; i++) {
					System.out.println(price.get(indicePrecos));
					indicePrecos++;
				}
				
				System.out.println("\n\n");
			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void instanceTypes() {

		String inputLine;
		URL url = null;

		try {
			url = new URL(iframe.get(5));

			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			while ((inputLine = in.readLine()) != null) {

				if (inputLine.contains("<tr>")) {
					inputLine = in.readLine();

					if (!inputLine.contains("<a") && !inputLine.contains("<strong")) {

						String nome = (String) inputLine.replaceAll("<td>", "");
						nome = nome.replaceAll("</td>", "");
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

	}

	public static List<String> precoInstancia() {

		List<String> price = new ArrayList<String>();
		List<String> aux = new ArrayList<String>();

		String inputLine;
		URL url = null;
		int quantidade = 0;
		double valor;

		try {
			url = new URL(iframe.get(0));

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

								aux.add(inputLine);

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

}
