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

package org.opencdms.web.core.panels.common;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.psygrid.data.reporting.GroupsNotAllowedException;
import org.psygrid.data.reporting.ReportRenderingException;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * @author Rob Harper
 *
 */
public class ErrorPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public ErrorPanel(String id) {
		this(id, "Unexpected Error", "An unexpected error has occurred.");
	}

	public ErrorPanel(String id, String title, String description) {
		super(id);
		add(new Label("title", title));
		add(new Label("description", description));
	}

	public ErrorPanel(String id, IModel<Exception> model){
		super(id);
		String[] text = getTitleAndDescription(model.getObject());
		add(new Label("title", text[0]));
		add(new Label("description", text[1]));
	}
	
	public static void show(MarkupContainer parent, Component container, AjaxRequestTarget target, Exception ex){
		ErrorPanel panel = null;
		String[] text = getTitleAndDescription(ex);
		panel = new ErrorPanel(parent.getId(), text[0], text[1]);
		parent.replaceWith(panel);
		target.addComponent(container);
	}
	
	private static String[] getTitleAndDescription(Exception ex){
		String[] result = new String[2];
		if ( ex instanceof NotAuthorisedFault ){
			result[0] = "Not Authorized";
			result[1] = "You are not authorized to perform this operation.";
		}
		else if (ex instanceof ConnectException || ex instanceof SocketTimeoutException ){
			result[0] = "No Connection";
			result[1] = "Could not connect to the remote server. Please try again later.";
		}
		else if (ex instanceof RepositoryServiceFault ){
			result[0] = "Service Error";
			if ( null == ex.getMessage() ){
				result[1] = "An unexpected error has occurred. Please contact support.";
			}
			else{
				result[1] = ex.getMessage();
			}
		}
		else if (ex instanceof RepositoryNoSuchDatasetFault ){
			result[0] = "Unknown Study";
			result[1] = "The selected study was not found on the remote server. Please contact support.";
		}
		else if (ex instanceof ReportException ){
			result[0] = "Report Error";
			result[1] = "There is a problem with the definition of the report. Please contact support.";
		}
		else if (ex instanceof GroupsNotAllowedException ){
			result[0] = "Report Error";
			if ( null == ex.getMessage() ){
				result[1] = "An unexpected error has occurred. Please contact support.";
			}
			else{
				result[1] = ex.getMessage();
			}
		}
		else if (ex instanceof ReportRenderingException ){
			result[0] = "Report Error";
			result[1] = "Unable to render the report into the selected format.";
		}
		else{
			result[0] = "Unexpected Error";
			result[1] = "An unexpected error has occurred. Please contact support.";
		}
		return result;
	}
	
}
