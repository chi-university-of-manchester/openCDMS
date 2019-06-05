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

public class ElementInstanceTest {

    @Test()
	public void testToHibernate(){
            
            ElementInstanceDTO ei = new DocumentInstanceDTO();
            
            RecordDTO record = new RecordDTO();
            Long recordId = new Long(2);
            record.setId(recordId);
            ei.setRecord(record);

            Long id = new Long(3);
            ei.setId(id);
            
            ei.setProvItems(new ProvenanceDTO[2]);
            ProvenanceDTO prov1 = new ProvenanceDTO();
            Long prov1id = new Long(4);
            prov1.setId(prov1id);
            ProvenanceDTO prov2 = new ProvenanceDTO();
            Long prov2id = new Long(5);
            prov2.setId(prov2id);
            ei.getProvItems()[0] = prov1;
            ei.getProvItems()[1] = prov2;
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.ElementInstance hEI = ei.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate element instance is null", hEI);
            AssertJUnit.assertNotNull("Hibernate element instance has null record", hEI.getRecord());
            AssertJUnit.assertEquals("Hibernate element instance has the wrong record",recordId,hEI.getRecord().getId());
            AssertJUnit.assertEquals("Hibernate element instance has wrong id", id, hEI.getId());
            AssertJUnit.assertEquals("Hibernate response has the wrong number of provenance items",ei.getProvItems().length,hEI.getProvItems().size());
            AssertJUnit.assertNotNull("Hibernate response has null provenance item at index 0", hEI.getProvItems().get(0));
            AssertJUnit.assertEquals("Hibernate response has the wrong provenance item at index 0",prov1id,hEI.getProvItems().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate response has null provenance item at index 1", hEI.getProvItems().get(1));
            AssertJUnit.assertEquals("Hibernate response has the wrong provenance item at index 1",prov2id,hEI.getProvItems().get(1).getId());            
    }
}
