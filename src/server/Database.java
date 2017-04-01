package server;
import java.sql.*;

/**
 * Handles connections to the Mysql database.
 * Each thread should request its own DatabaseConnection to use and close it promptly after use.
 * This pool design is to ensure thread-safe transactions. 
 * The Mysql should handle each separate connection atomically.
 * @author Shalin
 *
 */
public class Database {
	
	static final String DB_URL = "jdbc:mysql://localhost:3306/set_game";
	static final String USER = "root";
	static final String PASS = "vishnu1";
	
	/**
	 * Returns a DatabaseConnection object with a Connection already initialized
	 * @author		Shalin
	 * @return 		DatabaseConnection object with an open Connection
	 * @throws ClassNotFoundException 
	 */
	
	public static DatabaseConnection getConnection() throws SQLException, ClassNotFoundException{
	    Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
		return new DatabaseConnection(conn);
	}

}