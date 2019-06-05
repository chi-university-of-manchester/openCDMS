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

import java.util.Date;

import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.dto.extra.ConsentResult;
import org.psygrid.data.model.dto.extra.ConsentStatusResult;
import org.psygrid.data.model.dto.extra.StatusResult;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.dao.RepositoryDAO;

/**
 * @author Rob Harper
 *
 */
public class RecordDAOTest4 extends DAOTest {

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
    
    public void testSaveRecordDeletedDocInst(){
    	try{
            String name = "testSaveRecordDeletedDocInst - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            ds.addGroup(factory.createGroup("FOO"));
            
            Document doc1 = factory.createDocument("Doc 1", "Doc 1");
            ds.addDocument(doc1);
            
            DocumentOccurrence docOcc1 = factory.createDocumentOccurrence("Occ 1");
            doc1.addOccurrence(docOcc1);
            
            Section sec1 = factory.createSection("Sec 1", "Sec 1");
            doc1.addSection(sec1);
            
            SectionOccurrence secOcc1 = factory.createSectionOccurrence("Sec Occ 1");
            sec1.addOccurrence(secOcc1);
            
            TextEntry te1 = factory.createTextEntry("TE1", "TE1");
            te1.setSection(sec1);
            doc1.addEntry(te1);
    		
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "FOO");
            
            
            Record rec = ds.generateInstance();
            rec.setIdentifier(ids[0]);
            
            doc1 = ds.getDocument(0);
            docOcc1 = doc1.getOccurrence(0);
            DocumentInstance docInst = doc1.generateInstance(docOcc1);
            rec.addDocumentInstance(docInst);
            
            sec1 = doc1.getSection(0);
            secOcc1 = sec1.getOccurrence(0);
            te1 = (TextEntry)doc1.getEntry(0);
            
            BasicResponse br1 = te1.generateInstance(secOcc1);
            docInst.addResponse(br1);
            ITextValue tv1 = te1.generateValue();
            br1.setValue(tv1);
            tv1.setValue("Value 1");
            
            Long recId = dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            
            
            rec = dao.getRecord(recId, RetrieveDepth.RS_COMPLETE).toHibernate();
            rec.attach(ds);
            
            docInst = rec.getDocumentInstance(docOcc1);
            rec.removeDocumentInstance(docInst);
            
            dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            
            
    	}
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetRecordsByGroupsWithOffset(){
    	try{
            String name = "testGetRecordsByGroupsWithOffset - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            ds.addGroup(factory.createGroup("FOO"));
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            int numRecords = 25;
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, numRecords, "FOO");
    		
            for ( int i=0; i<numRecords; i++ ){
                Record rec = ds.generateInstance();
                rec.setIdentifier(ids[i]);
                dao.saveRecord(rec.toDTO(), true, null, "NoUser");
            }
            
            int batchSize = 10;
            
            //Get the first 10 records
            org.psygrid.data.model.dto.RecordDTO[] dtoRecords = dao.getRecordsByGroups(projectCode, new String[]{"FOO"}, new Date(0), batchSize, 0, RetrieveDepth.RS_SUMMARY);            
            assertEquals("Batch of records with offset=0 is wrong size",10,dtoRecords.length);
            for ( int i=0; i<10; i++ ){
            	String identifier = projectCode+"/FOO-"+(i+1);
            	assertEquals("Record at position "+(i+1)+" has the wrong identifier", identifier, dtoRecords[i].getIdentifier().getIdentifier());
            }
            
            //Get records 11-20
            dtoRecords = dao.getRecordsByGroups(projectCode, new String[]{"FOO"}, new Date(0), batchSize, 10, RetrieveDepth.RS_SUMMARY);            
            assertEquals("Batch of records with offset=10 is wrong size",10,dtoRecords.length);
            for ( int i=0; i<10; i++ ){
            	String identifier = projectCode+"/FOO-"+(i+11);
            	assertEquals("Record at position "+(i+11)+" has the wrong identifier", identifier, dtoRecords[i].getIdentifier().getIdentifier());
            }
            
            //Get records 21-25
            dtoRecords = dao.getRecordsByGroups(projectCode, new String[]{"FOO"}, new Date(0), batchSize, 20, RetrieveDepth.RS_SUMMARY);            
            assertEquals("Batch of records with offset=20 is wrong size",5,dtoRecords.length);
            for ( int i=0; i<5; i++ ){
            	String identifier = projectCode+"/FOO-"+(i+21);
            	assertEquals("Record at position "+(i+21)+" has the wrong identifier", identifier, dtoRecords[i].getIdentifier().getIdentifier());
            }
            
            //Get records with offset of 25
            dtoRecords = dao.getRecordsByGroups(projectCode, new String[]{"FOO"}, new Date(0), batchSize, 25, RetrieveDepth.RS_SUMMARY);            
            assertEquals("Batch of records with offset=25 is wrong size",0,dtoRecords.length);
            
            //Get records with offset of 28
            dtoRecords = dao.getRecordsByGroups(projectCode, new String[]{"FOO"}, new Date(0), batchSize, 28, RetrieveDepth.RS_SUMMARY);            
            assertEquals("Batch of records with offset=28 is wrong size",0,dtoRecords.length);
            
    	}
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetDeletedRecordsByGroups(){

    	try{
            String name = "testSaveRecordDeletedDocInst - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());
            ds.setProjectCode(projectCode);
            ds.addGroup(factory.createGroup("FOO"));
            ds.addGroup(factory.createGroup("BAR"));
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            Identifier[] fooIds = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 2, "FOO");
            Identifier[] barIds = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 2, "BAR");
                        
