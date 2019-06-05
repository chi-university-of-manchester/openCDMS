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


public class EntryTest {

    @Test()
	public void testToDTO(){
            Entry e = new TextEntry();
            EntryStatus status = EntryStatus.DISABLED;
            e.setEntryStatus(status);

            Long id = new Long(8);
            e.setId(id);
            
            Section sec = new Section();
            Long secId = new Long(7);
            sec.setId(secId);
            e.setSection(sec);
            
            String label = "Label";
            e.setLabel(label);
            
            DataSet ds = new DataSet();
            Long dsId = new Long(3);
            ds.setId(dsId);
            e.setMyDataSet(ds);
                        
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.EntryDTO dtoE = e.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
        
            AssertJUnit.assertNotNull("DTO entry is null", dtoE);
            AssertJUnit.assertEquals("DTO entry has the wrong status",status.toString(),dtoE.getEntryStatus());
            AssertJUnit.assertEquals("DTO entry has the wrong id",id,dtoE.getId());
            AssertJUnit.assertEquals("DTO entry has the wrong section",secId,dtoE.getSection().getId());
            AssertJUnit.assertEquals("DTO entry has the wrong label",label,dtoE.getLabel());
            AssertJUnit.assertNotNull("DTO element has null dataset", dtoE.getMyDataSet());
            AssertJUnit.assertEquals("DTO element has the wrong dataset",dsId,dtoE.getMyDataSet().getId());
    }
}
