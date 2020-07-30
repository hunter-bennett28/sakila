import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * Name: SakilaController.java
 * Author: Connor Black
 * Date: Jul. 17, 2020
 * Desc: This is the controller that interacts with the Sakila database for various
 * 			methods for selecting and altering data.
 */

public class SakilaController 
{

	//Connection Objects
	Connection connection = null;
	Statement statement = null;
	ResultSet result = null;
	//This is the controller that will interact with the database
	//Set up the calls you will need to make to the database

	public void createConnection() throws SQLException
	{
		if(connection == null || connection.isClosed())
		{
			//Set up connection to Sakila database
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sakila?useSSL=false&allowPublicKeyRetrieval=true", 
					"root","password");
		}
	}

	//Tests connection to the database, Should select all actors if it worked
	public void closeConnection() 
	{
		try {
			//Close it in reverse order
			if(result!=null) {
				result.close();
			}
			if(statement!=null) {
				statement.close();
			}
			if(connection!=null) {
				connection.close();
			}
		} 
		catch (SQLException ex) {
			System.out.println("SQL Exception caught while closing database objects: " + ex.getMessage());
		}
	}

	public boolean addActor(String firstName, String lastName)
	{
		try
		{
			//Establish a connection to the db
			createConnection();

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
		finally 
		{
			//Close the connection to the db
			closeConnection();
		}
		return false;
	}

	//Returns the id value (store #) for every store in the database
	public Vector<String> getStores()
	{
		try 
		{
			//Establish a connection to the db
			createConnection();

			statement=connection.createStatement();
			result= statement.executeQuery("SELECT store_id\r\n" + 
					"FROM store;");
			
			//Load into vector
			Vector<String> stores=new Vector<String>();
			while(result.next()) 
			{
				stores.add(result.getString("name"));
			}

			return stores;
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: " + ex.getMessage());
		}
		finally 
		{
			//Close the connection to the db
			closeConnection();
		}
		return null;
	}

	//Returns all categories in the Sakila Database
	public Vector<String> getCategories()
	{
		try 
		{
			//Establish a connection to the db
			createConnection();

			statement=connection.createStatement();
			result= statement.executeQuery("SELECT name\r\n" + 
					"FROM category;");

			//Load into vector
			Vector<String> categories=new Vector<String>();
			categories.add("All");
			
			while(result.next()) 
			{
				categories.add(result.getString("name"));
			}
			
			return categories;
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: " + ex.getMessage());
		}
		finally {
			//Close the connection to the db
			closeConnection();
		}
		return null;
	}

	/*This gets a report based on the criteria passed, searches both dates inclusively
	 * If a field is left null then it will not be queried (and storeId=0)
	 */
	public ResultSet getFilmReport(String category, Date startDate, Date endDate, int storeId)
	{
		try
		{
			//Establish a connection to the db
			createConnection();

			//Construct the where statement
			String conditions="WHERE 1=1";

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			//Check which conditions have been passed
			if(startDate!=null) 
			{
				String sDate = sdf.format(startDate);
				conditions+=" and r.rental_date >= '"+ sDate +"'";
			}

			if(endDate!=null) 
			{
				//Increment the end date by 1 as the date defaults to 00:00:00 UTC when querying a date
				String eDate = sdf.format(endDate);
				Calendar c = Calendar.getInstance();
				c.setTime(sdf.parse(eDate));
				c.add(Calendar.DATE, 1); //Add 1 day
				eDate = sdf.format(c.getTime());  
				conditions+=" and r.rental_date <= '"+ eDate +"'";
			}
			if(storeId!=0)
			{
				conditions+=" and store_id="+storeId;
			}
			if(category!=null) 
			{
				conditions+=" and c.name='"+ category+"'";
			}
			//Query the database
			statement = connection.createStatement();
			result = statement.executeQuery(
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
			return result;
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: " + ex.getMessage());
		}
		finally
		{
			//Close the connection to the db
			closeConnection();
		}
		//Error occurred
		return null;
	}

	/*This gets a report on users based on how much income they generated or films they have rented
	 * Default sorts by rental
	 */
	public ResultSet getCustomerReport(int storeId, boolean isSortedByIncome)
	{
		try
		{
			//Establish a connection to the db
			createConnection();

			//Construct the where statement
			String conditions="WHERE 1=1";

			if(storeId!=0)
			{
				conditions+=" and store_id="+storeId;
			}
			//Query the database
			PreparedStatement statement = connection.prepareStatement("SELECT first_name AS 'First Name', c.last_name AS 'Last Name', LPAD(CONCAT('$',CAST(SUM(amount) AS DECIMAL(10,2))),10,' ') AS 'Rental Income', COUNT(r.customer_id) AS 'Rentals'\r\n" + 
					"FROM customer c INNER JOIN payment p\r\n" + 
					"ON c.customer_id=p.customer_id\r\n" + 
					"INNER JOIN rental r\r\n" + 
					"ON r.rental_id=p.rental_id\r\n" + 
					conditions+"\r\n" + 
					"GROUP BY c.customer_id\r\n" + 
					"ORDER BY ? DESC, ? DESC;");

			//If its sorted by income, then order by col 3 (income) then col 4 (rental amount)
			if(isSortedByIncome) 
			{
				statement.setInt(1, 3);
				statement.setInt(2, 4);
			}
			else 
			{
				//Default to order by rental quantity then income
				statement.setInt(1, 4);
				statement.setInt(2, 3);
			}

			result = statement.executeQuery();
			return result;
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: " + ex.getMessage());
		}
		finally
		{
			//Close the connection to the db
			closeConnection();
		}
		//Error occurred
		return null;
	}
	
	/**
	 * Method Name: getActorIdByName(String firstName, String lastName)
	 * Purpose: retrieves the integer id of actor with given name from database
	 * Accepts: a String first name, a String last name
	 * Returns: the integer id of the actor in the database or -1 if not found
	 */
	public int getActorIdByName(String firstName, String lastName)
	{
		int id = -1;
		Statement getIdStatement = null;
		ResultSet results = null;
		
		try
		{
			//Use new objects because a statement can still be open while calling this
			getIdStatement = connection.createStatement();
			
			//Get actor Id, specifying by unique time stamp if possible for duplicates
			results = getIdStatement.executeQuery(
					"SELECT actor_id FROM actor WHERE last_name = '" + lastName + "' "
					+ "AND first_name = '" + firstName + "';"
			);
			
			//If a value retrieved, get 
			if(results.next())
				id = results.getInt(1);
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		finally 
		{
			try
			{
				if(results != null)
					results.close();
				
				if(getIdStatement != null)
					getIdStatement.close();
				
			} 
			catch(SQLException ex)
			{
				System.out.println("SQL Exception caught: " + ex.getMessage());
			}	
		}
		
		return id;
	}
	
	/**
	 * Method Name: getFilmIdByTitle(String title)
	 * Purpose: retrieves the integer id of a film with given title from database
	 * Accepts: a String title that is the film to search for
	 * Returns: the integer id of the film in the database or -1 if not found
	 */
	public int getFilmIdByTitle(String title)
	{
		int id = -1;
		Statement getIdStatement = null;
		ResultSet results = null;
		try
		{
			getIdStatement = connection.createStatement();
			results = getIdStatement.executeQuery(
					"SELECT film_id FROM film WHERE title = '" + title + "';"
			);
			
			//If a value retrieved, get 
			if(results.next())
				id = results.getInt(1);
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		finally 
		{
			try
			{
				if(results != null)
					results.close();
				
				if(getIdStatement != null)
					getIdStatement.close();
				
			} 
			catch(SQLException ex)
			{
				System.out.println("SQL Exception caught: " + ex.getMessage());
			}
		}
		
		return id;
	}
	
	/**
	 * Method Name: addActor(String firstName, String lastName, String movieTitle)
	 * Purpose: Adds an actor the the database in a transaction format, as well as an entry
	 * 					to the film_actor junction table if a film the actor is in is provided
	 * Accepts: a String first name, a String last name, and an optional String title of the movie they are in
	 * Returns: A String describing the outcome of the function
	 */
	public String addActor(String firstName, String lastName, String movieTitle)
	{
		String errorMessage = "Actor not added.";
		try
		{
			createConnection();
			//Check to see if actor already exists in the database before adding
			if(getActorIdByName(firstName, lastName) == -1) //if it doesn't exist, add it
			{
				//Get formatted date for update field
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
				Date date = new Date();
				String lastUpdate = sdf.format(date.getTime());
				
				//Begin transaction
				connection.setAutoCommit(false);
				
				//Add the actor to the database
				statement = connection.createStatement();
				int addActorReturnValue = statement.executeUpdate(
					"INSERT INTO actor (first_name, last_name, last_update)" +
					"VALUES ('" + firstName + "', '" + lastName + "', '" + lastUpdate + "');"
				);
				
				//If a movie title provided, and the actor insert worked, add to the junction table
				if(movieTitle.length() > 0 && addActorReturnValue > 0)
				{
					//Get ids to connect in the junction table
					int actorId = getActorIdByName(firstName, lastName);
					int filmId = getFilmIdByTitle(movieTitle);
					
					//Ensure both ids were correctly found
					if(actorId != -1 && filmId != -1)
					{
						//Add to the junction table
						int addJunctionReturnValue = statement.executeUpdate(
								"INSERT INTO film_actor VALUES (" + actorId + ", " + filmId + ", '" + lastUpdate +"');"
						);
						
						//If it worked, commit, otherwise roll back the whole transaction
						if(addJunctionReturnValue > 0)
						{
							connection.commit();
							return "Actor added successfully!";
						}
						else
						{
							//Roll back transaction if anything failed
							connection.rollback();
						}
					}
				}
				else //if no movie provided, just commit the actor add
				{
					connection.commit();
					return "Actor added successfully!";
				}
			}
			else //Actor already existed in database so it was not added
			{
				return "Actor already exists in the database.";
			}
			
		}
		catch(SQLException ex)
		{
			errorMessage += " SQL Exception: " + ex.getMessage();
		}
		catch(Exception ex)
		{
			errorMessage += " Exception: " + ex.getMessage();
		}
		finally
		{
			closeConnection();
		}
		
		return errorMessage;
	}

	/**
	 * Method Name: getFilms()
	 * Purpose: Retrieves a list of all film titles in the database
	 * Accepts: Nothing
	 * Returns: A String Vector of all film titles found
	 */
	public Vector<String> getFilms()
	{
		//Create vector and add a blank field as a default of none selected
		Vector<String> allFilms = new Vector<String>();
		allFilms.add("");
		try
		{
			createConnection();
			statement = connection.createStatement();
			result = statement.executeQuery("SELECT title FROM film");

			while(result.next())
			{
				//Add a new pair to films with the ID and title
				allFilms.add(result.getString(1));
			}

			return allFilms;
		}
		catch (SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		finally
		{
			closeConnection();
		}

		return allFilms;
	}
}
