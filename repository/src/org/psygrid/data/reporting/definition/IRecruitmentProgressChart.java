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

import java.util.Calendar;
import java.util.Map;

import org.psygrid.data.model.hibernate.Group;

/**
 * A management chart comparing the number of subjects consented
 * into the trial against the targets set for each month, giving
 * a view of the trial's progress.
 * 
 * @author Lucy Bridges
 *
 */
public interface IRecruitmentProgressChart extends IManagementChart {
	
	/**
	 * Set the period of time the chart is to cover.
	 * 
	 * Note: fields other than month and year are ignored
	 * 
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public void setTimePeriod(Calendar startDate, Calendar endDate);
	
	/**
	 * The date the chart starts at
	 * 
	 * @return endDate
	 */
	public Calendar getStartDate();
	
	/**
	 * The date the chart ends at
	 * 
	 * @return endDate
	 */
	public Calendar getEndDate();
	
	/**
	 * Get a list of the monthly targets for recruitment
	 * 
	 * @return targets
	 */
	public Map<Calendar, Integer> getTargets();
	
	/**
	 * Add a monthly recruitment target to the list.
	 * 
	 * Note: dates should be created as follows for
	 * accurate rendering:
	 * 
	 * Calendar targetDate = new GregorianCalendar(myYear, myMonth, 0);
	 * 
	 * @param month
	 * @param target
	 */
	public void addTarget(Calendar month, Integer target);
	
	/**
	 * Set the target figures for recruitment for each month,
	 * as a map of Calendar -> Integer
	 * 
	 * Note: the dates should be created as follows for
	 * accurate rendering:
	 * 
	 * Calendar startDate = new GregorianCalendar(myYear, myMonth, 0);
	 * 
	 * @param targets
	 */
	public void setTargets(Map<Calendar, Integer> targets);
	
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

	
}
