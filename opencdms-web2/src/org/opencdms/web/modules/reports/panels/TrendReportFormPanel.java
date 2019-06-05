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
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
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
import org.opencdms.web.modules.reports.models.ReportModel;
import org.opencdms.web.modules.reports.models.TrendReportModel;
import org.opencdms.web.modules.reports.repository.ReportResourceStream;
import org.opencdms.web.modules.reports.repository.ReportingClient;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class TrendReportFormPanel extends Panel implements IHeaderContributor {

	private static final Log LOG = LogFactory.getLog(TrendReportPanel.class);
	
	private static final long serialVersionUID = 1L;

	public TrendReportFormPanel(String id, Component container) {
		super(id);
		setOutputMarkupId(true);
		add(new ReportForm("reportForm", new CompoundPropertyModel<TrendReportModel>(new TrendReportModel()), container));
	}

	public static class ReportForm extends Form<TrendReportModel>{

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
		public ReportForm(final String id, final IModel<TrendReportModel> model, Component container) {
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

			final WebMarkupContainer typeContainer1 = new WebMarkupContainer("typeContainer1");
			typeContainer1.setOutputMarkupId(true);
			final WebMarkupContainer typeContainer2 = new WebMarkupContainer("typeContainer2");
			typeContainer2.setOutputMarkupId(true);
			typeContainer2.setVisible(false);

			final WebMarkupContainer totalsContainer1 = new WebMarkupContainer("totalsContainer1");
			totalsContainer1.setOutputMarkupId(true);
			final WebMarkupContainer totalsContainer2 = new WebMarkupContainer("totalsContainer2");
			totalsContainer2.setOutputMarkupId(true);
			totalsContainer2.setVisible(false);

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
			
			final DropDownChoice<String> summaryType = 
				new DropDownChoice<String>(
					"summaryType", 
					new ArrayList<String>());
			summaryType.setRequired(true);
		
			List<String> showTotalsList = new ArrayList<String>();
			showTotalsList.add("Yes");
			showTotalsList.add("No");
			final DropDownChoice<String> showTotals = 
				new DropDownChoice<String>(
					"showTotals",
					showTotalsList);
			showTotals.setRequired(true);
			
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
						
						TrendReportModel trm = model.getObject();
						String fileName = "report"+trm.getReport().getReportId()+"."+trm.getFormat();
						ReportingClient client = new ReportingClient();
						try {
							byte[] data = client.generateReport(trm, SamlHelper.getSaml(session.getUser()));
							IResourceStream resourceStream = new ReportResourceStream(data, trm.getFormat());
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
								"trends", 
								SamlHelper.getSaml(session.getUser()));
					}
					catch(Exception ex){
						LOG.error("Exception when calling getDataSetSummaryWithDocs", ex);
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
					centres.setChoices(session.getUser().getGroups().get(model.getObject().getStudy()));
					centresContainer2.setVisible(true);
					target.addComponent(centresContainer1);
					
					datesContainer2.setVisible(true);
					target.addComponent(datesContainer1);
					
					ReportingClient client = new ReportingClient();
					try {
						List<String> types =  client.getSummaryTypesForReport(
								model.getObject().getReport().getReportId(), 
								SamlHelper.getSaml(session.getUser()));
						summaryType.setChoices(types);
						typeContainer2.setVisible(true);
						target.addComponent(typeContainer1);
					}
					catch (Exception ex) {
						LOG.error("Error thrown when retrieving summary types.", ex);
						ErrorPanel.show(ReportForm.this.getParent(), ReportForm.this.getContainer(), target, ex);
						return;
					}

					totalsContainer2.setVisible(true);
					target.addComponent(totalsContainer1);
					
					formatContainer2.setVisible(true);
					target.addComponent(formatContainer1);
					
					submitContainer2.setVisible(true);
					target.addComponent(submitContainer1);
					
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
			
			typeContainer1.add(typeContainer2);
			typeContainer2.add(summaryType);
			typeContainer2.add(new ComponentFeedbackPanel("typeFeedback", summaryType));
			
			totalsContainer1.add(totalsContainer2);
			totalsContainer2.add(showTotals);
			totalsContainer2.add(new ComponentFeedbackPanel("totalsFeedback", showTotals));
			
			formatContainer1.add(formatContainer2);
			formatContainer2.add(format);
			formatContainer2.add(new ComponentFeedbackPanel("formatFeedback", format));
			
			submitContainer1.add(submitContainer2);
			submitContainer2.add(submit);
			
			add(study);
			add(reportContainer1);
			add(centresContainer1);
			add(datesContainer1);
			add(typeContainer1);
			add(totalsContainer1);
			add(formatContainer1);
			add(submitContainer1);			
			
			add(new StartAndEndDateValidator(startDatePanel, endDatePanel));
		}
	}

	public void renderHead(IHeaderResponse response) {
		response.renderOnLoadJavascript("selectStudy(\"study\")");
	}
	
}
