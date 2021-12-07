package com.finops.spotprice.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.SpotPrice;
import com.finops.spotprice.repository.PriceHistoryRepository;
import com.finops.spotprice.repository.SpotRepository;

@Component
@EnableScheduling
public class EnviarAws {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;
	private final long DIA = HORA * 24;

	@Autowired
	private SpotRepository spotRepository;

	@Autowired
	private PriceHistoryRepository priceHistoryRepository;

	// @Scheduled(fixedDelay = DIA)
	public void correrRegioes() {

		BasicAWSCredentials awsCredenciais = new BasicAWSCredentials("AKIA6KDLKFZSQL3QS5AX",
				"jG0NuRpfXS/1gRzPAgk0KDIDSsmH7rRjEMY7bFKl");

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCredenciais)).build();
		DescribeRegionsResult regions_response = ec2.describeRegions();

		System.out.println("Iniciando Envio da AWS...");

		for (Region region : regions_response.getRegions()) {
			System.out.println("\n--------Enviando região: " + region.getRegionName() + "----------");
			enviarParaBanco(region.getRegionName());
			System.out.println("\n--------Conteúdo Enviado----------");
		}

		System.out.println("Finalizado Envio da AWS...");
	}

	private void enviarParaBanco(String regiao) {

		boolean proximo = true;
		
		SpotPrices spotPrices;

		// Instancia o cliente AWS na região especificada
		AmazonEC2 client = AmazonEC2ClientBuilder.standard().withRegion(regiao).build();
		DescribeSpotPriceHistoryRequest request;

		// Pega a data atual
		Date data = new Date(System.currentTimeMillis());

		// Formata para o padrão de leitura da AWS.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {

			request = new DescribeSpotPriceHistoryRequest().withEndTime(sdf.parse(sdf.format(data)))
					.withStartTime(sdf.parse(sdf.format(data)));

			DescribeSpotPriceHistoryResult arrayInstanciasAws = client.describeSpotPriceHistory(request);

			System.out.println("\nEnviando Região " + regiao + " para o banco de dados");

			// Esse While controla se tem uma proxima pagina de dados a carregar
			while (proximo) {

				// percorre o array de instancias da AWS
				for (SpotPrice spotAws : arrayInstanciasAws.getSpotPriceHistory()) {

					spotPrices = null;
					String dataSpotFormadata = sdf.format(spotAws.getTimestamp());

					// verifica se já existe esse dado no banco de dados
					spotPrices = selectSpotPrices(spotAws, regiao);

					// Se o dado já estar no banco de dados, entra no IF
					if (spotPrices != null) {

						PriceHistory priceHistory = selectPriceHistory(spotPrices);

						// Se o dado não estiver já em priceHistory, entra no IF
						if (priceHistory == null) {

							// Insere o dado atual na tabela pricehistory
							insertPricehistory(spotPrices);

							// Atualiza o dado atual na tabela SpotPrices com a nova data e preco 
							updateSpotPrices(spotAws, spotPrices, dataSpotFormadata);

						}

					} else {
						// Se o dado não existir, insere ele no banco de dados na tabela spotPrices
						insertSpotPrices(spotAws, dataSpotFormadata, regiao);
					}

				}

				if (arrayInstanciasAws.getSpotPriceHistory().size() < 1000) {
					proximo = false;
				}

				request.setNextToken(arrayInstanciasAws.getNextToken());
				arrayInstanciasAws = client.describeSpotPriceHistory(request);

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected SpotPrices selectSpotPrices(SpotPrice spotAws, String regiao) {

		return spotRepository.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription("AWS",
				spotAws.getInstanceType(), regiao, spotAws.getProductDescription());

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

	protected void updateSpotPrices(SpotPrice spotAws, SpotPrices spotPrices, String dataSpotFormatada) {

		BigDecimal preco = new BigDecimal(Double.parseDouble(spotAws.getSpotPrice())).setScale(5,BigDecimal.ROUND_HALF_UP);
		
		spotPrices.setPrice(preco);
		spotPrices.setDataReq(dataSpotFormatada);

		spotRepository.save(spotPrices);

	}

	protected void insertSpotPrices(SpotPrice spotAws, String dataSpotFormatada, String regiao) {

		BigDecimal preco = new BigDecimal(Double.parseDouble(spotAws.getSpotPrice())).setScale(5,BigDecimal.ROUND_HALF_UP);
		
		SpotPrices newSpotPrice = new SpotPrices();
		newSpotPrice.setCloudName("AWS");
		newSpotPrice.setInstanceType(spotAws.getInstanceType());
		newSpotPrice.setRegion(regiao);
		newSpotPrice.setProductDescription(spotAws.getProductDescription());
		newSpotPrice.setPrice(preco);
		newSpotPrice.setDataReq(dataSpotFormatada);

		spotRepository.save(newSpotPrice);

	}

}
