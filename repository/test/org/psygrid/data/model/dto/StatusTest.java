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

public class StatusTest {

    @Test()
	public void testToHibernate(){
            StatusDTO s = new StatusDTO();
            String name = "Status 1";
            s.setShortName(name);
            String longName = "Long Status 1";
            s.setLongName(longName);
            int code = 4;
            s.setCode(code);
            Long id = new Long(2);
            s.setId(id);
            boolean inactive = true;
            s.setInactive(inactive);
            
            StatusDTO s1 = new StatusDTO();
            Long s1id = new Long(3);
            s1.setId(s1id);
            StatusDTO s2 = new StatusDTO();
            Long s2id = new Long(4);
            s2.setId(s2id);
            
            s.setStatusTransitions(new StatusDTO[2]);
            s.getStatusTransitions()[0] = s1;
            s.getStatusTransitions()[1] = s2;
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Status hS = s.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate status is null", hS);
            AssertJUnit.assertEquals("Hibernate status has the wrong code",code,hS.getCode());
            AssertJUnit.assertEquals("Hibernate status has the wrong short name",name,hS.getShortName());
            AssertJUnit.assertEquals("Hibernate status has the wrong long name",longName,hS.getLongName());
            AssertJUnit.assertEquals("Hibernate status has the wrong id",id,hS.getId());
            AssertJUnit.assertEquals("Hibernate status has the wrong inactive",inactive,hS.isInactive());
            AssertJUnit.assertEquals("Hibernate status has the wrong number of status transitions",s.getStatusTransitions().length,hS.getStatusTransitions().size());
            AssertJUnit.assertNotNull("Hibernate status has null status transition at index 0", hS.getStatusTransitions().get(0));
            AssertJUnit.assertEquals("Hibernate status has the wrong status transition at index 0",s1id,hS.getStatusTransitions().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate status has null status transition at index 1", hS.getStatusTransitions().get(1));
            AssertJUnit.assertEquals("Hibernate status has the wrong status transition at index 1",s2id,hS.getStatusTransitions().get(1).getId());
    }
    
}
