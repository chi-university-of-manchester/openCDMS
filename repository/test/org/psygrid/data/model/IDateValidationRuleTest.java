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

import org.psygrid.data.model.hibernate.DateValidationRule;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.TimeUnits;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.Calendar;
import java.util.Date;

public class IDateValidationRuleTest extends ModelTest {

    @Test()
	public void testValidate_AbsLowerOnly(){
            DateValidationRule rule = factory.createDateValidationRule();
            Date now = new Date();
            rule.setAbsLowerLimit(now);
            Calendar cal = Calendar.getInstance();
            Date val1 = now;
            cal.setTime(now);
            cal.add(Calendar.SECOND,1);
            Date val2 = new Date(cal.getTimeInMillis());
            cal.setTime(now);
            cal.add(Calendar.SECOND,-1);
            Date val3 = new Date(cal.getTimeInMillis());
            AssertJUnit.assertEquals("Validating the date '"+val1+"' against a lower limit of '"+now+"' should result in a pass",0,rule.validateAll(val1).size());
            AssertJUnit.assertEquals("Validating the date '"+val2+"' against a lower limit of '"+now+"' should result in a pass",0,rule.validateAll(val2).size());
            AssertJUnit.assertTrue("Validating the date '"+val3+"' against a lower limit of '"+now+"' should result in a fail",rule.validateAll(val3).size()>0);
    }
    
    @Test()
	public void testValidate_AbsUpperOnly(){
            DateValidationRule rule = factory.createDateValidationRule();
            Date now = new Date();
            rule.setAbsUpperLimit(now);
            Calendar cal = Calendar.getInstance();
            Date val1 = now;
            cal.setTime(now);
            cal.add(Calendar.SECOND,1);
            Date val2 = new Date(cal.getTimeInMillis());
            cal.setTime(now);
            cal.add(Calendar.SECOND,-1);
            Date val3 = new Date(cal.getTimeInMillis());
            AssertJUnit.assertEquals("Validating the date '"+val1+"' against an upper limit of '"+now+"' should result in a pass",0,rule.validateAll(val1).size());
            AssertJUnit.assertTrue("Validating the date '"+val2+"' against an upper limit of '"+now+"' should result in a fail",rule.validateAll(val2).size()>0);
            AssertJUnit.assertEquals("Validating the date '"+val3+"' against an upper limit of '"+now+"' should result in a pass",0,rule.validateAll(val3).size());
    }
    
    @Test()
	public void testValidate_AbsLowerAbsUpper(){
            DateValidationRule rule = factory.createDateValidationRule();
            Date now = new Date();
            rule.setAbsLowerLimit(now);
            rule.setAbsUpperLimit(now);
            Calendar cal = Calendar.getInstance();
            Date val1 = now;
            cal.setTime(now);
            cal.add(Calendar.SECOND,1);
            Date val2 = new Date(cal.getTimeInMillis());
            cal.setTime(now);
            cal.add(Calendar.SECOND,-1);
            Date val3 = new Date(cal.getTimeInMillis());
            AssertJUnit.assertEquals("Validating the date '"+val1+"' against a lower limit of '"+now+"' and an upper limit of '"+now+"' should result in a pass",0,rule.validateAll(val1).size());
            AssertJUnit.assertTrue("Validating the date '"+val2+"' against a lower limit of '"+now+"' and an upper limit of '"+now+"' should result in a fail",rule.validateAll(val2).size()>0);
            AssertJUnit.assertTrue("Validating the date '"+val3+"' against a lower limit of '"+now+"' and an upper limit of '"+now+"' should result in a fail",rule.validateAll(val3).size()>0);
    }
    
