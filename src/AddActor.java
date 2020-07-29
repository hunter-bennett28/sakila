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
	JComboBox<String> films;
	int width, height;

	public AddActor(SakilaHome home)
	{
		super(new GridLayout(4, 2, 10, 10));

		/* Variable Declarations */

		this.home = home;
		this.width = home.WINDOW_WIDTH;
		this.height = 250;
		
		/* Add Components */

		//First name row
		this.add(new JLabel("*First Name:", JLabel.RIGHT));
		firstName = new JTextField();
		this.add(firstName);

		//Last name row
		this.add(new JLabel("*Last Name:*", JLabel.RIGHT));
		lastName = new JTextField();
		this.add(lastName);

		//Film row
		this.add(new JLabel("Film:", JLabel.RIGHT));
		films = new JComboBox<String>(home.controller.getFilms()); //populate films combo box
		films.setEditable(false);
		this.add(films);

		//Button row
		this.add(new JLabel("* is required", JLabel.RIGHT));
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
		/**
		 * Method Name: actionPerformed(ActionEvent e)
		 * Purpose: Event handler that ensures text fields are filled and adds an actor to the database if so
		 * Accepts: an ActionEvent with the event information
		 * Returns: Void
		 */
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

			//Allow for no movie selection
			if(allFieldsEntered)
			{
				String message = home.controller.addActor(first, last, (String)films.getSelectedItem())
						? "Actor added successfully!"
						: "Error: actor not added.";
				
				JOptionPane.showMessageDialog(home, message);
			}	
		}
	}
}
