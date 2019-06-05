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

public class IDataSetTest extends ModelTest {
    
    @Test()
	public void testConstructor1(){
            String name = "DataSet 1";
            DataSet ds = factory.createDataset(name);
            AssertJUnit.assertEquals("Element has the wrong name",name,ds.getName());
            AssertJUnit.assertNull("Element has non-null display text",ds.getDisplayText());
    }

    @Test()
	public void testConstructor2(){
            String name = "DataSet 1";
            String displayText = "Display text";
            DataSet ds = factory.createDataset(name, displayText);
            AssertJUnit.assertEquals("Element has the wrong name",name,ds.getName());
            AssertJUnit.assertEquals("Element has the wrong display text",displayText,ds.getDisplayText());
    }

    @Test()
	public void testGenerateInstance_Blank() {
            String name = "DataSet 1";
            DataSet ds = factory.createDataset(name);
            Record instance = ds.generateInstance();
            AssertJUnit.assertNotNull("Instance is null",instance);
            AssertJUnit.assertTrue("Instance is not a Record",instance instanceof Record);
    }

    @Test()
	public void testGetAllConsentFormGroup_Success(){
            DataSet ds = factory.createDataset("DS");
            String cfgDesc1 = "Group 2";
            String cfgDesc2 = "Group 3";
            {
                Document d1 = factory.createDocument("D1");
                Document d2 = factory.createDocument("D2");
                ds.addDocument(d1);
                ds.addDocument(d2);
                ConsentFormGroup cfg2 = factory.createConsentFormGroup();
                cfg2.setDescription(cfgDesc1);
                ds.addAllConsentFormGroup(cfg2);
                d1.addConsentFormGroup(cfg2);
                ConsentFormGroup cfg3 = factory.createConsentFormGroup();
                cfg3.setDescription(cfgDesc2);
                ds.addAllConsentFormGroup(cfg3);
                d2.addConsentFormGroup(cfg3);
            }
            
            ConsentFormGroup cfg1 = ds.getAllConsentFormGroup(0);
            AssertJUnit.assertEquals("Consent form group has the wrong description",cfgDesc1,cfg1.getDescription());
            ConsentFormGroup cfg2 = ds.getAllConsentFormGroup(1);
            AssertJUnit.assertEquals("Consent form group has the wrong description",cfgDesc2,cfg2.getDescription());
    }
    
