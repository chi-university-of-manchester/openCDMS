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

package org.psygrid.data.client;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.data.dao.DAOTestHelper;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.IPersistent;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.dto.extra.ConsentResult;
import org.psygrid.data.model.dto.extra.ConsentStatusResult;
import org.psygrid.data.model.dto.extra.StatusResult;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RepositoryClientTest extends TestCase {
    
    private RepositoryDAO dao;
    private org.psygrid.data.model.hibernate.Factory factory;
    private org.psygrid.data.reporting.definition.Factory reportsFactory;
    
    protected ApplicationContext ctx = null;
    
    public RepositoryClientTest() {
        String[] paths = {"applicationContext.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
    }
        
    protected void setUp() throws Exception {
        super.setUp();
        dao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
        factory = (Factory) ctx.getBean("factory");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
        factory = null;
    }

    public void testGetVersion(){
        try{
            RepositoryClient client = new RepositoryClient();
            System.out.println(client.getVersion());
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testGetDataSetComplete(){
        try{
            //add a dataset
            String dsName = "testGetDataSetComplete - "+(new Date()).toString();
            String docName = "Doc 1";
            String secName = "Sec 1";
            String teName = "TE 1";
            String boDesc = "BO 1";
            String boFileName = "test-cf1.pdf";
            String boMimeType = "application/pdf";
            String cfDesc = "CF 1";
            DataSet ds = factory.createDataset(dsName);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());

            Document doc = factory.createDocument(docName);
            ds.addDocument(doc);
            Section sec = factory.createSection(secName);
            doc.addSection(sec);
            Entry te = factory.createTextEntry(teName);
            te.setSection(sec);
            doc.addEntry(te);

            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            BinaryObject bo = factory.createBinaryObject(read2Array("test/test-cf1.pdf"));
            bo.setFileName(boFileName);
            bo.setMimeType(boMimeType);
            bo.setDescription(boDesc);
            cf.setQuestion(cfDesc);
            cf.setElectronicDocument(bo);
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            ds.addAllConsentFormGroup(cfg);
            cfg.addConsentForm(cf);
            doc.addConsentFormGroup(cfg);
            
            //save the dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            
            //use web-service to get the dataset
            RepositoryClient client = new RepositoryClient();
            DataSet wsDs = client.getDataSet(dsId, null);
            
            assertEquals("DataSet name is wrong",dsName,wsDs.getName());
            Document wsDoc = (Document)wsDs.getDocument(0);
            assertEquals("Doc name is wrong",docName,wsDoc.getName());
            Section wsSec = wsDoc.getSection(0);
            assertEquals("Sec name is wrong",secName,wsSec.getName());
            Element wsTe = wsDoc.getEntry(0);
            assertEquals("Entry name is wrong",teName,wsTe.getName());
            ConsentFormGroup wsCfg = wsDoc.getConsentFormGroup(0);
            ConsentForm wsCf = wsCfg.getConsentForm(0);
            assertEquals("Consent form description is wrong",cfDesc,wsCf.getQuestion());
            BinaryObject wsBo = wsCf.getElectronicDocument();
            assertEquals("Binary object file name is wrong",boFileName,wsBo.getFileName());
            assertEquals("Binary object description is wrong",boDesc,wsBo.getDescription());
            assertEquals("Binary object mime-type is wrong",boMimeType,wsBo.getMimeType());
            assertNull("Binary object has non-null data",wsBo.getData());
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }

    public void testGetBinaryData(){
        try{
            //add a dataset
            String name = "testGetBinaryData - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            byte[] byteData = read2Array("test/test-info-doc.pdf");
            BinaryObject bo = factory.createBinaryObject(byteData);
            bo.setFileName("test-info-doc.pdf");
            bo.setMimeType("application/pdf");
            bo.setDescription("Test info doc");
            ds.setInfoSheet(bo);
            
            //save the dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            //use web-service to get the binary data
            RepositoryClient client = new RepositoryClient();
            byte[] result = client.getBinaryData(dsId, ds.getInfoSheet().getId(), null);

            //Can only check that the length of the data is correct, as 
            //different encoding schemes are used (I think)
            assertEquals("Binary data is not correct",byteData.length,result.length);
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }

    public void testGetModifiedDataSets(){
        try{
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, -1);
            
            //add three DataSets
            String name1 = "testGetModifiedDatasets - 1 - "+(new Date()).toString();
            DataSet ds1 = factory.createDataset(name1);
            //generate unique project code
            java.rmi.dgc.VMID guid1 = new java.rmi.dgc.VMID();
            ds1.setProjectCode(guid1.toString());
            
            String name2 = "testGetModifiedDatasets - 2 - "+(new Date()).toString();
            DataSet ds2 = factory.createDataset(name2);
            //generate unique project code
            java.rmi.dgc.VMID guid2 = new java.rmi.dgc.VMID();
            ds2.setProjectCode(guid2.toString());
    
            String name3 = "testGetModifiedDatasets - 3 - "+(new Date()).toString();
            DataSet ds3 = factory.createDataset(name3);
            //generate unique project code
            java.rmi.dgc.VMID guid3 = new java.rmi.dgc.VMID();
            ds3.setProjectCode(guid3.toString());
            
            dao.saveDataSet(ds1.toDTO());
            dao.saveDataSet(ds2.toDTO());
            dao.saveDataSet(ds3.toDTO());
            
            //use web-service to get the modified datasets
            RepositoryClient client = new RepositoryClient();
            List<DataSet> dataSets = client.getModifiedDataSets(cal.getTime(), null);
            
            assertTrue("Number of modified data sets is not at least 3",dataSets.size()>=3);
            
            for ( DataSet d:dataSets){
                assertNotNull("A data set in the list does not have an id", d.getId());
                assertNotNull("A data set in the list does not have a name", d.getName());
                assertTrue("A data set in the list has a last modified date not after the reference date",d.getDateModified().after(cal.getTime()));
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testSaveRecord(){
        try{
            //create dataset
            Long dsId = null;
            {
                DataSet ds = factory.createDataset("DS");
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                ds.setProjectCode(guid.toString());
                Group grp1 = factory.createGroup("FOO");
                ds.addGroup(grp1);

                Document doc1 = factory.createDocument("Doc 1");
                DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ 1");
                doc1.addOccurrence(occ1);
                ds.addDocument(doc1);
                Section sec1 = factory.createSection("Sec 1");
                SectionOccurrence so1 = factory.createSectionOccurrence("Default");
                so1.setMultipleAllowed(true);
                sec1.addOccurrence(so1);
                doc1.addSection(sec1);
                NumericEntry ne1 = factory.createNumericEntry("NE1");
                ne1.setSection(sec1);
                doc1.addEntry(ne1);
                OptionEntry oe1 = factory.createOptionEntry("OE1");
                oe1.setSection(sec1);
                Option o1 = factory.createOption("O1", "O1");
                Option o2 = factory.createOption("O2", "O2");
                Option o3 = factory.createOption("O3", "O3");
                Option o4 = factory.createOption("O4", "O4");
                oe1.addOption(o1);
                oe1.addOption(o2);
                oe1.addOption(o3);
                oe1.addOption(o4);
                doc1.addEntry(oe1);            
                PrimaryConsentForm cf = factory.createPrimaryConsentForm();
                ConsentFormGroup cfg = factory.createConsentFormGroup();
                ds.addAllConsentFormGroup(cfg);
                cfg.addConsentForm(cf);
                doc1.addConsentFormGroup(cfg);
            
                dsId = dao.saveDataSet(ds.toDTO());
                dao.publishDataSet(dsId);
            }
            DataSet ds = dao.getDataSet(dsId).toHibernate();
            
            //generate identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 1, null);
            
            //create a record from the dataset
            ConsentForm cf = null;
            Document doc1 = null;
            DocumentOccurrence occ1 = null;
            Section sec1 = null;
            SectionOccurrence soc1 = null;
            NumericEntry ne1 = null;
            OptionEntry oe1 = null;
            Option o2 = null;
            {
                Record r = ds.generateInstance();
                r.setIdentifier(ids.get(0));
                doc1 = ds.getDocument(0);
                occ1 = doc1.getOccurrence(0);
                sec1 = doc1.getSection(0);
                soc1 = sec1.getOccurrence(0);
                DocumentInstance di1 = doc1.generateInstance(doc1.getOccurrence(0));
                r.addDocumentInstance(di1);
                SecOccInstance soi1 = soc1.generateInstance();
                di1.addSecOccInstance(soi1);
                ConsentFormGroup cfg = doc1.getConsentFormGroup(0);
                cf = cfg.getConsentForm(0);
                Consent c = cf.generateConsent();
                c.setConsentGiven(true);
                r.addConsent(c);
                ne1 = (NumericEntry)doc1.getEntry(0);
                BasicResponse nr1 = ne1.generateInstance(soi1);
                INumericValue nv1 = (INumericValue)ne1.generateValue();
                nv1.setValue(2.345);
                nr1.setValue(nv1);
                di1.addResponse(nr1);
                oe1 = (OptionEntry)doc1.getEntry(1);
                BasicResponse or1 = oe1.generateInstance(soi1);
                IOptionValue ov1 = (IOptionValue)oe1.generateValue();
                o2 = oe1.getOption(1);
                ov1.setValue(o2);
                or1.setValue(ov1);
                di1.addResponse(or1);
                
                client.saveRecord(r, true, "NoUser");
            }
            
            //get all records for the dataset
            List<Record> records = client.getRecords(dsId, null);
            assertEquals("List of records for the dataset does not contain 1 item",1,records.size());
            
            //get an individual record
            Record r = client.getRecord(ds, records.get(0).getId(), null);
            
            //check the content of the record.
            assertEquals("Record is not associated with the correct dataset",ds,r.getDataSet());
            DocumentInstance di1 = r.getDocumentInstance(occ1);
            assertNotNull("Instance of document 1 is null",di1);
            assertEquals("Instance of document 1 does not reference document occurrence 1",occ1,di1.getOccurrence());
            SecOccInstance soi1 = di1.getSecOccInstance(0);
            assertNotNull("Sec occ inst 1 is null",soi1);
            assertEquals("Sec occ inst 1 does not reference section occurrence 1",soc1,soi1.getSectionOccurrence());
            Consent c = r.getConsent(doc1.getConsentFormGroup(0).getConsentForm(0));
            assertNotNull("Consent for dataset level consent form is null",c);
            assertTrue("Consent does not have consent given = true",c.isConsentGiven());
            BasicResponse nr1 = (BasicResponse)di1.getResponse(ne1, soi1);
            assertNotNull("Response to numeric entry 1 is null",nr1);
            assertEquals("Response to numeric entry 1 does not reference numeric entry 1",ne1,nr1.getEntry());
            assertEquals("Response to numeric entry 1 does not reference sec occ inst 1",soi1,nr1.getSecOccInstance());
            INumericValue nv1 = (INumericValue)nr1.getValue();
            assertNotNull("Value of response to numeric entry 1 is null",nv1);
            assertEquals("Value of response to numeric entry 1 does not have the correct value",2.345,nv1.getValue());     
            List<Provenance> nrpl1 = nr1.getProvenance();
            assertEquals("Response to numeric entry does not have one provenance item",1,nrpl1.size());
            Provenance nrp1 = nrpl1.get(0);
            assertEquals("Response to numeric entry provenance item does not reference the correct current value",nv1,nrp1.getCurrentValue());
            BasicResponse or1 = (BasicResponse)di1.getResponse(oe1,soi1);
            assertNotNull("Response to option entry 1 is null",or1);
            assertEquals("Response to option entry 1 does not reference option entry 1",oe1,or1.getEntry());
            assertEquals("Response to option entry 1 does not reference sec occ inst 1",soi1,or1.getSecOccInstance());
            IOptionValue ov1 = (IOptionValue)or1.getValue();
            assertNotNull("Value of response to option entry 1 is null",ov1);
            assertEquals("Value of response to option entry 1 does not reference the correct option",o2,ov1.getValue());
            List<Provenance> orpl1 = or1.getProvenance();
            assertEquals("Response to numeric entry does not have one provenance item",1,orpl1.size());
            Provenance orp1 = orpl1.get(0);
            assertEquals("Response to numeric entry provenance item does not reference the correct current value",ov1,orp1.getCurrentValue());
       }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testSaveRecord_Filtered(){
        try{
            //1. Create a dataset that has an entry with a transformer
            String name = "testTransform - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);

            Transformer t = factory.createTransformer();
            t.setWsUrl("http://localhost:8080/transformers/services/sha1transformer");
            t.setWsNamespace("urn:transformers.psygrid.org");
            t.setWsOperation("encrypt");
            t.setResultClass("org.psygrid.data.model.hibernate.TextValue");
            ds.addTransformer(t);
            Document doc = factory.createDocument("D1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ1);
            ds.addDocument(doc);
            Section sec = factory.createSection("S1");
            SectionOccurrence soc = factory.createSectionOccurrence("Default");
            sec.addOccurrence(soc);
            doc.addSection(sec);
            TextEntry te1 = factory.createTextEntry("TE1");
            te1.addTransformer(t);
            te1.setSection(sec);
            doc.addEntry(te1);
            
            //save the dataset and re-load it
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //generate identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 1, null);
            
            //create a record from the dataset
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids.get(0));
            DocumentInstance di = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            rec.addDocumentInstance(di);
            te1 = (TextEntry)ds.getDocument(0).getEntry(0);
            BasicResponse tr1 = te1.generateInstance(ds.getDocument(0).getSection(0).getOccurrence(0));
            ITextValue tv1 = (ITextValue)te1.generateValue();
            String value = "Foo Bar";
            tv1.setValue(value);
            tr1.setValue(tv1);
            di.addResponse(tr1);
            
            //save the record - the transformer should be invoked
            client.saveRecord(rec, true, "NoUser");
            
            //get all records for the dataset
            List<Record> records = client.getRecords(dsId, null);
            assertEquals("List of records for the dataset does not contain 1 item",1,records.size());
            
            //get an individual record
            Record r = client.getRecord(ds, records.get(0).getId(), null);
            
            di = r.getDocumentInstance(ds.getDocument(0).getOccurrence(0));
            tr1 = (BasicResponse)di.getResponse(ds.getDocument(0).getEntry(0), ds.getDocument(0).getSection(0).getOccurrence(0));
            tv1 = (ITextValue)tr1.getValue();

            assertTrue("Value does not have transformed flag set to true", tv1.isTransformed());
            assertFalse("Value is the same as it was before transformation", value.equals(tv1.getValue()));
            System.out.println(tv1.getValue());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testGenerateIdentifiers(){
        try{
            Long id = null;
            int suffixSize = 6;
            String projectCode = null;
            {
                String name1 = "testGenerateIdentifiers - "+(new Date()).toString();
                DataSet ds = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds.setProjectCode(projectCode);
                ds.setIdSuffixSize(suffixSize);
                Group grp1 = factory.createGroup("GRP1");
                Group grp2 = factory.createGroup("GRP2");
                Group grp3 = factory.createGroup("GRP3");
                ds.addGroup(grp1);
                ds.addGroup(grp2);
                ds.addGroup(grp3);
                id = dao.saveDataSet(ds.toDTO());
            }
            
            RepositoryClient client = new RepositoryClient();
            
            int number = 5;
            String groupCode = "GRP1";
            List<Identifier> ids = client.generateIdentifiers(id, groupCode, number, null);
            
            assertEquals("Array of identifiers is the wrong size",number,ids.size());
            int lastSuffix = 0;
            for ( int i=0; i<number; i++){
                Identifier iid = ids.get(i);
                assertNotNull("Identifier is null", iid);
                assertNotNull("Identifier overall identifier is null", iid.getIdentifier());
                assertEquals("Identifier has the wrong project prefix", projectCode, iid.getProjectPrefix());
                assertEquals("Identifier has the wrong group prefix", groupCode, iid.getGroupPrefix());
                if ( i > 0){
                    assertEquals("The suffix of the identifier at index "+i+" is not one greater than the suffix of the previous identifier",lastSuffix+1,iid.getSuffix());
                }
                lastSuffix = iid.getSuffix();
            }
            
            
            //generate a 2nd batch
            number = 10;
            ids = client.generateIdentifiers(id, groupCode, number, null);
            
            assertEquals("Array of identifiers is the wrong size",number,ids.size());
            for ( int i=0; i<number; i++){
                Identifier iid = ids.get(i);
                assertNotNull("Identifier is null", iid);
                assertNotNull("Identifier overall identifier is null", iid.getIdentifier());
                assertEquals("Identifier has the wrong project prefix", projectCode, iid.getProjectPrefix());
                assertEquals("Identifier has the wrong group prefix", groupCode, iid.getGroupPrefix());
                assertEquals("The suffix of the identifier at index "+i+" is not one greater than the suffix of the previous identifier",lastSuffix+1,iid.getSuffix());
                lastSuffix = iid.getSuffix();
            }
                        
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    
    public void testGetStandardCodes(){
        try{
            StandardCode sc1 = generateUniqueCode();
            dao.saveStandardCode(sc1.toDTO());

            RepositoryClient client = new RepositoryClient();
            List<StandardCode> codes = client.getStandardCodes(null);
            assertTrue("Standard codes list contains no items",codes.size() > 0);
            for(StandardCode code:codes){
                assertNotNull("A standard code in the list is null", code);
                assertNotNull("A standard code has a null description", code.getDescription());
                assertTrue("A standard code has a code not greater than zero", code.getCode() > 0);
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    
    public void testSaveDataset(){
        try{
            DataSet ds = factory.createDataset("DS");
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Document doc1 = factory.createDocument("Doc 1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ 1");
            doc1.addOccurrence(occ1);
            ds.addDocument(doc1);
            Section sec1 = factory.createSection("Sec 1");
            SectionOccurrence soc1 = factory.createSectionOccurrence("Default");
            sec1.addOccurrence(soc1);
            doc1.addSection(sec1);
            NumericEntry ne1 = factory.createNumericEntry("NE1");
            ne1.setSection(sec1);
            doc1.addEntry(ne1);
            OptionEntry oe1 = factory.createOptionEntry("OE1");
            oe1.setSection(sec1);
            Option o1 = factory.createOption("O1", "O1");
            Option o2 = factory.createOption("O2", "O2");
            Option o3 = factory.createOption("O3", "O3");
            Option o4 = factory.createOption("O4", "O4");
            oe1.addOption(o1);
            oe1.addOption(o2);
            oe1.addOption(o3);
            oe1.addOption(o4);
            doc1.addEntry(oe1);            
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            ds.addAllConsentFormGroup(cfg);
            cfg.addConsentForm(cf);
            doc1.addConsentFormGroup(cfg);
        
            DerivedEntry de1 = factory.createDerivedEntry("DE1");
            de1.setSection(sec1);
            de1.addVariable("x", ne1);
            de1.addVariableDefault("x", new NumericValue(Double.valueOf(0)));
            de1.setFormula("x*2");
            doc1.addEntry(de1);
            
            RepositoryClient client = new RepositoryClient();
            long dsId = client.saveDataSet(ds, null);
            
            assertTrue("Dataset id is not greater than zero", dsId > 0);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testGetRecords(){
        try{
            //create a dataset
            String name = "testGetRecords - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);

            Document doc = factory.createDocument("Doc 1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ1);
            ds.addDocument(doc);
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            doc = (Document)ds.getDocument(0);
            
            //get identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 3, null);

            //generate and save three records
            List<Long> recordIds = new ArrayList<Long>();
            for (int i=0; i<3; i++){
                Record r = ds.generateInstance();
                r.setIdentifier(ids.get(i));
                DocumentInstance di = doc.generateInstance(doc.getOccurrence(0));
                r.addDocumentInstance(di);
                recordIds.add(dao.saveRecord(r, true, null, "NoUser"));
            }
            
            List<Record> records = client.getRecords(dsId, null);
            assertEquals("List of records contains the wrong number of items",3,records.size());
            for(Record r:records){
                assertTrue("List of record ids does not contain record with id = "+r.getId(),recordIds.contains(r.getId()));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testGetRecordsByStatus(){
        try{
            //create a dataset
            String name = "testGetRecordsByStatus - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);

            Status stat1 = factory.createStatus("Status 1", 1);
            Status stat2 = factory.createStatus("Status 2", 1);
            Status stat3 = factory.createStatus("Status 3", 1);
            stat1.addStatusTransition(stat2);
            stat1.addStatusTransition(stat3);
            ds.addStatus(stat1);
            ds.addStatus(stat2);
            ds.addStatus(stat3);
            Document doc = factory.createDocument("Doc 1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ1);
            ds.addDocument(doc);
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            doc = (Document)ds.getDocument(0);
            
            //get identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 6, null);

            //generate and save six records
            List<Long> recordIdsStat1 = new ArrayList<Long>();
            List<Long> recordIdsStat2 = new ArrayList<Long>();
            List<Long> recordIdsStat3 = new ArrayList<Long>();
            for (int i=0; i<6; i++){
                Record r = ds.generateInstance();
                r.setIdentifier(ids.get(i));
                DocumentInstance di = doc.generateInstance(doc.getOccurrence(0));
                r.addDocumentInstance(di);
                //set status and save
                if ( i==0 ){
                    recordIdsStat1.add(dao.saveRecord(r, true, null, "NoUser"));                    
                }
                if ( i>0 && i<3 ){
                    Long rId = dao.saveRecord(r, true, null, "NoUser");
                    dao.changeStatus(rId, ds.getStatus(1).getId(), "NoUser");
                    recordIdsStat2.add(rId);                    
                }
                else if (i >= 3 ){
                    Long rId = dao.saveRecord(r, true, null, "NoUser");
                    dao.changeStatus(rId, ds.getStatus(2).getId(), "NoUser");
                    recordIdsStat3.add(rId);                    
                }
            }
            
            //Test for "Status 1" records
            {
                List<Record> records = client.getRecordsByStatus(dsId, ds.getStatus(0).getId(), null);
                assertEquals("List of records contains the wrong number of items",1,records.size());
                for(Record r:records){
                    assertTrue("List of record ids does not contain record with id = "+r.getId(),recordIdsStat1.contains(r.getId()));
                }
            }
            
            //Test for "Status 2" records
            {
                List<Record> records = client.getRecordsByStatus(dsId, ds.getStatus(1).getId(), null);
                assertEquals("List of records contains the wrong number of items",2,records.size());
                for(Record r:records){
                    assertTrue("List of record ids does not contain record with id = "+r.getId(),recordIdsStat2.contains(r.getId()));
                }
            }
            
            //Test for "Status 3" records
            {
                List<Record> records = client.getRecordsByStatus(dsId, ds.getStatus(2).getId(), null);
                assertEquals("List of records contains the wrong number of items",3,records.size());
                for(Record r:records){
                    assertTrue("List of record ids does not contain record with id = "+r.getId(),recordIdsStat3.contains(r.getId()));
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testGetRecord(){
        try{
            String name = "testGetRecordComplete_Success - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            {
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
                ds.setProjectCode(projectCode);
                Group grp1 = factory.createGroup("FOO");
                ds.addGroup(grp1);
                
                //units
                Unit u1 = factory.createUnit("Unit 1");
                Unit u2 = factory.createUnit("Unit 2");
                ds.addUnit(u1);
                ds.addUnit(u2);
                //document 1
                Document doc1 = factory.createDocument("Document 1");
                DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
                doc1.addOccurrence(occ1);
                ds.addDocument(doc1);
                Section sec1 = factory.createSection("Section 1");
                SectionOccurrence soc1 = factory.createSectionOccurrence("Default");
                sec1.addOccurrence(soc1);
                doc1.addSection(sec1);
                NumericEntry ne1 = factory.createNumericEntry("NE1");
                ne1.setSection(sec1);
                ne1.addUnit(u1);
                ne1.addUnit(u2);
                doc1.addEntry(ne1);
                //document 2
                Document doc2 = factory.createDocument("Document 2");
                DocumentOccurrence occ2 = factory.createDocumentOccurrence("Occ2");
                doc2.addOccurrence(occ2);
                ds.addDocument(doc2);
                Section sec2 = factory.createSection("Section 2");
                SectionOccurrence soc2 = factory.createSectionOccurrence("Default");
                sec2.addOccurrence(soc2);
                doc2.addSection(sec2);
                OptionEntry oe1 = factory.createOptionEntry("OE1");
                oe1.setSection(sec2);
                Option o1 = factory.createOption("Option 1", "Option 1");
                Option o2 = factory.createOption("Option 2", "Option 2");
                oe1.addOption(o1);
                oe1.addOption(o2);
                doc2.addEntry(oe1);
                //document 3
                Document doc3 = factory.createDocument("Document 3");
                DocumentOccurrence occ3 = factory.createDocumentOccurrence("Occ3");
                doc3.addOccurrence(occ3);
                ds.addDocument(doc3);
                Section sec3 = factory.createSection("Section 3");
                SectionOccurrence soc3 = factory.createSectionOccurrence("Default");
                sec3.addOccurrence(soc3);
                doc3.addSection(sec3);
                CompositeEntry comp1 = factory.createComposite("Comp 1");
                comp1.setSection(sec3);
                doc3.addEntry(comp1);
                TextEntry te1 = factory.createTextEntry("TE1");
                comp1.addEntry(te1);
                te1.setSection(sec3);
                TextEntry te2 = factory.createTextEntry("TE2");
                comp1.addEntry(te2);
                te2.setSection(sec3);
                //add a consent form on document 1
                ConsentFormGroup cfg1 = factory.createConsentFormGroup();
                ds.addAllConsentFormGroup(cfg1);
                doc1.addConsentFormGroup(cfg1);
                PrimaryConsentForm pcf1 = factory.createPrimaryConsentForm();
                pcf1.setReferenceNumber("Cons 1");
                cfg1.addConsentForm(pcf1);
                //add a consent form which has one associated consent form, and
                //add a consent form which doesn't require associated consent on document 2
                ConsentFormGroup cfg2 = factory.createConsentFormGroup();
                ds.addAllConsentFormGroup(cfg2);
                doc2.addConsentFormGroup(cfg2);
                PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
                pcf2.setReferenceNumber("Cons 2");
                AssociatedConsentForm acf = factory.createAssociatedConsentForm();
                acf.setReferenceNumber("ACF 1");
                pcf2.addAssociatedConsentForm(acf);
                cfg2.addConsentForm(pcf2);
                PrimaryConsentForm pcf3 = factory.createPrimaryConsentForm();
                pcf3.setReferenceNumber("Cons 3");
                cfg2.addConsentForm(pcf3);
            }
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();

            //get identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 1, null);

            Long recordId = null;
            Record r = ds.generateInstance();
            {
                //Document 1 consent
                Consent cons1 = ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0).generateConsent();
                cons1.setConsentGiven(true);
                r.addConsent(cons1);
                //Document consent
                Consent cons2 = ds.getDocument(1).getConsentFormGroup(0).getConsentForm(1).generateConsent();
                cons2.setConsentGiven(true);
                r.addConsent(cons2);
                 //document 1
                Document d1 = ds.getDocument(0);
                DocumentInstance di1 = d1.generateInstance(d1.getOccurrence(0));
                r.addDocumentInstance(di1);
                BasicEntry ne1 = (BasicEntry)d1.getEntry(0);
                BasicResponse nr1 = ne1.generateInstance(d1.getSection(0).getOccurrence(0));
                INumericValue nv1 = (INumericValue)ne1.generateValue();
                nv1.setValue(2.345);
                nv1.setUnit(ne1.getUnit(1));
                nr1.setValue(nv1);
                di1.addResponse(nr1);
                //document 2
                Document d2 = ds.getDocument(1);
                DocumentInstance di2 = d2.generateInstance(d2.getOccurrence(0));
                r.addDocumentInstance(di2);
                OptionEntry oe1 = (OptionEntry)d2.getEntry(0);
                BasicResponse or1 = oe1.generateInstance(d2.getSection(0).getOccurrence(0));
                IOptionValue ov1 = (IOptionValue)oe1.generateValue();
                ov1.setValue(oe1.getOption(1));
                or1.setValue(ov1);
                di2.addResponse(or1);
                //document 3
                Document d3 = ds.getDocument(2);
                DocumentInstance di3 = d3.generateInstance(d3.getOccurrence(0));
                r.addDocumentInstance(di3);
                CompositeEntry c1 = (CompositeEntry)d3.getEntry(0);
                CompositeResponse ci1 = c1.generateInstance(d3.getSection(0).getOccurrence(0));
                di3.addResponse(ci1);
                BasicEntry te1 = (BasicEntry)c1.getEntry(0);
                BasicResponse tr1 = te1.generateInstance(d3.getSection(0).getOccurrence(0));
                ITextValue tv1 = (ITextValue)te1.generateValue();
                tv1.setValue("Foo");
                tr1.setValue(tv1);
                CompositeRow row1 = ci1.createCompositeRow();
                row1.addResponse(tr1);
                BasicEntry te2 = (BasicEntry)c1.getEntry(1);
                BasicResponse tr2 = te2.generateInstance(d3.getSection(0).getOccurrence(0));
                ITextValue tv2 = (ITextValue)te2.generateValue();
                tv2.setValue("Bar");
                tr2.setValue(tv2);
                row1.addResponse(tr2);
                r.setIdentifier(ids.get(0));
            }
            recordId = dao.saveRecord(r.toDTO(), true, null, "NoUser");
            
            
            r = client.getRecord(ds, recordId, null);
            //check consents
            ConsentForm cf1 = ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0);
            Consent cons1 = r.getConsent(cf1);
            assertNotNull("Consent relating to document 1 is null",cons1);
            assertTrue("Consent relating to document 1 does not have consent given = true",cons1.isConsentGiven());
            assertEquals("Consent relating to document 1 does not correctly reference its consent form",cf1.getReferenceNumber(), cons1.getConsentForm().getReferenceNumber() );
            ConsentForm cf2 = ds.getDocument(1).getConsentFormGroup(0).getConsentForm(1);
            Consent cons2 = r.getConsent(cf2);
            assertNotNull("Consent relating to document 2 is null",cons2);
            assertTrue("Consent relating to document 2 does not have consent given = true",cons2.isConsentGiven());
            assertEquals("Consent relating to document 2 does not correctly reference its consent form",cf2.getReferenceNumber(), cons2.getConsentForm().getReferenceNumber() );
            
            //document 1
            Document d1 = ds.getDocument(0);
            DocumentInstance di1 = r.getDocumentInstance(d1.getOccurrence(0));
            assertNotNull("Document 1 instance is null", di1);
            assertNotNull("Document 1 instance does not correctly reference its Document element",di1.getOccurrence().getName());
            //numeric entry 1
            NumericEntry ne1 = (NumericEntry)d1.getEntry(0);
            BasicResponse nr1 = (BasicResponse)di1.getResponse(ne1, d1.getSection(0).getOccurrence(0));
            assertNotNull("Numeric Response 1 instance is null", nr1);
            assertNotNull("Numeric Response 1 instance does not correctly reference its Numeric entry element",nr1.getEntry().getName());
            INumericValue nv1 = (INumericValue)nr1.getValue();
            assertNotNull("Numeric Value 1 is null", nv1);
            assertNotNull("Numeric Value 1 instance does not have the correct value",nv1.getValue());
            assertNotNull("Numeric Value 1 instance does not correctly reference its unit",nv1.getUnit().getAbbreviation());
            //check provenance
            List<Provenance> nrpl1 = nr1.getProvenance();
            assertNotNull("Numeric Response 1 provenance list is null", nrpl1);
            Provenance nrp1 = nrpl1.get(0);
            assertNotNull("Numeric Response 1 provenance item is null", nrp1);
            assertNotNull("Numeric Response 1 provenance item 1 has a null date", nrp1.getTimestamp());
            assertNotNull("Numeric Response 1 provenance item 1 has an uninitialized value", ((INumericValue)nrp1.getCurrentValue()).getValue());

            //document 2
            Document d2 = ds.getDocument(1);
            DocumentInstance di2 = r.getDocumentInstance(d2.getOccurrence(0));
            assertNotNull("Document 2 instance is null", di2);
            assertNotNull("Document 2 instance does not correctly reference its Document element",di2.getOccurrence().getName());
            //option entry 1
            OptionEntry oe1 = (OptionEntry)d2.getEntry(0);
            BasicResponse or1 = (BasicResponse)di2.getResponse(oe1, d2.getSection(0).getOccurrence(0));
            assertNotNull("Option Response 1 instance is null", or1);
            assertNotNull("Option Response 1 instance does not correctly reference its Option entry element",or1.getEntry().getName());
            IOptionValue ov1 = (IOptionValue)or1.getValue();
            assertNotNull("Option Value 1 is null", ov1);
            assertNotNull("Option Value 1 instance does not correctly reference its option",ov1.getValue().getDisplayText());
            
            //document 3
            Document d3 = ds.getDocument(2);
            DocumentInstance di3 = r.getDocumentInstance(d3.getOccurrence(0));
            assertNotNull("Document 3 instance is null", di3);
            assertNotNull("Document 3 instance does not correctly reference its Document element",di3.getOccurrence().getName());
            //composite 1
            CompositeEntry c1 = (CompositeEntry)d3.getEntry(0);
            CompositeResponse ci1 = (CompositeResponse)di3.getResponse(c1, d3.getSection(0).getOccurrence(0));
            assertNotNull("Composite 1 instance is null", ci1);
            assertNotNull("Composite 1 instance does not correctly reference its Composite element",ci1.getEntry().getName());
            //text entry 1
            TextEntry te1 = (TextEntry)c1.getEntry(0);
            CompositeRow row1 = ci1.getCompositeRow(0);
            BasicResponse tr1 = row1.getResponse(te1);
            assertNotNull("Text Response 1 instance is null", tr1);
            assertNotNull("Text Response 1 instance does not correctly reference its Text entry element",tr1.getEntry().getName());
            ITextValue tv1 = (ITextValue)tr1.getValue();
            assertNotNull("Text Value 1 is null", tv1);
            assertNotNull("Text Value 1 instance does not have the correct value",tv1.getValue());
            //text entry 2
            TextEntry te2 = (TextEntry)c1.getEntry(1);
            BasicResponse tr2 = row1.getResponse(te2);
            assertNotNull("Text Response 2 instance is null", tr2);
            assertNotNull("Text Response 2 instance does not correctly reference its Text entry element",tr2.getEntry().getName());
            ITextValue tv2 = (ITextValue)tr2.getValue();
            assertNotNull("Text Value 1 is null", tv2);
            assertNotNull("Text Value 1 instance does not have the correct value",tv2.getValue());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testPublishDataSet(){
        try{
            DataSet ds = factory.createDataset("DS");
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());

            Long dsId = dao.saveDataSet(ds.toDTO());
            
            RepositoryClient client = new RepositoryClient();
            client.publishDataSet(dsId, null);
            
            ds = client.getDataSet(dsId, null);
            
            assertTrue("DataSet is not marked as published", ds.isPublished());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testWithdrawConsent(){
        try{
            String name = "testWithdrawConsent - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
            pcf.setQuestion("PCF 1");
            cfg.addConsentForm(pcf);
            ds.addAllConsentFormGroup(cfg);
            
            //document 1, governed by the consent form group
            Document d1 = factory.createDocument("D1");
            d1.addConsentFormGroup(cfg);
            DocumentOccurrence do1 = factory.createDocumentOccurrence("DO1");
            d1.addOccurrence(do1);
            Section s1 = factory.createSection("S1");
            SectionOccurrence so1 = factory.createSectionOccurrence("SO1");
            s1.addOccurrence(so1);
            d1.addSection(s1);
            TextEntry te1 = factory.createTextEntry("TE1");
            te1.setSection(s1);
            d1.addEntry(te1);
            ds.addDocument(d1);
            
            //document 2, governed by the consent form group
            Document d2 = factory.createDocument("D2");
            d2.addConsentFormGroup(cfg);
            DocumentOccurrence do2 = factory.createDocumentOccurrence("DO2");
            d2.addOccurrence(do2);
            Section s2 = factory.createSection("S2");
            SectionOccurrence so2 = factory.createSectionOccurrence("SO2");
            s2.addOccurrence(so2);
            d2.addSection(s2);
            TextEntry te2 = factory.createTextEntry("TE2");
            te2.setSection(s2);
            d2.addEntry(te2);
            ds.addDocument(d2);
            
            //document 3, not governed by the consent form group
            Document d3 = factory.createDocument("D3");
            DocumentOccurrence do3 = factory.createDocumentOccurrence("DO3");
            d3.addOccurrence(do3);
            Section s3 = factory.createSection("S3");
            SectionOccurrence so3 = factory.createSectionOccurrence("SO3");
            s3.addOccurrence(so3);
            d3.addSection(s3);
            TextEntry te3 = factory.createTextEntry("TE3");
            te3.setSection(s3);
            d3.addEntry(te3);
            ds.addDocument(d3);
            
            //save and publish the dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 1, null);
            
            //create a record based on the dataset
            Record r = ds.generateInstance();
            r.setIdentifier(ids.get(0));
            Consent c = ds.getAllConsentFormGroup(0).getConsentForm(0).generateConsent();
            c.setConsentGiven(true);
            r.addConsent(c);
            
            d1 = ds.getDocument(0);
            DocumentInstance di1 = d1.generateInstance(d1.getOccurrence(0));
            te1 = (TextEntry)d1.getEntry(0);
            BasicResponse br1 = te1.generateInstance(d1.getSection(0).getOccurrence(0));
            ITextValue tv1 = te1.generateValue();
            tv1.setValue("Foo");
            br1.setValue(tv1);
            di1.addResponse(br1);
            r.addDocumentInstance(di1);
            
            d2 = ds.getDocument(1);
            DocumentInstance di2 = d2.generateInstance(d2.getOccurrence(0));
            te2 = (TextEntry)d2.getEntry(0);
            BasicResponse br2 = te2.generateInstance(d2.getSection(0).getOccurrence(0));
            ITextValue tv2 = te2.generateValue();
            tv2.setValue("Bar");
            br2.setValue(tv2);
            di2.addResponse(br2);
            r.addDocumentInstance(di2);
            
            d3 = ds.getDocument(2);
            DocumentInstance di3 = d3.generateInstance(d3.getOccurrence(0));
            te3 = (TextEntry)d3.getEntry(0);
            BasicResponse br3 = te3.generateInstance(d3.getSection(0).getOccurrence(0));
            ITextValue tv3 = te3.generateValue();
            tv3.setValue("Foo Bar");
            br3.setValue(tv3);
            di3.addResponse(br3);
            r.addDocumentInstance(di3);
            
            //save the record
            Long rId = dao.saveRecord(r, true, null, "NoUser");
            r = dao.getRecord(rId, RetrieveDepth.RS_NO_BINARY).toHibernate();
            
            //make a list of the ids of objects that should be deleted when
            //consent is withdrawn
            List<Long> deletedIds = new ArrayList<Long>();
            di1 = r.getDocumentInstance(d1.getOccurrence(0));
            deletedIds.add(new Long(di1.getId()));
            br1 = (BasicResponse)di1.getResponse(te1, d1.getSection(0).getOccurrence(0));
            deletedIds.add(new Long(br1.getId()));
            tv1 = (ITextValue)br1.getValue();
            deletedIds.add(new Long(tv1.getId()));
            for ( Provenance p: br1.getProvenance() ){
                deletedIds.add(new Long(p.getId()));
            }
            di2 = r.getDocumentInstance(d2.getOccurrence(0));
            deletedIds.add(new Long(di2.getId()));
            br2 = (BasicResponse)di2.getResponse(te2, d2.getSection(0).getOccurrence(0));
            deletedIds.add(new Long(br2.getId()));
            tv2 = (ITextValue)br2.getValue();
            deletedIds.add(new Long(tv2.getId()));
            for ( Provenance p: br2.getProvenance() ){
                deletedIds.add(new Long(p.getId()));
            }
            
            //withdraw consent!
            client.withdrawConsent(rId, ds.getAllConsentFormGroup(0).getConsentForm(0).getId(), "At client's request", null);
            
            //reload the record
            r = dao.getRecord(rId, RetrieveDepth.RS_NO_BINARY).toHibernate();
            
            //check that the record now only contains a document instance for document 3
            assertNull("Record contains an instance for document 1", r.getDocumentInstance(d1.getOccurrence(0)));
            assertNull("Record contains an instance for document 2", r.getDocumentInstance(d2.getOccurrence(0)));
            assertNotNull("Record does not contain an instance for document 3", r.getDocumentInstance(d3.getOccurrence(0)));
            
            //check that all objects in the "to be deleted" list have actually been
            //physically deleted
            for ( Long id: deletedIds ){
                try{
                    IPersistent p = dao.getPersistent(id);
                    fail("Object that should have been deleted still exists in the database: class="+p.getClass()+"; id="+id);
                }
                catch(DAOException ex){
                    //do nothing
                }
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testChangeStatus(){
        try{
            //create a dataset with statuses
            String name = "testChangeStatus_OK - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            
            Status stat1 = factory.createStatus("Status 1", 1);
            Status stat2 = factory.createStatus("Status 2", 2);
            Status stat3 = factory.createStatus("Status 3", 3);
            Status stat4 = factory.createStatus("Status 4", 4);
            stat1.addStatusTransition(stat2);
            stat1.addStatusTransition(stat3);
            stat2.addStatusTransition(stat4);
            stat3.addStatusTransition(stat4);
            ds.addStatus(stat1);
            ds.addStatus(stat2);
            ds.addStatus(stat3);
            ds.addStatus(stat4);
            
            //save, publish, reload dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //generate some identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 1, null);
            
            //create and save a record - at this point status should be "Status 1"
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids.get(0));
            Long recId = dao.saveRecord(rec, true, null, "NoUser");
            
            //change the status to "Status 2"
            try{
                client.changeStatus(recId, ds.getStatus(1).getId(), null);
                //check that the status of the record has been changed
                rec = dao.getRecord(recId, RetrieveDepth.RS_NO_BINARY).toHibernate();
                assertEquals("Record does not have the correct status", ds.getStatus(1), rec.getStatus());
            }
            catch(RepositoryServiceFault ex){
                ex.printStackTrace();
                fail("Exception thrown when trying to perform valid status change from Status 1 to Status 2");
            }
            
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    
    public void testGetDataSetSummary(){
        try{
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MINUTE, -1);
            
            //add a DataSet
            String name1 = "testGetDataSetSummary - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name1);
            //generate unique project code
            java.rmi.dgc.VMID guid1 = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid1.toString());
            
            dao.saveDataSet(ds.toDTO());
            
            RepositoryClient client = new RepositoryClient();
            DataSet ds1 = client.getDataSetSummary(guid1.toString(), cal.getTime(), null);
            assertNotNull("Should not have retrieved a null dataset", ds1);
            assertEquals("Dataset summary has the wrong name",name1,ds1.getName());
            
            cal.add(Calendar.MONTH, 1);
            DataSet ds2 = client.getDataSetSummary(guid1.toString(), cal.getTime(), null);
            assertNull("Should have retrieved a null dataset", ds2);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    
    public void testMarkResponseAsInvalid(){
        try{
            //create a dataset
            String name = "testMarkResponseAsInvalid - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);

            Document doc = factory.createDocument("Doc");
            DocumentOccurrence docOcc = factory.createDocumentOccurrence("DOcc");
            doc.addOccurrence(docOcc);
            ds.addDocument(doc);
            
            Section sec = factory.createSection("Sec");
            SectionOccurrence sOcc = factory.createSectionOccurrence("SOcc");
            sec.addOccurrence(sOcc);
            doc.addSection(sec);
            
            TextEntry te = factory.createTextEntry("TE");
            doc.addEntry(te);
            te.setSection(sec);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            doc = ds.getDocument(0);
            docOcc = doc.getOccurrence(0);
            sec = doc.getSection(0);
            sOcc = sec.getOccurrence(0);
            te = (TextEntry)doc.getEntry(0);
            
            //generate some identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 1, null);
            
            //create and save a record
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids.get(0));
            
            DocumentInstance di = doc.generateInstance(docOcc);
            rec.addDocumentInstance(di);
            
            BasicResponse resp = te.generateInstance(sOcc);
            ITextValue tv = te.generateValue();
            tv.setValue("Foo bar");
            resp.setValue(tv);
            di.addResponse(resp);
            
            Long rId = dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            rec = dao.getRecord(rId, RetrieveDepth.RS_NO_BINARY).toHibernate();            
            di = rec.getDocumentInstance(docOcc);
            resp = (BasicResponse)di.getResponse(te, sOcc);
            
            String annot = "Rubbish!";
            client.markResponseAsInvalid(resp.getId(), annot, null);
            
            rec = dao.getRecord(rId, RetrieveDepth.RS_NO_BINARY).toHibernate();
            di = rec.getDocumentInstance(docOcc);
            resp = (BasicResponse)di.getResponse(te, sOcc);
            
            assertEquals("Response has the wrong status",ResponseStatus.FLAGGED_INVALID,resp.getStatus());
            assertEquals("Response has the wrong annotation",annot,resp.getAnnotation());
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testMarkResponseAsValid(){
        try{
            //create a dataset
            String name = "testMarkResponseAsValid - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);

            Document doc = factory.createDocument("Doc");
            DocumentOccurrence docOcc = factory.createDocumentOccurrence("DOcc");
            doc.addOccurrence(docOcc);
            ds.addDocument(doc);
            
            Section sec = factory.createSection("Sec");
            SectionOccurrence sOcc = factory.createSectionOccurrence("SOcc");
            sec.addOccurrence(sOcc);
            doc.addSection(sec);
            
            TextEntry te = factory.createTextEntry("TE");
            doc.addEntry(te);
            te.setSection(sec);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            doc = ds.getDocument(0);
            docOcc = doc.getOccurrence(0);
            sec = doc.getSection(0);
            sOcc = sec.getOccurrence(0);
            te = (TextEntry)doc.getEntry(0);
            
            //generate some identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 1, null);
            
            //create and save a record
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids.get(0));
            
            DocumentInstance di = doc.generateInstance(docOcc);
            rec.addDocumentInstance(di);
            
            BasicResponse resp = te.generateInstance(sOcc);
            ITextValue tv = te.generateValue();
            tv.setValue("Foo bar");
            resp.setValue(tv);
            di.addResponse(resp);
            
            Long rId = dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            rec = dao.getRecord(rId, RetrieveDepth.RS_NO_BINARY).toHibernate();            
            di = rec.getDocumentInstance(docOcc);
            resp = (BasicResponse)di.getResponse(te, sOcc);
            
            client.markResponseAsInvalid(resp.getId(), null, null);
            client.markResponseAsValid(resp.getId(), null);
            
            rec = dao.getRecord(rId, RetrieveDepth.RS_NO_BINARY).toHibernate();
            di = rec.getDocumentInstance(docOcc);
            resp = (BasicResponse)di.getResponse(te, sOcc);
            
            assertEquals("Response has the wrong status",ResponseStatus.NORMAL,resp.getStatus());
            assertNull("Response has the wrong annotation",resp.getAnnotation());
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testSaveRecord_UnknownIdentifier(){
        try{
            //create a dataset
            String name = "testSaveRecord_UnknownIdentifier - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);

            Document doc = factory.createDocument("Doc");
            DocumentOccurrence docOcc = factory.createDocumentOccurrence("DOcc");
            doc.addOccurrence(docOcc);
            ds.addDocument(doc);
            
            Section sec = factory.createSection("Sec");
            SectionOccurrence sOcc = factory.createSectionOccurrence("SOcc");
            sec.addOccurrence(sOcc);
            doc.addSection(sec);
            
            TextEntry te = factory.createTextEntry("TE");
            doc.addEntry(te);
            te.setSection(sec);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            doc = ds.getDocument(0);
            docOcc = doc.getOccurrence(0);
            sec = doc.getSection(0);
            sOcc = sec.getOccurrence(0);
            te = (TextEntry)doc.getEntry(0);
            
            //create a record
            Record rec = ds.generateInstance();
            DocumentInstance di = doc.generateInstance(docOcc);
            rec.addDocumentInstance(di);
            BasicResponse resp = te.generateInstance(sOcc);
            ITextValue tv = te.generateValue();
            tv.setValue("Foo bar");
            resp.setValue(tv);
            di.addResponse(resp);
            
            //generate a bogus identifier - it has the correct format, but will not have
            //been persisted in the repository
            rec.generateIdentifier(projectCode+IdentifierHelper.PROJ_GRP_SEPARATOR+"XYZ"+IdentifierHelper.GRP_SUFF_SEPARATOR+"100");
            
            //save the record
            RepositoryClient client = new RepositoryClient();
            try{
                client.saveRecord(rec, true, "NoUser");
                fail("Exception should have been thrown when trying to save record with invalid identifier");
            }
            catch(RepositoryInvalidIdentifierFault ex){
                //do nothing
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    
    public void testGetRecordsDocumentsByStatus(){
        try{
            String name = "testGetRecordsIncompleteDocuments - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);

            Document d1 = factory.createDocument("D1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            d1.addOccurrence(occ1);
            Status s1 = factory.createStatus("Incomplete", "Incomplete", 0);
            d1.addStatus(s1);
            ds.addDocument(d1);
            Document d2 = factory.createDocument("D2");
            DocumentOccurrence occ2 = factory.createDocumentOccurrence("Occ2");
            d2.addOccurrence(occ2);
            Status s2 = factory.createStatus("Complete", "Complete", 0);
            d2.addStatus(s2);
            ds.addDocument(d2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 1, null);

            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids.get(0));
            DocumentInstance di1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            r1.addDocumentInstance(di1);
            DocumentInstance di2 = ds.getDocument(1).generateInstance(ds.getDocument(1).getOccurrence(0));
            r1.addDocumentInstance(di2);
            
            Long r1Id = dao.saveRecord(r1, true, null, "NoUser");
            
            Record rInc = client.getRecordsDocumentsByStatus(ds, r1.getIdentifier().getIdentifier(), "Incomplete", null);
            
            assertNull("Record has a non-null database id", rInc.getId());
            assertNotNull("Record does not have an instance for document 1",rInc.getDocumentInstance(ds.getDocument(0).getOccurrence(0)));
            assertNull("Record does have an instance for document 2",rInc.getDocumentInstance(ds.getDocument(1).getOccurrence(0)));
            
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }

    public void testGetRecordsByGroups(){
        try{
            String name = "testGetRecordsByGroups - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            Group grp2 = factory.createGroup("BAR");
            ds.addGroup(grp2);
            Group grp3 = factory.createGroup("EGG");
            ds.addGroup(grp3);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers for groups "FOO", "BAR" and "EGG"
            RepositoryClient client = new RepositoryClient();
            List<Identifier> idsFoo = client.generateIdentifiers(dsId, "FOO", 3, null);
            List<Identifier> idsBar = client.generateIdentifiers(dsId, "BAR", 3, null);
            List<Identifier> idsEgg = client.generateIdentifiers(dsId, "EGG", 3, null);
            
            Record r1 = ds.generateInstance();
            r1.setIdentifier(idsFoo.get(0));
            Long r1id = dao.saveRecord(r1.toDTO(), true, null, "NoUser");
            
            Record r2 = ds.generateInstance();
            r2.setIdentifier(idsFoo.get(1));
            Long r2id = dao.saveRecord(r2.toDTO(), true, null, "NoUser");
            
            Record r3 = ds.generateInstance();
            r3.setIdentifier(idsFoo.get(2));
            Long r3id = dao.saveRecord(r3.toDTO(), true, null, "NoUser");
            
            Record r4 = ds.generateInstance();
            r4.setIdentifier(idsBar.get(0));
            Long r4id = dao.saveRecord(r4.toDTO(), true, null, "NoUser");
            
            Record r5 = ds.generateInstance();
            r5.setIdentifier(idsBar.get(1));
            Long r5id = dao.saveRecord(r5.toDTO(), true, null, "NoUser");
            
            Record r6 = ds.generateInstance();
            r6.setIdentifier(idsBar.get(2));
            Long r6id = dao.saveRecord(r6.toDTO(), true, null, "NoUser");
            
            Record r7 = ds.generateInstance();
            r7.setIdentifier(idsEgg.get(0));
            Long r7id = dao.saveRecord(r7.toDTO(), true, null, "NoUser");
            
            Record r8 = ds.generateInstance();
            r8.setIdentifier(idsEgg.get(1));
            Long r8id = dao.saveRecord(r8.toDTO(), true, null, "NoUser");
            
            Record r9 = ds.generateInstance();
            r9.setIdentifier(idsEgg.get(2));
            Long r9id = dao.saveRecord(r9.toDTO(), true, null, "NoUser");
                        
            List<String> grps1 = new ArrayList<String>();
            grps1.add("FOO");
            List<String> records = client.getRecordsByGroups(projectCode, grps1, null);
            assertEquals("Array of records for group 'FOO' has the wrong number of items",3,records.size());
            assertEquals("Identifier at index 0 for group 'FOO' is not correct", idsFoo.get(0).getIdentifier(), records.get(0));
            assertEquals("Identifier at index 1 for group 'FOO' is not correct", idsFoo.get(1).getIdentifier(), records.get(1));
            assertEquals("Identifier at index 2 for group 'FOO' is not correct", idsFoo.get(2).getIdentifier(), records.get(2));

            List<String> grps2 = new ArrayList<String>();
            grps2.add("FOO");
            grps2.add("BAR");
            List<String> records2 = client.getRecordsByGroups(projectCode, grps2, null);
            assertEquals("Array of records for groups 'FOO' and 'BAR' has the wrong number of items",6,records2.size());
            assertEquals("Identifier at index 0 for groups 'FOO+BAR' is not correct", idsBar.get(0).getIdentifier(), records2.get(0));
            assertEquals("Identifier at index 1 for groups 'FOO+BAR' is not correct", idsBar.get(1).getIdentifier(), records2.get(1));
            assertEquals("Identifier at index 2 for groups 'FOO+BAR' is not correct", idsBar.get(2).getIdentifier(), records2.get(2));
            assertEquals("Identifier at index 3 for groups 'FOO+BAR' is not correct", idsFoo.get(0).getIdentifier(), records2.get(3));
            assertEquals("Identifier at index 4 for groups 'FOO+BAR' is not correct", idsFoo.get(1).getIdentifier(), records2.get(4));
            assertEquals("Identifier at index 5 for groups 'FOO+BAR' is not correct", idsFoo.get(2).getIdentifier(), records2.get(5));

            try{
                client.getRecordsByGroups(projectCode, new ArrayList<String>(), null);
                fail("Exception should have been thrown when using an empty array of groups");
            }
            catch(RepositoryServiceFault ex){
                //do nothing
            }
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetRecordsByGroupsAndDocStatus(){
        try{
            String name = "testGetRecordsByGroupsAndDocStatus - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            Group grp2 = factory.createGroup("BAR");
            ds.addGroup(grp2);

            Document d1 = factory.createDocument("D1");
            DocumentOccurrence do1 = factory.createDocumentOccurrence("DO1");
            d1.addOccurrence(do1);
            ds.addDocument(d1);
            Status stat1a = factory.createStatus("Stat1", 0);
            Status stat2a = factory.createStatus("Stat2", 1);
            stat1a.addStatusTransition(stat2a);
            d1.addStatus(stat1a);
            d1.addStatus(stat2a);
            Document d2 = factory.createDocument("D2");
            DocumentOccurrence do2 = factory.createDocumentOccurrence("DO2");
            d2.addOccurrence(do2);
            ds.addDocument(d2);
            Status stat1b = factory.createStatus("Stat1", 0);
            Status stat2b = factory.createStatus("Stat2", 1);
            stat1b.addStatusTransition(stat2b);
            d2.addStatus(stat1b);
            d2.addStatus(stat2b);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            d1 = ds.getDocument(0);
            do1 = d1.getOccurrence(0);
            d2 = ds.getDocument(1);
            do2 = d2.getOccurrence(0);
            
            //get identifiers for groups "FOO", "BAR" and "EGG"
            RepositoryClient client = new RepositoryClient();
            List<Identifier> idsFoo = client.generateIdentifiers(dsId, "FOO", 4, null);
            List<Identifier> idsBar = client.generateIdentifiers(dsId, "BAR", 4, null);
            
            //r1 has group "FOO" and an instance of doc 1 with status "Stat1"
            Record r1 = ds.generateInstance();
            r1.setIdentifier(idsFoo.get(0));
            DocumentInstance di1 = d1.generateInstance(do1);
            r1.addDocumentInstance(di1);
            Long r1id = dao.saveRecord(r1.toDTO(), true, null, "NoUser");
            
            //r2 has group "FOO" and an instance of doc 1 with status "Stat2"
            Record r2 = ds.generateInstance();
            r2.setIdentifier(idsFoo.get(1));
            DocumentInstance di2 = d1.generateInstance(do1);
            r2.addDocumentInstance(di2);
            Long r2id = dao.saveRecord(r2.toDTO(), true, null, "NoUser");
            r2 = dao.getRecord(r2id, RetrieveDepth.RS_NO_BINARY).toHibernate();
            dao.changeStatus(r2.getDocumentInstance(do1).getId(), d1.getStatus(1).getId(), "NoUser");
            
            //r3 has group "FOO" and an instance of doc 2 with status "Stat1"
            Record r3 = ds.generateInstance();
            r3.setIdentifier(idsFoo.get(2));
            DocumentInstance di3 = d2.generateInstance(do2);
            r3.addDocumentInstance(di3);
            Long r3id = dao.saveRecord(r3.toDTO(), true, null, "NoUser");
            
            //r4 has group "FOO" and an instance of doc 2 with status "Stat2"
            Record r4 = ds.generateInstance();
            r4.setIdentifier(idsFoo.get(3));
            DocumentInstance di4 = d2.generateInstance(do2);
            r4.addDocumentInstance(di4);
            Long r4id = dao.saveRecord(r4.toDTO(), true, null, "NoUser");
            r4 = dao.getRecord(r4id, RetrieveDepth.RS_NO_BINARY).toHibernate();
            dao.changeStatus(r4.getDocumentInstance(do2).getId(), d2.getStatus(1).getId(), "NoUser");
            
            //r5 has group "BAR" and an instance of doc 1 with status "Stat1"
            Record r5 = ds.generateInstance();
            r5.setIdentifier(idsBar.get(0));
            DocumentInstance di5 = d1.generateInstance(do1);
            r5.addDocumentInstance(di5);
            Long r5id = dao.saveRecord(r5.toDTO(), true, null, "NoUser");
            
            //r6 has group "BAR" and an instance of doc 1 with status "Stat2"
            Record r6 = ds.generateInstance();
            r6.setIdentifier(idsBar.get(1));
            DocumentInstance di6 = d1.generateInstance(do1);
            r6.addDocumentInstance(di6);
            Long r6id = dao.saveRecord(r6.toDTO(), true, null, "NoUser");
            r6 = dao.getRecord(r6id, RetrieveDepth.RS_NO_BINARY).toHibernate();
            dao.changeStatus(r6.getDocumentInstance(do1).getId(), d1.getStatus(1).getId(), "NoUser");
            
            //r7 has group "BAR" and an instance of doc 2 with status "Stat1"
            Record r7 = ds.generateInstance();
            r7.setIdentifier(idsBar.get(2));
            DocumentInstance di7 = d2.generateInstance(do2);
            r7.addDocumentInstance(di7);
            Long r7id = dao.saveRecord(r7.toDTO(), true, null, "NoUser");
            
            //r8 has group "BAR" and an instance of doc 2 with status "Stat2"
            Record r8 = ds.generateInstance();
            r8.setIdentifier(idsBar.get(3));
            DocumentInstance di8 = d2.generateInstance(do2);
            r8.addDocumentInstance(di8);
            Long r8id = dao.saveRecord(r8.toDTO(), true, null, "NoUser");
            r8 = dao.getRecord(r8id, RetrieveDepth.RS_NO_BINARY).toHibernate();
            dao.changeStatus(r8.getDocumentInstance(do2).getId(), d2.getStatus(1).getId(), "NoUser");
                                    
            List<String> grps1 = new ArrayList<String>();
            grps1.add("FOO");
            List<String> records = client.getRecordsByGroupsAndDocStatus(projectCode, grps1, "Stat1", null);
            assertEquals("Array of records for group 'FOO', status 'Stat1' has the wrong number of items",2,records.size());
            assertEquals("Identifier at index 0 for group 'FOO', status 'Stat1' is not correct", idsFoo.get(0).getIdentifier(), records.get(0));
            assertEquals("Identifier at index 1 for group 'FOO', status 'Stat1' is not correct", idsFoo.get(2).getIdentifier(), records.get(1));

            List<String> grps2 = new ArrayList<String>();
            grps2.add("FOO");
            grps2.add("BAR");
            List<String> records2 = client.getRecordsByGroupsAndDocStatus(projectCode, grps2, "Stat2", null);
            assertEquals("Array of records for groups 'FOO' and 'BAR', status 'Stat2' has the wrong number of items",4,records2.size());
            assertEquals("Identifier at index 0 for groups 'FOO+BAR', status 'Stat2' is not correct", idsBar.get(1).getIdentifier(), records2.get(0));
            assertEquals("Identifier at index 1 for groups 'FOO+BAR', status 'Stat2' is not correct", idsBar.get(3).getIdentifier(), records2.get(1));
            assertEquals("Identifier at index 2 for groups 'FOO+BAR', status 'Stat2' is not correct", idsFoo.get(1).getIdentifier(), records2.get(2));
            assertEquals("Identifier at index 3 for groups 'FOO+BAR', status 'Stat2' is not correct", idsFoo.get(3).getIdentifier(), records2.get(3));

            try{
                client.getRecordsByGroupsAndDocStatus(projectCode, new ArrayList<String>(), "Stat1", null);
                fail("Exception should have been thrown when using an empty array of groups");
            }
            catch(RepositoryServiceFault ex){
                //do nothing
            }
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testChangeDocumentStatus(){
        try{
            //create a dataset with statuses
            String name = "testChangeDocumentStatus - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            
            Document d1 = factory.createDocument("D1");
            ds.addDocument(d1);
            DocumentOccurrence do1 = factory.createDocumentOccurrence("DO1");
            d1.addOccurrence(do1);
            
            Status stat1 = factory.createStatus("Status 1", 1);
            Status stat2 = factory.createStatus("Status 2", 2);
            Status stat3 = factory.createStatus("Status 3", 3);
            Status stat4 = factory.createStatus("Status 4", 4);
            stat1.addStatusTransition(stat2);
            stat1.addStatusTransition(stat3);
            stat2.addStatusTransition(stat4);
            stat3.addStatusTransition(stat4);
            d1.addStatus(stat1);
            d1.addStatus(stat2);
            d1.addStatus(stat3);
            d1.addStatus(stat4);
            
            //save, publish, reload dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            d1 = ds.getDocument(0);
            do1 = d1.getOccurrence(0);
            
            //generate some identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 1, null);
            
            //create and save a record - at this point status should be "Status 1"
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids.get(0));
            DocumentInstance di1 = d1.generateInstance(do1);
            rec.addDocumentInstance(di1);
            Long recId = dao.saveRecord(rec, true, null, "NoUser");
            
            //change the status to "Status 2"
            try{
                client.changeDocumentStatus(ids.get(0).getIdentifier(), do1.getId(), d1.getStatus(1).getId(), "NoUser");
                //check that the status of the record has been changed
                rec = dao.getRecord(recId, RetrieveDepth.RS_NO_BINARY).toHibernate();
                assertEquals("Document instance does not have the correct status", d1.getStatus(1), rec.getDocumentInstance(do1).getStatus());
            }
            catch(RepositoryServiceFault ex){
                ex.printStackTrace();
                fail("Exception thrown when trying to perform valid status change from Status 1 to Status 2");
            }
            
            //try to change the status to "Status 3"
            try{
                client.changeDocumentStatus(ids.get(0).getIdentifier(), do1.getId(), d1.getStatus(2).getId(), "NoUser");
                fail("Exception should have been thrown when trying to perform invalid status change from Status 2 to Status 3");
            }
            catch(RepositoryServiceFault ex){
                //do nothing
            }
            
            //change the status to "Status 4"
            try{
                client.changeDocumentStatus(ids.get(0).getIdentifier(), do1.getId(), d1.getStatus(3).getId(), "NoUser");
                //check that the status of the record has been changed
                rec = dao.getRecord(recId, RetrieveDepth.RS_NO_BINARY).toHibernate();
                assertEquals("Record does not have the correct status", d1.getStatus(3), rec.getDocumentInstance(do1).getStatus());
            }
            catch(RepositoryServiceFault ex){
                ex.printStackTrace();
                fail("Exception thrown when trying to perform valid status change from Status 2 to Status 4");
            }
            
            //try to change status using an invalid record identifier
            try{
                client.changeDocumentStatus(projectCode+IdentifierHelper.PROJ_GRP_SEPARATOR+"ABC"+IdentifierHelper.GRP_SUFF_SEPARATOR+"987654", 
                        do1.getId(), d1.getStatus(2).getId(), "NoUser");
                fail("Exception should have been thrown when trying to change status using an invalid record identifier");
            }
            catch(RepositoryServiceFault ex){
                //do nothing
            }
            
            //try to change status using an invalid doc occ id
            try{
                client.changeDocumentStatus(ids.get(0).getIdentifier(), -1L, d1.getStatus(2).getId(), "NoUser");
                fail("Exception should have been thrown when trying to change status using an invalid doc occ id");
            }
            catch(RepositoryServiceFault ex){
                //do nothing
            }
            
            //try to change status using an invalid id for the new status
            try{
                client.changeDocumentStatus(ids.get(0).getIdentifier(), do1.getId(), do1.getId(), "NoUser");
                fail("Exception should have been thrown when trying to change status using an invalid status id");
            }
            catch(RepositoryServiceFault ex){
                //do nothing
            }
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    
    public void testChangeDocumentStatus_Email(){
        try{
            //create a dataset with statuses
            String name = "testChangeDocumentStatus_Email - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            
            Document d1 = factory.createDocument("D1");
            ds.addDocument(d1);
            DocumentOccurrence do1 = factory.createDocumentOccurrence("DO1");
            d1.addOccurrence(do1);
            
            Status stat1 = factory.createStatus(Status.DOC_STATUS_INCOMPLETE, 1);
            Status stat2 = factory.createStatus(Status.DOC_STATUS_PENDING, 2);
            Status stat3 = factory.createStatus(Status.DOC_STATUS_REJECTED, 3);
            Status stat4 = factory.createStatus(Status.DOC_STATUS_APPROVED, 4);
            stat1.addStatusTransition(stat2);
            stat2.addStatusTransition(stat1);
            stat2.addStatusTransition(stat3);
            stat2.addStatusTransition(stat4);
            stat3.addStatusTransition(stat2);
            stat4.addStatusTransition(stat2);
            d1.addStatus(stat1);
            d1.addStatus(stat2);
            d1.addStatus(stat3);
            d1.addStatus(stat4);
            
            //save, publish, reload dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            d1 = ds.getDocument(0);
            do1 = d1.getOccurrence(0);
            
            //generate some identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 1, null);
            
            //create and save a record - at this point status should be "Incomplete"
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids.get(0));
            DocumentInstance di1 = d1.generateInstance(do1);
            rec.addDocumentInstance(di1);
            Long recId = dao.saveRecord(rec, true, null, "NoUser");
            
            //change the status to "Pending"
            try{
                client.changeDocumentStatus(ids.get(0).getIdentifier(), do1.getId(), d1.getStatus(1).getId(), "NoUser");
                //check that the status of the record has been changed
                rec = dao.getRecord(recId, RetrieveDepth.RS_NO_BINARY).toHibernate();
                assertEquals("Document instance does not have the correct status", d1.getStatus(1), rec.getDocumentInstance(do1).getStatus());
            }
            catch(RepositoryServiceFault ex){
                ex.printStackTrace();
                fail("Exception thrown when trying to perform valid status change from Status 1 to Status 2");
            }
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testAddConsent(){
        try{
            String name = "testAddConsent - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);

            ConsentFormGroup cfg = factory.createConsentFormGroup();
            cfg.setDescription("CFG1");
            PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
            pcf.setQuestion("PCF1");
            cfg.addConsentForm(pcf);
            ds.addAllConsentFormGroup(cfg);
            
            Document d1 = factory.createDocument("D1");
            d1.addConsentFormGroup(cfg);
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            d1.addOccurrence(occ1);
            ds.addDocument(d1);
            Section sec1 = factory.createSection("S1");
            d1.addSection(sec1);
            SectionOccurrence so1 = factory.createSectionOccurrence("SO1");
            sec1.addOccurrence(so1);
            so1.setMultipleAllowed(true);
            TextEntry te1 = factory.createTextEntry("TE1");
            d1.addEntry(te1);
            te1.setSection(sec1);
            
            //save dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "FOO");
            
            //save empty record
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);
            Long rId = dao.saveRecord(r1.toDTO(), true, null, "NoUser");
            
            //create record to append to saved record, containing instance of the document
            Record r2 = ds.generateInstance();
            r2.generateIdentifier(ids[0].getIdentifier());
            DocumentInstance di = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            r2.addDocumentInstance(di);
            
            //try to save it - should throw exception as there isn't consent
            try{
                dao.saveRecord(r2.toDTO(), true, null, "NoUser");
                fail("Exception should have been thrown when trying to save record with doc instance for which there isn't consent");
            }
            catch(DAOException ex){
                //do nothing
            }
            
            //add the consent
            RepositoryClient client = new RepositoryClient();
            client.addConsent(rId, ds.getAllConsentFormGroup(0).getConsentForm(0).getId(), null, null);
            
            //now try to save the record again
            dao.saveRecord(r2.toDTO(), true, null, "NoUser");
            
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testPatchDataSet(){
        try{
            String name = "testPatchDataSet - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            
            Document d1 = factory.createDocument("D1");
            ds.addDocument(d1);

            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            Document d2 = factory.createDocument("D2");
            ds.addDocument(d2);
            RepositoryClient client = new RepositoryClient();
            client.patchDataSet(ds, null);
            
            ds = dao.getDataSet(dsId).toHibernate();
            
            assertEquals("Dataset does not contain the correct number of documents",2,ds.numDocuments());
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testAddIdentifier(){
        try{
            String name = "testAddIdentifier - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp = factory.createGroup("001001");
            ds.addGroup(grp);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //TODO using implementation here, not interface
            Identifier id = new Identifier();
            id.initialize(projectCode, "001001", 500, 0);
            
            RepositoryClient client = new RepositoryClient();
            client.addIdentifier(dsId, id, null);
            
            //generate an identifier
            List<Identifier> ids = client.generateIdentifiers(dsId, "001001", 1, null);
            
            assertEquals("Suffix of identifier is not correct", 501, ids.get(0).getSuffix());
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testRemovePublishedDataSet(){
        try{
        	Long dsId = null;
            Long d1Id = null;
            DataSet ds = factory.createDataset("DS");
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
    		ds.setProjectCode(projectCode);
    		
            Group grp1 = factory.createGroup("FOO");
    		ds.addGroup(grp1);
    		
    		Document d1 = factory.createDocument("Document 1");
    		DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
    		d1.addOccurrence(occ1);
    		ds.addDocument(d1);

    		dsId = dao.saveDataSet(ds.toDTO());
    		dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            d1 = ds.getDocument(0);
    		
    		d1Id = d1.getId();
    		
    		//get identifiers
    		Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "FOO");            

    		Record r1 = ds.generateInstance();
    		r1.setIdentifier(ids[0]);
    		DocumentInstance di = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
    		r1.addDocumentInstance(di);
    		Long r1Id = dao.saveRecord(r1.toDTO(), true, null, "NoUser");
    		r1 = dao.getRecord(r1Id, RetrieveDepth.RS_NO_BINARY).toHibernate();  
    		
    		ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "FOO");
    		Record r2 = ds.generateInstance();
    		r2.setIdentifier(ids[0]);
    		DocumentInstance di2 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
    		r2.addDocumentInstance(di2);
    		Long r2Id = dao.saveRecord(r2.toDTO(), true, null, "NoUser");
    		r2 = dao.getRecord(r2Id, RetrieveDepth.RS_NO_BINARY).toHibernate(); 
            
         
            RepositoryClient client = new RepositoryClient(); 
            ds = client.getDataSet(dsId, null);
            
            client.removePublishedDataSet(ds.getId(), projectCode, null);
        
            //test that all of the child elements have been removed
            // via cascade delete
            assertFalse(dao.doesObjectExist("DataSet", dsId));
            assertFalse(dao.doesObjectExist("Document", d1Id));
            assertFalse(dao.doesObjectExist("Record", r1Id));
            
    	}
    	catch (Exception ex){
    		ex.printStackTrace();
    		fail(ex.toString());
    	}
    }
    
    /**
     * Read a file from local disk into a byte array.
     * 
     * @param fileName The path of file to read.
     * @return File as a byte array
     * @throws IOException
     * @see http://forum.java.sun.com/thread.jspa?threadID=457266&messageID=2090543
     */
    private byte[] read2Array(String fileName) throws IOException {
        InputStream is = new FileInputStream(fileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        for(int len=-1;(len=is.read(buf))!=-1;)
            baos.write(buf,0,len);
        baos.flush();
        is.close();
        baos.close();
        return baos.toByteArray();
    }
    
    private StandardCode generateUniqueCode() throws Exception{
        org.psygrid.data.model.dto.StandardCodeDTO[] codes = dao.getStandardCodes();
        int maxCode = 0;
        for ( int i=0; i<codes.length; i++){
            if ( codes[i].getCode() > maxCode ){
                maxCode = codes[i].getCode();
            }
        }
        maxCode++;
        return factory.createStandardCode("CODE "+Integer.toString(maxCode), maxCode);
    }
    
    public void testGetConsentAndStatusInfoForGroups(){
    	try{
    	
            String name = "testGetConsentAndStatusInfoForGroups - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            
            Status stat1 = factory.createStatus("Rec Stat 1", "Rec Stat 1", 1);
            Status stat2 = factory.createStatus("Rec Stat 2", "Rec Stat 2", 2);
            stat1.addStatusTransition(stat2);
            ds.addStatus(stat1);
            ds.addStatus(stat2);

            ConsentFormGroup cfg = factory.createConsentFormGroup();
            ds.addAllConsentFormGroup(cfg);
            PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
            pcf.setQuestion("PCF1");
            cfg.addConsentForm(pcf);
            AssociatedConsentForm acf = factory.createAssociatedConsentForm();
            acf.setQuestion("ACF1");
            pcf.addAssociatedConsentForm(acf);
            
            Document d1 = factory.createDocument("D1", "D1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            occ1.setDisplayText("Occ1");
            d1.addOccurrence(occ1);
            ds.addDocument(d1);

            Status d1Stat1 = factory.createStatus("Doc 1 Stat 1", "Doc Stat 1", 1);
            Status d1Stat2 = factory.createStatus("Doc 1 Stat 2", "Doc Stat 2", 2);
            d1Stat1.addStatusTransition(d1Stat2);
            d1.addStatus(d1Stat1);
            d1.addStatus(d1Stat2);
            
            Document d2 = factory.createDocument("D2", "D2");
            DocumentOccurrence occ2 = factory.createDocumentOccurrence("Occ2");
            occ2.setDisplayText("Occ2");
            d2.addOccurrence(occ2);
            ds.addDocument(d2);

            Status d2Stat1 = factory.createStatus("Doc 2 Stat 1", "Doc Stat 1", 1);
            Status d2Stat2 = factory.createStatus("Doc 2 Stat 2", "Doc Stat 2", 2);
            d2Stat1.addStatusTransition(d2Stat2);
            d2.addStatus(d2Stat1);
            d2.addStatus(d2Stat2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            Identifier[] fooIds = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 5, "FOO");
                                  
            //1.No consent or docs
            Record rec1 = ds.generateInstance();
            rec1.setIdentifier(fooIds[0]);
            
            //2. Primary consent only
            Record rec2 = ds.generateInstance();
            rec2.setIdentifier(fooIds[1]);
            Consent c2p = ds.getAllConsentFormGroup(0).getConsentForm(0).generateConsent();
            c2p.setConsentGiven(true);
            rec2.addConsent(c2p);
            
            //3. Primary and Secondary consent
            Record rec3 = ds.generateInstance();
            rec3.setIdentifier(fooIds[2]);
            Consent c3p = ds.getAllConsentFormGroup(0).getConsentForm(0).generateConsent();
            c3p.setConsentGiven(true);
            rec3.addConsent(c3p);
            Consent c3a = ds.getAllConsentFormGroup(0).getConsentForm(0).getAssociatedConsentForm(0).generateConsent();
            c3a.setConsentGiven(true);
            rec3.addConsent(c3a);
            
            //4. One document
            Record rec4 = ds.generateInstance();
            rec4.setIdentifier(fooIds[3]);
            DocumentInstance r4i1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            rec4.addDocumentInstance(r4i1);
            
            //5. Two documents
            Record rec5 = ds.generateInstance();
            rec5.setIdentifier(fooIds[4]);
            DocumentInstance r5i1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            rec5.addDocumentInstance(r5i1);
            DocumentInstance r5i2 = ds.getDocument(1).generateInstance(ds.getDocument(1).getOccurrence(0));
            rec5.addDocumentInstance(r5i2);

            //Save records
            Long recId1 = dao.saveRecord(rec1.toDTO(), true, null, "NoUser");
            Long recId2 = dao.saveRecord(rec2.toDTO(), true, null, "NoUser");
            Long recId3 = dao.saveRecord(rec3.toDTO(), true, null, "NoUser");
            Long recId4 = dao.saveRecord(rec4.toDTO(), true, null, "NoUser");
            Long recId5 = dao.saveRecord(rec5.toDTO(), true, null, "NoUser");
            
            //Change status of records 2 and 4
            dao.changeStatus(recId2, ds.getStatus(1).getId(), "NoUser");
            dao.changeStatus(recId4, ds.getStatus(1).getId(), "NoUser");
            
            //change status of doc inst 2 of record 5
            dao.changeDocumentStatus(
            		rec5.getIdentifier().getIdentifier(), 
            		ds.getDocument(1).getOccurrence(0).getId(), 
            		ds.getDocument(1).getStatus(1).getId(), 
            		"NoUser");
            
            RepositoryClient client = new RepositoryClient();
            List<String> groups = new ArrayList<String>();
            groups.add("FOO");
            ConsentStatusResult result = client.getConsentAndStatusInfoForGroups(projectCode, groups, new Date(0), null);
            
            Long pcfId = ds.getAllConsentFormGroup(0).getConsentForm(0).getId();
            Long acfId = ds.getAllConsentFormGroup(0).getConsentForm(0).getAssociatedConsentForm(0).getId();
            
            Long recStatus1Id = ds.getStatus(0).getId();
            Long recStatus2Id = ds.getStatus(1).getId();
            
            Long doc1Status1Id = ds.getDocument(0).getStatus(0).getId();
            Long doc1Status2Id = ds.getDocument(0).getStatus(1).getId();
            Long doc2Status1Id = ds.getDocument(1).getStatus(0).getId();
            Long doc2Status2Id = ds.getDocument(1).getStatus(1).getId();
            
            Long doc1Occ1Id = ds.getDocument(0).getOccurrence(0).getId();
            Long doc2Occ1Id = ds.getDocument(1).getOccurrence(0).getId();
            
            rec4 = dao.getRecord(recId4, RetrieveDepth.RS_NO_BINARY).toHibernate();
            rec4.attach(ds);
            Long rec4Inst1Id = rec4.getDocumentInstance(ds.getDocument(0).getOccurrence(0)).getId();
            rec5 = dao.getRecord(recId5, RetrieveDepth.RS_NO_BINARY).toHibernate();
            rec5.attach(ds);
            Long rec5Inst1Id = rec5.getDocumentInstance(ds.getDocument(0).getOccurrence(0)).getId();
            Long rec5Inst2Id = rec5.getDocumentInstance(ds.getDocument(1).getOccurrence(0)).getId();
            
            ConsentResult[] consentResults = result.getConsentResults();
            assertEquals("Consent results is wrong size",6,consentResults.length);
            //Record 1
            assertEquals("Identifier is wrong for consent result 0", fooIds[0].getIdentifier(), consentResults[0].getIdentifier());
            assertEquals("Consent given is wrong for consent result 0", false, consentResults[0].isConsentGiven());
            assertEquals("Consent form id is wrong for consent result 0", null, consentResults[0].getConsentFormId());
            //Record 2
            assertEquals("Identifier is wrong for consent result 1", fooIds[1].getIdentifier(), consentResults[1].getIdentifier());
            assertEquals("Consent given is wrong for consent result 1", true, consentResults[1].isConsentGiven());
            assertEquals("Consent form id is wrong for consent result 1", pcfId, consentResults[1].getConsentFormId());
            //Record 3
            assertEquals("Identifier is wrong for consent result 2.1", fooIds[2].getIdentifier(), consentResults[2].getIdentifier());
            assertEquals("Consent given is wrong for consent result 2.1", true, consentResults[2].isConsentGiven());
            assertEquals("Consent form id is wrong for consent result 2.1", pcfId, consentResults[2].getConsentFormId());
            assertEquals("Identifier is wrong for consent result 2.2", fooIds[2].getIdentifier(), consentResults[3].getIdentifier());
            assertEquals("Consent given is wrong for consent result 2.2", true, consentResults[3].isConsentGiven());
            assertEquals("Consent form id is wrong for consent result 2.2", acfId, consentResults[3].getConsentFormId());
            //Record 4
            assertEquals("Identifier is wrong for consent result 3", fooIds[3].getIdentifier(), consentResults[4].getIdentifier());
            assertEquals("Consent given is wrong for consent result 3", false, consentResults[4].isConsentGiven());
            assertEquals("Consent form id is wrong for consent result 3", null, consentResults[4].getConsentFormId());
            //Record 5
            assertEquals("Identifier is wrong for consent result 4", fooIds[4].getIdentifier(), consentResults[5].getIdentifier());
            assertEquals("Consent given is wrong for consent result 4", false, consentResults[5].isConsentGiven());
            assertEquals("Consent form id is wrong for consent result 4", null, consentResults[5].getConsentFormId());

            StatusResult[] statusResults = result.getStatusResults();
            assertEquals("Status results is wrong size",6,statusResults.length);
            //Record 1
            assertEquals("Identifier is wrong for status result 0", fooIds[0].getIdentifier(), statusResults[0].getIdentifier());
            assertEquals("Record status id is wrong for status result 0", recStatus1Id, statusResults[0].getRecStatusId());
            assertEquals("Instance id is wrong for status result 0", null, statusResults[0].getInstanceId());
            assertEquals("Occurrence id is wrong for status result 0", null, statusResults[0].getOccurrenceId());
            assertEquals("Doc status id is wrong for status result 0", null, statusResults[0].getDocStatusId());
            //Record 2
            assertEquals("Identifier is wrong for status result 1", fooIds[1].getIdentifier(), statusResults[1].getIdentifier());
            assertEquals("Record status id is wrong for status result 1", recStatus2Id, statusResults[1].getRecStatusId());
            assertEquals("Instance id is wrong for status result 1", null, statusResults[1].getInstanceId());
            assertEquals("Occurrence id is wrong for status result 1", null, statusResults[1].getOccurrenceId());
            assertEquals("Doc status id is wrong for status result 1", null, statusResults[1].getDocStatusId());
            //Record 3
            assertEquals("Identifier is wrong for status result 2", fooIds[2].getIdentifier(), statusResults[2].getIdentifier());
            assertEquals("Record status id is wrong for status result 2", recStatus1Id, statusResults[2].getRecStatusId());
            assertEquals("Instance id is wrong for status result 2", null, statusResults[2].getInstanceId());
            assertEquals("Occurrence id is wrong for status result 2", null, statusResults[2].getOccurrenceId());
            assertEquals("Doc status id is wrong for status result 2", null, statusResults[2].getDocStatusId());
            //Record 4
            assertEquals("Identifier is wrong for status result 3", fooIds[3].getIdentifier(), statusResults[3].getIdentifier());
            assertEquals("Record status id is wrong for status result 3", recStatus2Id, statusResults[3].getRecStatusId());
            assertEquals("Instance id is wrong for status result 3", rec4Inst1Id, statusResults[3].getInstanceId());
            assertEquals("Occurrence id is wrong for status result 3", doc1Occ1Id, statusResults[3].getOccurrenceId());
            assertEquals("Doc status id is wrong for status result 3", doc1Status1Id, statusResults[3].getDocStatusId());
            //Record 5
            assertEquals("Identifier is wrong for status result 4.1", fooIds[4].getIdentifier(), statusResults[4].getIdentifier());
            assertEquals("Record status id is wrong for status result 4.1", recStatus1Id, statusResults[4].getRecStatusId());
            assertEquals("Instance id is wrong for status result 4.1", rec5Inst1Id, statusResults[4].getInstanceId());
            assertEquals("Occurrence id is wrong for status result 4.1", doc1Occ1Id, statusResults[4].getOccurrenceId());
            assertEquals("Doc status id is wrong for status result 4.1", doc1Status1Id, statusResults[4].getDocStatusId());
            assertEquals("Identifier is wrong for status result 4.2", fooIds[4].getIdentifier(), statusResults[5].getIdentifier());
            assertEquals("Record status id is wrong for status result 4.2", recStatus1Id, statusResults[5].getRecStatusId());
            assertEquals("Instance id is wrong for status result 4.2", rec5Inst2Id, statusResults[5].getInstanceId());
            assertEquals("Occurrence id is wrong for status result 4.2", doc2Occ1Id, statusResults[5].getOccurrenceId());
            assertEquals("Doc status id is wrong for status result 4.2", doc2Status2Id, statusResults[5].getDocStatusId());
            

		}
	    catch(Exception ex){
	        ex.printStackTrace();
	        fail(ex.toString());
	    }

    }
    
    public void testGetConsentAndStatusInfoForGroups_time(){
    	try{

            String name = "testGetConsentAndStatusInfoForGroups_time - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            
            Status stat1 = factory.createStatus("Rec Stat 1", "Rec Stat 1", 1);
            Status stat2 = factory.createStatus("Rec Stat 2", "Rec Stat 2", 2);
            stat1.addStatusTransition(stat2);
            ds.addStatus(stat1);
            ds.addStatus(stat2);

            ConsentFormGroup cfg = factory.createConsentFormGroup();
            ds.addAllConsentFormGroup(cfg);
            PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
            pcf.setQuestion("PCF1");
            cfg.addConsentForm(pcf);
            AssociatedConsentForm acf = factory.createAssociatedConsentForm();
            acf.setQuestion("ACF1");
            pcf.addAssociatedConsentForm(acf);
            
            Document d1 = factory.createDocument("D1", "D1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            occ1.setDisplayText("Occ1");
            d1.addOccurrence(occ1);
            ds.addDocument(d1);

            Status d1Stat1 = factory.createStatus("Doc 1 Stat 1", "Doc Stat 1", 1);
            Status d1Stat2 = factory.createStatus("Doc 1 Stat 2", "Doc Stat 2", 2);
            d1Stat1.addStatusTransition(d1Stat2);
            d1.addStatus(d1Stat1);
            d1.addStatus(d1Stat2);
            
            Document d2 = factory.createDocument("D2", "D2");
            DocumentOccurrence occ2 = factory.createDocumentOccurrence("Occ2");
            occ2.setDisplayText("Occ2");
            d2.addOccurrence(occ2);
            ds.addDocument(d2);

            Status d2Stat1 = factory.createStatus("Doc 2 Stat 1", "Doc Stat 1", 1);
            Status d2Stat2 = factory.createStatus("Doc 2 Stat 2", "Doc Stat 2", 2);
            d2Stat1.addStatusTransition(d2Stat2);
            d2.addStatus(d2Stat1);
            d2.addStatus(d2Stat2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            Identifier[] fooIds = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 200, "FOO");
            
            //load 200 records into the repository
            for ( int i=0; i<200; i++ ){
                Record rec = ds.generateInstance();
                rec.setIdentifier(fooIds[i]);
                Consent c = ds.getAllConsentFormGroup(0).getConsentForm(0).generateConsent();
                c.setConsentGiven(true);
                rec.addConsent(c);
                DocumentInstance r5i1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
                rec.addDocumentInstance(r5i1);
                DocumentInstance r5i2 = ds.getDocument(1).generateInstance(ds.getDocument(1).getOccurrence(0));
                rec.addDocumentInstance(r5i2);
            	dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            }
            
            RepositoryClient client = new RepositoryClient();
            List<String> groups = new ArrayList<String>();
            groups.add("FOO");

            //time the getRecordsWithConsentByGroups operation
            Date start1 = new Date();
            List<Record> result1 = client.getRecordsWithConsentByGroups(projectCode, groups, new Date(0), null);
            Date end1 = new Date();
            
            //time the getConsentAndStatusInfoByGroups operation
            Date start2 = new Date();
            ConsentStatusResult result2 = client.getConsentAndStatusInfoForGroups(projectCode, groups, new Date(0), null);
            Date end2 = new Date();
            
            long time1 = (end1.getTime() - start1.getTime());
            long time2 = (end2.getTime() - start2.getTime());
            
            System.out.println("Time for getRecordsWithConsentByGroups = "+time1+" ms");
            System.out.println("Time for getConsentAndStatusInfoForGroups = "+time2+" ms");
            
            assertEquals("getRecordsWithConsentByGroups returned wrong number of records", 200, result1.size());
            
            assertEquals("getConsentAndStatusInfoForGroups returned wrong number of consent items", 200, result2.getConsentResults().length);
            assertEquals("getConsentAndStatusInfoForGroups returned wrong number of status items", 400, result2.getStatusResults().length);
            
            
		}
	    catch(Exception ex){
	        ex.printStackTrace();
	        fail(ex.toString());
	    }
    }
    
    public void testGetIdentifiers(){
        try{
            //create a dataset
            String name = "testGetIdentifiers - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);

            Document doc1 = factory.createDocument("Doc 1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            doc1.addOccurrence(occ1);
            ds.addDocument(doc1);
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            doc1 = (Document)ds.getDocument(0);
            //get identifiers
            RepositoryClient client = new RepositoryClient();
            List<Identifier> ids = client.generateIdentifiers(dsId, "FOO", 3, null);
            
            //generate and save three records
            List<Long> recordIds = new ArrayList<Long>();
            for (int i=0; i<3; i++){
                Record r = ds.generateInstance();
                r.setIdentifier(ids.get(i));
                
                DocumentInstance di = doc1.generateInstance(doc1.getOccurrence(0));
                r.addDocumentInstance(di);

                recordIds.add(dao.saveRecord(r, true, null, "NoUser"));
            }
            
            List<Record> records = client.getRecords(dsId, null);
            
            List<String> identifiers = client.getIdentifiers(new Long(5), null);
           // assertEquals("List of records contains the wrong number of items",3,records.size());
           // assertEquals("List of identifiers contains the wrong number of items",3,identifiers.size());
            
            System.out.println("Identifiers are: ");
            System.out.println(identifiers);
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
}
