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

public class BasicResponseDTO extends ResponseDTO {

    /**
     * Collection of value objects for the response that represent
     * all values of the response over its lifetime.
     */
    protected ValueDTO theValue = null;
    
    protected ValueDTO[] oldValues = new ValueDTO[0];

    public ValueDTO getTheValue() {
        return theValue;
    }

    public void setTheValue(ValueDTO value) {
        this.theValue = value;
    }
    
    public ValueDTO[] getOldValues() {
		return oldValues;
	}

	public void setOldValues(ValueDTO[] oldValues) {
		this.oldValues = oldValues;
	}

    public org.psygrid.data.model.hibernate.BasicResponse toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.BasicResponse hR = new org.psygrid.data.model.hibernate.BasicResponse();
        toHibernate(hR, hRefs);
        return hR;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.BasicResponse hR, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hR, hRefs);
        if ( null != this.theValue ){
            hR.setTheValue(this.theValue.toHibernate(hRefs));
        }       
        List<org.psygrid.data.model.hibernate.Value> hOldValues = hR.getOldValues();
        for ( int i=0; i<this.oldValues.length; i++ ){
            ValueDTO v = this.oldValues[i];
            if ( null != v ){
                hOldValues.add(v.toHibernate(hRefs));
            }
        }
    }
}
