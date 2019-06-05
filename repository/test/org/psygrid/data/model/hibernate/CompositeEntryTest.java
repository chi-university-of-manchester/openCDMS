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

public class CompositeEntryTest {

    @Test()
	public void testToDTO(){
            CompositeEntry e = new CompositeEntry();
            
            BasicEntry e1 = new TextEntry();
            Long e1Id = new Long(3);
            e1.setId(e1Id);
            e.getEntries().add(e1);
            BasicEntry e2 = new TextEntry();
            Long e2Id = new Long(4);
            e2.setId(e2Id);
            e.getEntries().add(e2);
            
            String label1 = "Label 1";
            e.getRowLabels().add(label1);
            String label2 = "Label 2";
            e.getRowLabels().add(label2);
            
            Section s = new Section();
            Long sId = new Long(5);
            s.setId(sId);
            e.setSection(s);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.CompositeEntryDTO dtoE = e.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO composite entry is null", dtoE);
            AssertJUnit.assertEquals("DTO composite entry has the wrong number of entries",e.getEntries().size(),dtoE.getEntries().length);
            AssertJUnit.assertNotNull("DTO composite entry has null entry at index 0", dtoE.getEntries()[0]);
            AssertJUnit.assertEquals("DTO composite entry has the wrong entry at index 0",e1Id,dtoE.getEntries()[0].getId());
            AssertJUnit.assertNotNull("DTO composite entry has null entry at index 1", dtoE.getEntries()[1]);
            AssertJUnit.assertEquals("DTO composite entry has the wrong entry at index 1",e2Id,dtoE.getEntries()[1].getId());
            AssertJUnit.assertEquals("DTO composite entry has the wrong number of row labels",e.getRowLabels().size(),dtoE.getRowLabels().length);
            AssertJUnit.assertNotNull("DTO composite entry has null row label at index 0", dtoE.getRowLabels()[0]);
            AssertJUnit.assertEquals("DTO composite entry has the wrong row label at index 0",label1,dtoE.getRowLabels()[0]);
            AssertJUnit.assertNotNull("DTO composite entry has null row label at index 1", dtoE.getRowLabels()[1]);
            AssertJUnit.assertEquals("DTO composite entry has the wrong row label at index 1",label2,dtoE.getRowLabels()[1]);
            AssertJUnit.assertEquals("DTO composite entry has the wrong section",sId,dtoE.getSection().getId());
    }
    
}
