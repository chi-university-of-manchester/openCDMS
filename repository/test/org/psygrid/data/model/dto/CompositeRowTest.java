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

public class CompositeRowTest {

    @Test()
	public void testToHibernate(){
            CompositeRowDTO r = new CompositeRowDTO();
            
            r.setBasicResponses(new BasicResponseDTO[2]);
            BasicResponseDTO br1 = new BasicResponseDTO();
            Long br1Id = new Long(3);
            br1.setId(br1Id);
            r.getBasicResponses()[0] = br1;
            BasicResponseDTO br2 = new BasicResponseDTO();
            Long br2Id = new Long(4);
            br2.setId(br2Id);
            r.getBasicResponses()[1] = br2;
            
            RecordDTO rec = new RecordDTO();
            Long recId = new Long(5);
            rec.setId(recId);
            r.setRecord(rec);

            CompositeResponseDTO resp = new CompositeResponseDTO();
            Long respId = new Long(6);
            resp.setId(respId);
            r.setCompositeResponse(resp);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.CompositeRow hR = r.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate composite response is null", hR);
            AssertJUnit.assertEquals("Hibernate composite response has the wrong number of basic responses",r.getBasicResponses().length,hR.getBasicResponses().size());
            for ( org.psygrid.data.model.hibernate.BasicResponse hBR : hR.getBasicResponses() ){
                AssertJUnit.assertNotNull("Hibernate record has null basic response", hBR);
                AssertJUnit.assertTrue("Hibernate record has the wrong basic response", hBR.getId().equals(br1Id) || hBR.getId().equals(br2Id));
            }
            AssertJUnit.assertEquals("Hibernate composite row has the wrong record", recId, hR.getRecord().getId());
            AssertJUnit.assertEquals("Hibernate composite row has the wrong composite response", respId, hR.getCompositeResponse().getId());
    }
    
}
