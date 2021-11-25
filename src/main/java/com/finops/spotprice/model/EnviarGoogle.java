package com.finops.spotprice.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.finops.spotprice.model.azurecloud.SpotAzure;
import com.finops.spotprice.model.azurecloud.SpotAzureArray;
import com.finops.spotprice.model.googlecloud.SpotGoogle;
import com.finops.spotprice.model.googlecloud.SpotGoogleArray;
import com.finops.spotprice.repository.PriceHistoryRepository;
import com.finops.spotprice.repository.SpotRepository;
import com.finops.spotprice.service.ConexaoMariaDb;
import com.finops.spotprice.service.JsonForObjectGoogle;

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

	@Scheduled(fixedDelay = HORA)
	public void enviar() {

		// Recebe o json convertido
		SpotGoogleArray googleSpot = solicitarObjetoGoogle(URL);

		System.out.println("\nEnviando GOOGLE");

		boolean proximo = true;

		while (proximo) {

			// Percorre o array de instancias da pagina
			for (SpotGoogle spotGoogle : googleSpot.getSkus()) {

				// Verifica quais são do tipo SPOT
				if (spotGoogle.getDescription().contains("Spot")) {

					// Formata a data
					DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
					OffsetDateTime dataSpot = OffsetDateTime.parse(spotGoogle.getPricingInfo().get(0).getEffectiveTime());
					String dataSpotFormatada = dataSpot.format(formatarPadrao);

					// Realiza o calculo do preço da instancia
					Double unitPrice = calcularPreco(spotGoogle);

					// Percorre as regions que aquela instancia está disponivel
					for (int countRegions = 0; countRegions < spotGoogle.getServiceRegions().size(); countRegions++) {

						SpotPrices spotPrices = new SpotPrices();

						spotPrices = spotRepository.findBySelectUsinginstanceTypeAndregionAndProductDescription(
								spotGoogle.getCategory().getResourceGroup(),
								spotGoogle.getServiceRegions().get(countRegions), spotGoogle.getDescription());

						// Se o dado já estar no banco de dados, entra no IF
						if (spotPrices != null) {

							PriceHistory priceHistory = new PriceHistory();

							// Insere o dado atual na tabela de historico
							priceHistory.setCodSpot(spotPrices.getCod_spot());
							priceHistory.setPrice(spotPrices.getPrice());
							priceHistory.setDataReq(spotPrices.getDataReq());

							priceHistoryRepository.save(priceHistory);

							// Atualiza o dado atual com a nova data e preco
							spotPrices.setPrice(unitPrice);
							spotPrices.setDataReq(dataSpotFormatada);
							
							spotRepository.save(spotPrices);

						} else {

							// Se o dado não existir, insere ele no banco de dados
							SpotPrices newSpotPrice = new SpotPrices();
							newSpotPrice.setCloudName("GOOGLE");
							newSpotPrice.setInstanceType(spotGoogle.getCategory().getResourceGroup());
							newSpotPrice.setRegion(spotGoogle.getServiceRegions().get(countRegions));
							newSpotPrice.setProductDescription(spotGoogle.getDescription());
							newSpotPrice.setPrice(unitPrice);
							newSpotPrice.setDataReq(dataSpotFormatada);

							spotRepository.save(newSpotPrice);

						}

					}

				}

			}

			// Controle se a existe uma proxima pagina
			if (googleSpot.getNextPageToken() == null || googleSpot.getSkus().size() != 5000) {
				proximo = false;
			} else {
				System.out.println(googleSpot.getNextPageToken());
				googleSpot = solicitarObjetoGoogle(URL + "&pageToken=" + googleSpot.getNextPageToken());

			}
		}

		System.out.println("Terminou google");

	}

	protected Double calcularPreco(SpotGoogle spot) {

		// Formatar valor da instancia
		DecimalFormat formatar = new DecimalFormat("0.000000");

		// Realiza o calculo do preço da instancia
		String preco = formatar.format(
				spot.getPricingInfo().get(0).getPricingExpression().getTieredRates().get(0).getUnitPrice().getNanos()
						* Math.pow(10, -9));

		preco += spot.getPricingInfo().get(0).getPricingExpression().getTieredRates().get(0).getUnitPrice().getUnits();

		return Double.valueOf(preco.replaceAll(",", ".")).doubleValue();
	}

	
	protected SpotGoogleArray solicitarObjetoGoogle(String URL) {

		// Instancia o objeto que vai converter o JSON para objeto
		JsonForObjectGoogle JsonForGoogle = new JsonForObjectGoogle();

		SpotGoogleArray googleArrayObject = JsonForGoogle.converter(URL);

		return googleArrayObject;
	}

}
