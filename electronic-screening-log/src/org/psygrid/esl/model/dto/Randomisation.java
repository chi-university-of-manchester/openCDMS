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
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.psygrid.common.email.Email;
import org.psygrid.common.email.EmailDTO;
import org.psygrid.esl.util.Pair;	
import org.psygrid.esl.model.dto.Role;
import org.psygrid.esl.model.dto.Strata;


/**
 * Class to represent the details of the Randomisation
 * process performed on the project.
 * 
 * @author Lucy Bridges
 *
 */
public class Randomisation extends Persistent {

	/**
	 * The name given to this randomisation process.
	 */
	private String name;

	/**
	 * A list of people to be notified when randomisation is used.
	 */
	private Role[] rolesToNotify = null;

	/**
	 * A list of the factors taken into account by the randomisation algorithm.
	 */
	private Strata[] strata = null;

	/**
	 * A list of treatments that can be applied during randomisation
	 */
	private Pair<String,String>[] treatments = null;

	/**
	 * Emails to be sent out to various roles at different points during the 
	 * randomisation process
	 */
	private Pair<String,EmailDTO>[] emails = null;
	
	/**
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the different roles to notify during randomisation process
	 * @return Role[]
	 */
	public Role[] getRolesToNotify() {
		return rolesToNotify;
	}

	/**
	 * Set the different Roles to notify during randomisation process
	 * 
	 * @param rolesToNotify the roleToNotify to set
	 */
	public void setRolesToNotify(Role[] rolesToNotify) {
		this.rolesToNotify = rolesToNotify;
	}
	
	/**
	 * @return Strata[]
	 */
	public Strata[] getStrata() {
		return strata;
	}

	/**
	 * @param strata the strata to set
	 */
	public void setStrata(Strata[] strata) {
		this.strata = strata;
	}

	/**
	 * Retrieve the treatments that can be allocated during randomisation
	 * 
	 * @return  Pair<String,String>[]
	 */
	public Pair<String,String>[] getTreatments() {
		return treatments;
	}

	/**
	 * Set the treatments that can be allocated during randomisation
	 * 
	 * @param treatments the treatments to set
	 */
	public void setTreatments(Pair<String,String>[] treatments) {
		this.treatments = treatments;
	}

	
	/**
	 * Get the emails that are to be sent out at various points during the randomisation
	 * process.
	 * 
	 * Stored as a Map of EmailType->Email, where EmailType should be a
	 * String specified in org.psygrid.esl.randomisation.EmailType
	 *
	 * ie:
	 * "invocation" The email to be sent when randomisation is invoked for a subject
	 * "decision"   The email to be sent when a treatment arm is allocated to a subject
	 * 				as a result of randomisation 
	 * "treatment" 	The email to be sent, normally to the therapist, to inform of the 
	 * 				treatment arm allocated by the randomisation process
	 * 
	 * @return Pair<String,Email>[]
	 */
	public Pair<String,EmailDTO>[] getEmails() {
		return emails;
	}

	/**
	 * Set the emails that are to be sent out at various points during the randomisation
	 * process.
	 * 
	 * Stored as a Map of EmailType->Email, where EmailType should be a
	 * String specified in org.psygrid.esl.randomisation.EmailType
	 *
	 * ie:
	 * "invocation" The email to be sent when randomisation is invoked for a subject
	 * "decision"   The email to be sent when a treatment arm is allocated to a subject
	 * 				as a result of randomisation 
	 * "treatment" 	The email to be sent, normally to the therapist, to inform of the 
	 * 				treatment arm allocated by the randomisation process
	 * 
	 * @param emails the emails to set
	 */
	public void setEmails(Pair<String,EmailDTO>[] emails) {
		this.emails = emails;
	}
	
	public org.psygrid.esl.model.hibernate.Randomisation toHibernate(){
		//create list to hold references to objects in the project's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its hibernate equivalent once and once only
		Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.esl.model.hibernate.Persistent>();
		org.psygrid.esl.model.hibernate.Randomisation hRand = toHibernate(dtoRefs);
		dtoRefs = null;
		return hRand;
	}

	public org.psygrid.esl.model.hibernate.Randomisation toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		//check for an already existing instance of a hibernate object for this 
		//randomisation in the set of references
		org.psygrid.esl.model.hibernate.Randomisation hRand = null;
		if ( hRefs.containsKey(this)){
			hRand = (org.psygrid.esl.model.hibernate.Randomisation)hRefs.get(this);
		}
		if ( hRand == null ){
			//an instance of the randomisation has not already
			//been created, so create it and add it to the map of references
			hRand = new org.psygrid.esl.model.hibernate.Randomisation();
			hRefs.put(this, hRand);
			toHibernate(hRand, hRefs);
		}

		return hRand;

	}

	public void toHibernate(org.psygrid.esl.model.hibernate.Randomisation hRand, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		super.toHibernate(hRand, hRefs);

		hRand.setName(this.name);
		
		if (treatments != null) {
			Map<String,String> hTreatments = new HashMap<String,String>();
			for (Pair<String,String> s: treatments) {
				hTreatments.put(s.getName(), s.getValue());
			}
			hRand.setTreatments(hTreatments);
		}
		
		//add the List of Roles to be notified and the list of strata into the Lists in the persistable hibernate bean.
		List<org.psygrid.esl.model.IStrata> hStrata = new ArrayList<org.psygrid.esl.model.IStrata>(); 
		if (strata != null) {
			for (int i=0; i<this.strata.length; i++){
				Strata r = strata[i];
				if ( r != null ){
					hStrata.add(r.toHibernate(hRefs));
				}
			} 
			hRand.setStrata(hStrata);
		}

		if (rolesToNotify != null) {
			List<org.psygrid.esl.model.IRole> hRole = new ArrayList<org.psygrid.esl.model.IRole>();
			for (int j=0; j<rolesToNotify.length; j++) {
				Role r = rolesToNotify[j];
				if (r != null) {
					hRole.add(r.toHibernate(hRefs));
				}
			}
			hRand.setRolesToNotify(hRole);
		}
		
		if (emails != null) {
			Map<String,Email> hEmails = new HashMap<String,Email>();
			for (Pair<String,EmailDTO> p: emails) {
				Email email = null;
				if (p.getValue() != null) {
					email = p.getValue().toHibernate();
				}
				hEmails.put(p.getName(), email);
			}
			hRand.setEmails(hEmails);
		}
		
	}
}
