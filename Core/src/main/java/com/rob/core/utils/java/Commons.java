package com.rob.core.utils.java;

import static java.lang.Math.abs;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;

import com.rob.core.utils.db.SortEnum;


/**
 * Libreria di funzioni di utilita' generale (fra cui conversione e formattazione
 * di date e di numeri, etc.).
 */
@SuppressWarnings("javadoc")
public class Commons {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Commons.class);
	
	/** Minima data consentita */
	public static final String SYS_DATAMIN = "01/01/1900";

	/** Massima data consentita */
	public static final String SYS_DATAMAX = "31/12/2999";

	/** Minima data/ora consentita */
	public static final String SYS_LONGDATAMIN = "01/01/1900 00:00:00";

	/** Massima data/ora consentita */
	public static final String SYS_LONGDATAMAX = "31/12/2999 23:59:59";

	/** Formato data standard (dd/MM/yyyy) */
	public static final String FORMAT_DATE = "dd/MM/yyyy";

	/** Formato data breve (dd/MM/yy) */
	public static final String FORMAT_SIMPLE_DATE = "dd/MM/yy";
	
	
	/** Formato data breve (dd-MM-yy) */
	public static final String FORMAT_SIMPLE_DATE_2 = "dd-MM-yy";
	/** Formato data breve (dd-MM-yyyy) */
	public static final String FORMAT_SIMPLE_DATE_3 = "dd-MM-yyyy";

	/** Formato ora standard (HH:mm:ss) */
	public static final String FORMAT_TIME = "HH:mm:ss";

	/** Formato ora breve (HH:mm) */
	public static final String FORMAT_SIMPLE_TIME = "HH:mm";

	/** Formato data/ora standard (dd/MM/yyyy HH:mm:ss) */
	public static final String FORMAT_DATETIME = FORMAT_DATE + " " + FORMAT_TIME;

	/** Formato data/ora breve (dd/MM/yyyy HH:mm) */
	public static final String FORMAT_SIMPLE_DATETIME = FORMAT_DATE + " " + FORMAT_SIMPLE_TIME;

	/** Formato data standard HL7 (yyyyMMdd) */
	public static final String FORMAT_DATE_HL7 = "yyyyMMdd";

	/** Formato data/ora standard HL7 (yyyyMMddHHmm) */
	public static final String FORMAT_DATETIME_HL7 = "yyyyMMddHHmm";
	
	/** Formato data (yyyy-MM-dd) */
	public static final String FORMAT_DATE_MINUS_SEPARATOR = "yyyy-MM-dd";
	
	/** Formato data (yyyy-MM-dd HH:mm:ss) */
	public static final String FORMAT_DATE_TIME_MINUS_SEPARATOR = FORMAT_DATE_MINUS_SEPARATOR + " " + FORMAT_TIME;
	
	/** Formato data/ora standard HL7 (yyyyMMddHHmmss) */
	public static final String FORMAT_FULLDATETIME_HL7 = "yyyyMMddHHmmss";

	/** Formato data/ora standard HL7 (ddMMyyyyHHmmss) */
	public static final String FORMAT_FULLDATETIME = "ddMMyyyyHHmmss";
	
	public static final String FORMAT_FULLDATETIME_DEMA = "yyyyMMdd HHmmss";
	
	/** Formato anno 4 cifre */
	public static final String FORMAT_YEAR = "yyyy";

	/** Charset (as string) attualmente utilizzato nell'applicazione*/
	public static final String CURRENT_ENCODING = "ISO-8859-1";
	
	/** Charset attualmente utilizzato nell'applicazione */
	public static final Charset CURRENT_CHARSET = Charset.forName(CURRENT_ENCODING);
	
	/** Charset UTF-8 */
	public static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");
	
	/** Unita' di misura per il risultato della funzione dateDiff */
	public enum DateDiffUnit {
		SECOND, MINUTE, HOUR, DAY, FULLDAY
	}
	
	public static final String JSON_EMPTY = "{}";
	
	public static final String[] TRUE_VALUES = new String[] {"TRUE","1","SI","S","YES","Y"};
	
	
	/** 
	 * Questo metodo si occupa di verificare una stringa sia numerica o meno
	 * @param value
	 * @return True nel cso in cui stringa numerica, False altrimenti.
	 */
	public static boolean isNumeric(String value){
		boolean isNumeric = false;
		
		String regex = "^[0-9]+$";
		Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        
        if(matcher.find()){
        	isNumeric = true;
        }

    	return isNumeric;
	}

	/**
	 * Metodo che restituisce la data odierna secondo il formato specificato nel
	 * parametro passato.
	 * 
	 * @param formatDate
	 *          Formato della data.
	 * @return Data odierna formattata sencondo il formato specificato.
	 */
	public static String now(String formatDate) {

		Date today = new Date();
		DateFormat formatter = new SimpleDateFormat(formatDate);

		return formatter.format(today).toString();
	}
	

	/** Add to specify date the specify number of day
	 * @param date -> date
	 * @param numberOfDay -> number of day to add
	 * @return correct date
	 */
	public static Date addDay(Date date, int numberOfDay)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, numberOfDay);
		return c.getTime();
	}
	
	public static Date subtractDay(Date date, int numberOfDay)
	{
		if (numberOfDay == 0) {
			return date;
		}
		if (numberOfDay > 0) {
			numberOfDay = -numberOfDay;
		}
		return addDay(date, numberOfDay);
	}

	/**
	 * Restituisce una stringa contenente la data nel formato esteso (es. 01
	 * Gennaio 2005)
	 * 
	 * @param in
	 *          Il Calendar da convertire
	 * @return La data convertita
	 */
	public static String calendarToVerboseString(Calendar in) {

		if (in == null) {
			return "";
		}

		String[] month = new String[] { "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre" };

		return in.get(Calendar.DAY_OF_MONTH) + " " + month[in.get(Calendar.MONTH)] + " " + in.get(Calendar.YEAR);
	}

	/**
	 * Formatta un double per la corretta visualizzazione di valuta Euro
	 * 
	 * @param amount
	 *          importo da formattare
	 * @return Stringa formattata per la visualizzazione in euro
	 */
	public static String formatCurrency(double amount) {

		int decimalPos = 2;
		double absAmount = Math.abs(amount);
		NumberFormat nfc = NumberFormat.getInstance(Locale.ITALIAN);

		// analisi decimali
		if (absAmount < (10 / 1936.27)) {

			// da 0 a 9 Lire 5 decimali
			decimalPos = 5;
		} else if (absAmount < (100 / 1936.27)) {

			// da 10 99 Lire 4 decimali
			decimalPos = 4;
		} else if (absAmount < (1000 / 1936.27)) {

			// da 100 a 999 Lire 3 decimali
			decimalPos = 3;
			// settaggio range minimo e massimo dei decimali
		}

		nfc.setMinimumFractionDigits(2);
		nfc.setMaximumFractionDigits(decimalPos);
		nfc.setGroupingUsed(false);

		return nfc.format(amount);
	}

	
	/**Il risultato avr� la data della variabile input "dt" e l'orario della variabile input "time"
	 * 
	 * @param dt
	 * @param time
	 * @return
	 */
	public static Calendar setTime(Calendar dt, Calendar time) {
		if (dt==null || time==null) {
			return null;
		}
	
		dt.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		dt.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		dt.set(Calendar.SECOND, time.get(Calendar.SECOND));
		dt.set(Calendar.MILLISECOND, time.get(Calendar.MILLISECOND));
		
		return dt;
	}

	
	/**Restituisce l'orario preso da un oggetto Date sfruttando l'oggetto Calendar
	 * Utilizzare per sostituire il deprecato "date.getHours()"
	 * @param date
	 * @return
	 */
	public static int getHours(Date date) {
		if (date==null) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);  
		return cal.get(Calendar.HOUR_OF_DAY);
	}
		
	/**Modifica i minuti in un oggetto date e restituisce nuova istanza di oggetto
	 * Utilizzare per sostituire il deprecato "date.setMinutes(int)"
	 * 
	 * @param date
	 */
	public static Date setHours(Date date, int hours) {
		if (date==null) {
			return date;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);  
			cal.set(Calendar.HOUR_OF_DAY, hours);
			return cal.getTime();
		}
	}
	
	
	/**Restituisce i minuti presi da un oggetto Date sfruttando l'oggetto Calendar
	 * Utilizzare per sostituire il deprecato "date.getMinutes()"
	 * @param date
	 * @return
	 */
	public static int getMinutes(Date date) {
		if (date==null) {
			return 0;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);  
			return cal.get(Calendar.MINUTE);
		}
	}
	
	/**Modifica i minuti in un oggetto date e restituisce nuova istanza di oggetto
	 * Utilizzare per sostituire il deprecato "date.setMinutes(int)"
	 * 
	 * @param date
	 */
	public static Date setMinutes(Date date, int minutes) {
		if (date==null) {
			return date;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);  
			cal.set(Calendar.MINUTE, minutes);
			return cal.getTime();
		}
	}

	/**
	 * Imposta il massimo valore per i campi ora, minuti, secondi di un Date.
	 * 
	 * @param in
	 *          Il <code>Date</code> da modificare
	 */
	public static Date setMaxTime(Date in) {
		if (in != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(in);
			dayEnd(calendar);
			in.setTime(calendar.getTimeInMillis());
		}
		return in;
	}

	/**
	 * Il metodo effettua un arrotondamento di legge ad un importo double con +
	 * decimali.
	 * 
	 * @param amount
	 *          Importo da arrotondare.
	 * @return Importo arrotondato secondo i termini di legge.
	 */
	public static double round(double amount) {

		double absAmount = Math.abs(amount);
		BigDecimal bd = new BigDecimal(String.valueOf(amount));

		// default 2 posizioni decimali
		int decimalPos = 2;

		// analisi decimali
		if (absAmount < (10 / 1936.27)) {

			// da 0 a 9 Lire 5 decimali
			decimalPos = 5;
		} else if (absAmount < (100 / 1936.27)) {

			// da 10 99 Lire 4 decimali
			decimalPos = 4;
		} else if (absAmount < (1000 / 1936.27)) {

			// da 100 a 999 Lire 3 decimali
			decimalPos = 3;
		}

		return bd.setScale((decimalPos+1), BigDecimal.ROUND_HALF_UP).setScale(decimalPos, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/** Genera una data e converte risultato in un determinato formato */
	public static String format(Integer year, Integer month, Integer day, String format) {
		Calendar in = Calendar.getInstance();
		resetTime(in);
		
		if (day!=null) {
			in.set(Calendar.DAY_OF_MONTH,day);
		}
		
		if (month!=null) {
			in.set(Calendar.MONTH,month);
		}
		
		if (year!=null) {
			in.set(Calendar.YEAR,year);
		}
		
		return format(in.getTime(), format);
	}

	/**
	 * Formatta una data/ora in un determinato formato
	 * 
	 * @param in
	 *          data da formattare
	 * @param format
	 *          formnato in cui formattare la data
	 * @return stringa contenente la data formattata secondo il parametro format
	 */
	public static String format(Date in, String format) {

		if (in == null) {
			return "";
		}
		return DateFormatUtils.format(in, format);
	}
	
	public static String format(Calendar in, String format) {
		if (in == null) {
			return "";
		}
		return format(in.getTime(),format);
	}
	
	/**
	 * Formatta una data nel formato dd/MM/yyyy
	 * 
	 * @param in
	 *          data da formattare
	 * @return stringa contenente la data formattata secondo il formato dd/MM/yyyy
	 */
	public static String formatDate(Date in) {
		return format(in, FORMAT_DATE);
	}
	public static String formatDate(long timestamp) {
		return DateFormatUtils.format(timestamp, FORMAT_DATE);
	}
	public static String formatDate(Calendar cal) {
		if (cal == null) {
			return StringUtils.EMPTY;
		}else{
			return formatDate(cal.getTime());
		}
	}

	/** Formatta una data nel formato HH:mm */
	public static String formatHour(Date in) {
		return format(in, FORMAT_SIMPLE_TIME);
	}

	/** Formatta una calendar nel formato HH:mm */
	public static String formatHour(Calendar cal) {
		if (cal == null) {
			return StringUtils.EMPTY;
		}else{
			return formatHour(cal.getTime());
		}
	}
	
	/**
	 * Formatta una data nel formato dd/MM/yyyy HH:mm:ss
	 * 
	 * @param in
	 *          data da formattare
	 * @return stringa contenente la data formattata secondo il formato dd/MM/yyyy
	 *         HH:mm:ss
	 */
	public static String formatDateTime(Date in) {
		return format(in, FORMAT_DATETIME);
	}
	
	public static String formatDateTime(long timestamp) {
		return DateFormatUtils.format(timestamp, FORMAT_DATETIME);
	}
	
	
	/**
	 * Formatta una data nel formato dd-MM-yy
	 * 
	 * @param in
	 *          data da formattare
	 * @return stringa contenente la data formattata secondo il formato dd/MM/yyyy
	 *         HH:mm:ss
	 */
	public static String formatSimpleDateTime(Date in) {
		return format(in, FORMAT_SIMPLE_DATE_2);
	}
	public static String formatSimpleDateTime(long timestamp) {
		return DateFormatUtils.format(timestamp, FORMAT_SIMPLE_DATE_2);
	}


	/**
	 * Formatta una data nel formato HH:mm:ss
	 * 
	 * @param in
	 *          data da formattare
	 * @return stringa contenente la data formattata secondo il formato HH:mm:ss
	 */
	public static String formatTime(Date in) {
		return format(in, FORMAT_TIME);
	}
	public static String formatTime(long timestamp) {
		return DateFormatUtils.format(timestamp, FORMAT_TIME);
	}
	public static String formatTime(Calendar in) {
		if (in==null) {
			return null;
		}
		return formatTime(in.getTime());
	}
	
	/**
	 * Formatta una data nel formato HH:mm
	 * 
	 * @param in
	 *          data da formattare
	 * @return stringa contenente la data formattata secondo il formato HH:mm
	 */
	public static String formatSimpleTime(Date in) {
		return format(in, FORMAT_SIMPLE_TIME);
	}

	/**
	 * Controlla se il parametro in ingresso � una data valida nel formato
	 * dd/MM/yyyy
	 * 
	 * @param in
	 *          Stringa contenente la data da validare nel formato dd/MM/yyyy
	 * @return un valore booleano con l'esito del controllo
	 */
	public static boolean isDate(String in) {
		return GenericValidator.isDate(in, FORMAT_DATE, true);
	}

	/**
	 * Controlla se il parametro in ingresso � una data valida nel formato
	 * dd/MM/yyyy HH:mm:ss
	 * 
	 * @param in
	 *          Stringa contenente la data da validare nel formato dd/MM/yyyy
	 *          HH:mm:ss
	 * @return un valore booleano con l'esito del controllo
	 */
	public static boolean isDateTime(String in) {
		return GenericValidator.isDate(in, FORMAT_DATETIME, true);
	}

	/**
	 * Controlla se il parametro in ingresso � una data valida nel formato
	 * HH:mm:ss
	 * 
	 * @param in
	 *          Stringa contenente la data da validare nel formato HH:mm:ss
	 * @return un valore booleano con l'esito del controllo
	 */
	public static boolean isTime(String in) {
		return GenericValidator.isDate(in, FORMAT_TIME, true);
	}
	
	/**
   * Trasforma una Stringa di formato "dd/MM/yyyy" in Calendar e imposta l'ora a 00:00:00.
   *  Viene tentata una seconda conversione per verificare se il dato �
   * in formato "yyyyMMdd" (nel caso di provenienza dei dati da SISS)
   * 
   * @param in La stringa da trasormare
   * @return null se la stringa in input e' null, altrimenti il Calendar
   *         corrispondente
   * @throws ParseException se la stringa in input non contiene un formato
   *           corretto. E' buona prassi lasciar uscire questa eccezione per
   *           avvertire il chiamante del problema di "formato".
   */
	public static Calendar stringToSimpleDate(String in) throws ParseException {
		if (StringUtils.isEmpty(in)) {
			return null;
		}

		Calendar ret = GregorianCalendar.getInstance();

		try {
			ret.setTime(Commons.parseDate(in));
		} catch (ParseException e) {
			ret.setTime(Commons.parse(in, "yyyyMMdd"));
		}

		resetTime(ret);
		return ret;
	}

	/**
	 * Effettua il parse di una data nel formato dd/MM/yy
	 * 
	 * @param in
	 *          stringa contenente la data su cui effettuare il parse
	 * @return data se il parametro in ingresso � NULL o "" (blank) viene
	 *         restituito un valore NULL
	 * @throws ParseException
	 *           se si verifica un errore durante il parse della data
	 */
	public static Date parseSimpleDate(String in) throws ParseException {
		return parse(in, FORMAT_SIMPLE_DATE);
	}

	/**
	 * Effettua il parse di una data nel formato dd/MM/yyyy
	 * 
	 * @param in
	 *          stringa contenente la data su cui effettuare il parse
	 * @return data se il parametro in ingresso � NULL o "" (blank) viene
	 *         restituito un valore NULL
	 * @throws ParseException
	 *           se si verifica un errore durante il parse della data
	 */
	public static Date parseDate(String in) throws ParseException {
		return parse(in, FORMAT_DATE);
	}

	/** 01/01/1900 as date*/
	public static Date getMinDate() {
		try {
			return Commons.parseDate(Commons.SYS_DATAMIN);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null; //essendo la formattazione di una costante, non andr� mai in errore..
	}
	
	/**01/01/1900 00:00:00*/
	public static Date getMinHour() {
		try {
			return Commons.parse(SYS_LONGDATAMIN, Commons.FORMAT_DATETIME);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null; //essendo la formattazione di una costante, non andr� mai in errore..
	}
	
	/** 01/01/1900 00:00:00*/
	public static Calendar getMinHourCalendar() {
		return Commons.dateToCalendar(getMinHour());
	}
	
	public static Date getMaxHour() {
		try {
			return Commons.parse("01/01/1900 23:59:59", Commons.FORMAT_DATETIME);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null; //essendo la formattazione di una costante, non andr� mai in errore..
	}
	

	/** 01/01/1900 as Calendar*/
	public static Calendar getMinCalendar() {
		Date dt = getMinDate();
		Calendar result = Calendar.getInstance();
		result.setTime(dt);
		return result;
	}
	
	/** 31/12/2999 as date*/
	public static Date getMaxDate() {
		try {
			return Commons.parseDate(Commons.SYS_DATAMAX);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null; //essendo la formattazione di una costante, non andr� mai in errore..
	}

	/**
	 * Torna true se la data in input � not null e
	 * coincide con {@link #getMaxDate()}
	 * @param input
	 * @return
	 */
	public static boolean isMaxDate(Date input){
		if (input == null){
			return false;
		}

		return input.equals(getMaxDate());
	}
	
	/** 31/12/2999 as Calendar*/
	public static Calendar getMaxCalendar() {
		Date dt = getMaxDate();
		Calendar result = Calendar.getInstance();
		result.setTime(dt);
		return result;
	}

	/**
	 * Torna true se il calendar in input � not null e
	 * coincide con {@link #getMaxCalendar()}
	 * @param input
	 * @return
	 */
	public static boolean isMaxCalendar(Calendar input){
		if (input == null){
			return false;
		}

		return input.equals(getMaxCalendar());
	}

	/** Restituisce la data massima tra quelle fornite */
	public static Date getMaxDate(Date dt1, Date dt2) {
		if (dt1==null) {
			return dt2;
		}
		if (dt2==null) {
			return dt1;
		}
		//Se dt1 � maggiore di dt2
		if (dt1.after(dt2)) {
			return dt1;
		}
		return dt2;
	}
	
	/** Restituisce la data massima tra quelle fornite */
	public static Calendar getMaxDate(Calendar dt1, Calendar dt2) {
		if (dt1==null) {
			return dt2;
		}
		if (dt2==null) {
			return dt1;
		}
		//Se dt1 � maggiore di dt2
		if (dt1.after(dt2)) {
			return dt1;
		}
		return dt2;
	}
	
	
	/** Restituisce la data minima tra quelle fornite */
	public static Date getMinDate(Date dt1, Date dt2) {
		if (dt1==null) {
			return dt2;
		}
		if (dt2==null) {
			return dt1;
		}
		//Se dt1 � minore di dt2
		if (dt1.before(dt2)) {
			return dt1;
		}
		return dt2;
	}
	
	/** Restituisce la data minima tra quelle fornite */
	public static Calendar getMinDate(Calendar dt1, Calendar dt2) {
		if (dt1==null) {
			return dt2;
		}
		if (dt2==null) {
			return dt1;
		}
		//Se dt1 � minore di dt2
		if (dt1.before(dt2)) {
			return dt1;
		}
		return dt2;
	}
	
	/**
	 * Effettua il parse di una data nel formato HH:mm:ss
	 * 
	 * @param in
	 *          stringa contenente la data su cui effettuare il parse
	 * @return data se il parametro in ingresso � NULL o "" (blank) viene
	 *         restituito un valore NULL
	 * @throws ParseException
	 *           se si verifica un errore durante il parse della data
	 */
	public static Date parseTime(String in) throws ParseException {
		return parse(in, FORMAT_TIME);
	}

	/**
	 * Effettua il parse di una data nel formato specificato
	 * 
	 * @param in
	 *          data da formmattare
	 * @param format
	 *          formato della data
	 * @return data, se il parametro in ingresso � NULL o "" (blank) viene
	 *         restituito un valore NULL
	 * @throws ParseException
	 *           se si verifica un errore durante il parse della data
	 */
	public static Date parse(String in, String format) throws ParseException {

		if (StringUtils.isEmpty(in)) {
			return null;
		}

		return new SimpleDateFormat(format).parse(in);
	}
	
	/**Effettua il parse di una data nel formato specificato e converte il risultato in calendar */	
	public static Calendar parseCalendar(String in, String format) throws ParseException {
		return dateToCalendar(parse(in, format));
	}

	/**
	 * Effettua il parse di una data nel formato HH:mm
	 * 
	 * @param in
	 *          stringa contenente la data su cui effettuare il parse
	 * @return data se il parametro in ingresso � NULL o "" (blank) viene
	 *         restituito un valore NULL
	 * @throws ParseException
	 *           se si verifica un errore durante il parse della data
	 */
	public static Date parseSimpleTime(String in) throws ParseException {
		return parse(in, FORMAT_SIMPLE_TIME);
	}

	/**
	 * Effettua il parse di una data nel formato dd/MM/yyyy HH:mm:ss
	 * 
	 * @param in
	 *          stringa contenente la data su cui effettuare il parse
	 * @return data se il parametro in ingresso e' NULL o "" (blank) viene
	 *         restituito un valore NULL
	 * @throws ParseException
	 *           se si verifica un errore durante il parse della data
	 */
	public static Date parseDateTime(String in) throws ParseException {
		return parse(in, FORMAT_DATETIME);
	}

	/**
	 * Effettua il parse di una data nel formato dd/MM/yyyy HH:mm
	 * 
	 * @param in
	 *          stringa contenente la data su cui effettuare il parse
	 * @return data se il parametro in ingresso e' NULL o "" (blank) viene
	 *         restituito un valore NULL
	 * @throws ParseException
	 *           se si verifica un errore durante il parse della data
	 */
	public static Date parseSimpleDateTime(String in) throws ParseException {
		return parse(in, FORMAT_SIMPLE_DATETIME);
	}
	
	/**
	 * Converte un Date in un calendar
	 * 
	 * @param in
	 *          Il date da convertire
	 * @return null se l'oggetto in input e' null, altrimenti il Calendar
	 *         convertito
	 */
	public static Calendar dateToCalendar(Date in) {

		if (in == null) {
			return null;
		}

		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(in);

		return cal;
	}
	
	/**Converte un oggetto Date in Instant */
	public static Instant dateToInstant(Date in) {
		//NB: Per qualche motivo java.sql.Date.toInstant() restituisce java.lang.UnsupportedOperationException*/
		if (in == null) {
			return null;
		}
		Calendar inCal = dateToCalendar(in);
		return inCal.toInstant();
	}
	
	/**
	 * Ritorna la differenza fra la prima e la seconda data espressa nell'unita' di
	 * misura specificata.
	 * 
	 * @param unit
	 *          L'unita' di misura nella quale esprimere il risultato
	 * @param date1
	 *          La prima data
	 * @param date2
	 *          La seconda data
	 * 
	 * @return La differenza fra la prima e la seconda data.
	 */
	public static long dateDiff(DateDiffUnit unit, Calendar date1, Calendar date2) {
		//Validazione input
		if (date1==null) {
			return 0;
		}
		if (date2==null) {
			return 0;
		}
		return dateDiff(unit, date1.getTime(), date2.getTime());
	}
	
	
	/**
	 * Ritorna la differenza fra la prima e la seconda data espressa nell'unita' di
	 * misura specificata.*/
	public static long dateDiff(DateDiffUnit unit, String date1, String date2) throws ParseException {
		String[] formats = { Commons.FORMAT_DATE, Commons.FORMAT_TIME, Commons.FORMAT_DATETIME };
		Date theDate1 = DateUtils.parseDate(date1, formats);
		Date theDate2 = DateUtils.parseDate(date2, formats);
		return dateDiff(unit, theDate1, theDate2);
	}
	
	/**
	 * Ritorna la differenza fra la prima e la seconda data espressa nell'unita' di
	 * misura specificata.
	 * 
	 * @param unit
	 *          L'unita' di misura nella quale esprimere il risultato
	 * @param date1
	 *          La prima data
	 * @param date2
	 *          La seconda data
	 * 
	 * @return La differenza fra la prima e la seconda data.
	 */
	public static long dateDiff(DateDiffUnit unit, Date date1, Date date2) {
		//Validazione input
		if (date1==null) {
			return 0;
		}
		if (date2==null) {
			return 0;
		}
		
		Calendar calendarDate1 = GregorianCalendar.getInstance();
		Calendar calendarDate2 = GregorianCalendar.getInstance();

		calendarDate1.setTime(date1);
		calendarDate2.setTime(date2);

		// restituisce la differenza tra due date in giorni
		// senza prendere in considerazione l'ora .
		if (unit.equals(DateDiffUnit.FULLDAY)) {
			// azzera l'orario della prima data
			calendarDate1.set(Calendar.HOUR_OF_DAY, 0);
			calendarDate1.set(Calendar.MINUTE, 0);
			calendarDate1.set(Calendar.SECOND, 0);

			// imposta l'orario della seconda data alle 23:59:59 per far calcolare il
			// giorno pieno
			calendarDate2.set(Calendar.HOUR_OF_DAY, 23);
			calendarDate2.set(Calendar.MINUTE, 59);
			calendarDate2.set(Calendar.SECOND, 59);
		}

		// Offset da aggiungere alla differenza dei secondi calcolati, per poter
		// gestire l'ora legale. Per le date rientranti nel periodo di orario legale
		// il valore dell'offset sara' pari a 3600000 millisecondi, 0 nel periodo di
		// orario solare.
		//
		// Es. differenza in giorni tra il 01/03 e il 31/03 dara' come offset:
		// per il 01/03 (cal1) = 0 per il 31/03 (cal2) = 3600000 nel nostro caso
		// quindi si avra'
		//
		// offset = 3600000 - 0 e quindi offset = 3600000 (1 ora)
		//
		// Es. differenza in giorni tra il 01/10 e il 31/10 dara' come offset:
		// per il 01/10 (cal1) = 3600000 per il 31/10 (cal2) = 0 nel nostro caso
		// quindi si avra'
		//
		// offset = 0 - 3600000 e quindi offset = -3600000 (-1 ora).

		int offset = (calendarDate2.get(Calendar.DST_OFFSET) - calendarDate1.get(Calendar.DST_OFFSET));

		// Differenza fra gli orari espressa in millisecondi
		long diff = (calendarDate2.getTimeInMillis() - calendarDate1.getTimeInMillis()) + offset;

		switch (unit) {
		case SECOND:
			diff /= DateUtils.MILLIS_PER_SECOND;
			break;

		case MINUTE:
			diff /= DateUtils.MILLIS_PER_MINUTE;
			break;

		case HOUR:
			diff /= DateUtils.MILLIS_PER_HOUR;
			break;

		case DAY:
			diff /= DateUtils.MILLIS_PER_DAY;
			break;

		case FULLDAY:
			diff /= DateUtils.MILLIS_PER_DAY;
			break;
		}

		return diff;
	}

	/**
	 * Concatena la data del primo parametro con l'ora del secondo parametro.
	 * 
	 * @param date
	 *          {@link Date} da cui recuperare la parte data da concatenare
	 * @param time
	 *          {@link Date} da cui recuperare la parte ora da concatenare
	 * 
	 * @return Una data con data e ora concatenate
	 */
	public static Date joinDateAndTime(Date date, Date time) {
		Date result = null;
		String formatted = Commons.formatDate(date) + " " + Commons.formatTime(time);

		try {
			result = Commons.parseDateTime(formatted);
		} catch (ParseException e) {
			result = null;
		}

		return result;
	}

	/**
	 * Concatena la data del primo parametro con l'ora del secondo parametro.
	 * 
	 * @param date
	 *          {@link Date} da cui recuperare la parte data da concatenare
	 * @param time
	 *          {@link Date} da cui recuperare la parte ora da concatenare
	 * 
	 * @return Una data con data e ora concatenate
	 */
	public static Calendar joinDateAndTime(Calendar date, Calendar time) {
		return dateToCalendar(joinDateAndTime(date.getTime(), time.getTime()));
	}
	
	
	/**
	 * Converte in formato numerico i numeri di telefono , eliminando gli /
	 * @param phoneNumber
	 * @return
	 */
	public static String formatPhoneNumber(String phoneNumber) {
		
		String retPhoneNumber = "";
		
		try{
		
			retPhoneNumber = phoneNumber.replace("/", "").trim();
		
		}catch(Exception ex){
			Logger logger = org.slf4j.LoggerFactory.getLogger("");
			logger.error("Errore durante la conversione numerica del numero di telefono " + ex.getMessage());
		}
		
		return retPhoneNumber;
	}

	/** Applica l'attuale encoding ad una stringa UTF-8*/
	public static String encodeString(String str) {
		if (StringUtils.isEmpty(str)) {
			return str;
		}
		
		byte ptext[] = str.getBytes(UTF_8_CHARSET); 
		return new String(ptext, CURRENT_CHARSET);
	}
	
	/** Converte una stringa CSV in un array di numeri interi */
	public static Integer[] csvToIntegerArray (String csv)
	{
		if (StringUtils.isEmpty(csv)) {
			return new Integer[0];
		}
		
		String[] strArray = String.valueOf(csv).split(",");
		Integer[] intArray = new Integer[strArray.length];
		for (int i = 0; i < strArray.length; i++) {
			if (StringUtils.isNotEmpty(strArray[i])) {
				intArray[i]=Integer.valueOf(strArray[i]);
			}
		}
		return intArray;
	}
	
	/** Date to Calendar */
	public static Calendar toCalendar(Date in) {
		if (in==null) {
			return null;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(in);
		return c;
	}
	
	public static String truncateString(String input, int length){
		String result = input;
		if(StringUtils.isNotEmpty(input) && input.length() > length){
			result = input.substring(0, length);
		}
		return result;
	}

	/**
	 * Ritorna un comparator che compara due stringhe parsate 
	 * in data con Commons.parseDate()
	 * @return
	 */
	public static Comparator<String> getDateComparator(){
		return new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				Date d1;
				Date d2;
				try {
					d1 = Commons.parseDate(o1);
				} catch (ParseException e) {
					logger.warn("data '"+o1+"' non valida");
					return 1;
				}
				try {
				    d2 = Commons.parseDate(o2);
				} catch (ParseException e) {
					logger.warn("data '"+o2+"' non valida");
					return -1;
				}
				
				return (d1.after(d2))? 1 : -1;
			}
		};
	}
	
	/** Metodo che confronta due date di calendario */
	public static int compareCalendar(Calendar cal1, Calendar cal2) {
		if (cal1==null || cal2==null) {
			return 0;
		}
		if (cal1.equals(cal2)) {
			return 0;
		}
		else if (cal1.after(cal2)) {
			return 1;
		} else {
			return -1;
		}
	}
	
	/** Metodo che confronta due date di calendario */
	public static int compareCalendar(Calendar cal1, Calendar cal2, SortEnum sortDirection) {
		if (sortDirection==null || sortDirection==SortEnum.ASC) {
			return compareCalendar(cal1, cal2);
		} else {
			return compareCalendar(cal2, cal1);
		}
	}
	
	/** Metodo che confronta due stringhe */
	public static int compareString(String str1, String str2) {
		if (str1==null || str2==null) {
			return 0;
		}
		if (StringUtils.isEmpty(str1) || StringUtils.isEmpty(str2)) {
			return 0;
		}
		if (str1.equalsIgnoreCase(str2)) {
			return 0;		
		} else {
			return str1.compareTo(str2);
		}
	}
	
	/** Metodo che confronta due stringhe */
	public static int compareString(String str1, String str2, SortEnum sortDirection) {
		if (sortDirection==null || sortDirection==SortEnum.ASC) {
			return compareString(str1, str2);
		} else {
			return compareString(str2, str1);
		}
	}
	
	/** Metodo che confronta due variabili Boolean */
	public static int compareBoolean(Boolean bool1, Boolean bool2) {
		if (bool1==null || bool2==null) {
			return 0;
		}
		if (bool1==bool2) {
			return 0;		
		} else {
			if (bool1) {
				return -1;
			} else {
				return 1;
			}
		}
	}
	
	/** Metodo che confronta due variabili Boolean */
	public static int compareBoolean(Boolean bool1, Boolean bool2, SortEnum sortDirection) {
		if (sortDirection==null || sortDirection==SortEnum.ASC) {
			return compareBoolean(bool1, bool2);
		} else {
			return compareBoolean(bool2, bool1);
		}
	}
		
	/** Metodo che confronta due variabili Integer */
	public static int compareInteger(Integer int1, Integer int2) {
		if (int1==null || int2==null) {
			return 0;
		}
		if (int1.equals(int2)) {
			return 0;		
		} else {
			return int1.compareTo(int2);
		}
	}
	
	/** Metodo che confronta due variabili Integer */
	public static int compareInteger(Integer int1, Integer int2, SortEnum sortDirection) {
		if (sortDirection==null || sortDirection==SortEnum.ASC) {
			return compareInteger(int1, int2);
		} else {
			return compareInteger(int2, int1);
		}
	}
	
	/** Metodo che confronta due variabili Double */
	public static int compareDouble(Double db1, Double db2) {
		if (db1==null || db2==null) {
			return 0;
		}
		if (db1.equals(db2)) {
			return 0;		
		} else {
			return db1.compareTo(db2);
		}
	}
	
	/** Metodo che confronta due variabili Double */
	public static int compareDouble(Double db1, Double db2, SortEnum sortDirection) {
		if (sortDirection==null || sortDirection==SortEnum.ASC) {
			return compareDouble(db1, db2);
		} else {
			return compareDouble(db1, db2);
		}
	}
	
	public static Double stringCurrencyToDouble(String sCurrency) {
		Double result = null;
		try {
			result = Double.parseDouble(sCurrency.replace(',','.'));
		}catch(Exception e){
			result = null;
		}
		return result;
	}

	/** Restituisce la data minima tra quelle passate in input */
	public static Calendar findMinCalendar(Calendar...calendars) {
		Calendar result = null;
		try 
		{			
			for (Calendar cal : calendars) {
				//Non considero le date non valorizzate
				if (cal==null) {
					continue;
				}
				
				//Scelgo come risultato "provvisorio" la prima data valorizzata
				if (result==null) {
					result = cal;
					continue;
				}

				//Confronto la data "provvisoria" col valore corrente e prendo il MINORE
				//Se il risultato "provvisorio" fosse dopo il valore corrente, sceglie il valore corrente
				if (result.after(cal)) {
					result = cal;
				}
			}

		} catch (Exception e) {
			logger.error("errore nel tentativo di calcolare il valore minimo.");
		}
		
		//Clono il risultato per evitare anomalie
		if (result!=null) {
			result = (Calendar) result.clone();
		}
		return result;
	}
	
	/** Restituisce la data minima tra quelle passate in input */
	public static Calendar findMaxCalendar(Calendar...calendars) {
		Calendar result = null;
		try 
		{			
			for (Calendar cal : calendars) {
				//Non considero le date non valorizzate
				if (cal==null) {
					continue;
				}
				
				//Scelgo come risultato "provvisorio" la prima data valorizzata
				if (result==null) {
					result = cal;
					continue;
				}

				//Confronto la data "provvisoria" col valore corrente e prendo il MAGGIORE
				//Se il risultato "provvisorio" fosse dopo il valore corrente, sceglie il valore corrente
				if (result.before(cal)) {
					result = cal;
				}
			}

		} catch (Exception e) {
			logger.error("errore nel tentativo di calcolare il valore massimo.");
		}
		
		//Clono il risultato per evitare anomalie
		if (result!=null) {
			result = (Calendar) result.clone();
		}
		return result;
	}

	/** Restituisce il numero intero minimo tra quelle passate in input */
	public static Integer getMinInteger(Integer...integers) {
		Integer result = null;
		try 
		{			
			for (Integer integer : integers) {
				//Non considero le date non valorizzate
				if (integer==null) {
					continue;
				}
				
				//Scelgo come risultato "provvisorio" la prima data valorizzata
				if (result==null) {
					result = integer;
					continue;
				}

				//Confronto il numero "provvisorio" col valore corrente e prendo il minore
				if (result>integer) {
					result = integer;
				}
			}

		} catch (Exception e) {
			logger.error("errore nel tentativo di calcolare il valore minimo.");
		}
		
		//Clono il risultato per evitare anomalie
		if (result!=null) {
			result = new Integer(result.intValue());
		}
		return result;
	}
	
	/** Restituisce il numero intero massimo tra quelle passate in input */
	public static Integer getMaxInteger(Integer...integers) {
		Integer result = null;
		try 
		{			
			for (Integer integer : integers) {
				//Non considero le date non valorizzate
				if (integer==null) {
					continue;
				}
				
				//Scelgo come risultato "provvisorio" la prima data valorizzata
				if (result==null) {
					result = integer;
					continue;
				}

				//Confronto il numero "provvisorio" col valore corrente e prendo il maggiore
				if (result<integer) {
					result = integer;
				}
			}

		} catch (Exception e) {
			logger.error("errore nel tentativo di calcolare il valore minimo.");
		}
		
		//Clono il risultato per evitare anomalie
		if (result!=null) {
			result = new Integer(result.intValue());
		}
		return result;
	}
	
	
	/** Restituisce la data massima tra quelle passate in input */
	public static Calendar getMaxDate(Calendar...calendars) {
		Calendar result = null;
		try 
		{			
			for (Calendar cal : calendars) {
				//Non considero le date non valorizzate
				if (cal==null) {
					continue;
				}
				
				//Scelgo come risultato "provvisorio" la prima data valorizzata
				if (result==null) {
					result = cal;
					continue;
				}

				//Confronto la data "provvisoria" col valore corrente e prendo il minore
				//Se il risultato "provvisorio" fosse dopo il valore corrente, sceglie il valore corrente
				if (result.before(cal)) {
					result = cal;
				}
			}

		} catch (Exception e) {
			logger.error("errore nel tentativo di calcolare il valore massimo.");
		}
		
		//Clono il risultato per evitare anomalie
		if (result!=null) {
			result = (Calendar) result.clone();
		}
		return result;
	}

	
	public static boolean isSameDay(Calendar cal1, Calendar cal2){
		if(cal1 == null || cal2 == null){
			return false;
		}
		else{
			return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
			                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		}
	}
	
	/** Converte una stringa in oggetto Integer gestendo valori null */
	public static Integer toInteger(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return new Integer(value);
	}
	
	/** Converte un long in oggetto Integer gestendo valori null 
	 * NON gestisce eventuale overflow nella conversione */
	public static Integer toInteger(Long value) {
		if (value==null) {
			return null;
		}
		return new Integer(String.valueOf(value));
	}
	
	/** Converte una stringa in oggetto Long gestendo valori null */
	public static Long toLong(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return new Long(value);
	}
	
	/** Se oggetto firstChoice not null resestituisce quello, in caso contrario restituisce valore secondChoice */
	public static <T> T coalesce(T firstChoice,T secondChoice) {
		if (firstChoice!=null) {
			return firstChoice;
		} else {
			return secondChoice;
		}
	}
	
	/**
	 * Restituisce true se le due liste si intersecano, ovvero se ci sono date in comune<br><br>
	 * <b>
	 * NB: Gli oggetti vengono confrontati nativamente con metodo equals quindi<br> 
	 * si da per scontato gli oggetti Date devo essere uguali (compresi ora, minuti e secondi)<br>
	 * </b>
	 *   
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static boolean dateListIntersects(List<Date> l1, List<Date> l2) {
		
		if (l1==null || l2==null) {
			return false;
		}
		
		if (l1.size()==0 || l2.size()==0) {
			return false;
		}
		
		boolean intersect=false;
		for (Date d:l1) {
				if (l2.contains(d)) {
					intersect=true;
					break;
				}
		}
		
		if (!intersect) {
			for (Date d:l2) {
				if (l1.contains(d)) {
					intersect=true;
					break;
				}
			}
		}
		
		return intersect;
	}	

	/**Ritorna una data che rappresenta la somma algebrica fra la data e il numero di unit� temporali specificati
	* 
	* @param field: Calendar constants (Calendar.SECOND,Calendar.MINUTE,Calendar.HOUR,Calendar.DATE,Calendar.MONTH,Calendar.YEAR) 
	* @param number: Delta da addizionare (pu� essere negativo)
	* @param date: Data da cui patire
	* @return La nuova data calcolata
	*/
	public static Date dateAdd(int field, int number, Date date) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.add(field, number);
		return cal.getTime();
	}
	
	/**Ritorna una data che rappresenta la somma algebrica fra la data e il numero di unit� temporali specificati
	* 
	* @param field: Calendar constants (Calendar.SECOND,Calendar.MINUTE,Calendar.HOUR,Calendar.DATE,Calendar.MONTH,Calendar.YEAR) 
	* @param number: Delta da addizionare (pu� essere negativo)
	* @param date: Data da cui patire
	* @return La nuova data calcolata
	*/
	public static Calendar calendarAdd(int field, int number, Calendar date) {
		if (date==null) {
			return null;
		}
		return Commons.dateToCalendar(dateAdd(field, number, date.getTime()));
	}
	

	/**Ritorna una data che rappresenta la somma algebrica fra la data e il numero di unit� temporali specificati
	* 
	* @param field: Calendar constants (Calendar.SECOND,Calendar.MINUTE,Calendar.HOUR,Calendar.DATE,Calendar.MONTH,Calendar.YEAR) 
	* @param number: Delta da addizionare (pu� essere negativo)
	* @param date: Data da cui patire
	* @return La nuova data calcolata
	*/
	public static Calendar dateAdd(int field, int number, Calendar date) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date.getTime());
		cal.add(field, number);
		return cal;
	}
	
	/**Restituisce true i due periodi sono sovrapposti.
	 * Se ignoreEqual==true, non considera "sovrapposti" gli estremi che si toccano (inizio di un periodo su fine dell'altro) */
	public static boolean between(Calendar from1, Calendar to1, Calendar from2, Calendar to2, boolean ignoreEqual) {
		//Gestiamo equals utilizzando le stringhe, per evitare errori (equals su date in certi casi da risultati imprevisti)
		String from1Str = Commons.format(from1, Commons.FORMAT_DATETIME);
		String to1Str = Commons.format(to1, Commons.FORMAT_DATETIME);
		String from2Str = Commons.format(from2, Commons.FORMAT_DATETIME);
		String to2Str = Commons.format(to2, Commons.FORMAT_DATETIME);
		
		//Validazione input
		if (from1==null || to1==null || from2==null || to2==null) {
			return false;
		}
		
		//Se uno dei periodi inizia dopo la fine dell'altro, inutile procedere
		if (from1.after(to2) || from2.after(to1)) {
			return false;
		}
		
		//Se l'inizio del primo periodo � compreso nel secondo..
		//Se l'inizio di un periodo � compresa nell'altro (estremi esclusi)
		if (from1.after(from2) && from1.before(to2)) {
			return true;
		}
		if (from2.after(from1) && from2.before(to1)) {
			return true;
		}

		//Se la fine di un periodo � compresa nell'altro (estremi esclusi)
//between(Calendar val, Calendar from, Calendar to, boolean ignoreEqual)
		if (to1.after(from2) && to1.before(to2)) {
			return true;
		}
		if (to2.after(from1) && to2.before(to1)) {
			return true;
		}

		//se inizio o fine corrispondono, sono sicuramente sovrapposti
		if (from1Str.equals(from2Str) || to1Str.equals(to2Str)) {
			return true;
		}
		
		//Verifico gli estremi (se l'inizio di un periodo corrisponde con la fine dell'altro)
		if (!ignoreEqual) {
			if (from1Str.equals(to2Str) || from2Str.equals(to1Str)) {
				return true;
			}			
		}
		
		return false;
	}
	
	/**Calcola la percentuale di un numero intero (per difetto)
	 * 
	 * @param value: valore di riferimento
	 * @param percentage: percentuale da calcolare
	 * @return
	 */
	public static int percentage(int value, int percentage) {
		if (percentage<=0) {
			return 0;
		}
		if (value==0) {
			return 0;
		}
		return (int)(value*(percentage/100.0f));
	}
	
	/**Converte una weekDayMask in una lista di giorni della settimana
	 * 1=lun, 2=mart, 3=merc, 4=giov, 5=ven, 6=sab, 7=dom
	 * 
	 * Se la maschera comprende "tutti" o "nessuno", il risultato � una lista vuota
	 * (si parte dal principio che "tutti" � da ignorare in quanto non un filtro e "nessuno" � da ignorare in quanto non ha senso)
	 * 
	 * @param weekDayMask
	 * @return
	 */
	public static Set<Integer> getWeekDays(String weekDayMask) {
		Set<Integer> result = new HashSet<>();
		final String emptyMap = "0000000";
//		final String fullMap = "1111111";
		
		if (weekDayMask==null) {
			return result;
		}
		if (weekDayMask.equals(emptyMap)) {
			return result;
		}
//		if (weekDayMask.equals(fullMap)) {
//			return result;
//		}
		
		//La mask prevede che il primo giorno sia il luned�; pertanto la posizione nella stringa corrisponde al giorno 
		for (int i = 0; i < weekDayMask.length(); i++) {
			if (weekDayMask.substring(i, (i + 1)).equals("1")) {
				result.add(i+1);
			}
		}
		
		return result;
	}
	
	/**Restituisce il giorno della settimana nel formato di riferimento 1=luned�, 7=domenica
	 * 
	 * @param rifDate
	 * @return
	 */
	public static Integer getWeekDay(Date rifDate) {
		return getWeekDay(dateToCalendar(rifDate));
	}
	
	/**Restituisce il giorno della settimana nel formato di riferimento 1=luned�, 7=domenica
	 * 
	 * @param rifDate
	 * @return
	 */
	public static Integer getWeekDay(Calendar rifDate) {
		if (rifDate==null) {
			return null;
		}
		
		//Giorno della settimana in formato 1-7 (1=Domenica, 2=Luned�, 7=Sabato)
		int dayOfWeek = rifDate.get(Calendar.DAY_OF_WEEK);
		
		//Converto il risultato in formato 1-7 (1=Luned�, 7=Domenica)
		dayOfWeek = dayOfWeek-1;
		if (dayOfWeek==0) {
			dayOfWeek=7;
		}

		return dayOfWeek;
	}

	/**Didide due numeri ed arrotonda per eccesso*/
	public static long roundUp(long num, long divisor) {
	    int sign = (num > 0 ? 1 : -1) * (divisor > 0 ? 1 : -1);
	    return sign * (abs(num) + abs(divisor) - 1) / abs(divisor);
	}
	
	/**Didide due numeri ed arrotonda per eccesso*/
	public static int roundUp(int num, int divisor) {
	    int sign = (num > 0 ? 1 : -1) * (divisor > 0 ? 1 : -1);
	    return sign * (abs(num) + abs(divisor) - 1) / abs(divisor);
	}
	
	/**Confonto tra due stringhe*/
	public static boolean equals(String v1, String v2, boolean withCase) {
		if (v1 == null) {
			if (v2 != null) {
				return false;
			}
		} else if (withCase) {
			if (!v1.equals(v2)) {
				return false;
			}
		} else if (!v1.equalsIgnoreCase(v2)) {
			return false;
		}
		return true;
	}
	
	/**Confonto tra due interi*/
	public static boolean equals(Integer v1, Integer v2) {
		if (v1 == null) {
			if (v2 != null) {
				return false;
			}
		} else if (!v1.equals(v2)) {
			return false;
		}
		return true;
	}
	
	/**Confronto tra date di calendario, convertiti in stringa per aggirare problemi di confronto tra oggetti calendar
	 * Essendo una classe astratta, l'equals tra due istanze di classe che la implementano potrebbe dare false anche se entrambe puntano alla stessa data
	 *  
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	public static boolean equals(Calendar cal1, Calendar cal2) {
		return equals(cal1, cal2, Commons.FORMAT_DATETIME);
	}

	public static boolean equals(Calendar cal1, Calendar cal2, String format) {
		String cal1Str = Commons.format(cal1, format);
		String cal2Str = Commons.format(cal2, format);
		return cal1Str.equals(cal2Str);
	}
	
	/**Restituisce un nuovo oggetto Calendar equivalente a quello passato in input*/
	public static Calendar getCalendar(Calendar in) {
		if (in==null) {
			return null;
		}
		Calendar out = Calendar.getInstance();
		out.setTime(in.getTime());
		return out;
	}
	
	/**Restituisce un nuovo oggetto Calendar equivalente a quello passato in input*/
	public static Calendar getCalendar(Date in) {
		if (in==null) {
			return null;
		}
		Calendar out = Calendar.getInstance();
		out.setTime(in);
		return out;
	}
	
	/**********************************************************************/
	/********** METODI PER IMPOSTARE ORA 00:00:00:000  ********************/		
	/**********************************************************************/
	
	/**Data odierna con orario impostato a 00:00:00:000 */
	public static Date getToday() {
		return Commons.getDayStart(new Date());
	}
	
	/** Fornisce una NUOVA data con orario impostato a 00:00:00:000 */
	public static Date getDayStart(Date in) {
		//Verifica input
		if (in == null) {
			return null;
		}
		Calendar out = Calendar.getInstance();
		out.setTime(in);
		return resetTime(out).getTime();
	}	


	/** Fornisce una NUOVA data con orario impostato a 00:00:00:000 */
	public static Calendar getDayStart(Calendar in)
	{
		//Verifica input
		if (in == null) {
			return null;
		}
		Calendar out = Calendar.getInstance();
		out.setTime(in.getTime());
		return resetTime(out);
	}
			
	/** AZZERA i campi ora, minuti, secondi di un Calendar impostandoli a
	 * 00:00:00.000 per ottenere l'inizio del giorno corrente.
	 * 
	 * @param in
	 *          Il Calendar da azzerare
	 * @return L'oggetto fornito in input con i campi opportunamente modificati
	 */
	public static Calendar dayStart(Calendar in) {
		if (in != null) {
			in.set(Calendar.HOUR_OF_DAY, 0);
			in.set(Calendar.MINUTE, 0);
			in.set(Calendar.SECOND, 0);
			in.set(Calendar.MILLISECOND, 0);
		}
		return in;
	}
	
	/** AZZERA i campi ora, minuti, secondi di un Calendar impostandoli a
	 * 00:00:00.000 per ottenere l'inizio del giorno corrente.
	 * 
	 * @param in
	 *          Il Calendar da azzerare
	 * @return L'oggetto fornito in input con i campi opportunamente modificati
	 */
	public static Calendar resetTime(Calendar in) {
		return dayStart(in);
	}

	
	/**********************************************************************/
	/********** METODI PER IMPOSTARE ORA 23:59:59:999  ********************/	
	/**********************************************************************/

	
	/** Fornisce una NUOVA data in cui l'orario � impostato a 23:59:59:999*/
	public static Date getDayEnd(Date in)
	{
		//Verifica input
		if (in == null) {
			return null;
		}
		Calendar out = Calendar.getInstance();
		out.setTime(in);
		return dayEnd(out).getTime();		
	}	


	/** Fornisce una NUOVA data in cui l'orario � impostato a 23:59:59:999*/
	public static Calendar getDayEnd(Calendar in)
	{
		//Verifica input
		if (in == null) {
			return null;
		}
		Calendar out = Calendar.getInstance();
		out.setTime(in.getTime());
		return dayEnd(out);
	}
	
	/** AZZERA i campi ora, minuti, secondi di un Calendar impostandoli a
	 * 23:59:59.999 per ottenere la fine del giorno corrente.
	 * 
	 * @param in
	 *          Il Calendar da impostare
	 * @return L'oggetto fornito in input con i campi opportunamente modificati
	 */
	public static Calendar dayEnd(Calendar in) {

		if (in != null) {
			in.set(Calendar.HOUR_OF_DAY, 23);
			in.set(Calendar.MINUTE, 59);
			in.set(Calendar.SECOND, 59);
			in.set(Calendar.MILLISECOND, 999);
		}

		return in;
	}
	

	
	/**********************************************************************/
	/********** METODI PER IMPOSTARE DATA 01/01/1900  *********************/		
	/**********************************************************************/
	
	/**Fornisce una NUOVA data da cui la data � impostata a 1/1/1900.
	 * 
	 * @param in
	 *          Il Date da azzerare
	 * @return Clone dell'oggetto fornito in input con i campi opportunamente modificati
	 * @throws ParseException
	 *           se si verifica un errore durante il parse dell'input
	 */
	public static Date getResettedDate(Date in) {
		if (in==null) {
			return null;
		}
		Calendar out = Calendar.getInstance();
		out.setTime(in);
		out.set(Calendar.YEAR, 1900);
		out.set(Calendar.MONTH, Calendar.JANUARY);
		out.set(Calendar.DATE, 1);
		return out.getTime();
	}

	/**Fornisce una NUOVA data da cui la data � impostata a 1/1/1900.
	 * 
	 * @param in
	 *          Il Calendar da azzerare
	 * @return Clone dell'oggetto fornito in input con i campi opportunamente modificati
	 */
	public static Calendar getResettedDate(Calendar in) {
		//Verifica input
		if (in == null) {
			return null;
		}
		Calendar out = Calendar.getInstance();
		out.setTime(in.getTime());
		return resetDate(out);
	}
	
	/**Resetta ed imposta la data a 1/1/1900 nell'oggetto in input.
	 * 
	 * @param in
	 *          Il Calendar da azzerare
	 * @return L'oggetto fornito in input con i campi opportunamente modificati
	 */
	public static Calendar resetDate(Calendar in) {
		//Verifica input
		if (in == null) {
			return null;
		}
		in.set(Calendar.YEAR, 1900);
		in.set(Calendar.MONTH, Calendar.JANUARY);
		in.set(Calendar.DATE, 1);
		return in;
	}

	/**Trova l'ennesimo giorno della settimana in un mese (es: 2� luned� del mese) */
	public static Calendar getDayOfMonth(int weekday, int month, int year, int occurrence) {
		Calendar cal = resetTime(Calendar.getInstance());
		
		//Primo giorno del mese 
		cal.set(year, month, 1);
		
		//Richiesto ultimo giorno del mese
		if (occurrence>=5) {
			return getLastDayOfMonth(weekday, month, year);
		}
		
		int currPos = 0;
		
		//Scansiona il mese trovando il giorno richiesto
		while(cal.get(Calendar.MONTH)==month) {
			if (getWeekDay(cal).intValue() == weekday) {
				currPos+=1;
			}
			if (currPos==occurrence) {
				return cal;
			}
			cal.add(Calendar.DATE, 1);
		}
		
		//Non dovrebbe mai arrivare qui..
		return null;
		
	}
	
	/** Find the last weekday of month in the specify month and year
	 * @param weekday: 1=luned�, 7=domenica.. vds getWeekDay(cal)
	 * @param month: 0=gennaio, 11=dicembre
	 * @param year
	 * @return
	 */
	public static Calendar getLastDayOfMonth(int weekday, int month, int year) {
		Calendar cal = resetTime(Calendar.getInstance());

		//Primo giorno del mese seguente
		cal.set(year, month + 1, 1);

		//Ultimo giorno del mese in corso
		cal.add(Calendar.DATE, -1);

		//Procede a ritroso trovando il giorno della settimana richiesto
		while(getWeekDay(cal).intValue() != weekday) {
			cal.add(Calendar.DATE, -1);
		}
		
		//Restituisce il giorno trovato
		return cal;
	}
	
	/** Find the last date of month in the specify month and year
	 * @param date
	 * @return
	 */
	public static Date getLastDateOfMonth(Date date) {
		date = DateUtils.addMonths(date, 1);
		Calendar cal = dateToCalendar(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return addDay(cal.getTime(), -1);
	}
	
	public static boolean toBoolean(String s) {
		return s!=null && ArrayUtils.contains(TRUE_VALUES, StringUtils.upperCase(s));
	}
	
	
	public static Integer toIntegerSafe(String value) {
		if(StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			logger.warn("Conversione del valore:" + value + "ad int fallita.", e);
			return null;
		}
	}
	
	
	/**
	 * Converte un Calendar in una LocalDate.
	 * Se Calendar è null verrà usato defaultCalendar per la conversione.
	 * La conversione tornerà una localDate basata sulla zone passata come parametro.
	 * Se zoneId è null, verrà usata la zoneId restituita dal metodo ZoneId.systemDefault()
	 * @param calendar
	 * @param defaultCalendar
	 * @param zoneId
	 * @return LocalDate
	 */
	public static LocalDate toLocalDate(Calendar calendar){
		return toLocalDate(calendar, null, null);
	}
	public static LocalDate toLocalDate(Calendar calendar, Calendar defaultCalendar, ZoneId zoneId){
		if (calendar == null) {
			calendar = defaultCalendar;
		}

		if (calendar == null) {
			return null;
		}

		if (zoneId == null){
			zoneId = ZoneId.systemDefault();
		}

		return calendar.toInstant().atZone(zoneId).toLocalDate();

	}
}
