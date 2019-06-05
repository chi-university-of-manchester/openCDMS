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
package org.psygrid.neden;

import org.psygrid.common.TransformersWrapper;
import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class PersonalDetails extends AssessmentForm {

    public static Document createDocument(Factory factory){

        ValidationRule postCodeRule = ValidationRulesWrapper.instance().getRule("Validation of UK postcode areas");
        ValidationRule positiveNumber = ValidationRulesWrapper.instance().getRule("Positive");
        ValidationRule notInFuture = ValidationRulesWrapper.instance().getRule("Not in future");
        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");
        Unit mg = UnitWrapper.instance().getUnit("mg");
        Unit ml = UnitWrapper.instance().getUnit("ml");

        //create the personal details document
        Document doc = factory.createDocument("Personal Details",
                "Personal Details Form");

        createDocumentStatuses(factory, doc);

        //-----------------------------------------------------
        //add questions to the document
        //-----------------------------------------------------

        Section mainSec = factory.createSection("Main", "Main Section");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence sectionOcc = factory.createSectionOccurrence("Main");
        mainSec.addOccurrence(sectionOcc);

        TextEntry patientInitials = factory.createTextEntry("Participant site-allocated ID",
                "Participant site-allocated ID", EntryStatus.OPTIONAL);
        doc.addEntry(patientInitials);
        patientInitials.setSection(mainSec);
        //patientInitials.addTransformer(Transformers.getInstance().getTransformer("sha1"));

        DateEntry dob = factory.createDateEntry("DOB", "Date of Birth");
        doc.addEntry(dob);
        dob.setSection(mainSec);
        dob.addValidationRule(notInFuture);
        dob.addValidationRule(after1900);
        dob.addTransformer(TransformersWrapper.instance().getTransformer("date"));

        OptionEntry sex = factory.createOptionEntry(
                "Sex", "Sex");
        sex.addOption(factory.createOption("Male",1));
        sex.addOption(factory.createOption("Female", 2));
        doc.addEntry(sex);
        sex.setSection(mainSec);

        OptionEntry ethnicity = factory.createOptionEntry("Ethnicity",
                "Ethnicity - ONS categorizations of ethnicity below");
		doc.addEntry(ethnicity);
		ethnicity.setSection(mainSec);
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

		TextEntry countryOfBirth = factory.createTextEntry("Country of Birth",
				"Country of Birth", EntryStatus.MANDATORY);
		doc.addEntry(countryOfBirth);
		countryOfBirth.setSection(mainSec);

        //Fluencey in English
        OptionEntry fluency = factory.createOptionEntry("Fluency", "Fluency in English");
        fluency.addOption(factory.createOption("Speaks no english",0));
        fluency.addOption(factory.createOption("Semi fluent (spoken English)",1));
        fluency.addOption(factory.createOption("Fluent - spoken and written",2));
        doc.addEntry(fluency);
        fluency.setSection(mainSec);

        //Religious Cultural Tradition
        OptionEntry religion = factory.createOptionEntry("Religion " +
                "Option", "Religious Cultural Tradition");
        religion.addOption(factory.createOption("Christian",1));
        religion.addOption(factory.createOption("Muslim",2));
        religion.addOption(factory.createOption("Hindu",3));
        religion.addOption(factory.createOption("Sikh",4));
        Option religionOther = factory.createOption("Other (specify)", 5);
        religion.addOption(religionOther);
        religionOther.setTextEntryAllowed(true);
        doc.addEntry(religion);
        religion.setSection(mainSec);

        //Marital Status
        OptionEntry maritalStatus = factory.createOptionEntry("Marital Status",
                "Marital Status");
        maritalStatus.addOption(factory.createOption("Married and cohabiting",1));
        maritalStatus.addOption(factory.createOption("Married but separated",2));
        maritalStatus.addOption(factory.createOption("Cohabiting > 2 yrs",3));
        maritalStatus.addOption(factory.createOption("Cohabiting < 2 yrs",4));
        maritalStatus.addOption(factory.createOption("Single",5));
        maritalStatus.addOption(factory.createOption("Divorced", 6));
        maritalStatus.addOption(factory.createOption("Widowed", 7));
        doc.addEntry(maritalStatus);
        maritalStatus.setSection(mainSec);

        //Marital Status
        OptionEntry divorcedOrWidowed = factory.createOptionEntry("Divorced or Widowed in the last 5 years",
                "Divorced or Widowed in the last 5 years");
        divorcedOrWidowed.addOption(factory.createOption("No",1));
        divorcedOrWidowed.addOption(factory.createOption("Yes",2));
        doc.addEntry(divorcedOrWidowed);
        divorcedOrWidowed.setSection(mainSec);

        TextEntry postCode = factory.createTextEntry("Postcode",
				"Postcode area of " + "residential address");
		postCode.addValidationRule(postCodeRule);
		doc.addEntry(postCode);
		postCode.setSection(mainSec);
		postCode.setEntryStatus(EntryStatus.MANDATORY);

	       //Living Status - At Baseline
        OptionEntry livingStatus = factory.createOptionEntry("Living Status " +
                "Option", "Living Status - At Baseline");
        doc.addEntry(livingStatus);
        livingStatus.setSection(mainSec);
        livingStatus.addOption(factory.createOption("Alone", 1));
        livingStatus.addOption(factory.createOption("With parents/guardians", 2));
        livingStatus.addOption(factory.createOption("With partner", 3));
        Option livingStatusOther = factory.createOption("Other (specify)", 4);
        livingStatus.addOption(livingStatusOther);
        livingStatusOther.setTextEntryAllowed(true);

        //Housing type - At Baseline
        OptionEntry housingType = factory.createOptionEntry("Housing Type Option",
                "Housing Type");
        doc.addEntry(housingType);
        housingType.setSection(mainSec);
        housingType.addOption(factory.createOption("Own home/parents home", 1));
        housingType.addOption(factory.createOption("Rented", 2));
        housingType.addOption(factory.createOption("Supported Accommodation", 3));
        housingType.addOption(factory.createOption("Temp. Accomodation", 4));
        housingType.addOption(factory.createOption("Long Stay Psychiatric Hospital", 5));
        Option housingTypeOther = factory.createOption("Other (specify)", 6);
        housingType.addOption(housingTypeOther);
        housingTypeOther.setTextEntryAllowed(true);

        //Children
        OptionEntry children = factory.createOptionEntry("Children Option",
                "Does the client have children");
        doc.addEntry(children);
        children.setSection(mainSec);
        children.addOption(factory.createOption("No children", 0));
        children.addOption(factory.createOption("Children - no contact at all", 1));
        children.addOption(factory.createOption("Children - not in regular contact (<once per week)", 2));
        children.addOption(factory.createOption("Children - in regular contact (at least once per week)", 3));
        children.addOption(factory.createOption("Children living with client", 4));

        IntegerEntry noChildrenYounger16 = factory.createIntegerEntry("No of " +
                "Children < 16", "No of children < 16 living with the client " +
                "for >50% of the time");
        doc.addEntry(noChildrenYounger16);
        noChildrenYounger16.setSection(mainSec);
        noChildrenYounger16.addValidationRule(positiveNumber);

        //Educational Qualifications Attained
        OptionEntry education = factory.createOptionEntry("Qualifications Option",
                "Educational Qualifications Attained");
        doc.addEntry(education);
        education.setSection(mainSec);
        education.addOption(factory.createOption(
                "No qualifications",0));
        education.addOption(factory.createOption(
                "GCSE/NVQ level 1 or 2",1));
        education.addOption(factory.createOption(
                "A-level/GNVQ/BTEC/NVQ level 3",2));
        education.addOption(factory.createOption(
                "Degree/HND/NVQ level 4 or above",3));
        education.addOption(factory.createOption(
                "Special Needs Educational Qualifications",4));

        //Employment Status - At Baseline
        OptionEntry employment = factory.createOptionEntry("Employment " +
                "Baseline", "Employment Status - At Baseline");
        employment.addOption(factory.createOption("Working (Paid)",1));
        employment.addOption(factory.createOption("Working (Voluntary)",2));
        employment.addOption(factory.createOption("Unemployed",3));
        employment.addOption(factory.createOption("Home maker",4));
        employment.addOption(factory.createOption("Student",5));
        employment.addOption(factory.createOption("Sheltered employment",6));
        Option employmentOther = factory.createOption("Other (specify)", 7);
        employment.addOption(employmentOther);
        employmentOther.setTextEntryAllowed(true);
        doc.addEntry(employment);
        employment.setSection(mainSec);

        //No. of hours worked per week
        OptionEntry hours = factory.createOptionEntry("Hours Worked",
                "No of hours worked per week");
        hours.addOption(factory.createOption("On sick leave",0));
        hours.addOption(factory.createOption("<16 hours",1));
        hours.addOption(factory.createOption(">16 hours<36 hours",2));
        hours.addOption(factory.createOption("36 hours or more",3));
        doc.addEntry(hours);
        hours.setSection(mainSec);

        //Client's Occupation
        TextEntry clientOccupation = factory.createTextEntry("Client's " +
                "Occupation", "Client's Occupation");
        doc.addEntry(clientOccupation);
        clientOccupation.setSection(mainSec);

        //Mother's Occupation
        TextEntry motherOccupation = factory.createTextEntry("Mother's " +
                "Occupation", "Mother's Occupation");
        motherOccupation.setEntryStatus(EntryStatus.OPTIONAL);
        doc.addEntry(motherOccupation);
        motherOccupation.setSection(mainSec);

        //Father's Occupation
        TextEntry fatherOccupation = factory.createTextEntry("Father's " +
                "Occupation", "Father's Occupation");
        fatherOccupation.setEntryStatus(EntryStatus.OPTIONAL);
        doc.addEntry(fatherOccupation);
        fatherOccupation.setSection(mainSec);

        //Partner's Occupation
        TextEntry partnerOccupation = factory.createTextEntry("Partner's " +
                "Occupation", "Partner's Occupation");
        partnerOccupation.setEntryStatus(EntryStatus.OPTIONAL);
        doc.addEntry(partnerOccupation);
        partnerOccupation.setSection(mainSec);

        //Probable Diagnosis At Baseline
        OptionEntry probableDiagnosis = factory.createOptionEntry("Diagnosis " +
                "Option", "Probable Diagnosis at Baseline");
        doc.addEntry(probableDiagnosis);
        probableDiagnosis.setSection(mainSec);
        probableDiagnosis.addOption(factory.createOption("Unspecified Psychosis",
                1));
        probableDiagnosis.addOption(factory.createOption("Schizophrenia",2));
        probableDiagnosis.addOption(factory.createOption("Bi-Polar",3));
        probableDiagnosis.addOption(factory.createOption("Schizo-Affective Disorder",4));
        probableDiagnosis.addOption(factory.createOption("Drug Induced " +
                "Psychosis",5));
        probableDiagnosis.addOption(factory.createOption("Paranoid Psychosis", 6));

        NumericEntry numOfEpisodes = factory.createNumericEntry("No of " +
                "Episodes", "No of Episodes");
        doc.addEntry(numOfEpisodes);
        numOfEpisodes.setSection(mainSec);
        numOfEpisodes.addValidationRule(positiveNumber);

        NumericEntry numOfAdmissions = factory.createNumericEntry("No of " +
                "Admissions", "No of Admissions");
        doc.addEntry(numOfAdmissions);
        numOfAdmissions.setSection(mainSec);
        numOfAdmissions.addValidationRule(positiveNumber);

        NumericEntry ageAtOnset = factory.createNumericEntry("Age at " +
                "Onset", "Age at Onset");
        doc.addEntry(ageAtOnset);
        ageAtOnset.setSection(mainSec);
        ageAtOnset.addValidationRule(positiveNumber);

        NumericEntry ageAtFirstAdmission = factory.createNumericEntry("Age at " +
                "First Admission", "Age at First Admission");
        doc.addEntry(ageAtFirstAdmission);
        ageAtFirstAdmission.setSection(mainSec);
        ageAtFirstAdmission.addValidationRule(positiveNumber);

        //Nature of First Admission
        OptionEntry natureOfFirstAdmission = factory.createOptionEntry("Nature of First Admission",
                "Nature of First Admission");
        doc.addEntry(natureOfFirstAdmission);
        natureOfFirstAdmission.setSection(mainSec);
        natureOfFirstAdmission.addOption(factory.createOption("Voluntary",
                1));
        natureOfFirstAdmission.addOption(factory.createOption("Compulsory",2));
        natureOfFirstAdmission.addOption(factory.createOption("Voluntary then sectioned",3));

        NumericEntry lengthOfFirstAdmission = factory.createNumericEntry("Length of First Admission (in days)",
                "Length of First Admission (in days)");
        doc.addEntry(lengthOfFirstAdmission);
        lengthOfFirstAdmission.setSection(mainSec);
        lengthOfFirstAdmission.addValidationRule(positiveNumber);

        //Nature of Last Admission
        OptionEntry natureOfLastAdmission = factory.createOptionEntry("Nature of Last Admission",
                "Nature of Last Admission");
        doc.addEntry(natureOfLastAdmission);
        natureOfLastAdmission.setSection(mainSec);
        natureOfLastAdmission.addOption(factory.createOption("Voluntary", 1));
        natureOfLastAdmission.addOption(factory.createOption("Compulsory",2));
        natureOfLastAdmission.addOption(factory.createOption("Voluntary then sectioned",3));

        DateEntry dola = factory.createDateEntry("DOLA", "Date of Last Admission");
        doc.addEntry(dola);
        dola.setSection(mainSec);
        dola.addValidationRule(notInFuture);
        dola.addValidationRule(after1900);

        NumericEntry lengthOfLastAdmission = factory.createNumericEntry("Length of Last Admission (in days)",
                "Length of Last Admission (in days)");
        doc.addEntry(lengthOfLastAdmission);
        lengthOfLastAdmission.setSection(mainSec);
        lengthOfLastAdmission.addValidationRule(positiveNumber);

        //Head Injury
        OptionEntry headInjury = factory.createOptionEntry("Head Injury " +
                "in lifetime", "Head injury in lifetime (time spent unconscious)");
        doc.addEntry(headInjury);
        headInjury.setSection(mainSec);
        headInjury.addOption(factory.createOption("Unknown whether injury has occurred",0));
        headInjury.addOption(factory.createOption("Very Mild (<5 mins)",1));
        headInjury.addOption(factory.createOption("Mild (5-60 mins)",2));
        headInjury.addOption(factory.createOption("Moderate (1-24 hours)",3));
        headInjury.addOption(factory.createOption("Severe (1-7 days)",4));
        headInjury.addOption(factory.createOption("Very Severe (1-4 weeks)",5));
        headInjury.addOption(factory.createOption("Extremely Severe (> 4 weeks)",6));
        headInjury.addOption(factory.createOption("Injury - Unknown Severity", 7));

        //Epilepsy
        OptionEntry epilepsy = factory.createOptionEntry("Epilepsy", "Epilepsy");
        doc.addEntry(epilepsy);
        epilepsy.setSection(mainSec);
        epilepsy.addOption(factory.createOption("None",0));
        epilepsy.addOption(factory.createOption("Yes - No medication needed",1));
        epilepsy.addOption(factory.createOption("Yes - Fits controlled by medication",2));
        epilepsy.addOption(factory.createOption("Yes - Medication taken but fits not controlled",3));
        epilepsy.addOption(factory.createOption("Other",4));

        //Previous Antipsychotic Medication
        OptionEntry prevMed = factory.createOptionEntry(
                "Had previous antipsychotic Medication",
                "Has the patient had previous antipsychotic medication?");
        doc.addEntry(prevMed);
        prevMed.setSection(mainSec);
        Option prevMedYes = factory.createOption("Yes", 1);
        prevMed.addOption(prevMedYes);
        prevMed.addOption(factory.createOption("No", 0));

        CompositeEntry prevMedication = factory.createComposite("Previous antipsychotic " +
                "Medication", "Previous antipsychotic Medication");
        doc.addEntry(prevMedication);
        prevMedication.setSection(mainSec);
        prevMedication.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, prevMedYes, prevMedication);

        TextEntry medication = factory.createTextEntry("Medication",
                "Medication");
        prevMedication.addEntry(medication);
        medication.setSection(mainSec);

        NumericEntry dose = factory.createNumericEntry("Dose",
                "Dose");
        prevMedication.addEntry(dose);
        dose.setSection(mainSec);
        dose.addUnit(mg);
        dose.addUnit(ml);
        dose.addValidationRule(positiveNumber);

        TextEntry frequency = factory.createTextEntry("Frequency", "Frequency");
        prevMedication.addEntry(frequency);
        frequency.setSection(mainSec);

        OptionEntry streetDrugOption = factory.createOptionEntry("Past Drug " +
                "Use", "Past Drug Use",
                EntryStatus.MANDATORY);
        doc.addEntry(streetDrugOption);
        streetDrugOption.setSection(mainSec);
        streetDrugOption.addOption(factory.createOption("No", 0));
        Option streetYesOption = factory.createOption("Yes", 1);
        streetDrugOption.addOption(streetYesOption);

        OptionDependent streetOptDep = factory.createOptionDependent();
        streetYesOption.addOptionDependent(streetOptDep);
        streetOptDep.setEntryStatus(EntryStatus.MANDATORY);

        CompositeEntry drugUse = factory.createComposite("Main Drug Use Table");
        doc.addEntry(drugUse);
        streetOptDep.setDependentEntry(drugUse);
        drugUse.setSection(mainSec);
        drugUse.setEntryStatus(EntryStatus.DISABLED);

        OptionEntry drug = factory.createOptionEntry("Drug Option", "Drug",
                EntryStatus.DISABLED);
        drugUse.addEntry(drug);
        drug.setSection(mainSec);
        drug.addOption(factory.createOption("Cannabis", 0));
        drug.addOption(factory.createOption("Amphetamines", 1));
        drug.addOption(factory.createOption("Ecstasy (MDMA)", 2));
        drug.addOption(factory.createOption("LSD", 3));
        drug.addOption(factory.createOption("Hallucinogenic Mushrooms", 4));
        drug.addOption(factory.createOption("Cocaine/Crack", 5));
        drug.addOption(factory.createOption("Heroin/Opiates", 6));
        drug.addOption(factory.createOption("Amyl/Butyl Nitrates", 7));
        drug.addOption(factory.createOption("Solvents", 8));
        drug.addOption(factory.createOption("Khat", 9));
        drug.addOption(factory.createOption("Ketamine", 10));
        drug.addOption(factory.createOption("GHB", 11));
        drug.addOption(factory.createOption("Barbituates", 12));
        drug.addOption(factory.createOption("Over Counter Medication", 13));
        drug.addOption(factory.createOption("Benzodiazepines", 14));
        Option otherOption = factory.createOption("Other Drugs Specify", 15);
        drug.addOption(otherOption);
        otherOption.setTextEntryAllowed(true);

        OptionEntry freqOfUse = factory.createOptionEntry("Frequency",
                "Previous Freq. of Use", EntryStatus.DISABLED);
        drugUse.addEntry(freqOfUse);
        freqOfUse.setSection(mainSec);
        freqOfUse.addOption(factory.createOption("None",0));
        freqOfUse.addOption(factory.createOption("Not more than 3 times",1));
        freqOfUse.addOption(factory.createOption("Occasional user (less than weekly)",2));
        freqOfUse.addOption(factory.createOption("Regular user (1-3 times weekly)",3));
        freqOfUse.addOption(factory.createOption("Frequent user (almost everyday)",4));

        DateEntry durPrevUseFrom =
            factory.createDateEntry("From", "Duration of Previous Use (From)",
                    EntryStatus.DISABLED);
        drugUse.addEntry(durPrevUseFrom);
        durPrevUseFrom.setSection(mainSec);

        DateEntry durPrevUseTo =
            factory.createDateEntry("To", "Duration of Previous Use (To)",
                    EntryStatus.DISABLED);
        drugUse.addEntry(durPrevUseTo);
        durPrevUseTo.setSection(mainSec);

        //Other Information about Client
        OptionEntry otherInfo = factory.createOptionEntry("Other Information " +
                "about Client", "Other Information about Client");
        otherInfo.addOption(factory.createOption("Possible mild learning " +
                "difficulty",1));
        otherInfo.addOption(factory.createOption("Possible moderate learning " +
                "difficulty",2));
        otherInfo.addOption(factory.createOption("Possible autistic spectrum " +
                "disorder",3));
        doc.addEntry(otherInfo);
        otherInfo.setSection(mainSec);

        //Sources of Information Used for Baseline Assessment
        NarrativeEntry sourcesNarrative = factory.createNarrativeEntry("Sources",
               "Sources of Information Used for Baseline Assessment");
        doc.addEntry(sourcesNarrative);
        sourcesNarrative.setSection(mainSec);

        OptionEntry client = factory.createOptionEntry("Client", "Client");
        doc.addEntry(client);
        client.setSection(mainSec);
        client.addOption(factory.createOption("No",1));
        client.addOption(factory.createOption("Yes",2));
        OptionEntry family = factory.createOptionEntry("Family", "Family");
        doc.addEntry(family);
        family.setSection(mainSec);
        family.addOption(factory.createOption("No",1));
        family.addOption(factory.createOption("Yes",2));
        OptionEntry notes = factory.createOptionEntry("Notes", "Notes");
        doc.addEntry(notes);
        notes.setSection(mainSec);
        notes.addOption(factory.createOption("No",1));
        notes.addOption(factory.createOption("Yes",2));
        OptionEntry other = factory.createOptionEntry("Other", "Other");
        doc.addEntry(other);
        other.setSection(mainSec);
        other.addOption(factory.createOption("No",1));
        other.addOption(factory.createOption("Yes",2));

        return doc;
    }

}
