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


package org.psygrid.web.helpers;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.springframework.web.servlet.ModelAndView;

/**
 * Helper class to provide exception handling support for
 * PsyGrid web-service calls, integrating with the Spring
 * MVC framework used by the PsyGrid web application.
 * 
 * @author Rob Harper
 *
 */
public class ExceptionHelper {

	public static ModelAndView handleWsException(Exception ex) {
		Map<String, Object> model = ModelHelper.getTemplateModel();
		if ( ex instanceof ConnectException ){
			//could not connect to the web-service
			return new ModelAndView("connectexception", model);
		}
		else if ( ex instanceof SocketTimeoutException ){
			//web-service call timed out or connection dropped
			return new ModelAndView("timeout", model);
		}
		else if ( ex instanceof NotAuthorisedFault ){
			//user not authorized to perform the requested WS operation
			return new ModelAndView("notauthorised", model);
		}
		else {
			model.put("exception", ex);
			return new ModelAndView("error", model);
		}
	}
	
}
