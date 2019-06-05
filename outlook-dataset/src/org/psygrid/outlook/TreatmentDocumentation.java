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
package org.psygrid.outlook;

import org.psygrid.data.model.hibernate.*;

public class TreatmentDocumentation extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document treatmentDocumentation = factory.createDocument("TreatmentDocumentation",
                "Treatment Documentation");

        createDocumentStatuses(factory, treatmentDocumentation);

        // general section
        Section generalSection = factory.createSection("General section occurrence");
        treatmentDocumentation.addSection(generalSection);
        generalSection.setDisplayText("General");
        SectionOccurrence generalSectionOcc = factory.createSectionOccurrence("General Section Occurrence");
        generalSection.addOccurrence(generalSectionOcc);

        DateEntry programmeEntry = factory.createDateEntry("Programme Entry",
                "Date of programme entry");
		treatmentDocumentation.addEntry(programmeEntry);
		programmeEntry.setSection(generalSection);

        //Treatment section
        Section treatmentSec = factory.createSection("Treatment Section");
        treatmentDocumentation.addSection(treatmentSec);
        treatmentSec.setDisplayText("Treatment Mode");
        SectionOccurrence treatmentOcc = factory.createSectionOccurrence("Treatment Section Occurrence");
        treatmentSec.addOccurrence(treatmentOcc);
        treatmentOcc.setMultipleAllowed(true);

        // Treatment mode multiselect
        OptionEntry treatmentModeOption = factory.createOptionEntry("Treatment Mode " +
                "option", "Treatment Mode");
        treatmentDocumentation.addEntry(treatmentModeOption);
        treatmentModeOption.setSection(treatmentSec);
        treatmentModeOption.setLabel("a");
        Option monOption = factory.createOption("Monitoring", 1);
        treatmentModeOption.addOption(monOption);
        Option pcOption = factory.createOption("Individual / Psychological Counselling", 2);
        treatmentModeOption.addOption(pcOption);
        Option fwOption = factory.createOption("Family Work", 3);
        treatmentModeOption.addOption(fwOption);
        Option gtOption =  factory.createOption("Group Therapy", 4);
        treatmentModeOption.addOption(gtOption);
        Option trainOption = factory.createOption("Psycho-educational / Skills training", 5);
        treatmentModeOption.addOption(trainOption);
        Option medOption = factory.createOption("Medication", 6);
        treatmentModeOption.addOption(medOption);
        Option otherOption = factory.createOption("Other (specify)", 7);
        treatmentModeOption.addOption(otherOption);
        otherOption.setTextEntryAllowed(true);

        {
			TextEntry modelUsedText = factory.createTextEntry("Model Used",
                    "Model Used");
			treatmentDocumentation.addEntry(modelUsedText);
			modelUsedText.setSection(treatmentSec);
			modelUsedText.setLabel("b");
		}

		{
			LongTextEntry tasksProblems = factory.createLongTextEntry(
                    "Tasks Problems", "Tasks Problems");
			treatmentDocumentation.addEntry(tasksProblems);
			tasksProblems.setSection(treatmentSec);
			tasksProblems.setLabel("c");
		}

		{
			DateEntry whenStartedText = factory.createDateEntry(
                    "Treatment Start", "Treatment Start Date");
			treatmentDocumentation.addEntry(whenStartedText);
			whenStartedText.setSection(treatmentSec);
			whenStartedText.setLabel("d");
		}

		{
			DateEntry whenEndedText = factory.createDateEntry(
                    "Treatment End", "Treatment End Date");
			treatmentDocumentation.addEntry(whenEndedText);
			whenEndedText.setSection(treatmentSec);
			whenEndedText.setLabel("e");
		}

        {
			TextEntry medicationTypeText = factory.createTextEntry("Medication Type",
                    "Medication Type (if applicable)");
			treatmentDocumentation.addEntry(medicationTypeText);
			medicationTypeText.setSection(treatmentSec);
			medicationTypeText.setLabel("f");
		}

        {
			TextEntry medicationDoseText = factory.createTextEntry("Medication Dose",
                    "Medication Dose (if applicable)");
			treatmentDocumentation.addEntry(medicationDoseText);
			medicationDoseText.setSection(treatmentSec);
			medicationDoseText.setLabel("g");
		}

        {
			OptionEntry compliance = factory.createOptionEntry("Treatment compliance",
                    "Treatment compliance");
			treatmentDocumentation.addEntry(compliance);
            compliance.setSection(treatmentSec);
            compliance.setLabel("h");
            compliance.addOption(factory.createOption("Complete refusal", 1));
            compliance.addOption(factory.createOption("Partial refusal (e.g. refused depot) or accepts only minimal dose", 2));
            compliance.addOption(factory.createOption("Accepts only because compulsory, or very reluctant/requires persuasion, or questions need often (e.g. once every two days)", 3));
            compliance.addOption(factory.createOption("Occasional reluctance (e.g. questions need once a week)", 4));
            compliance.addOption(factory.createOption("Passive acceptance", 5));
            compliance.addOption(factory.createOption("Moderate participation, some knowledge and interest in medication and no prompting required", 6));
            compliance.addOption(factory.createOption("Active participation, readily accepts, and shows some responsibility for regimen", 7));
		}

		{
			LongTextEntry notes = factory.createLongTextEntry(
                    "Notes", "Notes");
			treatmentDocumentation.addEntry(notes);
			notes.setSection(treatmentSec);
			notes.setLabel("i");
		}

        return treatmentDocumentation;
    }
}
