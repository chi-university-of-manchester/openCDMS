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

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

/**
 * Clock synchronization implementation that uses NTP to
 * calculate the offset between the local clock and a 
 * trusted remote clock.
 * 
 * @author Rob Harper
 *
 */
public class NtpClockSync implements ClockSync {

	/**
	 * Address of NTP time server
	 */
	private final String timeServer;
	
	public NtpClockSync(String timeServer) {
		super();
		this.timeServer = timeServer;
	}

	public long getOffset() throws ConnectException, ClockSyncException {
		try{
			InetAddress inetAdd = InetAddress.getByName(timeServer);
			NTPUDPClient client = new NTPUDPClient();
			client.setDefaultTimeout(10000);
			client.open();
			TimeInfo info = client.getTime(inetAdd);
			info.computeDetails();
			return info.getOffset().longValue();
		}
		catch(IOException ex){
			ConnectException cex = new ConnectException(ex.getMessage());
			cex.initCause(ex);
			throw cex;
		}
	}
	
}
