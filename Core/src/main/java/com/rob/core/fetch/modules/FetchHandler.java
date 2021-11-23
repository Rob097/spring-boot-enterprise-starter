package com.rob.core.fetch.modules;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.rob.core.fetch.modules.Fetch.Mapping;
import com.rob.core.fetch.modules.Fetch.Strategy;

/**
 * @author Roberto97
 * Interfaccia per la gestione dei criteri di fetch dei dati
 * @param <T> 
 */
public interface FetchHandler<T> {
	/**
	 * Questo metodo applica le logiche di fetch dei dati, ovvero della profondità del
	 * dato che si desidera estrarre dal database.
	 * Possono essere applicate due modalità differenti per scaricare/mappare i dati
	 * <ul>
	 * 	<li>Tramite join</li>
	 *  <li>Tramite repository</li>
	 * </ul>
	 * Le fetch tramite join necessitano di un {@link ResultSet} popolato con i dati della/e tabella/e in join,
	 * mentre le fetch tramite repository faranno ricerche puntuali sul repository dell'entità figlia.
	 * 
	 * E' possibile gestire configurazioni ibride di join e repository e su livelli multipli di profondità.
	 * 
	 * @param entity L'entità root
	 * @param fetch I criteri di fetch
	 * @param resultSet Il resultSet con i dati in join (necessario se si applicano fetch in modalità join)
	 * @return T L'entità root arricchita con i dati relazionati
	 * @throws SQLException
	 */
	default T handle(T entity, Fetch fetch, ResultSet resultSet) throws SQLException {
		return handle(entity, fetch, resultSet, null);
	}
	
	/**
	 * Questo metodo applica le logiche di fetch dei dati, ovvero della profondità del
	 * dato che si desidera estrarre dal database.
	 * Possono essere applicate due modalità differenti per scaricare/mappare i dati
	 * <ul>
	 * 	<li>Tramite join</li>
	 *  <li>Tramite repository</li>
	 * </ul>
	 * Le fetch tramite join necessitano di un {@link ResultSet} popolato con i dati della/e tabella/e in join,
	 * mentre le fetch tramite repository faranno ricerche puntuali sul repository dell'entità figlia.
	 * 
	 * E' possibile gestire configurazioni ibride di join e repository e su livelli multipli di profondità.
	 * 
	 * Questo metodo, a differenza di {@link #handle(Object, Fetch, ResultSet)} è in grado di gestire anche le fetch di
	 * entità figlie con cardinalità multipla (1..N).
	 * Ad esempio, considerando una entità foo correlata ad n entita bar, richiedendo le fetch delle entità bar
	 * tramite join, la select produrrà inevitabilmente un prodotto cartesiano facendo si che questo metodo potrebbe
	 * essere invocato più volte per la stessa entità foo.
	 * 
	 * La mappa, che DEVE essere instanziata al di fuori del ciclo di lettura del resultSet, serve per gestire l'univocità
	 * dell'entità padre (in questo caso foo), e <b>restituirà null qualora la stessa entità risultasse già gestita almeno una
	 * volta</b> 
	 * 
	 * Questo significa che per produrre una lista di foo con all'interno i suoi bar, il metodo chiamante non deve aggiungere
	 * alla lista dei risultati l'output di questo metodo nel caso in cui torni null
	 * 
	 * @param entity L'entità root
	 * @param fetch I criteri di fetch
	 * @param resultSet Il resultSet con i dati in join (necessario se si applicano fetch in modalità join)
	 * @param groupingMap La mappa di appoggio per gestire correttamente il prodotto cartesiano in caso di join
	 * @return T L'entità root arricchita con i dati relazionati. Null se l'entità è una ripetizione frutto di un prodotto cartesiano
	 * @throws SQLException
	 */
    T handle(T entity, Fetch fetch, ResultSet resultSet, Map<String, T> groupingMap) throws SQLException;
	
	boolean supportKey(Fetch.Option option);
	boolean supportStrategy(Fetch.Option option);
	boolean supportMapping(Fetch.Option option);
	
	default boolean support(Fetch.Option option) {
		return supportKey(option) && 
				(option.getStrategy() == Strategy.DEFAULT || supportStrategy(option)) && 
				(option.getMapping() == Mapping.DEFAULT || supportMapping(option));
	}
	
	default void validate(Fetch.Option option) {
		if (option == null) {
			throw new IllegalArgumentException("Option argument cannot be null");
		}
		
		if (!supportKey(option)) {
			throw new RuntimeException("Chiave di fetch non supportata "+option.toString());
		}
		
		if (option.getStrategy() != Fetch.Strategy.DEFAULT && !supportStrategy(option)) {
			throw new RuntimeException("Strategia di fetch non supportata "+option.toString());
		}
		
		if (option.getMapping() != Fetch.Mapping.DEFAULT && !supportMapping(option)) {
			throw new RuntimeException("Fetch mapping non supportato "+option.toString());
		}

		validateMetadata(option);
	}

	default void validateMetadata(Fetch.Option option){

	}
}
