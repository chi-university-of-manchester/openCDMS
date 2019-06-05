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

import org.psygrid.data.model.IPersistent;
import org.psygrid.data.reporting.definition.hibernate.AbstractChartItem;

/**
 * Interface to represent a list of data series in a complex
 * or categorised chart. 
 * 
 * @author Lucy Bridges
 *
 */
public interface ISimpleChartRow extends IPersistent {

    public static final String LABEL_LABEL_ONLY = "Label";
    public static final String LABEL_TEXT_ONLY  = "Text";
    public static final String LABEL_LABEL_TEXT = "LabelText";
    
    /**
     * Set the title of this category
     * 
     * @param title
     */
    public void setLabel(String label);
    
    /**
     * Get the title of this category
     * 
     * @return String
     */
    public String getLabel();
    
    //TODO javadoc
    public void setLabelType(String labelType);
    
    //TODO javadoc
    public String getLabelType();

    /**
     * 
     * @throws ReportException if the item is <code>null</code>.
     */
    public void addSeries(IAbstractChartItem series) throws ReportException;
    
    /**
     * Get an item featured in the chart.
     * 
     * @param index The index of the item.
     * @return The item.
     * @throws ReportException if no item exists for the given index.
     */
    public IAbstractChartItem getSeries(int index) throws ReportException;
    
    public List<AbstractChartItem> getSeries() throws ReportException;
}