            Record rec1 = ds.generateInstance();
            rec1.setIdentifier(fooIds[0]);
            Record rec2 = ds.generateInstance();
            rec2.setIdentifier(fooIds[1]);
            Record rec3 = ds.generateInstance();
            rec3.setIdentifier(barIds[0]);
            Record rec4 = ds.generateInstance();
            rec4.setIdentifier(barIds[1]);
            
            Long rec1Id = dao.saveRecord(rec1.toDTO(), true, null, "NoUser");
            Long rec2Id = dao.saveRecord(rec2.toDTO(), true, null, "NoUser");
            Long rec3Id = dao.saveRecord(rec3.toDTO(), true, null, "NoUser");
            Long rec4Id = dao.saveRecord(rec4.toDTO(), true, null, "NoUser");
            
            Date referenceDate = new Date();
            
            //Sleep for two seconds just so we are quite sure the reference 
            //date will be earlier than the edited date
            Thread.sleep(2000L);
            
            dao.deleteRecord(fooIds[0].getIdentifier());
            dao.deleteRecord(barIds[0].getIdentifier());
            dao.deleteRecord(barIds[1].getIdentifier());
            
            String[] fooDeletedIds = dao.getDeletedRecordsByGroups(projectCode, new String[]{"FOO"}, referenceDate);
            assertEquals("List of deleted records for group FOO is wrong length", 1, fooDeletedIds.length);
            assertEquals("Deleted record at index 0 for group FOO is wrong", fooIds[0].getIdentifier(), fooDeletedIds[0]);

