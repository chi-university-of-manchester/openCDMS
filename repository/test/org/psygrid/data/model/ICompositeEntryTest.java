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

public class ICompositeEntryTest extends ModelTest {
    
    @Test()
	public void testGetEntry_Success(){
            CompositeEntry comp = factory.createComposite("Comp");
            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            String entName4 = "Sec 4";
            BasicEntry ent4 = factory.createTextEntry(entName4);
            comp.addEntry(ent4);
            String entName5 = "Sec 5";
            BasicEntry ent5 = factory.createTextEntry(entName5);
            comp.addEntry(ent5);

            AssertJUnit.assertEquals("Entry retrieved from index 0 has the wrong name",entName1,comp.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry retrieved from index 2 has the wrong name",entName3,comp.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry retrieved from index 4 has the wrong name",entName5,comp.getEntry(4).getName());
    }
    
    @Test()
	public void testGetEntry_InvalidIndex(){
            CompositeEntry comp = factory.createComposite("Comp");
            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            String entName4 = "Sec 4";
            BasicEntry ent4 = factory.createTextEntry(entName4);
            comp.addEntry(ent4);
            String entName5 = "Sec 5";
            BasicEntry ent5 = factory.createTextEntry(entName5);
            comp.addEntry(ent5);

            try{
                comp.getEntry(-1);
                Assert.fail("Exception should have been thrown when trying to get entry with invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                comp.getEntry(5);
                Assert.fail("Exception should have been thrown when trying to get entry with invalid index (5)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testNumEntries(){
            CompositeEntry comp = factory.createComposite("Comp");
            AssertJUnit.assertEquals("Composite entry has wrong number of entries",0,comp.numEntries());

            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            AssertJUnit.assertEquals("Composite entry has wrong number of entries",2,comp.numEntries());
            
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            String entName4 = "Sec 4";
            BasicEntry ent4 = factory.createTextEntry(entName4);
            comp.addEntry(ent4);
            String entName5 = "Sec 5";
            BasicEntry ent5 = factory.createTextEntry(entName5);
            comp.addEntry(ent5);
            AssertJUnit.assertEquals("Composite entry has wrong number of entries",5,comp.numEntries());
    }
    
    @Test()
	public void testRemoveEntry_Success(){
            CompositeEntry comp = factory.createComposite("Comp");
            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            
            comp.removeEntry(1);
            AssertJUnit.assertEquals("Composite entry does not have two entries",2,comp.numEntries());
            AssertJUnit.assertEquals("Entry at position 0 has the wrong name",entName1,comp.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at position 1 has the wrong name",entName3,comp.getEntry(1).getName());
    }
    
    @Test()
	public void testRemoveEntry_InvalidIndex(){
            CompositeEntry comp = factory.createComposite("Comp");
            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            
            try{
                comp.removeEntry(-1);
                Assert.fail("Exception should have been thrown when trying to remove entry using index of -1");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",3,comp.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,comp.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,comp.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,comp.getEntry(2).getName());
            }
            try{
                comp.removeEntry(3);
                Assert.fail("Exception should have been thrown when trying to remove entry using index of 3");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",3,comp.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,comp.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,comp.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,comp.getEntry(2).getName());
            }
    }
    
    @Test()
	public void testMoveEntry_Up(){
            CompositeEntry comp = factory.createComposite("Comp");
            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            String entName4 = "Sec 4";
            BasicEntry ent4 = factory.createTextEntry(entName4);
            comp.addEntry(ent4);
            String entName5 = "Sec 5";
            BasicEntry ent5 = factory.createTextEntry(entName5);
            comp.addEntry(ent5);

            comp.moveEntry(3,1);
            AssertJUnit.assertEquals("Composite entry has the wrong number of entries",5,comp.numEntries());
            AssertJUnit.assertEquals("Entry at pos 0 has the wrong name",entName1,comp.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at pos 1 has the wrong name",entName4,comp.getEntry(1).getName());
            AssertJUnit.assertEquals("Entry at pos 2 has the wrong name",entName2,comp.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry at pos 3 has the wrong name",entName3,comp.getEntry(3).getName());
            AssertJUnit.assertEquals("Entry at pos 4 has the wrong name",entName5,comp.getEntry(4).getName());
    }

    @Test()
	public void testMoveEntry_Down(){
            CompositeEntry comp = factory.createComposite("Comp");
            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            String entName4 = "Sec 4";
            BasicEntry ent4 = factory.createTextEntry(entName4);
            comp.addEntry(ent4);
            String entName5 = "Sec 5";
            BasicEntry ent5 = factory.createTextEntry(entName5);
            comp.addEntry(ent5);

            comp.moveEntry(1,3);
            AssertJUnit.assertEquals("Composite entry has the wrong number of entries",5,comp.numEntries());
            AssertJUnit.assertEquals("Entry at pos 0 has the wrong name",entName1,comp.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at pos 1 has the wrong name",entName3,comp.getEntry(1).getName());
            AssertJUnit.assertEquals("Entry at pos 2 has the wrong name",entName4,comp.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry at pos 3 has the wrong name",entName2,comp.getEntry(3).getName());
            AssertJUnit.assertEquals("Entry at pos 4 has the wrong name",entName5,comp.getEntry(4).getName());
    }
    
    @Test()
	public void testMoveEntry_InvalidIndex(){
            CompositeEntry comp = factory.createComposite("Comp");
            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            String entName4 = "Sec 4";
            BasicEntry ent4 = factory.createTextEntry(entName4);
            comp.addEntry(ent4);
            String entName5 = "Sec 5";
            BasicEntry ent5 = factory.createTextEntry(entName5);
            comp.addEntry(ent5);

            try{
                comp.moveEntry(-1,3);
                Assert.fail("Exception should have been thrown when invalid current index (-1) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",5,comp.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,comp.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,comp.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,comp.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName4,comp.getEntry(3).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName5,comp.getEntry(4).getName());
            }
            try{
                comp.moveEntry(5,3);
                Assert.fail("Exception should have been thrown when invalid current index (5) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",5,comp.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,comp.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,comp.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,comp.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName4,comp.getEntry(3).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName5,comp.getEntry(4).getName());
            }
            try{
                comp.moveEntry(3,-1);
                Assert.fail("Exception should have been thrown when invalid new index (-1) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",5,comp.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,comp.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,comp.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,comp.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName4,comp.getEntry(3).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName5,comp.getEntry(4).getName());
            }
            try{
                comp.moveEntry(3,5);
                Assert.fail("Exception should have been thrown when invalid new index (5) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",5,comp.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,comp.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,comp.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,comp.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName4,comp.getEntry(3).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName5,comp.getEntry(4).getName());
            }
    }
    
    @Test()
	public void testInsertEntry_Success(){
            CompositeEntry comp = factory.createComposite("Comp");
            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            String entName4 = "Sec 4";
            BasicEntry ent4 = factory.createTextEntry(entName4);
            comp.addEntry(ent4);
            
            String entName5 = "Sec 5";
            BasicEntry ent5 = factory.createTextEntry(entName5);
            comp.insertEntry(ent5, 2);
            
            AssertJUnit.assertEquals("Incorrect number of entries",5,comp.numEntries());
            AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,comp.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,comp.getEntry(1).getName());
            AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName5,comp.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName3,comp.getEntry(3).getName());
            AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName4,comp.getEntry(4).getName());
            
            String entName6 = "Sec 6";
            BasicEntry ent6 = factory.createTextEntry(entName6);
            comp.insertEntry(ent6, 5);
            
            AssertJUnit.assertEquals("Incorrect number of entries",6,comp.numEntries());
            AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,comp.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,comp.getEntry(1).getName());
            AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName5,comp.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName3,comp.getEntry(3).getName());
            AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName4,comp.getEntry(4).getName());
            AssertJUnit.assertEquals("Entry at index 5 has the wrong name",entName6,comp.getEntry(5).getName());
    }
    
    @Test()
	public void testInsertEntry_Null(){
            CompositeEntry comp = factory.createComposite("Comp");
            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            String entName4 = "Sec 4";
            BasicEntry ent4 = factory.createTextEntry(entName4);
            comp.addEntry(ent4);
            
            try{
                comp.insertEntry(null, 1);
                Assert.fail("Exception should have been thrown when trying to add a null entry");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",4,comp.numEntries());                
            }
    }
    
    @Test()
	public void testInsertEntry_InvalidIndex(){
            CompositeEntry comp = factory.createComposite("Comp");
            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            String entName4 = "Sec 4";
            BasicEntry ent4 = factory.createTextEntry(entName4);
            comp.addEntry(ent4);
            
            String entName5 = "Sec 5";
            BasicEntry ent5 = factory.createTextEntry(entName5);
            
            try{
                comp.insertEntry(ent5, -1);
                Assert.fail("Exception not thrown when trying to an insert a entry using an invalid index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",4,comp.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,comp.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,comp.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName3,comp.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName4,comp.getEntry(3).getName());
            }
            
            try{
                comp.insertEntry(ent5, 5);
                Assert.fail("Exception not thrown when trying to an insert a entry using an invalid index (5)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",4,comp.numEntries());
                AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,comp.getEntry(0).getName());
                AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,comp.getEntry(1).getName());
                AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName3,comp.getEntry(2).getName());
                AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName4,comp.getEntry(3).getName());
            }
    }
    
    @Test()
	public void testAddEntry_Success(){
            CompositeEntry comp = factory.createComposite("Comp");
            String entName1 = "Sec 1";
            BasicEntry ent1 = factory.createTextEntry(entName1);
            comp.addEntry(ent1);
            String entName2 = "Sec 2";
            BasicEntry ent2 = factory.createTextEntry(entName2);
            comp.addEntry(ent2);
            String entName3 = "Sec 3";
            BasicEntry ent3 = factory.createTextEntry(entName3);
            comp.addEntry(ent3);
            String entName4 = "Sec 4";
            BasicEntry ent4 = factory.createTextEntry(entName4);
            comp.addEntry(ent4);
            
            String entName5 = "Sec 5";
            BasicEntry ent5 = factory.createTextEntry(entName5);
            comp.addEntry(ent5);
            
            AssertJUnit.assertEquals("Incorrect number of entries",5,comp.numEntries());
            AssertJUnit.assertEquals("Entry at index 0 has the wrong name",entName1,comp.getEntry(0).getName());
            AssertJUnit.assertEquals("Entry at index 1 has the wrong name",entName2,comp.getEntry(1).getName());
            AssertJUnit.assertEquals("Entry at index 2 has the wrong name",entName3,comp.getEntry(2).getName());
            AssertJUnit.assertEquals("Entry at index 3 has the wrong name",entName4,comp.getEntry(3).getName());
            AssertJUnit.assertEquals("Entry at index 4 has the wrong name",entName5,comp.getEntry(4).getName());
    }
    
    @Test()
	public void testAddEntry_Null(){
            CompositeEntry comp = factory.createComposite("Comp");

            try{
                comp.addEntry(null);
                Assert.fail("Exception should have been thrown when trying to add a null entry");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of entries",0,comp.numEntries());
            }                        
    }

    @Test()
	public void testNumRowLabels(){
            CompositeEntry comp = factory.createComposite("Comp");
            comp.addRowLabel("Label 1");
            comp.addRowLabel("Label 2");
            comp.addRowLabel("Label 3");
            AssertJUnit.assertEquals("Composite has the wrong number of row labels",3,comp.numRowLabels());
    }
    
    @Test()
	public void testAddRowLabel_OK(){
            CompositeEntry comp = factory.createComposite("Comp");
            String label1 = "Label 1";
            comp.addRowLabel(label1);
            AssertJUnit.assertEquals("Composite has the wrong number of row labels",1,comp.numRowLabels());
            AssertJUnit.assertEquals("Composite has the wrong row label",label1,comp.getRowLabel(0));
    }
    
    @Test()
	public void testAddRowLabel_Null(){
            CompositeEntry comp = factory.createComposite("Comp");
            try{
                comp.addRowLabel(null);
                Assert.fail("Exception should have been thrown when trying to add a null row label");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetRowLabel_OK(){
            CompositeEntry comp = factory.createComposite("Comp");
            String label1 = "Label 1";
            comp.addRowLabel(label1);
            String label2 = "Label 2";
            comp.addRowLabel(label2);
            String label3 = "Label 3";
            comp.addRowLabel(label3);
            AssertJUnit.assertEquals("Composite has the wrong number of row labels",3,comp.numRowLabels());
            AssertJUnit.assertEquals("Composite has the wrong row label at index 0",label1,comp.getRowLabel(0));
            AssertJUnit.assertEquals("Composite has the wrong row label at index 1",label2,comp.getRowLabel(1));
            AssertJUnit.assertEquals("Composite has the wrong row label at index 2",label3,comp.getRowLabel(2));
    }
    
    @Test()
	public void testGetRowLabel_Invalid(){
            CompositeEntry comp = factory.createComposite("Comp");
            String label1 = "Label 1";
            comp.addRowLabel(label1);
            String label2 = "Label 2";
            comp.addRowLabel(label2);
            String label3 = "Label 3";
            comp.addRowLabel(label3);
            try{
                comp.getRowLabel(-1);
                Assert.fail("Exception should have been thrown when trying to get a row label using invalid index -1");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                comp.getRowLabel(3);
                Assert.fail("Exception should have been thrown when trying to get a row label using invalid index 3");
            }
            catch(ModelException ex){
                //do nothing
            }
    }

    @Test()
	public void testGenerateInstance_SecOcc(){
            Document d = factory.createDocument("D");
            Section s = factory.createSection("S");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            s.addOccurrence(so);
            d.addSection(s);
            CompositeEntry ce = factory.createComposite("BE");
            d.addEntry(ce);
            ce.setSection(s);
            
            CompositeResponse cr = ce.generateInstance(so);
            
            AssertJUnit.assertEquals("Composite response has the wrong entry", ce, cr.getEntry());
            AssertJUnit.assertEquals("Composite response has the wrong section occurrence",so,cr.getSectionOccurrence());
            AssertJUnit.assertNull("Composite response has non-null sec occ inst",cr.getSecOccInstance());

            SectionOccurrence so2 = factory.createSectionOccurrence("SO2");
            try{
                ce.generateInstance(so2);
                Assert.fail("Exception should have been thrown when trying to generate instance using invalid sec occ");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            so.setMultipleAllowed(true);
            try{
                ce.generateInstance(so);
                Assert.fail("Exception should have been thrown when trying to generate instance for sec occ that allows multiple runtime instances");
            }
            catch(ModelException ex){
                //do nothing
            }
    }

    @Test()
	public void testGenerateInstance_SecOccInst(){
            Document d = factory.createDocument("D");
            DocumentOccurrence do1 = factory.createDocumentOccurrence("DO1");
            d.addOccurrence(do1);
            Section s = factory.createSection("S");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            so.setMultipleAllowed(true);
            s.addOccurrence(so);
            d.addSection(s);
            CompositeEntry ce = factory.createComposite("CE");
            d.addEntry(ce);
            ce.setSection(s);
            
            DocumentInstance di = d.generateInstance(do1);
            SecOccInstance soi = so.generateInstance();
            di.addSecOccInstance(soi);
            CompositeResponse cr = ce.generateInstance(soi);
            
            AssertJUnit.assertEquals("Composite response has the wrong entry", ce, cr.getEntry());
            AssertJUnit.assertEquals("Composite response has the wrong section occurrence instance",soi,cr.getSecOccInstance());
            AssertJUnit.assertNull("Composite response has non-null sec occ",cr.getSectionOccurrence());

            SectionOccurrence so2 = factory.createSectionOccurrence("SO2");
            so2.setMultipleAllowed(true);
            SecOccInstance soi2 = so2.generateInstance();
            try{
                ce.generateInstance(soi2);
                Assert.fail("Exception should have been thrown when trying to generate instance using invalid sec occ inst");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            so.setMultipleAllowed(false);
            try{
                ce.generateInstance(soi);
                Assert.fail("Exception should have been thrown when trying to generate instance for sec occ that does not allow multiple runtime instances");
            }
            catch(ModelException ex){
                //do nothing
            }
    }

}
