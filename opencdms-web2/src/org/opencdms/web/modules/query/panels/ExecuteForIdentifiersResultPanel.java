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

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Rob Harper
 *
 */
public class ExecuteForIdentifiersResultPanel extends Panel {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("serial")
	public ExecuteForIdentifiersResultPanel(String id, List<String> identifiers) {
		super(id);
		
		final WebMarkupContainer outerContainer = new WebMarkupContainer("outerContainer");
		outerContainer.setOutputMarkupId(true);

		final WebMarkupContainer resultsContainer = new WebMarkupContainer("resultsContainer");
		resultsContainer.setOutputMarkupId(true);

		final WebMarkupContainer noResultsContainer = new WebMarkupContainer("noResultsContainer");
		noResultsContainer.setOutputMarkupId(true);

		ListView<String> idList = new ListView<String>(
				"idList", identifiers){

			@Override
			protected void populateItem(ListItem<String> item) {
				item.add(new Label("identifier", item.getModelObject()));
			}
			
		};
		
		if ( identifiers.isEmpty() ){
			resultsContainer.setVisible(false);
			noResultsContainer.setVisible(true);
		}
		else{
			resultsContainer.setVisible(true);
			noResultsContainer.setVisible(false);			
		}
		
		add(outerContainer);
		outerContainer.add(resultsContainer);
		outerContainer.add(noResultsContainer);
		resultsContainer.add(idList);
		
	}

}
