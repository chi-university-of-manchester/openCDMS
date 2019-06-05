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

import java.util.Map;
import java.util.HashMap;


/**
 * Stores the log containing a history of all changes made to the Auditable object
 * 
 * @author Lucy Bridges
 *
 */
public class Auditable extends Provenanceable {


	/**
	 * The list of changes made to fields during this update instance
	 */
	private ProvenanceLog changes = new ProvenanceLog();
	


	public Auditable() {
	}


	public ProvenanceLog getLog() {
		return changes;
	}

	public void setLog(ProvenanceLog changes) {
		this.changes = changes;
	}


	public org.psygrid.esl.model.hibernate.Auditable toHibernate(){
		//create list to hold references to objects in the group's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its hibernate equivalent once and once only
		Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.esl.model.hibernate.Persistent>();
		org.psygrid.esl.model.hibernate.Auditable hElem = toHibernate(dtoRefs);
		dtoRefs = null;
		return hElem;
	}

	public org.psygrid.esl.model.hibernate.Auditable toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){

		//check for an already existing instance of a hibernate object for this 
		//record in the map of references
		org.psygrid.esl.model.hibernate.Auditable hE = null;
		if ( hRefs.containsKey(this)){
			hE = (org.psygrid.esl.model.hibernate.Auditable)hRefs.get(this);
		}
		if ( hE == null ){
			//an instance of the record has not already
			//been created, so create it, and add it to 
			//the map of references	
			hE = new org.psygrid.esl.model.hibernate.Auditable();
			hRefs.put(this, hE);
			toHibernate(hE, hRefs);
		}
		return hE;
	}

	public void toHibernate(org.psygrid.esl.model.hibernate.Auditable hE, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		super.toHibernate(hE, hRefs);

		if (changes != null) {
			hE.setLog(changes.toHibernate(hRefs));
		}
	}


}
