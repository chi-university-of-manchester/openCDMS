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

import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.GroupDTO;
import org.psygrid.data.model.dto.PersistentDTO;

/**
 * @author Rob Harper
 *
 */
public class BasicStatisticsChart extends ManagementChart {

	private GroupDTO[] groups = new GroupDTO[0];

	private Long[] entryIds = new Long[0];
	
	private String[] statistics = new String[0];
	
	public Long[] getEntryIds() {
		return entryIds;
	}

	public void setEntryIds(Long[] entryIds) {
		this.entryIds = entryIds;
	}

	public GroupDTO[] getGroups() {
		return groups;
	}

	public void setGroups(GroupDTO[] groups) {
		this.groups = groups;
	}

	public String[] getStatistics() {
		return statistics;
	}

	public void setStatistics(String[] statistics) {
		this.statistics = statistics;
	}

	@Override
	public org.psygrid.data.reporting.definition.hibernate.BasicStatisticsChart toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		//check for an already existing instance of a hibernate object for this 
		//project summary chart in the map of references
		org.psygrid.data.reporting.definition.hibernate.BasicStatisticsChart hBSC = null;
		if ( hRefs.containsKey(this)){
			hBSC = (org.psygrid.data.reporting.definition.hibernate.BasicStatisticsChart)hRefs.get(this);
		}
		else{
			//an instance of the chart has not already
			//been created, so create it, and add it to the
			//map of references
			hBSC = new org.psygrid.data.reporting.definition.hibernate.BasicStatisticsChart();
			hRefs.put(this, hBSC);
			toHibernate(hBSC, hRefs);
		}

		return hBSC;
	}

	public void toHibernate(org.psygrid.data.reporting.definition.hibernate.BasicStatisticsChart hC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		super.toHibernate(hC, hRefs);

        List<org.psygrid.data.model.hibernate.Group> hGroups = hC.getGroups();
        for (GroupDTO g: groups){
            if ( null != g ){
                hGroups.add(g.toHibernate(hRefs));
            }
        }

        for ( Long eid: this.entryIds){
			hC.getEntryIds().add(eid);
		}
		
		for ( String stat: this.statistics){
			hC.getStatistics().add(stat);
		}
		
	}

}
