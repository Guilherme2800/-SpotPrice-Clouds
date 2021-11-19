package com.finops.spotprice.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.amazonaws.services.ec2.model.SpotPrice;

@Component
@EnableScheduling
public class EnviarAws {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;

	//@Scheduled(fixedDelay = HORA)
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
		ConexaoMariaDb objetoMariaDb = new ConexaoMariaDb();

		// recebe a conexão
		Connection conexao = objetoMariaDb.conectar();

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

			DescribeSpotPriceHistoryResult arrayInstanciasAws = client.describeSpotPriceHistory(request);

			System.out.println("\nEnviando Região " + regiao + " para o banco de dados");

			boolean proximo = true;

			// Esse While controla se tem uma proxima pagina de dados a carregar
			while (proximo) {

				// percorre o array de instancias da AWS
				for (SpotPrice spot : arrayInstanciasAws.getSpotPriceHistory()) {

						// Select para verificar se já existe esse dado no banco de dados
						pstm = conexao.prepareStatement(
								"select * from spotprices where cloud_name = 'aws' and instance_type = '"
										+ spot.getInstanceType() + "'" + "and region = '" + regiao
										+ "' and product_description = '" + spot.getProductDescription() + "' ");

						ResultSet resultadoSelect = pstm.executeQuery();

						// Se o dado já estar no banco de dados, entra no IF
						if (resultadoSelect.next()) {
							
							//-------------------COMANDOS SQL-----------------------

							pstm = conexao.prepareStatement(
									"select * from pricehistory where cod_spot = '"+ resultadoSelect.getString("cod_spot") +"' and price = '"
											+ resultadoSelect.getString("price") + "'" + "and data_req = '" + resultadoSelect.getString("data_req")
											+ "' ");

							ResultSet resultadoSelectHistory = pstm.executeQuery();
							
							if(!resultadoSelectHistory.next()) {
								
								// Insere o dado atual na tabela de historico
								pstm = conexao.prepareStatement(
										"insert into pricehistory (cod_spot, price, data_req) values (?, ?, ?)");

								pstm.setString(1, resultadoSelect.getString("cod_spot"));
								pstm.setString(2, resultadoSelect.getString("price"));
								pstm.setString(3, resultadoSelect.getString("data_req"));

								pstm.execute();

								// Atualiza o dado atual com a nova data e preco
								pstm = conexao.prepareStatement(
										"update spotprices set price = ?, data_req = ? where cod_spot = ?");
								pstm.setString(1, spot.getSpotPrice());
								pstm.setString(2, sdf.format(spot.getTimestamp()));
								pstm.setString(3, resultadoSelect.getString("cod_spot"));

								pstm.execute();
								
							}

						} else {
							// Se o dado não existir, insere ele no banco de dados
							pstm = conexao.prepareStatement("insert into spotprices (cloud_name, instance_type,"
									+ "region, product_description, price, data_req) values (?, ?, ?, ?, ?, ?)");

							pstm.setString(1, "AWS");
							pstm.setString(2, spot.getInstanceType());
							pstm.setString(3, regiao);
							pstm.setString(4, spot.getProductDescription());
							pstm.setString(5, spot.getSpotPrice());
							pstm.setString(6, sdf.format(spot.getTimestamp()));

							pstm.execute();
							
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		objetoMariaDb.desconectar();

	}

}
