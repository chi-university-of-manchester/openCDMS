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
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.panels.common.ErrorPanel;
import org.opencdms.web.core.panels.common.ProjectAndGroupsPanel;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.query.models.QueryModel;
import org.opencdms.web.modules.query.models.QueryModel.QueryStatement;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.query.IDateStatement;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.IIntegerStatement;
import org.psygrid.data.query.INumericStatement;
import org.psygrid.data.query.IOptionStatement;
import org.psygrid.data.query.IQuery;
import org.psygrid.data.query.IStatement;
import org.psygrid.data.query.ITextStatement;
import org.psygrid.data.query.QueryFactory;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.client.QueryServiceClient;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.data.query.hibernate.HibernateQueryFactory;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.www.xml.security.core.types.GroupType;


/**
 * @author Rob Harper
 *
 */
public class BuildQueryFormPanel extends Panel implements IHeaderContributor {

	private static final long serialVersionUID = 1L;
	
	private static final Log LOG = LogFactory.getLog(BuildQueryFormPanel.class);
	
	public BuildQueryFormPanel(String id, Component container) {
		super(id);
		setOutputMarkupId(true);
		add(new BuildQueryForm("queryForm", new CompoundPropertyModel<QueryModel>(new QueryModel()), container));
	}

	public BuildQueryFormPanel(String id, IModel<QueryModel> model, Component container){
		super(id, model);
		setOutputMarkupId(true);
		add(new BuildQueryForm("queryForm", model, container));
	}
	
	public static class BuildQueryForm extends Form<QueryModel>{

		private static final long serialVersionUID = 1L;

		private final Component container;
		private ListEditor<QueryStatement> statements;
		
		public Component getContainer() {
			return container;
		}

