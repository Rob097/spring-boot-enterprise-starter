package com.rob.core.utils.db;

import java.util.Calendar;
import java.util.Date;

/**
 * Classe utilizzata per trasportare le informazioni di una bind variable per la costruzione di prepared statement
 */
public final class BindVariableInfo  {
	
	@SuppressWarnings("rawtypes")
	public enum BindVariableTypeEnum {

		STRING(String.class)
		, CLOB(String.class)
		, INTEGER(Integer.class)
		, LONG(Long.class)
		, BOOLEAN(Boolean.class)
		, CALENDAR(Calendar.class)
		, DATE(Date.class)
		, DOUBLE(Double.class)
		, FLOAT(Float.class)
		;

		private Class cls;

		/** Costruttore publico dell'enumerato */
		BindVariableTypeEnum(Class cls) {
			this.cls = cls;
			
		}

		/**
		 * Restituisce la classe che corrisponde al tipo enumerato
		 * @return 
		 */
		public Class getParameterClass() {
			return this.cls;
		}

	}

	private BindVariableTypeEnum typeEnum = null;
	private Object value = null;
	private String paramName;
	private int paramPos;
	private boolean input = true;
	
	/*
   * Costruttore classe
   */
	public BindVariableInfo(String paramName, BindVariableTypeEnum typeEnum, Object value, boolean input) {
		super();
		this.paramName = paramName;
		this.typeEnum = typeEnum;
		this.value = value;
		this.input = input;
	}
	
	/*
   * Costruttore classe
   */
	public BindVariableInfo(String paramName, BindVariableTypeEnum typeEnum, Object value) {
		this(paramName, typeEnum, value, true);
	}

	/** Tipo di parametro 
	 * @return */
	public BindVariableTypeEnum getTypeEnum() {
		return typeEnum;
	}

	/** Valore del parametro 
	 * @return */
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	/** Nome (descrittivo) del parametro, usato per consultazione e stampa dei valori 
	 * @return */
	public String getParamName() {
		return paramName;
	}

	/** TRUE:Parametro di input  FALSE:Parametro di output
	 * @return */
	public boolean isInput() {
		return input;
	}

	public int getParamPos() {
		return paramPos;
	}
	public void setParamPos(int paramPos) {
		this.paramPos = paramPos;
	}


}

