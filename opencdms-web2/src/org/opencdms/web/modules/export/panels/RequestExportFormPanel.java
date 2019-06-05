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

package org.opencdms.web.modules.export.panels;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.panels.common.ErrorPanel;
import org.opencdms.web.core.panels.common.ProjectAndGroupsPanel;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.export.models.ExportDetailsModel;
import org.opencdms.web.modules.export.models.ExportRequestModel;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.repository.client.RepositoryClient;

/**
 * @author Rob Harper
 *
 */
public class RequestExportFormPanel extends Panel implements IHeaderContributor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Log LOG = LogFactory.getLog(RequestExportFormPanel.class);

	public RequestExportFormPanel(String id, Component container) {
		super(id);
		setOutputMarkupId(true);
		add(new RequestExportForm("requestForm",  new CompoundPropertyModel<ExportRequestModel>(new ExportRequestModel()), container));
	}

	public static class RequestExportForm extends Form<ExportRequestModel>{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private final Component container;
		
		private final RequestExportDetailsPanel exportDetails;
		
		public Component getContainer() {
			return container;
		}

		public RequestExportForm(String id, final IModel<ExportRequestModel> model, Component container) {
			super(id, model);
			this.container = container;
			
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			
			final WebMarkupContainer submitContainer1 = new WebMarkupContainer("submitContainer1");
			submitContainer1.setOutputMarkupId(true);
			final WebMarkupContainer submitContainer2 = new WebMarkupContainer("submitContainer2");
			submitContainer2.setOutputMarkupId(true);
			submitContainer2.setVisible(false);

			exportDetails = new RequestExportDetailsPanel(
					"exportDetails",
					new CompoundPropertyModel<ExportDetailsModel>(model.getObject().getDetails())){

					/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

					@Override
					public void endOfPanel(AjaxRequestTarget target) {
						submitContainer2.setVisible(true);
						target.addComponent(submitContainer1);
					}
			
			};
			
			final ProjectAndGroupsPanel projectAndGroups = 
				new ProjectAndGroupsPanel("projectAndGroups", model){

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void updateFromProject(AjaxRequestTarget target) {
						super.updateFromProject(target);
						
						try{
							RepositoryClient client = new RepositoryClient();
							DataSet dataSet = client.getDataSetSummaryWithDocs(model.getObject().getStudy().getIdCode(), SamlHelper.getSaml(session.getUser()));
							exportDetails.buildDocumentTree(dataSet, target);
						}
						catch(Exception ex){
							LOG.error("Exception when calling getDataSetSummaryWithDocs", ex);
							ErrorPanel.show(RequestExportForm.this.getParent(), RequestExportForm.this.getContainer(), target, ex);
							return;
						}
						
						
					}
				
			};
			
			final AjaxButton submit = new AjaxButton("submit", this){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onError(final AjaxRequestTarget target, Form<?> form) {
					super.onError(target, form);
					LOG.info("onError! "+Session.get().getFeedbackMessages().size()+" messages");
					projectAndGroups.showErrors(target);
					exportDetails.showErrors(target);
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					ExportRequestModel request = (ExportRequestModel)form.getModelObject();
					
					ExportRequest export = new ExportRequest();
					export.init();
					request.populateExportRequest(export);
					
					RepositoryClient client = new RepositoryClient();
					try{
						client.requestExport(export, SamlHelper.getSaml(session.getUser()));
						RequestExportForm.this.getParent().replaceWith(new RequestExportSuccessPanel("request"));
						target.addComponent(RequestExportForm.this.getContainer());
					}
					catch(Exception ex){
						LOG.error("Exception whilst requesting export", ex);
						ErrorPanel.show(RequestExportForm.this.getParent(), RequestExportForm.this.getContainer(), target, ex);
					}
					
				}
				
			};
			
			submitContainer1.add(submitContainer2);
			submitContainer2.add(submit);

			add(projectAndGroups);
			add(exportDetails);
			add(submitContainer1);
		}

		@Override
		protected void onValidate() {
			exportDetails.validate();
		}
		
	}
	
	public void renderHead(IHeaderResponse response) {
		response.renderOnLoadJavascript("selectStudy(\"study\")");
	}
	
}
