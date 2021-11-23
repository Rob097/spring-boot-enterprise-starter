package com.rob.core.utils.java;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;

import com.rob.core.utils.db.SqlDataSource;

/**
 * Listener del contesto del core.
 * 
 */
public class ContextListener implements ServletContextListener {

	/** Istanza del gestore di log per la classe */
	protected final static Logger logger = org.slf4j.LoggerFactory.getLogger(ContextListener.class);

	/**
	 * Inizializza il contesto dell'applicazione web.
	 * 
	 * @param sce
	 *          Il contesto di esecuzione della servlet e tutte le informazioni correlate
	 *          
	 * @return Il buffer del messaggio da inviare per mail al termine dell'operazione di inizializzazione
	 * @throws Exception
	 *           In caso di errore di inizializzazione
	 */
	protected StringBuffer initContext(ServletContextEvent sce) throws Exception {

		StringBuffer mb = new StringBuffer();

		/* Inizializzazione del gestore della configurazione
		Configuration.init(sce.getServletContext());
		

		logger.info("");
		logger.info("Avvio dell'applicazione " + Configuration.getAppId().toUpperCase() + "...");
		logger.info("");
		logger.info("Gestore della configurazione inizializzato");
		logger.info("");
		logger.info("Contesto inizializzato");
		logger.info("  Ambiente...........: " + Configuration.getAppEnvironment());
		logger.info("  Versione...........: " + Configuration.getAppVersion());
		logger.info("  Data compilazione..: " + Configuration.getBuildDatetime());
		logger.info("  Context Path..: " + Configuration.getContextPath());

		mb.append("Avvio dell'applicazione...");
		mb.append("\n");
		mb.append("\nGestore della configurazione inizializzato");
		mb.append("\n");
		mb.append("\nContesto inizializzato");
		mb.append("\n  Ambiente...........: ").append(Configuration.getAppEnvironment());
		mb.append("\n  Versione...........: ").append(Configuration.getAppVersion());
		mb.append("\n  Data compilazione..: ").append(Configuration.getBuildDatetime());
		mb.append("\n  Context Path..: ").append(Configuration.getContextPath());*/

		// Inizializzazione del pool di connessioni
		SqlDataSource.init();

		/*logger.info("");
		logger.info("Pool di connessioni inizializzato");
		logger.info("  Data Source JNDI..: " + Configuration.getJndiName());
		if (SqlDataSource.getInstance().getRDBMS() == SqlDataSource.DB_SQLSERVER) {
			logger.info("  DBMS..............: SQL Server");
		} else if (SqlDataSource.getInstance().getRDBMS() == SqlDataSource.DB_ORACLE){
			logger.info("  DBMS..............: Oracle");
		} else if (SqlDataSource.getInstance().getRDBMS() == SqlDataSource.DB_POSTGRESQL){
			logger.info("  DBMS..............: Postgres");
		}else{
			logger.info("  DBMS..............: ??");			
		}
		logger.info("  Nome DB...........: " + Configuration.getDatabaseName());
		logger.info("  Versione DB.......: " + Configuration.getDatabaseVersion());

		mb.append("\n");
		mb.append("\nPool di connessioni inizializzato");
		mb.append("\n  Data Source JNDI..: ").append(Configuration.getJndiName());
		if (SqlDataSource.getInstance().getRDBMS() == SqlDataSource.DB_SQLSERVER) {
			mb.append("\n  DBMS..............: SQL Server");
		} else if (SqlDataSource.getInstance().getRDBMS() == SqlDataSource.DB_ORACLE){
			mb.append("\n  DBMS..............: Oracle");
		} else if (SqlDataSource.getInstance().getRDBMS() == SqlDataSource.DB_POSTGRESQL){
			mb.append("\n  DBMS..............: Postgres");
		} else {
			mb.append("\n  DBMS..............: ??");
		}
		mb.append("\n  Nome DB...........: ").append(Configuration.getDatabaseName());
		mb.append("\n  Versione DB.......: ").append(Configuration.getDatabaseVersion());

		// Inizializza il gestore delle applicazioni
		mb.append(ApplicationHandler.init());
		
		// Inizializza il gestore dell'autenticazione
		mb.append(AuthenticationHandler.init());

		// Inizializza il gestore dei plugin
		mb.append(PluginHandler.init());
		
		//Inizializzo il gestore delle festivita'
		if (Configuration.getCorePropertyAsBoolean("datepicker.holidays.loadFromDb", false)) {
			mb.append(HolidaysHandler.init());			
		}*/

		return mb;
	}

