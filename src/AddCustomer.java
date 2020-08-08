/**
 * Program Name: AddCustomer.java
 * Purpose: GUI Tab for adding a new Customer to the database
 * Coder: Connor Black, Hunter Bennett, Taylor DesRoches, James Dunton
 * Date: Aug. 4, 2020
 */

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;

public class AddCustomer extends JPanel implements SakilaTab
{
	SakilaHome home;
	int width, height;
	AddCustomer thisPanel;

	/* Components */
	JTextField firstName, lastName, email, address, address2, district, postalCode, phone;
	JComboBox<String> city, country;
	JButton addCustomer, resetForm; 
	JCheckBox activeCustomer;

	/**
	 * Method Name: AddCustomer
	 * Purpose:  To Create and populate the GUI for the Add Customer Tab
	 * Accepts: An instance of the Sakila Home
	 * Returns: Void
	 */	
	public AddCustomer(SakilaHome home)
	{
		super(new GridLayout(1, 2, 10, 10));
		this.home = home;
		this.width = home.WINDOW_WIDTH;
		this.height = 300;
		thisPanel = this;

		//Set Up Panels
		JPanel leftPanel = new JPanel(new GridLayout(6, 2, 10, 10));
		JPanel rightPanel = new JPanel(new GridLayout(6, 2, 10, 10));

		/* Set up Left panel */

		//First Name Row
		leftPanel.add(new JLabel("First Name:", JLabel.RIGHT));
		firstName = new JTextField();
		leftPanel.add(firstName);

		//Last Name Row
		leftPanel.add(new JLabel("Last Name:", JLabel.RIGHT));
		lastName = new JTextField();
		leftPanel.add(lastName);

		//Address Row
		leftPanel.add(new JLabel("Address:", JLabel.RIGHT));
		address = new JTextField(); 
		leftPanel.add(address);

		//Country Row
		leftPanel.add(new JLabel("Country:", JLabel.RIGHT));
		country = new JComboBox<String>(populateCountries());
		leftPanel.add(country);

		MyItemListener actionListener = new MyItemListener();
		country.addItemListener(actionListener);

		//District Row
		leftPanel.add(new JLabel("District:", JLabel.RIGHT));
		district = new JTextField(); 
		leftPanel.add(district);

		//Phone Number Row
		leftPanel.add(new JLabel("Phone Number:", JLabel.RIGHT));
		phone = new JTextField();
		leftPanel.add(phone);

		/* Set up right panel */

		//Email row
		rightPanel.add(new JLabel("Email:", JLabel.RIGHT));
		email = new JTextField();
		rightPanel.add(email);

		//Active Customer Row
		rightPanel.add(new JLabel("Active Customer:", JLabel.RIGHT));
		activeCustomer = new JCheckBox();
		rightPanel.add(activeCustomer);

		//Address Row
		rightPanel.add(new JLabel("Address 2:", JLabel.RIGHT));
		address2 = new JTextField(); 
		rightPanel.add(address2);

		//City Row
		rightPanel.add(new JLabel("City:", JLabel.RIGHT));
		city = new JComboBox<String>(populateBlank());
		rightPanel.add(city);

		//Postal Code Row
		rightPanel.add(new JLabel("Postal Code:", JLabel.RIGHT));
		postalCode = new JTextField();
		rightPanel.add(postalCode);

		//Customer Buttons Row
		addCustomer = new JButton("Add Customer");
		addCustomer.addActionListener(new AddCustomerListener());

		resetForm = new JButton("Reset");
		resetForm.addActionListener(new ActionListener()
		{	
			/**
			 * Method Name: actionPerformed(ActionEvent e)
			 * Purpose: Event handler that resets all forms to their default state
			 * Accepts: an ActionEvent with the event information
			 * Returns: Void
			 */
			@Override
			public void actionPerformed(ActionEvent e)
			{
				resetCustomerGUI();
			}
		});

		rightPanel.add(addCustomer); 
		rightPanel.add(resetForm);

		//Add Panels to main Panel
		this.add(leftPanel);
		this.add(rightPanel);

	}

	/**
	 * Method Name: populateBlank()
	 * Purpose: To populate the select cities tab with "Select Country"
	 * Accepts: void
	 * Returns: a vector of 1 string 
	 */	
	private Vector<String> populateBlank()
	{
		Vector<String> cities = new Vector<String>(1);
		cities.add(0, "Select a Country");
		return cities; 
	}

	/**
	 * Method Name: populateCountries()
	 * Purpose: to populate the countries drop down with countries in the database
	 * Accepts: void
	 * Returns: vector of country names
	 */	
	private Vector<String> populateCountries()
	{
		//Fetch list of countries and load them into countries combo box
		Vector<String> countries = home.controller.getCountries();
		countries.add(0,"Select a Country");
		return countries;
	}

	/**
	 * Method Name: populateCities
	 * Purpose:  once country has been selected, provide the cities for that country 
	 * Accepts: the index number of the country selected
	 * Returns: a string array of city names
	 */	
	private String[] populateCities(int countryIndex)
	{
		//Fetch list of cities and load them into cities combo box
		String[] cities = home.controller.getCities(countryIndex);
		return cities;
	}

