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


package org.psygrid.collection.entry.ui;

import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.psygrid.collection.entry.Editable;

/**
 * A display area for a text string that matches the look of a JLabel. Unlike a
 * JLabel, it provides line-wrapping and it is selectable. The latter is useful
 * if a user desires to copy and paste the text string.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 * 
 */
public class EntryLabel extends JTextArea implements Editable   {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public EntryLabel() {
        this(null, false);
    }
    /**
     * Creates an EntryLabel instance with the given text.
     * 
     * @param text
     *            The text to be displayed by the EntryLabel.
     */
    public EntryLabel(String text) {
        this(text, false);
    }

    /**
     * Creates an EntryLabel instance with the given text.
     * 
     * @param text
     *            The text to be displayed by the EntryLabel.
     * @param focusable
     *            Whether the EntryLabel is focusable.
     */
    public EntryLabel(String text, boolean focusable) {
        super(text);
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        setFocusable(focusable);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setDisabledTextColor(UIManager.getColor("Label.disabledForeground")); //$NON-NLS-1$
        setBackground(UIManager.getColor("Label.background")); //$NON-NLS-1$
        setForeground(UIManager.getColor("Label.foreground")); //$NON-NLS-1$
        setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
        setBorder((Border) UIManager.get("Label.border")); //$NON-NLS-1$
        setOpaque(false);
    }
    
    @Override
    public String toString() {
        return getText();
    }
    
    public void setEnabled(boolean b, boolean isStandardCode) {
        super.setEnabled(b && !isStandardCode);
    }
	public boolean isMandatory() {
		// TODO Auto-generated method stub
		return false;
	}
	public void setMandatory(boolean b) {
		// TODO Auto-generated method stub
		
	}

}
