package com.rob.core.utils.java;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;

/**
 * Listener attivato all'avvio dell'applicazione
 * */
public class PortfolioContextListener extends ContextListener {

	/** Oggetto utilizzato per il log su file */
	protected static Logger logger = org.slf4j.LoggerFactory.getLogger(SessionObject.class);
	
	/**
	 * Il metodo viene richiamato dal servlet container all'avvio del contesto della web application.
	 * 
	 * @param sce
	 *          Il contesto di esecuzione della servlet e tutte le informazioni correlate
	 * @throws RuntimeException
	 *           Se la creazione del contesto deve essere annullata
	 */
	public void contextInitialized(ServletContextEvent sce) {

		//StringBuffer mb = new StringBuffer();

		try {
			logger.error("Avvio del PortfolioContextListener iniziato.");
			
			// Inizializza il contesto della web application
			//mb = initContext(sce);
			initContext(sce);
			
			logger.error("Avvio del PortfolioContextListener concluso con successo.");
		} catch (Exception e) {
			logger.error("Errore nell'avvio del PortfolioContextListener");
		}
	}
	
}
