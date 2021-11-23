package com.rob.core.utils.db;

import java.security.Key;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;

import com.rob.core.models.enums.PropertiesEnum;
import com.rob.core.utils.Properties;


public class DataEncryption {
private final Logger log = LoggerFactory.getLogger(getClass());
	
	private Properties properties = new Properties(PropertiesEnum.MAIN_PROPERTIES.getName());
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final String IV_DEFAULT_VALUE = "00000000000000000000000000000000";
	private static final String ALGORITHM = "AES";
	private static final String COLUMN_SUFFIX = "_CR";

	private static DataEncryption dataEncryption;
	
	private Cipher encrypter = null;
	
	private DataEncryption() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DataEncryption getInstance() {
		if (dataEncryption == null) {
			dataEncryption = new DataEncryption();
		}
		return dataEncryption;
	}
	
	public boolean isEnabled() {
		return Boolean.parseBoolean(properties.getProperty(PropertiesEnum.ENCRYPT_ENABLED.getName()));
	}
	
	public static String getColumnName(String columnName) {
		if (StringUtils.isNotEmpty(columnName)) {
			return columnName + COLUMN_SUFFIX;
		}else{
			return columnName;
		}
	}
	
	public String getEncryptedValue(String columnValue) {
		if (isEnabled() && StringUtils.isNotEmpty(columnValue)) {
			return encryptToHexadecimalString(columnValue);
		}else{
			return columnValue;
		}
	}
	
	public CallableStatement setCryptContext(Connection connection)  {
		CallableStatement callableStatement = null;
		if (connection != null) {
			try {
				String cryptFun = properties.getProperty(PropertiesEnum.ENCRYPT_ALGORITHM.getName());
				String statement = "";
				if(StringUtils.isNotEmpty(cryptFun)) {
					statement = "{call "+cryptFun+"(?,?)}";
				}else {
					statement = "{call set_crypt_context(?,?)}";
				}
				callableStatement = connection.prepareCall(statement);
				callableStatement.setString(1, properties.getProperty(PropertiesEnum.ENCRYPT_PASSWORD.getName()));
				callableStatement.setString(2, properties.getProperty(PropertiesEnum.ENCRYPT_SALT.getName()));
				
			} catch (SQLException e) {
				log.error("Errore apertura contesto di criptatura.",e);
			}

		}
		return callableStatement;
	}

	private void init() throws Exception {
		if (isEnabled()) {
			Key secretKey = null;
			IvParameterSpec algorithmParameters = null;
			encrypter = Cipher.getInstance(TRANSFORMATION);
				secretKey = new SecretKeySpec(properties.getProperty(PropertiesEnum.ENCRYPT_SALT.getName()).getBytes(), ALGORITHM);
				algorithmParameters = new IvParameterSpec(Hex.decode(IV_DEFAULT_VALUE));
			encrypter.init(Cipher.ENCRYPT_MODE, secretKey, algorithmParameters);
		}
	}

	private String encryptToHexadecimalString(String textToEncrypt) {
		String result = null;
		try {
			byte[] encryptedText = encrypter.doFinal(padString(textToEncrypt).getBytes());
			result = new String(Hex.encode(encryptedText)).toUpperCase();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String padString(String sInput) {
		String result = sInput;
		return result;
	}
	
}
