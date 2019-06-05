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

import org.psygrid.data.model.hibernate.ConsentFormGroup;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.PrimaryConsentForm;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;

public class IConsentFormGroupTest extends ModelTest {

    @Test()
	public void testNumConsentForms(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            AssertJUnit.assertEquals("Consent form group has the wrong number of consent forms",0,cfg.numConsentForms());
            cfg.addConsentForm(factory.createPrimaryConsentForm());
            AssertJUnit.assertEquals("Consent form group has the wrong number of consent forms",1,cfg.numConsentForms());
            cfg.addConsentForm(factory.createPrimaryConsentForm());
            AssertJUnit.assertEquals("Consent form group has the wrong number of consent forms",2,cfg.numConsentForms());
            cfg.addConsentForm(factory.createPrimaryConsentForm());
            AssertJUnit.assertEquals("Consent form group has the wrong number of consent forms",3,cfg.numConsentForms());
    }
    
    @Test()
	public void testAddConsentForm(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm cf1 = factory.createPrimaryConsentForm();
            String cfDesc1 = "Consent form 1";
            cf1.setQuestion(cfDesc1);
            PrimaryConsentForm cf2 = factory.createPrimaryConsentForm();
            String cfDesc2 = "Consent form 2";
            cf2.setQuestion(cfDesc2);
            PrimaryConsentForm cf3 = factory.createPrimaryConsentForm();
            String cfDesc3 = "Consent form 3";
            cf3.setQuestion(cfDesc3);
            
            cfg.addConsentForm(cf1);
            cfg.addConsentForm(cf2);
            cfg.addConsentForm(cf3);
            
            AssertJUnit.assertEquals("Consent form at index 0 has the wrong description", cfDesc1, cfg.getConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 1 has the wrong description", cfDesc2, cfg.getConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 2 has the wrong description", cfDesc3, cfg.getConsentForm(2).getQuestion());
    }
    
    @Test()
	public void testAddConsentForm_Null(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();

            try{
                cfg.addConsentForm(null);
                Assert.fail("Exception should have been thrown when trying to add a null consent form");
            }
            catch (ModelException ex){
                AssertJUnit.assertEquals("Element contains the wrong number of consent forms",0,cfg.numConsentForms());
            }
    }
    
    @Test()
	public void testGetConsentForm_Success(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm cf1 = factory.createPrimaryConsentForm();
            String cfDesc1 = "Consent form 1";
            cf1.setQuestion(cfDesc1);
            PrimaryConsentForm cf2 = factory.createPrimaryConsentForm();
            String cfDesc2 = "Consent form 2";
            cf2.setQuestion(cfDesc2);
            PrimaryConsentForm cf3 = factory.createPrimaryConsentForm();
            String cfDesc3 = "Consent form 3";
            cf3.setQuestion(cfDesc3);
            
            cfg.addConsentForm(cf1);
            cfg.addConsentForm(cf2);
            cfg.addConsentForm(cf3);

            AssertJUnit.assertEquals("Consent form at index 2 has the wrong description", cfDesc3, cfg.getConsentForm(2).getQuestion());
    }
    
