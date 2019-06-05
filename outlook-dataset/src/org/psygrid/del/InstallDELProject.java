/*
Copyright (c) 2008, The University of Manchester, UK.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.
*/

package org.psygrid.del;

import org.psygrid.security.RBACRole;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;

/**
 * Install the specified project for the data-element-library.
 * 
 * args[0] should be the name of the class implementing DELProject and
 * providing the information for the project to be installed.
 * 
 * @author Lucy Bridges
 *
 */
public class InstallDELProject {

    public static void main (String[] args) throws Exception {
    	AAManagementClient mc = new AAManagementClient("test.properties");
		
		DELProject delProject = null;

		try {
			String projectClass = args[0]; 
			delProject = (DELProject)Class.forName(projectClass).newInstance();
		}
		catch (IllegalAccessException ex) {
			ex.printStackTrace();
			return;
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return;
		}
		catch (InstantiationException ex) {
			ex.printStackTrace();
			return;
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("You must supply the project class name");
			return;
		}
    	
    	ProjectType project = new ProjectType(delProject.getProject(), delProject.getProjectID(), delProject.getAliasName(), delProject.getAliasId(), false);
		
    	mc.getPort().deleteProject(new ProjectType[]{project});
    	
    	RoleType sa = RBACRole.SystemAdministrator.toRoleType();
		RoleType pa = RBACRole.ProjectAdministrator.toRoleType();
		RoleType pm = RBACRole.ProjectManager.toRoleType();
		
		RoleType viewer = RBACRole.DELViewer.toRoleType();
		RoleType author = RBACRole.DELAuthor.toRoleType();
		RoleType curator = RBACRole.DELCurator.toRoleType();
		
		GroupType[] allgroups = delProject.allGroups();
		GroupType[] nogroups = delProject.noGroups();
		
    	try{		
    		RoleType[] roles = new RoleType[]{sa, pa, pm, viewer, author, curator};
    		ProjectDescriptionType pdt = new ProjectDescriptionType(project, allgroups, roles);
    		ProjectDescriptionType[] pdta = new ProjectDescriptionType[]{pdt};
    		mc.getPort().addProject(pdta);	
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	
		/*try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=John Ainsworth, OU=Informatics, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);	
		} catch (Exception e){
			e.printStackTrace();
		}

		try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Robert Harper, OU=Informatics, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);	
		} catch (Exception e){
			e.printStackTrace();
		}

		try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lucy Bridges, OU=Informatics, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);	
		} catch (Exception e){
			e.printStackTrace();
		}

		try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Pauline Whelan, OU=Informatics, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);	
		} catch (Exception e){
			e.printStackTrace();
		}*/

		try{
			RoleType[] roles = new RoleType[]{sa, pa, pm};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Pauline", "Whelan", "pauline.whelan@manchester.ac.uk", null,"CN=Pauline Whelan, OU=users, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);	
		} catch (Exception e){
			e.printStackTrace();
		}

		try{
			RoleType[] roles = new RoleType[]{sa, pa, pm};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Lucy", "Bridges", "lucy.bridges@manchester.ac.uk", null,"CN=Lucy Bridges, OU=users, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);	
		} catch (Exception e){
			e.printStackTrace();
		}

		try{
			RoleType[] roles = new RoleType[]{sa, pa, pm};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Robert", "Harper", "robert.harper@manchester.ac.uk", null,"CN=Robert Harper, OU=users, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);	
		} catch (Exception e){
			e.printStackTrace();
		}

		try{
			RoleType[] roles = new RoleType[]{sa, pa, pm};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("John", "Ainsworth", "john.ainsworth@manchester.ac.uk", null,"CN=John Ainsworth, OU=users, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);	
		} catch (Exception e){
			e.printStackTrace();
		}

    	
    }
}
