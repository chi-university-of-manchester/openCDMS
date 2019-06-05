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

import org.psygrid.data.model.hibernate.AssociatedConsentForm;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.PrimaryConsentForm;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;


public class IConsentFormTest extends ModelTest {

    @Test()
	public void testNumAssociatedConsentForms(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",0,cf.numAssociatedConsentForms());
            cf.addAssociatedConsentForm(factory.createAssociatedConsentForm());
            AssertJUnit.assertEquals("Folder has the wrong number of consent forms",1,cf.numAssociatedConsentForms());
            cf.addAssociatedConsentForm(factory.createAssociatedConsentForm());
            AssertJUnit.assertEquals("Folder has the wrong number of consent forms",2,cf.numAssociatedConsentForms());
            cf.addAssociatedConsentForm(factory.createAssociatedConsentForm());
            AssertJUnit.assertEquals("Folder has the wrong number of consent forms",3,cf.numAssociatedConsentForms());
    }
    
    @Test()
	public void testAddAssociatedConsentForm(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            String cfDesc1 = "Associated consent form 1";
            acf1.setQuestion(cfDesc1);
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            String cfDesc2 = "Associated consent form 2";
            acf2.setQuestion(cfDesc2);
            AssociatedConsentForm acf3 = factory.createAssociatedConsentForm();
            String cfDesc3 = "Associated consent form 3";
            acf3.setQuestion(cfDesc3);
            
            cf.addAssociatedConsentForm(acf1);
            cf.addAssociatedConsentForm(acf2);
            cf.addAssociatedConsentForm(acf3);
            
            AssertJUnit.assertEquals("Associated consent form at index 0 has the wrong description", cfDesc1, cf.getAssociatedConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 1 has the wrong description", cfDesc2, cf.getAssociatedConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 2 has the wrong description", cfDesc3, cf.getAssociatedConsentForm(2).getQuestion());
    }
    
    @Test()
	public void testAddAssociatedConsentForm_Null(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            
            try{
                cf.addAssociatedConsentForm(null);
                Assert.fail("Exception should have been thrown when trying to add a null associated consent form");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Consent Form contains the wrong number of associated consent forms",0,cf.numAssociatedConsentForms());
            }
    }
    
    @Test()
	public void testGetAssociatedConsentForm_Success(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            String cfDesc1 = "Associated consent form 1";
            acf1.setQuestion(cfDesc1);
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            String cfDesc2 = "Associated consent form 2";
            acf2.setQuestion(cfDesc2);
            AssociatedConsentForm acf3 = factory.createAssociatedConsentForm();
            String cfDesc3 = "Associated consent form 3";
            acf3.setQuestion(cfDesc3);
            
            cf.addAssociatedConsentForm(acf1);
            cf.addAssociatedConsentForm(acf2);
            cf.addAssociatedConsentForm(acf3);

            AssertJUnit.assertEquals("Associated consent form at index 2 has the wrong description", cfDesc3, cf.getAssociatedConsentForm(2).getQuestion());
    }
    
