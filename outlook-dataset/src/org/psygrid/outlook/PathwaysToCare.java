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

public class PathwaysToCare extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");

        Document pathwaysToCare = factory.createDocument("PathwaysToCare",
                "Pathways to Care - Collated");

        createDocumentStatuses(factory, pathwaysToCare);

        // general section
        Section generalSection = factory.createSection("General section occurrence");
        pathwaysToCare.addSection(generalSection);
        generalSection.setDisplayText("General");
        SectionOccurrence generalSectionOcc = factory.createSectionOccurrence("General Section Occurrence");
        generalSection.addOccurrence(generalSectionOcc);

        OptionEntry prevCareOption = factory.createOptionEntry("Previous care history",
                "Does the client have any history of previous care by any " +
                        "mental health service?");
        Option yesOption = factory.createOption("Yes", 1);
        Option noOption = factory.createOption("No", 0);
        prevCareOption.addOption(yesOption);
        prevCareOption.addOption(noOption);
        pathwaysToCare.addEntry(prevCareOption);
        prevCareOption.setSection(generalSection);

        LongTextEntry additionalDetailsLong = factory.createLongTextEntry("Additional Details",
                "If Yes, give details:");
        additionalDetailsLong.setEntryStatus(EntryStatus.DISABLED);
        OptionDependent prevCareOptionDep = factory.createOptionDependent();
        yesOption.addOptionDependent(prevCareOptionDep);
        prevCareOptionDep.setDependentEntry(additionalDetailsLong);
        prevCareOptionDep.setEntryStatus(EntryStatus.MANDATORY);
        pathwaysToCare.addEntry(additionalDetailsLong);
        additionalDetailsLong.setSection(generalSection);

        LongTextEntry additionalInfoLong = factory.createLongTextEntry("Additional Info",
                "Additional Info");
        pathwaysToCare.addEntry(additionalInfoLong);
        additionalInfoLong.setSection(generalSection);

        //Pathways section
        Section pathwaySec = factory.createSection("Pathway Section");
        pathwaysToCare.addSection(pathwaySec);
        pathwaySec.setDisplayText("Pathway ");
        SectionOccurrence pathwayOcc = factory.createSectionOccurrence("Pathway Section Occurrence");
        pathwaySec.addOccurrence(pathwayOcc);
        pathwayOcc.setMultipleAllowed(true);

        // Source
		OptionEntry sourceOption = factory.createOptionEntry("Source", "Source");
		pathwaysToCare.addEntry(sourceOption);
		sourceOption.setSection(pathwaySec);
		Option sourceClientOption = factory.createOption("Client", 0);
		sourceOption.addOption(sourceClientOption);
		Option sourceCarerOption = factory.createOption("Carer/Family", 1);
		sourceOption.addOption(sourceCarerOption);
        Option sourceNotesOption = factory.createOption("Notes", 2);
        sourceOption.addOption(sourceNotesOption);
		Option sourceClientCarerOption = factory.createOption("Client and Carer", 3);
		sourceOption.addOption(sourceClientCarerOption);
		Option sourceOtherOption = factory.createOption("Other (Specify)", 4);
		sourceOption.addOption(sourceOtherOption);
		sourceOtherOption.setTextEntryAllowed(true);

        // Who was seen multiselect
        OptionEntry whoSeenOption = factory.createOptionEntry("Who was seen " +
                "option", "Who was contacted?");
        pathwaysToCare.addEntry(whoSeenOption);
        whoSeenOption.setSection(pathwaySec);
        whoSeenOption.setLabel("a");
        Option gpOption = factory.createOption("GP", 1);
        whoSeenOption.addOption(gpOption);
        Option cpnOption = factory.createOption("CPN", 2);
        whoSeenOption.addOption(cpnOption);
        Option ssOption = factory.createOption("Social services", 3);
        whoSeenOption.addOption(ssOption);
        Option relLeaderOption =  factory.createOption("Religious Leader", 4);
        whoSeenOption.addOption(relLeaderOption);
        Option casDeptOption = factory.createOption("Casualty Dept", 5);
        whoSeenOption.addOption(casDeptOption);
        Option privDoctorOption = factory.createOption("Private doctor", 6);
        whoSeenOption.addOption(privDoctorOption);
        Option policeOption = factory.createOption("Police", 7);
        whoSeenOption.addOption(policeOption);
        Option psychHospOption = factory.createOption("Psychiatric Hospital", 8);
        whoSeenOption.addOption(psychHospOption);
        Option homeTreatOption = factory.createOption("Home treatment team", 9);
        whoSeenOption.addOption(homeTreatOption);
        Option primCareOption = factory.createOption("Primary Care Team", 10);
        whoSeenOption.addOption(primCareOption);
        Option cmhtOption = factory.createOption("CMHT (unspecified)", 11);
        whoSeenOption.addOption(cmhtOption);
        Option psychOption = factory.createOption("Psychiatrist", 12);
        whoSeenOption.addOption(psychOption);
        Option psychDrugsOption = factory.createOption("Psychiatrist (drugs service)", 13);
        whoSeenOption.addOption(psychDrugsOption);
        Option neurologOption = factory.createOption("Neurologist", 14);
        whoSeenOption.addOption(neurologOption);
        Option counselOption = factory.createOption("Counsellor", 15);
        whoSeenOption.addOption(counselOption);
        Option suppWorkerOption = factory.createOption("Support Worker", 16);
        whoSeenOption.addOption(suppWorkerOption);
        Option childAndAdolServOption = factory.createOption("Child & Adol. " +
                "Psych services", 17);
        whoSeenOption.addOption(childAndAdolServOption);
        Option otherOption = factory.createOption("Other (specify)", 18);
        whoSeenOption.addOption(otherOption);
        otherOption.setTextEntryAllowed(true);
        Option prisonServOption = factory.createOption("Prison services", 19);
        whoSeenOption.addOption(prisonServOption);

        {
			// How seen question 2a
			OptionEntry howSeenOption = factory.createOptionEntry("How seen "
                    + "option", "How contacted?");
			pathwaysToCare.addEntry(howSeenOption);
			howSeenOption.setSection(pathwaySec);
			howSeenOption.setLabel("a2");
			Option inPersonOption = factory.createOption("In Person", 1);
			howSeenOption.addOption(inPersonOption);
			Option phoneCallOption = factory.createOption("Phone Call", 2);
			howSeenOption.addOption(phoneCallOption);
			Option letterOption = factory.createOption("Letter", 3);
			howSeenOption.addOption(letterOption);
		}

        DateEntry whenSeenText = factory.createDateEntry("When seen",
                "When was this person/team seen?");
        pathwaysToCare.addEntry(whenSeenText);
        whenSeenText.setSection(pathwaySec);
        whenSeenText.setLabel("b");
        whenSeenText.addValidationRule(after1900);

        OptionEntry appAttendedOption = factory.createOptionEntry("Appointment " +
                "attended", "Was the appointment attended?");
        pathwaysToCare.addEntry(appAttendedOption);
        appAttendedOption.setSection(pathwaySec);
        appAttendedOption.setLabel("b2");
        appAttendedOption.addOption(factory.createOption("No", 1));
        appAttendedOption.addOption(factory.createOption("Client", 2));
        appAttendedOption.addOption(factory.createOption("Family", 3));
        appAttendedOption.addOption(factory.createOption("Client and family", 4));

        OptionEntry whoSuggestedOption = factory.createOptionEntry("Who " +
                "suggested care", "Who suggested that care was sought");
        pathwaysToCare.addEntry(whoSuggestedOption);
        whoSuggestedOption.setSection(pathwaySec);
        whoSuggestedOption.setLabel("c");
        Option clientOption = factory.createOption("Client", 1);
        whoSuggestedOption.addOption(clientOption);
        Option familyOption = factory.createOption("Family member", 2);
        whoSuggestedOption.addOption(familyOption);
        Option friendOption = factory.createOption("Friend", 3);
        whoSuggestedOption.addOption(friendOption);
        Option referredOption = factory.createOption("Referred from previous" +
                " carer (Enter Pathway No.)", 4);
        whoSuggestedOption.addOption(referredOption);
        referredOption.setTextEntryAllowed(true);
        Option clientAndFamOption = factory.createOption("Client and family", 5);
        whoSuggestedOption.addOption(clientAndFamOption);
        Option gpSourceOption = factory.createOption("GP (source referral to " +
                "GP unknown)", 6);
        whoSuggestedOption.addOption(gpSourceOption);
        Option workOption = factory.createOption("Work colleague", 7);
        whoSuggestedOption.addOption(workOption);
        Option detainedOption = factory.createOption("Detained by police " +
                "(due to offending/behaviour)", 8);
        whoSuggestedOption.addOption(detainedOption);
        Option pathContactOption = factory.createOption("Pathway contact " +
                "approached client to offer help", 9);
        whoSuggestedOption.addOption(pathContactOption);
        Option fupOption = factory.createOption("Follow up appointment (Enter Pathway No.)", 10);
        whoSuggestedOption.addOption(fupOption);
        fupOption.setTextEntryAllowed(true);

        LongTextEntry mainProblemText = factory.createLongTextEntry("Main problem",
                "What was the main problem presented?");
        pathwaysToCare.addEntry(mainProblemText);
        mainProblemText.setSection(pathwaySec);
        mainProblemText.setLabel("d");

        OptionEntry symptomsOption = factory.createOptionEntry("Client symptoms",
                "Do you (the researcher) consider the client's symptoms at this " +
                        "point to have been: ");
        pathwaysToCare.addEntry(symptomsOption);
        symptomsOption.setSection(pathwaySec);
        Option psychoticOption = factory.createOption("Psychotic", 1);
        symptomsOption.addOption(psychoticOption);
        Option nonPsychoticOption = factory.createOption("Non-psychotic", 2);
        symptomsOption.addOption(nonPsychoticOption);
        Option unclearOption = factory.createOption("Symptomology unclear", 3);
        symptomsOption.addOption(unclearOption);

        OptionEntry medOffOption = factory.createOptionEntry("Medication offered",
                "Was medication offered?");
        pathwaysToCare.addEntry(medOffOption);
        medOffOption.setSection(pathwaySec);
        medOffOption.setLabel("e");
        Option medNoOption = factory.createOption("No", 0);
        medOffOption.addOption(medNoOption);
        Option antiPsychOption = factory.createOption("Antipsychotic", 1);
        medOffOption.addOption(antiPsychOption);
        Option medOtherOption = factory.createOption("Other", 2);
        medOffOption.addOption(medOtherOption);

        OptionEntry refToOtherOption = factory.createOptionEntry("Referred to " +
                "other services", "Was the client referred to other services.");
        pathwaysToCare.addEntry(refToOtherOption);
        refToOtherOption.setSection(pathwaySec);
        refToOtherOption.setLabel("f");
        Option refNoOption = factory.createOption("No", 0);
        refToOtherOption.addOption(refNoOption);
        Option acceptedOption = factory.createOption("Accepted by EIS", 1);
        refToOtherOption.addOption(acceptedOption);
        Option referredToEisOption = factory.createOption("Referred to EIS - not " +
                "accepted", 2);
        refToOtherOption.addOption(referredToEisOption);
        Option referredToOtherOption = factory.createOption("Referred " +
                "to other agency/professional (Enter Pathway No.)", 3);
        refToOtherOption.addOption(referredToOtherOption);
        referredToOtherOption.setTextEntryAllowed(true);
        Option refToFUPOption = factory.createOption("Follow up Appointment (Enter Pathway No.)", 4);
        refToOtherOption.addOption(refToFUPOption);
        refToFUPOption.setTextEntryAllowed(true);

        OptionEntry otherTreatOption = factory.createOptionEntry("Other " +
                "treatment offered", "What other treatment/advice offered?");
        pathwaysToCare.addEntry(otherTreatOption);
        otherTreatOption.setSection(pathwaySec);
        otherTreatOption.setLabel("g");
        Option noneOption = factory.createOption("None", 0);
        otherTreatOption.addOption(noneOption);
        Option counselAdvOption = factory.createOption("Counselling/Advice/Support", 1);
        otherTreatOption.addOption(counselAdvOption);
        Option visitsOption = factory.createOption("Regular Visits/ Assertive Outreach", 2);
        otherTreatOption.addOption(visitsOption);
        Option appointmOption = factory.createOption("Outpatients appointments", 3);
        otherTreatOption.addOption(appointmOption);
        Option religiousOption = factory.createOption("Religious intervention", 4);
        otherTreatOption.addOption(religiousOption);
        Option healthChecksOption = factory.createOption("Physical Health Checks", 5);
        otherTreatOption.addOption(healthChecksOption);
        Option sectionedOption = factory.createOption("Admission - Sectioned", 6);
        otherTreatOption.addOption(sectionedOption);
        Option voluntaryOption = factory.createOption("Admission - Voluntary", 7);
        otherTreatOption.addOption(voluntaryOption);

        return pathwaysToCare;
    }

}
