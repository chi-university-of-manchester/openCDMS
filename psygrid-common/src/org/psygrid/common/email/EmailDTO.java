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

package org.psygrid.common.email;

import java.util.Map;
import java.util.HashMap;

/**
 * An email used to inform particular roles that a step in the 
 * randomisation process has taken place.
 * 
 * @author Lucy Bridges
 *
 */
public class EmailDTO {

	/**
	 * Unique identifier, generated by the Hibernate framework
	 */
	protected Long id;
	
	protected String subject = null;
	protected String body    = null;
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	

	public org.psygrid.common.email.Email toHibernate(){
	     //check for an already existing instance of a hibernate object for this 
        //record in the map of references
		org.psygrid.common.email.Email hE = new Email();

		hE.setId(id);
		hE.setBody(body);
		hE.setSubject(subject); 

        return hE;
	}

}
