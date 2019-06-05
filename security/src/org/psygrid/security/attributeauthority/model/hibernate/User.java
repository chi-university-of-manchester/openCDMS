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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.attributeauthority.dao.DAOException;
import org.psygrid.security.attributeauthority.dao.ProjectDAO;
import org.psygrid.security.attributeauthority.dao.UserDAO;
import org.psygrid.security.attributeauthority.model.IUser;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;

/**
 * @author jda
 * 
 * @hibernate.joined-subclass table="t_users"
 * @hibernate.joined-subclass-key column="c_id"
 */

public class User extends Persistent implements IUser {

	private static final long serialVersionUID = 1L;

	private static Log sLog = LogFactory.getLog(User.class);

	/**
	 * User identitity, should be Distinuguished Name
	 */
	private String userName;
	
	private boolean passwordChangeRequired;
	
	private List<PasswordRecord> previousPasswords = new ArrayList<PasswordRecord>();

	private List<LoginRecord> loginHistory = new ArrayList<LoginRecord>();
	
	private Date passwordResetDate;
	
	private String passwordResetUUID;

	// Made these transient because Persistent is now Serializable
	// We should probably not be referring to DAOs in model objects.
	
	transient private ProjectDAO pDAO;

	transient private UserDAO uDAO;

	/**
	 * User's attributes
	 */
	private List<Attribute> attributes = new ArrayList<Attribute>();

	/**
	 * If True, then the user has left and the User object only remains
	 * to prevent a new user being created with the same username.
	 */
	private boolean dormant;
	
	/**
	 * Default no-arg constructor, as required by the Hibernate framework for
	 * all persistable classes.
	 * 
	 */
	protected User() {
	}

	/**
	 * Constructor that accepts the name of the user
	 * 
	 * @param userName
	 *            The name of the user.
	 */
	public User(String userName) {
		this.setUserName(userName);
	}

	/**
	 * Get the userName
	 * 
	 * @return The userName.
	 * @hibernate.property column = "c_user_name"
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set the userName
	 * 
	 * @param userName
	 *            The user name.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Get the attributes
	 * 
	 * @return A list containing the attributes.
	 * @hibernate.list cascade="all-delete-orphan" lazy="false" batch-size="100"
	 * @hibernate.one-to-many class="org.psygrid.security.attributeauthority.model.hibernate.Attribute"
	 * @hibernate.key column="c_user_id" not-null="true"
	 * @hibernate.list-index column="c_ua_index"
	 */
	public List<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * Set attributes
	 * 
	 * @param attributes
	 *            A list containing the attributes.
	 */
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param p
	 * @return
	 */
	public boolean isMember(Project p) {
		boolean rc = false;
		ListIterator<Attribute> it = attributes.listIterator();
		while (it.hasNext()) {
			Attribute atemp = it.next();
			if (atemp.getProject().identical(p)) {
				rc = true;
				break;
			}
		}
		return rc;
	}

	/**
	 * @param p
	 * @param r
	 * @return
	 */
	public boolean hasRoleInProject(Project p, Role r) {
		boolean rc = false;
		ListIterator<Attribute> it = attributes.listIterator();
		while (it.hasNext() && rc != true) {
			Attribute atemp = it.next();
			if (atemp.getProject().identical(p)) {
				ListIterator<Role> it2 = atemp.getRoles().listIterator();
				while (it2.hasNext()) {
					Role rtemp = it2.next();
					if (rtemp.identical(r)) {
						rc = true;
						break;
					}
				}
			}
		}
		return rc;
	}

	/**
	 * @param p
	 *            Project
	 * @param g
	 *            Group
	 * @return
	 */
	public boolean hasGroupInProject(Project p, Group g) {
		boolean rc = false;
		ListIterator<Attribute> it = attributes.listIterator();
		while (it.hasNext() && rc != true) {
			Attribute atemp = it.next();
			if (atemp.getProject().identical(p)) {
				ListIterator<Group> it2 = atemp.getGroups().listIterator();
				while (it2.hasNext()) {
					Group gtemp = it2.next();
					if (gtemp.identical(g)) {
						rc = true;
						break;
					}
				}
			}
		}
		return rc;
	}

