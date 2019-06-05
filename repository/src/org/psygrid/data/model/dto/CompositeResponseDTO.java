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

public class CompositeResponseDTO extends ResponseDTO {

    private CompositeRowDTO[] compositeRows = new CompositeRowDTO[0];

    private CompositeRowDTO[] deletedRows = new CompositeRowDTO[0];

    public CompositeRowDTO[] getCompositeRows() {
        return compositeRows;
    }

    public void setCompositeRows(CompositeRowDTO[] compositeRows) {
        this.compositeRows = compositeRows;
    }

    public CompositeRowDTO[] getDeletedRows() {
		return deletedRows;
	}

	public void setDeletedRows(CompositeRowDTO[] deletedRows) {
		this.deletedRows = deletedRows;
	}

	public org.psygrid.data.model.hibernate.CompositeResponse toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.CompositeResponse hR = null;
        if ( hRefs.containsKey(this) ){
            hR = (org.psygrid.data.model.hibernate.CompositeResponse)hRefs.get(this);
        }
        else{
            hR = new org.psygrid.data.model.hibernate.CompositeResponse();
            hRefs.put(this, hR);
            toHibernate(hR, hRefs);
        }
        return hR;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.CompositeResponse hR, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hR, hRefs);
        List<org.psygrid.data.model.hibernate.CompositeRow> hRows = hR.getCompositeRows();
        for ( int i=0; i<this.compositeRows.length; i++ ){
            CompositeRowDTO row = this.compositeRows[i];
            if ( null != row ){
                hRows.add(row.toHibernate(hRefs));
            }
        }
       
        List<org.psygrid.data.model.hibernate.CompositeRow> hDelRows = hR.getDeletedRows();
        for ( int i=0; i<this.deletedRows.length; i++ ){
            CompositeRowDTO row = this.deletedRows[i];
            if ( null != row ){
                hDelRows.add(row.toHibernate(hRefs));
            }
        }
    }
    
}
