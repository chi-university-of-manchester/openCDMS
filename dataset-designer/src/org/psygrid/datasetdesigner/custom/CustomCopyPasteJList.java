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
package org.psygrid.datasetdesigner.custom;

import javax.swing.JList;
import javax.swing.DefaultListModel;

import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.utils.JListTransferHandler;


/**
 * CustomCopyPasteJList
 * List that uses a custom built transfer handler
 * to copy sensible values from the JList (e.g. role names
 * instead of underlying role implementation)
 *
 * @author pwhelan
 */
public class CustomCopyPasteJList extends JList {
    /**
     * Constructor
     * Use the custom-built transfer handler
     */
    public CustomCopyPasteJList() {
        super();
        init();
    }

    /**
     * Constructor
     * Use the custom-built transfer handler
     * @param data a list of data to prepopulate the list
     */
    public CustomCopyPasteJList(Object[] data) {
        super(data);
        init();
    }
    
    private void init() {
        setTransferHandler(new JListTransferHandler());
        setCellRenderer(new OptionListCellRenderer());
        setModel(new DefaultListModel());
    }
}
