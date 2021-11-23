package com.rob.uiapi.utils;

import org.springframework.http.ResponseEntity;

import com.rob.uiapi.controllers.views.IView;
import com.rob.core.utils.java.messages.MessageResource;
import com.rob.core.utils.java.messages.MessageResources;
import com.rob.core.utils.db.Range;


/**
 * @author Roberto97
 * Interface implemented by all RestServices
 * @param <R>
 * @param <Q>
 */
public interface UIApiRS<R, Q> {
	String SORT = "sort";
	String RANGE = "Range";
	String VIEW = "view";
	String PRINT = "print";
	String LANGUAGE = "language";

	/**
	 * 
	 * @param parameters Parametri di ricerca
	 * @param sort Ordinamento del risultato
	 * @param view Vista da applicare al risultato, che stabilisce quali informazioni dell'entità caricare. Valori di base previsti: ultraVerbose,verbose,normal,synthetic,ultraSynthetic
	 * @param range 
	 * @param Range Range di risultati richiesti, diviso in unità di misura e range di valori richiesto. Esempio:"item=0-1" per avere il primo risultato.
	 * @return
	 * @throws Exception
	 */
	ResponseEntity<MessageResources<R>> find(Q parameters, Sort sort, IView view, Range range) throws Exception;

	/**
	 * 
	 * @param id Identificativo entità
	 * @param parameters Parametri di ricerca
	 * @param view Vista da applicare al risultato, che stabilisce quali informazioni dell'entità caricare. Valori di base previsti: ultraVerbose,verbose,normal,synthetic,ultraSynthetic
	 * @return
	 * @throws Exception
	 */
	ResponseEntity<MessageResource<R>> get(String id, Q parameters, IView view) throws Exception;

	ResponseEntity<MessageResource<R>> create(MetadataResource<R> resource) throws Exception;

	ResponseEntity<MessageResource<R>> update(String id, MetadataResource<R> resource) throws Exception;

	ResponseEntity<MessageResources<R>> deleteAll(Q parameters, IView view) throws Exception;

	ResponseEntity<MessageResource<R>> delete(String id, Q parameters, IView view) throws Exception;
}
