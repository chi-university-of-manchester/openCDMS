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


package org.psygrid.web.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.reporting.definition.hibernate.BasicStatisticsChart;
import org.psygrid.web.beans.DocumentBean;
import org.psygrid.web.beans.EntryBean;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.forms.Hub;
import org.psygrid.web.forms.ManagementReport;
import org.psygrid.web.forms.RecordReport;
import org.psygrid.web.forms.Report;
import org.psygrid.web.forms.TrendsReport;
import org.psygrid.web.forms.Trust;
import org.psygrid.web.repository.ReportingClient;
import org.psygrid.web.repository.RepositoryFault;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Helper class used by ReportGenerationController when 
 * compiling the report to be generated.
 *  
 * @author Lucy Bridges
 *
 */
public class ReportHelper {

	private static final Log log = LogFactory.getLog(ReportHelper.class);
	
	
	/**
	 * Get a list of possible report titles and ids 
	 * from the list held by the repository
	 * 
	 * @param datasetId the id of the dataset
	 * @param type the type of reports to be found
	 * @return titles
	 */
	public static Map<Long,String> getTitles(String datasetId, String type) {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String saml = SamlHelper.getSaAsString(user.getSaml());

		Map<Long,String> titles = new HashMap<Long,String>();	
		ReportingClient client  = new ReportingClient();

		try {
			return client.getReports(datasetId, type, saml);
		}
		catch (RepositoryFault rsf) {
			log.error("Error thrown in report helper when retrieving list of reports.", rsf);
		}

		return titles;	
	}

	/**
	 * Retrieve the potential summary types for the trends
	 * report provided
	 * 
	 * @return types
	 */
	public static List<String> getSummaryTypes(long reportId) {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String saml = SamlHelper.getSaAsString(user.getSaml());

		List<String> types = new ArrayList<String>();

		ReportingClient client = new ReportingClient();

		try {
			return client.getSummaryTypesForReport(reportId, saml);
		}
		catch (RepositoryFault rsf) {
			log.error("Error thrown in report helper when retrieving summary types.", rsf);
		}

		return types;
	}

	/**
	 * Retrieve a list of formats that can be used to 
	 * render the report. 
	 * 
	 * @return formatTypes
	 */
	public static List<String> getFormatTypes() {
		List<String> types = new ArrayList<String>();
		types.add("pdf");
		types.add("xls");
		types.add("csv");
		return types;
	}

	/**
	 * Retrieve the user friendly project name for a given
	 * project code
	 * 
	 * @param projectCode
	 * @return projectName
	 */
	public static String getProjectName(String projectCode) {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		for (ProjectType t: user.getProjects()) {
			if (t.getIdCode().equals(projectCode)) {
				return t.getName();
			}
		}

		return "";
	}

	/**
	 * Retrieve the list of groups the user has access to from the AA,
	 * which is split into trusts and hubs. Used to select the groups 
	 * to feature in the report.
	 * 
	 * Returns a list of groups split into hubs and trusts.
	 * 
	 * @return hubs
	 */
	public static List<Hub> getGroups(String projectCode) {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		List<Hub> hubs = new ArrayList<Hub>();

		ProjectType projectType = new ProjectType(null, projectCode, null, null, false);
		Set<ProjectType> projects = user.getGroups().keySet();		
		for (ProjectType p: projects) {
			if (p.getIdCode().equals(projectCode)) {
				projectType = p; 
			}
		}
		if (user.getGroups() != null && user.getGroups().containsKey(projectType)) {
					
			for (GroupType g: user.getGroups().get(projectType)) {
				String groupCode = g.getIdCode();
				String hubCode   = null;
				String trustCode = null;
				
				if (groupCode != null) {
					hubCode   = groupCode.substring(0, 3);
					trustCode = groupCode.substring(3, 6);
				
					
					boolean found = false;
					for (Hub h: hubs) {
						if (h.getCode().equals(hubCode)) {
							h.addTrust(new Trust(trustCode, g.getName()));
							found = true;
						}
					}
					
					if (!found) {
						String hubName = "";
						try {
							hubName = g.getName().split("-", 2)[0];
						}
						catch (Exception e) {
							hubName = hubCode;
						}
						
						Hub hub1 = new Hub(hubCode, hubName);
						List<Trust> trusts = new ArrayList<Trust>();
						trusts.add(new Trust(trustCode, g.getName()));
						hub1.setTrusts(trusts);
						hubs.add(hub1);
					}
				}
			}
		}


		// code for testing only, should be removed
		/*Hub hub1 = new Hub("001", "Manchester");
		List<Trust> trusts = new ArrayList<Trust>();
		trusts.add(new Trust("001", "South Manchester"));
		hub1.setTrusts(trusts);
		hubs.add(hub1);

		Hub hub2 = new Hub("002", "Birmingham");
		List<Trust> trusts2 = new ArrayList<Trust>();
		trusts2.add(new Trust("001", "North Birmingham"));
		trusts2.add(new Trust("002", "East Birmingham"));
		hub2.setTrusts(trusts2);
		hubs.add(hub2); */



		return hubs;
	}

