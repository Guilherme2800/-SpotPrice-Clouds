package com.finops.spotprice.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.finops.spotprice.persistence.entity.InstanceNormalPrice;
import com.finops.spotprice.persistence.entity.PriceHistorySpot;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.InstanceNormalRepository;
import com.finops.spotprice.persistence.repository.PriceHistoryRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;
import com.finops.spotprice.util.JsonForObjectAzure;

@Component
@EnableScheduling
public class EnviarAzureNormal {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;
	private final long DIA = HORA * 24;
	private final long SEMANA = DIA * 7;

	// Pega o dia atual
	Date data = new Date(System.currentTimeMillis());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

	// URL
	final String URL = "https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27%20and%20effectiveStartDate%20eq%20"
			+ sdf.format(data) + "-01";

	@Autowired
	private InstanceNormalRepository instanceRepository;

	@Autowired
	private SpotRepository spotRepository;

	// @Scheduled(fixedDelay = SEMANA)
	public void enviar() {

		InstanceNormalPrice instanceNormal;

		// Recebe o json convertido
		SpotAzureArray azureSpot = solicitarObjetoAzure(URL);

		System.out.println("\nEnviando Azure para o banco de dados");

		boolean proximo = true;

		while (proximo) {
			for (SpotAzure spotAzure : azureSpot.getItems()) {

				if (spotAzure.getUnitPrice() != 0 && !spotAzure.getSkuName().contains("Spot")) {

					instanceNormal = null;

					// Formata a data
					DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
					OffsetDateTime dataSpot = OffsetDateTime.parse(spotAzure.getEffectiveStartDate());
					String dataSpotFormatada = dataSpot.format(formatarPadrao);

					// verificar se já existe esse dado no banco de dados
					instanceNormal = selectInstancePrice(spotAzure);

					// Se existir na entra no IF
					if (instanceNormal != null) {

						// Atualiza o valor e data da requisição
						updateInstancePrice(spotAzure, instanceNormal, dataSpotFormatada);

					} else {

						List<SpotPrices> spotPrices = spotRepository.findBySelectUsingcloudNameAndinstanceTypeAndregion(
								"AZURE", spotAzure.getSkuName(), spotAzure.getLocation());

						if (spotPrices != null) {
							// Se não existir, insere os dados na tabela
							insertInstancePrice(spotAzure, dataSpotFormatada);
						}

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

	protected InstanceNormalPrice selectInstancePrice(SpotAzure spotAzure) {

		return instanceRepository.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription("AZURE",
				spotAzure.getSkuName(), spotAzure.getLocation(), spotAzure.getProductName());

	}

	protected void updateInstancePrice(SpotAzure spotAzure, InstanceNormalPrice instanceNormal, String dataSpotFormatada) {

		BigDecimal preco = new BigDecimal(spotAzure.getUnitPrice()).setScale(5, BigDecimal.ROUND_HALF_UP);

		instanceNormal.setPrice(preco);
		instanceNormal.setDataReq(dataSpotFormatada);

		instanceRepository.save(instanceNormal);

	}

	protected void insertInstancePrice(SpotAzure spotAzure, String dataSpotFormatada) {

		BigDecimal preco = new BigDecimal(spotAzure.getUnitPrice()).setScale(5, BigDecimal.ROUND_HALF_UP);

		InstanceNormalPrice instanceNormal = new InstanceNormalPrice();
		instanceNormal.setCloudName("AZURE");
		instanceNormal.setInstanceType(spotAzure.getSkuName().replaceAll(" Spot", ""));
		instanceNormal.setRegion(spotAzure.getLocation());
		instanceNormal.setProductDescription(spotAzure.getProductName());
		instanceNormal.setPrice(preco);
		instanceNormal.setDataReq(dataSpotFormatada);

		instanceRepository.save(instanceNormal);

	}

}
