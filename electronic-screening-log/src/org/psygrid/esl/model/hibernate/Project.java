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

package org.psygrid.esl.model.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.psygrid.esl.model.ICustomField;
import org.psygrid.esl.model.IGroup;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.ModelException;

/**
 * The top-level object representing the realization of a single Project.
 * 
 * @author Lucy Bridges
 * 
 * @hibernate.joined-subclass table="t_projects"
 * 								proxy="org.psygrid.esl.model.hibernate.Project"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Project extends Auditable implements IProject {

	private String projectCode;
	private String projectName;
	private boolean okToDeleteEslData;

	private IRandomisation randomisation = null;
	private List<IGroup> groups = new ArrayList<IGroup>();
	private List<ICustomField> customFields = new ArrayList<ICustomField>();
	

	/**
	 * Default constructor, required by Hibernate
	 */
	public Project() {
	}

	/**
	 * Constructor that accepts the name of the project.
	 *  
	 * @param name The Project Name
	 */
	public Project(String name){
		this.setProjectName(name);
	}

	/**
	 * Get the collection of NHS Groups involved in this project.
	 * 
	 * @return The List of Groups.
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.esl.model.hibernate.Group"
	 * @hibernate.key column="c_project_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<IGroup> getGroups() {
		return groups;
	}

	/**
	 * @hibernate.property column="c_project_code"
	 *       				not-null="true"
	 *                      unique="true"
	 */
	public String getProjectCode() {
		return projectCode;
	}

	/**
	 * @hibernate.property column="c_project_name"
	 */
	public String getProjectName() {
		return projectName;
	}
	
	/**
	 * @hibernate.property column="c_ok_to_delete_esl_data"
	 */
	public boolean getOkToDeleteEslData() {
		return okToDeleteEslData;
	}
	
	public void setOkToDeleteEslData(boolean value) {
		okToDeleteEslData = value;
	}

	/**
	 * Get the Randomisation associated with this project
	 * 
	 * @return The Randomisation object.
	 * 
	 * @hibernate.many-to-one class="org.psygrid.esl.model.hibernate.Randomisation"
	 *                        column="c_randomisation_id"
	 *                        not-null="false"
	 *                        unique="true"
	 *                        cascade="all"
	 */
	public IRandomisation getRandomisation() {
		return randomisation;
	}

	/**
	 * @see org.psygrid.esl.model.IProject#setGroups(java.util.List)
	 */
	protected void setGroups(List<IGroup> groups) {
		this.groups = groups;
	}

	public void setGroup(IGroup group) throws ModelException {
		if ( null == group ){
			throw new ModelException("Cannot add a null Group");
		}
		groups.add(group);
		
		/*Provenance prov;
		if (group.getId() != null) {
			prov = new Provenance(group, group);
		}
		else {
			prov = new Provenance(null, group);
		}
		this.provItems.add(prov);
		*/
		//propertyChangeSupport.firePropertyChange(null, null, null);
	}

	/**
	 * @see org.psygrid.esl.model.IProject#setProjectCode(java.lang.String)
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	/**
	 * @see org.psygrid.esl.model.IProject#setProjectName(java.lang.String)
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @see org.psygrid.esl.model.IProject#setRandomisation(org.psygrid.esl.model.IRandomisation)
	 */
	public void setRandomisation(IRandomisation randomisation) {
		this.randomisation = randomisation;
	}

	
	/**
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.esl.model.hibernate.CustomField"
	 * @hibernate.key column="c_project_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<ICustomField> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<ICustomField> customFields) {
		this.customFields = customFields;
	}

	public int getCustomFieldCount(){
		return customFields.size();
	}
	
	public void addCustomField(ICustomField field) throws ModelException {
		if ( null == field ){
			throw new ModelException("Cannot add a null custom field");
		}
		customFields.add(field);
	}
	
	public ICustomField getCustomField(int index) throws ModelException {
		try{
			return customFields.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No custom field exists for index "+index, ex);
		}
	}
	
	public void removeCustomField(int index) throws ModelException {
		try{
			customFields.remove(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No custom field exists for index "+index, ex);
		}
	}
	
	/**
	 * Store object reference to maintain persistence
	 * 
	 * @return dto.Project
	 */
	public org.psygrid.esl.model.dto.Project toDTO(){
		//create list to hold references to objects in the project's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its DTO equivalent once and once only
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.Project dtoProj = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoProj;
	}

	public org.psygrid.esl.model.dto.Project toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		//check for an already existing instance of a dto object for this 
		//project in the set of references
		org.psygrid.esl.model.dto.Project dtoProj = null;
		if ( dtoRefs.containsKey(this)){
			dtoProj = (org.psygrid.esl.model.dto.Project)dtoRefs.get(this);
		}
		if ( null == dtoProj ){
			//an instance of the project has not already
			//been created, so create it and add it to the map of references
			dtoProj = new org.psygrid.esl.model.dto.Project();
			dtoRefs.put(this, dtoProj);
			toDTO(dtoProj, dtoRefs);
		}

		return dtoProj;
	}
	

	public void toDTO(org.psygrid.esl.model.dto.Project dtoProj, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
		//note that the list of identifiers is not copied over to
		//the dto representation of the dataset - identifiers are
		//retrieved by a separate process
		super.toDTO(dtoProj, dtoRefs);

		dtoProj.setProjectName(this.projectName);
		dtoProj.setVersion(this.version);
		dtoProj.setProjectCode(this.projectCode);
		dtoProj.setOkToDeleteEslData(okToDeleteEslData);

		if (this.randomisation != null) {
			dtoProj.setRandomisation(this.randomisation.toDTO(dtoRefs));
		}

		if (this.groups != null) { 
			org.psygrid.esl.model.dto.Group[] dtoGrps = new org.psygrid.esl.model.dto.Group[this.groups.size()];
			for (int i=0; i<this.groups.size(); i++){
				IGroup g   = groups.get(i);
				if (g != null) {
					dtoGrps[i] = ((Group)g).toDTO(dtoRefs);
				}
			}        
			dtoProj.setGroups(dtoGrps);            
		}

		if (this.customFields != null) { 
			org.psygrid.esl.model.dto.CustomField[] dtoCFs = new org.psygrid.esl.model.dto.CustomField[this.customFields.size()];
			for (int i=0; i<this.customFields.size(); i++){
				ICustomField cf = customFields.get(i);
				if (cf != null) {
					dtoCFs[i] = cf.toDTO(dtoRefs);
				}
			}        
			dtoProj.setCustomFields(dtoCFs);            
		}

	}

}