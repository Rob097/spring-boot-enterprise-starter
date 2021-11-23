package com.rob.core.utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.rob.core.utils.db.BindVariableInfo.BindVariableTypeEnum;
import com.rob.core.utils.java.Commons;
import com.rob.core.utils.java.IntegerList;
import com.rob.core.utils.java.SessionObject;
import com.rob.core.utils.java.StringList;

/**
 * Classe utilizzata per trasportare le informazioni necessarie a generare un
 * preparedStatement
 */
@SuppressWarnings("javadoc")
public final class PreparedStatementBuilder extends SessionObject implements AutoCloseable {
	// Oggetto utilizzato per il log su file
	protected Logger logger;

	private StringBuilder sql = new StringBuilder(2048);
	private List<BindVariableInfo> bindVarInfos = new ArrayList<BindVariableInfo>();
	private boolean manageQuery = true; // se true (default) modifica il testo SQL durante l'aggiunta dei parametri
	private boolean validCriteria = true; // se false (default=true), lancia SQLException "parametri non validi" su
											// esecuzione query
	private boolean traceEnable = false; // se true, forza tracciamento comandi eseguiti

	private Range range; // Paginazione dei risultati, se non nullo limita i risultati dal record "start"
							// al record "end"

	/** Se true, attiva conteggio records */
	private boolean count = false;

	private final static char placeHolder = '?';
	/**
	 * Numero massimo di parametri accettati in una "IN/NOT IN". Potremmo spezzare
	 * in più "IN", ma il calo di prestazioni sarebbe devastante
	 */
	public final static int maxInParameters = 999;

	private List<String> warnings = new StringList();

	protected Vector<Statement> givenStatements = new Vector<Statement>();
	protected Vector<ResultSet> givenResultsets = new Vector<ResultSet>();

	// Lista di codici sql da ignorare
	private List<String> ignoreSqlStates;

	// Variabile sql state eccezione di tipo "violazione constraint"
	public final static String SQLSTATE_CONSTRAINT_VIOLATION = "23000";

	// Variabile sql state eccezione di tipo "java.sql.sqlTimeoutException"
	public final static String SQLSTATE_TIMEOUT_EXCEPTION = "01013";

	// Variabile sql state per exception:
	// java.sql.SQLTimeoutException","message":"ORA-01013: user requested cancel of
	// current operation\n"
	public final static String SQLSTATE_TIMEOUT_EXCEPTION_V1 = "72000";

	/**
	 * Se maggiore di 0, indica il tempo massimo (in secondi) di esecuzione della
	 * query
	 */
	private int queryTimeout;

	/**
	 * Costruisce un nuovo PreparedStatementBuilder
	 */
	public PreparedStatementBuilder() {
		super();
		logger = org.slf4j.LoggerFactory.getLogger(getClass());
	}

	public boolean isValidCriteria() {
		return validCriteria;
	}

	public void setValidCriteria(boolean validCriteria) {
		this.validCriteria = validCriteria;
	}

	/** se true, forza tracciamento comandi eseguiti **/
	public boolean isTraceEnable() {
		return traceEnable;
	}

	public void setTraceEnable(boolean traceEnable) {
		this.traceEnable = traceEnable;
	}

	/** Restituisce il segnaposto usato per variabili */
	public String getPlaceHolder() {
		return String.valueOf(placeHolder);
	}

	/** StringBuilder usato alla preparazione delle queries */
	public StringBuilder getSql() {
		return this.sql;
	}

	/**
	 * Aggiunge un preparedstatement alla query (accorpa il testo SQL ed i
	 * parametri)
	 */
	public PreparedStatementBuilder append(PreparedStatementBuilder psb) {
		this.sql.append(psb.getSql().toString());
		for (BindVariableInfo var : psb.getBindVarInfos()) {
			this.bindVarInfos.add(var);
			var.setParamPos(this.bindVarInfos.size()); // ad ogni parametro è associata una posizione
		}
		return this;
	}

