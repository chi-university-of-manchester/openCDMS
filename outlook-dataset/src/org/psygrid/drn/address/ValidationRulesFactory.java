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


package org.psygrid.drn.address;

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

	        DateValidationRule notInFuture = factory.createDateValidationRule();
	        notInFuture.setDescription("Not in future");
	        notInFuture.setRelUpperLimit(new Integer(0));
	        notInFuture.setRelUpperLimitUnits(TimeUnits.DAYS);
	        notInFuture.setMessage("It is not possible to enter a date in the future.");
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

	        DateValidationRule afterOr2007 = factory.createDateValidationRule();
	        afterOr2007.setDescription("2007 or after");
	        cal.set(2007, 0, 1);
	        cal.clear(Calendar.MILLISECOND);
	        cal.clear(Calendar.SECOND);
	        cal.clear(Calendar.MINUTE);
	        cal.clear(Calendar.HOUR);
	        cal.clear(Calendar.HOUR_OF_DAY);
	        cal.clear(Calendar.AM_PM);
	        afterOr2007.setAbsLowerLimit(cal.getTime());
	        rulesMap.put("2007 or after",afterOr2007);

	        DateValidationRule afterOr1990 = factory.createDateValidationRule();
	        afterOr1990.setDescription("1990 or after");
	        cal.set(1990, 0, 1);
	        cal.clear(Calendar.MILLISECOND);
	        cal.clear(Calendar.SECOND);
	        cal.clear(Calendar.MINUTE);
	        cal.clear(Calendar.HOUR);
	        cal.clear(Calendar.HOUR_OF_DAY);
	        cal.clear(Calendar.AM_PM);
	        afterOr1990.setAbsLowerLimit(cal.getTime());
	        rulesMap.put("1990 or after",afterOr1990);

	        DateValidationRule afterOr2000 = factory.createDateValidationRule();
	        afterOr2000.setDescription("2000 or after");
	        cal.set(2000, 0, 1);
	        cal.clear(Calendar.MILLISECOND);
	        cal.clear(Calendar.SECOND);
	        cal.clear(Calendar.MINUTE);
	        cal.clear(Calendar.HOUR);
	        cal.clear(Calendar.HOUR_OF_DAY);
	        cal.clear(Calendar.AM_PM);
	        afterOr2000.setAbsLowerLimit(cal.getTime());
	        rulesMap.put("2000 or after",afterOr2000);

	        IntegerValidationRule zeroToNinetyNine = factory.createIntegerValidationRule();
	        zeroToNinetyNine.setDescription("0 to 99");
	        zeroToNinetyNine.setLowerLimit(new Integer(0));
	        zeroToNinetyNine.setUpperLimit(new Integer(99));
	        rulesMap.put("0 to 99",zeroToNinetyNine);

	        IntegerValidationRule positive = factory.createIntegerValidationRule();
	        positive.setDescription("Positive");
	        positive.setLowerLimit(new Integer(0));
	        rulesMap.put("Positive",positive);

	        NumericValidationRule systolicBP = factory.createNumericValidationRule();
	        systolicBP.setDescription("Systolic BP");
	        systolicBP.setLowerLimit(new Double(50.0));
	        systolicBP.setUpperLimit(new Double(300.0));
	        rulesMap.put("Systolic BP",systolicBP);

	        NumericValidationRule diastolicBP = factory.createNumericValidationRule();
	        diastolicBP.setDescription("Diastolic BP");
	        diastolicBP.setLowerLimit(new Double(10.0));
	        diastolicBP.setUpperLimit(new Double(200.0));
	        rulesMap.put("Diastolic BP",diastolicBP);

	        NumericValidationRule weight = factory.createNumericValidationRule();
	        weight.setDescription("Weight kg");
	        weight.setLowerLimit(new Double(1.0));
	        weight.setUpperLimit(new Double(200.0));
	        rulesMap.put("Weight kg",weight);

	        NumericValidationRule height = factory.createNumericValidationRule();
	        height.setDescription("Height m");
	        height.setLowerLimit(new Double(0.1));
	        height.setUpperLimit(new Double(2.5));
	        rulesMap.put("Height m",height);

	        NumericValidationRule hipWaistCircumference = factory.createNumericValidationRule();
	        hipWaistCircumference.setDescription("Circumference");
	        hipWaistCircumference.setLowerLimit(new Double(20.0));
	        hipWaistCircumference.setUpperLimit(new Double(199.0));
	        rulesMap.put("Circumference",hipWaistCircumference);

	        NumericValidationRule proteinuriaConcentration = factory.createNumericValidationRule();
	        proteinuriaConcentration.setDescription("Proteinuria Concentration");
	        proteinuriaConcentration.setLowerLimit(new Double(0.1));
	        proteinuriaConcentration.setUpperLimit(new Double(1000));
	        proteinuriaConcentration.setUpperGte(true);
	        rulesMap.put("Proteinuria Concentration",proteinuriaConcentration);

	        NumericValidationRule pointZeroOneToFiveHundred = factory.createNumericValidationRule();
	        pointZeroOneToFiveHundred.setDescription("0.01 to 500");
	        pointZeroOneToFiveHundred.setLowerLimit(new Double(0.01));
	        pointZeroOneToFiveHundred.setUpperLimit(new Double(500));
	        pointZeroOneToFiveHundred.setUpperGte(true);
	        rulesMap.put("0.01 to 500",pointZeroOneToFiveHundred);

	        IntegerValidationRule oneToThreeThousand = factory.createIntegerValidationRule();
	        oneToThreeThousand.setDescription("1 to 3000");
	        oneToThreeThousand.setLowerLimit(new Integer(1));
	        oneToThreeThousand.setUpperLimit(new Integer(3000));
	        oneToThreeThousand.setUpperGte(true);
	        rulesMap.put("1 to 3000",oneToThreeThousand);

	        NumericValidationRule oneToOneThousand = factory.createNumericValidationRule();
	        oneToOneThousand.setDescription("1 to 1000");
	        oneToOneThousand.setLowerLimit(new Double(1));
	        oneToOneThousand.setUpperLimit(new Double(1000));
	        oneToOneThousand.setUpperGte(true);
	        rulesMap.put("1 to 1000",oneToOneThousand);

	        NumericValidationRule oneToFiveHundred = factory.createNumericValidationRule();
	        oneToFiveHundred.setDescription("1 to 500");
	        oneToFiveHundred.setLowerLimit(new Double(1));
	        oneToFiveHundred.setUpperLimit(new Double(500));
	        oneToFiveHundred.setUpperGte(true);
	        rulesMap.put("1 to 500",oneToFiveHundred);

	        NumericValidationRule pointOneToFiveHundred = factory.createNumericValidationRule();
	        pointOneToFiveHundred.setDescription("0.1 to 500");
	        pointOneToFiveHundred.setLowerLimit(new Double(0.1));
	        pointOneToFiveHundred.setUpperLimit(new Double(500));
	        pointOneToFiveHundred.setUpperGte(true);
	        rulesMap.put("0.1 to 500",pointOneToFiveHundred);

	        NumericValidationRule pointOneToFiveThousand = factory.createNumericValidationRule();
	        pointOneToFiveThousand.setDescription("0.1 to 5000");
	        pointOneToFiveThousand.setLowerLimit(new Double(0.1));
	        pointOneToFiveThousand.setUpperLimit(new Double(5000));
	        pointOneToFiveThousand.setUpperGte(true);
	        rulesMap.put("0.1 to 5000",pointOneToFiveThousand);

	        NumericValidationRule pointZeroOneToOneHundred = factory.createNumericValidationRule();
	        pointZeroOneToOneHundred.setDescription("0.01 to 100");
	        pointZeroOneToOneHundred.setLowerLimit(new Double(0.01));
	        pointZeroOneToOneHundred.setUpperLimit(new Double(100));
	        pointZeroOneToOneHundred.setUpperGte(true);
	        rulesMap.put("0.01 to 100",pointZeroOneToOneHundred);

	        NumericValidationRule pointZeroOneToTen = factory.createNumericValidationRule();
	        pointZeroOneToTen.setDescription("0.01 to 10");
	        pointZeroOneToTen.setLowerLimit(new Double(0.01));
	        pointZeroOneToTen.setUpperLimit(new Double(10));
	        pointZeroOneToTen.setUpperGte(true);
	        rulesMap.put("0.01 to 10",pointZeroOneToTen);

	        NumericValidationRule pointOneToOneHundred = factory.createNumericValidationRule();
	        pointOneToOneHundred.setDescription("0.1 to 100");
	        pointOneToOneHundred.setLowerLimit(new Double(0.1));
	        pointOneToOneHundred.setUpperLimit(new Double(100));
	        pointOneToOneHundred.setUpperGte(true);
	        rulesMap.put("0.1 to 100",pointOneToOneHundred);

	        NumericValidationRule tenToFiveHundred = factory.createNumericValidationRule();
	        tenToFiveHundred.setDescription("10 to 500");
	        tenToFiveHundred.setLowerLimit(new Double(10));
	        tenToFiveHundred.setUpperLimit(new Double(500));
	        tenToFiveHundred.setUpperGte(true);
	        rulesMap.put("10 to 500",tenToFiveHundred);

	        NumericValidationRule pointOneToFifty = factory.createNumericValidationRule();
	        pointOneToFifty.setDescription("0.1 to 50");
	        pointOneToFifty.setLowerLimit(new Double(0.1));
	        pointOneToFifty.setUpperLimit(new Double(50));
	        pointOneToFifty.setUpperGte(true);
	        rulesMap.put("0.1 to 50",pointOneToFifty);

	        NumericValidationRule pointZeroOneToFifty = factory.createNumericValidationRule();
	        pointZeroOneToFifty.setDescription("0.01 to 50");
	        pointZeroOneToFifty.setLowerLimit(new Double(0.01));
	        pointZeroOneToFifty.setUpperLimit(new Double(50));
	        pointZeroOneToFifty.setUpperGte(true);
	        rulesMap.put("0.01 to 50",pointZeroOneToFifty);

	        NumericValidationRule pointOneToSixty = factory.createNumericValidationRule();
	        pointOneToSixty.setDescription("0.1 to 60");
	        pointOneToSixty.setLowerLimit(new Double(0.1));
	        pointOneToSixty.setUpperLimit(new Double(60));
	        pointOneToSixty.setUpperGte(true);
	        rulesMap.put("0.1 to 60",pointOneToSixty);


	        for (ValidationRule rule : rulesMap.values()) {
	            dataSet.addValidationRule(rule);
	        }

		}

	}
}
