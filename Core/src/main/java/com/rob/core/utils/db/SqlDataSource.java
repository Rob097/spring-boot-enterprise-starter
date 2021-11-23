package com.rob.core.utils.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rob.core.models.enums.PropertiesEnum;
import com.rob.core.utils.Properties;



@Configuration
public class SqlDataSource {

	private static final SqlDataSource instance = new SqlDataSource();
	private DataSource connectionPool;
	//private static Properties properties;
	private SqlConnection testConnection;

	public SqlDataSource() {
	}

	public static SqlDataSource getInstance() {
		return instance;
	}

	public DataSource getPool() {
		return connectionPool;
	}

	/** Stringa per ottenere la data odierna */
	private String getDbDateQuery() {
		return "SELECT NOW() AS DB_DATE";
	}

	/**
	 * Carica l'ora corrente dal database
	 * 
	 * @param cnn
	 * @return
	 * @throws SQLException
	 */
	public Calendar getDBdate(SqlConnection cnn) throws SQLException {
		if (cnn == null) {
			return Calendar.getInstance();
		}
		return getDBdate(cnn.getConnection());
	}

	/**
	 * Carica l'ora corrente dal database
	 * 
	 * @param cnn
	 * @return
	 * @throws SQLException
	 */
	public Calendar getDBdate(Connection cnn) throws SQLException {
		Calendar res = Calendar.getInstance();
		String sql = getDbDateQuery();
		if (StringUtils.isBlank(sql)) {
			return res;
		}

		try (PreparedStatementBuilder bld = new PreparedStatementBuilder();) {
			bld.append(sql);
			ResultSet rst = bld.executeQuery(cnn);
			rst.next();
			res.setTime(rst.getTimestamp("DB_DATE"));
		}

		return res;
	}

	public static void init() throws NamingException, SQLException {
		instance._init();
	}

	/**
	 * Istanzia datasource
	 * 
	 * @return
	 */
	@Bean
	public static DataSource getDataSource() {
		if (getInstance().getPool() != null) {
			return getInstance().getPool();
		}
		
		DataSource result = null;
		Properties properties = new Properties(PropertiesEnum.MAC_PROPERTIES.getName());

		/** Connessione tramite parametri core.properties */
		String jdbcUrl = properties.getProperty(PropertiesEnum.URL.getName());
		if (StringUtils.isNotBlank(jdbcUrl)) {
			com.zaxxer.hikari.HikariConfig config = new com.zaxxer.hikari.HikariConfig();
			config.setJdbcUrl(String.format(jdbcUrl));

			boolean useSSL = Boolean.parseBoolean(properties.getProperty(PropertiesEnum.SSL.getName()));
			config.addDataSourceProperty("useSSL", useSSL);

			String username = properties.getProperty(PropertiesEnum.USERNAME.getName());
			config.setUsername(username);

			String password = properties.getProperty(PropertiesEnum.PASSWORD.getName());
			config.setPassword(password);

			config.setMaximumPoolSize(15);

			result = new com.zaxxer.hikari.HikariDataSource(config);
		}
		if (result != null) {
			return result;
		}
		/**************************************************************/

		/** Connesione tramite JNDI */
		try {
			result = (DataSource) (new InitialContext());
		} catch (javax.naming.NamingException ex) {
			result = null;
		}
		if (result != null) {
			return result;
		}

		try {
			result = (DataSource) (new InitialContext());
		} catch (javax.naming.NamingException ex1) {
			result = null;
		}
		if (result != null) {
			return result;
		}
		/**************************************************************/

		return result;
	}

	/** Inizializzazione datasource */
	private void _init() throws NamingException, SQLException {
		Connection cnn = null;

		// Inizializzazione datasource
		connectionPool = getDataSource();

		try {
			// Recupera la connessione
			cnn = connectionPool.getConnection();

		} finally {
			// RILASCIO CONNESSIONE DATABASE
			if (cnn != null) {
				cnn.close();
			}
		}

	}

	/**
	 * Initialize per unit test
	 * 
	 * @param testConnection
	 */
	public void initTest(Connection testConnection) {
		try {
			this.testConnection = new SqlConnection(testConnection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void initTest(DataSource ds, int dbms) {
		this.connectionPool = ds;
	}

	public SqlConnection getTestConnection() {
		return this.testConnection;
	}

}
