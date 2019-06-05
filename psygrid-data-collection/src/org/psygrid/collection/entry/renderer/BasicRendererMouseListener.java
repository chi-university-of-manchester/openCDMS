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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.data.model.hibernate.ResponseStatus;
import org.psygrid.data.model.hibernate.Status;

public final class BasicRendererMouseListener extends MouseAdapter {
    private final BasicPresModel presModel;

    private final MouseListener listener;

    private final EntryComponent field;

    private final Status docStatus;
    
    private final ValueFactory valueFactory;

    public BasicRendererMouseListener(BasicPresModel presModel,
            MouseListener listener, EntryComponent field, Status docStatus,
            ValueFactory valueFactory) {
        this.presModel = presModel;
        this.listener = listener;
        this.field = field;
        this.docStatus = docStatus;
        this.valueFactory = valueFactory;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (handleMouseListener(e)) {
            e.getComponent().removeMouseListener(this);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (handleMouseListener(e)) {
            e.getComponent().removeMouseListener(this);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (handleMouseListener(e)) {
            e.getComponent().removeMouseListener(this);
        }
    }
    
    private enum EditTransformed { NO, YES, COMPONENT_DISABLED, NOT_TRANSFORMED, RIGHT_CLICK }
    
    private void multiCastMouseEvent(MouseEvent event) {
        switch (event.getID()) {
        case MouseEvent.MOUSE_CLICKED:
            listener.mouseClicked(event);
            break;
        case MouseEvent.MOUSE_PRESSED:
            listener.mousePressed(event);
            break;
        case MouseEvent.MOUSE_RELEASED:
            listener.mouseReleased(event);
            break;
        }
    }
    protected boolean handleMouseListener(MouseEvent event) {
    	if (docStatus == null) {
    		return false;
    	}
        switch (DocumentStatus.valueOf(docStatus)) {
        case INCOMPLETE:
        case COMPLETE:
            switch (handleTransformedListener(event)) {
            case COMPONENT_DISABLED:
            case NO:
                return false;
            case YES:
                //TODO Consider propagating this event
                enableEditingInTransformed();
                return true;
            case NOT_TRANSFORMED:
            case RIGHT_CLICK:
                if (listener != null) {
                	multiCastMouseEvent(event);
                }
                return false;
            }
            break;
        case APPROVED:
        case PENDING:
            if (listener != null) {
                multiCastMouseEvent(event);
            }
            return false;
        case REJECTED:
        case CONTROLLED:
            BasicRendererMouseListener.EditTransformed result = handleTransformedListener(event);
            switch (result) {
            case COMPONENT_DISABLED:
            case NO:
                return false;
            case YES:
            case NOT_TRANSFORMED:
            case RIGHT_CLICK:
                if (listener != null) {
                	multiCastMouseEvent(event);
                }
                return false;
            }
        case DATASET_DESIGNER:
        	return false;
        default:
            if (listener != null) {
            	multiCastMouseEvent(event);
            }
            return false;
        }
        return false;
    }
    
    private BasicRendererMouseListener.EditTransformed handleTransformedListener(MouseEvent event) {
    	if ( MouseEvent.BUTTON3 == event.getButton() ){
    		return EditTransformed.RIGHT_CLICK;
    	}
        if ((event.getID() == MouseEvent.MOUSE_CLICKED) && (event.getClickCount() == 2)) { 
            if ( !presModel.getEntryStatusModel().getValue().equals(EntryStatus.DISABLED) ) {
	            if (presModel.getTransformedModel().getValue().equals(Boolean.TRUE)) {
	                if (presModel.getResponseStatusModel().getValue().equals(ResponseStatus.FLAGGED_INVALID)) {
	                    return EditTransformed.YES;
	                }
	                String title = "Value Transformed";
	                String message = "This value has been transformed. Are you sure you want to re-enter a new one?";
	                // For some reason when we pass the correct component, the confirmation
	                // dialog box sometimes appears in the bottom left corner. Passing
	                // null make it appear in the center which is slightly better
	                int result = JOptionPane.showConfirmDialog(null,
	                        message, title, JOptionPane.YES_NO_OPTION);
	                if (result == JOptionPane.YES_OPTION) {
	                    return EditTransformed.YES;
	                }
	                return EditTransformed.NO;
	            }
	            return EditTransformed.NOT_TRANSFORMED;
            }
            return EditTransformed.COMPONENT_DISABLED;            
        }
        return EditTransformed.COMPONENT_DISABLED;
    }

    private void enableEditingInTransformed() {
        field.setEditable(true);
        BasicResponse response = presModel.getResponse();
        BasicEntry entry = (BasicEntry) response.getEntry();
        IValue value = valueFactory.createValue(entry);
        RendererHelper.getInstance().setResponseValue(response, value);
        presModel.setBean(value);
    }
}