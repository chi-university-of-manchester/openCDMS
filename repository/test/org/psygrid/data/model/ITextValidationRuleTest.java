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
import org.psygrid.data.model.hibernate.TextValidationRule;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.Date;

public class ITextValidationRuleTest extends ModelTest {

    @Test()
	public void testValidate_LowerOnly(){
            TextValidationRule rule = factory.createTextValidationRule();
            rule.setLowerLimit(5);
            String val1 = "12345";
            String val2 = "123456";
            String val3 = "1234";
            AssertJUnit.assertEquals("Validating the string '"+val1+"' against a lower limit of 5 should result in a pass",0,rule.validateAll(val1).size());
            AssertJUnit.assertEquals("Validating the string '"+val2+"' against a lower limit of 5 should result in a pass",0,rule.validateAll(val2).size());
            AssertJUnit.assertTrue("Validating the string '"+val3+"' against a lower limit of 5 should result in a fail",rule.validateAll(val3).size()>0);
    }
    
    @Test()
	public void testValidate_UpperOnly(){
            TextValidationRule rule = factory.createTextValidationRule();
            rule.setUpperLimit(5);
            String val1 = "12345";
            String val2 = "123456";
            String val3 = "1234";
            AssertJUnit.assertEquals("Validating the string '"+val1+"' against an upper limit of 5 should result in a pass",0,rule.validateAll(val1).size());
            AssertJUnit.assertTrue("Validating the string '"+val2+"' against an upper limit of 5 should result in a fail",rule.validateAll(val2).size()>0);
            AssertJUnit.assertEquals("Validating the string '"+val3+"' against an upper limit of 5 should result in a pass",0,rule.validateAll(val3).size());
    }
    
    @Test()
	public void testValidate_LowerUpper(){
            TextValidationRule rule = factory.createTextValidationRule();
            rule.setLowerLimit(5);
            rule.setUpperLimit(5);
            String val1 = "12345";
            String val2 = "123456";
            String val3 = "1234";
            AssertJUnit.assertEquals("Validating the string '"+val1+"' against a lower limit of 5 and an upper limit of 5 should result in a pass",0,rule.validateAll(val1).size());
            AssertJUnit.assertTrue("Validating the string '"+val2+"' against a lower limit of 5 and an upper limit of 5 should result in a fail",rule.validateAll(val2).size()>0);
            AssertJUnit.assertTrue("Validating the string '"+val3+"' against a lower limit of 5 and an upper limit of 5 should result in a fail",rule.validateAll(val3).size()>0);
    }
    
    @Test()
	public void testValidate_Regex(){
            TextValidationRule rule = factory.createTextValidationRule();
            rule.setPattern("[A-Za-z0-9._%-]+@[A-Za-z0-9._%-]+\\.[A-Za-z]{2,4}");
            String val1 = "robert.s.harper@manchester.ac.uk";
            String val2 = "invalid_email_address";
            AssertJUnit.assertEquals("Validating the string '"+val1+"' using a regex pattern for valid email addresses should result in a pass",0,rule.validateAll(val1).size());
            AssertJUnit.assertTrue("Validating the string '"+val2+"' using a regex pattern for valid email addresses should result in a fail",rule.validateAll(val2).size()>0);
    }
    
    @Test()
	public void testValidate_Regex_Postcode(){
            TextValidationRule rule = factory.createTextValidationRule();
            rule.setPattern("(([A-PR-UWYZ](([0-9][0-9A-HJKSTUW]?)|([A-HK-Y][0-9][0-9ABEHMNPRVWXY]?))[ ]{0,2}[0-9][ABD-HJLNP-UW-Z]{2,2})|(GIR[ ]{0,2}0AA))");
            //valid postcodes
            String[] valid = new String[9];
            valid[0] = "M33 6QE";
            valid[1] = "BH189BW";
            valid[2] = "BH18 9BW";
            valid[3] = "M1 1AB";
            valid[4] = "M1  1AB";
            valid[5] = "SW1W 0EX";
            valid[6] = "SW1W0EX";
            valid[7] = "Z8F 7JN";
            valid[8] = "GIR 0AA";
            for ( int i=0; i<valid.length; i++ ){
                AssertJUnit.assertEquals("Validating the string '"+valid[i]+"' using a regex pattern for valid postcodes should result in a pass",0,rule.validateAll(valid[i]).size());
            }
            //invalid postcodes
            String[] invalid = new String[49];
            invalid[0] = "Q2 6QE";
            invalid[1] = "V2 6QE";
            invalid[2] = "X2 6QE";
            invalid[3] = "BI18 9BW";
            invalid[4] = "BJ18 9BW";
            invalid[5] = "BZ18 9BW";
            invalid[6] = "Z8I 7JN";
            invalid[7] = "Z8L 7JN";
            invalid[8] = "Z8M 7JN";
            invalid[9] = "Z8N 7JN";
            invalid[10] = "Z8O 7JN";
            invalid[11] = "Z8P 7JN";
            invalid[12] = "Z8Q 7JN";
            invalid[13] = "Z8R 7JN";
            invalid[14] = "Z8V 7JN";
            invalid[15] = "Z8X 7JN";
            invalid[16] = "Z8Y 7JN";
            invalid[17] = "Z8Z 7JN";
            invalid[18] = "SW1C 0EX";
            invalid[19] = "SW1D 0EX";
            invalid[20] = "SW1F 0EX";
            invalid[21] = "SW1G 0EX";
            invalid[22] = "SW1I 0EX";
            invalid[23] = "SW1J 0EX";
            invalid[24] = "SW1K 0EX";
            invalid[25] = "SW1L 0EX";
            invalid[26] = "SW1O 0EX";
            invalid[27] = "SW1Q 0EX";
            invalid[28] = "SW1S 0EX";
            invalid[29] = "SW1T 0EX";
            invalid[30] = "SW1U 0EX";
            invalid[31] = "SW1Z 0EX";
            invalid[32] = "M33 6CE";
            invalid[33] = "M33 6IE";
            invalid[34] = "M33 6KE";
            invalid[35] = "M33 6ME";
            invalid[36] = "M33 6OE";
            invalid[37] = "M33 6VE";
            invalid[38] = "M33 6QC";
            invalid[39] = "M33 6QI";
            invalid[40] = "M33 6QK";
            invalid[41] = "M33 6QM";
            invalid[42] = "M33 6QO";
            invalid[43] = "M33 6QV";
            invalid[44] = "M33 6Q";
            invalid[45] = "M33 6";
            invalid[46] = "M33 6QQQ";
            invalid[47] = "M33 66Q";
            invalid[48] = "BHB2 9BW";
            for ( int i=0; i<invalid.length; i++){
                AssertJUnit.assertTrue("Validating the string '"+invalid[i]+"' using a regex pattern for valid postcodes should result in a fail",rule.validateAll(invalid[i]).size()>0);
            }
    }
    
    @Test()
	public void testValidate_InvalidType(){
            TextValidationRule rule = factory.createTextValidationRule();
            try{
                rule.validateAll(new Date());
                Assert.fail("Exception should have been thrown when trying to valudate a Date with a text validation rule.");
            }
            catch(ModelException ex){
                //do nothing
            }
    }
}
