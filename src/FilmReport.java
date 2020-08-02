/**
 * Name: FilmReport.java
 * Author: Connor Black
 * Date: Jul. 22, 2020
 * Desc: This reports films based on a given search criteria 
 */
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


public class FilmReport extends JPanel implements SakilaTab {

	SakilaHome home;
	int width, height;
	JPanel searchPanel, userInputPanel;
	JComboBox<String> cbCategory, cbStores;
	JTextField txtStartDate,txtEndDate;
	JButton btnSearch;
	JTable resultTable;

	//Required for date formatting and searching
	String dateFormat=String.format("dd/mm/yyyy");
	JPanel page;
	final int MAX_DATE_LENGTH=10, FIRST_SLASH_INDEX=2, SECOND_SLASH_INDEX=5; //Max char for a date, and the char indexes of when to insert '/'
	final int BACKSPACE_KEY_CHAR=8; //Used to see if the user pressed backspace
	
	public FilmReport(SakilaHome home)
	{
		super(new BorderLayout());

		//Set up window
		this.home = home;
		this.width = 700;
		this.height = 250;

		//Construct window
		//Place table
		resultTable=new JTable();
		JScrollPane tableScrollPane=new JScrollPane(resultTable);
		this.add(tableScrollPane, BorderLayout.CENTER);

		//Create search panel
		searchPanel=new JPanel(new BorderLayout());
		searchPanel.add(new JLabel("Browse Rental Information", JLabel.CENTER), BorderLayout.PAGE_START);

		userInputPanel=new JPanel(new GridLayout(4,2,10,10));
		userInputPanel.add(new JLabel("Category"));

		//Fetch list of categories and load them into cbCategory
		Vector<String> categories=home.controller.getCategories();
		cbCategory=new JComboBox<String>(categories);
		userInputPanel.add(cbCategory);


		//Load store # into combo box
		userInputPanel.add(new JLabel("Store #"));

		//Check if data was returned and load it into a combo box
		Vector<String> stores = home.controller.getStores();
		cbStores=new JComboBox<String>(stores);
		userInputPanel.add(cbStores);

		//Set up the dates
		userInputPanel.add(new JLabel("Start Date"));
		//Set up formatting on the text fields


		//Display date format to start (and if the user leaves it empty)
		txtStartDate=new JTextField(dateFormat);
		//Add listener so if focus is lost, it will display the date prompt
		txtStartDate.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if(txtStartDate.getText().equals(dateFormat)) {
					txtStartDate.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(txtStartDate.getText().equals("")) {
					txtStartDate.setText(dateFormat);
				}
			}
		});
		
		//Add action listener so if the length is 2 or 5, it will add in the / for the date format
		txtStartDate.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				//format the date if it is the correct length and the user is not deleting their date by pressing backspace
				if((txtStartDate.getText().length()==FIRST_SLASH_INDEX || txtStartDate.getText().length()==SECOND_SLASH_INDEX) && !(e.getKeyChar()==BACKSPACE_KEY_CHAR))
					txtStartDate.setText(txtStartDate.getText()+'/');

				//Make sure the input value is within the max number of chars 
				if(txtStartDate.getText().length()>=MAX_DATE_LENGTH) //+1 the length as the key hasnt been input yet
					txtStartDate.setText(txtStartDate.getText().substring(0, MAX_DATE_LENGTH-1)); 
			}
			@Override
			public void keyPressed(KeyEvent e) { }
			@Override
			public void keyReleased(KeyEvent e) { }			
		});

		//Setup txtEndDate to act the same way
		txtEndDate=new JTextField(dateFormat);
		txtEndDate.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if(txtEndDate.getText().equals(dateFormat)) {
					txtEndDate.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(txtEndDate.getText().equals("")) {
					txtEndDate.setText(dateFormat);
				}
			}
		});

		//Override the key listener to add in '/' when enough numbers are input
		txtEndDate.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				//format the date if it is the correct length and the user is not deleting their date by pressing backspace
				if((txtEndDate.getText().length()==FIRST_SLASH_INDEX || txtEndDate.getText().length()==SECOND_SLASH_INDEX) && !(e.getKeyChar()==BACKSPACE_KEY_CHAR))
					txtEndDate.setText(txtEndDate.getText()+'/');

				//Make sure the input value is within the max number of chars 
				if(txtEndDate.getText().length()>=MAX_DATE_LENGTH)
					txtEndDate.setText(txtEndDate.getText().substring(0, MAX_DATE_LENGTH-1));
			}
			@Override
			public void keyPressed(KeyEvent e) { }
			@Override
			public void keyReleased(KeyEvent e) { }
		});

		userInputPanel.add(txtStartDate);
		userInputPanel.add(new JLabel("End Date"));
		userInputPanel.add(txtEndDate);

		searchPanel.add(userInputPanel);

		btnSearch=new JButton("Search");

		//Set page to equal the JPanel (used for popup dialogs)
		page=this;
		//Handle search query
		btnSearch.addActionListener(new SearchButtonListener()); 
		searchPanel.add(btnSearch,BorderLayout.PAGE_END);
		this.add(searchPanel, BorderLayout.LINE_START);
	}

	public class SearchButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//Check which data was provided
			String category=cbCategory.getSelectedItem().toString();
			if(category.equals("All")) {
				category=null;
			}

			String store=cbStores.getSelectedItem().toString();
			int storeId=0;
			if(!store.equals("All")) {
				storeId=Integer.parseInt(store);
			}

			Date startDate=null;
			//Get start date if one is given
			if(!txtStartDate.getText().equals(dateFormat)) {
				if(txtStartDate.getText().length()!=MAX_DATE_LENGTH) {
					JOptionPane.showMessageDialog(page, "Incorrect start date entered.");
					txtStartDate.requestFocus();
					return;
				}
				else {
					//Split up the date and convert it into a Calendar object
					String sDate[]=txtStartDate.getText().split("/");
					int day, month, year;
					try {
						//This will see if numbers were input
						day=Integer.parseInt(sDate[0]);
						month=Integer.parseInt(sDate[1]);
						year=Integer.parseInt(sDate[2]);

						Calendar calendar= Calendar.getInstance();
						calendar.set(year, month, day);

						//Check to make sure the date wasn't too large (day out of bounds for the month, etc)
						//If it is, then the calendar automatically accounts for that. For example, if you pass 60 days,
						//it will count a month and the leftover days which != 60 days
						if(day!=calendar.get(Calendar.DAY_OF_MONTH)
								|| month!=calendar.get(Calendar.MONTH)
								|| year!= calendar.get(Calendar.YEAR)) {
							throw(new Exception());
						}
						startDate=calendar.getTime();
						JOptionPane.showMessageDialog(page, startDate.toString());

					}
					catch(Exception ex) {
						JOptionPane.showMessageDialog(page, "Start date in incorrect format");
						txtStartDate.requestFocus();
						return;
					}
				}
			}

			//Check end date as well
			Date endDate=null;
			//Get start date if one is given
			if(!txtEndDate.getText().equals(dateFormat)) {
				if(txtEndDate.getText().length()!=MAX_DATE_LENGTH) {
					JOptionPane.showMessageDialog(page, "Incorrect end date entered.");
					txtEndDate.requestFocus();
					return;
				}
				else {
					String eDate[]=txtEndDate.getText().split("/");
					int day, month, year;
					try {
						//Make sure date is integers, then convert it to a calendar
						day=Integer.parseInt(eDate[0]);
						month=Integer.parseInt(eDate[1]);
						year=Integer.parseInt(eDate[2]);

						Calendar calendar= Calendar.getInstance();
						calendar.set(year, month, day);

						//Check to make sure the date wasn't too large (day out of bounds for the month, etc)
						//If it is, then the calendar automatically accounts for that. For example, if you pass 60 days,
						//it will count a month and the leftover days which != 60 days
						if(day!=calendar.get(Calendar.DAY_OF_MONTH)
								|| month!=calendar.get(Calendar.MONTH)
								|| year!= calendar.get(Calendar.YEAR)) {
							throw(new Exception());
						}
						endDate=calendar.getTime();
					}
					catch(Exception ex) {
						JOptionPane.showMessageDialog(page, "End date in incorrect format");
						txtEndDate.requestFocus();
						return;
					}
				}
			}

			//Call controller with the data
			TableModel tm=home.controller.getFilmReport(category, startDate, endDate, storeId);
			if(tm!=null) {
				resultTable.setModel(tm);
			}
			else {
				JOptionPane.showMessageDialog(page, "Error fetching query from the database");
				return;
			}
		}
	}
	//Return the width and height to the home page
	@Override
	public Dimension getDimensions() {
		return new Dimension(this.width, this.height);
	}

}
