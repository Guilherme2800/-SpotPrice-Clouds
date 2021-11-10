package com.finops.spotprice.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finops.spotprice.model.InstancesAzure;
import com.finops.spotprice.model.SpotAzure;
import com.finops.spotprice.repository.SpotRepository;


public class EnviarAzure {
	
	    // Instancia o objeto de conexão com o banco de dados
		ConexaoMariaDb conexao = new ConexaoMariaDb();

		// recebe a conexão
		Connection con = conexao.conectar();

		// Objeto que prepara o código SQL
		PreparedStatement pstm = null;

		// Instancia o objeto que vai converter o JSON para objeto
		JsonForObjectAzure converter = new JsonForObjectAzure();
		
		// Pega o dia atual
		Date data = new Date(System.currentTimeMillis()); 
		SimpleDateFormat formatarDate = new SimpleDateFormat("yyyy-MM");

		public void enviar() {

			// Recebe o json convertido
			SpotAzure azureSpot = converter.converter( 
					"https://prices.azure.com/api/retail/prices?$skip=0&$filter=serviceName%20eq%20%27Virtual%20Machines%27%20and%20priceType%20eq%20%27Consumption%27%20and%20endswith(meterName,%20%27Spot%27)%20and%20effectiveStartDate%20eq%20" 
							+ formatarDate.format(data) + "-01"); 

			try {
				pstm = con.prepareStatement("insert into azure (currency_code, tier_minimum_units, retail_price, unit_price, arm_region_name, "
						+ "location, effective_start_date, meter_id, meter_name, product_id, sku_id, "
						+ "product_name, sku_name, service_name, service_id, service_family, "
						+ "unit_of_measure, typ_e, is_primary_meter_region, arm_sku_name) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				System.out.println("\nEnviando para o banco de dados");

				boolean proximo = true;

				while (proximo) {

					for (int i = 0; i < azureSpot.Items.size(); i++) {

						if (azureSpot.Items.get(i).getUnitPrice() != 0) {
							pstm.setString(1, azureSpot.Items.get(i).getCurrencyCode());
							pstm.setDouble(2, azureSpot.Items.get(i).getTierMinimumUnits());
							pstm.setDouble(3, azureSpot.Items.get(i).getRetailPrice());
							pstm.setDouble(4, azureSpot.Items.get(i).getUnitPrice());
							pstm.setString(5, azureSpot.Items.get(i).getArmRegionName());
							pstm.setString(6, azureSpot.Items.get(i).getLocation());
							pstm.setString(7, azureSpot.Items.get(i).getEffectiveStartDate());
							pstm.setString(8, azureSpot.Items.get(i).getMeterId());
							pstm.setString(9, azureSpot.Items.get(i).getMeterName());
							pstm.setString(10, azureSpot.Items.get(i).getProductId());
							pstm.setString(11, azureSpot.Items.get(i).getSkuId());
							pstm.setString(12, azureSpot.Items.get(i).getProductName());
							pstm.setString(13, azureSpot.Items.get(i).getSkuName());
							pstm.setString(14, azureSpot.Items.get(i).getServiceName());
							pstm.setString(15, azureSpot.Items.get(i).getServiceId());
							pstm.setString(16, azureSpot.Items.get(i).getServiceFamily());
							pstm.setString(17, azureSpot.Items.get(i).getUnitOfMeasure());
							pstm.setString(18, azureSpot.Items.get(i).getType());
							pstm.setBoolean(19, azureSpot.Items.get(i).isPrimaryMeterRegion());
							pstm.setString(20, azureSpot.Items.get(i).getArmSkuName());
							

							pstm.execute();
						}

					}

					System.out.println(azureSpot.getNextPageLink());
					if (azureSpot.getCount() < 100) {
						proximo = false;
					} else {
						azureSpot = converter.converter(azureSpot.getNextPageLink());
					}

				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			conexao.desconectar();

		}
	
	
}
