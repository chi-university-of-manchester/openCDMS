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


package org.psygrid.datasetdesigner.utils;

import java.net.URL;

import java.awt.Dimension;

import javax.help.HelpBroker;
import javax.help.HelpSet;

import javax.swing.JButton;

import org.psygrid.datasetdesigner.utils.IconsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jvnet.substance.SubstanceDefaultLookAndFeel;

/**
 * Used for interaction with JavaHelp
 * 
 * @author Rob Harper
 */
public final class HelpHelper {

	//singleton instance
	private static HelpHelper INSTANCE = null;

	//log
	private final static Log LOG = LogFactory.getLog(HelpHelper.class);
	
	//HelpBroker used for brokering help objects
	private HelpBroker hb = null;
	
	/**
	 * Constructor 
	 * Initialise the JaveHelp and create a broker
	 * Set the home id to be the DSD
	 */
	private HelpHelper()  {
		// Private constructor to enforce singleton pattern
		try{
		    ClassLoader cl = HelpHelper.class.getClassLoader();
		    URL hsURL = HelpSet.findHelpSet(cl, "org/psygrid/doc/jhelpset.hs");
		    HelpSet hs = new HelpSet(cl, hsURL);
		    hs.setHomeID("dsd");
			hb = hs.createHelpBroker();
			
		} 
		catch (Exception ex) {
			LOG.error("HelpSet not found",ex);
		}
	}

	/**
	 * Get the HelpHelper singleton
	 * @return the HelpHelper singleton
	 */
	public static HelpHelper getInstance() {
		if ( null == INSTANCE ){
			INSTANCE = new HelpHelper();
		}
		return INSTANCE;
	}
	
	/**
	 * Return the help broker for this Java Help
	 * @return the HelpBroker
	 */
	public HelpBroker getHelpBroker(){
		return hb;
	}
	
	/*
	 * Return a help button for the given id
	 * Used for context-sensitive help
	 * @param id the help id reference for use with JavaHelp
	 * @return the configured help button referencing the help section
	 */
	public JButton getHelpButtonWithID(String id) {
		JButton helpButton = new JButton(IconsHelper.getInstance().getImageIcon("help.png"));
		helpButton.setPreferredSize(new Dimension(20, 20));
		helpButton.setFocusable(false);

		// Mark button to never paint its background - need this for Substance LAF
	    helpButton.putClientProperty(
	    		SubstanceDefaultLookAndFeel.BUTTON_PAINT_NEVER_PROPERTY, Boolean.TRUE);
	    
		helpButton.setContentAreaFilled(false);
		helpButton.setBorderPainted(false);
		HelpHelper.getInstance().getHelpBroker().enableHelpOnButton(helpButton, id, HelpHelper.getInstance().getHelpBroker().getHelpSet());
		return helpButton;
	}
	
}