	/**
	 * @param g
	 * @return
	 */
	public boolean isMember(String p) {
		return isMember(new Project(p));
	}

	/**
	 * @param g
	 * @param r
	 * @return
	 */
	public boolean hasRoleInProject(String p, String r) {
		return hasRoleInProject(new Project(p), new Role(r, null));
	}

	/**
	 * @param g
	 * @param r
	 * @return
	 */
	public boolean hasGroupInProject(String p, String g) {
		return hasGroupInProject(new Project(p), new Group(g));
	}

	/**
	 * @param p
	 * @return
	 */
	public boolean addAttribute(Attribute a) {
		boolean rc = false;
		Project fp = null;
		try {
			if ((fp = pDAO.getProject(a.getProject())) != null) {
				if (isMember(a.getProject())) {
					for (int i = 0; i < a.getRoleLink().size(); i++) {
						rc = addRoleInProject(a, a.getProject(), a.getRoles()
								.get(i));
					}
					for (int i = 0; i < a.getGroupLink().size(); i++) {
						rc = addGroupInProject(a, a.getProject(), a.getGroups()
								.get(i));
					}
				} else {
					a.linkToProject(fp);
					rc = attributes.add(a);
					uDAO.updateUser(this);
				}
			}
		} catch (DAOException daoe) {
			sLog.info(daoe.getMessage());
		}

		return rc;
	}

	/**
	 * @param ata
	 * @return
	 */
	public boolean addAttribute(AttributeType[] ata) {
		boolean rc = false;
		for (int i = 0; i < ata.length; i++) {
			Attribute a = Attribute.fromAttributeType(ata[i]);
			rc = addAttribute(a);
		}
		return rc;
	}

	/**
	 * @param p
	 * @param r
	 * @return
	 */
	public boolean addRoleInProject(Attribute a, Project p, Role r) {
		boolean rc = false;
		Project fp = null;
		try {
			if ((fp = pDAO.getProject(p)) != null) {
				if (!hasRoleInProject(p, r)) {
					if (!isMember(p)) {
						addAttribute(a);
						rc = true;
					} else {
						Role foundRole = null;
						if ((foundRole = fp.getPersistedRole(r)) != null) {
							Attribute aa = getAttributeByProject(fp);
							aa.addRole(foundRole);
							uDAO.updateUser(this);
							rc = true;
						}
					}
				} else {
					rc = true;
				}
			}
		} catch (DAOException daoe) {
			sLog.info(daoe.getMessage());
		}
		return rc;
	}

	/**
	 * @param ata
	 *            AttributeType[]
	 * @return
	 */
	public boolean addRoleInProject(AttributeType[] ata, ProjectDAO projectDAO) {
		for (int i = 0; i < ata.length; i++) {
			Attribute a = Attribute.fromAttributeType(ata[i]);
			for (int j = 0; j < a.getRoleLink().size(); j++) {
				addRoleInProject(a, a.getProject(), a.getRoles().get(j));
			}
		}
		return true;
	}

	/**
	 * @param p
	 * @param g
	 * @return
	 */
	public boolean addGroupInProject(Attribute a, Project p, Group g) {
		boolean rc = false;
		Project fp = null;
		try {
			if ((fp = pDAO.getProject(p)) != null) {
				if (!hasGroupInProject(p, g)) {
					if (!isMember(p)) {
						addAttribute(a);
						rc = true;
					} else {
						Group foundGroup = null;
						if ((foundGroup = fp.getPersistedGroup(g)) != null) {
							Attribute aa = getAttributeByProject(fp);
							aa.addGroup(foundGroup);
							uDAO.updateUser(this);
							rc = true;
						}
					}
				} else {
					rc = true;
				}
			}
		} catch (DAOException daoe) {
			sLog.info(daoe.getMessage());
		}
		return rc;
	}

	/**
	 * @param ata
	 *            AttributeType[]
	 * @return
	 */
	public boolean addGroupInProject(AttributeType[] ata) {
		for (int i = 0; i < ata.length; i++) {
			Attribute a = Attribute.fromAttributeType(ata[i]);
			for (int j = 0; j < a.getGroupLink().size(); j++) {
				addGroupInProject(a, a.getProject(), a.getGroups().get(j));
			}
		}
		return true;
	}

