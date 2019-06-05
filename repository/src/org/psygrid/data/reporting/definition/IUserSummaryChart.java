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

/**
 * Interface to represent a chart in a management report that displays
 * the number of records with each status by the user who first
 * created the record, for the groups defined in the parent report.
 * <p>
 * If no groups are defined in the parent report then the chart
 * is generated for the whole project i.e. all groups.
 * 
 * @author Rob Harper
 *
 */
public interface IUserSummaryChart extends IManagementChart {

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

}
