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

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Lucy Bridges
 *
 */
public class PhospitalV1Baseline extends AssessmentForm {

    public static Document createDocument(Factory factory, boolean baseline){

    	Document doc = factory.createDocument("PhospitalV1", "Psychiatric Hospital Record V.1");
    	createDocumentStatuses(factory, doc);

        Section sectionA = factory.createSection("Section A", "Hospital Admissions During Period");
        doc.addSection(sectionA);
        SectionOccurrence secAOcc = factory.createSectionOccurrence("Section A Occ");
        sectionA.addOccurrence(secAOcc);

        String qA1Text = null;
        if ( baseline ){
        	qA1Text = "Was the patient admitted as an inpatient to hospital during the period under observation (last 12 months)?";
        }
        else{
        	qA1Text = "Was the patient admitted as an inpatient to hospital during the period under observation (between BL and 18 months)?";
        }
        OptionEntry qA1 = factory.createOptionEntry("Patient admitted to non psychiatric hospital", qA1Text);
    	doc.addEntry(qA1);
    	qA1.setSection(sectionA);
    	qA1.setLabel("1");

        Option qA1no = factory.createOption("No", "No", 1);
        qA1.addOption(qA1no);
        Option qA1yes = factory.createOption("Yes", "Yes", 2);
        qA1.addOption(qA1yes);

    	CompositeEntry qA2 = factory.createComposite("Admissions", "If Yes, please supply the following information for each admission.");
    	doc.addEntry(qA2);
    	qA2.setSection(sectionA);
    	qA2.setLabel("2");
    	qA2.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qA1yes, qA2);


        //Admission Date
        DateEntry qA3 = factory.createDateEntry("Admission Date", "Admission Date", EntryStatus.DISABLED);
        qA2.addEntry(qA3);
        qA3.setSection(sectionA);
        qA3.addValidationRule(ValidationRulesWrapper.instance().getRule("After 1900"));

        DateEntry qA5 = factory.createDateEntry("Discharge Date", "Discharge Date", EntryStatus.DISABLED);
        qA2.addEntry(qA5);
        qA5.setSection(sectionA);
        qA5.addValidationRule(ValidationRulesWrapper.instance().getRule("After 1900"));

        OptionEntry qA4 = factory.createOptionEntry("Is hospital stay ongoing", "If no discharge date, is hospital stay ongoing?", EntryStatus.DISABLED);
        Option hospStayNo = factory.createOption("No", "No", 1);
        qA4.addOption(hospStayNo);
        Option hospStayYes = factory.createOption("Yes", "Yes", 2);
        qA4.addOption(hospStayYes);
        qA2.addEntry(qA4);
        qA4.setSection(sectionA);


        TextEntry qA6 = factory.createTextEntry("Hospital", "Hospital", EntryStatus.DISABLED);
        qA2.addEntry(qA6);
        qA6.setSection(sectionA);
        TextEntry qA7 = factory.createTextEntry("Department", "Department", EntryStatus.DISABLED);
        qA2.addEntry(qA7);
        qA7.setSection(sectionA);
        TextEntry qA8 = factory.createTextEntry("Ward", "Ward", EntryStatus.DISABLED);
        qA2.addEntry(qA8);
        qA8.setSection(sectionA);
        OptionEntry qA9 = factory.createOptionEntry("Admission from A & E", "Admission from A & E?", EntryStatus.DISABLED);
        qA2.addEntry(qA9);
        qA9.setSection(sectionA);
    	createOptions(factory, qA9, new String[]{"No", "Yes"}, new int[]{1, 2});

    	OptionEntry qA10 = factory.createOptionEntry("Was the patient on temporary leave during the dates specified?", "Was the patient on temporary leave during the dates specified?", EntryStatus.DISABLED);
        qA2.addEntry(qA10);
        qA10.setSection(sectionA);
        Option qA10a = factory.createOption("No", "No", 1);
        qA10.addOption(qA10a);
        Option qA10b = factory.createOption("Yes", "Yes", 2);
        qA10.addOption(qA10b);

    	IntegerEntry qA11 = factory.createIntegerEntry("If Yes, please specify the total number of days temporary leave in this period (Number of days)", "If Yes, please specify the total number of days temporary leave in this period (Number of days)", EntryStatus.DISABLED);
    	qA2.addEntry(qA11);
    	qA11.setSection(sectionA);
    	qA11.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

