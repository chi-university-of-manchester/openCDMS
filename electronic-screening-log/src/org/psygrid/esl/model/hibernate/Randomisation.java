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

import org.psygrid.common.email.Email;
import org.psygrid.common.email.EmailDTO;
import org.psygrid.esl.model.IRole;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.ModelException;
import org.psygrid.esl.randomise.EmailType;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


/**
 * Representation of a realization of a Randomisation process applied
 * to a particular project.
 * 
 * @author Lucy Bridges
 * 
 * @hibernate.joined-subclass table="t_randomisation
 * 								proxy="org.psygrid.esl.model.hibernate.Randomisation"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Randomisation extends Persistent implements IRandomisation {

	private String name;
	private List<IRole> rolesToNotify = new ArrayList<IRole>();
	private List<IStrata> strata = new ArrayList<IStrata>();


	/**
	 * Treatments that can be applied during randomisation
	 * Records treatment code and treatment name.
	 */
	private Map<String,String> treatments = new HashMap<String,String>();

	/**
	 * Emails to be sent out to various roles at different points during 
	 * the randomisation process
	 */
	private Map<String, Email> emails = new HashMap<String, Email>();




	public Randomisation() {
	}

	public Randomisation (String name) {
		this.name = name;
	}

	/**
	 * @hibernate.property column="c_name"
	 *       				not-null="true"
	 *                      unique="true"
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
	 * Get the List of Roles to be notified of randomisation 
	 * 
	 * @return The List of Roles.
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.esl.model.hibernate.Role"
	 * @hibernate.key column="c_randomisation_id" 
	 * 						not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<IRole> getRolesToNotify() {
		return rolesToNotify;
	}

	/**
	 * @param rolesToNotify the roleToNotify to set
	 */
	public void setRolesToNotify(List<IRole> rolesToNotify) {
		this.rolesToNotify = rolesToNotify;
	}

	/**
	 * Get the List of strata applied when randomising a Subject
	 * 
	 * @return The List of IStrata.
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.esl.model.hibernate.Strata"
	 * @hibernate.key column="c_randomisation_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<IStrata> getStrata() {
		return strata;
	}

	/**
	 * @param strata the strata to set
	 */
	public void setStrata(List<IStrata> strata) {
		this.strata = strata;

	}

	/**
	 * Get the List of treatments that can be allocated during randomisation
	 * 
	 * @return The List of Treatments.
	 * 
	 * @hibernate.map cascade="all" table="t_treatments"
	 * @hibernate.key column="c_treatment_id" not-null="true"
	 * 
	 * @hibernate.map-key column="c_name"
	 *                    type="string"
	 * @hibernate.element column="c_value"
	 *                    type="string"
	 * 
	 */
	public Map<String, String> getTreatments() {
		return treatments;
	}

	/**
	 * @param treatments the treatments to set
	 */
	public void setTreatments(Map<String, String> treatments) {
		this.treatments = treatments;
	}


	/**
	 * @return the emails
	 * 
	 * @hibernate.map cascade="all" table="t_emails_map"
	 * @hibernate.key column="c_randomisation_id" not-null="true"
	 * @hibernate.map-key column="c_name"
     *              		type="string"
	 * @hibernate.composite-element class="org.psygrid.common.email.Email" 
	 */
	public Map<String,Email> getEmails() {
		return emails;
	}
	
	public Email getEmail(String type) {
		return emails.get(type);
	}
	
	
	public void setEmails(Map<String,Email> emails) throws ModelException {
		for (String key: emails.keySet()) {
			//check that the email type specified is one that we expect
			if (EmailType.getType(key) == null) {
				throw new ModelException("Email must be of type specified in EmailType");
			}
		}
		this.emails = emails;
	}
	
	public void setEmail(String type, Email email) throws ModelException {
		//check that the email type specified is one that we expect
		if (EmailType.getType(type) == null) {
			throw new ModelException("Email must be of type specified in EmailType");
		}
		emails.put(type, email);
	}

	/**
	 * Store object reference to maintain persistence
	 * 
	 * @return dto.Randomisation
	 */
	public org.psygrid.esl.model.dto.Randomisation toDTO(){
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.Randomisation dtoRand = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoRand;
	}

	public org.psygrid.esl.model.dto.Randomisation toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		//check for an already existing instance of a dto object for this 
		//class in the set of references
		org.psygrid.esl.model.dto.Randomisation dtoRand = null;
		if ( dtoRefs.containsKey(this)){
			dtoRand = (org.psygrid.esl.model.dto.Randomisation)dtoRefs.get(this);
		}
		if ( null == dtoRand ){
			//an instance of randomisation has not already
			//been created, so create it and add it to the map of references
			dtoRand = new org.psygrid.esl.model.dto.Randomisation();
			dtoRefs.put(this, dtoRand);
			toDTO(dtoRand, dtoRefs);
		}

		return dtoRand;
	}

	public void toDTO(org.psygrid.esl.model.dto.Randomisation dtoRand, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
		super.toDTO(dtoRand, dtoRefs);
		dtoRand.setName(this.name);

		if (strata != null) {
			org.psygrid.esl.model.dto.Strata[] dtoStrata = new org.psygrid.esl.model.dto.Strata[this.strata.size()];
			for (int i=0; i<this.strata.size(); i++){
				Strata s = (Strata)strata.get(i);
				if (s != null) {
					dtoStrata[i] = s.toDTO(dtoRefs);
				}
			}        
			dtoRand.setStrata(dtoStrata);            
		}

		if (rolesToNotify != null) {
			org.psygrid.esl.model.dto.Role[] dtoRole = new org.psygrid.esl.model.dto.Role[rolesToNotify.size()];
			for (int j=0; j<rolesToNotify.size(); j++) {
				Role r = (Role)rolesToNotify.get(j);
				if (r != null) {
					dtoRole[j] = r.toDTO(dtoRefs);
				}
			}
			dtoRand.setRolesToNotify(dtoRole);
		}

		if (treatments != null) {
			@SuppressWarnings("unchecked")
			org.psygrid.esl.util.Pair<String,String>[] pairMap = new org.psygrid.esl.util.Pair[treatments.size()];
			int k = 0;
			for (String key: treatments.keySet()) {
				pairMap[k] = new org.psygrid.esl.util.Pair<String,String>(key, treatments.get(key));
				k++;
			}
			dtoRand.setTreatments(pairMap);
		}

		if (emails != null) {
			@SuppressWarnings("unchecked")
			org.psygrid.esl.util.Pair<String, EmailDTO>[] dtoEmails = new org.psygrid.esl.util.Pair[emails.size()];
			int k = 0;
			for (String key: emails.keySet()) {
				EmailDTO email = null;
				if (emails.get(key) != null) {
					email = emails.get(key).toDTO();
				}			
				dtoEmails[k] = new org.psygrid.esl.util.Pair<String, EmailDTO>(key, email);
				k++;
			}

			dtoRand.setEmails(dtoEmails);
		}
	}

}
