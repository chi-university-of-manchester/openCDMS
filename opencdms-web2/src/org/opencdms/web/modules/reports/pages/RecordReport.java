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

package org.opencdms.web.modules.reports.pages;

import org.apache.wicket.markup.html.panel.Panel;
import org.opencdms.web.core.pages.BasePage;
import org.opencdms.web.modules.reports.panels.RecordReportPanel;

/**
 * @author Rob Harper
 *
 */
public class RecordReport extends BasePage<Void> {

	@Override
	public Panel getContentPanel(String id) {
		return new RecordReportPanel(id);
	}

	@Override
	public String getPageGroup() {
		return "Reports";
	}

	@Override
	public String getPageTitle() {
		return "openCDMS | Reports | Record";
	}

}
