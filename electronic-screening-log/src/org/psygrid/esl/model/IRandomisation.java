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

package org.psygrid.esl.model;

import java.util.List;
import java.util.Map;

import org.psygrid.common.email.Email;


/**
 * Interface to represent the details of the Randomisation
 * process performed on the project.
 * 
 * @author Lucy Bridges
 *
 */
public interface IRandomisation extends IPersistent {
	
	/**
	 * Set the name given to the randomisation to be performed.
	 * 
	 * @param name e.g normal/strata
	 */
	public void setName(String name);
	
	/**
	 * Get the name given to the randomisation process.
	 * 
	 * @return String
	 */
	public String getName();
	
	/**
	 * Set the strata to be used during the randomisation process
	 * 
	 * @param strata
	 */
	public void setStrata(List<IStrata> strata);
	
	/**
	 * Get the strata that are to be applied during randomisation
	 * 
	 * @return A List<Strata> of strata
	 */
	public List<IStrata> getStrata();

	/**
	 * Get the Roles to be notified during randomisation
	 * 
	 * @return The roles to be notified
	 */
	public List<IRole> getRolesToNotify();
	
	/**
	 * Set the Roles to be notified during randomisation.
	 * 
	 * @param rolesToNotify The roles to be notified
	 */
	public void setRolesToNotify(List<IRole> rolesToNotify);
	
	
	/**
	 * Get the treatments to be allocated during randomisation
	 * @return allocated treatments
	 */
	public Map<String, String> getTreatments();

	/**
	 * Set the treatments that can be allocated during randomisation
	 * @param treatments the treatments to set
	 */
	public void setTreatments(Map<String, String> treatments);
	

	/**
	 * Get the Emails to be sent out to various Roles at different points during 
	 * the randomisation process
	 * 
	 * @return emails
	 */
	public Map<String, Email> getEmails();
	
	/**
	 * Get an Email of a given type that is to be sent out to various Roles during  
	 * the randomisation process
	 * 
	 * @return email
	 */
	public Email getEmail(String type);
	
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
	 * @throws ModelException if the email type provided is not one that
	 * is specified in org.psygrid.esl.randomisation.EmailType
	 */
	public void setEmails(Map<String, Email> emails) throws ModelException;
	
	/**
	 * Set an email that is to be sent out during the randomisation
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
	 * @param email
	 * @throws ModelException if the email type provided is not one that
	 * is specified in org.psygrid.esl.randomisation.EmailType
	 */
	public void setEmail(String type, Email email) throws ModelException;
	
	
	public org.psygrid.esl.model.dto.Randomisation toDTO();
	public org.psygrid.esl.model.dto.Randomisation toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs);
}
