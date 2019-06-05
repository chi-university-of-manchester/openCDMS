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
 * Singleton class used to store the offset between the local
 * clock and the "PsyGrid" clock, and to provide the current 
 * time accounting for this offset.
 * <p>
 * When required, the offset should be set by a user of the 
 * repository before new repository objects are created.
 * 
 * @author Rob Harper
 *
 */
public class TimeOffset {

	private static final TimeOffset INSTANCE = new TimeOffset();
	
	/**
	 * The offset in milliseconds between the local clock and the
	 * "PsyGrid" clock.
	 */
	private long offset = 0;
	
	private TimeOffset(){}
	
	public static TimeOffset getInstance(){
		return INSTANCE;
	}

	/**
	 * Get the time offset in milliseconds between the local clock 
	 * and the "PsyGrid" clock.
	 * 
	 * @return The time offset in milliseconds.
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * Set the time offset in milliseconds between the local clock 
	 * and the "PsyGrid" clock.
	 * 
	 * @param offset
	 */
	public void setOffset(long offset) {
		this.offset = offset;
	}
	
	/**
	 * Get the current time, both the local time and the PsyGrid "system time
	 * accounting for the offset between the local clock and the "PsyGrid" clock.
	 * <p>
	 * Should be used when timestamping repository objects on the client
	 * side.
	 * 
	 * @return The current time.
	 */
	public TimeResult getTime(){
		long currentTime = System.currentTimeMillis();
		return new TimeResult(
				new Date(currentTime),
				new Date(currentTime+offset));
	}
	
}
