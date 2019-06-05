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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.opencdms.web.core.models.MonthAndYearModel;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.www.xml.security.core.types.GroupType;

/**
 * @author Rob Harper
 *
 */
public class MgmtReportModel extends ReportModel {

	private static final long serialVersionUID = 1L;
	
	public static final String RECRUITMENT  = "recruitment";
	public static final String UKCRNSUMMARY = "ukcrnsummary";
	public static final String RECEIVINGTREATMENT  = "receivingtreatment";
	public static final String STATUSREPORT  = "statusreport";
	public static final String DATEREPORT  = "datereport";
	public static final String DOCUMENTREPORT = "documentreport";
	public static final String BASICSTATSREPORT = "basicstatsreport";
	
	private List<GroupType> centres = new ArrayList<GroupType>();
	private MonthAndYearModel startDate = new MonthAndYearModel();
	private MonthAndYearModel endDate = new MonthAndYearModel();
	private String targetType;
	private String type;
	private Integer allMonths = Integer.valueOf(0);
	private List<MonthlyTarget> perMonth = new ArrayList<MonthlyTarget>();
	private List<String> statistics = new ArrayList<String>();
	private DataSet dataSet;
	private DocumentOccurrence document;
	private List<Entry> entries;
	
	public List<GroupType> getCentres() {
		return centres;
	}

	public void setCentres(List<GroupType> centres) {
		this.centres = centres;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public List<String> getStatistics() {
		return statistics;
	}

	public void setStatistics(List<String> statistics) {
		this.statistics = statistics;
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

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public Integer getAllMonths() {
		return allMonths;
	}

	public void setAllMonths(Integer allMonths) {
		this.allMonths = allMonths;
	}

	public List<MonthlyTarget> getPerMonth() {
		return perMonth;
	}

	public void setPerMonth(List<MonthlyTarget> perMonth) {
		this.perMonth = perMonth;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	public DocumentOccurrence getDocument() {
		return document;
	}

	public void setDocument(DocumentOccurrence document) {
		this.document = document;
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	public boolean showCentres() {
		if (RECRUITMENT.equals(type) || 
				STATUSREPORT.equals(type) || 
				DATEREPORT.equals(type) ||
				BASICSTATSREPORT.equals(type) ) {
			return true;
		}
		return false;
	}

	public boolean showTargets() {
		if (RECRUITMENT.equals(type) ) {
			return true;
		}
		return false;
	}

	public boolean showDocuments() {
		if (DOCUMENTREPORT.equals(type) || 
				BASICSTATSREPORT.equals(type)) {
			return true;
		}
		return false;
	}

	public boolean showEntries() {
		if (BASICSTATSREPORT.equals(type)) {
			return true;
		}
		return false;
	}

	public boolean showStats() {
		if (BASICSTATSREPORT.equals(type)) {
			return true;
		}
		return false;
	}

	public static class MonthlyTarget{
		private String month;
		private Calendar cal;
		private Integer target;
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public Calendar getCal() {
			return cal;
		}
		public void setCal(Calendar cal) {
			this.cal = cal;
		}
		public Integer getTarget() {
			return target;
		}
		public void setTarget(Integer target) {
			this.target = target;
		}
	}


	public void initializeTargets(Integer target){
		Calendar start = startDate.getDate();
		Calendar end = endDate.getDate();
		List<String> months = MonthAndYearModel.getMonths();
		int cumulativeTarget = 0;
		while ( !start.after(end) ){
			
			cumulativeTarget += target.intValue();
			
			MonthlyTarget mt = new MonthlyTarget();
			mt.setTarget(new Integer(cumulativeTarget));
			mt.setMonth(months.get(start.get(Calendar.MONTH))+" "+start.get(Calendar.YEAR));
			mt.setCal((Calendar)start.clone());
			perMonth.add(mt);
			int nextMonth = start.get(Calendar.MONTH);
			int nextYear = start.get(Calendar.YEAR);
			nextMonth++;
			if ( nextMonth>11 ){
				nextMonth=0;
				nextYear++;
			}
			start.set(Calendar.MONTH, nextMonth);
			start.set(Calendar.YEAR, nextYear);
		}
	}
	
	public void initializeTargets(){
		initializeTargets(Integer.valueOf(0));
	}
}
