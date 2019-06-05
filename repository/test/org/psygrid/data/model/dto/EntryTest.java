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

public class EntryTest {

    @Test()
	public void testToHibernate(){
            EntryDTO e = new TextEntryDTO();
            EntryStatus status = EntryStatus.DISABLED;
            e.setEntryStatus(status.toString());
            
            Long id = new Long(6);
            e.setId(id);
            
            SectionDTO sec = new SectionDTO();
            Long secId = new Long(7);
            sec.setId(secId);
            e.setSection(sec);
            
            String label = "Label";
            e.setLabel(label);
            
            DataSetDTO ds = new DataSetDTO();
            Long dsId = new Long(3);
            ds.setId(dsId);
            e.setMyDataSet(ds);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Entry hE = e.toHibernate(hRefs);
        
            AssertJUnit.assertNotNull("Hibernate entry is null", hE);
            AssertJUnit.assertEquals("Hibernate entry has the wrong status",status,hE.getEntryStatus());
            AssertJUnit.assertEquals("Hibernate entry has the wrong id",id,hE.getId());
            AssertJUnit.assertEquals("Hibernate entry has the wrong section",secId,hE.getSection().getId());
            AssertJUnit.assertEquals("Hibernate entry has the wrong label",label,hE.getLabel());
            AssertJUnit.assertNotNull("Hibernate element has null dataset", hE.getDataSet());
            AssertJUnit.assertEquals("Hibernate element has the wrong dataset",dsId,hE.getDataSet().getId());
    }
}
