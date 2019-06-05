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

package org.psygrid.web.repository;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.model.dto.extra.MinimalEntry;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.reporting.GroupsNotAllowedException;
import org.psygrid.data.reporting.ReportRenderingException;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IBasicStatisticsChart;
import org.psygrid.data.reporting.definition.ICollectionDateChart;
import org.psygrid.data.reporting.definition.IDocumentStatusChart;
import org.psygrid.data.reporting.definition.IManagementChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IReceivingTreatmentChart;
import org.psygrid.data.reporting.definition.IRecordReport;
import org.psygrid.data.reporting.definition.IRecordStatusChart;
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.reporting.definition.IStdCodeStatusChart;
import org.psygrid.data.reporting.definition.ITrendsChart;
import org.psygrid.data.reporting.definition.ITrendsChartRow;
import org.psygrid.data.reporting.definition.ITrendsReport;
import org.psygrid.data.reporting.definition.IUKCRNSummaryChart;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.web.beans.DocumentBean;
import org.psygrid.web.beans.DocumentOccurrenceBean;
import org.psygrid.web.beans.EntryBean;
import org.psygrid.web.forms.ManagementReport;
import org.psygrid.web.forms.RecordReport;
import org.psygrid.web.forms.TrendsReport;
import org.psygrid.web.helpers.ReportHelper;

public class ReportingClient implements Reporting {

	private String url;

	private ReportsClient client;

	private static Log sLog = LogFactory.getLog(ReportingClient.class);

	//possible trends chart types - based on types in org.psygrid.data.reporting.Chart
	public enum chartType {TABLE, BAR, BAR_HZ, PIE, LINE, LINE_HZ, STACKED_BAR, TIME_SERIES, GANTT};

	public ReportingClient() {
		if (url != null) {
			try {
				client = new ReportsClient(new URL(url));
			}
			catch (MalformedURLException ex) {
				sLog.error("Problem instaniating ReportsClient with URL provided. Default used instead.", ex);
				client = new ReportsClient();
			}
		}
		else {
			client = new ReportsClient();
		}
	}

	public byte[] generateReport(TrendsReport trendsreport, String saml) 
	throws RepositoryFault, GroupsNotAllowedException, NotAuthorisedFault {

		long reportId = trendsreport.getId();
		String format = trendsreport.getFormatType();

		try {
			//retrieve the saved reportTemplate
			ITrendsReport reportTemplate = (ITrendsReport)client.getReport(reportId, saml);

			if (reportTemplate == null) {
				sLog.warn("Trend report id "+reportId+" was not retrieved from the database");
				return null;
			}

			//retrieve the list of groups
			List<String> groupCodes = parseGroups(trendsreport.getGroups());
			List<Group> groups = getGroupsForCodes(reportTemplate.getDataSet().getId(), groupCodes, saml);

			//add the details given in the report

			for (int i = 0; i < reportTemplate.numCharts(); i++) {

				//set dates
				reportTemplate.getChart(i).setTimePeriod(trendsreport.getStartDate(), trendsreport.getEndDate());

				//showtotals
				reportTemplate.getChart(i).setShowTotals(trendsreport.isShowTotals());

				for (int j = 0; j < reportTemplate.getChart(i).numRows(); j++) {
					//add the summary type, if one has been selected otherwise use the type originally given in the report template 
					if (trendsreport.getSummaryType() != null && !trendsreport.getSummaryType().equals("default")) {
						reportTemplate.getChart(i).getRow(j).setSummaryType(trendsreport.getSummaryType());
					}

					//add the list of groups
					for (Group g: groups) {
						reportTemplate.getChart(i).addGroup(g);
					}
				}

			}

			//generate and return the byte stream in the requested format
			return client.generateDynamicTrendsReport(reportTemplate, format, saml);
		}
		catch (ConnectException cex) {
			sLog.error("Problem with connecting to the database", cex);
			throw new RepositoryFault("Problem with connecting to the database", cex);
		}
		catch (SocketTimeoutException stex) {
			sLog.error("Connection to the database timed out", stex);
			throw new RepositoryFault("Connection to the database timed out", stex);
		}
		catch (RepositoryServiceFault rsf) {
			sLog.error("Problem with the database service.", rsf);
			throw new RepositoryFault("Problem with the database service.", rsf);
		}
		catch (ReportException e) {
			sLog.error("Problem retrieving charts belonging to the database's report template.", e);
			throw new RepositoryFault("Problem retrieving charts belonging to the database's report template.", e);
		}
		catch (ReportRenderingException e) {
			sLog.error("Exception thrown when rendering the report.", e);
			throw new RepositoryFault("Exception thrown when rendering the report.", e);
		}
	}

