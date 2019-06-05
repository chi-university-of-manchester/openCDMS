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


public class IBasicEntryTest extends ModelTest {

    @Test()
	public void testNumUnits(){
            BasicEntry te = factory.createTextEntry("te1");
            Unit u1 = factory.createUnit("m");
            Unit u2 = factory.createUnit("mm");
            Unit u3 = factory.createUnit("bpm");
            Unit u4 = factory.createUnit("mph");
            te.addUnit(u1);
            te.addUnit(u2);
            te.addUnit(u3);
            te.addUnit(u4);
            
            AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
    }
    
    @Test()
	public void testAddUnit(){
            BasicEntry te = factory.createTextEntry("te1");
            String abbrev1 = "m";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "mm";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "bpm";
            Unit u3 = factory.createUnit(abbrev3);
            String abbrev4 = "T";
            Unit u4 = factory.createUnit(abbrev4);
            te.addUnit(u1);
            te.addUnit(u2);
            te.addUnit(u3);
            te.addUnit(u4);
            
            AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev1,te.getUnit(0).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev2,te.getUnit(1).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 2 has the wrong abbreviation",abbrev3,te.getUnit(2).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 3 has the wrong abbreviation",abbrev4,te.getUnit(3).getAbbreviation());
    }
    
    @Test()
	public void testAddUnit_Null(){
            BasicEntry te = factory.createTextEntry("te1");

            try{
                te.addUnit(null);
                Assert.fail("Exception should have been thrown when trying to add a null unit");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of units",0,te.numUnits());
            }
    }

    @Test()
	public void testGetUnit_Success(){
            BasicEntry te = factory.createTextEntry("te1");
            String abbrev1 = "m";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "mm";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "bpm";
            Unit u3 = factory.createUnit(abbrev3);
            String abbrev4 = "T";
            Unit u4 = factory.createUnit(abbrev4);
            te.addUnit(u1);
            te.addUnit(u2);
            te.addUnit(u3);
            te.addUnit(u4);
            
            AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev1,te.getUnit(0).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev2,te.getUnit(1).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 2 has the wrong abbreviation",abbrev3,te.getUnit(2).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 3 has the wrong abbreviation",abbrev4,te.getUnit(3).getAbbreviation());
    }
    
    @Test()
	public void testGetUnit_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
            String abbrev1 = "m";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "mm";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "bpm";
            Unit u3 = factory.createUnit(abbrev3);
            String abbrev4 = "T";
            Unit u4 = factory.createUnit(abbrev4);
            te.addUnit(u1);
            te.addUnit(u2);
            te.addUnit(u3);
            te.addUnit(u4);
            
            try{
                te.getUnit(-1);
                Assert.fail("Exception should have been thrown when trying to get a unit using an invalid index (-1");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                te.getUnit(4);
                Assert.fail("Exception should have been thrown when trying to get a unit using an invalid index (4");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testInsertUnit_Success(){
            BasicEntry te = factory.createTextEntry("te1");
            String abbrev1 = "m";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "mm";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "bpm";
            Unit u3 = factory.createUnit(abbrev3);
            String abbrev4 = "T";
            Unit u4 = factory.createUnit(abbrev4);
            te.addUnit(u1);
            te.addUnit(u2);
            te.addUnit(u3);
            te.addUnit(u4);
            
            String abbrev5 = "Pa";
            Unit u5 = factory.createUnit(abbrev5);
            te.insertUnit(u5, 0);
            
            AssertJUnit.assertEquals("Entry has the wrong number of units",5,te.numUnits());
            AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev5,te.getUnit(0).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev1,te.getUnit(1).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 2 has the wrong abbreviation",abbrev2,te.getUnit(2).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 3 has the wrong abbreviation",abbrev3,te.getUnit(3).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 4 has the wrong abbreviation",abbrev4,te.getUnit(4).getAbbreviation());

            String abbrev6 = "Sv";
            Unit u6 = factory.createUnit(abbrev6);
            te.insertUnit(u6, 5);
            
            AssertJUnit.assertEquals("Entry has the wrong number of units",6,te.numUnits());
            AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev5,te.getUnit(0).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev1,te.getUnit(1).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 2 has the wrong abbreviation",abbrev2,te.getUnit(2).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 3 has the wrong abbreviation",abbrev3,te.getUnit(3).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 4 has the wrong abbreviation",abbrev4,te.getUnit(4).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 5 has the wrong abbreviation",abbrev6,te.getUnit(5).getAbbreviation());
    }
    
    @Test()
	public void testInsertUnit_Null(){
            BasicEntry te = factory.createTextEntry("te1");
            String abbrev1 = "m";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "mm";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "bpm";
            Unit u3 = factory.createUnit(abbrev3);
            String abbrev4 = "T";
            Unit u4 = factory.createUnit(abbrev4);
            te.addUnit(u1);
            te.addUnit(u2);
            te.addUnit(u3);
            te.addUnit(u4);
            
            try{
                te.insertUnit(null,1);
                Assert.fail("Exception should have been thrown when trying to insert a null unit");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
            }
    }
    
    @Test()
	public void testInsertUnit_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
            String abbrev1 = "m";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "mm";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "bpm";
            Unit u3 = factory.createUnit(abbrev3);
            String abbrev4 = "T";
            Unit u4 = factory.createUnit(abbrev4);
            te.addUnit(u1);
            te.addUnit(u2);
            te.addUnit(u3);
            te.addUnit(u4);

            String abbrev5 = "Pa";
            Unit u5 = factory.createUnit(abbrev5);
            
            try{
                te.insertUnit(u5, -1);
                Assert.fail("Exception should have been thrown when trying to insert a unit using an invalid index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
            }
            try{
                te.insertUnit(u5, 5);
                Assert.fail("Exception should have been thrown when trying to insert a unit using an invalid index (5)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
            }
    }
    
    @Test()
	public void testRemoveUnit_Success(){
            BasicEntry te = factory.createTextEntry("te1");
            String abbrev1 = "m";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "mm";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "bpm";
            Unit u3 = factory.createUnit(abbrev3);
            String abbrev4 = "T";
            Unit u4 = factory.createUnit(abbrev4);
            te.addUnit(u1);
            te.addUnit(u2);
            te.addUnit(u3);
            te.addUnit(u4);
            
            te.removeUnit(0);
            AssertJUnit.assertEquals("Entry has the wrong number of units",3,te.numUnits());
            AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev2,te.getUnit(0).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev3,te.getUnit(1).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 2 has the wrong abbreviation",abbrev4,te.getUnit(2).getAbbreviation());
            
            te.removeUnit(2);
            AssertJUnit.assertEquals("Entry has the wrong number of units",2,te.numUnits());
            AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev2,te.getUnit(0).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev3,te.getUnit(1).getAbbreviation());
    }
    
    @Test()
	public void testRemoveUnit_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
            String abbrev1 = "m";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "mm";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "bpm";
            Unit u3 = factory.createUnit(abbrev3);
            String abbrev4 = "T";
            Unit u4 = factory.createUnit(abbrev4);
            te.addUnit(u1);
            te.addUnit(u2);
            te.addUnit(u3);
            te.addUnit(u4);
            
            try{
                te.removeUnit(-1);
                Assert.fail("Exception should have been thrown when trying to remove a unit using an invalid index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
            }

            try{
                te.removeUnit(4);
                Assert.fail("Exception should have been thrown when trying to remove a unit using an invalid index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
            }
    }
    
    @Test()
	public void testMoveUnit_Success(){
            BasicEntry te = factory.createTextEntry("te1");
            String abbrev1 = "m";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "mm";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "bpm";
            Unit u3 = factory.createUnit(abbrev3);
            String abbrev4 = "T";
            Unit u4 = factory.createUnit(abbrev4);
            te.addUnit(u1);
            te.addUnit(u2);
            te.addUnit(u3);
            te.addUnit(u4);
            
            te.moveUnit(3,0);
            AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
            AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev4,te.getUnit(0).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev1,te.getUnit(1).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 2 has the wrong abbreviation",abbrev2,te.getUnit(2).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 3 has the wrong abbreviation",abbrev3,te.getUnit(3).getAbbreviation());
            
            te.moveUnit(0,3);
            AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
            AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev1,te.getUnit(0).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev2,te.getUnit(1).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 2 has the wrong abbreviation",abbrev3,te.getUnit(2).getAbbreviation());
            AssertJUnit.assertEquals("Unit at index 3 has the wrong abbreviation",abbrev4,te.getUnit(3).getAbbreviation());
    }
    
    @Test()
	public void testMoveUnit_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
            String abbrev1 = "m";
            Unit u1 = factory.createUnit(abbrev1);
            String abbrev2 = "mm";
            Unit u2 = factory.createUnit(abbrev2);
            String abbrev3 = "bpm";
            Unit u3 = factory.createUnit(abbrev3);
            String abbrev4 = "T";
            Unit u4 = factory.createUnit(abbrev4);
            te.addUnit(u1);
            te.addUnit(u2);
            te.addUnit(u3);
            te.addUnit(u4);
            
            try{
                te.moveUnit(-1, 2);
                Assert.fail("Exception should have been thrown when trying to move a unit using an invalid current index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
                AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev1,te.getUnit(0).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev2,te.getUnit(1).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 2 has the wrong abbreviation",abbrev3,te.getUnit(2).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 3 has the wrong abbreviation",abbrev4,te.getUnit(3).getAbbreviation());
            }

            try{
                te.moveUnit(4, 2);
                Assert.fail("Exception should have been thrown when trying to move a unit using an invalid current index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
                AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev1,te.getUnit(0).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev2,te.getUnit(1).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 2 has the wrong abbreviation",abbrev3,te.getUnit(2).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 3 has the wrong abbreviation",abbrev4,te.getUnit(3).getAbbreviation());
            }

            try{
                te.moveUnit(0, -1);
                Assert.fail("Exception should have been thrown when trying to move a unit using an invalid current index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
                AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev1,te.getUnit(0).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev2,te.getUnit(1).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 2 has the wrong abbreviation",abbrev3,te.getUnit(2).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 3 has the wrong abbreviation",abbrev4,te.getUnit(3).getAbbreviation());
            }

            try{
                te.moveUnit(0, 4);
                Assert.fail("Exception should have been thrown when trying to move a unit using an invalid current index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Entry has the wrong number of units",4,te.numUnits());
                AssertJUnit.assertEquals("Unit at index 0 has the wrong abbreviation",abbrev1,te.getUnit(0).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 1 has the wrong abbreviation",abbrev2,te.getUnit(1).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 2 has the wrong abbreviation",abbrev3,te.getUnit(2).getAbbreviation());
                AssertJUnit.assertEquals("Unit at index 3 has the wrong abbreviation",abbrev4,te.getUnit(3).getAbbreviation());
            }
    }
    
    @Test()
	public void testAddValidationRule(){
            BasicEntry te = factory.createTextEntry("te1");
            String desc1 = "Rule 1";
            ValidationRule rule1 = factory.createNumericValidationRule();
            rule1.setDescription(desc1);
            String desc2 = "Rule 2";
            ValidationRule rule2 = factory.createTextValidationRule();
            rule2.setDescription(desc2);
            String desc3 = "Rule 3";
            ValidationRule rule3 = factory.createDateValidationRule();
            rule3.setDescription(desc3);
            
            te.addValidationRule(rule1);
            te.addValidationRule(rule2);
            te.addValidationRule(rule3);
            
            AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc1,te.getValidationRule(0).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc2,te.getValidationRule(1).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 2 has the wrong description",desc3,te.getValidationRule(2).getDescription());
    }
    
    @Test()
	public void testAddValidationRule_Null(){
            BasicEntry te = factory.createTextEntry("te1");
            try{
                te.addValidationRule(null);
                Assert.fail("Exception should have been thrown when trying to add a null validation rule.");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetValidationRule_Success(){
            BasicEntry te = factory.createTextEntry("te1");
            String desc1 = "Rule 1";
            ValidationRule rule1 = factory.createNumericValidationRule();
            rule1.setDescription(desc1);
            String desc2 = "Rule 2";
            ValidationRule rule2 = factory.createTextValidationRule();
            rule2.setDescription(desc2);
            String desc3 = "Rule 3";
            ValidationRule rule3 = factory.createDateValidationRule();
            rule3.setDescription(desc3);
            
            te.addValidationRule(rule1);
            te.addValidationRule(rule2);
            te.addValidationRule(rule3);
            
            AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc1,te.getValidationRule(0).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc2,te.getValidationRule(1).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 2 has the wrong description",desc3,te.getValidationRule(2).getDescription());
    }
    
    @Test()
	public void testGetValidationRule_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
            String desc1 = "Rule 1";
            ValidationRule rule1 = factory.createNumericValidationRule();
            rule1.setDescription(desc1);
            String desc2 = "Rule 2";
            ValidationRule rule2 = factory.createTextValidationRule();
            rule2.setDescription(desc2);
            String desc3 = "Rule 3";
            ValidationRule rule3 = factory.createDateValidationRule();
            rule3.setDescription(desc3);
            
            te.addValidationRule(rule1);
            te.addValidationRule(rule2);
            te.addValidationRule(rule3);
            
            try{
                te.getValidationRule(-1);
                Assert.fail("Exception should have been thrown when trying to get a validation rule using an invalid index (-1)");
            }
            catch (ModelException ex){
                //do nothing
            }
            try{
                te.getValidationRule(3);
                Assert.fail("Exception should have been thrown when trying to get a validation rule using an invalid index (3)");
            }
            catch (ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testInsertValidationRule_Success(){
            BasicEntry te = factory.createTextEntry("te1");
            String desc1 = "Rule 1";
            ValidationRule rule1 = factory.createNumericValidationRule();
            rule1.setDescription(desc1);
            String desc2 = "Rule 2";
            ValidationRule rule2 = factory.createTextValidationRule();
            rule2.setDescription(desc2);
            String desc3 = "Rule 3";
            ValidationRule rule3 = factory.createDateValidationRule();
            rule3.setDescription(desc3);
            
            te.addValidationRule(rule1);
            te.addValidationRule(rule2);
            te.addValidationRule(rule3);
            
            String desc4 = "Rule 4";
            ValidationRule rule4 = factory.createDateValidationRule();
            rule4.setDescription(desc4);
            
            te.insertValidationRule(rule4, 0);
            AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc4,te.getValidationRule(0).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc1,te.getValidationRule(1).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 2 has the wrong description",desc2,te.getValidationRule(2).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 3 has the wrong description",desc3,te.getValidationRule(3).getDescription());
            
            String desc5 = "Rule 5";
            ValidationRule rule5 = factory.createDateValidationRule();
            rule5.setDescription(desc5);
            
            te.insertValidationRule(rule5, 4);
            AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc4,te.getValidationRule(0).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc1,te.getValidationRule(1).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 2 has the wrong description",desc2,te.getValidationRule(2).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 3 has the wrong description",desc3,te.getValidationRule(3).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 4 has the wrong description",desc5,te.getValidationRule(4).getDescription());
    }
    
    @Test()
	public void testInsertValidationRule_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
            String desc1 = "Rule 1";
            ValidationRule rule1 = factory.createNumericValidationRule();
            rule1.setDescription(desc1);
            String desc2 = "Rule 2";
            ValidationRule rule2 = factory.createTextValidationRule();
            rule2.setDescription(desc2);
            String desc3 = "Rule 3";
            ValidationRule rule3 = factory.createDateValidationRule();
            rule3.setDescription(desc3);
            
            te.addValidationRule(rule1);
            te.addValidationRule(rule2);
            te.addValidationRule(rule3);
            
            String desc4 = "Rule 4";
            ValidationRule rule4 = factory.createDateValidationRule();
            rule4.setDescription(desc4);
            
            try{
                te.insertValidationRule(rule4, -1);
                Assert.fail("Exception should have been thrown when trying to insert a validation rule using an invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                te.insertValidationRule(rule4, 4);
                Assert.fail("Exception should have been thrown when trying to insert a validation rule using an invalid index (4)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testInsertValidationRule_Null(){
            BasicEntry te = factory.createTextEntry("te1");
            String desc1 = "Rule 1";
            ValidationRule rule1 = factory.createNumericValidationRule();
            rule1.setDescription(desc1);
            String desc2 = "Rule 2";
            ValidationRule rule2 = factory.createTextValidationRule();
            rule2.setDescription(desc2);
            String desc3 = "Rule 3";
            ValidationRule rule3 = factory.createDateValidationRule();
            rule3.setDescription(desc3);
            
            te.addValidationRule(rule1);
            te.addValidationRule(rule2);
            te.addValidationRule(rule3);
            
            try{
                te.insertValidationRule(null, 0);
                Assert.fail("Exception should have been thrown when trying to insert a null validation rule");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testMoveValidationRule_Success(){
            BasicEntry te = factory.createTextEntry("te1");
            String desc1 = "Rule 1";
            ValidationRule rule1 = factory.createNumericValidationRule();
            rule1.setDescription(desc1);
            String desc2 = "Rule 2";
            ValidationRule rule2 = factory.createTextValidationRule();
            rule2.setDescription(desc2);
            String desc3 = "Rule 3";
            ValidationRule rule3 = factory.createDateValidationRule();
            rule3.setDescription(desc3);
            String desc4 = "Rule 4";
            ValidationRule rule4 = factory.createDateValidationRule();
            rule4.setDescription(desc4);
            
            te.addValidationRule(rule1);
            te.addValidationRule(rule2);
            te.addValidationRule(rule3);
            te.addValidationRule(rule4);
            
            te.moveValidationRule(3,0);
            AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc4,te.getValidationRule(0).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc1,te.getValidationRule(1).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 2 has the wrong description",desc2,te.getValidationRule(2).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 3 has the wrong description",desc3,te.getValidationRule(3).getDescription());
            
            te.moveValidationRule(0,3);
            AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc1,te.getValidationRule(0).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc2,te.getValidationRule(1).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 2 has the wrong description",desc3,te.getValidationRule(2).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 3 has the wrong description",desc4,te.getValidationRule(3).getDescription());
    }
    
    @Test()
	public void testMoveValidationRule_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
            String desc1 = "Rule 1";
            ValidationRule rule1 = factory.createNumericValidationRule();
            rule1.setDescription(desc1);
            String desc2 = "Rule 2";
            ValidationRule rule2 = factory.createTextValidationRule();
            rule2.setDescription(desc2);
            String desc3 = "Rule 3";
            ValidationRule rule3 = factory.createDateValidationRule();
            rule3.setDescription(desc3);
            String desc4 = "Rule 4";
            ValidationRule rule4 = factory.createDateValidationRule();
            rule4.setDescription(desc4);
            
            te.addValidationRule(rule1);
            te.addValidationRule(rule2);
            te.addValidationRule(rule3);
            te.addValidationRule(rule4);
            
            try{
                te.moveValidationRule(-1,0);
                Assert.fail("Exception should have been thrown when trying to move a validation rule using an invalid current index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc1,te.getValidationRule(0).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc2,te.getValidationRule(1).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 2 has the wrong description",desc3,te.getValidationRule(2).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 3 has the wrong description",desc4,te.getValidationRule(3).getDescription());
            }
            
            try{
                te.moveValidationRule(4,0);
                Assert.fail("Exception should have been thrown when trying to move a validation rule using an invalid current index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc1,te.getValidationRule(0).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc2,te.getValidationRule(1).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 2 has the wrong description",desc3,te.getValidationRule(2).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 3 has the wrong description",desc4,te.getValidationRule(3).getDescription());
            }
            
            try{
                te.moveValidationRule(0,-1);
                Assert.fail("Exception should have been thrown when trying to move a validation rule using an invalid new index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc1,te.getValidationRule(0).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc2,te.getValidationRule(1).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 2 has the wrong description",desc3,te.getValidationRule(2).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 3 has the wrong description",desc4,te.getValidationRule(3).getDescription());
            }

            try{
                te.moveValidationRule(0,4);
                Assert.fail("Exception should have been thrown when trying to move a validation rule using an invalid new index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc1,te.getValidationRule(0).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc2,te.getValidationRule(1).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 2 has the wrong description",desc3,te.getValidationRule(2).getDescription());
                AssertJUnit.assertEquals("Validation rule at index 3 has the wrong description",desc4,te.getValidationRule(3).getDescription());
            }
    }
    
    @Test()
	public void testNumValidationRules(){
            BasicEntry te = factory.createTextEntry("te1");
            String desc1 = "Rule 1";
            ValidationRule rule1 = factory.createNumericValidationRule();
            rule1.setDescription(desc1);
            String desc2 = "Rule 2";
            ValidationRule rule2 = factory.createTextValidationRule();
            rule2.setDescription(desc2);
            String desc3 = "Rule 3";
            ValidationRule rule3 = factory.createDateValidationRule();
            rule3.setDescription(desc3);
            String desc4 = "Rule 4";
            ValidationRule rule4 = factory.createDateValidationRule();
            rule4.setDescription(desc4);

            AssertJUnit.assertEquals("Entry has the wrong number of validation rules (should be 0)",0,te.numValidationRules());            
            
            te.addValidationRule(rule1);
            te.addValidationRule(rule2);
            AssertJUnit.assertEquals("Entry has the wrong number of validation rules (should be 2)",2,te.numValidationRules());
            
            te.addValidationRule(rule3);
            te.addValidationRule(rule4);
            AssertJUnit.assertEquals("Entry has the wrong number of validation rules (should be 4)",4,te.numValidationRules());
    }
    
    @Test()
	public void testRemoveValidationRule_Success(){
            BasicEntry te = factory.createTextEntry("te1");
            String desc1 = "Rule 1";
            ValidationRule rule1 = factory.createNumericValidationRule();
            rule1.setDescription(desc1);
            String desc2 = "Rule 2";
            ValidationRule rule2 = factory.createTextValidationRule();
            rule2.setDescription(desc2);
            String desc3 = "Rule 3";
            ValidationRule rule3 = factory.createDateValidationRule();
            rule3.setDescription(desc3);
            String desc4 = "Rule 4";
            ValidationRule rule4 = factory.createDateValidationRule();
            rule4.setDescription(desc4);
            
            te.addValidationRule(rule1);
            te.addValidationRule(rule2);
            te.addValidationRule(rule3);
            te.addValidationRule(rule4);
            
            te.removeValidationRule(0);
            AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc2,te.getValidationRule(0).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc3,te.getValidationRule(1).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 2 has the wrong description",desc4,te.getValidationRule(2).getDescription());
            
            te.removeValidationRule(2);
            AssertJUnit.assertEquals("Validation rule at index 0 has the wrong description",desc2,te.getValidationRule(0).getDescription());
            AssertJUnit.assertEquals("Validation rule at index 1 has the wrong description",desc3,te.getValidationRule(1).getDescription());            
    }
    
    @Test()
	public void testRemoveValidationRule_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
            String desc1 = "Rule 1";
            ValidationRule rule1 = factory.createNumericValidationRule();
            rule1.setDescription(desc1);
            String desc2 = "Rule 2";
            ValidationRule rule2 = factory.createTextValidationRule();
            rule2.setDescription(desc2);
            String desc3 = "Rule 3";
            ValidationRule rule3 = factory.createDateValidationRule();
            rule3.setDescription(desc3);
            String desc4 = "Rule 4";
            ValidationRule rule4 = factory.createDateValidationRule();
            rule4.setDescription(desc4);
            
            te.addValidationRule(rule1);
            te.addValidationRule(rule2);
            te.addValidationRule(rule3);
            te.addValidationRule(rule4);
            
            try{
                te.removeValidationRule(-1);
                Assert.fail("Exception should have been thrown when trying to remove a validation rule using an invalid index (-1)");
            }
            catch (ModelException ex){
                //do nothing
            }
            try{
                te.removeValidationRule(4);
                Assert.fail("Exception should have been thrown when trying to remove a validation rule using an invalid index (4)");
            }
            catch (ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testAddTransformer(){
            BasicEntry te = factory.createTextEntry("te1");
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
            String op3 = "op13";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            
            te.addTransformer(t1);
            te.addTransformer(t2);
            te.addTransformer(t3);
            
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url1,te.getTransformer(0).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong namespace",ns1,te.getTransformer(0).getWsNamespace());
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong operation",op1,te.getTransformer(0).getWsOperation());
            AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url2,te.getTransformer(1).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong namespace",ns2,te.getTransformer(1).getWsNamespace());
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong operation",op2,te.getTransformer(1).getWsOperation());
            AssertJUnit.assertEquals("Transformer at index 2 has the wrong url",url3,te.getTransformer(2).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 2 has the wrong namespace",ns3,te.getTransformer(2).getWsNamespace());
            AssertJUnit.assertEquals("Transformer at index 2 has the wrong operation",op3,te.getTransformer(2).getWsOperation());
    }
    
    @Test()
	public void testAddTransformer_Null(){
            BasicEntry te = factory.createTextEntry("te1");
            try{
                te.addTransformer(null);
                Assert.fail("Exception should have been thrown when trying to add a null transformer.");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testGetTransformer_Success(){
            BasicEntry te = factory.createTextEntry("te1");
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
            String op3 = "op13";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            
            te.addTransformer(t1);
            te.addTransformer(t2);
            te.addTransformer(t3);
            
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url1,te.getTransformer(0).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong namespace",ns1,te.getTransformer(0).getWsNamespace());
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong operation",op1,te.getTransformer(0).getWsOperation());
            AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url2,te.getTransformer(1).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong namespace",ns2,te.getTransformer(1).getWsNamespace());
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong operation",op2,te.getTransformer(1).getWsOperation());
            AssertJUnit.assertEquals("Transformer at index 2 has the wrong url",url3,te.getTransformer(2).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 2 has the wrong namespace",ns3,te.getTransformer(2).getWsNamespace());
            AssertJUnit.assertEquals("Transformer at index 2 has the wrong operation",op3,te.getTransformer(2).getWsOperation());
    }
    
    @Test()
	public void testGetTransformer_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
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
            String op3 = "op13";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            
            te.addTransformer(t1);
            te.addTransformer(t2);
            te.addTransformer(t3);
            
            try{
                te.getTransformer(-1);
                Assert.fail("Exception should have been thrown when trying to get a transformer using an invalid index (-1)");
            }
            catch (ModelException ex){
                //do nothing
            }
            try{
                te.getTransformer(3);
                Assert.fail("Exception should have been thrown when trying to get a transformer using an invalid index (3)");
            }
            catch (ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testInsertTransformer_Success(){
            BasicEntry te = factory.createTextEntry("te1");
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
            String op3 = "op13";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            
            te.addTransformer(t1);
            te.addTransformer(t2);
            te.addTransformer(t3);
           
            String url4 = "url4";
            String ns4 = "ns4";
            String op4 = "op4";
            Transformer t4 = factory.createTransformer(url4, ns4, op4, "org.psygrid.data.model.hibernate.TextValue");
            
            te.insertTransformer(t4, 0);
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url4,te.getTransformer(0).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url1,te.getTransformer(1).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 2 has the wrong url",url2,te.getTransformer(2).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 3 has the wrong url",url3,te.getTransformer(3).getWsUrl());
            
            String url5 = "url5";
            String ns5 = "ns5";
            String op5 = "op5";
            Transformer t5 = factory.createTransformer(url5, ns5, op5, "org.psygrid.data.model.hibernate.TextValue");
            
            te.insertTransformer(t5, 4);
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url4,te.getTransformer(0).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url1,te.getTransformer(1).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 2 has the wrong url",url2,te.getTransformer(2).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 3 has the wrong url",url3,te.getTransformer(3).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 4 has the wrong url",url5,te.getTransformer(4).getWsUrl());
    }
    
    @Test()
	public void testInsertTransformer_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
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
            String op3 = "op13";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            
            te.addTransformer(t1);
            te.addTransformer(t2);
            te.addTransformer(t3);
            
            String url4 = "url4";
            String ns4 = "ns4";
            String op4 = "op4";
            Transformer t4 = factory.createTransformer(url4, ns4, op4, "org.psygrid.data.model.hibernate.TextValue");
            
            try{
                te.insertTransformer(t4, -1);
                Assert.fail("Exception should have been thrown when trying to insert a transformer using an invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                te.insertTransformer(t4, 4);
                Assert.fail("Exception should have been thrown when trying to insert a transformer using an invalid index (4)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testInsertTransformer_Null(){
            BasicEntry te = factory.createTextEntry("te1");
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
            String op3 = "op13";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            
            te.addTransformer(t1);
            te.addTransformer(t2);
            te.addTransformer(t3);
            
            try{
                te.insertTransformer(null, 0);
                Assert.fail("Exception should have been thrown when trying to insert a null transformer");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testMoveTransformer_Success(){
            BasicEntry te = factory.createTextEntry("te1");
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
            String op3 = "op13";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            String url4 = "url4";
            String ns4 = "ns4";
            String op4 = "op4";
            Transformer t4 = factory.createTransformer(url4, ns4, op4, "org.psygrid.data.model.hibernate.TextValue");
            
            te.addTransformer(t1);
            te.addTransformer(t2);
            te.addTransformer(t3);
            te.addTransformer(t4);
            
            te.moveTransformer(3,0);
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url4,te.getTransformer(0).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url1,te.getTransformer(1).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 2 has the wrong url",url2,te.getTransformer(2).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 3 has the wrong url",url3,te.getTransformer(3).getWsUrl());
            
            te.moveTransformer(0,3);
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url1,te.getTransformer(0).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url2,te.getTransformer(1).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 2 has the wrong url",url3,te.getTransformer(2).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 3 has the wrong url",url4,te.getTransformer(3).getWsUrl());
    }
    
    @Test()
	public void testMoveTransformer_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
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
            String op3 = "op13";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            String url4 = "url4";
            String ns4 = "ns4";
            String op4 = "op4";
            Transformer t4 = factory.createTransformer(url4, ns4, op4, "org.psygrid.data.model.hibernate.TextValue");
            
            te.addTransformer(t1);
            te.addTransformer(t2);
            te.addTransformer(t3);
            te.addTransformer(t4);
            
            try{
                te.moveTransformer(-1,0);
                Assert.fail("Exception should have been thrown when trying to move a transformer using an invalid current index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url1,te.getTransformer(0).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url2,te.getTransformer(1).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 2 has the wrong url",url3,te.getTransformer(2).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 3 has the wrong url",url4,te.getTransformer(3).getWsUrl());
            }
            
            try{
                te.moveTransformer(4,0);
                Assert.fail("Exception should have been thrown when trying to move a transformer using an invalid current index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url1,te.getTransformer(0).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url2,te.getTransformer(1).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 2 has the wrong url",url3,te.getTransformer(2).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 3 has the wrong url",url4,te.getTransformer(3).getWsUrl());
            }
            
            try{
                te.moveTransformer(0,-1);
                Assert.fail("Exception should have been thrown when trying to move a transformer using an invalid new index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url1,te.getTransformer(0).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url2,te.getTransformer(1).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 2 has the wrong url",url3,te.getTransformer(2).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 3 has the wrong url",url4,te.getTransformer(3).getWsUrl());
            }

            try{
                te.moveTransformer(0,4);
                Assert.fail("Exception should have been thrown when trying to move a transformer using an invalid new index (4)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url1,te.getTransformer(0).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url2,te.getTransformer(1).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 2 has the wrong url",url3,te.getTransformer(2).getWsUrl());
                AssertJUnit.assertEquals("Transformer at index 3 has the wrong url",url4,te.getTransformer(3).getWsUrl());
            }
    }
    
    @Test()
	public void testNumTransformers(){
            BasicEntry te = factory.createTextEntry("te1");
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
            String op3 = "op13";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            String url4 = "url4";
            String ns4 = "ns4";
            String op4 = "op4";
            Transformer t4 = factory.createTransformer(url4, ns4, op4, "org.psygrid.data.model.hibernate.TextValue");
            
            AssertJUnit.assertEquals("Entry has the wrong number of transformers (should be 0)",0,te.numTransformers());            
            
            te.addTransformer(t1);
            te.addTransformer(t2);
            AssertJUnit.assertEquals("Entry has the wrong number of transformers (should be 2)",2,te.numTransformers());
            
            te.addTransformer(t3);
            te.addTransformer(t4);
            AssertJUnit.assertEquals("Entry has the wrong number of transformers (should be 4)",4,te.numTransformers());
    }
    
    @Test()
	public void testRemoveTransformer_Success(){
            BasicEntry te = factory.createTextEntry("te1");
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
            String op3 = "op13";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            String url4 = "url4";
            String ns4 = "ns4";
            String op4 = "op4";
            Transformer t4 = factory.createTransformer(url4, ns4, op4, "org.psygrid.data.model.hibernate.TextValue");
            
            te.addTransformer(t1);
            te.addTransformer(t2);
            te.addTransformer(t3);
            te.addTransformer(t4);
            
            te.removeTransformer(0);
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url2,te.getTransformer(0).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url3,te.getTransformer(1).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 2 has the wrong url",url4,te.getTransformer(2).getWsUrl());
            
            te.removeTransformer(2);
            AssertJUnit.assertEquals("Transformer at index 0 has the wrong url",url2,te.getTransformer(0).getWsUrl());
            AssertJUnit.assertEquals("Transformer at index 1 has the wrong url",url3,te.getTransformer(1).getWsUrl());
    }
    
    @Test()
	public void testRemoveTransformer_InvalidIndex(){
            BasicEntry te = factory.createTextEntry("te1");
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
            String op3 = "op13";
            Transformer t3 = factory.createTransformer(url3, ns3, op3, "org.psygrid.data.model.hibernate.TextValue");
            String url4 = "url4";
            String ns4 = "ns4";
            String op4 = "op4";
            Transformer t4 = factory.createTransformer(url4, ns4, op4, "org.psygrid.data.model.hibernate.TextValue");
            
            te.addTransformer(t1);
            te.addTransformer(t2);
            te.addTransformer(t3);
            te.addTransformer(t4);
            
            try{
                te.removeTransformer(-1);
                Assert.fail("Exception should have been thrown when trying to remove a transformer using an invalid index (-1)");
            }
            catch (ModelException ex){
                //do nothing
            }
            try{
                te.removeTransformer(4);
                Assert.fail("Exception should have been thrown when trying to remove a transformer using an invalid index (4)");
            }
            catch (ModelException ex){
                //do nothing
            }
    }

    @Test()
	public void testGenerateInstance_SecOcc(){
            Document d = factory.createDocument("D");
            Section s = factory.createSection("S");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            s.addOccurrence(so);
            d.addSection(s);
            BasicEntry be = factory.createTextEntry("BE");
            d.addEntry(be);
            be.setSection(s);
            
            BasicResponse br = be.generateInstance(so);
            
            AssertJUnit.assertEquals("Basic response has the wrong entry", be, br.getEntry());
            AssertJUnit.assertEquals("Basic response has the wrong section occurrence",so,br.getSectionOccurrence());
            AssertJUnit.assertNull("Basic response has non-null sec occ inst",br.getSecOccInstance());
            
            SectionOccurrence so2 = factory.createSectionOccurrence("SO2");
            try{
                be.generateInstance(so2);
                Assert.fail("Exception should have been thrown when trying to generate instance using invalid sec occ");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            so.setMultipleAllowed(true);
            try{
                be.generateInstance(so);
                Assert.fail("Exception should have been thrown when trying to generate instance for sec occ that allows multiple runtime instances");
            }
            catch(ModelException ex){
                //do nothing
            }
    }

    @Test()
	public void testGenerateInstance_SecOccInst(){
            Document d = factory.createDocument("D");
            DocumentOccurrence do1 = factory.createDocumentOccurrence("DO1");
            d.addOccurrence(do1);
            Section s = factory.createSection("S");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            so.setMultipleAllowed(true);
            s.addOccurrence(so);
            d.addSection(s);
            BasicEntry be = factory.createTextEntry("BE");
            d.addEntry(be);
            be.setSection(s);
            
            DocumentInstance di = d.generateInstance(do1);
            SecOccInstance soi = so.generateInstance();
            di.addSecOccInstance(soi);
            BasicResponse br = be.generateInstance(soi);
            
            AssertJUnit.assertEquals("Basic response has the wrong entry", be, br.getEntry());
            AssertJUnit.assertEquals("Basic response has the wrong section occurrence instance",soi,br.getSecOccInstance());
            AssertJUnit.assertNull("Basic response has non-null sec occ",br.getSectionOccurrence());

            SectionOccurrence so2 = factory.createSectionOccurrence("SO2");
            so2.setMultipleAllowed(true);
            SecOccInstance soi2 = so2.generateInstance();
            try{
                be.generateInstance(soi2);
                Assert.fail("Exception should have been thrown when trying to generate instance using invalid sec occ inst");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            so.setMultipleAllowed(false);
            try{
                be.generateInstance(soi);
                Assert.fail("Exception should have been thrown when trying to generate instance for sec occ that does not allow multiple runtime instances");
            }
            catch(ModelException ex){
                //do nothing
            }
    }

}
