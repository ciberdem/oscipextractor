package br.uniriotec.oscip.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import br.uniriotec.oscip.config.Config;

public class MySQLConn {
	
	private static final Logger LOGGER = Logger.getLogger(MySQLConn.class.getName());
	
	public static Connection getConnection() {
		
		String host = Config.DB_HOST;
		String port = Config.DB_PORT;
		String dbname = Config.DB_NAME;
		String user = Config.DB_USER;
		String password = Config.DB_PASS;

		LOGGER.info("Connecting database...");
		Connection connection = null;
		
		try {
			
			String dbPath = String.format(
					"jdbc:mysql://%s:%s/%s?user=%s&password=%s&characterEncoding=utf-8&" + 
					"useUnicode=true", host, port, dbname, user, password);
			
			connection = DriverManager.getConnection(dbPath);
			
			LOGGER.info("Conexão ao banco estabelecida");
		} catch (SQLException e) {
			LOGGER.error("Erro ao conectar ao banco de dados " + e.getMessage());
		}
		
		return connection;
	}
	
	public static void main(String[] args) {
		

	}
	
	
}
