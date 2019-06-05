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

import org.omg.PortableInterceptor.INACTIVE;

public class StatusTest {

    @Test()
	public void testToDTO(){
            Status s = new Status();
            int code = 4;
            s.setCode(code);
            String name = "Status 1";
            s.setShortName(name);
            String longName = "Long Status 1";
            s.setLongName(longName);
            Long id = new Long(23);
            s.setId(id);
            boolean inactive = true;
            s.setInactive(inactive);

            Status s1 = new Status();
            Long s1id = new Long(3);
            s1.setId(s1id);
            Status s2 = new Status();
            Long s2id = new Long(4);
            s2.setId(s2id);
            
            s.getStatusTransitions().add(s1);
            s.getStatusTransitions().add(s2);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.StatusDTO dtoS = s.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO status is null", dtoS);
            AssertJUnit.assertEquals("DTO status has the wrong code",code,dtoS.getCode());
            AssertJUnit.assertEquals("DTO status has the wrong short name",name,dtoS.getShortName());
            AssertJUnit.assertEquals("DTO status has the wrong long name",longName,dtoS.getLongName());
            AssertJUnit.assertEquals("DTO status has the wrong id",id,dtoS.getId());
            AssertJUnit.assertEquals("DTO status has the wrong inactive",inactive,dtoS.isInactive());
            AssertJUnit.assertEquals("DTO status has the wrong number of status transitions",s.getStatusTransitions().size(),dtoS.getStatusTransitions().length);
            AssertJUnit.assertNotNull("DTO status has null status transition at index 0", dtoS.getStatusTransitions()[0]);
            AssertJUnit.assertEquals("DTO status has the wrong status transition at index 0",s1id,dtoS.getStatusTransitions()[0].getId());
            AssertJUnit.assertNotNull("DTO status has null status transition at index 1", dtoS.getStatusTransitions()[1]);
            AssertJUnit.assertEquals("DTO status has the wrong status transition at index 1",s2id,dtoS.getStatusTransitions()[1].getId());
    }
    
}
