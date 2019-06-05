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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StatusedInstanceTest {

    @Test()
	public void testToDTO(){
            StatusedInstance ei = new DocumentInstance();
            
            Record record = new Record();
            Long recordId = new Long(2);
            record.setId(recordId);
            ei.setRecord(record);

            Long id = new Long(3);
            ei.setId(id);
            
            Status status = new Status();
            Long statusId = new Long(6);
            status.setId(statusId);
            ei.setStatus(status);
            
            Date edited = new Date(1234567L);
            ei.setEdited(edited);
            
            ChangeHistory ch1 = new ChangeHistory();
            Long ch1Id = new Long(11);
            ch1.setId(ch1Id);
            ei.getHistory().add(ch1);
            ChangeHistory ch2 = new ChangeHistory();
            Long ch2Id = new Long(12);
            ch2.setId(ch2Id);
            ei.getHistory().add(ch2);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.StatusedInstanceDTO dtoEI = ei.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO statused instance is null", dtoEI);
            AssertJUnit.assertNotNull("DTO statused instance has null record", dtoEI.getRecord());
            AssertJUnit.assertEquals("DTO statused instance has the wrong record",recordId,dtoEI.getRecord().getId());
            AssertJUnit.assertEquals("DTO statused instance has wrong id", id, dtoEI.getId());
            AssertJUnit.assertEquals("DTO statused instance has the wrong status",statusId,dtoEI.getStatusId());
            AssertJUnit.assertEquals("DTO statused instance has the wrong edited",edited,dtoEI.getEdited());
            AssertJUnit.assertEquals("DTO statused instance has the wrong number of history items",ei.getHistory().size(),dtoEI.getHistory().length);
            AssertJUnit.assertNotNull("DTO statused instance has null history item at index 0", dtoEI.getHistory()[0]);
            AssertJUnit.assertEquals("DTO statused instance has the wrong history item at index 0",ch1Id,dtoEI.getHistory()[0].getId());
            AssertJUnit.assertNotNull("DTO statused instance has null history item at index 1", dtoEI.getHistory()[1]);
            AssertJUnit.assertEquals("DTO statused instance has the wrong history item at index 1",ch2Id,dtoEI.getHistory()[1].getId());
    }
}
