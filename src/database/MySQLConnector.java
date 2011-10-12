package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * MySQLConnector
 * 
 * A wrapper class implementing the MySQL JDBC connector. This was written
 * using: mysql-connector-java-5.1.7
 * 
 */
public class MySQLConnector {

	private static final boolean debug = false;
	private static final int MAX_CONNECT_ATTEMPTS = 720; // ~60 min
	private static final int CONNECT_NOTIFY_THRESHOLD = MAX_CONNECT_ATTEMPTS - 3;
	private static final int CONNECT_INTERVAL = 5000;
	private static final int MAX_QUERY_ATTEMPTS = 5;

	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String schema;
	private final boolean autoCreate;
	private final Lock lock;
	private final Map<Object, PrepStmt> stmtPreps;

	private Connection db;
	private Statement stmtUpdate;
	private Statement stmtQuery;

	/**
	 * Creates the connector and connects to the database. Will not attempt to
	 * create the schema if it does not exist.
	 * 
	 * @param host
	 *            MySQL server host
	 * @param port
	 *            MySQL server port, usually 3306
	 * @param username
	 *            Login username
	 * @param password
	 *            Login password
	 * @param schema
	 *            Schema to open
	 * @throws SQLException
	 */
	public MySQLConnector(String host, int port, String username,
			String password, String schema) throws SQLException {
		this(host, port, username, password, schema, false);
	}

