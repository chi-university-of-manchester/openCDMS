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

import java.io.Serializable;

import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class ReportModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private ProjectType study;
	private Report report;
	private String format;

	public ProjectType getStudy() {
		return study;
	}

	public void setStudy(ProjectType study) {
		this.study = study;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}


	public static class Report implements Serializable {

		private static final long serialVersionUID = 1L;

		private Long reportId;
		private String reportName;
		public Report(Long reportId, String reportName){
			this.reportId = reportId;
			this.reportName = reportName;
		}
		public Long getReportId() {
			return reportId;
		}
		public void setReportId(Long reportId) {
			this.reportId = reportId;
		}
		public String getReportName() {
			return reportName;
		}
		public void setReportName(String reportName) {
			this.reportName = reportName;
		}
	}
	

}