    @Test()
	public void testGetConsentForm_InvalidIndex(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm cf1 = factory.createPrimaryConsentForm();
            String cfDesc1 = "Consent form 1";
            cf1.setQuestion(cfDesc1);
            PrimaryConsentForm cf2 = factory.createPrimaryConsentForm();
            String cfDesc2 = "Consent form 2";
            cf2.setQuestion(cfDesc2);
            PrimaryConsentForm cf3 = factory.createPrimaryConsentForm();
            String cfDesc3 = "Consent form 3";
            cf3.setQuestion(cfDesc3);
            
            cfg.addConsentForm(cf1);
            cfg.addConsentForm(cf2);
            cfg.addConsentForm(cf3);

            try{
                cfg.getConsentForm(-1);
                Assert.fail("Exception should have been thrown when trying to get a consent form using an invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                cfg.getConsentForm(3);
                Assert.fail("Exception should have been thrown when trying to get a consent form using an invalid index (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testInsertConsentForm_Success(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm cf1 = factory.createPrimaryConsentForm();
            String cfDesc1 = "Consent form 1";
            cf1.setQuestion(cfDesc1);
            PrimaryConsentForm cf2 = factory.createPrimaryConsentForm();
            String cfDesc2 = "Consent form 2";
            cf2.setQuestion(cfDesc2);
            PrimaryConsentForm cf3 = factory.createPrimaryConsentForm();
            String cfDesc3 = "Consent form 3";
            cf3.setQuestion(cfDesc3);
            
            cfg.addConsentForm(cf1);
            cfg.addConsentForm(cf2);
            cfg.addConsentForm(cf3);
            
            PrimaryConsentForm cf4 = factory.createPrimaryConsentForm();
            String cfDesc4 = "Consent form 4";
            cf4.setQuestion(cfDesc4);
            
            cfg.insertConsentForm(cf4, 0);
            
            AssertJUnit.assertEquals("Element has the wrong number of consent forms",4,cfg.numConsentForms());
            AssertJUnit.assertEquals("Consent form at index 0 has the wrong description", cfDesc4, cfg.getConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 1 has the wrong description", cfDesc1, cfg.getConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 2 has the wrong description", cfDesc2, cfg.getConsentForm(2).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 3 has the wrong description", cfDesc3, cfg.getConsentForm(3).getQuestion());

            PrimaryConsentForm cf5 = factory.createPrimaryConsentForm();
            String cfDesc5 = "Consent form 5";
            cf5.setQuestion(cfDesc5);
            
            cfg.insertConsentForm(cf5, 4);
            
            AssertJUnit.assertEquals("Element has the wrong number of consent forms",5,cfg.numConsentForms());
            AssertJUnit.assertEquals("Consent form at index 0 has the wrong description", cfDesc4, cfg.getConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 1 has the wrong description", cfDesc1, cfg.getConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 2 has the wrong description", cfDesc2, cfg.getConsentForm(2).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 3 has the wrong description", cfDesc3, cfg.getConsentForm(3).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 4 has the wrong description", cfDesc5, cfg.getConsentForm(4).getQuestion());
    }
    
    @Test()
	public void testInsertConsentForm_Null(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm cf1 = factory.createPrimaryConsentForm();
            String cfDesc1 = "Consent form 1";
            cf1.setQuestion(cfDesc1);
            PrimaryConsentForm cf2 = factory.createPrimaryConsentForm();
            String cfDesc2 = "Consent form 2";
            cf2.setQuestion(cfDesc2);
            PrimaryConsentForm cf3 = factory.createPrimaryConsentForm();
            String cfDesc3 = "Consent form 3";
            cf3.setQuestion(cfDesc3);
            
            cfg.addConsentForm(cf1);
            cfg.addConsentForm(cf2);
            cfg.addConsentForm(cf3);
            
            try{
                cfg.insertConsentForm(null, 1);
                Assert.fail("Exception should have been thrown when trying to insert a null consent form");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Element has the wrong number of consent forms",3,cfg.numConsentForms());                
            }            
    }
    
    @Test()
	public void testInsertConsentForm_InvalidIndex(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm cf1 = factory.createPrimaryConsentForm();
            String cfDesc1 = "Consent form 1";
            cf1.setQuestion(cfDesc1);
            PrimaryConsentForm cf2 = factory.createPrimaryConsentForm();
            String cfDesc2 = "Consent form 2";
            cf2.setQuestion(cfDesc2);
            PrimaryConsentForm cf3 = factory.createPrimaryConsentForm();
            String cfDesc3 = "Consent form 3";
            cf3.setQuestion(cfDesc3);
            
            cfg.addConsentForm(cf1);
            cfg.addConsentForm(cf2);
            cfg.addConsentForm(cf3);

            PrimaryConsentForm cf4 = factory.createPrimaryConsentForm();
            String cfDesc4 = "Consent form 4";
            cf4.setQuestion(cfDesc4);
                        
            try{
                cfg.insertConsentForm(cf4, -1);
                Assert.fail("Exception should have been thrown when trying to insert a consent form using an invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                cfg.insertConsentForm(cf4, 4);
                Assert.fail("Exception should have been thrown when trying to insert a consent form using an invalid index (4)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testRemoveConsentForm_Success(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm cf1 = factory.createPrimaryConsentForm();
            String cfDesc1 = "Consent form 1";
            cf1.setQuestion(cfDesc1);
            PrimaryConsentForm cf2 = factory.createPrimaryConsentForm();
            String cfDesc2 = "Consent form 2";
            cf2.setQuestion(cfDesc2);
            PrimaryConsentForm cf3 = factory.createPrimaryConsentForm();
            String cfDesc3 = "Consent form 3";
            cf3.setQuestion(cfDesc3);
            PrimaryConsentForm cf4 = factory.createPrimaryConsentForm();
            String cfDesc4 = "Consent form 4";
            cf4.setQuestion(cfDesc4);
            
            cfg.addConsentForm(cf1);
            cfg.addConsentForm(cf2);
            cfg.addConsentForm(cf3);
            cfg.addConsentForm(cf4);
            
            cfg.removeConsentForm(2);            
            AssertJUnit.assertEquals("Element has the wrong number of consent forms",3,cfg.numConsentForms());
            AssertJUnit.assertEquals("Consent form at index 0 has the wrong description", cfDesc1, cfg.getConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 1 has the wrong description", cfDesc2, cfg.getConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 2 has the wrong description", cfDesc4, cfg.getConsentForm(2).getQuestion());

            cfg.removeConsentForm(0);            
            AssertJUnit.assertEquals("Element has the wrong number of consent forms",2,cfg.numConsentForms());
            AssertJUnit.assertEquals("Consent form at index 0 has the wrong description", cfDesc2, cfg.getConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 1 has the wrong description", cfDesc4, cfg.getConsentForm(1).getQuestion());
    }
    
    @Test()
	public void testRemoveConsentForm_InvalidIndex(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm cf1 = factory.createPrimaryConsentForm();
            String cfDesc1 = "Consent form 1";
            cf1.setQuestion(cfDesc1);
            PrimaryConsentForm cf2 = factory.createPrimaryConsentForm();
            String cfDesc2 = "Consent form 2";
            cf2.setQuestion(cfDesc2);
            PrimaryConsentForm cf3 = factory.createPrimaryConsentForm();
            String cfDesc3 = "Consent form 3";
            cf3.setQuestion(cfDesc3);
            PrimaryConsentForm cf4 = factory.createPrimaryConsentForm();
            String cfDesc4 = "Consent form 4";
            cf4.setQuestion(cfDesc4);
            
            cfg.addConsentForm(cf1);
            cfg.addConsentForm(cf2);
            cfg.addConsentForm(cf3);
            cfg.addConsentForm(cf4);
            
            try{
                cfg.removeConsentForm(-1);
                Assert.fail("Exception should have been thrown when trying to remove a consent form using an invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                cfg.removeConsentForm(4);
                Assert.fail("Exception should have been thrown when trying to remove a consent form using an invalid index (4)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testMoveConsentForm_Success(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm cf1 = factory.createPrimaryConsentForm();
            String cfDesc1 = "Consent form 1";
            cf1.setQuestion(cfDesc1);
            PrimaryConsentForm cf2 = factory.createPrimaryConsentForm();
            String cfDesc2 = "Consent form 2";
            cf2.setQuestion(cfDesc2);
            PrimaryConsentForm cf3 = factory.createPrimaryConsentForm();
            String cfDesc3 = "Consent form 3";
            cf3.setQuestion(cfDesc3);
            PrimaryConsentForm cf4 = factory.createPrimaryConsentForm();
            String cfDesc4 = "Consent form 4";
            cf4.setQuestion(cfDesc4);
            
            cfg.addConsentForm(cf1);
            cfg.addConsentForm(cf2);
            cfg.addConsentForm(cf3);
            cfg.addConsentForm(cf4);
            
            cfg.moveConsentForm(3,0);
            AssertJUnit.assertEquals("Element has the wrong number of consent forms",4,cfg.numConsentForms());
            AssertJUnit.assertEquals("Consent form at index 0 has the wrong description", cfDesc4, cfg.getConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 1 has the wrong description", cfDesc1, cfg.getConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 2 has the wrong description", cfDesc2, cfg.getConsentForm(2).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 3 has the wrong description", cfDesc3, cfg.getConsentForm(3).getQuestion());
            
            cfg.moveConsentForm(1,3);
            AssertJUnit.assertEquals("Element has the wrong number of consent forms",4,cfg.numConsentForms());
            AssertJUnit.assertEquals("Consent form at index 0 has the wrong description", cfDesc4, cfg.getConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 1 has the wrong description", cfDesc2, cfg.getConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 2 has the wrong description", cfDesc3, cfg.getConsentForm(2).getQuestion());
            AssertJUnit.assertEquals("Consent form at index 3 has the wrong description", cfDesc1, cfg.getConsentForm(3).getQuestion());
    }
    
    @Test()
	public void testMoveConsentForm_InvalidIndex(){
            ConsentFormGroup cfg = factory.createConsentFormGroup();
            PrimaryConsentForm cf1 = factory.createPrimaryConsentForm();
            String cfDesc1 = "Consent form 1";
            cf1.setQuestion(cfDesc1);
            PrimaryConsentForm cf2 = factory.createPrimaryConsentForm();
            String cfDesc2 = "Consent form 2";
            cf2.setQuestion(cfDesc2);
            PrimaryConsentForm cf3 = factory.createPrimaryConsentForm();
            String cfDesc3 = "Consent form 3";
            cf3.setQuestion(cfDesc3);
            PrimaryConsentForm cf4 = factory.createPrimaryConsentForm();
            String cfDesc4 = "Consent form 4";
            cf4.setQuestion(cfDesc4);
            
            cfg.addConsentForm(cf1);
            cfg.addConsentForm(cf2);
            cfg.addConsentForm(cf3);
            cfg.addConsentForm(cf4);
            
            try{
                cfg.moveConsentForm(-1,2);
                Assert.fail("Exception should have been thrown when trying to move a consent form using an invalid current index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Element has the wrong number of consent forms",4,cfg.numConsentForms());
                AssertJUnit.assertEquals("Consent form at index 0 has the wrong description", cfDesc1, cfg.getConsentForm(0).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 1 has the wrong description", cfDesc2, cfg.getConsentForm(1).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 2 has the wrong description", cfDesc3, cfg.getConsentForm(2).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 3 has the wrong description", cfDesc4, cfg.getConsentForm(3).getQuestion());
            }
            
            try{
                cfg.moveConsentForm(4,2);
                Assert.fail("Exception should have been thrown when trying to move a consent form using an invalid current index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Element has the wrong number of consent forms",4,cfg.numConsentForms());
                AssertJUnit.assertEquals("Consent form at index 0 has the wrong description", cfDesc1, cfg.getConsentForm(0).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 1 has the wrong description", cfDesc2, cfg.getConsentForm(1).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 2 has the wrong description", cfDesc3, cfg.getConsentForm(2).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 3 has the wrong description", cfDesc4, cfg.getConsentForm(3).getQuestion());
            }

            try{
                cfg.moveConsentForm(1,-1);
                Assert.fail("Exception should have been thrown when trying to move a consent form using an invalid new index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Element has the wrong number of consent forms",4,cfg.numConsentForms());
                AssertJUnit.assertEquals("Consent form at index 0 has the wrong description", cfDesc1, cfg.getConsentForm(0).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 1 has the wrong description", cfDesc2, cfg.getConsentForm(1).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 2 has the wrong description", cfDesc3, cfg.getConsentForm(2).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 3 has the wrong description", cfDesc4, cfg.getConsentForm(3).getQuestion());
            }
            
            try{
                cfg.moveConsentForm(1,4);
                Assert.fail("Exception should have been thrown when trying to move a consent form using an invalid new index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Element has the wrong number of consent forms",4,cfg.numConsentForms());
                AssertJUnit.assertEquals("Consent form at index 0 has the wrong description", cfDesc1, cfg.getConsentForm(0).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 1 has the wrong description", cfDesc2, cfg.getConsentForm(1).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 2 has the wrong description", cfDesc3, cfg.getConsentForm(2).getQuestion());
                AssertJUnit.assertEquals("Consent form at index 3 has the wrong description", cfDesc4, cfg.getConsentForm(3).getQuestion());
            }
    }
    
}