	public static List<String> getIdentifiers(String projectCode) {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String saml = SamlHelper.getSaAsString(user.getSaml());

		ReportingClient client = new ReportingClient();

		try {
			return client.getIdentifiers(projectCode, saml);
		}
		catch (RepositoryFault rsf) {
			log.error("Error thrown in report helper when retrieving list of participant identifiers.", rsf);
		}
		return new ArrayList<String>();
	}

	/**
	 * Get a hard coded list of months
	 * 
	 * @return months
	 */
	public static List<String> getMonths() {
		List<String> months = new ArrayList<String>();
		months.add("January");
		months.add("February");
		months.add("March");
		months.add("April");
		months.add("May");
		months.add("June");
		months.add("July");
		months.add("August");
		months.add("September");
		months.add("October");
		months.add("November");
		months.add("December");
		return months;
	}

	/**
	 * Get a list of years, as strings, starting from the
	 * beginning of National Eden (2005) until the current year.
	 * 
	 * @return years
	 */
	public static List<String> getYears() {
		List<String> years = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		for (int i = 2005; i <= cal.get(Calendar.YEAR); i++ ) {
			years.add(Integer.toString(i));
		}
		return years;
	}

	public static String currentMonth() {
		Calendar cal = Calendar.getInstance();
		return getMonths().get(cal.get(Calendar.MONTH));
	}

	public static String currentYear() {
		Calendar cal = Calendar.getInstance();
		return Integer.toString(cal.get(Calendar.YEAR));
	}
	
	public static byte[] generateReport(Report report) throws Exception {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String saml = SamlHelper.getSaAsString(user.getSaml());

		ReportingClient client = new ReportingClient();

		try {
			if (report instanceof TrendsReport) {
				return client.generateReport((TrendsReport)report, saml);
			}
			else if (report instanceof RecordReport) {
				return client.generateReport((RecordReport)report, saml);
			}
			else if (report instanceof ManagementReport) {
				return client.generateReport((ManagementReport)report, saml);
			}
		}
		catch (RepositoryFault rsf) {
			log.error("Error occured in ReportHelper when generating the report "+report.getTitle(), rsf);
			throw new Exception("Error occured in ReportHelper when generating the report "+report.getTitle(), rsf);
		}

		return null;
	}
	
	/**
	 * Get the type of management report specified by the report id.
	 * Will return null if not a UKCRN, recruitment progress or a
	 * receiving treatment report.
	 * 
	 * @param reportId
	 * @return type
	 */
	public static String getReportType(long reportId) {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String saml = SamlHelper.getSaAsString(user.getSaml());

		ReportingClient client = new ReportingClient();

		try {
			String type = client.getReportType(reportId, saml);
			log.info("Report type="+type);
			return type;
		}
		catch (RepositoryFault rsf) {
			log.error("Error thrown in report helper when retrieving report type.", rsf);
		}
		return null;
	}
	
	/**
	 * Get a Map of dates and default targets for a given period
	 * 
	 * @param start
	 * @param end
	 * @return targets
	 */
	public static Map<String,String> getMonthlyTargets(Calendar startDate, Calendar endDate) {
			
		Map<String,String> targets = new LinkedHashMap<String,String>();
		List<String> months = getMonths();
		
		if (startDate == null || endDate == null) {
			return targets;
		}
		
		Calendar start = (Calendar)startDate.clone();
		Calendar end   = (Calendar)endDate.clone();
		
		while (start.before(end) || start.equals(end)) {
			targets.put(months.get(start.get(Calendar.MONTH))+" "+start.get(Calendar.YEAR), "0");
			start.add(Calendar.MONTH, 1);
		}
		
		return targets;
	}
	
	/**
	 * Get a list of documents held by the repository
	 * for a given dataset
	 * 
	 * @param datasetId the id of the dataset
	 * @return documents
	 */
	public static List<DocumentBean> getDocuments(String datasetId) {
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String saml = SamlHelper.getSaAsString(user.getSaml());

			
		ReportingClient client  = new ReportingClient();

		try {
			return client.getDocuments(datasetId, saml);
		}
		catch (RepositoryFault rsf) {
			log.error("Error thrown in report helper when retrieving list of reports.", rsf);
		}

		return new ArrayList<DocumentBean>();	
	}
	
	public static List<EntryBean> getEntries(String datasetId, long documentId){
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String saml = SamlHelper.getSaAsString(user.getSaml());

			
		ReportingClient client  = new ReportingClient();

		try {
			return client.getEntries(datasetId, documentId, saml);
		}
		catch (RepositoryFault rsf) {
			log.error("Error thrown in report helper when retrieving list of reports.", rsf);
		}

		return new ArrayList<EntryBean>();	
	}
	
	public static List<String> getStatTypes(){
		List<String> statTypes = new ArrayList<String>();
		statTypes.add(BasicStatisticsChart.STAT_MEAN);
		statTypes.add(BasicStatisticsChart.STAT_MEDIAN);
		statTypes.add(BasicStatisticsChart.STAT_MODE);
		statTypes.add(BasicStatisticsChart.STAT_MIN);
		statTypes.add(BasicStatisticsChart.STAT_MAX);
		return statTypes;
	}
}
