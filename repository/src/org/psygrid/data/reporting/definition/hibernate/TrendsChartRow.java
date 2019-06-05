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

import java.util.Map;

import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.definition.ITrendsChart;
import org.psygrid.data.reporting.definition.ITrendsChartRow;

/**
 * Class representing a list of data series in a trends
 * chart.
 *
 * It provides options for specifying the type of summary
 * applied to the data to identify trends. 
 * 
 * @author lucy
 *
 * @hibernate.joined-subclass table="t_trends_chart_row"
 * 							entity-name="TrendsChartRows"
 * 							proxy="org.psygrid.data.reporting.definition.hibernate.TrendsChartRow"
 * @hibernate.joined-subclass-key column="c_id"
 *
 */
public class TrendsChartRow extends SimpleChartRow implements ITrendsChartRow {

	/**
	 * The method employed to summarise the data
	 * 
	 * Defaults to mean, which will return an 
	 * 'average' of the values of all points in 
	 * a particular month.
	 */
	private String summaryType = SUMMARY_TYPE_MEAN;

	private ITrendsChart chart = null;

	/**
	 * Get the method used to summarise the data.
	 * Defaults to the mean if no other type is
	 * specified.
	 * 
	 * @return type
	 * 
	 * @hibernate.property column="c_summary_type"
	 */
	public String getSummaryType() {
		return summaryType;
	}

	/**
	 * Set the method used to summarise the data
	 * 
	 * @param summaryType
	 */
	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}

	/**
	 * Get the chart this row belongs to.
	 * 
	 * @return the chart
	 * 
	 * @hibernate.many-to-one class="org.psygrid.data.reporting.definition.hibernate.TrendsChart"
	 *                        column="c_trends_chart_id"
	 *                        not-null="true"
	 *                        update="false"
	 *                        insert="false"
	 */
	public ITrendsChart getChart() {
		return chart;
	}

	/**
	 * @param chart 
	 */
	public void setChart(ITrendsChart chart) {
		this.chart = chart;
	}
	
	@Override
	public org.psygrid.data.reporting.definition.dto.TrendsChartRow toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//trends chart in the map of references
		org.psygrid.data.reporting.definition.dto.TrendsChartRow dtoTrends = null;
		if ( dtoRefs.containsKey(this)){
			dtoTrends = (org.psygrid.data.reporting.definition.dto.TrendsChartRow)dtoRefs.get(this);
		}
		else {
			//an instance of the element has not already
			//been created, so create it, and add it to the
			//map of references
			dtoTrends = new org.psygrid.data.reporting.definition.dto.TrendsChartRow();
			dtoRefs.put(this, dtoTrends);
			toDTO(dtoTrends, dtoRefs, depth);
		}

		return dtoTrends;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.TrendsChartRow dtoTrends, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoTrends, dtoRefs, depth);

		dtoTrends.setSummaryType(this.summaryType);
		
		if (chart != null) {
			dtoTrends.setChart(chart.toDTO(dtoRefs, depth));
		}
	}
}
