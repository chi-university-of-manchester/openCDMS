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

package org.psygrid.data.reporting.renderer;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.psygrid.data.reporting.Report;

/**
 * Class to render a generated report textually, in
 * a tabular format.
 * 
 * @author Lucy Bridges
 */
public abstract class AbstractTextRenderer {

	/**
	 * Render a given generated report to the outputstream
	 *  
	 * @param report
	 * @param os
	 * @throws IOException
	 * @throws RendererException
	 */
	public abstract void render(Report report, OutputStream os) throws IOException, RendererException;

	/**
	 * Converts a String containing integers formated as "mm yyyy" into a nicely formated date for use 
	 * on chart labels.
	 * 
	 * @param label
	 * @return formatedLabel
	 */
	protected String formatDate(String label) {

		String[] date = null;
		boolean badDateFormat = false;

		String niceDate = label;	//this won't change unless the date formating is successful

		try {
			date = label.split(" ", 2);
		}
		catch (Exception e) {
			badDateFormat = true;
		}
		if (date.length < 2) {
			badDateFormat = true;
		}

		if (badDateFormat) {
			//give up trying to format date nicely as there was an error earlier
			return niceDate;
		}

		try {
			SimpleDateFormat format = new SimpleDateFormat("MMM-yyyy");
			Calendar cal = new GregorianCalendar(Integer.parseInt(date[1]), Integer.parseInt(date[0]), 0);
			Date date1 = cal.getTime();

			niceDate = format.format(date1);
		}
		catch (Exception e) {
			//do nothing to the date
		}

		return niceDate;
	}

	/**
	 * Sorts a list of dates, in the format 'month year'
	 * 
	 * @param dates
	 * @return sorted dates
	 */
	protected List<String> sortDates(List<String> dates) {

		String[] newDates = new String[dates.size()];
		for (int k = 0; k < dates.size(); k++) {
			newDates[k] = dates.get(k);
		}

		for (int i = 0; i < dates.size(); i++) {	
			for (int j = 1+i; j < dates.size(); j++) {
				int datePos = smallestDate(newDates[i], newDates[j]);
				if (datePos == 1)  {
					//new date is smaller so swap
					String old = new String(newDates[i]);
					newDates[i] = newDates[j];
					newDates[j] = old;
				}
			}
		}

		List<String> sortedDates = new ArrayList<String>();
		for (String d: newDates) {
			sortedDates.add(d);
		}
		return sortedDates;
	}

	/**
	 * Compares to date strings of the format: 'month year'
	 * 
	 * Returns 0 if they are equal, -1 if oldDate is smaller 
	 * and 1 if newDate is smaller.
	 * 
	 * @param oldDate
	 * @param newDate
	 * @return int
	 */
	protected int smallestDate(String oldDate, String newDate) {
		try {
			int oldMonth = Integer.parseInt(oldDate.split(" ", 2)[0]);
			int oldYear  = Integer.parseInt(oldDate.split(" ", 2)[1]);

			int newMonth = Integer.parseInt(newDate.split(" ", 2)[0]);
			int newYear  = Integer.parseInt(newDate.split(" ", 2)[1]);

			if (newYear < oldYear) {
				return 1;
			}
			else if (newYear == oldYear) {
				if (newMonth < oldMonth) {
					return 1;
				}
				else if (oldMonth < newMonth) {
					return -1;
				}
				else {
					return 0;
				}
			}
			else {	//oldYear > newYear
				return -1;
			}
		}
		catch (Exception e) {
			return -2;
		}
	}
}
