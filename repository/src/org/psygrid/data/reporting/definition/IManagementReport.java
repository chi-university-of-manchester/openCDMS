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

package org.psygrid.data.reporting.definition;

import org.psygrid.data.model.hibernate.Group;
import org.psygrid.security.RBACAction;

public interface IManagementReport extends IReport {
    
    /**
     * Get the name of the role-based action belonging to this 
     * report, used to determine who is to be emailed it.
     * 
     * @return emailAction
     */
    public String getEmailAction();
    
    /**
     * Set the role-based action belonging to this report, used to
     * determine who is to be emailed the report.
     * 
     * @param action
     */
    public void setEmailAction(RBACAction action);
    
    /**
     * Get the name of the role-based action belonging to this 
     * report, used to determine who can view this report through
     * psygrid-web.
     * 
     * @return viewAction
     */
    public String getViewAction();
    
    /**
     * Set the role-based action belonging to this report, used to
     * determine who can view this report through psygrid-web.
     * 
     * @param action
     */
    public void setViewAction(RBACAction action);
    
    /**
     * Get the number of groups in the dataset that this report is associated
     * with, and that will be featured in the report.
     * <p>
     * If the number of groups is zero then we assume that we report on
     * all groups in the project.
     * 
     * @return The number of groups.
     */
    public int numGroups();
    
    /**
     * Add a group to the collection of groups in the dataset that this report 
     * is associated with, and that will be featured in the report.
     * 
     * @param group The group to add.
     * @throws ReportException if the group being added is <code>null</code>.
     */
    public void addGroup(Group group) throws ReportException;
    
    /**
     * Retrieve a single group from the collection of groups in the dataset 
     * that this report is associated with, and that will be featured in the 
     * report.
     * 
     * @param index The index of the group to retrieve.
     * @return The group at the given index.
     * @throws ReportException if no group exists for the given index.
     */
    public Group getGroup(int index) throws ReportException;

    /**
     * Get the number of charts contained by this report.
     * 
     * @return The number of charts.
     */
    public int numCharts();
    
    /**
     * Add a chart to the report.
     * 
     * @param chart The chart to add.
     * @throws ReportException if the chart being added is <code>null</code>.
     */
    public void addChart(IManagementChart chart) throws ReportException;
    public void addManagementChart(IManagementChart chart) throws ReportException;
    
    /**
     * Get a chart contained by the report, at the specified index.
     * 
     * @param index The index.
     * @return The chart at the given index.
     * @throws ReportException if no chart exists for the given index.
     */
    public IManagementChart getManagementChart(int index) throws ReportException;
    public IManagementChart getChart(int index) throws ReportException;

    /**
     * Get the value of the flag to indicate whether raw data should accompany
     * a graphical report created from this definition.
     *
     * @return The "with raw data" flag.
     */
    public boolean isWithRawData();
    
    /**
     * Set the value of the flag to indicate whether raw data should accompany
     * a graphical report created from this definition.
     *
     * @param withRawData The "with raw data" flag.
     */
    public void setWithRawData(boolean withRawData);
    
    /**
     * Get the frequency with which the report will be delivered.
     * 
     * If not set, then weekly delivery is assumed. To disable,
     * set to 'NEVER'.
     * 
     * @return The frequency.
     */
    public ReportFrequency getFrequency();

    /**
     * Set the frequency with which the report will be delivered.
     * 
     * If not set, then weekly delivery is assumed. To disable,
     * set to 'NEVER'.
     * 
     * @param frequency The frequency.
     */
    public void setFrequency(ReportFrequency frequency);

    public org.psygrid.data.reporting.definition.dto.ManagementReport toDTO();
}
