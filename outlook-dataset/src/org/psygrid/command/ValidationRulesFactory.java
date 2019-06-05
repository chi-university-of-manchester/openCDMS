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


package org.psygrid.command;

import java.util.Calendar;

import org.psygrid.common.IValidationRuleFactory;
import org.psygrid.data.model.hibernate.*;

/**
 * @author Rob Harper
 *
 */
public class ValidationRulesFactory implements IValidationRuleFactory {

	public ValidationRules makeInstance() {
		return new ValidationRules();
	}

	private class ValidationRules extends org.psygrid.common.ValidationRules {
		private ValidationRules(){
			super();
		}

		@Override
		public void init(Factory factory, DataSet dataSet) {
	        //numeric validation rule for PANSS
	        NumericValidationRule panssRule = factory.createNumericValidationRule();
	        panssRule.setDescription("PANSS validation rule");
	        panssRule.setLowerLimit(1.0);
	        panssRule.setUpperLimit(7.0);
	        rulesMap.put("PANSS validation rule",panssRule);

	        IntegerValidationRule positive = factory.createIntegerValidationRule();
	    	positive.setDescription("Positive Integer");
	    	positive.setLowerLimit(new Integer(0));
	    	rulesMap.put("Positive Integer", positive);

	    	NumericValidationRule zeroToOneHundred = factory.createNumericValidationRule();
	    	zeroToOneHundred.setDescription("0 to 100");
	    	zeroToOneHundred.setLowerLimit(new Double(0));
	    	zeroToOneHundred.setUpperLimit(new Double(100));
	    	rulesMap.put("0 to 100", zeroToOneHundred);

	        TextValidationRule postCode = factory.createTextValidationRule();
	        postCode.setDescription("Validation of UK postcodes");
	        postCode.setMessage("This is not a valid UK postcode.");
	        postCode.setLowerLimit(5);
	        postCode.setUpperLimit(8);
	        //The following regular expression has been written with reference to
	        //the published rules for postcodes - see
	        //http://www.govtalk.gov.uk/gdsc/html/noframes/PostCode-2-1-Release.htm
	        //It has been tested in  the unit test
	        //org.psygrid.data.model.ITextValidationRuleTest#testValidate_Regex_Postcode
	        //of the repository project.
	        postCode.setPattern("(([A-PR-UWYZ](([0-9][0-9A-HJKSTUW]?)|([A-HK-Y][0-9][0-9ABEHMNPRVWXY]?))[ ]{0,2}[0-9][ABD-HJLNP-UW-Z]{2,2})|(GIR[ ]{0,2}0AA))");
	        postCode.setPatternDetails("For examples of valid postcodes please see http://www.govtalk.gov.uk/gdsc/html/noframes/PostCode-2-1-Release.htm");
	        rulesMap.put("Validation of UK postcodes",postCode);

	        TextValidationRule postCodeArea = factory.createTextValidationRule();
	        postCodeArea.setDescription("Validation of UK postcode areas");
	        postCodeArea.setMessage("This is not a valid UK postcode area.");
	        //The following regular expression has been written with reference to
	        //the published rules for postcodes - see
	        //http://www.govtalk.gov.uk/gdsc/html/noframes/PostCode-2-1-Release.htm
	        //It has been tested in  the unit test
	        //org.psygrid.data.model.ITextValidationRuleTest#testValidate_Regex_Postcode
	        //of the repository project.
	        postCodeArea.setPattern("(([A-PR-UWYZ](([0-9][0-9A-HJKSTUW]?)|([A-HK-Y][0-9][0-9ABEHMNPRVWXY]?))|(GIR)))");
	        postCodeArea.setPatternDetails("For examples of valid postcodes please see http://www.govtalk.gov.uk/gdsc/html/noframes/PostCode-2-1-Release.htm");
	        rulesMap.put("Validation of UK postcode areas",postCodeArea);

	        NumericValidationRule positiveNumber = factory.createNumericValidationRule();
	        positiveNumber.setDescription("Positive");
	        positiveNumber.setLowerLimit(0.0);
	        rulesMap.put("Positive", positiveNumber);

	        NumericValidationRule zeroToFive = factory.createNumericValidationRule();
	        zeroToFive.setDescription("ZeroToFive");
	        zeroToFive.setLowerLimit(0.0);
	        zeroToFive.setUpperLimit(5.0);
	        rulesMap.put("ZeroToFive", zeroToFive);

	        NumericValidationRule oneToFive = factory.createNumericValidationRule();
	        oneToFive.setDescription("OneToFive");
	        oneToFive.setLowerLimit(1.0);
	        oneToFive.setUpperLimit(5.0);
	        rulesMap.put("OneToFive", oneToFive);

	        NumericValidationRule oneToFiveAndMinusEight = factory.createNumericValidationRule();
	        zeroToFive.setDescription("OneToFiveAndMinusEight");
	        zeroToFive.setLowerLimit(1.0);
	        zeroToFive.setUpperLimit(5.0);
	        NumericValidationRule minusEight = factory.createNumericValidationRule();
	        minusEight.setLowerLimit(new Double(-8.0));
	        minusEight.setUpperLimit(new Double(-8.0));
	        oneToFiveAndMinusEight.addAssociatedRule(minusEight);
	        rulesMap.put("OneToFiveAndMinusEight", oneToFiveAndMinusEight);

	        NumericValidationRule positiveOrMinusEight = factory.createNumericValidationRule();
	        zeroToFive.setDescription("PositiveOrMinusEight");
	        zeroToFive.setLowerLimit(0.0);
	        oneToFiveAndMinusEight.addAssociatedRule(minusEight);
	        rulesMap.put("PositiveOrMinusEight", positiveOrMinusEight);

	        DateValidationRule notInFuture = factory.createDateValidationRule();
	        notInFuture.setDescription("Not in future");
	        notInFuture.setRelUpperLimit(new Integer(0));
	        notInFuture.setRelUpperLimitUnits(TimeUnits.DAYS);
	        notInFuture.setMessage("It is not possible to enter a date in the future for something that has already happened.");
	        rulesMap.put("Not in future", notInFuture);

	        DateValidationRule after1900 = factory.createDateValidationRule();
	        after1900.setDescription("After 1900");
	        Calendar cal = Calendar.getInstance();
	        cal.set(1900, 0, 1);
	        cal.clear(Calendar.MILLISECOND);
	        cal.clear(Calendar.SECOND);
	        cal.clear(Calendar.MINUTE);
	        cal.clear(Calendar.HOUR);
	        cal.clear(Calendar.HOUR_OF_DAY);
	        cal.clear(Calendar.AM_PM);
	        after1900.setAbsLowerLimit(cal.getTime());
	        rulesMap.put("After 1900", after1900);

	        //EQ5D health thermometer validation rule
	        NumericValidationRule healthThermRule = factory.createNumericValidationRule();
	        healthThermRule.setDescription("Health thermometer validation rule");
	        healthThermRule.setLowerLimit(0.0);
	        healthThermRule.setUpperLimit(100.0);
	        rulesMap.put("Health thermometer validation rule",healthThermRule);

	        //BHS suicidal ideation total validation rule
	        NumericValidationRule suicidalIdeationRule = factory.createNumericValidationRule();
	        suicidalIdeationRule.setDescription("Suicidal ideation total validation rule");
	        suicidalIdeationRule.setLowerLimit(0.0);
	        suicidalIdeationRule.setUpperLimit(38.0);
	        suicidalIdeationRule.setMessage("Total should be between 0-38");
	        rulesMap.put("Suicidal ideation total validation rule",suicidalIdeationRule);

	        for (ValidationRule rule : rulesMap.values()) {
	            dataSet.addValidationRule(rule);
	        }

		}

	}
}
