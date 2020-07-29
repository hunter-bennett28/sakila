
/**
 * Name: SakilaController.java
 * Author: Connor Black, Hunter Bennett, Taylor DesRoches, James Dunton
 * Date: Jul. 17, 2020
 * Desc: This is the controller that interacts with the Sakila database for various
 * 			methods for selecting and altering data.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class SakilaController {

	//Connection Objects
	Connection connection = null;
	Statement statement = null;
	ResultSet result = null;

	public SakilaController() {

		try {
			//Set up connection to Sakila database
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sakila?useSSL=false&allowPublicKeyRetrieval=true", 
					"root","password");
		}
		catch(SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: " + ex.getMessage());
		}
	}
	
	//Tests connection to the database, Should select all actors if it worked
	public void testConnection()
	{
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
		catch (SQLException ex)
		{
			System.out.println("SQL Exception caught: " + ex.getMessage());
		}
		finally
		{
			try
			{
				if(result != null)
					result.close();
				
				if(statement != null)
					statement.close();
			}
			catch (SQLException ex)
			{
				System.out.println("SQL Exception caught: " + ex.getMessage());
			}
		}
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
			try
			{
				if(statement != null)
					statement.close();
				
				//Turn connection back off "transaction mode"
				connection.setAutoCommit(true);
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
			try
			{
				if(result != null)
					result.close();
				
				if(statement != null)
					statement.close();
			}
			catch (SQLException ex)
			{
				System.out.println("SQL Exception caught: " + ex.getMessage());
			}
		}

		return allFilms;
	}
}
