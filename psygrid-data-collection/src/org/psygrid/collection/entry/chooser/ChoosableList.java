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


package org.psygrid.collection.entry.chooser;

import java.util.List;

/**
 * A Choosable that is simply a container for a list of children.
 * Used as the top-level of a hierarchy of Choosables a ChoosableList
 * cannot itself have a parent.
 * 
 * @author Rob Harper
 *
 */
public class ChoosableList extends AbstractChoosableWithChildren<Choosable> {

    public ChoosableList(List<Choosable> children) {
    	super(null);
        this.children = children;
    }
    
    public String getDisplayText() {
        return null;
    }

    public ChoosableType getType() {
        return null;
    }

}