	/**
	 * Chiude il contesto dell'applicazione web.
	 * 
	 * @return Il buffer del messaggio da inviare per mail al termine dell'operazione di chiusura
	 */
	protected StringBuffer destroyContext() {

		StringBuffer mb = new StringBuffer();

		/*logger.info("");
		logger.info("Interruzione dell'applicazione " + Configuration.getAppId().toUpperCase() + "...");*/

		mb.append("Interruzione dell'applicazione...");

		return mb;
	}

	/**
	 * Invia una mail di notifica di avvio terminato con successo.
	 * 
	 * @param mb
	 *          Il buffer del messaggio da inviare
	 */
	protected void notifyContextInitializedSuccess(StringBuffer mb) {

		/*logger.info("");
		logger.info("Avvio dell'applicazione " + Configuration.getAppId().toUpperCase() + " terminato con successo");
		logger.info("");

		mb.append("\n");
		mb.append("\nAvvio terminato con successo");

		// Invia una mail per segnalare l'avvio con successo dell'applicazione
		Mailer m = new Mailer(Configuration.getAppHomeSubItem("mailer.properties"), MailerNotificationContextEnum.systemEvent);
		try {
			m.send(Configuration.getMailSubject() + ": START SUCCESSFUL", mb.toString());
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.error("");
		}*/
	}

	/**
	 * Invia una mail di notifica di avvio fallito e solleva sempre una RuntimeException per comunicare al container
	 * l'annullamento della creazione del contesto.
	 * 
	 * @param mb
	 *          Il buffer del messaggio da inviare
	 * @param error
	 *          Il messaggio di errore che ha causato il fallimento
	 * @throws RuntimeException
	 *           Per comunicare al container l'annullamento della creazione del contesto
	 */
	protected void notifyContextInitializedFailure(StringBuffer mb, String error) {

		/*logger.error( "");
		logger.error( error);
		logger.error( "");
		logger.error( "Avvio dell'applicazione " + Configuration.getAppId().toUpperCase() + " fallito");
		logger.error( "");

		if (mb!=null) {
			mb.append("\n");
			mb.append("\n" + error);
			mb.append("\n");
			mb.append("\nAvvio dell'applicazione fallito");

			// Invia una mail per segnalare l'avvio fallito dell'applicazione
			Mailer m = new Mailer(Configuration.getAppHomeSubItem("mailer.properties"), MailerNotificationContextEnum.systemEvent);
			try {
				m.send(Configuration.getMailSubject() + ": START FAILURE", mb.toString());
			} catch (Exception ex1) {
				logger.error(ex1.getMessage());
				logger.error("");
			}
		}



		throw new RuntimeException();*/
	}

	/**
	 * Invia un messaggio di chiusura avvenuta con successo.
	 * 
	 * @param mb
	 *          Il buffer del messaggio da inviare
	 */
	protected void notifyContextDestroyed(StringBuffer mb) {

		/*logger.info("");
		logger.info("Interruzione dell'applicazione " + Configuration.getAppId().toUpperCase() + " terminata con successo");
		logger.info("");

		mb.append("\n");
		mb.append("\nInterruzione terminata con successo");

		// Invia una mail per segnalare il termine dell'applicazione
		Mailer m = new Mailer(Configuration.getAppHomeSubItem("mailer.properties"), MailerNotificationContextEnum.systemEvent);
		try {
			m.send(Configuration.getMailSubject() + ": STOP SUCCESSFUL", mb.toString());
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.error("");
		}*/
	}

	/**
	 * Il metodo viene richiamato dal servlet container all'avvio del contesto della web application.
	 * 
	 * @param sce
	 *          Il contesto di esecuzione della servlet e tutte le informazioni correlate
	 * @throws RuntimeException
	 *           Se la creazione del contesto deve essere annullata
	 */
	public void contextInitialized(ServletContextEvent sce) {

		StringBuffer mb = new StringBuffer();

		try {
			// Inizializza il contesto della web application
			mb = initContext(sce);

			// Invia una mail di notifica di avvio terminato con successo
			notifyContextInitializedSuccess(mb);

		} catch (Exception e) {
			e.printStackTrace();
			
			// Invia una mail di notifica di avvio fallito
			notifyContextInitializedFailure(mb, e.getMessage());
		}
	}

	/**
	 * Il metodo viene richiamato dal servlet container alla chiusura del contesto della web application.
	 * 
	 * @param sce
	 *          Il contesto di esecuzione della servlet e tutte le informazioni correlate
	 */
	public void contextDestroyed(ServletContextEvent sce) {
		notifyContextDestroyed(destroyContext());
	}
}