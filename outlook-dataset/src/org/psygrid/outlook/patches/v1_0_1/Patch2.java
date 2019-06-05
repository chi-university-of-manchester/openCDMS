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

package org.psygrid.outlook.patches.v1_0_1;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch2 extends AbstractPatch {

    public String getName() {
        return "Correct validation rules for Premorbid (e.g. 0,2,4,6 for Establishment of Independence etc)";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();

        NumericValidationRule zeroTwoFourOrSix = factory.createNumericValidationRule();
        zeroTwoFourOrSix.setDescription("ZeroTwoFourOrSix");
        zeroTwoFourOrSix.setLowerLimit(0.0);
        zeroTwoFourOrSix.setUpperLimit(0.0);
        zeroTwoFourOrSix.setMessage("Please enter either 0, 2, 4 or 6.");
        ds.addValidationRule(zeroTwoFourOrSix);

        NumericValidationRule twoOnly = factory.createNumericValidationRule();
        twoOnly.setDescription("TwoOnly");
        twoOnly.setLowerLimit(2.0);
        twoOnly.setUpperLimit(2.0);
        ds.addValidationRule(twoOnly);
        zeroTwoFourOrSix.addAssociatedRule(twoOnly);

        NumericValidationRule fourOnly = factory.createNumericValidationRule();
        fourOnly.setDescription("FourOnly");
        fourOnly.setLowerLimit(4.0);
        fourOnly.setUpperLimit(4.0);
        ds.addValidationRule(fourOnly);
        zeroTwoFourOrSix.addAssociatedRule(fourOnly);

        NumericValidationRule sixOnly = factory.createNumericValidationRule();
        sixOnly.setDescription("SixOnly");
        sixOnly.setLowerLimit(6.0);
        sixOnly.setUpperLimit(6.0);
        ds.addValidationRule(sixOnly);
        zeroTwoFourOrSix.addAssociatedRule(sixOnly);

        Document premorbid = ds.getDocument(12);
        if ( !"Premorbid Summary Sheet".equals(premorbid.getName())){
            throw new RuntimeException("This is not the Premorbid Summary Sheet - it is "+premorbid.getName());
        }

        String description2 = "Please enter a number from 0, 2, 4 and 6.";
        String description3 = "If Married (presently or formerly), enter a number between 0 and 3; "+
                              "if Never Married (Over 30), enter a number between 2 and 6;"+
                              "if Never Married (20-29), enter a number from 0, 1, 3, 5 and 6.";

        BasicEntry clientAdultSocSex = (BasicEntry)premorbid.getEntry(20);
        if ( !"Social sexual aspects of life".equals(clientAdultSocSex.getName())){
            throw new RuntimeException("This is not the 'Social sexual aspects of life' entry - it is "+clientAdultSocSex.getName());
        }
        clientAdultSocSex.setDescription(description3);

        BasicEntry clientEstabInd = (BasicEntry)premorbid.getEntry(26);
        if ( !"Establishment of Independence".equals(clientEstabInd.getName())){
            throw new RuntimeException("This is not the 'Establishment of Independence' entry - it is "+clientEstabInd.getName());
        }
        clientEstabInd.removeValidationRule(0);
        clientEstabInd.addValidationRule(zeroTwoFourOrSix);
        clientEstabInd.setDescription(description2);

        BasicEntry clientHighLevelFunction = (BasicEntry)premorbid.getEntry(27);
        if ( !"Highest Level of Functioning Achieved in Patient's Life".equals(clientHighLevelFunction.getName())){
            throw new RuntimeException("This is not the 'Highest Level of Functioning Achieved in Patient's Life' entry - it is "+clientHighLevelFunction.getName());
        }
        clientHighLevelFunction.removeValidationRule(0);
        clientHighLevelFunction.addValidationRule(zeroTwoFourOrSix);
        clientHighLevelFunction.setDescription(description2);

        BasicEntry clientDegreeInterest = (BasicEntry)premorbid.getEntry(29);
        if ( !"Degree of Interest in Life".equals(clientDegreeInterest.getName())){
            throw new RuntimeException("This is not the 'Degree of Interest in Life' entry - it is "+clientDegreeInterest.getName());
        }
        clientDegreeInterest.removeValidationRule(0);
        clientDegreeInterest.addValidationRule(zeroTwoFourOrSix);
        clientDegreeInterest.setDescription(description2);

        BasicEntry clientEnergyLevel = (BasicEntry)premorbid.getEntry(30);
        if ( !"Energy Level".equals(clientEnergyLevel.getName())){
            throw new RuntimeException("This is not the 'Energy Level' entry - it is "+clientEnergyLevel.getName());
        }
        clientEnergyLevel.removeValidationRule(0);
        clientEnergyLevel.addValidationRule(zeroTwoFourOrSix);
        clientEnergyLevel.setDescription(description2);

        BasicEntry carerAdultSocSex = (BasicEntry)premorbid.getEntry(51);
        if ( !"Social sexual aspects of life".equals(carerAdultSocSex.getName())){
            throw new RuntimeException("This is not the 'Social sexual aspects of life' entry - it is "+carerAdultSocSex.getName());
        }
        carerAdultSocSex.setDescription(description3);

        BasicEntry carerEstabInd = (BasicEntry)premorbid.getEntry(57);
        if ( !"Establishment of Independence".equals(carerEstabInd.getName())){
            throw new RuntimeException("This is not the 'Establishment of Independence' entry - it is "+carerEstabInd.getName());
        }
        carerEstabInd.removeValidationRule(0);
        carerEstabInd.addValidationRule(zeroTwoFourOrSix);
        carerEstabInd.setDescription(description2);

        BasicEntry carerHighLevelFunction = (BasicEntry)premorbid.getEntry(58);
        if ( !"Highest Level of Functioning Achieved in Patient's Life".equals(carerHighLevelFunction.getName())){
            throw new RuntimeException("This is not the 'Highest Level of Functioning Achieved in Patient's Life' entry - it is "+carerHighLevelFunction.getName());
        }
        carerHighLevelFunction.removeValidationRule(0);
        carerHighLevelFunction.addValidationRule(zeroTwoFourOrSix);
        carerHighLevelFunction.setDescription(description2);

        BasicEntry carerDegreeInterest = (BasicEntry)premorbid.getEntry(60);
        if ( !"Degree of Interest in Life".equals(carerDegreeInterest.getName())){
            throw new RuntimeException("This is not the 'Degree of Interest in Life' entry - it is "+carerDegreeInterest.getName());
        }
        carerDegreeInterest.removeValidationRule(0);
        carerDegreeInterest.addValidationRule(zeroTwoFourOrSix);
        carerDegreeInterest.setDescription(description2);

        BasicEntry carerEnergyLevel = (BasicEntry)premorbid.getEntry(61);
        if ( !"Energy Level".equals(carerEnergyLevel.getName())){
            throw new RuntimeException("This is not the 'Energy Level' entry - it is "+carerEnergyLevel.getName());
        }
        carerEnergyLevel.removeValidationRule(0);
        carerEnergyLevel.addValidationRule(zeroTwoFourOrSix);
        carerEnergyLevel.setDescription(description2);

        //remove validation rules and descritions from derived entries
        int[] derivedEntries = new int[]{63, 64, 65, 66, 68, 69, 70, 71, 72, 74, 75, 76, 77, 78, 80, 81, 82, 84, 85, 86, 87, 88, 89, 90, 91, 92};
        for ( int i=0; i<derivedEntries.length; i++ ){
        	BasicEntry be = (BasicEntry)premorbid.getEntry(derivedEntries[i]);
        	//check this entry is from the adjusted section and is a derived entry
        	if ( !"Adjusted scores".equals(be.getSection().getDisplayText()) ||
        			!(be instanceof DerivedEntry)){
        		throw new RuntimeException("This is not a derived entry from the adjusted scores section (index "+derivedEntries[i]+")");
        	}
        	be.removeValidationRule(0);
        	be.setDescription(null);
        }

    }

}
