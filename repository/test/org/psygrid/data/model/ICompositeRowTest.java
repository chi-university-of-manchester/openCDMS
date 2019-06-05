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


public class ICompositeRowTest extends ModelTest {

    @Test()
	public void testAddResponse(){
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
            BasicEntry be2 = factory.createTextEntry("TE2");
            ce.addEntry(be2);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            CompositeResponse ceResp = ce.generateInstance(so);
            docInst.addResponse(ceResp);
            CompositeRow row1 = ceResp.createCompositeRow();
            
            BasicResponse beResp = be.generateInstance(so);
            row1.addResponse(beResp);
            BasicResponse beResp2 = be2.generateInstance(so);
            row1.addResponse(beResp2);
            AssertJUnit.assertEquals("Basic response to basic entry 1 in composite row is not correct",beResp,row1.getResponse(be));
            AssertJUnit.assertEquals("Basic response to basic entry 2 in composite row is not correct",beResp2,row1.getResponse(be2));
    }
    
    @Test()
	public void testAddResponse_Invalid(){
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
            BasicResponse beResp = be.generateInstance(so);
            row1.addResponse(beResp);
            try{
                Section sec2 = factory.createSection("Sec2");
                SectionOccurrence so2 = factory.createSectionOccurrence("SO2");
                sec2.addOccurrence(so2);
                BasicEntry be2 = factory.createTextEntry("TE2");
                be2.setSection(sec2);
                BasicResponse br2 = be2.generateInstance(so2);
                row1.addResponse(br2);
                Assert.fail("Exception should have been thrown when trying to add an invalid basic response");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testAddResponse_Duplicate(){
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
            BasicResponse beResp = be.generateInstance(so);
            row1.addResponse(beResp);
            try{
                row1.addResponse(beResp);
                Assert.fail("Exception should have been thrown when trying to add a duplicate response");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetResponse_OK(){
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
            BasicEntry be2 = factory.createTextEntry("TE2");
            ce.addEntry(be2);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            CompositeResponse ceResp = ce.generateInstance(so);
            docInst.addResponse(ceResp);
            CompositeRow row1 = ceResp.createCompositeRow();
            BasicResponse beResp = be.generateInstance(so);
            row1.addResponse(beResp);
            BasicResponse beResp2 = be2.generateInstance(so);
            row1.addResponse(beResp2);
            
            AssertJUnit.assertTrue("Retrieved basic response to basic entry 1 is not correct",beResp == row1.getResponse(be));
            AssertJUnit.assertTrue("Retrieved basic response to basic entry 2 is not correct",beResp2 == row1.getResponse(be2));
    }
        
    @Test()
	public void testGetResponse_Null(){
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
            
            AssertJUnit.assertNull("No basic response exists for the given basic entry - should have returned null",row1.getResponse(be));
    }
            
}