		@SuppressWarnings("serial")
		public BuildQueryForm(String id, final IModel<QueryModel> model, Component container) {
			super(id, model);
			this.container = container;
			
			
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();

			final WebMarkupContainer operatorContainer1 = new WebMarkupContainer("operatorContainer1");
			operatorContainer1.setOutputMarkupId(true);
			final WebMarkupContainer operatorContainer2 = new WebMarkupContainer("operatorContainer2");
			operatorContainer2.setOutputMarkupId(true);
			
			final WebMarkupContainer statementsContainer1 = new WebMarkupContainer("statementsContainer1");
			statementsContainer1.setOutputMarkupId(true);
			final WebMarkupContainer statementsContainer2 = new WebMarkupContainer("statementsContainer2");
			statementsContainer2.setOutputMarkupId(true);
			
			final WebMarkupContainer statementsTable = new WebMarkupContainer("statementsTable");
			statementsTable.setOutputMarkupId(true);
			
			final WebMarkupContainer queryNameContainer1 = new WebMarkupContainer("queryNameContainer1");
			queryNameContainer1.setOutputMarkupId(true);
			final WebMarkupContainer queryNameContainer2 = new WebMarkupContainer("queryNameContainer2");
			queryNameContainer2.setOutputMarkupId(true);
			
			final WebMarkupContainer submitContainer1 = new WebMarkupContainer("submitContainer1");
			submitContainer1.setOutputMarkupId(true);
			final WebMarkupContainer submitContainer2 = new WebMarkupContainer("submitContainer2");
			submitContainer2.setOutputMarkupId(true);
			
			List<String> operators = new ArrayList<String>();
			operators.add("AND");
			operators.add("OR");
			final RadioChoice<String> operator = new RadioChoice<String>(
					"operator",
					operators);
			
			statements = new ListEditor<QueryStatement>(
					"statements", new PropertyModel<List<QueryStatement>>(model, "statements")){

				private static final long serialVersionUID = 1L;

				@Override
				public boolean canRemove(List<QueryStatement> items,
						QueryStatement item) {
					if ( items.size() > 1 ){
						return true;
					}
					return false;
				}

				@Override
				protected void onPopulateItem(final ListItem<QueryStatement> item) {
					
					final QueryStatement qs = item.getModelObject();
					
					if ( null != model.getObject().getQuery() ){
						//for an existing statement we need to replace the document
						//and entry as they are just stubs
						DataSet ds = model.getObject().getDataSet();
						loop:
						for ( int i=0, c=ds.numDocuments(); i<c; i++ ){
							Document doc = ds.getDocument(i);
							for ( int j=0, d=doc.numOccurrences(); j<d; j++ ){
								DocumentOccurrence occ = doc.getOccurrence(j);
								if ( occ.equals(qs.getDocument())){
									qs.setDocument(occ);
									for ( int k=0, e=doc.numEntries(); k<e; k++ ){
										Entry entry = doc.getEntry(k);
										if ( entry.equals(qs.getEntry())){
											qs.setEntry(entry);
											break loop;
										}
									}
								}
							}
						}
					}
					
					final DropDownChoice<DocumentOccurrence> docOccs =
						new DropDownChoice<DocumentOccurrence>(
							"document", 
							new PropertyModel<DocumentOccurrence>(item.getModelObject(), "document"),
							getDocumentList(model.getObject().getDataSet()),
							new ChoiceRenderer<DocumentOccurrence>("combinedDisplayText", "id"));
					docOccs.setRequired(true);
					
					final DropDownChoice<Entry> entries =
						new DropDownChoice<Entry>(
							"entry",
							new PropertyModel<Entry>(item.getModelObject(), "entry"),
							new ArrayList<Entry>(),
							new ChoiceRenderer<Entry>("displayText", "id"));
					entries.setOutputMarkupId(true);
					entries.setRequired(true);
					
					final DropDownChoice<QueryOperation> operation = 
						new DropDownChoice<QueryOperation>(
							"operator",
							new PropertyModel<QueryOperation>(item.getModelObject(), "operator"),
							new ArrayList<QueryOperation>());
					operation.setOutputMarkupId(true);
					operation.setRequired(true);
					operation.setChoiceRenderer(new IChoiceRenderer<QueryOperation>(){

						public Object getDisplayValue(QueryOperation object) {
							return object.getForDisplay();
						}

						public String getIdValue(QueryOperation object,
								int index) {
							return object.toString();
						}
						
					});
					
					final WebMarkupContainer valuesContainer = new WebMarkupContainer("valuesContainer");
					valuesContainer.setOutputMarkupId(true);
					
					final RequiredTextField<String> textValue =
						new RequiredTextField<String>(
							"textValue",
							new PropertyModel<String>(item.getModelObject(), "textValue"));
					
					final RequiredTextField<Integer> integerValue =
						new RequiredTextField<Integer>(
							"integerValue",
							new PropertyModel<Integer>(item.getModelObject(), "integerValue"));
					
					final RequiredTextField<Double> doubleValue =
						new RequiredTextField<Double>(
							"doubleValue",
							new PropertyModel<Double>(item.getModelObject(), "doubleValue"));
					
					final DropDownChoice<Option> optionValue =
						new DropDownChoice<Option>(
							"optionValue",
							new PropertyModel<Option>(item.getModelObject(), "optionValue"),
							new ArrayList<Option>(),
							new ChoiceRenderer<Option>("displayText", "id"));
					optionValue.setRequired(true);
					
					final DateField dateValue = new DateField("dateValue", new PropertyModel<Date>(item.getModelObject(), "dateValue"));
					dateValue.setRequired(true);
					
					docOccs.add(new AjaxFormComponentUpdatingBehavior("onchange"){

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							qs.setEntry(null);
							entries.setChoices(getEntryList(qs.getDocument().getDocument()));
							target.addComponent(entries);
							
							//clear any previous selections of dependencies
							qs.setOperator(null);
							operation.setChoices(new ArrayList<QueryOperation>());
							target.addComponent(operation);
							
							qs.clearValues();
							textValue.setVisible(false);
							integerValue.setVisible(false);
							doubleValue.setVisible(false);
							optionValue.setVisible(false);
							dateValue.setVisible(false);
							target.addComponent(valuesContainer);
						}
						
					});
					
					entries.add(new AjaxFormComponentUpdatingBehavior("onchange"){

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							qs.setOperator(null);
							operation.setChoices(item.getModelObject().getEntry().getQueryOperations());
							target.addComponent(operation);
							
							//clear any previous selection of dependencies
							qs.clearValues();
							textValue.setVisible(false);
							integerValue.setVisible(false);
							doubleValue.setVisible(false);
							optionValue.setVisible(false);
							dateValue.setVisible(false);
							target.addComponent(valuesContainer);
						}
						
					});
					
					operation.add(new AjaxFormComponentUpdatingBehavior("onchange"){

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							qs.clearValues();
							hideValues(textValue, integerValue, doubleValue, optionValue, dateValue);
							showValue(qs, textValue, integerValue, doubleValue, optionValue, dateValue);
							target.addComponent(valuesContainer);
						}
						
					});
					
