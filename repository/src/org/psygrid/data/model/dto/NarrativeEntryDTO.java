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

import org.psygrid.data.model.hibernate.NarrativeStyle;

public class NarrativeEntryDTO extends EntryDTO {

    private String style;
    
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public org.psygrid.data.model.hibernate.NarrativeEntry toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object for this 
        //long text entry in the map of references
        org.psygrid.data.model.hibernate.NarrativeEntry hNE = null;
        if ( hRefs.containsKey(this)){
            hNE = (org.psygrid.data.model.hibernate.NarrativeEntry)hRefs.get(this);
        }
        if ( null == hNE ){
            //an instance of the long text entry has not already
            //been created, so create it, and add it to the map 
            //of references
            hNE = new org.psygrid.data.model.hibernate.NarrativeEntry();
            hRefs.put(this, hNE);
            toHibernate(hNE, hRefs);
        }
        
        return hNE;
    }

    public void toHibernate(org.psygrid.data.model.hibernate.NarrativeEntry hNE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hNE, hRefs);
        if ( null != this.style ){
            hNE.setStyle(NarrativeStyle.valueOf(this.style));
        }
    }
}
