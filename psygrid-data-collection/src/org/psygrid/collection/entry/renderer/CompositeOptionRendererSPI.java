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

public class CompositeOptionRendererSPI implements RendererSPI {

    public boolean canHandle(Entry model, Entry modelParent) {
        if (model instanceof OptionEntry && modelParent != null &&
                modelParent instanceof CompositeEntry) {
            return true;
        }

        return false;
    }

    public Renderer getRenderer(RendererData rendererData) {
        CompositeOptionRendererSPIDelegate delegate = 
            new CompositeOptionRendererSPIDelegate(rendererData);
        return delegate.getRenderer();
    }
}
