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


package org.psygrid.data.reporting.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import com.mindprod.quicksort.QuickSort;

/**
 * @author Rob Harper
 *
 */
public class StatsHelper {

	/**
	 * Calculate the arithmetic mean of a list of double values.
	 * <p>
	 * Null values are ignored. If the list contains no non-null values
	 * then null is returned.
	 * 
	 * @param data The list of double values.
	 * @return The arithmetic mean.
	 */
	public static Double calculateMean(List<Double> data){
		double total = 0;
		int count = 0;
		for ( Double value: data ){
			if ( null != value ){
				total += value.doubleValue();
				count++;
			}
		}
		if ( 0 == count ){
			return null;
		}
		return new Double(total/count);
	}
	
	/**
	 * Calculate the median of a list of double values.
	 * <p>
	 * Null values are ignored. If the list contains no non-null values
	 * then null is returned.
	 * <p>
	 * If the list contains an even number of values then the mean of the
	 * central two values is returned.
	 * 
	 * @param data The list of double values.
	 * @return The median.
	 */
	public static Double calculateMedian(List<Double> data){
		//remove nulls
		List<Double> dataNoNulls = new ArrayList<Double>();
		for ( Double d: data ){
			if ( null != d ){
				dataNoNulls.add(d);
			}
		}
		//sort the list
		Object[] dataArray = dataNoNulls.toArray();
		if ( 0 == dataArray.length ){
			return null;
		}
		QuickSort.sort(dataArray, new DoubleComparator());
		//find the median value
		if ( 0 == dataArray.length % 2 ){
			//even number of values, return mean of middle two
			Double val1 = (Double)dataArray[(dataArray.length/2)-1];
			Double val2 = (Double)dataArray[dataArray.length/2];
			return new Double( ( val1.doubleValue() + val2.doubleValue() ) / 2 );
		}
		else{
			//odd number of values, return middle value
			return (Double)dataArray[(dataArray.length-1)/2];
		}
	}
	
	/**
	 * Calculate the mode of a list of double values.
	 * <p>
	 * Null values are ignored. If the list contains no non-null values
	 * then null is returned.
	 * <p>
	 * The data is binned with one bin for each discrete value in the list
	 * i.e. data is not binned using ranges. If more than one value has the
	 * same number of occurrences then the median of these values is returned.
	 * 
	 * @param data The list of double values.
	 * @return The mode.
	 */
	public static Double calculateMode(List<Double> data){
		//bin the data
		Map<Double, Integer> bins = new HashMap<Double, Integer>();
		for ( Double d: data ){
			if ( null != d ){
				if ( bins.containsKey(d) ){
					bins.put(d, new Integer((bins.get(d).intValue()+1)));
				}
				else {
					bins.put(d, new Integer(1));
				}
			}
		}
		if ( 0 == bins.size() ){
			return null;
		}
		//find the bins with the most contents
		List<Double> values = new ArrayList<Double>();
		int currentMaxContents = 0;
		for ( Entry<Double, Integer> e: bins.entrySet() ){
			if ( e.getValue().intValue() > currentMaxContents ){
				values.clear();
				values.add(e.getKey());
				currentMaxContents = e.getValue().intValue();
			}
			else if ( e.getValue().intValue() == currentMaxContents ){
				values.add(e.getKey());
			}
		}
		if ( 1 == values.size() ){
			//one bin had more contents than any others - this is the mode
			return values.get(0);
		}
		else{
			//more than one bin had the same number of contents, more than the other bins
			//calculate the median of these values
			//TODO is this the right thing to do? Seems more appropriate than the mean...
			return calculateMedian(values);
		}
	}
	
	/**
	 * Find the minimum value in a list of double values.
	 * <p>
	 * Null values are ignored. If the list contains no non-null values
	 * then null is returned.
	 * 
	 * @param data The list of double values.
	 * @return The minimum.
	 */
	public static Double calculateMin(List<Double> data){
		//remove nulls
		List<Double> dataNoNulls = new ArrayList<Double>();
		for ( Double d: data ){
			if ( null != d ){
				dataNoNulls.add(d);
			}
		}
		//sort the list
		Object[] dataArray = dataNoNulls.toArray();
		if ( 0 == dataArray.length ){
			return null;
		}
		QuickSort.sort(dataArray, new DoubleComparator());
		//return the min
		return (Double)dataArray[0];
	}
	
	/**
	 * Find the maximum value in a list of double values.
	 * <p>
	 * Null values are ignored. If the list contains no non-null values
	 * then null is returned.
	 * 
	 * @param data The list of double values.
	 * @return The maximum.
	 */
	public static Double calculateMax(List<Double> data){
		//remove nulls
		List<Double> dataNoNulls = new ArrayList<Double>();
		for ( Double d: data ){
			if ( null != d ){
				dataNoNulls.add(d);
			}
		}
		//sort the list
		Object[] dataArray = dataNoNulls.toArray();
		if ( 0 == dataArray.length ){
			return null;
		}
		QuickSort.sort(dataArray, new DoubleComparator());
		//return the max
		return (Double)dataArray[dataArray.length-1];
	}
	
}
