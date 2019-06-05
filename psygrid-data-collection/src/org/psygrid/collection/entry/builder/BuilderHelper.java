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


package org.psygrid.collection.entry.builder;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.psygrid.collection.entry.FormView;
import org.psygrid.collection.entry.renderer.PresModelRenderer;
import org.psygrid.collection.entry.ui.DividerLabel;
import org.psygrid.data.model.hibernate.EditAction;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * Helper class that contains methods useful to BuilderSPI implementations.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 */
public class BuilderHelper {

    /**
     * Appends a gap row to <code>builder</code> and moves the cursor to the 
     * next row. This gap row has the size required to separate two entries
     * in a form. As a result, this method should be called by BuilderSPI
     * implementations once they are finished building their own entry.
     * 
     * @param builder DefaultFormBuilder that is being used to build the entries.
     */
    public static void appendEndingGap(DefaultFormBuilder builder) {
        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine();
    }
    
    /**
     * Adds a row to <code>builder</code> with the row spec retrieved from 
     * {@link FormView#getDefaultRowSpec()} and appends <code>label</code> into
     * this row in column 3 spanning until the last column.
     * 
     * @param builder DefaultFormBuilder to add <code>label</code> to.
     * @param label JComponent to be added to <code>builder</code>.
     */
    public static void appendLabel(DefaultFormBuilder builder, JComponent label) {
        builder.appendRow(FormView.getDefaultRowSpec());
        builder.nextLine();
        int startColumn = 3;
        int columnSpan = builder.getColumnCount() - startColumn + 1;
        int rowHeight = 1;
        CellConstraints cc = new CellConstraints(startColumn,
                builder.getRow(), columnSpan, rowHeight);
        builder.add(label, cc);
    }

    /**
     * Adds a divider label; used for separating entries (for calculation by DSDesigner)
     * 
     * @param builder DefaultFormBuilder to add <code>label</code> to.
     * @param label JComponent to be added to <code>builder</code>.
     */
    public static void appendDivider(DefaultFormBuilder builder, DividerLabel label) {
        int startColumn = 3;
        int columnSpan = builder.getColumnCount() - startColumn + 1;
        builder.append(label, columnSpan);
    }

    
    /**
     * Adds a row to <code>builder</code> with the row spec retrieved from 
     * {@link FormView#getDefaultRowSpec()} and appends <code>validationLabel</code>
     * (if not <code>null</code> into this row in column 1 and <code>label</code>
     * into this row in column 3 spanning until the last column.
     * 
     * @param builder DefaultFormBuilder to add <code>label</code> to.
     * @param label JComponent to be added to <code>builder</code>.
     * @param validationLabel Validation label to be added to <code>builder</code>.
     */
    public static void appendLabel(DefaultFormBuilder builder, JComponent label, JComponent validationLabel, boolean span) {
        builder.appendRow(FormView.getDefaultRowSpec());
        builder.nextLine();
        CellConstraints cc = new CellConstraints();
        if ( null != validationLabel ){
            builder.add(validationLabel, cc.xy(1, builder.getRow()));
        }
        int startColumn = 3;
        if ( span ){
            int columnSpan = builder.getColumnCount() - startColumn + 1;
            builder.add(label, cc.xywh(startColumn, builder.getRow(), columnSpan, 1));
        }
        else{
            builder.add(label, cc.xy(startColumn, builder.getRow()));
        }
    }

    /**
     * Adds a row to <code>builder</code> with the row spec retrieved from 
     * {@link FormView#getDefaultRowSpec()} and appends <code>renderer</code>'s 
     * help label and label to it. This is done in the following way:<p>
     *
     * The help label is added to the first column and the label is added
     * to the third column spanning to the last column.
     * 
     * @param builder DefaultFormBuilder to add the components to.
     * @param renderer PresModelRenderer containing the label and help
     * label components that are to be inserted into <code>builder</code>.
     */
    public static void appendHelpLabelAndLabel(DefaultFormBuilder builder,
            PresModelRenderer<?> renderer) {
        builder.appendRow(FormView.getDefaultRowSpec());
        builder.nextLine();
        CellConstraints cc = new CellConstraints();
        
        EditAction editable = renderer.getPresModel().getEntry().getEditingPermitted();
        if ((EditAction.DENY.equals(editable)
        		|| EditAction.READONLY.equals(editable))
        		&& renderer.getRestrictedLabel() != null) {
            builder.add(renderer.getRestrictedLabel(), cc.xy(builder.getColumn(), builder.getRow()));
        }
        else {
        	JLabel helpLabel = renderer.getHelpLabel();	
        	builder.add(helpLabel, cc.xy(builder.getColumn(), builder.getRow()));
        }
        builder.nextColumn(2);
        JComponent label = renderer.getLabel();
        int columnSpan = builder.getColumnCount() - builder.getColumn() + 1;
        builder.add(label, cc.xywh(builder.getColumn(), builder.getRow(), 
                columnSpan, 1));
    }
    
    public static void assertRowIndexEqualsZero(BuilderData builderData) {
        if (builderData.getRowIndex() != 0) {
            throw new IllegalArgumentException("builderData#getRowIndex() must be 0," + //$NON-NLS-1$
                    " but it is: " + builderData.getRowIndex()); //$NON-NLS-1$
        }
    }
    
}
