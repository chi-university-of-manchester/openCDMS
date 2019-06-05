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
import org.psygrid.security.attributeauthority.model.IAttribute;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.w3c.dom.Element;

/**
 * @author jda
 *
 * @hibernate.joined-subclass table="t_attributes"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Attribute extends Persistent implements IAttribute {
	private static Log sLog = LogFactory.getLog(Attribute.class);
	
	/**
	 *  Attribute name
	 */
	private Project project;
	
	/**
	 *  Supported roles in this attribute
	 */
	private List<RoleLink> roleLink;
	
	/**
	 *  Supported groups in this attribute
	 */
	
	private List<GroupLink> groupLink;
	

	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 * 
	 */
	
	protected Attribute(){};
	
    /**
     * Constructor that accepts the attribute name and a list
     * of roles
     *  
     * @param project The project to which these privileges apply
     * @param groups A list of groups
     * @param roles A list of roles
     */
    public Attribute(Project project, List<Group> groups, 
    		List<Role> roles){
		this.setProject(project);
		this.setRoles(roles); 
		this.setGroups(groups);     
	}
		
	/**
     * Get the project
     * 
     * @return The project.
     * @hibernate.many-to-one class="org.psygrid.security.attributeauthority.model.hibernate.Project"
     *                        column="c_project_id"
     *                        not-null="true"
     *                        cascade="none"
     *                        lazy="false"
     *                        fetch="join"
     */
    public Project getProject(){
    		return this.project;
    }

    /**
     * Set the project
     * 
     * @param p The project name.
     */
    public void setProject(Project p){
    		this.project = p;
    }
    
	/**
     * Get the roles
     * 
     * @return A list containing the roles.
     */
    public List<Role> getRoles(){
    		List<Role> lg  = new ArrayList<Role>();
		for(int i=0; i<roleLink.size();i++){
			lg.add(roleLink.get(i).getRole());
		}
		return lg;
    }

	/**
     * Set roles
     * 
     * @param roles A list containing the roles.
     */
    public void setRoles(List<Role> roles){
		List<RoleLink> llg = new ArrayList<RoleLink>();
		for(int i=0; i<roles.size();i++){
			llg.add(new RoleLink(roles.get(i)));
		}
		this.roleLink=llg;
    }
    
    public Element toDOM(){
    		return null;
    }

    
	public void print(){
		sLog.info("Attribute: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: "+this.getProject());
		for(int i=0;i<getRoleLink().size();i++){
			getRoleLink().get(i).print();
		}
		for(int i=0;i<getGroupLink().size();i++){
			getGroupLink().get(i).print();
		}
		for(int i=0;i<getRoles().size();i++){
			getRoles().get(i).print();
		}
		for(int i=0;i<getGroups().size();i++){
			getGroups().get(i).print();
		}
	}

	/**
     * Get the groups
     * 
     * @return A list containing the groups. The group's GroupAttribute list is also populated.
     */

	public List<Group> getGroups() {
		List<Group> lg  = new ArrayList<Group>();
		for(int i=0; i<groupLink.size();i++){
			
			Group gr = groupLink.get(i).getGroup();
			gr.setGroupAttributes(null); 
			
			List<GroupAttribute> gAttributes = new ArrayList<GroupAttribute>();
			GroupLink l = groupLink.get(i);
			if(l!= null && l.getGroupAttriubutes() != null){
				for(GroupAttributeLink a : groupLink.get(i).getGroupAttriubutes()){
					gAttributes.add(a.getGroupAttribute());
				}
				
				gr.setGroupAttributes(gAttributes);
			}

			lg.add(gr);
		}	
		return lg;
	}
	

	/**
	 * @param groups The groups to set.
	 */
	public void setGroups(List<Group> groups) {
		List<GroupLink> llg = new ArrayList<GroupLink>();
		for(int i=0; i<groups.size();i++){
			llg.add(new GroupLink(groups.get(i)));
		}
		this.groupLink=llg;
	}

	
	/**
	 * @param g
	 * @return
	 */
	public boolean addGroup(Group g){
		boolean rc = false;
		if(!isKnownGroup(g)){
			GroupLink gl = new GroupLink();
			gl.setGroup(g);
			rc = groupLink.add(gl);
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
		for (int i=0; i < groupLink.size(); i++) {
			if (groupLink.get(i).getGroup().identical(g)) {
				groupLink.remove(i);
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
			RoleLink rl = new RoleLink(r);
			rl.setRole(r);
			rc = roleLink.add(rl);
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
		for (int i=0; i < roleLink.size(); i++) {
			if (roleLink.get(i).getRole().identical(r)) {
				roleLink.remove(i);
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
		ListIterator<Role> it = getRoles().listIterator();
		while(it.hasNext()){
			Role rtemp = it.next();
			if(rtemp.getRoleName().equals(r.getRoleName())){
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
	public Role getPersistedRole(RoleType r){
		Role rc = null;
		ListIterator<Role> it = getRoles().listIterator();
		while(it.hasNext()){
			Role rtemp = it.next();
			if(rtemp.getRoleName().equals(r.getName())){
				rc = rtemp;
				break;
			}
		}
		return rc;		
	}
	/**
	 * @param g
	 * @return
	 */
	public boolean isKnownGroup(Group g){
		boolean rc = false;
		ListIterator<Group> it = getGroups().listIterator();
		while(it.hasNext()){
			Group gtemp = it.next();
			if(gtemp.getGroupName().equals(g.getGroupName())){
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
	public Group getPersistedGroup(GroupType g){
		Group rc = null;
		ListIterator<Group> it = getGroups().listIterator();
		while(it.hasNext()){
			Group gtemp = it.next();
			if(gtemp.getGroupName().equals(g.getName())){
				rc = gtemp;
				break;
			}
		}
		return rc;		
	}
	public static Attribute fromAttributeType(AttributeType at,
			Project foundProject) {
		Attribute attribute = null;
		
		attribute = new Attribute();
		ArrayList<RoleLink> lra = new ArrayList<RoleLink>();
		ArrayList<GroupLink> lga = new ArrayList<GroupLink>();
		if(at.getRole()!=null){
			for (int j = 0; j < at.getRole().length; j++) {
				Role foundRole = null;
				if ((foundRole = foundProject.getPersistedRole(at.getRole(j))) != null) {
					RoleLink rl = new RoleLink();
					rl.setRole(foundRole);
					lra.add(rl);
				}
			}
		}
		if(at.getGroup()!=null){
			for (int j = 0; j < at.getGroup().length; j++) {
				Group foundGroup = null;
				if ((foundGroup = foundProject.getPersistedGroup(at.getGroup(j))) != null) {
					GroupLink gl = new GroupLink();
					gl.setGroup(foundGroup);
					lga.add(gl);
				}
			}
		}
		attribute.setProject(foundProject);
		attribute.setGroupLink(lga);
		attribute.setRoleLink(lra);
		return attribute;
	}
	
	public static Attribute fromAttributeType(AttributeType at) {
		Attribute attribute = new Attribute();
		//attribute.setGroupLink(new GroupLink());
		//attribute.setRoleLink(new RoleLink());
		ArrayList<RoleLink> roleList = new ArrayList<RoleLink>();
		ArrayList<GroupLink> groupList = new ArrayList<GroupLink>();
		if (at != null) {
			if (at.getRole() != null) {
				for (int i = 0; i < at.getRole().length; i++) {
					//roleList.add(Role.fromRoleType(at.getRole()[i]));
					roleList.add(new RoleLink(Role.fromRoleType(at.getRole(i))));
				}

			}
			if (at.getGroup() != null) {
				for (int i = 0; i < at.getGroup().length; i++) {
					//groupList.add(Group.fromGroupType(at.getGroup()[i]));
					groupList.add(new GroupLink(Group.fromGroupType(at.getGroup(i))));
				}

			}
			attribute.setRoleLink(roleList);
			attribute.setGroupLink(groupList);
			attribute.setProject(new Project(at.getProject().getName()));
		}
		return attribute;
	}
	
	public AttributeType toAttributeType(){
		AttributeType at = new AttributeType();
		at.setProject(project.toProjectType());

		if (getRoles() != null) {
			List<Role> rl = getRoles();
			RoleType[] rta = new RoleType[rl.size()];
			for(int i=0; i<rl.size();i++){
				rta[i]=rl.get(i).toRoleType();
			}
			at.setRole(rta);
		}
		if (getGroups() != null) {
			List<Group> gl = getGroups();
			GroupType[] gta = new GroupType[gl.size()];
			for(int i=0; i<gl.size();i++){
				gta[i]=gl.get(i).toGroupType();
			}
			at.setGroup(gta);						
		}	
		return at;
	}

	/**
	 * @return Returns the groupLink.
        * @hibernate.list cascade="all-delete-orphan" lazy="false" batch-size="100"
        * @hibernate.one-to-many class="org.psygrid.security.attributeauthority.model.hibernate.GroupLink"
        * @hibernate.key column="c_attribute_id" not-null="false"
        * @hibernate.list-index column="c_agl_index"
	 */
	public List<GroupLink> getGroupLink() {
		return this.groupLink;
	}

	/**
	 * @param groupLink The groupLink to set.
	 */
	public void setGroupLink(List<GroupLink> groupLink) {
		this.groupLink = groupLink;
	}

 
	/**
	 * @return Returns the roleLink.
        * @hibernate.list cascade="all-delete-orphan" lazy="false" batch-size="100"
        * @hibernate.one-to-many class="org.psygrid.security.attributeauthority.model.hibernate.RoleLink"
        * @hibernate.key column="c_attribute_id" not-null="false"
        * @hibernate.list-index column="c_arl_index"
	 */
	public List<RoleLink> getRoleLink() {
		return this.roleLink;
	}

	/**
	 * @param roleLink The roleLink to set.
	 * 
	 */
	public void setRoleLink(List<RoleLink> roleLink) {
		this.roleLink = roleLink;
	}
	
	public void linkToProject(Project p){
			
		setProject(p);
		
		List<RoleLink> rltemp = roleLink; 
		ListIterator<RoleLink> itr = rltemp.listIterator();
		while(itr.hasNext()){
			RoleLink rtemp = itr.next();
			Role foundRole = null;
			if((foundRole=p.getPersistedRole(rtemp.getRole()))!=null){
				// attach the persisted role
				rtemp.setRole(foundRole);
			}else{
				rltemp.remove(rtemp);
			}
		}
		roleLink=rltemp;
		
		List<GroupLink> gltemp = groupLink; 
		ListIterator<GroupLink> itg = gltemp.listIterator();
		while(itg.hasNext()){
			GroupLink gtemp = itg.next();
			Group foundGroup = null;
			if((foundGroup=p.getPersistedGroup(gtemp.getGroup()))!=null){
				// attach the persisted group
				gtemp.setGroup(foundGroup);
			}else{
				gltemp.remove(gtemp);
			}
		}
		groupLink=gltemp;

	}
}