       	OptionEntry qA12 = factory.createOptionEntry("Was the patient sectioned?", "Was the patient sectioned?", EntryStatus.DISABLED);
       	qA12.setSection(sectionA);
        qA2.addEntry(qA12);
        Option qA12a = factory.createOption("No", "No", 1);
        qA12.addOption(qA12a);
        Option qA12b = factory.createOption("Yes", "Yes", 2);
        qA12.addOption(qA12b);

      	OptionEntry qA13 = factory.createOptionEntry("If Yes, Did the patient have a mental health tribunal during this admission?", "If Yes, Did the patient have a mental health tribunal during this admission?", EntryStatus.DISABLED);
        qA2.addEntry(qA13);
        qA13.setSection(sectionA);
        Option qA13a = factory.createOption("No", "No", 1);
        qA13.addOption(qA13a);
        Option qA13b = factory.createOption("Yes", "Yes", 2);
        qA13.addOption(qA13b);



        Section sectionB = factory.createSection("Section B", "Tests Ordered During Period");
        doc.addSection(sectionB);
        SectionOccurrence secBOcc = factory.createSectionOccurrence("Section B Occ");
        sectionB.addOccurrence(secBOcc);

        OptionEntry qB1 = factory.createOptionEntry("any screens or evaluations to test for drugs, alcohol or other health problems", "Has the patient had any screens or evaluations to test for alcohol or drugs?");
        doc.addEntry(qB1);
    	qB1.setSection(sectionB);
    	qB1.setLabel("1");
        Option qB1No = factory.createOption("No", "No", 1);
        qB1.addOption(qB1No);
        Option qB1Yes = factory.createOption("Yes", "Yes", 2);
        qB1.addOption(qB1Yes);

