package server;
import java.sql.*;

public class DatabaseConnection {
	private Connection conn = null;
	
	public DatabaseConnection(Connection c){
		conn = c;
	}
	
	public void close() throws SQLException{
		conn.close();
	}
	
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
}
