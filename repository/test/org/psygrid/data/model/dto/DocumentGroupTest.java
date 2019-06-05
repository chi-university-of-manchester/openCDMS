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

public class DocumentGroupTest {

    @Test()
	public void testToHibernate(){
            DocumentGroupDTO dg = new DocumentGroupDTO();
            
            String name = "Foo bar";
            dg.setName(name);

            Long id = new Long(5);
            dg.setId(id);
            
            String label = "Label";
            dg.setLabel(label);
            
            StatusDTO[] allowedRecordStatus = new StatusDTO[1];
            allowedRecordStatus[0] = new StatusDTO();
            dg.setAllowedRecordStatus(allowedRecordStatus);
            
            DocumentGroupDTO[] prerequisiteGroups = new DocumentGroupDTO[2];
            prerequisiteGroups[0] = new DocumentGroupDTO(); 
            prerequisiteGroups[1] = new DocumentGroupDTO(); 
            dg.setPrerequisiteGroups(prerequisiteGroups);
            dg.setUpdateStatus(new StatusDTO());
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.DocumentGroup hDG = dg.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate doc group is null", hDG);
            AssertJUnit.assertEquals("Hibernate doc group has the wrong name",name,hDG.getName());
            AssertJUnit.assertEquals("Hibernate doc group has the wrong id",id,hDG.getId());
            AssertJUnit.assertEquals("Hibernate doc group has the wrong label",label,hDG.getLabel());
            AssertJUnit.assertEquals("DTO doc group has the wrong number of allowed record statuses", 1, hDG.getAllowedRecordStatus().size());
            AssertJUnit.assertEquals("DTO doc group has the wrong number of prerequisite groups", 2, hDG.getPrerequisiteGroups().size());
            AssertJUnit.assertNotNull("DTO doc group has no update status", hDG.getUpdateStatus());            
    }
}
