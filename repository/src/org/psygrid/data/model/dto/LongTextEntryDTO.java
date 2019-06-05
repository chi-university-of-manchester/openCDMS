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
 * Class to represent an entry for collecting long textual data.
 * 
 * @author Rob Harper
 * 
 */
public class LongTextEntryDTO extends BasicEntryDTO {

    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     * 
     * Scope is protected as all long text entrys must have a name.
     */
    public LongTextEntryDTO(){};
    
    public org.psygrid.data.model.hibernate.LongTextEntry toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //long text entry in the map of references
        org.psygrid.data.model.hibernate.LongTextEntry hLTE = null;
        if ( hRefs.containsKey(this)){
            hLTE = (org.psygrid.data.model.hibernate.LongTextEntry)hRefs.get(this);
        }
        if ( null == hLTE ){
            //an instance of the long text entry has not already
            //been created, so create it, and add it to the map 
            //of references
            hLTE = new org.psygrid.data.model.hibernate.LongTextEntry();
            hRefs.put(this, hLTE);
            toHibernate(hLTE, hRefs);
        }
        
        return hLTE;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.LongTextEntry hLTE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hLTE, hRefs);
    }
    
}
