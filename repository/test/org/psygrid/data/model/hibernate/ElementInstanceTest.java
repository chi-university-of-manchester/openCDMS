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

public class ElementInstanceTest {

    @Test()
	public void testToDTO(){
            ElementInstance ei = new DocumentInstance();
            
            Record record = new Record();
            Long recordId = new Long(2);
            record.setId(recordId);
            ei.setRecord(record);

            Long id = new Long(3);
            ei.setId(id);
            
            Provenance prov1 = new Provenance();
            Long prov1id = new Long(4);
            prov1.setId(prov1id);
            Provenance prov2 = new Provenance();
            Long prov2id = new Long(5);
            prov2.setId(prov2id);
            ei.getProvItems().add(prov1);
            ei.getProvItems().add(prov2);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.ElementInstanceDTO dtoEI = ei.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO element instance is null", dtoEI);
            AssertJUnit.assertNotNull("DTO element instance has null record", dtoEI.getRecord());
            AssertJUnit.assertEquals("DTO element instance has the wrong record",recordId,dtoEI.getRecord().getId());
            AssertJUnit.assertEquals("DTO element instance has wrong id", id, dtoEI.getId());
            AssertJUnit.assertEquals("DTO element instance has the wrong number of provenance items",ei.getProvItems().size(),dtoEI.getProvItems().length);
            AssertJUnit.assertNotNull("DTO element instance has null provenance item at index 0", dtoEI.getProvItems()[0]);
            AssertJUnit.assertEquals("DTO element instance has the wrong provenance item at index 0",prov1id,dtoEI.getProvItems()[0].getId());
            AssertJUnit.assertNotNull("DTO element instance has null provenance item at index 1", dtoEI.getProvItems()[1]);
            AssertJUnit.assertEquals("DTO element instance has the wrong provenance item at index 1",prov2id,dtoEI.getProvItems()[1].getId());
    }
}
