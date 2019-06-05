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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 */
public class Icons {

    private static final Icons INSTANCE = new Icons();

    private static final String PACKAGE = "org/psygrid/collection/entry/icon"; //$NON-NLS-1$

    private Map<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();

    private String[] suffixes = new String[] { "png", "gif", "jpeg", "jpg" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private ImageIcon unknownIcon;

    public static Icons getInstance() {
        return INSTANCE;
    }

    private Icons() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                PACKAGE + "/unknownicon.png"); //$NON-NLS-1$
        unknownIcon = new ImageIcon(url);
    }

    public ImageIcon getIcon(String name) {
        ImageIcon icon = iconCache.get(name);
        if (icon != null) {
            return icon;
        }
        for (int i = 0; i < suffixes.length; i++) {
            URL resourceURL = Thread.currentThread().getContextClassLoader()
                    .getResource(PACKAGE + "/" + name + "." + suffixes[i]); //$NON-NLS-1$ //$NON-NLS-2$
            if (resourceURL != null) {
                icon = new ImageIcon(resourceURL);
                iconCache.put(name, icon);
                return icon;
            }
        }
        return unknownIcon;
    }
}
