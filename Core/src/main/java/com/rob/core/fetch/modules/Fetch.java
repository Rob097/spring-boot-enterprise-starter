package com.rob.core.fetch.modules;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;


/**
 * Questa classe serve da collettore di criteri di fetch da applicare ad una ricerca.
 * I criteri di fetch sono rappresentati come delle semplici {@link Option} che il {@link FetchHandler}
 * deve essere in grado di riconoscere.
 * 
 * Le {@link Option}, nella loro forma base, sono delle semplici chiave di tipo stringa 
 * e difatti le opzioni possibili sono definite come constanti nelle implementazioni del {@link FetchHandler}
 * 
 * Tuttavia, in caso di necessità più particolari, le options possono avere delle customizzazioni che ne determineranno il comportamento.
 * Nello specifico le opzioni, oltre alla loro chiave, prevedo altri due attributi
 * 
 * <ul>
 * 	<li>strategy - Permette di definire la strategia da utilizzare per accedere ai dati relazionati</li>
 *  <li>mapping - Permette di definire la completezza del mapping dei dati del dato relazionato</li>
 * </ul>
 * 
 * Entrambi gli attributi prevedono un valore di default che sarà implementato nella maniera opportuna nel {@link FetchHandler} 
 * 
 * Le chiavi delle option in questa classe vengono interpretate come gerarchie separate da un punto, pertanto aggiungere un opzione del tipo
 * a.b.c.d farà si che risultino attivate le seguenti opzioni
 * <ul>
 * <li>a</li>
 * <li>a.b</li>
 * <li>a.b.c</li>
 * <li>a.b.c.d</li>
 * </ul>
 *  
 * FetchHandler
 *
 */
@SuppressWarnings("unchecked")
public class Fetch implements Serializable {
	
	/**
	 * Strategia di fetch
	 *
	 */
	public enum Strategy {
		/**
		 * La strategia di default
		 */
		DEFAULT,
		/**
		 * La strategia di fetch tramite join
		 */
		JOIN,
		/**
		 * La strategia di fetch tramite uso dei repository
		 */
		REPOSITORY
	}
	
	/**Il tipo di mapping dei dati
	 *
	 */
	public enum Mapping {
		/**
		 * Il mapping di default (nessun vincolo particolare sul risultato richiesto)
		 */
		DEFAULT,
		/**
		 * Il mapping completo dei dati (il chiamante richiede tutti gli attributi della relazione)
		 * Se l'handler non gestisse l'accesso "completo", il mapping verrebbe rifiutato
		 */
		FULL,
		/**
		 * Il mapping minimo dei dati (il chiamante richiede solo gli attributi minimi della relazione, NON tutti)
		 * Se l'handler non gestisse l'accesso "ridotto", il mapping verrebbe rifiutato
		 */
		MINIMAL
	}
	
	/**
	 * Opzione di fetch
	 *
	 */
	public static class Option implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private String key;
		private Strategy strategy;
		private Mapping mapping;
		private Map<String,Object> metadata;
		
		/**
		 * Equivale a {@link #Option(String, Strategy, Mapping, Map.Entry[])} passando null per strategy, mapping e metadata
		 * @param key
		 */
		protected Option(String key) {
			this(key, null, null, (Map.Entry<String,Object>[])null);
		}
		
		/**
		 * Equivale a {@link #Option(String, Strategy, Mapping, Map.Entry[])} passando null per strategy, mapping
		 * @param key
		 * @param metadata
		 */
		protected Option(String key, Map.Entry<String,Object>...metadata) {
			this(key, null, null, metadata);
		}
		
		/**
		 * Equivale a {@link #Option(String, Strategy, Mapping, Map.Entry[])} passando null per mapping e metadata
		 * @param key
		 * @param strategy
		 */
		protected Option(String key, Strategy strategy) {
			this(key, strategy, null, (Map.Entry<String,Object>[])null);
		}
		
		/**
		 * Equivale a {@link #Option(String, Strategy, Mapping, Map.Entry[])} passando null per mapping
		 * @param key
		 * @param strategy
		 * @param metadata
		 */
		protected Option(String key, Strategy strategy, Map.Entry<String,Object>...metadata) {
			this(key, strategy, null, metadata);
		}
		
		/**
		 * Equivale a {@link #Option(String, Strategy, Mapping, Map.Entry[])} passando null per strategy e metadata
		 * @param key
		 * @param mapping
		 */
		protected Option(String key, Mapping mapping) {
			this(key, null, mapping, (Map.Entry<String,Object>[])null);
		}
		
		/**
		 * Equivale a {@link #Option(String, Strategy, Mapping, Map.Entry[])} passando null per strategy
		 * @param key
		 * @param mapping
		 * @param metadata
		 */
		protected Option(String key, Mapping mapping, Map.Entry<String,Object>...metadata) {
			this(key, null, mapping, metadata);
		}
		