            String[] barDeletedIds = dao.getDeletedRecordsByGroups(projectCode, new String[]{"BAR"}, referenceDate);
            assertEquals("List of deleted records for group BAR is wrong length", 2, barDeletedIds.length);
            assertEquals("Deleted record at index 0 for group BAR is wrong", barIds[0].getIdentifier(), barDeletedIds[0]);
            assertEquals("Deleted record at index 1 for group BAR is wrong", barIds[1].getIdentifier(), barDeletedIds[1]);

    	}
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }

    	
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
            
            Identifier[] fooIds = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 10, "FOO");
                                  
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
            
            ConsentStatusResult result = dao.getConsentAndStatusInfoForGroups(projectCode, new String[]{"FOO"}, new Date(0));
            
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
            assertEquals("Consent results is wrong size - 1",6,consentResults.length);
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
            assertEquals("Status results is wrong size - 1",6,statusResults.length);
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
            
            //Note the time then sleep for two seconds
            Date lastUpdated = new Date();
            Thread.sleep(2000L);

            //change status of records 1 and 3
            dao.changeStatus(recId1, ds.getStatus(1).getId(), "NoUser");
            dao.changeStatus(recId3, ds.getStatus(1).getId(), "NoUser");

            result = dao.getConsentAndStatusInfoForGroups(projectCode, new String[]{"FOO"}, lastUpdated);

            consentResults = result.getConsentResults();
            assertEquals("Consent results is wrong size - 2",0,consentResults.length);
            
            statusResults = result.getStatusResults();
            assertEquals("Status results is wrong size - 2",2,statusResults.length);
            //Record 1
            assertEquals("Identifier is wrong for status result 2-0", fooIds[0].getIdentifier(), statusResults[0].getIdentifier());
            assertEquals("Record status id is wrong for status result 2-0", recStatus2Id, statusResults[0].getRecStatusId());
            assertEquals("Instance id is wrong for status result 2-0", null, statusResults[0].getInstanceId());
            assertEquals("Occurrence id is wrong for status result 2-0", null, statusResults[0].getOccurrenceId());
            assertEquals("Doc status id is wrong for status result 2-0", null, statusResults[0].getDocStatusId());
            //Record 3
            assertEquals("Identifier is wrong for status result 2-1", fooIds[2].getIdentifier(), statusResults[1].getIdentifier());
            assertEquals("Record status id is wrong for status result 2-1", recStatus2Id, statusResults[1].getRecStatusId());
            assertEquals("Instance id is wrong for status result 2-1", null, statusResults[1].getInstanceId());
            assertEquals("Occurrence id is wrong for status result 2-1", null, statusResults[1].getOccurrenceId());
            assertEquals("Doc status id is wrong for status result 2-1", null, statusResults[1].getDocStatusId());
            
            
            //Note the time then sleep for two seconds
            lastUpdated = new Date();
            Thread.sleep(2000L);
            
            //6. Primary consent only
            Record rec6 = ds.generateInstance();
            rec6.setIdentifier(fooIds[5]);
            Consent c6p = ds.getAllConsentFormGroup(0).getConsentForm(0).generateConsent();
            c6p.setConsentGiven(true);
            rec6.addConsent(c6p);
            
            Long recId6 = dao.saveRecord(rec6.toDTO(), true, null, "NoUser");
            
            result = dao.getConsentAndStatusInfoForGroups(projectCode, new String[]{"FOO"}, lastUpdated);

            consentResults = result.getConsentResults();
            assertEquals("Consent results is wrong size - 3",1,consentResults.length);
            //Record 6
            assertEquals("Identifier is wrong for consent result 3-0", fooIds[5].getIdentifier(), consentResults[0].getIdentifier());
            assertEquals("Consent given is wrong for consent result 3-0", true, consentResults[0].isConsentGiven());
            assertEquals("Consent form id is wrong for consent result 3-0", pcfId, consentResults[0].getConsentFormId());
            
            statusResults = result.getStatusResults();
            assertEquals("Status results is wrong size - 3",1,statusResults.length);
            //Record 6
            assertEquals("Identifier is wrong for status result 3-0", fooIds[5].getIdentifier(), statusResults[0].getIdentifier());
            assertEquals("Record status id is wrong for status result 3-0", recStatus1Id, statusResults[0].getRecStatusId());
            assertEquals("Instance id is wrong for status result 3-0", null, statusResults[0].getInstanceId());
            assertEquals("Occurrence id is wrong for status result 3-0", null, statusResults[0].getOccurrenceId());
            assertEquals("Doc status id is wrong for status result 3-0", null, statusResults[0].getDocStatusId());

            
            //Note the time then sleep for two seconds
            lastUpdated = new Date();
            Thread.sleep(2000L);
            
            //change status of doc inst 1 of record 4
            dao.changeDocumentStatus(
            		rec4.getIdentifier().getIdentifier(), 
            		ds.getDocument(0).getOccurrence(0).getId(), 
            		ds.getDocument(0).getStatus(1).getId(), 
            		"NoUser");

            result = dao.getConsentAndStatusInfoForGroups(projectCode, new String[]{"FOO"}, lastUpdated);

            consentResults = result.getConsentResults();
            assertEquals("Consent results is wrong size - 4",0,consentResults.length);
            
            statusResults = result.getStatusResults();
            assertEquals("Status results is wrong size - 4",1,statusResults.length);

            //Record 4
            assertEquals("Identifier is wrong for status result 4-0", fooIds[3].getIdentifier(), statusResults[0].getIdentifier());
            assertEquals("Record status id is wrong for status result 4-0", recStatus2Id, statusResults[0].getRecStatusId());
            assertEquals("Instance id is wrong for status result 4-0", rec4Inst1Id, statusResults[0].getInstanceId());
            assertEquals("Occurrence id is wrong for status result 4-0", doc1Occ1Id, statusResults[0].getOccurrenceId());
            assertEquals("Doc status id is wrong for status result 4-0", doc1Status2Id, statusResults[0].getDocStatusId());


		}
	    catch(Exception ex){
	        ex.printStackTrace();
	        fail(ex.toString());
	    }

    }
    
}
