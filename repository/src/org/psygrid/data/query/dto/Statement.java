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

import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.query.QueryOperation;

/**
 * @author Rob Harper
 *
 */
public abstract class Statement extends PersistentDTO {

	@Override
    public abstract org.psygrid.data.query.hibernate.Statement toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);

    public void toHibernate(org.psygrid.data.query.hibernate.Statement hDS, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
    	super.toHibernate(hDS, hRefs);
    }

}