    @Test()
	public void testGetAllConsentFormGroup_InvalidId(){
            DataSet ds = factory.createDataset("DS");
            String cfgDesc2 = "Group 2";
            String cfgDesc3 = "Group 3";
            {
                Document d1 = factory.createDocument("D1");
                Document d2 = factory.createDocument("D2");
                ds.addDocument(d1);
                ds.addDocument(d2);
                ConsentFormGroup cfg2 = factory.createConsentFormGroup();
                cfg2.setDescription(cfgDesc2);
                ds.addAllConsentFormGroup(cfg2);
                d1.addConsentFormGroup(cfg2);
                ConsentFormGroup cfg3 = factory.createConsentFormGroup();
                cfg3.setDescription(cfgDesc3);
                ds.addAllConsentFormGroup(cfg3);
                d2.addConsentFormGroup(cfg3);
            }
            
            try{
                ds.getAllConsentFormGroup(-1);
                Assert.fail("Exception should have been thrown when trying to get a consent form group with an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                ds.getAllConsentFormGroup(3);
                Assert.fail("Exception should have been thrown when trying to get a consent form group with an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testNumAllConsentFormGroups(){
            DataSet ds = factory.createDataset("DS");
            Document d1 = factory.createDocument("D1");
            Document d2 = factory.createDocument("D2");
            ds.addDocument(d1);
            ds.addDocument(d1);
            ConsentFormGroup cfg2 = factory.createConsentFormGroup();
            ds.addAllConsentFormGroup(cfg2);
            d1.addConsentFormGroup(cfg2);
            ConsentFormGroup cfg3 = factory.createConsentFormGroup();
            ds.addAllConsentFormGroup(cfg3);
            d2.addConsentFormGroup(cfg3);
            
            AssertJUnit.assertEquals("DataSet has the wrong number of consent form groups",2,ds.numAllConsentFormGroups());
    }
    
    @Test()
	public void testNumValidationRules(){
            DataSet ds = factory.createDataset("DS");
            TextValidationRule rule1 = factory.createTextValidationRule();
            TextValidationRule rule2 = factory.createTextValidationRule();
            TextValidationRule rule3 = factory.createTextValidationRule();
            ds.addValidationRule(rule1);
            ds.addValidationRule(rule2);
            ds.addValidationRule(rule3);
            
            AssertJUnit.assertEquals("DataSet has the wrong number of validation rules",3,ds.numValidationRules());
    }
    
    @Test()
	public void testAddValidationRule(){
            DataSet ds = factory.createDataset("DS");
            TextValidationRule rule1 = factory.createTextValidationRule();
            String desc = "Rule 1";
            rule1.setDescription(desc);
            ds.addValidationRule(rule1);
            
            AssertJUnit.assertEquals("Validation rule does not have the correct description",desc,ds.getValidationRule(0).getDescription());
    }
    
    @Test()
	public void testAddValidationRule_Null(){
            DataSet ds = factory.createDataset("DS");
            
            try{
                ds.addValidationRule(null);
                Assert.fail("Exception should have been thrown when trying to add a null validation rule");
            }
            catch(ModelException ex){
                //do nothing
            }        
    }
    
    @Test()
	public void testGetValidationRule_Success(){
            DataSet ds = factory.createDataset("DS");
            TextValidationRule rule1 = factory.createTextValidationRule();
            String desc1 = "Rule 1";
            rule1.setDescription(desc1);
            TextValidationRule rule2 = factory.createTextValidationRule();
            String desc2 = "Rule 2";
            rule2.setDescription(desc2);
            TextValidationRule rule3 = factory.createTextValidationRule();
            String desc3 = "Rule 3";
            rule3.setDescription(desc3);
            ds.addValidationRule(rule1);
            ds.addValidationRule(rule2);
            ds.addValidationRule(rule3);
            
            AssertJUnit.assertEquals("Rule at index 0 does not have the correct description",desc1,ds.getValidationRule(0).getDescription());
            AssertJUnit.assertEquals("Rule at index 1 does not have the correct description",desc2,ds.getValidationRule(1).getDescription());
            AssertJUnit.assertEquals("Rule at index 2 does not have the correct description",desc3,ds.getValidationRule(2).getDescription());
    }
    
    @Test()
	public void testGetValidationRule_InvalidId(){
            DataSet ds = factory.createDataset("DS");
            TextValidationRule rule1 = factory.createTextValidationRule();
            String desc1 = "Rule 1";
            rule1.setDescription(desc1);
            TextValidationRule rule2 = factory.createTextValidationRule();
            String desc2 = "Rule 2";
            rule2.setDescription(desc2);
            TextValidationRule rule3 = factory.createTextValidationRule();
            String desc3 = "Rule 3";
            rule3.setDescription(desc3);
            ds.addValidationRule(rule1);
            ds.addValidationRule(rule2);
            ds.addValidationRule(rule3);

            try{
                ds.getValidationRule(-1);
                Assert.fail("Exception should have been thrown when trying to get a validation rule using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                ds.getValidationRule(3);
                Assert.fail("Exception should have been thrown when trying to get a validation rule using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testRemoveValidation_Success(){
            DataSet ds = factory.createDataset("DS");
            TextValidationRule rule1 = factory.createTextValidationRule();
            String desc1 = "Rule 1";
            rule1.setDescription(desc1);
            TextValidationRule rule2 = factory.createTextValidationRule();
            String desc2 = "Rule 2";
            rule2.setDescription(desc2);
            TextValidationRule rule3 = factory.createTextValidationRule();
            String desc3 = "Rule 3";
            rule3.setDescription(desc3);
            ds.addValidationRule(rule1);
            ds.addValidationRule(rule2);
            ds.addValidationRule(rule3);

            ds.removeValidationRule(0);
            
            AssertJUnit.assertEquals("DataSet has the wrong number of validation rules",2,ds.numValidationRules());
            AssertJUnit.assertEquals("Rule at index 0 does not have the correct description",desc2,ds.getValidationRule(0).getDescription());
            AssertJUnit.assertEquals("Rule at index 1 does not have the correct description",desc3,ds.getValidationRule(1).getDescription());
    }
    
    @Test()
	public void testRemoveValidation_InvalidId(){
            DataSet ds = factory.createDataset("DS");
            TextValidationRule rule1 = factory.createTextValidationRule();
            String desc1 = "Rule 1";
            rule1.setDescription(desc1);
            TextValidationRule rule2 = factory.createTextValidationRule();
            String desc2 = "Rule 2";
            rule2.setDescription(desc2);
            TextValidationRule rule3 = factory.createTextValidationRule();
            String desc3 = "Rule 3";
            rule3.setDescription(desc3);
            ds.addValidationRule(rule1);
            ds.addValidationRule(rule2);
            ds.addValidationRule(rule3);

            try{
                ds.removeValidationRule(-1);
                Assert.fail("Exception should have been thrown when trying to remove a validation rule using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                ds.removeValidationRule(3);
                Assert.fail("Exception should have been thrown when trying to remove a validation rule using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testNumTransformers(){
            DataSet ds = factory.createDataset("DS");
            Transformer t1 = factory.createTransformer("url1", "ns1", "op1", "org.psygrid.data.model.hibernate.TextValue");
            Transformer t2 = factory.createTransformer("url2", "ns2", "op2", "org.psygrid.data.model.hibernate.TextValue");
            Transformer t3 = factory.createTransformer("url3", "ns3", "op3", "org.psygrid.data.model.hibernate.TextValue");
            ds.addTransformer(t1);
            ds.addTransformer(t2);
            ds.addTransformer(t3);
            
            AssertJUnit.assertEquals("DataSet has the wrong number of transformers",3,ds.numTransformers());
    }
    
    @Test()
	public void testAddTransformer_Success(){
            DataSet ds = factory.createDataset("DS");
            String url1 = "url1";
            String ns1 = "ns1";
            String op1 = "op1";
            Transformer t1 = factory.createTransformer(url1, ns1, op1, "org.psygrid.data.model.hibernate.TextValue");
            ds.addTransformer(t1);
            
            AssertJUnit.assertEquals("DataSet has the wrong number of transformers",1,ds.numTransformers());
            AssertJUnit.assertEquals("Validation rule does not have the correct url",url1,ds.getTransformer(0).getWsUrl());
            AssertJUnit.assertEquals("Validation rule does not have the correct namespace",ns1,ds.getTransformer(0).getWsNamespace());
            AssertJUnit.assertEquals("Validation rule does not have the correct operation",op1,ds.getTransformer(0).getWsOperation());
    }
    
    @Test()
	public void testAddTransformer_Null(){
            DataSet ds = factory.createDataset("DS");
            
            try{
                ds.addTransformer(null);
                Assert.fail("Exception should have been thrown when trying to add a null transformer");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetTransformer_Success(){
            DataSet ds = factory.createDataset("DS");
            String url1 = "url1";
            String ns1 = "ns1";
            String op1 = "op1";
            Transformer t1 = factory.createTransformer(url1, ns1, op1, "org.psygrid.data.model.hibernate.TextValue");
            String url2 = "url2";
            String ns2 = "ns2";
            String op2 = "op2";
            Transformer t2 = factory.createTransformer(url2, ns2, op2, "org.psygrid.data.model.hibernate.TextValue");
            String url3 = "url3";
            String ns3 = "ns3";
            String op3 = "op3";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            ds.addTransformer(t1);
            ds.addTransformer(t2);
            ds.addTransformer(t3);
            
            AssertJUnit.assertEquals("Transformer at index 0 does not have the correct url",url1,ds.getTransformer(0).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 0 does not have the correct namespace",ns1,ds.getTransformer(0).getWsNamespace());
            AssertJUnit.assertEquals("Transformer at index 0 does not have the correct operation",op1,ds.getTransformer(0).getWsOperation());
            AssertJUnit.assertEquals("Transformer at index 1 does not have the correct url",url2,ds.getTransformer(1).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 1 does not have the correct namespace",ns2,ds.getTransformer(1).getWsNamespace());
            AssertJUnit.assertEquals("Transformer at index 1 does not have the correct operation",op2,ds.getTransformer(1).getWsOperation());
            AssertJUnit.assertEquals("Transformer at index 2 does not have the correct url",url3,ds.getTransformer(2).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 2 does not have the correct namespace",ns3,ds.getTransformer(2).getWsNamespace());
            AssertJUnit.assertEquals("Transformer at index 2 does not have the correct operation",op3,ds.getTransformer(2).getWsOperation());
    }
    
    @Test()
	public void testGetTransformer_InvalidId(){
            DataSet ds = factory.createDataset("DS");
            String url1 = "url1";
            String ns1 = "ns1";
            String op1 = "op1";
            Transformer t1 = factory.createTransformer(url1, ns1, op1, "org.psygrid.data.model.hibernate.TextValue");
            String url2 = "url2";
            String ns2 = "ns2";
            String op2 = "op2";
            Transformer t2 = factory.createTransformer(url2, ns2, op2, "org.psygrid.data.model.hibernate.TextValue");
            String url3 = "url3";
            String ns3 = "ns3";
            String op3 = "op3";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            ds.addTransformer(t1);
            ds.addTransformer(t2);
            ds.addTransformer(t3);

            try{
                ds.getTransformer(-1);
                Assert.fail("Exception should have been thrown when trying to get a transformer using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                ds.getTransformer(3);
                Assert.fail("Exception should have been thrown when trying to get a transformer using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testRemoveTransformer_Success(){
            DataSet ds = factory.createDataset("DS");
            String url1 = "url1";
            String ns1 = "ns1";
            String op1 = "op1";
            Transformer t1 = factory.createTransformer(url1, ns1, op1, "org.psygrid.data.model.hibernate.TextValue");
            String url2 = "url2";
            String ns2 = "ns2";
            String op2 = "op2";
            Transformer t2 = factory.createTransformer(url2, ns2, op2, "org.psygrid.data.model.hibernate.TextValue");
            String url3 = "url3";
            String ns3 = "ns3";
            String op3 = "op3";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            ds.addTransformer(t1);
            ds.addTransformer(t2);
            ds.addTransformer(t3);

            ds.removeTransformer(0);
            
            AssertJUnit.assertEquals("DataSet has the wrong number of transformers",2,ds.numTransformers());
            AssertJUnit.assertEquals("Transformer at index 0 does not have the correct url",url2,ds.getTransformer(0).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 1 does not have the correct url",url3,ds.getTransformer(1).getWsUrl());
    }
    
    @Test()
	public void testRemoveTransformer_InvalidId(){
            DataSet ds = factory.createDataset("DS");
            String url1 = "url1";
            String ns1 = "ns1";
            String op1 = "op1";
            Transformer t1 = factory.createTransformer(url1, ns1, op1, "org.psygrid.data.model.hibernate.TextValue");
            String url2 = "url2";
            String ns2 = "ns2";
            String op2 = "op2";
            Transformer t2 = factory.createTransformer(url2, ns2, op2, "org.psygrid.data.model.hibernate.TextValue");
            String url3 = "url3";
            String ns3 = "ns3";
            String op3 = "op3";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            ds.addTransformer(t1);
            ds.addTransformer(t2);
            ds.addTransformer(t3);

            try{
                ds.removeTransformer(-1);
                Assert.fail("Exception should have been thrown when trying to remove a transformer using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                ds.removeTransformer(3);
                Assert.fail("Exception should have been thrown when trying to remove a transformer using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testNumDocumentGroups(){
            DataSet ds = factory.createDataset("DS");
            DocumentGroup dg1 = factory.createDocumentGroup("Group 1");
            DocumentGroup dg2 = factory.createDocumentGroup("Group 2");
            DocumentGroup dg3 = factory.createDocumentGroup("Group 3");
            ds.addDocumentGroup(dg1);
            ds.addDocumentGroup(dg2);
            ds.addDocumentGroup(dg3);
            
            AssertJUnit.assertEquals("DataSet has the wrong number of document groups",3,ds.numDocumentGroups());
    }
    
    @Test()
	public void testAddDocumentGroup_Success(){
            DataSet ds = factory.createDataset("DS");
            String name = "Group 1";
            DocumentGroup dg1 = factory.createDocumentGroup(name);
            ds.addDocumentGroup(dg1);
            
            AssertJUnit.assertEquals("Document group does not have the correct name",name,ds.getDocumentGroup(0).getName());
    }
    
    @Test()
	public void testAddDocumentGroup_Null(){
            DataSet ds = factory.createDataset("DS");
            
            try{
                ds.addDocumentGroup(null);
                Assert.fail("Exception should have been thrown when trying to add a null document group");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetDocumentGroup_Success(){
            DataSet ds = factory.createDataset("DS");
            String name1 = "Group 1";
            DocumentGroup dg1 = factory.createDocumentGroup(name1);
            String name2 = "Group 2";
            DocumentGroup dg2 = factory.createDocumentGroup(name2);
            String name3 = "Group 3";
            DocumentGroup dg3 = factory.createDocumentGroup(name3);
            ds.addDocumentGroup(dg1);
            ds.addDocumentGroup(dg2);
            ds.addDocumentGroup(dg3);
            
            AssertJUnit.assertEquals("Document group at index 0 does not have the correct name",name1,ds.getDocumentGroup(0).getName());
            AssertJUnit.assertEquals("Document group at index 1 does not have the correct name",name2,ds.getDocumentGroup(1).getName());
            AssertJUnit.assertEquals("Document group at index 2 does not have the correct name",name3,ds.getDocumentGroup(2).getName());
    }
    
    @Test()
	public void testGetDocumentGroup_InvalidId(){
            DataSet ds = factory.createDataset("DS");
            String name1 = "Group 1";
            DocumentGroup dg1 = factory.createDocumentGroup(name1);
            String name2 = "Group 2";
            DocumentGroup dg2 = factory.createDocumentGroup(name2);
            String name3 = "Group 3";
            DocumentGroup dg3 = factory.createDocumentGroup(name3);
            ds.addDocumentGroup(dg1);
            ds.addDocumentGroup(dg2);
            ds.addDocumentGroup(dg3);

            try{
                ds.getDocumentGroup(-1);
                Assert.fail("Exception should have been thrown when trying to get a document group using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                ds.getDocumentGroup(3);
                Assert.fail("Exception should have been thrown when trying to get a document group using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testRemoveDocumentGroup_Success(){
            DataSet ds = factory.createDataset("DS");
            String name1 = "Group 1";
            DocumentGroup dg1 = factory.createDocumentGroup(name1);
            String name2 = "Group 2";
            DocumentGroup dg2 = factory.createDocumentGroup(name2);
            String name3 = "Group 3";
            DocumentGroup dg3 = factory.createDocumentGroup(name3);
            ds.addDocumentGroup(dg1);
            ds.addDocumentGroup(dg2);
            ds.addDocumentGroup(dg3);

            ds.removeDocumentGroup(0);
            
            AssertJUnit.assertEquals("DataSet has the wrong number of document groups",2,ds.numDocumentGroups());
            AssertJUnit.assertEquals("Document group at index 0 does not have the correct name",name2,ds.getDocumentGroup(0).getName());
            AssertJUnit.assertEquals("Document group at index 1 does not have the correct name",name3,ds.getDocumentGroup(1).getName());
    }
    
    @Test()
	public void testRemoveDocumentGroup_InvalidId(){
            DataSet ds = factory.createDataset("DS");
            String name1 = "Group 1";
            DocumentGroup dg1 = factory.createDocumentGroup(name1);
            String name2 = "Group 2";
            DocumentGroup dg2 = factory.createDocumentGroup(name2);
            String name3 = "Group 3";
            DocumentGroup dg3 = factory.createDocumentGroup(name3);
            ds.addDocumentGroup(dg1);
            ds.addDocumentGroup(dg2);
            ds.addDocumentGroup(dg3);

            try{
                ds.removeDocumentGroup(-1);
                Assert.fail("Exception should have been thrown when trying to remove a document group using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                ds.removeDocumentGroup(3);
                Assert.fail("Exception should have been thrown when trying to remove a document group using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testNumUnits(){
            DataSet ds = factory.createDataset("DS");
            Unit u1 = factory.createUnit("Unit 1");
            Unit u2 = factory.createUnit("Unit 2");
            Unit u3 = factory.createUnit("Unit 3");
            ds.addUnit(u1);
            ds.addUnit(u2);
            ds.addUnit(u3);
            
            AssertJUnit.assertEquals("DataSet has the wrong number of unit",3,ds.numUnits());
    }
    
    @Test()
	public void testAddUnit_Success(){
            DataSet ds = factory.createDataset("DS");
            String abbrev = "Unit 1";
            Unit u1 = factory.createUnit(abbrev);
            ds.addUnit(u1);
            
            AssertJUnit.assertEquals("Unit does not have the correct name",abbrev,ds.getUnit(0).getAbbreviation());
    }
    
    @Test()
	public void testAddUnit_Null(){
            DataSet ds = factory.createDataset("DS");
            
            try{
                ds.addUnit(null);
                Assert.fail("Exception should have been thrown when trying to add a null unit");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetUnit_Success(){
            DataSet ds = factory.createDataset("DS");
            String abbrev1 = "Unit 1";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "Unit 2";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "Unit 3";
            Unit u3 = factory.createUnit(abbrev3);
            ds.addUnit(u1);
            ds.addUnit(u2);
            ds.addUnit(u3);
            
            AssertJUnit.assertEquals("Unit at index 0 does not have the correct abbreviation",abbrev1,ds.getUnit(0).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 1 does not have the correct abbreviation",abbrev2,ds.getUnit(1).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 2 does not have the correct abbreviation",abbrev3,ds.getUnit(2).getAbbreviation());
    }
    
    @Test()
	public void testGetUnit_InvalidId(){
            DataSet ds = factory.createDataset("DS");
            String abbrev1 = "Unit 1";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "Unit 2";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "Unit 3";
            Unit u3 = factory.createUnit(abbrev3);
            ds.addUnit(u1);
            ds.addUnit(u2);
            ds.addUnit(u3);

            try{
                ds.getUnit(-1);
                Assert.fail("Exception should have been thrown when trying to get a unit using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                ds.getUnit(3);
                Assert.fail("Exception should have been thrown when trying to get a unit using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testRemoveUnit_Success(){
            DataSet ds = factory.createDataset("DS");
            String abbrev1 = "Unit 1";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "Unit 2";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "Unit 3";
            Unit u3 = factory.createUnit(abbrev3);
            ds.addUnit(u1);
            ds.addUnit(u2);
            ds.addUnit(u3);

            ds.removeUnit(0);
            
            AssertJUnit.assertEquals("DataSet has the wrong number of units",2,ds.numUnits());
            AssertJUnit.assertEquals("Unit at index 0 does not have the correct abbreviation",abbrev2,ds.getUnit(0).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 1 does not have the correct abbreviation",abbrev3,ds.getUnit(1).getAbbreviation());
    }
    
    @Test()
	public void testRemoveUnit_InvalidId(){
            DataSet ds = factory.createDataset("DS");
            String abbrev1 = "Unit 1";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "Unit 2";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "Unit 3";
            Unit u3 = factory.createUnit(abbrev3);
            ds.addUnit(u1);
            ds.addUnit(u2);
            ds.addUnit(u3);

            try{
                ds.removeUnit(-1);
                Assert.fail("Exception should have been thrown when trying to remove a unit using an invalid id (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                ds.removeUnit(3);
                Assert.fail("Exception should have been thrown when trying to remove a unit using an invalid id (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetDocument_Success(){
            DataSet ds = factory.createDataset("DS");;
            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            String docName4 = "Sec 4";
            Document doc4 = factory.createDocument(docName4);
            ds.addDocument(doc4);
            String docName5 = "Sec 5";
            Document doc5 = factory.createDocument(docName5);
            ds.addDocument(doc5);

            AssertJUnit.assertEquals("Document retrieved from index 0 has the wrong name",docName1,ds.getDocument(0).getName());
            AssertJUnit.assertEquals("Document retrieved from index 2 has the wrong name",docName3,ds.getDocument(2).getName());
            AssertJUnit.assertEquals("Document retrieved from index 4 has the wrong name",docName5,ds.getDocument(4).getName());
    }
    
    @Test()
	public void testGetDocument_InvalidIndex(){
            DataSet ds = factory.createDataset("DS");;
            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            String docName4 = "Sec 4";
            Document doc4 = factory.createDocument(docName4);
            ds.addDocument(doc4);
            String docName5 = "Sec 5";
            Document doc5 = factory.createDocument(docName5);
            ds.addDocument(doc5);

            try{
                ds.getDocument(-1);
                Assert.fail("Exception should have been thrown when trying to get document with invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                ds.getDocument(5);
                Assert.fail("Exception should have been thrown when trying to get child with invalid index (5)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testNumDocuments(){
            DataSet ds = factory.createDataset("DS");;
            AssertJUnit.assertEquals("DataSet has wrong number of documents",0,ds.numDocuments());

            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            AssertJUnit.assertEquals("DataSet has wrong number of documents",2,ds.numDocuments());
            
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            String docName4 = "Sec 4";
            Document doc4 = factory.createDocument(docName4);
            ds.addDocument(doc4);
            String docName5 = "Sec 5";
            Document doc5 = factory.createDocument(docName5);
            ds.addDocument(doc5);

            AssertJUnit.assertEquals("DataSet has wrong number of documents",5,ds.numDocuments());
    }
    
    @Test()
	public void testRemoveDocument_Success(){
            DataSet ds = factory.createDataset("DS");;
            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            
            ds.removeDocument(1);
            AssertJUnit.assertEquals("DataSet does not have two documents",2,ds.numDocuments());
            AssertJUnit.assertEquals("Document at position 0 has the wrong name",docName1,ds.getDocument(0).getName());
            AssertJUnit.assertEquals("Document at position 1 has the wrong name",docName3,ds.getDocument(1).getName());
    }
    
    @Test()
	public void testRemoveDocument_InvalidIndex(){
            DataSet ds = factory.createDataset("DS");;
            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            
            try{
                ds.removeDocument(-1);
                Assert.fail("Exception should have been thrown when trying to remove document using index of -1");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of documents",3,ds.numDocuments());
                AssertJUnit.assertEquals("Child at index 0 has the wrong name",docName1,ds.getDocument(0).getName());
                AssertJUnit.assertEquals("Child at index 1 has the wrong name",docName2,ds.getDocument(1).getName());
                AssertJUnit.assertEquals("Child at index 2 has the wrong name",docName3,ds.getDocument(2).getName());
            }
            try{
                ds.removeDocument(3);
                Assert.fail("Exception should have been thrown when trying to remove child using index of 3");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of children",3,ds.numDocuments());
                AssertJUnit.assertEquals("Child at index 0 has the wrong name",docName1,ds.getDocument(0).getName());
                AssertJUnit.assertEquals("Child at index 1 has the wrong name",docName2,ds.getDocument(1).getName());
                AssertJUnit.assertEquals("Child at index 2 has the wrong name",docName3,ds.getDocument(2).getName());
            }
    }
    
    @Test()
	public void testMoveDocument_Up(){
            DataSet ds = factory.createDataset("DS");;
            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            String docName4 = "Sec 4";
            Document doc4 = factory.createDocument(docName4);
            ds.addDocument(doc4);
            String docName5 = "Sec 5";
            Document doc5 = factory.createDocument(docName5);
            ds.addDocument(doc5);

            ds.moveDocument(3,1);
            AssertJUnit.assertEquals("DataSet has the wrong number of documents",5,ds.numDocuments());
            AssertJUnit.assertEquals("Document at pos 0 has the wrong name",docName1,ds.getDocument(0).getName());
            AssertJUnit.assertEquals("Document at pos 1 has the wrong name",docName4,ds.getDocument(1).getName());
            AssertJUnit.assertEquals("Document at pos 2 has the wrong name",docName2,ds.getDocument(2).getName());
            AssertJUnit.assertEquals("Document at pos 3 has the wrong name",docName3,ds.getDocument(3).getName());
            AssertJUnit.assertEquals("Document at pos 4 has the wrong name",docName5,ds.getDocument(4).getName());
    }

    @Test()
	public void testMoveDocument_Down(){
            DataSet ds = factory.createDataset("DS");;
            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            String docName4 = "Sec 4";
            Document doc4 = factory.createDocument(docName4);
            ds.addDocument(doc4);
            String docName5 = "Sec 5";
            Document doc5 = factory.createDocument(docName5);
            ds.addDocument(doc5);

            ds.moveDocument(1,3);
            AssertJUnit.assertEquals("Parent has the wrong number of documents",5,ds.numDocuments());
            AssertJUnit.assertEquals("document at pos 0 has the wrong name",docName1,ds.getDocument(0).getName());
            AssertJUnit.assertEquals("document at pos 1 has the wrong name",docName3,ds.getDocument(1).getName());
            AssertJUnit.assertEquals("document at pos 2 has the wrong name",docName4,ds.getDocument(2).getName());
            AssertJUnit.assertEquals("document at pos 3 has the wrong name",docName2,ds.getDocument(3).getName());
            AssertJUnit.assertEquals("document at pos 4 has the wrong name",docName5,ds.getDocument(4).getName());
    }
    
    @Test()
	public void testMoveDocument_InvalidIndex(){
            DataSet ds = factory.createDataset("DS");;
            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            String docName4 = "Sec 4";
            Document doc4 = factory.createDocument(docName4);
            ds.addDocument(doc4);
            String docName5 = "Sec 5";
            Document doc5 = factory.createDocument(docName5);
            ds.addDocument(doc5);

            try{
                ds.moveDocument(-1,3);
                Assert.fail("Exception should have been thrown when invalid current index (-1) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of documents",5,ds.numDocuments());
                AssertJUnit.assertEquals("document at index 0 has the wrong name",docName1,ds.getDocument(0).getName());
                AssertJUnit.assertEquals("document at index 1 has the wrong name",docName2,ds.getDocument(1).getName());
                AssertJUnit.assertEquals("document at index 2 has the wrong name",docName3,ds.getDocument(2).getName());
                AssertJUnit.assertEquals("document at index 3 has the wrong name",docName4,ds.getDocument(3).getName());
                AssertJUnit.assertEquals("document at index 4 has the wrong name",docName5,ds.getDocument(4).getName());
            }
            try{
                ds.moveDocument(5,3);
                Assert.fail("Exception should have been thrown when invalid current index (5) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of documents",5,ds.numDocuments());
                AssertJUnit.assertEquals("document at index 0 has the wrong name",docName1,ds.getDocument(0).getName());
                AssertJUnit.assertEquals("document at index 1 has the wrong name",docName2,ds.getDocument(1).getName());
                AssertJUnit.assertEquals("document at index 2 has the wrong name",docName3,ds.getDocument(2).getName());
                AssertJUnit.assertEquals("document at index 3 has the wrong name",docName4,ds.getDocument(3).getName());
                AssertJUnit.assertEquals("document at index 4 has the wrong name",docName5,ds.getDocument(4).getName());
            }
            try{
                ds.moveDocument(3,-1);
                Assert.fail("Exception should have been thrown when invalid new index (-1) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of documents",5,ds.numDocuments());
                AssertJUnit.assertEquals("document at index 0 has the wrong name",docName1,ds.getDocument(0).getName());
                AssertJUnit.assertEquals("document at index 1 has the wrong name",docName2,ds.getDocument(1).getName());
                AssertJUnit.assertEquals("document at index 2 has the wrong name",docName3,ds.getDocument(2).getName());
                AssertJUnit.assertEquals("document at index 3 has the wrong name",docName4,ds.getDocument(3).getName());
                AssertJUnit.assertEquals("document at index 4 has the wrong name",docName5,ds.getDocument(4).getName());
            }
            try{
                ds.moveDocument(3,5);
                Assert.fail("Exception should have been thrown when invalid new index (5) used.");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of documents",5,ds.numDocuments());
                AssertJUnit.assertEquals("document at index 0 has the wrong name",docName1,ds.getDocument(0).getName());
                AssertJUnit.assertEquals("document at index 1 has the wrong name",docName2,ds.getDocument(1).getName());
                AssertJUnit.assertEquals("document at index 2 has the wrong name",docName3,ds.getDocument(2).getName());
                AssertJUnit.assertEquals("document at index 3 has the wrong name",docName4,ds.getDocument(3).getName());
                AssertJUnit.assertEquals("document at index 4 has the wrong name",docName5,ds.getDocument(4).getName());
            }
    }
    
    @Test()
	public void testInsertDocument_Success(){
            DataSet ds = factory.createDataset("DS");;
            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            String docName4 = "Sec 4";
            Document doc4 = factory.createDocument(docName4);
            ds.addDocument(doc4);
            
            String docName5 = "Sec 5";
            Document doc5 = factory.createDocument(docName5);
            ds.insertDocument(doc5, 2);
            
            AssertJUnit.assertEquals("Incorrect number of documents",5,ds.numDocuments());
            AssertJUnit.assertEquals("document at index 0 has the wrong name",docName1,ds.getDocument(0).getName());
            AssertJUnit.assertEquals("document at index 1 has the wrong name",docName2,ds.getDocument(1).getName());
            AssertJUnit.assertEquals("document at index 2 has the wrong name",docName5,ds.getDocument(2).getName());
            AssertJUnit.assertEquals("document at index 3 has the wrong name",docName3,ds.getDocument(3).getName());
            AssertJUnit.assertEquals("document at index 4 has the wrong name",docName4,ds.getDocument(4).getName());
            
            String docName6 = "Sec 6";
            Document doc6 = factory.createDocument(docName6);
            ds.insertDocument(doc6, 5);
            
            AssertJUnit.assertEquals("Incorrect number of documents",6,ds.numDocuments());
            AssertJUnit.assertEquals("document at index 0 has the wrong name",docName1,ds.getDocument(0).getName());
            AssertJUnit.assertEquals("document at index 1 has the wrong name",docName2,ds.getDocument(1).getName());
            AssertJUnit.assertEquals("document at index 2 has the wrong name",docName5,ds.getDocument(2).getName());
            AssertJUnit.assertEquals("document at index 3 has the wrong name",docName3,ds.getDocument(3).getName());
            AssertJUnit.assertEquals("document at index 4 has the wrong name",docName4,ds.getDocument(4).getName());
            AssertJUnit.assertEquals("document at index 5 has the wrong name",docName6,ds.getDocument(5).getName());
    }
    
    @Test()
	public void testInsertDocument_Null(){
            DataSet ds = factory.createDataset("DS");;
            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            String docName4 = "Sec 4";
            Document doc4 = factory.createDocument(docName4);
            ds.addDocument(doc4);
            
            try{
                ds.insertDocument(null, 1);
                Assert.fail("Exception should have been thrown when trying to add a null document");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of documents",4,ds.numDocuments());                
            }
    }
    
    @Test()
	public void testInsertDocument_InvalidIndex(){
            DataSet ds = factory.createDataset("DS");;
            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            String docName4 = "Sec 4";
            Document doc4 = factory.createDocument(docName4);
            ds.addDocument(doc4);
            
            String docName5 = "Sec 5";
            Document doc5 = factory.createDocument(docName5);
            
            try{
                ds.insertDocument(doc5, -1);
                Assert.fail("Exception not thrown when trying to an insert a document using an invalid index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of documents",4,ds.numDocuments());
                AssertJUnit.assertEquals("document at index 0 has the wrong name",docName1,ds.getDocument(0).getName());
                AssertJUnit.assertEquals("document at index 1 has the wrong name",docName2,ds.getDocument(1).getName());
                AssertJUnit.assertEquals("document at index 3 has the wrong name",docName3,ds.getDocument(2).getName());
                AssertJUnit.assertEquals("document at index 4 has the wrong name",docName4,ds.getDocument(3).getName());
            }
            
            try{
                ds.insertDocument(doc5, 5);
                Assert.fail("Exception not thrown when trying to an insert a document using an invalid index (5)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of documents",4,ds.numDocuments());
                AssertJUnit.assertEquals("document at index 0 has the wrong name",docName1,ds.getDocument(0).getName());
                AssertJUnit.assertEquals("document at index 1 has the wrong name",docName2,ds.getDocument(1).getName());
                AssertJUnit.assertEquals("document at index 3 has the wrong name",docName3,ds.getDocument(2).getName());
                AssertJUnit.assertEquals("document at index 4 has the wrong name",docName4,ds.getDocument(3).getName());
            }
    }
    
    @Test()
	public void testAddDocument_Success(){
            DataSet ds = factory.createDataset("DS");;
            String docName1 = "Sec 1";
            Document doc1 = factory.createDocument(docName1);
            ds.addDocument(doc1);
            String docName2 = "Sec 2";
            Document doc2 = factory.createDocument(docName2);
            ds.addDocument(doc2);
            String docName3 = "Sec 3";
            Document doc3 = factory.createDocument(docName3);
            ds.addDocument(doc3);
            String docName4 = "Sec 4";
            Document doc4 = factory.createDocument(docName4);
            ds.addDocument(doc4);
            
            String docName5 = "Sec 5";
            Document doc5 = factory.createDocument(docName5);
            ds.addDocument(doc5);
            
            AssertJUnit.assertEquals("Incorrect number of documents",5,ds.numDocuments());
            AssertJUnit.assertEquals("Document at index 0 has the wrong name",docName1,ds.getDocument(0).getName());
            AssertJUnit.assertEquals("Document at index 1 has the wrong name",docName2,ds.getDocument(1).getName());
            AssertJUnit.assertEquals("Document at index 2 has the wrong name",docName3,ds.getDocument(2).getName());
            AssertJUnit.assertEquals("Document at index 3 has the wrong name",docName4,ds.getDocument(3).getName());
            AssertJUnit.assertEquals("Document at index 4 has the wrong name",docName5,ds.getDocument(4).getName());
    }
    
    @Test()
	public void testAddDocument_Null(){
            DataSet ds = factory.createDataset("DS");;

            try{
                ds.addDocument(null);
                Assert.fail("Exception should have been thrown when trying to add a null document");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Incorrect number of documents",0,ds.numDocuments());
            }                        
    }

    
}
