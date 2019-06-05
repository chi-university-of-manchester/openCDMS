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

package org.opencdms.web.modules.query.pages;

import org.apache.wicket.markup.html.panel.Panel;
import org.opencdms.web.core.pages.BasePage;
import org.opencdms.web.modules.query.models.ExecuteQueryModel;
import org.opencdms.web.modules.query.panels.ExecuteQueryPanel;

/**
 * @author Rob Harper
 *
 */
public class ExecuteQueryPage extends BasePage<ExecuteQueryModel> {

	@Override
	public Panel getContentPanel(String id) {
		return new ExecuteQueryPanel(id);
	}

	@Override
	public String getPageGroup() {
		return "Query";
	}

	@Override
	public String getPageTitle() {
		return "openCDMS | Query | Execute";
	}

}
