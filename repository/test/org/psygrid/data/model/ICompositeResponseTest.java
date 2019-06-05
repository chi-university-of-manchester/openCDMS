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



public class ICompositeResponseTest extends ModelTest {

    @Test()
	public void testCreateCompositeRow(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            CompositeEntry ce = factory.createComposite("CE");
            doc.addEntry(ce);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            ce.setSection(sec);
            BasicEntry be = factory.createTextEntry("TE");
            ce.addEntry(be);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            CompositeResponse ceResp = ce.generateInstance(so);
            docInst.addResponse(ceResp);

            CompositeRow row = ceResp.createCompositeRow();
            
            AssertJUnit.assertNotNull("Created row is null",row);
            AssertJUnit.assertTrue("Created row is not at index 0",row==ceResp.getCompositeRow(0));
            AssertJUnit.assertEquals("Created row does not correctly reference the composite response",ceResp, row.getCompositeResponse());
    }
    
    @Test()
	public void testCreateCompositeRow_Reason(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            CompositeEntry ce = factory.createComposite("CE");
            doc.addEntry(ce);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            ce.setSection(sec);
            BasicEntry be = factory.createTextEntry("TE");
            ce.addEntry(be);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            CompositeResponse ceResp = ce.generateInstance(so);
            docInst.addResponse(ceResp);

            String reason = "Reason";
            CompositeRow row = ceResp.createCompositeRow(reason);
            
            AssertJUnit.assertNotNull("Created row is null",row);
            AssertJUnit.assertTrue("Created row is not at index 0",row==ceResp.getCompositeRow(0));
            AssertJUnit.assertEquals("Created row does not correctly reference the composite response",ceResp, row.getCompositeResponse());
            Provenance prov = ceResp.getProvenance().get(0);
            AssertJUnit.assertEquals("Provenance does not have the correct comment", reason, prov.getComment());
            AssertJUnit.assertEquals("Provenance does not have the correct current value", row, prov.getCurrentValue());
    }
    
    @Test()
	public void testGetCompositeRow(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            CompositeEntry ce = factory.createComposite("CE");
            doc.addEntry(ce);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            ce.setSection(sec);
            BasicEntry be = factory.createTextEntry("TE");
            ce.addEntry(be);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            CompositeResponse ceResp = ce.generateInstance(so);
            docInst.addResponse(ceResp);
            
            CompositeRow row1 = ceResp.createCompositeRow();
            CompositeRow row2 = ceResp.createCompositeRow();
            CompositeRow row3 = ceResp.createCompositeRow();
            
            AssertJUnit.assertEquals("Row at index 0 is not correct",row1,ceResp.getCompositeRow(0));
            AssertJUnit.assertEquals("Row at index 1 is not correct",row2,ceResp.getCompositeRow(1));
            AssertJUnit.assertEquals("Row at index 2 is not correct",row3,ceResp.getCompositeRow(2));
            
            try{
                ceResp.getCompositeRow(3);
                Assert.fail("Exception should have been thrown when trying to get a row using an invalid index (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testRemoveCompositeRow(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            CompositeEntry ce = factory.createComposite("CE");
            doc.addEntry(ce);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            ce.setSection(sec);
            BasicEntry be = factory.createTextEntry("TE");
            ce.addEntry(be);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            CompositeResponse ceResp = ce.generateInstance(so);
            docInst.addResponse(ceResp);
            
            CompositeRow row1 = ceResp.createCompositeRow();
            ceResp.createCompositeRow();
            CompositeRow row3 = ceResp.createCompositeRow();
            
            ceResp.removeCompositeRow(1);
            AssertJUnit.assertEquals("Row at index 0 is not correct",row1,ceResp.getCompositeRow(0));
            AssertJUnit.assertEquals("Row at index 1 is not correct",row3,ceResp.getCompositeRow(1));
            
            try{
                ceResp.getCompositeRow(2);
                Assert.fail("Exception should have been thrown when trying to get a row using an invalid index (2)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testRemoveCompositeRow_Reason(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            CompositeEntry ce = factory.createComposite("CE");
            doc.addEntry(ce);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            ce.setSection(sec);
            BasicEntry be = factory.createTextEntry("TE");
            ce.addEntry(be);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            CompositeResponse ceResp = ce.generateInstance(so);
            docInst.addResponse(ceResp);
            
            CompositeRow row1 = ceResp.createCompositeRow();
            CompositeRow row2 = ceResp.createCompositeRow();
            CompositeRow row3 = ceResp.createCompositeRow();
            
            String reason = "Reason";
            ceResp.removeCompositeRow(1, reason);
            AssertJUnit.assertEquals("Row at index 0 is not correct",row1,ceResp.getCompositeRow(0));
            AssertJUnit.assertEquals("Row at index 1 is not correct",row3,ceResp.getCompositeRow(1));
            Provenance prov = ceResp.getProvenance().get(ceResp.getProvenance().size() - 1);
            AssertJUnit.assertEquals("Provenance does not have the correct comment", reason, prov.getComment());
            AssertJUnit.assertEquals("Provenance does not have the correct prev value", row2, prov.getPrevValue());
            
            try{
                ceResp.getCompositeRow(2);
                Assert.fail("Exception should have been thrown when trying to get a row using an invalid index (2)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testNumCompositeRows(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            CompositeEntry ce = factory.createComposite("CE");
            doc.addEntry(ce);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            ce.setSection(sec);
            BasicEntry be = factory.createTextEntry("TE");
            ce.addEntry(be);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            CompositeResponse ceResp = ce.generateInstance(so);
            docInst.addResponse(ceResp);

            AssertJUnit.assertEquals("Composite response has the wrong number of composite rows",0,ceResp.numCompositeRows());
            ceResp.createCompositeRow();
            ceResp.createCompositeRow();
            AssertJUnit.assertEquals("Composite response has the wrong number of composite rows",2,ceResp.numCompositeRows());
            ceResp.createCompositeRow();
            AssertJUnit.assertEquals("Composite response has the wrong number of composite rows",3,ceResp.numCompositeRows());
    }
        
}
