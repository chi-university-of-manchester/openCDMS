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

public class ProjectSummaryChart extends ManagementChart {

	/**
	 * Show only the total number of people in project.
	 */
	private boolean showTotal = false;
	
		
    public boolean isShowTotal() {
		return showTotal;
	}

	public void setShowTotal(boolean showTotal) {
		this.showTotal = showTotal;
	}

	@Override
    public org.psygrid.data.reporting.definition.hibernate.ProjectSummaryChart toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object for this 
        //project summary chart in the map of references
        org.psygrid.data.reporting.definition.hibernate.ProjectSummaryChart hPSC = null;
        if ( hRefs.containsKey(this)){
            hPSC = (org.psygrid.data.reporting.definition.hibernate.ProjectSummaryChart)hRefs.get(this);
        }
        else{
            //an instance of the project summary chart has not already
            //been created, so create it, and add it to the
            //map of references
            hPSC = new org.psygrid.data.reporting.definition.hibernate.ProjectSummaryChart();
            hRefs.put(this, hPSC);
            toHibernate(hPSC, hRefs);
        }
        
        return hPSC;
    }

    public void toHibernate(org.psygrid.data.reporting.definition.hibernate.ProjectSummaryChart hC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        super.toHibernate(hC, hRefs);
        hC.setShowTotal(showTotal);
    }

}
