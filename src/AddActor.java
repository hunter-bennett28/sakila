/**
 * Program Name: SakilaHome.java
 * Purpose: GUI tab for adding a new actor to the database
 * Coder: Hunter Bennett
 * Date: Jul 14, 2020		
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AddActor extends JPanel implements SakilaTab
{
	JButton addButton;
	JTextField firstName, lastName;
	SakilaHome home;
	int width, height;

	public AddActor(SakilaHome home)
	{
		super(new GridLayout(3, 2, 10, 10));
		
		this.home = home;
		this.width = home.WINDOW_WIDTH;
		this.height = 250;
		
		//Add Components
		this.add(new JLabel("First Name:", JLabel.RIGHT));
		firstName = new JTextField();
		this.add(firstName);
		
		this.add(new JLabel("Last Name:", JLabel.RIGHT));
		lastName = new JTextField();
		this.add(lastName);
		
		this.add(new JLabel());
		addButton = new JButton("Add Actor");
		addButton.addActionListener(new ActorListener());
		this.add(addButton);
	}

	@Override
	public Dimension getDimensions()
	{
		return new Dimension(this.width, this.height);
	}
	
	//Listener class
	private class ActorListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			String first = firstName.getText();
			String last = lastName.getText();
			
			//Ensure all fields are completed
			boolean allFieldsEntered = true;
			if(first.isEmpty())
			{
				firstName.setText("Please enter a name");
				allFieldsEntered = false;
			}
			
			if(last.isEmpty())
			{
				lastName.setText("Please enter a name");
				allFieldsEntered = false;
			}
			
			if(allFieldsEntered)
			{
				String message = home.controller.addActor(first, last)
						? "Actor added successfully!"
						: "Error: actor not added.";
				
				JOptionPane.showMessageDialog(home, message);
			}	
		}
	}
}
