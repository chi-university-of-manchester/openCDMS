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
 * Interface to represent a list of data series in a trends
 * chart.
 *
 * It provides options for specifying the type of summary
 * used to identify data trends. 
 *  
 * @author Lucy Bridges
 *
 */
public interface ITrendsChartRow extends ISimpleChartRow {

	/**
	 * Use the mean of the values for each series
	 */
	public static final String SUMMARY_TYPE_MEAN    = "mean";
	
	/**
	 * Use the median of the values for each series
	 */
	public static final String SUMMARY_TYPE_MEDIAN    = "median";
	
	/**
	 * Sum the values of each series
	 */
	public static final String SUMMARY_TYPE_SUMMATION   = "summation";
	
	/**
	 * Get the total number of records found
	 */
	public static final String SUMMARY_TYPE_TOTAL   = "total";
	
	/**
	 * Compare the different categories side-by-side rather than
	 * adding together. Normally used with a stacked bar chart 
	 * and has a date x-axis.
	 */
	public static final String SUMMARY_TYPE_COLLATE = "collate";	
	
	/**
	 * Display the highest values as points on the graph
	 */
	public static final String SUMMARY_TYPE_HIGH = "high";
	
	/**
	 * Display the lowest values as points on the graph
	 */
	public static final String SUMMARY_TYPE_LOW = "low";
	
	/**
	 * Display all values for all records found
	 */
	public static final String SUMMARY_TYPE_ALL   = "all";
	
	/**
	 * Get the method used to summarise the data
	 * 
	 * @return type
	 */
	public String getSummaryType();
	
	/**
	 * Set the method used to summarise the data
	 * 
	 * @param summaryType
	 */
	public void setSummaryType(String summaryType);

}