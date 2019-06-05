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


package org.psygrid.web.controllers.reporting;


import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.web.forms.ManagementReport;
import org.psygrid.web.forms.RecordReport;
import org.psygrid.web.forms.Report;
import org.psygrid.web.forms.TrendsReport;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ReportValidator implements Validator {

	private final Log logger = LogFactory.getLog(getClass());

	public boolean supports(Class clazz) {
		return (clazz.equals(TrendsReport.class) || clazz.equals(RecordReport.class)
				|| clazz.equals(ManagementReport.class));
	}

	/**
	 * make this into a specific class for validating the trends
	 * report generation path. then extrapolate to make a generic
	 * super class for validating all report generation?
	 */
	
	public void validate(Object obj, Errors errors) {
		Report report = (Report) obj;
		if (report == null) {
			errors.rejectValue("dataset", "error.report.general", null,
			"Study required.");
		} else {
			logger.info("Validating user credentials for: "
					+ report.getDataset());
			if (report.getDataset().equals("")) {
				errors.rejectValue("dataset", "error.report.general",
						null, "Study required.");
			} else {
				if (report.getTitle() == null || report.getTitle().equals("")) {
					report.setTitle("This is a report title");
					errors.rejectValue("title", "error.report.general",
							null, "A report title must be selected.");
				}
			}
		}
	}
	
	/**
	 * Generates an error if the start date is NOT before the end date
	 * 
	 * @param startMonth
	 * @param startYear
	 * @param endMonth
	 * @param endYear
	 * @param errors
	 */
	public void checkDates(int startMonth, int startYear, int endMonth, int endYear, Errors errors) {
		Calendar start = new GregorianCalendar(startYear, startMonth, 0);
		Calendar end   = new GregorianCalendar(endYear, endMonth, 0);
		
		if ( start.after(end) ) {
			errors.rejectValue("endDate", "error.report.enddate");
		}
	}
}
