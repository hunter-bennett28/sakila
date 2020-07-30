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
		
		//Will currently throw an error if clicked back to because it doesn't implement SakilaTab interface
		tabPane.addTab("test", new JPanel()); //blank pane to test on click listener for switching to add actor
		
		AddActor addActor = new AddActor(this);
		tabPane.addTab("Add Actor", new ImageIcon("images/actor.png"), addActor, "Add an actor to the database");
	
		AddFilm addFilm = new AddFilm(this);
		tabPane.addTab("Add Film", new ImageIcon("images/camera.png"), addFilm, "Add a film to the database");
		
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
}
