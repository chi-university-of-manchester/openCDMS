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

package org.psygrid.data.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class to represent a single report to render.
 * 
 * @author Rob Harper
 *
 */
public class Report {

    /**
     * The title of the report.
     */
    private String title;
    
    /**
     * The date and time when the report was requested.
     */
    private Date requestDate;
    
    /**
     * The time period covered by the report 
     * (applicable for Trends Reports and some Management reports).
     * Shown as part of the header information.
     */
    private Date startDate;
    private Date endDate;
    
    /**
     * Display the header information for this report
     */
    private boolean showHeader;
    
    /**
     * The charts that are a part of the main report.
     */
    private Chart[] charts = new Chart[0];

    /**
     * The groups included when generating this report. Shown
     * as part of the header information.
     */
    private List<String> groups = new ArrayList<String>();
    
    /**
     * The type of summary used to calculate the values in this
     * report. Shown as part of the header information.
     * 
     * NB. Only used by trends reports.
     */
    private String summaryType;
    
    /**
     * Get the date and time when the report was requested.
     * 
     * @return The date and time when the report was requested.
     */
    public Date getRequestDate() {
        return requestDate;
    }

    /**
     * Set the date and time when the report was requested.
     * 
     * @param requestDate The date and time when the report was requested.
     */
    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }
    
    /**
     * Get the charts that are a part of the main report.
     * 
     * @return The charts.
     */
    public Chart[] getCharts() {
        return charts;
    }

    /**
     * Set the charts that are a part of the main report.
     * 
     * @param charts The charts.
     */
    public void setCharts(Chart[] charts) {
        this.charts = charts;
    }

    /**
     * Get the title of the report.
     * 
     * @return The title of the report.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the report.
     *  
     * @param title The title of the report.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the end date of the time period covered by this report
     * 
     * @return endDate
     */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Set the end date of the time period covered by this report
	 * 
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Get the start date of the time period covered by this report
	 * 
	 * @return startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Set the start date of the time period coverd by this report
	 * 
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get whether the header information is to be displayed 
	 * 
	 * @return showHeader
	 */
	public boolean isShowHeader() {
		return showHeader;
	}

	/**
	 * Set whether the header information is to be displayed.
	 * 
	 * @param showHeader
	 */
	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public String getSummaryType() {
		return summaryType;
	}

	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}


    
}
