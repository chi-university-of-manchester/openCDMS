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
import java.util.List;



public class IDocumentInstanceTest extends ModelTest {

    @Test()
	public void testAddResponse_Basic(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            TextEntry te = factory.createTextEntry("TE");
            doc.addEntry(te);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            te.setSection(sec);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            BasicResponse teResp = te.generateInstance(so);
            docInst.addResponse(teResp);
            
            AssertJUnit.assertEquals("Doc instance has the wrong number of responses",1,docInst.getResponses(te).size());
    }
    
    @Test()
	public void testAddResponse_Composite(){
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
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            CompositeResponse ceResp = ce.generateInstance(so);
            docInst.addResponse(ceResp);
            
            AssertJUnit.assertEquals("Doc instance has the wrong number of responses",1,docInst.getResponses(ce).size());
    }
    
    @Test()
	public void testAddResponse_Invalid(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            TextEntry te = factory.createTextEntry("TE");
            doc.addEntry(te);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            doc.addSection(sec);
            te.setSection(sec);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            BasicResponse resp = te.generateInstance(so);
            docInst.addResponse(resp);
            try{
                BasicEntry be = factory.createTextEntry("TE");
                Section sec2 = factory.createSection("Sec2");
                SectionOccurrence so2 = factory.createSectionOccurrence("SO2");
                sec2.addOccurrence(so2);
                be.setSection(sec2);
                BasicResponse br = be.generateInstance(so2);
                docInst.addResponse(br);
                Assert.fail("Exception should have been thrown when trying to add an invalid response");
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
            TextEntry te = factory.createTextEntry("TE");
            doc.addEntry(te);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            te.setSection(sec);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            BasicResponse resp = te.generateInstance(so);
            docInst.addResponse(resp);
            try{
                docInst.addResponse(resp);
                Assert.fail("Exception should have been thrown when trying to add a duplicate response");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetResponse_SecOcc(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            TextEntry te = factory.createTextEntry("TE");
            doc.addEntry(te);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            te.setSection(sec);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            BasicResponse teResp = te.generateInstance(so);
            ITextValue tv = te.generateValue();
            String value = "Foo bar";
            tv.setValue(value);
            teResp.setValue(tv);
            docInst.addResponse(teResp);

            teResp = (BasicResponse)docInst.getResponse(te, so);
            
            AssertJUnit.assertEquals("Retrieved response has the wrong value", value, ((ITextValue)teResp.getValue()).getValue());
    }
    
    @Test()
	public void testGetResponse_SecOcc_Null(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            TextEntry te = factory.createTextEntry("TE");
            doc.addEntry(te);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            te.setSection(sec);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);

            AssertJUnit.assertNull("No response for given entry and occurrence - should have returned null", docInst.getResponse(te, so));
            
            so.setMultipleAllowed(true);
            SecOccInstance soi = so.generateInstance();
            
            Record rec2 = ds.generateInstance();
            DocumentInstance docInst2 = doc.generateInstance(doc.getOccurrence(0));
            rec2.addDocumentInstance(docInst2);
            BasicResponse br = te.generateInstance(soi);
            docInst2.addResponse(br);
            
            AssertJUnit.assertNull("No response for given entry and occurrence - should have returned null (2)", docInst2.getResponse(te, so));
    }
    
    @Test()
	public void testGetResponse_SecOccInst(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            TextEntry te = factory.createTextEntry("TE");
            doc.addEntry(te);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            so.setMultipleAllowed(true);
            sec.addOccurrence(so);
            te.setSection(sec);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            SecOccInstance soi = so.generateInstance();
            docInst.addSecOccInstance(soi);
            BasicResponse teResp = te.generateInstance(soi);
            ITextValue tv = te.generateValue();
            String value = "Foo bar";
            tv.setValue(value);
            teResp.setValue(tv);
            docInst.addResponse(teResp);

            teResp = (BasicResponse)docInst.getResponse(te, soi);
            
            AssertJUnit.assertEquals("Retrieved response has the wrong value", value, ((ITextValue)teResp.getValue()).getValue());
    }
    
    @Test()
	public void testGetResponse_SecOccInst_Null(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            TextEntry te = factory.createTextEntry("TE");
            doc.addEntry(te);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            so.setMultipleAllowed(true);
            sec.addOccurrence(so);
            te.setSection(sec);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            SecOccInstance soi = so.generateInstance();
            docInst.addSecOccInstance(soi);

            AssertJUnit.assertNull("No response for given entry and sec occ inst - should have returned null", docInst.getResponse(te, soi));
            
            so.setMultipleAllowed(false);
            Record rec2 = ds.generateInstance();
            DocumentInstance docInst2 = doc.generateInstance(doc.getOccurrence(0));
            rec2.addDocumentInstance(docInst2);
            BasicResponse br = te.generateInstance(so);
            docInst2.addResponse(br);
            
            AssertJUnit.assertNull("No response for given entry and sec occ inst - should have returned null (2)", docInst2.getResponse(te, soi));
    }
    
    @Test()
	public void testNumSecOccInstances(){
            Document doc = factory.createDocument("D");
            DocumentOccurrence docOcc = factory.createDocumentOccurrence("DO");
            doc.addOccurrence(docOcc);
            Section sec = factory.createSection("S");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            so.setMultipleAllowed(true);
            sec.addOccurrence(so);
            doc.addSection(sec);
            
            DocumentInstance di = doc.generateInstance(docOcc);
            SecOccInstance soi1 = so.generateInstance();
            di.addSecOccInstance(soi1);
            SecOccInstance soi2 = so.generateInstance();
            di.addSecOccInstance(soi2);
            SecOccInstance soi3 = so.generateInstance();
            di.addSecOccInstance(soi3);
            
            AssertJUnit.assertEquals("Document instance has the wrong number of sec occ instances", 3, di.numSecOccInstances());
    }
    
    @Test()
	public void testAddSecOccInstance(){
            Document doc = factory.createDocument("D");
            DocumentOccurrence docOcc = factory.createDocumentOccurrence("DO");
            doc.addOccurrence(docOcc);
            Section sec = factory.createSection("S");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            so.setMultipleAllowed(true);
            sec.addOccurrence(so);
            doc.addSection(sec);
            
            DocumentInstance di = doc.generateInstance(docOcc);
            SecOccInstance soi1 = so.generateInstance();
            di.addSecOccInstance(soi1);
            SecOccInstance soi2 = so.generateInstance();
            di.addSecOccInstance(soi2);
            SecOccInstance soi3 = so.generateInstance();
            di.addSecOccInstance(soi3);

            AssertJUnit.assertEquals("Sec occ inst 1 not added correctly", soi1, di.getSecOccInstance(0));
            AssertJUnit.assertEquals("Sec occ inst 2 not added correctly", soi2, di.getSecOccInstance(1));
            AssertJUnit.assertEquals("Sec occ inst 3 not added correctly", soi3, di.getSecOccInstance(2));

            try{
                di.addSecOccInstance(null);
                Assert.fail("Exception should have been thrown when adding a null sec occ inst");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetSecOccInstance(){
            Document doc = factory.createDocument("D");
            DocumentOccurrence docOcc = factory.createDocumentOccurrence("DO");
            doc.addOccurrence(docOcc);
            Section sec = factory.createSection("S");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            so.setMultipleAllowed(true);
            sec.addOccurrence(so);
            doc.addSection(sec);
            
            DocumentInstance di = doc.generateInstance(docOcc);
            SecOccInstance soi1 = so.generateInstance();
            di.addSecOccInstance(soi1);
            SecOccInstance soi2 = so.generateInstance();
            di.addSecOccInstance(soi2);
            SecOccInstance soi3 = so.generateInstance();
            di.addSecOccInstance(soi3);

            AssertJUnit.assertEquals("Sec occ inst 1 not added correctly", soi1, di.getSecOccInstance(0));
            AssertJUnit.assertEquals("Sec occ inst 2 not added correctly", soi2, di.getSecOccInstance(1));
            AssertJUnit.assertEquals("Sec occ inst 3 not added correctly", soi3, di.getSecOccInstance(2));

            try{
                di.getSecOccInstance(-1);
                Assert.fail("Exception should have been thrown when trying to get sec occ inst using invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                di.getSecOccInstance(3);
                Assert.fail("Exception should have been thrown when trying to get sec occ inst using invalid index (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetSecOccInstances(){
            Document doc = factory.createDocument("D");
            DocumentOccurrence docOcc = factory.createDocumentOccurrence("DO");
            doc.addOccurrence(docOcc);
            Section sec = factory.createSection("S");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            so.setMultipleAllowed(true);
            sec.addOccurrence(so);
            SectionOccurrence so2 = factory.createSectionOccurrence("SO2");
            so2.setMultipleAllowed(true);
            sec.addOccurrence(so2);
            doc.addSection(sec);
            
            DocumentInstance di = doc.generateInstance(docOcc);
            SecOccInstance soi1_1 = so.generateInstance();
            di.addSecOccInstance(soi1_1);
            SecOccInstance soi2_1 = so2.generateInstance();
            di.addSecOccInstance(soi2_1);
            SecOccInstance soi1_2 = so.generateInstance();
            di.addSecOccInstance(soi1_2);
            SecOccInstance soi1_3 = so.generateInstance();
            di.addSecOccInstance(soi1_3);
            SecOccInstance soi2_2 = so2.generateInstance();
            di.addSecOccInstance(soi2_2);
            
            List<SecOccInstance> so1Insts = di.getSecOccInstances(so);
            AssertJUnit.assertEquals("List of instances for sec occ 1 has the wrong number of items",3,so1Insts.size());
            AssertJUnit.assertEquals("List of instances for sec occ 1 has the wrong item at index 0",soi1_1,so1Insts.get(0));
            AssertJUnit.assertEquals("List of instances for sec occ 1 has the wrong item at index 1",soi1_2,so1Insts.get(1));
            AssertJUnit.assertEquals("List of instances for sec occ 1 has the wrong item at index 2",soi1_3,so1Insts.get(2));
            List<SecOccInstance> so2Insts = di.getSecOccInstances(so2);
            AssertJUnit.assertEquals("List of instances for sec occ 2 has the wrong number of items",2,so2Insts.size());
            AssertJUnit.assertEquals("List of instances for sec occ 2 has the wrong item at index 0",soi2_1,so2Insts.get(0));
            AssertJUnit.assertEquals("List of instances for sec occ 2 has the wrong item at index 1",soi2_2,so2Insts.get(1));
    }
}