	/**
	 * Method Name: getDimensions
	 * Purpose: Method from SakilaTab to tell TabPane what size this tab wants to be
	 * Accepts: void
	 * Returns: dimension
	 */	
	@Override
	public Dimension getDimensions()
	{
		return new Dimension(this.width, this.height);
	}


	private class AddCustomerListener implements ActionListener
	{

		/**
		 * Method:  actionPerformed(ActionEvent e)
		 * Purpose: Event handler for the add customer button. It will retrieve and validate user
		 * 					input for all fields, and if valid, attempt to add the address to the database
		 *          then using the primary key from the address, attempt to add the customer.
		 * Accepts: Nothing
		 * Returns: Dimension object containing desired tab size
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			//Variable Declarations
			Vector<String> errorStrings = new Vector<String>();
			Component firstOffender = null;

			//Customer Fields
			String firstNameEntered, lastNameEntered;
			boolean activeEntered; 

			//Address Fields
			String addressEntered, districtEntered, phoneEntered, cityNameEntered, countryNameEntered;
			int cityIdEntered = -1, countryIdEntered = -1;

			//Ensure a first name was entered
			firstNameEntered = firstName.getText();
			if(firstNameEntered.isEmpty())
			{
				errorStrings.add("Please enter a first name.");

				if(firstOffender == null)
					firstOffender = firstName;
			}

			//Ensure a last name was entered
			lastNameEntered = lastName.getText();
			if(lastNameEntered.isEmpty())
			{
				errorStrings.add("Please enter a last name.");

				if(firstOffender == null)
					firstOffender = lastName;
			}

			//Email can be null

			//Check if active customer 
			activeEntered = activeCustomer.isSelected(); 

			//Ensure an address was entered
			addressEntered = address.getText();
			if(addressEntered.isEmpty())
			{
				errorStrings.add("Please enter an address.");

				if(firstOffender == null)
					firstOffender = address;
			}

			//address 2 can be null

			countryNameEntered = (String) country.getSelectedItem(); 
			if(country.getSelectedIndex() == 0)				
			{
				errorStrings.add("Please choose a country.");

				if(firstOffender == null)
					firstOffender = country;
			}
			//Get the country ID
			else
			{
				countryIdEntered = home.controller.getCountryIdByName(countryNameEntered);
			}

			//Get selections from combo boxes
			cityNameEntered = (String) city.getSelectedItem();
			if(city.getSelectedIndex() == 0) //if blank or prompt selected, incorrect				
			{
				errorStrings.add("Please choose a city.");

				if(firstOffender == null)
					firstOffender = city;
			}
			//Get the City ID
			else
			{
				cityIdEntered = home.controller.getCityIdByName(cityNameEntered, countryIdEntered);
			}

			//Ensure a district was entered
			districtEntered = district.getText();
			if(addressEntered.isEmpty())
			{
				errorStrings.add("Please enter a district.");

				if(firstOffender == null)
					firstOffender = district;
			}

			//Postal can be null

			//Ensure a phone number was entered
			phoneEntered = phone.getText();
			if(phoneEntered.isEmpty())
			{
				errorStrings.add("Please enter a phone number.");

				if(firstOffender == null)
					firstOffender = phone;
			}

			//If no errors, add first the address
			//FK needed for city_Id
			if(errorStrings.size() == 0)
			{
				String resultString = home.controller.addCustomer(
						firstNameEntered,
						lastNameEntered, 
						email.getText(), 
						activeEntered,
						addressEntered,
						address2.getText(),
						cityNameEntered,
						cityIdEntered, 
						countryNameEntered,
						countryIdEntered,
						districtEntered,
						phoneEntered,
						postalCode.getText());

				//Inform user of result of the add
				JOptionPane.showMessageDialog(home, resultString);

				//call to clear all dialogs if succeeded
				resetCustomerGUI();

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
	}

	/**
	 * Method Name: MyItemListener
	 * Purpose: To register when a change has been made on the country JComboBox
	 * Accepts: void
	 * Returns: void
	 */	
	class MyItemListener implements ItemListener {
		// This method is called only if a new item has been selected.
		public void itemStateChanged(ItemEvent evt) {
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				// Item was just selected
				city.removeAllItems();
				DefaultComboBoxModel<String> cityModel = new DefaultComboBoxModel<String>(populateCities(country.getSelectedIndex()));
				city.setModel(cityModel);

			} 
		}
	}

	/**
	 * Method Name: ResetCustomerGUI
	 * Purpose:  Sets the add customer GUI interface back to empty 
	 * Accepts: null
	 * Returns: null
	 */	
	private void resetCustomerGUI()
	{
		//call to clear all dialogs 
		firstName.setText("");
		lastName.setText("");
		email.setText("");
		activeCustomer.setSelected(false);
		address.setText("");
		address2.setText("");
		district.setText("");
		country.setSelectedIndex(0);
		postalCode.setText("");
		phone.setText(""); 

		//Set City back to "Select a Country"
		city.removeAllItems();
		DefaultComboBoxModel<String> cityModel = new DefaultComboBoxModel<String>(populateBlank());
		city.setModel(cityModel);
	}
}