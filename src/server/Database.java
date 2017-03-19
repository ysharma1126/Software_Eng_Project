package server;
import java.sql.*;
/**
 * Handles connections to the Mysql database.
 * @author Shalin
 *
 */
public class Database {
	
	static final String DB_URL = "jdbc:mysql://localhost/set_game";
	static final String USER = "root";
	static final String PASS = "vishnu1";
	
	public static DatabaseConnection getConnection() throws SQLException{
		Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
		return new DatabaseConnection(conn);
	}

}