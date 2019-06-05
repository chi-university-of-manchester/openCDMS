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
 * Interface to represent a record chart.
 * <p>
 * A record chart will create a chart based on the 
 * documents stored in one or more records.
 * <p>
 * A record chart will provide just one dataset but allows 
 * multiple series. For example, a table with just one value 
 * column, a multiple pie charts or a bar chart with 
 * several sets of bars, but not a bar chart overlayed 
 * with a line graph.
 * 
 * @author Lucy Bridges
 *
 */

public interface ISimpleChart extends IChart {

	/**
	 * Get  the number of rows featured in the
	 * chart.
	 * 
	 * @return The number of rows.
	 */
	public int numRows();

}