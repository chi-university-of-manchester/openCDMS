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

public class BasicResponseTest {

    @Test()
	public void testToHibernate(){
            BasicResponseDTO r = new BasicResponseDTO();
            
            ValueDTO val1 = new TextValueDTO();
            Long val1id = new Long(2);
            val1.setId(val1id);
            r.setTheValue(val1);
            
            Long eId = new Long(4);
            r.setEntryId(eId);
            
            r.setOldValues(new ValueDTO[2]);
            ValueDTO ov1 = new TextValueDTO();
            Long ov1Id = new Long(6);
            ov1.setId(ov1Id);
            r.getOldValues()[0] = ov1;
            ValueDTO ov2 = new TextValueDTO();
            Long ov2Id = new Long(7);
            ov2.setId(ov2Id);
            r.getOldValues()[1] = ov2;
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.BasicResponse hR = r.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate response is null", hR);
            AssertJUnit.assertNotNull("Hibernate response has null value", hR.getTheValue());
            AssertJUnit.assertEquals("Hibernate response has the wrong value",val1id,hR.getTheValue().getId());
            AssertJUnit.assertEquals("Hibernate response has the wrong entry", eId, hR.getEntryId());
            AssertJUnit.assertEquals("Hibernate response has the wrong number of old values",r.getOldValues().length,hR.getOldValues().size());
            AssertJUnit.assertNotNull("Hibernate response has null old value at index 0", hR.getOldValues().get(0));
            AssertJUnit.assertEquals("Hibernate response has the wrong old value at index 0", ov1Id, hR.getOldValues().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate response has null old value at index 1", hR.getOldValues().get(1));
            AssertJUnit.assertEquals("Hibernate response has the wrong old value at index 1", ov2Id, hR.getOldValues().get(1).getId());
    }
}
