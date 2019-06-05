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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.definition.IManagementChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.reporting.definition.ReportFrequency;
import org.psygrid.security.RBACAction;

/**
 * Class to represent the definition of a management report.
 * <p>
 * A management report is one that reports overall statistics
 * about a project.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_mgmt_reports"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class ManagementReport extends Report implements IManagementReport {

    
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
    
    /**
     * The groups in the dataset that this report is associated
     * with, and that will be featured in the report.
     * <p>
     * If this list is empty then we assume that we report on
     * all groups in the project.
     */
    private List<Group> groups = new ArrayList<Group>();

    /**
     * The charts that are featured in the report.
     */
    private List<ManagementChart> charts = new ArrayList<ManagementChart>();
    
    /**
     * Flag to indicate whether raw data should accompany
     * a graphical report created from this definition.
     */
    private boolean withRawData;
    
    /**
     * The frequency with which the report will be delivered.
     */
    private ReportFrequency frequency;
    
    public ManagementReport() {
    }

    public ManagementReport(DataSet ds, String title) {
        super(ds, title);
    }

    /**
     * Get the groups in the dataset that this report is associated
     * with, and that will be featured in the report.
     * <p>
     * If this list is empty then we assume that we report on
     * all groups in the project.
     * 
     * @return The groups.
     * 
     * @hibernate.list cascade="none" 
     *                 table="t_mgmtrep_groups"
     * @hibernate.key column="c_report_id"
     * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.Group"
     *                         column="c_group_id"
     * @hibernate.list-index column="c_index"
     */
    public List<Group> getGroups() {
        return groups;
    }

    /**
     * Set the groups in the dataset that this report is associated
     * with, and that will be featured in the report.
     * <p>
     * If this list is empty then we assume that we report on
     * all groups in the project.
     * 
     * @param groups The groups.
     */
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public int numGroups() {
        return groups.size();
    }

    public void addGroup(Group group) throws ReportException {
        if (null == group){
            throw new ReportException("Cannot add a null group");
        }
        this.groups.add((Group)group);
    }

    public Group getGroup(int index) throws ReportException {
        try{
            return this.groups.get(index);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ReportException("No group exists for index="+index, ex);
        }
    }
    
    /**
     * @hibernate.property column="c_action"
     */
    public String getEmailAction() {
		return emailAction;
	}

	public void setEmailAction(String action) {
		this.emailAction = action;
	}

	public void setEmailAction(RBACAction action) {
		if (action == null) {
			this.emailAction = null;
		}
		else {
			this.emailAction = action.name();
		}
	}
	
	
	/**
     * @hibernate.property column="c_view_action"
     */
    public String getViewAction() {
		return viewAction;
	}

	public void setViewAction(String action) {
		this.viewAction = action;
	}
	
	public void setViewAction(RBACAction action) {
		if (action == null) {
			this.viewAction = null;
		}
		else {
			this.viewAction = action.name();
		}
	}

	
    /**
     * Get the charts that are featured in the report.
     * 
     * @return The charts.
     * 
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.reporting.definition.hibernate.ManagementChart"
     * @hibernate.key column="c_report_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<ManagementChart> getCharts() {
        return charts;
    }

    /**
     * Set the charts that are featured in the report.
     * 
     * @param charts The charts.
     */
    public void setCharts(List<ManagementChart> charts) {
        this.charts = charts;
    }

    public void addChart(IManagementChart chart) throws ReportException {
    	addManagementChart(chart);
    }
    
    public void addManagementChart(IManagementChart chart) throws ReportException {
        if (null == chart){
            throw new ReportException("Cannot add a null chart");
        }
        ManagementChart c = (ManagementChart)chart;
        this.charts.add(c);
        c.setReport(this);
    }

    public IManagementChart getChart(int index) throws ReportException {
    	return getManagementChart(index);
    }
    public IManagementChart getManagementChart(int index) throws ReportException {
        try{
            return this.charts.get(index);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ReportException("No chart exists for index="+index, ex);
        }
    }

    public int numCharts() {
        return this.charts.size();
    }

    /**
     * @hibernate.property column="c_with_raw_data"
     */
    public boolean isWithRawData() {
        return withRawData;
    }

    public void setWithRawData(boolean withRawData) {
        this.withRawData = withRawData;
    }

    public ReportFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(ReportFrequency frequency) {
        this.frequency = frequency;
    }

    /**
     * Get the string value of the enumerated frequency.
     * <p>
     * Only used by Hibernate to persist the string value of the enumerated
     * frequency.
     * 
     * @return The string value of the enumerated frequency
     * 
     * @hibernate.property column="c_frequency"
     */
    protected String getEnumFrequency() {
        if ( null == frequency ){
            return null;
        }
        else{
            return frequency.toString();
        }
    }

    /**
     * Set the string value of the enumerated frequency.
     * <p>
     * Only used by Hibernate to un-persist the string value of the enumerated
     * frequency.
     * 
     * @param enumFrequency The string value of the enumerated frequency.
     */
    protected void setEnumFrequency(String enumFrequency) {
        if ( null == enumFrequency ){
            frequency = null;
        }
        else{
            frequency = ReportFrequency.valueOf(enumFrequency);
        }
    }
    
	public org.psygrid.data.reporting.definition.dto.ManagementReport toDTO(){
        return toDTO(RetrieveDepth.REP_SAVE);
    }
    
    public org.psygrid.data.reporting.definition.dto.ManagementReport toDTO(RetrieveDepth depth){
        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        return toDTO(dtoRefs, depth);
    }
    
    
    @Override
    public org.psygrid.data.reporting.definition.dto.ManagementReport toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //report in the map of references
        org.psygrid.data.reporting.definition.dto.ManagementReport dtoR = null;
        if ( dtoRefs.containsKey(this)){
            dtoR = (org.psygrid.data.reporting.definition.dto.ManagementReport)dtoRefs.get(this);
        }
        if ( null == dtoR ){
            //an instance of the report has not already
            //been created, so create it, and add it to the
            //map of references
            dtoR = new org.psygrid.data.reporting.definition.dto.ManagementReport();
            dtoRefs.put(this, dtoR);
            toDTO(dtoR, dtoRefs, depth);
        }
        
        return dtoR;
    }

    public void toDTO(org.psygrid.data.reporting.definition.dto.ManagementReport dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        super.toDTO(dtoR, dtoRefs, depth);
        dtoR.setEmailAction(emailAction);
        dtoR.setViewAction(viewAction);
        dtoR.setWithRawData(this.withRawData);
        if ( null != this.frequency ){
            dtoR.setFrequency(this.frequency.toString());
        }
        
        org.psygrid.data.model.dto.GroupDTO[] dtoGroups = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
        for (int i=0; i<this.groups.size(); i++){
            Group g = groups.get(i);
            dtoGroups[i] = g.toDTO(dtoRefs, depth);
        }        
        dtoR.setGroups(dtoGroups);
        org.psygrid.data.reporting.definition.dto.ManagementChart[] dtoCharts = 
            new org.psygrid.data.reporting.definition.dto.ManagementChart[this.charts.size()];
        for (int i=0; i<this.charts.size(); i++){
            ManagementChart c = charts.get(i);
            dtoCharts[i] = c.toDTO(dtoRefs, depth);
        }        
        dtoR.setCharts(dtoCharts);
        
    }
    
}
