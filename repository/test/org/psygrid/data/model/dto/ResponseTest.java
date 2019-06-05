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

import org.psygrid.data.model.hibernate.ResponseStatus;

public class ResponseTest {

    @Test()
	public void testToHibernate(){
            ResponseDTO r = new BasicResponseDTO();
            
            Long eId = new Long(2);
            r.setEntryId(eId);
            
            Long soId = new Long(3);
            r.setSectionOccurrenceId(soId);
            
            RecordDTO rec = new RecordDTO();
            Long recId = new Long(4);
            rec.setId(recId);
            r.setRecord(rec);
            
            SecOccInstanceDTO soi = new SecOccInstanceDTO();
            Long soiId = new Long(5);
            soi.setId(soiId);
            r.setSecOccInstance(soi);
            
            String annot = "Annotation";
            r.setAnnotation(annot);
            
            ResponseStatus status = ResponseStatus.FLAGGED_EDITED;
            r.setStatus(status.toString());
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Response hR = r.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate response is null", hR);
            AssertJUnit.assertEquals("Hibernate response has the wrong entry",eId,hR.getEntryId());
            AssertJUnit.assertEquals("Hibernate response has the wrong section occurrence",soId,hR.getSectionOccurrenceId());
            AssertJUnit.assertNotNull("Hibernate response has null record", hR.getRecord());
            AssertJUnit.assertEquals("Hibernate response has the wrong record",recId,hR.getRecord().getId());
            AssertJUnit.assertNotNull("Hibernate response has null section occurrence instance", hR.getSecOccInstance());
            AssertJUnit.assertEquals("Hibernate response has the wrong section occurrence instance",soiId,hR.getSecOccInstance().getId());
            AssertJUnit.assertEquals("Hibernate response has the wrong annotation", annot, hR.getAnnotation());
            AssertJUnit.assertEquals("Hibernate response has the wrong status", status, hR.getStatus());
    }
}
