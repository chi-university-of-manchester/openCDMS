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

public class EisFamilyHistory extends AssessmentForm {

    public static Document createDocument(Factory factory){

        Document doc = factory.createDocument("EIS Family History", "Family History");

        createDocumentStatuses(factory, doc);

        ValidationRule positiveRule = ValidationRulesWrapper.instance().getRule("Positive");

        Section relativeSec = factory.createSection("Relative Section");
        doc.addSection(relativeSec);
        relativeSec.setDisplayText("Relative");
        SectionOccurrence relativeOcc = factory.createSectionOccurrence("Relative Section Occurrence");
        relativeSec.addOccurrence(relativeOcc);
        relativeOcc.setMultipleAllowed(true);

        OptionEntry relativeOption = factory.createOptionEntry("Relative option",
                "Relative");
        doc.addEntry(relativeOption);
        relativeOption.setSection(relativeSec);
        relativeOption.addOption(factory.createOption("Mother", 1));
        relativeOption.addOption(factory.createOption("Father", 2));
        relativeOption.addOption(factory.createOption("Brother or sister", 3));
        relativeOption.addOption(factory.createOption("Grandparent (mother's side)", 4));
        relativeOption.addOption(factory.createOption("Grandparent (father's side)", 5));
        relativeOption.addOption(factory.createOption("Aunt or uncle (mother's side)", 6));
        relativeOption.addOption(factory.createOption("Aunt or uncle (father's side)", 7));
        relativeOption.addOption(factory.createOption("Cousin (mother's side)", 8));
        relativeOption.addOption(factory.createOption("Cousin (father's side)", 9));

        OptionEntry natureIllnOption = factory.createOptionEntry("Presumed Nature " +
                "of Illness option", "Presumed Nature of Illness");
        doc.addEntry(natureIllnOption);
        natureIllnOption.setSection(relativeSec);
        Option illnessOption = factory.createOption("Illness, diagnosis unknown " +
                "option", "Illness, diagnosis unknown", 1);
        natureIllnOption.addOption(illnessOption);
        Option schizOption = factory.createOption("Schizophrenia option",
                "Schizophrenia", 2);
        natureIllnOption.addOption(schizOption);
        Option biPolarOption = factory.createOption("Bi-polar option",
                "Bi-polar", 3);
        natureIllnOption.addOption(biPolarOption);
        Option depressiveOption = factory.createOption("Depressive illness " +
                "option", "Depressive illness", 4);
        natureIllnOption.addOption(depressiveOption);
        Option psychOption = factory.createOption("Unspecified psychosis option",
                "Unspecified psychosis", 5);
        natureIllnOption.addOption(psychOption);
        Option otherOption = factory.createOption("Other option",
                "Other (specify)", 6);
        natureIllnOption.addOption(otherOption);
        otherOption.setTextEntryAllowed(true);

        OptionDependent illnessOptDep = factory.createOptionDependent();
        illnessOptDep.setEntryStatus(EntryStatus.MANDATORY);
        illnessOption.addOptionDependent(illnessOptDep);
        OptionDependent schizOptDep = factory.createOptionDependent();
        schizOptDep.setEntryStatus(EntryStatus.MANDATORY);
        schizOption.addOptionDependent(schizOptDep);
        OptionDependent biPolarOptDep = factory.createOptionDependent();
        biPolarOptDep.setEntryStatus(EntryStatus.MANDATORY);
        biPolarOption.addOptionDependent(biPolarOptDep);
        OptionDependent depressiveOptDep = factory.createOptionDependent();
        depressiveOptDep.setEntryStatus(EntryStatus.MANDATORY);
        depressiveOption.addOptionDependent(depressiveOptDep);
        OptionDependent psychOptDep = factory.createOptionDependent();
        psychOptDep.setEntryStatus(EntryStatus.MANDATORY);
        psychOption.addOptionDependent(psychOptDep);
        OptionDependent otherOptDep = factory.createOptionDependent();
        otherOptDep.setEntryStatus(EntryStatus.MANDATORY);
        otherOption.addOptionDependent(otherOptDep);

        LongTextEntry detailsText = factory.createLongTextEntry("Details", "Details",
                EntryStatus.DISABLED);
        doc.addEntry(detailsText);
        detailsText.setSection(relativeSec);
        illnessOptDep.setDependentEntry(detailsText);
        schizOptDep.setDependentEntry(detailsText);
        biPolarOptDep.setDependentEntry(detailsText);
        depressiveOptDep.setDependentEntry(detailsText);
        psychOptDep.setDependentEntry(detailsText);
        otherOptDep.setDependentEntry(detailsText);

        OptionEntry contactOption = factory.createOptionEntry("Contact with MHS " +
                "Option", "Contact with Mental Health Services");
        doc.addEntry(contactOption);
        contactOption.setSection(relativeSec);
        contactOption.addOption(factory.createOption("Never received care option",
                "Never received care", 0));
        Option prevCareOption = factory.createOption("Previously received care " +
                "option", "Not currently receiving care, but has previously " +
                "received care", 1);
        contactOption.addOption(prevCareOption);
        Option receivCareOption = factory.createOption("Currently receiving " +
                "care option", "Currently receiving care", 2);
        contactOption.addOption(receivCareOption);

        OptionDependent prevCareOptDep = factory.createOptionDependent();
        prevCareOptDep.setEntryStatus(EntryStatus.MANDATORY);
        prevCareOption.addOptionDependent(prevCareOptDep);
        OptionDependent receivCareOptDep = factory.createOptionDependent();
        receivCareOptDep.setEntryStatus(EntryStatus.MANDATORY);
        receivCareOption.addOptionDependent(receivCareOptDep);

        TextEntry teamText = factory.createTextEntry("Team seen text entry",
                "Specify team seen, if known", EntryStatus.DISABLED);
        doc.addEntry(teamText);
        teamText.setSection(relativeSec);
        prevCareOptDep.setDependentEntry(teamText);
        receivCareOptDep.setDependentEntry(teamText);

        OptionEntry typeCareOption = factory.createOptionEntry("Type of Care " +
                "Received Option", "Type of Care Received");
        doc.addEntry(typeCareOption);
        typeCareOption.setSection(relativeSec);
        typeCareOption.addOption(factory.createOption("None option", "None", 0));
        typeCareOption.addOption(factory.createOption("Outpatients appointments " +
                "only option", "Outpatients appointments only", 1));
        typeCareOption.addOption(factory.createOption("Assertive outreach/ Home " +
                "treatment option", "Assertive outreach/ Home treatment", 2));
        typeCareOption.addOption(factory.createOption("Inpatient", "Inpatient", 3));

        OptionEntry admissionEntry = factory.createOptionEntry("Admissions " +
                "option entry", "Admissions");
        doc.addEntry(admissionEntry);
        admissionEntry.setSection(relativeSec);
        admissionEntry.addOption(factory.createOption("No option", "No", 0));
        Option yesOption = factory.createOption("Yes option", "Yes", 1);
        admissionEntry.addOption(yesOption);

        NumericEntry timesHosp = factory.createNumericEntry(
                "No of times hospitalised entry",
                "No of times hospitalised",
                EntryStatus.DISABLED);
        doc.addEntry(timesHosp);
        timesHosp.setSection(relativeSec);
        timesHosp.addValidationRule(positiveRule);
        createOptionDependent(factory, yesOption, timesHosp);

        NumericEntry yearAdmission = factory.createNumericEntry(
                "Year of last admission entry",
                "Year of last admission",
                EntryStatus.DISABLED);
        doc.addEntry(yearAdmission);
        yearAdmission.setSection(relativeSec);
        createOptionDependent(factory, yesOption, yearAdmission);

        TextEntry reasonAdmission = factory.createTextEntry(
                "Reason for last admission entry",
                "Reason for last admission",
                EntryStatus.DISABLED);

        doc.addEntry(reasonAdmission);
        reasonAdmission.setSection(relativeSec);
        createOptionDependent(factory, yesOption, reasonAdmission);

        OptionEntry medicationEntry = factory.createOptionEntry("Medication " +
                "entry", "Medication");
        doc.addEntry(medicationEntry);
        medicationEntry.setSection(relativeSec);
        medicationEntry.addOption(factory.createOption("No option", "No", 0));
        medicationEntry.addOption(factory.createOption("Yes, unknown option",
                "Yes, unknown", 1));
        medicationEntry.addOption(factory.createOption("Anti-psychotic option",
                "Anti-psychotic", 2));
        medicationEntry.addOption(factory.createOption("Other option", "Other",
                3));

        OptionEntry awareOption = factory.createOptionEntry("Client aware of " +
                "relative illness option", "Was the client aware of this " +
                "relative's illness?");
        doc.addEntry(awareOption);
        awareOption.setSection(relativeSec);
        awareOption.addOption(factory.createOption("Unaware " +
                "illness option", "Unaware of relative's illness", 0));
        awareOption.addOption(factory.createOption("Aware of illness but " +
                "little contact option", "Aware of relative's illness but had " +
                "little contact with them ", 1));
        awareOption.addOption(factory.createOption("Aware of illness and some " +
                "contact option", "Aware of relative's illness and had some " +
                "contact with them during their illness", 2));
        awareOption.addOption(factory.createOption("Regular contact option",
                "Had regular contact with their relative during their illness",
                3));
        awareOption.addOption(factory.createOption("Visited on ward option",
                "Visited their relative on a Mental Health ward", 4));

        return doc;
    }
}