					hideValues(textValue, integerValue, doubleValue, optionValue, dateValue);
					if ( null != qs.getStatement() ){
						entries.setChoices(getEntryList(qs.getDocument().getDocument()));
						operation.setChoices(qs.getEntry().getQueryOperations());
						showValue(qs, textValue, integerValue, doubleValue, optionValue, dateValue);
						docOccs.setEnabled(false);
						entries.setEnabled(false);
					}
					
					final WebMarkupContainer statementInner = new WebMarkupContainer("statementInner");

					Label index = new Label("index", new PropertyModel<Integer>(item.getModelObject(), "index"));
					index.setOutputMarkupId(true);
					item.add(index);
					
					valuesContainer.add(textValue);
					valuesContainer.add(integerValue);
					valuesContainer.add(doubleValue);
					valuesContainer.add(optionValue);
					valuesContainer.add(dateValue);
					
					statementInner.add(docOccs);
					statementInner.add(entries);
					statementInner.add(operation);
					statementInner.add(valuesContainer);
					
					item.add(new AjaxRemoveButton("remove"){
	                	protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
	                		List<?> statements = getList();
	                		super.onSubmit(target, form);
	                		
	                		//reset indices of remaining statements
	                		int counter = 1;
	                		for ( Object o: statements ){
	                			QueryStatement qs = (QueryStatement)o;
	                			qs.setIndex(counter);
	                			counter++;
	                		}
	                		
	                		target.addComponent(statementsTable);
	                		if ( null != qs.getStatement() ){
	                			//existing statement being deleted
	                			model.getObject().getQuery().deleteStatement(qs.getStatement());
	                		}
	                	}
	                });
	                
