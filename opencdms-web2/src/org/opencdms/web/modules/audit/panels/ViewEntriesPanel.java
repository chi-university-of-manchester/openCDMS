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
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.audit.models.AuditLogModel;
import org.psygrid.data.model.dto.extra.ProvenanceForChangeResult;
import org.psygrid.data.repository.client.RepositoryClient;

/**
 * @author Rob Harper
 *
 */
public class ViewEntriesPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	public ViewEntriesPanel(String id, IModel<AuditLogModel> model, Component container, Panel prevPanel) throws Exception {
		super(id);
		setOutputMarkupId(true);
		add(new AuditLogEntryForm("entryForm", model, container, prevPanel, this));
	}

	public static class AuditLogEntryForm extends Form<AuditLogModel>{

		private static final long serialVersionUID = 1L;

		private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
		
		private final Component container;

		private final Panel prevPanel;
		
		private final Panel currentPanel;
		
		@SuppressWarnings("serial")
		public AuditLogEntryForm(final String id, final IModel<AuditLogModel> model, Component container, Panel prevPanel, Panel currentPanel) throws Exception {
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
			Label document = new Label("document", alm.getSelectedDocResult().getDisplayText());
			Label user = new Label("user", alm.getSelectedDocResult().getUser());
			Label whenLocal = new Label("whenLocal", dateFormat.format(alm.getSelectedDocResult().getWhen()));
			Label whenSystem = new Label("whenSystem", dateFormat.format(alm.getSelectedDocResult().getWhenSystem()));
			Label action = new Label("action", alm.getSelectedDocResult().getAction());
			
			final ListView<ProvenanceForChangeResult> resultsList = new ListView<ProvenanceForChangeResult>(
					"resultsList", Arrays.asList(alm.getProvResult())){

				@Override
				protected void populateItem(ListItem<ProvenanceForChangeResult> item) {
					
					final ProvenanceForChangeResult pfcr = item.getModelObject();
					
					Label entry = new Label("entry", pfcr.getEntry());
					Label was = new Label("was", pfcr.getPrevValue());
					Label now = new Label("now", pfcr.getCurrentValue());
					Label comment = new Label("comment", pfcr.getComment());
					
					item.add(entry);
					item.add(was);
					item.add(now);
					item.add(comment);
				}
				
			};

			
			AjaxButton back = new AjaxButton("back"){

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					AuditLogEntryForm.this.currentPanel.replaceWith(AuditLogEntryForm.this.prevPanel);
					target.addComponent(AuditLogEntryForm.this.container);
				}
				
			};
			
			add(study);
			add(record);
			add(document);
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
			ProvenanceForChangeResult[] result = client.getProvenanceForChange(
					alm.getRchResult().getIdentifier(), 
					alm.getSelectedDocResult().getHistoryId(), 
					SamlHelper.getSaml(session.getUser()));
			alm.setProvResult(result);
			
		}
		

	}
}
