import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.TableModel;

/**
 * Name: SakilaController.java
 * Author: Connor Black, Hunter Bennett, Taylor DesRoches, James Dunton
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
	PreparedStatement prepStatement = null;

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
			if(result!=null)
				result.close();
			
			if(statement!=null)
				statement.close();
			
			if(prepStatement!=null)
				statement.close();

			if(connection!=null)
				connection.close();
		} 
		catch (SQLException ex) {
			System.out.println("SQL Exception caught while closing database objects: " + ex.getMessage());
		}
	}
	
	/**
	 * Method Name: getStores()
	 * Purpose: Retrieves a vector of all the stores
	 * Accepts: No params
	 * Returns: A vector of strings holding all the store_id fields in the store table
	 */	
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
				stores.add(result.getString("store_id"));
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

	/**
	 * Method Name: getCategories()
	 * Purpose: Retrieves a vector of all the movie categories
	 * Accepts: No params
	 * Returns: A vector of strings holding all the categories in the category table
	 */	
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

	/**
	 * Method Name: getFilmReport()
	 * Purpose: Retrieves data based on the films in inventory, most importantly how many times they were rented and how much money they make
	 * Accepts: string - category name
	 * 			date - the initial date that will be inclusively checked (starting at 00:00:00 UTC)
	 * 			date - the end date to be inclusively checked
	 * 			int - the store id represented in the database, if 0 is passed, all stores will be queried
	 * Returns: A table model representing the data returned from the query
	 */
	public TableModel getFilmReport(String category, Date startDate, Date endDate, int storeId)
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
			return DbUtils.resultSetToTableModel(result);
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
	 * Method Name: getCustomerReport()
	 * Purpose: Retrieves data based on the store the user is active with and will sort it appropriately
	 * Accepts: int, bool (represents how the data is sorted, default is by rental amount)
	 * Returns: A table model representing the data returned from the query
	 */
	public TableModel getCustomerReport(int storeId, boolean isSortedByIncome)
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
			return DbUtils.resultSetToTableModel(result);
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
		//Uses new objects because other functions with open objects call this
		PreparedStatement getIdStatement = null;
		ResultSet results = null;
		
		try
		{
			//Use new objects because a statement can still be open while calling this
			getIdStatement = connection.prepareStatement(
					"SELECT actor_id FROM actor WHERE last_name = ? AND first_name = ?;"
			);
			
			getIdStatement.setString(1, lastName);
			getIdStatement.setString(2, firstName);
			
			results = getIdStatement.executeQuery();
			
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
		PreparedStatement getIdStatement = null;
		ResultSet results = null;
		try
		{
			getIdStatement = connection.prepareStatement("SELECT film_id FROM film WHERE title = ?;");
			getIdStatement.setString(1, title);
			results = getIdStatement.executeQuery();
			
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
				prepStatement = connection.prepareStatement(
						"INSERT INTO actor (first_name, last_name, last_update) VALUES (?, ?, ?);"
				);
				
				prepStatement.setString(1, firstName);
				prepStatement.setString(2, lastName);
				prepStatement.setString(3, lastUpdate);
				
				int addActorReturnValue = prepStatement.executeUpdate();
				
				//If a movie title provided, and the actor insert worked, add to the junction table
				if(movieTitle.length() > 0 && addActorReturnValue > 0)
				{
					//Get ids to connect in the junction table
					int actorId = getActorIdByName(firstName, lastName);
					int filmId = getFilmIdByTitle(movieTitle);
					
					//Ensure both ids were correctly found
					if(actorId != -1 && filmId != -1)
					{
						statement = connection.createStatement();
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
			try
			{
				//Turn connection back off "transaction mode"
				connection.rollback();
				connection.setAutoCommit(true);
				closeConnection();
			}
			catch (SQLException ex)
			{
				errorMessage += " SQL Exception: " + ex.getMessage();
			}
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
			
			//Select all titles
			result = statement.executeQuery("SELECT title FROM film");

			while(result.next())
			{
				//Add title to the vector
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
		
	/**
	 * Method Name: getLanguages()
	 * Purpose: Retrieves a list of all languages in the database
	 * Accepts: Nothing
	 * Returns: A String Vector of all languages found
	 */
	public Vector<String> getLanguages()
	{
			Vector<String> languages = new Vector<String>();
			try
			{
				createConnection();
				statement = connection.createStatement();
				result = statement.executeQuery("SELECT name FROM language;");

				while(result.next())
				{
					//Add language name to the vector
					languages.add(result.getString(1));
				}

				return languages;
			}
			catch (SQLException ex)
			{
				System.out.println("SQL Exception caught: " + ex.getMessage());
			}
			finally
			{
				closeConnection();
			}

			return languages;
	}
	
	/**
	 * Method Name: getActors()
	 * Purpose: Retrieves a list of all actors in the database
	 * Accepts: Nothing
	 * Returns: A String Vector of actors found in 'lastName, firstName' format
	 */
	public Vector<String> getActors()
	{
		Vector<String> actors = new Vector<String>();
		try
		{
			createConnection();
			statement = connection.createStatement();
			result = statement.executeQuery("SELECT last_name, first_name FROM actor;");

			while(result.next())
			{
				//Add actor to vector in lastName, firstName format
				actors.add(result.getString(1) + ", " + result.getString(2));
			}

			//Sort on last name
			Collections.sort(actors);
			return actors;
		}
		catch (SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		finally
		{
			closeConnection();
		}

		return actors;
	}
	
	/**
	 * Method Name: getLanguageId(String language)
	 * Purpose: Retrieves the id of the given language from the database
	 * Accepts: a String name of a language
	 * Returns: the integer Id or -1 if not found
	 */
	public int getLanguageId(String language)
	{
		int id = -1;
		try
		{
			createConnection();
			statement = connection.createStatement();
			result = statement.executeQuery(
					"SELECT language_id FROM language WHERE name = '" + language + "';"
			);
			
			//If a value retrieved, get 
			if(result.next())
				id = result.getInt(1);
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		finally 
		{
			closeConnection();
		}
		
		return id;
	}

	/**
	 * Method Name: addFilm()
	 * Purpose: Adds a film to the database as well as the connections between it and its actors
	 * Accepts: title - the title of the movie
	 * 					description - a brief description of the movie
	 * 					releaseYear - the year it was released
	 * 					languageId  - the ID of the language the movie is in
	 * 					rentalDuration - the days between 3 and 7 the movie is rentable for
	 * 					rentalRate  - the monetary cost of renting the movie
	 * 					length 			- the length in minutes of the movie
	 * 					replacementCost - the cost of replacing the movie if lost/damaged
	 * 					rating			- the rating of the movie in enum(G, PG, PG-13, R, NC-17)
	 * 					specialFeatures - a comma separated list of special features
	 * 					actors			- an array of all actors in the film in 'lastName, firstName' format
	 * Returns: A String description of how the insert went
	 */
	public String addFilm(String title, String description, int releaseYear,
			int languageId, int rentalDuration, double rentalRate, int length,
			double replacementCost, String rating, String specialFeatures, String[] actors)
	{
		String errorMessage = "Film not added.";
		try
		{
			createConnection();
			//Check to see if film already exists in the database before adding
			if(getFilmIdByTitle(title) == -1) //if it doesn't exist, add it
			{
				//Get formatted date for update field
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
				Date date = new Date();
				String lastUpdate = sdf.format(date.getTime());
				
				//Begin transaction
				connection.setAutoCommit(false);
				
				//Create and populate prepared statement
				String sqlFilmInsert = 
						"INSERT INTO film (title, description, release_year, language_id, rental_duration, rental_rate, "
						+ "length, replacement_cost, rating, special_features, last_update) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
				prepStatement = connection.prepareStatement(sqlFilmInsert);
				
				int prepVariableIndex = 1;
				prepStatement.setString(prepVariableIndex++,  title);
				prepStatement.setString(prepVariableIndex++,  description);
				prepStatement.setInt(prepVariableIndex++,  releaseYear);
				prepStatement.setInt(prepVariableIndex++, languageId);
				prepStatement.setInt(prepVariableIndex++, rentalDuration);
				prepStatement.setDouble(prepVariableIndex++, rentalRate);
				prepStatement.setInt(prepVariableIndex++, length);
				prepStatement.setDouble(prepVariableIndex++, replacementCost);
				prepStatement.setString(prepVariableIndex++, rating);
				prepStatement.setString(prepVariableIndex++, specialFeatures);
				prepStatement.setString(prepVariableIndex++, lastUpdate);
				
				int addFilmReturnValue = prepStatement.executeUpdate();
				
				//If film insert worked, add to the junction table
				if(addFilmReturnValue > 0)
				{
					int filmId = getFilmIdByTitle(title);
					boolean allJunctionInsertsWorked = true;
					//Add all selected actors to junction table
					for (int i = 0; i < actors.length; i++)
					{
						int commaIndex = actors[i].indexOf(',');
						String firstName = actors[i].substring(commaIndex + 2);
						String lastName = actors[i].substring(0, commaIndex);
						
						int actorId = getActorIdByName(firstName, lastName);
						if(actorId == -1)
						{
							return "Film not added, actor " + firstName + " " + lastName + " does not exist.";
						}
						
						statement = connection.createStatement();
						String sqlString = "INSERT INTO film_actor VALUES (" + actorId + ", " + filmId + ", '" + lastUpdate + "');";
						int addJunctionReturnValue = statement.executeUpdate(sqlString);
						
						if(addJunctionReturnValue == -1)
							allJunctionInsertsWorked = false;
					}
					
					//Ensure all adds worked correctly
					if(allJunctionInsertsWorked)
					{
						connection.commit();
						return "Film added successfully!";
					}
				}
			}
			else //Actor already existed in database so it was not added
			{
				return "Film already exists in the database.";
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
			try
			{
				//Do a roll back in case it failed. If it didn't, will not undo the commit
				connection.rollback();
				//Turn connection back off "transaction mode"
				connection.setAutoCommit(true);
				closeConnection();
			}
			catch (SQLException ex)
			{
				errorMessage += " SQL Exception: " + ex.getMessage();
			}
		}
		
		return errorMessage;
	}

}