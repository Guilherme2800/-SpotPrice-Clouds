package com.finops.spotprice.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.finops.spotprice.model.googlecloud.SpotGoogle;
import com.finops.spotprice.model.googlecloud.SpotGoogleArray;
import com.finops.spotprice.model.googlecloud.Testando;
import com.finops.spotprice.repository.PriceHistoryRepository;
import com.finops.spotprice.repository.SpotRepository;
import com.finops.spotprice.service.JsonForObjectGoogle;
import com.finops.spotprice.model.googlecloud.Testando;

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
	private Testando test = new Testando();

	@Scheduled(fixedDelay = HORA)
	public void enviar() {

		// Pega a data atual
		Date data = new Date(System.currentTimeMillis());

		// Formata para o padrão de leitura da AWS.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		SpotPrices spotPrices;

		System.out.println("\nEnviando GOOGLE");
		URL url = null;
		File file = new File("D:\\test\\page2.html");

		try {

			url = new URL("https://cloud.google.com/compute/all-pricing");
			iframe = test.buscarIframe(url, file);

			for (int x = 0; x < iframe.size(); x++) {

				machine = test.instanceTypes(iframe.get(x));
				List<String> price = test.precoInstancia(iframe.get(x));

				int quantidadePrecos = price.size() / machine.size();
				int indicePrecos = 0;
				for (int y = 0; y < machine.size(); y++) {

					for (int z = 0; z < quantidadePrecos; z++) {
						spotPrices = null;

						spotPrices = selectSpotPrices("GOOGLE", machine.get(y), test.region(price.get(indicePrecos)),
								"");

						// Se o dado já estar no banco de dados, entra no IF

						if (spotPrices != null) {

							// Insere o dado atual na tabela de historico
							insertPriceHistory(spotPrices);

							// Atualiza o dado atual do spotPrices com a nova data e preco
							updateSpotPrices(spotPrices, test.obterPrecoConvertido(price.get(indicePrecos)),
									sdf.format(data));

						} else {
							// Se o dado não existir, insere ele no banco de dados
							
							if (test.obterPrecoConvertido(price.get(indicePrecos)) != 0.0 && !machine.get(y).contains("Predefined")) {
								insertSpotprices("GOOGLE", machine.get(y), test.region(price.get(indicePrecos)), "",
									test.obterPrecoConvertido(price.get(indicePrecos)), sdf.format(data));
							}
							

						}

						indicePrecos++;

					}
				}
				System.out.println(iframe.get(5));
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
