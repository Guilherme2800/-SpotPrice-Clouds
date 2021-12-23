package com.finops.spotprice.component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.finops.spotprice.persistence.entity.PriceHistorySpot;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.PriceHistoryRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;
import com.finops.spotprice.util.ReceberJson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
@EnableScheduling
public class EnviarGoogleSpot {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;
	private final long DIA = HORA * 24;
	private final long SEMANA = DIA * 7;

	final String URL = "https://cloudpricingcalculator.appspot.com/static/data/pricelist.json?v=1638364907294";
	
	@Autowired
	private SpotRepository spotRepository;

	@Autowired
	private PriceHistoryRepository priceHistoryRepository;

	// @Scheduled(fixedDelay = SEMANA)
	public boolean enviar() {
		
		boolean enviado = false;

		// Pega a data atual
		Date data = new Date(System.currentTimeMillis());

		// Formata para a data.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		SpotPrices spotPrices;

		ReceberJson json = new ReceberJson();

		System.out.println("\nEnviando GOOGLE");

		try {

			// Recebe o JSON da URL
			JsonObject jsonObject = json.requisitarJson(URL);

			// Pega o objeto JSON que contém as instancias
			JsonObject listInstancias = (JsonObject) jsonObject.get("gcp_price_list");

			//Realiza o mapeamento chave valor dos tipos de instâncias para seus valores em todas regiões
			Map<String, Object> attributes = new HashMap<String, Object>();
			Set<Entry<String, JsonElement>> entrySet = listInstancias.entrySet();
			for (Map.Entry<String, JsonElement> entry : entrySet) {
				attributes.put(entry.getKey(), listInstancias.get(entry.getKey()));
			}

			// Percorre as posições chave/valor
			for (Map.Entry<String, Object> instancia : attributes.entrySet()) {
				if (instancia.getKey().contains("PREEMPTIBLE")) {

					// -----Descrição do produto-----
					String productDescription = instancia.getKey().toLowerCase().substring(0, 16);
					if (instancia.getKey().contains("VMIMAGE")) {
						productDescription = productDescription.toLowerCase() + "-vmimage";
					}
					
					// -----Tipo da instância-----
					String instanceType = instancia.getKey().replaceAll("CP-COMPUTEENGINE-", "")
							.replaceAll("VMIMAGE-", "").replaceAll("-PREEMPTIBLE", "").replaceAll("-", " ")
							.toLowerCase();

					// Divide a string de precos em pequenas partes, cada parte contem a região e o preço
					StringTokenizer st = new StringTokenizer(instancia.getValue().toString() + "\n\n");

					boolean continuar;
					do {
						continuar = true;
						String region = null;
						BigDecimal preco;
						String linhaAtual = st.nextToken(",");

						if (linhaAtual.contains("}")) {
							continuar = false;
						}
						
						linhaAtual = linhaAtual.replaceAll("}", "");

						String regex = "\"([^\"]*)\""; // regex com um grupo entre aspas
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(linhaAtual);

						if (matcher.find()) {
							// Pega o conteúdo entre aspas a partir de uma string

							// ------- instace REGION------
							region = matcher.group(1);

							// Verifica se o nome que ele pegou realmente é uma região
							if (region.contains("central") || region.contains("east") || region.contains("west")
									|| region.contains("north") || region.contains("south") || region.contains("northeast") || region.contains("southeast")) {

								// ------Instance PRICE-----
								int indice = linhaAtual.indexOf(":");

								String precoFormatado = linhaAtual.substring(indice + 1, linhaAtual.length());

								if (precoFormatado.length() >= 7) {
									precoFormatado = precoFormatado.substring(0, 7);
								}

								preco = new BigDecimal(Double.parseDouble(precoFormatado)).setScale(7,BigDecimal.ROUND_HALF_UP);
								// ------ FIM Instance PRICE-----

								BigDecimal valorZerado = new BigDecimal("0.000000");

								// Verifica se o preço é diferente de 0
								if ((preco.compareTo(valorZerado)) != 0) {

									// --------------------ENVIO PARA O BANCO DE DADOS ----------------
									spotPrices = null;
									spotPrices = selectSpotPrices("GOOGLE", instanceType, region, productDescription);

									// Se o dado já estar no banco de dados, entra no IF
									if (spotPrices != null) {

										PriceHistorySpot priceHistory = selectPriceHistory(spotPrices);

										// Se o dado não estiver já em priceHistory, entra no IF
										if (priceHistory == null) {
											// Insere o dado atual na tabela de historico
											insertPriceHistory(spotPrices);

											// Atualiza o dado atual do spotPrices com a nova data e preco
											updateSpotPrice(spotPrices, preco, sdf.format(data));
										}
									} else {
										// Se o dado não existir, insere ele no banco de dados

										insertSpotPrice("GOOGLE", instanceType, region, productDescription, preco,
												sdf.format(data));

									}

									// --------------------FIM ENVIO PARA O BANCO DE DADOS ----------------
								}

							}

						}
					} while (continuar);

				}

			}

			enviado = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Terminou GOOGLE");
		return enviado;

	}

	protected SpotPrices selectSpotPrices(String cloudName, String instanceType, String region,
			String productDescription) {

		return spotRepository.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription("GOOGLE",
				instanceType, region, productDescription);
	}

	protected PriceHistorySpot selectPriceHistory(SpotPrices spotPrices) {

		return priceHistoryRepository.findBySelectUsingcodSpotAndpriceAnddataReq(spotPrices.getCod_spot(),
				spotPrices.getPrice().doubleValue(), spotPrices.getDataReq());

	}

	protected boolean insertPriceHistory(SpotPrices spotPrices) {

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

	protected boolean updateSpotPrice(SpotPrices spotPrices, BigDecimal unitPrice, String dataFormatada) {
		
		boolean salvoSucesso = false;
		
		spotPrices.setPrice(unitPrice);
		spotPrices.setDataReq(dataFormatada);

		SpotPrices spotSalva = spotRepository.save(spotPrices);
		
		if (spotSalva != null) {
			salvoSucesso = true;
		}

		return salvoSucesso;
		
	}
	
	protected boolean insertSpotPrice(String cloudName, String instanceType, String region, String productDescription,
			BigDecimal unitPrice, String dataFormatada) {

		boolean salvoSucesso = false;
		
		SpotPrices newSpotPrice = new SpotPrices();
		newSpotPrice.setCloudName("GOOGLE");
		newSpotPrice.setInstanceType(instanceType);
		newSpotPrice.setRegion(region);
		newSpotPrice.setProductDescription(productDescription);
		newSpotPrice.setPrice(unitPrice);
		newSpotPrice.setDataReq(dataFormatada);

		SpotPrices spotSalva = spotRepository.save(newSpotPrice);

		if (spotSalva != null) {
			salvoSucesso = true;
		}

		return salvoSucesso;
		
	}

}
