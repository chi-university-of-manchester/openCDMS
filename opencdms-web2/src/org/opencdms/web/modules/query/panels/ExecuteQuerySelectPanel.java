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
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.panels.common.ErrorPanel;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.query.models.ExecuteQueryModel;
import org.opencdms.web.modules.query.models.SelectQueryModel;
import org.opencdms.web.modules.query.models.StudyModel;
import org.psygrid.data.query.IQuery;
import org.psygrid.data.query.client.QueryServiceClient;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class ExecuteQuerySelectPanel extends Panel implements IHeaderContributor {
	
	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(ViewQueriesFormPanel.class);
	
	public ExecuteQuerySelectPanel(String id, Component container) {
		super(id);
		setOutputMarkupId(true);
		SelectQueryForm selectQueryForm = new SelectQueryForm("selectQueryForm", new CompoundPropertyModel<SelectQueryModel>(new SelectQueryModel()), container);
		SelectStudyForm selectStudyForm = new SelectStudyForm("selectStudyForm", new CompoundPropertyModel<StudyModel>(new StudyModel()), container, selectQueryForm);
		add(selectStudyForm);
		add(selectQueryForm);
	}

	public static class SelectStudyForm extends Form<StudyModel>{

		private static final long serialVersionUID = 1L;

		private final Component container;

		public Component getContainer() {
			return container;
		}

		@SuppressWarnings("serial")
		public SelectStudyForm(String id, final IModel<StudyModel> model, final Component container, 
				final SelectQueryForm myQueriesForm) {
			super(id, model);
			this.container = container;
			
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			
			final DropDownChoice<ProjectType> study = 
				new DropDownChoice<ProjectType>(
					"study", 
					session.getUser().getExportableProjects(),
					new ChoiceRenderer<ProjectType>("name", "idCode"));
			study.setRequired(true);
			study.setMarkupId("study");
		
			study.add(new AjaxFormComponentUpdatingBehavior("onchange"){

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					ProjectType study = model.getObject().getStudy();
					myQueriesForm.updateFromStudy(study, target);
				}
				
			});
			
			add(study);
		}

	}
	
	public static class SelectQueryForm extends Form<SelectQueryModel>{
		private static final long serialVersionUID = 1L;

		private final Component container;
		private final WebMarkupContainer queriesContainer1;
		private final WebMarkupContainer queriesContainer2;
		private final ListView<IQuery> queryList;
		private final WebMarkupContainer selectQueryContainer;
		private final WebMarkupContainer noQueriesContainer;

		public Component getContainer() {
			return container;
		}

		public WebMarkupContainer getQueriesContainer1() {
			return queriesContainer1;
		}

		public WebMarkupContainer getQueriesContainer2() {
			return queriesContainer2;
		}

		public ListView<IQuery> getQueryList() {
			return queryList;
		}

		public WebMarkupContainer getSelectQueryContainer() {
			return selectQueryContainer;
		}

		public WebMarkupContainer getNoQueriesContainer() {
			return noQueriesContainer;
		}

		@SuppressWarnings("serial")
		public SelectQueryForm(String id, final IModel<SelectQueryModel> model, Component container) {
			super(id, model);
			this.container = container;
			
			queriesContainer1 = new WebMarkupContainer("queriesContainer1");
			queriesContainer1.setOutputMarkupId(true);
			queriesContainer2 = new WebMarkupContainer("queriesContainer2");
			queriesContainer2.setOutputMarkupId(true);
			queriesContainer2.setVisible(false);
			
			selectQueryContainer = new WebMarkupContainer("selectQueryContainer");
			selectQueryContainer.setOutputMarkupId(true);
			selectQueryContainer.setVisible(false);
			noQueriesContainer = new WebMarkupContainer("noQueriesContainer");
			noQueriesContainer.setOutputMarkupId(true);
			noQueriesContainer.setVisible(false);
			
			queryList = new ListView<IQuery>(
					"queryList", new ArrayList<IQuery>()){

				@Override
				protected void populateItem(ListItem<IQuery> item) {

					final IQuery query = item.getModelObject();
					
					Label name = new Label("name", query.getName());
					Label description = new Label("description", query.getDescription());
					
					AjaxButton execute = new AjaxButton("execute"){

						@Override
						protected void onSubmit(AjaxRequestTarget target,
								Form<?> form) {
							try{
								SelectQueryForm.this.getParent().replaceWith(
										new ExecuteQueryFormPanel(
												"execute", 
												new CompoundPropertyModel<ExecuteQueryModel>(new ExecuteQueryModel(query)), 
												SelectQueryForm.this.getContainer()));
								target.addComponent(SelectQueryForm.this.getContainer());

							}
							catch(Exception ex){
								LOG.error("Error saving query", ex);
								ErrorPanel.show(SelectQueryForm.this.getParent(), SelectQueryForm.this.getContainer(), target, ex);
							}

						}

					};
										
					item.add(name);
					item.add(description);
					item.add(execute);
				}
			};
						
			queriesContainer1.add(queriesContainer2);
			queriesContainer2.add(selectQueryContainer);
			selectQueryContainer.add(queryList);
			queriesContainer2.add(noQueriesContainer);
			
			add(queriesContainer1);
		}

		/**
		 * Show the list of queries after a study is selected.
		 *  
		 * @param study The selected study.
		 * @param target
		 */
		public void updateFromStudy(ProjectType study, AjaxRequestTarget target){
			try{
				QueryServiceClient client = new QueryServiceClient();
				OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
				List<IQuery> queries = client.getMyQueries(study.getIdCode(), SamlHelper.getSaml(session.getUser()));
				getQueryList().setList(queries);
				if ( queries.isEmpty() ){
					getNoQueriesContainer().setVisible(true);
					getSelectQueryContainer().setVisible(false);
				}
				else{
					getSelectQueryContainer().setVisible(true);
					getNoQueriesContainer().setVisible(false);
				}
				getQueriesContainer2().setVisible(true);
				target.addComponent(getQueriesContainer1());
			}
			catch(Exception ex){
				LOG.error(ex);
				ErrorPanel.show(getParent(), getContainer(), target, ex);
			}
		}

	}
	
	public void renderHead(IHeaderResponse response) {
		response.renderOnLoadJavascript("selectStudy(\"study\")");
	}
	
}
