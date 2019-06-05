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

package org.psygrid.data.model.dto;

import java.util.Map;


/**
 * Class to represent an entry for collecting textual data.
 * 
 * @author Rob Harper
 * 
 */
public class TextEntryDTO extends BasicEntryDTO {

    /**
     * The maximum size (in number of characters) of the textual data that
     * can be collected by the text entry.
     */
    private int size;

    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     * 
     * Scope is protected as all text entrys must have a name.
     */
    public TextEntryDTO(){};
    
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public org.psygrid.data.model.hibernate.TextEntry toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //text entry in the map of references
        org.psygrid.data.model.hibernate.TextEntry hTE = null;
        if ( hRefs.containsKey(this)){
            hTE = (org.psygrid.data.model.hibernate.TextEntry)hRefs.get(this);
        }
        if ( null == hTE ){
            //an instance of the text entry has not already
            //been created, so create it, and add it to the map 
            //of references
            hTE = new org.psygrid.data.model.hibernate.TextEntry();
            hRefs.put(this, hTE);
            toHibernate(hTE, hRefs);
        }

        return hTE;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.TextEntry hTE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hTE, hRefs);
        hTE.setSize(this.size);
    }
    
}
