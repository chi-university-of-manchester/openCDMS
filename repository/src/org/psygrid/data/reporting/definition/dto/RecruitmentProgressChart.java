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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.psygrid.data.model.dto.GroupDTO;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.reporting.definition.Pair;


public class RecruitmentProgressChart extends ManagementChart {

	private Calendar startDate = new GregorianCalendar(0, 0, 0);
	private Calendar endDate   = new GregorianCalendar(0, 0, 0);

	private Pair<Calendar, Integer>[] targets = null; 

	private GroupDTO[] groups = new GroupDTO[0];

	public GroupDTO[] getGroups() {
		return groups;
	}

	public void setGroups(GroupDTO[] groups) {
		this.groups = groups;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Calendar endDate) {
		if ( endDate == null ){
			this.endDate = new GregorianCalendar(0, 0, 0);
		}
		else {
			endDate.clear(Calendar.MILLISECOND);
			endDate.clear(Calendar.SECOND);
			endDate.clear(Calendar.MINUTE);
			endDate.clear(Calendar.HOUR_OF_DAY);
			endDate.clear(Calendar.DATE);
			endDate.clear(Calendar.DAY_OF_MONTH);
			endDate.setTimeZone(TimeZone.getTimeZone("GMT"));
			this.endDate = endDate;
		}
	}

	public Calendar getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Calendar startDate) {
		if ( startDate == null ){
			this.startDate = new GregorianCalendar(0, 0, 0);
		}
		else {
			startDate.clear(Calendar.MILLISECOND);
			startDate.clear(Calendar.SECOND);
			startDate.clear(Calendar.MINUTE);
			startDate.clear(Calendar.HOUR_OF_DAY);
			startDate.clear(Calendar.DATE);
			startDate.clear(Calendar.DAY_OF_MONTH);
			startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
			this.startDate = startDate;
		}
	}

	/**
	 * @return the targets
	 */
	public Pair<Calendar, Integer>[] getTargets() {
		return targets;
	}

	/**
	 * @param targets the targets to set
	 */
	public void setTargets(Pair<Calendar, Integer>[] targets) {
		this.targets = targets;
	}

	@Override
	public org.psygrid.data.reporting.definition.hibernate.RecruitmentProgressChart toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		//check for an already existing instance of a hibernate object for this 
		//project summary chart in the map of references
		org.psygrid.data.reporting.definition.hibernate.RecruitmentProgressChart hPSC = null;
		if ( hRefs.containsKey(this)){
			hPSC = (org.psygrid.data.reporting.definition.hibernate.RecruitmentProgressChart)hRefs.get(this);
		}
		else{
			//an instance of the chart has not already
			//been created, so create it, and add it to the
			//map of references
			hPSC = new org.psygrid.data.reporting.definition.hibernate.RecruitmentProgressChart();
			hRefs.put(this, hPSC);
			toHibernate(hPSC, hRefs);
		}

		return hPSC;
	}

	public void toHibernate(org.psygrid.data.reporting.definition.hibernate.RecruitmentProgressChart hC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		super.toHibernate(hC, hRefs);
		hC.setEndDate(endDate);
		hC.setStartDate(startDate);

		if (targets != null) {
			for (int i = 0; i < targets.length; i++) {
				if (targets[i].getName() != null) {
					hC.addTarget(targets[i].getName(), targets[i].getValue());
				}
			}
		}

		List<Group> hGroups = hC.getGroups();
		for (GroupDTO g: groups){
			if ( null != g ){
				hGroups.add(g.toHibernate(hRefs));
			}
		}
	}

}
