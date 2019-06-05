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


package org.psygrid.collection.entry.renderer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.psygrid.collection.entry.ui.EntryWithButton;
import org.psygrid.data.model.hibernate.StandardCode;

import com.jgoodies.binding.value.ValueModel;

final class AbstractRendererValueChangeListener 
        implements PropertyChangeListener {

    private final EntryWithButton entryField;
    private final ValueModel stdCodeModel;

    AbstractRendererValueChangeListener(EntryWithButton entryField,
            ValueModel stdCodeModel) {
        this.entryField = entryField;
        this.stdCodeModel = stdCodeModel;
    }

    public final void propertyChange(PropertyChangeEvent evt) {
        StandardCode stdCode = (StandardCode) stdCodeModel.getValue();
        boolean popupButtonEnabled = entryField.getPopupButton().isEnabled();
        boolean enabled = entryField.getEnabled();
        if (stdCode != null) {
            entryField.setEnabled(enabled, true);
            entryField.getPopupButton().setToolTipText(
                    RendererHelper.getInstance().getNoAnswerButtonOtherMessage());
        } else {
            entryField.setEnabled(enabled, false);
            entryField.getPopupButton().setToolTipText(
                    RendererHelper.getInstance().getNoAnswerButtonInitialMessage());
        }
        entryField.getPopupButton().setEnabled(popupButtonEnabled);
    }

}
