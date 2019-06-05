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

import org.psygrid.security.RBACAction;

public class DocumentInstanceTest {

    @Test()
	public void testToDTO(){
            DocumentInstance di = new DocumentInstance();
            Status status = new Status();
            Long statusId = new Long(6);
            status.setId(statusId);
            di.setStatus(status);
            DocumentOccurrence o = new DocumentOccurrence();
            Long oId = new Long(7);
            o.setId(oId);
            di.setOccurrence(o);
            
            Response r1 = new BasicResponse();
            Long r1id = new Long(8);
            r1.setId(r1id);
            di.getResponses().add(r1);
            Response r2 = new BasicResponse();
            Long r2id = new Long(9);
            r2.setId(r2id);
            di.getResponses().add(r2);
            
            SecOccInstance soi1 = new SecOccInstance();
            Long soi1id = new Long(10);
            soi1.setId(soi1id);
            di.getSecOccInstances().add(soi1);
            SecOccInstance soi2 = new SecOccInstance();
            Long soi2id = new Long(11);
            soi2.setId(soi2id);
            di.getSecOccInstances().add(soi2);
            
            RBACAction action = RBACAction.ACTION_DR_DOC_STANDARD;
            di.setAction(action);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.DocumentInstanceDTO dtoDI = di.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO document instance is null", dtoDI);
            AssertJUnit.assertEquals("DTO document instance has the wrong status",statusId,dtoDI.getStatusId());
            AssertJUnit.assertEquals("DTO document instance has the wrong occurrence",oId,dtoDI.getOccurrenceId());
            AssertJUnit.assertEquals("DTO document instance has the wrong number of responses",di.getResponses().size(),dtoDI.getResponses().length);
            for ( org.psygrid.data.model.dto.ResponseDTO dtoR : dtoDI.getResponses() ){
                AssertJUnit.assertNotNull("DTO document instance has null response", dtoR);
                AssertJUnit.assertTrue("DTO document instance has the wrong response", dtoR.getId().equals(r1id) || dtoR.getId().equals(r2id));
            }
            AssertJUnit.assertEquals("DTO document instance has the wrong number of sec occ instances",di.getSecOccInstances().size(),dtoDI.getSecOccInstances().length);
            AssertJUnit.assertEquals("DTO document instance has the wrong sec occ instance at index 0",soi1id,dtoDI.getSecOccInstances()[0].getId());
            AssertJUnit.assertEquals("DTO document instance has the wrong sec occ instance at index 1",soi2id,dtoDI.getSecOccInstances()[1].getId());
            AssertJUnit.assertEquals("DTO document has wrong action",action.toString(), dtoDI.getAction());
    }
}
