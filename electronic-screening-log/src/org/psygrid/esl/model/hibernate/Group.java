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


import org.psygrid.esl.model.IGroup;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.ModelException;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


/**
 * A realization of a single Group.
 * 
 * @author Lucy Bridges
 * 
 * @hibernate.joined-subclass table="t_groups"
 * 							  proxy="org.psygrid.esl.model.hibernate.Group"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Group extends Auditable implements IGroup {

	private String groupName;
	private String groupCode;
	private Project project = null;

	private List<ISubject> subjects = new ArrayList<ISubject>();

	public Group() {
		super();
	}


	public Group(String name) {
		this.groupName = name;
	}

	public Group(String name, String code) {
		this.groupName = name;
		this.groupCode = code;
	}
	
	/**
	 * @hibernate.property column="c_group_code"
	 * 	        			not-null="true"
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	/**
	 * @hibernate.property column="c_group_name"
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param name the name to set
	 */
	public void setGroupName(String name) {
		this.groupName = name;
	}

	/**
	 * @return The collection of trial Subjects in this Group
	 * 
	 * @hibernate.bag cascade="all" inverse="true"
	 * @hibernate.one-to-many class="org.psygrid.esl.model.hibernate.Subject"
	 * @hibernate.key column="c_group_id" not-null="true"
	 */
	public List<ISubject> getSubjects() {
		return subjects;
	}

	/**
	 * @param subjects the subjects to set
	 */
	protected void setSubjects(List<ISubject> subjects) {
		//List<ISubject> oldSubjects = this.subjects;
		this.subjects = subjects;
		//	propertyChangeSupport.firePropertyChange(SUBJECTS_PROPERTY, oldSubjects,
		//      this.subjects);
	}

	/**
	 * @param subject
	 */
	public void setSubject(ISubject subject) throws ModelException {
		if ( null == subject ){
			throw new ModelException("Cannot add a null Subject");
		}
		subjects.add(subject);

		/*Provenance prov = null;
		prov = new Provenance(null, subject);
		this.provItems.add(prov);
		*/
		//propertyChangeSupport.firePropertyChange(null, null, null);
	}

/*	public void updateSubject(ISubject oldSubject, ISubject newSubject) throws ModelException {
		if ( null == newSubject ){
			throw new ModelException("Cannot add a null Subject");
		}
	//	subjects.remove(oldSubject);
		subjects.add(newSubject);

		Provenance prov = null;
		prov = new Provenance(oldSubject, newSubject);
		this.provItems.add(prov);
	
		//propertyChangeSupport.firePropertyChange(null, null, null);
	}
	*/
	
	/**
	 * @hibernate.many-to-one class="org.psygrid.esl.model.hibernate.Project"
	 *                        column="c_project_id"
	 *                        not-null="false"
	 *                        insert="false"
	 *                        update="false"
	 */
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * Store object reference to maintain persistence
	 * 
	 * @return dto.Group
	 */

	public org.psygrid.esl.model.dto.Group toDTO(){
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.Group dtoGroup = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoGroup;
	}

	public org.psygrid.esl.model.dto.Group toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {

		//check for an already existing instance of a dto object for this 
		//group in the set of references
		org.psygrid.esl.model.dto.Group dtoG = null;
		if ( dtoRefs.containsKey(this)){
			dtoG = (org.psygrid.esl.model.dto.Group)dtoRefs.get(this);
		}
		if ( null == dtoG ){
			dtoG = new org.psygrid.esl.model.dto.Group();
			dtoRefs.put(this, dtoG);
			toDTO(dtoG, dtoRefs);
		}
		return dtoG;
	}

	public void toDTO(org.psygrid.esl.model.dto.Group dtoG, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {

		super.toDTO(dtoG, dtoRefs);
		dtoG.setName(this.groupName);
		dtoG.setCode(this.groupCode);

		/* 
		 * Removed code that converts subjects to DTOs to stop all ESL subjects from being sent 
		 * to the client when an ESL Project or Subject is transfered.
		 */

		if (project != null) {
			dtoG.setProject(project.toDTO(dtoRefs));
		}
	}
}


