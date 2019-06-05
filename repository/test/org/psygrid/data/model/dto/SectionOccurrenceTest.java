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

import org.psygrid.data.model.hibernate.EntryStatus;

public class SectionOccurrenceTest {

    @Test()
	public void testToHibernate(){
            SectionOccurrenceDTO s = new SectionOccurrenceDTO();
            
            Long sId = new Long(2);
            s.setId(sId);
            
            String name = "Name";
            s.setName(name);
            
            String label = "Label";
            s.setLabel(label);
            
            SectionDTO sec = new SectionDTO();
            Long secId = new Long(3);
            sec.setId(secId);
            s.setSection(sec);
                        
            EntryStatus status = EntryStatus.DISABLED;
            s.setEntryStatus(status.toString());
            
            boolean multi = true;
            s.setMultipleAllowed(multi);

            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.SectionOccurrence hS = s.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate section occurrence is null", hS);
            AssertJUnit.assertEquals("Hibernate section occurrence has the wrong id",sId,hS.getId());
            AssertJUnit.assertEquals("Hibernate section occurrence has the wrong name",name,hS.getName());
            AssertJUnit.assertEquals("Hibernate section occurrence has the wrong label",label,hS.getLabel());
            AssertJUnit.assertEquals("Hibernate section occurrence has the wrong section",secId,hS.getSection().getId());
            AssertJUnit.assertEquals("Hibernate section occurrence has the wrong status",status,hS.getEntryStatus());
            AssertJUnit.assertEquals("Hibernate section occurrence has the wrong multiple allowed",multi,hS.isMultipleAllowed());
    }
    
}
