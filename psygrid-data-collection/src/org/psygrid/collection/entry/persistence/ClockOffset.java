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

package org.psygrid.collection.entry.persistence;

/**
 * Class to store details of the offset between the local clock
 * and the system clock.
 * 
 * @author Rob Harper
 *
 */
public class ClockOffset {

	/**
	 * The offset between the local clock and the system clock
	 * in milliseconds
	 */
	private long offset;
	
	/**
	 * The source of this offset
	 */
	private OffsetSource source;
	
	public enum OffsetSource{
		NTP, SERVER;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public OffsetSource getSource() {
		return source;
	}

	public void setSource(OffsetSource source) {
		this.source = source;
	}
	
}
