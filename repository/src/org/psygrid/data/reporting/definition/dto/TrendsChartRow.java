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

import java.util.Map;

import org.psygrid.data.model.dto.PersistentDTO;

public class TrendsChartRow extends SimpleChartRow {

	private String summaryType;
	
	private TrendsChart chart = null;
	
	/**
	 * Get the method used to summarise the data.
	 * Defaults to the mean if no other type is
	 * specified.
	 * 
	 * @return type
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

	public TrendsChart getChart() {
		return chart;
	}

	/**
	 * @param chart 
	 */
	public void setChart(TrendsChart chart) {
		this.chart = chart;
	}
	
	@Override
	public org.psygrid.data.reporting.definition.hibernate.TrendsChartRow toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		//check for an already existing instance of a hibernate object for this 
		//complex chart item in the map of references
		org.psygrid.data.reporting.definition.hibernate.TrendsChartRow hCCI = null;
		if ( hRefs.containsKey(this)){
			hCCI = (org.psygrid.data.reporting.definition.hibernate.TrendsChartRow)hRefs.get(this);
		}
		else{
			//an instance of the complex chart item has not already
			//been created, so create it, and add it to the
			//map of references
			hCCI = new org.psygrid.data.reporting.definition.hibernate.TrendsChartRow();
			hRefs.put(this, hCCI);
			toHibernate(hCCI, hRefs);
		}

		return hCCI;
	}

	public void toHibernate(org.psygrid.data.reporting.definition.hibernate.TrendsChartRow hRow, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hRow, hRefs);
		hRow.setSummaryType(this.summaryType);
		
		if (chart != null) {
			hRow.setChart(chart.toHibernate(hRefs));
		}
	}
}
