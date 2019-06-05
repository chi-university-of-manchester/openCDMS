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


public class OptionDependentTest {

    @Test()
	public void testToDTO(){
            OptionDependent od = new OptionDependent();
            Entry entry = new NumericEntry();
            Long entryId = new Long(8);
            entry.setId(entryId);
            od.setMyDependentEntry(entry);
            SectionOccurrence so = new SectionOccurrence();
            Long soId = new Long(10);
            so.setId(soId);
            od.setMyDependentSecOcc(so);
            EntryStatus status = EntryStatus.OPTIONAL;
            od.setEntryStatus(status);
            Long id = new Long(9);
            od.setId(id);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.OptionDependentDTO dtoOD = od.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO option dependent is null", dtoOD);
            AssertJUnit.assertNotNull("DTO option dependent has null dependent entry", dtoOD.getMyDependentEntry());
            AssertJUnit.assertEquals("DTO option dependent has the wrong dependent entry",entryId,dtoOD.getMyDependentEntry().getId());
            AssertJUnit.assertNotNull("DTO option dependent has null dependent section occurrence", dtoOD.getMyDependentSecOcc());
            AssertJUnit.assertEquals("DTO option dependent has the wrong dependent section occurrence",soId,dtoOD.getMyDependentSecOcc().getId());
            AssertJUnit.assertEquals("DTO option dependent has the wrong status",status.toString(),dtoOD.getEntryStatus());
            AssertJUnit.assertEquals("DTO option dependent has the wrong id",id,dtoOD.getId());
    }
}
