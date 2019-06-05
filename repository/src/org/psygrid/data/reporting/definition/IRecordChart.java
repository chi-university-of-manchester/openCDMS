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

/**
 * Interface to represent the definition of a "simple" chart.
 * <p>
 * A simple chart will create a chart based on the 
 * document stored in a record.
 * <p>
 * A simple chart will provide just one dataset but allows 
 * multiple series. For example, a table with just one value 
 * column, a multiple pie charts or a bar chart with 
 * several sets of bars, but not a bar chart overlayed 
 * with a line graph.
 * 
 * @author Rob Harper
 *
 */
public interface IRecordChart extends ISimpleChart {

	/**
	 * Add an item to be featured in the chart.
	 * 
	 * @param entry The item.
	 * @throws ReportException if the item is <code>null</code>.
	 */
	public void addRow(ISimpleChartRow row) throws ReportException;

	/**
	 * Get an item featured in the chart.
	 * 
	 * @param index The index of the item.
	 * @return The item.
	 * @throws ReportException if no item exists for the given index.
	 */
	public ISimpleChartRow getRow(int index) throws ReportException;
	
    /**
     * This method is deprecated, as charts now have multiple rows. Use
	 * addRow() instead.
	 * 
     * Add an item to be featured in the chart.
     * 
     * @param entry The item.
     * @throws ReportException if the item is <code>null</code>.
     */
	@Deprecated
    public void addItem(IAbstractChartItem item) throws ReportException;
    
}
