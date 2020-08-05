
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

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

/**
 * Program: AddRental.java
 * Descrption:
 * Name: j_dun
 * Date: Aug 2, 2020
 */

/**
 * @author j_dun
 *
 */
public class AddRental extends JPanel implements SakilaTab
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4334449123412768114L;
	JButton addButton;
	JComboBox<String> customerName;
	JComboBox<String> store;
	JList<String> movies;
	JComboBox<String> salesNumber;
	JPanel moviePanel, userSelectionPanel;

	int width, height;
	SakilaHome home;
	
	public AddRental(SakilaHome home) {
		
		super(new BorderLayout());
		
		userSelectionPanel = new JPanel(new GridLayout(6,1,10,10));
		moviePanel = new JPanel(new BorderLayout());
		this.add(userSelectionPanel,BorderLayout.WEST);
		this.add(moviePanel,BorderLayout.CENTER);
		
		this.home = home;
		this.width = home.WINDOW_WIDTH;
		this.height = home.WINDOW_HEIGHT;
		
		userSelectionPanel.add(new JLabel("Store #:" , JLabel.CENTER));
		store = new JComboBox<String>(home.controller.getStores());
		store.setSelectedIndex(1);	
		store.setEditable(false);
		userSelectionPanel.add(store);
		
		
		userSelectionPanel.add(new JLabel("Customer Name:" , JLabel.CENTER));
		customerName = new JComboBox<String>(home.controller.getCustomer());
		customerName.setEditable(false);
		userSelectionPanel.add(customerName);
		
		userSelectionPanel.add(new JLabel("Sales Rep #:", JLabel.CENTER));
		Vector<String> employees = home.controller.getSalesStaff(2);
		salesNumber = new JComboBox<String>(employees);
		salesNumber.setEditable(false);
		userSelectionPanel.add(salesNumber);

		
		moviePanel.add(new JLabel("Movie Selection:", JLabel.CENTER),BorderLayout.PAGE_START);
		Vector <String> moviesForRent = home.controller.getRentableFilms(2);
		movies = new JList<String>(moviesForRent);
		movies.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		movies.setLayoutOrientation(JList.VERTICAL);
		JScrollPane movieScroll = new JScrollPane(movies);

		moviePanel.add(movieScroll,BorderLayout.CENTER);
		addButton = new JButton("Add Rental");
		//addButton.setEnabled(false);
		this.add(addButton,BorderLayout.SOUTH);

		
		store.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e)
			{					
				
				String storeNum = (String) store.getSelectedItem();
				
				moviesForRent.removeAllElements();
				
				moviesForRent.addAll(home.controller.getRentableFilms(Integer.parseInt(storeNum))); 
				Vector <String> salesNum = home.controller.getSalesStaff(Integer.parseInt(storeNum));
				
				movies.removeAll();
				salesNumber.removeAllItems();
				for (int i = 0; i < salesNum.size(); i++) 
				{
					salesNumber.addItem(salesNum.get(i));
				}		
			}
		});
		
		
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				List<String> movieRentals = movies.getSelectedValuesList();
				if (movieRentals.size() == 0)
					JOptionPane.showMessageDialog(home, "Please select a movie to rent.");
				
				else if(customerName.getSelectedItem().toString() == "")
					JOptionPane.showMessageDialog(home, "Please select a customer to rent a movie to.");
				
				else {
				
				
				int salesClerkNum = Integer.parseInt(salesNumber.getSelectedItem().toString());
				int storeNum = Integer.parseInt(store.getSelectedItem().toString());
				
				
				String resultString = "";
				String customerFirstName = customerName.getSelectedItem().toString();
				String customerLastName = customerFirstName.substring(0,customerFirstName.indexOf(','));
				customerFirstName = customerFirstName.substring(customerFirstName.indexOf(' ')+1);

				double currentBalance = home.controller.getBalance(customerFirstName,customerLastName);

				if (currentBalance < 0) {
					JOptionPane.showMessageDialog(home, "Balance for " + customerLastName + ", " + customerFirstName
							+ " is currently $" +  currentBalance
							+ ".\nPlease repay balance and try to rent again.");
				}
				
				else 
				{	
					resultString = "Movie rental(s) for " + customerFirstName + " " + customerLastName + ":";
					for(String m: movieRentals)
					{
						resultString += home.controller.addRent(customerFirstName, customerLastName, m, salesClerkNum, storeNum);
					}
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