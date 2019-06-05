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



package org.psygrid.security.policyauthority.model;

import org.psygrid.security.policyauthority.model.hibernate.Privilege;

/**
 * @author jda
 *
 */
public interface IArgument extends IPersistent {
	/**
	 * @return Returns the assertion.
	 */
	public boolean isAssertion();

	/**
	 * @param assertion The assertion to set.
	 */
	public void setAssertion(boolean assertion);

	/**
	 * @return Returns the privilege.
 	 */
	public Privilege getPrivilege();

	/**
	 * @param privilege The privilege to set.
	 */
	public void setPrivilege(Privilege privilege);
}
