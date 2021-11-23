package com.rob.core.utils.java;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Title: StringList
 * </p>
 * <p>
 * Description: Lista tipizzata di string.
 * </p>
 */
public class StringList extends Vector<String> {

	/** Richiesto dalla serializzazione */
	private static final long serialVersionUID = -124545410098753360L;

	/**
	 * Costruisce un vettore vuoto le cui capacità iniziale e di incremento pari sono specificate.
	 * 
	 * @param initialCapacity
	 *          La capacità iniziale del vettore.
	 * @param capacityIncrement
	 *          La quantità di cui è incrementata la capacità quando il vettore sborda.
	 */
	public StringList(int initialCapacity, int capacityIncrement) {
		super(initialCapacity, capacityIncrement);
	}

	/**
	 * Costruisce un vettore vuoto con la capacità iniziale specificata e la capacità di incremento pari a zero.
	 * 
	 * @param initialCapacity
	 *          La capacità iniziale del vettore.
	 */
	public StringList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Inizializza il vettore tipizzato
	 */
	public StringList() {
		super();
	}

	/**
	 * Costruisce un vettore contenente gli elementi specificati dalla collezione, nell'ordine in cui sono restituiti dall'iteratore della
	 * collezione.
	 * 
	 * @param in
	 *          La collezione degli elementi che devono essere piazzati in questo vettore.
	 */
	public StringList(Vector<String> in) {
		super(in);
	}

	/**
	 * Costruisce un vettore contenente gli elementi specificati dalla collezione, nell'ordine in cui sono restituiti dall'iteratore della
	 * collezione.
	 * 
	 * @param in
	 *          La collezione degli elementi che devono essere piazzati in questo vettore.
	 */
	public StringList(Set<String> in) {
		super(in);
	}
	/**
	 * Costruisce un vettore contenente gli elementi specificati dalla collezione, nell'ordine in cui sono restituiti dall'iteratore della
	 * collezione.
	 * 
	 * @param in
	 *          La collezione degli elementi che devono essere piazzati in questo vettore.
	 */
	public StringList(List<String> in) {
		super(in);
	}
	/**
	 * Inizializza la lista tipizzata di oggetti partendo da un array di oggetti String.
	 * 
	 * @param in
	 *          String[] vettore di String
	 */
	public StringList(String[] in) {
		this(in,false);
	}

