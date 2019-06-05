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

package org.opencdms.web.modules.register.panels;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.modules.register.models.StudyNumberModel;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class DetailsByNumberFormPanel extends Panel {

	private static final long serialVersionUID = 1L;


	public DetailsByNumberFormPanel(String id, Component container) {
		super(id);
		setOutputMarkupId(true);
		add(new DetailsByNumberForm("requestForm",  new CompoundPropertyModel<StudyNumberModel>(new StudyNumberModel()), container));
	}
	
	
	public static class DetailsByNumberForm extends Form<StudyNumberModel>{

		private static final long serialVersionUID = 1L;
		
		private final Component container;
		
		public Component getContainer() {
			return container;
		}

		@SuppressWarnings("serial")
		public DetailsByNumberForm(String id, final IModel<StudyNumberModel> model, Component container) {
			super(id, model);
			this.container = container;
			
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			
			final WebMarkupContainer identifierContainer1 = new WebMarkupContainer("identifierContainer1");
			identifierContainer1.setOutputMarkupId(true);
			final WebMarkupContainer identifierContainer2 = new WebMarkupContainer("identifierContainer2");
			identifierContainer2.setOutputMarkupId(true);
			identifierContainer2.setVisible(false);

			final WebMarkupContainer submitContainer1 = new WebMarkupContainer("submitContainer1");
			submitContainer1.setOutputMarkupId(true);
			final WebMarkupContainer submitContainer2 = new WebMarkupContainer("submitContainer2");
			submitContainer2.setOutputMarkupId(true);
			submitContainer2.setVisible(false);

			final DropDownChoice<ProjectType> study = 
				new DropDownChoice<ProjectType>(
					"study", 
					session.getUser().getProjects(),
					new ChoiceRenderer<ProjectType>("name", "idCode"));
			study.setRequired(true);
		
			final Label identifierFormat = new Label("identifierFormat");
			
			final TextField<String> identifier = new TextField<String>("identifier");
			
			final AjaxButton search = new AjaxButton("search"){

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					
				}
				
			};
			
			
			study.add(new AjaxFormComponentUpdatingBehavior("onchange"){

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					ProjectType pt = model.getObject().getStudy();
					List<GroupType> groups = session.getUser().getGroups().get(pt);
					//TODO no groups
					model.getObject().setIdentifierFormat(
							pt.getIdCode()+"/"+groups.get(0)+"-11");
					
					identifierContainer2.setVisible(true);
					submitContainer2.setVisible(true);
					
					target.addComponent(identifierContainer1);
					target.addComponent(submitContainer1);
					
				}
				
			});
			
			identifierContainer1.add(identifierContainer2);
			identifierContainer2.add(identifierFormat);
			identifierContainer2.add(identifier);
			
			submitContainer1.add(submitContainer2);
			submitContainer2.add(search);
			
			add(study);
			add(identifierContainer1);
			add(submitContainer1);
			
		}
		
	}
}
