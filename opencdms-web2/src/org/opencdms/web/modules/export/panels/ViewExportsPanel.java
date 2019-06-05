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

package org.opencdms.web.modules.export.panels;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Rob Harper
 *
 */
public class ViewExportsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public ViewExportsPanel(String id) {
		super(id);
		setOutputMarkupId(true);
		WebMarkupContainer container = new WebMarkupContainer("viewContainer");
		add(container);
		container.setOutputMarkupId(true);
		container.add(new ViewExportFormPanel("view", container));
	}

}