       	CompositeEntry qB2 = factory.createComposite("If Yes, please supply the following information for each test", "If Yes, please supply the following information for each test.");
    	doc.addEntry(qB2);
    	qB2.setSection(sectionB);
    	qB2.setLabel("2");
    	qB2.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qB1Yes, qB2);

        TextEntry qB2a = factory.createTextEntry("Name of screen or evaluation to test for alcohol or drugs", "Name of screen or evaluation to test for alcohol or drugs", EntryStatus.DISABLED);
        qB2.addEntry(qB2a);
        qB2a.setSection(sectionB);
        IntegerEntry qB2b = factory.createIntegerEntry("Number of screens or evaluations", "Number of screens or evaluations", EntryStatus.DISABLED);
        qB2.addEntry(qB2b);
        qB2b.setSection(sectionB);
        qB2b.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        OptionEntry qB3 = factory.createOptionEntry("Has the patient had any other screens or evaluations or tests", "Has the patient had any other screens or evaluations or tests?");
        doc.addEntry(qB3);
        Option qB3No = factory.createOption("No", "No", 1);
        qB3.addOption(qB3No);
        Option qB3Yes = factory.createOption("Yes", "Yes", 2);
        qB3.addOption(qB3Yes);
        qB3.setSection(sectionB);
    	qB3.setLabel("3");

     	CompositeEntry qB4 = factory.createComposite("If Yes, please supply the following information for each test", "If Yes, please supply the following information for each test.");
    	doc.addEntry(qB4);
    	qB4.setSection(sectionB);
    	qB4.setLabel("4");
    	qB4.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qB3Yes, qB4);

        TextEntry qB4a = factory.createTextEntry("Name of screen or evaluation or test", "Name of screen or evaluation or test", EntryStatus.DISABLED);
        qB4.addEntry(qB4a);
        qB4a.setSection(sectionB);
        IntegerEntry qB4b = factory.createIntegerEntry("Number of screens or evaluations", "Number of screens or evaluations", EntryStatus.DISABLED);
        qB4.addEntry(qB4b);
        qB4b.setSection(sectionB);
        qB4b.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));



        Section sectionC = factory.createSection("Section C", "A & E Attendances During Period");
        doc.addSection(sectionC);
        SectionOccurrence secCOcc = factory.createSectionOccurrence("Section C Occ");
        sectionC.addOccurrence(secCOcc);


        OptionEntry qC1 = factory.createOptionEntry("Did the patient attend any A & E departments during the period under observation", "Did the patient attend any A & E departments during the period under observation?");
        Option qC1No = factory.createOption("No", "No", 1);
        qC1.addOption(qC1No);
        Option qC1Yes = factory.createOption("Yes", "Yes", 2);
        qC1.addOption(qC1Yes);
        doc.addEntry(qC1);
        qC1.setSection(sectionC);
    	qC1.setLabel("1");


        OptionEntry qC2 = factory.createOptionEntry("If Yes, was the patient admitted as an inpatient?", "If Yes, was the patient admitted as an inpatient?");
        Option qC2No = factory.createOption("No", "No", 1);
        qC2.addOption(qC2No);
        Option qC2Yes = factory.createOption("Yes", "Yes", 2);
        qC2.addOption(qC2Yes);
        doc.addEntry(qC2);
        qC2.setSection(sectionC);
    	qC2.setLabel("2");
        qC2.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qC1Yes, qC2);

        NarrativeEntry qNar = factory.createNarrativeEntry("If Yes, please ensure the admission is entered in Section A of this form for psychiatric admissions or Section A of the Non Psychiatric Hospital form for admissions to non psychiatric hospitals", "If Yes, please ensure the admission is entered in Section A of this form for psychiatric admissions or Section A of the Non Psychiatric Hospital form for admissions to non psychiatric hospitals.");
        doc.addEntry(qNar);
        qNar.setSection(sectionC);




        Section sectionD = factory.createSection("Section D", "Day Hospital Attendances During Period");
        doc.addSection(sectionD);
        SectionOccurrence secDOcc = factory.createSectionOccurrence("Section D Occ");
        sectionD.addOccurrence(secDOcc);

        OptionEntry qD1 = factory.createOptionEntry("day hospital attendances", "Did the patient attend an NHS day hospital or day centre where treatment is provided (NOT a day facility provided by a local authority or voluntary/independent organisation) during the period under observation?");
        Option qD1No = factory.createOption("No", "No", 1);
        qD1.addOption(qD1No);
        Option qD1Yes = factory.createOption("Yes", "Yes", 2);
        qD1.addOption(qD1Yes);
        doc.addEntry(qD1);
        qD1.setSection(sectionD);
    	qD1.setLabel("1");

        CompositeEntry qD2 = factory.createComposite("day hospital attendances table", "If Yes, please specify the hospital, department and total number of attendances during the period under observation.");
        doc.addEntry(qD2);
        qD2.setSection(sectionD);
        qD2.setLabel("2");
        qD2.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qD1Yes, qD2);

        TextEntry qD2b = factory.createTextEntry("Hospital", "Hospital", EntryStatus.DISABLED);
        qD2.addEntry(qD2b);
        qD2b.setSection(sectionD);
        TextEntry qD2c = factory.createTextEntry("Department", "Department", EntryStatus.DISABLED);
        qD2.addEntry(qD2c);
        qD2c.setSection(sectionD);
        IntegerEntry qD2d = factory.createIntegerEntry("Number of Attendances", "Number of Attendances", EntryStatus.DISABLED);
        qD2.addEntry(qD2d);
        qD2d.setSection(sectionD);
        qD2d.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        LongTextEntry qD3 = factory.createLongTextEntry("day hospital attendances notes", "How were the attendances calculated?", EntryStatus.DISABLED);
        doc.addEntry(qD3);
        qD3.setSection(sectionD);
        createOptionDependent(factory, qD1Yes, qD3);

        Section sectionE = factory.createSection("Section E", "Number of Psychiatric or Psychology Outpatient Consultations and Domiciliary Visits During Period");
        doc.addSection(sectionE);
        SectionOccurrence secEOcc = factory.createSectionOccurrence("Section E Occ");
        sectionE.addOccurrence(secEOcc);

        OptionEntry qE1 = factory.createOptionEntry("outpatient consultations or visits", "Did the patient have any psychiatric or psychology related outpatient consultations or domiciliary visits during the period under observation?");
        Option qE1No = factory.createOption("No", "No", 1);
        qE1.addOption(qE1No);
        Option qE1Yes = factory.createOption("Yes", "Yes", 2);
        qE1.addOption(qE1Yes);
        doc.addEntry(qE1);
        qE1.setSection(sectionE);
    	qE1.setLabel("1");


         CompositeEntry qE2 = factory.createComposite("Visits Medical", "If Yes to question E1, please specify the hospital, department and number of psychiatric or psychology related consultations and visits by MEDICAL staff during this period.");
         doc.addEntry(qE2);
         qE2.setSection(sectionE);
         qE2.setLabel("2");
         qE2.setEntryStatus(EntryStatus.DISABLED);
         createOptionDependent(factory, qE1Yes, qE2);

         OptionEntry qE2a = factory.createOptionEntry("Personnel, MEDICAL Staff", "Personnel, MEDICAL Staff", EntryStatus.DISABLED);
         qE2.addEntry(qE2a);
         qE2a.setSection(sectionE);
         Option qE2a1 = factory.createOption("Psychiatrist", "Psychiatrist", 1);
         qE2a.addOption(qE2a1);
         Option qE2a2 = factory.createOption("Psychologist", "Psychologist", 2);
         qE2a.addOption(qE2a2);
         Option qE2a3 = factory.createOption("Neurologist", "Neurologist", 3);
         qE2a.addOption(qE2a3);
         Option qE2a4 = factory.createOption("Other (Please Specify)", "Other (Please Specify)", 4);
         qE2a4.setTextEntryAllowed(true);
         qE2a.addOption(qE2a4);

         TextEntry qE2b = factory.createTextEntry("Name of Hospital", "Name of Hospital", EntryStatus.DISABLED);
         qE2.addEntry(qE2b);
         qE2b.setSection(sectionE);
         TextEntry qE2c = factory.createTextEntry("Ward", "Ward", EntryStatus.DISABLED);
         qE2.addEntry(qE2c);
         qE2c.setSection(sectionE);
         IntegerEntry qE2d = factory.createIntegerEntry("Number of Outpatient Consultations", "Number of Outpatient Consultations", EntryStatus.DISABLED);
         qE2.addEntry(qE2d);
         qE2d.setSection(sectionE);
         qE2d.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
         IntegerEntry qE2e = factory.createIntegerEntry("Number of Domiciliary Visits", "Number of Domiciliary Visits", EntryStatus.DISABLED);
         qE2.addEntry(qE2e);
         qE2e.setSection(sectionE);
         qE2e.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

         LongTextEntry qE2z = factory.createLongTextEntry("E2 - how calculated", "How were the number of consultations/visits calculated?", EntryStatus.DISABLED);
         doc.addEntry(qE2z);
         qE2z.setSection(sectionE);
         createOptionDependent(factory, qE1Yes, qE2z);

         CompositeEntry qE3 = factory.createComposite("Visits Medical", "If Yes to question E1, please specify the hospital, department and number of psychiatric or psychology related consultations and visits by NON-MEDICAL staff during this period.");
         doc.addEntry(qE3);
         qE3.setSection(sectionE);
         qE3.setLabel("3");
         qE3.setEntryStatus(EntryStatus.DISABLED);
         createOptionDependent(factory, qE1Yes, qE3);

         OptionEntry qE3a = factory.createOptionEntry("Personnel, NON MEDICAL Staff", "Personnel, NON MEDICAL Staff", EntryStatus.DISABLED);
         qE3.addEntry(qE3a);
         qE3a.setSection(sectionE);
         Option qE3a1 = factory.createOption("Occupational Therapist", "Occupational Therapist", 1);
         qE3a.addOption(qE3a1);
         Option qE3a2 = factory.createOption("Social Worker", "Social Worker", 2);
         qE3a.addOption(qE3a2);
         Option qE3a3 = factory.createOption("CPN", "CPN", 3);
         qE3a.addOption(qE3a3);
         Option qE3a4 = factory.createOption("Nurse", "Nurse", 4);
         qE3a.addOption(qE3a4);
         Option qE3a5 = factory.createOption("Other (Please Specify)", "Other (Please Specify)", 5);
         qE3a5.setTextEntryAllowed(true);
         qE3a.addOption(qE3a5);

         TextEntry qE3b = factory.createTextEntry("Name of Hospital", "Name of Hospital", EntryStatus.DISABLED);
         qE3.addEntry(qE3b);
         qE3b.setSection(sectionE);
         TextEntry qE3c = factory.createTextEntry("Ward", "Ward", EntryStatus.DISABLED);
         qE3.addEntry(qE3c);
         qE3c.setSection(sectionE);
         IntegerEntry qE3d = factory.createIntegerEntry("Number of Outpatient Consultations", "Number of Outpatient Consultations", EntryStatus.DISABLED);
         qE3.addEntry(qE3d);
         qE3d.setSection(sectionE);
         qE3d.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
         IntegerEntry qE3e = factory.createIntegerEntry("Number of Domiciliary Visits", "Number of Domiciliary Visits", EntryStatus.DISABLED);
         qE3.addEntry(qE3e);
         qE3e.setSection(sectionE);
         qE3e.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

         LongTextEntry qE3z = factory.createLongTextEntry("E3 - how calculated", "How were the number of consultations/visits calculated?", EntryStatus.DISABLED);
         doc.addEntry(qE3z);
         qE3z.setSection(sectionE);
         createOptionDependent(factory, qE1Yes, qE3z);

    	return doc;
    }

}
