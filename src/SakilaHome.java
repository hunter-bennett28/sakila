/**
 * Program Name: SakilaHome.java
 * Purpose: An application for interfacing with the Sakila MySQL database.
 * 					Allows for adding things such as actors, movies, transactions, and customers.
 * 					Also allows for querying and displaying data about the database
 * Coder: Hunter Bennett, Connor Black, James Dunton, Taylor DesRoches
 * Date: Jul 14, 2020		
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SakilaHome extends JFrame
{
	final int WINDOW_WIDTH = 600;
	final int WINDOW_HEIGHT = 400;
	SakilaController controller;

	public SakilaHome()
	{
		super("Sakila Database");

		//Create database connection
		this.controller = new SakilaController();
		
		//Boiler plate code
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		//Create JTabbedPane that will hold all windows
		JTabbedPane tabPane = new JTabbedPane();
		this.add(tabPane);
		
		/* Add tabs to tab pane */

		HomeTab homeTab = new HomeTab();
		tabPane.addTab("Home", new ImageIcon("images/home.png"), homeTab, "Home"); //blank pane to test on click listener for switching to add actor

		AddActor addActor = new AddActor(this);
		tabPane.addTab("Add Actor", new ImageIcon("images/actor.png"), addActor, "Add an actor to the database");
	
		AddFilm addFilm = new AddFilm(this);
		tabPane.addTab("Add Film", new ImageIcon("images/camera.png"), addFilm, "Add a film to the database");
		
		FilmReport reportFilm = new FilmReport(this);
		tabPane.addTab("Film Report", new ImageIcon("images/Magnifying Glass.png"), reportFilm, "Get a report from the database");
		
		CustomerReport reportCustomer = new CustomerReport(this);
		tabPane.addTab("Customer Report", new ImageIcon("images/userSilhouette.png"), reportCustomer, "Get a report from the database");
		
		AddRental addRental = new AddRental(this);
		tabPane.addTab("Add Rental", new ImageIcon("images/ticket.png"),addRental, "Add a rental to the database");
		
		//Setup change listener for clicking on tabs
		tabPane.addChangeListener(new TabChangeListener(this, tabPane));

		this.setVisible(true);
	}

	private class TabChangeListener implements ChangeListener
	{
		SakilaHome home;
		JTabbedPane tabPane;

		//Constructor
		public TabChangeListener(SakilaHome home, JTabbedPane tabPane)
		{
			this.home = home;
			this.tabPane = tabPane;
		}

		public void stateChanged(ChangeEvent e)
		{
			//Get selected component, cast as a SakilaTab to gain access to getDimensions
			SakilaTab tab = (SakilaTab)tabPane.getComponent(tabPane.getSelectedIndex());

			//set tab specific size
			home.setSize(tab.getDimensions());
		}
	}

	public static void main(String[] args)
	{
		new SakilaHome();
	}
	
	//Private inner class for setting up a simple introductory home tab
	private class HomeTab extends JPanel implements SakilaTab
	{
		public HomeTab()
		{
			super(new BorderLayout());
			
			//Add image to panel
			JLabel filmPicture = new JLabel(new ImageIcon("images/film.png"));
			this.add(filmPicture, BorderLayout.CENTER);
			
			//Add title panel with black background and white text
			JPanel titlePanel = new JPanel();
			titlePanel.setBackground(Color.BLACK);
			
			//Create the title text with custom font and colour
			JLabel welcome = new JLabel("Welcome to the Sakila Rental App", JLabel.CENTER);
			Font welcomeFont = new Font("Serif", Font.BOLD, 32);
			welcome.setFont(welcomeFont);
			welcome.setForeground(Color.WHITE);

			//Add to panel then to master
			titlePanel.add(welcome);
			this.add(titlePanel, BorderLayout.NORTH);
			
			//Create footer panel that explains usage
			JPanel footerPanel = new JPanel();
			footerPanel.setBackground(Color.BLACK);
			
			JLabel footer = new JLabel("Please Select Above Tabs To Begin");
			Font fontFooter = new Font("Serif", Font.ITALIC, 24);
			footer.setFont(fontFooter);
			footer.setForeground(Color.WHITE);
			
			//Add to panel then to master
			footerPanel.add(footer);	
			this.add(footerPanel, BorderLayout.SOUTH);
		}

		//Method from SakilaTab to tell TabPane what size this tab wants to be
		@Override
		public Dimension getDimensions()
		{
			return new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
		}
		
	}
}