    @Test()
	public void testGetAssociatedConsentForm_InvalidIndex(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            String cfDesc1 = "Associated consent form 1";
            acf1.setQuestion(cfDesc1);
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            String cfDesc2 = "Associated consent form 2";
            acf2.setQuestion(cfDesc2);
            AssociatedConsentForm acf3 = factory.createAssociatedConsentForm();
            String cfDesc3 = "Associated consent form 3";
            acf3.setQuestion(cfDesc3);
            
            cf.addAssociatedConsentForm(acf1);
            cf.addAssociatedConsentForm(acf2);
            cf.addAssociatedConsentForm(acf3);

            try{
                cf.getAssociatedConsentForm(-1);
                Assert.fail("Exception should have been thrown when trying to get an associated consent form using an invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                cf.getAssociatedConsentForm(3);
                Assert.fail("Exception should have been thrown when trying to get an associated consent form using an invalid index (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testInsertAssociatedConsentForm_Success(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            String cfDesc1 = "Associated consent form 1";
            acf1.setQuestion(cfDesc1);
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            String cfDesc2 = "Associated consent form 2";
            acf2.setQuestion(cfDesc2);
            AssociatedConsentForm acf3 = factory.createAssociatedConsentForm();
            String cfDesc3 = "Associated consent form 3";
            acf3.setQuestion(cfDesc3);
            
            cf.addAssociatedConsentForm(acf1);
            cf.addAssociatedConsentForm(acf2);
            cf.addAssociatedConsentForm(acf3);
            
            AssociatedConsentForm acf4 = factory.createAssociatedConsentForm();
            String cfDesc4 = "Associated consent form 4";
            acf4.setQuestion(cfDesc4);
            
            cf.insertAssociatedConsentForm(acf4, 0);
            
            AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",4,cf.numAssociatedConsentForms());
            AssertJUnit.assertEquals("Associated consent form at index 0 has the wrong description", cfDesc4, cf.getAssociatedConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 1 has the wrong description", cfDesc1, cf.getAssociatedConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 2 has the wrong description", cfDesc2, cf.getAssociatedConsentForm(2).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 3 has the wrong description", cfDesc3, cf.getAssociatedConsentForm(3).getQuestion());

            AssociatedConsentForm acf5 = factory.createAssociatedConsentForm();
            String cfDesc5 = "Associated consent form 5";
            acf5.setQuestion(cfDesc5);
            
            cf.insertAssociatedConsentForm(acf5, 4);
            
            AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",5,cf.numAssociatedConsentForms());
            AssertJUnit.assertEquals("Associated consent form at index 0 has the wrong description", cfDesc4, cf.getAssociatedConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 1 has the wrong description", cfDesc1, cf.getAssociatedConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 2 has the wrong description", cfDesc2, cf.getAssociatedConsentForm(2).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 3 has the wrong description", cfDesc3, cf.getAssociatedConsentForm(3).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 4 has the wrong description", cfDesc5, cf.getAssociatedConsentForm(4).getQuestion());
    }
    
    @Test()
	public void testInsertAssociatedConsentForm_InvalidIndex(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            String cfDesc1 = "Associated consent form 1";
            acf1.setQuestion(cfDesc1);
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            String cfDesc2 = "Associated consent form 2";
            acf2.setQuestion(cfDesc2);
            AssociatedConsentForm acf3 = factory.createAssociatedConsentForm();
            String cfDesc3 = "Associated consent form 3";
            acf3.setQuestion(cfDesc3);
            
            cf.addAssociatedConsentForm(acf1);
            cf.addAssociatedConsentForm(acf2);
            cf.addAssociatedConsentForm(acf3);

            AssociatedConsentForm acf4 = factory.createAssociatedConsentForm();
            String cfDesc4 = "Associated consent form 4";
            acf4.setQuestion(cfDesc4);
                        
            try{
                cf.insertAssociatedConsentForm(acf4, -1);
                Assert.fail("Exception should have been thrown when trying to insert an associated consent form using an invalid index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",3,cf.numAssociatedConsentForms());
            }
            try{
                cf.insertAssociatedConsentForm(acf4, 4);
                Assert.fail("Exception should have been thrown when trying to insert an associated consent form using an invalid index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",3,cf.numAssociatedConsentForms());
            }
    }
    
    @Test()
	public void testInsertAssociatedConsentForm_Null(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            String cfDesc1 = "Associated consent form 1";
            acf1.setQuestion(cfDesc1);
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            String cfDesc2 = "Associated consent form 2";
            acf2.setQuestion(cfDesc2);
            AssociatedConsentForm acf3 = factory.createAssociatedConsentForm();
            String cfDesc3 = "Associated consent form 3";
            acf3.setQuestion(cfDesc3);
            
            cf.addAssociatedConsentForm(acf1);
            cf.addAssociatedConsentForm(acf2);
            cf.addAssociatedConsentForm(acf3);

            try{
                cf.insertAssociatedConsentForm(null, 1);
                Assert.fail("Exception should have been thrown when trying to insert a null associated consent form");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",3,cf.numAssociatedConsentForms());
            }
    }

    @Test()
	public void testRemoveAssociatedConsentForm_Success(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            String cfDesc1 = "Associated consent form 1";
            acf1.setQuestion(cfDesc1);
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            String cfDesc2 = "Associated consent form 2";
            acf2.setQuestion(cfDesc2);
            AssociatedConsentForm acf3 = factory.createAssociatedConsentForm();
            String cfDesc3 = "Associated consent form 3";
            acf3.setQuestion(cfDesc3);
            AssociatedConsentForm acf4 = factory.createAssociatedConsentForm();
            String cfDesc4 = "Associated consent form 4";
            acf4.setQuestion(cfDesc4);
            
            cf.addAssociatedConsentForm(acf1);
            cf.addAssociatedConsentForm(acf2);
            cf.addAssociatedConsentForm(acf3);
            cf.addAssociatedConsentForm(acf4);
            
            cf.removeAssociatedConsentForm(2);            
            AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",3,cf.numAssociatedConsentForms());
            AssertJUnit.assertEquals("Associated consent form at index 0 has the wrong description", cfDesc1, cf.getAssociatedConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 1 has the wrong description", cfDesc2, cf.getAssociatedConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 2 has the wrong description", cfDesc4, cf.getAssociatedConsentForm(2).getQuestion());

            cf.removeAssociatedConsentForm(0);            
            AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",2,cf.numAssociatedConsentForms());
            AssertJUnit.assertEquals("Associated consent form at index 0 has the wrong description", cfDesc2, cf.getAssociatedConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 1 has the wrong description", cfDesc4, cf.getAssociatedConsentForm(1).getQuestion());
    }
    
    @Test()
	public void testRemoveAssociatedConsentForm_InvalidIndex(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            String cfDesc1 = "Associated consent form 1";
            acf1.setQuestion(cfDesc1);
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            String cfDesc2 = "Associated consent form 2";
            acf2.setQuestion(cfDesc2);
            AssociatedConsentForm acf3 = factory.createAssociatedConsentForm();
            String cfDesc3 = "Associated consent form 3";
            acf3.setQuestion(cfDesc3);
            AssociatedConsentForm acf4 = factory.createAssociatedConsentForm();
            String cfDesc4 = "Associated consent form 4";
            acf4.setQuestion(cfDesc4);
            
            cf.addAssociatedConsentForm(acf1);
            cf.addAssociatedConsentForm(acf2);
            cf.addAssociatedConsentForm(acf3);
            cf.addAssociatedConsentForm(acf4);
            
            try{
                cf.removeAssociatedConsentForm(-1);
                Assert.fail("Exception should have been thrown when trying to remove an associated consent form using an invalid index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",4,cf.numAssociatedConsentForms());
            }
            
            try{
                cf.removeAssociatedConsentForm(4);
                Assert.fail("Exception should have been thrown when trying to remove an associated consent form using an invalid index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",4,cf.numAssociatedConsentForms());
            }
    }
    
    @Test()
	public void testMoveAssociatedConsentForm_Success(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            String cfDesc1 = "Associated consent form 1";
            acf1.setQuestion(cfDesc1);
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            String cfDesc2 = "Associated consent form 2";
            acf2.setQuestion(cfDesc2);
            AssociatedConsentForm acf3 = factory.createAssociatedConsentForm();
            String cfDesc3 = "Associated consent form 3";
            acf3.setQuestion(cfDesc3);
            AssociatedConsentForm acf4 = factory.createAssociatedConsentForm();
            String cfDesc4 = "Associated consent form 4";
            acf4.setQuestion(cfDesc4);
            
            cf.addAssociatedConsentForm(acf1);
            cf.addAssociatedConsentForm(acf2);
            cf.addAssociatedConsentForm(acf3);
            cf.addAssociatedConsentForm(acf4);
            
            cf.moveAssociatedConsentForm(3,0);
            AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",4,cf.numAssociatedConsentForms());
            AssertJUnit.assertEquals("Associated consent form at index 0 has the wrong description", cfDesc4, cf.getAssociatedConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 1 has the wrong description", cfDesc1, cf.getAssociatedConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 2 has the wrong description", cfDesc2, cf.getAssociatedConsentForm(2).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 3 has the wrong description", cfDesc3, cf.getAssociatedConsentForm(3).getQuestion());
            
            cf.moveAssociatedConsentForm(1,3);
            AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",4,cf.numAssociatedConsentForms());
            AssertJUnit.assertEquals("Associated consent form at index 0 has the wrong description", cfDesc4, cf.getAssociatedConsentForm(0).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 1 has the wrong description", cfDesc2, cf.getAssociatedConsentForm(1).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 2 has the wrong description", cfDesc3, cf.getAssociatedConsentForm(2).getQuestion());
            AssertJUnit.assertEquals("Associated consent form at index 3 has the wrong description", cfDesc1, cf.getAssociatedConsentForm(3).getQuestion());
    }
    
    @Test()
	public void testMoveAssociatedConsentForm_InvalidIndex(){
            PrimaryConsentForm cf = factory.createPrimaryConsentForm();
            AssociatedConsentForm acf1 = factory.createAssociatedConsentForm();
            String cfDesc1 = "Associated consent form 1";
            acf1.setQuestion(cfDesc1);
            AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
            String cfDesc2 = "Associated consent form 2";
            acf2.setQuestion(cfDesc2);
            AssociatedConsentForm acf3 = factory.createAssociatedConsentForm();
            String cfDesc3 = "Associated consent form 3";
            acf3.setQuestion(cfDesc3);
            AssociatedConsentForm acf4 = factory.createAssociatedConsentForm();
            String cfDesc4 = "Associated consent form 4";
            acf4.setQuestion(cfDesc4);
            
            cf.addAssociatedConsentForm(acf1);
            cf.addAssociatedConsentForm(acf2);
            cf.addAssociatedConsentForm(acf3);
            cf.addAssociatedConsentForm(acf4);
            
            try{
                cf.moveAssociatedConsentForm(-1,2);
                Assert.fail("Exception should have been thrown when trying to move a consent form using an invalid current index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",4,cf.numAssociatedConsentForms());
                AssertJUnit.assertEquals("Associated consent form at index 0 has the wrong description", cfDesc1, cf.getAssociatedConsentForm(0).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 1 has the wrong description", cfDesc2, cf.getAssociatedConsentForm(1).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 2 has the wrong description", cfDesc3, cf.getAssociatedConsentForm(2).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 3 has the wrong description", cfDesc4, cf.getAssociatedConsentForm(3).getQuestion());
            }
            
            try{
                cf.moveAssociatedConsentForm(4,2);
                Assert.fail("Exception should have been thrown when trying to move a consent form using an invalid current index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",4,cf.numAssociatedConsentForms());
                AssertJUnit.assertEquals("Associated consent form at index 0 has the wrong description", cfDesc1, cf.getAssociatedConsentForm(0).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 1 has the wrong description", cfDesc2, cf.getAssociatedConsentForm(1).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 2 has the wrong description", cfDesc3, cf.getAssociatedConsentForm(2).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 3 has the wrong description", cfDesc4, cf.getAssociatedConsentForm(3).getQuestion());
            }

            try{
                cf.moveAssociatedConsentForm(1,-1);
                Assert.fail("Exception should have been thrown when trying to move a consent form using an invalid new index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",4,cf.numAssociatedConsentForms());
                AssertJUnit.assertEquals("Associated consent form at index 0 has the wrong description", cfDesc1, cf.getAssociatedConsentForm(0).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 1 has the wrong description", cfDesc2, cf.getAssociatedConsentForm(1).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 2 has the wrong description", cfDesc3, cf.getAssociatedConsentForm(2).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 3 has the wrong description", cfDesc4, cf.getAssociatedConsentForm(3).getQuestion());
            }
            
            try{
                cf.moveAssociatedConsentForm(1,4);
                Assert.fail("Exception should have been thrown when trying to move a consent form using an invalid new index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Consent form has the wrong number of associated consent forms",4,cf.numAssociatedConsentForms());
                AssertJUnit.assertEquals("Associated consent form at index 0 has the wrong description", cfDesc1, cf.getAssociatedConsentForm(0).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 1 has the wrong description", cfDesc2, cf.getAssociatedConsentForm(1).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 2 has the wrong description", cfDesc3, cf.getAssociatedConsentForm(2).getQuestion());
                AssertJUnit.assertEquals("Associated consent form at index 3 has the wrong description", cfDesc4, cf.getAssociatedConsentForm(3).getQuestion());
            }
    }    
}
