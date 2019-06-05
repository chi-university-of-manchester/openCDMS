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

public class ComponentTest {

    @Test()
	public void testToDTO(){
            Component c = new Document();
            
            String desc = "Desc";
            c.setDescription(desc);
            
            String displayText = "Display Text";
            c.setDisplayText(displayText);
            
            Long id = new Long(4);
            c.setId(id);
            
            String name = "Name";
            c.setName(name);
 
            String urn = "lsid:psygrid.org:psygird:OLK.1.1.1";
            c.setMetaDataReference(urn);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.ComponentDTO dtoC = c.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO component is null", dtoC);
            AssertJUnit.assertEquals("DTO component has the wrong description",desc,dtoC.getDescription());
            AssertJUnit.assertEquals("DTO component has the wrong display text",displayText,dtoC.getDisplayText());
            AssertJUnit.assertEquals("DTO component has the wrong id",id,dtoC.getId());
            AssertJUnit.assertEquals("DTO component has the wrong name",name,dtoC.getName());
            AssertJUnit.assertEquals("DTO component has the wrong meta data reference",urn, dtoC.getMetaDataReference());
    }
}
