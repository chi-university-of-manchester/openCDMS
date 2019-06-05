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

package org.psygrid.edie;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class NonPsychiatricHospitalRecord extends AssessmentForm {

    public static Document createDocument(Factory factory){

        Document nphs = factory.createDocument("NPHS", "Non-Psychiatric Hospital Record Record");

        createDocumentStatuses(factory, nphs);

        Section sectionA = factory.createSection("Section A", "Section A");
        nphs.addSection(sectionA);
        sectionA.setDisplayText("Hospital Admissions During Period");
        SectionOccurrence sectionAOcc = factory.createSectionOccurrence("Section A Occurrence");
        sectionA.addOccurrence(sectionAOcc);
        sectionAOcc.setLabel("A");

        OptionEntry inpatientAdmission = factory.createOptionEntry("Inpatient Admission",
                "Was the patient admitted " +
                        "as an inpatient to a non psychiatric hospital during the period under observation?");
        nphs.addEntry(inpatientAdmission);
        inpatientAdmission.setSection(sectionA);
        inpatientAdmission.setLabel("A1");
        inpatientAdmission.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");
        inpatientAdmission.addOption(factory.createOption("No", "No", 2));
        Option inpatientAdmissionYes = factory.createOption("Yes", "Yes", 1);
        inpatientAdmission.addOption(inpatientAdmissionYes);

        // Admission details
        CompositeEntry inpatientAdmissionComp = factory.createComposite("Inpatient Admisson Details",
                "Inpatient Admission Deatils");
        nphs.addEntry(inpatientAdmissionComp);
        createOptionDependent(factory, inpatientAdmissionYes, inpatientAdmissionComp);
        inpatientAdmissionComp.setEntryStatus(EntryStatus.DISABLED);
        inpatientAdmissionComp.setSection(sectionA);
        inpatientAdmissionComp.setLabel("A2");
        inpatientAdmissionComp.setDescription("Where information can not be determined, please enter " +
        		"'Data Unable to be Captured'.");

        DateEntry admissionDate = factory.createDateEntry("Admission Date",
                "Admission Date", EntryStatus.DISABLED);
        inpatientAdmissionComp.addEntry(admissionDate);
        admissionDate.setSection(sectionA);

        DateEntry dischargeDate = factory.createDateEntry("Discharge Date",
                "Discharge Date", EntryStatus.DISABLED);
        inpatientAdmissionComp.addEntry(dischargeDate);

        OptionEntry stillInHospital = factory.createOptionEntry("Still In Hospital",
                "If no discharge date, is hospital stay ongoing?", EntryStatus.DISABLED);
        inpatientAdmissionComp.addEntry(stillInHospital);
        stillInHospital.setSection(sectionA);
        buildYesNoOptions(factory, stillInHospital);

        TextEntry hospital = factory.createTextEntry("Hospital",
                "Hospital", EntryStatus.DISABLED);
        inpatientAdmissionComp.addEntry(hospital);
        hospital.setSection(sectionA);

        TextEntry department = factory.createTextEntry("Department",
                "Department", EntryStatus.DISABLED);
        inpatientAdmissionComp.addEntry(department);
        department.setSection(sectionA);

        TextEntry ward = factory.createTextEntry("Ward",
                "Ward", EntryStatus.DISABLED);
        inpatientAdmissionComp.addEntry(ward);
        ward.setSection(sectionA);

        OptionEntry fromAE = factory.createOptionEntry("From A&E",
                "Admission from A&E?", EntryStatus.DISABLED);
        inpatientAdmissionComp.addEntry(fromAE);
        fromAE.setSection(sectionA);
        buildYesNoOptions(factory, fromAE);


        //Section B

        Section sectionB = factory.createSection("Section B", "Section B");
        nphs.addSection(sectionB);
        sectionB.setDisplayText("Tests Ordered During Period");
        SectionOccurrence sectionBOcc = factory.createSectionOccurrence("Section B Occurrence");
        sectionB.addOccurrence(sectionBOcc);
        sectionBOcc.setLabel("B");

        OptionEntry otherTests = factory.createOptionEntry("Other Tests",
                "Has the patient had any screens or evaluations to test for other health problems (e.g. " +
                        "cardiac or gastrointestinal problems)?");
        nphs.addEntry(otherTests);
        otherTests.setSection(sectionB);
        otherTests.setLabel("B1");
        otherTests.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");
        otherTests.addOption(factory.createOption("No", "No", 2));
        Option otherTestsYes = factory.createOption("Yes", "Yes", 1);
        otherTests.addOption(otherTestsYes);

        CompositeEntry otherTestComp = factory.createComposite("Other Test Composite",
                "Other screens or evaluations");
        nphs.addEntry(otherTestComp);
        otherTestComp.setSection(sectionB);
        otherTestComp.setLabel("B2");
        otherTestComp.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, otherTestsYes, otherTestComp);

        TextEntry otherTestName = factory.createTextEntry("Other Test Name",
                "Name of screen or evaluation", EntryStatus.DISABLED);
        otherTestComp.addEntry(otherTestName);
        otherTestName.setSection(sectionB);

        IntegerEntry otherTestScreens = factory.createIntegerEntry("Number of Other Screens",
                "Number of screens or evaluations", EntryStatus.DISABLED);
        otherTestComp.addEntry(otherTestScreens);
        otherTestScreens.setSection(sectionB);

        //Section C

        Section sectionC = factory.createSection("Section C", "Section C");
        nphs.addSection(sectionC);
        sectionC.setDisplayText("A&E Attendances During Period");
        SectionOccurrence sectionCOcc = factory.createSectionOccurrence("Section C Occurrence");
        sectionC.addOccurrence(sectionCOcc);
        sectionCOcc.setLabel("C");
        OptionEntry aandeAttendance = factory.createOptionEntry("A&E Attendance",
                "Did the patient attend any A&E departments during the period under observation?");
        nphs.addEntry(aandeAttendance);
        aandeAttendance.setSection(sectionC);
        aandeAttendance.setLabel("C1");
        aandeAttendance.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");
        aandeAttendance.addOption(factory.createOption("No", "No", 2));
        Option aandeAttendanceYes = factory.createOption("Yes", "Yes", 1);
        aandeAttendance.addOption(aandeAttendanceYes);

        OptionEntry asInpatient = factory.createOptionEntry("As Inpatient",
                "Was the patient admitted as an inpatient", EntryStatus.DISABLED);
        nphs.addEntry(asInpatient);
        asInpatient.setSection(sectionC);
        asInpatient.setLabel("C2");
        asInpatient.addOption(factory.createOption("No", "No", 2));
        asInpatient.addOption(factory.createOption("Yes", "Yes", 1));
        createOptionDependent(factory, aandeAttendanceYes, asInpatient);

        NarrativeEntry asInpatientCheck = factory.createNarrativeEntry("Inpatient Narrative",
                "If YES, please ensure the admission is entered in Section A of this form for " +
                        "non psychiatric admissions or Section A of the Psychiatric Hospital " +
                        "form for psychiatric admissions");
        nphs.addEntry(asInpatientCheck);
        asInpatientCheck.setSection(sectionC);
        asInpatientCheck.setLabel("C3");

        //Section D
        Section sectionD = factory.createSection("Section D", "Section D");
        nphs.addSection(sectionD);
        sectionD.setDisplayText("Number of Outpatient Consultations and Domiciliary Visits" +
        		" During Period");
        SectionOccurrence sectionDOcc = factory.createSectionOccurrence("Section D Occurrence");
        sectionD.addOccurrence(sectionDOcc);
        sectionDOcc.setLabel("D");

        OptionEntry otherOutpatientAttendance = factory.createOptionEntry("Outpatient Attendance",
                "Did the patient have any outpatient consultations or domiciliary visits " +
                        "during the period under observation?");
        nphs.addEntry(otherOutpatientAttendance);
        otherOutpatientAttendance.setSection(sectionD);
        otherOutpatientAttendance.setLabel("D1");
        otherOutpatientAttendance.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");
        otherOutpatientAttendance.addOption(factory.createOption("No", "No", 2));
        Option otherOutpatientAttendanceYes = factory.createOption("Yes", "Yes", 1);
        otherOutpatientAttendance.addOption(otherOutpatientAttendanceYes);

        // Other Outpatient details
        CompositeEntry otherMedOutpatientComp = factory.createComposite("Other Medical Outpatient Details",
                "Details of Other Consultations (MEDICAL)");
        nphs.addEntry(otherMedOutpatientComp);
        createOptionDependent(factory, otherOutpatientAttendanceYes, otherMedOutpatientComp);
        otherMedOutpatientComp.setEntryStatus(EntryStatus.DISABLED);
        otherMedOutpatientComp.setSection(sectionD);
        otherMedOutpatientComp.setLabel("D2");
        otherMedOutpatientComp.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");

        //Personnel
        OptionEntry otherMedConsPersonnel = factory.createOptionEntry("Other Medical Consultations Personnel",
                "Personnel, MEDICAL Staff", EntryStatus.DISABLED);
        otherMedOutpatientComp.addEntry(otherMedConsPersonnel);
        otherMedConsPersonnel.setSection(sectionD);
        otherMedConsPersonnel.addOption(factory.createOption("Psychiatrist", "Psychiatrist", 1));
        otherMedConsPersonnel.addOption(factory.createOption("Psychologist", "Psychologist", 2));
        otherMedConsPersonnel.addOption(factory.createOption("Neurologist", "Neurologist", 3));
        Option otherOtherConsPersonnel = factory.createOption("Other", "Other", 4);
        otherMedConsPersonnel.addOption(otherOtherConsPersonnel);
        otherOtherConsPersonnel.setTextEntryAllowed(true);

        TextEntry otherMedConshospital = factory.createTextEntry("Other Medical Consultations Hospital",
                "Name of Hospital", EntryStatus.DISABLED);
        otherMedOutpatientComp.addEntry(otherMedConshospital);
        otherMedConshospital.setSection(sectionD);

        TextEntry otherMedConsDepartment = factory.createTextEntry("Other Medical Consultations Clinic",
                "Clinic", EntryStatus.DISABLED);
        otherMedOutpatientComp.addEntry(otherMedConsDepartment);
        otherMedConsDepartment.setSection(sectionD);

        IntegerEntry numberMedOutCons = factory.createIntegerEntry("Number Medical Outpatient Consultations",
                "Number of Outpatient Consultations", EntryStatus.DISABLED);
        otherMedOutpatientComp.addEntry(numberMedOutCons);
        numberMedOutCons.setSection(sectionD);

        IntegerEntry numberMedDomVisits = factory.createIntegerEntry("Number of Medical Domiciliary Visits",
                "Number of Domiciliary Visits", EntryStatus.DISABLED);
        otherMedOutpatientComp.addEntry(numberMedDomVisits);
        numberMedDomVisits.setSection(sectionD);

        // Other Outpatient details
        CompositeEntry otherNonMedOutpatientComp = factory.createComposite("Other Non-Medical Outpatient Details",
                "Details of Other Consultations (NON-MEDICAL)");
        nphs.addEntry(otherNonMedOutpatientComp);
        createOptionDependent(factory, otherOutpatientAttendanceYes, otherNonMedOutpatientComp);
        otherNonMedOutpatientComp.setEntryStatus(EntryStatus.DISABLED);
        otherNonMedOutpatientComp.setSection(sectionD);
        otherNonMedOutpatientComp.setLabel("D3");
        otherNonMedOutpatientComp.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");

        //Personnel
        TextEntry otherNonMedConsPersonnel = factory.createTextEntry("Other Non-Medical Consultations Personnel",
                "Personnel, MEDICAL Staff", EntryStatus.DISABLED);
        otherNonMedOutpatientComp.addEntry(otherNonMedConsPersonnel);
        otherNonMedConsPersonnel.setSection(sectionD);

        TextEntry otherNonMedConshospital = factory.createTextEntry("Other Non-Medical Consultations Hospital",
                "Name of Hospital", EntryStatus.DISABLED);
        otherNonMedOutpatientComp.addEntry(otherNonMedConshospital);
        otherNonMedConshospital.setSection(sectionD);

        TextEntry otherNonMedConsDepartment = factory.createTextEntry("Other Non-Medical Consultations Ward",
                "Ward", EntryStatus.DISABLED);
        otherNonMedOutpatientComp.addEntry(otherNonMedConsDepartment);
        otherNonMedConsDepartment.setSection(sectionD);

        IntegerEntry numberNonMedOutCons = factory.createIntegerEntry("Number Non-Medical Outpatient Consultations",
                "Number of Outpatient Consultations", EntryStatus.DISABLED);
        otherNonMedOutpatientComp.addEntry(numberNonMedOutCons);
        numberNonMedOutCons.setSection(sectionD);

        IntegerEntry numberNonMedDomVisits = factory.createIntegerEntry("Number of Non-Medical Domiciliary Visits",
                "Number of Domiciliary Visits", EntryStatus.DISABLED);
        otherNonMedOutpatientComp.addEntry(numberNonMedDomVisits);
        numberNonMedDomVisits.setSection(sectionD);

        return nphs;
    }

    static void buildYesNoOptions(Factory factory, OptionEntry q){
        Option op1 = factory.createOption("No", 0);
        q.addOption(op1);
        Option op2 = factory.createOption("Yes", 1);
        q.addOption(op2);
    }
}
