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

package org.psygrid.data.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.NoConsentException;
import org.psygrid.data.repository.dao.ObjectOutOfDateException;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.springframework.mail.SimpleMailMessage;

/**
 * Unit Tests for implementations of the RecordDAO interface
 * 
 * @author Rob Harper
 *
 */
public class RecordDAOTest extends DAOTest {

    private RepositoryDAO dao = null;
    private Factory factory = null;
    
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
    
    /**
     * Unit test for saving a new Record.
     */
    public void testSaveRecord_Create(){
        try{
            //create a dataset and save it
            String name = "testSaveRecord_Create - "+(new Date()).toString();
            DataSet ds = createDataSet(name, true);
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, ds.getProjectCode(), ds.getId(), 1, "FOO");            
            
            //generate template Record
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids[0]);
            
            //save the record
            Long recId = dao.saveRecord(rec, true, null, "NoUser");
            assertNotNull("Unique identifier has not been assigned",recId);

            Record savedRec = dao.getRecord(recId, RetrieveDepth.RS_NO_BINARY).toHibernate();
            assertNotNull("Retrieved a Null Record",savedRec);
        
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testSaveRecord_DTO(){
        try{
            //create a dataset and save it
            String name = "testSaveRecord_Create - "+(new Date()).toString();
            DataSet ds = createDataSet(name, true);
           
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, ds.getProjectCode(), ds.getId(), 1, "FOO");            
            
            //generate template Record
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids[0]);
            
            //save the record
            Long recId = dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            assertNotNull("Unique identifier has not been assigned",recId);

            Record savedRec = dao.getRecord(recId, RetrieveDepth.RS_NO_BINARY).toHibernate();
            assertNotNull("Retrieved a Null Record",savedRec);
        
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for saving a new Record when consent has not been
     * given.
     */
    public void testSaveRecord_NoConsent(){
        try{
            //create a dataset and save it
            String name = "testSaveRecord_NoConsent - "+(new Date()).toString();
            DataSet ds = createDataSet(name, true);
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, ds.getProjectCode(), ds.getId(), 1, "FOO");            

            //generate template Record
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids[0]);
            DocumentInstance di1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            rec.addDocumentInstance(di1);
            
            //save the record
            try{
                dao.saveRecord(rec, true, null, "NoUser");
                fail("Record saved even though consent has not been given");
            }
            catch(DAOException ex){
                //do nothing
            }
        
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for saving a new Record when standard consent has been
     * given, but its associated consent has not.
     */
    public void testSaveRecord_NoAssocConsent(){
        try{
            //create a dataset and save it
            String name = "testSaveRecord_NoAssocConsent - "+(new Date()).toString();
            DataSet ds = createDataSet(name, true);
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, ds.getProjectCode(), ds.getId(), 1, "FOO");            

            //generate template Record
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids[0]);
            DocumentInstance di2 = ds.getDocument(1).generateInstance(ds.getDocument(1).getOccurrence(0));
            rec.addDocumentInstance(di2);
            
            //complete the standard consent for document 2, but not its associated
            //consent
            Consent c = ds.getDocument(1).getConsentFormGroup(0).getConsentForm(0).generateConsent();
            c.setConsentGiven(true);
            rec.addConsent(c);
            
            //save the record
            try{
                dao.saveRecord(rec, true, null, "NoUser");
                fail("Record saved even though consent has not been given");
            }
            catch(DAOException ex){
                //do nothing
            }
        
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for saving (updating) an existing Record.
     */
    public void testSaveRecord_Update(){
        try{
            //create a dataset and save it
            String name = "testSaveRecord_Update - "+(new Date()).toString();
            DataSet ds = createDataSet(name, true);
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, ds.getProjectCode(), ds.getId(), 1, "FOO");            

            Long recordId = null;
            {
                //generate template Record
                Record rec = ds.generateInstance();
                rec.setIdentifier(ids[0]);
                DocumentInstance di1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
                rec.addDocumentInstance(di1);
                
                //complete the consent
                Consent c = ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0).generateConsent();
                c.setConsentGiven(true);
                rec.addConsent(c);
                
                //save the record
                recordId = dao.saveRecord(rec, true, null, "NoUser");
            }
            
            String newLoc = "New location";
            {
                //get the record
                Record rec = dao.getRecord(recordId, RetrieveDepth.RS_NO_BINARY).toHibernate();
                
                //modify a consent reply
                Consent c = rec.getConsent(ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0));
                c.setLocation(newLoc);
                
