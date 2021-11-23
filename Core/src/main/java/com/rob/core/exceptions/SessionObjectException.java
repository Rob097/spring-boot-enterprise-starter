package com.rob.core.exceptions;

/**
 * SessionObjectException rappresenta la classe padre di tutte le eccezione
 * sollevate dalle classi che ereditano da SessionObject.
 */
public class SessionObjectException extends RuntimeException {

	/** Richiesto per la serializzazione */
	private static final long serialVersionUID = -2411328119175262044L;

	/**
   * Costruttore della classe, inizializza il messaggio dell'eccezione
   * impostando il testo del messaggio
   * 
   * @param mess Il messaggio associato a questa eccezione
   */
	public SessionObjectException(String mess) {
		super(mess);
	}
}
