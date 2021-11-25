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

	private SpotPrices spot;

	@Scheduled(fixedDelay = HORA)
	public void enviar() {

		// Recebe o json convertido
		SpotGoogleArray googleSpot = solicitarObjetoGoogle(URL);

		System.out.println("\nEnviando para o banco de dados");

		boolean proximo = true;

		while (proximo) {

			for (SpotGoogle spotGoogle : googleSpot.getSkus()) {

				if (spotGoogle.getDescription().contains("Spot")) {

					// Formata a data
					DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
					OffsetDateTime dataSpot = OffsetDateTime
							.parse(spotGoogle.getPricingInfo().get(0).getEffectiveTime());
					String dataSpotFormatada = dataSpot.format(formatarPadrao);

					// Realiza o calculo do preço da instancia
					String unitPrice = calcularPreco(spotGoogle);

					for (int countRegions = 0; countRegions < spotGoogle.getServiceRegions().size(); countRegions++) {

						spot = null;

						spot = spotRepository.findBySelectUsinginstanceTypeAndregionAndProductDescription(
								spotGoogle.getCategory().getResourceGroup(),
								spotGoogle.getServiceRegions().get(countRegions), spotGoogle.getDescription());

						System.out.println(spot);

							// Se o dado já estar no banco de dados, entra no IF
							if (resultadoSelect.next()) {

								// Insere o dado atual na tabela de historico
								insertPricehistory(conexao, resultadoSelect);

								// Atualiza o dado atual com a nova data e preco
								updateSpotPrices(dataSpotFormatada, spotGoogle, conexao, resultadoSelect, unitPrice);

							} else {

								// Se o dado não existir, insere ele no banco de dados
								insertSpotPrices(dataSpotFormatada, spotGoogle, conexao, unitPrice, countRegions);

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

	protected String calcularPreco(SpotGoogle spot) {

		// Formatar valor da instancia
		DecimalFormat formatar = new DecimalFormat("0.000000");

		// Realiza o calculo do preço da instancia
		String preco = formatar.format(
				spot.getPricingInfo().get(0).getPricingExpression().getTieredRates().get(0).getUnitPrice().getNanos()
						* Math.pow(10, -9));

		preco += spot.getPricingInfo().get(0).getPricingExpression().getTieredRates().get(0).getUnitPrice().getUnits();

		return preco;
	}

//	
	protected SpotGoogleArray solicitarObjetoGoogle(String URL) {

		// Instancia o objeto que vai converter o JSON para objeto
		JsonForObjectGoogle JsonForGoogle = new JsonForObjectGoogle();

		SpotGoogleArray googleArrayObject = JsonForGoogle.converter(URL);

		return googleArrayObject;
	}
//
//	protected ResultSet selectSpotPrices(Connection conexao, SpotGoogle spot, int countRegions) {
//
//		PreparedStatement pstm = null;
//
//		ResultSet resultado = null;
//
//		try {
//			pstm = conexao.prepareStatement("select * from spotprices where cloud_name = 'google' and instance_type = '"
//					+ spot.getCategory().getResourceGroup() + "'" + "and region = '"
//					+ spot.getServiceRegions().get(countRegions) + "' and product_description = '"
//					+ spot.getDescription() + "' ");
//
//			resultado = pstm.executeQuery();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return resultado;
//
//	}
//
//	protected void insertPricehistory(Connection conexao, ResultSet resultadoSelect) {
//
//		PreparedStatement pstm = null;
//
//		try {
//			pstm = conexao.prepareStatement("insert into pricehistory (cod_spot, price, data_req) values (?, ?, ?)");
//			pstm.setString(1, resultadoSelect.getString("cod_spot"));
//			pstm.setString(2, resultadoSelect.getString("price"));
//			pstm.setString(3, resultadoSelect.getString("data_req"));
//
//			pstm.execute();
//
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	protected void updateSpotPrices(String dataSpotFormatada, SpotGoogle spot, Connection conexao,
//			ResultSet resultadoSelect, String unitPrice) {
//
//		PreparedStatement pstm = null;
//
//		try {
//			pstm = conexao.prepareStatement("update spotprices set price = ?, data_req = ? where cod_spot = ?");
//
//			pstm.setString(1, unitPrice.replaceAll(",", "."));
//			pstm.setString(2, dataSpotFormatada);
//			pstm.setString(3, resultadoSelect.getString("cod_spot"));
//
//			pstm.execute();
//
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	protected void insertSpotPrices(String dataSpotFormatada, SpotGoogle spot, Connection conexao, String unitPrice,
//			int countRegions) {
//
//		PreparedStatement pstm = null;
//
//		try {
//			pstm = conexao.prepareStatement("insert into spotprices (cloud_name, instance_type,"
//					+ "region, product_description, price, data_req) values (?, ?, ?, ?, ?, ?)");
//
//			pstm.setString(1, "GOOGLE");
//			pstm.setString(2, spot.getCategory().getResourceGroup());
//			pstm.setString(3, spot.getServiceRegions().get(countRegions));
//			pstm.setString(4, spot.getDescription());
//			pstm.setString(5, unitPrice.replaceAll(",", "."));
//			pstm.setString(6, dataSpotFormatada);
//
//			pstm.execute();
//
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//		}
//
//	}

}
