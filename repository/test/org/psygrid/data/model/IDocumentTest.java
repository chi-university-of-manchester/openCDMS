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

import org.psygrid.data.model.hibernate.*;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;


public class IDocumentTest extends ModelTest {
    
    @Test()
	public void testGenerateInstance_Occurrence(){
            Document doc = factory.createDocument("Doc 1");
            DocumentOccurrence o1 = factory.createDocumentOccurrence("O1");
            DocumentOccurrence o2 = factory.createDocumentOccurrence("O2");
            DocumentOccurrence o3 = factory.createDocumentOccurrence("O2");
            doc.addOccurrence(o1);
            doc.addOccurrence(o2);
            doc.addOccurrence(o3);
            
            DocumentInstance inst = doc.generateInstance(o2);
            
            AssertJUnit.assertEquals("Document instance does not reference the correct occurrence",o2,inst.getOccurrence());
    }
    
        
    @Test()
	public void testNumOccurrence(){
            Document doc = factory.createDocument("Doc 1");
            DocumentOccurrence o1 = factory.createDocumentOccurrence("EntryOccurrence 1");
            DocumentOccurrence o2 = factory.createDocumentOccurrence("EntryOccurrence 2");
            DocumentOccurrence o3 = factory.createDocumentOccurrence("EntryOccurrence 3");
            doc.addOccurrence(o1);
            doc.addOccurrence(o2);
            doc.addOccurrence(o3);
            
            AssertJUnit.assertEquals("Document has the wrong number of occurrences",3,doc.numOccurrences());
    }
    
    @Test()
	public void testAddOccurrence_Success(){
            Document doc = factory.createDocument("Doc 1");
            String sName = "EntryOccurrence 1";
            DocumentOccurrence o1 = factory.createDocumentOccurrence(sName);
            doc.addOccurrence(o1);
            
            AssertJUnit.assertEquals("Document has the wrong number of occurrences",1,doc.numOccurrences());
            AssertJUnit.assertEquals("EntryOccurrence at index 0 has the wrong name",sName,doc.getOccurrence(0).getName());
    }
    
    @Test()
	public void testAddOccurrence_Null(){
            Document doc = factory.createDocument("Doc 1");
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
            Document doc = factory.createDocument("Doc 1");
            DocumentOccurrence o1 = factory.createDocumentOccurrence("EntryOccurrence 1");
            DocumentOccurrence o2 = factory.createDocumentOccurrence("EntryOccurrence 2");
            String oName3 = "EntryOccurrence 3";
            DocumentOccurrence o3 = factory.createDocumentOccurrence(oName3);
            doc.addOccurrence(o1);
            doc.addOccurrence(o2);
            doc.addOccurrence(o3);
            
            DocumentOccurrence o = doc.getOccurrence(2);
            AssertJUnit.assertNotNull("EntryOccurrence at index 2 is null",o);
            AssertJUnit.assertEquals("EntryOccurrence at index 2 has the wrong name", oName3,o.getName());
    }
    
