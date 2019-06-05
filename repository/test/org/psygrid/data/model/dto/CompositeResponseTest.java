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

public class CompositeResponseTest {

    @Test()
	public void testToHibernate(){
            CompositeResponseDTO r = new CompositeResponseDTO();
            
            r.setCompositeRows(new CompositeRowDTO[2]);
            CompositeRowDTO br1 = new CompositeRowDTO();
            Long br1Id = new Long(3);
            br1.setId(br1Id);
            r.getCompositeRows()[0] = br1;
            CompositeRowDTO br2 = new CompositeRowDTO();
            Long br2Id = new Long(4);
            br2.setId(br2Id);
            r.getCompositeRows()[1] = br2;
            
            RecordDTO rec = new RecordDTO();
            Long recId = new Long(5);
            rec.setId(recId);
            r.setRecord(rec);

            r.setDeletedRows(new CompositeRowDTO[2]);
            CompositeRowDTO dr1 = new CompositeRowDTO();
            Long dr1Id = new Long(6);
            dr1.setId(dr1Id);
            r.getDeletedRows()[0] = dr1;
            CompositeRowDTO dr2 = new CompositeRowDTO();
            Long dr2Id = new Long(7);
            dr2.setId(dr2Id);
            r.getDeletedRows()[1] = dr2;
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.CompositeResponse hR = r.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate composite response is null", hR);
            AssertJUnit.assertEquals("Hibernate composite response has the wrong number of composite rows",r.getCompositeRows().length,hR.getCompositeRows().size());
            AssertJUnit.assertNotNull("Hibernate composite response has null composite row at index 0", hR.getCompositeRows().get(0));
            AssertJUnit.assertEquals("Hibernate composite response has the wrong composite row at index 0", br1Id, hR.getCompositeRows().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate composite response has null composite row at index 1", hR.getCompositeRows().get(1));
            AssertJUnit.assertEquals("Hibernate composite response has the wrong composite row at index 1", br2Id, hR.getCompositeRows().get(1).getId());
            AssertJUnit.assertEquals("Hibernate composite row has the wrong record", recId, hR.getRecord().getId());
            AssertJUnit.assertEquals("Hibernate composite response has the wrong number of deleted rows",r.getDeletedRows().length,hR.getDeletedRows().size());
            AssertJUnit.assertNotNull("Hibernate composite response has null deleted row at index 0", hR.getDeletedRows().get(0));
            AssertJUnit.assertEquals("Hibernate composite response has the wrong deleted row at index 0", dr1Id, hR.getDeletedRows().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate composite response has null deleted row at index 1", hR.getDeletedRows().get(1));
            AssertJUnit.assertEquals("Hibernate composite response has the wrong deleted row at index 1", dr2Id, hR.getDeletedRows().get(1).getId());
    }
}