		/**
		 * Equivale a {@link #Option(String, Strategy, Mapping, Map.Entry[])} passando null per metadata
		 * @param key
		 * @param strategy
		 * @param mapping
		 */
		protected Option(String key, Strategy strategy, Mapping mapping) {
			this(key, strategy, mapping, (Map.Entry<String,Object>[])null);
		}
		
		/**
		 * Crea una option con la chiave, la strategy ed il mapping specificati
		 * Se strategy e/o mapping sono null vengono applicati i valori di default
		 * {@link Strategy#DEFAULT} e {@link Mapping#DEFAULT}.
		 * 
		 * E' possibile inoltre fornire un elenco di coppie chiave/valore per permettere customizzazioni all'implementazione del {@link FetchHandler}
		 *  
		 * @param key
		 * @param strategy
		 * @param mapping
		 * @param metadata
		 * @throws IllegalArgumentException se key è null/blank o termina con un punto
		 */
		protected Option(String key, Strategy strategy, Mapping mapping, Map.Entry<String,Object>...metadata) {
			if (StringUtils.isBlank(key)) {
				throw new IllegalArgumentException("key must not be blank");
			}
			if (key.endsWith(".")) {
				throw new IllegalArgumentException("key cannot ends with a dot");
			}
			this.key = key;
			if (strategy == null) {
				this.strategy = Strategy.DEFAULT;
			}else {
				this.strategy = strategy;
			}
			
			if (mapping == null) {
				this.mapping = Mapping.DEFAULT;
			}else {
				this.mapping = mapping;
			}
			
			
			if (metadata != null) {
				Map<String,Object> meta = new HashMap<>();
				for(Map.Entry<String,Object> m : metadata) {
					meta.put(m.getKey(), m.getValue());
				}
				this.metadata = Collections.unmodifiableMap(meta);
			}else {
				this.metadata = Collections.emptyMap();
			}
		}
		
		@SuppressWarnings("unchecked")
		protected Option(String key, Strategy strategy, Mapping mapping, Set<Map.Entry<String,Object>> metadata) {
			this(key, strategy, mapping, (metadata != null ? metadata.toArray(new Map.Entry[metadata.size()]) : null));
		}
		
		public String getKey() {
			return key;
		}

		public Strategy getStrategy() {
			return strategy;
		}

		public Mapping getMapping() {
			return mapping;
		}
		
		public Map<String,Object> getMetadata() {
			return metadata;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((mapping == null) ? 0 : mapping.hashCode());
			result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
			result = prime * result + ((strategy == null) ? 0 : strategy.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Option other = (Option) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (mapping != other.mapping)
				return false;
			if (metadata == null) {
				if (other.metadata != null)
					return false;
			} else if (!metadata.equals(other.metadata))
				return false;
            return strategy == other.strategy;
        }

		@Override
		public String toString() {
			return "{key:"+key+", strategy:"+strategy+", mapping:"+mapping+", metadata:"+metadata+"}";
		}
	}
	private static final long serialVersionUID = 1L;
	
	private Map<String, Option> options;
	
	protected Fetch(Set<Option> options) {
		this.options = options.stream().collect(Collectors.toMap(o -> o.getKey(), o -> o));
	}
	
	/**
	 * Torna true se questa fetch contiene l'opzione specificata.
	 * Formalmente, torna true se e solo se questa fetch contiene la Option x tale che x.getKey().startsWith(key).
	 * 
	 * @param key
	 * @return
	 */
	public boolean hasOption(String key) {
		if (options.containsKey(key)) {
			return true;
		}
		return options.keySet().stream().anyMatch((v) -> v.startsWith(key.concat(".")));
	}

	public boolean hasOption(String key, Strategy strategy) {
		Option opt = getOption(key);
		if (opt == null){
			return false;
		}

		return opt.getStrategy() == strategy;
	}
	
	public Option getOption(String key) {
		Option opt = options.get(key);
		if (opt == null){
			boolean createFromChild = options.keySet().stream().filter((v) -> v.startsWith(key)).findFirst().isPresent();
			if (createFromChild){
				opt = new Option(key);
			}
		}
		return opt;
	}
	
	/**
	 * Torna una copia del set di tutte le key delle opzioni esplicitamente aggiunte in questa fetch
	 * @return
	 */
	public Set<String> getOptionKeys() {
		return new HashSet<>(options.keySet());
	}
	
	/**
	 * Torna un copia del set di tutte le opzioni esplicitamente aggiunte in questa fetch
	 * @return
	 */
	public Set<Option> getOptions() {
		return new HashSet<>(options.values());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Fetch)) {
			return false;
		}
		Fetch that = (Fetch)obj;
		
		return this.options.equals(that.options);
	}
	
	@Override
	public int hashCode() {
		return this.options.hashCode();
	}

}
