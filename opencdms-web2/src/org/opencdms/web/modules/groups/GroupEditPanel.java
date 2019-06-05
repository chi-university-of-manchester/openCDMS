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

package org.opencdms.web.modules.groups;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormSubmittingComponent;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.opencdms.web.core.application.OpenCdmsWeb;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.security.attributeauthority.model.hibernate.Project;

/**
 * Displays a group edit form.
 * 
 * @author Terry Child
 */
public class GroupEditPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	/**
	 * This panel will replace itself with the 
	 * previous panel when it is done.
	 */
	private Panel previous = null;

	/**
	 * Store the study here so we can access the project code
	 * from the form submit button.
	 */
	private DropDownChoice<Project> study;
	
	/**
	 * We use a SubmitLink for 'add site' link to submit the form so any data
	 * is stored before we show the add site panel.
	 */
	SubmitLink addSiteLink;
	
	/**
	 * Store the original group name so we can look 
	 * up the group in the ESL and Attribute Authority.
	 */
	private String originalGroupCode;
		
	/**
	 * Displays a panel for editing a group.
	 * 
	 * The group code is only editable if the group has no identifiers allocated.
	 * 
	 * @param id the component id
	 * @param isNew set true if this is a 'new' form or false is this is an 'edit' form
	 * @param group the group to be edited
	 * @param caller the panel that will be restored when this panel is done.
	 */
	@SuppressWarnings("serial")
	public GroupEditPanel(String id, final boolean isNew, final Group g, Panel caller) {
		super(id);
				
		previous=caller;
		
		originalGroupCode=g.getName();
		
		add(new Label("panelTitle",isNew?"New Centre":"Edit Centre"));
				
        add(new FeedbackPanel("feedback"));

   	 	// We want the data to be held in the session until is is 
   	 	// ready to be saved to the database, so this model is not detachable.
		Form<Group> form = new Form<Group>("form",new CompoundPropertyModel<Group>(g)){
			protected void onSubmit() {
				Group group = getModelObject();
				try {
					if(isNew){
						String projectCode = study.getModelObject().getIdCode();
						addGroup(projectCode,group);
					}
					else {
						saveGroup(group);
					}
					GroupEditPanel.this.replaceWith(previous);
				} catch (RepositoryServiceFault e){
					error("Unable to save the centre - please check that the centre name and code is unique.");
				}
			}
			
			/**
			 * Override the base implementation to not call the 
			 * form onSubmit handler when the 'add site' link is used.
			 */
			protected void delegateSubmit(IFormSubmittingComponent submittingComponent) {
				if (submittingComponent == addSiteLink){
					submittingComponent.onSubmit();
				}
				else {
					this.onSubmit();					
				}
			}
		};		
		add(form);
		
		// Model for the list of projects a user can access.
		IModel<List<Project>> projectListModel = new LoadableDetachableModel<List<Project>>() {
			
			private static final long serialVersionUID = 1L;
			
			protected List<Project> load() {			
				// Get user's projects from the attribute authority
				String userDN = OpenCdmsWebSession.get().getUser().getPgDn();
	    		List<Project> projects = OpenCdmsWeb.get().getAttributeAuthorityService().getProjects(userDN,"ProjectManager");			
	    		
	    		// Filter the list to only projects without randomization for now.
	    		Iterator<Project> i = projects.iterator();
	    		while(i.hasNext()){
	    			boolean randomized = OpenCdmsWeb.get().getRepositoryService().isProjectRandomized(i.next().getIdCode());
	    			if(randomized) i.remove();
	    		}
	    		
	    		return projects;
			}
		};

		study = new DropDownChoice<Project>("study", new Model<Project>(), projectListModel,
											new ChoiceRenderer<Project>("projectName", "idCode"));

		if(isNew){
			study.setRequired(true);
			// For a new centre, if there is only one project set the study to that project.
			if(projectListModel.getObject().size()==1){
				study.setModelObject(projectListModel.getObject().get(0));
			}
		}
		else {
			// For an existing centre, set the study to that project and disable changes.
			study.setEnabled(false);
			for(Project p:projectListModel.getObject()){
				if(p.getIdCode().equals(g.getDataSet().getProjectCode())){
					study.setModelObject(p);
					break;
				}
			}
		}
		
		
		form.add(study);
		
		form.add(new TextField<String>("longName").setRequired(true)
				.add(StringValidator.maximumLength(255)));
				
		// Allow editing the name (group code) only for new groups.
		form.add(new TextField<String>("name").setRequired(true)
				.add(StringValidator.maximumLength(255))
				.add(new GroupNameValidator())
				.setEnabled(isNew));
		
		// List of sites with 'edit' links.
		form.add(new PropertyListView<Site>("sites"){
			protected void populateItem(ListItem<Site> item) {
				item.add(new Label("siteName"));
				item.add(new Label("siteId"));
				item.add(new Link<Site>("edit",item.getModel()){
					public void onClick() {
						GroupEditPanel.this.replaceWith(new SiteEditPanel(GroupEditPanel.this.getId(),false,
								g,getModelObject(),GroupEditPanel.this));
					}
				});
			}			
		});

		addSiteLink = new SubmitLink("addSite"){
			public void onSubmit() {
				GroupEditPanel.this.replaceWith(new SiteEditPanel(GroupEditPanel.this.getId(),true,
						g,new Site(),GroupEditPanel.this));				
			}
		};
		form.add(addSiteLink);

		Button cancel = new Button("cancel"){
			public void onSubmit() {
				GroupEditPanel.this.replaceWith(previous);
			}
		};
		cancel.setDefaultFormProcessing(false);
		form.add(cancel);
		
	}
	
	/**
	 * Validates that numeric group names cannot be between 0 and 1000 inclusive.
	 */
	private static final class GroupNameValidator extends AbstractValidator<String> {

		private static final long serialVersionUID = 1L;

		@Override
		protected void onValidate(IValidatable<String> validatable) {
			try {
				int integerValue = Integer.parseInt(validatable.getValue());
				if(integerValue >= 0 && integerValue <= 1000) {
					error(validatable);
				}
			} catch (NumberFormatException ex){
				// ignore if not a number.
			}		
		}
	}

            
	void addGroup(String projectCode, Group group) throws RepositoryServiceFault {

		String groupCode = group.getName();
		String groupName = group.getLongName();

		// Update the repository
		OpenCdmsWeb.get().getRepositoryService().addGroup(projectCode,group);

		// Inform the ESL that a group had been added.
	    OpenCdmsWeb.get().getEslService().groupAdded(projectCode,groupCode,groupName);			

		// Inform the AA that a group had been added.
		OpenCdmsWeb.get().getAttributeAuthorityService().groupAdded(projectCode,groupCode,groupName);

		info("Centre '"+group.getLongName()+"' has been added.");
	}
	
	void saveGroup(Group group) throws RepositoryServiceFault {
		
		String projectCode = group.getDataSet().getProjectCode();
		String groupCode = group.getName();
		String groupName = group.getLongName();

		// Update the repository
		OpenCdmsWeb.get().getRepositoryService().updateGroup(group);

		// Inform the ESL that a group had been updated.
		OpenCdmsWeb.get().getEslService().groupUpdated(projectCode,originalGroupCode,groupCode,groupName);			

		// Inform the AA that a group had been updated.
		OpenCdmsWeb.get().getAttributeAuthorityService().groupUpdated(projectCode,originalGroupCode,groupCode,groupName);
		
		info("Centre '"+group.getLongName()+"' has been saved.");
	}
	
	void deleteGroup(Group group) throws RepositoryServiceFault {
		
		String projectCode = group.getDataSet().getProjectCode();
			
		// Update the repository
		OpenCdmsWeb.get().getRepositoryService().deleteGroup(group.getId());
		
		// Inform the AA that a group had been deleted.
		OpenCdmsWeb.get().getAttributeAuthorityService().groupDeleted(projectCode,originalGroupCode);

		// Inform the ESL that a group had been deleted.
		OpenCdmsWeb.get().getEslService().groupDeleted(projectCode,originalGroupCode);

		info("Centre '"+group.getLongName()+"' has been deleted.");
	}
}
	


