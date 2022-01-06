package com.finops.spotprice.component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.finops.spotprice.model.InstancesAzureArray;
import com.finops.spotprice.model.InstanceAzure;
import com.finops.spotprice.persistence.entity.PriceHistorySpot;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.PriceHistoryRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;
import com.finops.spotprice.util.JsonForObjectAzure;

@Component
@EnableScheduling
public class EnviarAzureSpot {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;
	private final long DIA = HORA * 24;
	private final long SEMANA = DIA * 7;

	// Pega o ano e mes atual
	Date data = new Date(System.currentTimeMillis());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

	// URL
	final String URL = "https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27%20and%20endswith(meterName,%20%27Spot%27)%20and%20effectiveStartDate%20eq%20"
			+ sdf.format(data) + "-01";

	@Autowired
	private SpotRepository spotRepository;

	@Autowired
	private PriceHistoryRepository priceHistoryRepository;
	
	//@Scheduled(fixedDelay = SEMANA)
	public boolean enviar() {

		boolean enviado = false;

		SpotPrices spotPrices;

		// Recebe o json convertido
		InstancesAzureArray azureSpot = solicitarObjetoAzure(URL);

		System.out.println("\nEnviando Azure Spot para o banco de dados");

		boolean proximo = true;

		while (proximo) {
			for (InstanceAzure spotAzure : azureSpot.getItems()) {

				if (spotAzure.getUnitPrice() != 0) {

					spotPrices = null;

					// Formata a data
					DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
					OffsetDateTime dataSpot = OffsetDateTime.parse(spotAzure.getEffectiveStartDate());
					String dataFormatada = dataSpot.format(formatarPadrao);

					// verificar se já existe esse dado no banco de dados
					spotPrices = selectSpotPrice(spotAzure);

					// Se o dado já estar no banco de dados, entra no IF
					if (spotPrices != null) {

						PriceHistorySpot priceHistory = selectPriceHistory(spotPrices);

						// Se o dado não estiver já em priceHistory, entra no IF
						if (priceHistory == null) {

							// Insere o dado atual na tabela de historico
							insertPricehistory(spotPrices);

							// Atualiza o dado atual do spotPrices com a nova data e preco
							updateSpotPrice(spotAzure, spotPrices, dataFormatada);

						}

					} else {

						// Se o dado não existir, insere ele no banco de dados
						insertSpotPrice(spotAzure, dataFormatada);

					}

				}

			}

			// Controle se a existe uma proxima pagina
			if (azureSpot.getCount() < 100) {
				proximo = false;
				enviado = true;
			} else if (azureSpot.getNextPageLink() != null) {
				azureSpot = solicitarObjetoAzure(azureSpot.getNextPageLink());
			} else {
				proximo = false;
				enviado = true;
			}
		}

		System.out.println("Azure enviada.");
		return enviado;

	}

	protected InstancesAzureArray solicitarObjetoAzure(String URL) {

		// Instancia o objeto que vai converter o JSON para objeto
		JsonForObjectAzure JsonForAzure = new JsonForObjectAzure();

		InstancesAzureArray azureArrayObject = JsonForAzure.converter(URL);

		return azureArrayObject;
	}
	
	// ------------ METODOS DE SQL --------

	protected SpotPrices selectSpotPrice(InstanceAzure spotAzure) {

		return spotRepository.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription("AZURE",
				spotAzure.getSkuName().replaceAll(" Spot", ""), spotAzure.getLocation(), spotAzure.getProductName());

	}
	
	protected PriceHistorySpot selectPriceHistory(SpotPrices spotPrices) {

		return priceHistoryRepository.findBySelectUsingcodSpotAndpriceAnddataReq(spotPrices.getCod_spot(),
				spotPrices.getPrice().doubleValue(), spotPrices.getDataReq());
		
	}

	protected boolean insertPricehistory(SpotPrices spotPrices) {

		boolean salvoSucesso = false;

		PriceHistorySpot priceHistory = new PriceHistorySpot();

		priceHistory.setCodSpot(spotPrices.getCod_spot());
		priceHistory.setPrice(spotPrices.getPrice().doubleValue());
		priceHistory.setDataReq(spotPrices.getDataReq());

		PriceHistorySpot historySalvo = priceHistoryRepository.save(priceHistory);

		if (historySalvo != null) {
			salvoSucesso = true;
		}

		return salvoSucesso;

	}

	protected boolean updateSpotPrice(InstanceAzure spotAzure, SpotPrices spotPrices, String dataSpotFormatada) {

		boolean salvoSucesso = false;

		BigDecimal preco = new BigDecimal(spotAzure.getUnitPrice()).setScale(5, BigDecimal.ROUND_HALF_UP);

		spotPrices.setPrice(preco);
		spotPrices.setDataReq(dataSpotFormatada);

		SpotPrices spotSalva = spotRepository.save(spotPrices);

		if (spotSalva != null) {
			salvoSucesso = true;
		}

		return salvoSucesso;

	}

	protected boolean insertSpotPrice(InstanceAzure spotAzure, String dataSpotFormatada) {

		boolean salvoSucesso = false;

		BigDecimal preco = new BigDecimal(spotAzure.getUnitPrice()).setScale(5, BigDecimal.ROUND_HALF_UP);

		SpotPrices newSpotPrice = new SpotPrices();
		newSpotPrice.setCloudName("AZURE");
		newSpotPrice.setInstanceType(spotAzure.getSkuName().replaceAll(" Spot", ""));
		newSpotPrice.setRegion(spotAzure.getLocation());
		newSpotPrice.setProductDescription(spotAzure.getProductName());
		newSpotPrice.setPrice(preco);
		newSpotPrice.setDataReq(dataSpotFormatada);

		SpotPrices spotSalva = spotRepository.save(newSpotPrice);

		if (spotSalva != null) {
			salvoSucesso = true;
		}

		return salvoSucesso;
	}

}
