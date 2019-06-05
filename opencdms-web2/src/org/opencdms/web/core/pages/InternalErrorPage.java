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

package org.opencdms.web.core.pages;

/**
 * @author Rob Harper
 *
 */
public class InternalErrorPage extends GenericPage {

	@Override
	public String getPageDescription() {
		return "An unexpected error has occurred. Please try again later or contact support.";
	}

	@Override
	public String getPageHeading() {
		return "500 Internal Error";
	}

	@Override
	public String getPageTitle() {
		return "openCDMS | Unexpected Error";
	}

}
