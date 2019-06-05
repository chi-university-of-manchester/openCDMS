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

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import org.psygrid.common.time.ClockSync;
import org.psygrid.data.repository.client.RepositoryClient;

/**
 * @author Rob Harper
 *
 */
public class RepositoryClockSync implements ClockSync {

	private static final RepositoryClient client = new RepositoryClient();
	
	/**
	 * @see org.psygrid.common.time.ClockSync#getOffset()
	 */
	public long getOffset() throws ConnectException {
		try{
			return client.getClockOffset();
		}
		catch(SocketTimeoutException ex){
			throw new ConnectException(ex.getMessage());
		}
	}

}
