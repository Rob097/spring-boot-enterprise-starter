package com.rob.uiapi.utils;

import java.net.URLDecoder;

public class Sort {
	private Field[] fields = new Field[0];

	/**
	 * Classe rappresentante un campo di ordinamento
	 * 
	 * @author u08446
	 *
	 */
	public class Field {
		/**
		 * Il nome del campo
		 */
		public String name;
		/**
		 * La direzione di ordinamento
		 */
		public boolean descending = false;
	}

	public Sort() {

	}

	/**
	 * Crea un oggetto sort parsando la stringa in ingresso.<br>
	 * Il sort può essere definito secondo questa sintassi: <br>
	 * <code>{+|-}{campo1}[,{+|-}{campo2}[,{+|-}{campoN}]]</code> <br>
	 * <br>
	 * es. +cognome,+nome,-dataNascita<br>
	 * equivale ad ordinare per cognome e nome in ordine crescente e per data di
	 * nascita in ordine descrescente
	 * 
	 * @param sort La stringa da parsare
	 */
	public Sort(String sort) {
		try {
			sort = URLDecoder.decode(sort, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String[] sorts = sort.split(",");
		fields = new Field[sorts.length];
		for (int i = 0; i < sorts.length; i++) {
			String s = sorts[i].trim();
			Field f = new Field();

			f.name = s.substring(1);

			if (s.startsWith("-")) {
				f.descending = true;
			} else if (s.startsWith("+")) {
				f.descending = false;
			} else {
				f.name = s;
			}
			fields[i] = f;
		}
	}

	/**
	 * L'elendo dei campi per cui ordinare in ordine di precedenza
	 * 
	 * @return L'elenco dei campi per cui ordinare
	 */
	public Field[] getFields() {
		return fields;
	}

	public void setFields(Field[] fields) {
		this.fields = fields;
	}

}
