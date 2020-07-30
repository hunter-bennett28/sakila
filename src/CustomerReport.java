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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
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
		this.width = 700;
		this.height = 250;
		this.setLayout(new BorderLayout());


		//Construct window
		//Place table
		resultTable=new JTable();
		JScrollPane tableScrollPane=new JScrollPane(resultTable);
		this.add(tableScrollPane, BorderLayout.CENTER);

		//Create search panel
		searchPanel=new JPanel(new BorderLayout());
		searchPanel.add(new JLabel("Browse Customers", JLabel.CENTER), BorderLayout.PAGE_START);

		userInputPanel=new JPanel(new GridLayout(3,2,10,10));

		//Load store # into combo box
		userInputPanel.add(new JLabel("Store #"));
		ResultSet rs=home.controller.getStores();

		Vector<String> stores = new Vector<String>();

		if(!(rs==null)) {
			try {
				stores.add("All");
				while(rs.next()) {
					stores.add(""+rs.getInt("store_id"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.print("Error loading stores into container");
			}
		}
		else {
			stores.add("Error");
			System.out.println("Error retrieving categories");
		}
		cbStores=new JComboBox<String>(stores);
		userInputPanel.add(cbStores);

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
				ResultSet rs=home.controller.getCustomerReport(storeId,rbSortByIncome.isSelected());
				TableModel tm=DbUtils.resultSetToTableModel(rs);
				resultTable.setModel(tm);
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
