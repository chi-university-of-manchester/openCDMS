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

public class IdentifierTest {

    @Test()
	public void testToHibernate(){
            IdentifierDTO identifier = new IdentifierDTO();
            Long id = new Long(2);
            identifier.setId(id);
            String idText = "foo";
            identifier.setIdentifier(idText);
            int suffix = 4;
            identifier.setSuffix(suffix);
            String groupPrefix = "Group";
            identifier.setGroupPrefix(groupPrefix);
            String projectPrefix = "Project";
            identifier.setProjectPrefix(projectPrefix);
            String user = "User";
            identifier.setUser(user);
            Date created = new Date();
            identifier.setCreated(created);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Identifier hI = identifier.toHibernate(hRefs);
        
            AssertJUnit.assertNotNull("Hibernate identifier is null", hI);
            AssertJUnit.assertEquals("Hibernate identifier has the wrong id",id,hI.getId());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong identifier",idText,hI.getIdentifier());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong suffix",suffix,hI.getSuffix());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong group prefix",groupPrefix,hI.getGroupPrefix());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong project prefix",projectPrefix,hI.getProjectPrefix());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong user",user,hI.getUser());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong created",created,hI.getCreated());
    }
    
}
