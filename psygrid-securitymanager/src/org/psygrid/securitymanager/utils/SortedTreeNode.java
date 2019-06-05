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

import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.*;

import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Simple class that sorts tree nodes as they are added.
 * @author pwhelan
 */
public class SortedTreeNode extends DefaultMutableTreeNode {
	private static Comparator nodeComparator = new Comparator (){
		public int compare(Object o1, Object o2) {
			
			String value1 = o1.toString();
			String value2 = o2.toString();
			
			o1 = ((SortedTreeNode)o1).getUserObject();
			o2 = ((SortedTreeNode)o2).getUserObject();
			
			if (o1 instanceof ProjectType)
			{
				ProjectType project = (ProjectType) o1;
				value1 = project.getName();
			} else if (o1 instanceof GroupType)
			{
				GroupType group = (GroupType) o1;
				value1 = group.getName();
			} else if (o1 instanceof RoleType)
			{
				RoleType role = (RoleType)o1;
				value1 = role.getName();
			} else if (o1 instanceof UserType)
			{
				UserType user = (UserType) o1;
				value1 = user.getName();
			}
			
			if (o2 instanceof ProjectType)
			{
				ProjectType project = (ProjectType) o2;
				value2 = project.getName();
			} else if (o2 instanceof GroupType)
			{
				GroupType group = (GroupType) o2;
				value2 = group.getName();
			} else if (o2 instanceof RoleType)
			{
				RoleType role = (RoleType)o2;
				value2 = role.getName();
			} else if (o2 instanceof UserType)
			{
				UserType user = (UserType) o2;
				value2 = user.getName();
			}

			return value1.compareToIgnoreCase(value2);
		}

		public boolean equals(Object obj) {
			return false; //static class -- 1 instance only
		}
	};
	
	public void add(MutableTreeNode node){
		super.add(node);
		Collections.sort(this.children, nodeComparator); //this.children is a proteced variable reference to the node's vector of children.
	}
	
	
	public SortedTreeNode(String root)
	{
		super(root);
	}

	public SortedTreeNode(RoleType root)
	{
		super(((RoleType)root).getName());
		setUserObject(root);
	}
	
	public SortedTreeNode(GroupType root)
	{
		super(root.getName());
		setUserObject(root);
	}
	
	public SortedTreeNode(ProjectType root)
	{
		super(root.getName());
		setUserObject(root);
	}
	
	public SortedTreeNode(UserType user)
	{
		super(user.getName());
		setUserObject(user);
	}
	
	
	public SortedTreeNode()
	{
		super();
	}
	
}