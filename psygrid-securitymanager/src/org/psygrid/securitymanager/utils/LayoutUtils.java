/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.psygrid.securitymanager.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.Box;

import org.psygrid.securitymanager.model.UserModel;
import org.psygrid.securitymanager.ui.CustomLabel;

public class LayoutUtils 
{
	public static LayoutUtils singleton;
	

	/**
	 * Return a static singleton of this class.
	 */
	public static LayoutUtils getInstance()
	{
		if (singleton == null)
		{
			singleton = new LayoutUtils();
		}
		return singleton;
	}
	
	public JPanel createSelectionPanel(String leftLabelString, String rightLabelString,
									   JButton leftButton, JButton rightButton,
									   JList leftList, JList rightList)
	{
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.X_AXIS));
		selectionPanel.add(createSubPanel(leftLabelString, leftButton, leftList));
		selectionPanel.add(createArrowPanel(rightButton, leftButton));
		selectionPanel.add(createSubPanel(rightLabelString, rightButton, rightList));
		return selectionPanel;
	}
	
	
	public JPanel createSubPanel(String labelString, JButton button, JList list)
	{
		JPanel subPanel = new JPanel();
		subPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		subPanel.setLayout(new BorderLayout());
		subPanel.add(createLabelPanel(labelString, list, button), BorderLayout.NORTH);
		JScrollPane scroller = new JScrollPane(list);
		scroller.setPreferredSize(new Dimension(250, 200));
		subPanel.add(scroller, BorderLayout.CENTER);
		return subPanel;
	}
	
	public JPanel createArrowPanel(JButton rightButton, JButton leftButton)
	{
		JPanel arrowPanel = new JPanel();
		arrowPanel.setLayout(new BoxLayout(arrowPanel, BoxLayout.Y_AXIS));
		arrowPanel.add(leftButton);
		arrowPanel.add(Box.createVerticalStrut(6));
		arrowPanel.add(rightButton);
		return arrowPanel;
	}
	
	/**
	 * Creates the header panel for the listbox seen in multiple wizard components.
	 * @param labelString
	 * @param list
	 * @param assignButton
	 * @return the correctly layed out JPanel
	 */
	public JPanel createLabelPanel(String labelString, JList list, JButton assignButton)
	{
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		labelPanel.add(new JLabel(labelString), BorderLayout.WEST);
		return labelPanel;
	}
	
	public JPanel createHeaderPanel(String headerString)
	{
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
//		headerPanel.setBackground(new Color(255,255,206));
		headerPanel.add(new CustomLabel(headerString + " " + UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname()));
		return headerPanel;
	}
	
	public JPanel createProjectSelectionPanel(String chooseString, JComboBox projectBox)
	{
		JPanel projectPanel = new JPanel();
		projectPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		projectPanel.add(new JLabel(chooseString));
		projectPanel.add(projectBox);
		return projectPanel;
	}
	
}