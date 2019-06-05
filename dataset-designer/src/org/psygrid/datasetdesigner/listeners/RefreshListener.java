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
package org.psygrid.datasetdesigner.listeners;

import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.datasetdesigner.ui.DocumentPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Class that refreshes the document panel when
 * an action event occurs
 * @author pwhelan
 */
public class RefreshListener implements ActionListener {
    /**
     * The <code>DocumentPanel</code> that requires refreshing
     */
    private DocumentPanel docPanel;

    /**
     * The section to which the selected entry belongs
     */
    private int section = 0;

    /**
     * Constructor: create the listener and set the <code>
     * DocumentPanel</code>
     * @param docPanel
     */
    public RefreshListener(DocumentPanel docPanel, Entry selectedEntry) {
        this.docPanel = docPanel;

        //find the section to which the selected entry belongs;
        for (int i = 0; i < docPanel.getDocument().numSections(); i++) {
            if (selectedEntry.getSection()
                                 .equals(docPanel.getDocument().getSection(i))) {
                section = i;

                break;
            }
        }
    }

    /**
     * Refresh the <code>DocumentPanel</code> when
     * an action is performed and ensure the relevant
     * section is still displayed
     */
    public void actionPerformed(ActionEvent aet) {
        docPanel.refresh(section);
    }
}
