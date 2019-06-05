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

import org.psygrid.security.RBACAction;

public class DocumentInstanceTest {

    @Test()
	public void testToHibernate(){
            DocumentInstanceDTO di = new DocumentInstanceDTO();
            Long statusId = new Long(6);
            di.setStatusId(statusId);
            Long oId = new Long(7);
            di.setOccurrenceId(oId);
            
            di.setResponses(new ResponseDTO[2]);
            ResponseDTO r1 = new BasicResponseDTO();
            Long r1id = new Long(8);
            r1.setId(r1id);
            di.getResponses()[0] = r1;
            ResponseDTO r2 = new BasicResponseDTO();
            Long r2id = new Long(9);
            r2.setId(r2id);
            di.getResponses()[1] = r2;
            
            di.setSecOccInstances(new SecOccInstanceDTO[2]);
            SecOccInstanceDTO soi1 = new SecOccInstanceDTO();
            Long soi1id = new Long(10);
            soi1.setId(soi1id);
            di.getSecOccInstances()[0] = soi1;
            SecOccInstanceDTO soi2 = new SecOccInstanceDTO();
            Long soi2id = new Long(11);
            soi2.setId(soi2id);
            di.getSecOccInstances()[1] = soi2;
            
            String action = RBACAction.ACTION_DR_DOC_STANDARD.toString();
            di.setAction(action);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.DocumentInstance hDI = di.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate document instance is null", hDI);
            AssertJUnit.assertEquals("Hibernate document instance has the wrong status",statusId,hDI.getStatusId());
            AssertJUnit.assertEquals("Hibernate document instance has the wrong occurrence",oId,hDI.getOccurrenceId());
            AssertJUnit.assertEquals("Hibernate document instance has the wrong number of responses",di.getResponses().length,hDI.getResponses().size());
            for ( org.psygrid.data.model.hibernate.Response hR : hDI.getResponses() ){
                AssertJUnit.assertNotNull("Hibernate document instance has null response", hR);
                AssertJUnit.assertTrue("Hibernate document instance has the wrong response", hR.getId().equals(r1id) || hR.getId().equals(r2id));
            }
            AssertJUnit.assertEquals("Hibernate document instance has the wrong number of sec occ instance",di.getSecOccInstances().length,hDI.getSecOccInstances().size());
            AssertJUnit.assertEquals("Hibernate document instance has the wrong sec occ instance at index 0",soi1id,hDI.getSecOccInstances().get(0).getId());
            AssertJUnit.assertEquals("Hibernate document instance has the wrong sec occ instance at index 1",soi2id,hDI.getSecOccInstances().get(1).getId());
            AssertJUnit.assertEquals("Hibernate document has the wrong action",action,hDI.getAction());
    }
}
