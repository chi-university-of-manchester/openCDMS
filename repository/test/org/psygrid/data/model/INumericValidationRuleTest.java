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

import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.NumericValidationRule;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.Date;

public class INumericValidationRuleTest extends ModelTest {

    @Test()
	public void testValidate_LowerOnly(){
            NumericValidationRule rule = factory.createNumericValidationRule();
            rule.setLowerLimit(new Double(2.5));
            AssertJUnit.assertEquals("Validating the value 2.5 against a lower limit of 2.5 should result in a pass",0,rule.validateAll(new Double(2.5)).size());
            AssertJUnit.assertEquals("Validating the value 2.51 against a lower limit of 2.5 should result in a pass",0,rule.validateAll(new Double(2.51)).size());
            AssertJUnit.assertTrue("Validating the value 2.49 against a lower limit of 2.5 should result in a fail",rule.validateAll(new Double(2.49)).size()>0);
    }
    
    @Test()
	public void testValidate_UpperOnly(){
            NumericValidationRule rule = factory.createNumericValidationRule();
            rule.setUpperLimit(new Double(2.5));
            AssertJUnit.assertEquals("Validating the value 2.5 against an upper limit of 2.5 should result in a pass",0,rule.validateAll(new Double(2.5)).size());
            AssertJUnit.assertEquals("Validating the value 2.49 against an upper limit of 2.5 should result in a pass",0,rule.validateAll(new Double(2.49)).size());
            AssertJUnit.assertTrue("Validating the value 2.51 against an upper limit of 2.5 should result in a fail",rule.validateAll(new Double(2.51)).size()>0);
    }
    
    @Test()
	public void testValidate_LowerUpper(){
            NumericValidationRule rule = factory.createNumericValidationRule();
            rule.setLowerLimit(new Double(2.5));
            rule.setUpperLimit(new Double(2.5));
            AssertJUnit.assertEquals("Validating the value 2.5 against a lower limit of 2.5 and an upper limit of 2.5 should result in a pass",0,rule.validateAll(new Double(2.5)).size());
            AssertJUnit.assertTrue("Validating the value 2.49 against a lower limit of 2.5 and an upper limit of 2.5 should result in a fail",rule.validateAll(new Double(2.49)).size()>0);
            AssertJUnit.assertTrue("Validating the value 2.51 against a lower limit of 2.5 and an upper limit of 2.5 should result in a fail",rule.validateAll(new Double(2.51)).size()>0);
    }
    
    @Test()
	public void testValidate_StringArg(){
            NumericValidationRule rule = factory.createNumericValidationRule();
            String val1 = "2.53";
            AssertJUnit.assertEquals("Validating the string value '"+val1+"' should result in a pass", 0, rule.validateAll(val1).size());
            String val2 = "2.53e+2";
            AssertJUnit.assertEquals("Validating the string value '"+val2+"' should result in a pass", 0, rule.validateAll(val2).size());
            String val3 = "2.53E-2";
            AssertJUnit.assertEquals("Validating the string value '"+val3+"' should result in a pass", 0, rule.validateAll(val3).size());
            String val4 = "2.53.2";
            AssertJUnit.assertTrue("Validating the string value '"+val4+"' should result in a fail", rule.validateAll(val4).size()>0);
            String val5 = "2.53l";
            AssertJUnit.assertTrue("Validating the string value '"+val5+"' should result in a fail", rule.validateAll(val5).size()>0);
    }
    
    @Test()
	public void testValidate_LongArg(){
            NumericValidationRule rule = factory.createNumericValidationRule();
            rule.setLowerLimit(new Double(3.0));
            rule.setUpperLimit(new Double(3.0));
            AssertJUnit.assertEquals("Validating the value 3 against a lower limit of 3.0 and an upper limit of 3.0 should result in a pass",0,rule.validateAll(new Long(3)).size());
            AssertJUnit.assertTrue("Validating the value 2 against a lower limit of 3.0 and an upper limit of 3.0 should result in a fail",rule.validateAll(new Long(2)).size()>0);
            AssertJUnit.assertTrue("Validating the value 4 against a lower limit of 3.0 and an upper limit of 3.0 should result in a fail",rule.validateAll(new Long(4)).size()>0);
    }
    
    @Test()
	public void testValidate_InvalidType(){
            NumericValidationRule rule = factory.createNumericValidationRule();
            try{
                rule.validateAll(new Date());
                Assert.fail("Validating a Date type should result in an exception being thrown");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
}
