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


public class SectionOccurrenceTest {

    @Test()
	public void testToDTO(){
    	
            SectionOccurrence s = new SectionOccurrence();
            
            Long sId = new Long(2);
            s.setId(sId);
            
            String name = "Name";
            s.setName(name);
                        
            String label = "Label";
            s.setLabel(label);
            
            Section sec = new Section();
            Long secId = new Long(3);
            sec.setId(secId);
            s.setSection(sec);
                        
            EntryStatus status = EntryStatus.DISABLED;
            s.setEntryStatus(status);
            
            boolean multi = true;
            s.setMultipleAllowed(multi);

            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.SectionOccurrenceDTO dtoS = s.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO section occurrence is null", dtoS);
            AssertJUnit.assertEquals("DTO section occurrence has the wrong id",sId,dtoS.getId());
            AssertJUnit.assertEquals("DTO section occurrence has the wrong name",name,dtoS.getName());
            AssertJUnit.assertEquals("DTO section occurrence has the wrong label",label,dtoS.getLabel());
            AssertJUnit.assertEquals("DTO section occurrence has the wrong section",secId,dtoS.getSection().getId());
            AssertJUnit.assertEquals("DTO section occurrence has the wrong status",status.toString(),dtoS.getEntryStatus());
            AssertJUnit.assertEquals("DTO section occurrence has the wrong multiple allowed",multi,dtoS.isMultipleAllowed());
            
    }

}
