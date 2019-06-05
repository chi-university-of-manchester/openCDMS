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

package org.psygrid.securitymanager.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JComboBox;

import javax.swing.DefaultListModel;

import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

import org.psygrid.securitymanager.model.UserModel;

import org.psygrid.securitymanager.utils.IconsHelper;

public class SwitchListsAction extends AbstractAction  {
    private static final long serialVersionUID = 1L;
    
    private JComboBox projectBox;
    private JList possibleList;
    private JList assignedList;
    
    //specifies whether button is for assigning to assigned list or unassigning
    private boolean assign = true;
    
    /**
     * Lists action attached to the arrow buttons for assigning/removing
     * projects, groups and roles
     * 
     * @param name name of the field 
     * @param possibleList left list
     * @param assignedList right list
     * @param assign whether action is to assign or remove
     */
    public SwitchListsAction(String name, JList  possibleList, JList assignedList, boolean assign) {
    	super("", IconsHelper.getInstance().getImageIcon(name));
    	this.possibleList = possibleList;
        this.assignedList = assignedList;
        this.assign = assign;
    }
    
    public SwitchListsAction(String name, JComboBox projectBox , JList possibleList, JList assignedList, boolean assign)
    {
    	this(name, possibleList, assignedList, assign);
    	this.projectBox = projectBox;
    }
    
    /**
     * Perform the switch
     */
    public void actionPerformed(ActionEvent e) {
    	Object[] selectedValues;
    	
    	if (assign)
    	{
    		selectedValues = possibleList.getSelectedValues();
    	} else
    	{
    		selectedValues = assignedList.getSelectedValues();
    	}
    	
    	
    	for (Object selectedItem: selectedValues)
    	{
        	if (selectedItem instanceof RoleType)
        	{
        		ProjectType selectedProject = (ProjectType)projectBox.getSelectedItem();
        		if (assign)
        		{
        			UserModel.getInstance().addRole(selectedProject, (RoleType)selectedItem);
        		} else
        		{
        			UserModel.getInstance().removeRole(selectedProject, (RoleType)selectedItem);
        		}
        	}
        	else if (selectedItem instanceof GroupType)
        	{
        		ProjectType selectedProject = (ProjectType)projectBox.getSelectedItem();
        		if (assign)
        		{
            		UserModel.getInstance().addGroup(selectedProject, (GroupType)selectedItem);
        		} else
        		{
        			UserModel.getInstance().removeGroup(selectedProject, (GroupType)selectedItem);
        		}
        	}
        	else if (selectedItem instanceof ProjectType)
        	{
        		if (assign)
        		{
            		UserModel.getInstance().addProject((ProjectType)selectedItem, true); 
        		}
        		else
        		{
        			UserModel.getInstance().removeProject((ProjectType)selectedItem);
        		}
        	}
        	
        	if (assign)
        	{
        		((DefaultListModel)assignedList.getModel()).addElement(selectedItem);
            	((DefaultListModel)possibleList.getModel()).removeElement(selectedItem);
        	}
        	else
        	{
        		((DefaultListModel)assignedList.getModel()).removeElement(selectedItem);
        		((DefaultListModel)possibleList.getModel()).addElement(selectedItem);
        	}
    	}
    }
    
}
	