	public byte[] generateReport(RecordReport recordreport, String saml) 
	throws RepositoryFault, GroupsNotAllowedException, NotAuthorisedFault {

		long reportId = recordreport.getId();
		String format = recordreport.getFormatType();
		String identifier = recordreport.getIdentifier();
		try {
			//retrieve the saved reportTemplate
			IRecordReport reportTemplate = (IRecordReport)client.getReport(reportId, saml);

			if (reportTemplate == null) {
				sLog.warn("Record report id "+reportId+" was not retrieved from the database");
				return null;
			}

			//generate and return the byte stream in the requested format
			return client.generateDynamicRecordReport(reportTemplate, identifier, format, saml);
		}
		catch (ConnectException cex) {
			sLog.error("Problem with connecting to the database", cex);
			throw new RepositoryFault("Problem with connecting to the database", cex);
		}
		catch (SocketTimeoutException stex) {
			sLog.error("Connection to the database timed out", stex);
			throw new RepositoryFault("Connection to the database timed out", stex);
		}
		catch (RepositoryServiceFault rsf) {
			sLog.error("Problem with the database service.", rsf);
			throw new RepositoryFault("Problem with the database service.", rsf);
		}
		catch (ReportRenderingException e) {
			sLog.error("Exception thrown when rendering the report.", e);
			throw new RepositoryFault("Exception thrown when rendering the report.", e);
		}
	}

	public byte[] generateReport(ManagementReport managementreport, String saml) 
	throws RepositoryFault, GroupsNotAllowedException, NotAuthorisedFault {

		long reportId = managementreport.getId();
		String format = managementreport.getFormatType();

		try {
			//retrieve the saved reportTemplate
			IManagementReport reportTemplate = (IManagementReport)client.getReport(reportId, saml);

			if (reportTemplate == null) {
				sLog.warn("Management report id "+reportId+" was not retrieved from the database");
				return null;
			}

			//retrieve the list of groups
			List<String> groupCodes = parseGroups(managementreport.getGroups());
			List<Group> groups = getGroupsForCodes(reportTemplate.getDataSet().getId(), groupCodes, saml);

			//add the details given in the report

			for (int i = 0; i < reportTemplate.numCharts(); i++) {
				IManagementChart chart = reportTemplate.getChart(i);

				if (chart instanceof IUKCRNSummaryChart) {
					IUKCRNSummaryChart c = (IUKCRNSummaryChart)chart;
					c.setTimePeriod(managementreport.getStartDate(), managementreport.getEndDate());

				}
				else if (chart instanceof IReceivingTreatmentChart) {
					IReceivingTreatmentChart c = (IReceivingTreatmentChart)chart;
					c.setTimePeriod(managementreport.getStartDate(), managementreport.getEndDate());
				}
				else if (chart instanceof IRecruitmentProgressChart) {
					IRecruitmentProgressChart c = (IRecruitmentProgressChart)chart;
					c.setTimePeriod(managementreport.getStartDate(), managementreport.getEndDate());
					c.setTargets(new LinkedHashMap<Calendar, Integer>());	//reset any previous targets
					for (String date: managementreport.getTargets().keySet()) {
						String[] monthAndYear = date.split(" ", 2);
						int monthNo = parseMonth(monthAndYear[0]);
						Calendar cal = new GregorianCalendar(Integer.parseInt(monthAndYear[1]), monthNo, 0);
						c.addTarget(cal, Integer.parseInt(managementreport.getTargets().get(date)));
					}
				}
				else if (chart instanceof IStdCodeStatusChart) {
					IStdCodeStatusChart c = (IStdCodeStatusChart)chart;
					DocumentBean document = managementreport.getDocument();
					for (DocumentOccurrenceBean docOcc: document.getDocOccs()) {
						c.addDocOcc(docOcc.getId());	
					}					
				}
				else if (chart instanceof IBasicStatisticsChart) {
					IBasicStatisticsChart c= (IBasicStatisticsChart)chart;
					for ( EntryBean eb: managementreport.getEntries() ){
						c.addEntryId(eb.getId());
					}
					for ( String stat: managementreport.getStatistics() ){
						c.addStatistic(stat);
					}
				}
			}

			//add the list of groups
			for (Group g: groups) {
				reportTemplate.addGroup(g);
			}

			//generate and return the byte stream in the requested format
			return client.generateDynamicManagementReport(reportTemplate, format, saml);

		}
		catch (ConnectException cex) {
			sLog.error("Problem with connecting to the database", cex);
			throw new RepositoryFault("Problem with connecting to the database", cex);
		}
		catch (SocketTimeoutException stex) {
			sLog.error("Connection to the database timed out", stex);
			throw new RepositoryFault("Connection to the database timed out", stex);
		}
		catch (RepositoryServiceFault rsf) {
			sLog.error("Problem with the database service.", rsf);
			throw new RepositoryFault("Problem with the database service.", rsf);
		}
		catch (ReportException e) {
			sLog.error("Problem retrieving charts belonging to the database's report template.", e);
			throw new RepositoryFault("Problem retrieving charts belonging to the database's report template.", e);
		}
		catch (ReportRenderingException e) {
			sLog.error("Exception thrown when rendering the report.", e);
			throw new RepositoryFault("Exception thrown when rendering the report.", e);
		}
	}

