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

import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.OptionEntry;

public class OptionRendererSPI implements RendererSPI {

    public boolean canHandle(Entry model, Entry parent) {
        if (model instanceof OptionEntry) {
            if (parent == null) {
                return true;
            }
            if (parent instanceof CompositeEntry == false) {
                return true;
            }
        }
        return false;
    }
    
    public Renderer getRenderer(RendererData rendererData) {
    	Entry model = rendererData.getModel();
    	if (model instanceof OptionEntry) {
    		if (((OptionEntry)model).isDropDownDisplay()) {
    	    	return new OptionDropDownSPIDelegate(rendererData).getRenderer();
    		}
    	}
        return new OptionRendererSPIDelegate(rendererData).getRenderer();
    }
}
