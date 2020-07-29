import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Name: SakilaController.java
 * Author: Connor Black
 * Date: Jul. 17, 2020
 * Desc: This is the controller that interacts with the Sakila database for various
 * 			methods for selecting and altering data.
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

	public boolean addActor(String firstName, String lastName)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
			Date date = new Date();
			String lastUpdate = sdf.format(date.getTime());

			statement = connection.createStatement();
			int returnValue = statement.executeUpdate(
					"INSERT INTO actor (first_name, last_name, last_update)" +
							"VALUES ('" + firstName + "', '" + lastName + "', '" + lastUpdate + "');"
					);

			if(returnValue == 1)
				return true;
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: " + ex.getMessage());
		}

		return false;
	}


	public ResultSet getStores() {
		try {
			statement=connection.createStatement();
			ResultSet rs= statement.executeQuery("SELECT store_id\r\n" + 
					"FROM store;");

			return rs;
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: " + ex.getMessage());
		}

		return null;
	}

	public ResultSet getCategories() {
		try {
			statement=connection.createStatement();
			ResultSet rs= statement.executeQuery("SELECT name\r\n" + 
					"FROM category;");

			return rs;
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: " + ex.getMessage());
		}

		return null;
	}

	/*This gets a report based on the criteria passed, searches both dates inclusively
	 * If a field is left null then it will not be queried (and storeId=0)
	 */
	public ResultSet getReport(String category, Date startDate, Date endDate, int storeId)
	{
		try
		{

			//Construct the where statement
			String conditions="WHERE 1=1";

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			if(startDate!=null) {
				String sDate = sdf.format(startDate);
				conditions+=" and r.rental_date >= '"+ sDate +"'";
			}
			
			if(endDate!=null) {
				//Increment the end date by 1 as the date defaults to 00:00:00 UTC when querying a date
				String eDate = sdf.format(endDate);
				Calendar c = Calendar.getInstance();
				c.setTime(sdf.parse(eDate));
				c.add(Calendar.DATE, 1); //Add 1 day
				eDate = sdf.format(c.getTime());  
				conditions+=" and r.rental_date <= '"+ eDate +"'";
			}
			if(storeId!=0) {
				conditions+=" and store_id="+storeId;
			}
			if(category!=null) {
				conditions+=" and c.name='"+ category+"'";
			}
			//Query the database
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(
					//Select the film name, category, # of rentals, and amount earned
					"SELECT title AS Title, c.name AS Category, count(title) AS 'Rentals', LPAD(CONCAT('$',CAST(SUM(amount) AS DECIMAL(10,2))),10,' ') AS Income\r\n" + 
					"FROM rental r INNER JOIN payment p ON r.rental_id = p.rental_id\r\n" + 
					"LEFT JOIN inventory i ON r.inventory_id=i.inventory_id\r\n" + 
					"INNER JOIN film f ON i.film_id=f.film_id\r\n" + 
					"INNER JOIN film_category fc ON f.film_id=fc.film_id\r\n" + 
					"INNER JOIN category c ON fc.category_id= c.category_id\r\n" + 
					conditions+"\r\n" + 
					"GROUP BY title\r\n" + 
					"ORDER BY 4 DESC, 3 DESC;"
					);
			return rs;
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: " + ex.getMessage());
		}

		//Error occurred
		return null;
	}
}
