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
import org.psygrid.data.reporting.definition.ITrendsChartRow;

/**
 * Class to represent a "trends" chart.
 * <p>
 * A trends chart provides a summary of data 
 * from all documents of a particular type that 
 * have been entered into a data set.
 * 
 * @author Lucy Bridges
 */
public class TrendsChart extends SimpleChart {


	private TrendsChartRow[] rows = new TrendsChartRow[0];
	
	/**
	 * The period of time the chart is to cover
	 */
	private Calendar startDate = Calendar.getInstance();
	private Calendar endDate   = Calendar.getInstance();

	private GroupDTO[] groups = new GroupDTO[0];

	private TrendsReport report = null; 

	private boolean showTotals = false;


	public TrendsChartRow[] getRows() {
		return rows;
	}

	public void setRows(TrendsChartRow[] rows) {
		this.rows = rows;
	}

	public int numRows() {
		return this.rows.length;
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
	 * Ignores any field less than month
	 * 
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Calendar endDate) {
		endDate.clear(Calendar.MILLISECOND);
		endDate.clear(Calendar.SECOND);
		endDate.clear(Calendar.MINUTE);
		endDate.clear(Calendar.HOUR_OF_DAY);
		endDate.clear(Calendar.DATE);
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
	 * Ignores any field less than month
	 * 
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Calendar startDate) {
		startDate.clear(Calendar.MILLISECOND);
		startDate.clear(Calendar.SECOND);
		startDate.clear(Calendar.MINUTE);
		startDate.clear(Calendar.HOUR_OF_DAY);
		startDate.clear(Calendar.DATE);
		this.startDate = startDate;
	}

	/**
	 * Get the report this chart belongs to
	 * @return the report
	 * 
	 * 
	 */
	public TrendsReport getReport() {
		return report;
	}

	/**
	 * @param report the report to set
	 */
	public void setReport(TrendsReport report) {
		this.report = report;
	}

	public GroupDTO[] getGroups() {
		return groups;
	}

	public void setGroups(GroupDTO[] groups) {
		this.groups = groups;
	}
	
	public boolean isShowTotals() {
		return showTotals;
	}
	
	public void setShowTotals(boolean showTotals) {
		this.showTotals = showTotals;
	}

	@Override
	public org.psygrid.data.reporting.definition.hibernate.TrendsChart toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		//check for an already existing instance of a hibernate object for this 
		//trends chart in the map of references
		org.psygrid.data.reporting.definition.hibernate.TrendsChart hTrends = null;
		if ( hRefs.containsKey(this)){
			hTrends = (org.psygrid.data.reporting.definition.hibernate.TrendsChart)hRefs.get(this);
		}
		else{
			//an instance of the trends chart has not already
			//been created, so create it, and add it to the
			//map of references
			hTrends = new org.psygrid.data.reporting.definition.hibernate.TrendsChart();
			hRefs.put(this, hTrends);
			toHibernate(hTrends, hRefs);
		}

		return hTrends;
	}

	public void toHibernate(org.psygrid.data.reporting.definition.hibernate.TrendsChart hC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		super.toHibernate(hC, hRefs);
		hC.setEndDate(endDate);
		hC.setStartDate(startDate);
		hC.setShowTotals(showTotals);

		if (report != null) {
			hC.setReport(report.toHibernate(hRefs));
		}

		List<ITrendsChartRow> hRows = hC.getRows();
		for ( TrendsChartRow r: getRows() ){
			if ( r != null ){
				hRows.add(r.toHibernate(hRefs));
			}
		}
		hC.setRows(hRows);
		
		List<Group> hGroups = hC.getGroups();
        for (GroupDTO g: groups){
            if ( null != g ){
                hGroups.add(g.toHibernate(hRefs));
            }
        }
	}
}
