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

import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.definition.IChart;

/**
 * Class to represent an abstract chart.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_charts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class Chart extends Persistent implements IChart {

	/**
	 * The type of a chart generated from this definition.
	 */
	protected List<String> types = new ArrayList<String>();

	/**
	 * The title of the chart.
	 */
	protected String title;

	/**
	 * The label given to the range axis
	 */
	protected String rangeAxisLabel = null;
	
	/**
	 * Define whether the data axis is to show absolute or percentage values 
	 */
	protected boolean usePercentages = false;
	
	List<String> allowedStates = new ArrayList<String>();
	
	public Chart(){
		setAllowedStates();
	}
	

	public Chart(String type, String title){
		types.add(type);
		this.title = title;
		setAllowedStates();
	}

	/**
	 * @hibernate.list table="t_chart_types"
	 *                 cascade="all"
	 * @hibernate.key column="c_chart_id"
	 * @hibernate.list-index column="c_index"
	 * @hibernate.element type="string"
	 *                    column="c_type"
	 *                    not-null="true"
	 */
	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public void addType(String type) {
		this.types.add(type);
	}

	public String getType(int index) throws ModelException {
		try{
			return this.types.get(index);
		}
		catch (IndexOutOfBoundsException ex){
			throw new ModelException("No chart type exists for index="+index);
		}
	}

	public int numTypes() {
		return this.types.size();
	}

	/**
	 * @hibernate.property column="c_title"
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return rangeAxisLabel
	 * 
	 * @hibernate.property column="c_range_axis_label"
	 */
	public String getRangeAxisLabel() {
		return rangeAxisLabel;
	}
	
	public void setRangeAxisLabel(String axisLabel) {
		this.rangeAxisLabel = axisLabel;
	}

   /**
     * Set whether the chart is to display values using percentages
     * 
     * @return usePercentages
     * 
     * @hibernate.property column="c_use_percentages"
     */
    public boolean isUsePercentages() {
    	return usePercentages;
    }
    
    /**
     * Get whether the chart is to display values using percentages
     * 
     * @param usePercentages
     */
    public void setUsePercentages(boolean usePercentages) {
    	this.usePercentages = usePercentages;
    }

	public List<String> getAllowedStates() {
		return allowedStates;
	}

	/**
	 * Set the Record states that are to be featured
	 * in this report using generic states
	 *
	 */
	protected abstract void setAllowedStates();
	
	@Override
	public abstract org.psygrid.data.reporting.definition.dto.Chart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);

	public void toDTO(org.psygrid.data.reporting.definition.dto.Chart dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoC, dtoRefs, depth);
		String[] dtoTypes = new String[this.types.size()];
		for ( int i=0; i<this.types.size(); i++ ){
			dtoTypes[i] = this.types.get(i);
		}
		dtoC.setTypes(dtoTypes);
		dtoC.setTitle(this.title);
		dtoC.setRangeAxisLabel(rangeAxisLabel);
		dtoC.setUsePercentages(usePercentages);
	}

}
