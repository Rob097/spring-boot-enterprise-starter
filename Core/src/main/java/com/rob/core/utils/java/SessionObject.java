package com.rob.core.utils.java;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;

import com.rob.core.utils.db.PreparedStatementBuilder;
import com.rob.core.utils.db.QueryFactory;
import com.rob.core.utils.db.SqlConnection;
import com.rob.core.utils.db.SqlDataSource;

/**
 * Classe base per PreparedStatementBuilder
 */
@SuppressWarnings("javadoc")
public abstract class SessionObject {

	/** Oggetto utilizzato per il log su file */
	protected static Logger logger = org.slf4j.LoggerFactory.getLogger(SessionObject.class);

	/** Riferimento al pool di connessioni */
	protected DataSource connectionPool;

	/** La connessione esterna */
	protected SqlConnection extConnection;
	protected ArrayList<SqlConnection> givenConnections = new ArrayList<SqlConnection>();

	/** Vale true se l'oggetto è stato inizializzato in contesto master */
	protected boolean master;

	/**
	 * Cache per ridurre accessi a database; la cache è definita per ogni oggetto,
	 * ma può essere condivisa sul costruttore tra più oggetti della stessa request
	 * 1° chiave: className della classe salvata nella mappa 2° chiave:
	 * identificativo univoco istanza di classe valore: oggetto del tipo definito in
	 * className
	 */
	private Map<String, Map<String, Object>> hmObjectsCache = new HashMap<String, Map<String, Object>>();

	private void init(SqlConnection con) {
		if (con == null) {
			if (SqlDataSource.getInstance().getPool() != null) {
				connectionPool = SqlDataSource.getInstance().getPool();
				master = true;
			} else if (SqlDataSource.getInstance().getTestConnection() != null) {
				extConnection = SqlDataSource.getInstance().getTestConnection();
				master = false;
				connectionPool = null;
			}
		} else {
			extConnection = con;
			master = false;
			connectionPool = null;
		}

	}

	/**
	 * Costruttore in contesto master rispetto alle operazioni sul database.
	 */
	public SessionObject() {
		init(null);
	}

	/**
	 * Costruttore in contesto slave rispetto alle operazioni sul database.
	 * 
	 * @param con La connessione da utilizzare
	 */
	public SessionObject(SqlConnection con) {
		init(con);
	}

	/**
	 * Restituisce una connessione al database prelevandola dal pool se l'oggetto è
	 * stato istanziato in contesto master altrimenti la connessione ricevuta
	 * dall'esterno.
	 * 
	 * @return Una connessione al database
	 * @throws SQLException
	 */
	protected final SqlConnection getConnection() throws SQLException {
		if (connectionPool != null) {
			SqlConnection newConn = new SqlConnection(connectionPool.getConnection());
			givenConnections.add(newConn);
			return newConn;
		} else if (extConnection != null) {
			return extConnection;
		} else {
			throw new NullPointerException("Il pool di connessioni e la connessione esterna non sono valorizzati.");
		}
	}

	/** Converte il PreparedStatementBuilder in stringa, controllando se NULL */
	public static final String getStatementBuilderAsString(PreparedStatementBuilder bld) {
		String sql = "";
		try {
			if (bld != null) {
				sql = bld.toString();
			}
		} catch (Exception e) {
			logger.error("Errore conversione PreparedStatementBuilder in string.", e);
		}
		return sql;
	}