	                item.add(statementInner);
				}

				private List<DocumentOccurrence> getDocumentList(DataSet dataSet){
					List<DocumentOccurrence> docOccs = new ArrayList<DocumentOccurrence>();
					for ( int i=0, c=dataSet.numDocumentGroups(); i<c; i++ ){
						DocumentGroup group = dataSet.getDocumentGroup(i);
						for ( int j=0, d=dataSet.numDocuments(); j<d; j++ ){
							Document doc = dataSet.getDocument(j);
							for ( int k=0, e=doc.numOccurrences(); k<e; k++ ){
								DocumentOccurrence  docOcc = doc.getOccurrence(k);
								if ( docOcc.getDocumentGroup().equals(group) ){
									docOccs.add(docOcc);
								}
							}
						}
					}
					return docOccs;
				}
				
				private List<Entry> getEntryList(Document document){
					List<Entry> entryList = new ArrayList<Entry>();
					for ( int i=0, c=document.numEntries(); i<c; i++ ){
						Entry e  = document.getEntry(i);
						if ( e.isQueryable() ){
							entryList.add(document.getEntry(i));
						}
					}
					return entryList;
				}
				
				private void hideValues(RequiredTextField<String> textValue,
										RequiredTextField<Integer> integerValue,
										RequiredTextField<Double> doubleValue,
										DropDownChoice<Option> optionValue,
										DateField dateValue){
					textValue.setVisible(false);
					integerValue.setVisible(false);
					doubleValue.setVisible(false);
					optionValue.setVisible(false);
					dateValue.setVisible(false);
				}
				
				private void showValue(QueryStatement qs,
									   RequiredTextField<String> textValue,
									   RequiredTextField<Integer> integerValue,
									   RequiredTextField<Double> doubleValue,
									   DropDownChoice<Option> optionValue,
									   DateField dateValue){
					if ( qs.textValueRequired() ){
						textValue.setVisible(true);
					}
					if ( qs.integerValueRequired() ){
						integerValue.setVisible(true);
					}
					if ( qs.doubleValueRequired() ){
						doubleValue.setVisible(true);
					}
					if ( qs.optionValueRequired() ){
						OptionEntry oe = (OptionEntry)qs.getEntry();
						List<Option> options = new ArrayList<Option>();
						for ( int i=0, c=oe.numOptions(); i<c; i++ ){
							options.add(oe.getOption(i));
						}
						optionValue.setChoices(options);
						optionValue.setVisible(true);
					}
					if ( qs.dateValueRequired() ){
						dateValue.setVisible(true);
					}
				}
			};
			
			final ComponentFeedbackPanel statementsFeedback = new ComponentFeedbackPanel("statementsFeedback", statements);
			statementsFeedback.setOutputMarkupId(true);
			statementsFeedback.setMaxMessages(1);
			statementsFeedback.setFilter(
					new ContainerFeedbackMessageFilter(statements){
						/*
						private transient Set<String> messages = new HashSet<String>();
						
						@Override
						public boolean accept(FeedbackMessage message) {
							// TODO Auto-generated method stub
							if ( super.accept(message) ){
								LOG.info("messages.size="+messages.size());
								if ( !messages.contains(((ValidationErrorFeedback)message.getMessage()).getMessage())){
									messages.add(((ValidationErrorFeedback)message.getMessage()).getMessage());
									return true;
								}
							}
							return false;
						}
						*/
						
					});
			
			final ProjectAndGroupsPanel projectAndGroups = 
				new ProjectAndGroupsPanel("projectAndGroups", model){

					@Override
					public void updateFromProject(AjaxRequestTarget target) {
						super.updateFromProject(target);
						
						RepositoryClient client = new RepositoryClient();
						DataSet ds = null;
						try{
							ds = client.getDataSetSummaryWithDocs(
									model.getObject().getStudy().getIdCode(), 
									SamlHelper.getSaml(session.getUser()));
							model.getObject().setDataSet(ds);
						}
						catch(Exception ex){
							LOG.error("Exception when calling getDataSetSummaryWithDocs", ex);
							ErrorPanel.show(BuildQueryForm.this.getParent(), BuildQueryForm.this.getContainer(), target, ex);
						}
							
						operatorContainer2.setVisible(true);
						target.addComponent(operatorContainer1);
	
						statementsContainer2.setVisible(true);
						target.addComponent(statementsContainer1);
						
						statements.addItem(new QueryStatement(statements.size()+1));
						target.addComponent(statementsTable);
	
						queryNameContainer2.setVisible(true);
						target.addComponent(queryNameContainer1);
	
						submitContainer2.setVisible(true);
						target.addComponent(submitContainer1);

					}

			};
			
			final AjaxButton addStatement = new AjaxButton("addStatement", this){

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					LOG.info("add clicked");
					statements.addItem(new QueryStatement(statements.size()+1));
					target.addComponent(statementsTable);
				}
				
			};
			addStatement.setDefaultFormProcessing(false);
						
			final RequiredTextField<String> queryName = new RequiredTextField<String>("queryName");
			queryName.add(new StringValidator.MaximumLengthValidator(255));
			
			final TextArea<String> queryDescription = new TextArea<String>("queryDescription");
			queryDescription.add(new StringValidator.MaximumLengthValidator(1024));
			
			final AjaxButton submit = new AjaxButton("submit", this){

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					super.onError(target, form);
					projectAndGroups.showErrors(target);
					target.addComponent(queryNameContainer1);
					target.addComponent(statementsFeedback);
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					try{
						IQuery q = createQuery(model.getObject());
						QueryServiceClient client = new QueryServiceClient();
						client.saveQuery(q, SamlHelper.getSaml(session.getUser()));
						
						BuildQueryForm.this.getParent().replaceWith(new BuildQuerySuccessPanel(BuildQueryForm.this.getParent().getId()));
						target.addComponent(BuildQueryForm.this.getContainer());
					}
					catch(Exception ex){
						LOG.error("Error saving query", ex);
						ErrorPanel.show(BuildQueryForm.this.getParent(), BuildQueryForm.this.getContainer(), target, ex);
					}
				}
				
				private IQuery createQuery(QueryModel qm){
					QueryFactory factory = new HibernateQueryFactory();
					IQuery q = null;
					if ( null == qm.getQuery()){
						q = factory.createQuery();
					}
					else{
						q = qm.getQuery();
					}
					q.setName(qm.getQueryName());
					q.setDescription(qm.getQueryDescription());
					q.setOperator(qm.getOperator());
					if ( qm.getVisibility().equals("Public")){
						q.setPubliclyVisible(true);
					}
					q.setDataSet(qm.getDataSet());
					
					//look through the query's groups to see if any have been removed
					List<Group> groupsToRemove = new ArrayList<Group>();
					for ( int i=0, c=q.groupCount(); i<c; i++ ){
						Group g = q.getGroup(i);
						boolean found = false;
						for ( GroupType gt: qm.getCentres() ){
							if ( g.getName().equals(gt.getIdCode()) ){
								found = true;
								break;
							}
						}
						if ( !found ){
							groupsToRemove.add(g);
						}
					}
					for ( Group g: groupsToRemove ){
						q.removeGroup(g);
					}
					
					//add groups
					for ( GroupType gt: qm.getCentres() ){
						for ( int i=0, c=qm.getDataSet().numGroups(); i<c; i++ ){
							Group g = qm.getDataSet().getGroup(i);
							if (g.getName().equals(gt.getIdCode())){
								q.addGroup(g);
								break;
							}
						}
					}
					
					for ( QueryStatement qs: qm.getStatements() ){
						IStatement stat = null;
						if ( null == qs.getStatement() ){
							//A new statement that has not previously been saved
							if ( null != qs.getEntry() ){
								stat = createNewEntryStatement(qs, factory);
							}
							/* MM - I suspect that this statement should be inside the preceding if block,
							 * otherwise it's possible to add "stat" when it has the value null. Without a better understanding
							 * of how this will be used, it's hard to be sure though.
							 */
							q.addStatement(stat);
						}
						else{
							//existing statement that is being edited
							modifyExistingEntryStatement(qs);
						}						
					}
					
					return q;
				}
				
				private IEntryStatement createNewEntryStatement(QueryStatement queryStatement, QueryFactory factory) {
					// Convert the relevant part of the queryStatement to pass across to the server
					QueryStatementValue value = queryStatement.toQueryStatementValue();
					IEntryStatement entryStatement = queryStatement.getEntry().createStatement(value);
					entryStatement.setEntry(queryStatement.getEntry());
					entryStatement.setDocOcc(queryStatement.getDocument());
					entryStatement.setOperator(queryStatement.getOperator());
					
					return entryStatement;
				}
				
				private void modifyExistingEntryStatement(QueryStatement queryStatement) {
					IStatement statement = queryStatement.getStatement();
					if ( statement instanceof IEntryStatement ){
						if ( statement instanceof INumericStatement ){
							((INumericStatement)statement).setValue(queryStatement.getDoubleValue());
						}
						if ( statement instanceof IIntegerStatement ){
							((IIntegerStatement)statement).setValue(queryStatement.getIntegerValue());
						}
						if ( statement instanceof IDateStatement ){
							((IDateStatement)statement).setValue(queryStatement.getDateValue());
						}
						if ( statement instanceof IOptionStatement ){
							((IOptionStatement)statement).setValue(queryStatement.getOptionValue());
						}
						if ( statement instanceof ITextStatement ){
							((ITextStatement)statement).setValue(queryStatement.getTextValue());
						}
						((IEntryStatement)statement).setOperator(queryStatement.getOperator());
					}
				}
				
			};
			
			//set initial state of components
			if ( null == model.getObject().getQuery() ){
				//building a new query
				operatorContainer2.setVisible(false);
				statementsContainer2.setVisible(false);
				queryNameContainer2.setVisible(false);
				submitContainer2.setVisible(false);
			}
			else{
				//editing an existing query
				projectAndGroups.disableStudyAndShowGroups();
			}
			
			add(projectAndGroups);
			operatorContainer1.add(operatorContainer2);
			operatorContainer2.add(operator);
			add(operatorContainer1);
			statementsContainer1.add(statementsContainer2);
			statementsContainer2.add(addStatement);
			add(statementsContainer1);
			statementsTable.add(statements);
			add(statementsTable);
			add(statementsFeedback);
			queryNameContainer1.add(queryNameContainer2);
			queryNameContainer2.add(queryName);
			queryNameContainer2.add(new ComponentFeedbackPanel("queryNameFeedback", queryName));
			queryNameContainer2.add(queryDescription);
			queryNameContainer2.add(new ComponentFeedbackPanel("queryDescriptionFeedback", queryDescription));
			add(queryNameContainer1);
			submitContainer1.add(submitContainer2);
			submitContainer2.add(submit);
			add(submitContainer1);
		}
		
	}

	public void renderHead(IHeaderResponse response) {
		response.renderOnLoadJavascript("selectStudy(\"study\")");
	}
	
}
