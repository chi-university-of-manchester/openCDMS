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

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;

public class AdverseOutcomesClientSelfHarm extends AssessmentForm{

    public static Document createDocument(Factory factory) {

        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");

        Document doc = factory.createDocument("Adverse Outcomes Detailed " +
                "Questionnaire (Self-Harm)", "Adverse Outcomes Detailed Questionnaire (Self-Harm)");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main " +
                "Section Occurrence");
        mainSec.addOccurrence(mainSecOcc);

        DateEntry whenHarmed = factory.createDateEntry("When did you harm " +
                "yourself", "When did you harm yourself?");
        doc.addEntry(whenHarmed);
        whenHarmed.setSection(mainSec);
        whenHarmed.addValidationRule(after1900);

        OptionEntry howHarmed = factory.createOptionEntry("How did you harm "
                + "yourself?", "How did you harm yourself?");
        doc.addEntry(howHarmed);
        howHarmed.setSection(mainSec);
        howHarmed.addOption(factory.createOption("Knife/razor", "Knife/razor",
                1));
        howHarmed.addOption(factory.createOption("Pills/drugs/alcohol",
                "Pills/drugs/alcohol", 2));
        howHarmed.addOption(factory.createOption("Hanging", "Hanging", 3));
        howHarmed.addOption(factory.createOption("Suffocation", "Suffocation ",
                4));
        howHarmed.addOption(factory.createOption("Jump from high place",
                "Jump from high place", 5));
        howHarmed.addOption(factory.createOption(
                "Throw self in front of vehicle / in vehicle",
                "Throw self in front of vehicle / in vehicle", 6));
        howHarmed
                .addOption(factory.createOption("Starvation", "Starvation", 7));
        howHarmed.addOption(factory.createOption("Fire/burning",
                "Fire/burning", 8));
        howHarmed.addOption(factory.createOption("Drowning", "Drowning", 9));
        howHarmed.addOption(factory.createOption("Gun", "Gun", 10));
        Option otherOption = factory.createOption("Other (please specify)",
                "Other (please specify)", 11);
        otherOption.setTextEntryAllowed(true);
        howHarmed.addOption(otherOption);
        howHarmed.addOption(factory.createOption(
                "Knife/razor and pills/ drugs/alcohol",
                "Knife/razor and pills/ drugs/alcohol", 12));
        howHarmed.addOption(factory.createOption("Knife/razor and other",
                "Knife/razor and other", 13));
        howHarmed.addOption(factory.createOption(
                "Pills/drugs/alcohol and other",
                "Pills/drugs/alcohol and other", 14));
        Option otherMultipleOption = factory.createOption(
                "Other multiple (please specify)",
                "Other multiple (please specify)", 15);
        otherMultipleOption.setTextEntryAllowed(true);
        howHarmed.addOption(otherMultipleOption);

        OptionEntry degreeHarm = factory.createOptionEntry(
                "Degree of self-harm " + "sought",
                "Degree of self-harm sought");
        doc.addEntry(degreeHarm);
        degreeHarm.setSection(mainSec);
        degreeHarm.addOption(factory.createOption("None", "None", 0));
        degreeHarm.addOption(factory.createOption("Minor injury",
                "Minor injury", 1));
        degreeHarm.addOption(factory.createOption("Major injury",
                "Major injury", 2));
        degreeHarm.addOption(factory.createOption("Injury (degree not clear)",
                "Injury (degree not clear)", 3));
        degreeHarm.addOption(factory.createOption("Death", "Death", 4));

        OptionEntry environment = factory.createOptionEntry("Alteration of "
                + "environment as aim of self-harm",
                "Alteration/manipulation of "
                        + "environment as aim of self-harm");
        doc.addEntry(environment);
        environment.setSection(mainSec);
        environment.addOption(factory.createOption("None", "None", 0));
        environment.addOption(factory.createOption("Primary aim",
                "Primary aim", 1));
        environment.addOption(factory.createOption("Unclear or secondary aim",
                "Unclear or secondary aim", 2));

