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

import java.util.Calendar;

import org.hibernate.Session;
import org.psygrid.data.utils.esl.IRemoteClient;

/**
 * Class to represent a chart in a management report that displays
 * a UKCRN accural report for a given study
 * 
 * See bug 641 for full report requirements.
 */
public interface IUKCRNSummaryChart extends IManagementChart {

	/**
	 * Set the time period the chart is to cover
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public void setTimePeriod(Calendar startDate, Calendar endDate);
	
	/**
	 * Get the chart's start date
	 * 
	 * @return startDate
	 */
	public Calendar getStartDate();
	
	/**
	 * Get the chart's end date
	 * 
	 * @return endDate
	 */
	public Calendar getEndDate();
	
	/**
	 * Generate the UKCRNSummary chart
	 * 
	 * @param session
	 * @param client
	 * @return
	 */
	public org.psygrid.data.reporting.Chart generateChart(Session session, IRemoteClient client, String saml);
}
