import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	// Sesuaikan URL, Username, dan Password dengan konfigurasi MySQL lokal Anda
	private static final String DB_URL = "jdbc:mysql://localhost:3306/db_people";
	private static final String USER = "root";
	private static final String PASS = "";

	private Connection stmtConnection;
	private Statement stmt;

	public Database() {
		try {
			// Membuka koneksi ke database MySQL
			stmtConnection = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = stmtConnection.createStatement();
		} catch (SQLException e) {
			throw new RuntimeException("Koneksi Database Gagal: " + e.getMessage());
		}
	}

	// Method untuk query SELECT
	public ResultSet selectQuery(String sql) {
		try {
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// Method untuk query INSERT, UPDATE, DELETE
	public int insertUpdateDeleteQuery(String sql) {
		try {
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Statement getStatement() {
		return stmt;
	}
}