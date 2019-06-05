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

package org.psygrid.web.controllers.audit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.model.dto.extra.DocInstChangeHistoryResult;
import org.psygrid.data.model.dto.extra.RecordChangeHistoryResult;
import org.psygrid.data.model.dto.extra.SearchRecordChangeHistoryResult;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.forms.Audit;
import org.psygrid.web.helpers.AuditHelper;
import org.psygrid.web.helpers.ModelHelper;
import org.psygrid.web.helpers.SamlHelper;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

/**
 * @author Rob Harper
 *
 */
public class AuditController extends AbstractWizardFormController {

	private static final Log LOG = LogFactory.getLog(AuditController.class);
	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
	
	private AAQueryClient aaqc;
	
	private RepositoryClient client;
	
	public AuditController(){
		setPages(new String[]{"audit", "audit1", "audit2", "audit3", "audit4"});
		client = new RepositoryClient();
	}
	
	public void setAaqc(AAQueryClient aaqc) {
		this.aaqc = aaqc;
	}

	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors, int page) throws Exception {
		LOG.info("referenceData for page "+page);
		Audit a = (Audit)command;
		Map<String, Object> model = ModelHelper.getTemplateModel();
		model.put(this.getCommandName(), a);
		PsygridUserDetails pgud = (PsygridUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if ( 0 == page ){
			model.put("projects", pgud.getAuditableProjects());
		}
		if ( 1 == page ){
			ProjectType project = null;
			for ( ProjectType pt: pgud.getAuditableProjects() ){
				if ( pt.getIdCode().equals(a.getProject()) ){
					project = pt;
					break;
				}
			}
			if ( null == project ){
				throw new Exception("Couldn't find study for code '"+a.getProject()+"'");
			}
			a.setProjectText(project.getName()+" ("+project.getIdCode()+")");
			a.setSearchRecordChangeHistoryResult(null);
			model.put("userList", AuditHelper.createUserList(aaqc, project));
			model.put("identifierList", AuditHelper.createIdentifierList(project, pgud));
		}
		if ( 2 == page ){
			String user = null;
			if ( !"--none--".equals(a.getUser()) ){
				user = a.getUser();
			}
			
			Date start = null;
			if ( null != a.getStartDate() && a.getStartDate().length() > 0 ){
				start = dateFormatter.parse(a.getStartDate());
			}
			
			Date end = null;
			if ( null != a.getEndDate() && a.getEndDate().length() > 0 ){
				end = dateFormatter.parse(a.getEndDate());
			}
			
			String identifier = null;
			if ( !"--none--".equals(a.getIdentifier()) ){
				identifier = a.getIdentifier();
			}
			
			SearchRecordChangeHistoryResult result = client.searchRecordChangeHistory(a.getProject(), start, end, user, identifier, a.getStartIndex(), SamlHelper.getSaAsString(pgud.getSaml()));
			a.setSearchRecordChangeHistoryResult(result);
			a.setRecordHistoryId(null);
			a.setRecordChangeHistoryItem(null);
			int prevStartIndex = result.getFirstResult()-result.getMaxResultCount()-1;
			if ( prevStartIndex >= 0 ){
				model.put("prevStartIndex", prevStartIndex);
			}
			if ( result.getLastResult() < result.getTotalCount() ){
				model.put("nextStartIndex", result.getLastResult());
			}
		}
		if ( 3 == page ){
			for ( RecordChangeHistoryResult rchr: a.getSearchRecordChangeHistoryResult().getResults() ){
				if ( rchr.getHistoryId().equals(a.getRecordHistoryId())){
					a.setRecordChangeHistoryItem(rchr);
					break;
				}
			}
			DocInstChangeHistoryResult[] result = client.searchDocInstChangeHistory(a.getRecordChangeHistoryItem().getIdentifier(), a.getRecordChangeHistoryItem().getHistoryId(), SamlHelper.getSaAsString(pgud.getSaml()));
			a.setSearchDocInstChangeHistoryResults(result);
			a.setDocumentHistoryId(null);
			a.setDocInstChangeHistoryItem(null);
		}
		if ( 4 == page ){
			for ( DocInstChangeHistoryResult dichr: a.getSearchDocInstChangeHistoryResults() ){
				if ( dichr.getHistoryId().equals(a.getDocumentHistoryId())){
					a.setDocInstChangeHistoryItem(dichr);
					break;
				}
			}
			LOG.info("Identifier="+a.getRecordChangeHistoryItem().getIdentifier()+"; history id="+a.getDocumentHistoryId());
			model.put("result", client.getProvenanceForChange(a.getRecordChangeHistoryItem().getIdentifier(), a.getDocumentHistoryId(), SamlHelper.getSaAsString(pgud.getSaml())));
		}
		return model;
	}
	
	@Override
	protected ModelAndView processFinish(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		//This point is never reached!
		return null;
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return new Audit();
	}

	@Override
	protected void validatePage(Object command, Errors errors, int page, boolean finish) {
		super.validatePage(command, errors, page);
		Audit a = (Audit)command;
		if ( 1 == page ){
			//validate the start date
			try{
				if ( null != a.getStartDate() && a.getStartDate().length() > 0 ){
					dateFormatter.parse(a.getStartDate());
				}
			}
			catch(ParseException ex){
				errors.rejectValue("startDate", "error.audit.startDate");
			}
			//validate the end date
			try{
				if ( null != a.getEndDate() && a.getEndDate().length() > 0 ){
					dateFormatter.parse(a.getEndDate());
				}
			}
			catch(ParseException ex){
				errors.rejectValue("endDate", "error.audit.endDate");
			}
		}
		if ( 2 == page ){
			//validate that a record-level history item has been selected
			if ( null == a.getRecordHistoryId() ){
				errors.rejectValue("recordHistoryId", "error.audit.recordHistoryId");
			}
				
		}
		if ( 3 == page ){
			//validate that a document-level history item has been selected
			if ( null == a.getRecordHistoryId() ){
				errors.rejectValue("documentHistoryId", "error.audit.documentHistoryId");
			}
				
		}
	}

}
