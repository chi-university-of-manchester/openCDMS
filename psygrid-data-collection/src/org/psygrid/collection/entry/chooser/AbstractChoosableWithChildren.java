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
 * Abstract class to represent any Choosable that can have children.
 * 
 * @author Rob Harper
 *
 */
public abstract class AbstractChoosableWithChildren<T extends Choosable> extends AbstractChoosable {

    protected List<T> children = new ArrayList<T>(); 

	public AbstractChoosableWithChildren(Choosable parent) {
		super(parent);
	}

    public List<T> getChildren() throws ChoosableException {
		return children;
	}
    
    public void setChildren(List<T> children){
    	this.children = children;
    }
    
    public void addChild(T child){
    	children.add(child);
    }
    
}
