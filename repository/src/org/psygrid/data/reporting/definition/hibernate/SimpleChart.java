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

package org.psygrid.data.reporting.definition.hibernate;

import java.util.List;

import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.reporting.definition.ISimpleChart;

/**
 * A simple chart contains the information needed to create a chart
 * for a document. 
 * 
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_simple_charts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class SimpleChart extends Chart implements ISimpleChart {
	
	public SimpleChart(){}

	public SimpleChart(String type, String title){
		super(type, title);
	}

	protected void setAllowedStates() {
		allowedStates.add(GenericState.ACTIVE.toString());
		allowedStates.add(GenericState.COMPLETED.toString());
		allowedStates.add(GenericState.REFERRED.toString());
		allowedStates.add(GenericState.LEFT.toString());
	}
	
	/**
	 * Records with a status of invalid and inactive are not included 
	 * in this chart.
	 * 
	 * @param recordStatus
	 * @return eligability
	 */
	protected boolean recordEligible(String recordStatus) {

		List<String> allowedStates = this.getAllowedStates();

		if (recordStatus != null && allowedStates.contains(recordStatus)) {
			return true;
		}
		return false;
	}
}