    @Test()
	public void testGetOccurrence_InvalidId(){
            Document doc = factory.createDocument("Doc 1");
            DocumentOccurrence o1 = factory.createDocumentOccurrence("EntryOccurrence 1");
            DocumentOccurrence o2 = factory.createDocumentOccurrence("EntryOccurrence 2");
            DocumentOccurrence o3 = factory.createDocumentOccurrence("EntryOccurrence 3");
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
    
    @Test()
	public void testRemoveOccurrence_Success(){
            Document doc = factory.createDocument("Doc 1");
            String oName1 = "EntryOccurrence 1";
            DocumentOccurrence o1 = factory.createDocumentOccurrence(oName1);
            DocumentOccurrence o2 = factory.createDocumentOccurrence("EntryOccurrence 2");
            String oName3 = "EntryOccurrence 3";
            DocumentOccurrence o3 = factory.createDocumentOccurrence(oName3);
            doc.addOccurrence(o1);
            doc.addOccurrence(o2);
            doc.addOccurrence(o3);

            doc.removeOccurrence(1);
            AssertJUnit.assertEquals("Document has the wrong number of occurrences",2,doc.numOccurrences());
            AssertJUnit.assertEquals("EntryOccurrence at index 0 has the wrong name",oName1,doc.getOccurrence(0).getName());
            AssertJUnit.assertEquals("EntryOccurrence at index 1 has the wrong name",oName3,doc.getOccurrence(1).getName());
    }
    
    @Test()
	public void testRemoveOccurrence_InvalidId(){
            Document doc = factory.createDocument("Doc 1");
            DocumentOccurrence o1 = factory.createDocumentOccurrence("EntryOccurrence 1");
            DocumentOccurrence o2 = factory.createDocumentOccurrence("EntryOccurrence 2");
            DocumentOccurrence o3 = factory.createDocumentOccurrence("EntryOccurrence 3");
            doc.addOccurrence(o1);
            doc.addOccurrence(o2);
            doc.addOccurrence(o3);

            try{
                doc.removeOccurrence(-1);
                Assert.fail("Exception should have been thrown when trying to remove an occurrence using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                doc.removeOccurrence(3);
                Assert.fail("Exception should have been thrown when trying to remove an occurrence using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetEntry_Success(){
            Document doc = factory.createDocument("Doc");
            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            String entName4 = "Sec 4";
            Entry ent4 = factory.createTextEntry(entName4);
            doc.addEntry(ent4);
            String entName5 = "Sec 5";
            Entry ent5 = factory.createTextEntry(entName5);
            doc.addEntry(ent5);

            AssertJUnit.assertEquals("Entry retrieved from index 0 has the wrong name",entName1,doc.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry retrieved from index 2 has the wrong name",entName3,doc.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry retrieved from index 4 has the wrong name",entName5,doc.getEntry(4).getName());
    }
    
    @Test()
	public void testGetEntry_InvalidIndex(){
            Document doc = factory.createDocument("Doc");
            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            String entName4 = "Sec 4";
            Entry ent4 = factory.createTextEntry(entName4);
            doc.addEntry(ent4);
            String entName5 = "Sec 5";
            Entry ent5 = factory.createTextEntry(entName5);
            doc.addEntry(ent5);

            try{
                doc.getEntry(-1);
                Assert.fail("Exception should have been thrown when trying to get entry with invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                doc.getEntry(5);
                Assert.fail("Exception should have been thrown when trying to get entry with invalid index (5)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testNumEntries(){
            Document doc = factory.createDocument("Doc");
            AssertJUnit.assertEquals("Document has wrong number of entries",0,doc.numEntries());

            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            AssertJUnit.assertEquals("Document has wrong number of entries",2,doc.numEntries());
            
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            String entName4 = "Sec 4";
            Entry ent4 = factory.createTextEntry(entName4);
            doc.addEntry(ent4);
            String entName5 = "Sec 5";
            Entry ent5 = factory.createTextEntry(entName5);
            doc.addEntry(ent5);
            AssertJUnit.assertEquals("Document has wrong number of entries",5,doc.numEntries());
    }
    
    @Test()
	public void testRemoveEntry_Success(){
            Document doc = factory.createDocument("Doc");
            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            
            doc.removeEntry(1);
            AssertJUnit.assertEquals("Document does not have two entries",2,doc.numEntries());
            AssertJUnit.assertEquals("Entry at position 0 has the wrong name",entName1,doc.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at position 1 has the wrong name",entName3,doc.getEntry(1).getName());
    }
    
    @Test()
	public void testRemoveEntry_InvalidIndex(){
            Document doc = factory.createDocument("Doc");
            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            
            try{
                doc.removeEntry(-1);
                Assert.fail("Exception should have been thrown when trying to remove entry using index of -1");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",3,doc.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,doc.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,doc.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,doc.getEntry(2).getName());
            }
            try{
                doc.removeEntry(3);
                Assert.fail("Exception should have been thrown when trying to remove entry using index of 3");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",3,doc.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,doc.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,doc.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,doc.getEntry(2).getName());
            }
    }
    
    @Test()
	public void testMoveEntry_Up(){
            Document doc = factory.createDocument("Doc");
            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            String entName4 = "Sec 4";
            Entry ent4 = factory.createTextEntry(entName4);
            doc.addEntry(ent4);
            String entName5 = "Sec 5";
            Entry ent5 = factory.createTextEntry(entName5);
            doc.addEntry(ent5);

            doc.moveEntry(3,1);
            AssertJUnit.assertEquals("Document has the wrong number of entries",5,doc.numEntries());
            AssertJUnit.assertEquals("Entry at pos 0 has the wrong name",entName1,doc.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at pos 1 has the wrong name",entName4,doc.getEntry(1).getName());
            AssertJUnit.assertEquals("Entry at pos 2 has the wrong name",entName2,doc.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry at pos 3 has the wrong name",entName3,doc.getEntry(3).getName());
            AssertJUnit.assertEquals("Entry at pos 4 has the wrong name",entName5,doc.getEntry(4).getName());
    }

    @Test()
	public void testMoveEntry_Down(){
            Document doc = factory.createDocument("Doc");
            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            String entName4 = "Sec 4";
            Entry ent4 = factory.createTextEntry(entName4);
            doc.addEntry(ent4);
            String entName5 = "Sec 5";
            Entry ent5 = factory.createTextEntry(entName5);
            doc.addEntry(ent5);

            doc.moveEntry(1,3);
            AssertJUnit.assertEquals("Document has the wrong number of entries",5,doc.numEntries());
            AssertJUnit.assertEquals("Entry at pos 0 has the wrong name",entName1,doc.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at pos 1 has the wrong name",entName3,doc.getEntry(1).getName());
            AssertJUnit.assertEquals("Entry at pos 2 has the wrong name",entName4,doc.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry at pos 3 has the wrong name",entName2,doc.getEntry(3).getName());
            AssertJUnit.assertEquals("Entry at pos 4 has the wrong name",entName5,doc.getEntry(4).getName());
    }
    
    @Test()
	public void testMoveEntry_InvalidIndex(){
            Document doc = factory.createDocument("Doc");
            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            String entName4 = "Sec 4";
            Entry ent4 = factory.createTextEntry(entName4);
            doc.addEntry(ent4);
            String entName5 = "Sec 5";
            Entry ent5 = factory.createTextEntry(entName5);
            doc.addEntry(ent5);

            try{
                doc.moveEntry(-1,3);
                Assert.fail("Exception should have been thrown when invalid current index (-1) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",5,doc.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,doc.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,doc.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,doc.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName4,doc.getEntry(3).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName5,doc.getEntry(4).getName());
            }
            try{
                doc.moveEntry(5,3);
                Assert.fail("Exception should have been thrown when invalid current index (5) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",5,doc.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,doc.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,doc.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,doc.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName4,doc.getEntry(3).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName5,doc.getEntry(4).getName());
            }
            try{
                doc.moveEntry(3,-1);
                Assert.fail("Exception should have been thrown when invalid new index (-1) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",5,doc.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,doc.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,doc.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,doc.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName4,doc.getEntry(3).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName5,doc.getEntry(4).getName());
            }
            try{
                doc.moveEntry(3,5);
                Assert.fail("Exception should have been thrown when invalid new index (5) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",5,doc.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,doc.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,doc.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,doc.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName4,doc.getEntry(3).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName5,doc.getEntry(4).getName());
            }
    }
    
    @Test()
	public void testInsertEntry_Success(){
            Document doc = factory.createDocument("Doc");
            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            String entName4 = "Sec 4";
            Entry ent4 = factory.createTextEntry(entName4);
            doc.addEntry(ent4);
            
            String entName5 = "Sec 5";
            Entry ent5 = factory.createTextEntry(entName5);
            doc.insertEntry(ent5, 2);
            
            AssertJUnit.assertEquals("Incorrect number of entries",5,doc.numEntries());
            AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,doc.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,doc.getEntry(1).getName());
            AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName5,doc.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName3,doc.getEntry(3).getName());
            AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName4,doc.getEntry(4).getName());
            
            String entName6 = "Sec 6";
            Entry ent6 = factory.createTextEntry(entName6);
            doc.insertEntry(ent6, 5);
            
            AssertJUnit.assertEquals("Incorrect number of entries",6,doc.numEntries());
            AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,doc.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,doc.getEntry(1).getName());
            AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName5,doc.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName3,doc.getEntry(3).getName());
            AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName4,doc.getEntry(4).getName());
            AssertJUnit.assertEquals("Entry at index 5 has the wrong name",entName6,doc.getEntry(5).getName());
    }
    
    @Test()
	public void testInsertEntry_Null(){
            Document doc = factory.createDocument("Doc");
            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            String entName4 = "Sec 4";
            Entry ent4 = factory.createTextEntry(entName4);
            doc.addEntry(ent4);
            
            try{
                doc.insertEntry(null, 1);
                Assert.fail("Exception should have been thrown when trying to add a null entry");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",4,doc.numEntries());                
            }
    }
    
    @Test()
	public void testInsertEntry_InvalidIndex(){
            Document doc = factory.createDocument("Doc");
            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            String entName4 = "Sec 4";
            Entry ent4 = factory.createTextEntry(entName4);
            doc.addEntry(ent4);
            
            String entName5 = "Sec 5";
            Entry ent5 = factory.createTextEntry(entName5);
            
            try{
                doc.insertEntry(ent5, -1);
                Assert.fail("Exception not thrown when trying to an insert a entry using an invalid index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",4,doc.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,doc.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,doc.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName3,doc.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName4,doc.getEntry(3).getName());
            }
            
            try{
                doc.insertEntry(ent5, 5);
                Assert.fail("Exception not thrown when trying to an insert a entry using an invalid index (5)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",4,doc.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,doc.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,doc.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName3,doc.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName4,doc.getEntry(3).getName());
            }
    }
    
    @Test()
	public void testAddEntry_Success(){
            Document doc = factory.createDocument("Doc");
            String entName1 = "Sec 1";
            Entry ent1 = factory.createTextEntry(entName1);
            doc.addEntry(ent1);
            String entName2 = "Sec 2";
            Entry ent2 = factory.createTextEntry(entName2);
            doc.addEntry(ent2);
            String entName3 = "Sec 3";
            Entry ent3 = factory.createTextEntry(entName3);
            doc.addEntry(ent3);
            String entName4 = "Sec 4";
            Entry ent4 = factory.createTextEntry(entName4);
            doc.addEntry(ent4);
            
            String entName5 = "Sec 5";
            Entry ent5 = factory.createTextEntry(entName5);
            doc.addEntry(ent5);
            
            AssertJUnit.assertEquals("Incorrect number of entries",5,doc.numEntries());
            AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,doc.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,doc.getEntry(1).getName());
            AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,doc.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName4,doc.getEntry(3).getName());
            AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName5,doc.getEntry(4).getName());
    }
    
    @Test()
	public void testAddEntry_Null(){
            Document doc = factory.createDocument("Doc");

            try{
                doc.addEntry(null);
                Assert.fail("Exception should have been thrown when trying to add a null entry");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",0,doc.numEntries());
            }                        
    }

    @Test()
	public void testAddSection_Success(){
            Document doc = factory.createDocument("Doc");
            String secName1 = "Sec 1";
            Section sec1 = factory.createSection(secName1);
            doc.addSection(sec1);
            String secName2 = "Sec 2";
            Section sec2 = factory.createSection(secName2);
            doc.addSection(sec2);
            String secName3 = "Sec 3";
            Section sec3 = factory.createSection(secName3);
            doc.addSection(sec3);
            String secName4 = "Sec 4";
            Section sec4 = factory.createSection(secName4);
            doc.addSection(sec4);            
            String secName5 = "Sec 5";
            Section sec5 = factory.createSection(secName5);
            doc.addSection(sec5);
            
            AssertJUnit.assertEquals("Incorrect number of sections",5,doc.numSections());
            AssertJUnit.assertEquals("Section at index 0 has the wrong name",secName1,doc.getSection(0).getName());
            AssertJUnit.assertEquals("Section at index 1 has the wrong name",secName2,doc.getSection(1).getName());
            AssertJUnit.assertEquals("Section at index 2 has the wrong name",secName3,doc.getSection(2).getName());
            AssertJUnit.assertEquals("Section at index 3 has the wrong name",secName4,doc.getSection(3).getName());
            AssertJUnit.assertEquals("Section at index 4 has the wrong name",secName5,doc.getSection(4).getName());
    }
    
    @Test()
	public void testAddSection_Null(){
            Document doc = factory.createDocument("Doc");

            try{
                doc.addSection(null);
                Assert.fail("Exception should have been thrown when trying to add a null section");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of sections",0,doc.numSections());
            }                        
    }
    
    @Test()
	public void testNumSections(){
            Document doc = factory.createDocument("Doc");
            AssertJUnit.assertEquals("Document has wrong number of sections",0,doc.numSections());

            String secName1 = "Sec 1";
            Section sec1 = factory.createSection(secName1);
            doc.addSection(sec1);
            String secName2 = "Sec 2";
            Section sec2 = factory.createSection(secName2);
            doc.addSection(sec2);
            AssertJUnit.assertEquals("Document has wrong number of sections",2,doc.numSections());
            
            String secName3 = "Sec 3";
            Section sec3 = factory.createSection(secName3);
            doc.addSection(sec3);
            String secName4 = "Sec 4";
            Section sec4 = factory.createSection(secName4);
            doc.addSection(sec4);            
            String secName5 = "Sec 5";
            Section sec5 = factory.createSection(secName5);
            doc.addSection(sec5);
            AssertJUnit.assertEquals("Document has wrong number of sections",5,doc.numSections());
    }
    
    @Test()
	public void testGetSection_Success(){
            Document doc = factory.createDocument("Doc");
            String secName1 = "Sec 1";
            Section sec1 = factory.createSection(secName1);
            doc.addSection(sec1);
            String secName2 = "Sec 2";
            Section sec2 = factory.createSection(secName2);
            doc.addSection(sec2);
            String secName3 = "Sec 3";
            Section sec3 = factory.createSection(secName3);
            doc.addSection(sec3);
            String secName4 = "Sec 4";
            Section sec4 = factory.createSection(secName4);
            doc.addSection(sec4);            
            String secName5 = "Sec 5";
            Section sec5 = factory.createSection(secName5);
            doc.addSection(sec5);

            AssertJUnit.assertEquals("Section retrieved from index 0 has the wrong name",secName1,doc.getSection(0).getName());
            AssertJUnit.assertEquals("Section retrieved from index 2 has the wrong name",secName3,doc.getSection(2).getName());
            AssertJUnit.assertEquals("Section retrieved from index 4 has the wrong name",secName5,doc.getSection(4).getName());
    }
    
    @Test()
	public void testGetSection_InvalidIndex(){
            Document doc = factory.createDocument("Doc");
            String secName1 = "Sec 1";
            Section sec1 = factory.createSection(secName1);
            doc.addSection(sec1);
            String secName2 = "Sec 2";
            Section sec2 = factory.createSection(secName2);
            doc.addSection(sec2);
            String secName3 = "Sec 3";
            Section sec3 = factory.createSection(secName3);
            doc.addSection(sec3);
            String secName4 = "Sec 4";
            Section sec4 = factory.createSection(secName4);
            doc.addSection(sec4);            
            String secName5 = "Sec 5";
            Section sec5 = factory.createSection(secName5);
            doc.addSection(sec5);

            try{
                doc.getSection(-1);
                Assert.fail("Exception should have been thrown when trying to get section with invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                doc.getSection(5);
                Assert.fail("Exception should have been thrown when trying to get section with invalid index (5)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
}
