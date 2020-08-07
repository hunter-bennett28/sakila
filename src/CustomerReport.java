/**
 * Name: CustomerReport.java
 * Author: Connor Black
 * Date: Jul. 28, 2020
 * Desc: This reports customers based on a given search criteria in order to see which customers generate the most income and which customers rent the most
 */
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


public class CustomerReport extends JPanel implements SakilaTab {

	SakilaHome home;
	int width, height;
	JPanel searchPanel, userInputPanel;
	JComboBox<String> cbStores;
	JRadioButton rbSortByRental, rbSortByIncome;
	JButton btnSearch;
	JTable resultTable;

	public CustomerReport(SakilaHome home)
	{
		super(new BorderLayout());

		//Set up window
		this.home = home;
		this.width = home.WINDOW_WIDTH;
		this.height = 250;

		//Construct window
		//Place table
		resultTable=new JTable();
		JScrollPane tableScrollPane=new JScrollPane(resultTable);
		this.add(tableScrollPane, BorderLayout.CENTER);

		//Create search panel
		searchPanel=new JPanel(new BorderLayout());
		searchPanel.add(new JLabel("Browse Customers", JLabel.CENTER), BorderLayout.PAGE_START);

		//Create inner panel for the search panel, it will hold all of the user input for the query
		userInputPanel=new JPanel(new GridLayout(3,2,10,10));

		//Load store # into combo box
		userInputPanel.add(new JLabel("Store #"));
		Vector<String> stores = home.controller.getStores();
		stores.add(0,"All");
		cbStores=new JComboBox<String>(stores);
		userInputPanel.add(cbStores);

		//Add in buttons to organize sorting by rental or by income
		userInputPanel.add(new JLabel("Sort results by:"));
		userInputPanel.add(new JLabel());

		rbSortByRental=new JRadioButton("Rental");
		rbSortByIncome=new JRadioButton("Income");

		ButtonGroup bgSortResultsBy=new ButtonGroup();
		bgSortResultsBy.add(rbSortByRental);
		bgSortResultsBy.add(rbSortByIncome);

		//Set rental to be by default
		rbSortByRental.setSelected(true);

		userInputPanel.add(rbSortByRental);
		userInputPanel.add(rbSortByIncome);
		searchPanel.add(userInputPanel);

		btnSearch=new JButton("Search");

		JPanel page=this;
		//Handle search query
		btnSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String store=cbStores.getSelectedItem().toString();
				int storeId=0;
				if(!store.equals("All")) {
					storeId=Integer.parseInt(store);
				}

				//Call controller with the data
				TableModel tm=home.controller.getCustomerReport(storeId,rbSortByIncome.isSelected());
				
				if(tm!=null) {
					resultTable.setModel(tm);
				}
				else {
					JOptionPane.showMessageDialog(page, "Error fetching data from the database");
				}
			}
		}); //btnSeach ActionListener
		searchPanel.add(btnSearch,BorderLayout.PAGE_END);
		this.add(searchPanel, BorderLayout.LINE_START);
	}

	@Override
	public Dimension getDimensions() {
		return new Dimension(this.width, this.height);
	}

}
