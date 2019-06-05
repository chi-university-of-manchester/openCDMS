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
 * Class to represent an entry for collecting date and time data.
 * 
 * @author Rob Harper
 */
public class DateEntryDTO extends BasicEntryDTO {
    
    /**
     * The format string of the date entry.
     * <p>
     * The format string should be compatible with the Java API
     * class java.text.SimpleDateFormat.
     */
    private String format;
    
    private boolean disablePartialDate;

    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     */
    public DateEntryDTO(){}
    
    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    
    public boolean isDisablePartialDate() {
		return disablePartialDate;
	}

	public void setDisablePartialDate(boolean disablePartialDate) {
		this.disablePartialDate = disablePartialDate;
	}

	public org.psygrid.data.model.hibernate.DateEntry toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //numeric entry in the map of references
        org.psygrid.data.model.hibernate.DateEntry hDE = null;
        if ( hRefs.containsKey(this)){
            hDE = (org.psygrid.data.model.hibernate.DateEntry)hRefs.get(this);
        }
        if ( null == hDE ){
            //an instance of the date entry has not already
            //been created, so create it, and add it to the map of 
            //references
            hDE = new org.psygrid.data.model.hibernate.DateEntry();
            hRefs.put(this, hDE);
            toHibernate(hDE, hRefs);
        }
        return hDE;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.DateEntry hDE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hDE, hRefs);
        hDE.setFormat(this.format);
        hDE.setDisablePartialDate(this.disablePartialDate);
    }
    
    
}
