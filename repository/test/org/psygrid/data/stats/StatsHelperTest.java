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


package org.psygrid.data.stats;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.reporting.stats.StatsHelper;

/**
 * @author Rob Harper
 *
 */
public class StatsHelperTest {

	@Test()
	public void testCalculateMean(){
		
		List<Double> data1 = new ArrayList<Double>();
		data1.add(new Double(1.5));
		data1.add(new Double(2.3));
		data1.add(new Double(4.6));
		data1.add(new Double(3.1));
		data1.add(new Double(2.2));
		data1.add(new Double(7.3));
		
		AssertJUnit.assertEquals("Mean for data1 is incorrect",new Double(3.5),StatsHelper.calculateMean(data1));
		
		List<Double> data2 = new ArrayList<Double>();
		data2.add(null);
		data2.add(null);
		data2.add(new Double(2.0));
		data2.add(null);
		data2.add(new Double(4.0));
		
		AssertJUnit.assertEquals("Mean for data2 is incorrect",new Double(3.0),StatsHelper.calculateMean(data2));
		
		List<Double> data3 = new ArrayList<Double>();
		data3.add(null);
		data3.add(null);
		
		AssertJUnit.assertEquals("Mean for data3 is incorrect",null,StatsHelper.calculateMean(data3));
		
		List<Double> data4 = new ArrayList<Double>();
		
		AssertJUnit.assertEquals("Mean for data4 is incorrect",null,StatsHelper.calculateMean(data4));
		
	}
	
	@Test()
	public void testCalculateMedian(){
		
		List<Double> data1 = new ArrayList<Double>();
		data1.add(new Double(1.5));
		data1.add(new Double(2.3));
		data1.add(new Double(4.6));
		data1.add(new Double(3.1));
		data1.add(new Double(2.2));
		data1.add(new Double(7.3));
		data1.add(new Double(5.6));
		
		AssertJUnit.assertEquals("Median for data1 is incorrect",new Double(3.1),StatsHelper.calculateMedian(data1));
		
		List<Double> data2 = new ArrayList<Double>();
		data2.add(new Double(1.5));
		data2.add(new Double(2.3));
		data2.add(new Double(4.6));
		data2.add(new Double(3.1));
		data2.add(new Double(2.2));
		data2.add(new Double(7.3));
		
		AssertJUnit.assertEquals("Median for data2 is incorrect",new Double(2.7),StatsHelper.calculateMedian(data2));
		
		List<Double> data3 = new ArrayList<Double>();
		data3.add(new Double(1.5));
		data3.add(new Double(2.3));
		data3.add(null);
		data3.add(new Double(4.6));
		data3.add(new Double(3.1));
		data3.add(null);
		data3.add(new Double(2.2));
		data3.add(new Double(7.3));
		
		AssertJUnit.assertEquals("Median for data3 is incorrect",new Double(2.7),StatsHelper.calculateMedian(data3));
		
		List<Double> data4 = new ArrayList<Double>();
		data4.add(null);
		data4.add(null);
		data4.add(null);
		
		AssertJUnit.assertEquals("Median for data4 is incorrect",null,StatsHelper.calculateMedian(data4));
		
		List<Double> data5 = new ArrayList<Double>();
		
		AssertJUnit.assertEquals("Median for data5 is incorrect",null,StatsHelper.calculateMedian(data5));
		
	}
	
