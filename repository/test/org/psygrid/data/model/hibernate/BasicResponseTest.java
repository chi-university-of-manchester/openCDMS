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


public class BasicResponseTest {

    @Test()
	public void testToDTO(){
            BasicResponse r = new BasicResponse();
            
            Value val1 = new TextValue();
            Long val1id = new Long(2);
            val1.setId(val1id);
            r.setTheValue(val1);
            
            Entry e = new TextEntry();
            Long eId = new Long(4);
            e.setId(eId);
            r.setEntry(e);
            
            Value val2 = new TextValue();
            Long val2id = new Long(6);
            val2.setId(val2id);
            r.getOldValues().add(val2);
            Value val3 = new TextValue();
            Long val3id = new Long(7);
            val3.setId(val3id);
            r.getOldValues().add(val3);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.BasicResponseDTO dtoR = r.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO response is null", dtoR);
            AssertJUnit.assertNotNull("DTO response has null value", dtoR.getTheValue());
            AssertJUnit.assertEquals("DTO response has the wrong value",val1id,dtoR.getTheValue().getId());
            AssertJUnit.assertEquals("DTO response has the wrong entry", eId, dtoR.getEntryId());
            AssertJUnit.assertEquals("DTO response has the wrong number of old values",r.getOldValues().size(),dtoR.getOldValues().length);
            AssertJUnit.assertNotNull("DTO response has null old value at index 0", dtoR.getOldValues()[0]);
            AssertJUnit.assertEquals("DTO response has the wrong old value at index 0", val2id, dtoR.getOldValues()[0].getId());
            AssertJUnit.assertNotNull("DTO response has null old value at index 1", dtoR.getOldValues()[1]);
            AssertJUnit.assertEquals("DTO response has the wrong old value at index 1", val3id, dtoR.getOldValues()[1].getId());
    }
}
