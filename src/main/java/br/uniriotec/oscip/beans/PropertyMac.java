package br.uniriotec.oscip.beans;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyMac {

	Properties props;

	public PropertyMac() throws IOException {

		this.props = new Properties();
		FileInputStream file = new FileInputStream("./tmpfiles/configMac.properties");
		this.props.load(file);
	}

	public String getValor(String parametro) throws IOException {

		return this.props.getProperty(parametro);

	}

}
