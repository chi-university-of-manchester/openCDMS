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

package org.psygrid.web.forms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Used to store the results of the 'generate report' web form wizard
 * 
 * @author Lucy Bridges
 *
 */
public class TrendsReport extends Report {

	private List<String> groups = new ArrayList<String>();
	private String summaryType;
	private boolean showTotals;
	private Calendar startDate;
	private Calendar endDate;


	/**
	 * Get the list of group codes this report is to
	 * be generated for.
	 * 
	 * @return group codes
	 */
	public List<String> getGroups() {
		return groups;
	}

	public void addGroup(String g) {
		groups.add(g);
	}
	
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public boolean isShowTotals() {
		return showTotals;
	}

	public void setShowTotals(boolean showTotals) {
		this.showTotals = showTotals;
	}

	public String getSummaryType() {
		return summaryType;
	}

	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}

	public Calendar getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public Calendar getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
}
