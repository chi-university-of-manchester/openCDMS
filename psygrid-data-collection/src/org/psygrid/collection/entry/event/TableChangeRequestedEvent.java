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


package org.psygrid.collection.entry.event;

import java.util.EventObject;

import org.psygrid.collection.entry.model.VariableTableModel;
import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;

public class TableChangeRequestedEvent extends EventObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public enum Type { INSERT, DELETE }
    
    private final Type type;
    
    private final int rowIndex;
    
    private final EditableStatus editable;

    public TableChangeRequestedEvent(VariableTableModel source, Type type, 
            int rowIndex, EditableStatus editable) {
        super(source);
        this.type = type;
        this.rowIndex = rowIndex;
        this.editable = editable;
    }
    
    public TableChangeRequestedEvent(VariableTableModel source, Type type, 
            int rowIndex) {
        this(source, type, rowIndex, null);
    }

    public final int getRowIndex() {
        return rowIndex;
    }

    public final Type getType() {
        return type;
    }

    public EditableStatus isEditable() {
        return editable;
    }
    
}
