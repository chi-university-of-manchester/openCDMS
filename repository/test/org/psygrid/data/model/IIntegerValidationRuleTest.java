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

import org.psygrid.data.model.hibernate.IntegerValidationRule;
import org.psygrid.data.model.hibernate.ModelException;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.Date;

public class IIntegerValidationRuleTest extends ModelTest {

    @Test()
	public void testValidate_LowerOnly(){
            IntegerValidationRule rule = factory.createIntegerValidationRule();
            rule.setLowerLimit(new Integer(3));
            AssertJUnit.assertEquals("Validating the value 3 against a lower limit of 3 should result in a pass",0,rule.validateAll(new Integer(3)).size());
            AssertJUnit.assertEquals("Validating the value 4 against a lower limit of 3 should result in a pass",0,rule.validateAll(new Integer(4)).size());
            AssertJUnit.assertTrue("Validating the value 2 against a lower limit of 3 should result in a fail",rule.validateAll(new Integer(2)).size()>0);
    }
    
    @Test()
	public void testValidate_UpperOnly(){
            IntegerValidationRule rule = factory.createIntegerValidationRule();
            rule.setUpperLimit(new Integer(3));
            AssertJUnit.assertEquals("Validating the value 3 against an upper limit of 3 should result in a pass",0,rule.validateAll(new Integer(3)).size());
            AssertJUnit.assertEquals("Validating the value 2 against an upper limit of 3 should result in a pass",0,rule.validateAll(new Integer(2)).size());
            AssertJUnit.assertTrue("Validating the value 4 against an upper limit of 3 should result in a fail",rule.validateAll(new Integer(4)).size()>0);
    }
    
    @Test()
	public void testValidate_LowerUpper(){
            IntegerValidationRule rule = factory.createIntegerValidationRule();
            rule.setLowerLimit(new Integer(3));
            rule.setUpperLimit(new Integer(3));
            AssertJUnit.assertEquals("Validating the value 3 against a lower limit of 3 and an upper limit of 3 should result in a pass",0,rule.validateAll(new Integer(3)).size());
            AssertJUnit.assertTrue("Validating the value 2 against a lower limit of 3 and an upper limit of 3 should result in a fail",rule.validateAll(new Integer(2)).size()>0);
            AssertJUnit.assertTrue("Validating the value 4 against a lower limit of 3 and an upper limit of 3 should result in a fail",rule.validateAll(new Integer(4)).size()>0);
    }
    
    @Test()
	public void testValidate_StringArg(){
            IntegerValidationRule rule = factory.createIntegerValidationRule();
            String val1 = "3";
            AssertJUnit.assertEquals("Validating the string value '"+val1+"' should result in a pass", 0, rule.validateAll(val1).size());
            String val2 = "3.21";
            AssertJUnit.assertTrue("Validating the string value '"+val2+"' should result in a fail", rule.validateAll(val2).size()>0);
            String val3 = "-4";
            AssertJUnit.assertEquals("Validating the string value '"+val3+"' should result in a pass", 0, rule.validateAll(val3).size());
            String val4 = "abc";
            AssertJUnit.assertTrue("Validating the string value '"+val4+"' should result in a fail", rule.validateAll(val4).size()>0);
            String val5 = "2.53l";
            AssertJUnit.assertTrue("Validating the string value '"+val5+"' should result in a fail", rule.validateAll(val5).size()>0);
    }
    
    @Test()
	public void testValidate_DoubleArg(){
            IntegerValidationRule rule = factory.createIntegerValidationRule();
            rule.setLowerLimit(new Integer(3));
            rule.setUpperLimit(new Integer(3));
            AssertJUnit.assertEquals("Validating the value 3.1 against a lower limit of 3 and an upper limit of 3 should result in a pass",0,rule.validateAll(new Double(3.1)).size());
            AssertJUnit.assertTrue("Validating the value 2.9 against a lower limit of 3 and an upper limit of 3 should result in a fail",rule.validateAll(new Double(2.9)).size()>0);
            AssertJUnit.assertEquals("Validating the value 3.99 against a lower limit of 3 and an upper limit of 3 should result in a pass",0,rule.validateAll(new Double(3.99)).size());
    }
    
    @Test()
	public void testValidate_InvalidType(){
            IntegerValidationRule rule = factory.createIntegerValidationRule();
            try{
                rule.validateAll(new Date());
                Assert.fail("Validating a Date type should result in an exception being thrown");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
}
