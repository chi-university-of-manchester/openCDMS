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

package org.opencdms.web.modules.export.panels;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.pages.ErrorPage;
import org.opencdms.web.core.panels.common.ErrorPanel;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.export.models.ViewExportModel;
import org.opencdms.web.modules.reports.repository.ReportResourceStream;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class ViewExportFormPanel extends Panel implements IHeaderContributor {

	private static final long serialVersionUID = 1L;
	
	private static final Log LOG = LogFactory.getLog(ViewExportFormPanel.class);
	
	/**
	 * @param id
	 */
	public ViewExportFormPanel(String id, Component container) {
		super(id);
		setOutputMarkupId(true);
		add(new ViewExportForm("viewExports", new CompoundPropertyModel<ViewExportModel>(new ViewExportModel()), container));
	}

	public static class ViewExportForm extends Form<ViewExportModel> {

		private static final long serialVersionUID = 1L;

		private static final SimpleDateFormat dateFormatter = 
			new SimpleDateFormat("HH:mm dd-MMM-yyyy");
		
		private final Component container;
		
		public Component getContainer() {
			return container;
		}

		/**
		 * @param id
		 * @param model
		 */
		@SuppressWarnings("serial")
		public ViewExportForm(String id, final IModel<ViewExportModel> model, Component container) {
			super(id, model);
			this.container = container;
			
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			
			final WebMarkupContainer requestsContainer1 = new WebMarkupContainer("requestsContainer1");
			requestsContainer1.setOutputMarkupId(true);
			final WebMarkupContainer requestsContainer2 = new WebMarkupContainer("requestsContainer2");
			requestsContainer2.setOutputMarkupId(true);
			requestsContainer2.setVisible(false);

			final DropDownChoice<ProjectType> study = 
				new DropDownChoice<ProjectType>(
					"study", 
					session.getUser().getExportableProjects(),
					new ChoiceRenderer<ProjectType>("name", "idCode"));
			study.setRequired(true);
			study.setMarkupId("study");

			final ListView<ExportRequest> requests = new ListView<ExportRequest>("requests"){

				@Override
				protected void populateItem(final ListItem<ExportRequest> item) {
					
					ExportRequest er = item.getModelObject();
					Label date = new Label("date", dateFormatter.format(er.getRequestDate()) );
					Label status = new Label("status", er.getStatus());
					Button download = new Button("download"){

						@Override
						public void onSubmit() {
							try{
								RepositoryClient client = new RepositoryClient();
								long id = item.getModelObject().getId();
								byte[] data = client.downloadExport(
										id, 
										SamlHelper.getSaml(session.getUser()));
								String fileName = "export"+id+".zip";
								String format = "application/zip";
								IResourceStream resourceStream = new ReportResourceStream(data, format);
								getRequestCycle().setRequestTarget(
										new ResourceStreamRequestTarget(resourceStream, fileName));
							}
							catch(Exception ex){
								LOG.error(ex);
								getRequestCycle().setResponsePage(new ErrorPage(new Model<Exception>(ex)));
							}
						}
						
					};
					AjaxButton cancel = new AjaxButton("cancel"){

						@Override
						protected void onSubmit(AjaxRequestTarget target,
								Form<?> form) {
							try{
								RepositoryClient client = new RepositoryClient();
								long id = item.getModelObject().getId();
								client.cancelExport(
										id, 
										SamlHelper.getSaml(session.getUser()));

								model.getObject().setRequests(
										client.getMyExportRequests(
												new String[]{model.getObject().getStudy().getIdCode()}, 
														SamlHelper.getSaml(session.getUser())));
								target.addComponent(requestsContainer1);
								
							}
							catch(Exception ex){
								LOG.error("Exception from getMyExportRequests", ex);
								ErrorPanel.show(ViewExportForm.this.getParent(), 
												ViewExportForm.this.getContainer(), 
												target, ex);
							}
							
						}
					};

					Button md5 = new Button("md5"){

						@Override
						public void onSubmit() {
							try{
								RepositoryClient client = new RepositoryClient();
								long id = item.getModelObject().getId();
								byte[] data = client.downloadExportHash(
										id, "MD5",
										SamlHelper.getSaml(session.getUser()));
								String fileName = "export"+id+".md5";
								String format = "text/plain";
								IResourceStream resourceStream = new ReportResourceStream(data, format);
								getRequestCycle().setRequestTarget(
										new ResourceStreamRequestTarget(resourceStream, fileName));
							}
							catch(Exception ex){
								LOG.error(ex);
								getRequestCycle().setResponsePage(new ErrorPage(new Model<Exception>(ex)));
							}
						}
						
					};
					Button sha1 = new Button("sha1"){

						@Override
						public void onSubmit() {
							try{
								RepositoryClient client = new RepositoryClient();
								long id = item.getModelObject().getId();
								byte[] data = client.downloadExportHash(
										id, "SHA1",
										SamlHelper.getSaml(session.getUser()));
								String fileName = "export"+id+".sha1";
								String format = "text/plain";
								IResourceStream resourceStream = new ReportResourceStream(data, format);
								getRequestCycle().setRequestTarget(
										new ResourceStreamRequestTarget(resourceStream, fileName));
							}
							catch(Exception ex){
								LOG.error(ex);
								getRequestCycle().setResponsePage(new ErrorPage(new Model<Exception>(ex)));
							}
						}
						
					};
					
					String reqStatus = item.getModelObject().getStatus();
					if ( ExportRequest.STATUS_COMPLETE.equals(reqStatus) ){
						cancel.setVisible(false);
					}
					if ( ExportRequest.STATUS_PENDING.equals(reqStatus) ){
						download.setVisible(false);
						md5.setVisible(false);
						sha1.setVisible(false);
					}
					if ( ExportRequest.STATUS_ERROR.equals(reqStatus) ||
							ExportRequest.STATUS_PROCESSING.equals(reqStatus) ||
							ExportRequest.STATUS_NO_DATA.equals(reqStatus)){
						cancel.setVisible(false);
						download.setVisible(false);
						md5.setVisible(false);
						sha1.setVisible(false);
					}
					
					item.add(date);
					item.add(status);
					item.add(download);
					item.add(cancel);
					item.add(md5);
					item.add(sha1);
				}
				
			};
			
			study.add(new AjaxFormComponentUpdatingBehavior("onchange"){

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					try{
						RepositoryClient client = new RepositoryClient();
						List<ExportRequest> reqs = 
							client.getMyExportRequests(
									new String[]{model.getObject().getStudy().getIdCode()}, 
									SamlHelper.getSaml(session.getUser()));
						
						model.getObject().setRequests(reqs);
						
						requestsContainer2.setVisible(true);
						target.addComponent(requestsContainer1);

					}
					catch(Exception ex){
						LOG.error("Exception from getMyExportRequests", ex);
						ErrorPanel.show(ViewExportForm.this.getParent(), 
										ViewExportForm.this.getContainer(), 
										target, ex);
					}
				}
				
			});
			
			requestsContainer1.add(requestsContainer2);
			requestsContainer2.add(requests);
			
			add(study);
			add(requestsContainer1);
			
		}
		
	}
	
	public void renderHead(IHeaderResponse response) {
		response.renderOnLoadJavascript("selectStudy(\"study\")");
	}
	
}
