package br.com.cea.batimentoVendas.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {

	private Properties props;
	private String nomeDoProperties = "./batimento.vendas.properties";

	protected PropertiesLoader() {
		props = new Properties();

		try {
			FileInputStream in = new FileInputStream(nomeDoProperties);
			props.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String getValor(String chave) {
		return (String) props.getProperty(chave);
	}

}
