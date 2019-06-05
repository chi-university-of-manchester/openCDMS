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

package org.psygrid.data.model.hibernate;

/**
 * A GenericState defines common record states, used to given 
 * commonality across States for Records in different datasets.
 *  
 * @author Lucy Bridges
 *
 */
public enum GenericState {

	/**
	 * In the study but not consented.
	 * 
	 * Records are incomplete, inaccessible, open.
	 */
	REFERRED,

	/**
	 * Patients consented into the study. This includes most records.
	 * 
	 * Records are incomplete, accessible, open.
	 */
	ACTIVE,

	/**
	 * Patients who were in the study, but have later withdrawn without
	 * withdrawing consent.
	 * 
	 * Records are incomplete, accessible, closed.
	 */
	LEFT,

	/**
	 * Patients consented into the study and where all assessments have been
	 * completed.
	 * 
	 * Records are complete, accessible, closed.
	 */
	COMPLETED,

	/**
	 * Patients consented into the study but later withdrew all consent or who have never consented.
	 * 
	 * Records are inaccessible, closed.
	 */
	INACTIVE,
	
	/**
	 * Patient record created by mistake, typically as a duplicate record.
	 * 
	 * This record is inactive and should not be included in the study in anyway.
	 */
	INVALID

}
