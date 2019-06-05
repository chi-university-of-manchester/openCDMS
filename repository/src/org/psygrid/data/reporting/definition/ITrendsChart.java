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
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;

/**
 * Interface to represent a trends chart.
 * <p>
 * A trends chart displays a summary of data from 
 * across a dataset (from documents in one or more
 * records) allowing trends to be highlighted.
 * <p>
 * It will provide just one dataset but allows multiple 
 * series. For example, a table with just one value 
 * column, a multiple pie charts or a bar chart with 
 * several sets of bars, but not a bar chart overlayed 
 * with a line graph.
 * 
 * @author Lucy Bridges
 */
public interface ITrendsChart extends ISimpleChart {
	
	
	/**
	 * Add an item to be featured in the chart.
	 * 
	 * @param entry The item.
	 * @throws ReportException if the item is <code>null</code>.
	 */
	public void addRow(ITrendsChartRow row) throws ReportException;

	/**
	 * Get an item featured in the chart.
	 * 
	 * @param index The index of the item.
	 * @return The item.
	 * @throws ReportException if no item exists for the given index.
	 */
	public ITrendsChartRow getRow(int index) throws ReportException;
	
	/**
	 * Get the end date of the chart
	 * 
	 * @return endDate
	 */
	public Calendar getEndDate();
	
	/**
	 * Get the start date of the chart
	 * 
	 * @return startDate
	 */
	public Calendar getStartDate();
	
	/**
	 * Set the time period to be covered by the chart, based
	 * on a record's creation date.
	 * 
	 * Note: Fields other than month and year will be ignored
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public void setTimePeriod(Calendar startDate, Calendar endDate);
	
	public int numGroups();
	public void addGroup(Group group) throws ReportException;
	public Group getGroup(int index) throws ReportException;
	public List<Group> getGroups() throws ReportException;
	
	public boolean isShowTotals();
	public void setShowTotals(boolean showTotals); 
	
	public void setReport(ITrendsReport report);
	public ITrendsReport getReport();
	
	public org.psygrid.data.reporting.Chart generateChart(Session session, Long datasetId) throws ReportException;
	public org.psygrid.data.reporting.definition.dto.TrendsChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);
}
