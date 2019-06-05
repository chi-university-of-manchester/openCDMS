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

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author pwhelan
 *
 */
public class XMLFileFilter extends FileFilter {
	
    public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".xml") ||
        f.isDirectory();
    }

    public String getDescription() {
        return "XML file";
    }
}
