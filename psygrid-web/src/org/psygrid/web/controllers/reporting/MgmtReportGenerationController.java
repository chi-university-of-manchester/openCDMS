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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.web.beans.DocumentBean;
import org.psygrid.web.beans.DocumentOccurrenceBean;
import org.psygrid.web.beans.EntryBean;
import org.psygrid.web.beans.ErrorBean;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.forms.Hub;
import org.psygrid.web.forms.ManagementReport;
import org.psygrid.web.helpers.ModelHelper;
import org.psygrid.web.helpers.ReportHelper;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller used by the report generation jsps within reporting/
 * of the PsyGrid clinical website, to generate a MANAGEMENT report.
 * <p>
 * Presents a series of forms used by the user to populate a Report
 * object, which is used during processFinish(..) to generate the
 * report.
 * 
 * @author Lucy Bridges
 *
 */
public class MgmtReportGenerationController extends AbstractWizardFormController {

	@SuppressWarnings("unused")
	private static final Log LOG = LogFactory.getLog(MgmtReportGenerationController.class);
	
	public MgmtReportGenerationController() {
		//If new pages are included check the overridden getTargetPage method for changes
		setPages(new String[] { "managementreport", 
								"reporting/management1", 
								"reporting/management2", 
								"reporting/management3", 
								"reporting/management4", 
								"reporting/management5", 
								"reporting/management6", 
								"reporting/management7", 
								"reporting/management8", 
								"reporting/management9", 
								"reporting/management10", 
								"reporting/generatereport" });
		this.setAllowDirtyBack(true);
	}

	protected void onBind(HttpServletRequest request, Object command,
			BindException errors) {

		ManagementReport report = (ManagementReport) command;

		if (request.getParameter("title") != null) {
			String[] idAndTitle = request.getParameter("title").split("=", 2);
			report.setId(new Long(idAndTitle[0]));
			report.setTitle(idAndTitle[1]);

			//determine type of report now that report has been chosen
			report.setManagementType(ReportHelper.getReportType(report.getId()));
		}

		//Format and set the start date in time for validation
		if (request.getParameter("startMonth") != null 
				&& request.getParameter("startYear") != null) {
			List<String> months = ReportHelper.getMonths();
			Calendar startDate = Calendar.getInstance();
			startDate.clear();
			for (int i = 0; i < months.size(); i++) {
				if (request.getParameter("startMonth").equals(months.get(i))) {
					startDate.set(Calendar.MONTH, i);
					break;
				}
			}
			startDate.set(Calendar.YEAR, Integer.parseInt(request.getParameter("startYear")));
			startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
			report.setStartDate(startDate);
		}

		//Format and set the end date in time for validation
		if (request.getParameter("endMonth") != null 
				&& request.getParameter("endYear") != null) {
			List<String> months = ReportHelper.getMonths();
			Calendar endDate = Calendar.getInstance();
			endDate.clear();
			for (int i = 0; i < months.size(); i++) {
				if (request.getParameter("endMonth").equals(months.get(i))) {
					endDate.set(Calendar.MONTH, i);
					break;
				}
			}
			endDate.set(Calendar.YEAR, Integer.parseInt(request.getParameter("endYear")));
			endDate.setTimeZone(TimeZone.getTimeZone("GMT"));
			report.setEndDate(endDate);
		}

		//Process the list of monthly recruitment targets
		if (request.getParameter("newtargets") != null) {
			Map<String,String> targets = new LinkedHashMap<String,String>();

			if (request.getParameter("newtargets").equals("all")) {
				String target = request.getParameter("alltargets");

				//Retrieve the list of dates used in the correct string format
				Map<String,String> template = ReportHelper.getMonthlyTargets(report.getStartDate(), report.getEndDate());
				if (target != null) {
					for (String month: template.keySet()) {
						targets.put(month, target);
					}
				}
			}
			else if (request.getParameter("newtargets").equals("monthly")) {
				//Retrieve the list of dates used in the correct string format
				Map<String,String> template = ReportHelper.getMonthlyTargets(report.getStartDate(), report.getEndDate());
				for (String month: template.keySet()) {
					String target = request.getParameter("monthlytarget-"+month);
					if (target != null) {
						targets.put(month,target);
					}
				}
			}
			report.setTargets(targets);
		}
		
		if (request.getParameter("documentid") != null) {
			
			Long id = Long.parseLong(request.getParameter("documentid"));
			for (DocumentBean document: report.getPotentialDocuments()) {
				if (id.compareTo(document.getId()) == 0) {
					report.setDocument(document);
				}
			}
		}
		
		if ( request.getParameter("dococcid") != null){
			Long id = Long.parseLong(request.getParameter("dococcid"));
			for (DocumentOccurrenceBean occ: report.getPotentialOccurrences()) {
				if (id.compareTo(occ.getId()) == 0) {
					report.setDocOcc(occ);
				}
			}
		}
		
	}