        OptionEntry premeditation = factory.createOptionEntry("Degree of " +
                "premeditation", "Degree of premeditation");
        doc.addEntry(premeditation);
        premeditation.setSection(mainSec);
        premeditation.addOption(factory.createOption("None; impulsive", "None; " +
                "impulsive", 0));
        premeditation.addOption(factory.createOption("Contemplated for three " +
                "hours or less", "Self harm contemplated for three hours or " +
                "less prior to attempt", 1));
        premeditation.addOption(factory.createOption("Contemplated for more " +
                "than three hours", "Self harm contemplated for more than three " +
                "hours prior to attempt", 2));

        OptionEntry death = factory.createOptionEntry(
                "Acts in anticipation of death",
                "Acts in anticipation of death");
        doc.addEntry(death);
        death.setSection(mainSec);
        death.addOption(factory.createOption("None", "None", 0));
        death.addOption(factory.createOption("Thought about", "Thought about", 1));
        death.addOption(factory.createOption("Made definite plans or completed " +
                "arrangements", "Made definite plans or completed arrangements", 2));

        OptionEntry attempt = factory.createOptionEntry(
                "Active preparation for attempt",
                "Active preparation for attempt");
        doc.addEntry(attempt);
        attempt.setSection(mainSec);
        attempt.addOption(factory.createOption("None", "None", 0));
        attempt.addOption(factory.createOption("Minimal to moderate",
                "Minimal to moderate", 1));
        attempt.addOption(factory.createOption("Extensive", "Extensive", 2));

        OptionEntry harmNote = factory.createOptionEntry("Self-harm note",
                "Self-harm note");
        doc.addEntry(harmNote);
        harmNote.setSection(mainSec);
        harmNote.addOption(factory.createOption("Absence of note",
                "Absence of note", 0));
        harmNote.addOption(factory.createOption("Note thought about",
                "Note thought about", 1));
        harmNote.addOption(factory.createOption("Note written, but torn up",
                "Note written, but torn up", 2));
        harmNote.addOption(factory.createOption("Presence of note",
                "Presence of note", 3));

        OptionEntry discovery = factory.createOptionEntry(
                "Precautions against discovery/intervention",
                "Precautions against discovery/intervention");
        doc.addEntry(discovery);
        discovery.setSection(mainSec);
        discovery.addOption(factory.createOption("No precautions",
                "No precautions", 0));
        discovery.addOption(factory.createOption("Precautions", "Precautions",
                1));

        OptionEntry isolation = factory.createOptionEntry("Isolation",
                "Isolation");
        doc.addEntry(isolation);
        isolation.setSection(mainSec);
        isolation.addOption(factory.createOption("Somebody present",
                "Somebody present", 0));
        isolation.addOption(factory.createOption(
                "Somebody nearby or in visual or vocal contact",
                "Somebody nearby or in visual or vocal contact", 1));
        isolation.addOption(factory.createOption(
                "No one nearby or in visual or vocal contact",
                "No one nearby or in visual or vocal contact", 2));

        OptionEntry intervention = factory.createOptionEntry(
                "Probability of intervention by others",
                "Probability of intervention by others");
        doc.addEntry(intervention);
        intervention.setSection(mainSec);
        intervention.addOption(factory.createOption(
                "Intervention was probable", "Intervention was probable", 0));
        intervention.addOption(factory
                .createOption("Intervention was not likely",
                        "Intervention was not likely", 1));
        intervention.addOption(factory.createOption(
                "Intervention was highly unlikely",
                "Intervention was highly unlikely", 2));

        OptionEntry selfIntervention = factory.createOptionEntry(
                "Self intervention", "Self intervention");
        doc.addEntry(selfIntervention);
        selfIntervention.setSection(mainSec);
        selfIntervention.addOption(factory.createOption("No", "No", 0));
        selfIntervention.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry hospital = factory.createOptionEntry(
                "If you harmed yourself, did you go to general hospital?",
                "If you harmed yourself, did you go to general hospital?");
        doc.addEntry(hospital);
        hospital.setSection(mainSec);
        hospital.addOption(factory.createOption("No", "No", 0));
        hospital.addOption(factory.createOption("Yes", "Yes", 1));

        return doc;
    }
}
