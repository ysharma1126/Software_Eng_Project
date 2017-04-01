package server;
import java.sql.*;

/**
 * A single instance of a Connection to the database.
 * The main purpose of providing this class instead of the raw Connection object is
 * abstract away database queries with the functions in this class.
 * @author Shalin
 *
 */
public class DatabaseConnection {
	private Connection conn = null;
	
	/**
	 * Initializes the DatabaseConnection object with a Connection
	 * @param	c	The Connection object obtained from the database driver
	 * @author		Shalin
	 */
	public DatabaseConnection(Connection c){
		conn = c;
	}
	
	/**
	 * Closes a the Connection inside the DatabaseConnection object.
	 * Should be called right after completing a database query to avoid
	 * saturating the connection pool
	 * @author		Shalin
	 */
	public void close() throws SQLException{
		conn.close();
	}
	
	/**
	 * Checks if a given username,password pair exists in the database
	 * @author		Shalin
	 * @param	username	A String containing the client's username
	 * @param	password	A String containing the client's password
	 * @return 		true if the username,password pair is found, otherwise false
	 */
	public Boolean authenticateUser(String username, String password) throws SQLException{
		Statement stmt = conn.createStatement();
		String querystring = String.format(
				"SELECT * FROM user where username='%s' AND password='%s';", 
				username,
				password);
		ResultSet rs = stmt.executeQuery(querystring);
		Boolean result = rs.first();
		rs.close();
		stmt.close();
		return result;
	}
	
	public boolean addUser(String username, String password) throws SQLException {
		String querystring = "INSERT INTO user(username, password) " + "VALUES(?,?)";
		PreparedStatement preparedStmt = conn.prepareStatement(querystring);
		preparedStmt.setString(1, username);
		preparedStmt.setString(2, password);
		
		return(preparedStmt.execute());
	}
}
