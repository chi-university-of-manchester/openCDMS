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

package org.psygrid.collection.entry.chooser;

import org.psygrid.collection.entry.Application;

/**
 * Chooser Loader for selecting a record to render as a PDF document
 * suitable for printing.
 * <p>
 * A list of all available records is displayed to the user in a
 * dialog. On selection of a record the data for the record is
 * retrieved from the data repository, and the record is rendered as
 * a PDF before being displayed to the user in their systems default 
 * PDF viewer.
 * 
 * @author Rob Harper
 *
 */
public class PrintRecordChooserLoader extends PrintDocInstanceChooserLoader {

    public PrintRecordChooserLoader(Application application) {
        super(application);
    }

    protected void launchDialog(ChoosableList choosableList) {
        new PrintRecordChooserDialog(application, choosableList);
    }
}
