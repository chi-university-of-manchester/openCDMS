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

package org.psygrid.edie.control;

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class EPQv3Short extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "EPQv3Short",
                "Economic Patient Questionnaire v3 Short");

        createDocumentStatuses(factory, doc);

        //add section 1 (Service Receipt)
        addServiceReceiptSection(doc, factory, "A", false);

        //add section 2 (Employment and Income)
        addEmploymentSection(doc, factory, "B", false);

        //add section 3 (Education)
        addEducationSection(doc, factory, "C", false);

        //add section 4 (Criminal Justice Service)
        addCriminalJusticeServiceSection(doc, factory, "D", false);

        //add section 5 (Interviewer Comments)
        addInterviewerCommentsSection(doc, factory, "E", false);

        return doc;
    }

    public static void addEmploymentSection(Document doc, Factory factory,
    		String sec, boolean followUp){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");
        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");

        //Employment and Income section
        Section employmentSec = factory.createSection("Your employment section");
        doc.addSection(employmentSec);
        employmentSec.setDisplayText("Your Employment");
        SectionOccurrence employmentSecOcc = factory.createSectionOccurrence(
                "Your Employment section occurrence");
        employmentSec.addOccurrence(employmentSecOcc);
        employmentSecOcc.setLabel(sec);

        CompositeEntry q1 = factory.createComposite("QB1",
                "How have you been employed in the last 3 months?");
        doc.addEntry(q1);
        q1.setSection(employmentSec);
        q1.setLabel(sec+".1");
        q1.addRowLabel("Employee, full time (more than 30 hours/week)");
        q1.addRowLabel("Employee, part time (less than 30 hours/week)");
        q1.addRowLabel("Self-employed (paid work)");
        q1.addRowLabel("Government-supported employment training");
        q1.addRowLabel("Non-government supported employment training or education");
        q1.addRowLabel("Employee on sick leave because of mental health problems");
        q1.addRowLabel("Employee on sick leave becuase of other health reasons");
        q1.addRowLabel("Not in paid employment because of mental health problems");
        q1.addRowLabel("Not in paid employment due to retirement");
        q1.addRowLabel("Not in paid employment for other reasons");
        q1.addRowLabel("Unpaid employment for a business owned by self, friend or relative");

        TextEntry q1Label = factory.createTextEntry(
                "QB1a", "Employment Status");
        q1.addEntry(q1Label);
        q1Label.setSection(employmentSec);

        OptionEntry q1Apply = factory.createOptionEntry(
                "QB1b", "Applicable");
        q1.addEntry(q1Apply);
        q1Apply.addOption(factory.createOption("Yes", 1));
        q1Apply.addOption(factory.createOption("No", 2));
        q1Apply.setSection(employmentSec);

        NumericEntry q1Freq = factory.createNumericEntry("QB1c",
                "Number of weeks spent in the last 3 months");
        q1.addEntry(q1Freq);
        q1Freq.setSection(employmentSec);
        q1Freq.setDefaultValue(new Double(0.0));
        q1Freq.addUnit(UnitWrapper.instance().getUnit("weeks"));

        OptionEntry q1d = factory.createOptionEntry(
                "QB1d",
                "Which category best describes your employment now?");
        doc.addEntry(q1d);
        q1d.setSection(employmentSec);
        q1d.addOption(factory.createOption("Employee, full time (more than 30 hours/week)", 1));
        q1d.addOption(factory.createOption("Employee, part time (less than 30 hours/week)", 2));
        q1d.addOption(factory.createOption("Self-employed (paid work)", 3));
        q1d.addOption(factory.createOption("Government-supported employment training", 4));
        q1d.addOption(factory.createOption("Non-government supported employment training or education", 5));
        q1d.addOption(factory.createOption("Employee on sick leave because of mental health problems", 6));
        q1d.addOption(factory.createOption("Employee on sick leave because of other health reasons", 7));
        q1d.addOption(factory.createOption("Not in paid employment because of mental health problems", 8));
        q1d.addOption(factory.createOption("Not in paid employment due to retirement", 9));
        q1d.addOption(factory.createOption("Not in paid employment for other reasons", 10));
        q1d.addOption(factory.createOption("Unpaid employment for a business owned by self, friend or relative", 11));



        OptionEntry q7 = factory.createOptionEntry("QB2", "Have you ever been employed?");
        doc.addEntry(q7);
        q7.setSection(employmentSec);
        q7.setLabel(sec+".2");
        Option q7Yes = factory.createOption("Yes (ask B.3)", 1);
        q7.addOption(q7Yes);
        Option q7No = factory.createOption("No (Go to B.4)", 2);
        q7.addOption(q7No);

        LongTextEntry q5 = factory.createLongTextEntry(
                "QB3",
                "What was your main job in the last 3 months or most " +
                        "recent period of paid work?");
        doc.addEntry(q5);
        q5.setSection(employmentSec);
        q5.setEntryStatus(EntryStatus.DISABLED);
        q5.setLabel(sec+".3");
        createOptionDependent(factory, q7Yes, q5);

        CompositeEntry q19 = factory.createComposite("QB4",
                "Which benefits do you receive?");
        doc.addEntry(q19);
        q19.setSection(employmentSec);
        q19.setLabel(sec+".4");

        OptionEntry q19a = factory.createOptionEntry("QB4a",
                "Benefits currently receiving (add all that are applicable)");
        q19.addEntry(q19a);
        q19a.setSection(employmentSec);
        q19a.setOptionCodesDisplayed(true);
        q19a.addOption(factory.createOption("Child Benefit", 1));
        q19a.addOption(factory.createOption("Guardian's Allowance", 2));
        q19a.addOption(factory.createOption("Invalid Care Allowance", 3));
        q19a.addOption(factory.createOption("Pension (of any kind, i.e. retirement, widow's, etc)", 4));
        q19a.addOption(factory.createOption("Severe Disability Allowance", 5));
        q19a.addOption(factory.createOption("Disability Working Allowance", 6));
        q19a.addOption(factory.createOption("Job Seekers' Allowance", 7));
        q19a.addOption(factory.createOption("Income Support", 8));
        q19a.addOption(factory.createOption("Incapacity Benefit", 9));
        q19a.addOption(factory.createOption("Statutory Sick Pay", 10));
        q19a.addOption(factory.createOption("Industrial Injury Disablement " +
                "Benefit", 11));
        q19a.addOption(factory.createOption("Maternity Allowance", 12));
        q19a.addOption(factory.createOption("Working Families' Tax Credit", 13));
        q19a.addOption(factory.createOption("Disabled Persons' Tax Credit", 14));
        q19a.addOption(factory.createOption("Disability Living Allowance", 15));
    }

    public static void addServiceReceiptSection(Document doc, Factory factory,
    		String sec, boolean followUp){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");

        //Service Receipt section
        Section serviceSec = factory.createSection("Service Receipt section");
        doc.addSection(serviceSec);
        serviceSec.setDisplayText("Service Receipt");
        SectionOccurrence serviceSecOcc = factory
                .createSectionOccurrence("Service Receipt section occurrence");
        serviceSec.addOccurrence(serviceSecOcc);
        serviceSecOcc.setLabel(sec);

        CompositeEntry inpatient = factory.createComposite(
                "Use of inpatient hospital services",
                "Have you used any of these inpatient hospital services during the " +
                        "last three months?");
        doc.addEntry(inpatient);
        inpatient.setSection(serviceSec);
        inpatient.setLabel(sec+".1");

        OptionEntry inpatientService = factory.createOptionEntry(
                "Inpatient service option", "Service");
        inpatient.addEntry(inpatientService);
        inpatientService.setSection(serviceSec);
        inpatientService.addOption(factory.createOption("Acute psychiatric ward",
                "Acute psychiatric ward", 1));
        inpatientService.addOption(factory.createOption("Psychiatric " +
                "rehabilitation ward", "Psychiatric rehabilitation ward", 2));
        inpatientService.addOption(factory.createOption("Long-stay psychiatric ward",
                "Long-stay psychiatric ward", 3));
        inpatientService.addOption(factory.createOption("Emergency / crisis centre",
                "Emergency / crisis centre", 4));
        inpatientService.addOption(factory.createOption("General medical ward",
                "General medical ward", 5));
        inpatientService.addOption(factory.createOption("Drug treatment ward", "Drug treatment ward", 6));
        inpatientService.addOption(factory.createOption("Alcohol tretament ward",
        "Alcohol treatment ward", 7));
        Option inpatientServiceOther = factory.createOption("Other", "Other", 8);
        inpatientServiceOther.setTextEntryAllowed(true);
        inpatientService.addOption(inpatientServiceOther);

        TextEntry hospitalName = factory.createTextEntry("Hospital Name",
        "Name of Hospital");
        inpatient.addEntry(hospitalName);
        hospitalName.setSection(serviceSec);

        NumericEntry admissions = factory.createNumericEntry("Admissions",
                "Total number of admissions (during last 3 months)");
        inpatient.addEntry(admissions);
        admissions.setSection(serviceSec);
        admissions.setDefaultValue(0.0);
        admissions.addValidationRule(positive);

        NumericEntry inpatientDays = factory.createNumericEntry("Total number " +
                "of inpatient days",
                "Total number of inpatient days (during last 3 months)");
        inpatient.addEntry(inpatientDays);
        inpatientDays.setSection(serviceSec);
        inpatientDays.setDefaultValue(0.0);
        inpatientDays.addValidationRule(positive);

        CompositeEntry outpatient1 = factory.createComposite("Use of outpatient " +
                "hospital services", "Have you used any hospital outpatient or day services during " +
                "the last three months?");
        doc.addEntry(outpatient1);
        outpatient1.setSection(serviceSec);
        outpatient1.setLabel(sec+".2");

        OptionEntry outpatientService1 = factory.createOptionEntry("Outpatient " +
                "service option", "Service");
        outpatient1.addEntry(outpatientService1);
        outpatientService1.setSection(serviceSec);
        outpatientService1.addOption(factory.createOption(
                "Psychiatric",
                "Psychiatric", 1));
        outpatientService1.addOption(factory.createOption(
                "Hospital alcohol service",
                "Hospital alcohol service", 2));
        outpatientService1.addOption(factory.createOption(
                "Hospital substance use service",
                "Hospital substance use service", 3));
        Option nonPsych = factory.createOption(
                "Non-psychiatric",
                "Non-psychiatric (specify)", 4);
        nonPsych.setTextEntryAllowed(true);
        outpatientService1.addOption(nonPsych);
        outpatientService1.addOption(factory.createOption(
                "Accident and Emergency",
                "Accident and Emergency", 5));
        outpatientService1.addOption(factory.createOption(
                "Day hospital",
                "Day hospital", 6));
        Option outpatientServiceOther = factory.createOption("Other", "Other", 7);
        outpatientServiceOther.setTextEntryAllowed(true);
        outpatientService1.addOption(outpatientServiceOther);

        TextEntry serviceName = factory.createTextEntry("Service Name",
        "Name of Service");
        outpatient1.addEntry(serviceName);
        serviceName.setSection(serviceSec);

        NumericEntry unitsReceived1 = factory.createNumericEntry("Number of outpatient attendances",
                "Total number of outpatient visits made (during last 3 months)");
        outpatient1.addEntry(unitsReceived1);
        unitsReceived1.setSection(serviceSec);
        unitsReceived1.setDefaultValue(0.0);
        unitsReceived1.addValidationRule(positive);

        NumericEntry unitsReceived2 = factory.createNumericEntry("Number of day attendances",
                "Total number of day attendances (during last 3 months)");
        outpatient1.addEntry(unitsReceived2);
        unitsReceived2.setSection(serviceSec);
        unitsReceived2.setDefaultValue(0.0);
        unitsReceived2.addValidationRule(positive);

        CompositeEntry communityServices = factory.createComposite(
                "Community-based day services", "Have you had any other primary and community-care " +
                "contacts during the last three months?");
        doc.addEntry(communityServices);
        communityServices.setSection(serviceSec);
        communityServices.setLabel(sec+".3");

        OptionEntry communityService = factory.createOptionEntry("Community Day " +
                "Services Option", "Type of Contact");
        communityServices.addEntry(communityService);
        communityService.setSection(serviceSec);
        communityService.addOption(factory.createOption("GP, surgery visit ",
        		"GP, surgery visit", 1));
        communityService.addOption(factory.createOption("GP, home visit ",
		"GP, home visit", 2));
        communityService.addOption(factory.createOption("Psychiatrist ",
		"Psychiatrist", 3));
        communityService.addOption(factory.createOption("Psychologist",
                "Psychologist", 4));
        communityService.addOption(factory.createOption("Alcohol treatment or rehab",
                "Alcohol treatment or rehabilitation service", 5));
        communityService.addOption(factory.createOption("Drug treatment or rehab",
                "Drug treatment or rehabilitation service", 6));
        communityService.addOption(factory.createOption("District Nurse",
                "District Nurse", 7));
        communityService.addOption(factory.createOption("Community psychiatric nurse",
        "Community psychiatric nurse / case manager", 8));
        communityService.addOption(factory.createOption("Social worker",
        "Social worker", 9));
        communityService.addOption(factory.createOption("Occupational therapist",
        "Occupational therapist", 10));
        communityService.addOption(factory.createOption("Voluntary counsellor",
        "Voluntary counsellor", 11));
        communityService.addOption(factory.createOption("Home help / care worker",
        "Home help / care worker", 12));
        Option communityServiceOther = factory.createOption("Other", "Other", 13);
        communityServiceOther.setTextEntryAllowed(true);
        communityService.addOption(communityServiceOther);

        NumericEntry numberAttendance = factory.createNumericEntry("Number of " +
                "attendances", "Total number of contacts");
        communityServices.addEntry(numberAttendance);
        numberAttendance.setSection(serviceSec);
        numberAttendance.setDefaultValue(0.0);
        numberAttendance.addValidationRule(positive);

        NumericEntry durationAttendance = factory.createNumericEntry("Average " +
                "duration of attendance", "Average time per contact");
        communityServices.addEntry(durationAttendance);
        durationAttendance.setSection(serviceSec);
        durationAttendance.addValidationRule(positive);
        durationAttendance.setDefaultValue(0.0);
        durationAttendance.addUnit(UnitWrapper.instance().getUnit("hours"));
    }


    public static void addCriminalJusticeServiceSection(Document doc, Factory factory,
    		String sec, boolean followUp){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");


        //Criminal justice section
        Section serviceSec = factory.createSection("Criminal Justice Service section");
        doc.addSection(serviceSec);
        serviceSec.setDisplayText("Criminal Justice Service");
        SectionOccurrence serviceSecOcc = factory
                .createSectionOccurrence("Criminal Justice Service section occurrence");
        serviceSec.addOccurrence(serviceSecOcc);
        serviceSecOcc.setLabel(sec);

        OptionEntry contactWithPolice = factory.createOptionEntry(
                "Been in contact with the just services",
                "Over the last 6 months, have you been in contact with the " +
                        "criminal justice services?");
        doc.addEntry(contactWithPolice);
        contactWithPolice.setSection(serviceSec);
        contactWithPolice.setLabel(sec+".1");
        contactWithPolice.addOption(factory.createOption("No", "No", 1));
        Option contactWithPoliceYes = factory.createOption("Yes", "Yes", 2);
        contactWithPolice.addOption(contactWithPoliceYes);

        NarrativeEntry contactPoliceYes = factory.createNarrativeEntry(
                "Contact with police yes",
                "If yes:");
        doc.addEntry(contactPoliceYes);
        contactPoliceYes.setSection(serviceSec);

        NumericEntry numberContactsPolice = factory.createNumericEntry(
                "Number of contacts with police",
                "How many contacts with the police during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(numberContactsPolice);
        numberContactsPolice.setSection(serviceSec);
        numberContactsPolice.setLabel(sec+".2a");
        numberContactsPolice.setDescription(
                "Note: contact = interview or stay of some hours, but not overnight");
        numberContactsPolice.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, numberContactsPolice);

        NumericEntry numberContactsProbation = factory.createNumericEntry(
                "Number of contacts with probation",
                "How many contacts with a probation officer during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(numberContactsProbation);
        numberContactsProbation.setSection(serviceSec);
        numberContactsProbation.setLabel(sec+".2b");
        numberContactsProbation.setDescription(
                "Note: contact = interview or stay of some hours, but not overnight");
        numberContactsProbation.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, numberContactsProbation);

        NumericEntry nightsPrison = factory.createNumericEntry(
                "Nights spent in prison",
                "How many nights spent in a police cell or prison during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(nightsPrison);
        nightsPrison.setSection(serviceSec);
        nightsPrison.setLabel(sec+".3");
        nightsPrison.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, nightsPrison);

        NumericEntry psychAssessments = factory.createNumericEntry(
                "Number of psychiatric assessments",
                "How many psychiatric assessments whilst in custody during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(psychAssessments);
        psychAssessments.setSection(serviceSec);
        psychAssessments.addValidationRule(positive);
        psychAssessments.setLabel(sec+".4");
        createOptionDependent(factory, contactWithPoliceYes, psychAssessments);

        NumericEntry criminalCourt = factory.createNumericEntry(
                "Number of criminal court appearances",
                "How many criminal court appearances during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(criminalCourt);
        criminalCourt.setSection(serviceSec);
        criminalCourt.addValidationRule(positive);
        criminalCourt.setLabel(sec+".5a");
        createOptionDependent(factory, contactWithPoliceYes, criminalCourt);

        NumericEntry civilCourt = factory.createNumericEntry(
                "Number of civil court appearances",
                "How many civil court appearances during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(civilCourt);
        civilCourt.setSection(serviceSec);
        civilCourt.addValidationRule(positive);
        civilCourt.setLabel(sec+".5b");
        createOptionDependent(factory, contactWithPoliceYes, civilCourt);

        OptionEntry convictions = factory.createOptionEntry(
                "Number of convictions",
                "During the last 6 months, have you been convicted for any offences?",
                EntryStatus.DISABLED);
        doc.addEntry(convictions);
        convictions.setSection(serviceSec);
        convictions.setLabel(sec+".6");
        createOptionDependent(factory, contactWithPoliceYes, convictions);
        convictions.addOption(factory.createOption("No", "No", 1));
        Option convictionsYes = factory.createOption("Yes", "Yes", 2);
        convictions.addOption(convictionsYes);

        CompositeEntry offenceType = factory.createComposite(
                "Offence Type", "What offences have you been convicted of during the last 6 months?");
        doc.addEntry(offenceType);
        offenceType.setSection(serviceSec);
        offenceType.setLabel(sec+".7");
        offenceType.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, convictionsYes, offenceType);

        OptionEntry offenceCategory = factory.createOptionEntry("Category of offence",
                "Category of offence");
        offenceType.addEntry(offenceCategory);
        offenceCategory.setSection(serviceSec);
        offenceCategory.addOption(factory.createOption("Homocide/Manslaughter",
        		"Homocide/Manslaughter", 1));
        offenceCategory.addOption(factory.createOption("Serious assault leading to major injury",
		"Serious assault leading to major injury", 2));
        offenceCategory.addOption(factory.createOption("Less serious assault leading to minor or no injury",
		"Less serious assault leading to minor or no injury", 3));
        offenceCategory.addOption(factory.createOption("Sexual offences",
		"Sexual offences", 11));
        offenceCategory.addOption(factory.createOption("Robbery",
		"Robbery", 4));
        offenceCategory.addOption(factory.createOption("Burlgary",
		"Burlgary", 5));
        offenceCategory.addOption(factory.createOption("Theft and handling stolen goods",
		"Theft and handling stolen goods", 6));
        offenceCategory.addOption(factory.createOption("Fraud and forgery",
		"Fraud and forgery", 7));
        offenceCategory.addOption(factory.createOption("Criminal damage",
		"Criminal damage", 8));
        offenceCategory.addOption(factory.createOption("Drug offences",
		"Drug offences", 9));
        Option offenceCategoryOther = factory.createOption("Other", "Other", 10);
        offenceCategoryOther.setTextEntryAllowed(true);
        offenceCategory.addOption(offenceCategoryOther);

        OptionEntry convictedOfOffences = factory.createOptionEntry("Convicted of offence",
                "Convicted");
        offenceType.addEntry(convictedOfOffences);
        convictedOfOffences.setSection(serviceSec);
        convictedOfOffences.addOption(factory.createOption("No", 1));
        convictedOfOffences.addOption(factory.createOption("Yes", 2));

        TextEntry clientOffenceName = factory.createTextEntry("Client offence name",
        		"Name of offence (as given by respondent)");
        offenceType.addEntry(clientOffenceName);
        clientOffenceName.setSection(serviceSec);
    }

    public static void addInterviewerCommentsSection(Document doc, Factory factory,
    		String sec, boolean followup){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");


        //Interviewer Comments section
        Section interviewerCommentsSec = factory.createSection(
                "Interviewer Comments section");
        doc.addSection(interviewerCommentsSec);
        interviewerCommentsSec.setDisplayText("Interviewer Comments");
        SectionOccurrence interviewerCommentsSecOcc = factory.createSectionOccurrence(
                "Interviewer Comments section occurrence");
        interviewerCommentsSec.addOccurrence(interviewerCommentsSecOcc);
        interviewerCommentsSecOcc.setLabel(sec);

        NarrativeEntry note = factory.createNarrativeEntry(
                "Interviewer Comments section note",
                "Before filing this questionnaire or proceeding to the next interview " +
                        ", please complete the following section while you impressions of the" +
                        " patient's responses are still fresh in your memory");
        doc.addEntry(note);
        note.setSection(interviewerCommentsSec);

        NumericEntry interviewDuration = factory.createNumericEntry("Interview Duration",
                "How long did the interview take?");
        doc.addEntry(interviewDuration);
        interviewDuration.setSection(interviewerCommentsSec);
        interviewDuration.addValidationRule(positive);
        interviewDuration.addUnit(UnitWrapper.instance().getUnit("mins"));

        OptionEntry reliability = factory.createOptionEntry("Reliability",
                "How reliable or unreliable do you think the patient's responses were?");
        doc.addEntry(reliability);
        reliability.setSection(interviewerCommentsSec);
        reliability.addOption(factory.createOption("Very reliable", 1));
        reliability.addOption(factory.createOption("Generally reliable", 2));
        reliability.addOption(factory.createOption("Generally unreliable", 3));
        reliability.addOption(factory.createOption("Very unreliable", 4));

        LongTextEntry comments = factory.createLongTextEntry("Other comments", "Any other comments?");
        doc.addEntry(comments);
        comments.setSection(interviewerCommentsSec);
    }


    public static void addEducationSection(Document doc, Factory factory,
    		String sec, boolean followup) {

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");
        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");

        //Education and Training section
        Section educationSec = factory.createSection("Education and Training section");
        doc.addSection(educationSec);
        educationSec.setDisplayText("Education and Training");
        SectionOccurrence educationSecOcc = factory.createSectionOccurrence(
                "Education and Training section occurrence");
        educationSec.addOccurrence(educationSecOcc);
        educationSecOcc.setLabel(sec);

        OptionEntry qualifications = factory.createOptionEntry(
                "Qualifications connected with work",
                "Do you have any qualifications from school, college or " +
                        "university, connected with work or from government schemes?");
        doc.addEntry(qualifications);
        qualifications.setSection(educationSec);
        qualifications.setLabel(sec+".1");
        Option qualificationsYes = factory.createOption("Yes", "Yes (Go to C2)", 1);
        qualifications.addOption(qualificationsYes);
        qualifications.addOption(factory.createOption("No", "No (Go to C3)", 2));

        LongTextEntry qualificationsHighest = factory.createLongTextEntry(
                "Highest qualification",
                "What is the highest qualification that you have?",
                EntryStatus.DISABLED);
        doc.addEntry(qualificationsHighest);
        qualificationsHighest.setSection(educationSec);
        qualificationsHighest.setLabel(sec+".2");
        createOptionDependent(factory, qualificationsYes, qualificationsHighest);

        LongTextEntry qualificationsOther = factory.createLongTextEntry(
                "Other qualifications",
                "Which other qualifications do you have?",
                EntryStatus.DISABLED);
        doc.addEntry(qualificationsOther);
        qualificationsOther.setSection(educationSec);
        createOptionDependent(factory, qualificationsYes, qualificationsOther);


        OptionEntry q4 = factory.createOptionEntry("Studying " +
                "for qualifications at the moment",
                "Are you studying for any qualifications at the moment?");
        doc.addEntry(q4);
        q4.setSection(educationSec);
        q4.setLabel(sec+".3");
        Option q4Yes = factory.createOption("Yes", "Yes (Go to C4)", 1);
        q4.addOption(q4Yes);
        Option q4No = factory.createOption("No",
                "No (Go to Section D)", 0);
        q4.addOption(q4No);
        q4.setOptionCodesDisplayed(true);

        CompositeEntry q5 = factory.createComposite(
                "QC4", "What qualifications are you studying for at the moment?");
        doc.addEntry(q5);
        q5.setLabel(sec+".4");
        q5.setSection(educationSec);
        q5.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, q4Yes, q5);

        OptionEntry q5a = factory.createOptionEntry("QC4a",
                "Qualifications currently studying for");
        q5.addEntry(q5a);
        q5a.addOption(factory.createOption("Degree level qualification including graduate membership of a " +
                "professional institute or PGCE or higher (include undergraduate " +
                "and postgraduate degrees)", 1));
        q5a.addOption(factory.createOption("Diploma in higher education", 2));
        q5a.addOption(factory.createOption("HNC/HND", 3));
        q5a.addOption(factory.createOption("ONC/OND", 4));
        q5a.addOption(factory.createOption("BTEC, BEC or TEC", 5));
        q5a.addOption(factory.createOption("SCOTVEC, SCOTEC or SCOTBEC", 6));
        q5a.addOption(factory.createOption("Teaching qualification excluding PGCE", 7));
        q5a.addOption(factory.createOption("Nursing or other " +
                "medical qualification not yet mentioned", 8));
        q5a.addOption(factory.createOption("Other higher education " +
                "qualification below degree level", 9));
        q5a.addOption(factory.createOption("A-level or equivalent", 10));
        q5a.addOption(factory.createOption("SCE highers", 11));
        q5a.addOption(factory.createOption("NVQ/SVQ", 12));
        q5a.addOption(factory.createOption("GNVQ/GSVQ", 13));
        q5a.addOption(factory.createOption("AS-level", 14));
        q5a.addOption(factory.createOption("Certificate of sixth year " +
                "studies (CSYS) or equivalent", 15));
        q5a.addOption(factory.createOption("O-Level or equivalent", 16));
        q5a.addOption(factory.createOption("SCE Standard or Ordinary (O) grade", 17));
        q5a.addOption(factory.createOption("GCSE", 18));
        q5a.addOption(factory.createOption("CSE", 19));
        q5a.addOption(factory.createOption("RSA", 20));
        q5a.addOption(factory.createOption("City and Guilds", 21));
        q5a.addOption(factory.createOption("YT certificate/YTP", 22));
        q5a.addOption(factory.createOption("Any other professional or vocational " +
                "qualification or foreign qualifications (e.g. apprenticeship)", 23));
        q5a.addOption(factory.createOption("Don't know", 24));
        q5a.setSection(educationSec);

        OptionEntry ftOrPt = factory.createOptionEntry("FT or PT", "Are you studying full time or part time");
        q5.addEntry(ftOrPt);
        ftOrPt.setSection(educationSec);
        ftOrPt.addOption(factory.createOption("F/T", 1));
        ftOrPt.addOption(factory.createOption("P/T", 2));


    }
}