    @Test()
	public void testValidate_InvalidType(){
            DateValidationRule rule = factory.createDateValidationRule();
            try{
                rule.validateAll(new Double(2));
                Assert.fail("Exception should have been thrown when trying to validate a Double using a date validation rule");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testValidate_RelLower_Days(){
            DateValidationRule rule = factory.createDateValidationRule();
            rule.setRelLowerLimit(-3);
            rule.setRelLowerLimitUnits(TimeUnits.DAYS);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -4);
            AssertJUnit.assertTrue("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a fail",rule.validateAll(cal.getTime()).size()>0);
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -3);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -2);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
    }
    
    @Test()
	public void testValidate_RelLower_Weeks(){
            DateValidationRule rule = factory.createDateValidationRule();
            rule.setRelLowerLimit(-7);
            rule.setRelLowerLimitUnits(TimeUnits.WEEKS);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_MONTH, -8);
            AssertJUnit.assertTrue("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a fail",rule.validateAll(cal.getTime()).size()>0);
            cal = Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_MONTH, -7);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
            cal = Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_MONTH, -6);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
    }
    
    @Test()
	public void testValidate_RelLower_Months(){
            DateValidationRule rule = factory.createDateValidationRule();
            rule.setRelLowerLimit(-7);
            rule.setRelLowerLimitUnits(TimeUnits.MONTHS);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -8);
            AssertJUnit.assertTrue("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a fail",rule.validateAll(cal.getTime()).size()>0);
            cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -7);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
            cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -6);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
    }
    
    @Test()
	public void testValidate_RelLower_Years(){
            DateValidationRule rule = factory.createDateValidationRule();
            rule.setRelLowerLimit(-7);
            rule.setRelLowerLimitUnits(TimeUnits.YEARS);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -8);
            AssertJUnit.assertTrue("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a fail",rule.validateAll(cal.getTime()).size()>0);
            cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -7);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
            cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -6);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelLowerLimit()+" "+rule.getRelLowerLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
    }
    
    @Test()
	public void testValidate_RelLower_NoUnits(){
            DateValidationRule rule = factory.createDateValidationRule();
            rule.setRelLowerLimit(-7);
            try{
                rule.validateAll(new Date());
                Assert.fail("Exception should have been thrown when trying to validate with null date units");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
    @Test()
	public void testValidate_RelUpper_Days(){
            DateValidationRule rule = factory.createDateValidationRule();
            rule.setRelUpperLimit(5);
            rule.setRelUpperLimitUnits(TimeUnits.DAYS);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 6);
            AssertJUnit.assertTrue("Validating the date '"+cal.getTime()+"' against a relative upper limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a fail",rule.validateAll(cal.getTime()).size()>0);
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 5);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 4);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
    }
    
    @Test()
	public void testValidate_RelUpper_Weeks(){
            DateValidationRule rule = factory.createDateValidationRule();
            rule.setRelUpperLimit(5);
            rule.setRelUpperLimitUnits(TimeUnits.WEEKS);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_MONTH, 6);
            AssertJUnit.assertTrue("Validating the date '"+cal.getTime()+"' against a relative upper limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a fail",rule.validateAll(cal.getTime()).size()>0);
            cal = Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_MONTH, 5);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
            cal = Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_MONTH, 4);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
    }
    
    @Test()
	public void testValidate_RelUpper_Months(){
            DateValidationRule rule = factory.createDateValidationRule();
            rule.setRelUpperLimit(5);
            rule.setRelUpperLimitUnits(TimeUnits.MONTHS);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 6);
            AssertJUnit.assertTrue("Validating the date '"+cal.getTime()+"' against a relative upper limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a fail",rule.validateAll(cal.getTime()).size()>0);
            cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 5);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
            cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 4);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
    }
    
    @Test()
	public void testValidate_RelUpper_Years(){
            DateValidationRule rule = factory.createDateValidationRule();
            rule.setRelUpperLimit(5);
            rule.setRelUpperLimitUnits(TimeUnits.YEARS);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 6);
            AssertJUnit.assertTrue("Validating the date '"+cal.getTime()+"' against a relative upper limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a fail",rule.validateAll(cal.getTime()).size()>0);
            cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 5);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
            cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 4);
            AssertJUnit.assertEquals("Validating the date '"+cal.getTime()+"' against a relative lower limit of "+rule.getRelUpperLimit()+" "+rule.getRelUpperLimitUnits()+" should result in a pass",0,rule.validateAll(cal.getTime()).size());
    }
    
    @Test()
	public void testValidate_RelUpper_NoUnits(){
            DateValidationRule rule = factory.createDateValidationRule();
            rule.setRelUpperLimit(-7);
            try{
                rule.validateAll(new Date());
                Assert.fail("Exception should have been thrown when trying to validate with null date units");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
    
}
