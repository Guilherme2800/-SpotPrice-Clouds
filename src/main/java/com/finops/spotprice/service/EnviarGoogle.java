package com.finops.spotprice.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.finops.spotprice.model.googlecloud.SpotGoogle;
import com.finops.spotprice.model.googlecloud.SpotGoogleArray;

public class EnviarGoogle {

	// Instancia o objeto de conexão com o banco de dados
	ConexaoMariaDb objetoMariaDb = new ConexaoMariaDb();

	// recebe a conexão
	Connection conexao = objetoMariaDb.conectar();

	// Objeto que prepara o código SQL
	PreparedStatement pstm = null;

	// Instancia o objeto que vai converter o JSON para objeto
	JsonForObjectGoogle converterJson = new JsonForObjectGoogle();
	
	DecimalFormat formatar = new DecimalFormat("0.0000");

	public void enviar() {
		System.out.println("Entrou no metodo");

		SpotGoogleArray googleSpot = converterJson.converter(
				"https://cloudbilling.googleapis.com/v1/services/6F81-5844-456A/skus?key=AIzaSyA9eUyryawCIAgta6f2TzUgVEijCmvauns");

		try {

			System.out.println("\nEnviando para o banco de dados");

			boolean proximo = true;
			
			while (proximo) {
				System.out.println("Entrou while");
				for (SpotGoogle spot : googleSpot.getSpot()) {
					
					System.out.println("entrou for");

					if (spot.getCategory().getUsageType().contains("Preemptible")) {

						DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
						OffsetDateTime dataSpot = OffsetDateTime.parse(spot.getPricingInfo().get(0).getEffectiveTime());
						String dataSpotFormatada = dataSpot.format(formatarPadrao);

						
						String unitPrice = formatar.format(spot.getPricingInfo().get(0).getPricingExpression().getTieredRates().get(0).getUnitPrice().getNanos() * Math.pow(10, -9));		
						unitPrice += spot.getPricingInfo().get(0).getPricingExpression().getTieredRates().get(0).getUnitPrice().getUnits();
						
						// Select para verificar se já existe esse dado no banco de dados
						pstm = conexao.prepareStatement(
								"select * from spotprices where cloud_name = 'azure' and instance_type = '"
										+ spot.getCategory().getResourceGroup() + "'" + "and region = '" + spot.getCategory().getResourceGroup()
										+ "' and product_description = '" + spot.getDescription() + "' ");

						ResultSet resultadoSelect = pstm.executeQuery();

						// Se o dado já estar no banco de dados, entra no IF
						if (resultadoSelect.next()) {
							
							System.out.println(spot);

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
						
							pstm.setString(1, unitPrice);
							pstm.setString(2, dataSpotFormatada);
							pstm.setString(3, resultadoSelect.getString("cod_spot"));

							pstm.execute();

						} else {
							
							System.out.println(spot);
							// Se o dado não existir, insere ele no banco de dados
							pstm = conexao.prepareStatement("insert into spotprices (cloud_name, instance_type,"
									+ "region, product_description, price, data_req) values (?, ?, ?, ?, ?, ?)");

							pstm.setString(1, "GOOGLE");
							pstm.setString(2, spot.getCategory().getResourceGroup());
							pstm.setString(3, spot.getCategory().getResourceGroup());
							pstm.setString(4, spot.getDescription());
							pstm.setString(5, unitPrice);
							pstm.setString(6, dataSpotFormatada);

							pstm.execute();

						}

					}

				}

				if (googleSpot.getNextPageToken() == null || googleSpot.getSpot().size() != 5000) {
					proximo = false;
				} else {
					System.out.println(googleSpot.getNextPageToken());
					googleSpot = converterJson.converter(
							"https://cloudbilling.googleapis.com/v1/services/6F81-5844-456A/skus?key=AIzaSyA9eUyryawCIAgta6f2TzUgVEijCmvauns&pageToken="
									+ googleSpot.getNextPageToken());
				}
			}
			
			System.out.println("Terminou google");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		objetoMariaDb.desconectar();

	}

}
