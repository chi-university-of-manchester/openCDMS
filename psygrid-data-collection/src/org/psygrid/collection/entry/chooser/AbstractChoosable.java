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

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class containing common functionality for all
 * Choosable implementations.
 * 
 * @author Rob Harper
 *
 */
public abstract class AbstractChoosable implements Choosable {
    
    private final Choosable parent;
    
    public AbstractChoosable(Choosable parent) {
        this.parent = parent;
    }

    public List<? extends Choosable> getChildren() throws ChoosableException {
		return new ArrayList<Choosable>(0);
	}

	public String getDescription() {
		return null;
	}

	public Choosable getParent() {
        return parent;
    }
	
    public boolean isComplete() {
    	return false;
    }

	public boolean isLocked() {
		return false;
	}
}