	public Map<Long, String> getReports(String dataSetCode, String type, String saml) 
	throws RepositoryFault {

		try {
			List<IReport> reports = client.getReportsOfType(dataSetCode, type, saml);

			Map<Long,String> results = new LinkedHashMap<Long,String>();
			for (IReport r: reports) {
				if (r.isTemplate()) {
					results.put(r.getId(), r.getTitle());
				}
			}
			return results;
		}
		catch (ConnectException cex) {
			sLog.error("Problem with connecting to the database", cex);
			throw new RepositoryFault("Problem with connecting to the database", cex);
		}
		catch (SocketTimeoutException stex) {
			sLog.error("Connection to the database timed out", stex);
			throw new RepositoryFault("Connection to the database timed out", stex);
		}
		catch (NotAuthorisedFault naf) {
			sLog.error("Not authorized to connect to the database.", naf);
			throw new RepositoryFault("Not authorized to connect to the database.", naf);
		}
		catch (RepositoryServiceFault rsf) {
			sLog.error("Problem with the database service.", rsf);
			throw new RepositoryFault("Problem with the database service.", rsf);
		}

	}

	public List<String> getSummaryTypesForReport(long id, String saml) 
	throws RepositoryFault {
		try {
			List<String> types = new ArrayList<String>();

			ITrendsReport report = (ITrendsReport)client.getReport(id, saml);

			if (report == null) {
				return null;
			}

			//base the possible summary types on the chart type of the first (main) chart

			//the summary type will depend on the chart type, as some combinations
			// will not make sense
			if (report.getChart(0) != null) {
				ITrendsChart chart = report.getChart(0);

				//look at the first chart type as most trends charts only use one type and 
				//there should be no difference in summary types
				String type = chart.getType(0);
				if (type != null) {

					if (type.equalsIgnoreCase(chartType.STACKED_BAR.toString())) {
						//a stacked bar chart will always collate values and display %s
						types.add("default");
					}
					else if (type.equalsIgnoreCase(chartType.GANTT.toString())) {
						//no choice, as a gantt chart will always show all values
						types.add("default");
					}
					else {

						//look at the various summary types which have been specifed for each row
						for (int j = 0; j < chart.numRows(); j++) {
							if (chart.getRow(j).getSummaryType().equalsIgnoreCase(ITrendsChartRow.SUMMARY_TYPE_MEAN)
									|| chart.getRow(j).getSummaryType().equalsIgnoreCase(ITrendsChartRow.SUMMARY_TYPE_MEDIAN)) {

								//If either the mean or median are used to summarise data, give the user other interchangeable
								//options
								types.add("default");
								types.add(ITrendsChartRow.SUMMARY_TYPE_HIGH);
								types.add(ITrendsChartRow.SUMMARY_TYPE_LOW);
								types.add(ITrendsChartRow.SUMMARY_TYPE_MEAN);
								types.add(ITrendsChartRow.SUMMARY_TYPE_MEDIAN);
								//types.add(ITrendsChartRow.SUMMARY_TYPE_ALL);
								break;
							}
						}
					}
				}

				//if nothing has been added so far, then drop back to the default
				if (types.size() == 0) {
					types.add("default");
				}
			}

			return types;
		}
		catch (ConnectException cex) {
			sLog.error("Problem with connecting to the database", cex);
			throw new RepositoryFault("Problem with connecting to the database", cex);
		}
		catch (SocketTimeoutException stex) {
			sLog.error("Connection to the database timed out", stex);
			throw new RepositoryFault("Connection to the database timed out", stex);
		}
		catch (NotAuthorisedFault naf) {
			sLog.error("Not authorized to connect to the database.", naf);
			throw new RepositoryFault("Not authorized to connect to the database.", naf);
		}
		catch (RepositoryServiceFault rsf) {
			sLog.error("Problem with the database service.", rsf);
			throw new RepositoryFault("Problem with the database service.", rsf);
		}
		catch (ReportException re) {
			sLog.error("ReportException thrown by the database service.", re);
			throw new RepositoryFault("ReportException thrown by the database service.", re);
		}
	}

