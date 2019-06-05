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

package org.psygrid.security.attributeauthority.model.hibernate;

import java.util.ArrayList;

import org.psygrid.security.attributeauthority.dao.ProjectDAO;
import org.psygrid.security.attributeauthority.model.Factory;
import org.psygrid.security.attributeauthority.model.IGroup;
import org.psygrid.security.attributeauthority.model.IProject;
import org.psygrid.security.attributeauthority.model.IRole;
import org.psygrid.security.attributeauthority.model.IUser;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;

public class HibernateFactory implements Factory {

    public IRole createRole(String name) {
        return new Role(name, null);
    }

    public IGroup createGroup(String name) {
        return new Group(name);
    }
    
    public IProject createProject(String name, String id, ArrayList<Group> groups, ArrayList<Role> roles) {
        return new Project(name, id, groups, roles);
    }
    
    public IUser createUser(String name, ProjectDAO pdao) {
        return new User(name);
    }
 
   
    public User createUser(UserPrivilegesType ugrt, ProjectDAO pdao){
    		return User.fromUserPrivelegesType(ugrt, pdao);
    }
    
    public Project createProject(ProjectDescriptionType pdt){
		return Project.fromProjectDescriptionType(pdt);
    }
}
