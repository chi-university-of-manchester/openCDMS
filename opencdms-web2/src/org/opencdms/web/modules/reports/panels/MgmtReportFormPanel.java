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

package org.opencdms.web.modules.reports.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.models.MonthAndYearModel;
import org.opencdms.web.core.pages.ErrorPage;
import org.opencdms.web.core.panels.common.ErrorPanel;
import org.opencdms.web.core.panels.common.MonthAndYearPanel;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.core.validators.StartAndEndDateValidator;
import org.opencdms.web.modules.reports.models.MgmtReportModel;
import org.opencdms.web.modules.reports.models.ReportModel;
import org.opencdms.web.modules.reports.models.MgmtReportModel.MonthlyTarget;
import org.opencdms.web.modules.reports.repository.ReportResourceStream;
import org.opencdms.web.modules.reports.repository.ReportingClient;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.definition.hibernate.BasicStatisticsChart;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class MgmtReportFormPanel extends Panel implements IHeaderContributor {

	private static final Log LOG = LogFactory.getLog(TrendReportPanel.class);
	
	private static final long serialVersionUID = 1L;

	public MgmtReportFormPanel(String id, Component container){
		super(id);
		setOutputMarkupId(true);
		add(new ReportForm("reportForm", new CompoundPropertyModel<MgmtReportModel>(new MgmtReportModel()), container));
	}
	
	public static class ReportForm extends Form<MgmtReportModel>{

		private static final long serialVersionUID = 1L;

		private final Component container;
		
		public Component getContainer() {
			return container;
		}

		/**
		 * @param id
		 * @param model
		 */
		@SuppressWarnings("serial")
		public ReportForm(final String id, final IModel<MgmtReportModel> model, Component container) {
			super(id, model);
			this.container = container;
			
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			
			final WebMarkupContainer reportContainer1 = new WebMarkupContainer("reportContainer1");
			reportContainer1.setOutputMarkupId(true);
			final WebMarkupContainer reportContainer2 = new WebMarkupContainer("reportContainer2");
			reportContainer2.setOutputMarkupId(true);
			reportContainer2.setVisible(false);

			final WebMarkupContainer centresContainer1 = new WebMarkupContainer("centresContainer1");
			centresContainer1.setOutputMarkupId(true);
			final WebMarkupContainer centresContainer2 = new WebMarkupContainer("centresContainer2");
			centresContainer2.setOutputMarkupId(true);
			centresContainer2.setVisible(false);

			final WebMarkupContainer datesContainer1 = new WebMarkupContainer("datesContainer1");
			datesContainer1.setOutputMarkupId(true);
			final WebMarkupContainer datesContainer2 = new WebMarkupContainer("datesContainer2");
			datesContainer2.setOutputMarkupId(true);
			datesContainer2.setVisible(false);

			final WebMarkupContainer targetTypeContainer1 = new WebMarkupContainer("targetTypeContainer1");
			targetTypeContainer1.setOutputMarkupId(true);
			final WebMarkupContainer targetTypeContainer2 = new WebMarkupContainer("targetTypeContainer2");
			targetTypeContainer2.setOutputMarkupId(true);
			targetTypeContainer2.setVisible(false);

			final WebMarkupContainer allMonthsContainer1 = new WebMarkupContainer("allMonthsContainer1");
			allMonthsContainer1.setOutputMarkupId(true);
			final WebMarkupContainer allMonthsContainer2 = new WebMarkupContainer("allMonthsContainer2");
			allMonthsContainer2.setOutputMarkupId(true);
			allMonthsContainer2.setVisible(false);

			final WebMarkupContainer perMonthContainer1 = new WebMarkupContainer("perMonthContainer1");
			perMonthContainer1.setOutputMarkupId(true);
			final WebMarkupContainer perMonthContainer2 = new WebMarkupContainer("perMonthContainer2");
			perMonthContainer2.setOutputMarkupId(true);
			perMonthContainer2.setVisible(false);

			final WebMarkupContainer documentContainer1 = new WebMarkupContainer("documentContainer1");
			documentContainer1.setOutputMarkupId(true);
			final WebMarkupContainer documentContainer2 = new WebMarkupContainer("documentContainer2");
			documentContainer2.setOutputMarkupId(true);
			documentContainer2.setVisible(false);

			final WebMarkupContainer entryContainer1 = new WebMarkupContainer("entryContainer1");
			entryContainer1.setOutputMarkupId(true);
			final WebMarkupContainer entryContainer2 = new WebMarkupContainer("entryContainer2");
			entryContainer2.setOutputMarkupId(true);
			entryContainer2.setVisible(false);

			final WebMarkupContainer statContainer1 = new WebMarkupContainer("statContainer1");
			statContainer1.setOutputMarkupId(true);
			final WebMarkupContainer statContainer2 = new WebMarkupContainer("statContainer2");
			statContainer2.setOutputMarkupId(true);
			statContainer2.setVisible(false);

			final WebMarkupContainer formatContainer1 = new WebMarkupContainer("formatContainer1");
			formatContainer1.setOutputMarkupId(true);
			final WebMarkupContainer formatContainer2 = new WebMarkupContainer("formatContainer2");
			formatContainer2.setOutputMarkupId(true);
			formatContainer2.setVisible(false);

			final WebMarkupContainer submitContainer1 = new WebMarkupContainer("submitContainer1");
			submitContainer1.setOutputMarkupId(true);
			final WebMarkupContainer submitContainer2 = new WebMarkupContainer("submitContainer2");
			submitContainer2.setOutputMarkupId(true);
			submitContainer2.setVisible(false);

			final DropDownChoice<ProjectType> study = 
				new DropDownChoice<ProjectType>(
					"study", 
					session.getUser().getExportableProjects(),
					new ChoiceRenderer<ProjectType>("name", "idCode"));
			study.setRequired(true);
			study.setMarkupId("study");
		
			final DropDownChoice<ReportModel.Report> report = 
				new DropDownChoice<ReportModel.Report>(
					"report", 
					new ArrayList<ReportModel.Report>(),
					new ChoiceRenderer<ReportModel.Report>("reportName", "reportId"));
			report.setRequired(true);
			
			final CheckBoxMultipleChoice<GroupType> centres = new CheckBoxMultipleChoice<GroupType>("centres");
			centres.setChoiceRenderer(new ChoiceRenderer<GroupType>("name", "idCode"));
			centres.setOutputMarkupId(true);
			centres.setRequired(true);

			IModel<MonthAndYearModel> startDateModel = new PropertyModel<MonthAndYearModel>(model, "startDate");
			final MonthAndYearPanel startDatePanel = new MonthAndYearPanel("startDate", startDateModel);
			
			IModel<MonthAndYearModel> endDateModel = new PropertyModel<MonthAndYearModel>(model, "endDate");
			final MonthAndYearPanel endDatePanel = new MonthAndYearPanel("endDate", endDateModel);
			
			final AjaxButton confirmDates = new AjaxButton("confirmDates"){

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					super.onError(target, form);
					target.addComponent(datesContainer1);
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					targetTypeContainer2.setVisible(true);
					target.addComponent(targetTypeContainer1);
				}
				
			};
			
			List<String> targetTypes = new ArrayList<String>();
			targetTypes.add("All months");
			targetTypes.add("Per month");
			final DropDownChoice<String> targetType = new DropDownChoice<String>(
					"targetType",
					targetTypes);
			
			final TextField<Integer> allMonths = new TextField<Integer>(
					"allMonths");
			
			final ListView<MonthlyTarget> perMonth = new ListView<MonthlyTarget>(
					"perMonth", new PropertyModel<List<MonthlyTarget>>(model, "perMonth")){

						@Override
						protected void populateItem(ListItem<MonthlyTarget> item) {
							final Label month = new Label(
									"month", new PropertyModel<String>(item.getModelObject(), "month"));
							final RequiredTextField<Integer> target = new RequiredTextField<Integer>(
									"target", new PropertyModel<Integer>(item.getModelObject(), "target"));							
							item.add(month);
							item.add(target);
						}
			};
			
			final FeedbackPanel perMonthFeedback = new FeedbackPanel("perMonthFeedback");
			perMonthFeedback.setOutputMarkupId(true);
			perMonthFeedback.setMaxMessages(1);
			perMonthFeedback.setFilter(
					new ContainerFeedbackMessageFilter(perMonth));
			
			final DropDownChoice<DocumentOccurrence> document =
				new DropDownChoice<DocumentOccurrence>(
					"document", 
					new ArrayList<DocumentOccurrence>(),
					new ChoiceRenderer<DocumentOccurrence>("combinedDisplayText", "id"));
			document.setRequired(true);
		
			final CheckBoxMultipleChoice<Entry> entries =
				new CheckBoxMultipleChoice<Entry>(
					"entries", 
					new ArrayList<Entry>(),
					new ChoiceRenderer<Entry>("displayText", "id"));
			entries.setRequired(true);
		
			List<String> statTypes = new ArrayList<String>();
			statTypes.add(BasicStatisticsChart.STAT_MEAN);
			statTypes.add(BasicStatisticsChart.STAT_MEDIAN);
			statTypes.add(BasicStatisticsChart.STAT_MODE);
			statTypes.add(BasicStatisticsChart.STAT_MIN);
			statTypes.add(BasicStatisticsChart.STAT_MAX);
			final CheckBoxMultipleChoice<String> statistics = 
				new CheckBoxMultipleChoice<String>("statistics", statTypes);
			statistics.setOutputMarkupId(true);
			statistics.setRequired(true);

			List<String> formats = new ArrayList<String>();
			formats.add("pdf");
			formats.add("xls");
			formats.add("csv");
			
			final DropDownChoice<String> format = 
				new DropDownChoice<String>(
					"format", 
					formats);
			format.setRequired(true);
		
			final Button submit = 
				new Button("submit"){

					@Override
					public void onSubmit() {
						
						MgmtReportModel mrm = model.getObject();
						String fileName = "report"+mrm.getReport().getReportId()+"."+mrm.getFormat();
						ReportingClient client = new ReportingClient();
						try {
							byte[] data = client.generateReport(mrm, 
									SamlHelper.getSaml(session.getUser()));
							IResourceStream resourceStream = new ReportResourceStream(data, mrm.getFormat());
							getRequestCycle().setRequestTarget(
									new ResourceStreamRequestTarget(resourceStream, fileName));
						}
						catch(Exception ex){
							LOG.error(ex);
							getRequestCycle().setResponsePage(new ErrorPage(new Model<Exception>(ex)));
						}
					}
				
			};

			study.add(new AjaxFormComponentUpdatingBehavior("onchange"){

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					
					ReportingClient client  = new ReportingClient();
					Map<Long, String> reportsMap = null;
					try{
						reportsMap = client.getReports(
								model.getObject().getStudy().getIdCode(), 
								"management", 
								SamlHelper.getSaml(session.getUser()));
					}
					catch(Exception ex){
						LOG.error("Exception when calling getReports", ex);
						ErrorPanel.show(ReportForm.this.getParent(), ReportForm.this.getContainer(), target, ex);
						return;
					}
					List<ReportModel.Report> reports = new ArrayList<ReportModel.Report>();
					for ( Map.Entry<Long, String> entry: reportsMap.entrySet() ){
						reports.add(new ReportModel.Report(entry.getKey(), entry.getValue()));
					}
					
					report.setChoices(reports);
					reportContainer2.setVisible(true);
					target.addComponent(reportContainer1);
				
				}
				
			});
			
			report.add(new AjaxFormComponentUpdatingBehavior("onchange"){

				@Override
				protected void onUpdate(AjaxRequestTarget target) {

					MgmtReportModel mrm = model.getObject();
					ReportingClient client = new ReportingClient();
					try {
						String type = client.getReportType(
								mrm.getReport().getReportId(), 
								SamlHelper.getSaml(session.getUser()));
						mrm.setType(type);
					}
					catch(Exception ex){
						LOG.error("Exception when calling getReportType", ex);
						ErrorPanel.show(ReportForm.this.getParent(), ReportForm.this.getContainer(), target, ex);
						return;
					}
					
					boolean showCentres = mrm.showCentres();
					boolean showTargets = mrm.showTargets();
					boolean showDocuments = mrm.showDocuments();
					if ( showCentres ){
						LOG.info("Showing centres");
						centres.setChoices(session.getUser().getGroups().get(mrm.getStudy()));
						centresContainer2.setVisible(true);
						target.addComponent(centresContainer1);
					}else{
						if(centresContainer2 != null){
							centresContainer2.setVisible(false);
							target.addComponent(centresContainer1);
						}
					}
					
					if ( showTargets ){
						LOG.info("Showing targets");
						datesContainer2.setVisible(true);
						target.addComponent(datesContainer1);
					}else{
						if(datesContainer2 != null){
							datesContainer2.setVisible(false);
							target.addComponent(datesContainer1);
						}
					}
					
					if ( showDocuments ){
						LOG.info("Showing documents");
						RepositoryClient repClient = new RepositoryClient();
						try{
							DataSet ds = repClient.getDataSetSummaryWithDocs(
									model.getObject().getStudy().getIdCode(), 
									SamlHelper.getSaml(session.getUser()));
							mrm.setDataSet(ds);

							document.setChoices(getDocumentList(ds));
							documentContainer2.setVisible(true);
							target.addComponent(documentContainer1);
						}
						catch(Exception ex){
							LOG.error("Exception when calling getDataSetSummaryWithDocs", ex);
							ErrorPanel.show(ReportForm.this.getParent(), ReportForm.this.getContainer(), target, ex);
						}
					}else{
						if(documentContainer2 != null){
							documentContainer2.setVisible(false);
							target.addComponent(documentContainer1);
						}
					}
					
					if ( !showTargets && !showDocuments ){
						formatContainer2.setVisible(true);
						target.addComponent(formatContainer1);
						submitContainer2.setVisible(true);
						target.addComponent(submitContainer1);
					}
					
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

			});
			
			targetType.add(new AjaxFormComponentUpdatingBehavior("onchange"){

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					MgmtReportModel mrm = model.getObject();
					if ( mrm.getTargetType().equals("All months") ){
						allMonthsContainer2.setVisible(true);
						target.addComponent(allMonthsContainer1);
					}
					if ( mrm.getTargetType().equals("Per month") ){
						mrm.initializeTargets();
						perMonthContainer2.setVisible(true);
						target.addComponent(perMonthContainer1);
					}
					
					formatContainer2.setVisible(true);
					target.addComponent(formatContainer1);
					submitContainer2.setVisible(true);
					target.addComponent(submitContainer1);					
					
				}
				
			});
			
			document.add(new AjaxFormComponentUpdatingBehavior("onchange"){

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					MgmtReportModel mrm = model.getObject();
					if ( mrm.showEntries() ){
						entries.setChoices(getEntryList(mrm.getDocument().getDocument()));
						entryContainer2.setVisible(true);
						target.addComponent(entryContainer1);
					}
					if ( mrm.showStats() ){
						statContainer2.setVisible(true);
						target.addComponent(statContainer1);
					}
					
					formatContainer2.setVisible(true);
					target.addComponent(formatContainer1);
					submitContainer2.setVisible(true);
					target.addComponent(submitContainer1);					
					
				}
				
				private List<Entry> getEntryList(Document document){
					List<Entry> entryList = new ArrayList<Entry>();
					for ( int i=0, c=document.numEntries(); i<c; i++ ){
						Entry  e = document.getEntry(i);
						if ( e.isQueryable() ){
							entryList.add(document.getEntry(i));
						}
					}
					return entryList;
				}

			});
			
			reportContainer1.add(reportContainer2);
			reportContainer2.add(report);
			reportContainer2.add(new ComponentFeedbackPanel("reportFeedback", report));

			centresContainer1.add(centresContainer2);
			centresContainer2.add(centres);
			centresContainer2.add(new ComponentFeedbackPanel("centresFeedback", centres));

			datesContainer1.add(datesContainer2);
			datesContainer2.add(startDatePanel);
			datesContainer2.add(endDatePanel);
			datesContainer2.add(confirmDates);
			
			targetTypeContainer1.add(targetTypeContainer2);
			targetTypeContainer2.add(targetType);
			
			allMonthsContainer1.add(allMonthsContainer2);
			allMonthsContainer2.add(allMonths);
			
			perMonthContainer1.add(perMonthContainer2);
			perMonthContainer2.add(perMonth);
			perMonthContainer2.add(perMonthFeedback);
			
			documentContainer1.add(documentContainer2);
			documentContainer2.add(document);
			documentContainer2.add(new ComponentFeedbackPanel("documentFeedback", document));
			
			entryContainer1.add(entryContainer2);
			entryContainer2.add(entries);
			entryContainer2.add(new ComponentFeedbackPanel("entryFeedback", entries));
			
			statContainer1.add(statContainer2);
			statContainer2.add(statistics);
			statContainer2.add(new ComponentFeedbackPanel("statsFeedback", statistics));
			
			formatContainer1.add(formatContainer2);
			formatContainer2.add(format);
			formatContainer2.add(new ComponentFeedbackPanel("formatFeedback", format));
			
			submitContainer1.add(submitContainer2);
			submitContainer2.add(submit);
			
			add(study);
			add(reportContainer1);
			add(centresContainer1);
			add(datesContainer1);
			add(targetTypeContainer1);
			add(allMonthsContainer1);
			add(perMonthContainer1);
			add(documentContainer1);
			add(entryContainer1);
			add(statContainer1);
			add(formatContainer1);
			add(submitContainer1);			
			
			add(new StartAndEndDateValidator(startDatePanel, endDatePanel));

		}
	}

	public void renderHead(IHeaderResponse response) {
		response.renderOnLoadJavascript("selectStudy(\"study\")");
	}
	
}
