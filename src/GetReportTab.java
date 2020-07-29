/**
 * Name: GetReportTab.java
 * Author: Connor Black
 * Date: Jul. 22, 2020
 * Desc:
 */
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class GetReportTab extends JPanel implements SakilaTab {

	SakilaHome home;
	int width, height;
	JPanel searchPanel;
	JComboBox cbCategory;
	
	public GetReportTab(SakilaHome home)
	{
		super(new BorderLayout());

		//Fetch list of categories and load them into cbCategory
		final String categories[]={"All",""};
		
		//Set up window
		this.home = home;
		this.width = 700;
		this.height = 800;
		this.setLayout(new BorderLayout());
		
		
		//Construct window
		searchPanel=new JPanel();
		this.add(new JLabel("Search", JLabel.CENTER), BorderLayout.LINE_START);
		
		this.add(new JLabel("Test"), BorderLayout.LINE_START);
		
		
	}
	
	
	@Override
	public Dimension getDimensions() {
		// TODO Auto-generated method stub
		return null;
	}

}
