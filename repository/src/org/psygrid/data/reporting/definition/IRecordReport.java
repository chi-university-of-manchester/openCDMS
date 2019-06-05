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

import org.psygrid.data.model.hibernate.Record;

public interface IRecordReport extends IReport {

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
    public void addChart(IRecordChart chart) throws ReportException;
    
    /**
     * Get a chart contained by the report, at the specified index.
     * 
     * @param index The index.
     * @return The chart at the given index.
     * @throws ReportException if no chart exists for the given index.
     */
    public IRecordChart getChart(int index) throws ReportException;
    
    /**
     * The record to be reported on.
     * 
	 * @return the record
	 */
	public Record getRecord();
	
	/**
	 * The record to be reported on.
	 * 
	 * @param record the record to set
	 */
	public void setRecord(Record record);
	
	public org.psygrid.data.reporting.definition.dto.RecordReport toDTO();
}
