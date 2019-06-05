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

package org.psygrid.collection.entry.replication;

/**
 * Class to contain the results of a pre-linking check for
 * conflicts between two records being linked.
 * 
 * @author Rob Harper
 *
 */
public class DdeCheckResult{
	
	/**
	 * If True then one or more document instances exist in the secondary
	 * Record for which there is no equivalent document instance in the
	 * primary Record.
	 */
	private final boolean noPrimaryYesSeconday;
	
	/**
	 * If True then one or more document instances exist in the secondary
	 * Record which have equivalent - conflicting - document instances in 
	 * the primary Record.
	 */
	private final boolean yesPrimaryYesSecondary;

	/**
	 * If True then then one or more incomplete document instances exist 
	 * in the secondary Record for which there is no equivalent document 
	 * instance in the primary Record.
	 */
	private final boolean noPrimaryYesIncompSecondary;
	
	/**
	 * If True then one or more document instances exist in the secondary
	 * Record which have equivalent incomplete - conflicting - document
	 * instances in the primary record.
	 */
	private final boolean yesIncompPrimYesSecondary;

	public DdeCheckResult(
			boolean noPrimaryYesSeconday, boolean yesPrimaryYesSecondary, 
			boolean noPrimaryYesIncompSecondary, boolean yesIncompPrimYesSecondary){
		this.noPrimaryYesSeconday = noPrimaryYesSeconday;
		this.yesPrimaryYesSecondary = yesPrimaryYesSecondary;
		this.noPrimaryYesIncompSecondary = noPrimaryYesIncompSecondary;
		this.yesIncompPrimYesSecondary = yesIncompPrimYesSecondary;
	}
	
	public boolean isNoPrimaryYesSeconday() {
		return noPrimaryYesSeconday;
	}

	public boolean isYesPrimaryYesSecondary() {
		return yesPrimaryYesSecondary;
	}

	public boolean isNoPrimaryYesIncompSecondary() {
		return noPrimaryYesIncompSecondary;
	}

	public boolean isYesIncompPrimYesSecondary() {
		return yesIncompPrimYesSecondary;
	}
	
}
