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

import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;


public class ISectionTest extends ModelTest {

    @Test()
	public void testNumOccurrence(){
            Section doc = factory.createSection("Sec 1");
            SectionOccurrence o1 = factory.createSectionOccurrence("Occurrence 1");
            SectionOccurrence o2 = factory.createSectionOccurrence("Occurrence 2");
            SectionOccurrence o3 = factory.createSectionOccurrence("Occurrence 3");
            doc.addOccurrence(o1);
            doc.addOccurrence(o2);
            doc.addOccurrence(o3);
            
            AssertJUnit.assertEquals("Section has the wrong number of occurrences",3,doc.numOccurrences());
    }
    
    @Test()
	public void testAddOccurrence_Success(){
            Section sec = factory.createSection("Sec 1");
            String sName = "Occurrence 1";
            SectionOccurrence o1 = factory.createSectionOccurrence(sName);
            sec.addOccurrence(o1);
            
            AssertJUnit.assertEquals("Section has the wrong number of occurrences",1,sec.numOccurrences());
            AssertJUnit.assertEquals("Occurrence at index 0 has the wrong name",sName,sec.getOccurrence(0).getName());
            AssertJUnit.assertEquals("Occurrence does not reference the correct section",sec,sec.getOccurrence(0).getSection());
    }
    
    @Test()
	public void testAddOccurrence_Null(){
            Section doc = factory.createSection("Sec 1");
            try{
                doc.addOccurrence(null);
                Assert.fail("Exception should have been thrown when trying to add a null occurrence");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetOccurrence_Success(){
            Section doc = factory.createSection("Sec 1");
            SectionOccurrence o1 = factory.createSectionOccurrence("Occurrence 1");
            SectionOccurrence o2 = factory.createSectionOccurrence("Occurrence 2");
            String oName3 = "Occurrence 3";
            SectionOccurrence o3 = factory.createSectionOccurrence(oName3);
            doc.addOccurrence(o1);
            doc.addOccurrence(o2);
            doc.addOccurrence(o3);
            
            SectionOccurrence o = doc.getOccurrence(2);
            AssertJUnit.assertNotNull("Occurrence at index 2 is null",o);
            AssertJUnit.assertEquals("Occurrence at index 2 has the wrong name", oName3,o.getName());
    }
    
    @Test()
	public void testGetOccurrence_InvalidId(){
            Section doc = factory.createSection("Sec 1");
            SectionOccurrence o1 = factory.createSectionOccurrence("Occurrence 1");
            SectionOccurrence o2 = factory.createSectionOccurrence("Occurrence 2");
            SectionOccurrence o3 = factory.createSectionOccurrence("Occurrence 3");
            doc.addOccurrence(o1);
            doc.addOccurrence(o2);
            doc.addOccurrence(o3);

            try{
                doc.getOccurrence(-1);
                Assert.fail("Exception should have been thrown when trying to get an occurrence using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                doc.getOccurrence(3);
                Assert.fail("Exception should have been thrown when trying to get an occurrence using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    

}
