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

package org.opencdms.web.modules.reports.models;

import java.util.List;

import org.opencdms.web.core.models.MonthAndYearModel;
import org.psygrid.www.xml.security.core.types.GroupType;

/**
 * @author Rob Harper
 *
 */
public class TrendReportModel extends ReportModel {

	private static final long serialVersionUID = 1L;

	private List<GroupType> centres;
	private MonthAndYearModel startDate = new MonthAndYearModel();
	private MonthAndYearModel endDate = new MonthAndYearModel();
	private String summaryType = "default";
	private String showTotals = "No";
	public List<GroupType> getCentres() {
		return centres;
	}
	public void setCentres(List<GroupType> centres) {
		this.centres = centres;
	}
	public MonthAndYearModel getStartDate() {
		return startDate;
	}
	public void setStartDate(MonthAndYearModel startDate) {
		this.startDate = startDate;
	}
	public MonthAndYearModel getEndDate() {
		return endDate;
	}
	public void setEndDate(MonthAndYearModel endDate) {
		this.endDate = endDate;
	}
	public String getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}
	public String getShowTotals() {
		return showTotals;
	}
	public void setShowTotals(String showTotals) {
		this.showTotals = showTotals;
	}
	
}
