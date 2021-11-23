package com.rob.core.exceptions;

/**
 * Eccezione che viene lanciata dal QueryFactory ed i suoi discendenti quando il
 * dbms al quale si puna è di un tipo sconosciuto.
 */
public class UnsupportedDatabaseException extends RuntimeException {

	/**
   * Il compilatore si lamenta se questo non c'è...
   */
	private static final long serialVersionUID = -460136088656997531L;

	/**
   * Costruttore di default.
   */
	public UnsupportedDatabaseException() {
		super();
	}

	/**
   * Costruttore con messaggio.
   * 
   * @param message Il messaggio trasportato dalla eccezione.
   */
	public UnsupportedDatabaseException(String message) {
		super(message);
	}

	/**
   * Costruttore con messaggio e origine.
   * 
   * @param message Il messaggio trasportato dalla eccezione.
   * @param origin L'eccezione di origine che ha causato il lancio di questa.
   */
	public UnsupportedDatabaseException(String message, Throwable origin) {
		super(message, origin);
	}

	/**
   * Costruttore con origine.
   * 
   * @param origin L'eccezione di origine che ha causato il lancio di questa.
   */
	public UnsupportedDatabaseException(Throwable origin) {
		super(origin);
	}

}
