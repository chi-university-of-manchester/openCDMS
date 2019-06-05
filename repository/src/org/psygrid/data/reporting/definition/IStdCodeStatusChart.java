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

import java.util.List;

import org.psygrid.data.model.hibernate.Group;

/**
 * A management chart showing the percentage of standard codes
 * used broken down by field for the given document occurrences 
 * and sites in a project (identified by record).
 *  
 * This provides an overview of data quality in a project (too high
 * a percentage of std codes in use indicates that assessments are
 * not being filled in!)
 * 
 * The chart can be displayed by group, or whole project if no 
 * group is specified.
 * 
 * @author Lucy Bridges
 */
public interface IStdCodeStatusChart extends IManagementChart {

	/**
     * Add a group to be featured in the chart.
     * <p>
     * This collection should be a subset of the groups associated
     * with the parent report (unless the parent report has no groups 
     * defined, which we take to mean all groups).
     */
	public void addGroup(Group group) throws ReportException;
	
	/**
     * Get a group that will be featured in the chart.
     * <p>
     * This collection should be a subset of the groups associated
     * with the parent report (unless the parent report has no groups 
     * defined, which we take to mean all groups).
     */
	public Group getGroup(int index) throws ReportException;
	
	/**
	 * Get the number of groups featured in the chart.
	 * 
	 * @return numGroups
	 * @throws ReportException
	 */
	public int numGroups() throws ReportException;
	
	/**
	 * Get the document occurrences that will be featured in this 
	 * chart
	 * 
	 * @return docOccs
	 */
	public List<Long> getDocOccs();
	
	/**
	 * Add a document occurrence to be included in this chart
	 * 
	 * @param docOcc
	 */
	public void addDocOcc(Long docOcc);
	
	/**
	 * Specify that the chart should summarise the percentage
	 * occurrences of each value per document (default is per record)
	 * 
	 * @return perDocument
	 */
	public boolean isPerDocument();
	
	/**
	 * Specify that the chart should summarise the percentage
	 * occurrences of each value per entry (default is per record)
	 * 
	 * @return perEntry
	 */
	public boolean isPerEntry();
	
	/**
	 * Specify that the chart should summarise the percentage
	 * occurrences of each value per document (default is per record)
	 * 
	 * @param perDocument
	 */
	public void setPerDocument(boolean perDocument);
	
	/**
	 * Specify that the chart should summarise the percentage
	 * occurrences of each value per entry (default is per record)
	 * 
	 * @param perEntry
	 */
	public void setPerEntry(boolean perEntry);

}
