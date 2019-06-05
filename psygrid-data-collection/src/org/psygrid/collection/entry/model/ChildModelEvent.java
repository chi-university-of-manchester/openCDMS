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


package org.psygrid.collection.entry.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

public final class ChildModelEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public enum Type { ADD, REMOVE }
    
    private final Type type;
    private final List<BasicPresModel> childModels;
    private final int rowIndex;
    
    public ChildModelEvent(CompositePresModel source, Type eventType, 
            List<BasicPresModel> childModels, int rowIndex) {
        super(source);
        this.type = eventType;
        this.childModels = new ArrayList<BasicPresModel>(childModels);
        this.rowIndex = rowIndex;
    }

    public final Type getType() {
        return type;
    }
    
    @Override
    public final CompositePresModel getSource() {
        return (CompositePresModel) source;
    }
    
    public final List<BasicPresModel> getChildModels()  {
        return Collections.unmodifiableList(childModels);
    }
    
    public final int getRowIndex() {
        return rowIndex;
    }

}
