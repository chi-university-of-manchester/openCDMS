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


package org.psygrid.collection.entry.util;

import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Rob Harper
 *
 */
public final class HelpHelper {

	private static HelpHelper INSTANCE = null;

	private final static Log LOG = LogFactory.getLog(HelpHelper.class);
	
	private HelpBroker hb = null;
	
	private HelpHelper()  {
		// Private constructor to enforce singleton pattern
		try{
		    ClassLoader cl = HelpHelper.class.getClassLoader();
		    URL hsURL = HelpSet.findHelpSet(cl, "org/psygrid/doc/jhelpset.hs");
		    HelpSet hs = new HelpSet(cl, hsURL);
		    hs.setHomeID("cocoa");
			hb = hs.createHelpBroker();
		} 
		catch (Exception ee) {
			LOG.error("HelpSet not found", ee);
		}
		
	}

	public static HelpHelper getInstance() {
		if ( null == INSTANCE ){
			INSTANCE = new HelpHelper();
		}
		return INSTANCE;
	}
	
	public HelpBroker getHelpBroker(){
		return hb;
	}
	
}
