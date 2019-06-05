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
package org.psygrid.datasetdesigner.actions;

import org.psygrid.datasetdesigner.ui.configurationdialogs.AddAssociatedFormDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;


/**
 * Add an associated consent form action
 * @author pwhelan
 */
public class AddAssociatedFormAction extends AbstractAction {
    /**
     * dataset selector box
     */
    private JComboBox datasetBox;

    /**
     * list of associated consent forms
     */
    private JList associatedList;

    /**
     * the owner dialog
     */
    private JDialog parentDialog;

    /**
     * Constructor
     * @param parentDialog the owner dialog
     * @param datasetBox the dataset selector drop-down
     * @param associatedList the list of associated consent forms
     */
    public AddAssociatedFormAction(JDialog parentDialog, JComboBox datasetBox,
        JList associatedList) {
        super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
        this.parentDialog = parentDialog;
        this.datasetBox = datasetBox;
        this.associatedList = associatedList;
    }

    /**
     * Show add associated form dialog
     * @param aet the calling action event
     */
    public void actionPerformed(ActionEvent aet) {
        new AddAssociatedFormDialog(parentDialog, datasetBox, associatedList);
    }
}
