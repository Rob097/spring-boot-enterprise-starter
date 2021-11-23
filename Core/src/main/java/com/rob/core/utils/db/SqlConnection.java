package com.rob.core.utils.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.rob.core.utils.java.SessionObject;

/**
 * Il wrapper della classe java.sql.Connection.
 */
public class SqlConnection {

	/*private TransactionStatus transactionStatus;
	private Object parentConnectionHolder;
	private Vector<Statement> statementPool = new Vector<Statement>();*/

	protected Connection cnn = null;

	private Vector<PreparedStatement> preparedStmtPool = new Vector<PreparedStatement>();

	/**
	 * Crea una connessione al database che per essere utilizzata deve essere
	 * inizializzata.
	 */
	public SqlConnection() {

	}

	/** Mappa statica per la gestione delle connessioni */
	private static Set<Integer> connMap = new HashSet<Integer>();

	/**
	 * Ogni connessione viene inizializzata una sola volta eseguendo alcuni comandi
	 * predefiniti (CryptContext, NLS, etc..)
	 */
	private void init(Connection connection) {
		try {
			if (connMap.contains(connection.hashCode())) {
				return;
			}

			// Imposta il contesto per l'eventuale cifratura dei dati
			// setCryptContext(connection);

			// Set NLS
			// setDateFormat();

			// Memorizza inizializzazione
			connMap.add(connection.hashCode());

			// La mappa cresce di continuo: limito il suo tasso di crescita per evitare
			// memory leak
			if (connMap.size() > 10000) {
				connMap = ConcurrentHashMap.newKeySet();
			}
		} catch (Exception e) {
			connMap = ConcurrentHashMap.newKeySet();
		}
	}

	/**
	 * Crea e inizializza una connessione al database a partire da quella
	 * specificata già pronta per essere utilizzata.
	 * 
	 * @param connection La connessione al database
	 * @throws SQLException
	 */
	public SqlConnection(Connection connection) throws SQLException {
		cnn = connection;

		// Inizializza connessione (CryptContext, NLS, etc..)
		init(connection);

	}

	/**
	 * Restituisce l'oggetto Connection incapsulato dalla classe
	 * 
	 * @return
	 */
	public Connection getConnection() {
		return cnn;
	}

	/**
	 * Metodo per il prepareStatement
	 * 
	 * @param strSql Stringa SQL
	 * @return PreparedStatement
	 * @throws SQLException
	 */
	public PreparedStatement prepareStatement(String strSql) throws SQLException {

		PreparedStatement prepStmt = cnn.prepareStatement(strSql);
		preparedStmtPool.addElement(prepStmt);

		return prepStmt;
	}

	/** Applica contesto di criptatura a connessione fornita */
	private void setCryptContext(Connection connection) throws SQLException {
		if (DataEncryption.getInstance().isEnabled()) {
			CallableStatement cs = null;
			try {
				cs = DataEncryption.getInstance().setCryptContext(connection);
				cs.execute();
			} finally {
				SessionObject.closeStatements(cs);
				cs = null;
			}
		}
	}

	/**
	 * Indica se la connessione ha una transazione pendente.
	 * 
	 * @return un booleano che indica se c'è una transazione attiva per la
	 *         connessione.
	 *
	 *         public boolean isTransactionPending() { return transactionStatus !=
	 *         null; }
	 */

	/**
	 * Il metodo disattiva il commit automatico delle transazioni. Serve per
	 * iniziare una transazione che si concluderà con una commit o una rollback.
	 * Inoltre imposta l'indicatore di transazione pendente.
	 * 
	 * @throws SQLException si verifica un problema sulla connessione viene
	 *                      sollevata una SQLException.
	 * 
	 *                      public void beginTrans() throws SQLException {
	 * 
	 *                      if (transactionStatus != null) { throw new
	 *                      SessionObjectException("E' gia' presente una
	 *                      transazione, impossibile proseguire."); }
	 * 
	 *                      parentConnectionHolder =
	 *                      TransactionSynchronizationManager
	 *                      .unbindResourceIfPossible(getTransactionManager().getDataSource());
	 * 
	 *                      TransactionSynchronizationManager.bindResource(getTransactionManager().getDataSource(),
	 *                      new ConnectionHolder(cnn));
	 * 
	 *                      transactionStatus = getTransactionManager()
	 *                      .getTransaction(new
	 *                      DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
	 *                      }
	 */

	/**
	 * Il metodo esegue la commit di una transazione. Inoltre azzera l'indicatore di
	 * transazione pendente.
	 * 
	 * @throws SQLException si verifica un problema sulla connessione solleva una
	 *                      SQLException.
	 * 
	 *                      public void commitTrans() throws SQLException {
	 * 
	 *                      if (transactionStatus == null) { throw new
	 *                      SessionObjectException("Non esiste nessuna transazione
	 *                      aperta, impossibile proseguire."); }
	 * 
	 *                      getTransactionManager().commit(transactionStatus);
	 * 
	 *                      TransactionSynchronizationManager.unbindResource(getTransactionManager().getDataSource());
	 * 
	 *                      if (parentConnectionHolder != null) {
	 *                      TransactionSynchronizationManager.bindResource(getTransactionManager().getDataSource(),
	 *                      parentConnectionHolder); parentConnectionHolder = null;
	 *                      }
	 * 
	 *                      transactionStatus = null; }
	 */

