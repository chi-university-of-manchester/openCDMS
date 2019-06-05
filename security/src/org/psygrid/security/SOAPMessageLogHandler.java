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


//Created on Oct 12, 2005 by John Ainsworth
package org.psygrid.security;

/**
 * @author jda
 *
 */
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SOAPMessageLogHandler extends BasicHandler {
	private static Log log = LogFactory.getLog(SOAPMessageLogHandler.class);

	public void invoke(MessageContext msgContext) throws AxisFault {

		// if (msgContext.getPastPivot()) {
		Message inMsg = msgContext.getRequestMessage();
		Message outMsg = msgContext.getResponseMessage();

		if (outMsg != null) {
			log.info(outMsg.getSOAPPartAsString());
		} else {
			if (inMsg != null) {
				log.info(inMsg.getSOAPPartAsString());
			}
		}
		// }
	   }	public void undo(MessageContext msgContext) {
	}
}