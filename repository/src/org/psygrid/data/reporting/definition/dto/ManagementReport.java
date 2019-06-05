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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.GroupDTO;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.reporting.definition.ReportFrequency;

public class ManagementReport extends Report {
	
    /**
     * The role-based action belonging to this report, used to 
     * determine who is to be emailed this report.
     */
    private String emailAction;
    
    /**
     * The role-based action belonging to this report, used to 
     * determine who can view this report through psygrid-web.
     */
    private String viewAction;
    
    private GroupDTO[] groups;

    private ManagementChart[] charts = new ManagementChart[0];
    
    private boolean withRawData;

    private String frequency;
    
    public GroupDTO[] getGroups() {
        return groups;
    }

    public void setGroups(GroupDTO[] groups) {
        this.groups = groups;
    }

    /**
     * The role-based action belonging to this report, used to 
     * determine who is to be emailed this report.
     *
     * @return emailAction
     */
    public String getEmailAction() {
		return emailAction;
	}

    /**
     * Set the role-based action belonging to this report, used to 
     * determine who is to be emailed this report.
     * @param action
     */
	public void setEmailAction(String action) {
		this.emailAction = action;
	}
    
	/**
     * The role-based action belonging to this report, used to 
     * determine who can view this report through psygrid-web.
     *
     * @return viewAction
     */
	public String getViewAction() {
		return viewAction;
	}
	
    /**
     * Set the role-based action belonging to this report, used to 
     * determine who can view this report through psygrid-web.
     * 
     * @param action
     */
	public void setViewAction(String action) {
		this.viewAction = action;
	}
		
    public ManagementChart[] getCharts() {
        return charts;
    }

    public void setCharts(ManagementChart[] charts) {
        this.charts = charts;
    }

    public boolean isWithRawData() {
        return withRawData;
    }

    public void setWithRawData(boolean withRawData) {
        this.withRawData = withRawData;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public org.psygrid.data.reporting.definition.hibernate.ManagementReport toHibernate(){
        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
        return toHibernate(hRefs);
    }
    
    @Override
    public org.psygrid.data.reporting.definition.hibernate.ManagementReport toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object for this 
        //report in the map of references
        org.psygrid.data.reporting.definition.hibernate.ManagementReport hR = null;
        if ( hRefs.containsKey(this)){
            hR = (org.psygrid.data.reporting.definition.hibernate.ManagementReport)hRefs.get(this);
        }
        else{
            //an instance of the report has not already
            //been created, so create it, and add it to the
            //map of references
            hR = new org.psygrid.data.reporting.definition.hibernate.ManagementReport();
            hRefs.put(this, hR);
            toHibernate(hR, hRefs);
        }
        
        return hR;
    }

    public void toHibernate(org.psygrid.data.reporting.definition.hibernate.ManagementReport hP, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        super.toHibernate(hP, hRefs);
        hP.setEmailAction(emailAction);
        hP.setViewAction(viewAction);
        hP.setWithRawData(this.withRawData);
        if ( null != this.frequency ){
            hP.setFrequency(ReportFrequency.valueOf(this.frequency));
        }
        List<org.psygrid.data.model.hibernate.Group> hGroups = hP.getGroups();
        for (GroupDTO g: groups){
            if ( null != g ){
                hGroups.add(g.toHibernate(hRefs));
            }
        }
        List<org.psygrid.data.reporting.definition.hibernate.ManagementChart> hCharts = hP.getCharts();
        for (ManagementChart c: charts){
            if ( null != c ){
                hCharts.add(c.toHibernate(hRefs));
            }
        }
    }

}
