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

package org.opencdms.web.modules.reports.repository;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencdms.web.modules.reports.models.MgmtReportModel;
import org.opencdms.web.modules.reports.models.MgmtReportModel.MonthlyTarget;
import org.opencdms.web.modules.reports.models.RecordReportModel;
import org.opencdms.web.modules.reports.models.TrendReportModel;
import org.psygrid.data.model.dto.extra.IdentifierData;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Entry;
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
import org.psygrid.data.reporting.definition.hibernate.BasicStatisticsChart;
import org.psygrid.data.reporting.definition.hibernate.CollectionDateChart;
import org.psygrid.data.reporting.definition.hibernate.DocumentStatusChart;
import org.psygrid.data.reporting.definition.hibernate.GroupsSummaryChart;
import org.psygrid.data.reporting.definition.hibernate.ManagementReport;
import org.psygrid.data.reporting.definition.hibernate.ReceivingTreatmentChart;
import org.psygrid.data.reporting.definition.hibernate.RecordStatusChart;
import org.psygrid.data.reporting.definition.hibernate.RecruitmentProgressChart;
import org.psygrid.data.reporting.definition.hibernate.StdCodeStatusChart;
import org.psygrid.data.reporting.definition.hibernate.UserSummaryChart;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.www.xml.security.core.types.GroupType;

public class ReportingClient implements Reporting {

	private ReportsClient client;

	private static Log sLog = LogFactory.getLog(ReportingClient.class);

	//possible trends chart types - based on types in org.psygrid.data.reporting.Chart
	public enum chartType {TABLE, BAR, BAR_HZ, PIE, LINE, LINE_HZ, STACKED_BAR, TIME_SERIES, GANTT};

	public ReportingClient() {
			client = new ReportsClient();
	}

