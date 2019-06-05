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


package org.psygrid.collection.entry.ui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.Editable;
import org.psygrid.collection.entry.event.TableChangeRequestedEvent;
import org.psygrid.collection.entry.event.TableChangeRequestedListener;
import org.psygrid.collection.entry.model.VariableTableModel;
import org.psygrid.collection.entry.renderer.BasicRenderer;
import org.psygrid.collection.entry.renderer.Renderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;

public class VariableTable extends EntryTable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JButton addRowButton;
    private List<JButton> deleteRowButtons;
    
    public VariableTable(VariableTableModel model, RendererHandler rendererHandler, JLabel validationLabel) {
        super(model, rendererHandler, validationLabel);
        initEventHandling();
    }
    
    private void initEventHandling() {
        getModel().addTableChangeRequestedListener(new TableChangeRequestedListener() {
            public void changeRequested(TableChangeRequestedEvent event) {
                handleTableChangeRequestedEvent(event);
            }
        });
    }
    
    private void handleTableChangeRequestedEvent(TableChangeRequestedEvent event) {
        JFrame window = RendererHelper.getInstance().findJFrame(this);
        String title = Messages.getString("VariableTable.enterChangeReasonTitle");
        String labelText = null;
        switch (event.getType()) {
        case DELETE:
            labelText = Messages.getString("VariableTable.enterRowRemovalReason");
            break;
        case INSERT:
            labelText = Messages.getString("VariableTable.enterRowAdditionReason");
            break;
        }
        AnnotationDialog dialog = new AnnotationDialog(window, title, labelText);
        dialog.setVisible(true);
        if (dialog.isOkSelected()) {
            String comment = dialog.getAnnotation();
            switch (event.getType()) {
            case DELETE:
                getModel().removeRow(event.getRowIndex(), comment);
                break;
            case INSERT:
                EditableStatus editable = event.isEditable();
                getModel().addRow(event.getRowIndex(), comment, editable);
                break;
            }
            getModel().markAsEdited(comment);
        }
    }
    
    public JButton getAddRowButton() {
        return addRowButton;
    }
    
    public JButton getDeleteRowButton(int row) {
        return deleteRowButtons.get(row);
    }
    
    @Override
    public VariableTableModel getModel() {
        return (VariableTableModel) super.getModel();
    }
    
    @Override
    protected void populateComponents() {
        super.populateComponents();
        deleteRowButtons = new ArrayList<JButton>();
    }
    
    @Override
    protected int getRightMargin() {
        if (deleteRowButtons.size() > 0  ) {
            return deleteRowButtons.get(0).getPreferredSize().width;
        }
        return 0;
    }
    
    @Override
    protected JComponent getRightComponent(int row) {
        return deleteRowButtons.get(row);
    }
    
    @Override
    protected int getFarRightMargin() {
        if ( applyStdCodeToRowButtons.size() > 0 ) {
            return applyStdCodeToRowButtons.get(0).getPreferredSize().width;
        }
        return 0;
    }
    
    @Override
    protected JComponent getFarRightComponent(int row) {
        return applyStdCodeToRowButtons.get(row);
    }
    
    @Override
    protected JComponent getBottomComponent() {
        return addRowButton;
    }
    
    @Override
    protected void insertRow(int rowIndex, EditableStatus editable) {
        JButton deleteRowButton = 
            new JButton(getModel().getRemoveRowAction(rowIndex));
        deleteRowButtons.add(rowIndex, deleteRowButton);
        add(deleteRowButton);
        super.insertRow(rowIndex, editable);
    }
    
    @Override
    protected void deleteRow(int row) {
        JButton deleteRowButton = deleteRowButtons.remove(row);
        remove(deleteRowButton);
        super.deleteRow(row);    
    }

    @Override
    public void setEnabled(boolean enabled, boolean isStandardCode) {
        if (isEnabled() == enabled) {
            return;
        }

        VariableTableModel vModel = getModel();
        for (Action removeRowAction : vModel.getRemoveRowActions()) {
            removeRowAction.setEnabled(enabled);
        }
        if (enabled) {
            vModel.processRemoveRowActionsStatus();
        }
        
        vModel.getAddRowAction().setEnabled(enabled);
        
        super.setEnabled(enabled, isStandardCode);
    }
    
    /**
     * This method sets the state of the table to editable, but it also uses
     * its internal state to determine how certain items behave. Namely,
     * if the DocumentStatus == REJECTED, <code>editable</code> is ignored.
     * TODO Find a better solution for this
     */
    @Override
    public void setEditable(boolean editable) {
        VariableTableModel vModel = getModel();
        boolean value;
        if (editable) {
            value = isEnabled();
        }
        else {
            value = false;
        }
        DocumentStatus docStatus = null;
        if (model.getDocInstance().getStatus() == null) {
        	docStatus = DocumentStatus.VIEW_ONLY;
        }
        else {
        	docStatus = DocumentStatus.valueOf(model.getDocInstance().getStatus());
        }
        
        //Added to fix bug with being able to select std codes when only viewing a document
    	if (!editable) {
    		for (Map<JComponent,Boolean> row : components) {
    			for (JComponent comp : row.keySet()) {
    				if (comp instanceof Editable) {
    					((Editable) comp).setEnabled(false, false);
    				}
    				else {
    					comp.setEnabled(false);
    				}
    			}
    		}
    		for (JButton stdCodeButton: applyStdCodeToRowButtons) {
    			stdCodeButton.setEnabled(false);
    		}
    	}
        
        if ((docStatus == DocumentStatus.REJECTED || 
           docStatus == DocumentStatus.VIEW_ONLY ||
             docStatus == DocumentStatus.CONTROLLED ) 
        		&& !value) {
        	return;
        }
        vModel.getAddRowAction().setEnabled(value);
        for (Action removeRowAction : vModel.getRemoveRowActions()) {
            removeRowAction.setEnabled(value);
        }
        super.setEditable(editable);
    }
    
    @Override
    protected void initComponents() {
        super.initComponents();
        addRowButton = new JButton(getModel().getAddRowAction());
        add(addRowButton);
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();

        if (deleteRowButtons.size() > 0)  {
            d.width += deleteRowButtons.get(0).getPreferredSize().width;
        }
        if (applyStdCodeToRowButtons.size() > 0)  {
            d.width += applyStdCodeToRowButtons.get(0).getPreferredSize().width;
        }
        
        d.height += addRowButton.getPreferredSize().height + 5;
        
        return d;
    }
    
    @Override
    protected Renderer getRenderer(int rowIndex, int columnIndex,
            EditableStatus editable) {
        Renderer renderer = super.getRenderer(rowIndex, columnIndex, editable);
        if (renderer instanceof BasicRenderer == false) {
            throw new IllegalStateException(
                    "A BasicRenderer was expected, " //$NON-NLS-1$
                            + "but the type received was: " //$NON-NLS-1$
                            + renderer.getClass());
        }
        return renderer;
    }
}