	@Override
	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors, int page) throws Exception {
		ManagementReport report = (ManagementReport)command;
		if ( 7 == page ){
			//process the entry ids into a list of the selected entries
			report.clearEntries();
			for ( String entryId: report.getEntryIds() ){
				for ( EntryBean eb: report.getPotentialEntries() ){
					if ( Long.toString(eb.getId()).equals(entryId) ){
						report.addEntry(eb);
						break;
					}
				}
			}
		}		
	}

	public Object formBackingObject(HttpServletRequest request) throws ServletException {
		ManagementReport report = new ManagementReport();

		/* The backing object is set up here, with data for the initial values
		 * of the forms fields. This could either be hard-coded, or retrieved from a
		 * a parameter, eg. request.getParameter()
		 */
		report.setType("Management");

		return report;
	}


	protected void validatePage(Object command, Errors errors, int page) {

		/*
		 * Note: This method is re-called for every page on form finish 
		 */

		ManagementReport report = (ManagementReport) command;	 

		//check that some groups have been selected
		if (page == 2 && report.getGroups().size() == 0
				&& showGroups(report.getManagementType())) {
			errors.rejectValue("groups", "error.report.groups");
		}

		//check that the dates are valid
		if (page == 3 && report.getStartDate() != null && report.getEndDate() != null) {
			ReportValidator validator = (ReportValidator) getValidator();
			validator.checkDates(report.getStartDate().get(Calendar.MONTH), report.getStartDate().get(Calendar.YEAR), report.getEndDate().get(Calendar.MONTH), report.getEndDate().get(Calendar.YEAR), errors);
		}

		//check that the correct number of targets have been entered
		if (page == 4 && showTargets(report.getManagementType())) {
			if (report.getTargets().size() < ReportHelper.getMonthlyTargets(report.getStartDate(), report.getEndDate()).size()) {
				errors.rejectValue("targets", "error.report.targets");
			}
		}
		
		//check that at least one entry has been selected
		if (page == 7 && showEntries(report.getManagementType())) {
			if ( 0 == report.getEntries().size() ) {
				errors.rejectValue("entryIds", "error.report.entries");
			}
		}
		
		//check that at least one stat has been selected
		if (page == 8 && showStatTypes(report.getManagementType())) {
			if ( 0 == report.getStatistics().size() ) {
				errors.rejectValue("statistics", "error.report.statistics");
			}
		}
		
	}

	protected ModelAndView processFinish(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors) {

		ManagementReport report = (ManagementReport) command;
		Map<String, Object> model = ModelHelper.getTemplateModel();
		byte[] data = null;
		String fileName = "report"+report.getId()+"."+report.getFormatType();

		//generate report here
		try {
			data = ReportHelper.generateReport(report);
		}
		catch (org.psygrid.data.reporting.GroupsNotAllowedException e) {
			model.put("error", "You do not have access to all of the centres required to be able to view this report.\nPlease contact PsyGrid support if you require further information.");
			ErrorBean eb = new ErrorBean(e);
			eb.setTitle("You do not have access to all of the centres required to be able to view this report.\nPlease contact <a href='mailto:support@psygrid.org'>PsyGrid support</a> if you require further information.");
			model.put("exception", eb);
			return new ModelAndView("error", model);
		}
		catch (NotAuthorisedFault e) {
			model.put("error", "You do not have permission to view this report.");
			ErrorBean eb = new ErrorBean(e);
			eb.setTitle("You do not have permission to view this report.");
			model.put("exception", eb);
			return new ModelAndView("error", model);
		}
		catch (Exception e) {
			model.put("error", "An error occurred when generating the report. The error was: "+e.getMessage());
			ErrorBean eb = new ErrorBean(e);
			model.put("exception", eb);
			return new ModelAndView("error", model);
		}

		if (data == null) {
			model.put("error", "An error occurred when generating the report. The generated report was null");
			return new ModelAndView("error", model);
		}

		model.put("fileName", fileName);
		model.put("fileData", data);

		if (report.getFormatType().equalsIgnoreCase("pdf")) {
			model.put("contentType", "application/pdf");
		}
		else {
			model.put("contentType", "application/octet-stream"); //vnd.ms-excel?
		}

		return new ModelAndView("zip", model);
	}

	protected ModelAndView processCancel(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors) {
		return new ModelAndView(new RedirectView("managementreport"));
	}


	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors, int page)
	throws Exception {
		ManagementReport report = (ManagementReport) command;
		Map<String, Object> data = ModelHelper.getTemplateModel();
		
		if (page == 0) {
			PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			data.put("projects", user.getProjects());
			
			//Remove all fields, in case the back button was used and a report was previously selected
			//See Bug #844
			data.remove("titles");
			data.remove("hubs");
			data.remove("months");
			data.remove("years");
			data.remove("currentMonth");
			data.remove("currentYear");
			data.remove("targets");
			data.remove("documents");
			data.remove("occurrences");
			data.remove("entries");
			data.remove("statTypes");
			data.remove("formatTypes");
		}
		else if (page == 1) {
			//Remove any management report specific fields, in case the back button has been
			//used and a report was previously selected. See Bug #844
			report.clearManagementFields();
			//for pretty display to the user, retrieve the dataset name for the given identifier
			report.setDataset(ReportHelper.getProjectName(report.getDatasetId()));
			//is this the best way to access the report helper?
			data.put("titles", ReportHelper.getTitles(report.getDatasetId(), "management"));
			
			//Remove all fields that have yet to be set, in case the back button was used and a 
			//report was previously selected. See Bug #844
			data.remove("hubs");
			data.remove("months");
			data.remove("years");
			data.remove("currentMonth");
			data.remove("currentYear");
			data.remove("targets");
			data.remove("documents");
			data.remove("occurrences");
			data.remove("entries");
			data.remove("statTypes");
			data.remove("formatTypes");
		}
		else if (page == 2) {
			//Remove all fields that have yet to be set, in case the back button was used.
			data.remove("hubs");
			data.remove("months");
			data.remove("years");
			data.remove("currentMonth");
			data.remove("currentYear");
			data.remove("targets");
			data.remove("documents");
			data.remove("occurrences");
			data.remove("entries");
			data.remove("statTypes");
			data.remove("formatTypes");
			
			if ( showGroups(report.getManagementType()) ) {
				List<Hub> hubs = ReportHelper.getGroups(report.getDatasetId());
				data.put("hubs", hubs);
			}
		}
		else if (page == 3) {
			data.put("months", ReportHelper.getMonths());
			data.put("years", ReportHelper.getYears());
			data.put("currentMonth", ReportHelper.currentMonth());
			data.put("currentYear", ReportHelper.currentYear());
			
			//Remove all fields that have yet to be set, in case the back button was used.
			data.remove("targets");
			data.remove("documents");
			data.remove("occurrences");
			data.remove("entries");
			data.remove("statTypes");
			data.remove("formatTypes");
		}
		else if (page == 4) {
			//Remove all fields that have yet to be set, in case the back button was used.
			data.remove("targets");
			data.remove("documents");
			data.remove("occurrences");
			data.remove("entries");
			data.remove("statTypes");
			data.remove("formatTypes");
			
			//set the targets for recruitment
			if ( showTargets(report.getManagementType()) ) {	
				Map<String,String> targets = ReportHelper.getMonthlyTargets(report.getStartDate(), report.getEndDate());
				data.put("targets", targets);
			}
		}
		else if (page == 5) {
			data.remove("documents");
			//set list of documents to choose for report
			if ( showDocuments(report.getManagementType()) ) {	
				List<DocumentBean> documents = ReportHelper.getDocuments(report.getDatasetId());
				report.setPotentialDocuments(documents);	//store the list of documents in the report object so that can be accessed later
				data.put("documents", documents);
			}
			//Remove all fields that have yet to be set, in case the back button was used.
			data.remove("occurrences");
			data.remove("entries");
			data.remove("statTypes");
			data.remove("formatTypes");
		}
		else if (page == 6) {			
			data.remove("occurrences");
			//set list of document occurrences to choose for report
			if ( showDocumentOccurrences(report.getManagementType()) ) {
				List<DocumentOccurrenceBean> occurrences = report.getDocument().getDocOccs();
				data.put("occurrences", occurrences);
			}
			//Remove all fields that have yet to be set, in case the back button was used.
			data.remove("entries");
			data.remove("statTypes");
			data.remove("formatTypes");

		}
		else if (page == 7) {
			//Remove all fields that have yet to be set, in case the back button was used.
			data.remove("statTypes");
			data.remove("formatTypes");
			
			//set list of entries to choose from for report
			if ( showEntries(report.getManagementType()) ) {	
				List<EntryBean> entries = ReportHelper.getEntries(report.getDatasetId(), report.getDocument().getId());
				report.setPotentialEntries(entries);
				data.put("entries", entries);
			}
		}
		else if (page == 8) {
			//set list of types of statistic
			if ( showStatTypes(report.getManagementType()) ) {	
				List<String> statTypes = ReportHelper.getStatTypes();
				data.put("statTypes", statTypes);
			}
			data.remove("formatTypes");
		}
		else if (page == 9) { 
			data.put("formatTypes", ReportHelper.getFormatTypes());
		}

		return data;
	}

	@Override
	protected int getTargetPage(HttpServletRequest request, Object command, Errors errors, int currentPage) {

		ManagementReport report = (ManagementReport) command;

		boolean backButton = false;
		if (super.getTargetPage(request, command, errors, currentPage) < currentPage) {
			backButton = true;
		}

		//Skip the selection of groups if anything other than a recruitment report has been selected
		if (currentPage == 1) {
			if (!showGroups(report.getManagementType())) {
				if (backButton) {
					return super.getTargetPage(request, command, errors, currentPage);
				}
				else  {
					if (showDates(report.getManagementType())) {
						return 3;
					}
					else {
						if (showTargets(report.getManagementType())) {
							return 4;
						}
						if (showDocuments(report.getManagementType())) {
							return 5;
						}
						else {
							return 9;
						}
					}
				}
			}
		}
		//Skip the dates page if the report is not a recruitment progress or UKCRN report
		else if (currentPage == 2) {
			if (backButton) {
				return super.getTargetPage(request, command, errors, currentPage);
			}
			else {
				if (showDates(report.getManagementType())) {
					return 3;
				}
				else if (showTargets(report.getManagementType())) {
					return 4;
				}
				if (showDocuments(report.getManagementType())) {
					return 5;
				}
				else {
					return 9;
				}
			}
		}
		//Back button support for groups page
		else if (currentPage == 3) {
			if (backButton) {
				if (showGroups(report.getManagementType())) {
					return 2;
				}
				else {
					return 1;
				}
			}
			else {
				if (showTargets(report.getManagementType())) {
					return 4;
				}
				if (showDocuments(report.getManagementType())) {
					return 5;
				}
				else {
					return 9;
				}
			}
		}
		//Back button support for dates page
		else if (currentPage == 4) { 
			if (backButton) {
				if (showDates(report.getManagementType())) {
					return 3;
				}
				else if (showGroups(report.getManagementType())) {
					return 2;
				}
				else {
					return 1;
				}
			}
			else {
				if (showDocuments(report.getManagementType())) {
					return 5;
				}
				else {
					return 9;
				}
			}
		}
		//Back button support for documents page
		else if (currentPage == 5) { 
			if (backButton) {
				if (showTargets(report.getManagementType())) {
					return 4;
				}
				else {
					if (showDates(report.getManagementType())) {
						return 3;
					}
					else {
						if (showGroups(report.getManagementType())) {
							return 2;
						}
						else {
							return 1;
						}
					}
				}
			}
			else {
				if (showDocumentOccurrences(report.getManagementType())) {
					return 6;
				}
				else {
					return 9;
				}
			}
		}
		//Back button support for document occurrences page
		else if (currentPage == 6) { 
			if (backButton) {
				if (showDocuments(report.getManagementType())) {
					return 5;
				}
				else{
					if (showTargets(report.getManagementType())) {
						return 4;
					}
					else {
						if (showDates(report.getManagementType())) {
							return 3;
						}
						else {
							if (showGroups(report.getManagementType())) {
								return 2;
							}
							else {
								return 1;
							}
						}
					}
				}
			}
			else {
				if (showEntries(report.getManagementType())) {
					return 7;
				}
				else {
					return 9;
				}
			}
		}
		//Back button support for entries page
		else if (currentPage == 7) { 
			if (backButton) {
				if ( showDocumentOccurrences(report.getManagementType())){
					return 6;
				}
				else{
					if (showDocuments(report.getManagementType())) {
						return 5;
					}
					else{
						if (showTargets(report.getManagementType())) {
							return 4;
						}
						else {
							if (showDates(report.getManagementType())) {
								return 3;
							}
							else {
								if (showGroups(report.getManagementType())) {
									return 2;
								}
								else {
									return 1;
								}
							}
						}
					}
				}
			}
			else {
				return super.getTargetPage(request, command, errors, currentPage);
			}
		}
		//Back button support
		else if (currentPage == 8) {
			if (backButton) {
				if ( showEntries(report.getManagementType())){
					return 7;
				}
				else{
					if ( showDocumentOccurrences(report.getManagementType())){
						return 6;
					}
					else{
						if (showDocuments(report.getManagementType())) {
							return 5;
						}
						else{
							if (showTargets(report.getManagementType())) {
								return 4;
							}
							else {
								if (showDates(report.getManagementType())) {
									return 3;
								}
								else {
									if (showGroups(report.getManagementType())) {
										return 2;
									}
									else {
										return 1;
									}
								}
							}
						}
					}
				}
			}
			else {
				return super.getTargetPage(request, command, errors, currentPage);
			}
		}
		
		else if (currentPage == 9) {
			if (backButton) {
				if ( showStatTypes(report.getManagementType())){
					return 8;
				}
				else{
					if ( showEntries(report.getManagementType())){
						return 7;
					}
					else{
						if ( showDocumentOccurrences(report.getManagementType())){
							return 6;
						}
						else{
							if (showDocuments(report.getManagementType())) {
								return 5;
							}
							else{
								if (showTargets(report.getManagementType())) {
									return 4;
								}
								else {
									if (showDates(report.getManagementType())) {
										return 3;
									}
									else {
										if (showGroups(report.getManagementType())) {
											return 2;
										}
										else {
											return 1;
										}
									}
								}
							}
						}
					}
				}
			}
			else {
				return super.getTargetPage(request, command, errors, currentPage);
			}
		}

		return super.getTargetPage(request, command, errors, currentPage);
	}

	/**
	 * Show the selection of groups only if a recruitment progress report has been selected
	 * 
	 * @param reportType
	 * @return boolean
	 */
	private boolean showGroups(String reportType) {
		if (ManagementReport.RECRUITMENT.equals(reportType) || 
				ManagementReport.STATUSREPORT.equals(reportType) || 
				ManagementReport.DATEREPORT.equals(reportType) ||
				ManagementReport.BASICSTATSREPORT.equals(reportType) ) {
			return true;
		}

		return false;
	}

	/**
	 * Show dates if the report is a recruitment progress, UKCRN or receiving treatment report
	 * 
	 * @param reportType
	 * @return boolean
	 */

	private boolean showDates(String reportType) {
		if (ManagementReport.RECRUITMENT.equals(reportType) || ManagementReport.UKCRNSUMMARY.equals(reportType) || ManagementReport.RECEIVINGTREATMENT.equals(reportType)) {
			return true;
		}

		return false;
	}

	/**
	 * Show recruitment targets if a recruitment progress report has been selected
	 * 
	 * @param reportType
	 * @return boolean
	 */
	private boolean showTargets(String reportType) {
		if (ManagementReport.RECRUITMENT.equals(reportType)) {
			return true;
		}

		return false;
	}
	
	/**
	 * Show a list of documents if a suitable report has been selected
	 * e.g StdCodeStatusReport
	 * 
	 * @param reportType
	 * @return boolean
	 */
	private boolean showDocuments(String reportType) {
		if (ManagementReport.DOCUMENTREPORT.equals(reportType) || ManagementReport.BASICSTATSREPORT.equals(reportType)) {
			return true;
		}

		return false;
	}
	
	private boolean showDocumentOccurrences(String reportType) {
		if ( ManagementReport.BASICSTATSREPORT.equals(reportType)) {
			return true;
		}
		return false;
	}
	
	private boolean showEntries(String reportType) {
		if ( ManagementReport.BASICSTATSREPORT.equals(reportType)) {
			return true;
		}
		return false;
	}
	
	private boolean showStatTypes(String reportType) {
		if ( ManagementReport.BASICSTATSREPORT.equals(reportType)) {
			return true;
		}
		return false;
	}
	
}
