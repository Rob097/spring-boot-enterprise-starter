package com.rob.core.utils.java;

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
 * 
 * <p>
 * Title: IntegerList
 * </p>
 * 
 * <p>
 * Description: Vettore tipizzato di Integer.
 * </p>
 * 
 */
public class IntegerList extends Vector<Integer> {

	/** Richiesto per la serializzazione */
	private static final long serialVersionUID = 1585040687146641252L;

	/**
	 * Genera una nuova integerList ed inizializza il primo valore
	 * 
	 * @param initialValue
	 */
	public IntegerList(int initialValue) {
		super();
		this.add(initialValue);
	}

	/**
	 * Inizializza il vettore vuoto
	 */
	public IntegerList() {
		super();
	}

	/**
	 * Costruisce un vettore contenente gli elementi specificati dalla collezione,
	 * nell'ordine in cui sono restituiti dall'iteratore della collezione.
	 * 
	 * @param in La collezione degli elementi che devono esser epiazzati in questo
	 *           vettore.
	 */
	public IntegerList(Vector<Integer> in) {
		super(in);
	}

	/**
	 * Converte una stringlist in IntegerList
	 * 
	 * @param in
	 */
	public IntegerList(StringList in) {
		super();

		if (in == null || in.size() == 0)
			return;

		for (int loop = 0; loop < in.size(); loop++) {
			this.add(Integer.valueOf(in.get(loop)));
		}
	}

	/**
	 * Inizializza la lista tipizzata di oggetti partendo da un array di oggetti
	 * Integer.
	 * 
	 * @param in Integer[] vettore di Integer
	 */
	public IntegerList(Integer[] in) {
		addAll(in);
	}

	public IntegerList(String in) {
		split(in, ",", true);
	}

	public IntegerList(String in, String separator) {
		split(in, separator, true);
	}

	public IntegerList(Set<Integer> in) {
		if (in == null) {
			in = new HashSet<Integer>();
		}
		addAll(in);
	}

	public IntegerList(List<Integer> in) {
		if (in == null) {
			in = new ArrayList<Integer>();
		}
		addAll(in);
	}

	/**
	 * Aggiunge a questo oggetto tutti gli elementi contenuti nell'array.
	 * 
	 * @param all Array contenente tutte gli Integer da aggiungere alla lista
	 */
	public void addAll(Integer[] all) {
		if (!ArrayUtils.isEmpty(all)) {
			addAll(Arrays.asList(all));
		}
	}

	/**
	 * Ordina la lista di elementi
	 */
	public void sort() {
		Collections.sort(this);
	}

	/**
	 * Metodo di utilità per determinare se una lista è piena. Il metodo non entra
	 * nel merito di controllare se i valori sono tutti diversi da null.
	 * 
	 * @param list La lista da controllare
	 * @return true se la lista contiene almeno un elemento, false in caso
	 *         contrario.
	 */
	public static final boolean isNotEmpty(IntegerList list) {
		return (!isEmpty(list));
	}

	/**
	 * Metodo di utilità per determinare se una lista è vuota. Il metodo non entra
	 * nel merito di controllare se i valori sono tutti null.
	 * 
	 * @param list La lista da controllare
	 * @return true se la lista non contiene nessun elemento, false in caso
	 *         contrario.
	 */
	public static final boolean isEmpty(IntegerList list) {
		return ((list == null) || list.isEmpty());
	}

	/**
	 * Reimposta la lista delle stringhe con una operazione di split sul parametro
	 * di input usando il delimiter come delimitatore delle stringhe.
	 * 
	 * @param input             La stringa da suddividere.
	 * @param separator         Delimitatore delle stringhe
	 * @param preserveAllTokens Indica se effettuare lo split mantenendo tutti i
	 *                          valori o escludendo quelli nulli
	 */
	private void split(String input, String separator, boolean preserveAllTokens) {
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
			// convert to integer
			Integer[] il = new Integer[splitted.length];
			for (int i = 0; i < splitted.length; i++)
				il[i] = Integer.parseInt(splitted[i]);

			addAll(Arrays.asList(il));
		}
	}

	/**
	 * Restituisce una stringa che contiene tutte quelle presenti nella lista,
	 * delimitandole con una virgola.
	 * 
	 * @return la lista delimitata da virgole
	 */
	public String join() {
		return join((String) null);
	}

	/**
	 * Restituisce una stringa che contiene tutte quelle presenti nella lista,
	 * delimitandole con il parametro delimiter.
	 * 
	 * @param separator Delimitatore delle stringhe
	 * @return la lista delimitata
	 */
	public String join(String separator) {
		if (StringUtils.isEmpty(separator)) {
			separator = ",";
		}
		return StringUtils.join(toIntegerArray(), separator);
	}

	/**
	 * Restituisce un array di String
	 * 
	 * @return String[] array di String
	 */
	public Integer[] toIntegerArray() {
		return toArray(new Integer[size()]);
	}

	/**
	 * Restituisce una lista di valori distinti dalla lista
	 * 
	 * @return
	 */
	public Set<Integer> toHashSet() {
		Set<Integer> list = new HashSet<Integer>();
		list.addAll(this);
		return list;
	}

	/**
	 * Restituisce una IntegerList contenente solo i valori diversi da null ed unici
	 * 
	 * @return
	 */
	public IntegerList getDistinctValues() {
		Set<Integer> ret = new HashSet<Integer>();
		for (Integer val : this) {
			if (val == null) {
				continue;
			}
			ret.add(val);
		}
		return new IntegerList(ret);
	}

	/**
	 * Restituisce una IntegerList contenente solo i valori diversi da null
	 * 
	 * @return
	 */
	public IntegerList getRealValues() {
		Set<Integer> ret = new HashSet<>();
		for (Integer val : this) {
			if (val == null) {
				continue;
			}
			ret.add(val);
		}
		return new IntegerList(ret);
	}

	/**
	 * Converte una la lista di Integer(IntegerList) in una lista di String
	 * (StringList)
	 * 
	 * @return
	 */
	public StringList toStringList() {
		StringList ret = new StringList();

		for (Integer val : this) {
			if (val == null) {
				continue;
			}
			ret.add(String.valueOf(val));
		}
		return ret;
	}
}
