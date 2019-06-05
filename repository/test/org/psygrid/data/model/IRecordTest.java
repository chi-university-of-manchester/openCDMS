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
import java.util.Set;


public class IRecordTest extends ModelTest {

    @Test()
	public void testAddConsent(){
            DataSet ds = factory.createDataset("DS");
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm pcf1 = factory.createPrimaryConsentForm();
            cfg.addConsentForm(pcf1);
            PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
            cfg.addConsentForm(pcf2);
            
            Record r = ds.generateInstance();
            String loc1 = "Location 1";
            String loc2 = "Location 2";
            {
                Consent c1 = pcf1.generateConsent();
                c1.setLocation(loc1);
                Consent c2 = pcf2.generateConsent();
                c2.setLocation(loc2);
                r.addConsent(c1);
                r.addConsent(c2);
            }
            
            Consent c1 = r.getConsent(pcf1);
            AssertJUnit.assertNotNull("Consent for consent form 1 is null", c1);
            AssertJUnit.assertEquals("Consent for consent form 1 has the wrong location",loc1,c1.getLocation());
            Consent c2 = r.getConsent(pcf2);
            AssertJUnit.assertNotNull("Consent for consent form 2 is null", c2);
            AssertJUnit.assertEquals("Consent for consent form 2 has the wrong location",loc2,c2.getLocation());
    }
    
    @Test()
	public void testGetConsent(){
            DataSet ds = factory.createDataset("DS");
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm pcf1 = factory.createPrimaryConsentForm();
            cfg.addConsentForm(pcf1);
            PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
            cfg.addConsentForm(pcf2);
            
            Record r = ds.generateInstance();
            String loc1 = "Location 1";
            {
                Consent c1 = pcf1.generateConsent();
                c1.setLocation(loc1);
                r.addConsent(c1);
            }
            
            Consent c1 = r.getConsent(pcf1);
            AssertJUnit.assertNotNull("Consent for consent form 1 is null", c1);
            AssertJUnit.assertEquals("Consent for consent form 1 has the wrong location",loc1,c1.getLocation());
            Consent c2 = r.getConsent(pcf2);
            AssertJUnit.assertNull("Consent for consent form 2 is not null", c2);
    }
    
    @Test()
	public void testGenerateIdentifier(){
    		DataSet ds = factory.createDataset("DS");
    		Record r = ds.generateInstance();
    		
    		String id = "ABC/XYZ-000123";
    		r.generateIdentifier(id);
    		
    		AssertJUnit.assertNotNull("Record has a null identifier", r.getIdentifier());
    		AssertJUnit.assertEquals("Record has the incorrect identifier", id, r.getIdentifier().getIdentifier());
    }
    
    @Test()
	public void testAddDocumentInstance_OK(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            
            AssertJUnit.assertEquals("Record has the wrong number of document instances",1,rec.getDocumentInstances(doc).size());
    }
    
    @Test()
	public void testAddDocumentInstance_Invalid(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            try{
                Document doc2 = factory.createDocument("Doc 2");
                DocumentOccurrence occ2 = factory.createDocumentOccurrence("Occ2");
                doc2.addOccurrence(occ2);
                DocumentInstance di2 = doc2.generateInstance(doc2.getOccurrence(0));
                rec.addDocumentInstance(di2);
                Assert.fail("Exception should have been thrown when trying to add an invalid document instance");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testAddDocumentInstance_Duplicate(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            try{
                rec.addDocumentInstance(docInst);
                Assert.fail("Exception should have been thrown when trying to add a duplicate document instance (same object)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            DocumentInstance docInst2 = doc.generateInstance(doc.getOccurrence(0));
            try{
                rec.addDocumentInstance(docInst2);
                Assert.fail("Exception should have been thrown when trying to add a duplicate document instance (different object)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetDocumentInstance_OK(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            
            Record rec = ds.generateInstance();
            DocumentInstance docInst = doc.generateInstance(doc.getOccurrence(0));
            rec.addDocumentInstance(docInst);
            
            AssertJUnit.assertTrue("Returned document instance is not correct",docInst == rec.getDocumentInstance(occ));
    }
    
    @Test()
	public void testGetDocumentInstance_Null(){
            DataSet ds = factory.createDataset("DS");
            Document doc = factory.createDocument("Doc");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            
            Record rec = ds.generateInstance();
            
            AssertJUnit.assertNull("No document instance exists for given document occurrence - should have returned null",rec.getDocumentInstance(occ));
    }
    
    @Test()
	public void testGetAllConsents(){
            DataSet ds = factory.createDataset("DS");
            ConsentFormGroup cfg1 = factory.createConsentFormGroup();
            ds.addAllConsentFormGroup(cfg1);
            PrimaryConsentForm pcf1 = factory.createPrimaryConsentForm();
            pcf1.setQuestion("PCF1");
            cfg1.addConsentForm(pcf1);
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            acf1.setQuestion("ACF1");
            pcf1.addAssociatedConsentForm(acf1);
            ConsentFormGroup cfg2 = factory.createConsentFormGroup();
            ds.addAllConsentFormGroup(cfg2);
            PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
            pcf2.setQuestion("PCF2");
            cfg2.addConsentForm(pcf2);
            
            Record rec = ds.generateInstance();
            rec.addConsent(pcf1.generateConsent());
            rec.addConsent(acf1.generateConsent());
            
            Set<Consent> cs = rec.getAllConsents();
            
            AssertJUnit.assertEquals("Wrong number of consents",2,cs.size());
            for ( Consent c: cs){
                AssertJUnit.assertNotNull("Consent form is null",c.getConsentForm());
            }
    }
    
}
