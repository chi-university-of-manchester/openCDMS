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
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
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
import org.opencdms.web.modules.reports.models.RecordReportModel;
import org.opencdms.web.modules.reports.models.ReportModel;
import org.opencdms.web.modules.reports.repository.ReportResourceStream;
import org.opencdms.web.modules.reports.repository.ReportingClient;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class RecordReportFormPanel extends Panel implements IHeaderContributor {

	private static final Log LOG = LogFactory.getLog(RecordReportFormPanel.class);
	
	private static final long serialVersionUID = 1L;

	public RecordReportFormPanel(String id, Component container) {
		super(id);
		setOutputMarkupId(true);
		add(new ReportForm("reportForm", new CompoundPropertyModel<RecordReportModel>(new RecordReportModel()), container));
	}

	public static class ReportForm extends Form<RecordReportModel>{

		private static final long serialVersionUID = 1L;

		private final Component container;

		// Maps openCDMS identifiers to display identifiers e.g. external identifiers.
		private Map<String,String> identifierMap;
		
		public Component getContainer() {
			return container;
		}

		/**
		 * @param id
		 * @param model
		 */
		@SuppressWarnings("serial")
		public ReportForm(final String id, final IModel<RecordReportModel> model, Component container) {
			super(id, model);
			this.container = container;
						
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			
			final WebMarkupContainer reportContainer1 = new WebMarkupContainer("reportContainer1");
			reportContainer1.setOutputMarkupId(true);
			final WebMarkupContainer reportContainer2 = new WebMarkupContainer("reportContainer2");
			reportContainer2.setOutputMarkupId(true);
			reportContainer2.setVisible(false);

			final WebMarkupContainer identifierContainer1 = new WebMarkupContainer("identifierContainer1");
			identifierContainer1.setOutputMarkupId(true);
			final WebMarkupContainer identifierContainer2 = new WebMarkupContainer("identifierContainer2");
			identifierContainer2.setOutputMarkupId(true);
			identifierContainer2.setVisible(false);

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
		
			final DropDownChoice<String> identifier = 
				new DropDownChoice<String>("identifier", 
					new ArrayList<String>(),
					new IChoiceRenderer<String>(){
						public Object getDisplayValue(String object) {
							return identifierMap.get(object);
						}
						public String getIdValue(String object, int index) {
							return object;
						}						
					});
			identifier.setRequired(true);
		
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
						
						RecordReportModel rrm = model.getObject();
						String fileName = "report"+rrm.getReport().getReportId()+"."+rrm.getFormat();
						ReportingClient client = new ReportingClient();
						try {
							byte[] data = client.generateReport(rrm, SamlHelper.getSaml(session.getUser()));
							IResourceStream resourceStream = new ReportResourceStream(data, rrm.getFormat());
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
						reportsMap = client.getReports(model.getObject().getStudy().getIdCode(), "record", SamlHelper.getSaml(session.getUser()));
						identifierMap = client.getIdentifierMap(model.getObject().getStudy().getIdCode(), SamlHelper.getSaml(session.getUser()));
					}
					catch(Exception ex){
						LOG.error("Exception when study selected", ex);
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
				
					identifier.setChoices(new ArrayList<String>(identifierMap.keySet()));
					identifierContainer2.setVisible(true);
					target.addComponent(identifierContainer1);
					
					formatContainer2.setVisible(true);
					target.addComponent(formatContainer1);
					
					submitContainer2.setVisible(true);
					target.addComponent(submitContainer1);
				}
				
			});
			
			reportContainer1.add(reportContainer2);
			reportContainer2.add(report);
			reportContainer2.add(new ComponentFeedbackPanel("reportFeedback", report));

			identifierContainer1.add(identifierContainer2);
			identifierContainer2.add(identifier);
			identifierContainer2.add(new ComponentFeedbackPanel("identifierFeedback", identifier));

			formatContainer1.add(formatContainer2);
			formatContainer2.add(format);
			formatContainer2.add(new ComponentFeedbackPanel("formatFeedback", format));
			
			submitContainer1.add(submitContainer2);
			submitContainer2.add(submit);
			
			add(study);
			add(reportContainer1);
			add(identifierContainer1);
			add(formatContainer1);
			add(submitContainer1);
			
		}
		
	}

	public void renderHead(IHeaderResponse response) {
		response.renderOnLoadJavascript("selectStudy(\"study\")");
	}
	
}
