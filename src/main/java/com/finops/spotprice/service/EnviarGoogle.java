package com.finops.spotprice.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.finops.spotprice.model.googlecloud.SpotGoogle;
import com.finops.spotprice.model.googlecloud.SpotGoogleArray;

@Component
@EnableScheduling
public class EnviarGoogle {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;
	
	@Scheduled(fixedDelay = HORA)
	public void enviar() {
		
		// ------------- VARIAVEIS ------------
		
		// Instancia o objeto de conexão com o banco de dados
		ConexaoMariaDb objetoMariaDb = new ConexaoMariaDb();

		// recebe a conexão
		Connection conexao = objetoMariaDb.conectar();

		// Objeto que prepara o código SQL
		PreparedStatement pstm = null;

		// Instancia o objeto que vai converter o JSON para objeto
		JsonForObjectGoogle converterJson = new JsonForObjectGoogle();

		// Formatar valor da instancia
		DecimalFormat formatar = new DecimalFormat("0.000000");

		// ------------------- INICIO METODO ----------
		
		// Recebe o json convertido
		SpotGoogleArray googleSpot = converterJson.converter(
				"https://cloudbilling.googleapis.com/v1/services/6F81-5844-456A/skus?key=AIzaSyA9eUyryawCIAgta6f2TzUgVEijCmvauns");

		try {

			System.out.println("\nEnviando para o banco de dados");

			boolean proximo = true;

			int i = 0;

			while (proximo) {

				for (SpotGoogle spot : googleSpot.getSkus()) {

					if (spot.getDescription().contains("Spot")) {

						    // Formata a data
							DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
							OffsetDateTime dataSpot = OffsetDateTime.parse(spot.getPricingInfo().get(0).getEffectiveTime());
							String dataSpotFormatada = dataSpot.format(formatarPadrao);
							
							//Realiza o calculo do preço da instancia 
							String unitPrice = formatar.format(spot.getPricingInfo().get(0).getPricingExpression().getTieredRates().get(0).getUnitPrice().getNanos() * Math.pow(10, -9));
							unitPrice += spot.getPricingInfo().get(0).getPricingExpression().getTieredRates().get(0).getUnitPrice().getUnits();
						
						for (int countRegions = 0; countRegions < spot.getServiceRegions().size(); countRegions++) {
				

							//-------------------COMANDOS SQL-----------------------
							
							// Select para verificar se já existe esse dado no banco de dados
							pstm = conexao.prepareStatement(
									"select * from spotprices where cloud_name = 'google' and instance_type = '"
											+ spot.getCategory().getResourceGroup() + "'" + "and region = '"
											+ spot.getServiceRegions().get(countRegions)
											+ "' and product_description = '" + spot.getDescription() + "' ");

							ResultSet resultadoSelect = pstm.executeQuery();

							// Se o dado já estar no banco de dados, entra no IF
							if (resultadoSelect.next()) {
								
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

								pstm.setString(1, unitPrice.replaceAll(",", "."));
								pstm.setString(2, dataSpotFormatada);
								pstm.setString(3, resultadoSelect.getString("cod_spot"));

								pstm.execute();

							} else {

								// Se o dado não existir, insere ele no banco de dados
								pstm = conexao.prepareStatement("insert into spotprices (cloud_name, instance_type,"
										+ "region, product_description, price, data_req) values (?, ?, ?, ?, ?, ?)");

								pstm.setString(1, "GOOGLE");
								pstm.setString(2, spot.getCategory().getResourceGroup());
								pstm.setString(3, spot.getServiceRegions().get(countRegions));
								pstm.setString(4, spot.getDescription());
								pstm.setString(5, unitPrice.replaceAll(",", "."));
								pstm.setString(6, dataSpotFormatada);
								pstm.execute();

							}
							i++;
						}

					}

				}

				// Controle se a existe uma proxima pagina
				if (googleSpot.getNextPageToken() == null || googleSpot.getSkus().size() != 5000) {
					proximo = false;
				} else {
					System.out.println(googleSpot.getNextPageToken());
					googleSpot = converterJson.converter(
							"https://cloudbilling.googleapis.com/v1/services/6F81-5844-456A/skus?key=AIzaSyA9eUyryawCIAgta6f2TzUgVEijCmvauns&pageToken="
									+ googleSpot.getNextPageToken());
				}
			}

			System.out.println("Terminou google: " + i + " Instancias atualizadas");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		objetoMariaDb.desconectar();

	}

}
