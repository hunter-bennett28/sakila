import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Name: SakilaController.java
 * Author: Connor Black
 * Date: Jul. 17, 2020
 * Desc:
 */

public class SakilaController {

	//Connection Objects
	Connection connection = null;
	Statement statement = null;
	ResultSet result = null;
	//This is the controller that will interact with the database
	//Set up the calls you will need to make to the database

	public SakilaController() {

		try {
			//Set up connection to Sakila database
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sakila?useSSL=false&allowPublicKeyRetrieval=true", 
					"root","password");
			
		}
		catch(SQLException ex) {
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		catch(Exception ex) {
			System.out.println("Exception caught: " + ex.getMessage());
		}
	}
	
	//Tests connection to the database, Should select all actors if it worked
	public void testConnection() {
		try {
			statement = connection.createStatement();
			result = statement.executeQuery("SELECT * FROM sakila.actor;");
		
			//Print all actors
			while(result.next())
			{
				System.out.println(result.getString("actor_id")+
									", "+result.getString("first_name") +
						            ", " + result.getString("last_name") +
						            ", " + result.getString("last_update"));
			}
		} 
		catch (SQLException ex) {
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
	}
}
