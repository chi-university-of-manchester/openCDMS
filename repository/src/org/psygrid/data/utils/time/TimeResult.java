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

package org.psygrid.data.utils.time;

import java.util.Date;

/**
 * Class to hold the result of a query to find the current
 * time.
 * 
 * @author Rob Harper
 *
 */
public class TimeResult {

	/**
	 * The time according to the local clock.
	 */
	private final Date localTime;
	
	/**
	 * The time according to the PsyGrid "system" clock.
	 */
	private final Date systemTime;

	public TimeResult(Date localTime, Date systemTime) {
		super();
		this.localTime = localTime;
		this.systemTime = systemTime;
	}

	public Date getLocalTime() {
		return localTime;
	}

	public Date getSystemTime() {
		return systemTime;
	}
	
}
