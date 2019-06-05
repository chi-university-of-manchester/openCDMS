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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.opencdms.web.core.application.OpenCdmsWeb;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.psygrid.data.model.dto.extra.GroupSummary;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.Project;

/**
 * Displays a list of groups that a user may administer.
 * 
 * Each group has an edit link.
 * 
 * There is also a button to add a new group.
 * 
 * @author Terry Child
 */
public class GroupListPanel extends Panel {

	private static final long serialVersionUID = 1L;
					
	/**
	 * A model holding the list of group summaries.
	 * 
	 * This is an LDM, so the model data is not written to the session.
	 * 
	 * The data is loaded dynamically from the database when needed.
	 */
	private static class GroupListModel extends LoadableDetachableModel<List<GroupSummary>> {

		private static final long serialVersionUID = 1L;
		
		@Override
		protected List<GroupSummary> load() {
			
			// Get user's projects from the attribute authority
			String userDN = OpenCdmsWebSession.get().getUser().getPgDn();
    		List<Project> projects = OpenCdmsWeb.get().getAttributeAuthorityService().getProjects(userDN,"ProjectManager");
			
    		// Extract the project codes for projects without randomisation for now.
    		List<String> projectCodes = new ArrayList<String>();	
    		for(Project project:projects){
	    		boolean randomized = OpenCdmsWeb.get().getRepositoryService().isProjectRandomized(project.getIdCode());
	    		if(!randomized) {
	    			projectCodes.add(project.getIdCode());
	    		}
    		}
    		
    		// Retrieve information on groups for the given project codes.
    		return OpenCdmsWeb.get().getRepositoryService().getGroupSummary(projectCodes);
		}
	}
	
	@SuppressWarnings("serial")
	public GroupListPanel(String id) {
		super(id);
		
        add(new FeedbackPanel("feedback"));

		add(new PropertyListView<GroupSummary>("groups",new GroupListModel()){
			@Override
			protected void populateItem(ListItem<GroupSummary> item) {
				item.add(new Label("groupName"));
				item.add(new Label("groupCode"));
				item.add(new Label("datasetName"));
				item.add(new Link<GroupSummary>("edit",item.getModel()){
					public void onClick() {
						Group g = OpenCdmsWeb.get().getRepositoryService().getGroup(getModelObject().getGroupID());
						GroupListPanel.this.replaceWith(
								new GroupEditPanel(GroupListPanel.this.getId(),false,g,GroupListPanel.this)
						);
					}
				});
			}
		});
		
		add(new Link<Void>("add"){
			@Override
			public void onClick() {
				GroupListPanel.this.replaceWith(new GroupEditPanel(GroupListPanel.this.getId(),true,new Group(),GroupListPanel.this));				
			}
		});
	}
        
}
