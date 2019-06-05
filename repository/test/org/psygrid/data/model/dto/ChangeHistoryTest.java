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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rob Harper
 *
 */
public class ChangeHistoryTest {

    @Test()
	public void testToHibernate(){
            ChangeHistoryDTO ch = new ChangeHistoryDTO();
            
            Long chId = new Long(2);
            ch.setId(chId);
            
            String user = "User";
            ch.setUser(user);
            
            String action = "Action";
            ch.setAction(action);
            
            Date when = new Date();
            ch.setWhen(when);

            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.ChangeHistory hCH = ch.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate change history is null", hCH);
            AssertJUnit.assertEquals("Hibernate change history has the wrong id",chId,hCH.getId());
            AssertJUnit.assertEquals("Hibernate change history has the wrong user",user,hCH.getUser());
            AssertJUnit.assertEquals("Hibernate change history has the wrong action",action,hCH.getAction());
            AssertJUnit.assertEquals("Hibernate change history has the wrong when",when,hCH.getWhen());
    }
}
