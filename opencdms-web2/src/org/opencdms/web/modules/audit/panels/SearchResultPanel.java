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

package org.opencdms.web.modules.audit.panels;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.panels.common.ErrorPanel;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.audit.models.AuditLogModel;
import org.psygrid.data.model.dto.extra.RecordChangeHistoryResult;
import org.psygrid.data.model.dto.extra.SearchRecordChangeHistoryResult;
import org.psygrid.data.repository.client.RepositoryClient;

/**
 * @author Rob Harper
 *
 */
public class SearchResultPanel extends Panel {
	
	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(SearchResultPanel.class);
	
	private final AjaxButton prevButton;
	
	private final AjaxButton nextButton;
	
	private final Component container;

	private final MarkupContainer errorParent;
	
	public Component getContainer() {
		return container;
	}

	public MarkupContainer getErrorParent(){
		return errorParent;
	}
	
	@SuppressWarnings("serial")
	public SearchResultPanel(String id, final IModel<AuditLogModel> model, final Component container, final Panel errorParent) throws Exception {
		super(id, model);
		this.container = container;
		this.errorParent = errorParent;
		
		final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();

		boolean resultsReturned = doSearch(model, session);
		
		final WebMarkupContainer noResultsContainer = new WebMarkupContainer("noResultsContainer");
		noResultsContainer.setOutputMarkupId(true);
		
		final WebMarkupContainer resultsContainer = new WebMarkupContainer("resultsContainer");
		resultsContainer.setOutputMarkupId(true);
		
		AuditLogModel alm = model.getObject();
		final Label startResult = new Label("startResult", new PropertyModel<Integer>(alm, "searchResult.firstResult") );
		final Label endResult = new Label("endResult", new PropertyModel<Integer>(alm, "searchResult.lastResult") );
		final Label totalResults = new Label("totalResults", new PropertyModel<Integer>(alm, "searchResult.totalCount") );
		
		final ListView<RecordChangeHistoryResult> resultsList = new ListView<RecordChangeHistoryResult>(
				"resultsList", Arrays.asList(alm.getSearchResult().getResults())){

			@Override
			protected void populateItem(ListItem<RecordChangeHistoryResult> item) {
				
				final RecordChangeHistoryResult rchr = item.getModelObject();
				
				String displayID = model.getObject().getIdentifierMap().get(rchr.getIdentifier());
				
				Label participant = new Label("participant",displayID );
				Label when = new Label("when", rchr.getWhenSystem().toString());
				Label user = new Label("user", rchr.getUser());
				Label action = new Label("action", rchr.getAction());
				
				AjaxButton view = new AjaxButton("view"){

					@Override
					protected void onSubmit(AjaxRequestTarget target,
							Form<?> form) {

						model.getObject().setRchResult(rchr);
						
						try{
							ViewDocumentsPanel nextPanel = new ViewDocumentsPanel(
									errorParent.getId(),
									model,
									container,
									errorParent);
							
							errorParent.replaceWith(nextPanel);
							target.addComponent(container);
						}
						catch(Exception ex){
							LOG.error("Error occurred whilst trying to display document-level audit log", ex);
							ErrorPanel.show(errorParent, container, target, ex);
						}
						
					}
					
				};
				
				item.add(participant);
				item.add(when);
				item.add(user);
				item.add(action);
				item.add(view);
			}
			
		};
		resultsList.setOutputMarkupId(true);
				
		prevButton = new AjaxButton("prevButton"){

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try{
					AuditLogModel alm = model.getObject();
					alm.setStartIndex(alm.getSearchResult().getFirstResult()-alm.getSearchResult().getMaxResultCount()-1);
					doSearch(model, session);
					resultsList.setList(Arrays.asList(alm.getSearchResult().getResults()));
					enableDisableNextPrevButtons(model);
					
					target.addComponent(resultsContainer);
				}
				catch(Exception ex){
					LOG.error("Error occurred when Prev button clicked", ex);
					ErrorPanel.show(SearchResultPanel.this.getErrorParent(), SearchResultPanel.this.getContainer(), target, ex);
					return;
				}
			}
			
		};
		
		nextButton = new AjaxButton("nextButton"){

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try{
					AuditLogModel alm = model.getObject();
					alm.setStartIndex(alm.getSearchResult().getLastResult());
					doSearch(model, session);
					resultsList.setList(Arrays.asList(alm.getSearchResult().getResults()));
					enableDisableNextPrevButtons(model);
					
					target.addComponent(resultsContainer);
				}
				catch(Exception ex){
					LOG.error("Error occurred when Next button clicked", ex);
					ErrorPanel.show(SearchResultPanel.this.getErrorParent(), SearchResultPanel.this.getContainer(), target, ex);
					return;
				}
			}
			
		};
				
		//set initial state of prev and next buttons
		enableDisableNextPrevButtons(model);
		
		if ( resultsReturned ){
			noResultsContainer.setVisible(false);
			resultsContainer.setVisible(true);
		}
		else{
			noResultsContainer.setVisible(true);
			resultsContainer.setVisible(false);
		}

		
		resultsContainer.add(startResult);
		resultsContainer.add(endResult);
		resultsContainer.add(totalResults);
		
		resultsContainer.add(prevButton);
		resultsContainer.add(nextButton);
						
		resultsContainer.add(resultsList);
		
		add(noResultsContainer);
		add(resultsContainer);
		
	}

	private boolean doSearch(IModel<AuditLogModel> model, OpenCdmsWebSession session) throws Exception {
		AuditLogModel alm = model.getObject();
		
		String user = null;
		if ( null != alm.getUser() ){
			user = alm.getUser().getDn();
		}
		
		RepositoryClient client = new RepositoryClient();
		SearchRecordChangeHistoryResult result = client.searchRecordChangeHistory(
				alm.getStudy().getIdCode(), alm.getStartDate(), alm.getEndDate(), user, 
				alm.getParticipant(), alm.getStartIndex(), SamlHelper.getSaml(session.getUser()));
		
		alm.setSearchResult(result);
		return (result.getTotalCount()>0);
	}
	
	private void enableDisableNextPrevButtons(IModel<AuditLogModel> model){
		AuditLogModel alm = model.getObject();
		int prevStartIndex = alm.getSearchResult().getFirstResult()-alm.getSearchResult().getMaxResultCount()-1;
		if ( prevStartIndex >= 0 ){
			prevButton.setEnabled(true);
		}
		else{
			prevButton.setEnabled(false);
		}
		
		if ( alm.getSearchResult().getLastResult() < alm.getSearchResult().getTotalCount() ){
			nextButton.setEnabled(true);
		}
		else{
			nextButton.setEnabled(false);
		}
	}
	
}