	private List<Group> getGroupsForCodes(long dsId, List<String> groupCodes, String saml)
	throws RepositoryFault {

		try {
			List<Group> groups = client.getGroupsForCodes(dsId, groupCodes, saml);

			return groups;

		}
		catch (ConnectException cex) {
			sLog.error("Problem with connecting to the database", cex);
			throw new RepositoryFault("Problem with connecting to the database", cex);
		}
		catch (SocketTimeoutException stex) {
			sLog.error("Connection to the database timed out", stex);
			throw new RepositoryFault("Connection to the database timed out", stex);
		}
		catch (NotAuthorisedFault naf) {
			sLog.error("Not authorized to connect to database service.", naf);
			throw new RepositoryFault("Not authorized to connect to database service.", naf);
		}
		catch (RepositoryServiceFault rsf) {
			sLog.error("Problem with the database service.", rsf);
			throw new RepositoryFault("Problem with the database service.", rsf);
		}

	}

	public List<String> getIdentifiers(String projectCode, String saml) 
	throws RepositoryFault {

		RepositoryClient rep = new RepositoryClient();

		try {
			Date date = new Date();
			date.setYear(date.getYear()-1);
			DataSet ds = rep.getDataSetSummary(projectCode, date, saml);
			return rep.getIdentifiers(ds.getId(), saml);
		}
		catch (RepositoryNoSuchDatasetFault ds) {
			sLog.error("The study code "+projectCode+" is not recognised by the database.", ds);
			throw new RepositoryFault("The study code "+projectCode+" is not recognised by the database.", ds);
		}
		catch (ConnectException ce) {
			sLog.error("Problem with connecting to the database.", ce);
			throw new RepositoryFault("Problem with connecting to the database.", ce);
		}
		catch (SocketTimeoutException stex) {
			sLog.error("Connection to the database timed out", stex);
			throw new RepositoryFault("Connection to the database timed out", stex);
		}
		catch (RepositoryServiceFault rsf) {
			sLog.error("Problem with the database service.", rsf);
			throw new RepositoryFault("Problem with the database service.", rsf);
		}
		catch (NotAuthorisedFault naf) {
			sLog.error("Not authorized to connect to database service", naf);
			throw new RepositoryFault("Not authorized to connect to database service", naf);
		}
		catch (RemoteException re) {
			sLog.error("Remote exception occured when connected to the database service", re);
			throw new RepositoryFault("Remote exception occured when connected to the database service", re);
		}

		//return identifiers;
	}

	public String getReportType(long id, String saml) 
	throws RepositoryFault {
		try {
			IManagementReport report = (IManagementReport)client.getReport(id, saml);

			if (report == null) {
				return null;
			}

			for (int i = 0; i < report.numCharts(); i++) {
				if (report.getChart(i) instanceof IRecruitmentProgressChart) {
					return ManagementReport.RECRUITMENT;
				}
				else if (report.getChart(i) instanceof IUKCRNSummaryChart) {
					return ManagementReport.UKCRNSUMMARY;
				}
				else if (report.getChart(i) instanceof IReceivingTreatmentChart) {
					return ManagementReport.RECEIVINGTREATMENT;
				}
				else if (report.getChart(i) instanceof IRecordStatusChart || report.getChart(i) instanceof IDocumentStatusChart) {
					return ManagementReport.STATUSREPORT;
				}
				else if (report.getChart(i) instanceof ICollectionDateChart ) {
					return ManagementReport.DATEREPORT;
				}
				else if (report.getChart(i) instanceof IStdCodeStatusChart ) {
					return ManagementReport.DOCUMENTREPORT;
				}
				else if (report.getChart(i) instanceof IBasicStatisticsChart ) {
					return ManagementReport.BASICSTATSREPORT;
				}
			}
		}
		catch(Exception e) {
			sLog.error("Problem when retrieving report type", e);
			throw new RepositoryFault("Problem when retrieving report type", e);
		}
		return null;
	}

