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

package org.opencdms.web.modules.query.panels;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.opencdms.web.modules.query.pages.BuildQueryPage;
import org.opencdms.web.modules.query.pages.ExecuteQueryPage;
import org.opencdms.web.modules.query.pages.ViewQueriesPage;

/**
 * @author Rob Harper
 *
 */
public class QueryMainPanel extends Panel {

	private static final long serialVersionUID = 1L;

	/**
	 * @param id
	 */
	public QueryMainPanel(String id) {
		super(id);
		add(new BookmarkablePageLink<Object>("buildLink", BuildQueryPage.class));
		add(new BookmarkablePageLink<Object>("viewLink", ViewQueriesPage.class));
		add(new BookmarkablePageLink<Object>("executeLink", ExecuteQueryPage.class));
	}

}
