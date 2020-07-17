/**
 * Program Name: SakilaHome.java
 * Purpose:
 * Coder: Hunter Bennett
 * Date: Jul 14, 2020		
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SakilaHome extends JFrame
{
	final int WINDOW_WIDTH = 400;
	final int WINDOW_HEIGHT = 600;

	public SakilaHome()
	{
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		//Decide layout
		//this.setLayout(new ...);

		//Another Test
		//Fuck you Hunter, it works now /////////////////////////////////////git 
		this.setVisible(true);
	}

	public static void main(String[] args)
	{
		new SakilaHome();

		//Uncomment to test if controller is working
		SakilaController sk=new SakilaController();
		sk.testConnection();

		/*
		 * If it isnt working, make sure that your build path incorporates the MySQL connector
		 * jar file in your build path
		 * 
		 * Steps to do so:
		 * right click on the project > build path > Configure Build Path > Class Path > Add external JARs
		 * and connect your connector_j jar file to the build path 
		 */
		
	}
}
