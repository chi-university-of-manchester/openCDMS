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

import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.panels.common.ErrorPanel;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.audit.models.AuditLogModel;
import org.psygrid.data.model.dto.extra.DocInstChangeHistoryResult;
import org.psygrid.data.repository.client.RepositoryClient;

/**
 * @author Rob Harper
 *
 */
public class ViewDocumentsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(AuditLogFormPanel.class);
	
	public ViewDocumentsPanel(String id, IModel<AuditLogModel> model, Component container, Panel prevPanel) throws Exception {
		super(id);
		setOutputMarkupId(true);
		add(new AuditLogDocumentForm("documentForm", model, container, prevPanel, this));
	}

	public static class AuditLogDocumentForm extends Form<AuditLogModel>{

		private static final long serialVersionUID = 1L;

		private final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd-MM-yyyy");
		
		private final Component container;

		private final Panel prevPanel;
		
		private final Panel currentPanel;
		
		@SuppressWarnings("serial")
		public AuditLogDocumentForm(final String id, final IModel<AuditLogModel> model, final Component container, final Panel prevPanel, final Panel currentPanel) throws Exception {
			super(id, model);
			this.container = container;
			this.prevPanel = prevPanel;
			this.currentPanel = currentPanel;
			
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			
			doSearch(model, session);
			
			AuditLogModel alm = model.getObject();

			String displayID = alm.getIdentifierMap().get(alm.getRchResult().getIdentifier());
			
			Label study = new Label("study", alm.getStudy().getName());
			Label record = new Label("record", displayID);
			Label user = new Label("user", alm.getRchResult().getUser());
			Label whenLocal = new Label("whenLocal", dateFormat.format(alm.getRchResult().getWhen()));
			Label whenSystem = new Label("whenSystem", dateFormat.format(alm.getRchResult().getWhenSystem()));
			Label action = new Label("action", alm.getRchResult().getAction());
			
			final ListView<DocInstChangeHistoryResult> resultsList = new ListView<DocInstChangeHistoryResult>(
					"resultsList", Arrays.asList(alm.getDocResult())){

				@Override
				protected void populateItem(ListItem<DocInstChangeHistoryResult> item) {
					
					final DocInstChangeHistoryResult dichr = item.getModelObject();
					
					Label document = new Label("document", dichr.getDisplayText());
					Label when = new Label("when", dateFormat.format(dichr.getWhen()));
					Label user = new Label("user", dichr.getUser());
					Label action = new Label("action", dichr.getAction());
					
					AjaxButton view = new AjaxButton("view"){

						@Override
						protected void onSubmit(AjaxRequestTarget target,
								Form<?> form) {

							model.getObject().setSelectedDocResult(dichr);
							
							try{
								ViewEntriesPanel nextPanel = new ViewEntriesPanel(
										currentPanel.getId(),
										model,
										container,
										currentPanel);
								
								currentPanel.replaceWith(nextPanel);
								target.addComponent(container);
							}
							catch(Exception ex){
								LOG.error("Error occurred whilst trying to display entry-level audit log", ex);
								ErrorPanel.show(currentPanel, container, target, ex);
							}
						
						}
						
					};
					
					item.add(document);
					item.add(when);
					item.add(user);
					item.add(action);
					item.add(view);
				}
				
			};

			
			AjaxButton back = new AjaxButton("back"){

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					AuditLogDocumentForm.this.currentPanel.replaceWith(AuditLogDocumentForm.this.prevPanel);
					target.addComponent(AuditLogDocumentForm.this.container);
				}
				
			};
			
			add(study);
			add(record);
			add(user);
			add(whenLocal);
			add(whenSystem);
			add(action);
			
			add(resultsList);
			
			add(back);
			
		}
		
		private void doSearch(IModel<AuditLogModel> model, OpenCdmsWebSession session) throws Exception {
			AuditLogModel alm = model.getObject();
			
			RepositoryClient client = new RepositoryClient();
			DocInstChangeHistoryResult[] result = client.searchDocInstChangeHistory(
					alm.getRchResult().getIdentifier(), 
					alm.getRchResult().getHistoryId(), 
					SamlHelper.getSaml(session.getUser()));
			alm.setDocResult(result);
			
		}
		

	}
}
