package com.finops.spotprice.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.finops.spotprice.model.azurecloud.*;

@Component
@EnableScheduling
public class EnviarAzure {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;

	// Pega o dia atual
	Date data = new Date(System.currentTimeMillis());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

	// URL
	final String URL = "https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27%20and%20endswith(meterName,%20%27Spot%27)%20and%20effectiveStartDate%20eq%20"
			+ sdf.format(data) + "-01";

	// @Scheduled(fixedDelay = HORA)
	public void enviar() {

		// -------------------VARIAVEIS -----------

		// Instancia o objeto de conexão com o banco de dados
		ConexaoMariaDb objetoMariaDb = new ConexaoMariaDb();

		// recebe a conexão
		Connection conexao = objetoMariaDb.conectar();

		// Objeto que prepara o código SQL
		PreparedStatement pstm = null;

		// ------------- INICIO DO MÉTODO ----------------

		// Recebe o json convertido
		SpotAzureArray azureSpot = solicitarObjetoAzure(URL);

		System.out.println("\nEnviando para o banco de dados");

		boolean proximo = true;

		while (proximo) {

			for (SpotAzure spot : azureSpot.getItems()) {

				if (spot.getUnitPrice() != 0) {

					// Formata a data
					DateTimeFormatter formatarPadrao = DateTimeFormatter.ofPattern("uuuu/MM/dd");
					OffsetDateTime dataSpot = OffsetDateTime.parse(spot.getEffectiveStartDate());
					String dataSpotFormatada = dataSpot.format(formatarPadrao);

					// Select para verificar se já existe esse dado no banco de dados
					ResultSet resultadoSelect = selectSpotPrices(pstm, conexao, spot);

					try {
						// Se o dado já estar no banco de dados, entra no IF
						if (resultadoSelect.next()) {

							// Insere o dado atual na tabela de historico
							insertPricehistory(pstm, conexao, resultadoSelect);

							// Atualiza o dado atual do spotPrices com a nova data e preco
							updateSpotPrices(dataSpotFormatada, spot, pstm, conexao, resultadoSelect);

						} else {
							// Se o dado não existir, insere ele no banco de dados
							insertSpotPrices(dataSpotFormatada, spot, pstm, conexao);

						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

			// Controle se a existe uma proxima pagina
			if (azureSpot.getCount() < 100) {
				proximo = false;
			} else if (azureSpot.getNextPageLink() != null) {
				System.out.println(azureSpot.getNextPageLink());
				azureSpot = solicitarObjetoAzure(azureSpot.getNextPageLink());
			} else {
				proximo = false;
			}
		}

		objetoMariaDb.desconectar();

	}

	protected SpotAzureArray solicitarObjetoAzure(String URL) {

		// Instancia o objeto que vai converter o JSON para objeto
		JsonForObjectAzure JsonForAzure = new JsonForObjectAzure();

		SpotAzureArray azureArrayObject = JsonForAzure.converter(URL);

		return azureArrayObject;
	}

	protected ResultSet selectSpotPrices(PreparedStatement pstm, Connection conexao, SpotAzure spot) {

		ResultSet resultado = null;

		try {
			pstm = conexao.prepareStatement("select * from spotprices where cloud_name = 'azure' and instance_type = '"
					+ spot.getSkuName() + "'" + "and region = '" + spot.getLocation() + "' and product_description = '"
					+ spot.getProductName() + "' ");

			resultado = pstm.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultado;

	}

	protected void insertPricehistory(PreparedStatement pstm, Connection conexao, ResultSet resultadoSelect) {

		try {
			pstm = conexao.prepareStatement("insert into pricehistory (cod_spot, price, data_req) values (?, ?, ?)");
			pstm.setString(1, resultadoSelect.getString("cod_spot"));
			pstm.setString(2, resultadoSelect.getString("price"));
			pstm.setString(3, resultadoSelect.getString("data_req"));

			pstm.execute();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void updateSpotPrices(String dataSpotFormatada, SpotAzure spot, PreparedStatement pstm, Connection conexao,
			ResultSet resultadoSelect) {

		try {
			pstm = conexao.prepareStatement("update spotprices set price = ?, data_req = ? where cod_spot = ?");

			pstm.setDouble(1, spot.getUnitPrice());
			pstm.setString(2, dataSpotFormatada);
			pstm.setString(3, resultadoSelect.getString("cod_spot"));

			pstm.execute();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void insertSpotPrices(String dataSpotFormatada, SpotAzure spot, PreparedStatement pstm,
			Connection conexao) {

		try {
			pstm = conexao.prepareStatement("insert into spotprices (cloud_name, instance_type,"
					+ "region, product_description, price, data_req) values (?, ?, ?, ?, ?, ?)");

			pstm.setString(1, "AZURE");
			pstm.setString(2, spot.getSkuName());
			pstm.setString(3, spot.getLocation());
			pstm.setString(4, spot.getProductName());
			pstm.setDouble(5, spot.getUnitPrice());
			pstm.setString(6, dataSpotFormatada);

			pstm.execute();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

}