	/**
	 * Chiude tutti i resultset passati in inputed ed eventualmente i relativi
	 * statement associati NOTA BENE: Utilizzare sempre all'interno di un blocco
	 * finally
	 * 
	 * @param resultSets uno o più resultset
	 */
	public final static void closeResultSets(ResultSet... resultSets) {
		try {
			// Niente da chiudere
			if (resultSets == null) {
				return;
			}

			for (ResultSet rs : resultSets) {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					logger.error("errore nel tentativo di rilasciare il resultset:" + e.getMessage());
				}
			}

		} catch (Exception e) {
			logger.error("errore nel tentativo di rilasciare il resultset:" + e.getMessage());
		}
	}

	/**
	 * Chiude tutti gli statement passati in input ed eventualmente i relativi
	 * resultset associati NOTA BENE: Utilizzare sempre all'interno di un blocco
	 * finally
	 * 
	 * @param statements uno o più statement
	 */
	public final static void closeStatements(Statement... statements) {
		try {
			if (statements == null) {
				return;
			}

			for (Statement stmt : statements) {
				try {
					if (stmt != null) {
						stmt.close();
					}
				} catch (Exception e) {
					logger.error("errore nel tentativo di rilasciare lo statement:" + e.getMessage());
				}
			}

		} catch (Exception e) {
			logger.error("errore nel tentativo di rilasciare lo statement:" + e.getMessage());
		}
	}

	/**
	 * Imposta la variabile nell'oggetto {@link PreparedStatement} in base alla
	 * specifica tipologia della variabile. Il metodo permette di valorizzare la
	 * variabile nell'oggetto {@link PreparedStatement} solo nel caso la variabile
	 * sia diversa da null, tramite l'atteributo <code>skipNullValues</code>:
	 * <ul>
	 * <li>TRUE: effettua il controllo sulla variabile nell'attributo
	 * <code>value</code>, se il valore della variabile è null non viene impostata
	 * la variabile nel {@link PreparedStatement}</li>
	 * <li>FALSE: il valore presente nella variabile nell'attributo
	 * <code>value</code> viene sempre impostato nel {@link PreparedStatement}</li>
	 * </ul>
	 * 
	 * N.B. E' fondamentale la mappatura tra il tipo della variabile java passata al
	 * metodo <code>value</code> e il tipo del campo sul database (esempio:
	 * java.lang.String - VARCHAR2; java.lang.Integer - NUMBER)
	 * 
	 * @param statement {@link PreparedStatement} per il quale effettuare il bind
	 *                  delle variabili.
	 * @param index     Indice posizionale della variabile da impostare nel
	 *                  PreparedStatement.
	 * @param value     Valore della variabile da impostare.
	 * @throws SQLException
	 */
	public final static void setBindVariable(PreparedStatement statement, int index, Object value) throws SQLException {
		setBindVariable(statement, false, index, value, false);
	}

	public final static void setBindVariable(PreparedStatement statement, int index, String value) throws SQLException {
		setBindVariable(statement, false, index, value, false);
	}

	public final static void setBindVariable(PreparedStatement statement, int index, Integer value)
			throws SQLException {
		setBindVariable(statement, false, index, value, false);
	}

	public final static void setBindVariable(PreparedStatement statement, int index, Long value) throws SQLException {
		setBindVariable(statement, false, index, value, false);
	}

	public final static void setBindVariable(PreparedStatement statement, int index, Boolean value)
			throws SQLException {
		setBindVariable(statement, false, index, value, false);
	}

	public final static void setBindVariableOK(PreparedStatement statement, int index, Calendar value)
			throws SQLException {
		setBindVariable(statement, false, index, value, false);
	}

	public final static void setBindVariableOK(PreparedStatement statement, int index, Date value) throws SQLException {
		setBindVariable(statement, false, index, value, false);
	}

	public final static void setBindVariable(PreparedStatement statement, int index, Double value) throws SQLException {
		setBindVariable(statement, false, index, value, false);
	}

	public final static void setBindVariable(PreparedStatement statement, int index, StringList value)
			throws SQLException {
		setBindVariable(statement, false, index, value, false);
	}

	public final static void setBindVariable(PreparedStatement statement, int index, IntegerList value)
			throws SQLException {
		setBindVariable(statement, false, index, value, false);
	}

	/**
	 * PER I TIPI DI BASE, NON SUPPORTA:
	 * <ul>
	 * <li>CLOB</li>
	 * <li>LONG</li>
	 * <li>StringList</li>
	 * <li>IntegerList</li>
	 * <li>ValuedEnum</li>
	 * <li>StringEnum</li>
	 * <li>ICodedEnum</li>
	 * <li>IValuedEnum</li>
	 * </ul>
	 * 
	 * @param query
	 * @param value
	 * @return
	 */
	public final static String replaceBindVariable(String query, Object value) {
		String replacement = "";
		QueryFactory qf = new QueryFactory();
		// Null
		if (value == null) {
			replacement = "NULL";
			// String
		} else if (value instanceof String) {
			replacement = qf.writeString((String) value);
			// Integer
		} else if (value instanceof Integer) {
			replacement = qf.writeInteger((Integer) value);
			// Boolean
		} else if (value instanceof Boolean) {
			replacement = qf.writeBoolean((Boolean) value);
			// Calendar
		} else if (value instanceof Calendar) {
			replacement = qf.writeDate((Calendar) value);
			// Date
		} else if (value instanceof Date) {
			replacement = qf.writeDate((Date) value);
			// Double
		} else if (value instanceof Double) {
			replacement = qf.writeDouble((Double) value);
			// Long
		} else if (value instanceof Long) {
			replacement = qf.writeLong((Long) value);
		} else {
			replacement = "N/D";
		}
		query = query.replaceFirst("\\?", Matcher.quoteReplacement(replacement));
		return query;
	}

	/**
	 * Imposta la variabile nell'oggetto {@link PreparedStatement} in base alla
	 * specifica tipologia della variabile. Il metodo permette di valorizzare la
	 * variabile nell'oggetto {@link PreparedStatement} solo nel caso la variabile
	 * sia diversa da null, tramite l'atteributo <code>skipNullValues</code>:
	 * <ul>
	 * <li>TRUE: effettua il controllo sulla variabile nell'attributo
	 * <code>value</code>, se il valore della variabile è null non viene impostata
	 * la variabile nel {@link PreparedStatement}</li>
	 * <li>FALSE: il valore presente nella variabile nell'attributo
	 * <code>value</code> viene sempre impostato nel {@link PreparedStatement}</li>
	 * </ul>
	 * 
	 * N.B. E' fondamentale la mappatura tra il tipo della variabile java passata al
	 * metodo <code>value</code> e il tipo del campo sul database (esempio:
	 * java.lang.String - VARCHAR2; java.lang.Integer - NUMBER)
	 * 
	 * @param statement      {@link PreparedStatement} per il quale effettuare il
	 *                       bind delle variabili.
	 * @param skipNullValues E' un flag che indica se i valori null vanno impostati
	 *                       nel PreparedStatement o devono essere saltati.
	 * @param index          Indice posizionale della variabile da impostare nel
	 *                       PreparedStatement.
	 * @param value          Valore della variabile da impostare.
	 * @param isClob         Booleano che indica se il campo è un Clob.
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("deprecation")
	public final static int setBindVariable(PreparedStatement statement, boolean skipNullValues, int index,
			Object value, boolean isClob) throws SQLException {
		// controllo del NULL sulla variabile (solo se previsto): nel caso di valore
		// null la variabile non viene impostata nel PreparedStatement
		if (!(skipNullValues && value == null)) {
			// Null
			if (value == null) {
				statement.setNull(index, Types.NULL);

				// String
			} else if (value instanceof String) {
				if (isClob) {
					statement.setAsciiStream(index, IOUtils.toInputStream((String) value), ((String) value).length());
				} else {
					statement.setString(index, (String) value);
				}

				// Integer
			} else if (value instanceof Integer) {
				statement.setInt(index, ((Integer) value).intValue());

				// Long
			} else if (value instanceof Long) {
				statement.setLong(index, ((Long) value).longValue());

				// Boolean
			} else if (value instanceof Boolean) {
				statement.setInt(index, BooleanUtils.toInteger((Boolean) value, 1, 0));

				// Calendar
			} else if (value instanceof Calendar) {
				statement.setTimestamp(index, new Timestamp(((Calendar) value).getTimeInMillis()));

				// Date
			} else if (value instanceof Date) {
				statement.setTimestamp(index, new Timestamp(((Date) value).getTime()));

				// Double
			} else if (value instanceof Double) {
				statement.setDouble(index, ((Double) value).doubleValue());

				// StringList
			} else if (value instanceof StringList) {
				statement.setString(index, ((StringList) value).join());

				// IntegerList
			} else if (value instanceof IntegerList) {
				statement.setString(index, ((IntegerList) value).join());

				// Tipo di oggetto non previsto..
			} else {
				String clazzName = value.getClass().getName();
				throw new SQLException(String.format("%s", clazzName));
			}
			index++;
		}
		return index;
	}

	/**
	 * Recupera la mappa di cache riferita ad una certa tipologia di classe
	 * 
	 * @param cls : tipo di classe di cui voglio recupare la cache
	 * @return
	 */
	private Map<String, Object> getClassCache(@SuppressWarnings("rawtypes") Class cls) {
		// Recupera la mappa che contiene le istanze di una particolare classe
		Map<String, Object> classCache = null;
		if (hmObjectsCache.containsKey(cls.getName())) {
			classCache = hmObjectsCache.get(cls.getName());
		} else {
			classCache = new HashMap<String, Object>();
			hmObjectsCache.put(cls.getName(), classCache);
		}
		return classCache;
	}

	/**
	 * Memorizza un oggetto nella cache
	 * 
	 * @param cls : tipo di classe salvato in cache
	 * @param id  : identificativo univoco istanza di classe
	 * 
	 * @return Object del tipo specificato nel parametro cls
	 */
	public Object getObjectFromCache(@SuppressWarnings("rawtypes") Class cls, String id) {
		// Recupera la mappa che contiene le istanze di una particolare classe
		Map<String, Object> classCache = getClassCache(cls);

		// Recupera eventuale oggetto salvata nella mappa specifica per classe
		Object result = classCache.get(id);

		// Verifica che l'oggetto salvato sia del tipo previsto per la cache
		if (result != null) {
			if (!result.getClass().getName().equalsIgnoreCase(cls.getName())) {
				result = null;
				classCache.remove(id);
			}
		}

		return result;
	}

	/**
	 * 
	 * @param cls    : tipo di classe salvato in cache
	 * @param id     : identificativo univoco istanza di classe
	 * @param object : istanza di classe da salvare in cache
	 */
	public void putObjectIntoCache(@SuppressWarnings("rawtypes") Class cls, String id, Object object) {
		// Recupera la mappa che contiene le istanze di una particolare classe
		Map<String, Object> classCache = getClassCache(cls);

		// Aggiungo oggetto in cache (non controllo il tipo; verrà fatto in fase di get)
		classCache.put(id, object);
	}

	/**
	 * Metodo invocato al dispose di un oggetto, per ovviare ad eventuali
	 * connessioni dimenticate aperte
	 * 
	 * @Override protected void finalize() throws Throwable { if (givenConnections
	 *           != null) for (SqlConnection conn : givenConnections) { try { if
	 *           (conn != null) conn.close(); } catch (Exception e) { //Do nothing }
	 *           }
	 * 
	 *           super.finalize(); }
	 */

	/**
	 * Ritorna true se l'oggetto è stato inizializzato in contesto master.
	 * 
	 * @return true se l'oggetto è stato inizializzato in contesto master.
	 * 
	 *         protected boolean isMaster() { return master; }
	 */

	/**
	 * Restituisce la connessione al pool (rilasciando gli eventuali statement
	 * "appesi") se l'oggetto è stato istanziato in contesto master.
	 * 
	 * @param con La connessione da chiudere
	 * 
	 *            protected final void freeConnection(SqlConnection con) { try {
	 *            //Se connessione not null e MASTER (extConnection == null) if (con
	 *            != null && extConnection == null) { try { if
	 *            (givenConnections.contains(con)) { givenConnections.remove(con); }
	 *            } catch (Exception e) { // blocca eventuale errore }
	 * 
	 *            //Chiude la connessione (Il metodo chiude eventuali transazioni
	 *            rimaste appese e non committate) con.close(); con = null;
	 * 
	 *            }
	 * 
	 *            } catch (Exception e) { logger.error("Errore nel rilascio della
	 *            connessione", e); } }
	 */

	/**
	 * Rilascia le risorse (statements,resultsets) istanziate dal
	 * PreparedStatementBuilder public static final void
	 * closeStatementBuilder(PreparedStatementBuilder bld) { if (bld!=null) {
	 * bld.close(); } }
	 */

	/**
	 * esegue la chiusura dell'ultimo statement associato a questo resultSet e del
	 * result set stesso controlla che il result set passato non sia nullo prima di
	 * eseguire, separatamente, le due operazioni
	 * 
	 * @param resultSet
	 * 
	 *                  protected final void
	 *                  closeCurrentStatementAndResultSet(ResultSet resultSet){ try
	 *                  { if(resultSet!=null && resultSet.getStatement()!=null){
	 *                  //nota: dalla doc se è già stato chiuso lo statement,
	 *                  chiuderlo nuovamente è un'operazione senza effetto
	 *                  resultSet.getStatement().close(); } } catch (Exception e) {
	 *                  logger.error("errore nel rilascio dello statement"); } try {
	 *                  if(resultSet!=null){ //nota: dalla doc se è già stato chiuso
	 *                  il resultset, chiuderlo nuovamente è una no-op
	 *                  resultSet.close(); } } catch (Exception e) {
	 *                  logger.error("errore nel rilascio del result set"); } }
	 */

}
