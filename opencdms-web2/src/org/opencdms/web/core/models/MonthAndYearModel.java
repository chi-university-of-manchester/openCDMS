/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.opencdms.web.core.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Rob Harper
 *
 */
public class MonthAndYearModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String month;
	private String year;
	public MonthAndYearModel(){}
	public MonthAndYearModel(String month, String year){
		this.month = month;
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public Calendar getDate(){
		return getDate(this.month, this.year);
	}
	public static Calendar getDate(String month, String year){
		if ( null == month || null == year ){
			return null;
		}
		List<String> months = getMonths();
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(Calendar.MONTH, months.indexOf(month));
		date.set(Calendar.YEAR, Integer.parseInt(year));
		date.setTimeZone(TimeZone.getTimeZone("GMT"));
		return date;
	}
	public static final List<String> getMonths(){
		List<String> months = new ArrayList<String>();
		months.add("January");
		months.add("February");
		months.add("March");
		months.add("April");
		months.add("May");
		months.add("June");
		months.add("July");
		months.add("August");
		months.add("September");
		months.add("October");
		months.add("November");
		months.add("December");
		return months;
	}
}
