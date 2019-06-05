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


package org.psygrid.drn.address;

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class MedicalDetails extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document medicalDetails = factory.createDocument("Diabetes and Medical Details",
                "Diabetes and Medical Details");

        createDocumentStatuses(factory, medicalDetails);

        //General section
        Section generalSection = factory.createSection("General section");
        medicalDetails.addSection(generalSection);
        generalSection.setDisplayText("General");
        SectionOccurrence generalSectionOcc = factory.createSectionOccurrence("General Section Occurrence");
        generalSection.addOccurrence(generalSectionOcc);

        DateEntry dateDiagnosis = factory.createDateEntry("Date of diagnosis", "Date of diagnosis");
        medicalDetails.addEntry(dateDiagnosis);
        dateDiagnosis.setSection(generalSection);
        dateDiagnosis.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));
        dateDiagnosis.addValidationRule(ValidationRulesWrapper.instance().getRule("2007 or after"));
        dateDiagnosis.setDisableStandardCodes(true);

        IntegerEntry ageDiagnosis = factory.createIntegerEntry("Age at diagnosis", "Age at diagnosis");
        medicalDetails.addEntry(ageDiagnosis);
        ageDiagnosis.setSection(generalSection);
        ageDiagnosis.addValidationRule(ValidationRulesWrapper.instance().getRule("0 to 99"));

        //Diabetes classification section
        Section diabetesSection = factory.createSection("Diabetes section");
        medicalDetails.addSection(diabetesSection);
        diabetesSection.setDisplayText("Diabetes classification");
        SectionOccurrence diabetesSectionOcc = factory.createSectionOccurrence("Diabetes Section Occurrence");
        diabetesSection.addOccurrence(diabetesSectionOcc);

        OptionEntry diabetesType = factory.createOptionEntry("Type of diabetes",
                "Type of diabetes (if untested, as determined by diagnosing clinician)");
        medicalDetails.addEntry(diabetesType);
        diabetesType.setSection(diabetesSection);
        createOptions(factory, diabetesType,
        		new String[]{"Type 1", "Maturity onset diabetes of the young (MODY)", "Latent autoimmune diabetes in adults (LADA)", "Other (please specify)"},
        		new int[]{1,3,4,5});
        Option typeOther = diabetesType.getOption(3);

        TextEntry typeOtherSpecifics = factory.createTextEntry("Type of diabetes - other - specifics", "Type of diabetes - other - specifics", EntryStatus.DISABLED);
        medicalDetails.addEntry(typeOtherSpecifics);
        typeOtherSpecifics.setSection(diabetesSection);
        createOptionDependent(factory, typeOther, typeOtherSpecifics);

        LongTextEntry diabetesTypeComments = factory.createLongTextEntry("Diabetes type comments", "Comments on type of diabetes", EntryStatus.OPTIONAL);
        medicalDetails.addEntry(diabetesTypeComments);
        diabetesTypeComments.setSection(diabetesSection);

        //Medical and social history section
        Section historySection = factory.createSection("History section");
        medicalDetails.addSection(historySection);
        historySection.setDisplayText("Medical and social history");
        SectionOccurrence historySectionOcc = factory.createSectionOccurrence("History Section Occurrence");
        historySection.addOccurrence(historySectionOcc);

        OptionEntry otherMedConditions = factory.createOptionEntry("Other medical conditions", "Other medical conditions");
        medicalDetails.addEntry(otherMedConditions);
        otherMedConditions.setSection(historySection);
        createOptions(factory, otherMedConditions, new String[]{"Yes","No"}, new int[]{1,0});
        Option otherMedConditionsYes = otherMedConditions.getOption(0);

        LongTextEntry otherMedConditionsComments = factory.createLongTextEntry("Comments on other medical conditions",
                "Comments on other medical conditions", EntryStatus.DISABLED);
        medicalDetails.addEntry(otherMedConditionsComments);
        otherMedConditionsComments.setSection(historySection);
        createOptionDependent(factory, otherMedConditionsYes, otherMedConditionsComments);

        OptionEntry gestationalDiabetes = factory.createOptionEntry("Gestational diabetes", "Gestational diabetes (adult women only)");
        medicalDetails.addEntry(gestationalDiabetes);
        gestationalDiabetes.setSection(historySection);
        createOptions(factory, gestationalDiabetes, new String[]{"Yes", "No"}, new int[]{1,0});

        OptionEntry smoking = factory.createOptionEntry("Smoking", "Smoking");
        medicalDetails.addEntry(smoking);
        smoking.setSection(historySection);
        createOptions(factory, smoking,
        		new String[]{"Present smoker", "Occasional smoker", "Past smoker", "Never smoked"},
        		new int[]{3,2,1,0});

        //Medication section
        Section medicationSection = factory.createSection("Medication section");
        medicalDetails.addSection(medicationSection);
        medicationSection.setDisplayText("Medication");
        SectionOccurrence medicationSectionOcc = factory.createSectionOccurrence("Medication Section Occurrence");
        medicationSection.addOccurrence(medicationSectionOcc);

        OptionEntry insulin = factory.createOptionEntry("Insulin", "Insulin");
        medicalDetails.addEntry(insulin);
        insulin.setSection(medicationSection);
        createOptions(factory, insulin, new String[]{"Yes","No"}, new int[]{1,0});
        Option insulinYes = insulin.getOption(0);

        String[] insulinTypeNames = new String[]{"Subcutaneous injection", "Continuous subcutaneous insulin infusion", "Inhaled insulin"};
        for ( int i=0, c=insulinTypeNames.length; i<c; i++ ){
            OptionEntry oe = factory.createOptionEntry(insulinTypeNames[i], insulinTypeNames[i], EntryStatus.DISABLED);
            medicalDetails.addEntry(oe);
            oe.setSection(medicationSection);
            createOptions(factory, oe, new String[]{"Yes", "No"}, new int[]{1,0});
            createOptionDependent(factory, insulinYes, oe);
        }

        OptionEntry ohas = factory.createOptionEntry("OHAs", "Oral hypoglycaemic agents (OHAs)");
        medicalDetails.addEntry(ohas);
        ohas.setSection(medicationSection);
        createOptions(factory, ohas, new String[]{"Yes","No"}, new int[]{1,0});
        Option ohasYes = ohas.getOption(0);

        String[] ohasTypeNames = new String[]{"Sulphonylureas", "Metformin", "Other treatment (please specify)"};
        for ( int i=0, c=ohasTypeNames.length; i<c; i++ ){
            OptionEntry oe = factory.createOptionEntry(ohasTypeNames[i], ohasTypeNames[i], EntryStatus.DISABLED);
            medicalDetails.addEntry(oe);
            oe.setSection(medicationSection);
            createOptions(factory, oe, new String[]{"Yes", "No"}, new int[]{1,0});
            createOptionDependent(factory, ohasYes, oe);
            if ( ohasTypeNames[i].equals("Other treatment (please specify)") ){
            	TextEntry te = factory.createTextEntry("Other treatment - specifics", "Other treatment - specifics", EntryStatus.DISABLED);
            	medicalDetails.addEntry(te);
                te.setSection(medicationSection);
                createOptionDependent(factory, oe.getOption(0), te);
            }
        }

        LongTextEntry diabTreatComments = factory.createLongTextEntry("Comments on diabetes treatment",
                "Comments on diabetes treatment", EntryStatus.OPTIONAL);
        medicalDetails.addEntry(diabTreatComments);
        diabTreatComments.setSection(medicationSection);

        OptionEntry otherMed = factory.createOptionEntry("Other medication", "Other medication");
        medicalDetails.addEntry(otherMed);
        otherMed.setSection(medicationSection);
        createOptions(factory, otherMed, new String[]{"Yes","No"}, new int[]{1,0});
        Option otherMedYes = otherMed.getOption(0);

        LongTextEntry otherMedComments = factory.createLongTextEntry("Comments on other medication",
                "Comments on other medication", EntryStatus.DISABLED);
        medicalDetails.addEntry(otherMedComments);
        otherMedComments.setSection(medicationSection);
        createOptionDependent(factory, otherMedYes, otherMedComments);

        //General comments section
        Section commentsSection = factory.createSection("Comments section");
        medicalDetails.addSection(commentsSection);
        commentsSection.setDisplayText("General comments");
        SectionOccurrence commentsSectionOcc = factory.createSectionOccurrence("Comments Section Occurrence");
        commentsSection.addOccurrence(commentsSectionOcc);

        LongTextEntry generalComments = factory.createLongTextEntry("General comments", "General comments", EntryStatus.OPTIONAL);
        medicalDetails.addEntry(generalComments);
        generalComments.setSection(commentsSection);


        return medicalDetails;
    }
}
