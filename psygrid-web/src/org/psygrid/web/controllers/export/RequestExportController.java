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


package org.psygrid.web.controllers.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.export.ExportFormat;
import org.psygrid.data.export.hibernate.ExportDocument;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.web.beans.DocumentGroupBean;
import org.psygrid.web.beans.DocumentOccurrenceBean;
import org.psygrid.web.beans.DocumentSectionBean;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.forms.ExportRequest;
import org.psygrid.web.forms.Hub;
import org.psygrid.web.helpers.ExceptionHelper;
import org.psygrid.web.helpers.ModelHelper;
import org.psygrid.web.helpers.ReportHelper;
import org.psygrid.web.helpers.SamlHelper;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Rob Harper
 *
 */
public class RequestExportController extends AbstractWizardFormController {

	private static final Log LOG = LogFactory.getLog(RequestExportController.class);

	public RequestExportController(){
		setPages(new String[]{"requestexport", "requestexport1", "requestexport2", "requestexport3"});
	}

	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors, int page) throws Exception {
		LOG.info("referenceData for page "+page);
		ExportRequest er = (ExportRequest)command;
		Map<String, Object> model = ModelHelper.getTemplateModel();
		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if ( 0 == page ){
			//Otherwise
			model.put("projects", user.getExportableProjects());

		}
		else if ( 1 == page ){
			ProjectType project = null;
			for ( ProjectType pt: user.getExportableProjects() ){
				if ( pt.getIdCode().equals(er.getProject()) ){
					project = pt;
					break;
				}
			}
			if ( null == project ){
				throw new Exception("Couldn't find study for code '"+er.getProject()+"'");
			}
			er.setProjectText(project.getName()+" ("+project.getIdCode()+")");
			model.put("project", er.getProjectText());
			List<Hub> hubs = ReportHelper.getGroups(er.getProject());
			model.put("hubs", hubs);
		}
		else if ( 2 == page ){
			model.put("project", er.getProjectText());
			model.put("groups", getReadableGroups(er.getProject(), er.getGroups()));
			List<String> formats = new ArrayList<String>();
			ExportFormat[] allFormats = ExportFormat.values();
			for (ExportFormat format: allFormats) {
				formats.add(this.getReadableFormat(format));
			}
			model.put("formats", formats);
		}
		else if ( 3 == page || 4 == page){
			model.put("project", er.getProjectText());
			model.put("groups", getReadableGroups(er.getProject(), er.getGroups()));
			model.put("format", er.getFormat());

			//Allow the user to select statuses for documents
			List<String> docStatuses = new ArrayList<String>();
			docStatuses.add("Complete");
			docStatuses.add("Rejected");
			docStatuses.add("Approved");
			docStatuses.add("Pending Approval");
			docStatuses.add("Incomplete");
			model.put("docStatuses", docStatuses);

			//see if the user is allowed to do immediate export
			boolean immediateExport = false;
			for ( ProjectType pt: user.getImmediatelyExportableProjects() ){
				if ( pt.getIdCode().equals(er.getProject()) ){
					immediateExport = true;
				}
			}
			model.put("immediateExport", immediateExport);

			if (er.getFormat().contains("CSV")
					|| er.getFormat().equals("Excel")) {
				List<String> valueOrCode = new ArrayList<String>();
				valueOrCode.add("Both");
				valueOrCode.add("Values Only");
				valueOrCode.add("Codes Only");
				model.put("codevalues", valueOrCode);
			}

			//get the documents for the selected dataset
			RepositoryClient client = new RepositoryClient();

			DataSet ds = null;
			try{
				ds = client.getDataSetSummaryWithDocs(er.getProject(), SamlHelper.getSaAsString(user.getSaml()));
			}
			catch(Exception ex){
				LOG.error("Exception when calling getDataSetSummaryWithDocs", ex);
				throw new ModelAndViewDefiningException(new ModelAndView(new RedirectView("../error.html")));
			}
			List<DocumentGroupBean> docGroups = new ArrayList<DocumentGroupBean>();
			for ( int i=0, c=ds.numDocumentGroups(); i<c; i++ ){
				DocumentGroup docGrp = ds.getDocumentGroup(i);
				DocumentGroupBean dgb = new DocumentGroupBean(docGrp.getId(), docGrp.getDisplayText());
				docGroups.add(dgb);
				for ( int j=0, d=ds.numDocuments(); j<d; j++ ){
					Document doc = ds.getDocument(j);
					for ( int k=0, e=doc.numOccurrences(); k<e; k++ ){
						DocumentOccurrence docOcc = doc.getOccurrence(k);
						if ( docOcc.getDocumentGroup().equals(docGrp) ){
							DocumentOccurrenceBean docOccBean = new DocumentOccurrenceBean(docOcc.getId(), docOcc.getCombinedDisplayText());
							docOccBean.setDocumentid(doc.getId());
							dgb.addDocOcc(docOccBean);

							for (int section = 0; section < doc.numSections(); section++) {
								DocumentSectionBean sectionBean = new DocumentSectionBean(doc.getSection(section).getId(), doc.getSection(section).getDisplayText());
								docOccBean.addSection(sectionBean);
								for (int entry = 0; entry < doc.numEntries(); entry++) {
									if (doc.getEntry(entry).getSection().equals(doc.getSection(section))) {
										String displayText = doc.getEntry(entry).getDisplayText();
										if (displayText == null || displayText.equals("")) {
											displayText = "[No name]";
										}
										sectionBean.addEntry(doc.getEntry(entry).getId(), displayText);			
									}
								}
							}
						}
					}
				}
			}
			model.put("docGroups", docGroups);
			

			if (er.getFormat().equals("STATA Import")
					|| er.getFormat().equals("SAS Import")
					|| er.getFormat().equals("SPSS Import")) {
				//TODO use the ds to check the number of missing values for the dataset (in SPSS missing codes will be used if there are 3 or fewer).
				String sysmissmessage = "Please note that any missing codes will be replaced by the system missing value.";
				model.put("sysmissmessage", sysmissmessage);
			}
		}

		return model;
	}

	@Override
	protected void validatePage(Object command, Errors errors, int page, boolean finish) {
		ExportRequest r = (ExportRequest)command;
		if ( 1 == page ){
			//check that at least 1 group has been selected
			if ( r.getGroups().size() < 1 ){
				errors.rejectValue("groups", "error.export.groups");
			}
		}
		if ( 2 == page ){
			if ( null == r.getFormat() || 0 == r.getFormat().length() ){
				errors.rejectValue("format", "error.export.format");
			}
		}
		if ( 3 == page ){
			if ( r.getDocuments().size() < 1 ){
				errors.rejectValue("documents", "error.export.documents");
			}
			if (r.getDocStatuses().size() < 1) {
				errors.rejectValue("docStatuses", "error.export.docStatuses");
			}
		}
	}

	@Override
	protected ModelAndView processFinish(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
	throws Exception {
		ExportRequest r = (ExportRequest)command;
		PsygridUserDetails pgud = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		RepositoryClient client = new RepositoryClient();

		String project = r.getProject();
		String format = r.getFormat();
		String codeValue = r.getCodeValue();
		boolean immediate = false;
		if ( "true".equals(r.getImmediate()) ){
			immediate = true;
		};

		List<ExportDocument> docOccs = new ArrayList<ExportDocument>();
		for ( int i=0, c=r.getDocuments().size(); i<c; i++ ){
			ExportDocument docOcc = new ExportDocument();
			docOcc.setDocOccId(Long.parseLong(r.getDocuments().get(i)));
			docOccs.add(docOcc);	
		}
		Pattern underscore = Pattern.compile("_");

		for (String docAndEntry: r.getEntries()) {
			String[] dae = underscore.split(docAndEntry);
			String document = dae[0];
			String entry = dae[1];
			//Add the entry to the list for that document..
			for (ExportDocument docOcc: docOccs) {
				if (docOcc.getDocOccId().equals(Long.parseLong(document))) {
					docOcc.addEntryId(Long.parseLong(entry));
					continue;
				}
			}
		}

		String shortFormat = this.getReadableFormat(format).toStringForRequest();

		ModelAndView mav = null;

		String user = pgud.getPgDn();
		org.psygrid.data.export.hibernate.ExportRequest export = new org.psygrid.data.export.hibernate.ExportRequest(user, project, r.getGroups(), shortFormat, immediate);
		export.setDocOccs(docOccs);
		export.setDocumentStatuses(r.getDocStatuses());
		boolean showCodes  = true;
		boolean showValues = true;
		if ("Codes Only".equals(codeValue)) {
			showValues = false;
		}
		else if ("Values Only".equals(codeValue)) {
			showCodes = false;
		}
		export.setShowCodes(showCodes);
		export.setShowValues(showValues);

		try{
			if ( immediate ){
				client.requestImmediateExport(export, SamlHelper.getSaAsString(pgud.getSaml()));
			}
			else{
				client.requestExport(export, SamlHelper.getSaAsString(pgud.getSaml()));
			}
			Map<String, Object> model = ModelHelper.getTemplateModel();
			return new ModelAndView("exportrequested", model);
		}
		catch(Exception ex){
			mav = ExceptionHelper.handleWsException(ex);
			ex.printStackTrace();
		}
		return mav;
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return new ExportRequest();
	}

	private List<String> getReadableGroups(String projectCode, List<String> groupCodes){

		List<String> groups = new ArrayList<String>();

		PsygridUserDetails user = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ProjectType projectType = new ProjectType(null, projectCode, null, null, false);
		Set<ProjectType> projects = user.getGroups().keySet();		
		for (ProjectType p: projects) {
			if (p.getIdCode().equals(projectCode)) {
				projectType = p; 
			}
		}
		if (user.getGroups() != null && user.getGroups().containsKey(projectType)) {	
			for (GroupType gt: user.getGroups().get(projectType)) {
				for ( String g: groupCodes ){
					if ( gt.getIdCode().equals(g) ){
						groups.add(gt.getName());
					}
				}
			}
		}
		return groups;
	}


	/**
	 * Get the pretty print version of the ExportFormat name.
	 * 
	 * This should be in the ExportFormat class but it doesn't
	 * get included in the jar client when it is.
	 */
	public String getReadableFormat(ExportFormat format) {
		switch (format) {
		case SINGLE_CSV:
			return "Single CSV File";
		case MULTIPLE_CSV:
			return "Multiple CSV files";
		case EXCEL:
			return "Excel";
		case XML:
			return "XML";
		case STATA:
			return "STATA Import";
		case SPSS:
			return "SPSS Import";
		case SAS:
			return "SAS Import";
		}

		return "";
	}

	public ExportFormat getReadableFormat(String format) {
		if ("Single CSV File".equals(format)) {
			return ExportFormat.SINGLE_CSV;
		}
		if ("Multiple CSV files".equals(format)) {
			return ExportFormat.MULTIPLE_CSV;
		}
		if ("Excel".equals(format)) {
			return ExportFormat.EXCEL;
		}
		if ("XML".equals(format)) {
			return ExportFormat.XML;
		}
		if ("STATA Import".equals(format)) {
			return ExportFormat.STATA;
		}
		if ("SPSS Import".equals(format)) {
			return ExportFormat.SPSS;
		}
		if ("SAS Import".equals(format)) {
			return ExportFormat.SAS;
		}		

		return null;
	}
}
