package com.finops.spotprice.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.finops.spotprice.model.InstanceNormalAws;
import com.finops.spotprice.persistence.entity.InstanceNormalPrice;
import com.finops.spotprice.persistence.repository.InstanceNormalRepository;

@Component
@EnableScheduling
public class EnviarAwsNormal {

	private final long SEGUNDO = 1000;
	private final long MINUTO = SEGUNDO * 60;
	private final long HORA = MINUTO * 60;
	private final long DIA = HORA * 24;
	private final long SEMANA = DIA * 7;
	private final long MES = SEMANA * 4;

	@Autowired
	private InstanceNormalRepository instanceRepository;

	String data;

	// @Scheduled(fixedDelay = MES)
	public void enviar() {

		final String UrlPrincipal = "https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AmazonEC2/current/region_index.json";

		try {

			URL url = new URL(UrlPrincipal);
			URLConnection conn = url.openConnection();

			InputStream is = url.openStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String linha = br.readLine();

			while (linha != null) {

				String region = null;
				String urlRegion = null;

				String atributeValue = null;

				StringTokenizer st;

				if (linha.contains("regionCode")) {

					st = new StringTokenizer(linha);
					st.nextToken(":");
					atributeValue = st.nextToken(":");

					String regex = "\"([^\"]*)\""; // regex com um grupo entre aspas
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(atributeValue);

					if (matcher.find()) {
						atributeValue = matcher.group(1);
						region = atributeValue;
					}

					linha = br.readLine();

					st = new StringTokenizer(linha);
					st.nextToken(":");
					atributeValue = st.nextToken(":");

					regex = "\"([^\"]*)\""; // regex com um grupo entre aspas
					pattern = Pattern.compile(regex);
					matcher = pattern.matcher(atributeValue);

					if (matcher.find()) {
						atributeValue = matcher.group(1);
						urlRegion = atributeValue;

						percorrerRegioes(urlRegion, region);

					}

				}

				linha = br.readLine();

			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	protected void percorrerRegioes(String urlRegion, String region) {

		List<InstanceNormalAws> instanceList = new ArrayList<InstanceNormalAws>();

		String atributeValue = null;

		StringTokenizer st;

		try {

			URL url = new URL("https://pricing.us-east-1.amazonaws.com" + urlRegion);

			URLConnection conn = url.openConnection();

			InputStream is = url.openStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String linha = br.readLine();

			InstanceNormalAws instance = new InstanceNormalAws();

			boolean onDemand = false;

			System.out.println(region);

			// Percorre as linhas que especifica a REGIÃO, TIPO DE INSTÂNCIA E DATA DE
			// ATUALIZAÇÃO
			while (!onDemand) {

				String regex = "\"([^\"]*)\"";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher;

				instance.setRegion(region);
				instance.setCloudName("AWS");

				// instance Type
				if (linha.contains("sku")) {

					st = new StringTokenizer(linha);
					st.nextToken(":");
					atributeValue = st.nextToken(":");

					matcher = pattern.matcher(atributeValue);

					if (matcher.find()) {

						atributeValue = matcher.group(1);
						instance.setChaveInstance(atributeValue);

					}

				}

				// instance Type
				if (linha.contains("\"instanceType\"")) {

					st = new StringTokenizer(linha);
					st.nextToken(":");
					atributeValue = st.nextToken(":");

					matcher = pattern.matcher(atributeValue);

					if (matcher.find()) {
						atributeValue = matcher.group(1);
						instance.setInstanceType(atributeValue);

					}

				}

				// product description e adiciona na lista de instâncias
				if (linha.contains("operatingSystem")) {

					st = new StringTokenizer(linha);
					st.nextToken(":");
					atributeValue = st.nextToken(":");

					matcher = pattern.matcher(atributeValue);

					if (matcher.find()) {
						atributeValue = matcher.group(1);
						instance.setProductDescription(atributeValue);

					}

					InstanceNormalAws newInstance = new InstanceNormalAws();
					newInstance.setChaveInstance(instance.getChaveInstance());
					newInstance.setCloudName(instance.getCloudName());
					newInstance.setInstanceType(instance.getInstanceType());
					newInstance.setRegion(instance.getRegion());
					newInstance.setProductDescription(instance.getProductDescription());
					instanceList.add(newInstance);

				}

				linha = br.readLine();

				if (linha.contains("\"terms\"")) {

					onDemand = true;

				}

			}

			// Percorre as linhas que especifica o PREÇO.

			InstanceNormalAws instanceBuscada = null;

			boolean dadosAtualizados = false;

			while (linha != null) {

				String regex = "\"([^\"]*)\"";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher;

				String chaveInstance = null;
				String price = null;

				// Obtem a chave de referencia a instância na API
				if (linha.contains("sku")) {

					st = new StringTokenizer(linha);
					st.nextToken(":");
					atributeValue = st.nextToken(":");

					matcher = pattern.matcher(atributeValue);

					if (matcher.find()) {

						atributeValue = matcher.group(1);
						chaveInstance = atributeValue;

					}

					// Busca na lista de instância, qual instância pertence a chave de referência
					// encontrada.
					instanceBuscada = procurarInstance(instanceList, chaveInstance);

				}

				// Obtem a chave de referencia a instância na API
				if (linha.contains("effectiveDate")) {

					// Pega o ano e mes atual
					Date date = new Date(System.currentTimeMillis());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
					String anoMesAtual = sdf.format(date);

					// Obtem o ano atual e o ano anterior em INT
					int anoAtual = Integer.parseInt(anoMesAtual.substring(0, 4));
					int anoAnterior = anoAtual - 1;

					// Obtem o mes atual e mes anterior em INT
					int mesAtual = Integer.parseInt(anoMesAtual.substring(5, 7));
					int mesAnterior = mesAtual - 1;

					// Se o mes anterior for igual a zero, ele é referente ao mês 12
					if (mesAnterior == 0) {
						mesAnterior = 12;
					}

					// Leitura da linha do arquivo que informa a data de atualização
					data = linha.substring(29, 39);

					String anoMesAnterior;

					// Obtem a string que referencia o mês anterior
					if (mesAnterior >= 10) {
						anoMesAnterior = Integer.toString(anoAnterior) + "-" + Integer.toString(mesAnterior);
					} else {
						anoMesAnterior = Integer.toString(anoAnterior) + "-0" + Integer.toString(mesAnterior);
					}

					if (data.contains(anoMesAnterior) || data.contains(anoMesAtual)) {
						dadosAtualizados = true;
					}
				
				}

				// Se os dados estiverem atualizados, entra no if que obtem o preço e envia para o banco de dados
				if (dadosAtualizados) {

					// Obtem o preço da instância, e envia para o banco de dados
					if (linha.contains("\"USD\"")) {

						st = new StringTokenizer(linha);
						st.nextToken(":");
						atributeValue = st.nextToken(":");

						matcher = pattern.matcher(atributeValue);

						if (matcher.find()) {

							atributeValue = matcher.group(1);
							price = atributeValue;

						}

						// Insere o preço e a data na instância encontrada
						
						if(price.length() < 6) {
							instanceBuscada.setPrice(new BigDecimal(price.substring(0, 3)));
						}else {
							instanceBuscada.setPrice(new BigDecimal(price.substring(0, 6)));
						}
						
						instanceBuscada.setDataReq(data);

						enviarParaBanco(instanceBuscada);

						dadosAtualizados = false;

					}

				}

				linha = br.readLine();

			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected InstanceNormalAws procurarInstance(List<InstanceNormalAws> list, String chaveInstance) {

		InstanceNormalAws instance = new InstanceNormalAws();

		for (InstanceNormalAws instanceNormalAws : list) {

			if (instanceNormalAws.getChaveInstance().contains(chaveInstance)) {

				instance.setChaveInstance(instanceNormalAws.getChaveInstance());
				instance.setCloudName(instanceNormalAws.getCloudName());
				instance.setInstanceType(instanceNormalAws.getInstanceType());
				instance.setRegion(instanceNormalAws.getRegion());
				instance.setProductDescription(instanceNormalAws.getProductDescription());

			}

		}

//		System.out.println(chaveInstance + " " + instance.getChaveInstance());
		return instance;

	}

	protected void enviarParaBanco(InstanceNormalAws instance) {

		BigDecimal valorZerado = new BigDecimal("0.000000");

		// Verifica se o preço é diferente de 0
		if ((instance.getPrice().compareTo(valorZerado)) != 0 && instance.getInstanceType() != null
				&& instance.getProductDescription() != null) {

			// --------------------ENVIO PARA O BANCO DE DADOS ----------------
			InstanceNormalPrice instanceNormalPrice = null;

			instanceNormalPrice = selectInstancePrice("AWS", instance.getInstanceType(), instance.getRegion(),
					instance.getProductDescription());

			// Se o dado já estar no banco de dados, entra no IF
			if (instanceNormalPrice != null) {

				// Atualiza o dado atual do spotPrices com a nova data e preco
				updateInstancePrice(instanceNormalPrice, instance.getPrice(), data);

			} else {
				// Se o dado não existir, insere ele no banco de dados

				insertInstancePrice("AWS", instance.getInstanceType(), instance.getRegion(),
						instance.getProductDescription(), instance.getPrice(), data);

			}

			// --------------------FIM ENVIO PARA O BANCO DE DADOS ----------------
		}
	}

	// ------------ METODOS DE SQL --------

	protected InstanceNormalPrice selectInstancePrice(String cloudName, String instanceType, String region,
			String productDescription) {

		InstanceNormalPrice instance = instanceRepository
				.findBySelectUsingcloudNameAndinstanceTypeAndregionAndProductDescription("AWS", instanceType, region,
						productDescription);

		return instance;

	}

	protected boolean updateInstancePrice(InstanceNormalPrice instanceNormalPrice, BigDecimal unitPrice, String data) {

		boolean salvoSucesso = false;

		instanceNormalPrice.setPrice(unitPrice);
		instanceNormalPrice.setDataReq(data);

		InstanceNormalPrice instanceSave = instanceRepository.save(instanceNormalPrice);

		if (instanceSave != null) {
			salvoSucesso = true;
		}

		return salvoSucesso;
	}

	protected boolean insertInstancePrice(String cloudName, String instanceType, String region,
			String productDescription, BigDecimal unitPrice, String dataSpotFormatada) {

		boolean salvoSucesso = false;

		InstanceNormalPrice instanceNormal = new InstanceNormalPrice();
		instanceNormal.setCloudName("AWS");
		instanceNormal.setInstanceType(instanceType);
		instanceNormal.setRegion(region);
		instanceNormal.setProductDescription(productDescription);
		instanceNormal.setPrice(unitPrice);
		instanceNormal.setDataReq(dataSpotFormatada);

		InstanceNormalPrice instanceSave = instanceRepository.save(instanceNormal);

		if (instanceSave != null) {
			salvoSucesso = true;
		}

		return salvoSucesso;

	}

}
