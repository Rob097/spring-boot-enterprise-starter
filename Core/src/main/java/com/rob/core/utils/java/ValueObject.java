package com.rob.core.utils.java;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


/**
 * @author Roberto97
 * Class extended by all the BE models (not DTO).
 */
public class ValueObject {
	private transient SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private transient SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("dd/MM/yyyy");

	private transient SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

	/** Formato per le date DD/MM/YYYY */
	public static final String DATETIME_FORMAT = "dd/MM/yyyy";

	
	
	/**
   * Formatta un oggetto Calendar in Stringa "dd/MM/yyyy HH:mm:ss".
   * 
   * @param in Il Calendar da formattare
   * @return La rappresentazione in formato "dd/MM/yyyy HH:mm:ss"
   */
	protected String dateTimeToString(Calendar in) {
		if (in == null) {
			return null;
		}
		return dateTimeFormatter.format(in.getTime());
	}

	/**
   * Trasforma una Stringa di formato "dd/MM/yyyy HH:mm:ss" in Calendar.
   * 
   * @param in La stringa da trasormare
   * @return null se la stringa in input e' null, altrimenti il Calendar
   *         corrispondente
   * @throws ParseException se la stringa in input non contiene un formato
   *           corretto. E' buona prassi lasciar uscire questa eccezione per
   *           avvertire il chiamante del problema di "formato".
   */
	protected Calendar stringToDateTime(String in) throws ParseException {
		if (in == null || in.length() == 0) {
			return null;
		}
		Calendar ret = GregorianCalendar.getInstance();
		ret.setTime(dateTimeFormatter.parse(in));
		return ret;
	}

	/**
   * Formatta un oggetto Calendar in Stringa "dd/MM/yyyy".
   * 
   * @param in Il Calendar da formattare
   * @return La rappresentazione in formato "dd/MM/yyyy"
   */
	protected String simpleDateToString(Calendar in) {
		if (in == null) {
			return null;
		}
		return simpleDateFormatter.format(in.getTime());
	}

	/**
   * Trasforma una Stringa di formato "dd/MM/yyyy" in Calendar e imposta l'ora a
   * 00:00:00. Viene tentata una seconda conversione per verificare se il dato è
   * in formato "yyyyMMdd" (nel caso di provenienza dei dati da SISS)
   * 
   * @param in La stringa da trasormare
   * @return null se la stringa in input e' null, altrimenti il Calendar
   *         corrispondente
   * @throws ParseException se la stringa in input non contiene un formato
   *           corretto. E' buona prassi lasciar uscire questa eccezione per
   *           avvertire il chiamante del problema di "formato".
   */
	protected static Calendar stringToSimpleDate(String in) throws ParseException {
		return Commons.stringToSimpleDate(in);
	}

	/**
   * Formatta un oggetto Calendar in Stringa "HH:mm:ss".
   * 
   * @param in Il Calendar da formattare
   * @return La rappresentazione in formato "HH:mm:ss"
   */
	protected String timeToString(Calendar in) {
		if (in == null) {
			return null;
		}
		return timeFormatter.format(in.getTime());
	}

	/**
   * Trasforma una Stringa di formato "HH:mm:ss" in Calendar e imposta la data a
   * 1/1/1900.
   * 
   * @param in La stringa da trasormare
   * @return null se la stringa in input e' null, altrimenti il Calendar
   *         corrispondente
   * @throws ParseException se la stringa in input non contiene un formato
   *           corretto. E' buona prassi lasciar uscire questa eccezione per
   *           avvertire il chiamante del problema di "formato".
   */
	protected Calendar stringToTime(String in) throws ParseException {
		if (in == null || in.length() == 0) {
			return null;
		}
		Calendar ret = GregorianCalendar.getInstance();
		ret.setTime(timeFormatter.parse(in));
		resetDate(ret);
		return ret;
	}

	/**
   * Azzera i campi ora, minuti, secondi di un Calendar impostandoli a 00:00:00.
   * 
   * @param in Il Calendar da azzerare
   */
	protected static void resetTime(Calendar in) {
		Commons.resetTime(in);
	}

	/**
   * Azzera i campi giorno, mese, anno di un Calendar impostandoli a 1/1/1900.
   * 
   * @param in Il Calendar da azzerare
   */
	protected static void resetDate(Calendar in) {
		Commons.resetDate(in);
	}

	/**
   * Trasforma una oggetto di tipo java.util.Date in Calendar.
   * 
   * @param in L'oggetto da trasormare
   * @return null se l'oggetto in input e' null, altrimenti il Calendar
   *         corrispondente
   */
	protected Calendar dateToCalendar(Date in) {
		return Commons.dateToCalendar(in);
	}

	/**
   * Trasforma una stringa in un booleano restituisce true nel caso la stringa
   * contenga "YES", "SI", "TRUE", numerico diverso da 0 viene ignorato il case
   * durante il confronto
   * 
   * @param in La stringa da trasformare
   * @return true/false
   */
	protected boolean stringToBoolean(String in) {
		
		// se la stringa e' null
		if (in == null) {
			return false;
		}
		
		// comparazione da stringa a stringa
		if (in.equalsIgnoreCase("true") || in.equalsIgnoreCase("si") || in.equalsIgnoreCase("yes")) {
			return true;
		}
		
		// comparazione numerico <> da zero
		try {
			if (Long.parseLong(in) != 0) {
				return true;
			}
		} catch (NumberFormatException ex) {
		}

		return false;
	}
	
	/**
	 * static helper per convertire un valueobjectlist in JSON
	 * @param c 
	 * @param toconvert 
	 * @return 
	 */
	@SuppressWarnings("rawtypes")
	static public JSONObject toJSON(Class c, Object toconvert){
		Field[] props = c.getDeclaredFields();
		JSONObject ret = new JSONObject();		
		try {
			for( int i = 0; i<props.length ; i++){
				props[i].setAccessible(true);
				try {
					ret.put(props[i].getName(), BeanUtils.getProperty(c.cast(toconvert), props[i].getName()));
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				} catch (NoSuchMethodException e) {
				} catch (JSONException e) {
				}		
				props[i].setAccessible(false);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

}
