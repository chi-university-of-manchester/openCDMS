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

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.GroupDTO;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.model.hibernate.Group;

/**
 * Class to represent a chart in a management report that displays
 * a list of study participants receiving treatment.
 * 
 * See bug 662 for report requirements.
 * 
 * @author Lucy Bridges
 * 
 */
public class ReceivingTreatmentChart extends ManagementChart {

	/**
	 * The period of time the chart is to cover
	 */
	private Calendar startDate = Calendar.getInstance();
	private Calendar endDate   = Calendar.getInstance();

	private GroupDTO[] groups = new GroupDTO[0];
    
    public GroupDTO[] getGroups() {
        return groups;
    }

    public void setGroups(GroupDTO[] groups) {
        this.groups = groups;
    }
	
	/**
	 * Get the chart's end date
	 * 
	 * @return the endDate
	 */
	public Calendar getEndDate() {
		return endDate;
	}

	/**
	 * Set the chart's end date
	 * 
	 * Ignores fields other than month and year
	 * 
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Calendar endDate) {
		endDate.clear(Calendar.MILLISECOND);
		endDate.clear(Calendar.SECOND);
		endDate.clear(Calendar.MINUTE);
		endDate.clear(Calendar.HOUR_OF_DAY);
		endDate.clear(Calendar.DATE);
		endDate.clear(Calendar.DAY_OF_MONTH);
		this.endDate = endDate;
	}

	/**
	 * Get the chart's start date
	 * 
	 * @return the startDate
	 */
	public Calendar getStartDate() {
		return startDate;
	}

	/**
	 * Set the start date for the chart
	 * 
	 * Fields other than month and year are ignored
	 * 
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Calendar startDate) {
		startDate.clear(Calendar.MILLISECOND);
		startDate.clear(Calendar.SECOND);
		startDate.clear(Calendar.MINUTE);
		startDate.clear(Calendar.HOUR_OF_DAY);
		startDate.clear(Calendar.DATE);
		startDate.clear(Calendar.DAY_OF_MONTH);
		this.startDate = startDate;
	}


	@Override
	public org.psygrid.data.reporting.definition.hibernate.ReceivingTreatmentChart toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		//check for an already existing instance of a hibernate object for this 
		//UKCRN summary chart in the map of references
		org.psygrid.data.reporting.definition.hibernate.ReceivingTreatmentChart hC = null;
		if ( hRefs.containsKey(this)){
			hC = (org.psygrid.data.reporting.definition.hibernate.ReceivingTreatmentChart)hRefs.get(this);
		}
		else{
			//an instance of the UKCRN summary chart has not already
			//been created, so create it, and add it to the
			//map of references
			hC = new org.psygrid.data.reporting.definition.hibernate.ReceivingTreatmentChart();
			hRefs.put(this, hC);
			toHibernate(hC, hRefs);
		}

		return hC;
	}

	public void toHibernate(org.psygrid.data.reporting.definition.hibernate.ReceivingTreatmentChart hC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		super.toHibernate(hC, hRefs);
		hC.setTimePeriod(startDate, endDate);
		List<Group> hGroups = hC.getGroups();
        for (GroupDTO g: groups){
            if ( null != g ){
                hGroups.add(g.toHibernate(hRefs));
            }
        }
	}


}
