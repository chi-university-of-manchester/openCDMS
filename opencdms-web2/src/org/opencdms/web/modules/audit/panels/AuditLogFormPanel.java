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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.panels.common.ErrorPanel;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.audit.models.AuditLogModel;
import org.opencdms.web.modules.audit.models.AuditLogModel.User;
import org.opencdms.web.modules.reports.repository.ReportingClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class AuditLogFormPanel extends Panel implements IHeaderContributor {

	private static final long serialVersionUID = 1L;
	
	private static final Log LOG = LogFactory.getLog(AuditLogFormPanel.class);
	
	public AuditLogFormPanel(String id, Component container) {
		super(id);
		setOutputMarkupId(true);
		add(new AuditLogForm("auditForm", new CompoundPropertyModel<AuditLogModel>(new AuditLogModel()), container));
	}

	public static class AuditLogForm extends Form<AuditLogModel>{

		private static final long serialVersionUID = 1L;

		private final Component container;

		private Panel activeSearchResultPanel;

		public Component getContainer() {
			return container;
		}

		@SuppressWarnings("serial")
		public AuditLogForm(final String id, final IModel<AuditLogModel> model, Component container) {
			super(id, model);
			this.container = container;
			
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			
			final WebMarkupContainer filtersContainer1 = new WebMarkupContainer("filtersContainer1");
			filtersContainer1.setOutputMarkupId(true);
			final WebMarkupContainer filtersContainer2 = new WebMarkupContainer("filtersContainer2");
			filtersContainer2.setOutputMarkupId(true);
			filtersContainer2.setVisible(false);

			final WebMarkupContainer searchResultContainer = new WebMarkupContainer("searchResultContainer");
			searchResultContainer.setOutputMarkupId(true);

			activeSearchResultPanel = new EmptyPanel("searchResultPanel");
			
			final DropDownChoice<ProjectType> study = 
				new DropDownChoice<ProjectType>(
					"study", 
					session.getUser().getExportableProjects(),
					new ChoiceRenderer<ProjectType>("name", "idCode"));
			study.setRequired(true);
			study.setMarkupId("study");
		
			final DateField startDate = new DateField("startDate");
			
			final DateField endDate = new DateField("endDate");
			
			final DropDownChoice<User> user = 
				new DropDownChoice<User>("user",
						new ArrayList<User>(),
						new ChoiceRenderer<User>("name"));
			
			final DropDownChoice<String> participant = 
				new DropDownChoice<String>("participant",
						new ArrayList<String>(),
						new IChoiceRenderer<String>(){
							public Object getDisplayValue(String object) {
								return getModelObject().getIdentifierMap().get(object);
							}
							public String getIdValue(String object, int index) {
								return object;
							}						
						});						
			
			final AjaxButton search = new AjaxButton("search") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					try{
						SearchResultPanel searchResultPanel = new SearchResultPanel("searchResultPanel", model, AuditLogForm.this.container, (Panel)AuditLogForm.this.getParent());
						activeSearchResultPanel.replaceWith(searchResultPanel);
						activeSearchResultPanel = searchResultPanel;
						target.addComponent(searchResultContainer);
					}
					catch(Exception ex){
						LOG.error("Exception when showing search results", ex);
						ErrorPanel.show(AuditLogForm.this.getParent(), AuditLogForm.this.getContainer(), target, ex);
						return;
					}
				}
			};
			
			study.add(new AjaxFormComponentUpdatingBehavior("onchange"){

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					ProjectType pt = model.getObject().getStudy();
					try{
						//get users
						AAQueryClient aaqc = new AAQueryClient("aaclient.properties");
						String[] users = aaqc.getUsersInProject(pt);
						List<User> userList = new ArrayList<User>();
						for ( int i=0, c=users.length; i<c; i++ ){
							userList.add(new User(users[i]));
						}
						user.setChoices(userList);
					}
					catch(Exception ex){
						LOG.error("Exception when calling getUsersInProject", ex);
						ErrorPanel.show(AuditLogForm.this.getParent(), AuditLogForm.this.getContainer(), target, ex);
						return;
					}

					try{
						//get participants
						ReportingClient client = new ReportingClient();
						Map<String,String> identifierMap = client.getIdentifierMap(pt.getIdCode(), 
								SamlHelper.getSaml(session.getUser()));
						participant.setChoices(new ArrayList<String>(identifierMap.keySet()));
						// Identifier map is needed in results panels so add it to the model
						getModelObject().setIdentifierMap(identifierMap);
					}
					catch(Exception ex){
						LOG.error("Exception when calling getIdentifierMap", ex);
						ErrorPanel.show(AuditLogForm.this.getParent(), AuditLogForm.this.getContainer(), target, ex);
						return;
					}
					
					filtersContainer2.setVisible(true);
					target.addComponent(filtersContainer1);
					
				}
				
			});
			
			filtersContainer1.add(filtersContainer2);
			filtersContainer2.add(startDate);
			filtersContainer2.add(endDate);
			filtersContainer2.add(user);
			filtersContainer2.add(participant);
			filtersContainer2.add(search);
			
			searchResultContainer.add(activeSearchResultPanel);
			
			add(study);
			add(filtersContainer1);
			add(searchResultContainer);
			
		}
		
	}

	public void renderHead(IHeaderResponse response) {
		response.renderOnLoadJavascript("selectStudy(\"study\")");
	}
	
}
