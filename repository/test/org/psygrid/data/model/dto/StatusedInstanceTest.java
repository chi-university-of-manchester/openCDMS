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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StatusedInstanceTest {

    @Test()
	public void testToHibernate(){
            StatusedInstanceDTO ei = new DocumentInstanceDTO();
            
            RecordDTO record = new RecordDTO();
            Long recordId = new Long(2);
            record.setId(recordId);
            ei.setRecord(record);

            Long id = new Long(3);
            ei.setId(id);
            
            Long statusId = new Long(6);
            ei.setStatusId(statusId);
            
            Date edited = new Date(1234567L);
            ei.setEdited(edited);
 
            ei.setHistory(new ChangeHistoryDTO[2]);
            ChangeHistoryDTO ch1 = new ChangeHistoryDTO();
            Long ch1Id = new Long(11);
            ch1.setId(ch1Id);
            ei.getHistory()[0] = ch1;
            ChangeHistoryDTO ch2 = new ChangeHistoryDTO();
            Long ch2Id = new Long(6);
            ch2.setId(ch2Id);
            ei.getHistory()[1] = ch2;

            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.StatusedInstance hEI = ei.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate statused instance is null", hEI);
            AssertJUnit.assertNotNull("Hibernate statused instance has null record", hEI.getRecord());
            AssertJUnit.assertEquals("Hibernate statused instance has the wrong record",recordId,hEI.getRecord().getId());
            AssertJUnit.assertEquals("Hibernate statused instance has wrong id", id, hEI.getId());
            AssertJUnit.assertEquals("Hibernate statused instance has the wrong status",statusId,hEI.getStatusId());
            AssertJUnit.assertEquals("Hibernate statused instance has the wrong edited",edited,hEI.getEdited());
            AssertJUnit.assertEquals("Hibernate statused instance has the wrong number of history items",ei.getHistory().length,hEI.getHistory().size());
            AssertJUnit.assertNotNull("Hibernate statused instance has null history item at index 0", hEI.getHistory().get(0));
            AssertJUnit.assertEquals("Hibernate statused instance has the wrong history item at index 0",ch1Id,hEI.getHistory().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate statused instance has null history item at index 1", hEI.getHistory().get(1));
            AssertJUnit.assertEquals("Hibernate statused instance has the wrong history item at index 1",ch2Id,hEI.getHistory().get(1).getId());
    }
}
