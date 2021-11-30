package com.finops.spotprice.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import com.finops.spotprice.service.FiltrosHtmlGoogle;
import com.finops.spotprice.repository.PriceHistoryRepository;
import com.finops.spotprice.repository.SpotRepository;


@Component
@EnableScheduling
public class EnviarGoogle {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;

	final String URL = "https://cloudbilling.googleapis.com/v1/services/6F81-5844-456A/skus?key=AIzaSyA9eUyryawCIAgta6f2TzUgVEijCmvauns";

	@Autowired
	private SpotRepository spotRepository;

	@Autowired
	private PriceHistoryRepository priceHistoryRepository;

	private List<String> iframe = new ArrayList<String>();
	private List<String> machine = new ArrayList<String>();
	private FiltrosHtmlGoogle googleDadosSpot = new FiltrosHtmlGoogle();
	
	@Scheduled(fixedDelay = HORA)
	public void enviar() {

		// Pega a data atual
		Date data = new Date(System.currentTimeMillis());
		
		// Formata para a data.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		SpotPrices spotPrices;

		System.out.println("\nEnviando GOOGLE");
		

		try {
			// Busca a lista de iframes (Links que levam aos dados das instancias)
			URL url = null;
			url = new URL("https://cloud.google.com/compute/all-pricing");
			iframe = googleDadosSpot.buscarIframe(url);

			// Percorre a lista dos iframes.
			for (int x = 0; x < iframe.size(); x++) {

				// Pega os instance types do iframe atual
				machine = googleDadosSpot.instanceTypes(iframe.get(x));
				// Pega uma string com a região e preço da instancia
				List<String> price = googleDadosSpot.precoInstanciaString(iframe.get(x));

				// Define quantos preços tem para cada instancia.
				int quantidadePrecos = price.size() / machine.size();
				
				// indica o indice atual na lista total de preços
				int indicePrecos = 0;
				
				// Percorre todas as intancias 
				for (int y = 0; y < machine.size(); y++) {

					// percorre todos os preços da determinada instância.
					for (int z = 0; z < quantidadePrecos; z++) {
						spotPrices = null;
						spotPrices = selectSpotPrices("GOOGLE", machine.get(y), googleDadosSpot.region(price.get(indicePrecos)),"");

						// Se o dado já estar no banco de dados, entra no IF
						if (spotPrices != null) {

							// Insere o dado atual na tabela de historico
							insertPriceHistory(spotPrices);

							// Atualiza o dado atual do spotPrices com a nova data e preco
							updateSpotPrices(spotPrices, googleDadosSpot.obterPrecoConvertido(price.get(indicePrecos)),
									sdf.format(data));

						} else {
							// Se o dado não existir, insere ele no banco de dados
							if (googleDadosSpot.obterPrecoConvertido(price.get(indicePrecos)) != 0 && !machine.get(y).contains("Predefined")  && !machine.get(y).equalsIgnoreCase("")) {
								insertSpotprices("GOOGLE", machine.get(y), googleDadosSpot.region(price.get(indicePrecos)), "",
									googleDadosSpot.obterPrecoConvertido(price.get(indicePrecos)), sdf.format(data));
							}
							

						}

						indicePrecos++;

					}
				}
				// Limpa a lista de instancias.
				machine.removeAll(machine);
				
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Terminou GOOGLE");

	}

	protected SpotPrices selectSpotPrices(String cloudName, String instanceType, String region,
			String productDescription) {

		return spotRepository.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription("GOOGLE",
				instanceType, region, productDescription);
	}

	protected void insertPriceHistory(SpotPrices spotPrices) {

		PriceHistory priceHistory = new PriceHistory();

		priceHistory.setCodSpot(spotPrices.getCod_spot());
		priceHistory.setPrice(spotPrices.getPrice());
		priceHistory.setDataReq(spotPrices.getDataReq());

		priceHistoryRepository.save(priceHistory);
	}

	protected void updateSpotPrices(SpotPrices spotPrices, double unitPrice, String dataSpotFormatada) {
		spotPrices.setPrice(unitPrice);
		spotPrices.setDataReq(dataSpotFormatada);

		spotRepository.save(spotPrices);
	}

	protected void insertSpotprices(String cloudName, String instanceType, String region, String productDescription,
			double unitPrice, String dataSpotFormatada) {

		SpotPrices newSpotPrice = new SpotPrices();
		newSpotPrice.setCloudName("GOOGLE");
		newSpotPrice.setInstanceType(instanceType);
		newSpotPrice.setRegion(region);
		newSpotPrice.setProductDescription(productDescription);
		newSpotPrice.setPrice(unitPrice);
		newSpotPrice.setDataReq(dataSpotFormatada);

		spotRepository.save(newSpotPrice);

	}

}