	/** Aggiunge stringa alla query */
	public PreparedStatementBuilder append(String value) {
		this.sql.append(value);
		return this;
	}

	/** Aggiunge int alla query */
	public PreparedStatementBuilder append(int value) {
		this.sql.append(value);
		return this;
	}

	/** Aggiunge int alla query */
	public PreparedStatementBuilder append(Integer value) {
		this.sql.append(value);
		return this;
	}

	/** Aggiunge int alla query */
	public PreparedStatementBuilder append(long value) {
		this.sql.append(value);
		return this;
	}

	/** Aggiunge int alla query */
	public PreparedStatementBuilder append(Long value) {
		this.sql.append(value);
		return this;
	}

	/** Memorizza un warning e lo logga */
	private void addWarning(String warn) {
		this.warnings.add(warn);
		logger.warn(warn);
	}

	/** Lista dei parametri (info), con cui costruirà lo statement */
	public List<BindVariableInfo> getBindVarInfos() {
		return bindVarInfos;
	}

	public boolean isManageQuery() {
		return manageQuery;
	}

	public void setManageQuery(boolean manageQuery) {
		this.manageQuery = manageQuery;
	}

	public int getMaxRows() {
		return RangeUtils.rangeToSize(range, Integer.MAX_VALUE, 0);
	}

