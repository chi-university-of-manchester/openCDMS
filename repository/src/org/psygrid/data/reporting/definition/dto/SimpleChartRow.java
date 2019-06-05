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

import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.PersistentDTO;

/**
 * A class representing a list of data series in a complex
 * or categorised chart. 
 * 
 * @author Lucy Bridges
 */
public class SimpleChartRow extends PersistentDTO {

	/**
	 * The category/series name
	 */
	private String label = "";

	private String labelType = "";
	
	/**
	 * The collection of chart items that will be featured in the
	 * chart.
	 */
	private AbstractChartItem[] series = new AbstractChartItem[0];

	public SimpleChartRow() {
	}
	
	public void setLabel(String title) {
		this.label = title;
	}

	public String getLabel() {
		return label;
	}

	public void setLabelType(String type) {
		this.labelType = type;
	}

	public String getLabelType() {
		return labelType;
	}
	
	public void setSeries(AbstractChartItem[] series) {
		this.series = series;
	}

	public AbstractChartItem[] getSeries() {
		return series;
	}

	@Override
	public org.psygrid.data.reporting.definition.hibernate.SimpleChartRow toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		//check for an already existing instance of a hibernate object for this 
		//complex chart item in the map of references
		org.psygrid.data.reporting.definition.hibernate.SimpleChartRow hCCI = null;
		if ( hRefs.containsKey(this)){
			hCCI = (org.psygrid.data.reporting.definition.hibernate.SimpleChartRow)hRefs.get(this);
		}
		else{
			//an instance of the complex chart item has not already
			//been created, so create it, and add it to the
			//map of references
			hCCI = new org.psygrid.data.reporting.definition.hibernate.SimpleChartRow();
			hRefs.put(this, hCCI);
			toHibernate(hCCI, hRefs);
		}

		return hCCI;
	}

	public void toHibernate(org.psygrid.data.reporting.definition.hibernate.SimpleChartRow hRow, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hRow, hRefs);
		List<org.psygrid.data.reporting.definition.hibernate.AbstractChartItem> hItems = hRow.getSeries();
		for ( int i=0; i<this.series.length; i++ ){
			if (this.series[i] != null) {
				hItems.add(this.series[i].toHibernate(hRefs));
			}
		}
		hRow.setSeries(hItems);
		
		if (label != null) {
			hRow.setLabel(this.label);
		}
		if (labelType != null) {
			hRow.setLabelType(this.labelType);
		}
	}
}
