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

import org.psygrid.collection.entry.model.EntryTableModel;
import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;

public class EntryTableModelEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public enum Type { INSERT, UPDATE, DELETE }
    
    private final Type type;
    
    private final int rowIndex;
    
    private final String comment;
    
    private final EditableStatus editable;
    
    public EntryTableModelEvent(EntryTableModel source, Type type, int rowIndex,
            String comment, EditableStatus editable) {
        super(source);
        this.type = type;
        this.rowIndex = rowIndex;
        this.comment = comment;
        this.editable = editable;
    }

    public EntryTableModelEvent(EntryTableModel source, Type type, int rowIndex,
            String comment) {
        this(source, type, rowIndex, comment, null);
    }
    
    public final EditableStatus isEditable() {
        return editable;
    }

    public final int getRowIndex() {
        return rowIndex;
    }

    public final Type getType() {
        return type;
    }
    
    public final String getComment() {
        return comment;
    }
}
