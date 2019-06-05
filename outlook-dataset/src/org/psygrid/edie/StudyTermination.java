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

public class StudyTermination extends AssessmentForm {

	public static Document createDocument(Factory factory) {

		ValidationRule notInFuture = ValidationRulesWrapper.instance().getRule(
				"Not in future");
		ValidationRule after1900 = ValidationRulesWrapper.instance().getRule(
				"After 1900");

		Document termination = factory.createDocument("Study Termination",
                "Study Termination");

		createDocumentStatuses(factory, termination);

		// main section
		Section mainSection = factory.createSection("Main section occurrence");
		termination.addSection(mainSection);
		mainSection.setDisplayText("Main");
		SectionOccurrence mainSectionOcc = factory
				.createSectionOccurrence("Main Section Occurrence");
		mainSection.addOccurrence(mainSectionOcc);

		DateEntry dot = factory.createDateEntry("Termination Date", "Date of Termination");
		termination.addEntry(dot);
		dot.setSection(mainSection);
		dot.addValidationRule(notInFuture);
		dot.addValidationRule(after1900);

		OptionEntry site = factory.createOptionEntry("Termination Reason", "For what reason did termination occur?");
		site.addOption(factory.createOption("End of scheduled assessments", 1));
		site.addOption(factory.createOption("Withdrew", 2));
		site.addOption(factory.createOption("Lost Contact", 3));
		site.addOption(factory.createOption("Moved out of area", 4));
		site.addOption(factory.createOption("Transition necessitating inpatient treatment", 5));
		site.addOption(factory.createOption("Adverse event", 6));
		Option other = factory.createOption("Other", 7);
		site.addOption(other);
		other.setTextEntryAllowed(true);
		termination.addEntry(site);
		site.setSection(mainSection);

		OptionEntry allocation = factory.createOptionEntry("Allocation", "Please guess the participant's allocation");
		allocation.addOption(factory.createOption("Cognitive Therapy", 1));
		allocation.addOption(factory.createOption("Monitoring", 0));
		termination.addEntry(allocation);
		allocation.setSection(mainSection);

		LongTextEntry otherTreatment = factory.createLongTextEntry(
                "Other Treatment", "Describe any other psychological treatment"
                + " the participant received during the study.");
		otherTreatment.setSection(mainSection);
		termination.addEntry(otherTreatment);



		return termination;
	}
}
