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


public class ResponseTest {

    @Test()
	public void testToDTO(){
            Response r = new BasicResponse();
            
            Entry e = new TextEntry();
            Long eId = new Long(2);
            e.setId(eId);
            r.setEntry(e);
            
            SectionOccurrence so = new SectionOccurrence();
            Long soId = new Long(3);
            so.setId(soId);
            r.setSectionOccurrence(so);
            
            Record rec = new Record();
            Long recId = new Long(4);
            rec.setId(recId);
            r.setRecord(rec);
            
            SecOccInstance soi = new SecOccInstance();
            Long soiId = new Long(5);
            soi.setId(soiId);
            r.setSecOccInstance(soi);
            
            String annot = "Annotation";
            r.setAnnotation(annot);
            
            ResponseStatus status = ResponseStatus.FLAGGED_EDITED;
            r.setStatus(status);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.ResponseDTO dtoR = r.toDTO(dtoRefs,RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO response is null", dtoR);
            AssertJUnit.assertEquals("DTO response has the wrong entry",eId,dtoR.getEntryId());
            AssertJUnit.assertEquals("DTO response has the wrong section occurrence",soId,dtoR.getSectionOccurrenceId());
            AssertJUnit.assertNotNull("DTO response has null record", dtoR.getRecord());
            AssertJUnit.assertEquals("DTO response has the wrong record",recId,dtoR.getRecord().getId());
            AssertJUnit.assertNotNull("DTO response has null section occurrence instance", dtoR.getSecOccInstance());
            AssertJUnit.assertEquals("DTO response has the wrong section occurrence instance",soiId,dtoR.getSecOccInstance().getId());
            AssertJUnit.assertEquals("DTO response has the wrong annotation", annot, dtoR.getAnnotation());
            AssertJUnit.assertEquals("DTO response has the wrong status", status.toString(), dtoR.getStatus());
    }
}
