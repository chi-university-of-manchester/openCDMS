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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Simple class to calculate the difference between local time
 * and the time on a specified webserver.
 * <p>
 * Should only be used when NTP is not available, as this is nowhere
 * near as accurate.
 * 
 * @author Rob Harper
 *
 */
public class HttpClockSync implements ClockSync {

	/**
	 * The format of a date returned in an HTTP header.
	 */
	private static final SimpleDateFormat httpDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
	
	/**
	 * The URL of a remote server with a trusted clock.
	 */
	private final String url;
	
	/**
	 * The number of HTTP responses to request so that an
	 * average offset may be calculated.
	 */
	private final int calls;
	
	/**
	 * The time in milliseconds to sleep between requests
	 */
	private final long sleep;

	public HttpClockSync(String server) {
		super();
		this.url = server;
		this.calls = 10;
		this.sleep = 1250;
	}
	
	public HttpClockSync(String url, int calls, long sleep) {
		super();
		this.url = url;
		this.calls = calls;
		this.sleep = sleep;
	}

	public long getOffset() throws ConnectException, ClockSyncException {
		
		try{
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(url);
			method.addRequestHeader("Pragma", "no-cache");
			//execute the request once here which is thrown away - this is because
			//the first request has consistently shown to have a higher latency
			//than subsequent ones, which I assume is due to some sort of initialization
			//at our end
			client.executeMethod(method);
			
			long offsetsSum = 0;
			for ( int i=0; i<calls; i++ ){
				Date start = new Date();
				client.executeMethod(method);
				Date finish = new Date();
				long latency = (finish.getTime() - start.getTime()) / 2;
				
				Header dateHeader = method.getResponseHeader("date");
				Date serverDate = httpDateFormat.parse(dateHeader.getValue());
			
				offsetsSum += ((serverDate.getTime() + latency) - finish.getTime());
				
				//As the date in the HTTP header is only to second accuracy we need
				//to wait now for the clock to tick over again. Otherwise we will likely
				//have all our offsets calculated from the same date value (if the network
				//latency is small).
				Thread.sleep(sleep);
				
			}
			
			return offsetsSum / calls;
		}
		catch(IOException ex){
			ConnectException cex = new ConnectException(ex.getMessage());
			cex.initCause(ex);
			throw cex;
		}
		catch(InterruptedException ex){
			throw new ClockSyncException(ex);
		}
		catch(ParseException ex){
			throw new ClockSyncException(ex);
		}
	}
	
}
