package com.finops.spotprice.component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import com.finops.spotprice.persistence.entity.InstanceNormalPrice;
import com.finops.spotprice.persistence.entity.SpotPrices;
import com.finops.spotprice.persistence.repository.InstanceNormalRepository;
import com.finops.spotprice.persistence.repository.SpotRepository;
import com.finops.spotprice.util.ReceberJson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
@EnableScheduling
public class EnviarGoogleNormal {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;
	private final long DIA = HORA * 24;
	private final long SEMANA = DIA * 7;

	final String URL = "https://cloudpricingcalculator.appspot.com/static/data/pricelist.json?v=1638364907294";

	@Autowired
	private SpotRepository spotRepository;

	@Autowired
	private InstanceNormalRepository instanceRepository;

	// @Scheduled(fixedDelay = SEMANA)
	public void enviar() {

		// Pega a data atual
		Date data = new Date(System.currentTimeMillis());

		// Formata para a data.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		InstanceNormalPrice instanceNormal;

		ReceberJson json = new ReceberJson();

		System.out.println("\nEnviando GOOGLE sob demanda");

		try {

			// Recebe o JSON da URL
			JsonObject jsonObject = json.requisitarJson(URL);

			// Pega o objeto JSON que contém as instancias
			JsonObject listInstancias = (JsonObject) jsonObject.get("gcp_price_list");

			// Realiza o mapeamento chave valor dos tipos de instâncias para seus valores em
			// todas regiões
			Map<String, Object> attributes = new HashMap<String, Object>();
			Set<Entry<String, JsonElement>> entrySet = listInstancias.entrySet();
			for (Map.Entry<String, JsonElement> entry : entrySet) {
				attributes.put(entry.getKey(), listInstancias.get(entry.getKey()));
			}

			// Percorre as posições chave/valor
			for (Map.Entry<String, Object> instancia : attributes.entrySet()) {
				if (!instancia.getKey().contains("PREEMPTIBLE") && instancia.getKey().contains("CP-COMPUTEENGINE-") && !instancia.getKey().contains("CP-COMPUTEENGINE-OS")) {

					// -----Descrição do produto-----
					String productDescription = instancia.getKey().toLowerCase().substring(0, 16);
					if (instancia.getKey().contains("VMIMAGE")) {
						productDescription = productDescription.toLowerCase() + "-vmimage";
					}

					// -----Tipo da instância-----
					String instanceType = instancia.getKey().replaceAll("CP-COMPUTEENGINE-", "")
							.replaceAll("VMIMAGE-", "").replaceAll("-PREEMPTIBLE", "").replaceAll("-", " ")
							.toLowerCase();

					// Divide a string de precos em pequenas partes, cada parte contem a região e o
					// preço
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

								preco = new BigDecimal(Double.parseDouble(precoFormatado)).setScale(7,
										BigDecimal.ROUND_HALF_UP);
								// ------ FIM Instance PRICE-----

								BigDecimal valorZerado = new BigDecimal("0.000000");

								// Verifica se o preço é diferente de 0
								if ((preco.compareTo(valorZerado)) != 0) {

									// --------------------ENVIO PARA O BANCO DE DADOS ----------------
									instanceNormal = null;
									instanceNormal = selectInstancePrice("GOOGLE", instanceType, region,
											productDescription);

									// Se o dado já estar no banco de dados, entra no IF
									if (instanceNormal != null) {

										// Atualiza o dado atual do spotPrices com a nova data e preco
										updateInstancePrice(instanceNormal, preco, sdf.format(data));

									} else {
										// Se o dado não existir, insere ele no banco de dados

										List<SpotPrices> spotPrices = spotRepository
												.findBySelectUsingcloudNameAndinstanceTypeAndregion("GOOGLE",
														instanceType, region);

										if (spotPrices != null) {
											insertInstancePrice("GOOGLE", instanceType, region, productDescription,
													preco, sdf.format(data));
										}

									}

									// --------------------FIM ENVIO PARA O BANCO DE DADOS ----------------
								}

							}

						}
					} while (continuar);

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Terminou GOOGLE");

	}

	protected InstanceNormalPrice selectInstancePrice(String cloudName, String instanceType, String region,
			String productDescription) {

		return instanceRepository.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription("GOOGLE",
				instanceType, region, productDescription);
	}

	protected void updateInstancePrice(InstanceNormalPrice instanceNormal, BigDecimal unitPrice,
			String dataSpotFormatada) {
		instanceNormal.setPrice(unitPrice);
		instanceNormal.setDataReq(dataSpotFormatada);

		instanceRepository.save(instanceNormal);
	}

	protected void insertInstancePrice(String cloudName, String instanceType, String region, String productDescription,
			BigDecimal unitPrice, String dataSpotFormatada) {

		InstanceNormalPrice instanceNormal = new InstanceNormalPrice();
		instanceNormal.setCloudName("GOOGLE");
		instanceNormal.setInstanceType(instanceType);
		instanceNormal.setRegion(region);
		instanceNormal.setProductDescription(productDescription);
		instanceNormal.setPrice(unitPrice);
		instanceNormal.setDataReq(dataSpotFormatada);

		instanceRepository.save(instanceNormal);

	}

}
