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

public class CompositeRowTest {

    @Test()
	public void testToDTO(){
            CompositeRow r = new CompositeRow();
            
            BasicResponse br1 = new BasicResponse();
            Long br1Id = new Long(3);
            br1.setId(br1Id);
            r.getBasicResponses().add(br1);
            BasicResponse br2 = new BasicResponse();
            Long br2Id = new Long(4);
            br2.setId(br2Id);
            r.getBasicResponses().add(br2);
            
            Record rec = new Record();
            Long recId = new Long(5);
            rec.setId(recId);
            r.setRecord(rec);

            CompositeResponse resp = new CompositeResponse();
            Long respId = new Long(6);
            resp.setId(respId);
            r.setCompositeResponse(resp);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.CompositeRowDTO dtoR = r.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO composite response is null", dtoR);
            AssertJUnit.assertEquals("DTO composite response has the wrong number of basic responses",r.getBasicResponses().size(),dtoR.getBasicResponses().length);
            for ( org.psygrid.data.model.dto.BasicResponseDTO dtoBR : dtoR.getBasicResponses() ){
                AssertJUnit.assertNotNull("DTO record has null basic response", dtoBR);
                AssertJUnit.assertTrue("DTO record has the wrong basic response", dtoBR.getId().equals(br1Id) || dtoBR.getId().equals(br2Id));
            }
            AssertJUnit.assertEquals("Hibernate composite row has the wrong record", recId, dtoR.getRecord().getId());
            AssertJUnit.assertEquals("Hibernate composite row has the wrong composite response", respId, dtoR.getCompositeResponse().getId());
    }
}
