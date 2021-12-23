package com.finops.spotprice.component;

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
import com.finops.spotprice.persistence.entity.PriceHistorySpot;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.PriceHistoryRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;

@Component
@EnableScheduling
public class EnviarAwsSpot {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;
	private final long DIA = HORA * 24;

	@Autowired
	private SpotRepository spotRepository;

	@Autowired
	private PriceHistoryRepository priceHistoryRepository;

	
	//@Scheduled(fixedDelay = DIA)
	public void correrRegioes() {
		
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		DescribeRegionsResult regions_response = ec2.describeRegions();

		System.out.println("Iniciando Envio da AWS...");

		for (Region region : regions_response.getRegions()) {
			System.out.println("\n--------Enviando região: " + region.getRegionName() + "----------");
			enviarParaBanco(region.getRegionName());
			System.out.println("\n--------Conteúdo Enviado----------");
		}

		System.out.println("Finalizado Envio da AWS...");
	}

	protected boolean enviarParaBanco(String regiao) {

		boolean enviado = false;
		
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
					String dataFormadata = sdf.format(spotAws.getTimestamp());

					// verifica se já existe esse dado no banco de dados
					spotPrices = selectSpotPrice(spotAws, regiao);

					// Se o dado já estar no banco de dados, entra no IF
					if (spotPrices != null) {

						PriceHistorySpot priceHistory = selectPriceHistory(spotPrices);

						// Se o dado não estiver já em priceHistory, entra no IF
						if (priceHistory == null) {

							// Insere o dado atual na tabela pricehistory
							insertPricehistory(spotPrices);

							// Atualiza o dado atual na tabela SpotPrices com a nova data e preco 
							updateSpotPrice(spotAws, spotPrices, dataFormadata);

						}

					} else {
						// Se o dado não existir, insere ele no banco de dados na tabela spotPrices
						insertSpotPrice(spotAws, dataFormadata, regiao);
					}

				}

				if (arrayInstanciasAws.getSpotPriceHistory().size() < 1000) {
					proximo = false;
				}

				request.setNextToken(arrayInstanciasAws.getNextToken());
				arrayInstanciasAws = client.describeSpotPriceHistory(request);

			}
			
			enviado = true;
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return enviado;
	}

	protected SpotPrices selectSpotPrice(SpotPrice spotAws, String regiao) {

		return spotRepository.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription("AWS",
				spotAws.getInstanceType(), regiao, spotAws.getProductDescription());

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

	protected boolean insertSpotPrice(SpotPrice spotAws, String dataFormatada, String regiao) {

		boolean salvoSucesso = false;
		
		BigDecimal preco = new BigDecimal(Double.parseDouble(spotAws.getSpotPrice())).setScale(5,BigDecimal.ROUND_HALF_UP);
		
		SpotPrices newSpotPrice = new SpotPrices();
		newSpotPrice.setCloudName("AWS");
		newSpotPrice.setInstanceType(spotAws.getInstanceType());
		newSpotPrice.setRegion(regiao);
		newSpotPrice.setProductDescription(spotAws.getProductDescription());
		newSpotPrice.setPrice(preco);
		newSpotPrice.setDataReq(dataFormatada);

		SpotPrices spotSalva = spotRepository.save(newSpotPrice);

		if (spotSalva != null) {
			salvoSucesso = true;
		}

		return salvoSucesso;
		
	}
	
	protected boolean updateSpotPrice(SpotPrice spotAws, SpotPrices spotPrices, String dataFormatada) {

		boolean salvoSucesso = false;
		
		BigDecimal preco = new BigDecimal(Double.parseDouble(spotAws.getSpotPrice())).setScale(5,BigDecimal.ROUND_HALF_UP);
		
		spotPrices.setPrice(preco);
		spotPrices.setDataReq(dataFormatada);

		SpotPrices spotSalva = spotRepository.save(spotPrices);
		
		if (spotSalva != null) {
			salvoSucesso = true;
		}

		return salvoSucesso;

	}

	

}
