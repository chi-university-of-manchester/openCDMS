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

package org.opencdms.web.modules.imports.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.security.SamlHelper;
import org.psygrid.data.importing.ImportStatus;
import org.psygrid.data.importing.client.ImportClient;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Terry Child
 *
 */
public class ViewImportsPanel extends Panel {

	private static final Log logger = LogFactory.getLog(ViewImportsPanel.class);

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("serial")
	public ViewImportsPanel(String id) {
		super(id);

        add(new FeedbackPanel("feedback"));

        OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
		
		List<ProjectType> projects = session.getUser().getImportableProjects();
		
		// update the import status list for all projects
		List<ImportStatus> statuses = new ArrayList<ImportStatus>();

		try {
			ImportClient client = new ImportClient();
			String saml = SamlHelper.getSaml(session.getUser());
			for(ProjectType p:projects){
	    		ImportStatus[] imports = client.getImportStatuses(p.getIdCode(),saml);
				statuses.addAll(Arrays.asList(imports));
			}		
		} catch (Exception e) {
		     error("Problem retrieving imports:"+e.getMessage());
		     logger.error("Problem viewing imports",e);
		} 	    		
    	
		add(new ListView<ImportStatus>("imports",statuses) {
				@Override
				protected void populateItem(ListItem<ImportStatus> item) {
					ImportStatus status = item.getModelObject();
					item.add(new Label("id",status.getId().toString()));
					item.add(new Label("projectCode",status.getProjectCode()));
					String user = status.getUser();
					item.add(new Label("user",user.substring(3,user.indexOf(','))));
					item.add(new Label("requestDate",status.getRequestDate().toString()));
					item.add(new Label("file",status.getRemoteFilePath()));
					item.add(new Label("status",status.getStatus()));
					item.add(new Label("completedDate",status.getCompletedDate()!=null?status.getCompletedDate().toString():""));
				}}    			
    		);
	}
        	
//	public void renderHead(IHeaderResponse response) {
//		response.renderOnLoadJavascript("selectStudy(\"study\")");
//	}    
}