	/**
	 * Creates the connector and connects to the database.
	 * 
	 * @param host
	 *            MySQL server host
	 * @param port
	 *            MySQL server port, usually 3306
	 * @param username
	 *            Login username
	 * @param password
	 *            Login password
	 * @param schema
	 *            Schema to open
	 * @param autoCreate
	 *            True to attempt to create the schema if it does not exist,
	 *            false to throw an exception.
	 * @throws SQLException
	 */
	public MySQLConnector(String host, int port, String username,
			String password, String schema, boolean autoCreate)
			throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			throw new SQLException("Driver not found.", null, -1);
		}
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.schema = schema;
		this.autoCreate = autoCreate;
		lock = new ReentrantLock();
		stmtPreps = new HashMap<Object, PrepStmt>(20, 0.6f);
		connect();
	}

	/**
	 * Checks whether the SQLException thrown is possibly due to a connection
	 * issue.
	 * 
	 * @param ex
	 *            SQLException to check
	 * @return true if it may be a connection issue, false otherwise.
	 */
	public static boolean isConnectionIssue(SQLException ex) {
		// These are MySQL-specific error codes to check whether the
		// exception is due to a connection issue, which means we can
		// attempt to retry connecting.
		switch (ex.getErrorCode()) {
		case 1040:
		case 1042:
		case 1043:
		case 1047:
		case 1053:
		case 1080:
		case 1081:
		case 1152:
		case 1153:
		case 1154:
		case 1155:
		case 1156:
		case 1157:
		case 1158:
		case 1159:
		case 1160:
		case 1161:
		case 1184:
		case 1189:
		case 1190:
		case 1049:
			return true;
		case 0:
			String sqlState = ex.getSQLState();
			if ("08S01".equals(sqlState) || "08003".equals(sqlState)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the SQLException thrown is due to a syntax issue.
	 * 
	 * @param ex
	 *            SQLException to check
	 * @return true if it is a syntax issue, false otherwise.
	 */
	public static boolean isSyntaxIssue(SQLException ex) {
		return ex.getClass().getName().endsWith("SyntaxErrorException");
	}

	/**
	 * Checks and creates the schema if it is missing.
	 */
	private boolean checkSchema() {
		MySQLConnector dbInfo = null;
		try {
			dbInfo = new MySQLConnector(host, port, username, password,
					"information_schema");

			// Check for existence of target schema
			ResultSet rs = dbInfo
					.query("SELECT * FROM schemata WHERE schema_name LIKE '"
							+ schema + "'");
			if (!rs.next()) {
				System.err.println("Creating schema '" + schema + "'.");
				// Try to create it if doesn't exist
				dbInfo.update("CREATE DATABASE IF NOT EXISTS `" + schema + "`");
			}
			return true;
		} catch (Exception e) {
		} finally {
			try {
				dbInfo.close();
			} catch (Exception e) {
			}
		}
		return false;
	}

	/**
	 * Connects to the database.
	 * 
	 * @throws SQLException
	 */
	private void connect() throws SQLException {
		for (;;) {
			try {
				db = DriverManager.getConnection("jdbc:mysql://" + host + ":"
						+ port + "/" + schema, username, password);
				break;
			} catch (SQLException ex) {
				if (autoCreate && isConnectionIssue(ex) && checkSchema()) {
					continue;
				}
				throw ex;
			}
		}
		stmtUpdate = db.createStatement();
		stmtQuery = db.createStatement(ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY);
		stmtUpdate.execute("USE `" + schema + "`");
		stmtQuery.execute("USE `" + schema + "`");
	}

	/**
	 * Checks the current connection and reconnects if required.
	 * 
	 * @throws SQLException
	 *             If we are unable to connect to the database
	 */
	public void reconnect() throws SQLException {
		lock.lock();
		try {
			for (int i = MAX_CONNECT_ATTEMPTS; --i >= 0;) {
				try {
					if (stmtUpdate != null) {
						try {
							stmtUpdate.execute("USE `" + schema + "`");
						} catch (SQLException ex) {
							try {
								stmtUpdate.close();
							} catch (Exception e) {
							}
							try {
								db.close();
							} catch (Exception e) {
							}
						}
					}
					if (stmtQuery != null) {
						try {
							stmtQuery.execute("USE `" + schema + "`");
						} catch (SQLException ex) {
							try {
								stmtQuery.close();
							} catch (Exception e) {
							}
							try {
								db.close();
							} catch (Exception e) {
							}
						}
					}
					if (db == null || db.isClosed()) {
						if (db != null) {
							try {
								db.close();
							} catch (SQLException ex) {
							}
						}
						connect();
						for (PrepStmt ps : stmtPreps.values()) {
							ps.clear();
						}
						if (i < CONNECT_NOTIFY_THRESHOLD) {
							System.err.println("MySQL Reconnect OK!");
						}
					}
					return;
				} catch (SQLException ex) {
					if (!isConnectionIssue(ex) || i == 0) {
						throw ex;
					} else if (i < CONNECT_NOTIFY_THRESHOLD) {
						ex.printStackTrace();
					}
				}
				Thread.sleep(CONNECT_INTERVAL);
			}
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Prepares an SQL statement for execution.
	 * 
	 * @param sql
	 *            SQL to prepare
	 * @param cacheKey
	 *            If not null, a key to cache the prepared statement under and
	 *            to use for subsequent retrievals of the object.
	 * @return prepared SQL statement
	 * @throws SQLException
	 */
	public PreparedStatement prepare(String sql, Object cacheKey)
			throws SQLException {
		if (debug) {
			System.out.println(sql);
		}
		SQLException exception = null;
		for (int i = MAX_QUERY_ATTEMPTS; --i >= 0;) {
			lock.lock();
			try {
				if (cacheKey != null) {
					PrepStmt ps = stmtPreps.get(cacheKey);
					if (ps == null) {
						ps = new PrepStmt(sql);
						stmtPreps.put(cacheKey, ps);
						return ps.stmt;
					} else if (!sql.equals(ps.sql)) {
						throw new SQLException(
								"Another statement already cached under "
										+ cacheKey, null, -1);
					} else if (ps.stmt != null) {
						try {
							ps.stmt.close();
						} catch (Exception e) {
						}
					}
					return ps.stmt = db.prepareStatement(sql);
				}
				return db.prepareStatement(sql);
			} catch (SQLException ex) {
				if (isConnectionIssue(ex)) {
					exception = ex;
					reconnect();
				} else if (isSyntaxIssue(ex)) {
					System.err.println("Invalid SQL:\n" + sql);
					throw ex;
				} else {
					System.err.println("SQL Error: " + ex.getErrorCode() + " "
							+ ex.getSQLState());
					throw ex;
				}
			} finally {
				lock.unlock();
			}
		}
		throw exception;
	}

	/**
	 * Returns the cached prepared SQL statement for execution.
	 * 
	 * @param cacheKey
	 *            The key used to cache the prepared statement
	 * @return prepared SQL statement or null if not cached yet
	 * @throws SQLException
	 */
	public PreparedStatement getPrepared(Object cacheKey) throws SQLException {
		SQLException exception = null;
		for (int i = MAX_QUERY_ATTEMPTS; --i >= 0;) {
			lock.lock();
			try {
				PrepStmt ps = stmtPreps.get(cacheKey);
				if (ps == null) {
					return null;
				} else if (ps.stmt != null) {
					return ps.stmt;
				}
				return ps.stmt = db.prepareStatement(ps.sql);
			} catch (SQLException ex) {
				if (isConnectionIssue(ex)) {
					exception = ex;
					reconnect();
				} else {
					System.err.println("SQL Error: " + ex.getErrorCode() + " "
							+ ex.getSQLState());
					throw ex;
				}
			} finally {
				lock.unlock();
			}
		}
		throw exception;
	}

	/**
	 * Executes update with sql string.
	 * 
	 * @param sql
	 * @return rows updated
	 * @throws SQLException
	 */
	public int update(String sql) throws SQLException {
		if (debug) {
			System.out.println(sql);
		}
		SQLException exception = null;
		for (int i = MAX_QUERY_ATTEMPTS; --i >= 0;) {
			lock.lock();
			try {
				return stmtUpdate.executeUpdate(sql);
			} catch (SQLException ex) {
				if (isConnectionIssue(ex)) {
					exception = ex;
					reconnect();
				} else if (isSyntaxIssue(ex)) {
					System.err.println("Invalid SQL:\n" + sql);
					throw ex;
				} else {
					System.err.println("SQL Error: " + ex.getErrorCode() + " "
							+ ex.getSQLState());
					throw ex;
				}
			} finally {
				lock.unlock();
			}
		}
		throw exception;
	}

	/**
	 * Execute Query with sql string, expects to return a resultset
	 * 
	 * @param sql
	 * @return a resultset
	 * @throws SQLException
	 */
	public ResultSet query(String sql) throws SQLException {
		if (debug) {
			System.out.println(sql);
		}
		SQLException exception = null;
		for (int i = MAX_QUERY_ATTEMPTS; --i >= 0;) {
			lock.lock();
			try {
				return stmtQuery.executeQuery(sql);
			} catch (SQLException ex) {
				if (isConnectionIssue(ex)) {
					exception = ex;
					reconnect();
				} else if (isSyntaxIssue(ex)) {
					System.err.println("Invalid SQL:\n" + sql);
					throw ex;
				} else {
					System.err.println("SQL Error: " + ex.getErrorCode() + " "
							+ ex.getSQLState());
					throw ex;
				}
			} finally {
				lock.unlock();
			}
		}
		throw exception;
	}

	/**
	 * Closes the connection
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		db.close();
	}

	/**
	 * Returns whether the connection is closed or not.
	 * 
	 * @return true if closed, false otherwise.
	 * @throws SQLException
	 */
	public boolean isClosed() throws SQLException {
		return db.isClosed();
	}

	/**
	 * Internal prepared statement record
	 */
	private final class PrepStmt {
		private final String sql;
		private PreparedStatement stmt;

		private PrepStmt(String sql) throws SQLException {
			this.sql = sql;
			stmt = db.prepareStatement(sql);
		}

		private void clear() {
			PreparedStatement stmt = this.stmt;
			this.stmt = null;
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
		}
	}

}
