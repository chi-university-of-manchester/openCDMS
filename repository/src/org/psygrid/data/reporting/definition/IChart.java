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

import org.psygrid.data.model.IPersistent;
import org.psygrid.data.model.hibernate.ModelException;

/**
 * Interface to represent a sub-report of a report.
 * <p>
 * A simple chart will provide just one series of data
 * for (e.g.) a table with just one value column, a single
 * pie chart, or a bar chart with one set of bars.
 * 
 * @author Rob Harper
 *
 */
public interface IChart extends IPersistent {

    /**
     * Get the number of chart types associated with the chart
     * definition.
     * 
     * @return The number of chart types.
     */
    public int numTypes() ;

    /**
     * Get one of the chart types associated with the chart
     * definition.
     * 
     * @param index The index of the chart type to retrieve.
     * @return The chart type.
     * @throws org.psygrid.data.model.hibernate.ModelException if no chart type 
     * exists for the specified index.
     */
    public String getType(int index) throws ModelException;

    /**
     * Add a chart type to the collection of chart types associated
     * with the chart definition.
     * 
     * @param type The chart type.
     */
    public void addType(String type);
    
    /**
     * Get the title of the chart.
     * 
     * @return The title.
     */
    public String getTitle();

    /**
     * Set the title of the chart.
     * 
     * @param title The title.
     */
    public void setTitle(String title);
    
    /**
     * Get the label given to the range axis
     * 
     * @return rangeAxisLabel
     */
    public String getRangeAxisLabel();
    
    /**
     * Set the label to be given to the range axis
     */
    public void setRangeAxisLabel(String axisLabel);
    
    /**
     * Set whether the chart is to display values using percentages
     * 
     * @return usePercentages
     */
    public boolean isUsePercentages();
    
    /**
     * Get whether the chart is to display values using percentages
     * 
     * @param usePercentages
     */
    public void setUsePercentages(boolean usePercentages);

}
