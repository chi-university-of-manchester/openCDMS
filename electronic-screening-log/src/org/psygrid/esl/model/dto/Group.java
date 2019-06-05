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
 * A Group belonging to a project (typically an NHS area)
 * 
 * @author Lucy Bridges
 *
 */
public class Group extends Auditable {

	private String name;
	private String code;

	private Project project = null;
	
	/**
	 * @return the Group name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the unique Group code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	public Project getProject() {
		return project;
	}
	
	public void setProject(Project project) {
		this.project = project;
	}
	
	public org.psygrid.esl.model.hibernate.Group toHibernate(){
		//create list to hold references to objects in the group's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its hibernate equivalent once and once only
		Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.esl.model.hibernate.Persistent>();
		org.psygrid.esl.model.hibernate.Group hGroup = toHibernate(dtoRefs);
		dtoRefs = null;
		return hGroup;
	}

	public org.psygrid.esl.model.hibernate.Group toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		
	     //check for an already existing instance of a hibernate object for this 
        //record in the map of references
		org.psygrid.esl.model.hibernate.Group hG = null;
        if ( hRefs.containsKey(this)){
            hG = (org.psygrid.esl.model.hibernate.Group)hRefs.get(this);
        }
        if ( null == hG ){
            //an instance of the record has not already
            //been created, so create it, and add it to 
            //the map of references	
        	hG = new org.psygrid.esl.model.hibernate.Group();
        	hRefs.put(this, hG);
        	toHibernate(hG, hRefs);
        }
		return hG;
	}

	public void toHibernate(org.psygrid.esl.model.hibernate.Group hG, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		super.toHibernate(hG, hRefs);
		hG.setGroupName(this.name);
		hG.setGroupCode(this.code);

		if (project != null) {
			hG.setProject(project.toHibernate(hRefs));
		}
	}

}