	@Test()
	public void testCalculateMode(){
		
		List<Double> data1 = new ArrayList<Double>();
		data1.add(new Double(1.0));
		data1.add(new Double(1.0));
		data1.add(new Double(2.0));
		data1.add(new Double(2.0));
		data1.add(new Double(2.0));
		data1.add(new Double(1.5));
		data1.add(new Double(1.2));
		
		AssertJUnit.assertEquals("Mode for data1 is incorrect",new Double(2.0),StatsHelper.calculateMode(data1));
		
		List<Double> data2 = new ArrayList<Double>();
		data2.add(new Double(1.0));
		data2.add(new Double(1.0));
		data2.add(new Double(2.0));
		data2.add(new Double(2.0));
		data2.add(new Double(2.0));
		data2.add(new Double(1.0));
		data2.add(new Double(1.2));
		
		AssertJUnit.assertEquals("Mode for data2 is incorrect",new Double(1.5),StatsHelper.calculateMode(data2));
		
		List<Double> data3 = new ArrayList<Double>();
		data3.add(new Double(1.0));
		data3.add(new Double(1.0));
		data3.add(null);
		data3.add(new Double(2.0));
		data3.add(new Double(2.0));
		data3.add(new Double(2.0));
		data3.add(null);
		data3.add(new Double(1.5));
		data3.add(new Double(1.2));
		
		AssertJUnit.assertEquals("Mode for data3 is incorrect",new Double(2.0),StatsHelper.calculateMode(data3));
		
		List<Double> data4 = new ArrayList<Double>();
		data4.add(null);
		data4.add(null);
		data4.add(null);
		
		AssertJUnit.assertEquals("Mode for data4 is incorrect",null,StatsHelper.calculateMode(data4));
		
		List<Double> data5 = new ArrayList<Double>();
		
		AssertJUnit.assertEquals("Mode for data5 is incorrect",null,StatsHelper.calculateMode(data5));
		
	}
	
	@Test()
	public void testCalculateMin(){
		
		List<Double> data1 = new ArrayList<Double>();
		data1.add(new Double(1.5));
		data1.add(new Double(2.3));
		data1.add(new Double(4.6));
		data1.add(new Double(3.1));
		data1.add(new Double(2.2));
		data1.add(new Double(7.3));
		
		AssertJUnit.assertEquals("Min for data1 is incorrect",new Double(1.5),StatsHelper.calculateMin(data1));
		
		List<Double> data2 = new ArrayList<Double>();
		data2.add(null);
		data2.add(null);
		data2.add(new Double(2.0));
		data2.add(null);
		data2.add(new Double(4.0));
		
		AssertJUnit.assertEquals("Min for data2 is incorrect",new Double(2.0),StatsHelper.calculateMin(data2));
		
		List<Double> data3 = new ArrayList<Double>();
		data3.add(null);
		data3.add(null);
		
		AssertJUnit.assertEquals("Min for data3 is incorrect",null,StatsHelper.calculateMin(data3));
		
		List<Double> data4 = new ArrayList<Double>();
		
		AssertJUnit.assertEquals("Min for data4 is incorrect",null,StatsHelper.calculateMin(data4));
		
	}
	
	@Test()
	public void testCalculateMax(){
		
		List<Double> data1 = new ArrayList<Double>();
		data1.add(new Double(1.5));
		data1.add(new Double(2.3));
		data1.add(new Double(4.6));
		data1.add(new Double(3.1));
		data1.add(new Double(2.2));
		data1.add(new Double(7.3));
		
		AssertJUnit.assertEquals("Max for data1 is incorrect",new Double(7.3),StatsHelper.calculateMax(data1));
		
		List<Double> data2 = new ArrayList<Double>();
		data2.add(null);
		data2.add(null);
		data2.add(new Double(2.0));
		data2.add(null);
		data2.add(new Double(4.0));
		
		AssertJUnit.assertEquals("Max for data2 is incorrect",new Double(4.0),StatsHelper.calculateMax(data2));
		
		List<Double> data3 = new ArrayList<Double>();
		data3.add(null);
		data3.add(null);
		
		AssertJUnit.assertEquals("Max for data3 is incorrect",null,StatsHelper.calculateMax(data3));
		
		List<Double> data4 = new ArrayList<Double>();
		
		AssertJUnit.assertEquals("Max for data4 is incorrect",null,StatsHelper.calculateMax(data4));
		
	}
	

}
