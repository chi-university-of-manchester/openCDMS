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

import java.util.Map;

import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;

/**
 * Interface to represent a trends report.
 * 
 * A trends report provides a summary of data from 
 * across a dataset (from documents in one or more
 * records) allowing trends to be highlighted.
 * 
 * @author Lucy Bridges
 *
 */
public interface ITrendsReport extends IReport {

    /**
     * Get the number of charts contained by this report.
     * 
     * @return The number of charts.
     */
    public int numCharts();
    
    /**
     * Add a chart to the report.
     * 
     * @param chart The chart to add.
     * @throws ReportException if the chart being added is <code>null</code>.
     */
    public void addChart(ITrendsChart chart) throws ReportException;
    
    /**
     * Get a chart contained by the report, at the specified index.
     * 
     * @param index The index.
     * @return The chart at the given index.
     * @throws ReportException if no chart exists for the given index.
     */
    public ITrendsChart getChart(int index) throws ReportException;
    

    public org.psygrid.data.reporting.definition.dto.TrendsReport toDTO();
    public org.psygrid.data.reporting.definition.dto.TrendsReport toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);
}
