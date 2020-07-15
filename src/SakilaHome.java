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
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	 
		//Decide layout
		//this.setLayout(new ...);
	 
		//Another Test
		//Fuck you Connor /////////////////////////////////////git 
		this.setVisible(true);
	}

 public static void main(String[] args)
 {
	 new SakilaHome();
 }
}
