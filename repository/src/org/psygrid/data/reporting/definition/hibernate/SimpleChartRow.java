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
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.definition.IAbstractChartItem;
import org.psygrid.data.reporting.definition.ISimpleChartRow;
import org.psygrid.data.reporting.definition.ReportException;

/**
 * A class representing a list of data series in a categorised chart. 
 * 
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_simple_chart_rows"
 * 							  proxy="org.psygrid.data.reporting.definition.hibernate.SimpleChartRow"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class SimpleChartRow extends Persistent implements ISimpleChartRow {

	/**
	 * The category/series name
	 */
	private String label = "";
	
	private String labelType = "";
	
	/**
	 * The collection of chart items that will be featured in the
	 * chart.
	 */
	private List<AbstractChartItem> series = new ArrayList<AbstractChartItem>();

	public SimpleChartRow() {
	}

	
	/**
	 * 
	 * @return chartItems
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.reporting.definition.hibernate.AbstractChartItem"					  
	 * @hibernate.key column="c_simple_chart_row_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<AbstractChartItem> getSeries() {
		return series;
	}

	public void setSeries(List<AbstractChartItem> series) {
		this.series = series;
	}

	public void addSeries(IAbstractChartItem series) throws ReportException {
		if ( null == series ){
			throw new ReportException("Cannot add a null item");
		}
		this.series.add((AbstractChartItem)series);
	}

	public IAbstractChartItem getSeries(int index) throws ReportException {
		try{
			return this.series.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ReportException("No item exists for index="+index, ex);
		}
	}

	public int numItems() {
		return this.series.size();
	}


	/**
	 * @hibernate.property column="c_label"
	 */
	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @hibernate.property column="c_label_type"
	 */
	public String getLabelType() {
		return this.label;
	}

	public void setLabelType(String type) {
		this.labelType = type;
	}

	
	
	@Override
	public org.psygrid.data.reporting.definition.dto.SimpleChartRow toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//complex chart item in the map of references
		org.psygrid.data.reporting.definition.dto.SimpleChartRow dtoCCI = null;
		if ( dtoRefs.containsKey(this)){
			dtoCCI = (org.psygrid.data.reporting.definition.dto.SimpleChartRow)dtoRefs.get(this);
		}
		else {
			//an instance of the element has not already
			//been created, so create it, and add it to the
			//map of references
			dtoCCI = new org.psygrid.data.reporting.definition.dto.SimpleChartRow();
			dtoRefs.put(this, dtoCCI);
			toDTO(dtoCCI, dtoRefs, depth);
		}

		return dtoCCI;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.SimpleChartRow dtoRow, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoRow, dtoRefs, depth);

		if (label != null) {
			dtoRow.setLabel(label);
		}

		if (labelType != null) {
			dtoRow.setLabelType(labelType);
		}
		
		if ( depth != RetrieveDepth.DS_SUMMARY ){
			org.psygrid.data.reporting.definition.dto.AbstractChartItem[] dtoItems = new org.psygrid.data.reporting.definition.dto.AbstractChartItem[this.series.size()];
			for (int i=0; i<this.series.size(); i++){
				AbstractChartItem s = (AbstractChartItem)series.get(i);
				dtoItems[i] = s.toDTO(dtoRefs, depth);
			}        
			dtoRow.setSeries(dtoItems);
		}
	}
}
