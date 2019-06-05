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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

import org.psygrid.securitymanager.Application;
import org.psygrid.securitymanager.renderers.*;

import org.psygrid.securitymanager.utils.UserType;

import javax.swing.JTree;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.psygrid.securitymanager.utils.SortedTreeNode;

import org.psygrid.securitymanager.actions.AddUserAction;
import org.psygrid.securitymanager.actions.DeleteUserAction;
import org.psygrid.securitymanager.actions.ModifyUserAction;
import org.psygrid.securitymanager.actions.DeleteRoleFromProjectAction;
import org.psygrid.securitymanager.actions.DeleteGroupFromProjectAction;
import org.psygrid.securitymanager.actions.ResetPasswordAction;

import org.psygrid.securitymanager.model.UserModel;

/**
 * Custom tree that uses renderer with icons and right-click menus.
 * @author pwhelan
 *
 */
public class CustomTree extends JTree
{
	
	private Application application = null;

	public CustomTree(Application application, SortedTreeNode sortedNode) {
		super(sortedNode);
		this.application = application;
		addMouseListener(new CustomMouseAdapter());
		setCellRenderer(new CustomTreeCellRenderer());
	}
	
	private class CustomMouseAdapter extends MouseAdapter
	{
		
		/**
		 * Display context sensitive popup menu on right-click
		 * For a project, show Add User option
		 * For a user/ show Delete user and Modify User and fill in user details
		 * For a group or project, just show Delete group or project 
		 */
		public void mouseClicked(MouseEvent e) {
			CustomTree tree = (CustomTree)e.getSource();
			int x = e.getX();
			int y = e.getY();
			
			//show to user which is selected
			int row = tree.getRowForLocation(x, y);
			tree.setSelectionRow(row);
			
			try
			{
				Object[] path = tree.getPathForLocation(e.getX(), e.getY()).getPath();

				SortedTreeNode sortedNode = (SortedTreeNode)path[path.length-1];
				if (SwingUtilities.isRightMouseButton(e))
				{
					if (sortedNode.getUserObject() instanceof ProjectType)
					{
						JPopupMenu addUserMenu = new JPopupMenu();
						addUserMenu.add(new JMenuItem(new AddUserAction()));
						addUserMenu.show((Component)e.getSource(), e.getX(), e.getY());
					}
					else if (sortedNode.getUserObject() instanceof RoleType)
					{
						SortedTreeNode twoLevelNode = (SortedTreeNode)path[path.length-3];
						UserType user = (UserType)twoLevelNode.getUserObject();
						RoleType r = (RoleType)sortedNode.getUserObject();
						ProjectType p = (ProjectType)((SortedTreeNode)path[path.length-4]).getUserObject();
						showDeleteRoleFromProject(tree, p, r, user, x, y);
					} else if (sortedNode.getUserObject() instanceof GroupType)
					{
						SortedTreeNode twoLevelNode = (SortedTreeNode)path[path.length-3];
						UserType user = (UserType)twoLevelNode.getUserObject();
						GroupType g = (GroupType)sortedNode.getUserObject();
						ProjectType p = (ProjectType)((SortedTreeNode)path[path.length-4]).getUserObject();
						showDeleteGroupFromProject(tree, p, g, user, x, y);
					} else if (sortedNode.getUserObject() instanceof UserType)
					{
						showModifyDeletePopUpMenu(sortedNode, tree, x, y);
					}
				}
				//if something goes wrong with selection or producing the popup, just ignore
			} catch (Exception ex)
			{
				//do nothing
			}
		}
	}
	
	/**
	 * Display a modify and delete user popup at the x,y location
	 * @param node
	 * @param source
	 * @param x
	 * @param y
	 */
	public void showModifyDeletePopUpMenu(SortedTreeNode node, Component source, int x, int y)
	{
		UserModel.getInstance().setFirstname(((UserType)node.getUserObject()).getFirstName());
		UserModel.getInstance().setLastname(((UserType)node.getUserObject()).getLastName());
		JPopupMenu modifyAndDeleteUser = new JPopupMenu();
		//set the first name and last name in the model before displaying
		modifyAndDeleteUser.add(new JMenuItem(new ModifyUserAction()));
		modifyAndDeleteUser.add(new JMenuItem(new DeleteUserAction()));
		modifyAndDeleteUser.add(new JMenuItem(new ResetPasswordAction()));
		modifyAndDeleteUser.show(source, x, y);
	}
	
	public void showDeleteRoleFromProject(Component source, ProjectType p, RoleType r, UserType u, int x, int y)
	{
		JPopupMenu modifyAndDeleteUser = new JPopupMenu();
		//set the first name and last name in the model before displaying
		modifyAndDeleteUser.add(new JMenuItem(new DeleteRoleFromProjectAction(application, p, r, u)));
		modifyAndDeleteUser.show(source, x, y);
	}

	public void showDeleteGroupFromProject(Component source, ProjectType p, GroupType g, UserType u, int x, int y)
	{
		JPopupMenu modifyAndDeleteUser = new JPopupMenu();
		//set the first name and last name in the model before displaying
		modifyAndDeleteUser.add(new JMenuItem(new DeleteGroupFromProjectAction(application, p, g, u)));
		modifyAndDeleteUser.show(source, x, y);
	}

	
}