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

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.RepositoryDAO;

public class DeleteObjectsTests extends DAOTest {

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

    public void testRemoveStatus(){
        try{
            String name = "testRemoveStatus - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Document d = factory.createDocument("D");
            ds.addDocument(d);
            String stat1 = "Status 1";
            Status s1 = factory.createStatus(stat1, 1);
            d.addStatus(s1);
            String stat2 = "Status 2";
            Status s2 = factory.createStatus(stat2, 2);
            d.addStatus(s2);
            d.removeStatus(0);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            assertEquals("Folder contains the wrong number of statuses",1,ds.getDocument(0).numStatus());
            assertEquals("Status has the wrong name",stat2,ds.getDocument(0).getStatus(0).getShortName());
            
            Long statId = ds.getDocument(0).getStatus(0).getId();
            
            String stat3 = "Status 3";
            Status s3 = factory.createStatus(stat3, 3);
            
            ds.getDocument(0).addStatus(s3);
            ds.getDocument(0).removeStatus(0);
            
            dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            assertEquals("Folder contains the wrong number of statuses",1,ds.getDocument(0).numStatus());
            assertEquals("Status has the wrong name",stat3,ds.getDocument(0).getStatus(0).getShortName());

            try{
                dao.getPersistent(statId);
                fail("Exception should have been thrown when trying to get status object with id = "+statId);
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
    
    public void testRemoveChild(){
        try{
            String name = "testRemoveChild - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            String f1Name = "D1";
            Document d1 = factory.createDocument(f1Name);
            String f2Name = "D2";
            Document d2 = factory.createDocument(f2Name);
            ds.addDocument(d1);
            ds.addDocument(d2);
            ds.removeDocument(0);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            assertEquals("Dataset contains the wrong number of children",1,ds.numDocuments());
            assertEquals("Child has the wrong name",f2Name,ds.getDocument(0).getName());
            
            Long childId = ds.getDocument(0).getId();
            
            String d3Name = "D3";
            Document d3 = factory.createDocument(d3Name);
            ds.addDocument(d3);
            ds.removeDocument(0);
            
            dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            assertEquals("Dataset contains the wrong number of children",1,ds.numDocuments());
            assertEquals("Child has the wrong name",d3Name,ds.getDocument(0).getName());

            try{
                dao.getPersistent(childId);
                fail("Exception should have been thrown when trying to get object with id = "+childId);
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
    
    public void testRemoveConsentForm(){
        try{
            String name = "testRemoveConsentForm - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Document d1 = factory.createDocument("D1");
            ds.addDocument(d1);
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            String pcf1Desc = "PCF1";
            PrimaryConsentForm pcf1 = factory.createPrimaryConsentForm();
            pcf1.setQuestion(pcf1Desc);
            String pcf2Desc = "PCF2";
            PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
            pcf2.setQuestion(pcf2Desc);
            cfg.addConsentForm(pcf1);
            cfg.addConsentForm(pcf2);
            cfg.removeConsentForm(0);
            ds.addAllConsentFormGroup(cfg);
            d1.addConsentFormGroup(cfg);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            assertEquals("Consent form group has the wrong number of consent forms",1,ds.getDocument(0).getConsentFormGroup(0).numConsentForms());
            assertEquals("Consent form has the wrong description",pcf2Desc,ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0).getQuestion());
            
            String pcf3Desc = "PCF3";
            PrimaryConsentForm pcf3 = factory.createPrimaryConsentForm();
            pcf3.setQuestion(pcf3Desc);
            
            cfg = ds.getDocument(0).getConsentFormGroup(0);
            Long pcfId = cfg.getConsentForm(0).getId();
            cfg.addConsentForm(pcf3);
            cfg.removeConsentForm(0);
            
            dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();

            assertEquals("Consent form group has the wrong number of consent forms",1,ds.getDocument(0).getConsentFormGroup(0).numConsentForms());
            assertEquals("Consent form has the wrong description",pcf3Desc,ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0).getQuestion());

            try{
                dao.getPersistent(pcfId);
                fail("Exception should have been thrown when trying to get object with id = "+pcfId);
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
    
    public void testRemoveAssociatedConsentForm(){
        try{
            String name = "testRemoveAssociatedConsentForm - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Document d1 = factory.createDocument("D1");
            ds.addDocument(d1);
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            String pcf1Desc = "PCF1";
            PrimaryConsentForm pcf1 = factory.createPrimaryConsentForm();
            pcf1.setQuestion(pcf1Desc);
            String acf1Desc = "ACF1";
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            acf1.setQuestion(acf1Desc);
            String acf2Desc = "ACF2";
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            acf2.setQuestion(acf2Desc);
            pcf1.addAssociatedConsentForm(acf1);
            pcf1.addAssociatedConsentForm(acf2);
            pcf1.removeAssociatedConsentForm(0);
            cfg.addConsentForm(pcf1);
            ds.addAllConsentFormGroup(cfg);
            d1.addConsentFormGroup(cfg);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            pcf1 = ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0);
            assertEquals("Primary consent form has the wrong number of associated consent forms",1,pcf1.numAssociatedConsentForms());
            assertEquals("Associated consent form has the wrong description",acf2Desc,pcf1.getAssociatedConsentForm(0).getQuestion());
            
            String acf3Desc = "PCF3";
            AssociatedConsentForm acf3 = factory.createAssociatedConsentForm();
            acf3.setQuestion(acf3Desc);
            
            Long acfId = pcf1.getAssociatedConsentForm(0).getId();
            pcf1.addAssociatedConsentForm(acf3);
            pcf1.removeAssociatedConsentForm(0);
            
            dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();

            pcf1 = ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0);
            assertEquals("Primary consent form has the wrong number of associated consent forms",1,pcf1.numAssociatedConsentForms());
            assertEquals("Associated consent form has the wrong description",acf3Desc,pcf1.getAssociatedConsentForm(0).getQuestion());

            try{
                dao.getPersistent(acfId);
                fail("Exception should have been thrown when trying to get object with id = "+acfId);
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
    
    public void testSetInfoSheet(){
        try{
            String name = "testSetInfoSheet - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            BinaryObject bo1 = factory.createBinaryObject(new byte[10]);
            String bo1Desc = "BO1";
            bo1.setDescription(bo1Desc);
            ds.setInfoSheet(bo1);
            
            BinaryObject bo2 = factory.createBinaryObject(new byte[10]);
            String bo2Desc = "BO2";
            bo2.setDescription(bo2Desc);
            ds.setInfoSheet(bo2);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            assertEquals("Info sheet has the wrong description",bo2Desc,ds.getInfoSheet().getDescription());
            
            Long boId = ds.getInfoSheet().getId();
            
            BinaryObject bo3 = factory.createBinaryObject(new byte[10]);
            String bo3Desc = "BO3";
            bo3.setDescription(bo3Desc);
            ds.setInfoSheet(bo3);
            
            dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();

            assertEquals("Info sheet has the wrong description",bo3Desc,ds.getInfoSheet().getDescription());
            
            try{
                dao.getPersistent(boId);
                fail("Exception should have been thrown when trying to get deleted binary object with id = "+boId);
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
    
    public void testRemoveOption(){
        try{
            String name = "testRemoveOption - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Document doc1 = factory.createDocument("D1");
            ds.addDocument(doc1);
            OptionEntry oe1 = factory.createOptionEntry("OE1");
            String o1Name = "Option 1";
            Option o1 = factory.createOption(o1Name, o1Name);
            String o2Name = "Option 2";
            Option o2 = factory.createOption(o2Name, o2Name);
            oe1.addOption(o1);
            oe1.addOption(o2);
            oe1.removeOption(0);
            doc1.addEntry(oe1);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            oe1 = (OptionEntry)ds.getDocument(0).getEntry(0);
            assertEquals("Option entry has the wrong number of options",1,oe1.numOptions());
            assertEquals("Option has the wrong text value",o2Name,oe1.getOption(0).getDisplayText());
            
            Long oId = oe1.getOption(0).getId();
            
            String o3Name = "Option 3";
            Option o3 = factory.createOption(o3Name, o3Name);
            
            oe1.addOption(o3);
            oe1.removeOption(0);
            
            dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            oe1 = (OptionEntry)ds.getDocument(0).getEntry(0);
            assertEquals("Option entry has the wrong number of options",1,oe1.numOptions());
            assertEquals("Option has the wrong text value",o3Name,oe1.getOption(0).getDisplayText());
            
            try{
                dao.getPersistent(oId);
                fail("Exception should have been thrown when trying to get deleted option object with id "+oId);
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
    
    public void testRemoveOptionDependent(){
        try{
            String name = "testRemoveOptionDependent - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Document doc1 = factory.createDocument("D1");
            ds.addDocument(doc1);
            TextEntry te1 = factory.createTextEntry("TE1");
            doc1.addEntry(te1);
            OptionEntry oe1 = factory.createOptionEntry("OE1");
            String o1Name = "Option 1";
            Option o1 = factory.createOption(o1Name, o1Name);
            oe1.addOption(o1);
            OptionDependent od1 = factory.createOptionDependent();
            od1.setDependentEntry(te1);
            od1.setEntryStatus(EntryStatus.MANDATORY);
            OptionDependent od2 = factory.createOptionDependent();
            od2.setDependentEntry(te1);
            od2.setEntryStatus(EntryStatus.OPTIONAL);
            o1.addOptionDependent(od1);
            o1.addOptionDependent(od2);
            o1.removeOptionDependent(0);
            doc1.addEntry(oe1);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            o1 = ((OptionEntry)ds.getDocument(0).getEntry(1)).getOption(0);
            assertEquals("Option has the wrong number of option dependents",1,o1.numOptionDependents());
            assertEquals("Option dependent has the wrong entry status",EntryStatus.OPTIONAL,o1.getOptionDependent(0).getEntryStatus());
            
            Long odId = o1.getOptionDependent(0).getId();
            
            OptionDependent od3 = factory.createOptionDependent();
            te1 = (TextEntry)ds.getDocument(0).getEntry(0);
            od3.setDependentEntry(te1);
            od3.setEntryStatus(EntryStatus.DISABLED);
            
            o1.addOptionDependent(od3);
            o1.removeOptionDependent(0);
            
            dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            o1 = ((OptionEntry)ds.getDocument(0).getEntry(1)).getOption(0);
            assertEquals("Option has the wrong number of option dependents",1,o1.numOptionDependents());
            assertEquals("Option dependent has the wrong entry status",EntryStatus.DISABLED,o1.getOptionDependent(0).getEntryStatus());
            
            try{
                dao.getPersistent(odId);
                fail("Exception should have been thrown when trying to get deleted option dependent object with id "+odId);
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
    
    public void testPrimaryConsentFormSetEDoc(){
        try{
            String name = "testPrimaryConsentFormSetEDoc - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Document d = factory.createDocument("D1");
            DocumentOccurrence docOcc = factory.createDocumentOccurrence("DO1");
            d.addOccurrence(docOcc);
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            ds.addAllConsentFormGroup(cfg);
            PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
            BinaryObject bo1 = factory.createBinaryObject(new byte[10]);
            String bo1Desc = "BO1";
            bo1.setDescription(bo1Desc);
            BinaryObject bo2 = factory.createBinaryObject(new byte[10]);
            String bo2Desc = "BO2";
            bo2.setDescription(bo2Desc);
            pcf.setElectronicDocument(bo1);
            pcf.setElectronicDocument(bo2);
            cfg.addConsentForm(pcf);
            d.addConsentFormGroup(cfg);
            ds.addDocument(d);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();

            pcf = ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0);
            assertEquals("Consent form electronic document has the wrong description",bo2Desc,pcf.getElectronicDocument().getDescription());
            
            Long boId = pcf.getElectronicDocument().getId();
            
            BinaryObject bo3 = factory.createBinaryObject(new byte[10]);
            String bo3Desc = "BO3";
            bo3.setDescription(bo3Desc);
            pcf.setElectronicDocument(bo3);
            
            dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();

            pcf = ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0);
            assertEquals("Consent form electronic document has the wrong description",bo3Desc,pcf.getElectronicDocument().getDescription());
            
            try{
                dao.getPersistent(boId);
                fail("Exception should have been thrown when trying to get deleted binary object with id "+boId);
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

    public void testAssociatedConsentFormSetEDoc(){
        try{
            String name = "testAssociatedConsentFormSetEDoc - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Document d = factory.createDocument("D1");
            DocumentOccurrence docOcc = factory.createDocumentOccurrence("DO1");
            d.addOccurrence(docOcc);
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            ds.addAllConsentFormGroup(cfg);
            PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf = factory.createAssociatedConsentForm();
            BinaryObject bo1 = factory.createBinaryObject(new byte[10]);
            String bo1Desc = "BO1";
            bo1.setDescription(bo1Desc);
            BinaryObject bo2 = factory.createBinaryObject(new byte[10]);
            String bo2Desc = "BO2";
            bo2.setDescription(bo2Desc);
            acf.setElectronicDocument(bo1);
            acf.setElectronicDocument(bo2);
            pcf.addAssociatedConsentForm(acf);
            cfg.addConsentForm(pcf);
            d.addConsentFormGroup(cfg);
            ds.addDocument(d);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();

            acf = ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0).getAssociatedConsentForm(0);
            assertEquals("Associated consent form electronic document has the wrong description",bo2Desc,acf.getElectronicDocument().getDescription());
            
            Long boId = acf.getElectronicDocument().getId();
            
            BinaryObject bo3 = factory.createBinaryObject(new byte[10]);
            String bo3Desc = "BO3";
            bo3.setDescription(bo3Desc);
            acf.setElectronicDocument(bo3);
            
            dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();

            acf = ds.getDocument(0).getConsentFormGroup(0).getConsentForm(0).getAssociatedConsentForm(0);
            assertEquals("Associated consent form electronic document has the wrong description",bo3Desc,acf.getElectronicDocument().getDescription());
            
            try{
                dao.getPersistent(boId);
                fail("Exception should have been thrown when trying to get deleted binary object with id "+boId);
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
    
    public void testRemoveValidationRule(){
        try{
            String name = "testRemoveValidationRule - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            ValidationRule vr1 = factory.createTextValidationRule();
            String vr1Desc = "VR1";
            vr1.setDescription(vr1Desc);
            ValidationRule vr2 = factory.createTextValidationRule();
            String vr2Desc = "VR2";
            vr2.setDescription(vr2Desc);
            ds.addValidationRule(vr1);
            ds.addValidationRule(vr2);
            ds.removeValidationRule(0);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            assertEquals("Dataset has the wrong number of validation rules",1,ds.numValidationRules());
            assertEquals("Validation rule has the wrong description",vr2Desc,ds.getValidationRule(0).getDescription());
            
            Long vrId = ds.getValidationRule(0).getId();
            
            ValidationRule vr3 = factory.createTextValidationRule();
            String vr3Desc = "VR3";
            vr3.setDescription(vr3Desc);
            
            ds.addValidationRule(vr3);
            ds.removeValidationRule(0);
            
            dao.saveDataSet(ds.toDTO());
            ds = dao.getDataSet(dsId).toHibernate();
            
            assertEquals("Dataset has the wrong number of validation rules",1,ds.numValidationRules());
            assertEquals("Validation rule has the wrong description",vr3Desc,ds.getValidationRule(0).getDescription());
            
            try{
                dao.getPersistent(vrId);
                fail("Exception should have been thrown when trying to get deleted validation rule with id "+vrId);
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
    
}
