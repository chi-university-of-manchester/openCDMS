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

import org.psygrid.common.ui.WrappedJOptionPane;

import org.psygrid.data.model.hibernate.ValidationRule;

import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureValidationRuleDialog;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureValidationRuleDialog.Location;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import java.awt.event.ActionEvent;

import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;


/**
 * Action to remove a validation rule from a dataset
 * @author pwhelan
 */
public class RemoveValidationRuleAction extends AbstractAction {
    /**
     * Version ID for serialization
     */
    private static final long serialVersionUID = -9073967744768272737L;

    /**
     * The list containing existing validation rules
     */
    private JList valdiationList;

    /**
     * The parent of this dialog (the configuration validation rule dialog)
     */
    private JDialog parentDialog;

    /**
     * Map of validation rules to their status (pending, approved)
     */
    private Map<ValidationRule, Location> validationMap; //list of validation rules for the dataset currently selected

    /**
     * Creates a new RemoveValidationRuleAction object.
     *
     * @param parentDialog the owner dialog
     * @param validationList the list of existing validation rules
     * @param validationMap the map of validation rules to locations for the dataset
     */
    public RemoveValidationRuleAction(JDialog parentDialog,
        JList validationList, Map<ValidationRule, Location> validationMap) {
        super(PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.actions.remove"));
        this.valdiationList = validationList;
        this.parentDialog = parentDialog;
        this.validationMap = validationMap;
    }

    /**
     * Action event Handling; show confirmation dialog and then
     * delete from map, list and refresh the table (used in DEL mode)
     *
     * @param aet the trigger event
     */
    public void actionPerformed(ActionEvent aet) {
        //System.out.println("UnitsList model "+unitsList);
        //unitsList.remove(unitsList.getSelectedIndex());
        int result = WrappedJOptionPane.showConfirmDialog(parentDialog,
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.actions.confirmdeleterule"),
                PropertiesHelper.getStringFor(
                    "org.psygrid.datasetdesigner.actions.confirmdelete"),
                WrappedJOptionPane.YES_NO_OPTION);

        if (WrappedJOptionPane.NO_OPTION == result) {
            return;
        } else if (WrappedJOptionPane.OK_OPTION == result) {
            System.out.println("RV : removing val rule");
            System.out.println("selected value is " +
                valdiationList.getSelectedValue());
            validationMap.remove(valdiationList.getSelectedValue());
            ((DefaultListModel) valdiationList.getModel()).removeElement(valdiationList.getSelectedValue());
            ((ConfigureValidationRuleDialog) parentDialog).refreshTable();
        }
    }
}
