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

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class Demographics extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document demographics = factory.createDocument("Demographics",
                "Demographics");

        createDocumentStatuses(factory, demographics);

        // main section
        Section mainSection = factory.createSection("Main section occurrence");
        demographics.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        TextEntry dareNumber = factory.createTextEntry("DARE Number", "DARE diabetes number (if assigned)", EntryStatus.OPTIONAL);
        demographics.addEntry(dareNumber);
        dareNumber.setSection(mainSection);

        DateEntry dateOfVisit = factory.createDateEntry("Date of baseline visit", "Date of baseline visit");
        demographics.addEntry(dateOfVisit);
        dateOfVisit.setSection(mainSection);
        dateOfVisit.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));

        OptionEntry gender = factory.createOptionEntry("Gender", "Gender");
        demographics.addEntry(gender);
        gender.setSection(mainSection);
        createOptions(factory, gender, new String[]{"Male", "Female"}, new int[]{1,2});
        gender.setDisableStandardCodes(true);

        DateEntry dob = factory.createDateEntry("Date of birth", "Date of birth");
        demographics.addEntry(dob);
        dob.setSection(mainSection);
        dob.addValidationRule(ValidationRulesWrapper.instance().getRule("After 1900"));
        dob.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dob.setDisableStandardCodes(true);
        dob.setDisablePartialDate(true);

        IntegerEntry age = factory.createIntegerEntry("Age", "Age");
        demographics.addEntry(age);
        age.setSection(mainSection);
        age.addValidationRule(ValidationRulesWrapper.instance().getRule("0 to 99"));

        OptionEntry ethnicity = factory.createOptionEntry("Ethnic origin",
                "Ethnic origin");
        demographics.addEntry(ethnicity);
		ethnicity.setSection(mainSection);
		createOptions(factory, ethnicity,
				new String[]{
				"White - British",
				"White - Irish",
				"White - Other White Background",
				"Asian or Asian British - Indian",
				"Asian or Asian British - Pakistani",
				"Asian or Asian British - Bangladeshi",
				"Asian or Asian British - Other Asian Background",
				"Chinese",
				"Black or Black British - African",
				"Black or Black British - Caribbean",
				"Black or Black British - Other Black Background",
				"Mixed - Asian and White",
				"Mixed - Black Caribbean and White",
				"Mixed - Black African and White",
				"Mixed - Other Mixed Background",
				"Other Ethnic Background"},
				new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});

        return demographics;
    }
}