	public StringList(String[] in, boolean decode){
		if (in != null && decode) {
			String s2[] = new String[in.length];
			for (int i = 0; i < in.length; i++) {
				String s;
				try {
					s = URLDecoder.decode(in[i], "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					s = "";
				}
				s2[i]=s;
			}
			addAll(s2);
		} else {
			addAll(in);
		}
	}
	
	/**
	 * Inizializza la lista tipizzata di oggetti effettuando lo split della stringa passata come parametro.
	 * 
	 * @param in
	 *          La stringa da splittare.
	 */
	public StringList(String in) {
		split(in, null, false);
	}

	/**
	 * Inizializza la lista tipizzata di oggetti effettuando lo split della stringa passata come parametro.
	 * 
	 * @param in
	 *          La stringa da splittare.
	 * @param separator
	 *          Delimitatore delle stringhe
	 */
	public StringList(String in, String separator) {
		split(in, separator, false);
	}

	/**
	 * Inizializza la lista tipizzata di oggetti effettuando lo split della stringa passata come parametro.
	 * 
	 * @param in
	 *          La stringa da splittare.
	 * @param separator
	 *          Delimitatore delle stringhe
	 * @param preserveAllTokens
	 *          Indica se effettuare lo split mantenendo tutti i valori o escludendo quelli nulli
	 */
	public StringList(String in, String separator, boolean preserveAllTokens) {
		split(in, separator, preserveAllTokens);
	}

	/**
	 * Aggiunge a questo oggetto tutti gli elementi contenuti nell'array.
	 * 
	 * @param all
	 *          Array contenente tutte le stringhe da aggiungere alla lista
	 */
	public void addAll(String[] all) {
		if (!ArrayUtils.isEmpty(all)) {
			addAll(Arrays.asList(all));
		}
	}

	/**
	 * Restituisce un array di String
	 * 
	 * @return String[] array di String
	 */
	public String[] toStringArray() {
		return toArray(new String[size()]);
	}

	/**Restituisce una lista di String 
	 * @return */
	public List<String> toArrayList() {
		List<String> result = new ArrayList<String>();
		result.addAll(this);
		return result;
	}
	/**Restituisce un Set di String 
	 * @return */
	public Set<String> toSet() {
		Set<String> ret = new HashSet<>();
		for (String val : this) {
			if (val==null) {
				continue;
			}
			if (val.length()==0) {
				continue;
			}
			ret.add(val);
		}
		return ret;
	}
	
	/**
	 * Ritorna l'oggetto StringList ordinato
	 */
	public void sort() {
		Collections.sort(this);
	}

	/**
	 * Reimposta la lista delle stringhe con una operazione di split sul parametro di input.
	 * 
	 * @param input
	 *          La stringa da suddividere.
	 */
	public void split(String input) {
		split(input, null, false);
	}

	/**
	 * Reimposta la lista delle stringhe con una operazione di split sul parametro di input.
	 * 
	 * @param input
	 *          La stringa da suddividere.
	 * @param preserveAllTokens
	 *          Indica se effettuare lo split mantenendo tutti i valori o escludendo quelli nulli
	 */
	public void split(String input, boolean preserveAllTokens) {
		split(input, null, preserveAllTokens);
	}

	/**
	 * Restituisce una stringa che contiene tutte quelle presenti nella lista, delimitandole con una virgola.
	 * 
	 * @return la lista delimitata da virgole
	 */
	public String join() {
		return join((String) null);
	}


	/**
	 * Reimposta la lista delle stringhe con una operazione di split sul parametro di input usando il delimiter come delimitatore delle stringhe.
	 * 
	 * @param input
	 *          La stringa da suddividere.
	 * @param separator
	 *          Delimitatore delle stringhe
	 */
	public void split(String input, String separator) {
		split(input, separator, false);
	}

	/**
	 * Reimposta la lista delle stringhe con una operazione di split sul parametro di input usando il delimiter come delimitatore delle stringhe.
	 * 
	 * @param input
	 *          La stringa da suddividere.
	 * @param separator
	 *          Delimitatore delle stringhe
	 * @param preserveAllTokens
	 *          Indica se effettuare lo split mantenendo tutti i valori o escludendo quelli nulli
	 */
	public void split(String input, String separator, boolean preserveAllTokens) {
		if (StringUtils.isEmpty(separator)) {
			separator = ",";
		}
		if (StringUtils.isNotEmpty(input)) {
			String[] splitted = null;
			if (preserveAllTokens) {
				splitted = StringUtils.splitPreserveAllTokens(input, separator);
			} else {
				splitted = StringUtils.split(input, separator);
			}
			addAll(Arrays.asList(splitted));
		}
	}

	/**
	 * Restituisce una stringa che contiene tutte quelle presenti nella lista, delimitandole con il parametro delimiter.
	 * 
	 * @param separator
	 *          Delimitatore delle stringhe
	 * @return la lista delimitata
	 */
	public String join(String separator) {
		if (StringUtils.isEmpty(separator)) {
			separator = ",";
		}
		return StringUtils.join(toStringArray(), separator);
	}

	/* ----------------------------------------------------------------------- */
	/* - Metodi statici di utilità per la classe - */
	/* ----------------------------------------------------------------------- */

	/**
	 * Aggiunge a questo oggetto tutti gli elementi contenuti nell'array.
	 * 
	 * @param list
	 *          La lista da riempire.
	 * @param all
	 *          Array contenente tutte le stringhe da aggiungere alla lista.
	 */
	public static final void addAll(StringList list, String[] all) {
		if (StringList.isNotEmpty(list)) {
			list.addAll(all);
		}
	}

	/**
	 * Restituisce una stringa che contiene tutte quelle presenti nella lista, delimitandole con il parametro delimiter.
	 * 
	 * @param list
	 *          La lista da concatenare.
	 * @param separator
	 *          Delimitatore delle stringhe.
	 * 
	 * @return la lista delimitata.
	 */
	public static final String join(StringList list, String separator) {
		if (StringList.isEmpty(list)) {
			return "";
		}
		return list.join(separator);
	}

	/**
	 * Restituisce una stringa che contiene tutte quelle presenti nella lista, delimitandole con una virgola.
	 * 
	 * @param list
	 *          La lista da concatenare.
	 * 
	 * @return la lista delimitata da virgole.
	 */
	public final static String join(StringList list) {
		if (StringList.isEmpty(list)) {
			return "";
		}
		return list.join((String) null);
	}

	/**
	 * Reimposta la lista delle stringhe con una operazione di split sul parametro di input.
	 * 
	 * @param list
	 *          La lista da riempire.
	 * @param input
	 *          La stringa da suddividere.
	 */
	public static final void split(StringList list, String input) {
		if (StringList.isNotEmpty(list)) {
			list.split(input, null, false);
		}

	}

	/**
	 * Reimposta la lista delle stringhe con una operazione di split sul parametro di input.
	 * 
	 * @param list
	 *          La lista da riempire.
	 * @param input
	 *          La stringa da suddividere.
	 * @param preserveAllTokens
	 *          Indica se effettuare lo split mantenendo tutti i valori o escludendo quelli nulli.
	 */
	public static final void split(StringList list, String input, boolean preserveAllTokens) {
		if (StringList.isNotEmpty(list)) {
			list.split(input, null, preserveAllTokens);
		}

	}

	/**
	 * Reimposta la lista delle stringhe con una operazione di split sul parametro di input usando il delimiter come delimitatore delle stringhe.
	 * 
	 * @param list
	 *          La lista da riempire
	 * @param input
	 *          La stringa da suddividere.
	 * @param separator
	 *          Delimitatore delle stringhe
	 */
	public static final void split(StringList list, String input, String separator) {
		if (StringList.isNotEmpty(list)) {
			list.split(input, separator, false);
		}

	}

	/**
	 * Reimposta la lista delle stringhe con una operazione di split sul parametro di input usando il delimiter come delimitatore delle stringhe.
	 * 
	 * @param list
	 *          La lista da riempire
	 * @param input
	 *          La stringa da suddividere.
	 * @param separator
	 *          Delimitatore delle stringhe
	 * @param preserveAllTokens
	 *          Indica se effettuare lo split mantenendo tutti i valori o escludendo quelli nulli
	 */
	public final static void split(StringList list, String input, String separator, boolean preserveAllTokens) {
		if (StringList.isNotEmpty(list)) {
			list.split(input, separator, preserveAllTokens);
		}
	}

	/**
	 * Metodo di utilità per determinare se una lista è piena. Il metodo non entra nel merito di controllare se i valori sono tutti diversi da
	 * null.
	 * 
	 * @param list
	 *          La lista da controllare
	 * @return true se la lista contiene almeno un elemento, false in caso contrario.
	 */
	public static final boolean isNotEmpty(StringList list) {
		return (!StringList.isEmpty(list));
	}

	/**
	 * Metodo di utilità per determinare se una lista è vuota. Il metodo non entra nel merito di controllare se i valori sono tutti null.
	 * 
	 * @param list
	 *          La lista da controllare
	 * @return true se la lista non contiene nessun elemento, false in caso contrario.
	 */
	public static final boolean isEmpty(StringList list) {
		return ((list == null) || list.isEmpty());
	}
	
	/** Restituisce una lista di valori distinti dalla lista 
	 * @return */
	public Set<String> toHashSet() {
		Set<String> list = new HashSet<String>();
		list.addAll(this);
		return list;		
	}
	
	/**Uppercase della lista 
	 * @return */
	public StringList toUpperCase() {
		StringList ret = new StringList();
		for (String val : this) {
			if (val==null) {
				continue;
			}
			if (val.length()==0) {
				continue;
			}
			ret.add(val.toUpperCase());
		}
		return ret;
	}
	
	/** Restituisce una StringList contenente solo i valori distinti, diversi da null e stringa vuota
	 * Accetta la stringa vuota di lunghezza maggiore di 0 come valore 
	 * @return */
	
	public StringList getRealValues() {
		return new StringList(this.toSet());
	}
	
	
	/** Converte una la lista di String(StringList) in una lista di Integer(IntegerList) 
	 * @return 
	 * @throws NumberFormatException */
	public IntegerList toIntegerList() throws NumberFormatException {
		IntegerList ret = new IntegerList();

		for (String val : this) {
			if (StringUtils.isEmpty(val)) {
				continue;
			}
			ret.add(Integer.parseInt(val));
		}
		return ret;
	}
	
	/**Return true se collezione contiene valori
	 * @return */
	public synchronized boolean isNotEmpty() {
		return !this.isEmpty();
	}
}

