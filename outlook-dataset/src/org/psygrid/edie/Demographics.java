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
package org.psygrid.edie;

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class Demographics extends AssessmentForm {

	public static Document createDocument(Factory factory) {

		ValidationRule notInFuture = ValidationRulesWrapper.instance().getRule(
				"Not in future");
		ValidationRule after1900 = ValidationRulesWrapper.instance().getRule(
				"After 1900");

		Document demographics = factory.createDocument("Demographics",
                "Demographics");

		createDocumentStatuses(factory, demographics);

		// main section
		Section mainSection = factory.createSection("Main section occurrence");
		demographics.addSection(mainSection);
		mainSection.setDisplayText("Main");
		SectionOccurrence mainSectionOcc = factory
				.createSectionOccurrence("Main Section Occurrence");
		mainSection.addOccurrence(mainSectionOcc);

		DateEntry dob = factory.createDateEntry("DOB", "Date of Birth");
		demographics.addEntry(dob);
		dob.setSection(mainSection);
		dob.addValidationRule(notInFuture);
		dob.addValidationRule(after1900);

		IntegerEntry age = factory.createIntegerEntry("Age", "Age (at entry to the study)");
		demographics.addEntry(age);
		age.setSection(mainSection);

		IntegerEntry edu = factory.createIntegerEntry("Education", "Years of full-time education");
		demographics.addEntry(edu);
		edu.setSection(mainSection);

		OptionEntry site = factory.createOptionEntry("Site", "Site");
		site.addOption(factory.createOption("Manchester", 1));
		site.addOption(factory.createOption("Birmingham", 2));
		site.addOption(factory.createOption("Cambridge", 3));
		site.addOption(factory.createOption("East Anglia", 4));
		site.addOption(factory.createOption("Glasgow", 5));
		demographics.addEntry(site);
		site.setSection(mainSection);

		OptionEntry sex = factory.createOptionEntry("Gender", "Gender");
		sex.addOption(factory.createOption("Male", 1));
		sex.addOption(factory.createOption("Female", 2));
		demographics.addEntry(sex);
		sex.setSection(mainSection);

		OptionEntry ethnicity = factory.createOptionEntry("Ethnicity",
                "Ethnic Group");
		demographics.addEntry(ethnicity);
		ethnicity.setSection(mainSection);
		ethnicity.addOption(factory.createOption("White", 1));
		ethnicity.addOption(factory.createOption("Black - Caribbean", 2));
		ethnicity.addOption(factory.createOption("Black - African", 3));
		ethnicity.addOption(factory.createOption("Black - Other", 4));
		ethnicity.addOption(factory.createOption("Indian", 5));
		ethnicity.addOption(factory.createOption("Pakistani", 6));
		ethnicity.addOption(factory.createOption("Bangladeshi", 7));
		ethnicity.addOption(factory.createOption("Chinese", 8));
		ethnicity.addOption(factory.createOption("Other", 9));
		ethnicity.addOption(factory.createOption("Undisclosed", 10));

		return demographics;
	}
}
