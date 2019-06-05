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

import java.util.List;
import java.util.Map;

public class CompositeEntryDTO extends EntryDTO {

    /**
     * Collection of entries that are contained by the composite
     * entry.
     */
    private BasicEntryDTO[] entries = new BasicEntryDTO[0];
    
    /**
     * List of strings that act as labels for the rows in the 
     * composite entry.
     * <p>
     * If the list has non-zero size then it also acts to set the
     * number of rows in the rendered composite. If the list has 
     * zero size then assume that the composite starts with one row
     * and can grow to hold any number of rows.
     */
    private String[] rowLabels = new String[0];

    public BasicEntryDTO[] getEntries() {
        return entries;
    }

    public void setEntries(BasicEntryDTO[] entries) {
        this.entries = entries;
    }

    public String[] getRowLabels() {
        return rowLabels;
    }

    public void setRowLabels(String[] rowLabels) {
        this.rowLabels = rowLabels;
    }

    public org.psygrid.data.model.hibernate.CompositeEntry toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //composite entry in the map of references
        org.psygrid.data.model.hibernate.CompositeEntry hCE = null;
        if ( hRefs.containsKey(this)){
            hCE = (org.psygrid.data.model.hibernate.CompositeEntry)hRefs.get(this);
        }
        else{
            //an instance of the composite entry has not already
            //been created, so create it, and add it to the map 
            //of references
            hCE = new org.psygrid.data.model.hibernate.CompositeEntry();
            hRefs.put(this, hCE);
            toHibernate(hCE, hRefs);
        }

        return hCE;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.CompositeEntry hCE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hCE, hRefs);
        List<org.psygrid.data.model.hibernate.BasicEntry> hEntries = hCE.getEntries();
        for ( int i=0; i<this.entries.length; i++ ){
            BasicEntryDTO be = this.entries[i];
            if ( null != be ){
                hEntries.add(be.toHibernate(hRefs));
            }
        }
       
        List<String> hRowLabels = hCE.getRowLabels();
        for ( int i=0; i<rowLabels.length; i++ ){
            String label = rowLabels[i];
            if ( null != label ){
                hRowLabels.add(label);
            }
        }
    }
    
}