                //save the record again
                dao.saveRecord(rec, true, null, "NoUser");
            }
            
            Record rec = dao.getRecord(recordId, RetrieveDepth.RS_NO_BINARY).toHibernate();
            Consent c = rec.getConsent(ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0));
            assertEquals("Consent form location has not been updated",newLoc,c.getLocation());
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }        
    }
    
    /**
     * Unit test for saving a Record when the DataSet that the
     * Record is based upon has not been published.
     */
    public void testSaveRecord_DSNotPublished(){
        try{
            //create a dataset and save it
            String name = "testSaveRecord_DSNotPublished - "+(new Date()).toString();
            DataSet ds = createDataSet(name, false);
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, ds.getProjectCode(), ds.getId(), 1, "FOO");            

            //generate template Record
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids[0]);
            
            //save the record
            try{
                dao.saveRecord(rec, true, null, "NoUser");
                fail("Record saved even though the DataSet has not been published");
            }
            catch(DAOException ex){
                //do nothing
            }
        
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for saving (updating) a Record when the
     * Record is out-of-date i.e. it has been concurrently
     * updated by abother session.
     */
    public void testSaveRecord_OutOfDate(){
        try{
            //create a dataset and save it
            String name = "testSaveRecord_OutOfDate - "+(new Date()).toString();
            DataSet ds = createDataSet(name, true);
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, ds.getProjectCode(), ds.getId(), 1, "FOO");            

            Long recordId = null;
            
            //generate template Record
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids[0]);
            DocumentInstance di1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            rec.addDocumentInstance(di1);
            
            //complete the consent
            Consent c = ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0).generateConsent();
            c.setConsentGiven(true);
            rec.addConsent(c);
            
            //save the record
            recordId = dao.saveRecord(rec, true, null, "NoUser");
            rec = dao.getRecord(recordId, RetrieveDepth.RS_NO_BINARY).toHibernate();
            
            String newLoc = "New location";
            {
                //get the record
                Record r = dao.getRecord(recordId, RetrieveDepth.RS_NO_BINARY).toHibernate();
                
                //modify a consent reply
                Consent c2 = r.getConsent(ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0));
                c2.setLocation(newLoc);
                
                //save the record again
                dao.saveRecord(r, true, null, "NoUser");
            }
            
            //try to save the outdated record
            try{
                dao.saveRecord(rec, true, null, "NoUser");
                fail("No exception thrown when trying to save out-of-date record");
            }
            catch (ObjectOutOfDateException ex){
                //do nothing
            }
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
            
    }
    
    public void testGetRecordComplete_Success(){
        try{
            String name = "testGetRecordComplete_Success - "+(new Date()).toString();
            DataSet ds = createDataSet(name, true);

            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, ds.getProjectCode(), ds.getId(), 1, "FOO");            

            Long recordId = null;
            {
                Record r = createRecord(ds);
                r.setIdentifier(ids[0]);
                recordId = dao.saveRecord(r, true, null, "NoUser");
            }
            
            Record r = dao.getRecord(recordId, RetrieveDepth.RS_NO_BINARY).toHibernate();
            r.attach(ds);
            
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
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetRecordComplete_InvalidId(){
        try{
            try{
                dao.getRecord(new Long(-1), RetrieveDepth.RS_NO_BINARY);
                fail("Exception should have been thrown when trying to retrieve a record using an invalid id");
            }
            catch(DAOException ex){
                //do nothing
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
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
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, ds.getId(), 3, "FOO");            

            //generate and save three records
            List<Long> recordIds = new ArrayList<Long>();
            for (int i=0; i<3; i++){
                Record r = ds.generateInstance();
                r.setIdentifier(ids[i]);
                DocumentInstance di = doc.generateInstance(doc.getOccurrence(0));
                r.addDocumentInstance(di);
                recordIds.add(dao.saveRecord(r, true, null, "NoUser"));
            }
            
            List<Record> records = new ArrayList<Record>();
            org.psygrid.data.model.dto.RecordDTO[] dtoRecords = dao.getRecords(ds.getId());
            for ( int i=0; i<dtoRecords.length; i++ ){
                org.psygrid.data.model.dto.RecordDTO r = dtoRecords[i];
                if ( null != r ){
                    records.add(r.toHibernate());
                }
            }
            assertEquals("List of records contains the wrong number of items",3,records.size());
            for(Record r:records){
                assertTrue("List of record ids does not contain record with id = "+r.getId(),recordIds.contains(r.getId()));
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
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
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, ds.getId(), 6, "FOO");            

            //generate and save six records
            List<Long> recordIdsStat1 = new ArrayList<Long>();
            List<Long> recordIdsStat2 = new ArrayList<Long>();
            List<Long> recordIdsStat3 = new ArrayList<Long>();
            for (int i=0; i<6; i++){
                Record r = ds.generateInstance();
                r.setIdentifier(ids[i]);
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
                List<Record> records = new ArrayList<Record>();
                org.psygrid.data.model.dto.RecordDTO[] dtoRecords = dao.getRecordsByStatus(ds.getId(), ds.getStatus(0).getId());
                assertEquals("List of records contains the wrong number of items",1,dtoRecords.length);
                for ( int i=0; i<dtoRecords.length; i++ ){
                    org.psygrid.data.model.dto.RecordDTO r = dtoRecords[i];
                    if ( null != r ){
                        records.add(r.toHibernate());
                    }
                }
                for(Record r:records){
                    assertTrue("List of record ids does not contain record with id = "+r.getId(),recordIdsStat1.contains(r.getId()));
                }
            }
            
            //Test for "Status 2" records
            {
                List<Record> records = new ArrayList<Record>();
                org.psygrid.data.model.dto.RecordDTO[] dtoRecords = dao.getRecordsByStatus(ds.getId(), ds.getStatus(1).getId());
                assertEquals("List of records contains the wrong number of items",2,dtoRecords.length);
                for ( int i=0; i<dtoRecords.length; i++ ){
                    org.psygrid.data.model.dto.RecordDTO r = dtoRecords[i];
                    if ( null != r ){
                        records.add(r.toHibernate());
                    }
                }
                for(Record r:records){
                    assertTrue("List of record ids does not contain record with id = "+r.getId(),recordIdsStat2.contains(r.getId()));
                }
            }
            
            //Test for "Status 3" records
            {
                List<Record> records = new ArrayList<Record>();
                org.psygrid.data.model.dto.RecordDTO[] dtoRecords = dao.getRecordsByStatus(ds.getId(), ds.getStatus(2).getId());
                assertEquals("List of records contains the wrong number of items",3,dtoRecords.length);
                for ( int i=0; i<dtoRecords.length; i++ ){
                    org.psygrid.data.model.dto.RecordDTO r = dtoRecords[i];
                    if ( null != r ){
                        records.add(r.toHibernate());
                    }
                }
                for(Record r:records){
                    assertTrue("List of record ids does not contain record with id = "+r.getId(),recordIdsStat3.contains(r.getId()));
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testSaveRecord_Append_OK(){
        try{
            String name = "testSaveRecord_Append_OK - "+(new Date()).toString();
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
            Document d2 = factory.createDocument("D2");
            DocumentOccurrence occ2 = factory.createDocumentOccurrence("Occ2");
            d2.addOccurrence(occ2);
            ds.addDocument(d1);
            ds.addDocument(d2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "FOO");

            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);
            DocumentInstance di1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            r1.addDocumentInstance(di1);
            
            Long r1Id = dao.saveRecord(r1, true, null, "NoUser");
            
            Record r2 = ds.generateInstance();
            r2.setIdentifier(ids[0]);
            DocumentInstance di2 = ds.getDocument(1).generateInstance(ds.getDocument(1).getOccurrence(0));
            r2.addDocumentInstance(di2);
            
            Long r2Id = dao.saveRecord(r2, true, null, "NoUser");
            
            assertEquals("A new record id has been allocated to the 2nd record",r1Id,r2Id);
            
            Record r = dao.getRecord(r1Id, RetrieveDepth.RS_NO_BINARY).toHibernate();
            d1 = (Document)ds.getDocument(0);
            DocumentInstance ei1 = r.getDocumentInstance(d1.getOccurrence(0));
            assertNotNull("The saved record does contain a document instance relating to document "+d1.getName(),ei1);
            d2 = (Document)ds.getDocument(1);
            DocumentInstance ei2 = r.getDocumentInstance(d2.getOccurrence(0));
            assertNotNull("The saved record does contain a document instance relating to document "+d2.getName(),ei2);
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testSaveRecord_Append_OK2(){
        try{
            String name = "testSaveRecord_Append_OK2 - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            Document d1 = factory.createDocument("D1");
            DocumentOccurrence o1 = factory.createDocumentOccurrence("O1");
            DocumentOccurrence o2 = factory.createDocumentOccurrence("O2");
            d1.addOccurrence(o1);
            d1.addOccurrence(o2);
            Document d2 = factory.createDocument("D2");
            ds.addDocument(d1);
            ds.addDocument(d2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "FOO");
            
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);
            DocumentInstance di1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            r1.addDocumentInstance(di1);
            
            Long r1Id = dao.saveRecord(r1, true, null, "NoUser");
            
            Record r2 = ds.generateInstance();
            r2.setIdentifier(ids[0]);
            d1 = (Document)ds.getDocument(0);
            DocumentInstance di2 = d1.generateInstance(d1.getOccurrence(1));
            r2.addDocumentInstance(di2);
            
            Long r2Id = dao.saveRecord(r2, true, null, "NoUser");
            
            assertEquals("A new record id has been allocated to the 2nd record",r1Id,r2Id);

            Record r = dao.getRecord(r1Id, RetrieveDepth.RS_NO_BINARY).toHibernate();
            List<DocumentInstance> insts = r.getDocumentInstances(d1);
            assertEquals("The saved record does contain the correct number of child instances relating to element "+d1.getName(), 2, insts.size());
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testSaveRecord_Append_Error(){
        try{
            String name = "testSaveRecord_Append_Error - "+(new Date()).toString();
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
            ds.addDocument(d1);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "FOO");
            
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);
            DocumentInstance di1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            r1.addDocumentInstance(di1);
            
            Long r1id = dao.saveRecord(r1, true, null, "NoUser");
            
            Record r2 = ds.generateInstance();
            r2.generateIdentifier(ids[0].getIdentifier());
            DocumentInstance di2 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            r2.addDocumentInstance(di2);
            
            try{
                dao.saveRecord(r2, true, null, "NoUser");
                fail("Exception should have been thrown when trying to append a duplicate instance");
            }
            catch(DAOException ex){
                //do nothing
            }
            
            r1 = dao.getRecord(r1id, RetrieveDepth.RS_NO_BINARY).toHibernate();
            assertEquals("The record has the wrong number of instances relating to document 1",1,r1.getDocumentInstances(ds.getDocument(0)).size());
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testSaveRecord_Append_Error2(){
        try{
            String name = "testSaveRecord_Append_Error2 - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            Document d1 = factory.createDocument("D1");
            DocumentOccurrence o1 = factory.createDocumentOccurrence("O1");
            DocumentOccurrence o2 = factory.createDocumentOccurrence("O2");
            d1.addOccurrence(o1);
            d1.addOccurrence(o2);
            Document d2 = factory.createDocument("D2");
            ds.addDocument(d1);
            ds.addDocument(d2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "FOO");
            
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);
            DocumentInstance di1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            r1.addDocumentInstance(di1);
            
            Long r1Id = dao.saveRecord(r1, true, null, "NoUser");
            
            Record r2 = ds.generateInstance();
            r2.generateIdentifier(ids[0].getIdentifier());
            d1 = (Document)ds.getDocument(0);
            DocumentInstance di2 = d1.generateInstance(d1.getOccurrence(1));
            r2.addDocumentInstance(di2);
            
            Long r2Id = dao.saveRecord(r2, true, null, "NoUser");
            
            assertEquals("A new record id has been allocated to the 2nd record",r1Id,r2Id);

            Record r3 = ds.generateInstance();
            r3.generateIdentifier(ids[0].getIdentifier());
            DocumentInstance di3 = d1.generateInstance(d1.getOccurrence(1));
            r3.addDocumentInstance(di3);
            
            try{
                dao.saveRecord(r3, true, null, "NoUser");
                fail("Exception should have been thrown when trying to append a 3rd instance when the occurrence it references already has an instance");
            }
            catch(DAOException ex){
                //do nothing
            }
            
            r1 = dao.getRecord(r1Id, RetrieveDepth.RS_NO_BINARY).toHibernate();
            assertEquals("The record has the wrong number of instances relating to document 1",2,r1.getDocumentInstances(ds.getDocument(0)).size());
            assertEquals("The record has the wrong number of instances relating to document 2",0,r1.getDocumentInstances(ds.getDocument(1)).size());
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testSaveRecord_Append_Error3(){
        try{
            String name = "testSaveRecord_Append_Error3 - "+(new Date()).toString();
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
            ds.addDocument(d1);
            Document d2 = factory.createDocument("D2");
            DocumentOccurrence occ2 = factory.createDocumentOccurrence("Occ2");
            d2.addOccurrence(occ2);
            ds.addDocument(d2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "FOO");
            
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);
            DocumentInstance di1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            r1.addDocumentInstance(di1);
            
            Long r1id = dao.saveRecord(r1, true, null, "NoUser");
            
            Record r2 = ds.generateInstance();
            r2.generateIdentifier(ids[0].getIdentifier());
            DocumentInstance di1a = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            r2.addDocumentInstance(di1a);
            DocumentInstance di2 = ds.getDocument(1).generateInstance(ds.getDocument(1).getOccurrence(0));
            r2.addDocumentInstance(di2);
            
            try{
                dao.saveRecord(r2, true, null, "NoUser");
                fail("Exception should have been thrown when trying to append a duplicate instance");
            }
            catch(DAOException ex){
                //do nothing
            }
            
            r1 = dao.getRecord(r1id, RetrieveDepth.RS_NO_BINARY).toHibernate();
            assertEquals("The record has the wrong number of instances relating to document 1",1,r1.getDocumentInstances(ds.getDocument(0)).size());
            assertEquals("The record has the wrong number of instances relating to document 2",0,r1.getDocumentInstances(ds.getDocument(1)).size());
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testSaveRecord_Append_Overwrite(){
        try{
            String name = "testSaveRecord_Append_Overwrite - "+(new Date()).toString();
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
            ds.addDocument(d1);
            Status stat1 = factory.createStatus("Incomplete", 0);
            d1.addStatus(stat1);
            Section sec1 = factory.createSection("Sec1");
            SectionOccurrence so1 = factory.createSectionOccurrence("SO1");
            sec1.addOccurrence(so1);
            d1.addSection(sec1);
            TextEntry te1 = factory.createTextEntry("TE1");
            d1.addEntry(te1);
            te1.setSection(sec1);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "FOO");
            
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);
            DocumentInstance di1 = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            r1.addDocumentInstance(di1);
            BasicResponse resp = (BasicResponse)ds.getDocument(0).getEntry(0).generateInstance(ds.getDocument(0).getSection(0).getOccurrence(0));
            di1.addResponse(resp);
            ITextValue tv1 = ((TextEntry)ds.getDocument(0).getEntry(0)).generateValue();
            tv1.setValue("Foo");
            resp.setValue(tv1);
            
            Long r1id = dao.saveRecord(r1, true, null, "NoUser");
            r1 = dao.getRecordsDocumentsByStatus(ids[0].getIdentifier(), "Incomplete").toHibernate();
            
            di1 = r1.getDocumentInstance(ds.getDocument(0).getOccurrence(0));
            resp = (BasicResponse)di1.getResponse(ds.getDocument(0).getEntry(0), ds.getDocument(0).getSection(0).getOccurrence(0));
            ITextValue tv2 = ((TextEntry)ds.getDocument(0).getEntry(0)).generateValue();
            tv2.setValue("Bar");
            resp.setValue(tv2);
            
            Long r2id = dao.saveRecord(r1, true, null, "NoUser");
            assertEquals("Record has been allocated a new identifier",r1id, r2id);
            
            r1 = dao.getRecord(r1id, RetrieveDepth.RS_NO_BINARY).toHibernate();
            assertEquals("The record has the wrong number of instances relating to document 1",1,r1.getDocumentInstances(ds.getDocument(0)).size());

            di1 = r1.getDocumentInstance(ds.getDocument(0).getOccurrence(0));
            resp = (BasicResponse)di1.getResponse(ds.getDocument(0).getEntry(0), ds.getDocument(0).getSection(0).getOccurrence(0));
            assertEquals("The value of the response is wrong","Bar",((ITextValue)resp.getValue()).getValue());
            List<Provenance> provs = resp.getProvenance();
            assertEquals("The provenance list has the wrong number of items",2,provs.size());
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testSaveRecord_Consent(){
        try{
            String name = "testSaveRecord_Consent - "+(new Date()).toString();
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
            ds.addDocument(d1);
            ConsentFormGroup cfg1 = factory.createConsentFormGroup();
            PrimaryConsentForm pcf1 = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            pcf1.addAssociatedConsentForm(acf1);
            pcf1.addAssociatedConsentForm(acf2);
            cfg1.addConsentForm(pcf1);
            PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
            cfg1.addConsentForm(pcf2);
            ds.addAllConsentFormGroup(cfg1);
            d1.addConsentFormGroup(cfg1);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            
            ds = dao.getDataSet(dsId).toHibernate();
            d1 = (Document)ds.getDocument(0);
            pcf1 = d1.getConsentFormGroup(0).getConsentForm(0);
            acf1 = pcf1.getAssociatedConsentForm(0);
            acf2 = pcf1.getAssociatedConsentForm(1);
            pcf2 = d1.getConsentFormGroup(0).getConsentForm(1);
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 6, "FOO");            

            {
                //record with pcf1 and its children completed
                Record r1 = ds.generateInstance();
                r1.setIdentifier(ids[0]);
                DocumentInstance di1 = d1.generateInstance(d1.getOccurrence(0));
                r1.addDocumentInstance(di1);
                
                Consent pc1 = pcf1.generateConsent();
                pc1.setConsentGiven(true);
                Consent ac1 = acf1.generateConsent();
                ac1.setConsentGiven(true);
                Consent ac2 = acf2.generateConsent();
                ac2.setConsentGiven(true);
                
                r1.addConsent(pc1);
                r1.addConsent(ac1);
                r1.addConsent(ac2);
                
                dao.saveRecord(r1, true, null, "NoUser");
            }
            
            {
                //record with pcf2 completed
                Record r1 = ds.generateInstance();
                r1.setIdentifier(ids[1]);
                DocumentInstance di1 = d1.generateInstance(d1.getOccurrence(0));
                r1.addDocumentInstance(di1);
                
                Consent pc2 = pcf2.generateConsent();
                pc2.setConsentGiven(true);
                
                r1.addConsent(pc2);
                
                dao.saveRecord(r1, true, null, "NoUser");
            }
            
            {
                //record with pcf1 and ONLY 1 child completed
                Record r1 = ds.generateInstance();
                r1.setIdentifier(ids[3]);
                DocumentInstance di1 = d1.generateInstance(d1.getOccurrence(0));
                r1.addDocumentInstance(di1);
                
                Consent pc1 = pcf1.generateConsent();
                pc1.setConsentGiven(true);
                Consent ac1 = acf1.generateConsent();
                ac1.setConsentGiven(true);
                
                r1.addConsent(pc1);
                r1.addConsent(ac1);
                
                try{
                    dao.saveRecord(r1, true, null, "NoUser");
                    fail("Exception should have been thrown when trying to save record without all consent completed (2)");
                }
                catch(NoConsentException ex){
                    //do nothing
                }
            }
            
            {
                //record with pcf1 and NO children completed
                Record r1 = ds.generateInstance();
                r1.setIdentifier(ids[4]);
                DocumentInstance di1 = d1.generateInstance(d1.getOccurrence(0));
                r1.addDocumentInstance(di1);
                
                Consent pc1 = pcf1.generateConsent();
                pc1.setConsentGiven(true);
                Consent ac1 = acf1.generateConsent();
                ac1.setConsentGiven(false);
                
                r1.addConsent(pc1);
                r1.addConsent(ac1);
                
                try{
                    dao.saveRecord(r1, true, null, "NoUser");
                    fail("Exception should have been thrown when trying to save record without all consent completed (3)");
                }
                catch(NoConsentException ex){
                    //do nothing
                }
            }
            
            {
                //record with NO consent forms completed positively
                Record r1 = ds.generateInstance();
                r1.setIdentifier(ids[5]);
                DocumentInstance di1 = d1.generateInstance(d1.getOccurrence(0));
                r1.addDocumentInstance(di1);
                
                Consent pc1 = pcf1.generateConsent();
                pc1.setConsentGiven(false);
                Consent ac1 = acf1.generateConsent();
                ac1.setConsentGiven(false);
                
                r1.addConsent(pc1);
                r1.addConsent(ac1);
                
                try{
                    dao.saveRecord(r1, true, null, "NoUser");
                    fail("Exception should have been thrown when trying to save record without all consent completed (4)");
                }
                catch(NoConsentException ex){
                    //do nothing
                }
            }
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testSaveRecord_Consent2(){
        try{
            String name = "testSaveRecord_Consent2 - "+(new Date()).toString();
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
            ds.addDocument(d1);
            ConsentFormGroup cfg1 = factory.createConsentFormGroup();
            PrimaryConsentForm pcf1 = factory.createPrimaryConsentForm();
            cfg1.addConsentForm(pcf1);
            ds.addAllConsentFormGroup(cfg1);
            d1.addConsentFormGroup(cfg1);
            
            ConsentFormGroup cfg2 = factory.createConsentFormGroup();
            PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
            cfg2.addConsentForm(pcf2);
            ds.addAllConsentFormGroup(cfg2);
            d1.addConsentFormGroup(cfg2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            
            ds = dao.getDataSet(dsId).toHibernate();
            d1 = (Document)ds.getDocument(0);
            pcf1 = d1.getConsentFormGroup(0).getConsentForm(0);
            pcf2 = d1.getConsentFormGroup(1).getConsentForm(0);
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 2, "FOO");            

            {
                //record with pcf1 and pcf2 completed
                Record r1 = ds.generateInstance();
                r1.setIdentifier(ids[0]);
                DocumentInstance di1 = d1.generateInstance(d1.getOccurrence(0));
                r1.addDocumentInstance(di1);
                
                Consent pc1 = pcf1.generateConsent();
                pc1.setConsentGiven(true);
                Consent pc2 = pcf2.generateConsent();
                pc2.setConsentGiven(true);
                
                r1.addConsent(pc1);
                r1.addConsent(pc2);
                
                dao.saveRecord(r1, true, null, "NoUser");
            }
            
            {
                //record with only pcf1 completed
                Record r1 = ds.generateInstance();
                r1.setIdentifier(ids[1]);
                DocumentInstance di1 = d1.generateInstance(d1.getOccurrence(0));
                r1.addDocumentInstance(di1);
                
                Consent pc1 = pcf1.generateConsent();
                pc1.setConsentGiven(true);
                
                r1.addConsent(pc1);
                
                try{
                    dao.saveRecord(r1, true, null, "NoUser");
                    fail("Exception should have been thrown when trying to save record without all consent completed (1)");
                }
                catch(NoConsentException ex){
                    //do nothing
                }
            }
            
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testSaveRecord_Append_Consent(){
        try{
            String name = "testSaveRecord_Consent - "+(new Date()).toString();
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
            ds.addDocument(d1);
            Document d2 = factory.createDocument("D2");
            DocumentOccurrence occ2 = factory.createDocumentOccurrence("Occ2");
            d2.addOccurrence(occ2);
            ds.addDocument(d2);
            ConsentFormGroup cfg1 = factory.createConsentFormGroup();
            PrimaryConsentForm pcf1 = factory.createPrimaryConsentForm();
            cfg1.addConsentForm(pcf1);
            ds.addAllConsentFormGroup(cfg1);
            d1.addConsentFormGroup(cfg1);
            ConsentFormGroup cfg2 = factory.createConsentFormGroup();
            PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
            cfg2.addConsentForm(pcf2);
            ds.addAllConsentFormGroup(cfg2);
            d2.addConsentFormGroup(cfg2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            
            ds = dao.getDataSet(dsId).toHibernate();
            d1 = ds.getDocument(0);
            d2 = ds.getDocument(1);
            pcf1 = d1.getConsentFormGroup(0).getConsentForm(0);
            pcf2 = d2.getConsentFormGroup(0).getConsentForm(0);

            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 2, "FOO");

            {
                //add a record containing f1, then add a record containing f2
                //all consents completed
                Record r1 = ds.generateInstance();
                r1.setIdentifier(ids[0]);
                DocumentInstance fi1 = d1.generateInstance(d1.getOccurrence(0));
                r1.addDocumentInstance(fi1);
                Consent c1 = pcf1.generateConsent();
                c1.setConsentGiven(true);
                Consent c2 = pcf2.generateConsent();
                c2.setConsentGiven(true);
                r1.addConsent(c1);
                r1.addConsent(c2);
                
                dao.saveRecord(r1, true, null, "NoUser");
                
                Record r2 = ds.generateInstance();
                r2.setIdentifier(ids[0]);
                DocumentInstance fi2 = d2.generateInstance(d2.getOccurrence(0));
                r2.addDocumentInstance(fi2);
                
                dao.saveRecord(r2, true, null, "NoUser");
            }

            {
                //add a record containing f1, then add a record containing f2
                //consent for f2 NOT completed
                Record r1 = ds.generateInstance();
                r1.setIdentifier(ids[1]);
                DocumentInstance fi1 = d1.generateInstance(d1.getOccurrence(0));
                r1.addDocumentInstance(fi1);
                Consent c1 = pcf1.generateConsent();
                c1.setConsentGiven(true);
                Consent c2 = pcf2.generateConsent();
                c2.setConsentGiven(false);
                r1.addConsent(c1);
                r1.addConsent(c2);
                
                dao.saveRecord(r1, true, null, "NoUser");
                
                Record r2 = ds.generateInstance();
                r2.setIdentifier(ids[1]);
                DocumentInstance fi2 = d2.generateInstance(d2.getOccurrence(0));
                r2.addDocumentInstance(fi2);
                
                try{
                    dao.saveRecord(r2, true, null, "NoUser");
                    fail("Exception should have been thrown when trying to save record without adequate consent");
                }
                catch(NoConsentException ex){
                    //do nothing
                }
            }
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testAttach(){
        try{
            String name = "testAttach - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            Unit u1 = factory.createUnit("Unit 1");
            Unit u2 = factory.createUnit("Unit 2");
            ds.addUnit(u1);
            ds.addUnit(u2);
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
            pcf.setQuestion("Blah");
            cfg.addConsentForm(pcf);
            ds.addAllConsentFormGroup(cfg);
            Status dsStat1 = factory.createStatus("DS Status 1", 1);
            Status dsStat2 = factory.createStatus("DS Status 2", 2);
            ds.addStatus(dsStat1);
            ds.addStatus(dsStat2);
            Document d1 = factory.createDocument("D1");
            ds.addDocument(d1);
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            d1.addOccurrence(occ1);
            d1.addConsentFormGroup(cfg);
            Status status1 = factory.createStatus("Status 1", 1);
            Status status2 = factory.createStatus("Status 2", 2);
            d1.addStatus(status1);
            d1.addStatus(status2);
            Section s1 = factory.createSection("S1");
            d1.addSection(s1);
            SectionOccurrence so1 = factory.createSectionOccurrence("SO1");
            so1.setMultipleAllowed(true);
            s1.addOccurrence(so1);
            OptionEntry oe = factory.createOptionEntry("OE1");
            oe.setSection(s1);
            oe.addUnit(u1);
            oe.addUnit(u2);
            Option o1 = factory.createOption("O1", "O1");
            Option o2 = factory.createOption("O2", "O2");
            oe.addOption(o1);
            oe.addOption(o2);
            d1.addEntry(oe);
            Section s2 = factory.createSection("S2");
            d1.addSection(s2);
            SectionOccurrence so2 = factory.createSectionOccurrence("SO2");
            s2.addOccurrence(so2);
            OptionEntry oe2 = factory.createOptionEntry("OE2");
            d1.addEntry(oe2);
            oe2.setSection(s2);
            Option o3 = factory.createOption("O3", "O3");
            Option o4 = factory.createOption("O4", "O4");
            oe2.addOption(o3);
            oe2.addOption(o4);
            CompositeEntry ce1 = factory.createComposite("CE1");
            d1.addEntry(ce1);
            ce1.setSection(s2);
            TextEntry te1 = factory.createTextEntry("TE1");
            ce1.addEntry(te1);
            te1.addUnit(u1);
            te1.addUnit(u2);
            
            //save dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, ds.getId(), 1, "FOO");            

            d1 = ds.getDocument(0);
            cfg = ds.getAllConsentFormGroup(0);
            pcf = cfg.getConsentForm(0);
            dsStat1 = ds.getStatus(0);
            occ1 = d1.getOccurrence(0);
            status1 = d1.getStatus(0);
            status2 = d1.getStatus(0);
            s1 = d1.getSection(0);
            so1 = s1.getOccurrence(0);
            oe = (OptionEntry)d1.getEntry(0);
            s2 = d1.getSection(1);
            so2 = s2.getOccurrence(0);
            oe2 = (OptionEntry)d1.getEntry(1);
            ce1 = (CompositeEntry)d1.getEntry(2);
            te1 = (TextEntry)ce1.getEntry(0);
            
            //create a record
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);
            Consent con1 = pcf.generateConsent();
            con1.setConsentGiven(true);
            r1.addConsent(con1);
            DocumentInstance di1 = d1.generateInstance(occ1);
            r1.addDocumentInstance(di1);
            SecOccInstance soi1 = so1.generateInstance();
            di1.addSecOccInstance(soi1);
            BasicResponse br1 = oe.generateInstance(soi1);
            di1.addResponse(br1);
            IOptionValue ov1 = oe.generateValue();
            ov1.setValue(oe.getOption(1));
            ov1.setUnit(oe.getUnit(0));
            br1.setValue(ov1);
            BasicResponse br2 = oe2.generateInstance(so2);
            di1.addResponse(br2);
            IOptionValue ov2a = oe2.generateValue();
            ov2a.setValue(oe2.getOption(0));
            br2.setValue(ov2a);
            IOptionValue ov2b = oe2.generateValue();
            ov2b.setValue(oe2.getOption(1));
            br2.setValue(ov2b);
            CompositeResponse cr1 = ce1.generateInstance(so2);
            di1.addResponse(cr1);
            CompositeRow row1 = cr1.createCompositeRow();
            BasicResponse br3 = te1.generateInstance(so2);
            row1.addResponse(br3);
            ITextValue tv1 = te1.generateValue();
            tv1.setValue("Foo");
            tv1.setUnit(te1.getUnit(1));
            br3.setValue(tv1);
            CompositeRow row2 = cr1.createCompositeRow();
            BasicResponse br4 = te1.generateInstance(so2);
            row2.addResponse(br4);
            ITextValue tv2 = te1.generateValue();
            tv2.setValue("Bar");
            br4.setValue(tv2);
            cr1.removeCompositeRow(1);
            
            r1.detach();
            
            r1.attach(ds);
            
            assertTrue("Record references the wrong dataset", ds == r1.getDataSet());
            assertTrue("Record references the wrong status", dsStat1 == r1.getStatus());
            assertTrue("Consent references the wrong consent form", pcf == con1.getConsentForm());
            assertTrue("Doc instance references the wrong occurrence", occ1 == di1.getOccurrence());
            assertTrue("Doc instance references the wrong status", status1 == di1.getStatus());
            assertTrue("Basic response 1 references the wrong entry", oe == br1.getEntry());
            assertTrue("Sec occ inst references the wrong sec occ", so1 == soi1.getSectionOccurrence());
            assertTrue("Option value 1 references the wrong option", oe.getOption(1) == ov1.getValue());
            assertTrue("Option value 1 references the wrong unit", oe.getUnit(0) == ov1.getUnit());
            assertTrue("Basic response 2 references the wrong entry", oe2 == br2.getEntry());
            assertTrue("Basic response 2 references the wrong sec occ", so2 == br2.getSectionOccurrence());
            assertTrue("Composite response 1 references the wrong entry", ce1 == cr1.getEntry());
            assertTrue("Composite response 1 references the wrong sec occ", so2 == cr1.getSectionOccurrence());
            assertTrue("Basic response 3 references the wrong entry", te1 == br3.getEntry());
            assertTrue("Basic response 3 references the wrong sec occ", so2 == br3.getSectionOccurrence());
            assertTrue("Text value 1 references the wrong unit", te1.getUnit(1) == tv1.getUnit());
            
            //check provenance of response to option entry 2
            Provenance prov1 = br2.getProvenance().get(0);
            {
                IOptionValue ov = (IOptionValue)prov1.getCurrentValue();
                assertTrue("Option value that is current value in provenance 1 references the wrong option", oe2.getOption(0) == ov.getValue());
            }
            Provenance prov2 = br2.getProvenance().get(1);
            {
                IOptionValue ov = (IOptionValue)prov2.getCurrentValue();
                assertTrue("Option value that is current value in provenance 2 references the wrong option", oe2.getOption(1) == ov.getValue());
            }
            {
                IOptionValue ov = (IOptionValue)prov2.getPrevValue();
                assertTrue("Option value that is prev value in provenance 2 references the wrong option", oe2.getOption(0) == ov.getValue());
            }
           
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testAttach_Status(){
        try{
            String name = "testAttach_Status - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            Status dsStat1 = factory.createStatus("DS Status 1", 1);
            Status dsStat2 = factory.createStatus("DS Status 2", 2);
            dsStat1.addStatusTransition(dsStat2);
            ds.addStatus(dsStat1);
            ds.addStatus(dsStat2);
            
            //save dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, ds.getId(), 1, "FOO");            

            dsStat1 = ds.getStatus(0);
            dsStat2 = ds.getStatus(1);
            
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);

            Long rsId = dao.saveRecord(r1.toDTO(), true, null, "NoUser");
            dao.changeStatus(rsId, dsStat2.getId(), "NoUser");
            r1 = dao.getRecord(rsId, RetrieveDepth.RS_NO_BINARY).toHibernate();
            r1.attach(ds);
            
            r1.detach();
            r1.attach(ds);
            
            //check provenance of status of record
            Provenance prov1 = r1.getProvenance().get(0);
            {
                Status stat1 = (Status)prov1.getCurrentValue();
                assertTrue("Status that is current value in provenance 1 is wrong", dsStat1 == stat1);
            }
            Provenance prov2 = r1.getProvenance().get(1);
            {
                Status stat2 = (Status)prov2.getCurrentValue();
                assertTrue("Status that is current value in provenance 2 is wrong", dsStat2 == stat2);
            }
            {
                Status stat1 = (Status)prov2.getPrevValue();
                assertTrue("Status that is prev value in provenance 2 is wrong", dsStat1 == stat1);
            }
            
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testDetach(){
        try{
            String name = "testDetach - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            Unit u1 = factory.createUnit("Unit 1");
            Unit u2 = factory.createUnit("Unit 2");
            ds.addUnit(u1);
            ds.addUnit(u2);
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
            pcf.setQuestion("Blah");
            cfg.addConsentForm(pcf);
            ds.addAllConsentFormGroup(cfg);
            Status dsStat1 = factory.createStatus("DS Status 1", 1);
            Status dsStat2 = factory.createStatus("DS Status 2", 2);
            ds.addStatus(dsStat1);
            ds.addStatus(dsStat2);
            Document d1 = factory.createDocument("D1");
            ds.addDocument(d1);
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            d1.addOccurrence(occ1);
            d1.addConsentFormGroup(cfg);
            Status status1 = factory.createStatus("Status 1", 1);
            Status status2 = factory.createStatus("Status 2", 2);
            d1.addStatus(status1);
            d1.addStatus(status2);
            Section s1 = factory.createSection("S1");
            d1.addSection(s1);
            SectionOccurrence so1 = factory.createSectionOccurrence("SO1");
            so1.setMultipleAllowed(true);
            s1.addOccurrence(so1);
            OptionEntry oe = factory.createOptionEntry("OE1");
            oe.setSection(s1);
            oe.addUnit(u1);
            oe.addUnit(u2);
            Option o1 = factory.createOption("O1", "O1");
            Option o2 = factory.createOption("O2", "O2");
            oe.addOption(o1);
            oe.addOption(o2);
            d1.addEntry(oe);
            Section s2 = factory.createSection("S2");
            d1.addSection(s2);
            SectionOccurrence so2 = factory.createSectionOccurrence("SO2");
            s2.addOccurrence(so2);
            OptionEntry oe2 = factory.createOptionEntry("OE2");
            d1.addEntry(oe2);
            oe2.setSection(s2);
            Option o3 = factory.createOption("O3", "O3");
            Option o4 = factory.createOption("O4", "O4");
            oe2.addOption(o3);
            oe2.addOption(o4);
            CompositeEntry ce1 = factory.createComposite("CE1");
            d1.addEntry(ce1);
            ce1.setSection(s2);
            TextEntry te1 = factory.createTextEntry("TE1");
            ce1.addEntry(te1);
            te1.addUnit(u1);
            te1.addUnit(u2);
            
            //save dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, ds.getId(), 1, "FOO");            

            d1 = ds.getDocument(0);
            cfg = ds.getAllConsentFormGroup(0);
            pcf = cfg.getConsentForm(0);
            dsStat1 = ds.getStatus(0);
            occ1 = d1.getOccurrence(0);
            status1 = d1.getStatus(0);
            status2 = d1.getStatus(0);
            s1 = d1.getSection(0);
            so1 = s1.getOccurrence(0);
            oe = (OptionEntry)d1.getEntry(0);
            s2 = d1.getSection(1);
            so2 = s2.getOccurrence(0);
            oe2 = (OptionEntry)d1.getEntry(1);
            ce1 = (CompositeEntry)d1.getEntry(2);
            te1 = (TextEntry)ce1.getEntry(0);
            
            //create a record
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);
            Consent con1 = pcf.generateConsent();
            con1.setConsentGiven(true);
            r1.addConsent(con1);
            DocumentInstance di1 = d1.generateInstance(occ1);
            r1.addDocumentInstance(di1);
            SecOccInstance soi1 = so1.generateInstance();
            di1.addSecOccInstance(soi1);
            BasicResponse br1 = oe.generateInstance(soi1);
            di1.addResponse(br1);
            IOptionValue ov1 = oe.generateValue();
            ov1.setValue(oe.getOption(1));
            ov1.setUnit(oe.getUnit(0));
            br1.setValue(ov1);
            BasicResponse br2 = oe2.generateInstance(so2);
            di1.addResponse(br2);
            IOptionValue ov2a = oe2.generateValue();
            ov2a.setValue(oe2.getOption(0));
            br2.setValue(ov2a);
            IOptionValue ov2b = oe2.generateValue();
            ov2b.setValue(oe2.getOption(1));
            br2.setValue(ov2b);
            CompositeResponse cr1 = ce1.generateInstance(so2);
            di1.addResponse(cr1);
            CompositeRow row1 = cr1.createCompositeRow();
            BasicResponse br3 = te1.generateInstance(so2);
            row1.addResponse(br3);
            ITextValue tv1 = te1.generateValue();
            tv1.setValue("Foo");
            tv1.setUnit(te1.getUnit(1));
            br3.setValue(tv1);
            CompositeRow row2 = cr1.createCompositeRow();
            BasicResponse br4 = te1.generateInstance(so2);
            row2.addResponse(br4);
            ITextValue tv2 = te1.generateValue();
            tv2.setValue("Bar");
            br4.setValue(tv2);
            cr1.removeCompositeRow(1);
            
            r1.detach();
            
            assertEquals("Record references the wrong dataset", ds.getId(), r1.getDataSet().getId());
            assertNull("Dataset referenced by record has non null name", r1.getDataSet().getName());            
            assertEquals("Record references the wrong status", dsStat1.getId(), r1.getStatus().getId());
            assertNull("Status referenced by record has non null name", r1.getStatus().getShortName());            
            assertEquals("Consent references the wrong consent form", pcf.getId(), con1.getConsentForm().getId());
            assertNull("Consent form referenced by consent has non null question", con1.getConsentForm().getQuestion());            
            assertEquals("Doc instance references the wrong occurrence", occ1.getId(), di1.getOccurrence().getId());
            assertNull("Document occurrence referenced by doc instance has non null name", di1.getOccurrence().getName());
            assertEquals("Doc instance references the wrong status", status1.getId(), di1.getStatus().getId());
            assertNull("Status referenced by doc instance has non null name", di1.getStatus().getShortName());            
            assertEquals("Basic response 1 references the wrong entry", oe.getId(), br1.getEntry().getId());
            assertNull("Entry referenced by BR1 has non null name", br1.getEntry().getName());
            assertEquals("Sec occ inst references the wrong sec occ", so1.getId(), soi1.getSectionOccurrence().getId());
            assertNull("Sec occ referenced by sec occ inst has non null name", soi1.getSectionOccurrence().getName());
            assertEquals("Option value 1 references the wrong option", oe.getOption(1).getId(), ov1.getValue().getId());
            assertNull("Option referenced by ov1 inst has non null name", ov1.getValue().getName());
            assertEquals("Option value 1 references the wrong unit", oe.getUnit(0).getId(), ov1.getUnit().getId());
            assertNull("Unit referenced by ov1 inst has non null abbreviation", ov1.getUnit().getAbbreviation());
            assertEquals("Basic response 2 references the wrong entry", oe2.getId(), br2.getEntry().getId());
            assertNull("Entry referenced by BR2 has non null name", br2.getEntry().getName());
            assertEquals("Basic response 2 references the wrong sec occ", so2.getId(), br2.getSectionOccurrence().getId());
            assertNull("Sec occ referenced by BR2 has non null name", br2.getSectionOccurrence().getName());
            assertEquals("Composite response 1 references the wrong entry", ce1.getId(), cr1.getEntry().getId());
            assertNull("Entry referenced by CR1 has non null name", cr1.getEntry().getName());
            assertEquals("Composite response 1 references the wrong sec occ", so2.getId(), cr1.getSectionOccurrence().getId());
            assertNull("Sec occ referenced by CR1 has non null name", cr1.getSectionOccurrence().getName());
            assertEquals("Basic response 3 references the wrong entry", te1.getId(), br3.getEntry().getId());
            assertNull("Entry referenced by BR3 has non null name", br3.getEntry().getName());
            assertEquals("Basic response 3 references the wrong sec occ", so2.getId(), br3.getSectionOccurrence().getId());
            assertNull("Sec occ referenced by BR3 has non null name", br3.getSectionOccurrence().getName());
            assertEquals("Text value 1 references the wrong unit", te1.getUnit(1).getId(), tv1.getUnit().getId());
            assertNull("Unit referenced by tv1 has non null abbreviation", tv1.getUnit().getAbbreviation());
            
            //check provenance of response to option entry 2
            Provenance prov1 = br2.getProvenance().get(0);
            {
                IOptionValue ov = (IOptionValue)prov1.getCurrentValue();
                assertEquals("Option value that is current value in provenance 1 references the wrong option", oe2.getOption(0).getId(), ov.getValue().getId());
                assertNull("Option referenced by current value in provenance 1 has non null name", ov.getValue().getName());                    
            }
            Provenance prov2 = br2.getProvenance().get(1);
            {
                IOptionValue ov = (IOptionValue)prov2.getCurrentValue();
                assertEquals("Option value that is current value in provenance 2 references the wrong option", oe2.getOption(1).getId(), ov.getValue().getId());
                assertNull("Option referenced by current value in provenance 2 has non null name", ov.getValue().getName());                    
            }
            {
                IOptionValue ov = (IOptionValue)prov2.getPrevValue();
                assertEquals("Option value that is prev value in provenance 2 references the wrong option", oe2.getOption(0).getId(), ov.getValue().getId());
                assertNull("Option referenced by prev value in provenance 2 has non null name", ov.getValue().getName());                    
            }
           
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testDetach_Status(){
        try{
            String name = "testDetach_Status - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            ds.addGroup(grp1);
            Status dsStat1 = factory.createStatus("DS Status 1", 1);
            Status dsStat2 = factory.createStatus("DS Status 2", 2);
            dsStat1.addStatusTransition(dsStat2);
            ds.addStatus(dsStat1);
            ds.addStatus(dsStat2);
            
            //save dataset
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //get identifiers
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, ds.getId(), 1, "FOO");            

            dsStat1 = ds.getStatus(0);
            dsStat2 = ds.getStatus(1);
            
            Record r1 = ds.generateInstance();
            r1.setIdentifier(ids[0]);

            Long rsId = dao.saveRecord(r1.toDTO(), true, null, "NoUser");
            dao.changeStatus(rsId, dsStat2.getId(), "NoUser");
            r1 = dao.getRecord(rsId, RetrieveDepth.RS_NO_BINARY).toHibernate();
            r1.attach(ds);
            
            r1.detach();
            
            //check provenance of status of record
            Provenance prov1 = r1.getProvenance().get(0);
            {
                Status stat1 = (Status)prov1.getCurrentValue();
                assertEquals("Status that is current value in provenance 1 is wrong", dsStat1.getId(), stat1.getId());
                assertNull("Status referenced by current value in provenance 1 has non null name", stat1.getShortName());                    
            }
            Provenance prov2 = r1.getProvenance().get(1);
            {
                Status stat2 = (Status)prov2.getCurrentValue();
                assertEquals("Status that is current value in provenance 2 is wrong", dsStat2.getId(), stat2.getId());
                assertNull("Status referenced by current value in provenance 2 has non null name", stat2.getShortName());                    
            }
            {
                Status stat1 = (Status)prov2.getPrevValue();
                assertEquals("Status that is prev value in provenance 2 is wrong", dsStat1.getId(), stat1.getId());
                assertNull("Status referenced by prev value in provenance 2 has non null name", stat1.getShortName());                    
            }
            
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetScheduledRemindersForDataset(){
        try{
            //create dataset with scheduled components
            //create a dataset
            DataSet dataSet = factory.createDataset("Test dataset", "Test dataset");
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            dataSet.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            dataSet.addGroup(grp1);

            //add a document scheduled for day 1, reminder for day 1
            Document doc1 = factory.createDocument("Doc 1", "Document for day 1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("DocumentOccurrence for Day 1");
            doc1.addOccurrence(occ1);
            occ1.setScheduleTime(1);
            occ1.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem1 = factory.createReminder(1, TimeUnits.DAYS, ReminderLevel.MILD);
            occ1.addReminder(rem1);
            dataSet.addDocument(doc1);
            
            //add a document scheduled for day 2, reminders for days 1 and 2
            Document doc2 = factory.createDocument("Doc 2", "Document for day 2");
            DocumentOccurrence occ2 = factory.createDocumentOccurrence("DocumentOccurrence for Day 2");
            doc2.addOccurrence(occ2);
            occ2.setScheduleTime(2);
            occ2.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem2a = factory.createReminder(1, TimeUnits.DAYS, ReminderLevel.MILD);
            occ2.addReminder(rem2a);
            Reminder rem2b = factory.createReminder(2, TimeUnits.DAYS, ReminderLevel.MILD);
            occ2.addReminder(rem2b);
            dataSet.addDocument(doc2);
            
            //add a document with two occurrences, scheduled for days 3 and 4 with reminders
            //for days 3 and 4
            Document doc3 = factory.createDocument("Doc 3", "Document 3");
            DocumentOccurrence occ3 = factory.createDocumentOccurrence("DocumentOccurrence for Day 3");
            doc3.addOccurrence(occ3);
            occ3.setScheduleTime(3);
            occ3.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem3 = factory.createReminder(3, TimeUnits.DAYS, ReminderLevel.MILD);
            occ3.addReminder(rem3);
            DocumentOccurrence occ4 = factory.createDocumentOccurrence("DocumentOccurrence for Day 4");
            doc3.addOccurrence(occ4);
            occ4.setScheduleTime(4);
            occ4.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem4 = factory.createReminder(4, TimeUnits.DAYS, ReminderLevel.MILD);
            occ4.addReminder(rem4);
            dataSet.addDocument(doc3);

            //add a document scheduled for day 4, with a reminder for day 4
            Document doc4 = factory.createDocument("Doc 4", "Document for day 4");
            DocumentOccurrence occ5 = factory.createDocumentOccurrence("DocumentOccurrence for Day 3");
            doc4.addOccurrence(occ5);
            occ5.setScheduleTime(4);
            occ5.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem5 = factory.createReminder(4, TimeUnits.DAYS, ReminderLevel.MILD);
            occ5.addReminder(rem5);
            dataSet.addDocument(doc4);
            
            //save and publish the dataset
            Long dsId = dao.saveDataSet(dataSet.toDTO());
            dao.publishDataSet(dsId);
            dataSet = dao.getDataSet(dsId).toHibernate();
            
            //create two records based on the dataset and save them
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 2, "FOO");
            Record rec = dataSet.generateInstance();
            rec.setIdentifier(ids[0]);
            dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            Record rec2 = dataSet.generateInstance();
            rec2.setIdentifier(ids[1]);
            dao.saveRecord(rec2.toDTO(), true, null, "NoUser");
            
            //get reminders for day 1
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);            
            List<SimpleMailMessage> reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("For day 1 should have 4 reminder messages", 4, reminders.size());

            //get reminders for day 2
            cal.add(Calendar.DAY_OF_MONTH, 1);            
            reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("For day 2 should have 2 reminder messages", 2, reminders.size());
            
            //get reminders for day 3
            cal.add(Calendar.DAY_OF_MONTH, 1);            
            reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("For day 3 should have 2 reminder messages", 2, reminders.size());
            
            //get reminders for day 4
            cal.add(Calendar.DAY_OF_MONTH, 1);            
            reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("For day 4 should have 4 reminder messages", 4, reminders.size());
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetScheduledRemindersForDataset2(){
        try{
            //create dataset with scheduled components
            //create a dataset
            DataSet dataSet = factory.createDataset("Test dataset", "Test dataset");
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            dataSet.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            dataSet.addGroup(grp1);

            //add a document scheduled for day 1, reminder for day 1
            Document d1 = factory.createDocument("Document 1", "Document for day 1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            d1.addOccurrence(occ1);
            occ1.setScheduleTime(1);
            occ1.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem1 = factory.createReminder(1, TimeUnits.DAYS, ReminderLevel.MILD);
            occ1.addReminder(rem1);
            dataSet.addDocument(d1);
            
            //add a document scheduled for day 2, reminder for day 2
            Document d2 = factory.createDocument("Document 2", "Document for day 2");
            DocumentOccurrence occ2 = factory.createDocumentOccurrence("Occ1");
            d2.addOccurrence(occ2);
            occ2.setScheduleTime(1);
            occ2.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem2b = factory.createReminder(2, TimeUnits.DAYS, ReminderLevel.MILD);
            occ2.addReminder(rem2b);
            dataSet.addDocument(d2);
            
            //add a document with two occurrences, scheduled for days 3 and 4 with reminders
            //for days 3 and 4
            Document d3 = factory.createDocument("Document 3", "Document 3");
            DocumentOccurrence occ3 = factory.createDocumentOccurrence("DocumentOccurrence for Day 3");
            d3.addOccurrence(occ3);
            occ3.setScheduleTime(1);
            occ3.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem3 = factory.createReminder(3, TimeUnits.DAYS, ReminderLevel.MILD);
            occ3.addReminder(rem3);
            DocumentOccurrence occ4 = factory.createDocumentOccurrence("DocumentOccurrence for Day 4");
            d3.addOccurrence(occ4);
            occ4.setScheduleTime(2);
            occ4.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem4 = factory.createReminder(4, TimeUnits.DAYS, ReminderLevel.MILD);
            occ4.addReminder(rem4);
            dataSet.addDocument(d3);

            //add a document scheduled for day 4, with a reminder for day 4
            Document d4 = factory.createDocument("Folder 4", "Folder for day 4");
            DocumentOccurrence occ5 = factory.createDocumentOccurrence("Occ5");
            d4.addOccurrence(occ5);
            occ5.setScheduleTime(4);
            occ5.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem5 = factory.createReminder(4, TimeUnits.DAYS, ReminderLevel.MILD);
            occ5.addReminder(rem5);
            dataSet.addDocument(d4);
            
            //save and publish the dataset
            Long dsId = dao.saveDataSet(dataSet.toDTO());
            dao.publishDataSet(dsId);
            dataSet = dao.getDataSet(dsId).toHibernate();
            
            //create a record based on the dataset and save it
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 2, "FOO");
            Record rec = dataSet.generateInstance();
            rec.setIdentifier(ids[0]);
            //create instance for document 1
            DocumentInstance doc1i = dataSet.getDocument(0).generateInstance(dataSet.getDocument(0).getOccurrence(0));
            rec.addDocumentInstance(doc1i);
            //create instance for document 2
            DocumentInstance doc2i = dataSet.getDocument(1).generateInstance(dataSet.getDocument(1).getOccurrence(0));
            rec.addDocumentInstance(doc2i);
            //create instance of doc3, occurrence 1
            DocumentInstance doc3i = dataSet.getDocument(2).generateInstance(dataSet.getDocument(2).getOccurrence(0));
            rec.addDocumentInstance(doc3i);
            
            dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            
            //get reminders for day 1
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);            
            List<SimpleMailMessage> reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("For day 1 should have 0 reminder messages", 0, reminders.size());

            //get reminders for day 2
            cal.add(Calendar.DAY_OF_MONTH, 1);            
            reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("For day 2 should have 0 reminder messages", 0, reminders.size());
            
            //get reminders for day 3
            cal.add(Calendar.DAY_OF_MONTH, 1);            
            reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("For day 3 should have 0 reminder messages", 0, reminders.size());
            
            //get reminders for day 4
            cal.add(Calendar.DAY_OF_MONTH, 1);            
            reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("For day 4 should have 2 reminder messages", 2, reminders.size());
                        
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Create a test DataSet that will be used as the basis for all
     * unit tests in this test case.
     * 
     * @param name
     * @return
     * @throws Exception
     */
    private DataSet createDataSet(String name, boolean publish) throws Exception{

        DataSet ds = factory.createDataset(name);
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
        TextEntry te2 = factory.createTextEntry("TE2");
        comp1.addEntry(te2);
        //add a consent form for document 1
        ConsentFormGroup cfg1 = factory.createConsentFormGroup();
        ds.addAllConsentFormGroup(cfg1);
        doc1.addConsentFormGroup(cfg1);
        PrimaryConsentForm cons1 = factory.createPrimaryConsentForm();
        cons1.setReferenceNumber("Cons 1");
        cfg1.addConsentForm(cons1);
        //add two consent forms for document 2, the first with an associated 
        //consent form
        ConsentFormGroup cfg2 = factory.createConsentFormGroup();
        ds.addAllConsentFormGroup(cfg2);
        doc2.addConsentFormGroup(cfg2);
        PrimaryConsentForm cons2 = factory.createPrimaryConsentForm();
        cons2.setReferenceNumber("Cons 2");
        AssociatedConsentForm acf = factory.createAssociatedConsentForm();
        acf.setReferenceNumber("ACF 1");
        cons2.addAssociatedConsentForm(acf);
        cfg2.addConsentForm(cons2);
        PrimaryConsentForm cons3 = factory.createPrimaryConsentForm();
        cons3.setReferenceNumber("Cons 3");
        cfg2.addConsentForm(cons3);
        
        Long dsId = dao.saveDataSet(ds.toDTO());
        
        if ( publish ){
            dao.publishDataSet(dsId);
        }
        ds = dao.getDataSet(dsId).toHibernate();
       
        return ds;
    }
    
    private Record createRecord(DataSet ds) throws ModelException{
        Record r = ds.generateInstance();
        //Document 1 consent
        Consent cons1 = ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0).generateConsent();
        cons1.setConsentGiven(true);
        r.addConsent(cons1);
        //Document 2 consent
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
        
        return r;
    }
    
    public void testGetScheduledRemindersForDataset3(){
        try{
            //create dataset with scheduled components
            //create a dataset
            DataSet dataSet = factory.createDataset("Test dataset", "Test dataset");
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            dataSet.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            dataSet.addGroup(grp1);

            //add a document scheduled for day 1, reminder for day 1
            Document doc1 = factory.createDocument("Doc 1", "Document for day 1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("Occ1");
            doc1.addOccurrence(occ1);
            occ1.setScheduleTime(1);
            occ1.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem1 = factory.createReminder(1, TimeUnits.DAYS, ReminderLevel.MILD);
            occ1.addReminder(rem1);
            dataSet.addDocument(doc1);
            
            //add a document with two occurrences, scheduled for days 3 and 4 with reminders
            //for days 3 and 4
            Document doc3 = factory.createDocument("Doc 3", "Document 3");
            DocumentOccurrence occ2 = factory.createDocumentOccurrence("DocumentOccurrence for Day 3");
            doc3.addOccurrence(occ2);
            occ2.setScheduleTime(3);
            occ2.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem3 = factory.createReminder(3, TimeUnits.DAYS, ReminderLevel.MILD);
            occ2.addReminder(rem3);
            DocumentOccurrence occ3 = factory.createDocumentOccurrence("DocumentOccurrence for Day 4");
            doc3.addOccurrence(occ3);
            occ3.setScheduleTime(4);
            occ3.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem4 = factory.createReminder(4, TimeUnits.DAYS, ReminderLevel.MILD);
            occ3.addReminder(rem4);
            dataSet.addDocument(doc3);

            //add a document scheduled for day 4, with a reminder for day 4
            Document doc4 = factory.createDocument("Doc 4", "Document for day 4");
            DocumentOccurrence occ4 = factory.createDocumentOccurrence("Occ4");
            doc4.addOccurrence(occ4);
            occ4.setScheduleTime(4);
            occ4.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem5 = factory.createReminder(4, TimeUnits.DAYS, ReminderLevel.MILD);
            occ4.addReminder(rem5);
            dataSet.addDocument(doc4);
            
            //save and publish the dataset
            Long dsId = dao.saveDataSet(dataSet.toDTO());
            dao.publishDataSet(dsId);
            dataSet = dao.getDataSet(dsId).toHibernate();
            
            //create a record based on the dataset and save it
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 2, "FOO");
            Record rec = dataSet.generateInstance();
            rec.setIdentifier(ids[0]);
            //create instance for document 1
            DocumentInstance doc1i = dataSet.getDocument(0).generateInstance(dataSet.getDocument(0).getOccurrence(0));
            rec.addDocumentInstance(doc1i);
            //create instance of doc3, occurrence 1
            DocumentInstance doc3i = dataSet.getDocument(1).generateInstance(dataSet.getDocument(1).getOccurrence(0));
            rec.addDocumentInstance(doc3i);
            
            dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            
            //get reminders for day 1
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);            
            List<SimpleMailMessage> reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("For day 1 should have 0 reminder messages", 0, reminders.size());

            //get reminders for day 3
            cal.add(Calendar.DAY_OF_MONTH, 2);            
            reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("For day 3 should have 0 reminder messages", 0, reminders.size());
            
            //get reminders for day 4
            cal.add(Calendar.DAY_OF_MONTH, 1);            
            reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("For day 4 should have 2 reminder messages", 2, reminders.size());
            
        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetScheduledRemindersForDataset4(){
        try{
            //create dataset with scheduled components
            //create a dataset
            DataSet dataSet = factory.createDataset("Test dataset", "Test dataset");
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            dataSet.setProjectCode(projectCode);
            Group grp1 = factory.createGroup("FOO");
            dataSet.addGroup(grp1);
            Status stat1 = factory.createStatus("Active", 0);
            Status stat2 = factory.createStatus("Inactive", 1);
            stat2.setInactive(true);
            stat1.addStatusTransition(stat2);
            dataSet.addStatus(stat1);
            dataSet.addStatus(stat2);

            //add a document scheduled for day 1, reminder for day 1
            Document doc1 = factory.createDocument("Doc 1", "Document for day 1");
            DocumentOccurrence occ1 = factory.createDocumentOccurrence("DocumentOccurrence for Day 1");
            doc1.addOccurrence(occ1);
            occ1.setScheduleTime(1);
            occ1.setScheduleUnits(TimeUnits.DAYS);
            Reminder rem1 = factory.createReminder(1, TimeUnits.DAYS, ReminderLevel.MILD);
            occ1.addReminder(rem1);
            dataSet.addDocument(doc1);
            
            //save and publish the dataset
            Long dsId = dao.saveDataSet(dataSet.toDTO());
            dao.publishDataSet(dsId);
            dataSet = dao.getDataSet(dsId).toHibernate();
            
            //create three records based on the dataset and save them
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 3, "FOO");
            Record rec = dataSet.generateInstance();
            rec.setIdentifier(ids[0]);
            Long rec1id = dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            Record rec2 = dataSet.generateInstance();
            rec2.setIdentifier(ids[1]);
            Long rec2id = dao.saveRecord(rec2.toDTO(), true, null, "NoUser");
            Record rec3 = dataSet.generateInstance();
            rec3.setIdentifier(ids[2]);
            Long rec3id = dao.saveRecord(rec3.toDTO(), true, null, "NoUser");
            
            //get reminders for day 1
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);            
            List<SimpleMailMessage> reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("Wrong number of reminder messages when all records have status=Active", 3, reminders.size());

            //change status of first record to Inactive 
            dao.changeStatus(rec1id, dataSet.getStatus(1).getId(), null);
            
            //get reminders for day 1
            reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("Wrong number of reminder messages when two records have status=Active", 2, reminders.size());

            //change status of second record to Inactive 
            dao.changeStatus(rec2id, dataSet.getStatus(1).getId(), null);
            
            //get reminders for day 1
            reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("Wrong number of reminder messages when one record has status=Active", 1, reminders.size());

            //change status of third record to Inactive 
            dao.changeStatus(rec3id, dataSet.getStatus(1).getId(), null);
            
            //get reminders for day 1
            reminders = dao.getScheduledRemindersForDataset(cal.getTime(), dsId);            
            assertEquals("Wrong number of reminder messages when no records have status=Active", 0, reminders.size());

        }
        catch (Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
}

