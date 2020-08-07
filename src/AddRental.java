
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Name: AddRental.java
 * Author: James Dunton, Hunter Bennett
 * Descrption: GUI component for adding rentals to the database
 * Date: Aug 2, 2020
 */

public class AddRental extends JPanel implements SakilaTab
{

	//create gui components and declare variables
	JButton addButton;
	JComboBox<String> customerName, store, salesNumber;
	JList<String> movies;
	JPanel moviePanel, userSelectionPanel;
	DefaultListModel<String> allMoviesModel;

	int width, height;
	SakilaHome home;

	public AddRental(SakilaHome home) {

		super(new BorderLayout());

		// create panels to add to the layout
		userSelectionPanel = new JPanel(new GridLayout(6,1,10,10));
		moviePanel = new JPanel(new BorderLayout());
		this.add(userSelectionPanel,BorderLayout.WEST);
		this.add(moviePanel,BorderLayout.CENTER);

		this.home = home;
		this.width = home.WINDOW_WIDTH;
		this.height = home.WINDOW_HEIGHT;

		// add store combo box to layout
		userSelectionPanel.add(new JLabel("Store #:" , JLabel.CENTER));
		store = new JComboBox<String>(home.controller.getStores());
		store.setEditable(false);
		userSelectionPanel.add(store);

		// add customerName combo box to layout
		userSelectionPanel.add(new JLabel("Customer Name:" , JLabel.CENTER));
		customerName = new JComboBox<String>(home.controller.getCustomer());
		customerName.setEditable(false);
		userSelectionPanel.add(customerName);

		// add salesNumber combo box to layout
		userSelectionPanel.add(new JLabel("Sales Rep #:", JLabel.CENTER));
		Vector<String> employees = home.controller.getSalesStaff(Integer.parseInt(store.getSelectedItem().toString()));
		salesNumber = new JComboBox<String>(employees);
		salesNumber.setEditable(false);
		userSelectionPanel.add(salesNumber);

		// add salesNumber JList to layout
		moviePanel.add(new JLabel("Movie Selection (ctrl+click to select multiple movies):", JLabel.CENTER),BorderLayout.PAGE_START);
		Vector <String> moviesForRent = home.controller.getRentableFilms(Integer.parseInt(store.getSelectedItem().toString()));
		allMoviesModel = new DefaultListModel<String>();
		for (int i = 0; i < moviesForRent.size(); i++)
		{
			allMoviesModel.add(i, moviesForRent.get(i));
		}
		movies = new JList<String>(allMoviesModel);
		movies.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		movies.setLayoutOrientation(JList.VERTICAL);
		JScrollPane movieScroll = new JScrollPane(movies);


		//add button to add movie rental to the database
		moviePanel.add(movieScroll,BorderLayout.CENTER);
		addButton = new JButton("Add Rental");
		this.add(addButton,BorderLayout.SOUTH);


		//add item listener to change the movie list and employee list depending on the store that's selected
		store.addItemListener(new ItemListener() {

			/**
			 * Method Name: itemStateChanged(ItemEvent e)
			 * Purpose: Event handler that performs a new query on the films and employees in the database
			 * 					and updates the respective combo boxes
			 * Accepts: an ItemEvent with the event information
			 * Returns: Void
			 */
			@Override
			public void itemStateChanged(ItemEvent e)
			{					

				String storeNum = (String) store.getSelectedItem();

				moviesForRent.removeAllElements();

				moviesForRent.addAll(home.controller.getRentableFilms(Integer.parseInt(storeNum))); 
				Vector <String> salesNum = home.controller.getSalesStaff(Integer.parseInt(storeNum));

				allMoviesModel.clear();
				for (int i = 0; i < moviesForRent.size(); i++)
				{
					allMoviesModel.add(i, moviesForRent.get(i));
				}

				salesNumber.removeAllItems();
				for (int i = 0; i < salesNum.size(); i++) 
				{
					salesNumber.addItem(salesNum.get(i));
				}		
			}
		});


		addButton.addActionListener(new ActionListener() {
			/**
			 * Method Name: actionPerformed(ActionEvent e)
			 * Purpose: Adds a movie rental to the database based on their selection
			 * Accepts: an ActionEvent with the event information
			 * Returns: Void
			 */
			@Override
			public void actionPerformed(ActionEvent e)
			{
				List<String> movieRentals = movies.getSelectedValuesList();
				//Validates whether the user has selected a movie to rent and asks them to select a movie
				//if they haven't.

				if (movieRentals.size() == 0)
					JOptionPane.showMessageDialog(home, "Please select a movie to rent.");

				//Validates whether the user has selected a customer to rent to
				//and tells them to select one if they haven't.
				else if(customerName.getSelectedItem().toString() == "")
					JOptionPane.showMessageDialog(home, "Please select a customer to rent a movie to.");

				//If validations validations are passed, perform the add
				else {
					//This string will print result to a message dialog box
					String resultString = "";

					//get info from the gui components to pass to 
					int salesClerkNum = Integer.parseInt(salesNumber.getSelectedItem().toString());
					int storeNum = Integer.parseInt(store.getSelectedItem().toString());
					String customerFirstName = customerName.getSelectedItem().toString();
					String customerLastName = customerFirstName.substring(0,customerFirstName.indexOf(','));
					customerFirstName = customerFirstName.substring(customerFirstName.indexOf(' ')+1);

					double currentBalance = home.controller.getBalance(customerFirstName,customerLastName);

					Vector<String> currentlyRentedMovies = new Vector<String>(home.controller.GetCurrentlyRentedMovies(customerFirstName, customerLastName));

					//check to see if the customer currently owes a ballance
					if (currentBalance < 0) {
						JOptionPane.showMessageDialog(home, "Balance for " + customerLastName + ", " + customerFirstName
								+ " is currently $" +  currentBalance
								+ ".\nPlease repay balance and try to rent again.");
					}
					//check to see if the customer has movies rented out already
					else if (currentlyRentedMovies.size()>0) {
						resultString += customerLastName + ", " + customerFirstName + " has currently rented the following:\n";

						for(String movie:currentlyRentedMovies) {
							resultString += movie+"\n";
						}
						resultString +="Please return these movies before proceeding to rent more.";
						JOptionPane.showMessageDialog(home, resultString);
					}
					//if the customer is ok to rent a movie, proceed with the rental
					else 
					{	
						resultString = "Movie rental(s) for " + customerFirstName + " " + customerLastName + ":";

						//loop through and add movies to rental
						for(String m: movieRentals)
						{
							resultString += home.controller.addRent(customerFirstName, customerLastName, m, salesClerkNum, storeNum);
						}
						//find balance in the return string
						double movieTotalBalance = 0.0;
						Matcher doubleFinder = Pattern.compile( "[-+]?\\d*\\.\\d+([eE][-+]?\\d+)?" ).matcher(resultString);

						while(doubleFinder.find()) {
							movieTotalBalance += Double.parseDouble(doubleFinder.group());
						}

						String doubleFormat = String.format("%.2f", movieTotalBalance);

						resultString += "\nA total balance of $"+ doubleFormat +" + applicable tax is due.";

						//show rental result and clear the fields that can be cleared and updated
						JOptionPane.showMessageDialog(home, resultString);
						movies.clearSelection();
						customerName.setSelectedIndex(0);
					}
				}
			}
		});
	}

	@Override
	public Dimension getDimensions()
	{
		return new Dimension(this.width, this.height);
	}	
}