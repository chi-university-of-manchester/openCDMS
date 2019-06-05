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

public class DocumentGroupTest {

    @Test()
	public void testToDTO(){
            DocumentGroup dg = new DocumentGroup();
            
            String name = "Foo bar";
            dg.setName(name);
            
            Long id = new Long(5);
            dg.setId(id);
            
            String label = "Label";
            dg.setLabel(label);
            
            dg.addAllowedRecordStatus(new Status());
            dg.addPrerequisiteGroup(new DocumentGroup());
            dg.addPrerequisiteGroup(new DocumentGroup());
            dg.setUpdateStatus(new Status());
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.DocumentGroupDTO dtoDG = dg.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO doc group is null", dtoDG);
            AssertJUnit.assertEquals("DTO doc group has the wrong name",name,dtoDG.getName());
            AssertJUnit.assertEquals("DTO doc group has the wrong id",id,dtoDG.getId());
            AssertJUnit.assertEquals("DTO doc group has the wrong label",label,dtoDG.getLabel());
            AssertJUnit.assertEquals("DTO doc group has the wrong number of allowed record statuses", 1, dtoDG.getAllowedRecordStatus().length);
            AssertJUnit.assertEquals("DTO doc group has the wrong number of prerequisite groups", 2, dtoDG.getPrerequisiteGroups().length);
            AssertJUnit.assertNotNull("DTO doc group has no update status", dtoDG.getUpdateStatus());
    }
}
