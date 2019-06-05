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

public class CompositeEntryTest {

    @Test()
	public void testToHibernate(){
            CompositeEntryDTO e = new CompositeEntryDTO();
            
            e.setEntries(new BasicEntryDTO[2]);
            BasicEntryDTO e1 = new TextEntryDTO();
            Long e1Id = new Long(3);
            e1.setId(e1Id);
            e.getEntries()[0] = e1;
            BasicEntryDTO e2 = new TextEntryDTO();
            Long e2Id = new Long(4);
            e2.setId(e2Id);
            e.getEntries()[1] = e2;
            
            e.setRowLabels(new String[2]);
            String label1 = "Label 1";
            e.getRowLabels()[0] = label1;
            String label2 = "Label 2";
            e.getRowLabels()[1] = label2;
            
            SectionDTO s = new SectionDTO();
            Long sId = new Long(5);
            s.setId(sId);
            e.setSection(s);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.CompositeEntry hE = e.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate composite entry is null", hE);
            AssertJUnit.assertEquals("Hibernate composite entry has the wrong number of entries",e.getEntries().length,hE.getEntries().size());
            AssertJUnit.assertNotNull("Hibernate composite entry has null entry at index 0", hE.getEntries().get(0));
            AssertJUnit.assertEquals("Hibernate composite entry has the wrong entry at index 0",e1Id,hE.getEntries().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate composite entry has null entry at index 1", hE.getEntries().get(1));
            AssertJUnit.assertEquals("Hibernate composite entry has the wrong entry at index 1",e2Id,hE.getEntries().get(1).getId());
            AssertJUnit.assertEquals("Hibernate composite entry has the wrong number of row labels",e.getRowLabels().length,hE.getRowLabels().size());
            AssertJUnit.assertNotNull("Hibernate composite entry has null row label at index 0", hE.getRowLabels().get(0));
            AssertJUnit.assertEquals("Hibernate composite entry has the wrong row label at index 0",label1,hE.getRowLabels().get(0));
            AssertJUnit.assertNotNull("Hibernate composite entry has null row label at index 1", hE.getRowLabels().get(1));
            AssertJUnit.assertEquals("Hibernate composite entry has the wrong row label at index 1",label2,hE.getRowLabels().get(1));
            AssertJUnit.assertEquals("Hibernate composite entry has the wrong section",sId,hE.getSection().getId());
    }
    
}
