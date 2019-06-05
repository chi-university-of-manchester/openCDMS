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

package org.psygrid.data.reporting.definition.dto;

import java.util.Map;

import org.psygrid.data.model.dto.PersistentDTO;


public class TrendsGanttChart extends TrendsChart {
	
	@Override
	public org.psygrid.data.reporting.definition.hibernate.TrendsGanttChart toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		//check for an already existing instance of a hibernate object for this 
		//trends chart in the map of references
		org.psygrid.data.reporting.definition.hibernate.TrendsGanttChart hTrends = null;
		if ( hRefs.containsKey(this)){
			hTrends = (org.psygrid.data.reporting.definition.hibernate.TrendsGanttChart)hRefs.get(this);
		}
		else{
			//an instance of the trends chart has not already
			//been created, so create it, and add it to the
			//map of references
			hTrends = new org.psygrid.data.reporting.definition.hibernate.TrendsGanttChart();
			hRefs.put(this, hTrends);
			toHibernate(hTrends, hRefs);
		}

		return hTrends;
	}
	
	public void toHibernate(org.psygrid.data.reporting.definition.hibernate.TrendsGanttChart hC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		super.toHibernate(hC, hRefs);
	}
}
