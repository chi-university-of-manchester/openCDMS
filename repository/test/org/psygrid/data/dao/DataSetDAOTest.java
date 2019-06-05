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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.NoDatasetException;
import org.psygrid.data.repository.dao.ObjectOutOfDateException;
import org.psygrid.data.repository.dao.RepositoryDAO;

/**
 * Unit Tests for implementations of the DataSetDAO interface
 * 
 * @author Rob Harper
 *
 */
public class DataSetDAOTest extends DAOTest {

    private RepositoryDAO dao = null;
    private Factory factory = null;
    
    protected void setUp() throws Exception {
        super.setUp();
        dao = (RepositoryDAO) ctx.getBean("repositoryDAOService");
        factory = (Factory) ctx.getBean("factory");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
        factory = null;
    }

    /**
     * Unit test for saving a new DataSet in the repository
     */
    public void testSaveDataSet_Create(){
        try{
            String name = "testSaveDataSet_Create - "+(new Date()).toString();
            DataSet dataSet = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
            dataSet.setProjectCode(projectCode);
            Document doc1 = factory.createDocument("Doc 1");
            dataSet.addDocument(doc1);
            
            Long dsId = dao.saveDataSet(dataSet.toDTO());
            
            assertNotNull("Unique identifier has not been assigned",dsId);
            
            DataSet savedDataSet = dao.getDataSet(dsId).toHibernate();
            assertNotNull("DataSet could not be retrieved from the repository (id = "
                          +dataSet.getId()+")",savedDataSet);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for updating an existing DataSet in the repository.
     * The DataSet is updated by adding an extra child Folder.
     */
    public void testSaveDataSet_Update(){
        try{
            String name = "testSaveDataSet_Update - "+(new Date()).toString();
            DataSet dataSet = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
            dataSet.setProjectCode(projectCode);
            Document doc1 = factory.createDocument("Doc 1");
            dataSet.addDocument(doc1);
            
            Long dataSetId = dao.saveDataSet(dataSet.toDTO());
            dataSet = null;
            
            dataSet = dao.getDataSet(dataSetId).toHibernate();
            Document doc2 = factory.createDocument("Doc 2");
            dataSet.addDocument(doc2);
            
            dao.saveDataSet(dataSet.toDTO());
            
            assertEquals("Unique identifier of DataSet has changed", dataSetId, dataSet.getId());
            
            dataSet = null;
            dataSet = dao.getDataSet(dataSetId).toHibernate();
            assertEquals("DataSet does not have two children",2,dataSet.numDocuments());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());            
        }
    }
  
    /**
     * Unit test for updating an existing DataSet in the repository.
     * The DataSet is updated by removing an existing Folder and
     * adding a new Document.
     */
    public void testSaveDataSet_Update2(){
        try{
            //add a dataset
            Long dataSetId = null;
            Long docId = null;
            {
                String name = "testSaveDataSet_Update2 - "+(new Date()).toString();
                DataSet dataSet = factory.createDataset(name);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                dataSet.setProjectCode(projectCode);
                Document doc1 = factory.createDocument("Doc 1");
                dataSet.addDocument(doc1);
                createDocumentStatuses(factory, doc1);
                
                dataSetId = dao.saveDataSet(dataSet.toDTO());
                dataSet = dao.getDataSet(dataSetId).toHibernate();
                docId = dataSet.getDocument(0).getId();
            }
                        
            DataSet dataSet = dao.getDataSet(dataSetId).toHibernate();
            dataSet.removeDocument(0);
            Document doc2 = factory.createDocument("Doc 2");
            dataSet.addDocument(doc2);
            
            Long dsId = dao.saveDataSet(dataSet.toDTO());
            
            assertEquals("Unique identifier of DataSet has changed", 
                         dataSetId, dsId);
            
            dataSet = null;
            dataSet = dao.getDataSet(dataSetId).toHibernate();
            assertEquals("DataSet does not have one document", 1, dataSet.numDocuments());
            
            //check that folder has been removed from the repository
            try{
                dao.getPersistent(docId);
                fail("Removed document still exists in the repository");
            }
            catch(DAOException ex){
                //do nothing
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());            
        }
    }
    
    /**
     * Unit test for saving a DataSet that has already been published.
     * An exception should be thrown, and the DataSet in the repository
     * should not be modified.
     */
    public void testSaveDataset_Published(){
        try{
            //add a dataset
            String name1 = "testSaveDataset_Published - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name1);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
            ds.setProjectCode(projectCode);
            Long id = dao.saveDataSet(ds.toDTO());
            
            //publish the dataset
            dao.publishDataSet(id);
            
            //reload then modify the DataSet
            ds = dao.getDataSet(id).toHibernate();
            Document d = factory.createDocument("Doc 1");
            ds.addDocument(d);
            
            //save the DataSet
            try{
                dao.saveDataSet(ds.toDTO());
                //should never get to here
                fail("Exception not thrown when trying to save a published DataSet");
            }
            catch(DAOException ex){
                //do nothing
            }
            
            //check that the DataSet has not been modified
            ds = dao.getDataSet(id).toHibernate();
            assertEquals("DataSet has been modified",0,ds.numDocuments());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());            
        }
    }
    
