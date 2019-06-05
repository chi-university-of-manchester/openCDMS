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


public class DocumentOccurrenceTest {

    @Test()
	public void testToDTO(){
        
        DocumentOccurrence o = new DocumentOccurrence();
        
        String eName = "Doc";
        Document d = new Document();
        d.setName(eName);
        o.setDocument(d);
        
        String name = "Foo";
        o.setName(name);
        
        String gName = "Group";
        DocumentGroup group = new DocumentGroup();
        group.setName(gName);
        o.setDocumentGroup(group);
        
        Integer time = new Integer(103);
        o.setScheduleTime(time);
        
        TimeUnits units = TimeUnits.DAYS;
        o.setScheduleUnits(units);

        Reminder r1 = new Reminder();
        Long r1id = new Long(10);
        r1.setId(r1id);
        o.getReminders().add(r1);
        Reminder r2 = new Reminder();
        Long r2id = new Long(11);
        r2.setId(r2id);
        o.getReminders().add(r2);

        String label = "Label";
        o.setLabel(label);
        
        boolean rndTrigger = true;
        o.setRandomizationTrigger(rndTrigger);
        
        boolean locked = true;
        o.setLocked(true);
        
        Long priOccIndex = new Long(12);
        o.setPrimaryOccIndex(priOccIndex);

        Long secOccIndex = new Long(13);
        o.setSecondaryOccIndex(secOccIndex);

        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        org.psygrid.data.model.dto.DocumentOccurrenceDTO dtoO = o.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
        
        AssertJUnit.assertNotNull("DTO occurrence is null", dtoO);
        AssertJUnit.assertEquals("DTO occurrence has the wrong name",name,dtoO.getName());
        AssertJUnit.assertEquals("DTO occurrence has the wrong document group",gName,dtoO.getDocumentGroup().getName());
        AssertJUnit.assertEquals("DTO occurrence has the wrong time",time,dtoO.getScheduleTime());
        AssertJUnit.assertEquals("DTO occurrence has the wrong units",units.toString(),dtoO.getScheduleUnits());
        AssertJUnit.assertEquals("DTO occurrence has the wrong number of reminders",o.getReminders().size(),dtoO.getReminders().length);
        AssertJUnit.assertNotNull("DTO occurrence has null reminder at index 0", dtoO.getReminders()[0]);
        AssertJUnit.assertEquals("DTO occurrence has the wrong reminder at index 0",r1id,dtoO.getReminders()[0].getId());
        AssertJUnit.assertNotNull("DTO occurrence has null reminder at index 1", dtoO.getReminders()[1]);
        AssertJUnit.assertEquals("DTO occurrence has the wrong reminder at index 1",r2id,dtoO.getReminders()[1].getId());
        AssertJUnit.assertEquals("DTO entry has the wrong label",label,dtoO.getLabel());
        AssertJUnit.assertEquals("DTO entry has the wrong randomization trigger",rndTrigger,dtoO.isRandomizationTrigger());
        AssertJUnit.assertEquals("DTO entry has the wrong locked",locked,dtoO.isLocked());
        AssertJUnit.assertEquals("DTO entry has the wrong primary occurrence index",priOccIndex,dtoO.getPrimaryOccIndex());
        AssertJUnit.assertEquals("DTO entry has the wrong secondary occurrence index",secOccIndex,dtoO.getSecondaryOccIndex());
    }
    
}
