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

import org.psygrid.data.export.security.DataExportActions;
import org.psygrid.data.export.security.ExportSecurityValues;

import javax.swing.JComboBox;


/**
 * Reusable extension of JComboBox to contains export security settings
 * @author pwhelan
 *
 */
public class ExportSecurityComboBox extends JComboBox {
    /**
     * Constructor add items to the combo box
     * @param viewOnly if in DEL view only mode
     */
    public ExportSecurityComboBox(boolean viewOnly) {
        addItem(null);
        addItem(DataExportActions.ACTION_EXPORT_RESTRICTED);
        addItem(DataExportActions.ACTION_EXPORT_UNRESTRICTED);
        addItem(DataExportActions.ACTION_EXPORT_TRANSFORMED);

        if (viewOnly) {
            setEditable(false);
        }
    }

    /**
     * Select the export security value
     * @param securityValue The export security value to be set
     */
    public void setSelectedSecurityValue(ExportSecurityValues securityValue) {
        if (securityValue == ExportSecurityValues.EXPORT_LEVEL_0) {
            setSelectedItem(DataExportActions.ACTION_EXPORT_UNRESTRICTED);
        } else if (securityValue == ExportSecurityValues.EXPORT_LEVEL_8) {
            setSelectedItem(DataExportActions.ACTION_EXPORT_TRANSFORMED);
        } else if (securityValue == ExportSecurityValues.EXPORT_LEVEL_15) {
            setSelectedItem(DataExportActions.ACTION_EXPORT_RESTRICTED);
        } else {
            setSelectedItem(null);
        }
    }

    /**
     * Get the currently selected export security value
     * @return export security value
     */
    public ExportSecurityValues getSecurityValue() {
        if (getSelectedItem() == null) {
            return null;
        } else if (getSelectedItem()
                           .equals(DataExportActions.ACTION_EXPORT_UNRESTRICTED)) {
            return ExportSecurityValues.EXPORT_LEVEL_0;
        } else if (getSelectedItem()
                           .equals(DataExportActions.ACTION_EXPORT_TRANSFORMED)) {
            return ExportSecurityValues.EXPORT_LEVEL_8;
        } else if (getSelectedItem()
                           .equals(DataExportActions.ACTION_EXPORT_RESTRICTED)) {
            return ExportSecurityValues.EXPORT_LEVEL_15;
        } else {
            return null;
        }
    }
}
