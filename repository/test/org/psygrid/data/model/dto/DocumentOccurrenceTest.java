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

import org.psygrid.data.model.hibernate.TimeUnits;

public class DocumentOccurrenceTest {

    @Test()
	public void testToHibernate(){
        
        DocumentOccurrenceDTO o = new DocumentOccurrenceDTO();
        
        String eName = "Doc";
        DocumentDTO d = new DocumentDTO();
        d.setName(eName);
        o.setDocument(d);
        
        String name = "Foo";
        o.setName(name);
        
        String gName = "Group";
        DocumentGroupDTO group = new DocumentGroupDTO();
        group.setName(gName);
        o.setDocumentGroup(group);
        
        Integer time = new Integer(103);
        o.setScheduleTime(time);
        
        TimeUnits units = TimeUnits.DAYS;
        o.setScheduleUnits(units.toString());

        o.setReminders(new ReminderDTO[2]);
        ReminderDTO r1 = new ReminderDTO();
        Long r1id = new Long(10);
        r1.setId(r1id);
        o.getReminders()[0] = r1;
        ReminderDTO r2 = new ReminderDTO();
        Long r2id = new Long(11);
        r2.setId(r2id);
        o.getReminders()[1] = r2;
        
        String label = "Label";
        o.setLabel(label);
        
        boolean rndTrigger = true;
        o.setRandomizationTrigger(rndTrigger);
        
        boolean locked = true;
        o.setLocked(locked);
        
        Long priOccIndex = new Long(12);
        o.setPrimaryOccIndex(priOccIndex);

        Long secOccIndex = new Long(13);
        o.setSecondaryOccIndex(secOccIndex);

        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
        org.psygrid.data.model.hibernate.DocumentOccurrence hO = o.toHibernate(hRefs);
        
        AssertJUnit.assertNotNull("Hibernate occurrence is null", hO);
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong document",eName,hO.getDocument().getName());
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong name",name,hO.getName());
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong document group",gName,hO.getDocumentGroup().getName());
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong time",time,hO.getScheduleTime());
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong units",units,hO.getScheduleUnits());
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong number of reminders",o.getReminders().length,hO.getReminders().size());
        AssertJUnit.assertNotNull("Hibernate occurrence has null reminder at index 0", hO.getReminders().get(0));
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong reminder at index 0",r1id,hO.getReminders().get(0).getId());
        AssertJUnit.assertNotNull("Hibernate occurrence has null reminder at index 1", hO.getReminders().get(1));
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong reminder at index 1",r2id,hO.getReminders().get(1).getId());
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong label",label,hO.getLabel());
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong randomization trigger",rndTrigger,hO.isRandomizationTrigger());
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong locked",locked,hO.isLocked());
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong primary occurrence index",priOccIndex,hO.getPrimaryOccIndex());
        AssertJUnit.assertEquals("Hibernate occurrence has the wrong secondary occurrence index",secOccIndex,hO.getSecondaryOccIndex());
    }
    
}
