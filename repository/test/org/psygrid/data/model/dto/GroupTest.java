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

public class GroupTest {

    @Test()
	public void testToHibernate(){
            
            GroupDTO g = new GroupDTO();
            Long grpId = new Long(3);
            g.setId(grpId);
            String name = "Group";
            g.setName(name);
            int maxSuffix = 4;
            g.setMaxSuffix(maxSuffix);
            
            g.setSites(new SiteDTO[2]);
            SiteDTO s1 = new SiteDTO();
            Long s1id = new Long(5);
            s1.setId(s1id);
            g.getSites()[0] = s1;
            SiteDTO s2 = new SiteDTO();
            Long s2id = new Long(6);
            s2.setId(s2id);
            g.getSites()[1] = s2;
            
            g.setTheSecondaryGroups(new String[2]);
            String secGrp1 = "secgrp1";
            g.getTheSecondaryGroups()[0] = secGrp1;
            String secGrp2 = "secgrp2";
            g.getTheSecondaryGroups()[1] = secGrp2;
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Group hG = g.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate group is null", hG);
            AssertJUnit.assertEquals("Hibernate group has incorrect id",grpId,hG.getId());
            AssertJUnit.assertEquals("Hibernate group has incorrect name",name,hG.getName());
            AssertJUnit.assertEquals("Hibernate group has incorrect max suffix",maxSuffix,hG.getMaxSuffix());
            AssertJUnit.assertEquals("Hibernate group has incorrect number of sites",2,hG.getSites().size());
            AssertJUnit.assertEquals("Hibernate group has incorrect site at index 0",s1id,hG.getSites().get(0).getId());
            AssertJUnit.assertEquals("Hibernate group has incorrect site at index 1",s2id,hG.getSites().get(1).getId());
            AssertJUnit.assertEquals("Hibernate group has incorrect number of sec groups",2,hG.getSites().size());
            AssertJUnit.assertEquals("Hibernate group has incorrect sec group at index 0",secGrp1,hG.getTheSecondaryGroups().get(0));
            AssertJUnit.assertEquals("Hibernate group has incorrect sec group at index 1",secGrp2,hG.getTheSecondaryGroups().get(1));
    }
}