	public byte[] generateReport(TrendReportModel trendsreport, String saml) 
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, ReportException, ReportRenderingException, GroupsNotAllowedException, NotAuthorisedFault {
		long reportId = trendsreport.getReport().getReportId();
		String format = trendsreport.getFormat();

		//retrieve the saved reportTemplate
		ITrendsReport reportTemplate = (ITrendsReport)client.getReport(reportId, saml);

		if (reportTemplate == null) {
			sLog.warn("Trend report id "+reportId+" was not retrieved from the database");
			return null;
		}

		//retrieve the list of groups
		List<String> groupCodes = getCentreCodes(trendsreport.getCentres());
		List<Group> groups = getGroupsForCodes(reportTemplate.getDataSet().getId(), groupCodes, saml);

		//add the details given in the report
		Calendar startDate = trendsreport.getStartDate().getDate();
		Calendar endDate = trendsreport.getEndDate().getDate();
		boolean showTotals = false;
		if ( trendsreport.getShowTotals().equals("Yes") ){
			showTotals = true;
		}
		for (int i = 0; i < reportTemplate.numCharts(); i++) {

			//set dates
			reportTemplate.getChart(i).setTimePeriod(startDate, endDate);

			//showtotals
			reportTemplate.getChart(i).setShowTotals(showTotals);

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

	public byte[] generateReport(RecordReportModel recordreport, String saml) 
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, 
	ReportRenderingException, GroupsNotAllowedException, NotAuthorisedFault {

		long reportId = recordreport.getReport().getReportId();
		String format = recordreport.getFormat();
		String identifier = recordreport.getIdentifier();
		//retrieve the saved reportTemplate
		IRecordReport reportTemplate = (IRecordReport)client.getReport(reportId, saml);

		if (reportTemplate == null) {
			sLog.warn("Record report id "+reportId+" was not retrieved from the database");
			return null;
		}

		//generate and return the byte stream in the requested format
		return client.generateDynamicRecordReport(reportTemplate, identifier, format, saml);
	}
	
	private void clearCentreInfoFromManagementReport(IManagementReport r) throws ReportException{
		
		List<Group> emptyGroupsList = new ArrayList<Group>();
		List<Group> emptyGroupsList2 = new ArrayList<Group>();
		
		ManagementReport mR = (ManagementReport) r;
		mR.setGroups(emptyGroupsList);
		
		int numCharts = r.numCharts();
		for(int i = 0; i < numCharts; i++){
			IManagementChart ch = r.getChart(i);
			if(ch instanceof BasicStatisticsChart){
				BasicStatisticsChart bsc = (BasicStatisticsChart)ch;
				bsc.setGroups(emptyGroupsList);
				
			}else if(ch instanceof CollectionDateChart){
				CollectionDateChart cdc = (CollectionDateChart) ch;
				cdc.setGroups(emptyGroupsList2);
				
			}else if(ch instanceof DocumentStatusChart){
				DocumentStatusChart dsc = (DocumentStatusChart)ch;
				dsc.setGroups(emptyGroupsList2);
				
			}else if(ch instanceof GroupsSummaryChart){
				GroupsSummaryChart gsc = (GroupsSummaryChart)ch;
				gsc.setGroups(emptyGroupsList);
				
			}else if(ch instanceof ReceivingTreatmentChart){
				ReceivingTreatmentChart rtc = (ReceivingTreatmentChart)ch;
				rtc.setGroups(emptyGroupsList2);
				
			}else if(ch instanceof RecordStatusChart){
				RecordStatusChart rst = (RecordStatusChart)ch;
				rst.setGroups(emptyGroupsList2);
				
			}else if(ch instanceof RecruitmentProgressChart){
				RecruitmentProgressChart rpc = (RecruitmentProgressChart)ch;
				rpc.setGroups(emptyGroupsList2);
				
			}else if(ch instanceof StdCodeStatusChart){
				StdCodeStatusChart scsc = (StdCodeStatusChart)ch;
				scsc.setGroups(emptyGroupsList2);
				
			}else if(ch instanceof UserSummaryChart){
				UserSummaryChart usc = (UserSummaryChart)ch;
				usc.setGroups(emptyGroupsList);
				
			}
			
		}
		
	}

	public byte[] generateReport(MgmtReportModel managementreport, String saml) 
	throws ConnectException, SocketTimeoutException, RepositoryServiceFault, ReportException, ReportRenderingException, GroupsNotAllowedException, NotAuthorisedFault {

		long reportId = managementreport.getReport().getReportId();
		String format = managementreport.getFormat();

		//retrieve the saved reportTemplate
		IManagementReport reportTemplate = (IManagementReport)client.getReport(reportId, saml);
		
		//We need to clear out the groups specified for the charts in this 'reportTemplate'. Otherwise the groups included in the
		//report won't match the ones the users have requested.
		this.clearCentreInfoFromManagementReport(reportTemplate);

		if (reportTemplate == null) {
			sLog.warn("Management report id "+reportId+" was not retrieved from the database");
			return null;
		}

		//retrieve the list of groups
		List<String> groupCodes = getCentreCodes(managementreport.getCentres());
		List<Group> groups = getGroupsForCodes(reportTemplate.getDataSet().getId(), groupCodes, saml);

		//add the details given in the report

		for (int i = 0; i < reportTemplate.numCharts(); i++) {
			IManagementChart chart = reportTemplate.getChart(i);

			if (chart instanceof IUKCRNSummaryChart) {
				IUKCRNSummaryChart c = (IUKCRNSummaryChart)chart;
				c.setTimePeriod(
						managementreport.getStartDate().getDate(), 
						managementreport.getEndDate().getDate());

			}
			else if (chart instanceof IReceivingTreatmentChart) {
				IReceivingTreatmentChart c = (IReceivingTreatmentChart)chart;
				c.setTimePeriod(
						managementreport.getStartDate().getDate(), 
						managementreport.getEndDate().getDate());
			}
			else if (chart instanceof IRecruitmentProgressChart) {
				IRecruitmentProgressChart c = (IRecruitmentProgressChart)chart;
				c.setTimePeriod(
						managementreport.getStartDate().getDate(), 
						managementreport.getEndDate().getDate());
				c.setTargets(new LinkedHashMap<Calendar, Integer>());	//reset any previous targets
				if ( managementreport.getTargetType().equals("All months") ){
					managementreport.initializeTargets(managementreport.getAllMonths());
					
					for (MonthlyTarget mt: managementreport.getPerMonth()){
						c.addTarget(mt.getCal(), mt.getTarget());
					}
					
				}else{ //Targets have been specified 'Per Month'
					//Users specify the monthly targets. We need to transform them so that they are cumulative.
					int cumulativeTarget = 0;
					for (MonthlyTarget mt: managementreport.getPerMonth()){
						cumulativeTarget += mt.getTarget().intValue();
						c.addTarget(mt.getCal(), cumulativeTarget);
					}
					
					
				}
				
				

			}
			else if (chart instanceof IStdCodeStatusChart) {
				IStdCodeStatusChart c = (IStdCodeStatusChart)chart;
				c.addDocOcc(managementreport.getDocument().getId());
			}
			else if (chart instanceof IBasicStatisticsChart) {
				IBasicStatisticsChart c= (IBasicStatisticsChart)chart;
				for ( Entry eb: managementreport.getEntries() ){
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

	public Map<Long, String> getReports(String dataSetCode, String type, String saml) 
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, RepositoryServiceFault {

		List<IReport> reports = client.getReportsOfType(dataSetCode, type, saml);

		Map<Long,String> results = new LinkedHashMap<Long,String>();
		for (IReport r: reports) {
			if (r.isTemplate()) {
				results.put(r.getId(), r.getTitle());
			}
		}
		return results;
	}

	public List<String> getSummaryTypesForReport(long id, String saml) 
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, RepositoryServiceFault, ReportException {
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

	private List<Group> getGroupsForCodes(long dsId, List<String> groupCodes, String saml)
	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, RepositoryServiceFault {

		return client.getGroupsForCodes(dsId, groupCodes, saml);

	}

	public List<String> getIdentifiers(String projectCode, String saml) 
	throws RepositoryNoSuchDatasetFault, ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, NotAuthorisedFault, RemoteException {

		RepositoryClient rep = new RepositoryClient();
		Date date = new Date(0);
		DataSet ds = rep.getDataSetSummary(projectCode, date, saml);
		return rep.getIdentifiers(ds.getId(), saml);
	}

	public Map<String,String> getIdentifierMap(String projectCode, String saml) 
	throws RepositoryNoSuchDatasetFault, ConnectException, SocketTimeoutException, 
	RepositoryServiceFault, NotAuthorisedFault, RemoteException {

		RepositoryClient rep = new RepositoryClient();
		Date date = new Date(0);
		DataSet ds = rep.getDataSetSummary(projectCode, date, saml);
		IdentifierData[] identifiers = rep.getIdentifiersExtended(ds.getId(), saml);
		Map<String,String> results = new LinkedHashMap<String,String>();
		for(IdentifierData id:identifiers){
			String identifier = id.getIdentifier();
			String externalID = id.getExternalId();
			String displayID = id.getUseExternalID()?externalID:identifier;
			results.put(identifier, displayID);
		}
		return results;
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
					return MgmtReportModel.RECRUITMENT;
				}
				else if (report.getChart(i) instanceof IUKCRNSummaryChart) {
					return MgmtReportModel.UKCRNSUMMARY;
				}
				else if (report.getChart(i) instanceof IReceivingTreatmentChart) {
					return MgmtReportModel.RECEIVINGTREATMENT;
				}
				else if (report.getChart(i) instanceof IRecordStatusChart || report.getChart(i) instanceof IDocumentStatusChart) {
					return MgmtReportModel.STATUSREPORT;
				}
				else if (report.getChart(i) instanceof ICollectionDateChart ) {
					return MgmtReportModel.DATEREPORT;
				}
				else if (report.getChart(i) instanceof IStdCodeStatusChart ) {
					return MgmtReportModel.DOCUMENTREPORT;
				}
				else if (report.getChart(i) instanceof IBasicStatisticsChart ) {
					return MgmtReportModel.BASICSTATSREPORT;
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
	/*
	public List<DocumentBean> getDocuments(String projectCode, String saml) 
	throws RepositoryFault {
		try {
			List<DocumentBean> beans = new ArrayList<DocumentBean>();	
			List<IDocument> documents = client.getDocuments(projectCode, saml);

			for (IDocument document: documents) {
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
	*/

	/**
	 * 
	 * @param dataSetId
	 * @param saml
	 * @return
	 * @throws RepositoryFault
	 */
	/*
	public List<EntryBean> getEntries(String projectCode, long documentId, String saml) 
	throws RepositoryFault {
		return null;
		/*
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
	*/
	
	/**
	 * Extract a list of centre codes from a list of centres (GroupTypes)
	 * 
	 * @param groups
	 * @return codes
	 */
	private List<String> getCentreCodes(List<GroupType> groups) {
		List<String> codes = new ArrayList<String>();

		for (GroupType g: groups) {
			codes.add(g.getIdCode());
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
	/*
	private int parseMonth(String month) {
		List<String> months = ReportHelper.getMonths();

		for (int j = 0; j < months.size(); j++) {
			if (months.get(j).equalsIgnoreCase(month)) {
				return j;
			}
		}
		return 0;
	}
	*/
}
