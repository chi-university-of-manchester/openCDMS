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

package org.psygrid.esl.model.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * The details of a project held by the ESL
 * 
 * @author Lucy Bridges
 *
 */
public class Project extends Auditable {

	/**
	 * The project code of the project. 
	 * <p>
	 * Typically this property is used to link the dataset to 
	 * the security system.
	 */
	private String projectCode;

	/**
	 * The name of the project. 
	 * <p>
	 */
	private String projectName;
	
	
	/**
	 * Whether it is ok to delete data from the ESL or not
	 */
	private boolean okToDeleteEslData;

	/**
	 * Array of groups associated with the Project.
	 */
	private Group[] groups = new Group[0];

	private Randomisation randomisation = null;

	private CustomField[] customFields = new CustomField[0];

	public Group[] getGroups() {
		return groups;
	}

	public void setGroups(Group[] groups) {
		this.groups = groups;
	}

	/**
	 * @return the projectCode
	 */
	public String getProjectCode() {
		return projectCode;
	}

	/**
	 * @param projectCode the projectCode to set
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	/**
	 * Return a boolean to indicate whether it is ok to delete data from the ESL or not
	 * @return The boolean result
	 */
	public boolean getOkToDeleteEslData() {
		return okToDeleteEslData;
	}
	
	/**
	 * Set whether or not it is ok to delete data from the ESL
	 * @param value The value to set
	 */
	public void setOkToDeleteEslData(boolean value) {
		okToDeleteEslData = value;
	}

	/**
	 * @return the randomisation
	 */
	public Randomisation getRandomisation() {
		return randomisation;
	}

	/**
	 * @param randomisation the randomisation to set
	 */
	public void setRandomisation(Randomisation randomisation) {
		this.randomisation = randomisation;
	}


	public CustomField[] getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomField[] customFields) {
		this.customFields = customFields;
	}
	

	public org.psygrid.esl.model.hibernate.Project toHibernate(){
		//create list to hold references to objects in the project's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its hibernate equivalent once and once only
		Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.esl.model.hibernate.Persistent>();
		org.psygrid.esl.model.hibernate.Project hDS = toHibernate(dtoRefs);
		dtoRefs = null;
		return hDS;
	}

	public org.psygrid.esl.model.hibernate.Project toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		//check for an already existing instance of a hibernate object for this 
		//project in the set of references
		org.psygrid.esl.model.hibernate.Project hProj = null;
		if ( hRefs.containsKey(this)){
			hProj = (org.psygrid.esl.model.hibernate.Project)hRefs.get(this);
		}
		if ( null == hProj ){
			//an instance of the project has not already
			//been created, so create it and add it to the map of references
			hProj = new org.psygrid.esl.model.hibernate.Project();
			hRefs.put(this, hProj);
			toHibernate(hProj, hRefs);
		}

		return hProj;

	}

	public void toHibernate(org.psygrid.esl.model.hibernate.Project hProject, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		super.toHibernate(hProject, hRefs);

		hProject.setProjectCode(this.projectCode);
		hProject.setProjectName(this.projectName);
		hProject.setOkToDeleteEslData(okToDeleteEslData);

		if (randomisation != null) {
			hProject.setRandomisation(randomisation.toHibernate(hRefs));
		}

		//add each group in the array to the List in the persistable hibernate bean. 
		if (groups != null) {
			for (int i=0; i<this.groups.length; i++){
				Group g = groups[i];
				if ( null != g ){
					hProject.setGroup(g.toHibernate(hRefs));
				}
			}    
		}

		if (customFields != null) {
			for (int i=0, c=customFields.length; i<c; i++){
				CustomField cf = customFields[i];
				if ( null != cf ){
					hProject.getCustomFields().add(cf.toHibernate(hRefs));
				}
			}    
		}

	}



}
