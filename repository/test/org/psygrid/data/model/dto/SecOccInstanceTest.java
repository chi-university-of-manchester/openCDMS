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

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.HashMap;
import java.util.Map;

public class SecOccInstanceTest {

    @Test()
	public void testToHibernate(){
            SecOccInstanceDTO soi = new SecOccInstanceDTO();
            
            Long sId = new Long(2);
            soi.setId(sId);
            
            Long soId = new Long(3);
            soi.setSectionOccurrenceId(soId);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.SecOccInstance hSOI = soi.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate section occurrence instance is null", hSOI);
            AssertJUnit.assertEquals("Hibernate section occurrence instance has the wrong id",sId,hSOI.getId());
            AssertJUnit.assertEquals("Hibernate section occurrence instance has the wrong section occurrence",soId,hSOI.getSectionOccurrenceId());
    }
    
}
