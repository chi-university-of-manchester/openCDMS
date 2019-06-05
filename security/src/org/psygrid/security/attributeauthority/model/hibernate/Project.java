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


//Created on Oct 27, 2005 by John Ainsworth


package org.psygrid.security.attributeauthority.model.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.attributeauthority.model.IProject;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.w3c.dom.Element;

/**
 * @author jda
 *
 * @hibernate.joined-subclass table="t_projects"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Project extends Persistent implements IProject {
	private static Log sLog = LogFactory.getLog(Project.class);
	
	
	/**
	 *  Project ID Code
	 */
	private String idCode;
	
	/**
	 * Virtual projects do not have an associated dataset
	 * e.g. the SYSTEM project is virutal
	 * 
	 */
	private boolean virtual;
	
	/**
	 *  Project name
	 */
	private String projectName;

	/**
	 *  External name - used when another authority assigns a project name
	 */
	private String aliasName;
	
	/**
	 * External id - used when another authority assigns a project id code
	 */
	private String aliasId;

	/**
	 *  Supported roles in this project
	 */
	private List<Role> roles = new ArrayList<Role>();
	
	/**
	 *  Supported roles in this project
	 */
	private List<Group> groups = new ArrayList<Group>();
	

	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 * 
	 */
	
	protected Project(){};
	
	/**
	 * Constructor that accepts the name of the project
	 *  
	 * @param projectName The name of the project.
	 */
	public Project(String projectName){
		this.setProjectName(projectName);
	}
	
    /**
     * Constructor that accepts the project name and a list
     * of roles
     *  
     * @param projectName The name of the user.
     * @param idCode The project's idCode
     * @param aliasName The projects external Name
     * @param aliasId The projects aliasId
     * @param groups A list of groups
     * @param roles A list of roles
     */
    public Project(String projectName, String idcode, String externalName,
    		String externalId, ArrayList<Group> groups, 
    		ArrayList<Role> roles){
		this.setProjectName(projectName);
		this.setIdCode(idcode);
		this.setAliasName(externalName);
		this.setAliasId(externalId);
		this.setRoles(roles); 
		this.setGroups(groups);     
	}
		
    /**
     * Constructor that accepts the project name and a list
     * of roles
     *  
     * @param projectName The name of the user.
     * @param idCode The project's idCode
     * @param groups A list of groups
     * @param roles A list of roles
     */
    public Project(String projectName, String idcode, ArrayList<Group> groups, 
    		ArrayList<Role> roles){
		this.setProjectName(projectName);
		this.setIdCode(idcode);
		this.setRoles(roles); 
		this.setGroups(groups);     
	}
    
	/**
     * Get the projectName
     * 
     * @return The projectName.
     * @hibernate.property column = "c_project_name"
     */
    public String getProjectName(){
    		return this.projectName;
    }

    /**
     * Set the projectName
     * 
     * @param projectName The project name.
     */
    public void setProjectName(String projectName){
    		this.projectName = projectName;
    }
    
	/**
     * Get the roles
     * 
     * @return A list containing the roles.
     * @hibernate.list cascade="all-delete-orphan" lazy="false" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.security.attributeauthority.model.hibernate.Role"
     * @hibernate.key column="c_roleproject_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<Role> getRoles(){
    		return roles;
    }

	/**
     * Set roles
     * 
     * @param roles A list containing the roles.
     */
    public void setRoles(List<Role> roles){
    		this.roles = roles;
    }
    public Element toDOM(){
    	return null;
    }
    
    public static Project fromProjectDescriptionType(ProjectDescriptionType pt) {
		Project np = new Project();
		ArrayList<Role> roleList = new ArrayList<Role>();
		ArrayList<Group> groupList = new ArrayList<Group>();
		if (pt != null) {
			if (pt.getRole() != null) {
				for (int i = 0; i < pt.getRole().length; i++) {
					roleList.add(Role.fromRoleType(pt.getRole()[i]));
				}
				np.setRoles(roleList);
			}
			if (pt.getGroup() != null) {
				for (int i = 0; i < pt.getGroup().length; i++) {
					groupList.add(Group.fromGroupType(pt.getGroup()[i]));
				}
				np.setGroups(groupList);
			}
			np.setProjectName(pt.getProject().getName());
			np.setIdCode(pt.getProject().getIdCode());
			np.setAliasName(pt.getProject().getAliasName());
			np.setAliasId(pt.getProject().getAliasId());
			np.setVirtual(pt.getProject().isVirtual());
		}
		return np;
	}
    
    public static Project fromProjectType(ProjectType pt){
		Project np = new Project();  	
		np.setProjectName(pt.getName());
		np.setIdCode(pt.getIdCode());
		np.setAliasName(pt.getAliasName());
		np.setAliasId(pt.getAliasId());
		np.setVirtual(pt.isVirtual());
		return np;
    }
    
	public void print(){
		sLog.info("Project: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: "
				+this.getProjectName()+"\n\tIdCode: "+this.getIdCode()
				+"\n\tExternal Name: "
				+this.getAliasName()+"\n\tExternal Id: "+this.getAliasId()+"\n\tVirutal: "+this.isVirtual());
		for(int i=0;i<roles.size();i++){
			roles.get(i).print();
		}
		for(int i=0;i<groups.size();i++){
			groups.get(i).print();
		}
	}

	/**
     * Get the groups
     * 
     * @return A list containing the groups.
     * @hibernate.list cascade="all-delete-orphan" lazy="false" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.security.attributeauthority.model.hibernate.Group"
     * @hibernate.key column="c_groupproject_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */

	public List<Group> getGroups() {
		return this.groups;
	}

	/**
	 * @param groups The groups to set.
	 */
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	/**
	 * @param g
	 * @return
	 */
	public boolean addGroup(Group g){
		boolean rc = false;
		if(!isKnownGroup(g)){
			rc = groups.add(g);
		}
		return rc;
	}
	
	/**
	 * @param gta
	 * @return
	 */
	public boolean addGroup(GroupType[] gta){
		for(int i=0; i<gta.length;i++){
			Group g = Group.fromGroupType(gta[i]);
			addGroup(g);
		}
		return true;		
	}
	/**
	 * @param g
	 * @return
	 */
	public boolean removeGroup(Group g){
		boolean rc = false;
		ListIterator<Group> it = groups.listIterator();
		while(it.hasNext()){
			Group gtemp = it.next();
			if(gtemp.identical(g)){
				it.remove();
				rc = true;
				break;
			}
		}
		return rc;	
	}
	
	/**
	 * @param gta
	 * @return
	 */
	public boolean removeGroup(GroupType[] gta){
		for(int i=0; i<gta.length;i++){
			Group g = Group.fromGroupType(gta[i]);
			removeGroup(g);
		}
		return true;
	}
	
	/**
	 * @param r
	 * @return
	 */
	public boolean addRole(Role r){
		boolean rc = false;
		if(!isKnownRole(r)){
			rc = roles.add(r);
		}
		return rc;
	}
	
	/**
	 * @param rta
	 * @return
	 */
	public boolean addRole(RoleType[] rta){
		for(int i=0; i<rta.length;i++){
			Role r = Role.fromRoleType(rta[i]);
			addRole(r);
		}
		return true;		
	}
	
	/**
	 * @param r
	 * @return
	 */
	public boolean removeRole(Role r){
		boolean rc = false;
		ListIterator<Role> it = roles.listIterator();
		while(it.hasNext()){
			Role rtemp = it.next();
			if(rtemp.identical(r)){
				it.remove();
				rc = true;
				break;
			}
		}
		return rc;	
	}
	
	/**
	 * @param rta
	 * @return
	 */
	public boolean removeRole(RoleType[] rta){
		for(int i=0; i<rta.length;i++){
			Role r = Role.fromRoleType(rta[i]);
			removeRole(r);
		}
		return true;
	}
	
	/**
	 * @param r
	 * @return
	 */
	public boolean isKnownRole(Role r){
		boolean rc = false;
		ListIterator<Role> it = roles.listIterator();
		while(it.hasNext()){
			Role rtemp = it.next();
			if(rtemp.identical(r)){
				rc = true;
				break;
			}
		}
		return rc;		
	}
	/**
	 * @param r
	 * @return
	 */
	public boolean isKnownRole(String r){
		return isKnownRole(new Role(r, null));
	}
	/**
	 * @param r
	 * @return
	 */
	public Role getPersistedRole(Role r){
		Role rc = null;
		ListIterator<Role> it = roles.listIterator();
		while(it.hasNext()){
			Role rtemp = it.next();
			if(rtemp.identical(r)){
				rc = rtemp;
				break;
			}
		}
		return rc;		
	}
	public Role getPersistedRole(RoleType r){
		return getPersistedRole(Role.fromRoleType(r));	
	}
	/**
	 * @param g
	 * @return
	 */
	public boolean isKnownGroup(Group g){
		boolean rc = false;
		ListIterator<Group> it = groups.listIterator();
		while(it.hasNext()){
			Group gtemp = it.next();
			if(gtemp.identical(g)){
				rc = true;
				break;
			}
		}
		return rc;		
	}
	/**
	 * @param g
	 * @return
	 */
	public boolean isKnownGroup(String g){
		return isKnownGroup(new Group(g));
	}
	/**
	 * @param g
	 * @return
	 */
	public Group getPersistedGroup(Group g){
		Group rc = null;
		ListIterator<Group> it = groups.listIterator();
		while(it.hasNext()){
			Group gtemp = it.next();
			if(gtemp.identical(g)){
				rc = gtemp;
				break;
			}
		}
		return rc;		
	}
	public Group getPersistedGroup(GroupType g){
		return getPersistedGroup(Group.fromGroupType(g));	
	}
	
	public ProjectDescriptionType toProjectDescriptionType(){
		RoleType[] rta = new RoleType[roles.size()];
		GroupType[] gta = new GroupType[groups.size()];
		for(int i=0;i<roles.size();i++){
			rta[i] = roles.get(i).toRoleType();
		}
		for(int i=0;i<groups.size();i++){
			gta[i] = groups.get(i).toGroupType();
		}
		return new ProjectDescriptionType(new ProjectType(getProjectName(), getIdCode(), getAliasName(), getAliasId(), isVirtual()), gta, rta);
	}
	
	public ProjectType toProjectType(){
		return new ProjectType(getProjectName(), getIdCode(), getAliasName(), getAliasId(), isVirtual());		
	}

	/**
	 * @return Returns the idCode.
	 * @hibernate.property column = "c_id_code"
	 */
	public String getIdCode() {
		return this.idCode;
	}

	/**
	 * @param idCode The idCode to set.
	 */
	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}
	
	/**
     * Get the aliasName
     * 
     * @return The aliasName.
     * @hibernate.property column = "c_alias_name"
     */
    public String getAliasName(){
    		return this.aliasName;
    }

    /**
     * Set the aliasName
     * 
     * @param aliasName The external name.
     */
    public void setAliasName(String externalName){
    		this.aliasName = externalName;
    }
	
	/**
	 * @return Returns the aliasId.
	 * @hibernate.property column = "c_alias_id"
	 */
	public String getAliasId() {
		return this.aliasId;
	}

	/**
	 * @param extId The aliasId to set.
	 */
	public void setAliasId(String extId) {
		this.aliasId = extId;
	}
	
	public boolean identical(Project p){
		boolean result = false;
		if ((p.getProjectName() != null) && (getProjectName() != null)) {
			if ((getProjectName().equals(p.getProjectName()) 
					&& (!p.getProjectName().equals("")))) {
				result = true;
			}
		}
		if ((p.getIdCode() != null) && (getIdCode()!=null)) {
			if (getIdCode().equals(p.getIdCode())
					&& (!p.getIdCode().equals(""))) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * @return Returns the virtual.
	 * 
     * @hibernate.property column = "c_virtual"
	 */
	public boolean isVirtual() {
		return this.virtual;
	}

	/**
	 * @param virtual The virtual to set.
	 */
	public void setVirtual(boolean virtual) {
		this.virtual = virtual;
	}
	
}
