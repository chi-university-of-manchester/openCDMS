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
import org.opencdms.web.modules.query.models.QueryModel;
import org.opencdms.web.modules.query.models.SelectQueryModel;
import org.opencdms.web.modules.query.models.StudyModel;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.query.IQuery;
import org.psygrid.data.query.client.QueryServiceClient;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class ViewQueriesFormPanel extends Panel implements IHeaderContributor {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(ViewQueriesFormPanel.class);
	
	public ViewQueriesFormPanel(String id, Component container) {
		super(id);
		setOutputMarkupId(true);
		SelectQueryForm myQueriesForm = new SelectQueryForm("myQueriesForm", new CompoundPropertyModel<SelectQueryModel>(new SelectQueryModel()), container);
		SelectStudyForm selectStudyForm = new SelectStudyForm("selectStudyForm", new CompoundPropertyModel<StudyModel>(new StudyModel()), container, myQueriesForm);
		add(selectStudyForm);
		add(myQueriesForm);
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

		public static final String EXECUTE_OPERATION = "Execute";

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
			
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			
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
					
					AjaxButton view = new AjaxButton("view"){

						@Override
						protected void onSubmit(AjaxRequestTarget target,
								Form<?> form) {
							try{
								QueryServiceClient qsc = new QueryServiceClient();
								IQuery fullQuery = qsc.getQuery(query.getId(), SamlHelper.getSaml(session.getUser()));
								//the dataset attached to the query is fairly minimal - need more
								//to be able to edit the query
								RepositoryClient rc = new RepositoryClient();
								DataSet ds = rc.getDataSetSummaryWithDocs(fullQuery.getDataSet().getProjectCode(), SamlHelper.getSaml(session.getUser()));
								fullQuery.setDataSet(ds);
								//find project
								ProjectType study = null;
								for ( ProjectType pt: session.getUser().getQueryableProjects() ){
									if ( pt.getIdCode().equals(fullQuery.getDataSet().getProjectCode())){
										study = pt;
										break;
									}
								}
								if ( null == study ){
									throw new RuntimeException("Unable to find project "+fullQuery.getDataSet().getProjectCode());
								}
								//find groups
								List<GroupType> groups = new ArrayList<GroupType>();
								for ( int i=0, c=fullQuery.groupCount(); i<c; i++ ){
									Group g = fullQuery.getGroup(i);
									for ( GroupType gt: session.getUser().getGroups().get(study)){
										if ( gt.getIdCode().equals(g.getName())){
											groups.add(gt);
											break;
										}
									}
								}
								if ( groups.size() != fullQuery.groupCount() ){
									throw new RuntimeException("Not all groups could be found");
								}
								
								SelectQueryForm.this.getParent().replaceWith(
										new BuildQueryFormPanel(
												"view", 
												new CompoundPropertyModel<QueryModel>(new QueryModel(fullQuery, study, groups)), 
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
					item.add(view);
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
					getNoQueriesContainer().setVisible(false);
					getSelectQueryContainer().setVisible(true);
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
