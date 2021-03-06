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

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.psygrid.data.reporting.GroupsNotAllowedException;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.web.beans.ErrorBean;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.forms.RecordReport;
import org.psygrid.web.helpers.ModelHelper;
import org.psygrid.web.helpers.ReportHelper;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller used by the report generation jsps within reporting/
 * of the PsyGrid clinical website, to generate a RECORD report.
 * <p>
 * Presents a series of forms used by the user to populate a Report
 * object, which is used during processFinish(..) to generate the
 * report.
 * 
 * @author Lucy Bridges
 */
public class RecordReportGenerationController extends AbstractWizardFormController {


	public RecordReportGenerationController() {
		setPages(new String[] { "recordreport", "reporting/record1", "reporting/record2", "reporting/record3", "reporting/record4", "reporting/generatereport" });
		this.setAllowDirtyBack(true);
	}

	protected void onBind(HttpServletRequest request, Object command,
			BindException errors) {
	}

	public Object formBackingObject(HttpServletRequest request) throws ServletException {
		RecordReport report = new RecordReport();

		/* The backing object is set up here, with data for the initial values
		 * of the forms fields. This could either be hard-coded, or retrieved from a
		 * a parameter, eg. request.getParameter(primaryKey)
		 */
		report.setType("Record");

		return report;
	}

	//protected void validatePage(Object command, Errors errors, int page) {
		//do validation here
	//}

	protected ModelAndView processFinish(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors) {

		RecordReport report = (RecordReport) command;
		Map<String, Object> model = ModelHelper.getTemplateModel();
		byte[] data = null;
		String fileName = "report"+report.getId()+"."+report.getFormatType();

		//generate report here
		try {
			data = ReportHelper.generateReport(report);
		}
		catch (GroupsNotAllowedException e) {
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
			//String contentType;
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
		return new ModelAndView(new RedirectView("recordreport"));
	}


	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors, int page)
	throws Exception {
		RecordReport report = (RecordReport) command;
		Map<String, Object> data = ModelHelper.getTemplateModel();

		if (page == 0) {
			PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			data.put("projects", user.getProjects());
		}
		else if (page == 1) {
			//for pretty display to the user, retrieve the dataset name for the given identifier
			report.setDataset(ReportHelper.getProjectName(report.getDatasetId()));
			//is this the best way to access the report helper?
			data.put("titles", ReportHelper.getTitles(report.getDatasetId(), "record"));
		}
		else if (page == 2) {
			if (request.getParameter("title") != null) {
				String[] idAndTitle = request.getParameter("title").split("=", 2);
				report.setId(new Long(idAndTitle[0]));
				report.setTitle(idAndTitle[1]);
			}

			List<String> identifiers = ReportHelper.getIdentifiers(report.getDatasetId());
			data.put("identifiers", identifiers);
		}
		else if (page == 3) {
			data.put("formatTypes", ReportHelper.getFormatTypes());
		}

		return data;

	}

}
