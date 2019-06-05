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

import org.psygrid.data.model.hibernate.DocumentInstance;

/**
 * Choosable implementation to represent a Document Instance.
 * <p>
 * Is always at the bottom level of a Choosable hierarchy, so
 * cannot have children.
 * 
 * @author Rob Harper
 *
 */
public class ChoosableDocInstance extends AbstractChoosable    {

    private final DocumentInstance docInstance;
    
    public ChoosableDocInstance(DocumentInstance docInstance,
            ChoosableDocInstanceGroup parent) {
        super(parent);
        this.docInstance = docInstance;
        parent.addChild(this);
    }
    
    public final  DocumentInstance  getDocInstance() {
        return docInstance;
    }

    public ChoosableType getType() {
        return ChoosableType.DOCUMENT_INSTANCE;
    }

    public String getDisplayText() {
        return docInstance.getOccurrence().getCombinedDisplayText();
    }

    public String getDescription() {
        return docInstance.getOccurrence().getDescription();
    }

}
