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

import org.psygrid.security.RBACAction;

public class DocumentTest {

    @Test()
	public void testToDTO(){
            Document d = new Document();
            
            Long docId = new Long(2);
            d.setId(docId);
            
            d.setIsImportEnabled(true);
            
            DocumentOccurrence occ1 = new DocumentOccurrence();
            Long occ1id = new Long(3);
            occ1.setId(occ1id);
            d.getOccurrences().add(occ1);
            DocumentOccurrence occ2 = new DocumentOccurrence();
            Long occ2id = new Long(4);
            occ2.setId(occ2id);
            d.getOccurrences().add(occ2);

            ConsentFormGroup cfg1 = new ConsentFormGroup();
            Long cfg1Id = new Long(5);
            cfg1.setId(cfg1Id);
            d.getConFrmGrps().add(cfg1);
            ConsentFormGroup cfg2 = new ConsentFormGroup();
            Long cfg2Id = new Long(6);
            cfg2.setId(cfg2Id);
            d.getConFrmGrps().add(cfg2);
            
            Entry child1 = new TextEntry();
            Long child1id = new Long(7);
            child1.setId(child1id);
            d.getEntries().add(child1);
            Entry child2 = new TextEntry();
            Long child2id = new Long(8);
            child2.setId(child2id);
            d.getEntries().add(child2);
                        
            Section sec1 = new Section();
            Long sec1id = new Long(9);
            sec1.setId(sec1id);
            d.getSections().add(sec1);
            Section sec2 = new Section();
            Long sec2id = new Long(10);
            sec2.setId(sec2id);
            d.getSections().add(sec2);
                        
            d.getStatuses().add(new Status());
            
            Long priDocId = new Long(11);
            d.setPrimaryDocIndex(priDocId);
            
            Long secDocId = new Long(12);
            d.setSecondaryDocIndex(secDocId);
            
            RBACAction action = RBACAction.ACTION_DR_DOC_STANDARD;
            d.setAction(action);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.DocumentDTO dtoD = d.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO document is null", dtoD);
            AssertJUnit.assertEquals("DTO document has the wrong id",docId,dtoD.getId());
            AssertJUnit.assertEquals("DTO document has the wrong number of occurrences",d.getOccurrences().size(), dtoD.getOccurrences().length);
            AssertJUnit.assertNotNull("DTO document has null occurrence at index 0", dtoD.getOccurrences()[0]);
            AssertJUnit.assertEquals("DTO document has the wrong occurrence at index 0",occ1id,dtoD.getOccurrences()[0].getId());
            AssertJUnit.assertNotNull("DTO document has null occurrence at index 1", dtoD.getOccurrences()[1]);
            AssertJUnit.assertEquals("DTO document has the wrong occurrence at index 1",occ2id,dtoD.getOccurrences()[1].getId());
            AssertJUnit.assertEquals("DTO document has the wrong number of consent form groups",d.getConFrmGrps().size(),dtoD.getConFrmGrps().length);
            AssertJUnit.assertNotNull("DTO document has null consent form group at index 0", dtoD.getConFrmGrps()[0]);
            AssertJUnit.assertEquals("DTO document has the wrong consent form group at index 0",cfg1Id,dtoD.getConFrmGrps()[0].getId());
            AssertJUnit.assertNotNull("DTO document has null consent form group at index 1", dtoD.getConFrmGrps()[1]);
            AssertJUnit.assertEquals("DTO document has the wrong consent form group at index 1",cfg2Id,dtoD.getConFrmGrps()[1].getId());
            AssertJUnit.assertEquals("DTO document has the wrong number of entries",d.getEntries().size(),dtoD.getEntries().length);
            AssertJUnit.assertNotNull("DTO document has null entry at index 0", dtoD.getEntries()[0]);
            AssertJUnit.assertEquals("DTO document has the wrong entry at index 0",child1id,dtoD.getEntries()[0].getId());
            AssertJUnit.assertNotNull("DTO document has null entry at index 1", dtoD.getEntries()[1]);
            AssertJUnit.assertEquals("DTO document has the wrong entry at index 1",child2id,dtoD.getEntries()[1].getId());
            AssertJUnit.assertEquals("DTO document has the wrong number of sections",d.getSections().size(),dtoD.getSections().length);
            AssertJUnit.assertNotNull("DTO document has null section at index 0", dtoD.getSections()[0]);
            AssertJUnit.assertEquals("DTO document has the wrong section at index 0",sec1id,dtoD.getSections()[0].getId());
            AssertJUnit.assertNotNull("DTO document has null section at index 1", dtoD.getSections()[1]);
            AssertJUnit.assertEquals("DTO document has the wrong section at index 1",sec2id,dtoD.getSections()[1].getId());
            AssertJUnit.assertEquals("DTO document has the wrong number of statuses",1,dtoD.getStatuses().length);
            AssertJUnit.assertEquals("DTO document is import-enabled",true, dtoD.getIsImportEnabled());
            AssertJUnit.assertEquals("DTO document has wrong primary doc index",priDocId, dtoD.getPrimaryDocIndex());
            AssertJUnit.assertEquals("DTO document has wrong secondary doc index",secDocId, dtoD.getSecondaryDocIndex());
            AssertJUnit.assertEquals("DTO document has wrong action",action.toString(), dtoD.getAction());
    }
}
