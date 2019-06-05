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
 * Interface to represent a trends chart for showing treatment usage.
 * Behaves the same as a trends chart but uses a gantt chart to 
 * show the data, with two datapoints per chart item to show the
 * start and end points. 
 */
public interface ITrendsGanttChart extends ITrendsChart {
	
	/**
	 * Get the total number of records
	 */
	public static final String SUMMARY_TYPE_GANTT   = "gantt";
	
}
