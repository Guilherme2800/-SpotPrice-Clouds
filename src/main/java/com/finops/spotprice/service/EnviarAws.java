package com.finops.spotprice.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.Region;

@Component
@EnableScheduling
public class EnviarAws {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;

	@Scheduled(fixedDelay = MINUTO * 8)
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

	private void enviarParaBanco(String regiao) {

		// conecta com BD
		ConexaoMariaDb conexao = new ConexaoMariaDb();

		// recebe a conexão
		Connection con = conexao.conectar();

		// Objeto que prepara o código SQL
		PreparedStatement pstm = null;

		// Instancia o cliente AWS
		AmazonEC2 client = AmazonEC2ClientBuilder.standard().withRegion(regiao).build();
		DescribeSpotPriceHistoryRequest request;

		// Pega a data atual
		Date data = new Date(System.currentTimeMillis());

		// Formata para o padrão de leitura da AWS.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {

			request = new DescribeSpotPriceHistoryRequest().withEndTime(sdf.parse(sdf.format(data)))
					.withStartTime(sdf.parse(sdf.format(data)));

			DescribeSpotPriceHistoryResult response = client.describeSpotPriceHistory(request);

			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			pstm = con.prepareStatement("insert into aws (availabilityZone, instanceType,"
					+ "productDescription, spotPrice, timeStam_p) values (?, ?, ?, ?, ?)");

			System.out.println("\nEnviando Região " + regiao + " para o banco de dados");

			boolean proximo = true;

			while (proximo) {

				for (int i = 0; i < response.getSpotPriceHistory().size(); i++) {

					if (response.getSpotPriceHistory().get(i).getAvailabilityZone().toLowerCase().endsWith("a")) {

						pstm.setString(1, response.getSpotPriceHistory().get(i).getAvailabilityZone());
						pstm.setString(2, response.getSpotPriceHistory().get(i).getInstanceType());
						pstm.setString(3, response.getSpotPriceHistory().get(i).getProductDescription());
						pstm.setString(4, response.getSpotPriceHistory().get(i).getSpotPrice());
						pstm.setString(5, dateFormat.format(response.getSpotPriceHistory().get(i).getTimestamp()));

						pstm.execute();

					}
				}

				if (response.getSpotPriceHistory().size() < 1000) {
					proximo = false;
				}

				request.setNextToken(response.getNextToken());
				response = client.describeSpotPriceHistory(request);

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		conexao.desconectar();

	}

}
