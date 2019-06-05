/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.data.query.dto;

import java.util.Map;

import org.psygrid.data.model.dto.OptionDTO;
import org.psygrid.data.model.dto.PersistentDTO;

/**
 * @author Rob Harper
 *
 */
public class OptionStatement extends EntryStatement {

	private OptionDTO value;

	public OptionDTO getValue() {
		return value;
	}

	public void setValue(OptionDTO value) {
		this.value = value;
	}
	
    @Override
    public org.psygrid.data.query.hibernate.OptionStatement toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object for this 
        //statement in the map of references
    	org.psygrid.data.query.hibernate.OptionStatement hDS = null;
        if ( hRefs.containsKey(this)){
            hDS = (org.psygrid.data.query.hibernate.OptionStatement)hRefs.get(this);
        }
        else{
            //an instance of the statement has not already
            //been created, so create it, and add it to the
            //map of references
            hDS = new org.psygrid.data.query.hibernate.OptionStatement();
            hRefs.put(this, hDS);
            toHibernate(hDS, hRefs);
        }
        
        return hDS;
    }

    public void toHibernate(org.psygrid.data.query.hibernate.OptionStatement hDS, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
    	super.toHibernate(hDS, hRefs);
    	if ( null != this.value ){
    		hDS.setValue(this.value.toHibernate(hRefs));
    	}
    }
    
}
