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

import org.psygrid.security.RBACAction;

public class DocumentTest {

    @Test()
	public void testToHibernate(){
            
            DocumentDTO d = new DocumentDTO();
            
            Long docId = new Long(2);
            d.setId(docId);
            
            d.setOccurrences(new DocumentOccurrenceDTO[2]);
            DocumentOccurrenceDTO occ1 = new DocumentOccurrenceDTO();
            Long occ1id = new Long(3);
            occ1.setId(occ1id);
            d.getOccurrences()[0] = occ1;
            DocumentOccurrenceDTO occ2 = new DocumentOccurrenceDTO();
            Long occ2id = new Long(4);
            occ2.setId(occ2id);
            d.getOccurrences()[1] = occ2;
            
            d.setConFrmGrps(new ConsentFormGroupDTO[2]);
            ConsentFormGroupDTO cfg1 = new ConsentFormGroupDTO();
            Long cfg1Id = new Long(5);
            cfg1.setId(cfg1Id);
            d.getConFrmGrps()[0] = cfg1;
            ConsentFormGroupDTO cfg2 = new ConsentFormGroupDTO();
            Long cfg2Id = new Long(6);
            cfg2.setId(cfg2Id);
            d.getConFrmGrps()[1] = cfg2;
            
            d.setEntries(new EntryDTO[2]);
            EntryDTO child1 = new TextEntryDTO();
            Long child1id = new Long(7);
            child1.setId(child1id);
            d.getEntries()[0] = child1;
            EntryDTO child2 = new TextEntryDTO();
            Long child2id = new Long(8);
            child2.setId(child2id);
            d.getEntries()[1] = child2;
                        
            d.setSections(new SectionDTO[2]);
            SectionDTO sec1 = new SectionDTO();
            Long sec1id = new Long(9);
            sec1.setId(sec1id);
            d.getSections()[0] = sec1;
            SectionDTO sec2 = new SectionDTO();
            Long sec2id = new Long(10);
            sec2.setId(sec2id);
            d.getSections()[1] = sec2;
                        
            d.setStatuses(new StatusDTO[1]);
            d.getStatuses()[0] = new StatusDTO();
            
            Long priDocId = new Long(11);
            d.setPrimaryDocIndex(priDocId);
            
            Long secDocId = new Long(12);
            d.setSecondaryDocIndex(secDocId);
            
            String action = RBACAction.ACTION_DR_DOC_STANDARD.toString();
            d.setAction(action);
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.Document hD = d.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate document is null", hD);
            AssertJUnit.assertEquals("Hibernate document has the wrong id",docId,hD.getId());
            AssertJUnit.assertEquals("Hibernate document has the wrong number of occurrences",d.getOccurrences().length, hD.getOccurrences().size());
            AssertJUnit.assertNotNull("Hibernate document has null occurrence at index 0", hD.getOccurrences().get(0));
            AssertJUnit.assertEquals("Hibernate document has the wrong occurrence at index 0",occ1id,hD.getOccurrences().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate document has null occurrence at index 1", hD.getOccurrences().get(1));
            AssertJUnit.assertEquals("Hibernate document has the wrong occurrence at index 1",occ2id,hD.getOccurrences().get(1).getId());
            AssertJUnit.assertEquals("Hibernate document has the wrong number of consent form groups",d.getConFrmGrps().length,hD.getConFrmGrps().size());
            AssertJUnit.assertNotNull("Hibernate document has null consent form group at index 0", hD.getConFrmGrps().get(0));
            AssertJUnit.assertEquals("Hibernate document has the wrong consent form group at index 0",cfg1Id,hD.getConFrmGrps().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate document has null consent form group at index 1", hD.getEntries().get(1));
            AssertJUnit.assertEquals("Hibernate document has the wrong consent form group at index 1",cfg2Id,hD.getConFrmGrps().get(1).getId());
            AssertJUnit.assertEquals("Hibernate document has the wrong number of entries",d.getEntries().length,hD.getEntries().size());
            AssertJUnit.assertNotNull("Hibernate document has null entry at index 0", hD.getEntries().get(0));
            AssertJUnit.assertEquals("Hibernate document has the wrong entry at index 0",child1id,hD.getEntries().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate document has null entry at index 1", hD.getEntries().get(1));
            AssertJUnit.assertEquals("Hibernate document has the wrong entry at index 1",child2id,hD.getEntries().get(1).getId());
            AssertJUnit.assertEquals("Hibernate document has the wrong number of sections",d.getSections().length,hD.getSections().size());
            AssertJUnit.assertNotNull("Hibernate document has null section at index 0", hD.getSections().get(0));
            AssertJUnit.assertEquals("Hibernate document has the wrong section at index 0",sec1id,hD.getSections().get(0).getId());
            AssertJUnit.assertNotNull("Hibernate document has null section at index 1", hD.getSections().get(1));
            AssertJUnit.assertEquals("Hibernate document has the wrong section at index 1",sec2id,hD.getSections().get(1).getId());
            AssertJUnit.assertEquals("Hibernate document has the wrong number of statuses",1,hD.getStatuses().size());
            AssertJUnit.assertEquals("Hibernate document has the wrong primary doc index",priDocId,hD.getPrimaryDocIndex());
            AssertJUnit.assertEquals("Hibernate document has the wrong secondary doc index",secDocId,hD.getSecondaryDocIndex());
            AssertJUnit.assertEquals("Hibernate document has the wrong action",action,hD.getAction());
    }
}
