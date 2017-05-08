package de.dc.systems.tenhourswarn.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.dc.systems.tenhourswarn.mapper.MapperUtil;
import de.dc.systems.tenhourswarn.persistence.entities.Timer;

/**
 * @author Thomas Reno
 *
 */
public class TimerDAO {

	private static final String TABLE = "timer";

	private static final String DDL_CREATE =
			//
			"CREATE TABLE " + TABLE + " ("
			//
					+ "date DATE, "
					//
					+ "time TIME, "
					//
					+ "PRIMARY KEY (date));";

	private static final String DML_INSERT =
			//
			"INSERT INTO " + TABLE
			//
					+ " VALUES (?, ?);";

	private static final String SQL_SELECT_SQLITEMASTER = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?";;

	private static Connection connection;

	private boolean tableTimerExists = false;

	public TimerDAO() {
		try {
			if (!tablesExists())
				initialize();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public Timer insert(Timer timer) {
		return executeDML(DML_INSERT, timer);
	}

	public List<Timer> select() {
		try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
			StringBuilder builder = new StringBuilder("SELECT * FROM ");
			builder.append(Timer.class.getSimpleName().toLowerCase());
			ResultSet rs = stmt.executeQuery(builder.toString());
			return MapperUtil.mapToTimerList(rs);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	protected Connection getConnection() throws ClassNotFoundException, SQLException {
		if (null != connection && !connection.isClosed()) {
			return connection;
		}
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:thw.db");
		connection.setAutoCommit(false);
		return connection;
	}

	private void executeDDL(String statement) {
		try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
			stmt.execute(statement);
			conn.commit();
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	private Timer executeDML(String statement, Timer timer) {
		int number = 1;
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(statement)) {
			stmt.setObject(number++, timer.getDate());
			stmt.setObject(number, timer.getTime());
			if (0 == stmt.executeUpdate())
				throw new SQLException("No rows affected.");
			conn.commit();
			return timer;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void initialize() throws ClassNotFoundException, SQLException {
		if (!(tableTimerExists = tableExists(TABLE))) {
			executeDDL(DDL_CREATE);
			tableTimerExists = true;
		}
	}

	private boolean tableExists(String tableName) {
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_SQLITEMASTER)) {
			stmt.setString(1, tableName);
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (ClassNotFoundException | SQLException e) {
			return false;
		}
	}

	private boolean tablesExists() {
		return tableTimerExists;
	}
}
