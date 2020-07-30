import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

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
		if(connection == null)
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
		
	/**
	 * Method Name: getActorIdByName(String firstName, String lastName)
	 * Purpose: retrieves the integer id of actor with given name from database
	 * Accepts: a String first name, a String last name
	 * Returns: the integer id of the actor in the database or -1 if not found
	 */
	public int getActorIdByName(String firstName, String lastName)
	{
		int id = -1;
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
			
			//Get actor Id, specifying by unique time stamp if possible for duplicates
//			results = getIdStatement.executeQuery(
//					"SELECT actor_id FROM actor WHERE last_name = '" + lastName + "' "
//					+ "AND first_name = '" + firstName + "';"
//			);
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
				if(prepStatement != null)
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
	
	public Vector<String> getLanguages()
	{
			Vector<String> languages = new Vector<String>();
			try
			{
				statement = connection.createStatement();
				result = statement.executeQuery("SELECT name FROM language;");

				while(result.next())
				{
					//Add a new pair to films with the ID and title
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

			return languages;
	}
	
	public Vector<String> getActors()
	{
		Vector<String> actors = new Vector<String>();
		try
		{
			statement = connection.createStatement();
			result = statement.executeQuery("SELECT last_name, first_name FROM actor;");

			while(result.next())
			{
				//Add a new pair to films with the ID and title
				actors.add(result.getString(1) + ", " + result.getString(2));
			}

			Collections.sort(actors);
			return actors;
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

		return actors;
	}
	
	public int getLanguageId(String language)
	{
		int id = -1;
		Statement getIdStatement = null;
		ResultSet results = null;
		try
		{
			getIdStatement = connection.createStatement();
			results = getIdStatement.executeQuery(
					"SELECT language_id FROM language WHERE name = '" + language + "';"
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

	public String addFilm(String title, String description, int releaseYear,
			int languageId, int rentalDuration, double rentalRate, int length,
			double replacementCost, String rating, String specialFeatures, String[] actors)
	{
		String errorMessage = "Film not added.";
		try
		{
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
				PreparedStatement prepStatement = connection.prepareStatement(sqlFilmInsert);
				
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

//					+ "VALUES ("
//					+ "'" + addQuoteEscapes(title) + "', " 
//					+ "'" + addQuoteEscapes(description) + "', "
//					+ releaseYear + ", "
//					+ languageId + ", "
//					+ rentalDuration + ", "
//					+ rentalRate + ", "
//					+ length + ", "
//					+ replacementCost + ", "
//					+ "'" + rating + "', "
//					+ "'" + specialFeatures + "', "
//					+ "'" + lastUpdate + "');";
				
				
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
//					else
//					{
//						//Roll back transaction if anything failed
//						connection.rollback();
//					}
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
				if(statement != null)
					statement.close();
				
				if(prepStatement != null)
					prepStatement.close();
				
				connection.rollback();
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
	 * Method:  addQuoteEscapes(String str)
	 * Purpose: Escapes any single quotations in string with a second to fit SQL syntax
	 * Accepts: A string to escape single quotes in
	 * Returns: The string with any single quotes escaped with a second
	 */
	public String addQuoteEscapes(String str)
	{
		return str.replace("'", "''");
	}
}
