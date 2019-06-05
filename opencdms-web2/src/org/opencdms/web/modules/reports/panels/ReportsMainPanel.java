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

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.opencdms.web.modules.reports.pages.MgmtReport;
import org.opencdms.web.modules.reports.pages.RecordReport;
import org.opencdms.web.modules.reports.pages.TrendReport;

/**
 * @author Rob Harper
 *
 */
public class ReportsMainPanel extends Panel {

	private static final long serialVersionUID = 1L;

	/**
	 * @param id
	 */
	public ReportsMainPanel(String id) {
		super(id);
		add(new BookmarkablePageLink<Object>("record", RecordReport.class));
		add(new BookmarkablePageLink<Object>("trend", TrendReport.class));
		add(new BookmarkablePageLink<Object>("management", MgmtReport.class));

	}

}
