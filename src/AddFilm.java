/**
 * Program Name: AddFilm.java
 * Purpose:
 * Coder: Hunter Bennett
 * Date: Jul 21, 2020		
 */

import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;

public class AddFilm extends JPanel implements SakilaTab
{
	final int MIN_RENTAL_DURATION = 3;
	final int MAX_RENTAL_DURATION = 7;
	
	JTextField title, description, filmLength, replacementCost, actors, selectedActorsField;
	JComboBox<String> releaseYear, language, rentalDuration, rentalRate, rating;
	JList<String> actorsList, selectedActors;
	Vector<String> selectedActorsVector;
	JCheckBox trailers, commentaries, deletedScenes, behindScenes;
	SakilaHome home;
	JButton addFilm, addActor, removeActor, resetForm;
	JScrollPane actorsScrollPane;
	int width, height;
	AddFilm thisPanel;
	DefaultListModel<String> actorsModel;
	
	public AddFilm(SakilaHome home)
	{
		super(new GridLayout(1, 2, 10, 10));
		this.home = home;
		this.width = home.WINDOW_WIDTH + 200;
		this.height = 400;
		thisPanel = this;
		
		//Set Up Panels
		JPanel leftPanel = new JPanel(new GridLayout(8, 2, 10, 10));
		JPanel rightPanel = new JPanel(new GridLayout(8, 2, 10, 10));

		/* Set up Left panel */
		
		//Title Row
		leftPanel.add(new JLabel("Title:", JLabel.RIGHT));
		title = new JTextField();
		leftPanel.add(title);
		
		//Description Row
		leftPanel.add(new JLabel("Description:", JLabel.RIGHT));
		description = new JTextField();
		leftPanel.add(description);
		
		//Release Year Row
		leftPanel.add(new JLabel("Release Year:", JLabel.RIGHT));
		releaseYear = populateReleaseYears();
		leftPanel.add(releaseYear);
		
		//Language Row
		leftPanel.add(new JLabel("Language:", JLabel.RIGHT));
		language = new JComboBox<String>(home.controller.getLanguages());
		leftPanel.add(language);
		
		//Rental Duration Row
		leftPanel.add(new JLabel("Rental Duration (Days):", JLabel.RIGHT));
		rentalDuration = populateRentalDurations();
		leftPanel.add(rentalDuration);
		
		//Rental Rate Row
		leftPanel.add(new JLabel("Rental Rate: $", JLabel.RIGHT));
		rentalRate = populateRentalRates();
		leftPanel.add(rentalRate);
		
		//Length Row
		leftPanel.add(new JLabel("Length (Minutes):", JLabel.RIGHT));
		filmLength = new JTextField();
		leftPanel.add(filmLength);
		
		//Replacement Cost Row
		leftPanel.add(new JLabel("Replacement Cost: $", JLabel.RIGHT));
		replacementCost = new JTextField();
		leftPanel.add(replacementCost);
		
		/* Set up Right panel */
		
		//Rating Row
	  rightPanel.add(new JLabel("Rating:", JLabel.RIGHT));
		rating = populateRatings();
		rightPanel.add(rating);
		
		//Trailers Row
		rightPanel.add(new JLabel("Trailers", JLabel.RIGHT));
		trailers = new JCheckBox();
		rightPanel.add(trailers);
		
		//Commentaries Row
		rightPanel.add(new JLabel("Commentaries", JLabel.RIGHT));
		commentaries = new JCheckBox();
		rightPanel.add(commentaries);
		
		//Deleted Scenes Row
		rightPanel.add(new JLabel("Deleted Scenes", JLabel.RIGHT));
		deletedScenes = new JCheckBox();
		rightPanel.add(deletedScenes);
		
		//Behind the Scenes Row
		rightPanel.add(new JLabel("Behind the Scenes", JLabel.RIGHT));
		behindScenes = new JCheckBox();
		rightPanel.add(behindScenes);
		
		//Actors Row
		rightPanel.add(new JLabel("Actors:", JLabel.RIGHT));
		actorsModel = new DefaultListModel<String>();
		selectedActors = new JList<String>(actorsModel);
		actorsScrollPane = new JScrollPane(selectedActors);
		rightPanel.add(actorsScrollPane);
		
		//Actor Row
		addActor = new JButton("Add Actor");
		addActor.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				//Retrieve available actors
				Vector<String> allActors = home.controller.getActors();
				String [] options = allActors.toArray(new String[allActors.size()]);
				
				//Pop window and get user choice of which actor to add
				String actorToAdd = (String)JOptionPane.showInputDialog(null, "Select Actor",
						"Add an Actor", JOptionPane.QUESTION_MESSAGE, new ImageIcon("images/actor.png"),
						options, options[0]
				);
				
				//If not already selected, add to model
				if(!actorsModel.contains(actorToAdd))
					actorsModel.addElement(actorToAdd);
			}
		});
		rightPanel.add(addActor);
		
		removeActor = new JButton("Remove Actor");
		removeActor.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				if(!actorsModel.isEmpty())
				{
					//Retrieve available actors
					//Vector<String> allActors = home.controller.getActors();
					//String [] options = allActors.toArray(new String[allActors.size()]);
					Object [] options	= actorsModel.toArray();
					
					//Pop window and get user choice of which actor to add
					String actorToAdd = (String)JOptionPane.showInputDialog(null, "Select Actor To Remove",
							"Remove an Actor", JOptionPane.QUESTION_MESSAGE, new ImageIcon("images/actor.png"),
							options, options[0]
					);
					
					actorsModel.removeElement(actorToAdd);
				}
				else
				{
					JOptionPane.showMessageDialog(home, "No actors added yet.");
				}
			}
		});
		rightPanel.add(removeActor);
		
		//Add Film/Reset Row
		addFilm = new JButton("Add Film");
		addFilm.addActionListener(new AddFilmListener());
		rightPanel.add(addFilm);
		
		resetForm = new JButton("Reset");
		resetForm.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//Clear all fields and reset combo boxes and check boxes to default
				title.setText("");
				description.setText("");
				releaseYear.setSelectedIndex(0);
				language.setSelectedIndex(0);
				rentalDuration.setSelectedIndex(0);
				rentalRate.setSelectedIndex(0);
				filmLength.setText("");
				replacementCost.setText("");
				rating.setSelectedIndex(0);
				trailers.setSelected(false);
				commentaries.setSelected(false);
				deletedScenes.setSelected(false);
				behindScenes.setSelected(false);
				actorsModel.clear();
			}
		});
		rightPanel.add(resetForm);
		
		//Add Panels to main Panel
		this.add(leftPanel);
		this.add(rightPanel);
	}
	
	/**
	 * Method:  populateReleaseYears()
	 * Purpose: Returns a combo box populated with every year from the current year to 50 years prior
	 * Accepts: Nothing
	 * Returns: Populated JComboBox of type String
	 */
	public JComboBox<String> populateReleaseYears()
	{
		Vector<String> validYears = new Vector<String>();
		
		//get valid index from 50 years ago to current year
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int minimumYear = currentYear - 50;
		
		//Add all numbers in index to Vector
		for (int year = currentYear; year >= minimumYear; year--)
		{
			validYears.add(Integer.toString(year));
		}
		
		//Create combo box containing vector and return
		JComboBox<String> completedBox = new JComboBox<String>(validYears);
//		completedBox.setSelectedIndex(validYears.size() - 1);
		
		return completedBox;
	}
	
	/**
	 * Method:  populateRentalDurations()
	 * Purpose: Returns a combo box populated with valid rental durations from 3 to 7
	 * Accepts: Nothing
	 * Returns: Populated JComboBox of type String
	 */
	public JComboBox<String> populateRentalDurations()
	{
		Vector<String> durations = new Vector<String>();
		
		//Add all valid durations to Vector
		for (int i = MIN_RENTAL_DURATION; i <= MAX_RENTAL_DURATION; i++)
		{
			durations.add(Integer.toString(i));
		}
		
		return new JComboBox<String>(durations);
	}
	
	/**
	 * Method:  populateRentalRates()
	 * Purpose: Returns a combo box populated with valid rental rates
	 * Accepts: Nothing
	 * Returns: Populated JComboBox of type String
	 */
	public JComboBox<String> populateRentalRates()
	{
		Vector<String> rates = new Vector<String>();
		
		//Add predetermined valid rental rates
		rates.add("4.99");
		rates.add("2.99");
		rates.add("0.99");
		
		return new JComboBox<String>(rates);
	}
	
	/**
	 * Method:  populateRatings()
	 * Purpose: Returns a combo box populated with valid ratings
	 * Accepts: Nothing
	 * Returns: Populated JComboBox of type String
	 */
	public JComboBox<String> populateRatings()
	{
		Vector<String> ratings = new Vector<String>();
		
		ratings.add("G");
		ratings.add("PG");
		ratings.add("PG-13");
		ratings.add("R");
		ratings.add("NC-17");

		return new JComboBox<String>(ratings);
	}

	/**
	 * Method:  getDimensions()
	 * Purpose: Method from SakilaTab that tells tab pane what size it wants to be
	 * Accepts: Nothing
	 * Returns: Dimension object containing desired tab size
	 */
	@Override
	public Dimension getDimensions()
	{
		return new Dimension(this.width, this.height);
	}
	
	private class AddFilmListener implements ActionListener
	{

		/**
		 * Method:  actionPerformed(ActionEvent e)
		 * Purpose: Event handler for the add film button. It will retireve and validate user
		 * 					input for all fields, and if valid, attempt to add the film to the database.
		 * Accepts: Nothing
		 * Returns: Dimension object containing desired tab size
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			//Variable Declarations
			Vector<String> errorStrings = new Vector<String>();
			Component firstOffender = null;
			String titleEntered;
			String descriptionEntered;
			int releaseYearEntered = 0;
			int languageEnteredId = 0;
			int rentalDurationEntered = 0;
			double rentalRateEntered = 0.0;
			int lengthEntered = 0;
			double replacementCostEntered = 0.0;
			String ratingEntered;
			boolean hasTrailers;
			boolean hasCommentaries;
			boolean hasDeletedScenes;
			boolean hasBehindTheScenes;
			String [] actorsEntered;
			
			//Ensure a title was entered
			titleEntered = title.getText();
			if(titleEntered.isEmpty())
			{
				errorStrings.add("Please enter a title.");
				
				if(firstOffender == null)
					firstOffender = title;
			}
			
			//Ensure a description was entered
			descriptionEntered = description.getText();
			if(descriptionEntered.isEmpty())
			{
				errorStrings.add("Please enter a description.");
				
				if(firstOffender == null)
					firstOffender = description;
			}
			
			//Get selections from combo boxes
			releaseYearEntered = Integer.parseInt((String)releaseYear.getSelectedItem());
			languageEnteredId = home.controller.getLanguageId((String)language.getSelectedItem());
			rentalDurationEntered = Integer.parseInt((String)rentalDuration.getSelectedItem());
			rentalRateEntered = Double.parseDouble((String)rentalRate.getSelectedItem());
			
			//Ensure a valid integer length was entered
			try
			{
				lengthEntered = Integer.parseInt(filmLength.getText());
			}
			catch (NumberFormatException ex)
			{
				errorStrings.add("Please enter an integer length.");
				
				if(firstOffender == null)
					firstOffender = filmLength;
			}
			
			//Ensure valid double cost was entered
			try
			{
				replacementCostEntered = Double.parseDouble(replacementCost.getText());
			}
			catch (NumberFormatException ex)
			{
				errorStrings.add("Please enter a monetary value replacement cost.");
				
				if(firstOffender == null)
					firstOffender = replacementCost;
			}
			
			ratingEntered = (String)rating.getSelectedItem();
			
			//Get check box selections
			hasTrailers = trailers.isSelected();
			hasCommentaries = commentaries.isSelected();
			hasDeletedScenes = deletedScenes.isSelected();
			hasBehindTheScenes = behindScenes.isSelected();
			
			//Create a features string based on selections
			String specialFeaturesString = getSpecialFeaturesString(
					hasTrailers, hasCommentaries, hasDeletedScenes, hasBehindTheScenes
			);
			
			//Create a string array of all actors selected
			actorsEntered = new String[actorsModel.size()];
			for (int i = 0; i < actorsModel.size(); i++)
			{
				actorsEntered[i]= actorsModel.get(i); 
			}
			
			//Ensure at least one actor selected
			if(actorsEntered.length == 0)
			{
				errorStrings.add("Please enter at least one actor.");
				
				if(firstOffender == null)
					firstOffender = addActor;
			}
			
			//If no errors, add film to the database
			if(errorStrings.size() == 0)
			{
				String resultString = home.controller.addFilm(
						titleEntered,
						descriptionEntered,
						releaseYearEntered,
						languageEnteredId,
						rentalDurationEntered,
						rentalRateEntered,
						lengthEntered,
						replacementCostEntered,
						ratingEntered,
						specialFeaturesString,
						actorsEntered
				);
				
				//Inform user of result of the add
				JOptionPane.showMessageDialog(home, resultString);
			}
			else
			{
				String errorString = "";
				for (int i = 0; i < errorStrings.size(); i++)
				{
					errorString += errorStrings.elementAt(i) + "\n";
				}
				
				//If there were errors, don't add and show what went wrong, and set focus to first failed field
				JOptionPane.showMessageDialog(null, errorString, "Form Incomplete", JOptionPane.ERROR_MESSAGE);
				
				//Set focus to the first field that was incorrect
				firstOffender.requestFocus();
			}
		}
		
		/**
		 * Method:  getSpecialFeaturesString( boolean hasTrailers, 
									boolean hasCommentaries, boolean hasDeletedScenes, boolean hasBehindTheScenes)
		 * Purpose: Formats a valid comma separated string of all selected feature options
		 * Accepts: Four booleans representing which features user deemed the film as having
		 * Returns: A comma separated String in accordance with the database constraints
		 */
		public String getSpecialFeaturesString( boolean hasTrailers, 
				boolean hasCommentaries, boolean hasDeletedScenes, boolean hasBehindTheScenes
		)
		{
			String formattedString = "";
			
			//Create a vector and add all selections to it
			Vector<String> selectedFeatureStrings = new Vector<String>();
			
			if(hasTrailers)
				selectedFeatureStrings.add("Trailers");
			
			if(hasCommentaries)
				selectedFeatureStrings.add("Commentaries");
			
			if(hasDeletedScenes)
				selectedFeatureStrings.add("Deleted Scenes");
			
			if(hasBehindTheScenes)
				selectedFeatureStrings.add("Behind the Scenes");
			
			//Print vector as a comma separated String
			for (int i = 0; i < selectedFeatureStrings.size(); i++)
			{
				//add a comma if not the first entry
				if(!formattedString.isEmpty())
					formattedString += ",";
				
				formattedString += selectedFeatureStrings.get(i);
			}
			
			return formattedString;
		}
		
	}

}
