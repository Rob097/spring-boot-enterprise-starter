package com.rob.core.fetch.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.rob.core.fetch.modules.Fetch.Mapping;
import com.rob.core.fetch.modules.Fetch.Option;
import com.rob.core.fetch.modules.Fetch.Strategy;

/**
 * Fluent api per costruire i criteri di fetch
 */
public class FetchBuilder {
	public class OptionBuilder {
		private final String key;
		private Strategy strategy;
		private Mapping mapping;
		private Map<String, Object> meta = new HashMap<>();

		private OptionBuilder(String key) {
			this.key = key;
		}

		public OptionBuilder usingJoin() {
			this.strategy = Strategy.JOIN;
			return this;
		}

		public OptionBuilder usingRepository() {
			this.strategy = Strategy.REPOSITORY;
			return this;
		}

		public OptionBuilder mapMinimalData() {
			this.mapping = Mapping.MINIMAL;
			return this;
		}

		public OptionBuilder mapFullData() {
			this.mapping = Mapping.FULL;
			return this;
		}

		public OptionBuilder putMetadata(Map.Entry<String, Object> metadata) {
			meta.put(metadata.getKey(), metadata.getValue());
			return this;
		}

		public FetchBuilder add() {
			return FetchBuilder.this.addOption(new Option(key, strategy, mapping, meta.entrySet()));
		}

		public Fetch build() {
			return FetchBuilder.this.addOption(new Option(key, strategy, mapping, meta.entrySet())).build();
		}

	}

	private static Fetch none = null;

	private Fetch fetch;
	private Set<Option> options = new HashSet<>();

	public FetchBuilder() {
	}

	public FetchBuilder(Fetch fetch) {
		addAll(fetch);
	}

	/**
	 * Crea una opzione di fetch, senza aggiungerla all'elenco delle opzioni di
	 * questa FecthBuilder. Restituisce l'{@link OptionBuilder} per permettere altre
	 * modifiche all'opzione e per aggiungere tale opzione alla fetch al termine
	 * delle modifiche.
	 * 
	 * @param key La chiave dell'opzione da aggiungere
	 * @throws {@link IllegalArgumentException} se key è null/blank o termina con un
	 *                punto
	 * @return {@link OptionBuilder}
	 */
	public OptionBuilder option(String key) {
		fetch = null;
		return new OptionBuilder(key);
	}

	/**
	 * Aggiunge una opzione di fetch
	 * 
	 * @param key La chiave dell'opzione da aggiungere
	 * @throws {@link IllegalArgumentException} se key è null/blank o termina con un
	 *                punto
	 * @return true se questa fetch non conteneva già l'opzione specificata
	 */
	public FetchBuilder addOption(String key) {
		fetch = null;
		return new OptionBuilder(key).add();
	}

	public FetchBuilder addOption(Option option) {
		fetch = null;
		if (option == null) {
			throw new IllegalArgumentException("option must not be null");
		}
		options.add(option);

		return this;
	}

	public FetchBuilder addAll(Fetch fetch) {
		return addAll(fetch.getOptions());
	}

	protected FetchBuilder addAll(Option... options) {
		fetch = null;
		for (Option option : options) {
			addOption(option);
		}
		return this;

	}

	protected FetchBuilder addAll(Set<Option> options) {
		fetch = null;
		this.options.addAll(options);
		return this;
	}

	public FetchBuilder removeOption(String key) {
		fetch = null;
		options.removeIf(item -> item.getKey().equals(key));
		return this;
	}

	public FetchBuilder removeOption(Option option) {
		fetch = null;
		options.remove(option);
		return this;
	}

	/**
	 * Ritorna una nuova fetch non modificabile e priva di opzioni
	 * 
	 * @return
	 */
	public static Fetch none() {
		if (none == null) {
			none = new Fetch(Collections.emptySet());
		}
		return none;
	}

	/**
	 * Ritorna una nuova fetch con tutte le opzioni specificate
	 * 
	 * @param keys
	 * @return
	 */
	public static Fetch all(String... keys) {
		Set<Option> options = new HashSet<>();
		for (String key : keys) {
			options.add(new Option(key));
		}
		return new Fetch(options);
	}

	/**
	 * Ritorna una nuova fetch con tutte le opzioni specificate
	 * 
	 * @param keys
	 * @return
	 */
	public static Fetch all(Set<String> keys) {
		Set<Option> options = new HashSet<>();
		for (String key : keys) {
			options.add(new Option(key));
		}
		return new Fetch(options);
	}

	/**
	 * Ritorna una nuova fetch con tutte le opzioni specificate
	 * 
	 * @param options
	 * @return
	 */
	public static Fetch all(Option... options) {
		Set<Option> opts = new HashSet<>();
		for (Option opt : options) {
			opts.add(opt);
		}
		return new Fetch(opts);
	}

	/**
	 * Crea un nuovo oggetto fetch applicabile per entità figlie. Il metodo filtra
	 * il set delle opzioni (o) incluse in parent fetch tale che
	 * o.startsWith(key+"."), e poi le rimappa in o.substring(key.length + 1))
	 * 
	 * Ad esempio se la parent fetch contiene queste opzioni:
	 * <ul>
	 * <li>a.b.c</li>
	 * <li>a.b.d</li>
	 * <li>x.y</li>
	 * <li>x.z</li>
	 * </ul>
	 * 
	 * child("a") restituirà "b.c" e "b.d"<br/>
	 * child("x") restituirà "y" e "z"<br/>
	 * child("a.b") resituirà "c" e "d"
	 * 
	 * @param parent
	 * 
	 * @param key
	 * @return
	 */
	public static Fetch child(Fetch parent, String key) {
		if (parent == null) {
			return FetchBuilder.none();
		}
		Set<Option> childOptions = parent.getOptions().stream().filter((v) -> v.getKey().startsWith(key + "."))
				.map((v) -> new Option(v.getKey().substring(key.length() + 1), v.getStrategy(), v.getMapping(),
						v.getMetadata().entrySet()))
				.collect(Collectors.toSet());
		if (childOptions.isEmpty()) {
			return FetchBuilder.none();
		}
		return new Fetch(childOptions);
	}

	public Fetch build() {
		if (options == null || options.isEmpty()) {
			return none();
		}
		if (fetch == null) {
			fetch = new Fetch(options);
		}
		return fetch;
	}
}
