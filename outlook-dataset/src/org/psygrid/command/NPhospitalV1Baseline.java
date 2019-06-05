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
public class NPhospitalV1Baseline extends AssessmentForm {

    public static Document createDocument(Factory factory, boolean baseline){

    	Document doc = factory.createDocument("NPhospitalV1", "Non-Psychiatric Hospital Record V.1");
    	createDocumentStatuses(factory, doc);

        Section sectionA = factory.createSection("Section A", "Hospital Admissions During Period");
        doc.addSection(sectionA);
        SectionOccurrence secAOcc = factory.createSectionOccurrence("Section A Occ");
        sectionA.addOccurrence(secAOcc);


        String qA1Text = null;
        if ( baseline ){
        	qA1Text = "Was the patient admitted as an inpatient to a non psychiatric hospital during the period under observation (last 12 months)?";
        }
        else{
        	qA1Text = "Was the patient admitted as an inpatient to a non psychiatric hospital during the period under observation (between BL and 18 months)?";
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

        DateEntry admission = factory.createDateEntry("Admission Date", "Admission Date", EntryStatus.DISABLED);
        qA2.addEntry(admission);
        admission.setSection(sectionA);
        admission.addValidationRule(ValidationRulesWrapper.instance().getRule("After 1900"));

        DateEntry discharge = factory.createDateEntry("Discharge Date", "Discharge Date", EntryStatus.DISABLED);
        qA2.addEntry(discharge);
        discharge.setSection(sectionA);
        discharge.addValidationRule(ValidationRulesWrapper.instance().getRule("After 1900"));
        discharge.addValidationRule(ValidationRulesWrapper.instance().getRule("Not in future"));

        OptionEntry hospStay = factory.createOptionEntry("Is hospital stay ongoing", "If no discharge date, is hospital stay ongoing?", EntryStatus.DISABLED);
        qA2.addEntry(hospStay);
        hospStay.setSection(sectionA);
        Option hospStayNo = factory.createOption("No", "No", 1);
        hospStay.addOption(hospStayNo);
        Option hospStayYes = factory.createOption("Yes", "Yes", 2);
        hospStay.addOption(hospStayYes);

        TextEntry hosp = factory.createTextEntry("Hospital", "Hospital", EntryStatus.DISABLED);
        qA2.addEntry(hosp);
        hosp.setSection(sectionA);
        TextEntry dep = factory.createTextEntry("Department", "Department", EntryStatus.DISABLED);
        qA2.addEntry(dep);
        dep.setSection(sectionA);
        TextEntry ward = factory.createTextEntry("Ward", "Ward", EntryStatus.DISABLED);
        qA2.addEntry(ward);
        ward.setSection(sectionA);
        OptionEntry aAndE = factory.createOptionEntry("Admission from A & E", "Admission from A & E?", EntryStatus.DISABLED);
        qA2.addEntry(aAndE);
        aAndE.setSection(sectionA);
    	createOptions(factory, aAndE, new String[]{"No", "Yes"}, new int[]{1, 2});



        Section sectionB = factory.createSection("Section B", "Tests Ordered During Period");
        doc.addSection(sectionB);
        SectionOccurrence secBOcc = factory.createSectionOccurrence("Section B Occ");
        sectionB.addOccurrence(secBOcc);

        OptionEntry qB1 = factory.createOptionEntry("any screens or evaluations to test for drugs, alcohol or other health problems", "Has the patient had any screens or evaluations to test for drugs, alcohol or other health problems (eg cardiac or gastrointestinal problems)?");
        doc.addEntry(qB1);
    	qB1.setSection(sectionB);
    	qB1.setLabel("1");

        Option qB1No = factory.createOption("No", "No", 1);
        qB1.addOption(qB1No);
        Option qB1Yes = factory.createOption("Yes", "Yes", 2);
        qB1.addOption(qB1Yes);

       	CompositeEntry qB2 = factory.createComposite("If Yes, please supply the following information for each admission", "If Yes, please supply the following information for each admission.");
    	doc.addEntry(qB2);
    	qB2.setSection(sectionB);
    	qB2.setLabel("2");
    	qB2.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qB1Yes, qB2);

        TextEntry qB2a = factory.createTextEntry("Name of screen or evaluation to test for other health problems", "Name of screen or evaluation to test for other health problems", EntryStatus.DISABLED);
        qB2.addEntry(qB2a);
        qB2a.setSection(sectionB);

        IntegerEntry qB2b = factory.createIntegerEntry("Number of screens or evaluations", "Number of screens or evaluations", EntryStatus.DISABLED);
        qB2.addEntry(qB2b);
        qB2b.setSection(sectionB);
        qB2b.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));


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

        NarrativeEntry qNar = factory.createNarrativeEntry("If Yes, please ensure the admission is entered in Section A of this form for non psychiatric admissions or Section A of the Psychiatric Hospital form for psychiatric hospital admissions", "If Yes, please ensure the admission is entered in Section A of this form for non psychiatric admissions or Section A of the Psychiatric Hospital form for psychiatric hospital admissions");
        doc.addEntry(qNar);
        qNar.setSection(sectionC);



        Section sectionD = factory.createSection("Section D", "Number of Outpatient Consultations and Domiciliary Visits During Period");
        doc.addSection(sectionD);
        SectionOccurrence secDOcc = factory.createSectionOccurrence("Section D Occ");
        sectionD.addOccurrence(secDOcc);


        OptionEntry qD1 = factory.createOptionEntry("Did the patient have any outpatient consultations or domiciliary visits during the period under observation?", "Did the patient have any outpatient consultations or domiciliary visits during the period under observation?");
        Option qD1No = factory.createOption("No", "No", 1);
        qD1.addOption(qD1No);
        Option qD1Yes = factory.createOption("Yes", "Yes", 2);
        qD1.addOption(qD1Yes);
        doc.addEntry(qD1);
        qD1.setSection(sectionD);
    	qD1.setLabel("1");

        CompositeEntry qD2 = factory.createComposite("Visits", "If Yes, please specify the hospital, department and number of consultations and visits by MEDICAL staff during this period.");
        doc.addEntry(qD2);
        qD2.setSection(sectionD);
        qD2.setLabel("2");
        qD2.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qD1Yes, qD2);

        TextEntry qD2a = factory.createTextEntry("Specialty, MEDICAL Staff", "Specialty, MEDICAL Staff", EntryStatus.DISABLED);
        qD2.addEntry(qD2a);
        qD2a.setSection(sectionD);
        TextEntry qD2b = factory.createTextEntry("Name of Hospital", "Name of Hospital", EntryStatus.DISABLED);
        qD2.addEntry(qD2b);
        qD2b.setSection(sectionD);
        TextEntry qD2c = factory.createTextEntry("Clinic", "Clinic", EntryStatus.DISABLED);
        qD2.addEntry(qD2c);
        qD2c.setSection(sectionD);
        IntegerEntry qD2d = factory.createIntegerEntry("Number of Outpatient Consultations", "Number of Outpatient Consultations", EntryStatus.DISABLED);
        qD2.addEntry(qD2d);
        qD2d.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
        qD2d.setSection(sectionD);
        IntegerEntry qD2e = factory.createIntegerEntry("Number of Domiciliary Visits", "Number of Domiciliary Visits", EntryStatus.DISABLED);
        qD2.addEntry(qD2e);
        qD2e.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
        qD2e.setSection(sectionD);

        LongTextEntry qD2z = factory.createLongTextEntry("D2 - how calculated", "How were the number of consultations/visits calculated?", EntryStatus.DISABLED);
        doc.addEntry(qD2z);
        qD2z.setSection(sectionD);
        createOptionDependent(factory, qD1Yes, qD2z);

        CompositeEntry qD3 = factory.createComposite("Visits Non-Medical", "If Yes, please specify the hospital, department and number of consultations and visits by NON-MEDICAL staff during this period.");
        doc.addEntry(qD3);
        qD3.setSection(sectionD);
        qD3.setLabel("3");
        qD3.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, qD1Yes, qD3);

        OptionEntry qD3a = factory.createOptionEntry("Personnel, NON-MEDICAL Staff", "Personnel, NON-MEDICAL Staff", EntryStatus.DISABLED);
        qD3.addEntry(qD3a);
        qD3a.setSection(sectionD);
        Option qD3a1 = factory.createOption("Occupational Therapist", "Occupational Therapist", 1);
        qD3a.addOption(qD3a1);
        Option qD3a2 = factory.createOption("Social Worker", "Social Worker", 2);
        qD3a.addOption(qD3a2);
        Option qD3a3 = factory.createOption("Nurse", "Nurse", 3);
        qD3a.addOption(qD3a3);
        Option qD3a4 = factory.createOption("Other (Please Specify)", "Other (Please Specify)", 4);
        qD3a4.setTextEntryAllowed(true);
        qD3a.addOption(qD3a4);

        TextEntry qD3b = factory.createTextEntry("Name of Hospital", "Name of Hospital", EntryStatus.DISABLED);
        qD3.addEntry(qD3b);
        qD3b.setSection(sectionD);
        TextEntry qD3c = factory.createTextEntry("Ward", "Ward", EntryStatus.DISABLED);
        qD3.addEntry(qD3c);
        qD3c.setSection(sectionD);
        IntegerEntry qD3d = factory.createIntegerEntry("Number of Outpatient Consultations", "Number of Outpatient Consultations", EntryStatus.DISABLED);
        qD3.addEntry(qD3d);
        qD3d.setSection(sectionD);
        qD2d.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));
        IntegerEntry qD3e = factory.createIntegerEntry("Number of Domiciliary Visits", "Number of Domiciliary Visits", EntryStatus.DISABLED);
        qD3.addEntry(qD3e);
        qD3e.setSection(sectionD);
        qD2e.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        LongTextEntry qD3z = factory.createLongTextEntry("D3 - how calculated", "How were the number of consultations/visits calculated?", EntryStatus.DISABLED);
        doc.addEntry(qD3z);
        qD3z.setSection(sectionD);
        createOptionDependent(factory, qD1Yes, qD3z);

        return doc;
    }

}
