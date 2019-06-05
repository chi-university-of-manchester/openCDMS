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


package org.psygrid.command;

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class EPQv2 extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "EPQv2",
                "Economic Patient Questionnaire v2");

        createDocumentStatuses(factory, doc);

        Section srSec = factory.createSection("Service Receipt Section", "Service Receipt");
        doc.addSection(srSec);
        SectionOccurrence srSecOcc = factory.createSectionOccurrence("Service Receipt Sec Occ");
        srSec.addOccurrence(srSecOcc);

        OptionEntry qA1 = factory.createOptionEntry("Inpatient hospital services",
                "Have you used any inpatient hospital services during the last 18 months?");
        doc.addEntry(qA1);
        qA1.setSection(srSec);
        qA1.setLabel("A1");
        createOptions(factory, qA1, new String[]{"Yes", "No"});
        Option qA1Yes = qA1.getOption(0);

        NarrativeEntry qA1N = factory.createNarrativeEntry("A1 Narrative",
        		"If yes, the trial researcher should complete the appropriate psychiatric and non psychiatric hospital " +
        		"record forms from the patient's hospital records after the interview with the patient has ended. Please " +
        		"enter '0' if service has not been used. If information CANNOT be determined, enter 999");
        doc.addEntry(qA1N);
        qA1N.setSection(srSec);
        qA1N.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qA1Yes, qA1N);

        CompositeEntry inpatient = factory.createComposite(
                "Inpatient hospital services table",
                "Inpatient hospital services");
        doc.addEntry(inpatient);
        inpatient.setSection(srSec);
        inpatient.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qA1Yes, inpatient);

        OptionEntry inpatientService = factory.createOptionEntry(
                "Inpatient service option", "Service");
        inpatient.addEntry(inpatientService);
        inpatientService.setSection(srSec);
        inpatientService.addOption(factory.createOption("Acute psychiatric ward",
                "Acute psychiatric ward"));
        inpatientService.addOption(factory.createOption("Psychiatric " +
                "rehabilitation ward", "Psychiatric rehabilitation ward"));
        inpatientService.addOption(factory.createOption("Long-stay psychiatric ward",
                "Long-stay psychiatric ward"));
        inpatientService.addOption(factory.createOption("Emergency / crisis centre",
                "Emergency / crisis centre"));
        inpatientService.addOption(factory.createOption("General medical ward",
                "General medical ward"));
        inpatientService.addOption(factory.createOption("Alcohol treatment ward",
        		"Alcohol treatment ward"));
        inpatientService.addOption(factory.createOption("Drug treatment ward", "Drug treatment ward"));
        Option inpatientServiceOther = factory.createOption("Other", "Other (please specify)");
        inpatientServiceOther.setTextEntryAllowed(true);
        inpatientService.addOption(inpatientServiceOther);

        TextEntry hospitalName = factory.createTextEntry("Hospital Name",
        "Name of Hospital");
        inpatient.addEntry(hospitalName);
        hospitalName.setSection(srSec);

        IntegerEntry admissions = factory.createIntegerEntry("Admissions",
                "Total number of admissions (during last 18 months)");
        inpatient.addEntry(admissions);
        admissions.setSection(srSec);
        admissions.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        OptionEntry qA2 = factory.createOptionEntry("Hospital outpatient or day services",
                "Have you used any hospital outpatient or day services during the last 18 months?");
		doc.addEntry(qA2);
		qA2.setSection(srSec);
		qA2.setLabel("A2");
		createOptions(factory, qA2, new String[]{"Yes", "No"});
		Option qA2Yes = qA2.getOption(0);

		NarrativeEntry qA2N = factory.createNarrativeEntry("A2 Narrative",
				"If yes, the trial researcher should complete the appropriate psychiatric and non psychiatric " +
				"hospital record forms from the patient's hospital records after the interview with the patient has ended." +
				"Please enter '0' if service has not been used. If information CANNOT be determined, enter 999");
		doc.addEntry(qA2N);
		qA2N.setSection(srSec);
		qA2N.setEntryStatus(EntryStatus.DISABLED);
		createOptionDependent(factory, qA2Yes, qA2N);

        CompositeEntry outpatient1 = factory.createComposite("Hospital outpatient or day services",
                "Hospital outpatient or day services");
        doc.addEntry(outpatient1);
        outpatient1.setSection(srSec);
        outpatient1.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qA2Yes, outpatient1);

        OptionEntry outpatientService1 = factory.createOptionEntry("Outpatient " +
                "service option", "Service");
        outpatient1.addEntry(outpatientService1);
        outpatientService1.setSection(srSec);
        outpatientService1.addOption(factory.createOption(
                "Psychiatric",
                "Psychiatric"));
        outpatientService1.addOption(factory.createOption(
                "Hospital alcohol service",
                "Hospital alcohol service"));
        outpatientService1.addOption(factory.createOption(
                "Hospital substance use service",
                "Hospital substance use service"));
        Option nonPsych = factory.createOption(
                "Non-psychiatric",
                "Non-psychiatric (specify)");
        nonPsych.setTextEntryAllowed(true);
        outpatientService1.addOption(nonPsych);
        outpatientService1.addOption(factory.createOption(
                "Accident and Emergency",
                "Accident and Emergency"));
        outpatientService1.addOption(factory.createOption(
                "Day hospital",
                "Day hospital"));
        Option outpatientServiceOther = factory.createOption("Other", "Other (please specify)");
        outpatientServiceOther.setTextEntryAllowed(true);
        outpatientService1.addOption(outpatientServiceOther);

        TextEntry serviceName = factory.createTextEntry("Service Name",
        "Name of Service");
        outpatient1.addEntry(serviceName);
        serviceName.setSection(srSec);

        OptionEntry qA3 = factory.createOptionEntry("Community based services",
                "Have you used any community based services during the last 18 months?");
		doc.addEntry(qA3);
		qA3.setSection(srSec);
		qA3.setLabel("A3");
		createOptions(factory, qA3, new String[]{"Yes", "No"});
		Option qA3Yes = qA3.getOption(0);

		NarrativeEntry qA3N = factory.createNarrativeEntry("A3 Narrative",
				"Please enter '0' if service has not been used. If information CANNOT be determined, enter 999. " +
				"It may be helpful to ask the patient the average number of times they attend per week or month, " +
				"and then the number of weeks (or months) they have attended in the last 18 months.  Please note " +
				"how the number of attendances was calculated.");
		doc.addEntry(qA3N);
		qA3N.setSection(srSec);
		qA3N.setEntryStatus(EntryStatus.DISABLED);
		createOptionDependent(factory, qA3Yes, qA3N);

        CompositeEntry communityServices = factory.createComposite(
                "Community based services table", "Community based services");
        doc.addEntry(communityServices);
        communityServices.setSection(srSec);
        communityServices.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qA3Yes, communityServices);

        OptionEntry communityService = factory.createOptionEntry("Service", "Service");
        communityServices.addEntry(communityService);
        communityService.setSection(srSec);
        createOptions(factory, communityService,
        		new String[]{"Community mental health centre",
        					 "Day care centre",
        					 "Group therapy",
        					 "Sheltered workshop",
        					 "Specialist education",
        					 "Other (specify)"});
        communityService.getOption(5).setTextEntryAllowed(true);

        IntegerEntry numberAttendance = factory.createIntegerEntry(
                "Number of attendances", "Number of attendances");
        communityServices.addEntry(numberAttendance);
        numberAttendance.setSection(srSec);
        numberAttendance.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        NumericEntry durationAttendance = factory.createNumericEntry(
                "Average length per attendance", "Average length per attendance");
        communityServices.addEntry(durationAttendance);
        durationAttendance.setSection(srSec);
        durationAttendance.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
        durationAttendance.addUnit(UnitWrapper.instance().getUnit("hrs"));

        LongTextEntry communityServicesCalc = factory.createLongTextEntry("Calculate Attendance?", "How were the attendances and attendance length calculated?");
        doc.addEntry(communityServicesCalc);
        communityServicesCalc.setSection(srSec);
        communityServicesCalc.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qA3Yes, communityServicesCalc);

        OptionEntry qA4 = factory.createOptionEntry(
                "Primary and community-care contacts",
                "Have you had any other primary and community-care contacts during the last 18 months?");
		doc.addEntry(qA4);
		qA4.setSection(srSec);
		qA4.setLabel("A4");
		createOptions(factory, qA4, new String[]{"Yes", "No"});
		Option qA4Yes = qA4.getOption(0);

		NarrativeEntry qA4N = factory.createNarrativeEntry("A4 Narrative",
				"Please enter '0' if service has not been used. If information CANNOT be determined, enter 999");
		doc.addEntry(qA4N);
		qA4N.setSection(srSec);
		qA4N.setEntryStatus(EntryStatus.DISABLED);
		createOptionDependent(factory, qA4Yes, qA4N);

        CompositeEntry primaryServices = factory.createComposite(
                "Primary and community-care contacts table", "Primary and community-care contacts");
        doc.addEntry(primaryServices);
        primaryServices.setSection(srSec);
        primaryServices.setEntryStatus(EntryStatus.DISABLED);
		createOptionDependent(factory, qA4Yes, primaryServices);

        OptionEntry typeOfContact = factory.createOptionEntry("Community Day " +
                "Services Option", "Type of Contact");
        primaryServices.addEntry(typeOfContact);
        typeOfContact.setSection(srSec);
        typeOfContact.addOption(factory.createOption("GP, surgery visit ",
        		"GP, surgery visit"));
        typeOfContact.addOption(factory.createOption("GP, home visit ",
				"GP, home visit"));
        typeOfContact.addOption(factory.createOption("Psychiatrist ",
				"Psychiatrist"));
        typeOfContact.addOption(factory.createOption("Psychologist",
                "Psychologist (excluding COMMAND therapists)"));
        typeOfContact.addOption(factory.createOption("Alcohol treatment or rehab",
                "Alcohol treatment or rehabilitation service"));
        typeOfContact.addOption(factory.createOption("Drug treatment or rehab",
                "Drug treatment or rehabilitation service"));
        typeOfContact.addOption(factory.createOption("District Nurse",
                "District Nurse"));
        typeOfContact.addOption(factory.createOption("Community psychiatric nurse",
        		"Community psychiatric nurse / case manager"));
        typeOfContact.addOption(factory.createOption("Social worker",
        		"Social worker"));
        typeOfContact.addOption(factory.createOption("Occupational therapist",
        		"Occupational therapist"));
        typeOfContact.addOption(factory.createOption("Voluntary counsellor",
        		"Voluntary counsellor"));
        typeOfContact.addOption(factory.createOption("Home help / care worker",
        		"Home help / care worker"));
        Option communityServiceOther = factory.createOption("Other", "Other (specify)");
        communityServiceOther.setTextEntryAllowed(true);
        typeOfContact.addOption(communityServiceOther);

        IntegerEntry numberContacts = factory.createIntegerEntry("Number of " +
                "contacts", "Total number of contacts");
        primaryServices.addEntry(numberContacts);
        numberContacts.setSection(srSec);
        numberContacts.setDescription("during last 18 months");
        numberContacts.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        NumericEntry avContactTime = factory.createNumericEntry("Average contact time",
                "Average contact time");
        primaryServices.addEntry(avContactTime);
        avContactTime.setSection(srSec);
        avContactTime.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
        avContactTime.addUnit(UnitWrapper.instance().getUnit("hrs"));

        LongTextEntry primaryServicesCalc = factory.createLongTextEntry("Calculate Attendance?", "How were the contacts and average contact time calculated?");
        doc.addEntry(primaryServicesCalc);
        primaryServicesCalc.setSection(srSec);
        primaryServicesCalc.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qA4Yes, primaryServicesCalc);

        Section lsSec = factory.createSection("Living Situation Section", "Living Situation");
        doc.addSection(lsSec);
        SectionOccurrence lsSecOcc = factory.createSectionOccurrence("Living Situation Sec Occ");
        lsSec.addOccurrence(lsSecOcc);

        OptionEntry qB1 = factory.createOptionEntry("Living situation today", "What is your living situation today?");
        doc.addEntry(qB1);
        qB1.setSection(lsSec);
        qB1.setLabel("B1");
        createOptions(factory, qB1, new String[]{"Living alone",
        										 "Living alone with children",
        										 "Living with spouse or partner",
        										 "Living with partner as a couple",
        										 "Living with parents",
        										 "Living with relatives other than parents or partner/spouse",
        										 "Living with others not related to you",
        										 "Not known"});

        OptionEntry qB2 = factory.createOptionEntry("Type of accommodation", "What type of accommodation do you live in?");
        doc.addEntry(qB2);
        qB2.setSection(lsSec);
        qB2.setLabel("B2");
        createOptions(factory, qB2, new String[]{"Owner occupied flat or house",
        										 "Flat or house rented from private landlord or company",
        										 "Flat or house rented from local authority/municipality or housing association/co-operative",
        										 "Community home (sheltered, residential or nursing home)",
        										 "Long stay hospital",
        										 "Homeless/roofless",
        										 "Other",
        										 "Not known"});
        Option qB2Dom1 = qB2.getOption(0);
        Option qB2Dom2 = qB2.getOption(1);
        Option qB2Dom3 = qB2.getOption(2);
        Option qB2Hosp1 = qB2.getOption(3);
        Option qB2Hosp2 = qB2.getOption(4);

        NarrativeEntry lsN1 = factory.createNarrativeEntry("Narrative",
        		"If the patient lives in domestic accommodation complete questions B3 to B6. " +
        		"If the patient lives in hospital or community accommodation complete questions B7 to B10.");
        doc.addEntry(lsN1);
        lsN1.setSection(lsSec);

        NarrativeEntry qB3 = factory.createNarrativeEntry("QB3 Narrative",
        		"If domestic accommodation (Owner occupied flat or house, rented flat or house) how many people live there?");
        doc.addEntry(qB3);
        qB3.setSection(lsSec);
        qB3.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qB2Dom1, qB3);
        createOptionDependent(factory, qB2Dom2, qB3);
        createOptionDependent(factory, qB2Dom3, qB3);

        IntegerEntry qB3a = factory.createIntegerEntry("Number of adults", "Number of adults (including the patient, over the age of 18)",
                EntryStatus.DISABLED);
        doc.addEntry(qB3a);
        qB3a.setSection(lsSec);
        qB3a.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
        createOptionDependent(factory, qB2Dom1, qB3a);
        createOptionDependent(factory, qB2Dom2, qB3a);
        createOptionDependent(factory, qB2Dom3, qB3a);

        IntegerEntry qB3b = factory.createIntegerEntry("Number of children", "Number of children (under the age of 18)",
                EntryStatus.DISABLED);
        doc.addEntry(qB3b);
        qB3b.setSection(lsSec);
        qB3b.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
        createOptionDependent(factory, qB2Dom1, qB3b);
        createOptionDependent(factory, qB2Dom2, qB3b);
        createOptionDependent(factory, qB2Dom3, qB3b);

        OptionEntry qB4 = factory.createOptionEntry("Live there all the time - domestic", "Do you live there all the time?",
                EntryStatus.DISABLED);
        doc.addEntry(qB4);
        qB4.setSection(lsSec);
        qB4.setLabel("B4");
        createOptions(factory, qB4, new String[]{"Yes", "No"}, new int[]{1,2});
        createOptionDependent(factory, qB2Dom1, qB4);
        createOptionDependent(factory, qB2Dom2, qB4);
        createOptionDependent(factory, qB2Dom3, qB4);

        OptionEntry qB5 = factory.createOptionEntry("Lived anywhere else - domestic",
                "Have you lived anywhere else in the last 18 months?",
                EntryStatus.DISABLED);
        doc.addEntry(qB5);
        qB5.setSection(lsSec);
        qB5.setLabel("B5");
        createOptions(factory, qB5, new String[]{"Yes", "No"}, new int[]{1,2});
        createOptionDependent(factory, qB2Dom1, qB5);
        createOptionDependent(factory, qB2Dom2, qB5);
        createOptionDependent(factory, qB2Dom3, qB5);

        CompositeEntry qB6 = factory.createComposite(
                "Other accommodation - domestic",
                "What other types of accommodation have you lived in during the last 18 months?");
        doc.addEntry(qB6);
        qB6.setSection(lsSec);
        qB6.setLabel("B6");
        qB6.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qB2Dom1, qB6);
        createOptionDependent(factory, qB2Dom2, qB6);
        createOptionDependent(factory, qB2Dom3, qB6);

        OptionEntry qB6Accom = factory.createOptionEntry("Accommodation", "Accommodation");
        qB6.addEntry(qB6Accom);
        qB6Accom.setSection(lsSec);
        createOptions(factory, qB6Accom,
        		new String[]{"Owner occupied flat or house",
        					 "Flat or house rented from private landlord or company",
        					 "Flat or house rented from local authority or housing association/co-operative",
        					 "Community home (sheltered, residential or nursing home)",
        					 "Long stay hospital",
        					 "Homeless/roofless",
        					 "Other",
        					 "Not known"});

        NumericEntry qB6Months = factory.createNumericEntry("Months lived there", "Months lived there");
        qB6.addEntry(qB6Months);
        qB6Months.setSection(lsSec);
        qB6Months.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
        qB6Months.addUnit(UnitWrapper.instance().getUnit("months"));

        TextEntry qB6Name = factory.createTextEntry("Name and address", "Name and address of previous hospital or community accommodation");
        qB6.addEntry(qB6Name);
        qB6Name.setSection(lsSec);

        LongTextEntry qB7 = factory.createLongTextEntry("Name and address of home",
                "If you live in hospital or community accommodation what is the name and address of the home?",
                EntryStatus.DISABLED);
        doc.addEntry(qB7);
        qB7.setSection(lsSec);
        qB7.setLabel("B7");
        createOptionDependent(factory, qB2Hosp1, qB7);
        createOptionDependent(factory, qB2Hosp2, qB7);

        OptionEntry qB8 = factory.createOptionEntry("Live there all the time - hospital", "Do you live there all the time?",
                EntryStatus.DISABLED);
        doc.addEntry(qB8);
        qB8.setSection(lsSec);
        qB8.setLabel("B8");
        createOptions(factory, qB8, new String[]{"Yes", "No"}, new int[]{1,2});
        createOptionDependent(factory, qB2Hosp1, qB8);
        createOptionDependent(factory, qB2Hosp2, qB8);

        OptionEntry qB9 = factory.createOptionEntry("Lived anywhere elase - hospital",
                "Have you lived anywhere else in the last 18 months?",
                EntryStatus.DISABLED);
        doc.addEntry(qB9);
        qB9.setSection(lsSec);
        qB9.setLabel("B9");
        createOptions(factory, qB9, new String[]{"Yes", "No"}, new int[]{1,2});
        createOptionDependent(factory, qB2Hosp1, qB9);
        createOptionDependent(factory, qB2Hosp2, qB9);

        CompositeEntry qB10 = factory.createComposite(
                "Other accommodation - hospital",
                "What other types of accommodation have you lived in during the last 18 months?");
        doc.addEntry(qB10);
        qB10.setSection(lsSec);
        qB10.setLabel("B10");
        qB10.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qB2Hosp1, qB10);
        createOptionDependent(factory, qB2Hosp2, qB10);

        OptionEntry qB10Accom = factory.createOptionEntry("Accommodation", "Accommodation");
        qB10.addEntry(qB10Accom);
        qB10Accom.setSection(lsSec);
        createOptions(factory, qB10Accom,
        		new String[]{"Owner occupied flat or house",
        					 "Flat or house rented from private landlord or company",
        					 "Flat or house rented from local authority or housing association/co-operative",
        					 "Community home (sheltered, residential or nursing home)",
        					 "Long stay hospital",
        					 "Homeless/roofless",
        					 "Other",
        					 "Not known"});

        NumericEntry qB10Months = factory.createNumericEntry("Months lived there", "Months lived there");
        qB10.addEntry(qB10Months);
        qB10Months.setSection(lsSec);
        qB10Months.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
        qB10Months.addUnit(UnitWrapper.instance().getUnit("months"));

        TextEntry qB10Name = factory.createTextEntry("Name and address", "Name and address of previous hospital or community accommodation");
        qB10.addEntry(qB10Name);
        qB10Name.setSection(lsSec);

        //Extra Costs section
        Section extraCostsSec = factory.createSection(
                "Extra Costs section", "Extra Costs");
        doc.addSection(extraCostsSec);
        SectionOccurrence extraCostsSecOcc = factory.createSectionOccurrence(
                "Extra Costs section occurrence");
        extraCostsSec.addOccurrence(extraCostsSecOcc);

        NarrativeEntry note = factory.createNarrativeEntry(
                "Spent on Care",
                "During the last 18 months, how much do you think you have spent on the following: " +
                "Please enter '0' if service has not been used. If information CANNOT be determined, enter 999");
        doc.addEntry(note);
        note.setSection(extraCostsSec);
        note.setLabel("C1");

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");

        NumericEntry medicationCosts = factory.createNumericEntry("Medication Costs",
                "Prescribed, and over-the-counter, medications (UKP)?");
        doc.addEntry(medicationCosts);
        medicationCosts.setSection(extraCostsSec);
        medicationCosts.addValidationRule(positive);
        medicationCosts.addUnit(UnitWrapper.instance().getUnit("gbp"));

        NumericEntry travelCosts = factory.createNumericEntry("Travel Costs",
                "Travel costs (e.g. parking fees to attend any hospital, GP, or day care appointments) (UKP)?");
        doc.addEntry(travelCosts);
        travelCosts.setSection(extraCostsSec);
        travelCosts.addValidationRule(positive);
        travelCosts.addUnit(UnitWrapper.instance().getUnit("gbp"));

        NumericEntry privateCareCosts = factory.createNumericEntry("Private Care Costs",
                "Private health care (include use of alternative therapies and practitioners) (UKP)?");
        doc.addEntry(privateCareCosts);
        privateCareCosts.setSection(extraCostsSec);
        privateCareCosts.addValidationRule(positive);
        privateCareCosts.addUnit(UnitWrapper.instance().getUnit("gbp"));

        LongTextEntry careCalc = factory.createLongTextEntry("Care costs - how calculated", "How were the costs calculated?");
        doc.addEntry(careCalc);
        careCalc.setSection(extraCostsSec);

        OptionEntry oneOffExpenses = factory.createOptionEntry(
                "One off expenses",
                "Over the last 18 months, are there any other MAJOR (UKP50+) one-off " +
                        "expenses that you have had to meet?");
        doc.addEntry(oneOffExpenses);
        oneOffExpenses.setSection(extraCostsSec);
        oneOffExpenses.setLabel("C2");
        oneOffExpenses.addOption(factory.createOption("No", "No", 1));
        Option oneOffExpensesYes = factory.createOption("Yes", "Yes", 2);
        oneOffExpenses.addOption(oneOffExpensesYes);

        CompositeEntry oneOffCostComp = factory.createComposite(
                "One off cost items",
                "If the answer to C2 is Yes, please complete table below");
        doc.addEntry(oneOffCostComp);
        oneOffCostComp.setSection(extraCostsSec);
        oneOffCostComp.setLabel("C3");
        oneOffCostComp.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, oneOffExpensesYes, oneOffCostComp);

        TextEntry itemDescription = factory.createTextEntry("Description of item",
        		"Description of item");
        oneOffCostComp.addEntry(itemDescription);
        itemDescription.setSection(extraCostsSec);

        NumericEntry otherCareCosts = factory.createNumericEntry("Other Costs",
                "Amount spent (during last 3 months, UKP)");
        oneOffCostComp.addEntry(otherCareCosts);
        otherCareCosts.setSection(extraCostsSec);
        otherCareCosts.addValidationRule(positive);
        otherCareCosts.addUnit(UnitWrapper.instance().getUnit("gbp"));

        OptionEntry dueToMentalHealthProblem = factory.createOptionEntry("Incurred due to mental health problems?",
                "Incurred due to mental health problems?");
        oneOffCostComp.addEntry(dueToMentalHealthProblem);
        dueToMentalHealthProblem.setSection(extraCostsSec);
        dueToMentalHealthProblem.addOption(factory.createOption("No", 1));
        dueToMentalHealthProblem.addOption(factory.createOption("Yes", 2));

        LongTextEntry costCalc = factory.createLongTextEntry("One off cost calculation", "How were the expenses calculated?");
        doc.addEntry(costCalc);
        costCalc.setSection(extraCostsSec);
        costCalc.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, oneOffExpensesYes, costCalc);


        //Employment and Income section
        Section employmentSec = factory.createSection("Your employment section", "Your employment or usual activities");
        doc.addSection(employmentSec);
        SectionOccurrence employmentSecOcc = factory.createSectionOccurrence(
                "Your Employment section occurrence");
        employmentSec.addOccurrence(employmentSecOcc);

        CompositeEntry qD1 = factory.createComposite("Employment",
                "Which of the following describes how you have been employed in the last 18 months?");
        doc.addEntry(qD1);
        qD1.setSection(employmentSec);
        qD1.setLabel("D1");
        qD1.addRowLabel("Employee, full time (more than 30 hours/week)");
        qD1.addRowLabel("Employee, part time (less than 30 hours/week)");
        qD1.addRowLabel("Self-employed");
        qD1.addRowLabel("Government-supported training");
        qD1.addRowLabel("Non-government supported training or education");
        qD1.addRowLabel("Employee on sick leave because of treatment");
        qD1.addRowLabel("Employee on sick leave for other health reasons");
        qD1.addRowLabel("Not in paid employment because of treatment");
        qD1.addRowLabel("Not in paid employment due to retirement");
        qD1.addRowLabel("Not in paid employment for other reasons");

        TextEntry qD1Label = factory.createTextEntry(
                "Employment Status", "Employment Status");
        qD1.addEntry(qD1Label);
        qD1Label.setSection(employmentSec);

        OptionEntry qD1Apply = factory.createOptionEntry(
                "Applicable", "Applicable");
        qD1.addEntry(qD1Apply);
        qD1Apply.addOption(factory.createOption("Yes", 1));
        qD1Apply.addOption(factory.createOption("No", 2));
        qD1Apply.setSection(employmentSec);

        NumericEntry qD1Freq = factory.createNumericEntry("Number of weeks",
                "Number of weeks spent in the last 3 months");
        qD1.addEntry(qD1Freq);
        qD1Freq.setSection(employmentSec);
        qD1Freq.setDefaultValue(new Double(0.0));
        qD1Freq.addUnit(UnitWrapper.instance().getUnit("weeks"));

		OptionEntry qD1Now = factory.createOptionEntry("Now",
                "Select one category that best describes your employment now");
		qD1.addEntry(qD1Now);
		qD1Now.setSection(employmentSec);
		createOptions(factory, qD1Now, new String[]{"No", "Yes"}, new int[]{0,1});
		qD1Now.setDefaultValue(qD1Now.getOption(0));

        OptionEntry qD2 = factory.createOptionEntry(
                "Has lost earnings", "Have you lost any earnings during the last 18 months because of your mental health problems?");
        doc.addEntry(qD2);
        qD2.setSection(employmentSec);
        qD2.setLabel("D2");
        createOptions(factory, qD2, new String[]{"Yes", "No"}, new int[]{1,2});
        Option qD2Yes = qD2.getOption(0);

        NumericEntry qD3 = factory.createNumericEntry("Amount lost earnings",
                "If YES, please estimate the amount lost over the last 18 months because of your mental health problems?",
                EntryStatus.DISABLED);
        doc.addEntry(qD3);
        qD3.setSection(employmentSec);
        qD3.setLabel("D3");
        qD3.addUnit(UnitWrapper.instance().getUnit("gbp"));
        qD3.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
        createOptionDependent(factory, qD2Yes, qD3);

        LongTextEntry lostEarningsCalc = factory.createLongTextEntry("Lost earnings - how calculated", "How was this amount calculated?");
        doc.addEntry(lostEarningsCalc);
        lostEarningsCalc.setSection(employmentSec);
        lostEarningsCalc.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qD2Yes, lostEarningsCalc);

        OptionEntry qD4 = factory.createOptionEntry(
                "Employment affected",
                "Over the last 18 months do you think that your employment and/or potential employment opportunities have " +
                        "been affected by your mental health problems?");
        doc.addEntry(qD4);
        qD4.setSection(employmentSec);
        qD4.setLabel("D4");
        createOptions(factory, qD4, new String[]{"Yes", "No"}, new int[]{1,2});
        Option qD4Yes = qD4.getOption(0);

        CompositeEntry qD5 = factory.createComposite("Employment problems",
                "If Yes, thinking about your career and your job, have you experienced any of the following in the " +
                        "last 18 months because of your mental health problems? Please tick the appropriate box for each category.");
		doc.addEntry(qD5);
		qD5.setSection(employmentSec);
		qD5.setLabel("D5");
		qD5.setEntryStatus(EntryStatus.DISABLED);
		qD5.addRowLabel("Lost job and became unemployed");
		qD5.addRowLabel("Had difficulty getting a job");
		qD5.addRowLabel("Changed type of work (e.g. lower paid job, but not change in hours worked.)");
		qD5.addRowLabel("Changed hours worked");
		qD5.addRowLabel("Promotion prospects/career development restricted");
		qD5.addRowLabel("Opportunities for changing job reduced");
		qD5.addRowLabel("Opportunities for overtime reduced");
		qD5.addRowLabel("Distance can travel reduced");
		qD5.addRowLabel("Attendance reduced");
		qD5.addRowLabel("Other");
		createOptionDependent(factory, qD4Yes, qD5);

		TextEntry qD5Label = factory.createTextEntry(
		        "Problem", "Employment problem");
		qD5.addEntry(qD5Label);
		qD5Label.setSection(employmentSec);

		OptionEntry qD5Apply = factory.createOptionEntry(
                "Applicable", "Applicable");
		qD5.addEntry(qD5Apply);
		qD5Apply.addOption(factory.createOption("Yes", 1));
		qD5Apply.addOption(factory.createOption("No", 2));
		qD5Apply.setSection(employmentSec);

		OptionEntry qD5Now = factory.createOptionEntry("Now",
                "Select one category that best describes your employment now");
		qD5.addEntry(qD5Now);
		qD5Now.setSection(employmentSec);
		createOptions(factory, qD5Now, new String[]{"No", "Yes"}, new int[]{0,1});
		qD5Now.setDefaultValue(qD5Now.getOption(0));

        OptionEntry qD6 = factory.createOptionEntry(
                "Usual activities affected",
                "Over the last 18 months do you think that your usual activities have been affected by your mental health problems?");
        doc.addEntry(qD6);
        qD6.setSection(employmentSec);
        qD6.setLabel("D6");
        createOptions(factory, qD6, new String[]{"Yes", "No"}, new int[]{1,2});
        Option qD6Yes = qD6.getOption(0);

        CompositeEntry qD7 = factory.createComposite("Usual activities",
                "If Yes, thinking about your usual activities, have you experienced any of the following " +
                        "in the last 18 months because of your mental health problems? Please tick the appropriate box for each category.");
		doc.addEntry(qD7);
		qD7.setSection(employmentSec);
		qD7.setLabel("D7");
		qD7.setEntryStatus(EntryStatus.DISABLED);
		qD7.addRowLabel("Changed to less demanding or less intensive activities");
		qD7.addRowLabel("Changed to more demanding or more intensive activities");
		qD7.addRowLabel("Spend less time on activities");
		qD7.addRowLabel("Spend more time on activities");
		qD7.addRowLabel("Opportunities for taking on new activities decreased");
		qD7.addRowLabel("Opportunities for taking on new activities increased");
		qD7.addRowLabel("Other (specify)");
		createOptionDependent(factory, qD6Yes, qD7);

		TextEntry qD7Label = factory.createTextEntry(
		        "Change", "Change in activities");
		qD7.addEntry(qD7Label);
		qD7Label.setSection(employmentSec);

		OptionEntry qD7Apply = factory.createOptionEntry(
                "Applicable", "Applicable");
		qD7.addEntry(qD7Apply);
		qD7Apply.addOption(factory.createOption("Yes", 1));
		qD7Apply.addOption(factory.createOption("No", 2));
		qD7Apply.setSection(employmentSec);

		OptionEntry qD7Now = factory.createOptionEntry("Now",
                "Select one only category that best describes your usual level of activities now");
		qD7.addEntry(qD7Now);
		qD7Now.setSection(employmentSec);
        createOptions(factory, qD7Now, new String[]{"No", "Yes"}, new int[]{0,1});
        qD7Now.setDefaultValue(qD7Now.getOption(0));



        //Criminal justice section
        Section serviceSec = factory.createSection("Criminal Justice Service section");
        doc.addSection(serviceSec);
        serviceSec.setDisplayText("Criminal Justice Service");
        SectionOccurrence serviceSecOcc = factory
                .createSectionOccurrence("Criminal Justice Service section occurrence");
        serviceSec.addOccurrence(serviceSecOcc);

        OptionEntry contactWithPolice = factory.createOptionEntry(
                "In contact with criminal justice services",
                " During the last 6 months, have you been in contact with the criminal justice services?");
        doc.addEntry(contactWithPolice);
        contactWithPolice.setSection(serviceSec);
        contactWithPolice.setLabel("E1");
        contactWithPolice.addOption(factory.createOption("No", "No", 1));
        Option contactWithPoliceYes = factory.createOption("Yes", "Yes", 2);
        contactWithPolice.addOption(contactWithPoliceYes);

        NarrativeEntry contactPoliceYes = factory.createNarrativeEntry(
                "Contact with police yes",
                "If yes, please complete questions E2 to E7");
        doc.addEntry(contactPoliceYes);
        contactPoliceYes.setSection(serviceSec);

        NarrativeEntry qE2 = factory.createNarrativeEntry(
                "E2",
                "How many contacts have you had during the last 6 months with the following " +
                "(Note: contact = interview or stay of some hours, but not overnight)");
        doc.addEntry(qE2);
        qE2.setSection(serviceSec);
        qE2.setLabel("E2");

        NumericEntry numberContactsPolice = factory.createNumericEntry(
                "Number of contacts with police",
                "With the police?",
                EntryStatus.DISABLED);
        doc.addEntry(numberContactsPolice);
        numberContactsPolice.setSection(serviceSec);
        numberContactsPolice.setLabel("E2a");
        numberContactsPolice.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, numberContactsPolice);

        NumericEntry numberContactsProbation = factory.createNumericEntry(
                "Number of contacts with probation",
                "With a probation officer?",
                EntryStatus.DISABLED);
        doc.addEntry(numberContactsProbation);
        numberContactsProbation.setSection(serviceSec);
        numberContactsProbation.setLabel("E2b");
        numberContactsProbation.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, numberContactsProbation);

        NumericEntry nightsPrison = factory.createNumericEntry(
                "Nights spent in prison",
                "How many nights spent in a police cell or prison during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(nightsPrison);
        nightsPrison.setSection(serviceSec);
        nightsPrison.setLabel("E3");
        nightsPrison.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, nightsPrison);

        NumericEntry psychAssessments = factory.createNumericEntry(
                "Number of psychiatric assessments",
                "How many psychiatric assessments whilst in custody during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(psychAssessments);
        psychAssessments.setSection(serviceSec);
        psychAssessments.addValidationRule(positive);
        psychAssessments.setLabel("E4");
        createOptionDependent(factory, contactWithPoliceYes, psychAssessments);

        NarrativeEntry qE5 = factory.createNarrativeEntry(
                "E5",
                "How many (criminal or civil) court appearances have you had during the last 6 months?");
        doc.addEntry(qE5);
        qE5.setSection(serviceSec);
        qE5.setLabel("E5");
        createOptionDependent(factory, contactWithPoliceYes, qE5);

        NumericEntry criminalCourt = factory.createNumericEntry(
                "Criminal court appearances",
                "Criminal courts:",
                EntryStatus.DISABLED);
        doc.addEntry(criminalCourt);
        criminalCourt.setSection(serviceSec);
        criminalCourt.addValidationRule(positive);
        criminalCourt.setLabel("E5a");
        createOptionDependent(factory, contactWithPoliceYes, criminalCourt);

        NumericEntry civilCourt = factory.createNumericEntry(
                "Civil court appearances",
                "Civil courts:",
                EntryStatus.DISABLED);
        doc.addEntry(civilCourt);
        civilCourt.setSection(serviceSec);
        civilCourt.addValidationRule(positive);
        civilCourt.setLabel("E5b");
        createOptionDependent(factory, contactWithPoliceYes, civilCourt);

        OptionEntry convictions = factory.createOptionEntry(
                "Number of convictions",
                "During the last 6 months, have you been convicted for any offences?",
                EntryStatus.DISABLED);
        doc.addEntry(convictions);
        convictions.setSection(serviceSec);
        convictions.setLabel("E6");
        createOptionDependent(factory, contactWithPoliceYes, convictions);
        convictions.addOption(factory.createOption("No", "No", 1));
        Option convictionsYes = factory.createOption("Yes", "Yes", 2);
        convictions.addOption(convictionsYes);

        CompositeEntry offenceType = factory.createComposite(
                "Offence Type", "What offences have you been convicted of during the last 6 months?");
        doc.addEntry(offenceType);
        offenceType.setSection(serviceSec);
        offenceType.setLabel("E7");
        offenceType.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, convictionsYes, offenceType);

        OptionEntry offenceCategory = factory.createOptionEntry("Category of offence",
                "Category of offence");
        offenceType.addEntry(offenceCategory);
        offenceCategory.setSection(serviceSec);
        offenceCategory.addOption(factory.createOption("Homocide/Manslaughter",
        		"Homocide/Manslaughter"));
        offenceCategory.addOption(factory.createOption("Serious assault leading to major injury",
				"Serious assault leading to major injury"));
        offenceCategory.addOption(factory.createOption("Less serious assault leading to minor or no injury",
				"Less serious assault leading to minor or no injury"));
        offenceCategory.addOption(factory.createOption("Sexual offences",
				"Sexual offences"));
        offenceCategory.addOption(factory.createOption("Robbery",
				"Robbery"));
        offenceCategory.addOption(factory.createOption("Burglary",
				"Burglary"));
        offenceCategory.addOption(factory.createOption("Theft and handling stolen goods",
				"Theft and handling stolen goods"));
        offenceCategory.addOption(factory.createOption("Fraud and forgery",
				"Fraud and forgery"));
        offenceCategory.addOption(factory.createOption("Criminal damage",
				"Criminal damage"));
        offenceCategory.addOption(factory.createOption("Drug offences",
				"Drug offences"));
        Option offenceCategoryOther = factory.createOption("Other", "Other");
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


        Section interviewerSec = factory.createSection("Interviewer Comments section", "Interviewer Comments");
        doc.addSection(interviewerSec);
        SectionOccurrence interviewerSecOcc = factory.createSectionOccurrence("Interviewer Comments section occ");
        interviewerSec.addOccurrence(interviewerSecOcc);

        NumericEntry interviewLength = factory.createNumericEntry("Interview length", "How long did the interview take");
        doc.addEntry(interviewLength);
        interviewLength.setSection(interviewerSec);
        interviewLength.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive"));
        interviewLength.addUnit(UnitWrapper.instance().getUnit("mins"));

        OptionEntry reliability = factory.createOptionEntry("Patients reliability",
                "How reliable or unreliable do you think the patient's responses were?");
        doc.addEntry(reliability);
        reliability.setSection(interviewerSec);
        createOptions(factory, reliability, new String[]{"very reliable",
        												 "generally reliable",
        												 "generally unreliable",
        												 "very unreliable"},
        									new int[]{1,2,3,4});

        LongTextEntry comments = factory.createLongTextEntry("Comments", "Any other comments");
        doc.addEntry(comments);
        comments.setSection(interviewerSec);

        return doc;

    }
}