	/**
	 * 
	 * @param dataSetId
	 * @param saml
	 * @return
	 * @throws RepositoryFault
	 */
	public List<DocumentBean> getDocuments(String projectCode, String saml) 
	throws RepositoryFault {

		try {
			List<DocumentBean> beans = new ArrayList<DocumentBean>();	
			List<Document> documents = client.getDocuments(projectCode, saml);

			for (Document document: documents) {
				DocumentBean bean = new DocumentBean(document.getId(), document.getDisplayText());

				for (int i=0, c=document.numOccurrences(); i<c; i++ ) {
					bean.addDocOcc(new DocumentOccurrenceBean(document.getOccurrence(i)));
				}

				beans.add(bean);
			}

			return beans;
		}
		catch (ConnectException cex) {
			sLog.error("Problem with connecting to the database", cex);
			throw new RepositoryFault("Problem with connecting to the database", cex);
		}
		catch (SocketTimeoutException stex) {
			sLog.error("Connection to the database timed out", stex);
			throw new RepositoryFault("Connection to the database timed out", stex);
		}
		catch (NotAuthorisedFault naf) {
			sLog.error("Not authorized to connect to the database.", naf);
			throw new RepositoryFault("Not authorized to connect to the database.", naf);
		}
		catch (RepositoryServiceFault rsf) {
			sLog.error("Problem with the database service.", rsf);
			throw new RepositoryFault("Problem with the database service.", rsf);
		}

	}

	/**
	 * 
	 * @param dataSetId
	 * @param saml
	 * @return
	 * @throws RepositoryFault
	 */
	public List<EntryBean> getEntries(String projectCode, long documentId, String saml) 
	throws RepositoryFault {

		try {
			
			MinimalEntry[] mes = client.getEntries(projectCode, documentId, saml);
			List<EntryBean> beans = new ArrayList<EntryBean>();
			for ( MinimalEntry me: mes ){
				beans.add(new EntryBean(me.getId(), me.getDisplayText()));
			}
			
			return beans;
		}
		catch (ConnectException cex) {
			sLog.error("Problem with connecting to the database", cex);
			throw new RepositoryFault("Problem with connecting to the database", cex);
		}
		catch (SocketTimeoutException stex) {
			sLog.error("Connection to the database timed out", stex);
			throw new RepositoryFault("Connection to the database timed out", stex);
		}
		catch (NotAuthorisedFault naf) {
			sLog.error("Not authorized to connect to the database.", naf);
			throw new RepositoryFault("Not authorized to connect to the database.", naf);
		}
		catch (RepositoryServiceFault rsf) {
			sLog.error("Problem with the database service.", rsf);
			throw new RepositoryFault("Problem with the database service.", rsf);
		}

	}

	/**
	 * The list of groups are stored as strings "code=name" in the local report.
	 * E.g "001001=South Manchester". This method extracts the group codes and 
	 * returns them as a list.
	 * 
	 * @param groups
	 * @return codes
	 */
	private List<String> parseGroups(List<String> groups) {
		List<String> codes = new ArrayList<String>();

		for (String g: groups) {
			try {
				String code[] = g.split("=", 2);
				codes.add(code[0]);
			}
			catch (Exception e) {
				codes.add(g);
			}
		}
		return codes;
	}

	/**
	 * Convert the named month into an integer, where January is 0 and 
	 * December is 11, suitable for populating a Calendar instance.
	 * 
	 * @param month
	 * @return month
	 */
	private int parseMonth(String month) {
		List<String> months = ReportHelper.getMonths();

		for (int j = 0; j < months.size(); j++) {
			if (months.get(j).equalsIgnoreCase(month)) {
				return j;
			}
		}
		return 0;
	}

}
