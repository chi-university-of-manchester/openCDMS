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

package org.opencdms.web.core.panels.common;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.models.ProjectAndGroupsModel;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class ProjectAndGroupsPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private final CheckBoxMultipleChoice<GroupType> centres;
	private final WebMarkupContainer centresContainer1;
	private final WebMarkupContainer centresContainer2;
	private final DropDownChoice<ProjectType> study;
	
	@SuppressWarnings("serial")
	public ProjectAndGroupsPanel(String id, final IModel<? extends ProjectAndGroupsModel> model) {
		super(id, model);
		
		final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
		
		study = 
			new DropDownChoice<ProjectType>(
				"study", 
				session.getUser().getExportableProjects(),
				new ChoiceRenderer<ProjectType>("name", "idCode"));
		study.setRequired(true);
		study.setMarkupId("study");
		
		centresContainer1 = new WebMarkupContainer("centresContainer1");
		centresContainer1.setOutputMarkupId(true);

		centresContainer2 = new WebMarkupContainer("centresContainer2");
		centresContainer2.setOutputMarkupId(true);
		centresContainer2.setVisible(false);
		
		centres = new CheckBoxMultipleChoice<GroupType>(
				"centres");
		centres.setChoiceRenderer(new ChoiceRenderer<GroupType>("name", "idCode"));
		centres.setOutputMarkupId(true);
		centres.setRequired(true);
		if ( null != model.getObject().getStudy() ){
			centres.setChoices(session.getUser().getGroups().get(model.getObject().getStudy()));
		}

		Button selectAllButton = new Button("selectAllButton"){
			
			@Override
			public void onSubmit(){
				ProjectAndGroupsModel m = model.getObject();
				m.setCentres(session.getUser().getGroups().get(model.getObject().getStudy()));
				centres.modelChanged();
			}
		};
		selectAllButton.setDefaultFormProcessing(false);
		selectAllButton.setOutputMarkupId(true);
		
		centresContainer1.add(centresContainer2);
		centresContainer2.add(centres);
		centresContainer2.add(selectAllButton);
		centresContainer2.add(new ComponentFeedbackPanel("centresFeedback", centres));
		
		study.add(new AjaxFormComponentUpdatingBehavior("onchange"){

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				centres.setChoices(session.getUser().getGroups().get(model.getObject().getStudy()));
				centresContainer2.setVisible(true);
				target.addComponent(centresContainer1);
				updateFromProject(target);
			}
			
		});
		
		add(study);
		add(centresContainer1);

	}

	public void updateFromProject(AjaxRequestTarget target){
		//do nothing
	}
	
	public void showErrors(final AjaxRequestTarget target){
		target.addComponent(centresContainer1);
	}

	public void disableStudyAndShowGroups(){
		study.setEnabled(false);
		centresContainer2.setVisible(true);
	}
	
}
