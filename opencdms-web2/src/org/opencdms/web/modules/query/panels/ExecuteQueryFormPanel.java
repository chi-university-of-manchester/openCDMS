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

package org.opencdms.web.modules.query.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.panels.common.ErrorPanel;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.export.models.ExportDetailsModel;
import org.opencdms.web.modules.export.panels.RequestExportDetailsPanel;
import org.opencdms.web.modules.export.panels.RequestExportSuccessPanel;
import org.opencdms.web.modules.query.models.ExecuteQueryModel;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.query.IQuery;
import org.psygrid.data.query.client.QueryServiceClient;
import org.psygrid.data.repository.client.RepositoryClient;

/**
 * @author Rob Harper
 *
 */
public class ExecuteQueryFormPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(ExecuteQueryFormPanel.class);
	
	public ExecuteQueryFormPanel(String id, IModel<ExecuteQueryModel> model, Component container) {
		super(id, model);
		setOutputMarkupId(true);
		add(new ExecuteQueryForm("executeForm", model, container));
	}

	public static class ExecuteQueryForm extends Form<ExecuteQueryModel>{

		private static final long serialVersionUID = 1L;

		private final Component container;
		private Panel resultPanel;
		private final RequestExportDetailsPanel exportDetailsPanel;
		
		public Component getContainer() {
			return container;
		}

		@SuppressWarnings("serial")
		public ExecuteQueryForm(String id, final IModel<ExecuteQueryModel> model, Component container) {
			super(id, model);
			this.container = container;
			
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			
			final WebMarkupContainer runContainer1 = new WebMarkupContainer("runContainer1");
			runContainer1.setOutputMarkupId(true);
			final WebMarkupContainer runContainer2 = new WebMarkupContainer("runContainer2");
			runContainer2.setOutputMarkupId(true);
			runContainer2.setVisible(false);

			final WebMarkupContainer exportContainer1 = new WebMarkupContainer("exportContainer1");
			exportContainer1.setOutputMarkupId(true);
			final WebMarkupContainer exportContainer2 = new WebMarkupContainer("exportContainer2");
			exportContainer2.setOutputMarkupId(true);
			exportContainer2.setVisible(false);
			
			final WebMarkupContainer submitExportContainer1 = new WebMarkupContainer("submitExportContainer1");
			submitExportContainer1.setOutputMarkupId(true);
			final WebMarkupContainer submitExportContainer2 = new WebMarkupContainer("submitExportContainer2");
			submitExportContainer2.setOutputMarkupId(true);
			submitExportContainer2.setVisible(false);
			
			final WebMarkupContainer resultContainer = new WebMarkupContainer("resultContainer");
			resultContainer.setOutputMarkupId(true);
			
			Label query = new Label("query", model.getObject().getQuery().getName());
			
			final List<String> methods = new ArrayList<String>();
			methods.add("Count");
			methods.add("Identifiers");
			methods.add("Export");
			DropDownChoice<String> method = new DropDownChoice<String>(
					"method", methods);
			method.setRequired(true);
			
			final AjaxButton run = new AjaxButton("run"){

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					LOG.info("Method="+model.getObject().getMethod());
					try{
						RepositoryClient client = new RepositoryClient();
						QueryServiceClient qClient = new QueryServiceClient();
						if ( model.getObject().getMethod().equals("Count")){
							Long count = qClient.executeQueryForCount(model.getObject().getQuery().getId(), 
									SamlHelper.getSaml(session.getUser()));
							Panel countPanel = new ExecuteForCountResultPanel("resultPanel", count);
							resultPanel.replaceWith(countPanel);
							resultPanel = countPanel;
							target.addComponent(resultContainer);
						}
						else if ( model.getObject().getMethod().equals("Identifiers")){
							//The query currently in the model is just a stub so retrieve
							//the full query
							IQuery q = qClient.getQuery(
									model.getObject().getQuery().getId(), 
									SamlHelper.getSaml(session.getUser()));
							DataSet dataSet = client.getDataSetSummaryWithDocs(
                                    q.getDataSet().getProjectCode(),
                                    SamlHelper.getSaml(session.getUser()));
							boolean useExternalID = dataSet.getUseExternalIdAsPrimary();
							List<String> ids = null;
							// Conditionally show internal or external identifiers.
							if(useExternalID){
								ids = qClient.executeQueryForExternalIdentifiers(model.getObject().getQuery().getId(), 
										SamlHelper.getSaml(session.getUser()));								
							} else {
								ids = qClient.executeQueryForIdentifiers(model.getObject().getQuery().getId(), 
									SamlHelper.getSaml(session.getUser()));
							}
							Panel idsPanel = new ExecuteForIdentifiersResultPanel("resultPanel", ids);
							resultPanel.replaceWith(idsPanel);
							resultPanel = idsPanel;
							target.addComponent(resultContainer);
						}
						else{
							throw new RuntimeException("Unknown execution method "+model.getObject().getMethod());
						}
					}
					catch(Exception ex){
						LOG.error("Error whilst trying to run a query", ex);
						ErrorPanel.show(ExecuteQueryForm.this.getParent(), ExecuteQueryForm.this.getContainer(), target, ex);
					}
				}
				
			};
			
			resultPanel = new EmptyPanel("resultPanel");
			
			exportDetailsPanel = new RequestExportDetailsPanel(
					"exportDetailsPanel", 
					new CompoundPropertyModel<ExportDetailsModel>(model.getObject().getExportDetails())){

				@Override
				public void endOfPanel(AjaxRequestTarget target) {
					submitExportContainer2.setVisible(true);
					target.addComponent(submitExportContainer1);
				}
		

			};
			
			final AjaxButton submitExport = new AjaxButton("submitExport"){

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					super.onError(target, form);
					exportDetailsPanel.showErrors(target);
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					try{
						ExportRequest export = new ExportRequest();
						export.init();
						model.getObject().populateExportRequest(export);

						RepositoryClient client = new RepositoryClient();
						client.requestExport(export, SamlHelper.getSaml(session.getUser()));
						ExecuteQueryForm.this.getParent().replaceWith(
								new RequestExportSuccessPanel("execute"));
						target.addComponent(ExecuteQueryForm.this.getContainer());
					}
					catch(Exception ex){
						LOG.error("Exception whilst requesting export", ex);
						ErrorPanel.show(ExecuteQueryForm.this.getParent(), ExecuteQueryForm.this.getContainer(), target, ex);
					}
				}
				
			};
			
			method.add(new AjaxFormComponentUpdatingBehavior("onchange"){

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					if ( model.getObject().getMethod().equals("Export")){
						try{
							RepositoryClient client = new RepositoryClient();
							QueryServiceClient qClient = new QueryServiceClient();
							//The query currently in the model is just a stub so retrieve
							//the full query now
							IQuery q = qClient.getQuery(
									model.getObject().getQuery().getId(), 
									SamlHelper.getSaml(session.getUser()));
							model.getObject().setQuery(q);
							DataSet dataSet = client.getDataSetSummaryWithDocs(
                                    q.getDataSet().getProjectCode(),
                                    SamlHelper.getSaml(session.getUser()));
							exportDetailsPanel.buildDocumentTree(dataSet, target);
							
							runContainer2.setVisible(false);
							resultContainer.setVisible(false);
							target.addComponent(runContainer1);
														
							exportContainer2.setVisible(true);
							target.addComponent(exportContainer1);
						}
						catch(Exception ex){
							LOG.error(ex);
							ErrorPanel.show(ExecuteQueryForm.this.getParent(), ExecuteQueryForm.this.getContainer(), target, ex);
						}
					}
					else{
						exportContainer2.setVisible(false);
						target.addComponent(exportContainer1);
						
						runContainer2.setVisible(true);
						resultContainer.setVisible(true);
						Panel emptyPanel = new EmptyPanel("resultPanel");
						ExecuteQueryForm.this.resultPanel.replaceWith(emptyPanel);
						ExecuteQueryForm.this.resultPanel = emptyPanel;
						target.addComponent(runContainer1);
					}
				}
				
			});
			
			
			runContainer1.add(runContainer2);
			runContainer2.add(run);
			runContainer1.add(resultContainer);
			resultContainer.add(resultPanel);
			
			submitExportContainer1.add(submitExportContainer2);
			submitExportContainer2.add(submitExport);
			
			exportContainer1.add(exportContainer2);
			exportContainer2.add(exportDetailsPanel);
			exportContainer2.add(submitExportContainer1);
			
			add(query);
			add(method);
			add(runContainer1);
			add(exportContainer1);
		}
		
		@Override
		protected void onValidate() {
			exportDetailsPanel.validate();
		}

	}
	
}
