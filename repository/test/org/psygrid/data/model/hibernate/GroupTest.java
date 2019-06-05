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

public class GroupTest {

    @Test()
	public void testToDTO(){
            Group g = new Group();
            Long grpId = new Long(3);
            g.setId(grpId);
            String name = "Group";
            g.setName(name);
            int maxSuffix = 4;
            g.setMaxSuffix(maxSuffix);
            
            Site s1 = new Site();
            Long s1id = new Long(5);
            s1.setId(s1id);
            g.getSites().add(s1);
            Site s2 = new Site();
            Long s2id = new Long(6);
            s2.setId(s2id);
            g.getSites().add(s2);
            
            String secGrp1 = "secgrp1";
            g.getTheSecondaryGroups().add(secGrp1);
            String secGrp2 = "secgrp2";
            g.getTheSecondaryGroups().add(secGrp2);

            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.GroupDTO dtoG = g.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO group is null", dtoG);
            AssertJUnit.assertEquals("DTO group has incorrect id",grpId,dtoG.getId());
            AssertJUnit.assertEquals("DTO group has incorrect name",name,dtoG.getName());
            AssertJUnit.assertEquals("DTO group has incorrect max suffix",maxSuffix,dtoG.getMaxSuffix());
            AssertJUnit.assertEquals("DTO group has incorrect number of sites",2,dtoG.getSites().length);
            AssertJUnit.assertEquals("DTO group has incorrect site at index 0",s1id,dtoG.getSites()[0].getId());
            AssertJUnit.assertEquals("DTO group has incorrect site at index 1",s2id,dtoG.getSites()[1].getId());
            AssertJUnit.assertEquals("DTO group has incorrect number of sec groups",2,dtoG.getSites().length);
            AssertJUnit.assertEquals("DTO group has incorrect sec group at index 0",secGrp1,dtoG.getTheSecondaryGroups()[0]);
            AssertJUnit.assertEquals("DTO group has incorrect sec group at index 1",secGrp2,dtoG.getTheSecondaryGroups()[1]);
    }
}