    /**
     * Unit test for saving a DataSet to the repository when the DataSet
     * is out-of-date i.e. the DataSet has been modified by another session 
     * since it was retrieved from the repository.
     */
    public void testSaveDataset_OutOfDate(){
        
        try{
            //create a new DataSet
            String name = "testSaveDataset_OutOfDate - "+(new Date()).toString();
            DataSet dataSet = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
            dataSet.setProjectCode(projectCode);
            Long dsId = dao.saveDataSet(dataSet.toDTO());
            dataSet = dao.getDataSet(dsId).toHibernate();
            
            //Retrieve the DataSet, modify it, then save it.
            DataSet ds1 = dao.getDataSet(dsId).toHibernate();
            Document d1 = factory.createDocument("Doc 1");
            ds1.addDocument(d1);
            dao.saveDataSet(ds1.toDTO());
            
            //Now make some modifications to the original DataSet
            //and try to save it
            dataSet.addDocument(factory.createDocument("Doc 2"));
            try{
                dao.saveDataSet(dataSet.toDTO());
                fail("Exception not thrown when trying to save an out-of-date DataSet");
            }
            catch(ObjectOutOfDateException ex){
                //do nothing
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
        
    }
    
    /**
     * Unit test for saving a DataSet to the repository when the DataSet
     * is out-of-date. The DataSet has been deleted by another session 
     * since it was retrieved from the repository.
     */
    public void testSaveDataset_OutOfDate2(){
        
        try{
            //create a new DataSet
            String name = "testSaveDataset_OutOfDate2 - "+(new Date()).toString();
            DataSet dataSet = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
            dataSet.setProjectCode(projectCode);
            Long dsId = dao.saveDataSet(dataSet.toDTO());
            dataSet = dao.getDataSet(dsId).toHibernate();
            
            //Delete the DataSet
            dao.removeDataSet(dsId);
            
            //Now try to save the dataset again
            try{
                dao.saveDataSet(dataSet.toDTO());
                fail("Exception not thrown when trying to save an out-of-date DataSet");
            }
            catch(ObjectOutOfDateException ex){
                //do nothing
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
        
    }
    
    /**
     * Unit test for retrieving a List of DataSets from the repository
     */
    public void testGetDatasets(){
        
        try{
            //find current number of DataSets
            int numBefore = dao.getDataSets().length;
            
            //add three DataSets
            String name1 = "testGetDatasets - 1 - "+(new Date()).toString();
            DataSet ds1 = factory.createDataset(name1);
            //generate unique project code
            java.rmi.dgc.VMID guid1 = new java.rmi.dgc.VMID();
            String projectCode1 = DAOTestHelper.checkProjectCode(guid1.toString());            
            ds1.setProjectCode(projectCode1);
            
            String name2 = "testGetDatasets - 2 - "+(new Date()).toString();
            DataSet ds2 = factory.createDataset(name2);
            //generate unique project code
            java.rmi.dgc.VMID guid2 = new java.rmi.dgc.VMID();
            String projectCode2 = DAOTestHelper.checkProjectCode(guid2.toString());            
            ds2.setProjectCode(projectCode2);
    
            String name3 = "testGetDatasets - 3 - "+(new Date()).toString();
            DataSet ds3 = factory.createDataset(name3);
            //generate unique project code
            java.rmi.dgc.VMID guid3 = new java.rmi.dgc.VMID();
            String projectCode3 = DAOTestHelper.checkProjectCode(guid3.toString());            
            ds3.setProjectCode(projectCode3);
            
            dao.saveDataSet(ds1.toDTO());
            dao.saveDataSet(ds2.toDTO());
            dao.saveDataSet(ds3.toDTO());
            
            org.psygrid.data.model.dto.DataSetDTO[] dtoDS = dao.getDataSets();
            List<DataSet> dataSets = new ArrayList<DataSet>();
            for ( int i=0; i<dtoDS.length; i++ ){
                dataSets.add(dtoDS[i].toHibernate());
            }
            
            assertEquals("Number of DataSets has not increased by 3",3,dataSets.size()-numBefore);
            
            for ( DataSet d:dataSets){
                assertNotNull("A DataSet in the list does not have an id", d.getId());
                assertNotNull("A DataSet in the list does not have a name", d.getName());
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());            
        }
        
    }
    
    /**
     * Unit test for retrieving a list of modified data sets from the repository.
     */
    public void testGetModifiedDatasets(){
        
        try{
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, -1);
            Date referenceDate = cal.getTime();
            
            //add three DataSets
            String name1 = "testGetModifiedDatasets - 1 - "+(new Date()).toString();
            DataSet ds1 = factory.createDataset(name1);
            //generate unique project code
            java.rmi.dgc.VMID guid1 = new java.rmi.dgc.VMID();
            String projectCode1 = DAOTestHelper.checkProjectCode(guid1.toString());            
            ds1.setProjectCode(projectCode1);
            
            String name2 = "testGetModifiedDatasets - 2 - "+(new Date()).toString();
            DataSet ds2 = factory.createDataset(name2);
            //generate unique project code
            java.rmi.dgc.VMID guid2 = new java.rmi.dgc.VMID();
            String projectCode2 = DAOTestHelper.checkProjectCode(guid2.toString());            
            ds2.setProjectCode(projectCode2);
    
            String name3 = "testGetModifiedDatasets - 3 - "+(new Date()).toString();
            DataSet ds3 = factory.createDataset(name3);
            //generate unique project code
            java.rmi.dgc.VMID guid3 = new java.rmi.dgc.VMID();
            String projectCode3 = DAOTestHelper.checkProjectCode(guid3.toString());            
            ds3.setProjectCode(projectCode3);
            
            dao.saveDataSet(ds1.toDTO());
            dao.saveDataSet(ds2.toDTO());
            dao.saveDataSet(ds3.toDTO());
            
            org.psygrid.data.model.dto.DataSetDTO[] dtoDS = dao.getModifiedDataSets(referenceDate);
            List<DataSet> dataSets = new ArrayList<DataSet>();
            for ( int i=0; i<dtoDS.length; i++ ){
                dataSets.add(dtoDS[i].toHibernate());
            }
            assertTrue("Number of modified data sets is not at least 3",dataSets.size()>=3);
            
            for ( DataSet d:dataSets){
                assertNotNull("A data set in the list does not have an id", d.getId());
                assertNotNull("A data set in the list does not have a name", d.getName());
                assertTrue("A data set in the list has a last modified date not after the reference date",d.getDateModified().after(referenceDate));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());            
        }
        
    }

    /**
     * Unit test for retrieving a single DataSet from the repository.
     * The unique identifier is valid and a DataSet should be successfully
     * retrieved, including its child Folders and Documents.
     */
    public void testGetDataset_Success(){
        
        try{
            //add a DataSet
            String name2 = "testGetDataset_Success - "+(new Date()).toString();
            String doc1Name = "Doc 1";
            String doc2Name = "Doc 2";
            String cfgDesc2 = "Group 2";
            String pcfDesc2 = "PCF 2";
            String acfDesc2 = "ACF 2";
            Long id2 = null;
            {
                DataSet ds1 = factory.createDataset(name2);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds1.setProjectCode(projectCode);
                Document d1 = factory.createDocument(doc1Name);
                ds1.addDocument(d1);
                Document d2 = factory.createDocument(doc2Name);
                ds1.addDocument(d2);
                //attach consent form to a document
                ConsentFormGroup cfg2 = factory.createConsentFormGroup();
                ds1.addAllConsentFormGroup(cfg2);
                cfg2.setDescription(cfgDesc2);
                PrimaryConsentForm cons2 = factory.createPrimaryConsentForm();
                cons2.setQuestion(pcfDesc2);
                AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
                acf2.setQuestion(acfDesc2);
                cons2.addAssociatedConsentForm(acf2);
                cfg2.addConsentForm(cons2);
                d1.addConsentFormGroup(cfg2);
                //save dataset
                id2 = dao.saveDataSet(ds1.toDTO());
            }

            DataSet ds = dao.getDataSet(id2).toHibernate();
            
            assertNotNull("Retrieved a null DataSet", ds);
            assertEquals("DataSet has incorrect name",name2,ds.getName());
            
            //traverse the DataSets children to make sure that all Folders and
            //Documents have been initialized
            Document d1 = ds.getDocument(0);
            assertEquals("Document at position 0 is has the wrong name", doc1Name, d1.getName());
            Document d2 = ds.getDocument(1);
            assertEquals("Document at position 1 is has the wrong name", doc2Name, d2.getName());

            
            {
                //check that consent forms have been initialized by their
                //owning elements
                ConsentFormGroup cfg2 = d1.getConsentFormGroup(0);
                assertNotNull("Folder has a null Consent Form Group", cfg2);
                PrimaryConsentForm cons2 = cfg2.getConsentForm(0);
                assertNotNull("Folder CFG has a null Consent Form", cons2);
                assertEquals("Folder Consent form has the wrong description", pcfDesc2, cons2.getQuestion());
                AssociatedConsentForm acf2 = cons2.getAssociatedConsentForm(0);
                assertNotNull("Folder Consent form has a null associated consent form", acf2);
                assertEquals("Folder Associated Consent form has the wrong description", acfDesc2, acf2.getQuestion());
            }
            {
                //check that consent forms have been initialized from the
                //dataset's list of all consent forms
                assertEquals("DataSet list of consent form groups does not have the correct number of items",1,ds.numAllConsentFormGroups());
                ConsentFormGroup cfg2 = ds.getAllConsentFormGroup(0);
                assertNotNull("DataSet 'all' CFG at index 0 is null", cfg2);
                assertEquals("DataSet 'all' CFG at index 0 does not have the correct description",cfgDesc2,cfg2.getDescription());
                PrimaryConsentForm cons2 = cfg2.getConsentForm(0);
                assertNotNull("Folder CFG has a null Consent Form", cons2);
                assertEquals("Folder Consent form has the wrong description", pcfDesc2, cons2.getQuestion());
                AssociatedConsentForm acf2 = cons2.getAssociatedConsentForm(0);
                assertNotNull("Folder Consent form has a null associated consent form", acf2);
                assertEquals("Folder Associated Consent form has the wrong description", acfDesc2, acf2.getQuestion());
            }
            
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
        
    }
    
    /**
     * Unit test for retrieving a single (complete) DataSet from the repository.
     * The unique identifier is valid and a DataSet should be successfully
     * retrieved, including its child Folders and Documents.
     */
    public void testGetDatasetComplete_Success(){
        
        try{
            //add a DataSet
            String dateSuffix = " ["+(new Date()).toString()+"]";
            String name2 = "testGetDatasetComplete_Success"+dateSuffix;
            String r1Desc = "Rule 1"+dateSuffix;
            String r2Desc = "Rule 2"+dateSuffix;
            String r3Desc = "Rule 3"+dateSuffix;
            String stat1Name = "Status 1"+dateSuffix;
            String stat2Name = "Status 2"+dateSuffix;
            String stat3Name = "Status 3"+dateSuffix;
            String d1Name = "Doc 1"+dateSuffix;
            String d2Name = "Doc 2"+dateSuffix;
            String d3Name = "Doc 3"+dateSuffix;
            String s1Name = "Section 1"+dateSuffix;
            String te1Name = "TE1"+dateSuffix;
            String ne1Name = "NE1"+dateSuffix;
            String s2Name = "Section 2"+dateSuffix;
            String c1Name = "Comp 1"+dateSuffix;
            String te2Name = "TE2"+dateSuffix;
            String be1Name = "BE1"+dateSuffix;
            String s3Name = "Section 3"+dateSuffix;
            String oe1Name = "OE1"+dateSuffix;
            String o1Name = "Option 1"+dateSuffix;
            String o2Name = "Option 2"+dateSuffix;
            String te3Name = "TE3"+dateSuffix;
            String s4Name = "Section 4"+dateSuffix;
            String ne2Name = "NE2"+dateSuffix;
            String ne3Name = "NE3"+dateSuffix;
            String de1Name = "DE1"+dateSuffix;
            String con2Ref = "Cons 2"+dateSuffix;
            String acf2Ref = "ACF2"+dateSuffix;
            String unit1 = null;
            String unit2 = null;
            Long id2 = null;
            String nae1Name = "NAE1"+dateSuffix;
            NarrativeStyle nae1Style = NarrativeStyle.HEADER;
            {
                DataSet ds1 = factory.createDataset(name2);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds1.setProjectCode(projectCode);
                ValidationRule rule1 = factory.createTextValidationRule();
                rule1.setDescription(r1Desc);
                ds1.addValidationRule(rule1);
                ValidationRule rule2 = factory.createTextValidationRule();
                rule2.setDescription(r2Desc);
                ds1.addValidationRule(rule2);
                NumericValidationRule rule3 = factory.createNumericValidationRule();
                rule3.setDescription(r3Desc);
                ds1.addValidationRule(rule3);
                Status stat1 = factory.createStatus(stat1Name, 1);
                Status stat2 = factory.createStatus(stat2Name, 2);
                Status stat3 = factory.createStatus(stat3Name, 3);
                ds1.addStatus(stat1);
                ds1.addStatus(stat2);
                ds1.addStatus(stat3);
                stat1.addStatusTransition(stat2);
                stat1.addStatusTransition(stat3);
                Document d1 = factory.createDocument(d1Name);
                ds1.addDocument(d1);
                Document d2 = factory.createDocument(d2Name);
                ds1.addDocument(d2);
                Document d3 = factory.createDocument(d3Name);
                ds1.addDocument(d3);
                //add content for document 1
                Section s1 = factory.createSection(s1Name);
                d1.addSection(s1);
                TextEntry te1 = factory.createTextEntry(te1Name);
                te1.setSection(s1);
                d1.addEntry(te1);
                NumericEntry ne1 = factory.createNumericEntry(ne1Name);
                ne1.setSection(s1);
                d1.addEntry(ne1);
                Unit u1 = factory.createUnit("Unit 1");
                ds1.addUnit(u1);
                unit1 = u1.getAbbreviation();
                Unit u2 = factory.createUnit("Unit 2");
                ds1.addUnit(u2);
                unit2 = u2.getAbbreviation();
                ne1.addUnit(u1);
                ne1.addUnit(u2);
                ne1.addValidationRule(rule3);
                //add content for document 2
                Section s2 = factory.createSection(s2Name);
                d2.addSection(s2);
                CompositeEntry c1 = factory.createComposite(c1Name);
                c1.setSection(s2);
                d2.addEntry(c1);
                TextEntry te2 = factory.createTextEntry(te2Name);
                c1.addEntry(te2);
                te2.addValidationRule(rule1);
                BooleanEntry be1 = factory.createBooleanEntry(be1Name);
                c1.addEntry(be1);
                NarrativeEntry nae1 = factory.createNarrativeEntry(nae1Name);
                nae1.setSection(s2);
                nae1.setStyle(nae1Style);
                d2.addEntry(nae1);
                //add content for document 3
                Section s3 = factory.createSection(s3Name);
                d3.addSection(s3);
                OptionEntry oe1 = factory.createOptionEntry(oe1Name);
                Option o1 = factory.createOption(o1Name, o1Name);
                Option o2 = factory.createOption(o2Name, o2Name);
                oe1.addOption(o1);
                oe1.addOption(o2);
                oe1.setSection(s3);
                d3.addEntry(oe1);
                TextEntry te3 = factory.createTextEntry(te3Name, EntryStatus.DISABLED);
                te3.setSection(s3);
                d3.addEntry(te3);
                OptionDependent od1 = factory.createOptionDependent();
                od1.setDependentEntry(te3);
                od1.setEntryStatus(EntryStatus.MANDATORY);
                o2.addOptionDependent(od1);
                Section s4 = factory.createSection(s4Name);
                d3.addSection(s4);
                NumericEntry ne2 = factory.createNumericEntry(ne2Name);
                ne2.setSection(s4);
                d3.addEntry(ne2);
                NumericEntry ne3 = factory.createNumericEntry(ne3Name);
                ne3.setSection(s4);
                d3.addEntry(ne3);
                DerivedEntry de1 = factory.createDerivedEntry(de1Name);
                de1.addVariable("x",ne2);
                de1.addVariable("y",ne3);
                de1.setFormula("x+y");
                de1.setSection(s4);
                d3.addEntry(de1);
                
                //attach info sheet to the document
                BinaryObject infoObject1 = factory.createBinaryObject(read2Array("test/test-info-doc.pdf"));
                infoObject1.setFileName("test-info-doc.pdf");
                infoObject1.setMimeType("application/pdf");
                ds1.setInfoSheet(infoObject1);
                
                //attach consent form to a document
                PrimaryConsentForm cons2 = factory.createPrimaryConsentForm();
                cons2.setReferenceNumber(con2Ref);
                BinaryObject bo3 = factory.createBinaryObject(read2Array("test/test-cf2.pdf"));
                bo3.setDescription("Test CF2");
                bo3.setFileName("test-cf2.pdf");
                bo3.setMimeType("application/pdf");
                cons2.setElectronicDocument(bo3);
                AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
                acf2.setReferenceNumber(acf2Ref);
                BinaryObject bo4 = factory.createBinaryObject(read2Array("test/test-acf2.pdf"));
                bo4.setDescription("Test ACF2");
                bo4.setFileName("test-acf2.pdf");
                bo4.setMimeType("application/pdf");
                acf2.setElectronicDocument(bo4);
                cons2.addAssociatedConsentForm(acf2);
                ConsentFormGroup cfg2 = factory.createConsentFormGroup();
                ds1.addAllConsentFormGroup(cfg2);
                cfg2.addConsentForm(cons2);
                d1.addConsentFormGroup(cfg2);
                //save dataset
                id2 = dao.saveDataSet(ds1.toDTO());
            }

            DataSet ds = dao.getDataSet(id2).toHibernate();
            
            assertNotNull("Retrieved a null DataSet", ds);
            assertEquals("DataSet has incorrect name",name2,ds.getName());
            
            //check dataset info sheet
            BinaryObject info1 = ds.getInfoSheet();
            assertEquals("Mime type of electronic document of dataset info sheet is incorrect","application/pdf",info1.getMimeType());
            try{
                info1.getData().getData();
                fail("Binary data of binary object info1 should NOT have been initialized");
            }
            catch(Exception ex){
                //do nothing
            }
            
            //check dataset validation rules
            assertEquals("Dataset has the wrong number of validation rules",3,ds.numValidationRules());
            assertEquals("Dataset has validation rule at index 0 with the wrong description",r1Desc,ds.getValidationRule(0).getDescription());
            assertEquals("Dataset has validation rule at index 1 with the wrong description",r2Desc,ds.getValidationRule(1).getDescription());
            assertEquals("Dataset has validation rule at index 2 with the wrong description",r3Desc,ds.getValidationRule(2).getDescription());
            
            //check dataset statuses
            assertEquals("Dataset has the wrong number of statuses",3,ds.numStatus());
            assertEquals("Dataset has status at index 0 with the wrong name",stat1Name,ds.getStatus(0).getShortName());
            assertEquals("Dataset has status at index 1 with the wrong name",stat2Name,ds.getStatus(1).getShortName());
            assertEquals("Dataset has status at index 2 with the wrong name",stat3Name,ds.getStatus(2).getShortName());
            assertEquals("Dataset status at index 0 has the wrong number of status transitions",2,ds.getStatus(0).numStatusTransitions());
            assertEquals("Dataset status at index 0 has transition at index 0 with the wrong name",stat2Name,ds.getStatus(0).getStatusTransition(0).getShortName());
            assertEquals("Dataset status at index 0 has transition at index 1 with the wrong name",stat3Name,ds.getStatus(0).getStatusTransition(1).getShortName());
            assertTrue("Status at index 1 and transition at index 0 of status at index 0 do not reference the same object", ds.getStatus(1) == ds.getStatus(0).getStatusTransition(0));
            assertTrue("Status at index 2 and transition at index 1 of status at index 0 do not reference the same object", ds.getStatus(2) == ds.getStatus(0).getStatusTransition(1));
            
            //check the document and folder structure
            Document d1 = ds.getDocument(0);
            assertEquals("Document at position 0 has the wrong name",d1Name,d1.getName());

            Document d2 = ds.getDocument(1);
            assertEquals("Document at position 1 has the wrong name",d2Name,d2.getName());

            Document d3 = ds.getDocument(2);
            assertEquals("Document at position 2 has the wrong name",d3Name,d3.getName());

            //check folder 1 consent forms
            {
                ConsentFormGroup cfg2 = d1.getConsentFormGroup(0);
                assertNotNull("Folder consent form group is null",cfg2);
                PrimaryConsentForm cf2 = cfg2.getConsentForm(0);
                assertEquals("Reference of folder 1 consent form is incorrect", con2Ref, cf2.getReferenceNumber());
                BinaryObject bo3 = cf2.getElectronicDocument();
                assertEquals("Mime type of electronic document of folder 1 consent form is incorrect","application/pdf",bo3.getMimeType());
                assertEquals("Description of electronic document of dataset consent form is incorrect","Test CF2",bo3.getDescription());
                assertEquals("File name of electronic document of dataset consent form is incorrect","test-cf2.pdf",bo3.getFileName());
                try{
                    bo3.getData().getData();
                    fail("Binary data of binary object bo1 should NOT have been initialized");
                }
                catch(Exception ex){
                    //do nothing
                }
                AssociatedConsentForm acf2 = cf2.getAssociatedConsentForm(0);
                assertEquals("Reference of folder 1 associated consent form is incorrect", acf2Ref, acf2.getReferenceNumber());
                BinaryObject bo4 = acf2.getElectronicDocument();
                assertEquals("Mime type of electronic document of dataset associated consent form is incorrect","application/pdf",bo4.getMimeType());
                assertEquals("Description of electronic document of dataset consent form is incorrect","Test ACF2",bo4.getDescription());
                assertEquals("File name of electronic document of dataset consent form is incorrect","test-acf2.pdf",bo4.getFileName());
                try{
                    bo4.getData().getData();
                    fail("Binary data of binary object bo2 should NOT have been initialized");
                }
                catch(Exception ex){
                    //do nothing
                }
            }
            
            //check content of document 1
            Section s1 = d1.getSection(0);
            assertEquals("Section of doc 1 at position 0 has the wrong name",s1Name,s1.getName());
            Entry te1 = d1.getEntry(0);
            assertEquals("Entry of doc 1 at position 0 has the wrong name",te1Name,te1.getName());
            assertEquals("Entry of doc 1 at position 0 has the wrong section",s1Name,te1.getSection().getName());
            Entry ne1 = d1.getEntry(1);
            assertEquals("Entry of doc 1 at position 1 has the wrong name",ne1Name,ne1.getName());
            assertEquals("Entry of doc 1 at position 1 has the wrong section",s1Name,ne1.getSection().getName());
            BasicEntry ne1a = (BasicEntry)ne1;
            assertEquals("Unit of numeric entry 1 at position 0 is not correct",unit1,ne1a.getUnit(0).getAbbreviation());
            assertEquals("Unit of numeric entry 1 at position 1 is not correct",unit2,ne1a.getUnit(1).getAbbreviation());
            assertEquals("Numeric entry 1 does not have a validation rule",1,ne1a.numValidationRules());
            
            //check content of document 2
            Section s2 = d2.getSection(0);
            assertEquals("Section of doc 2 at position 0 has the wrong name",s2Name,s2.getName());
            CompositeEntry ce1 = (CompositeEntry)d2.getEntry(0);
            assertEquals("Entry of doc 2 at position 0 has the wrong name",c1Name,ce1.getName());
            assertEquals("Entry of doc 2 at position 0 has the wrong section",s2Name,ce1.getSection().getName());
            BasicEntry te2 = ce1.getEntry(0);
            assertTrue("Child of composite 1 at position 0 is not a text entry", te2 instanceof TextEntry);
            assertEquals("Child of composite 1 at position 0 has the wrong name",te2Name,te2.getName());
            assertEquals("Child of composite 1 at position 0 has the wrong number of validation rules",1,te2.numValidationRules());
            assertEquals("Child of composite 1 at position 0 has the validation rule with the wrong description",r1Desc,te2.getValidationRule(0).getDescription());
            BasicEntry be1 = ce1.getEntry(1);
            assertTrue("Child of composite 1 at position 1 is not a boolean entry", be1 instanceof BooleanEntry);
            assertEquals("Child of composite 1 at position 1 has the wrong name",be1Name,be1.getName());
            NarrativeEntry nae1 = (NarrativeEntry)d2.getEntry(1);
            assertEquals("Entry of doc 2 at position 1 has the wrong name",nae1Name,nae1.getName());
            assertEquals("Entry of doc 2 at position 1 has the wrong section",s2Name,nae1.getSection().getName());
            assertEquals("Entry of doc 2 at position 1 has the wrong style",nae1Style,nae1.getStyle());
            
            //check content of document 3
            Section s3 = d3.getSection(0);
            assertEquals("Section of doc 3 at position 0 has the wrong name",s3Name,s3.getName());
            Section s4 = d3.getSection(1);
            assertEquals("Section of doc 3 at position 1 has the wrong name",s4Name,s4.getName());
            Entry oe1 = d3.getEntry(0);
            assertTrue("Child of doc 3 at position 0 is not a option entry", oe1 instanceof OptionEntry);
            assertEquals("Child of doc 3 at position 0 has the wrong name",oe1Name,oe1.getName());
            assertEquals("Child of doc 3 at position 0 has the wrong section",s3Name,oe1.getSection().getName());
            OptionEntry oe1a = (OptionEntry)oe1;
            assertEquals("Option of option entry 1 at position 0 is incorrect", o1Name, oe1a.getOption(0).getDisplayText());
            assertEquals("Option of option entry 1 at position 1 is incorrect", o2Name, oe1a.getOption(1).getDisplayText());
            Option o2 = oe1a.getOption(1);
            assertEquals("Option dependent entry of option 2 is incorrect", te3Name, o2.getOptionDependent(0).getDependentEntry().getName());
            assertEquals("Option dependent status of option 2 is incorrect", EntryStatus.MANDATORY, o2.getOptionDependent(0).getEntryStatus());
            Entry te3 = d3.getEntry(1);
            assertTrue("Child of doc 3 at position 1 is not a text entry", te3 instanceof TextEntry);
            assertEquals("Child of doc 3 at position 1 has the wrong name",te3Name,te3.getName());
            assertEquals("Child of doc 3 at position 1 has the wrong section",s3Name,te3.getSection().getName());
            Entry ne2 = d3.getEntry(2);
            assertTrue("Child of doc 3 at position 2 is not a numeric entry", ne2 instanceof NumericEntry);
            assertEquals("Child of doc 3 at position 2 has the wrong name",ne2Name,ne2.getName());
            assertEquals("Child of doc 3 at position 2 has the wrong section",s4Name,ne2.getSection().getName());
            Entry ne3 = d3.getEntry(3);
            assertTrue("Child of doc 3 at position 3 is not a numeric entry", ne3 instanceof NumericEntry);
            assertEquals("Child of doc 3 at position 3 has the wrong name",ne3Name,ne3.getName());
            assertEquals("Child of doc 3 at position 3 has the wrong section",s4Name,ne3.getSection().getName());
            Entry de1 = d3.getEntry(4);
            assertTrue("Child of doc 3 at position 4 is not a derived entry", de1 instanceof DerivedEntry);
            assertEquals("Child of doc 3 at position 4 has the wrong name",de1Name,de1.getName());
            assertEquals("Child of doc 3 at position 4 has the wrong section",s4Name,de1.getSection().getName());
            DerivedEntry de1a = (DerivedEntry)de1;
            assertEquals("Variable 'x' of derived entry 1 is incorrect",ne2,de1a.getVariable("x"));
            assertEquals("Variable 'x' of derived entry 1 has the wrong name",ne2Name,de1a.getVariable("x").getName());
            assertEquals("Variable 'y' of derived entry 1 is incorrect",ne3,de1a.getVariable("y"));
            assertEquals("Variable 'y' of derived entry 1 has the wrong name",ne3Name,de1a.getVariable("y").getName());
            
            //check dataset references to 'all' consent forms
            {
                assertEquals("DataSet has the wrong number of 'all' consent form groups",1,ds.numAllConsentFormGroups());
                
                //folder cfg
                ConsentFormGroup cfg2 = d1.getConsentFormGroup(0);
                assertNotNull("Folder consent form group is null",cfg2);
                PrimaryConsentForm cf2 = cfg2.getConsentForm(0);
                assertEquals("Reference of folder 1 consent form is incorrect", con2Ref, cf2.getReferenceNumber());
                BinaryObject bo3 = cf2.getElectronicDocument();
                assertEquals("Mime type of electronic document of folder 1 consent form is incorrect","application/pdf",bo3.getMimeType());
                assertEquals("Description of electronic document of dataset consent form is incorrect","Test CF2",bo3.getDescription());
                assertEquals("File name of electronic document of dataset consent form is incorrect","test-cf2.pdf",bo3.getFileName());
                try{
                    bo3.getData().getData();
                    fail("Binary data of binary object bo1 should NOT have been initialized");
                }
                catch(Exception ex){
                    //do nothing
                }
                AssociatedConsentForm acf2 = cf2.getAssociatedConsentForm(0);
                assertEquals("Reference of folder 1 associated consent form is incorrect", acf2Ref, acf2.getReferenceNumber());
                BinaryObject bo4 = acf2.getElectronicDocument();
                assertEquals("Mime type of electronic document of dataset associated consent form is incorrect","application/pdf",bo4.getMimeType());
                assertEquals("Description of electronic document of dataset consent form is incorrect","Test ACF2",bo4.getDescription());
                assertEquals("File name of electronic document of dataset consent form is incorrect","test-acf2.pdf",bo4.getFileName());
                try{
                    bo4.getData().getData();
                    fail("Binary data of binary object bo2 should NOT have been initialized");
                }
                catch(Exception ex){
                    //do nothing
                }
                
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
        
    }
    
    /**
     * Unit test for retrieving a single DataSet from the repository
     * when an invalid unique identifier is given as the argument.
     *
     */
    public void testGetDataset_InvalidId(){

        try{
            //add two DataSets
            String name1 = "testGetDataset_InvalidId - "+(new Date()).toString();
            Long invalidId = null;
            {
                DataSet ds1 = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds1.setProjectCode(projectCode);
                Document d1 = factory.createDocument("Doc 1");
                ds1.addDocument(d1);
                Document d2 = factory.createDocument("Doc 2");
                ds1.addDocument(d2);
                Long dsId = dao.saveDataSet(ds1.toDTO());
                ds1 = dao.getDataSet(dsId).toHibernate();
                invalidId = ds1.getDocument(0).getId();
            }
            
            try{
                dao.getDataSet(invalidId);
                //should not get to here!
                fail("InvalidPersistentIdException not thrown by getDataSet when invalid id used");
            }
            catch(DAOException ex){
                //do nothing - this is what is supposed to happen!
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for removing a single DataSet from the repository.
     * The DataSet has not been published, so it should be removed
     * successfully. Also check that children of the DataSet are also
     * removed by way of cascade delete.
     */
    public void testRemoveDataset_Success(){
        try{
            //add a DataSet
            Long dsId = null;
            Long d1Id = null;
            Long s1Id = null;
            Long te1Id = null;
            Long ds1Id = null;
            String name1 = "testRemoveDataset_Success - "+(new Date()).toString();
            {
                DataSet ds = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds.setProjectCode(projectCode);
                Document d1 = factory.createDocument("Doc 1");
                ds.addDocument(d1);
                Status ds1 = factory.createStatus("S1", "S1", 1);
                d1.addStatus(ds1);
                Section s1 = factory.createSection("Sec 1");
                d1.addSection(s1);
                TextEntry te1 = factory.createTextEntry("Text entry 1");
                te1.setSection(s1);
                d1.addEntry(te1);
                dsId = dao.saveDataSet(ds.toDTO());
                ds = dao.getDataSet(dsId).toHibernate();
                d1 = ds.getDocument(0);
                s1 = d1.getSection(0);
                te1 = (TextEntry)d1.getEntry(0);
                d1Id = d1.getId();
                s1Id = s1.getId();
                te1Id = te1.getId();
                ds1Id = d1.getStatus(0).getId();
            }
            
            dao.removeDataSet(dsId);
            
            //test that DataSet has been removed
            try{
                dao.getDataSet(dsId);
                //should not get to here
                fail("DataSet has not been removed");
            }
            catch (DAOException ex){
                //do nothing
            }
            
            //test that all of the child elements have been removed
            //via cascade delete
            try{
                dao.getPersistent(d1Id);
                //should not get to here
                fail("Element "+d1Id+" has not been removed");
            }
            catch (DAOException ex){
                //do nothing
            }
            try{
                dao.getPersistent(s1Id);
                //should not get to here
                fail("Element "+s1Id+" has not been removed");
            }
            catch (DAOException ex){
                //do nothing
            }
            try{
                dao.getPersistent(te1Id);
                //should not get to here
                fail("Element "+te1Id+" has not been removed");
            }
            catch (DAOException ex){
                //do nothing
            }
            try{
                dao.getPersistent(ds1Id);
                //should not get to here
                fail("Element "+ds1Id+" has not been removed");
            }
            catch (DAOException ex){
                //do nothing
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for removing a DataSet when the DataSet has been
     * published. The DataSet should not be removed, and an exception
     * is raised.
     */
    public void testRemoveDataset_Published(){
        try{
            //add a DataSet
            Long id = null;
            {
                String name1 = "testRemoveDataset_Published - "+(new Date()).toString();
                DataSet ds1 = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds1.setProjectCode(projectCode);
                id = dao.saveDataSet(ds1.toDTO());
                dao.publishDataSet(id);
            }
            
            //try to remove the published DataSet
            try{
                dao.removeDataSet(id);
                //should not get to here
                fail("Exception not thrown when trying to remove a published DataSet");
            }
            catch(DAOException ex){
                //do nothing
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for removing a DataSet when an invalid unique identifier
     * is used. An exception should be raised.
     */
    public void testRemoveDataset_InvalidId(){
        try{
            Long invalidId = null;
            {
                String name1 = "testRemoveDataset_InvalidId - "+(new Date()).toString();
                DataSet ds = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds.setProjectCode(projectCode);
                Document d1 = factory.createDocument("Doc 1");
                ds.addDocument(d1);
                Long dsId = dao.saveDataSet(ds.toDTO());
                ds = dao.getDataSet(dsId).toHibernate();
                invalidId = ds.getDocument(0).getId();
            }
            
            try{
                dao.removeDataSet(invalidId);
                //should never get to here
                fail("Exception not thrown when trying to remove a DataSet using an invalid id");
            }
            catch(DAOException ex){
                //do nothing
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for removing a single DataSet from the repository.
     * The DataSet has been published, so it should only be removed 
     * if the ID and project code are provided and match. Also 
     * check that children of the DataSet are also removed by way 
     * of cascade delete.
     */
    public void testRemovePublishedDataset_Success(){
        try{
            //add a DataSet
            Long dsId = null;
            Long d1Id = null;
            Long s1Id = null;
            Long te1Id = null;
            String projectCode = null;
            
            String name1 = "testRemovePublishedDataset_Success - "+(new Date()).toString();
            {
                DataSet ds = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds.setProjectCode(projectCode);
                Document d1 = factory.createDocument("Doc 1");
                ds.addDocument(d1);
                //add more documents?? add records??
                Section s1 = factory.createSection("Sec 1");
                d1.addSection(s1);
                TextEntry te1 = factory.createTextEntry("Text entry 1");
                te1.setSection(s1);
                d1.addEntry(te1);
                
                dsId = dao.saveDataSet(ds.toDTO());
        		dao.publishDataSet(dsId);
                ds = dao.getDataSet(dsId).toHibernate();
                d1 = ds.getDocument(0);
                s1 = d1.getSection(0);
                te1 = (TextEntry)d1.getEntry(0);
                d1Id = d1.getId();
                s1Id = s1.getId();
                te1Id = te1.getId();
            }
            
            dao.removePublishedDataSet(dsId, projectCode);
            
            //test that DataSet has been removed
            try{
                dao.getDataSet(dsId);
                //should not get to here
                fail("DataSet has not been removed");
            }
            catch (DAOException ex){
                //do nothing
            }
            
            //test that all of the child elements have been removed
            //via cascade delete
            
            assertFalse(dao.doesObjectExist("Document", d1Id));
            assertFalse(dao.doesObjectExist("Section", s1Id));
            assertFalse(dao.doesObjectExist("Element", te1Id));
 
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for removing a single DataSet from the repository
     * when more than one DataSet has been loaded.
     */
    public void testRemovePublishedDataset_TwoDataSets(){
        try{
            //add a DataSet
            Long dsId = null;
            Long d1Id = null;
            Long s1Id = null;
            Long te1Id = null;
            String projectCode = null;
            
            //create second DataSet
            Long ds2Id = null;
            Long d2Id = null;
            Long s2Id = null;
            Long te2Id = null;
            String projectCode2 = null;
            
            String name1 = "testRemovePublishedDataset_TwoDataSets - A - "+(new Date()).toString();
            String name2 = "testRemovePublishedDataset_TwoDataSets - B - "+(new Date()).toString();
            {
                DataSet ds = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds.setProjectCode(projectCode);
                Document d1 = factory.createDocument("Doc 1");
                ds.addDocument(d1);
                //add more documents?? add records??
                Section s1 = factory.createSection("Sec 1");
                d1.addSection(s1);
                TextEntry te1 = factory.createTextEntry("Text entry 1");
                te1.setSection(s1);
                d1.addEntry(te1);
                
                dsId = dao.saveDataSet(ds.toDTO());
        		dao.publishDataSet(dsId);
                ds = dao.getDataSet(dsId).toHibernate();
                d1 = ds.getDocument(0);
                s1 = d1.getSection(0);
                te1 = (TextEntry)d1.getEntry(0);
                d1Id = d1.getId();
                s1Id = s1.getId();
                te1Id = te1.getId();
            }
            
            {
                DataSet ds2 = factory.createDataset(name2);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                projectCode2 = DAOTestHelper.checkProjectCode(guid.toString());            
                ds2.setProjectCode(projectCode2);
                Document d2 = factory.createDocument("Doc 2");
                ds2.addDocument(d2);
                //add more documents?? add records??
                Section s2 = factory.createSection("Sec 2");
                d2.addSection(s2);
                TextEntry te2 = factory.createTextEntry("Text entry 2");
                te2.setSection(s2);
                d2.addEntry(te2);
                
                ds2Id = dao.saveDataSet(ds2.toDTO());
        		dao.publishDataSet(ds2Id);
                ds2 = dao.getDataSet(ds2Id).toHibernate();
                d2 = ds2.getDocument(0);
                s2 = d2.getSection(0);
                te2 = (TextEntry)d2.getEntry(0);
                d2Id = d2.getId();
                s2Id = s2.getId();
                te2Id = te2.getId();
            }
            
            //remove first dataset only
            dao.removePublishedDataSet(dsId, projectCode);
            
            //test that DataSet has been removed
            try{
                dao.getDataSet(dsId);
                //should not get to here
                fail("DataSet has not been removed");
            }
            catch (DAOException ex){
                //do nothing
            }
            
            //test that the second DataSet is still there
            try{
                dao.getDataSet(ds2Id);
            }
            catch (DAOException ex){
                //should not get to here
                fail("DataSet has been removed");
            }
            
            //test that all of the child elements have been removed
            //via cascade delete
            assertFalse(dao.doesObjectExist("Document", d1Id));
            assertFalse(dao.doesObjectExist("Section", s1Id));
            assertFalse(dao.doesObjectExist("Element", te1Id));
            
            //test that all of the child elements of the second
            //dataset still exist
            assertTrue(dao.doesObjectExist("Document", d2Id));
            assertTrue(dao.doesObjectExist("Section", s2Id));
            assertTrue(dao.doesObjectExist("Element", te2Id));
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for removing a DataSet when the unique identifier
     * doesn't match the project name provided. An exception should be raised.
     */
    public void testRemovePublishedDataset_InvalidProjectName(){
        try{
            Long dsId = null;
            String invalidProjectName = null;
            { //update code!!??
                String name1 = "testRemovePublishedDataset_InvalidProjectName - "+(new Date()).toString();
                DataSet ds = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds.setProjectCode(projectCode);
                Document d1 = factory.createDocument("Doc 1");
                ds.addDocument(d1);
                dsId = dao.saveDataSet(ds.toDTO());
                ds = dao.getDataSet(dsId).toHibernate();
                invalidProjectName = "something";
            }
            
            try{
                dao.removePublishedDataSet(dsId, invalidProjectName);
                //should never get to here
                fail("Exception not thrown when trying to remove a DataSet using an invalid id");
            }
            catch(DAOException ex){
                //do nothing
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for publishing a DataSet. Publishing is successful.
     */
    public void testPublishDataset_Success(){
        try{
            //add a dataset
            Long id = null;
            {
                String name1 = "testPublishDataset_Success - "+(new Date()).toString();
                DataSet ds = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds.setProjectCode(projectCode);
                id = dao.saveDataSet(ds.toDTO());
            }
            
            dao.publishDataSet(id);
            
            DataSet ds = dao.getDataSet(id).toHibernate();
            assertTrue("DataSet is not marked as published",ds.isPublished());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
        
    }
    
    /**
     * Unit test for publishing a DataSet when an invalid unique
     * identifier is used. An exception should be raised.
     *
     */
    public void testPublishDataset_InvalidId(){
        try{
            Long invalidId = null;
            {
                String name1 = "testPublishDataset_InvalidId - "+(new Date()).toString();
                DataSet ds = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds.setProjectCode(projectCode);
                Document d1 = factory.createDocument("Doc 1");
                ds.addDocument(d1);
                Long dsId = dao.saveDataSet(ds.toDTO());
                ds = dao.getDataSet(dsId).toHibernate();
                invalidId = ds.getDocument(0).getId();
            }
            
            try{
                dao.publishDataSet(invalidId);
                //should never get to here
                fail("Exception not thrown when trying to publish a DataSet using an invalid id");
            }
            catch(DAOException ex){
                //do nothing
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
        
    }
    
    /**
     * Unit test for publishing a DataSet when the DataSet has already
     * been published. An exception is raised.
     *
     */
    public void testPublishDataset_AlreadyPublished(){
        try{
            //add a dataset
            Long id = null;
            {
                String name1 = "testPublishDataset_AlreadyPublished - "+(new Date()).toString();
                DataSet ds = factory.createDataset(name1);
                //generate unique project code
                java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
                String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
                ds.setProjectCode(projectCode);
                id = dao.saveDataSet(ds.toDTO());
            }
            
            dao.publishDataSet(id);
            
            DataSet ds = dao.getDataSet(id).toHibernate();
            assertTrue("DataSet is not marked as published",ds.isPublished());
            
            try{
                dao.publishDataSet(id);
                //should never get to here
                fail("Exception not thrown when trying to publish a DataSet that has already been published");
            }
            catch(DAOException ex){
                //do nothing
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGenerateIdentifiers_4args_Success(){
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
            
            int number = 5;
            String groupCode = "GRP1";
            int maxSuffix = dao.reserveIdentifierSpace(id, groupCode, number);
            org.psygrid.data.model.dto.IdentifierDTO[] ids = dao.generateIdentifiers(projectCode, groupCode, number, maxSuffix, "NoUser");
            
            assertEquals("Array of identifiers is the wrong size",number,ids.length);
            int lastSuffix = 0;
            for ( int i=0; i<number; i++){
                org.psygrid.data.model.dto.IdentifierDTO iid = ids[i];
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
            maxSuffix = dao.reserveIdentifierSpace(id, groupCode, number);
            ids = dao.generateIdentifiers(projectCode, groupCode, number, maxSuffix, "NoUser");
            
            assertEquals("Array of identifiers is the wrong size",number,ids.length);
            for ( int i=0; i<number; i++){
                org.psygrid.data.model.dto.IdentifierDTO iid = ids[i];
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
            fail(ex.toString());
        }
    }
    
    public void testGenerateIdentifiers_4args_Invalid(){
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

            try{
                dao.generateIdentifiers(null, "GRP1", 5, 5, "NoUser");
                fail("Exception should have been thrown when trying to generate identifiers using a null project code");
            }
            catch (DAOException ex){
                //do nothing
            }
            
            try{
                dao.generateIdentifiers("Nonsense", "GRP1", 5, 5, "NoUser");
                fail("Exception should have been thrown when trying to generate identifiers using an invalid project code");
            }
            catch (DAOException ex){
                //do nothing
            }
            
            try{
                dao.generateIdentifiers(projectCode, "GRP1", 0, 10, "NoUser");
                fail("Exception should have been thrown when trying to generate identifiers using an invalid number");
            }
            catch(DAOException ex){
                //do nothing
            }
            
            try{
                dao.generateIdentifiers(projectCode, null, 5, 5, "NoUser");
                fail("Exception should have been thrown when trying to generate identifiers using a null group code");
            }
            catch(DAOException ex){
                //do nothing
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testCFGWithMultipleElements(){
        try{
            String name = "testGenerateIdentifiers - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
            ds.setProjectCode(projectCode);
            Document d1 = factory.createDocument("D1");
            Document d2 = factory.createDocument("D2");
            ds.addDocument(d1);
            ds.addDocument(d2);
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            String cfgDesc = "CFG";
            cfg.setDescription(cfgDesc);
            PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
            cfg.addConsentForm(pcf);
            ds.addAllConsentFormGroup(cfg);
            d1.addConsentFormGroup(cfg);
            d2.addConsentFormGroup(cfg);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            assertTrue("Document 1 and document 2 reference different consent form group objects", ds.getDocument(0).getConsentFormGroup(0) == ds.getDocument(1).getConsentFormGroup(0));
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetProjectCode_Success(){
        try{
            String name = "testGenerateIdentifiers - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
            ds.setProjectCode(projectCode);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            
            String dsCode = dao.getProjectCodeForDataset(dsId);
            
            assertEquals("Retrieved the wrong code", projectCode, dsCode);
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetProjectCode_InvalidId(){
        try{
            try{
                dao.getProjectCodeForDataset(-1L);
                fail("Exception should have been thrown when trying to get a projetc code using an invalid dataset id");
            }
            catch(DAOException ex){
                //do nothing
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testSaveDataSet_BaseUnits(){
        try{
            String name = "testSaveDataSet_BaseUnits - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
            ds.setProjectCode(projectCode);
            
            Unit hours = factory.createUnit("hrs");
            Unit days = factory.createUnit("days");
            Unit weeks = factory.createUnit("wks");
            
            hours.setBaseUnit(days);
            Double hoursFactor = new Double(0.04167);
            hours.setFactor(hoursFactor);
            weeks.setBaseUnit(days);
            Double weeksFactor = new Double(7.0);
            weeks.setFactor(weeksFactor);
            
            ds.addUnit(hours);
            ds.addUnit(days);
            ds.addUnit(weeks);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            hours = ds.getUnit(0);
            days = ds.getUnit(1);
            weeks = ds.getUnit(2);
            assertEquals("Unit hours has the wrong base unit",days,hours.getBaseUnit());
            assertEquals("Unit hours has the wrong factor",hoursFactor, hours.getFactor());
            assertEquals("Unit weeks has the wrong base unit",days,weeks.getBaseUnit());
            assertEquals("Unit weeks has the wrong factor",weeksFactor, weeks.getFactor());
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetSummaryForProjectCode_OK(){
        try{
            //add a DataSet
            String name1 = "testGetSummaryForProjectCode_OK - "+(new Date()).toString();
            DataSet ds1 = factory.createDataset(name1);
            //generate unique project code
            java.rmi.dgc.VMID guid1 = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid1.toString());            
            ds1.setProjectCode(projectCode);
            
            dao.saveDataSet(ds1.toDTO());

            ds1 = dao.getSummaryForProjectCode(projectCode, RetrieveDepth.DS_SUMMARY).toHibernate();
            assertEquals("Dataset summary has the wrong name", name1, ds1.getName());
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetSummaryForProjectCode_InvalidCode(){
        try{
            try{
                dao.getSummaryForProjectCode("If this is a real project code I'll eat my hat", RetrieveDepth.DS_SUMMARY);
                fail("Exception should have been thrown when trying to get a dataset summary using an invalid project code");
            }
            catch(NoDatasetException ex){
                //do nothing
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetDataSetModified_OK(){
        try{
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MINUTE, -1);
            
            //add a DataSet
            String name1 = "testGetDataSetModified_OK - "+(new Date()).toString();
            DataSet ds1 = factory.createDataset(name1);
            //generate unique project code
            java.rmi.dgc.VMID guid1 = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid1.toString());            
            ds1.setProjectCode(projectCode);
            
            dao.saveDataSet(ds1.toDTO());
            
            boolean result1 = dao.getDataSetModified(projectCode, cal.getTime());
            assertTrue("getDataSetModified should have returned True", result1);
            
            cal.add(Calendar.MONTH, 1);
            boolean result2 = dao.getDataSetModified(projectCode, cal.getTime());
            assertFalse("getDataSetModified should have returned False", result2);
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testGetDataSetModified_InvalidCode(){
        try{
            try{
                dao.getDataSetModified("If this is a real project code I'll eat my hat", new Date());
                fail("Exception should have been thrown when trying to find if a dataset has been modified using an invalid project code");
            }
            catch(NoDatasetException ex){
                //do nothing
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    
    public void testGetStatusShortName(){
        try{
            //add a DataSet
            String name = "testGetStatusShortName - "+(new Date()).toString();
            DataSet ds1 = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid1 = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid1.toString());            
            ds1.setProjectCode(projectCode);
            
            String name1 = "Status 1";
            String name2 = "Status 2";
            String name3 = "Status 3";
            Status s1 = factory.createStatus(name1, "Long Status 1", 1);
            Status s2 = factory.createStatus(name2, "Long Status 2", 2);
            Status s3 = factory.createStatus(name3, "Long Status 3", 3);
            ds1.addStatus(s1);
            ds1.addStatus(s2);
            ds1.addStatus(s3);
            
            Long dsId = dao.saveDataSet(ds1.toDTO());
            ds1 = dao.getDataSet(dsId).toHibernate();
            
            assertEquals("Status 1 has the wrong short name", name1, dao.getStatusShortName(ds1.getStatus(0).getId()));
            assertEquals("Status 2 has the wrong short name", name2, dao.getStatusShortName(ds1.getStatus(1).getId()));
            assertEquals("Status 3 has the wrong short name", name3, dao.getStatusShortName(ds1.getStatus(2).getId()));
            
            try{
                dao.getStatusShortName(-1L);
                fail("Exception should have been thrown when trying to get short name using an invalid id");
            }
            catch(DAOException ex){
                //do nothing
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    public void testReserveIdentifierSpace(){
        try{
            //add a DataSet
            String name = "testReserveIdentifierSpace - "+(new Date()).toString();
            DataSet ds1 = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid1 = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid1.toString());            
            ds1.setProjectCode(projectCode);
            
            Group grp1 = factory.createGroup("GRP1");
            Group grp2 = factory.createGroup("GRP2");
            Group grp3 = factory.createGroup("GRP3");
            
            ds1.addGroup(grp1);
            ds1.addGroup(grp2);
            ds1.addGroup(grp3);
            
            Long dsId = dao.saveDataSet(ds1.toDTO());
            
            int max = dao.reserveIdentifierSpace(dsId, "GRP2", 10);
            assertEquals("New max suffix is incorrect", 10, max);
            
            max = dao.reserveIdentifierSpace(dsId, "GRP2", 15);
            assertEquals("New max suffix is incorrect", 25, max);
            
            try{
                dao.reserveIdentifierSpace(-1L, "GRP2", 10);
                fail("Exception should have been thrown when trying to reserve identifier space using an invalid dataset id");
            }
            catch(DAOException ex){
                //do nothing
            }
            
            try{
                dao.reserveIdentifierSpace(dsId, "GRP4", 10);
                fail("Exception should have been thrown when trying to reserve identifier space using an invalid group");
            }
            catch(DAOException ex){
                //do nothing
            }
            
            try{
                dao.reserveIdentifierSpace(dsId, "GRP2", 0);
                fail("Exception should have been thrown when trying to reserve identifier space using an invalid number of identifiers");
            }
            catch(DAOException ex){
                //do nothing
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail(ex.toString());
        }
    }
    
    /**
     * Unit test for patching an existing DataSet in the repository.
     */
    public void testPatchDataSet_Update(){
        try{
            String name = "testSaveDataSet_Update - "+(new Date()).toString();
            DataSet dataSet = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
            dataSet.setProjectCode(projectCode);
            Document doc1 = factory.createDocument("Doc 1");
            dataSet.addDocument(doc1);
            
            Long dataSetId = dao.saveDataSet(dataSet.toDTO());
            dataSet = null;
            
            dataSet = dao.getDataSet(dataSetId).toHibernate();
            Document doc2 = factory.createDocument("Doc 2");
            dataSet.addDocument(doc2);
            
            dao.patchDataSet(dataSet.toDTO());
            
            assertEquals("Unique identifier of DataSet has changed (1)", dataSetId, dataSet.getId());
            
            dataSet = null;
            dataSet = dao.getDataSet(dataSetId).toHibernate();
            assertEquals("DataSet does not have two children",2,dataSet.numDocuments());
            
            dao.publishDataSet(dataSetId);
            
            dataSet = dao.getDataSet(dataSetId).toHibernate();
            Document doc3 = factory.createDocument("Doc 3");
            dataSet.addDocument(doc3);
            
            dao.patchDataSet(dataSet.toDTO());
            
            assertEquals("Unique identifier of DataSet has changed (2)", dataSetId, dataSet.getId());
            
            dataSet = null;
            dataSet = dao.getDataSet(dataSetId).toHibernate();
            assertEquals("DataSet does not have three documents",3,dataSet.numDocuments());
            
        }
        catch(Exception ex){
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
    
    /**
     * Configure the standard document statuses
     * @param factory HibernateFactory
     * @param document the IDocument for which statuses must be added
     */
    public static void createDocumentStatuses(Factory factory, Document document) {
        Status incomplete = factory.createStatus(Status.DOC_STATUS_INCOMPLETE,
                "Incomplete", 0);
        Status pending = factory.createStatus(Status.DOC_STATUS_PENDING,
                "Pending Approval", 1);
        Status rejected = factory.createStatus(Status.DOC_STATUS_REJECTED,
                "Rejected", 2);
        Status approved = factory.createStatus(Status.DOC_STATUS_APPROVED,
                "Approved", 3);
        Status complete = factory.createStatus(Status.DOC_STATUS_COMPLETE,
        		"Complete", 4);
        
        incomplete.addStatusTransition(pending);
        incomplete.addStatusTransition(complete);
        complete.addStatusTransition(incomplete);
        complete.addStatusTransition(pending);
        pending.addStatusTransition(incomplete);
        pending.addStatusTransition(rejected);
        pending.addStatusTransition(approved);
        rejected.addStatusTransition(pending);
        approved.addStatusTransition(pending);
        
        document.addStatus(incomplete);
        document.addStatus(pending);
        document.addStatus(rejected);
        document.addStatus(approved);
        document.addStatus(complete);
    }

}