	public void setMaxRows(Integer maxRows) {
		if (maxRows == null || maxRows <= 0) {
			this.range = null;
		} else {
			this.range = new Range(Range.ROWS, 0, maxRows - 1);
		}
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public boolean isCount() {
		return count;
	}

	public void setCount(boolean count) {
		this.count = count;
	}

	public int getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(int seconds) {
		this.queryTimeout = seconds;
	}

	/** Aggiunge place holder alla query */
	public PreparedStatementBuilder addPlaceHolder() {
		this.sql.append(" ");
		this.sql.append(placeHolder);
		this.sql.append(" ");
		return this;
	}

	public PreparedStatementBuilder addBindVariable(BindVariableInfo var) {
		this.bindVarInfos.add(var);
		var.setParamPos(this.bindVarInfos.size()); // ad ogni parametro è associata una posizione
		if (manageQuery) {
			addPlaceHolder();
		}
		return this;
	}

	/** Aggiunge parametro outPut (CallableStatements) */
	public PreparedStatementBuilder addOutParameter(String paramName, BindVariableTypeEnum varType) {
		boolean input = false;
		BindVariableInfo var = new BindVariableInfo(paramName, varType, null, input);
		addBindVariable(var);
		return this;
	}

	/**
	 * Aggiunge parametro di tipo stringa
	 * 
	 * @param paramName
	 * @param value
	 * @param upperCase: se true, il valore viene impostato in uppercase
	 * @return
	 */
	public PreparedStatementBuilder addBindVariableWithCase(String paramName, String value, boolean upperCase) {
		// Se richiesto (default=true), il valore del campo viene messo in uppercase
		if (upperCase && StringUtils.isNotEmpty(value)) {
			value = value.toUpperCase();
		}
		BindVariableInfo var = new BindVariableInfo(paramName, BindVariableTypeEnum.STRING, value);
		addBindVariable(var);
		return this;
	}

	/** Imposta variabile stringa IN UPPERCASE (DEFAULT) */
	public PreparedStatementBuilder addBindVariable(String paramName, String value) {
		boolean upperCase = true; // DEFAULT: Il valore del campo viene messo in uppercase
		return addBindVariableWithCase(paramName, value, upperCase);
	}

	/**
	 * Aggiunge variabile di tipo String, permettendo di gestire anche i CLOB
	 * 
	 * @param paramName
	 * @param value
	 * @param isClob
	 */
	public PreparedStatementBuilder addBindVariableClob(String paramName, String value) {
		BindVariableInfo var = new BindVariableInfo(paramName, BindVariableTypeEnum.CLOB, value);
		addBindVariable(var);
		return this;
	}

	public PreparedStatementBuilder addBindVariable(String paramName, Integer value) {
		BindVariableInfo var = new BindVariableInfo(paramName, BindVariableTypeEnum.INTEGER, value);
		addBindVariable(var);
		return this;
	}

	public PreparedStatementBuilder addBindVariable(String paramName, Float value) {
		BindVariableInfo var = new BindVariableInfo(paramName, BindVariableTypeEnum.FLOAT, value);
		addBindVariable(var);
		return this;
	}

	public PreparedStatementBuilder addBindVariable(String paramName, Short value) {
		Integer intVal = null;
		if (value != null) {
			intVal = Integer.valueOf(value);
		}
		BindVariableInfo var = new BindVariableInfo(paramName, BindVariableTypeEnum.INTEGER, intVal);
		addBindVariable(var);
		return this;
	}

	public PreparedStatementBuilder addBindVariable(String paramName, Long value) {
		BindVariableInfo var = new BindVariableInfo(paramName, BindVariableTypeEnum.LONG, value);
		addBindVariable(var);
		return this;
	}

	public PreparedStatementBuilder addBindVariable(String paramName, Boolean value) {
		BindVariableInfo var = new BindVariableInfo(paramName, BindVariableTypeEnum.BOOLEAN, value);
		addBindVariable(var);
		return this;
	}

	public PreparedStatementBuilder addBindVariable(String paramName, Calendar value) {
		boolean truncate = false;
		addBindVariable(paramName, value, truncate);
		return this;
	}

	public PreparedStatementBuilder addBindVariable(String paramName, Calendar value, boolean truncate) {
		Date dtValue = null;
		if (value != null) {
			dtValue = value.getTime();
		}
		addBindVariable(paramName, dtValue, truncate);
		return this;
	}

	private static final String JAVA_FORMAT_DAY = "dd/MM/yyyy";
	private static final String DB_FORMAT_DAY = ",'%d/%m/%Y') ";

	private static final String JAVA_FORMAT_TIME = "dd/MM/yyyy HH:mm:ss";
	private static final String DB_FORMAT_TIME = ",'%d/%m/%Y %H:%i:%s') ";

	public void addBindVariable(String paramName, Date value) {
		boolean truncate = false;
		addBindVariable(paramName, value, truncate);
	}

	public PreparedStatementBuilder addBindVariable(String paramName, Date value, boolean truncate) {
		// Comando fornito dal chiamante; non possiamo modificare il testo della query
		if (!manageQuery) {
			BindVariableInfo var = new BindVariableInfo(paramName, BindVariableTypeEnum.DATE, value);
			addBindVariable(var);
			return this;
		}

		this.sql.append(" STR_TO_DATE(");

		if (value != null) {
			String calendarAsString = "";
			if (truncate) {
				// Precisione data: giorno
				calendarAsString = Commons.format(value, JAVA_FORMAT_DAY);
			} else {
				// Precisione data: secondi
				calendarAsString = Commons.format(value, JAVA_FORMAT_TIME);
			}
			addBindVariableWithCase(paramName, calendarAsString, false); // parametro VARCHAR2 formattato
		} else {
			this.sql.append("NULL");
		}

		if (truncate) {
			// Precisione data: giorno
			this.sql.append(DB_FORMAT_DAY);
		} else {
			// Precisione data: secondi
			this.sql.append(DB_FORMAT_TIME);
		}
		return this;
	}

	public PreparedStatementBuilder addBindVariable(String paramName, Double value) {
		BindVariableInfo var = new BindVariableInfo(paramName, BindVariableTypeEnum.DOUBLE, value);
		return addBindVariable(var);
	}

	/**
	 * Aggiunge al comando una stringa contenente una lista di valori, del tipo
	 * "(?,?,?)"
	 */
	public PreparedStatementBuilder addBindVariable(String paramName, StringList value) {
		// Normalizzo input
		if (value == null) {
			value = new StringList();
		}

		// Estra la lista dei valori non nulli
		StringList realValues = value.getRealValues();

		if (realValues.size() == 0 || realValues.size() >= maxInParameters) {
			// Se la lista non contiene alcun valore valido (lista di null o stringhe vuote,
			// oppure parametro null)
			// , devo comunque completare il comando evitando statement del tipo "IN(NULL)"
			// , il quale produce "nessun risultato", ma avvierebbe un full scan della
			// tabella
			this.getSql().append("(");
			addBindVariable(paramName, "NULL");
			this.getSql().append(")");

			// Blocco l'esecuzione della query, alleggerendola
			this.getSql().append(" AND 1=0 ");

			// Registro il problema
			this.addWarning(
					paramName + ": La lista di valori non contiene elementi validi, oppure contiene troppi elementi.");

		} else {
			// Aggiungo la lista di parametri
			this.getSql().append("(");
			String sep = "";
			for (String val : realValues) {
				this.getSql().append(sep);
				addBindVariable(paramName, val);
				sep = String.valueOf(",");
			}
			this.getSql().append(")");
		}

		return this;
	}

	/**
	 * Aggiunge al comando una stringa contenente una lista di valori, del tipo
	 * "(?,?,?)"
	 */
	public PreparedStatementBuilder addBindVariable(String paramName, IntegerList value) {
		// Normalizzo input
		if (value == null) {
			value = new IntegerList();
		}

		// Estra la lista dei valori non nulli ed univoci
		IntegerList realValues = value.getDistinctValues();

		if (realValues.size() == 0 || realValues.size() >= maxInParameters) {
			// Se la lista non contiene alcun valore valido (lista di null oppure parametro
			// null)
			// , devo comunque completare il comando evitando statement del tipo "IN(NULL)"
			// , il quale produce "nessun risultato", ma avvierebbe un full scan della
			// tabella
			this.getSql().append("(");
			addBindVariable(paramName, Integer.valueOf(-1));
			this.getSql().append(")");

			// Blocco l'esecuzione della query, alleggerendola
			this.getSql().append(" AND 1=0 ");

			// Registro il problema
			this.addWarning(
					paramName + ": La lista di valori non contiene elementi validi, oppure contiene troppi elementi.");

		} else {
			// Aggiungo la lista di parametri
			this.getSql().append("(");
			String sep = "";
			for (Integer val : realValues) {
				this.getSql().append(sep);
				addBindVariable(paramName, val);
				sep = String.valueOf(",");
			}
			this.getSql().append(")");
		}

		return this;

	}

	/**
	 * Aggiunge al comando una stringa contenente una lista di valori, del tipo
	 * "(?,?,?)"
	 * 
	 * @param paramName il nome del parametro sul quale fare la in
	 * @param value     la lista di valori
	 */
	public PreparedStatementBuilder addBindVariable(String paramName, List<Integer> value) {
		// Normalizzo input
		if (value == null) {
			value = new ArrayList<Integer>();
		}
		return addBindVariable(paramName, new IntegerList(value));
	}

	/**
	 * Restituisce lo statement come stringa, in cui sostituisce i valori dei
	 * parametri ai "?"
	 */
	@Override
	public String toString() {
		String ret = getCommand();

		try {
			for (BindVariableInfo var : this.bindVarInfos) {
				if (var.isInput()) {
					// Parametro in input
					ret = replaceBindVariable(ret, var.getValue());
				} else {
					// Parametro in output
					ret = replaceBindVariable(ret, var.getParamName());
				}
			}
		} catch (Exception e) {
			// Per qualche motivo non riesco a valorizzare i parametri
			logger.error("Si è verificato un errore", e);

			// Restituisco almeno la query senza parametri..
			ret = getCommand();
		}
		return ret;
	}

	/**
	 * Aggiunge lo stato fornito in input a quelli da ignorare nella gestione errori
	 * statement
	 */
	public void ignoreSqlException(String sqlState) {
		if (this.ignoreSqlStates == null) {
			this.ignoreSqlStates = new ArrayList<>();
		}
		this.ignoreSqlStates.add(sqlState);
	}

	/** Costruisce il comando da eseguire */
	private String getCommand() {
		// Genero il comando da eseguire
		String cmd = this.getSql().toString();

		if (count) {
			// CONTEGGIO RISULTATI
			StringBuilder pagingBuffer = new StringBuilder(2048);
			pagingBuffer.append("SELECT COUNT(*) AS CNT FROM ( ");
			pagingBuffer.append(cmd);
			pagingBuffer.append(") SUB_CNT ");
			cmd = pagingBuffer.toString();

		} else if (range != null) {
			QueryFactory factory = new QueryFactory();
			StringBuilder pagingBuffer = new StringBuilder(2048);
			pagingBuffer.append(cmd);
			if (range.getEnd() >= 0) {
				pagingBuffer.append(factory.limitRows(RangeUtils.rangeToSize(range)));
			}
			if (range.getStart() > 0) {
				pagingBuffer.append(factory.offsetRows(range.getStart()));
			}

			cmd = pagingBuffer.toString();
		}

		return cmd;
	}

	/** Restituisce un PreparedStatement pronto per l'esecuzione */
	private PreparedStatement prepareStatement(SqlConnection con) throws SQLException {
		if (con == null) {
			throw new SQLException("Connection is null!");
		}

		// Genera comando compilato
		PreparedStatement pst = con.prepareStatement(getCommand());

		// Limito sul client JDBC il numero di risultati accettati
		// Rinforza lato client il limite impostato sulle righe da caricare
		if (this.getMaxRows() > 0) {
			pst.setMaxRows(this.getMaxRows());
		}

		// Memorizza il preparedStatements (per il successivo rilascio risorse)
		givenStatements.add(pst);

		for (BindVariableInfo par : this.getBindVarInfos()) {
			// Verifico non siano utilizzati parametri di output (vengono gestiti da metodo
			// "prepareCall")
			if (!par.isInput()) {
				throw new SQLException(
						"Unable to use outParameters with PreparedStatement.. use 'executeCall' method.");
			}
			if (par.getTypeEnum() == BindVariableTypeEnum.CLOB) {
				setBindVariable(pst, false, par.getParamPos(), par.getValue(), true);
			} else {
				setBindVariable(pst, par.getParamPos(), par.getValue());
			}
		}

		return pst;
	}

	/**
	 * Restituisce un CallableStatement pronto per l'esecuzione private
	 * CallableStatement prepareCall(SqlConnection con) throws SQLException { if
	 * (con == null) { throw new SQLException("Connection is null!"); }
	 * 
	 * CallableStatement stmt = con.prepareCall(this.getSql().toString());
	 * 
	 * // Memorizza il preparedStatements (per il successivo rilascio risorse)
	 * givenStatements.add(stmt);
	 * 
	 * for (BindVariableInfo par : this.getBindVarInfos()) { if (par.isInput()) { //
	 * Parametro in input setBindVariable(stmt, par.getParamPos(), par.getValue());
	 * } else { // Parametro in output registerOutParameter(stmt, par.getParamPos(),
	 * par); } }
	 * 
	 * return stmt; }
	 */

	/**
	 * Registra parametro di outPut su CallableStatement private void
	 * registerOutParameter(CallableStatement stmt, int i, BindVariableInfo par)
	 * throws SQLException {
	 * 
	 * if (par.getTypeEnum() == BindVariableTypeEnum.STRING) {
	 * stmt.registerOutParameter(i, java.sql.Types.VARCHAR);
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.INTEGER) {
	 * stmt.registerOutParameter(i, java.sql.Types.INTEGER);
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.LONG) {
	 * stmt.registerOutParameter(i, java.sql.Types.NUMERIC);
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.FLOAT) {
	 * stmt.registerOutParameter(i, java.sql.Types.FLOAT);
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.DOUBLE) {
	 * stmt.registerOutParameter(i, java.sql.Types.DOUBLE);
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.BOOLEAN) {
	 * stmt.registerOutParameter(i, java.sql.Types.BOOLEAN);
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.CALENDAR) {
	 * stmt.registerOutParameter(i, java.sql.Types.TIMESTAMP);
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.DATE) {
	 * stmt.registerOutParameter(i, java.sql.Types.TIMESTAMP); }
	 * 
	 * }
	 */

	/** Esegue una query e restituisce il risultato */
	public ResultSet executeQuery(Connection con) throws SQLException {
		SqlConnection sqlConn = new SqlConnection(con);
		return executeQuery(sqlConn);
	}

	/** Valuta se sia necessario tracciare l'esecuzione dello statement */
	private void writeStatement(long msDuration, SqlConnection con) {
		boolean traceEnable = false; // se true, traccia ogni query eseguita
		long msWarningTime = 15000; // 15 secs

		// Trace queries attivo
		try {
			String cfgTrace = "true";
			if (StringUtils.isNotEmpty(cfgTrace)) {
				traceEnable = Boolean.valueOf(cfgTrace);
			}
		} catch (Exception e) {
			traceEnable = false;
		}

		// Se singolo statement ha richiesto il trace di esecuzione
		if (this.isTraceEnable()) {
			traceEnable = true;
		}

		// Query fuori tempo massimo
		boolean warningEnable = false;
		if (msDuration > msWarningTime) {
			warningEnable = true;
		}

		String logString = "";
		if (warningEnable || traceEnable) {
			StringBuilder sb = new StringBuilder(2048);
			sb.append("STATEMENT (").append(String.valueOf(msDuration)).append(" ms): ").append(this.toString());
			logString = sb.toString();

		}

		// Trace in console
		if (traceEnable) {
			// System.out.println(logString);
			logger.info(con.getConnection().toString());
			logger.info(logString);
		}

		// Scrittura log su file
		if (warningEnable) {
			logger.warn(logString);
		}
	}

	/** Esegue una query e restituisce il risultato */
	public ResultSet executeQuery(SqlConnection con) throws SQLException {
		ElapsedMeter crono = new ElapsedMeter();

		try {
			PreparedStatement pst = this.prepareStatement(con);

			// Verifica se il comando creato è valido e lancia eventuale eccezione
			// Eseguo il controllo subito prima di eseguire il comando
			// , in modo da riportare nei logs il comando con tutti i parametri che erano
			// stati forniti e permettere una analisi del problema
			if (!this.isValidCriteria()) {
				String sql = getStatementBuilderAsString(this);
				logger.error(sql);
				throw new SQLException("Invalid criterias for statement!");
			}

			// Imposta timeout custom a query eseguita
			if (this.getQueryTimeout() > 0) {
				pst.setQueryTimeout(this.getQueryTimeout());
			} else {
				pst.setQueryTimeout(120); //
			}

			ResultSet rst = pst.executeQuery();

			// Memorizza il ResultSet (per il successivo rilascio risorse)
			givenResultsets.add(rst);

			return rst;

		} catch (SQLException ex) {
			// Log eccezione
			String sql = getStatementBuilderAsString(this);
			// System.out.println(ex.getMessage() + " > " + sql);
			logger.error(sql, ex);

			// In caso di errore, rilascia immediatamente le risorse
			this.close();

			// Rilancia eccezione
			throw ex;

		} finally {
			writeStatement(crono.partialDuration(true), con);
		}
	}

	/** Esegue un update e restituisce il risultato */
	public int executeUpdate(Connection con) throws SQLException {
		SqlConnection sqlConn = new SqlConnection(con);
		return executeUpdate(sqlConn);
	}

	/** Esegue un update e restituisce il risultato */
	public int executeUpdate(SqlConnection con) throws SQLException {
		ElapsedMeter crono = new ElapsedMeter();

		try {
			PreparedStatement pst = this.prepareStatement(con);

			// Verifica se il comando creato è valido e lancia eventuale eccezione
			// Eseguo il controllo subito prima di eseguire il comando
			// , in modo da riportare nei logs il comando con tutti i parametri che erano
			// stati forniti e permettere una analisi del problema
			if (!this.isValidCriteria()) {
				throw new SQLException("Invalid criterias for statement!");
			}

			// Imposta timeout custom a query eseguita
			if (this.getQueryTimeout() > 0) {
				pst.setQueryTimeout(this.getQueryTimeout());
			} else {
				pst.setQueryTimeout(10);
			}

			int result = pst.executeUpdate();
			return result;

		} catch (SQLException ex) {
			// Gestione tipologie di eccezione di ignorare (NON VENGONO LOGGATE, MA CAUSANO
			// COMUNQUE ECCEZIONE)
			boolean writeLog = true;
			if (ex.getSQLState() != null && this.ignoreSqlStates != null) {
				if (this.ignoreSqlStates.contains(ex.getSQLState())) {
					writeLog = false;
				}
			}

			// Log eccezione
			if (writeLog) {
				String sql = getStatementBuilderAsString(this);
				// System.out.println(ex.getMessage() + " > " + sql);
				logger.error(sql, ex);
			}

			// In caso di errore, rilascia immediatamente le risorse
			this.close();

			// Rilancia eccezione
			throw ex;

		} finally {
			writeStatement(crono.partialDuration(true), con);

		}
	}

	/** Esegue un comando */
	public boolean execute(SqlConnection con) throws SQLException {
		ElapsedMeter crono = new ElapsedMeter();

		try {
			PreparedStatement pst = this.prepareStatement(con);

			// Verifica se il comando creato è valido e lancia eventuale eccezione
			// Eseguo il controllo subito prima di eseguire il comando
			// , in modo da riportare nei logs il comando con tutti i parametri che erano
			// stati forniti e permettere una analisi del problema
			if (!this.isValidCriteria()) {
				throw new SQLException("Invalid criterias for statement!");
			}

			boolean result = pst.execute();
			return result;

		} catch (SQLException ex) {
			// Log eccezione
			String sql = getStatementBuilderAsString(this);
			// System.out.println(ex.getMessage() + " > " + sql);
			logger.error(sql, ex);

			// In caso di errore, rilascia immediatamente le risorse
			this.close();

			// Rilancia eccezione
			throw ex;

		} finally {
			writeStatement(crono.partialDuration(true), con);

		}
	}

	/**
	 * Metodo utilizzato per testare update/insert, facendo poi rollback
	 * immediatamente
	 */
	public void executeRollbackUpdates(Connection con) throws SQLException {
		SqlConnection sqlConn = new SqlConnection(con);
		executeRollbackUpdates(sqlConn);
	}

	/**
	 * Metodo utilizzato per testare update/insert, facendo poi rollback
	 * immediatamente
	 */
	public void executeRollbackUpdates(SqlConnection con) throws SQLException {
		// Memorizza stato connessione all'ingresso
		boolean autoCommit = con.getConnection().getAutoCommit();

		try {
			con.getConnection().setAutoCommit(false);

			this.executeUpdate(con);

			con.getConnection().rollback();

		} catch (SQLException ex) {
			// Log eccezione
			String sql = getStatementBuilderAsString(this);
			// System.out.println(ex.getMessage() + " > " + sql);
			logger.error(sql, ex);

			// Rilancia eccezione
			throw ex;

		} finally {
			// Ripristina precedente autocommit
			con.getConnection().setAutoCommit(autoCommit);

			this.close();
		}
	}

	/**
	 * Rilascia le risorse (statements,resultsets) istanziate dal
	 * PreparedStatementBuilder
	 */
	public void close() {
		// Chiusura dei ResultSets istanziati
		try {
			for (ResultSet rst : givenResultsets) {
				closeResultSets(rst);
			}
			givenResultsets.clear();
		} catch (Exception e) {
			logger.error("Si è verificato un errore", e);
		}

		// Chiusura degli statements istanziati
		try {
			for (Statement stm : givenStatements) {
				closeStatements(stm);
			}
			givenStatements.clear();
		} catch (Exception e) {
			logger.error("Si è verificato un errore", e);
		}
	}

	/**
	 * Preapara ed esegue un callable statement public List<BindVariableInfo>
	 * executeCall(Connection con) throws SQLException { SqlConnection sqlConn = new
	 * SqlConnection(con); return executeCall(sqlConn); }
	 * 
	 * /** Preapara ed esegue un callable statement public List<BindVariableInfo>
	 * executeCall(SqlConnection con) throws SQLException { ElapsedMeter crono = new
	 * ElapsedMeter(); List<BindVariableInfo> outPut = new
	 * ArrayList<BindVariableInfo>();
	 * 
	 * try { CallableStatement pst = this.prepareCall(con); pst.execute();
	 * 
	 * for (BindVariableInfo par : this.bindVarInfos) { // Scarta i parametri di
	 * inputPut if (par.isInput()) { continue; }
	 * 
	 * // Restituisco i parametri di outPut previsti outPut.add(par);
	 * 
	 * // Gestione NULL if (pst.getObject(par.getParamPos()) == null) {
	 * par.setValue(null); continue; }
	 * 
	 * // Valorizzo il risultato if (par.getTypeEnum() ==
	 * BindVariableTypeEnum.STRING) { // in PostgreSQL '' non viene considerato null
	 * come in Oracle // quindi se value è stringa vuota setto null per avere lo
	 * stesso comportamento // per Oracle e PostgreSQL par.setValue(
	 * StringUtils.isNotEmpty(pst.getString(par.getParamPos())) ?
	 * pst.getString(par.getParamPos()) : null);
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.INTEGER) {
	 * par.setValue(pst.getInt(par.getParamPos()));
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.LONG) {
	 * par.setValue(pst.getLong(par.getParamPos()));
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.FLOAT) {
	 * par.setValue(pst.getFloat(par.getParamPos()));
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.DOUBLE) {
	 * par.setValue(pst.getDouble(par.getParamPos()));
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.BOOLEAN) {
	 * par.setValue(pst.getBoolean(par.getParamPos()));
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.CALENDAR) {
	 * par.setValue(pst.getTimestamp(par.getParamPos()));
	 * 
	 * } else if (par.getTypeEnum() == BindVariableTypeEnum.DATE) {
	 * par.setValue(pst.getTimestamp(par.getParamPos())); } }
	 * 
	 * return outPut;
	 * 
	 * } catch (SQLException ex) { // Log eccezione String sql =
	 * getStatementBuilderAsString(this); // System.out.println(ex.getMessage() + "
	 * > " + sql); logger.error(sql, ex);
	 * 
	 * // In caso di errore, rilascia immediatamente le risorse this.close();
	 * 
	 * // Rilancia eccezione throw ex;
	 * 
	 * } finally { writeStatement(crono.partialDuration(true), con);
	 * 
	 * } }
	 */

	/**
	 * Metodo utilizzato per testare procedure, facendo poi rollback immediatamente
	 * (attenzione a pragma)
	 * 
	 * public void executeRollbackCalls(Connection con) throws SQLException {
	 * SqlConnection sqlConn = new SqlConnection(con);
	 * executeRollbackCalls(sqlConn); }
	 * 
	 * /** Metodo utilizzato per testare procedure, facendo poi rollback
	 * immediatamente (attenzione a pragma)
	 * 
	 * public void executeRollbackCalls(SqlConnection con) throws SQLException { //
	 * Memorizza stato connessione all'ingresso boolean autoCommit =
	 * con.getConnection().getAutoCommit();
	 * 
	 * try { con.getConnection().setAutoCommit(false);
	 * 
	 * this.executeCall(con);
	 * 
	 * con.getConnection().rollback();
	 * 
	 * } catch (SQLException ex) { // Log eccezione String sql =
	 * getStatementBuilderAsString(this); // System.out.println(ex.getMessage() + "
	 * > " + sql); logger.error(sql, ex);
	 * 
	 * // Rilancia eccezione throw ex;
	 * 
	 * } finally { // Ripristina precedente autocommit
	 * con.getConnection().setAutoCommit(autoCommit);
	 * 
	 * this.close(); } }
	 */

}
