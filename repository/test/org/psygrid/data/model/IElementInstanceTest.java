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

public class IElementInstanceTest extends ModelTest {

    @Test()
	public void testGetRecord(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            Section sec = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            doc.addSection(sec);
            TextEntry te = factory.createTextEntry("TE");
            te.setSection(sec);
            doc.addEntry(te);
            
            Record recInst = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            recInst.addDocumentInstance(docInst);
            AssertJUnit.assertEquals("Document instance does not correctly reference its record",recInst,docInst.getRecord());
            BasicResponse teResp = te.generateInstance(so);
            docInst.addResponse(teResp);
            AssertJUnit.assertEquals("Text Response does not correctly reference its record",recInst,teResp.getRecord());
    }
       
    @Test()
	public void testGetProvenance(){
            List<Provenance> provList = null;
            Provenance p1 = null;
            Provenance p2 = null;
            Provenance p3 = null;
            
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            TextEntry te = factory.createTextEntry("TE");
            te.setSection(sec);
            BasicResponse resp = te.generateInstance(so);
            AssertJUnit.assertEquals("Response has the wrong number of provenance items (should be 0)",0,resp.getProvenance().size());

            ITextValue tv = te.generateValue();
            tv.setValue("Foo bar");
            resp.setValue(tv);
            provList = resp.getProvenance();
            AssertJUnit.assertEquals("Response has the wrong number of provenance items (should be 1)",1,provList.size());
            p1 = provList.get(0);
            AssertJUnit.assertNull("Provenance previous value is not null", p1.getPrevValue());
            AssertJUnit.assertEquals("Provenance current value is not correct", tv, p1.getCurrentValue());
            AssertJUnit.assertEquals("Provenance action is not correct", Provenance.ACTION_ADD, p1.getAction());
            
            ITextValue tv2 = te.generateValue();
            tv2.setValue("Bar foo");
            resp.setValue(tv2);
            provList = resp.getProvenance();
            AssertJUnit.assertEquals("Response has the wrong number of provenance items (should be 2)",2,provList.size());
            p1 = provList.get(0);
            AssertJUnit.assertNull("Provenance previous value is not null", p1.getPrevValue());
            AssertJUnit.assertEquals("Provenance current value is not correct", tv, p1.getCurrentValue());
            AssertJUnit.assertEquals("Provenance action is not correct", Provenance.ACTION_ADD, p1.getAction());
            p2 = provList.get(1);
            AssertJUnit.assertEquals("Provenance previous value is not correct", tv, p2.getPrevValue());
            AssertJUnit.assertEquals("Provenance current value is not correct", tv2, p2.getCurrentValue());
            AssertJUnit.assertEquals("Provenance action is not correct", Provenance.ACTION_EDIT, p2.getAction());
            AssertJUnit.assertTrue("Date of 2nd provenance item is not after that of the 1st", !p2.getTimestamp().before(p1.getTimestamp()));
            
            IValue tv3 = null;
            resp.setValue(tv3);
            provList = resp.getProvenance();
            AssertJUnit.assertEquals("Response has the wrong number of provenance items (should be 3)",3,provList.size());
            p1 = provList.get(0);
            AssertJUnit.assertNull("Provenance previous value is not null", p1.getPrevValue());
            AssertJUnit.assertEquals("Provenance current value is not correct", tv, p1.getCurrentValue());
            AssertJUnit.assertEquals("Provenance action is not correct", Provenance.ACTION_ADD, p1.getAction());
            p2 = provList.get(1);
            AssertJUnit.assertEquals("Provenance previous value is not correct", tv, p2.getPrevValue());
            AssertJUnit.assertEquals("Provenance current value is not correct", tv2, p2.getCurrentValue());
            AssertJUnit.assertEquals("Provenance action is not correct", Provenance.ACTION_EDIT, p2.getAction());
            AssertJUnit.assertTrue("Date of 2nd provenance item is not after that of the 1st", !p2.getTimestamp().before(p1.getTimestamp()));
            p3 = provList.get(2);
            AssertJUnit.assertEquals("Provenance previous value is not correct", tv2, p3.getPrevValue());
            AssertJUnit.assertNull("Provenance current value is not null", p3.getCurrentValue());
            AssertJUnit.assertEquals("Provenance action is not correct", Provenance.ACTION_EDIT, p2.getAction());
            //Note that as Date objects only resolve to the nearest second we can only
            //test that a provenance item is not before the item preceding it in the list,
            //rather than actually testing that it is after.
            AssertJUnit.assertTrue("Date of 2nd provenance item is not after that of the 1st", !p2.getTimestamp().before(p1.getTimestamp()));
            AssertJUnit.assertTrue("Date of 3rd provenance item is not after that of the 2nd", !p3.getTimestamp().before(p2.getTimestamp()));
            
            //text that the values can't be modified
            ITextValue v1 = (ITextValue)p2.getCurrentValue();
            try{
                v1.setValue("bleerrghh");
                Assert.fail("Exception should have been thrown when trying to modify the value of a read-only value");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            //test that provenance list can't be modified
            try{
                provList.remove(0);
            }
            catch(Exception ex){
                //do nothing
            }
            List<Provenance> provList2 = resp.getProvenance();
            AssertJUnit.assertEquals("Response has the wrong number of provenance items (should be 3)",3,provList2.size());
    }
    
    @Test()
	public void testGetProvenance2(){
            Section s = factory.createSection("Sec");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            s.addOccurrence(so);
            TextEntry te = factory.createTextEntry("TE");
            te.setSection(s);
            
            BasicResponse resp = te.generateInstance(so);
            ITextValue tv1 = te.generateValue();
            tv1.setValue("Val 1");
            resp.setValue(tv1);
            AssertJUnit.assertEquals("Response has the wrong number of provenance items (1)",1,resp.getProvenance().size());
            
            ITextValue tv2 = te.generateValue();
            tv2.setValue("Val 2");
            resp.setValue(tv2);
            ITextValue tv3 = te.generateValue();
            tv3.setValue("Val 3");
            resp.setValue(tv3);
            ITextValue tv4 = te.generateValue();
            tv4.setValue("Val 4");
            resp.setValue(tv4);
            
            AssertJUnit.assertEquals("Response has the wrong number of provenance items (2)",4,resp.getProvenance().size());
            AssertJUnit.assertEquals("Response has the wrong number of Status provenance items",0,resp.getProvenance(Status.class).size());
            AssertJUnit.assertEquals("Response has the wrong number of Value provenance items",4,resp.getProvenance(IValue.class).size());
    }
}
