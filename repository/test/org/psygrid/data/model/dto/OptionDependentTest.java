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

public class OptionDependentTest {

    @Test()
	public void testToHibernate(){
            OptionDependentDTO od = new OptionDependentDTO();
            EntryDTO entry = new NumericEntryDTO();
            Long entryId = new Long(8);
            entry.setId(entryId);
            od.setMyDependentEntry(entry);
            SectionOccurrenceDTO so = new SectionOccurrenceDTO();
            Long soId = new Long(10);
            so.setId(soId);
            od.setMyDependentSecOcc(so);
            EntryStatus status = EntryStatus.OPTIONAL;
            od.setEntryStatus(status.toString());
            Long id = new Long(9);
            od.setId(id);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.OptionDependent hOD = od.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate option dependent is null", hOD);
            AssertJUnit.assertNotNull("Hibernate option dependent has null dependent entry", hOD.getDependentEntry());
            AssertJUnit.assertEquals("Hibernate option dependent has the wrong dependent entry",entryId,hOD.getDependentEntry().getId());
            AssertJUnit.assertNotNull("Hibernate option dependent has null dependent section occurrence", hOD.getDependentSecOcc());
            AssertJUnit.assertEquals("Hibernate option dependent has the wrong dependent section occurrence",soId,hOD.getDependentSecOcc().getId());
            AssertJUnit.assertEquals("Hibernate option dependent has the wrong status",status,hOD.getEntryStatus());
            AssertJUnit.assertEquals("Hibernate option dependent has the wrong id",id,hOD.getId());
    }
}
