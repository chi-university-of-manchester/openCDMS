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

package org.psygrid.data.model.hibernate;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.HashMap;
import java.util.Map;

public class CompositeResponseTest {

    @Test()
	public void testToDTO(){
            CompositeResponse r = new CompositeResponse();
            
            CompositeRow br1 = new CompositeRow();
            Long br1Id = new Long(3);
            br1.setId(br1Id);
            r.getCompositeRows().add(br1);
            CompositeRow br2 = new CompositeRow();
            Long br2Id = new Long(4);
            br2.setId(br2Id);
            r.getCompositeRows().add(br2);
            
            Record rec = new Record();
            Long recId = new Long(5);
            rec.setId(recId);
            r.setRecord(rec);

            CompositeRow dr1 = new CompositeRow();
            Long dr1Id = new Long(6);
            dr1.setId(dr1Id);
            r.getDeletedRows().add(dr1);
            CompositeRow dr2 = new CompositeRow();
            Long dr2Id = new Long(7);
            dr2.setId(dr2Id);
            r.getDeletedRows().add(dr2);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.CompositeResponseDTO dtoR = r.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO composite response is null", dtoR);
            AssertJUnit.assertEquals("DTO composite response has the wrong number of composite rows",r.getCompositeRows().size(),dtoR.getCompositeRows().length);
            AssertJUnit.assertNotNull("DTO composite response has null compsite row at index 0", dtoR.getCompositeRows()[0]);
            AssertJUnit.assertEquals("DTO composite response has the wrong composite row at index 0", br1Id, dtoR.getCompositeRows()[0].getId());
            AssertJUnit.assertNotNull("DTO composite response has null compsite row at index 1", dtoR.getCompositeRows()[1]);
            AssertJUnit.assertEquals("DTO composite response has the wrong composite row at index 1", br2Id, dtoR.getCompositeRows()[1].getId());
            AssertJUnit.assertEquals("DTO composite response has the wrong number of deleted rows",r.getDeletedRows().size(),dtoR.getDeletedRows().length);
            AssertJUnit.assertNotNull("DTO composite response has null deleted row at index 0", dtoR.getDeletedRows()[0]);
            AssertJUnit.assertEquals("DTO composite response has the wrong deleted row at index 0", dr1Id, dtoR.getDeletedRows()[0].getId());
            AssertJUnit.assertNotNull("DTO composite response has null deleted row at index 1", dtoR.getDeletedRows()[1]);
            AssertJUnit.assertEquals("DTO composite response has the wrong deleted row at index 1", dr2Id, dtoR.getDeletedRows()[1].getId());
            AssertJUnit.assertEquals("DTO composite response has the wrong record", recId, dtoR.getRecord().getId());
    }
}
