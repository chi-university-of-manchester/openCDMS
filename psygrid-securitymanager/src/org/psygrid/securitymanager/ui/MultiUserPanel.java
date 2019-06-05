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

package org.psygrid.securitymanager.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.wizard.WizardPanel;

/**
 * Optional panel that is presented when more than one user is 
 * found matching the user name entered (applicable for
 * modify user or delete user).
 * 
 * @author pwhelan
 */
public class MultiUserPanel extends JPanel implements WizardPanel
{
	
	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	private JList selectList;
	
	private JPanel createMainPanel(ArrayList<String> list)
	{
		JPanel mainPanel = new JPanel();
		selectList = new JList();
		selectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		DefaultListModel listModel = new DefaultListModel();
		
		for(String cn: list)
		{
			listModel.addElement(cn);
		}
		
		selectList.setModel(listModel);
		
		try
		{
			selectList.setSelectedIndex(0);
		} catch (NullPointerException nex) {
			//if no elements in list, we can't select anything anyway
		}
		
		JScrollPane scroller = new JScrollPane(selectList);
		scroller.setPreferredSize(new Dimension(250, 200));
		mainPanel.add(scroller, BorderLayout.CENTER);
		return mainPanel;
		
	}
	
	private JPanel createHeaderPanel()
	{
		JPanel headerPanel = new JPanel();
//		headerPanel.setBackground(new Color(255,255,206));
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		headerPanel.add(new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "multiusersfound")));
		return headerPanel;
	}
	
	public void refreshPanel(ArrayList list)
	{
		removeAll();
		setLayout(new BorderLayout());
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createMainPanel(list), BorderLayout.CENTER);
		validate();
		repaint();
	}
	
	public String getSelectedName()
	{
		return (String)selectList.getSelectedValue();
	}
	
	public void refreshPanel() {
		
	}
	
	

	
	
}