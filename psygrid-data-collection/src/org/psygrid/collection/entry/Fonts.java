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


package org.psygrid.collection.entry;

import java.awt.Font;

import javax.swing.UIManager;

public class Fonts {
    private static final Fonts INSTANCE = new Fonts();

    private Font boldLabelFont;

    private Font titleFont;

    private Font smallFont;

    private Font headerFont;
    
    private Fonts() {
        // Private constructor to enforce singleton pattern
    }
    
    public static final Fonts getInstance() {
        return INSTANCE;
    }
    
    public Font getBoldLabelFont() {
        if (boldLabelFont == null) {
            Font labelFont = UIManager.getFont("Label.font"); //$NON-NLS-1$
            boldLabelFont = labelFont.deriveFont(Font.BOLD);
        }
        return boldLabelFont;
    }
    
    public Font getTitleFont() {
        if (titleFont == null) {
            Font boldLabel = getBoldLabelFont();
            titleFont = boldLabel.deriveFont((float) boldLabel.getSize() + 2);
        }
        return titleFont;
    }
    
    public Font getHeaderFont() {
        if (headerFont == null) {
            Font boldLabel = getBoldLabelFont();
            headerFont = boldLabel.deriveFont((float) boldLabel.getSize() + 1);
        }
        
        return headerFont;
    }
    
    public Font getSmallFont() {
        if (smallFont == null) {
            Font labelFont = UIManager.getFont("Label.font"); //$NON-NLS-1$
            smallFont = labelFont.deriveFont((float) labelFont.getSize() - 1);
        }
        return smallFont;
    }
}
