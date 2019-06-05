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

import org.psygrid.common.TransformersWrapper;
import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;

public class PersonalDetailsWithConsent extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        ValidationRule postCodeRule = ValidationRulesWrapper.instance().getRule("Validation of UK postcodes");
        ValidationRule positiveNumber = ValidationRulesWrapper.instance().getRule("Positive");
        ValidationRule notInFuture = ValidationRulesWrapper.instance().getRule("Not in future");
        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");
        Unit mg = UnitWrapper.instance().getUnit("mg");


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

        OptionEntry hasFixedAbode = factory.createOptionEntry(
                "Has Fixed Abode", "Has Fixed Abode?");
        doc.addEntry(hasFixedAbode);
        hasFixedAbode.setSection(mainSec);
        hasFixedAbode.addOption(factory.createOption("No", 0));
        Option hasFixedAbodeYes = factory.createOption("Yes", 1);
        hasFixedAbode.addOption(hasFixedAbodeYes);

        TextEntry postCode = factory.createTextEntry("Postcode", "Postcode of " +
                "residential address");
        postCode.addValidationRule(postCodeRule);
        doc.addEntry(postCode);
        postCode.setSection(mainSec);
        postCode.setEntryStatus(EntryStatus.DISABLED);
        postCode.addTransformer(TransformersWrapper.instance().getTransformer("postcode"));
        createOptionDependent(factory, hasFixedAbodeYes, postCode);

        OptionEntry ethnicity = factory.createOptionEntry("Ethnicity",
                "Ethnicity - ONS categorizations of ethnicity below");
        doc.addEntry(ethnicity);
        ethnicity.setSection(mainSec);
        ethnicity.addOption(factory.createOption("White - British", 1));
        ethnicity.addOption(factory.createOption("White - Irish", 2));
        ethnicity.addOption(factory.createOption("White - Other White Background", 3));
        ethnicity.addOption(factory.createOption("Asian - Indian", 4));
        ethnicity.addOption(factory.createOption("Asian - Pakistani", 5));
        ethnicity.addOption(factory.createOption("Asian - Bangladeshi", 6));
        ethnicity.addOption(factory.createOption("Asian - Other Asian Background", 7));
        ethnicity.addOption(factory.createOption("Black - Caribbean", 8));
        ethnicity.addOption(factory.createOption("Black - African", 9));
        ethnicity.addOption(factory.createOption("Black - Other Black Background", 10));
        ethnicity.addOption(factory.createOption("Mixed - White and Black Caribbean", 11));
        ethnicity.addOption(factory.createOption("Mixed - White and Black African", 12));
        ethnicity.addOption(factory.createOption("Mixed - White and Asian", 13));
        ethnicity.addOption(factory.createOption("Mixed - Other Mixed Background", 14));
        ethnicity.addOption(factory.createOption("Other Ethnic Groups - Chinese", 15));
        ethnicity.addOption(factory.createOption("Other Ethnic Groups - Other Ethnic Group", 16));

        //Fluencey in English
        OptionEntry fluency = factory.createOptionEntry("Fluency", "Fluency in English");
        fluency.addOption(factory.createOption("Speaks no english",0));
        fluency.addOption(factory.createOption("Semi fluent",1));
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
        maritalStatus.addOption(factory.createOption("Divorced/widowed in the " +
                "last 5 years", 6));
        doc.addEntry(maritalStatus);
        maritalStatus.setSection(mainSec);

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
                "Housing Type - At Baseline");
        doc.addEntry(housingType);
        housingType.setSection(mainSec);
        housingType.addOption(factory.createOption("Own home/parents home", 1));
        housingType.addOption(factory.createOption("Rented", 2));
        housingType.addOption(factory.createOption("Supported Accommodation", 3));
        housingType.addOption(factory.createOption("Temp. Accomodation", 4));
        housingType.addOption(factory.createOption("Student accomodation", 5));
        Option housingTypeOther = factory.createOption("Other (specify)", 6);
        housingType.addOption(housingTypeOther);
        housingTypeOther.setTextEntryAllowed(true);

        IntegerEntry noChildren = factory.createIntegerEntry("No of children",
                "No of Children");
        doc.addEntry(noChildren);
        noChildren.setSection(mainSec);
        noChildren.addValidationRule(positiveNumber);

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
        Option intCollegeOption = factory.createOption(
                "International College/University (specify Country)", 5);
        education.addOption(intCollegeOption);
        intCollegeOption.setTextEntryAllowed(true);

        NumericEntry timeEducation = factory.createNumericEntry("Years in " +
                "Education", "Years in full time education");
        doc.addEntry(timeEducation);
        timeEducation.setSection(mainSec);
        timeEducation.addValidationRule(positiveNumber);

        //Employment Status - At Baseline
        OptionEntry employment = factory.createOptionEntry("Employment " +
                "Baseline", "Employment Status - At Baseline");
        employment.addOption(factory.createOption("Working (Paid)",1));
        employment.addOption(factory.createOption("Working (Voluntary)",2));
        employment.addOption(factory.createOption("Unemployed",3));
        employment.addOption(factory.createOption("Home maker",4));
        employment.addOption(factory.createOption("Student",5));
        doc.addEntry(employment);
        employment.setSection(mainSec);

        //No. of hours worked per week
        OptionEntry hours = factory.createOptionEntry("Hours Worked",
                "No of hours worked per week");
        hours.addOption(factory.createOption("On sick leave",0));
        hours.addOption(factory.createOption("<16 hours",1));
        hours.addOption(factory.createOption(">16 hours<36 hours",2));
        hours.addOption(factory.createOption("36 hours or more",3));
        hours.addOption(factory.createOption("N/A (Unemployed / Home maker)",4));
        doc.addEntry(hours);
        hours.setSection(mainSec);

        //Client's Occupation
        OptionEntry clientOccupation = factory.createOptionEntry("Client's " +
                "Occupation", "Client's Occupation");
        doc.addEntry(clientOccupation);
        clientOccupation.setSection(mainSec);
        clientOccupation.addOption(factory.createOption("Manager and Senior " +
                "Offical", 1));
        clientOccupation.addOption(factory.createOption("Professional", 2));
        clientOccupation.addOption(factory.createOption("Associate Professional " +
                "and Technical", 3));
        clientOccupation.addOption(factory.createOption("Administrative and " +
                "secretarial", 4));
        clientOccupation.addOption(factory.createOption("Skilled trades", 5));
        clientOccupation.addOption(factory.createOption("Personal Service", 6));
        clientOccupation.addOption(factory.createOption("Sales and customer " +
                "service", 7));
        clientOccupation.addOption(factory.createOption("Process, Plant and " +
                "Machine", 8));
        clientOccupation.addOption(factory.createOption("Elementary", 9));

        //Mother's Occupation
        OptionEntry motherOccupation = factory.createOptionEntry("Mother's " +
                "Occupation", "Mother's Occupation");
        motherOccupation.setEntryStatus(EntryStatus.OPTIONAL);
        doc.addEntry(motherOccupation);
        motherOccupation.setSection(mainSec);
        motherOccupation.addOption(factory.createOption("Manager and Senior " +
                "Offical", 1));
        motherOccupation.addOption(factory.createOption("Professional", 2));
        motherOccupation.addOption(factory.createOption("Associate Professional " +
                "and Technical", 3));
        motherOccupation.addOption(factory.createOption("Administrative and " +
                "secretarial", 4));
        motherOccupation.addOption(factory.createOption("Skilled trades", 5));
        motherOccupation.addOption(factory.createOption("Personal Service", 6));
        motherOccupation.addOption(factory.createOption("Sales and customer " +
                "service", 7));
        motherOccupation.addOption(factory.createOption("Process, Plant and " +
                "Machine", 8));
        motherOccupation.addOption(factory.createOption("Elementary", 9));
        motherOccupation.addOption(factory.createOption("Unemployed", 10));

        //Father's Occupation
        OptionEntry fatherOccupation = factory.createOptionEntry("Father's " +
                "Occupation", "Father's Occupation");
        fatherOccupation.setEntryStatus(EntryStatus.OPTIONAL);
        doc.addEntry(fatherOccupation);
        fatherOccupation.setSection(mainSec);
        fatherOccupation.addOption(factory.createOption("Manager and Senior " +
                "Offical", 1));
        fatherOccupation.addOption(factory.createOption("Professional", 2));
        fatherOccupation.addOption(factory.createOption("Associate Professional " +
                "and Technical", 3));
        fatherOccupation.addOption(factory.createOption("Administrative and " +
                "secretarial", 4));
        fatherOccupation.addOption(factory.createOption("Skilled trades", 5));
        fatherOccupation.addOption(factory.createOption("Personal Service", 6));
        fatherOccupation.addOption(factory.createOption("Sales and customer " +
                "service", 7));
        fatherOccupation.addOption(factory.createOption("Process, Plant and " +
                "Machine", 8));
        fatherOccupation.addOption(factory.createOption("Elementary", 9));
        fatherOccupation.addOption(factory.createOption("Unemployed", 10));

        //Partner's Occupation
        OptionEntry partnerOccupation = factory.createOptionEntry("Partner's " +
                "Occupation", "Partner's Occupation");
        partnerOccupation.setEntryStatus(EntryStatus.OPTIONAL);
        doc.addEntry(partnerOccupation);
        partnerOccupation.setSection(mainSec);
        partnerOccupation.addOption(factory.createOption("Manager and Senior " +
                "Offical", 1));
        partnerOccupation.addOption(factory.createOption("Professional", 2));
        partnerOccupation.addOption(factory.createOption("Associate Professional " +
                "and Technical", 3));
        partnerOccupation.addOption(factory.createOption("Administrative and " +
                "secretarial", 4));
        partnerOccupation.addOption(factory.createOption("Skilled trades", 5));
        partnerOccupation.addOption(factory.createOption("Personal Service", 6));
        partnerOccupation.addOption(factory.createOption("Sales and customer " +
                "service", 7));
        partnerOccupation.addOption(factory.createOption("Process, Plant and " +
                "Machine", 8));
        partnerOccupation.addOption(factory.createOption("Elementary", 9));
        partnerOccupation.addOption(factory.createOption("Unemployed", 10));

        //Probable Diagnosis At Baseline
        OptionEntry probableDiagnosis = factory.createOptionEntry("Diagnosis " +
                "Option", "Probable DSM Diagnosis at Baseline");
        doc.addEntry(probableDiagnosis);
        probableDiagnosis.setSection(mainSec);
        probableDiagnosis.addOption(factory.createOption("Schizophreniform Disorder",
                1));
        probableDiagnosis.addOption(factory.createOption("Schizophrenia",2));
        probableDiagnosis.addOption(factory.createOption("Schizoaffective Disorder",3));
        probableDiagnosis.addOption(factory.createOption("Bipolar manic",4));
        probableDiagnosis.addOption(factory.createOption("Major depression with psychosis",5));
        probableDiagnosis.addOption(factory.createOption("Drug Induced " +
                "Psychosis",6));
        probableDiagnosis.addOption(factory.createOption("Delusional Disorder", 7));
        probableDiagnosis.addOption(factory.createOption("Psychosis unspecified",
                8));
        Option diagnosisOther = factory.createOption("Other (specify)", 9);
        probableDiagnosis.addOption(diagnosisOther);
        diagnosisOther.setTextEntryAllowed(true);

        OptionEntry epilepsyOption = factory.createOptionEntry("Epilepsy",
                "Epilepsy");
        doc.addEntry(epilepsyOption);
        epilepsyOption.setSection(mainSec);
        epilepsyOption.addOption(factory.createOption("No", 0));
        epilepsyOption.addOption(factory.createOption("Yes", 1));

        NarrativeEntry glossaryNarrative = factory.createNarrativeEntry(
                "Glossary definition",
                "A disorder characterized by transient but " +
                "recurrent disturbances of brain function that may or may not " +
                "be associated with impairment or loss of consciousness and " +
                "abnormal movements or behaviour.");
        doc.addEntry(glossaryNarrative);
        glossaryNarrative.setSection(mainSec);

        NarrativeEntry ageNarrative = factory.createNarrativeEntry("Up to Age 16",
                "Up to Age 16, number of years spent living in:");
        doc.addEntry(ageNarrative);
        ageNarrative.setSection(mainSec);

        NumericEntry city = factory.createNumericEntry("A city", "A city");
        doc.addEntry(city);
        city.setSection(mainSec);
        city.addValidationRule(positiveNumber);

        NumericEntry suburbs = factory.createNumericEntry("City suburbs",
                "City suburbs");
        doc.addEntry(suburbs);
        suburbs.setSection(mainSec);
        suburbs.addValidationRule(positiveNumber);

        NumericEntry town = factory.createNumericEntry("Smaller Town",
                "Smaller Town");
        doc.addEntry(town);
        town.setSection(mainSec);
        town.addValidationRule(positiveNumber);

        NumericEntry village = factory.createNumericEntry("Village/rural",
                "Village/rural");
        doc.addEntry(village);
        village.setSection(mainSec);
        village.addValidationRule(positiveNumber);

        NumericEntry moves = factory.createNumericEntry("Number of moves of address",
                "Number of moves of address up to the age of 16");
        doc.addEntry(moves);
        moves.setSection(mainSec);
        moves.addValidationRule(positiveNumber);

        //Other Information about Client
        OptionEntry otherInfo = factory.createOptionEntry("Other Information " +
                "about Client", "Other Information about Client");
        otherInfo.addOption(factory.createOption("Possible mild learning " +
                "difficulty",1));
        otherInfo.addOption(factory.createOption("Possible moderate learning " +
                "difficulty",2));
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
        dose.addValidationRule(positiveNumber);

        OptionEntry freq = factory.createOptionEntry("Frequency", "Frequency");
        prevMedication.addEntry(freq);
        freq.setSection(mainSec);
        freq.addOption(factory.createOption("3 times daily", "3 times daily", 1));
        freq.addOption(factory.createOption("2 times daily", "2 times daily", 2));
        freq.addOption(factory.createOption("Once daily", "Once daily", 3));
        freq.addOption(factory.createOption("Weekly", "Weekly", 4));
        freq.addOption(factory.createOption("Every 2 weeks", "Every 2 weeks", 5));
        freq.addOption(factory.createOption("Monthly", "Monthly", 6));

        OptionEntry depot = factory.createOptionEntry("Oral/Depot", "Oral/Depot");
        prevMedication.addEntry(depot);
        depot.setSection(mainSec);
        depot.addOption(factory.createOption("Oral", "Oral", 0));
        depot.addOption(factory.createOption("Depot", "Depot", 1));

        DateEntry dateFirstDose = factory.createDateEntry("Date of first dose", "Date of first dose");
        prevMedication.addEntry(dateFirstDose);
        dateFirstDose.setSection(mainSec);

        //Current Medication
        OptionEntry currentMed = factory.createOptionEntry(
                "Currently receiving medication?",
                "Is the patient currently receiving medication?");
        doc.addEntry(currentMed);
        currentMed.setSection(mainSec);
        Option currentMedYes = factory.createOption("Yes", 1);
        currentMed.addOption(currentMedYes);
        currentMed.addOption(factory.createOption("No", 0));

        CompositeEntry currentMedication = factory.createComposite("Current Medication", "Current Medication");
        doc.addEntry(currentMedication);
        currentMedication.setSection(mainSec);
        currentMedication.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, currentMedYes, currentMedication);

        TextEntry medicationC = factory.createTextEntry("Medication", "Medication");
        currentMedication.addEntry(medicationC);
        medicationC.setSection(mainSec);

        NumericEntry doseC = factory.createNumericEntry("Dose", "Dose");
        currentMedication.addEntry(doseC);
        doseC.setSection(mainSec);
        doseC.addUnit(mg);
        doseC.addValidationRule(positiveNumber);

        OptionEntry freqC = factory.createOptionEntry("Frequency", "Frequency");
        currentMedication.addEntry(freqC);
        freqC.setSection(mainSec);
        freqC.addOption(factory.createOption("3 times daily", "3 times daily", 1));
        freqC.addOption(factory.createOption("2 times daily", "2 times daily", 2));
        freqC.addOption(factory.createOption("Once daily", "Once daily", 3));
        freqC.addOption(factory.createOption("Weekly", "Weekly", 4));
        freqC.addOption(factory.createOption("Every 2 weeks", "Every 2 weeks", 5));
        freqC.addOption(factory.createOption("Monthly", "Monthly", 6));

        OptionEntry currentDepot = factory.createOptionEntry("Oral/Depot", "Oral/Depot");
        currentMedication.addEntry(currentDepot);
        currentDepot.setSection(mainSec);
        currentDepot.addOption(factory.createOption("Oral", "Oral", 0));
        currentDepot.addOption(factory.createOption("Depot", "Depot", 1));

        return doc;
    }
}
