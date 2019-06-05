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


package org.psygrid.matisse;

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

	        NumericValidationRule oneToHundred = factory.createNumericValidationRule();
	        oneToHundred.setDescription("OneToHundred");
	        oneToHundred.setLowerLimit(1.0);
	        oneToHundred.setUpperLimit(100.0);
	        rulesMap.put("OneToHundred",oneToHundred);

	        NumericValidationRule oneToFive = factory.createNumericValidationRule();
	        oneToFive.setDescription("OneToFive");
	        oneToFive.setLowerLimit(1.0);
	        oneToFive.setUpperLimit(5.0);
	        rulesMap.put("OneToFive",oneToFive);

	        NumericValidationRule zeroToThree = factory.createNumericValidationRule();
	        zeroToThree.setDescription("ZeroToThree");
	        zeroToThree.setLowerLimit(0.0);
	        zeroToThree.setUpperLimit(3.0);
	        rulesMap.put("ZeroToThree",zeroToThree);

	        NumericValidationRule zeroToFour = factory.createNumericValidationRule();
	        zeroToFour.setDescription("ZeroToFour");
	        zeroToFour.setLowerLimit(0.0);
	        zeroToFour.setUpperLimit(4.0);
	        rulesMap.put("ZeroToFour",zeroToFour);

	        NumericValidationRule zeroToFive = factory.createNumericValidationRule();
	        zeroToFive.setDescription("ZeroToFive");
	        zeroToFive.setLowerLimit(0.0);
	        zeroToFive.setUpperLimit(5.0);
	        rulesMap.put("ZeroToFive",zeroToFive);

	        NumericValidationRule zeroToSix = factory.createNumericValidationRule();
	        zeroToSix.setDescription("ZeroToSix");
	        zeroToSix.setLowerLimit(0.0);
	        zeroToSix.setUpperLimit(6.0);
	        rulesMap.put("ZeroToSix",zeroToSix);

	        NumericValidationRule zeroToHundred = factory.createNumericValidationRule();
	        zeroToHundred.setDescription("ZeroToHundred");
	        zeroToHundred.setLowerLimit(0.0);
	        zeroToHundred.setUpperLimit(100.0);
	        rulesMap.put("ZeroToHundred",zeroToHundred);

	        NumericValidationRule zeroToThirty = factory.createNumericValidationRule();
	        zeroToThirty.setDescription("ZeroToThirty");
	        zeroToThirty.setLowerLimit(0.0);
	        zeroToThirty.setUpperLimit(30.0);
	        rulesMap.put("ZeroToThirty",zeroToThirty);

	        NumericValidationRule oneToSeven = factory.createNumericValidationRule();
	        oneToSeven.setDescription("OneToSeven");
	        oneToSeven.setLowerLimit(1.0);
	        oneToSeven.setUpperLimit(7.0);
	        rulesMap.put("OneToSeven",oneToSeven);

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
	        postCodeArea.setLowerLimit(2);
	        postCodeArea.setUpperLimit(4);
	        //The following regular expression has been written with reference to
	        //the published rules for postcodes - see
	        //http://www.govtalk.gov.uk/gdsc/html/noframes/PostCode-2-1-Release.htm
	        //It has been tested in  the unit test
	        //org.psygrid.data.model.ITextValidationRuleTest#testValidate_Regex_Postcode
	        //of the repository project.
	        postCodeArea.setPattern("(([A-PR-UWYZ](([0-9][0-9A-HJKSTUW]?)|([A-HK-Y][0-9][0-9ABEHMNPRVWXY]?)))|(GIR))");
	        postCodeArea.setPatternDetails("For examples of valid postcodes please see http://www.govtalk.gov.uk/gdsc/html/noframes/PostCode-2-1-Release.htm");
	        rulesMap.put("Validation of UK postcode areas",postCodeArea);

	        NumericValidationRule positiveNumber = factory.createNumericValidationRule();
	        positiveNumber.setDescription("Positive");
	        positiveNumber.setLowerLimit(0.0);
	        rulesMap.put("Positive",positiveNumber);

	        NumericValidationRule greaterThanZero = factory.createNumericValidationRule();
	        greaterThanZero.setDescription("Greater than zero");
	        greaterThanZero.setLowerLimit(1.0);
	        rulesMap.put("Greater than zero",greaterThanZero);

	        DateValidationRule notInFuture = factory.createDateValidationRule();
	        notInFuture.setDescription("Not in future");
	        notInFuture.setRelUpperLimit(new Integer(0));
	        notInFuture.setRelUpperLimitUnits(TimeUnits.DAYS);
	        notInFuture.setMessage("It is not possible to enter a date in the future for something that has already happened.");
	        rulesMap.put("Not in future",notInFuture);

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
	        rulesMap.put("After 1900",after1900);

	        DateValidationRule after2000 = factory.createDateValidationRule();
	        after2000.setDescription("After 2000");
	        cal.set(2000, 0, 1);
	        cal.clear(Calendar.MILLISECOND);
	        cal.clear(Calendar.SECOND);
	        cal.clear(Calendar.MINUTE);
	        cal.clear(Calendar.HOUR);
	        cal.clear(Calendar.HOUR_OF_DAY);
	        cal.clear(Calendar.AM_PM);
	        after2000.setAbsLowerLimit(cal.getTime());
	        rulesMap.put("After 2000",after2000);

	        NumericValidationRule zeroOnly = factory.createNumericValidationRule();
	        zeroOnly.setDescription("ZeroTwoFourOrSix");
	        zeroOnly.setLowerLimit(0.0);
	        zeroOnly.setUpperLimit(0.0);
	        zeroOnly.setMessage("Please enter either 0, 2, 4 or 6.");
	        rulesMap.put("ZeroTwoFourOrSix",zeroOnly);

	        NumericValidationRule twoOnly = factory.createNumericValidationRule();
	        twoOnly.setDescription("TwoOnly");
	        twoOnly.setLowerLimit(2.0);
	        twoOnly.setUpperLimit(2.0);
	        rulesMap.put("TwoOnly",twoOnly);
	        zeroOnly.addAssociatedRule(twoOnly);

	        NumericValidationRule fourOnly = factory.createNumericValidationRule();
	        fourOnly.setDescription("FourOnly");
	        fourOnly.setLowerLimit(4.0);
	        fourOnly.setUpperLimit(4.0);
	        rulesMap.put("FourOnly",fourOnly);
	        zeroOnly.addAssociatedRule(fourOnly);

	        NumericValidationRule sixOnly = factory.createNumericValidationRule();
	        sixOnly.setDescription("SixOnly");
	        sixOnly.setLowerLimit(6.0);
	        sixOnly.setUpperLimit(6.0);
	        rulesMap.put("SixOnly", sixOnly);
	        zeroOnly.addAssociatedRule(sixOnly);

	        NumericValidationRule zeroToTen = factory.createNumericValidationRule();
	        zeroToTen.setDescription("ZeroTwoFourSixEightOrTen");
	        zeroToTen.setLowerLimit(0.0);
	        zeroToTen.setUpperLimit(0.0);
	        zeroToTen.setMessage("Please enter either 0, 2, 4, 6, 8 or 10.");
	        rulesMap.put("ZeroTwoFourSixEightOrTen",zeroToTen);

	        NumericValidationRule eightOnly = factory.createNumericValidationRule();
	        eightOnly.setDescription("EightOnly");
	        eightOnly.setLowerLimit(8.0);
	        eightOnly.setUpperLimit(8.0);
	        rulesMap.put("EightOnly",eightOnly);

	        NumericValidationRule tenOnly = factory.createNumericValidationRule();
	        tenOnly.setDescription("TenOnly");
	        tenOnly.setLowerLimit(10.0);
	        tenOnly.setUpperLimit(10.0);
	        rulesMap.put("TenOnly",tenOnly);

	        zeroToTen.addAssociatedRule(twoOnly);
	        zeroToTen.addAssociatedRule(fourOnly);
	        zeroToTen.addAssociatedRule(sixOnly);
	        zeroToTen.addAssociatedRule(eightOnly);
	        zeroToTen.addAssociatedRule(tenOnly);

	        for (ValidationRule rule : rulesMap.values()) {
	            dataSet.addValidationRule(rule);
	        }

		}

	}
}
