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

/**
 * Exception to represent operational problems with clock
 * synchronization methods i.e. anything that isn't a 
 * connectivity problem.
 * 
 * @author Rob Harper
 *
 */
public class ClockSyncException extends Exception {

	private static final long serialVersionUID = -8341448158560770496L;

	/**
	 * 
	 */
	public ClockSyncException() {
	}

	/**
	 * @param message
	 */
	public ClockSyncException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ClockSyncException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ClockSyncException(String message, Throwable cause) {
		super(message, cause);
	}

}
