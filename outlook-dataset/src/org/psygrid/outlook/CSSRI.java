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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;

public class CSSRI extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "CSSRI",
                "Client Sociodemographic and Service Receipt Inventory (CSSRI - EU)");

        createDocumentStatuses(factory, doc);

        //add section 1 (Living situation)
        addLivingSituationSection(doc, factory, 1, false);

        //add section 2 (Employment and Income)
        addEmploymentSection(doc, factory, 2, false);

        //add section 3 (Service Receipt)
        addServiceReceiptSection(doc, factory, 3, false);

        //add section 4 (Medication Profile)
        addMedicationProfileSection(doc, factory, 4, false);

        //add section 5 (Informal Care and Help)
        addInformalCareSection(doc, factory, 5, false);

        //add section 6 (Hospital Or Community Accomodation Details)
        addAccomodationDetails(doc, factory,  6);

        return doc;
    }

    public static void addSociodemographicsSection(Document doc, Factory factory, int sectionNumber){

        ValidationRule positiveNumber = ValidationRulesWrapper.instance().getRule("Positive");

        String sec = Integer.toString(sectionNumber);

        //Living Situation Section
        Section socdemSec = factory
                .createSection("Sociodemographic info section");
        doc.addSection(socdemSec);
        socdemSec.setDisplayText("Sociodemographic Information");
        SectionOccurrence socdemSecOcc = factory
                .createSectionOccurrence("Sociodemographic info section occurrence");
        socdemSec.addOccurrence(socdemSecOcc);
        socdemSecOcc.setLabel(sec);

        OptionEntry sex = factory.createOptionEntry("Sex", "Sex");
        doc.addEntry(sex);
        sex.setSection(socdemSec);
        sex.setLabel(sec+".2");
        sex.addOption(factory.createOption("Female", 1));
        sex.addOption(factory.createOption("Male", 2));

        OptionEntry maritalStatus = factory.createOptionEntry("Marital status", "Marital status");
        doc.addEntry(maritalStatus);
        maritalStatus.setSection(socdemSec);
        maritalStatus.setDescription("From a legal perspective");
        maritalStatus.setLabel(sec+".3");
        maritalStatus.addOption(factory.createOption("Single/unmarried", 1));
        maritalStatus.addOption(factory.createOption("Married", 2));
        maritalStatus.addOption(factory.createOption("Separated", 3));
        maritalStatus.addOption(factory.createOption("Divorced", 4));
        maritalStatus.addOption(factory.createOption("Widow/widower", 5));
        maritalStatus.addOption(factory.createOption("Cohabiting - not married", 6));

        OptionEntry ethnicity = factory.createOptionEntry("Ethnicity", "What is your ethnic group");
        doc.addEntry(ethnicity);
        ethnicity.setSection(socdemSec);
        ethnicity.setLabel(sec+".4");
		ethnicity.addOption(factory.createOption("White - British", 1));
		ethnicity.addOption(factory.createOption("White - Irish", 2));
		ethnicity.addOption(factory.createOption(
				"White - Other White Background", 3));
		ethnicity.addOption(factory.createOption("Asian - Indian", 4));
		ethnicity.addOption(factory.createOption("Asian - Pakistani", 5));
		ethnicity.addOption(factory.createOption("Asian - Bangladeshi", 6));
		ethnicity.addOption(factory.createOption(
				"Asian - Other Asian Background", 7));
		ethnicity.addOption(factory.createOption("Black - Caribbean", 8));
		ethnicity.addOption(factory.createOption("Black - African", 9));
		ethnicity.addOption(factory.createOption(
				"Black - Other Black Background", 10));
		ethnicity.addOption(factory.createOption(
				"Mixed - White and Black Caribbean", 11));
		ethnicity.addOption(factory.createOption(
				"Mixed - White and Black African", 12));
		ethnicity
				.addOption(factory.createOption("Mixed - White and Asian", 13));
		ethnicity.addOption(factory.createOption(
				"Mixed - Other Mixed Background", 14));
		ethnicity.addOption(factory.createOption(
				"Other Ethnic Groups - Chinese", 15));
		ethnicity.addOption(factory.createOption(
				"Other Ethnic Groups - Other Ethnic Group", 16));

        TextEntry countryOfBirth = factory.createTextEntry("Country of birth", "Country of birth");
        doc.addEntry(countryOfBirth);
        countryOfBirth.setSection(socdemSec);
        countryOfBirth.setLabel(sec+".5");

        OptionEntry motherTongue = factory.createOptionEntry("Mother tongue", "Mother tongue");
        doc.addEntry(motherTongue);
        motherTongue.setSection(socdemSec);
        motherTongue.setLabel(sec+".6");
        motherTongue.addOption(factory.createOption("English language", 1));
        motherTongue.addOption(factory.createOption("Other language (but having good knowledge of English language)", 2));
        motherTongue.addOption(factory.createOption("Other language (and having poor or no knowledge of English language)", 3));

        NumericEntry yearsSchooling = factory.createNumericEntry("Number of years schooling", "Number of years schooling in general education");
        doc.addEntry(yearsSchooling);
        yearsSchooling.setSection(socdemSec);
        yearsSchooling.setLabel(sec+".7");
        yearsSchooling.addValidationRule(positiveNumber);

        OptionEntry highestEdu = factory.createOptionEntry("Highest education", "Highest completed level of education");
        doc.addEntry(highestEdu);
        highestEdu.setSection(socdemSec);
        highestEdu.setLabel(sec+".8");
        highestEdu.addOption(factory.createOption("Primary education or less", 1));
        highestEdu.addOption(factory.createOption("Secondary education", 2));
        highestEdu.addOption(factory.createOption("Tertiary/further education", 3));
        highestEdu.addOption(factory.createOption("Other general education", 4));

        NarrativeEntry furtherEdu = factory.createNarrativeEntry("Further education",
                "What further education or vocational training have you completed or are doing now?");
        doc.addEntry(furtherEdu);
        furtherEdu.setSection(socdemSec);
        furtherEdu.setLabel(sec+".9");

        CompositeEntry furtherEduComp = factory.createComposite("Further education composite");
        doc.addEntry(furtherEduComp);
        furtherEduComp.setSection(socdemSec);

        OptionEntry furtherEduOpts = factory.createOptionEntry("Education/Training options", "Education/Training");
        furtherEduComp.addEntry(furtherEduOpts);
        furtherEduOpts.setSection(socdemSec);
        furtherEduOpts.addOption(factory.createOption("Specific vocational training (< 1 year)", 1));
        furtherEduOpts.addOption(factory.createOption("Specific vocational training (> 1 year)", 2));
        furtherEduOpts.addOption(factory.createOption("Tertiary level qualification/diploma", 3));
        furtherEduOpts.addOption(factory.createOption("University degree (undergraduate)", 4));
        furtherEduOpts.addOption(factory.createOption("University higher degree (postgraduate)", 5));
        furtherEduOpts.addOption(factory.createOption("Other vocational training", 6));

    }

    public static void addLivingSituationSection(Document doc, Factory factory, int sectionNumber, boolean followUp){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");

        String sec = Integer.toString(sectionNumber);
        EntryStatus defaultStatus = null;
        if ( followUp ){
        	defaultStatus = EntryStatus.DISABLED;
        }
        else{
        	defaultStatus = EntryStatus.MANDATORY;
        }

        //Living Situation Section
        Section livingSec = factory
                .createSection("Usual Living Situation section");
        doc.addSection(livingSec);
        livingSec.setDisplayText("Usual Living Situation");
        SectionOccurrence livingSecOcc = factory
                .createSectionOccurrence("Usual Living Situation section occurrence");
        livingSec.addOccurrence(livingSecOcc);
        livingSecOcc.setLabel(sec);

        int quNumber = 1;

        Option sitChangedYes = null;
        if ( followUp ){
        	OptionEntry situationChanged = factory.createOptionEntry("Living Situation changed",
                    "Has your living situation changed at all since I saw you 6 months ago?");
        	doc.addEntry(situationChanged);
        	situationChanged.setSection(livingSec);
        	situationChanged.setLabel(sec+"."+Integer.toString(quNumber));
            situationChanged.setDescription("Prompt: same type of accommodation, same number of people etc. "+
                    "If patient answers ‘yes’ please ask the remainder of this section.");
            situationChanged.addOption(factory.createOption("No", "No", 0));
        	sitChangedYes = factory.createOption("Yes", "Yes", 1);
        	situationChanged.addOption(sitChangedYes);
        	quNumber++;
        }

        OptionEntry livingSituation = factory.createOptionEntry("Usual/normal living situation",
                "What is your usual/normal living situation now?", defaultStatus);
        doc.addEntry(livingSituation);
        livingSituation.setSection(livingSec);
        livingSituation.setLabel(sec+"."+Integer.toString(quNumber));
        livingSituation.addOption(factory.createOption("Living alone", "Living alone (+/- children)", 1));
        livingSituation.addOption(factory.createOption("Living with husband/wife", "Living with husband/wife (+/- children)", 2));
        livingSituation.addOption(factory.createOption("Living together as a couple", "Living together as a couple", 3));
        livingSituation.addOption(factory.createOption("Living with parents", "Living with parents", 4));
        livingSituation.addOption(factory.createOption("Living with other relatives", "Living with other relatives", 5));
        livingSituation.addOption(factory.createOption("Living with others", "Living with others", 6));
        if ( followUp ){
        	createOptionDependent(factory, sitChangedYes, livingSituation);
        }
        quNumber++;

        OptionEntry accomodationKind = factory.createOptionEntry("Kind of " +
                "accomodation", "What kind of accommodation is it?", defaultStatus);
        doc.addEntry(accomodationKind);
        accomodationKind.setSection(livingSec);
        accomodationKind.setLabel(sec+"."+Integer.toString(quNumber));
        if ( followUp ){
        	createOptionDependent(factory, sitChangedYes, accomodationKind);
        }
        quNumber++;

        Map<String, List<Option>> accomOptions =
            addAccomodationOptions(factory, accomodationKind, true);

        NarrativeEntry domestic = factory.createNarrativeEntry("If domestic " +
                "accommodation ", "If domestic accommodation:");
        doc.addEntry(domestic);
        domestic.setSection(livingSec);
        domestic.setLabel(sec+"."+Integer.toString(quNumber));
        quNumber++;

        NumericEntry adultsLive = factory.createNumericEntry(
                "Number of adults",
                "How many adults live there? (over the age of 18, " +
                        "including the patient)",
                EntryStatus.DISABLED);
        doc.addEntry(adultsLive);
        adultsLive.setSection(livingSec);
        adultsLive.addValidationRule(positive);
        List<Option> domesticOptions = accomOptions.get("domestic");
        for (Option option : domesticOptions) {
            createOptionDependent(factory, option, adultsLive);
        }

        NumericEntry childrenLive = factory.createNumericEntry(
                "Number of children",
                "And how many children? (under the age of 18)",
                EntryStatus.DISABLED);
        doc.addEntry(childrenLive);
        childrenLive.setSection(livingSec);
        childrenLive.addValidationRule(positive);
        for (Option option : domesticOptions) {
            createOptionDependent(factory, option, childrenLive);
        }

        if ( followUp ){
            List<Option> commHospOptions = accomOptions.get("commHosp");

            NarrativeEntry nameAddress = factory.createNarrativeEntry("Name Address narrative",
                    "If hospital or community accommodation, please give the name and address of the accommodation (within the last 6 months):");
            doc.addEntry(nameAddress);
            nameAddress.setSection(livingSec);

            TextEntry accomName = factory.createTextEntry("Accomodation Name", "Name", defaultStatus);
            doc.addEntry(accomName);
            accomName.setSection(livingSec);
            for ( Option opt: commHospOptions){
                createOptionDependent(factory, opt, accomName);
            }

            LongTextEntry accomAddress = factory.createLongTextEntry("Accomodation Address", "Address", defaultStatus);
            doc.addEntry(accomAddress);
            accomAddress.setSection(livingSec);
            for ( Option opt: commHospOptions){
                createOptionDependent(factory, opt, accomAddress);
            }

            NarrativeEntry finalSheet = factory.createNarrativeEntry("Final sheet narrative",
                    "Please complete the final " +
                    "sheet of the schedule after finishing this interview");
            doc.addEntry(finalSheet);
            finalSheet.setSection(livingSec);
        }
        else{
            NarrativeEntry finalSheet = factory.createNarrativeEntry("Final sheet " +
                    "narrative",
                    "If hospital or community accommodation: Complete the final " +
                    "sheet of the schedule after finishing this interview");
            doc.addEntry(finalSheet);
            finalSheet.setSection(livingSec);
        }

        String livedElseText = null;
        if ( followUp ){
            livedElseText = "Have you lived anywhere else in the last 6 months?";
        }
        else{
            livedElseText = "Have you lived anywhere else in the last 3 months?";
        }

        OptionEntry livedElse = factory.createOptionEntry("Lived anywhere else",
                livedElseText, defaultStatus);
        doc.addEntry(livedElse);
        livedElse.setSection(livingSec);
        livedElse.setLabel(sec+"."+Integer.toString(quNumber));
        livedElse.addOption(factory.createOption("No", "No", 0));
        Option livedElseYes = factory.createOption("Yes", "Yes", 1);
        livedElse.addOption(livedElseYes);
        if ( followUp ){
        	createOptionDependent(factory, sitChangedYes, livedElse);
        }
        quNumber++;

        CompositeEntry livedElseDetails = factory.createComposite(
                "Lived anywhere else details",
                "If yes: please complete the table.");
        doc.addEntry(livedElseDetails);
        livedElseDetails.setSection(livingSec);
        livedElseDetails.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, livedElseYes, livedElseDetails);

        OptionEntry accomodationType = factory.createOptionEntry(
                "Accomodation type",
                "Accomodation type");
        livedElseDetails.addEntry(accomodationType);
        accomodationType.setSection(livingSec);
        addAccomodationOptions(factory, accomodationType, false);

        NumericEntry livedElseDays = factory.createNumericEntry(
                "Days lived elsewhere",
                "Number of days in the last 3 months");
        livedElseDetails.addEntry(livedElseDays);
        livedElseDays.setSection(livingSec);
        livedElseDays.addValidationRule(positive);

    }

    private static Map<String, List<Option>> addAccomodationOptions(
            Factory factory, OptionEntry accomodationKind, boolean optDep) {
        Option ownerFlat = factory.createOption("Owner occupied flat or house",
                "Domestic / family - Owner occupied flat or house", 1);
        accomodationKind.addOption(ownerFlat);
        Option privateRented = factory.createOption("Privately rented flat or " +
                "house", "Domestic / family - Privately rented flat or house", 2);
        accomodationKind.addOption(privateRented);
        Option municipRented = factory.createOption("Rented from local authority",
                "Domestic / family - Rented from local authority/municipality or " +
                        "housing association/co-operative", 3);
        accomodationKind.addOption(municipRented);
        Option facilityStaffedAlways = factory.createOption("Overnight facility, " +
                "24-hour staffed", "Community (non-hospital) - Overnight " +
                "facility, 24-hour staffed", 4);
        accomodationKind.addOption(facilityStaffedAlways);
        Option facilityStaffed = factory.createOption("Overnight facility, " +
                "staffed (not 24-hour)", "Community (non-hospital) - Overnight " +
                "facility, staffed (not 24-hour)", 5);
        accomodationKind.addOption(facilityStaffed);
        Option facilityUnstaffed = factory.createOption("Overnight facility, " +
                "unstaffed at all times", "Community (non-hospital) - Overnight " +
                "facility, unstaffed at all times", 6);
        accomodationKind.addOption(facilityUnstaffed);
        Option acutePsychWard = factory.createOption("Acute psychiatric ward",
                "Hospital - Acute psychiatric ward", 7);
        accomodationKind.addOption(acutePsychWard);
        Option rehabPsychWard = factory.createOption("Rehabilitation " +
                "psychiatric ward", "Hospital - Rehabilitation psychiatric ward", 8);
        accomodationKind.addOption(rehabPsychWard);
        Option psychWard = factory.createOption("Long-stay psychiatric ward",
                "Hospital - Long-stay psychiatric ward", 9);
        accomodationKind.addOption(psychWard);
        Option medWard = factory.createOption("General medical ward",
                "Hospital - General medical ward", 10);
        accomodationKind.addOption(medWard);
        Option homeless = factory.createOption("Homeless / roofless",
                "Homeless / roofless", 11);
        accomodationKind.addOption(homeless);
        Option accKindOther = factory.createOption("Other", "Other", 12);
        accKindOther.setTextEntryAllowed(true);
        accomodationKind.addOption(accKindOther);

        if (!optDep) {
            return null;
        }

        Map<String, List<Option>> map = new HashMap<String, List<Option>>();

        // domestic option dependents
        List<Option> domestic = new ArrayList<Option>();
        map.put("domestic", domestic);
        domestic.add(ownerFlat);
        domestic.add(privateRented);
        domestic.add(municipRented);

        // community/hospital option dependents
        List<Option> community = new ArrayList<Option>();
        map.put("commHosp", community);
        community.add(facilityStaffedAlways);
        community.add(facilityStaffed);
        community.add(facilityUnstaffed);
        community.add(acutePsychWard);
        community.add(rehabPsychWard);
        community.add(psychWard);
        community.add(medWard);

        return map;
    }

    public static void addEmploymentSection(Document doc, Factory factory, int sectionNumber, boolean followUp){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");

        String sec = Integer.toString(sectionNumber);
        EntryStatus defaultStatus = null;
        if ( followUp ){
            defaultStatus = EntryStatus.DISABLED;
        }
        else{
            defaultStatus = EntryStatus.MANDATORY;
        }

        //Employment and Income section
        Section employmentSec = factory.createSection("Employment and Income section");
        doc.addSection(employmentSec);
        employmentSec.setDisplayText("Employment and Income");
        SectionOccurrence employmentSecOcc = factory.createSectionOccurrence(
                "Employment and Income section occurrence");
        employmentSec.addOccurrence(employmentSecOcc);
        employmentSecOcc.setLabel(sec);

        int quNumber = 1;

        Option sitChangedYes = null;
        if ( followUp ){
            OptionEntry situationChanged = factory.createOptionEntry("Employment Situation changed",
                    "Has your employment situation changed at all over the last 6 months?");
            doc.addEntry(situationChanged);
            situationChanged.setSection(employmentSec);
            situationChanged.setLabel(sec+"."+Integer.toString(quNumber));
            situationChanged.addOption(factory.createOption("No", "No", 0));
            sitChangedYes = factory.createOption("Yes", "Yes", 1);
            situationChanged.addOption(sitChangedYes);
            quNumber++;
        }

        CompositeEntry employment = factory.createComposite("Employment status", "What is your employment status? (Include up to two options)");
        doc.addEntry(employment);
        employment.setSection(employmentSec);
        employment.setEntryStatus(defaultStatus);
        employment.setLabel(sec+"."+Integer.toString(quNumber));
        if ( followUp ){
            createOptionDependent(factory, sitChangedYes, employment);
        }
        quNumber++;

        OptionEntry employmentStatus = factory.createOptionEntry("Employment status", "What is your employment status?");
        employment.addEntry(employmentStatus);
        employmentStatus.setSection(employmentSec);
        Option paidEmployment = factory.createOption("Paid or self employment",
                "Paid or self employment", 1);
        employmentStatus.addOption(paidEmployment);
        Option volEmployment = factory.createOption("Voluntary employment",
                "Voluntary employment", 2);
        employmentStatus.addOption(volEmployment);
        Option shelteredEmployment = factory.createOption("Sheltered employment",
                "Sheltered employment", 3);
        employmentStatus.addOption(shelteredEmployment);
        Option unemployedOpt = factory.createOption("Unemployed", "Unemployed",
                4);
        employmentStatus.addOption(unemployedOpt);
        Option studentOption = factory.createOption("Student", "Student", 5);
        employmentStatus.addOption(studentOption);
        Option houseWifeOption = factory.createOption("Housewife/husband",
                "Housewife/husband", 6);
        employmentStatus.addOption(houseWifeOption);
        Option retiredOption = factory.createOption("Retired", "Retired", 7);
        employmentStatus.addOption(retiredOption);
        Option employmentStatusOther = factory.createOption("Other", "Other", 8);
        employmentStatusOther.setTextEntryAllowed(true);
        employmentStatus.addOption(employmentStatusOther);

        OptionEntry fullPartTime = factory.createOptionEntry("Full or part time", "Full or part time?");
        employment.addEntry(fullPartTime);
        fullPartTime.setSection(employmentSec);
        fullPartTime.addOption(factory.createOption("Full time", "Full time", 1));
        fullPartTime.addOption(factory.createOption("Part time", "Part time", 2));

        OptionEntry occupation = factory.createOptionEntry(
                "Occupation",
                "If employed: state occupation");
        doc.addEntry(occupation);
        occupation.setSection(employmentSec);
        occupation.setDescription("Refer to manual for definitions");
        occupation.setLabel(sec+"."+Integer.toString(quNumber));
        occupation.addOption(factory.createOption("Manager/administrator", "Manager/administrator", 1));
        occupation.addOption(factory.createOption("Professional (eg health, teaching, legal)", "Professional (eg health, teaching, legal)", 2));
        occupation.addOption(factory.createOption("Associate professional (eg technical, nursing)", "Associate professional (eg technical, nursing)", 3));
        occupation.addOption(factory.createOption("Clerical worker /secretary", "Clerical worker /secretary", 4));
        occupation.addOption(factory.createOption("Skilled labourer (eg building, electrical etc.)", "Skilled labourer (eg building, electrical etc.)", 5));
        occupation.addOption(factory.createOption("Services/sales (eg retail)   ", "Services/sales (eg retail)   ", 6));
        occupation.addOption(factory.createOption("Factory worker", "Factory worker", 7));
        Option occupationOther = factory.createOption("Other", "Other", 8);
        occupationOther.setTextEntryAllowed(true);
        occupation.addOption(occupationOther);
        quNumber++;

        String illnessText = null;
        if ( followUp ){
            illnessText = "How many days have you been absent from work owing to illness within the last 6 months?";
        }
        else{
            illnessText = "How many days have you been absent from work owing to illness within the last 3 months?";
        }
        NumericEntry illness = factory.createNumericEntry(
                "Absent from work due to illness",
                illnessText);
        doc.addEntry(illness);
        illness.setSection(employmentSec);
        illness.addUnit(UnitWrapper.instance().getUnit("days"));
        illness.addValidationRule(positive);

        String unemployedText = null;
        if ( followUp ){
            unemployedText = "If unemployed: Number of weeks unemployed within the last 6 months";
        }
        else{
            unemployedText = "If unemployed: Number of weeks unemployed within the last 3 months";
        }

        NumericEntry unemployed = factory.createNumericEntry("Weeks unemployed",
                unemployedText);
        doc.addEntry(unemployed);
        unemployed.setSection(employmentSec);
        unemployed.setLabel(sec+"."+Integer.toString(quNumber));
        unemployed.addUnit(UnitWrapper.instance().getUnit("weeks"));
        unemployed.addValidationRule(positive);
        quNumber++;

        if ( followUp ){
            CompositeEntry unemployedReason = factory.createComposite("Unemployed reason",
                    "Please could you tell me the main reason you are unemployed at the moment?");
            doc.addEntry(unemployedReason);
            unemployedReason.setSection(employmentSec);
            unemployedReason.setLabel(sec+"."+Integer.toString(quNumber));
            quNumber++;

            OptionEntry unempReasonOptions = factory.createOptionEntry("Unemployed reason options", "Reason");
            unemployedReason.addEntry(unempReasonOptions);
            unempReasonOptions.setSection(employmentSec);
            unempReasonOptions.addOption(factory.createOption("Mental Illness", 0));
            unempReasonOptions.addOption(factory.createOption("Physical Illness or Disability", 1));
            unempReasonOptions.addOption(factory.createOption("General Employment Situation", 2));
            unempReasonOptions.addOption(factory.createOption("Redundancy", 3));
            Option unempReasonOther = factory.createOption("Other (please specify)", 4);
            unempReasonOther.setTextEntryAllowed(true);
            unempReasonOptions.addOption(unempReasonOther);

            NarrativeEntry remainedUnemployed = factory.createNarrativeEntry("Remained unemployed narrative",
                    "If subject has remained unemployed since last interview, please check that their benefits have not changed:");
            doc.addEntry(remainedUnemployed);
            remainedUnemployed.setSection(employmentSec);
            remainedUnemployed.setLabel(sec+"."+Integer.toString(quNumber));
            quNumber++;

            TextEntry newBenefits = factory.createTextEntry("New benefits",
                    "Are you getting any new benefits that you were not receiving the last time I saw you?");
            doc.addEntry(newBenefits);
            newBenefits.setSection(employmentSec);

            TextEntry stoppedBenefits = factory.createTextEntry("Stopped benefits",
                    "Have you stopped getting any benefits that you were receiving the last time I saw you?");
            doc.addEntry(stoppedBenefits);
            stoppedBenefits.setSection(employmentSec);

        }


        OptionEntry stateBenefits = factory.createOptionEntry("Do you receive " +
                "any state benefits?", "Do you receive any state benefits?");
        doc.addEntry(stateBenefits);
        stateBenefits.setSection(employmentSec);
        stateBenefits.setLabel(sec+"."+Integer.toString(quNumber));
        stateBenefits.addOption(factory.createOption("No", "No", 0));
        Option stateBenefitsYes = factory.createOption("Yes", "Yes", 1);
        stateBenefits.addOption(stateBenefitsYes);
        quNumber++;

        NarrativeEntry stateBenefitsNarrative = factory.createNarrativeEntry(
                "What benefits narrative", "If yes: What benefits are received?");
        doc.addEntry(stateBenefitsNarrative);
        stateBenefitsNarrative.setSection(employmentSec);

        NarrativeEntry incomeSupportNarrative = factory.createNarrativeEntry(
                "Unemployment/income support", "Unemployment/income support " +
                "(International category)");
        doc.addEntry(incomeSupportNarrative);
        incomeSupportNarrative.setSection(employmentSec);

        OptionEntry incomeSupport = factory.createOptionEntry("Income support ",
                "Income support (National variant)", EntryStatus.DISABLED);
        doc.addEntry(incomeSupport);
        incomeSupport.setSection(employmentSec);
        incomeSupport.addOption(factory.createOption("Yes", "Yes", 1));
        incomeSupport.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, stateBenefitsYes, incomeSupport);

        OptionEntry jobseekers = factory.createOptionEntry("Jobseeker's " +
                "allowance", "Jobseeker's allowance (National variant)",
                EntryStatus.DISABLED);
        doc.addEntry(jobseekers);
        jobseekers.setSection(employmentSec);
        jobseekers.addOption(factory.createOption("Yes", "Yes", 1));
        jobseekers.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, stateBenefitsYes, jobseekers);

        NarrativeEntry sicknessNarrative = factory.createNarrativeEntry(
                "Sickness/disability", "Sickness/disability " +
                "(International category)");
        doc.addEntry(sicknessNarrative);
        sicknessNarrative.setSection(employmentSec);

        OptionEntry disability = factory.createOptionEntry("Disability living " +
                "allowance", "Disability living allowance", EntryStatus.DISABLED);
        doc.addEntry(disability);
        disability.setSection(employmentSec);
        disability.addOption(factory.createOption("Yes", "Yes", 1));
        disability.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, stateBenefitsYes, disability);

        OptionEntry sickPay = factory.createOptionEntry("Statutory sick pay",
                "Statutory sick pay (National variant)", EntryStatus.DISABLED);
        doc.addEntry(sickPay);
        sickPay.setSection(employmentSec);
        sickPay.addOption(factory.createOption("Yes", "Yes", 1));
        sickPay.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, stateBenefitsYes, sickPay);

        NarrativeEntry housingNarrative = factory.createNarrativeEntry(
                "Housing", "Housing " +
                "(International category)");
        doc.addEntry(housingNarrative);
        housingNarrative.setSection(employmentSec);

        OptionEntry housingBenefit = factory.createOptionEntry("Housing benefit",
                "Housing benefit (National variant)", EntryStatus.DISABLED);
        doc.addEntry(housingBenefit);
        housingBenefit.setSection(employmentSec);
        housingBenefit.addOption(factory.createOption("Yes", "Yes", 1));
        housingBenefit.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, stateBenefitsYes, housingBenefit);

        NarrativeEntry otherBenefitsNarrative = factory.createNarrativeEntry(
                "Other benefits", "Other benefits " +
                "(International category)");
        doc.addEntry(otherBenefitsNarrative);
        otherBenefitsNarrative.setSection(employmentSec);

        OptionEntry statePension = factory.createOptionEntry("State pension",
                "State pension (National variant)", EntryStatus.DISABLED);
        doc.addEntry(statePension);
        statePension.setSection(employmentSec);
        statePension.addOption(factory.createOption("Yes", "Yes", 1));
        statePension.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, stateBenefitsYes, statePension);

        OptionEntry childBenefit = factory.createOptionEntry("Child benefit",
                "Child benefit (National variant)", EntryStatus.DISABLED);
        doc.addEntry(childBenefit);
        childBenefit.setSection(employmentSec);
        childBenefit.addOption(factory.createOption("Yes", "Yes", 1));
        childBenefit.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, stateBenefitsYes, childBenefit);

        Option incomeChangedYes = null;
        if ( followUp ){
            OptionEntry incomeChanged = factory.createOptionEntry("Income changed",
                    "Has your income changed at all in the last 6 months?");
            doc.addEntry(incomeChanged);
            incomeChanged.setSection(employmentSec);
            incomeChanged.setLabel(sec+"."+Integer.toString(quNumber));
            incomeChanged.addOption(factory.createOption("No", "No", 0));
            incomeChangedYes = factory.createOption("Yes", "Yes", 1);
            incomeChanged.addOption(incomeChangedYes);
            quNumber++;
        }

        OptionEntry incomeSource = factory.createOptionEntry("Main income source",
                "What is your main income source?",
                defaultStatus);
        doc.addEntry(incomeSource);
        incomeSource.setSection(employmentSec);
        incomeSource.setLabel(sec+"."+Integer.toString(quNumber));
        incomeSource.addOption(factory.createOption("Salary/Wage", "Salary/Wage", 1));
        incomeSource.addOption(factory.createOption("State benefits", "State benefits", 2));
        incomeSource.addOption(factory.createOption("Pension", "Pension", 3));
        incomeSource.addOption(factory.createOption("Family support",
                "Family support (e.g. from spouse)", 4));
        Option incomeSourceOther = factory.createOption("Other", "Other", 5);
        incomeSourceOther.setTextEntryAllowed(true);
        incomeSource.addOption(incomeSourceOther);
        if ( followUp ){
            createOptionDependent(factory, incomeChangedYes, incomeSource);
        }
        quNumber++;

        OptionEntry personalIncome = factory.createOptionEntry("Total personal income",
                "What is your total personal gross income from all sources? " +
                        "(Note: if  gross income not known, please give net income, " +
                        "i.e. after tax and other deductions)",
                defaultStatus);
        doc.addEntry(personalIncome);
        personalIncome.setSection(employmentSec);
        personalIncome.setLabel(sec+"."+Integer.toString(quNumber));
        personalIncome.addOption(factory.createOption("Under £149",
                "Less than £149 weekly / £649 monthly / £7785 yearly",
                1));
        personalIncome.addOption(factory.createOption("£150 - £204",
                "£150 - £204 weekly / £650 - £885 monthly / £7,786 - £10,635 yearly",
                2));
        personalIncome.addOption(factory.createOption("£205 - £279",
                "£205 - £279 weekly / £886 - £1,208 monthly / £10,636 - £14,504 yearly",
                3));
        personalIncome.addOption(factory.createOption("£280 - £392",
                "£280 - £392 weekly / £1,209 - £1,699 monthly / £14,505 - £20,394 yearly",
                4));
        personalIncome.addOption(factory.createOption("More than £393",
                "More than £393 weekly / £1,700 monthly / £20,395 yearly", 5));
        if ( followUp ){
            createOptionDependent(factory, incomeChangedYes, personalIncome);
        }
        quNumber++;

        OptionEntry includesBenefits = factory.createOptionEntry("Includes benefits",
                "Does this include all of your benefits (housing and grants)?",
                defaultStatus);
        doc.addEntry(includesBenefits);
        includesBenefits.setSection(employmentSec);
        includesBenefits.addOption(factory.createOption("Yes", "Yes", 1));
        includesBenefits.addOption(factory.createOption("No", "No", 2));
        if ( followUp ){
            createOptionDependent(factory, incomeChangedYes, includesBenefits);
        }

        OptionEntry typeIncome = factory.createOptionEntry("Type of income",
                "What type of income did you state?",
                defaultStatus);
        doc.addEntry(typeIncome);
        typeIncome.setSection(employmentSec);
        Option grossIncomeType = factory.createOption("Gross income",
                "Gross income", 1);
        typeIncome.addOption(grossIncomeType);
        Option netIncomeType = factory.createOption("Net income",
                "Net income", 2);
        typeIncome.addOption(netIncomeType);
        if ( followUp ){
            createOptionDependent(factory, incomeChangedYes, typeIncome);
        }

    }

    public static void addServiceReceiptSection(Document doc, Factory factory, int sectionNumber, boolean followUp){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");

        String sec = Integer.toString(sectionNumber);

        //Service Receipt section
        Section serviceSec = factory.createSection("Service Receipt section");
        doc.addSection(serviceSec);
        serviceSec.setDisplayText("Service Receipt");
        SectionOccurrence serviceSecOcc = factory
                .createSectionOccurrence("Service Receipt section occurrence");
        serviceSec.addOccurrence(serviceSecOcc);
        serviceSecOcc.setLabel(sec);

        String inpatientText = null;
        if ( followUp ){
            inpatientText = "Please list any use of inpatient hospital services over the "+
                "last 6 months. (Note: see manual for definitions)";
        }
        else{
            inpatientText = "Please list any use of inpatient hospital services over the "+
                "last 12 months. (Note: see manual for definitions)";
        }

        CompositeEntry inpatient = factory.createComposite(
                "Use of inpatient hospital services",
                inpatientText);
        doc.addEntry(inpatient);
        inpatient.setSection(serviceSec);
        inpatient.setLabel(sec+".1");

        OptionEntry inpatientService = factory.createOptionEntry(
                "Inpatient service option", "Service");
        inpatient.addEntry(inpatientService);
        inpatientService.setSection(serviceSec);
        inpatientService.addOption(factory.createOption("Acute psychiatric ward",
                "Acute psychiatric ward", 0));
        inpatientService.addOption(factory.createOption("Psychiatric " +
                "rehabilitation ward", "Psychiatric rehabilitation ward", 1));
        inpatientService.addOption(factory.createOption("Long-stay ward",
                "Long-stay ward", 2));
        inpatientService.addOption(factory.createOption("Emergency / crisis centre",
                "Emergency / crisis centre", 3));
        inpatientService.addOption(factory.createOption("General medical ward",
                "General medical ward", 4));
        Option inpatientServiceOther = factory.createOption("Other", "Other", 5);
        inpatientServiceOther.setTextEntryAllowed(true);
        inpatientService.addOption(inpatientServiceOther);

        NumericEntry admissions = factory.createNumericEntry("Admissions",
                "Admissions");
        inpatient.addEntry(admissions);
        admissions.setSection(serviceSec);
        admissions.addValidationRule(positive);

        String inpatientDaysText = null;
        if ( followUp ){
            inpatientDaysText = "Total number of inpatient days (over the last 6 months)";
        }
        else{
            inpatientDaysText = "Total number of inpatient days (over the last 12 months)";
        }

        NumericEntry inpatientDays = factory.createNumericEntry("Total number " +
                "of inpatient days",
                inpatientDaysText);
        inpatient.addEntry(inpatientDays);
        inpatientDays.setSection(serviceSec);
        inpatientDays.addValidationRule(positive);

        String outpatient1Text = null;
        if ( followUp ){
            outpatient1Text = "Please list any use of outpatient hospital " +
            "services over the last 6 months. (Note: see manual for " +
            "definitions)";
        }
        else{
            outpatient1Text = "Please list any use of outpatient hospital " +
            "services over the last 3 months. (Note: see manual for " +
            "definitions)";
        }
        CompositeEntry outpatient1 = factory.createComposite("Use of outpatient " +
                "hospital services", outpatient1Text);
        doc.addEntry(outpatient1);
        outpatient1.setSection(serviceSec);
        outpatient1.setLabel(sec+".2");

        OptionEntry outpatientService1 = factory.createOptionEntry("Outpatient " +
                "service option", "Service");
        outpatient1.addEntry(outpatientService1);
        outpatientService1.setSection(serviceSec);
        outpatientService1.addOption(factory.createOption(
                "Accident and Emergency department",
                "Accident and Emergency department (attendances)", 0));
        outpatientService1.addOption(factory.createOption(
                "Psychiatric outpatient visit",
                "Psychiatric outpatient visit (appointments)", 1));
        outpatientService1.addOption(factory.createOption(
                "Other hospital outpatient visit",
                "Other hospital outpatient visit (incl. A&E) (appointments)", 2));
        outpatientService1.addOption(factory.createOption(
                "Day hospital",
                "Day hospital (day attendances)", 3));
        Option outpatientServiceOther = factory.createOption("Other", "Other", 4);
        outpatientServiceOther.setTextEntryAllowed(true);
        outpatientService1.addOption(outpatientServiceOther);

        String unitsReceived1Text = null;
        if ( followUp ){
            unitsReceived1Text = "Number of attendances (over the last 6 months)";
        }
        else{
            unitsReceived1Text = "Number of attendances (over the last 3 months)";
        }

        NumericEntry unitsReceived1 = factory.createNumericEntry("Number of attendances",
                unitsReceived1Text);
        outpatient1.addEntry(unitsReceived1);
        unitsReceived1.setSection(serviceSec);
        unitsReceived1.addValidationRule(positive);

        String communityServicesText = null;
        if ( followUp ){
            communityServicesText = "Please list any use of " +
            "community-based day services over the last 6 months. (Note: " +
            "see manual for definitions)";
        }
        else{
            communityServicesText = "Please list any use of " +
            "community-based day services over the last 3 months. (Note: " +
            "see manual for definitions)";
        }

        CompositeEntry communityServices = factory.createComposite(
                "Community-based day services", communityServicesText);
        doc.addEntry(communityServices);
        communityServices.setSection(serviceSec);
        communityServices.setLabel(sec+".3");

        OptionEntry communityService = factory.createOptionEntry("Community Day " +
                "Services Option", "Service");
        communityServices.addEntry(communityService);
        communityService.setSection(serviceSec);
        communityService.addOption(factory.createOption("Community mental " +
                "health centre", "Community mental health centre", 0));
        communityService.addOption(factory.createOption("Day care centre",
                "Day care centre", 1));
        communityService.addOption(factory.createOption("Group therapy",
                "Group therapy", 2));
        communityService.addOption(factory.createOption("Sheltered workshop",
                "Sheltered workshop", 3));
        communityService.addOption(factory.createOption("Specialist education",
                "Specialist education", 4));
        Option communityServiceOther = factory.createOption("Other", "Other", 5);
        communityServiceOther.setTextEntryAllowed(true);
        communityService.addOption(communityServiceOther);

        NumericEntry numberAttendance = factory.createNumericEntry("Number of " +
                "attendances", "Number of attendances");
        communityServices.addEntry(numberAttendance);
        numberAttendance.setSection(serviceSec);
        numberAttendance.addValidationRule(positive);

        NumericEntry durationAttendance = factory.createNumericEntry("Average " +
                "duration of attendance", "Average duration of attendance");
        communityServices.addEntry(durationAttendance);
        durationAttendance.setSection(serviceSec);
        durationAttendance.addValidationRule(positive);
        durationAttendance.addUnit(UnitWrapper.instance().getUnit("mins"));
        durationAttendance.addUnit(UnitWrapper.instance().getUnit("hours"));
        durationAttendance.addUnit(UnitWrapper.instance().getUnit("days"));

        String communityContactsText = null;
        if ( followUp ){
            communityContactsText = "Please list any other primary " +
            "and community care contacts over the last 6 months (Note: " +
            "see manual for definitions)";
        }
        else{
            communityContactsText = "Please list any other primary " +
            "and community care contacts over the last 3 months (Note: " +
            "see manual for definitions)";
        }

        CompositeEntry communityContacts = factory.createComposite(
                "Community care contacts", communityContactsText);
        doc.addEntry(communityContacts);
        communityContacts.setSection(serviceSec);
        if ( followUp ){
            communityContacts.setLabel(sec+".4");
        }
        else{
            communityContacts.setLabel(sec+".4a");
        }

        OptionEntry communityContactsService = factory.createOptionEntry(
                "Community Care Contacts Service option",
                "Service");
        communityContacts.addEntry(communityContactsService);
        communityContactsService.setSection(serviceSec);
        communityContactsService.addOption(factory.createOption("Psychiatrist",
                "Psychiatrist", 0));
        communityContactsService.addOption(factory.createOption("Psychologist",
                "Psychologist", 1));
        communityContactsService.addOption(factory.createOption("GP / Primary care " +
                "physician", "GP / Primary care physician*", 2));
        communityContactsService.addOption(factory.createOption("Counsellor",
                "Counsellor", 3));
        communityContactsService.addOption(factory.createOption("District nurse",
                "District nurse", 4));
        communityContactsService.addOption(factory.createOption("Community " +
                "psychiatric nurse", "Community psychiatric nurse / case manager", 5));
        communityContactsService.addOption(factory.createOption("Social worker",
                "Social worker", 6));
        communityContactsService.addOption(factory.createOption("Occupational " +
                "therapist", "Occupational therapist", 7));
        communityContactsService.addOption(factory.createOption("Home help / " +
                "care worker", "Home help / care worker", 8));
        Option communityContactsOther = factory.createOption("Other", "Other", 9);
        communityContactsOther.setTextEntryAllowed(true);
        communityContactsService.addOption(communityContactsOther);

        OptionEntry sector = factory.createOptionEntry("Sector", "Sector");
        communityContacts.addEntry(sector);
        sector.setSection(serviceSec);
        sector.addOption(factory.createOption("govt", "govt", 1));
        sector.addOption(factory.createOption("vol", "vol", 2));
        sector.addOption(factory.createOption("private", "private", 3));

        NumericEntry numberContacts = factory.createNumericEntry("Total number " +
                "of contacts", "Total number of contacts over the last 3 months");
        communityContacts.addEntry(numberContacts);
        numberContacts.setSection(serviceSec);
        numberContacts.addValidationRule(positive);

        NumericEntry contactTime = factory.createNumericEntry("Average contact " +
                "time", "Average contact time");
        communityContacts.addEntry(contactTime);
        contactTime.setSection(serviceSec);
        contactTime.addValidationRule(positive);
        contactTime.addUnit(UnitWrapper.instance().getUnit("mins"));
        contactTime.addUnit(UnitWrapper.instance().getUnit("hours"));

        if ( !followUp ){
            NumericEntry totalGPContacts = factory.createNumericEntry("GP contacts", "*Please also estimate total GP contacts over past 12 months");
            doc.addEntry(totalGPContacts);
            totalGPContacts.setSection(serviceSec);
            totalGPContacts.setLabel(sec+".4b");
        }

        String contactWithPoliceText = null;
        if ( followUp ){
            contactWithPoliceText = "Over the last 6 months, has the patient been in contact with the " +
            "criminal justice services?";
        }
        else{
            contactWithPoliceText = "Over the last 3 months, has the patient been in contact with the " +
            "criminal justice services?";
        }

        OptionEntry contactWithPolice = factory.createOptionEntry(
                "Been in contact with the just services",
                contactWithPoliceText);
        doc.addEntry(contactWithPolice);
        contactWithPolice.setSection(serviceSec);
        contactWithPolice.setLabel(sec+".5");
        contactWithPolice.addOption(factory.createOption("No", "No", 0));
        Option contactWithPoliceYes = factory.createOption("Yes", "Yes", 1);
        contactWithPolice.addOption(contactWithPoliceYes);

        NarrativeEntry contactPoliceYes = factory.createNarrativeEntry(
                "Contact with police yes",
                "If yes:");
        doc.addEntry(contactPoliceYes);
        contactPoliceYes.setSection(serviceSec);

        NumericEntry numberContactsPolice = factory.createNumericEntry(
                "Number of contacts with police",
                "How many face-to-face contacts with the police?",
                EntryStatus.DISABLED);
        doc.addEntry(numberContactsPolice);
        numberContactsPolice.setSection(serviceSec);
        numberContactsPolice.setDescription(
                "Note: contact = interview or stay of some hours, but not overnight");
        numberContactsPolice.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, numberContactsPolice);

        NumericEntry nightsPrison = factory.createNumericEntry(
                "Nights spent in prison",
                "How many nights spent in a police cell or prison?",
                EntryStatus.DISABLED);
        doc.addEntry(nightsPrison);
        nightsPrison.setSection(serviceSec);
        nightsPrison.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, nightsPrison);

        NumericEntry psychAssessments = factory.createNumericEntry(
                "Number of psychiatric assessments",
                "How many psychiatric assessments whilst in custody?",
                EntryStatus.DISABLED);
        doc.addEntry(psychAssessments);
        psychAssessments.setSection(serviceSec);
        psychAssessments.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, psychAssessments);

        NumericEntry civilCourt = factory.createNumericEntry(
                "Number of civil court appearances",
                "How many civil court appearances?",
                EntryStatus.DISABLED);
        doc.addEntry(civilCourt);
        civilCourt.setSection(serviceSec);
        civilCourt.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, civilCourt);

        NumericEntry criminalCourt = factory.createNumericEntry(
                "Number of criminal court appearances",
                "How many criminal court appearances?",
                EntryStatus.DISABLED);
        doc.addEntry(criminalCourt);
        criminalCourt.setSection(serviceSec);
        criminalCourt.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, criminalCourt);

    }

    public static void addMedicationProfileSection(Document doc, Factory factory, int sectionNumber, boolean followUp){

        Unit mg = UnitWrapper.instance().getUnit("mg");
        Unit ml = UnitWrapper.instance().getUnit("ml");

        String sec = Integer.toString(sectionNumber);

        // Medication profile section
        Section medicationSec = factory.createSection("Medication Profile section");
        doc.addSection(medicationSec);
        medicationSec.setDisplayText("Medication Profile");
        SectionOccurrence medicationSecOcc = factory.createSectionOccurrence(
                "Medication Profile section occurrence");
        medicationSec.addOccurrence(medicationSecOcc);
        medicationSecOcc.setLabel(sec);

        String drugsText = null;
        if ( followUp ){
            drugsText = "Please list " +
            "below use of any drugs taken over the last six months";
        }
        else{
            drugsText = "Please list " +
            "below use of any drugs taken over the last three months";
        }

        CompositeEntry drugs = factory.createComposite("Drugs", drugsText);
        doc.addEntry(drugs);
        drugs.setSection(medicationSec);
        drugs.setLabel(sec+".1");

        TextEntry drug = factory.createTextEntry("Name of drug", "Name of drug");
        drugs.addEntry(drug);
        drug.setSection(medicationSec);

        NumericEntry dosage = factory.createNumericEntry("Dosage",
                "Dosage (if known)");
        drugs.addEntry(dosage);
        dosage.setSection(medicationSec);
        dosage.addUnit(mg);
        dosage.addUnit(ml);

        OptionEntry dosageFrequency = factory.createOptionEntry("Dosage frequency", "Dosage frequency");
        drugs.addEntry(dosageFrequency);
        dosageFrequency.setSection(medicationSec);
        dosageFrequency.addOption(factory.createOption("3 times daily", "3 times daily", 1));
        dosageFrequency.addOption(factory.createOption("2 times daily", "2 times daily", 2));
        dosageFrequency.addOption(factory.createOption("Once daily", "Once daily", 3));
        dosageFrequency.addOption(factory.createOption("Weekly", "Weekly", 4));
        dosageFrequency.addOption(factory.createOption("Every 2 weeks", "Every 2 weeks", 5));
        dosageFrequency.addOption(factory.createOption("Monthly", "Monthly", 6));

        OptionEntry depot = factory.createOptionEntry("Oral/Depot", "Oral/Depot");
        drugs.addEntry(depot);
        depot.setSection(medicationSec);
        depot.addOption(factory.createOption("Oral", "Oral", 0));
        depot.addOption(factory.createOption("Depot", "Depot", 1));

    }

    public static void addInformalCareSection(Document doc, Factory factory, int sectionNumber, boolean followUp){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");
        Unit hours = UnitWrapper.instance().getUnit("hours");

        String sec = Integer.toString(sectionNumber);

        // Informal care section
        Section careSec = factory.createSection("Informal Care section");
        doc.addSection(careSec);
        careSec.setDisplayText("Informal Care");
        SectionOccurrence careSecOcc = factory.createSectionOccurrence(
                "Informal Care section occurrence");
        careSec.addOccurrence(careSecOcc);
        careSecOcc.setLabel(sec);

        String helpText = null;
        if ( followUp ){
            helpText = "In the last 6 months, have you received help from friends "+
            "or relatives on any of the following tasks, as a consequence " +
            "of your mental health problems?";
        }
        else{
            helpText = "In the last 3 months, have you received help from friends "+
            "or relatives on any of the following tasks, as a consequence " +
            "of your mental health problems?";
        }

        CompositeEntry help = factory.createComposite(
                "Help from friends or relatives", helpText);
        doc.addEntry(help);
        help.setSection(careSec);
        help.setLabel(sec+".1");

        OptionEntry typeOfHelp = factory.createOptionEntry("Type of help",
                "Type of help");
        help.addEntry(typeOfHelp);
        typeOfHelp.setSection(careSec);
        typeOfHelp.addOption(factory.createOption(
                "Child Care", "Child Care", 0));
        typeOfHelp.addOption(factory.createOption(
                "Personal care",
                "Personal care (e.g. washing, dressing etc.)", 1));
        typeOfHelp.addOption(factory.createOption(
                "Help in/ around the house",
                "Help in/ around the house (e.g. cooking, cleaning etc.)", 2));
        typeOfHelp.addOption(factory.createOption(
                "Help outside the home",
                "Help outside the home (e.g. shopping, transport etc.)", 3));
        Option typeOfHelpOther = factory.createOption("Other", "Other", 4);
        typeOfHelpOther.setTextEntryAllowed(true);
        typeOfHelp.addOption(typeOfHelpOther);

        NumericEntry hoursHelp = factory.createNumericEntry("Number of hours " +
                "help per week", "Average number of hours help per week");
        help.addEntry(hoursHelp);
        hoursHelp.setSection(careSec);

        String voluntaryAgenciesText = null;
        if ( followUp ){
            voluntaryAgenciesText = "In the last 6 months, have you seen anyone from any voluntary "+
            "agencies for help with anything?";
        }
        else{
            voluntaryAgenciesText = "In the last 3 months, have you seen anyone from any voluntary "+
            "agencies for help with anything?";
        }

        CompositeEntry voluntaryAgencies = factory.createComposite(
                "Voluntary agencies help",
                voluntaryAgenciesText);
        doc.addEntry(voluntaryAgencies);
        voluntaryAgencies.setSection(careSec);
        voluntaryAgencies.setLabel(sec+".2");

        TextEntry vaName = factory.createTextEntry("Voluntary agency name", "Name of voluntary agency");
        voluntaryAgencies.addEntry(vaName);
        vaName.setSection(careSec);

        OptionEntry vaTypeHelp = factory.createOptionEntry("Type of help", "Type of help received");
        voluntaryAgencies.addEntry(vaTypeHelp);
        vaTypeHelp.setSection(careSec);
        vaTypeHelp.addOption(factory.createOption("Telephone counselling / info", 1));
        vaTypeHelp.addOption(factory.createOption("Individual*", 2));
        vaTypeHelp.addOption(factory.createOption("Group", 3));

        NumericEntry vaNumberTimes = factory.createNumericEntry("Number of times", "Number of times visited / telephoned");
        voluntaryAgencies.addEntry(vaNumberTimes);
        vaNumberTimes.setSection(careSec);
        vaNumberTimes.addValidationRule(positive);

        NumericEntry vaDuration = factory.createNumericEntry("Duration", "Duration of contact by phone or person");
        voluntaryAgencies.addEntry(vaDuration);
        vaDuration.setSection(careSec);
        vaDuration.addValidationRule(positive);
        vaDuration.addUnit(hours);

        LongTextEntry natureIndHelp = factory.createLongTextEntry("Nature of individual help",
                "* Please specify nature of individual help");
        doc.addEntry(natureIndHelp);
        natureIndHelp.setSection(careSec);

    }

    public static void addAccomodationDetails(Document doc, Factory factory,
            int sectionNumber){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");
        Unit gbpUnit = UnitWrapper.instance().getUnit("gbp");

        String sec = Integer.toString(sectionNumber);

        //Hospital of Community Accomodation Details section
        Section hospitalSec = factory.createSection(
                "Hospital or Community Accomodation Details section");
        doc.addSection(hospitalSec);
        hospitalSec.setDisplayText("Hospital or Community Accomodation Details");
        SectionOccurrence hospitalSecOcc = factory.createSectionOccurrence(
                "Hospital or Community Accomodation Details section occurrence");
        hospitalSec.addOccurrence(hospitalSecOcc);
        hospitalSecOcc.setLabel(sec);

        NarrativeEntry note = factory.createNarrativeEntry(
                "Hospital section note",
                "Note: This sheet should be completed as soon as possible " +
                "after the patient face-to-face interview. The best source of " +
                "information is likely to be a key worker or facility manager.");
        doc.addEntry(note);
        note.setSection(hospitalSec);

        OptionEntry enterDetails = factory.createOptionEntry("Enter details",
                "Do you want to enter hospital or community accomodation details?");
        doc.addEntry(enterDetails);
        enterDetails.setSection(hospitalSec);
        Option enterDetailsYes = factory.createOption("Yes", 1);
        enterDetails.addOption(enterDetailsYes);
        enterDetails.addOption(factory.createOption("No", 0));

        NumericEntry bedsAvailable = factory.createNumericEntry(
                "Beds available",
                "How many beds/places in the hospital ward or residential " +
                        "facility are currently available?",
                EntryStatus.DISABLED);
        doc.addEntry(bedsAvailable);
        bedsAvailable.setSection(hospitalSec);
        bedsAvailable.setLabel("1.a");
        bedsAvailable.addValidationRule(positive);
        createOptionDependent(factory, enterDetailsYes, bedsAvailable);

        NumericEntry bedsOccupied = factory.createNumericEntry(
                "Beds occupied",
                "How many beds/places in the hospital ward or residential " +
                        "facility are currently occupied?",
                EntryStatus.DISABLED);
        doc.addEntry(bedsOccupied);
        bedsOccupied.setSection(hospitalSec);
        bedsOccupied.setLabel("1.b");
        bedsOccupied.addValidationRule(positive);
        createOptionDependent(factory, enterDetailsYes, bedsOccupied);

        CompositeEntry staffing = factory.createComposite(
                "Staffing table",
                "Please complete the following staffing table (see manual for " +
                        "assistance). Note: only one category per staff member.");
        doc.addEntry(staffing);
        staffing.setSection(hospitalSec);
        staffing.setLabel("2");
        staffing.setEntryStatus(EntryStatus.DISABLED);
        staffing.addRowLabel("Staff with a medical qualification");
        staffing.addRowLabel("Staff with a psychology qualification");
        staffing.addRowLabel("Staff with a nursing qualification");
        staffing.addRowLabel("Staff with a social care qualification");
        staffing.addRowLabel("Staff with no care qualification");
        staffing.addRowLabel("Vacant care staff positions");
        createOptionDependent(factory, enterDetailsYes, staffing);

        TextEntry careStaffCategory = factory.createTextEntry(
                "Care Staff Category",
                "Care staff category");
        staffing.addEntry(careStaffCategory);
        careStaffCategory.setSection(hospitalSec);
        careStaffCategory.setDescription("Note: only one category per staff member");

        NumericEntry posts =  factory.createNumericEntry(
                "Number of posts",
                "Number of 'full-time equivalent' posts");
        staffing.addEntry(posts);
        posts.setSection(hospitalSec);
        posts.addValidationRule(positive);

        NumericEntry cost = factory.createNumericEntry("Annual cost",
                "Total annual cost of care staff category");
        staffing.addEntry(cost);
        cost.setSection(hospitalSec);
        cost.addValidationRule(positive);
        cost.addUnit(gbpUnit);

        CompositeEntry staffingTotals = factory.createComposite("Staffing totals");
        doc.addEntry(staffingTotals);
        staffingTotals.setSection(hospitalSec);
        staffingTotals.setEntryStatus(EntryStatus.DISABLED);
        staffingTotals.addRowLabel("All carestaff categories (total)");
        createOptionDependent(factory, enterDetailsYes, staffingTotals);

        TextEntry careStaffCategoryTotal = factory.createTextEntry(
                "Care staff category",
                "Care staff category");
        staffingTotals.addEntry(careStaffCategoryTotal);
        careStaffCategoryTotal.setSection(hospitalSec);

        DerivedEntry postsTotal = factory.createDerivedEntry(
                "Number of posts total",
                "Number of 'full-time equivalent' posts");
        staffingTotals.addEntry(postsTotal);
        postsTotal.setSection(hospitalSec);
        postsTotal.setAggregateOperator("+");
        postsTotal.setComposite(staffing);
        postsTotal.addVariable("a", posts);
        postsTotal.setFormula("a");

        DerivedEntry costTotal = factory.createDerivedEntry(
                "Total annual costs",
                "Total annual cost of carestaff category");
        staffingTotals.addEntry(costTotal);
        costTotal.setSection(hospitalSec);
        costTotal.setComposite(staffing);
        costTotal.setAggregateOperator("+");
        costTotal.addVariable("a", cost);
        costTotal.setFormula("a");

        NumericEntry facilityCost = factory.createNumericEntry(
                "Annual recurrent cost of the facility",
                "What is the annual recurrent cost of the facility, excluding " +
                        "care staff?, (Include catering, cleaning, etc., but exclude " +
                        "rent and capital costs; See manual)",
                EntryStatus.DISABLED);
        doc.addEntry(facilityCost);
        facilityCost.setSection(hospitalSec);
        facilityCost.setLabel("3");
        facilityCost.addValidationRule(positive);
        facilityCost.addUnit(gbpUnit);
        createOptionDependent(factory, enterDetailsYes, facilityCost);

        NumericEntry weeklyCharge = factory.createNumericEntry(
                "Weekly charge per resident",
                "What is the average weekly charge or fee per " +
                        "resident place/bed?",
                EntryStatus.DISABLED);
        doc.addEntry(weeklyCharge);
        weeklyCharge.setSection(hospitalSec);
        weeklyCharge.setLabel("4");
        weeklyCharge.addValidationRule(positive);
        weeklyCharge.addUnit(gbpUnit);
        createOptionDependent(factory, enterDetailsYes, weeklyCharge);

        CompositeEntry contributesCost = factory.createComposite(
                "Who contributes towards cost of accommodation",
                "Who contributes towards the full cost of this accommodation? " +
                        "(Select all that apply)");
        doc.addEntry(contributesCost);
        contributesCost.setSection(hospitalSec);
        contributesCost.setEntryStatus(EntryStatus.DISABLED);
        contributesCost.setLabel("5");
        createOptionDependent(factory, enterDetailsYes, contributesCost);

        OptionEntry contributesCostOption = factory.createOptionEntry(
                "Who contributes option", "Contributer");
        contributesCost.addEntry(contributesCostOption);
        contributesCostOption.setSection(hospitalSec);
        contributesCostOption.addOption(factory.createOption(
                "National government",
                "National government (health service/insurance fund)", 0));
        contributesCostOption.addOption(factory.createOption(
                "Local government",
                "Local government", 1));
        contributesCostOption.addOption(factory.createOption(
                "Voluntary organisation/charity",
                "Voluntary organisation/charity", 2));
        contributesCostOption.addOption(factory.createOption(
                "Private organisation/company",
                "Private organisation/company", 3));
        contributesCostOption.addOption(factory.createOption(
                "Private individual",
                "Private individual", 4));

    }

}
