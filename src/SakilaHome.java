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
	BorderLayout homePage;
	public JPanel homePanel;
	public JPanel otherPanel;
 public SakilaHome()
 {
	 super("Sakila Database");
	 this.setLocationRelativeTo(null);
	 this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	 
	 //Decide layout
	 this.setLayout(new FlowLayout());
	 
	 homePage = new BorderLayout();
	 homePanel = new JPanel(homePage);
	 JButton homeBtn = new JButton("Home Panel");
	 homeBtn.addActionListener(new MyListener(homePanel));
	 homePanel.add(homeBtn);
	 
	 otherPanel = new JPanel(new FlowLayout());
	 JButton otherButton = new JButton("Page 2");
	 otherPanel.add(otherButton);
	 otherPanel.add(new JLabel("This is a label"));
	 otherButton.addActionListener(new MyListener(otherPanel));

	 
	 this.add(homePanel);
	 
	 
	 this.setVisible(true);
 }
 
 public JPanel getPanel()
 {
	 return homePanel;
 }
 
 public void setPanel(JPanel panel)
 {
	 this.homePanel = panel;
 }
 
private class MyListener implements ActionListener
{
	 JPanel panel;
	 
	 public MyListener(JPanel panel)
	 {
		 this.panel = panel;
	 }
	 
	 public void actionPerformed(ActionEvent ev)
	 {
		 changePanel(panel);
	 }
}

public void changePanel(JPanel panel)
{
	this.getContentPane().removeAll();
	this.getContentPane().add(panel);
	this.getContentPane().doLayout();
	this.update(getGraphics());
}
 
public static void main(String[] args)
 {
	 new SakilaHome();
 }
}