	/**
	 * Il metodo provvede ad eseguire il rollback di una transazione. Inoltre
	 * decrementa il contatore che fornisce il numero di transazioni pendenti.
	 * 
	 * @throws SQLException si verifica un problema sulla connessione solleva una
	 *                      SQLException.
	 * 
	 *                      public void rollBackTrans() throws SQLException {
	 * 
	 *                      if (transactionStatus == null) { return; }
	 * 
	 *                      getTransactionManager().rollback(transactionStatus);
	 * 
	 *                      TransactionSynchronizationManager.unbindResource(getTransactionManager().getDataSource());
	 * 
	 *                      if (parentConnectionHolder != null) {
	 *                      TransactionSynchronizationManager.bindResource(getTransactionManager().getDataSource(),
	 *                      parentConnectionHolder); parentConnectionHolder = null;
	 *                      }
	 * 
	 *                      transactionStatus = null; }
	 */

	/**
	 * Esegue una query di aggiornamento o modifica nel DB.
	 * 
	 * @param query query da eseguire
	 * @return int numero di record aggiornati
	 * @throws SQLException
	 * 
	 *                      public int execute(String query) throws SQLException {
	 *                      return execute(query, true); }
	 * 
	 *                      /** Esegue una query di aggiornamento o modifica nel DB.
	 * 
	 * @param query     Query da eseguire
	 * @param upperCase Trasforma in UpperCase la query
	 * @return int Numero di record aggiornati
	 * @throws SQLException
	 * 
	 *                      public int execute(String query, boolean upperCase)
	 *                      throws SQLException {
	 * 
	 *                      Statement statement = null; int recordsAffected;
	 * 
	 *                      try { statement = cnn.createStatement(); if (upperCase)
	 *                      { recordsAffected =
	 *                      statement.executeUpdate(query.toUpperCase()); } else {
	 *                      recordsAffected = statement.executeUpdate(query); } }
	 *                      finally { this.closeStatements(statement); this.close();
	 *                      }
	 * 
	 *                      return recordsAffected; }
	 * 
	 *                      /** Esegue una query di aggiornamento o modifica nel DB.
	 * 
	 * @param bld PreparedStatementBuilder da eseguire
	 * @return int Numero di record aggiornati
	 * @throws SQLException
	 * 
	 *                      public int executeUpdate(PreparedStatementBuilder bld)
	 *                      throws SQLException { try { if (bld == null) { return 0;
	 *                      }
	 * 
	 *                      // Esegue comando su PreparedStatementBuilder return
	 *                      bld.executeUpdate(this);
	 * 
	 *                      } finally { // Chiude le risorse utilizzate if (bld !=
	 *                      null) { bld.close(); } } }
	 * 
	 *                      /** Rilascia la connessione al pool.
	 * 
	 * @throws SQLException
	 * 
	 *                      public void close() throws SQLException {
	 * 
	 *                      // Se esiste una transazione effettua una rollback per
	 *                      assicurarsi che tutto // sia chiuso prima di rilasciare
	 *                      la connessione e chiudere gli statement. try { if
	 *                      (isTransactionPending()) { rollBackTrans(); } } catch
	 *                      (Exception e) { // Blocca eventuale eccezione }
	 * 
	 *                      // chiude tutti gli statement eventualmente aperti try {
	 *                      while (statementPool.size() > 0) {
	 *                      this.closeStatements(statementPool.elementAt(0));
	 *                      statementPool.removeElementAt(0); } } catch (Exception
	 *                      e) { // Blocca eventuale eccezione }
	 * 
	 *                      // chiude tutti i prepareStatement eventualmente aperti
	 *                      try { while (preparedStmtPool.size() > 0) {
	 *                      this.closeStatements(preparedStmtPool.elementAt(0));
	 *                      preparedStmtPool.removeElementAt(0); } } catch
	 *                      (Exception e) { // Blocca eventuale eccezione }
	 * 
	 *                      // rilascia la connessione al pool cnn.close(); }
	 */

	/**
	 * Prepara uno statement che può essere utilizzato per chiamare una stored
	 * procedure.
	 * 
	 * @param sql Il sorgente della chiamata da efettuare.
	 * @return un CallableStatement che contiene la chiamata SQL precompilata.
	 * @throws SQLException in caso di errore di accesso al database.
	 * 
	 *                      public CallableStatement prepareCall(String sql) throws
	 *                      SQLException {
	 * 
	 *                      CallableStatement stmt = cnn.prepareCall(sql);
	 *                      statementPool.addElement(stmt);
	 * 
	 *                      return stmt; }
	 */

	/**
	 * Chiude tutti gli statement passati in input ed eventualmente i relativi
	 * resultset associati <br>
	 * <BR>
	 * 
	 * <hr>
	 * <blockquote> <b> NOTA BENE </B><BR>
	 * Utilizzare sempre all'interno di un blocco finally
	 * </p>
	 * </blockquote>
	 * <hr>
	 * <BR>
	 * <BR>
	 * 
	 * @param statements uno o più statement
	 * 
	 *                   protected final void closeStatements(Statement...
	 *                   statements) { try { if (statements == null) { return; }
	 * 
	 *                   for (Statement stmt : statements) { try { if (stmt != null)
	 *                   { stmt.close(); } } catch (Exception e) {
	 *                   e.printStackTrace(); } }
	 * 
	 *                   } catch (Exception e) { // do nothing } }
	 */

	/*
	 * private DataSourceTransactionManager getTransactionManager() { return
	 * SpringContextBridge.getBean(DataSourceTransactionManager.class); }
	 */

}