	/**
	 * @param p
	 * @return
	 */
	public boolean removeProject(Project p) {
		boolean rc = false;
		for (int i=0; i < attributes.size(); i++) {
			if (attributes.get(i).getProject().identical(p)) {
				attributes.remove(i);
				rc = true;
				break;
			}
		}
		return rc;	
	}
	
	/**
	 * Remove all of the User's attributes
	 */
	public void removeAllAttributes(){
		attributes.clear();
	}

	/**
	 * @param pdt
	 * @return
	 */
	public boolean removeProject(AttributeType[] pdt) {
		for (int i = 0; i < pdt.length; i++) {
			Attribute a = Attribute.fromAttributeType(pdt[i]);
			removeProject(a.getProject());
		}
		return true;
	}

	/**
	 * @param p
	 * @param r
	 * @return
	 */
	public boolean removeRoleFromProject(Project p, Role r) {
		boolean rc = false;
		ListIterator<Attribute> it = attributes.listIterator();
		while (it.hasNext() && rc != true) {
			Attribute atemp = it.next();
			if (atemp.getProject().identical(p)) {
				atemp.removeRole(r);
			}
		}
		return rc;
	}

	/**
	 * @param pdt
	 * @return
	 */
	public boolean removeRoleFromProject(AttributeType[] pdt) {
		for (int i = 0; i < pdt.length; i++) {
			Attribute a = Attribute.fromAttributeType(pdt[i]);
			for (int j = 0; j < a.getRoleLink().size(); j++) {
				removeRoleFromProject(a.getProject(), a.getRoles().get(j));
			}
		}
		return true;
	}

	/**
	 * @param p
	 * @param g
	 * @return
	 */
	public boolean removeGroupFromProject(Project p, Group g) {
		boolean rc = false;
		ListIterator<Attribute> it = attributes.listIterator();
		while (it.hasNext() && rc != true) {
			Attribute atemp = it.next();
			if (atemp.getProject().identical(p)) {
				atemp.removeGroup(g);
			}
		}
		return rc;
	}

	/**
	 * @param pdt
	 * @return
	 */
	public boolean removeGroupFromProject(AttributeType[] pdt) {
		for (int i = 0; i < pdt.length; i++) {
			Attribute a = Attribute.fromAttributeType(pdt[i]);
			for (int j = 0; j < a.getGroupLink().size(); j++) {
				removeGroupFromProject(a.getProject(), a.getGroups().get(j));
			}
		}
		return true;
	}


	public static User fromUserPrivelegesType(UserPrivilegesType upt,
			ProjectDAO projectDAO) {
		User u = new User();

		if (upt != null) {
			ArrayList<Attribute> attributeList = new ArrayList<Attribute>();
			if (upt.getAttribute() != null) {
				for (int i = 0; i < upt.getAttribute().length; i++) {
					try {
						Project foundProject = null;
						if ((foundProject = projectDAO.getProject(upt
								.getAttribute(i).getProject())) != null) {
							Attribute atr = Attribute.fromAttributeType(upt
									.getAttribute(i), foundProject);
							if (atr != null) {
								attributeList.add(atr);
							}
						}
					} catch (DAOException daoe) {
						daoe.printStackTrace();
					}
				}
			}
			u.setAttributes(attributeList);
		}
		u.setUserName(upt.getUser().getDistinguishedName());
		return u;
	}

	public void print() {
		sLog.info("User: " + toString() + "\n\tID: " + this.getId()
				+ "\n\tVersion: " + getVersion() + "\n\tName: "
				+ this.getUserName());
		for (int i = 0; i < attributes.size(); i++) {
			attributes.get(i).print();
		}
	}

	public Attribute getAttributeByProject(Project p) {
		Iterator<Attribute> it = attributes.iterator();
		while (it.hasNext()) {
			Attribute temp = it.next();
			if (temp.getProject().identical(p)) {
				return temp;
			}
		}
		return null;
	}

