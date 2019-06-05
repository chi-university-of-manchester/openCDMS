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

package org.psygrid.data.model;

import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.Reminder;
import org.psygrid.data.model.hibernate.TimeUnits;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.psygrid.data.model.hibernate.ReminderLevel;

public class IDocumentOccurrenceTest extends ModelTest {

    @Test()
	public void testConstructor(){
            String name = "Foo bar";
            DocumentOccurrence o = factory.createDocumentOccurrence(name);
            
            AssertJUnit.assertEquals("DocumentOccurrence has the wrong name", name, o.getName());
    }
    
    @Test()
	public void testNumReminders(){
            DocumentOccurrence s = factory.createDocumentOccurrence("D1");
            AssertJUnit.assertEquals("Schedulable has the wrong number of reminders",0,s.numReminders());
            
            Reminder r1 = factory.createReminder(2, TimeUnits.MONTHS, ReminderLevel.MILD);
            s.addReminder(r1);
            AssertJUnit.assertEquals("Schedulable has the wrong number of reminders",1,s.numReminders());
            
            Reminder r2 = factory.createReminder(3, TimeUnits.DAYS, ReminderLevel.SEVERE);
            s.addReminder(r2);
            Reminder r3 = factory.createReminder(5, TimeUnits.DAYS, ReminderLevel.NORMAL);
            s.addReminder(r3);
            AssertJUnit.assertEquals("Schedulable has the wrong number of reminders",3,s.numReminders());
    }
    
    @Test()
	public void testAddReminder_Success(){
            DocumentOccurrence s = factory.createDocumentOccurrence("D1");
            Integer time1 = new Integer(2);
            Reminder r1 = factory.createReminder(time1, TimeUnits.MONTHS, ReminderLevel.MILD);
            Integer time2 = new Integer(5);
            Reminder r2 = factory.createReminder(time2, TimeUnits.DAYS, ReminderLevel.SEVERE);
            s.addReminder(r1);
            s.addReminder(r2);
            
            Reminder r = s.getReminder(0);
            AssertJUnit.assertNotNull("Retrieved a null reminder", r);
            AssertJUnit.assertEquals("Reminder has the wrong time value", time1, r.getTime());
    }
    
    @Test()
	public void testAddReminder_Null(){
            DocumentOccurrence s = factory.createDocumentOccurrence("D1");
            try{
                s.addReminder(null);
                Assert.fail("Exception should have been thrown when trying to add a null reminder");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetReminder_Success(){
            DocumentOccurrence s = factory.createDocumentOccurrence("D1");
            Integer time1 = new Integer(2);
            Reminder r1 = factory.createReminder(time1, TimeUnits.MONTHS, ReminderLevel.MILD);
            Integer time2 = new Integer(5);
            Reminder r2 = factory.createReminder(time2, TimeUnits.DAYS, ReminderLevel.SEVERE);
            s.addReminder(r1);
            s.addReminder(r2);
            
            Reminder r = s.getReminder(0);
            AssertJUnit.assertNotNull("Retrieved a null reminder", r);
            AssertJUnit.assertEquals("Reminder has the wrong time value", time1, r.getTime());
    }
    
    @Test()
	public void testGetReminder_InvalidId(){
            DocumentOccurrence s = factory.createDocumentOccurrence("D1");
            Integer time1 = new Integer(2);
            Reminder r1 = factory.createReminder(time1, TimeUnits.MONTHS, ReminderLevel.MILD);
            Integer time2 = new Integer(5);
            Reminder r2 = factory.createReminder(time2, TimeUnits.DAYS, ReminderLevel.SEVERE);
            s.addReminder(r1);
            s.addReminder(r2);

            try{
                s.getReminder(-1);
                Assert.fail("Exception should have been thrown when trying to get reminder using invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                s.getReminder(2);
                Assert.fail("Exception should have been thrown when trying to get reminder using invalid id (2)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testRemoveReminder_Success(){
            DocumentOccurrence s = factory.createDocumentOccurrence("D1");
            Integer time1 = new Integer(2);
            Reminder r1 = factory.createReminder(time1, TimeUnits.MONTHS, ReminderLevel.MILD);
            Integer time2 = new Integer(5);
            Reminder r2 = factory.createReminder(time2, TimeUnits.DAYS, ReminderLevel.SEVERE);
            s.addReminder(r1);
            s.addReminder(r2);
            
            s.removeReminder(0);
            AssertJUnit.assertEquals("Schedulable has the wrong number of reminders",1,s.numReminders());
            Reminder r = s.getReminder(0);
            AssertJUnit.assertNotNull("Retrieved a null reminder", r);
            AssertJUnit.assertEquals("Reminder has the wrong time value", time2, r.getTime());
    }
    
    @Test()
	public void testRemoveReminder_InvalidId(){
            DocumentOccurrence s = factory.createDocumentOccurrence("D1");
            Integer time1 = new Integer(2);
            Reminder r1 = factory.createReminder(time1, TimeUnits.MONTHS, ReminderLevel.MILD);
            Integer time2 = new Integer(5);
            Reminder r2 = factory.createReminder(time2, TimeUnits.DAYS, ReminderLevel.SEVERE);
            s.addReminder(r1);
            s.addReminder(r2);

            try{
                s.removeReminder(-1);
                Assert.fail("Exception should have been thrown when trying to get reminder using invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                s.removeReminder(2);
                Assert.fail("Exception should have been thrown when trying to get reminder using invalid id (2)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
}
