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

import org.psygrid.data.model.dto.LSIDDTO;
import org.psygrid.data.model.dto.LSIDAuthorityDTO;
import org.psygrid.data.model.dto.LSIDNameSpaceDTO;

public class ComponentTest {

    @Test()
	public void testToHibernate(){
            ComponentDTO e = new DocumentDTO();
            
            Long id = new Long(4);
            e.setId(id);
            
            String name = "Name";
            e.setName(name);
            
            String desc = "Desc";
            e.setDescription(desc);
            
            String displayText = "Display Text";
            e.setDisplayText(displayText);
            
            String urn = "URN:LSID:psygrid.org:psygrid:OLK.1.1.1";
            e.setMetaDataReference(urn);
            
            LSIDDTO lsid = new LSIDDTO(new LSIDAuthorityDTO("psygrid.org"), new LSIDNameSpaceDTO("psygrid"), "OLK.1.1.1", null);
            e.setLSID(lsid);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Component hC = e.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate component is null", hC);
            AssertJUnit.assertEquals("Hibernate element has the wrong description",desc,hC.getDescription());
            AssertJUnit.assertEquals("Hibernate element has the wrong display text",displayText,hC.getDisplayText());
            AssertJUnit.assertEquals("Hibernate element has the wrong name",name,hC.getName());
            AssertJUnit.assertEquals("Hibernate element has the wrong id",id,hC.getId());
            AssertJUnit.assertEquals("Hibernate component has the wrong lsid",urn, hC.getLSID().toString());
            AssertJUnit.assertEquals("Hibernate component has the wrong meta data reference",urn, hC.getMetaDataReference());
    }
}