	/**
	 * @return Returns the pDAO.
	 */
	public ProjectDAO getPDAO() {
		return this.pDAO;
	}

	/**
	 * @param pdao
	 *            The pDAO to set.
	 */
	public void setPDAO(ProjectDAO pdao) {
		this.pDAO = pdao;
	}

	/**
	 * @return Returns the uDAO.
	 */
	public UserDAO getUDAO() {
		return this.uDAO;
	}

	/**
	 * @param udao
	 *            The uDAO to set.
	 */
	public void setUDAO(UserDAO udao) {
		this.uDAO = udao;
	}
	
	public UserPrivilegesType toUserPrivilegesType(){
		UserPrivilegesType upt = new UserPrivilegesType();
		upt.setUser(new UserType(null, null, null, null, userName, null, null));
		AttributeType[] ata = new AttributeType[attributes.size()];
		for(int i=0;i<attributes.size();i++){
			ata[i] = attributes.get(i).toAttributeType();
		}
		upt.setAttribute(ata);
		return upt;	
	}

	/**
	 * @return Returns the passwordChangeRequired.
	 * @hibernate.property column = "c_passwd_change"
	 */
	public boolean getPasswordChangeRequired() {
		return this.passwordChangeRequired;
	}

	/**
	 * @param passwordChangeRequired The passwordChangeRequired to set.
	 */
	public void setPasswordChangeRequired(boolean passwordChangeRequired) {
		this.passwordChangeRequired = passwordChangeRequired;
	}

	/**
	 * Get the previousPasswords
	 * 
	 * @return A list containing the attributes.
	 * @hibernate.list cascade="all-delete-orphan" lazy="false" batch-size="100"
	 * @hibernate.one-to-many class="org.psygrid.security.attributeauthority.model.hibernate.PasswordRecord"
	 * @hibernate.key column="c_user_id" not-null="false"
	 * @hibernate.list-index column="c_uph_index"
	 */
	public List<PasswordRecord> getPreviousPasswords() {
		return this.previousPasswords;
	}

	/**
	 * @param previousPasswords The previousPasswords to set.
	 */
	public void setPreviousPasswords(List<PasswordRecord> previousPasswords) {
		this.previousPasswords = previousPasswords;
	}

	/**
	 * @param loginHistory The loginHistory to set.
	 */
	public void setLoginHistory(List<LoginRecord> loginHistory) {
		this.loginHistory = loginHistory;
	}
	/**
	 * Get the loginHistory
	 * 
	 * @return A list containing the attributes.
	 * @hibernate.list cascade="all-delete-orphan" lazy="false" batch-size="100"
	 * @hibernate.one-to-many class="org.psygrid.security.attributeauthority.model.hibernate.LoginRecord"
	 * @hibernate.key column="c_user_id" not-null="false"
	 * @hibernate.list-index column="c_ulh_index"
	 */
	public List<LoginRecord> getLoginHistory() {
		return loginHistory;
	}

	/**
	 * @hibernate.property column="c_dormant"
	 */
	public boolean isDormant() {
		return dormant;
	}

	public void setDormant(boolean dormant) {
		this.dormant = dormant;
	}

	/**
	 * @return the passwordResetDate
	 * @hibernate.property column="c_passwd_reset_date"
	 */
	public Date getPasswordResetDate() {
		// Defensive copy.
		return passwordResetDate==null?null:new Date(passwordResetDate.getTime());
	}

	/**
	 * @param passwordResetDate the passwordResetDate to set
	 */
	public void setPasswordResetDate(Date passwordResetDate) {
		// Defensive copy.
		this.passwordResetDate = passwordResetDate==null?null:new Date(passwordResetDate.getTime());
	}

	/**
	 * @hibernate.property column="c_passwd_reset_uuid" length="36" index="user_uuid_index"
	 * @return the passwordResetGUID
	 */
	public String getPasswordResetUUID() {
		return passwordResetUUID;
	}

	/**
	 * @param passwordResetGUID the passwordResetGUID to set
	 */
	public void setPasswordResetUUID(String passwordResetGUID) {
		this.passwordResetUUID = passwordResetGUID;
	}
	
	@Override
	public String toString(){
		return userName;
	}
}
