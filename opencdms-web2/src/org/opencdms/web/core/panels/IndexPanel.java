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

package org.opencdms.web.core.panels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.models.LoginHistoryModel;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.types.PostProcessLoginResponseType;

/**
 * @author Rob Harper
 *
 */
public class IndexPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(IndexPanel.class);
	
	private static final int MAX_LOGINS_TO_DISPLAY = 5;
	
	private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy");
	
	public IndexPanel(String id) {
		super(id);
		try{
			final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
			AAQueryClient aaqc = new AAQueryClient("aaclient.properties");
			
			PostProcessLoginResponseType pplrt = aaqc.postProcessLogin(session.getUser().getUsername());
			
    		int nLogins = pplrt.getPreviousLoginAddresses().length;
    		int nLoginsToDisplay = nLogins;
    		if ( nLoginsToDisplay > MAX_LOGINS_TO_DISPLAY ){
    			nLoginsToDisplay = MAX_LOGINS_TO_DISPLAY;
    		}
			
    		List<LoginHistoryModel> historyList = new ArrayList<LoginHistoryModel>();
    		for ( int i=nLogins-nLoginsToDisplay, c=nLogins; i<c; i++ ){
    			String authenticated = null;
    			if ( pplrt.getAuthenticated(i) ){
    				authenticated = "Success";
    			}
    			else{
    				authenticated = "Failure";
    			}
    			historyList.add(new LoginHistoryModel(
    					formatter.format(pplrt.getPreviousLoginDates(i).getTime()),
    					pplrt.getPreviousLoginAddresses(i),
    					authenticated));
    		}
    		
			
			@SuppressWarnings("serial")
			ListView<LoginHistoryModel> history = 
				new ListView<LoginHistoryModel>("history", historyList){

					@Override
					protected void populateItem(ListItem<LoginHistoryModel> item) {
						item.add(new Label("date", item.getModelObject().getDate()));
						item.add(new Label("address", item.getModelObject().getAddress()));
						item.add(new Label("authenticated", item.getModelObject().getAuthenticated()));
					}
				
			};
			
			add(history);
		}
		catch(Exception ex){
			LOG.error("Exception when calling getUsersInProject", ex);
		}
	}

}
