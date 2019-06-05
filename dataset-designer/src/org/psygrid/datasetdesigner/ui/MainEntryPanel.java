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

package org.psygrid.datasetdesigner.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import org.psygrid.datasetdesigner.custom.EntryButton;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Basic JPanel using GridBagLayout to display 
 * all types of entries in the right-hand panel of the DSD
 * @author pwhelan
 */
public class MainEntryPanel extends JPanel{
	
	//layout variables
	private GridBagLayout gbl;
	private GridBagConstraints gbc;
	
	private boolean buttonsEnabled = false;
	
	private EntryButton narrativeButton;
	private EntryButton textButton;
	private EntryButton optionButton;
	private EntryButton numericButton;
	private EntryButton dateButton;
	private EntryButton compositeButton;
	private EntryButton derivedButton;
	private EntryButton booleanButton;
	
	
	/**
	 * Constructor - lay out the various entries
	 */
	public MainEntryPanel() {
		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();
		setLayout(gbl);
		
		gbc.weighty= 0.5;
		gbc.ipadx = 2;
		gbc.ipady = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		gbc.insets = new Insets(10, 0, 10, 0);
		gbc.gridx = 0;
		gbc.gridy= 1;
		narrativeButton = new EntryButton("NarrativeEntry", "narrativeentry.png", 
										PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.narrativeentry"), "dsdecrfpagedescription");
		add(narrativeButton, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		textButton = new EntryButton("TextEntry", "longtextentry.png", 
										 PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.textentry"), "dsdtextentry");
		add(textButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		optionButton = new EntryButton("OptionEntry", "optionentry.png", 
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.optionentry"), "dsdoptionentry");
		add(optionButton, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		numericButton = new EntryButton("NumericEntry", "numericentry.png", 
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.numericentry"), "dsdnumericentry");
		add(numericButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		dateButton = new EntryButton("DateEntry", "dateentry.png", 
					PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.dateentry"), "dsddateentry");
		add(dateButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		booleanButton = new EntryButton("BooleanEntry", "booleanentry.png", 
					PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.booleanentry"), "dsdbooleanentry");
		add(booleanButton, gbc);

		
		gbc.gridx = 0;
		gbc.gridy = 7;
		compositeButton = new EntryButton("CompositeEntry", "compositeentry.png", 
					PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.compositeentry"), "dsdtableentry");
		add(compositeButton, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 8;
		derivedButton = new EntryButton("DerivedEntry", "derivedentry.png", 
					PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.derivedentry"), "dsdderivedentry");
		add(derivedButton, gbc);
		
		setButtonsEnabled(buttonsEnabled);
	}

	/**
	 * Check if the buttons are enabled
	 * @return true if the buttons are enabled; false if not
	 */
	public boolean isButtonsEnabled() {
		return buttonsEnabled;
	}

	/**
	 * Set the flag that controls the buttons enabled or not
	 * @param buttonsEnabled true if buttons are enabled; false if not
	 */
	public void setButtonsEnabled(boolean buttonsEnabled) {
		this.buttonsEnabled = buttonsEnabled;
		
		narrativeButton.setEnabled(buttonsEnabled);
		textButton.setEnabled(buttonsEnabled);
		optionButton.setEnabled(buttonsEnabled);
		numericButton.setEnabled(buttonsEnabled);
		dateButton.setEnabled(buttonsEnabled);
		compositeButton.setEnabled(buttonsEnabled);
		derivedButton.setEnabled(buttonsEnabled);
		booleanButton.setEnabled(buttonsEnabled);
		
		this.revalidate();
		this.repaint();
	}
	
	
}