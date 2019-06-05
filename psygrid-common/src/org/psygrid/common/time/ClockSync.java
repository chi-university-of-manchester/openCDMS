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

package org.psygrid.common.time;

import java.net.ConnectException;

/**
 * Interface for clock synchronization.
 * 
 * @author Rob Harper
 *
 */
public interface ClockSync {

	/**
	 * Calculate the offset (in milliseconds) between the
	 * local clock and a trusted remote clock.
	 * <p>
	 * A positive value indicates that the remote clock is
	 * later than the local clock; a negative value that the
	 * remote clock is earlier than the local clock.
	 * 
	 * @return The offset between local and remote clocks
	 * in milliseconds.
	 * 
	 * @throws ConnectException if no connection can be made
	 * to calculate the clock offset.
	 * @throws ClockSyncException if an operational error occured 
	 * shilst calculating the clock offset (other than a connection
	 * error).
	 */
	public long getOffset() throws ConnectException, ClockSyncException;
	
}
