package com.finops.spotprice.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.finops.spotprice.model.azurecloud.*;
import com.finops.spotprice.repository.PriceHistoryRepository;
import com.finops.spotprice.repository.SpotRepository;
import com.finops.spotprice.service.JsonForObjectAzure;

@Component
@EnableScheduling
public class EnviarAzure {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;
	private final long DIA = HORA * 24;
	private final long SEMANA = DIA * 7;

	// Pega o dia atual
	Date data = new Date(System.currentTimeMillis());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

	// URL
	final String URL = "https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27%20and%20endswith(meterName,%20%27Spot%27)%20and%20effectiveStartDate%20eq%20"
			+ sdf.format(data) + "-01";

	@Autowired
	private SpotRepository spotRepository;

	@Autowired
	private PriceHistoryRepository priceHistoryRepository;

	// @Scheduled(fixedDelay = SEMANA)
	public void enviar() {

		SpotPrices spotPrices;

		// Recebe o json convertido
		SpotAzureArray azureSpot = solicitarObjetoAzure(URL);

		System.out.println("\nEnviando Azure para o banco de dados");

		boolean proximo = true;

		while (proximo) {
			for (SpotAzure spotAzure : azureSpot.getItems()) {

				if (spotAzure.getUnitPrice() != 0) {

					spotPrices = null;

					// Formata a data
					DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
					OffsetDateTime dataSpot = OffsetDateTime.parse(spotAzure.getEffectiveStartDate());
					String dataSpotFormatada = dataSpot.format(formatarPadrao);
					
					// verificar se já existe esse dado no banco de dados
					spotPrices = selectSpotPrices(spotAzure);

					// Se o dado já estar no banco de dados, entra no IF
					if (spotPrices != null) {
						
						PriceHistory priceHistory = selectPriceHistory(spotPrices);

						// Se o dado não estiver já em priceHistory, entra no IF
						if (priceHistory == null) {
						
						// Insere o dado atual na tabela de historico
						insertPricehistory(spotPrices);
						
						// Atualiza o dado atual do spotPrices com a nova data e preco
						updateSpotPrices(spotAzure, spotPrices, dataSpotFormatada);
						
						}
						
					} else {
						// Se o dado não existir, insere ele no banco de dados
						insertSpotPrices(spotAzure, dataSpotFormatada);

					}

				}

			}

			// Controle se a existe uma proxima pagina
			if (azureSpot.getCount() < 100) {
				proximo = false;
			} else if (azureSpot.getNextPageLink() != null) {
				
				azureSpot = solicitarObjetoAzure(azureSpot.getNextPageLink());
			} else {
				proximo = false;
			}
		}
		
		System.out.println("Azure enviada.");
		
	}

	protected SpotAzureArray solicitarObjetoAzure(String URL) {

		// Instancia o objeto que vai converter o JSON para objeto
		JsonForObjectAzure JsonForAzure = new JsonForObjectAzure();

		SpotAzureArray azureArrayObject = JsonForAzure.converter(URL);

		return azureArrayObject;
	}

	protected SpotPrices selectSpotPrices(SpotAzure spotAzure) {

		return spotRepository.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription("AZURE",spotAzure.getSkuName(),
				spotAzure.getLocation(), spotAzure.getProductName());

	}
	
	protected PriceHistory selectPriceHistory(SpotPrices spotPrices) {

		return priceHistoryRepository.findBySelectUsingcodSpotAndpriceAnddataReq(spotPrices.getCod_spot(),
				spotPrices.getPrice().doubleValue(), spotPrices.getDataReq());

	}

	protected void insertPricehistory(SpotPrices spotPrices) {

		PriceHistory priceHistory = new PriceHistory();

		priceHistory.setCodSpot(spotPrices.getCod_spot());
		priceHistory.setPrice(spotPrices.getPrice().doubleValue());
		priceHistory.setDataReq(spotPrices.getDataReq());

		priceHistoryRepository.save(priceHistory);

	}

	protected void updateSpotPrices(SpotAzure spotAzure, SpotPrices spotPrices, String dataSpotFormatada) {

		BigDecimal preco = new BigDecimal(spotAzure.getUnitPrice()).setScale(5,BigDecimal.ROUND_HALF_UP);
		
		spotPrices.setPrice(preco);
		spotPrices.setDataReq(dataSpotFormatada);

		spotRepository.save(spotPrices);

	}

	protected void insertSpotPrices(SpotAzure spotAzure, String dataSpotFormatada) {
		
		BigDecimal preco = new BigDecimal(spotAzure.getUnitPrice()).setScale(5,BigDecimal.ROUND_HALF_UP);

		SpotPrices newSpotPrice = new SpotPrices();
		newSpotPrice.setCloudName("AZURE");
		newSpotPrice.setInstanceType(spotAzure.getSkuName().replaceAll(" Spot", ""));
		newSpotPrice.setRegion(spotAzure.getLocation());
		newSpotPrice.setProductDescription(spotAzure.getProductName());
		newSpotPrice.setPrice(preco);
		newSpotPrice.setDataReq(dataSpotFormatada);

		spotRepository.save(newSpotPrice);

	}

}
