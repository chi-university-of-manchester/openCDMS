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

public class PsychiatricHospitalRecord extends AssessmentForm {

    public static Document createDocument(Factory factory){

        Document phs = factory.createDocument("PHS", "Psychiatric Hospital Record Record");

        createDocumentStatuses(factory, phs);

        Section sectionA = factory.createSection("Section A", "Section A");
        phs.addSection(sectionA);
        sectionA.setDisplayText("Hospital Admissions During Period");
        SectionOccurrence sectionAOcc = factory.createSectionOccurrence("Section A Occurrence");
        sectionA.addOccurrence(sectionAOcc);
        sectionAOcc.setLabel("A");

        OptionEntry inpatientAdmission = factory.createOptionEntry("Inpatient Admission",
                "Was the patient admitted " +
                        "as an inpatient to hospital during the period under observation?");
        phs.addEntry(inpatientAdmission);
        inpatientAdmission.setSection(sectionA);
        inpatientAdmission.setLabel("A1");
        inpatientAdmission.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");
        inpatientAdmission.addOption(factory.createOption("No", "No", 1));
        Option inpatientAdmissionYes = factory.createOption("Yes", "Yes", 2);
        inpatientAdmission.addOption(inpatientAdmissionYes);

        // Admission details
        CompositeEntry inpatientAdmissionComp = factory.createComposite("Inpatient Admisson Details",
                "Inpatient Admission Deatils");
        phs.addEntry(inpatientAdmissionComp);
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

        OptionEntry tempLeave = factory.createOptionEntry("Temporary Leave",
                "Was the patient on temporary leave during the dates specified above?",
                EntryStatus.DISABLED);
        inpatientAdmissionComp.addEntry(tempLeave);
        tempLeave.setSection(sectionA);
        buildYesNoOptions(factory, tempLeave);

        IntegerEntry tempLeaveDuration = factory.createIntegerEntry("Temporary Leave Duration",
                "Specifiy the total number of days temporary leave in this period?",
                EntryStatus.DISABLED);
        inpatientAdmissionComp.addEntry(tempLeaveDuration);
        tempLeaveDuration.setSection(sectionA);

        OptionEntry sectioned = factory.createOptionEntry("Sectioned",
                "Was the patient sectioned?",
                EntryStatus.DISABLED);
        inpatientAdmissionComp.addEntry(sectioned);
        sectioned.setSection(sectionA);
        buildYesNoOptions(factory, sectioned);

        OptionEntry tribunal = factory.createOptionEntry("Tribunal",
                "Did the patient have a mental health tribunal during this admission?",
                EntryStatus.DISABLED);
        inpatientAdmissionComp.addEntry(tribunal);
        tribunal.setSection(sectionA);
        buildYesNoOptions(factory, tribunal);

        //Section B

        Section sectionB = factory.createSection("Section B", "Section B");
        phs.addSection(sectionB);
        sectionB.setDisplayText("Test Ordered During Period");
        SectionOccurrence sectionBOcc = factory.createSectionOccurrence("Section B Occurrence");
        sectionB.addOccurrence(sectionBOcc);
        sectionBOcc.setLabel("B");

        OptionEntry drugTests = factory.createOptionEntry("Drug Tests",
                "Has the patient had any screens or evaluations to test for alcohol or drugs?");
        phs.addEntry(drugTests);
        drugTests.setSection(sectionB);
        drugTests.setLabel("B1");
        drugTests.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");
        drugTests.addOption(factory.createOption("No", "No", 1));
        Option drugTestsYes = factory.createOption("Yes", "Yes", 2);
        drugTests.addOption(drugTestsYes);

        CompositeEntry drugTestComp = factory.createComposite("Drug Test Composite",
                "Name of screen or evaluation to test for alcohol or drugs");
        phs.addEntry(drugTestComp);
        drugTestComp.setSection(sectionB);
        drugTestComp.setLabel("B2");
        drugTestComp.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, drugTestsYes, drugTestComp);

        TextEntry drugTestName = factory.createTextEntry("Drug Test Name",
                "Name", EntryStatus.DISABLED);
        drugTestComp.addEntry(drugTestName);
        drugTestName.setSection(sectionB);

        IntegerEntry drugTestScreens = factory.createIntegerEntry("Number of Drug Screens",
                "Number of screens or evaluations", EntryStatus.DISABLED);
        drugTestComp.addEntry(drugTestScreens);
        drugTestScreens.setSection(sectionB);

        OptionEntry medTests = factory.createOptionEntry("Medication Tests",
                "Has the patient had any screens or evaluations to test for anitpsychotic medications?");
        phs.addEntry(medTests);
        medTests.setSection(sectionB);
        medTests.setLabel("B3");
        medTests.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");
        medTests.addOption(factory.createOption("No", "No", 1));
        Option medTestsYes = factory.createOption("Yes", "Yes", 2);
        medTests.addOption(medTestsYes);

        CompositeEntry medTestComp = factory.createComposite("Anitpsychotic Test Composite",
                "Name of screen or evaluation to test for prescribed antipsychotic medications");
        phs.addEntry(medTestComp);
        medTestComp.setSection(sectionB);
        medTestComp.setLabel("B4");
        medTestComp.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, medTestsYes, medTestComp);

        TextEntry medTestName = factory.createTextEntry("Drug Test Name",
                "Name", EntryStatus.DISABLED);
        medTestComp.addEntry(medTestName);
        medTestName.setSection(sectionB);

        IntegerEntry medTestScreens = factory.createIntegerEntry("Number of Drug Screens",
                "Number of screens or evaluations", EntryStatus.DISABLED);
        medTestComp.addEntry(medTestScreens);
        medTestScreens.setSection(sectionB);

        OptionEntry otherTests = factory.createOptionEntry("Other Tests",
                "Has the patient had any screens or evaluations to test for other health problems " +
                        "(eg cardiac or gastrointestinal problems)?");
        phs.addEntry(otherTests);
        otherTests.setSection(sectionB);
        otherTests.setLabel("B5");
        otherTests.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");
        otherTests.addOption(factory.createOption("No", "No", 1));
        Option otherTestsYes = factory.createOption("Yes", "Yes", 2);
        otherTests.addOption(otherTestsYes);

        CompositeEntry otherTestComp = factory.createComposite("Other Test Composite",
                "Name of screen or evaluation to test for other health problems");
        phs.addEntry(otherTestComp);
        otherTestComp.setSection(sectionB);
        otherTestComp.setLabel("B6");
        otherTestComp.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, otherTestsYes, otherTestComp);

        TextEntry otherTestName = factory.createTextEntry("Other Test Name",
                "Name", EntryStatus.DISABLED);
        otherTestComp.addEntry(otherTestName);
        otherTestName.setSection(sectionB);

        IntegerEntry otherTestScreens = factory.createIntegerEntry("Number of Other Screens",
                "Number of screens or evaluations", EntryStatus.DISABLED);
        otherTestComp.addEntry(otherTestScreens);
        otherTestScreens.setSection(sectionB);

        //Section C

        Section sectionC = factory.createSection("Section C", "Section C");
        phs.addSection(sectionC);
        sectionC.setDisplayText("A&E Attendances During Period");
        SectionOccurrence sectionCOcc = factory.createSectionOccurrence("Section C Occurrence");
        sectionC.addOccurrence(sectionCOcc);
        sectionCOcc.setLabel("C");

        OptionEntry aandeAttendance = factory.createOptionEntry("A&E Attendance",
                "Did the patient attend any A&E departments during the period under observation?");
        phs.addEntry(aandeAttendance);
        aandeAttendance.setSection(sectionC);
        aandeAttendance.setLabel("C1");
        aandeAttendance.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");
        aandeAttendance.addOption(factory.createOption("No", "No", 1));
        Option aandeAttendanceYes = factory.createOption("Yes", "Yes", 2);
        aandeAttendance.addOption(aandeAttendanceYes);

        OptionEntry asInpatient = factory.createOptionEntry("As Inpatient",
                "Was the patient admitted as an inpatient?", EntryStatus.DISABLED);
        phs.addEntry(asInpatient);
        asInpatient.setSection(sectionC);
        asInpatient.setLabel("C2");
        asInpatient.addOption(factory.createOption("No", "No", 1));
        asInpatient.addOption(factory.createOption("Yes", "Yes", 2));
        createOptionDependent(factory, aandeAttendanceYes, asInpatient);

        NarrativeEntry asInpatientCheck = factory.createNarrativeEntry("Inpatient narrative",
                "If Yes, " +
                        "please ensure the admission is entered in Section A of this form " +
                        "for psychiatric admissions or Section A of the Non-Psychiatric " +
                        "Hospital form for non-psychiatric admissions");
        phs.addEntry(asInpatientCheck);
        asInpatientCheck.setSection(sectionC);
        asInpatientCheck.setLabel("C3");

        //Section D
        Section sectionD = factory.createSection("Section D", "Day Hospital Attendances During Period");
        phs.addSection(sectionD);
        SectionOccurrence sectionDOcc = factory.createSectionOccurrence("Section D Occurrence");
        sectionD.addOccurrence(sectionDOcc);
        sectionDOcc.setLabel("D");

        OptionEntry nhsOutpatientAttendance = factory.createOptionEntry("NHS Outpatient Attendance",
                "Did the patient attend an NHS day hospital or day centre where " +
                        "treatment is provided (NOT a day facility provided by the local authority or voluntary" +
                        "/independent organisation) during the period under observation?");
        phs.addEntry(nhsOutpatientAttendance);
        nhsOutpatientAttendance.setSection(sectionD);
        nhsOutpatientAttendance.setLabel("D1");
        nhsOutpatientAttendance.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");
        nhsOutpatientAttendance.addOption(factory.createOption("No", "No", 2));
        Option nhsOutpatientAttendanceYes = factory.createOption("Yes", "Yes", 1);
        nhsOutpatientAttendance.addOption(nhsOutpatientAttendanceYes);

        // NHS Outpatient details
        CompositeEntry nhsOutpatientComp = factory.createComposite("NHS Outpatient Admisson Details",
                "Day Hospital Details");
        phs.addEntry(nhsOutpatientComp);
        createOptionDependent(factory, nhsOutpatientAttendanceYes, nhsOutpatientComp);
        nhsOutpatientComp.setEntryStatus(EntryStatus.DISABLED);
        nhsOutpatientComp.setSection(sectionD);
        nhsOutpatientComp.setLabel("D2");
        nhsOutpatientComp.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");


        TextEntry nhsOuthospital = factory.createTextEntry("NHS Outpatient Hospital",
                "Hospital", EntryStatus.DISABLED);
        nhsOutpatientComp.addEntry(nhsOuthospital);
        nhsOuthospital.setSection(sectionD);

        TextEntry nhsOutDepartment = factory.createTextEntry("NHS Outpatient Department",
                "Department", EntryStatus.DISABLED);
        nhsOutpatientComp.addEntry(nhsOutDepartment);
        nhsOutDepartment.setSection(sectionD);

        IntegerEntry nhsOutAttendances = factory.createIntegerEntry("NHS Outpatient Attendances",
                "Number of Attendances", EntryStatus.DISABLED);
        nhsOutpatientComp.addEntry(nhsOutAttendances);
        nhsOutAttendances.setSection(sectionD);

        //Section E
        Section sectionE = factory.createSection("Section E", "Number of Psychiatric or Psychology Outpatient " +
                "Consultations and Domiciliary Visits During Period");
        phs.addSection(sectionE);
        SectionOccurrence sectionEOcc = factory.createSectionOccurrence("Section E Occurrence");
        sectionE.addOccurrence(sectionEOcc);
        sectionEOcc.setLabel("E");

        OptionEntry otherOutpatientAttendance = factory.createOptionEntry("Other Outpatient Attendance",
                "Did the patient have any psychaitric or psychology related outpatient consultations or domiciliary visits " +
                        "during the period under observation?");
        phs.addEntry(otherOutpatientAttendance);
        otherOutpatientAttendance.setSection(sectionE);
        otherOutpatientAttendance.setLabel("E1");
        otherOutpatientAttendance.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");
        otherOutpatientAttendance.addOption(factory.createOption("No", "No", 2));
        Option otherOutpatientAttendanceYes = factory.createOption("Yes", "Yes", 1);
        otherOutpatientAttendance.addOption(otherOutpatientAttendanceYes);

        // Other Outpatient details
        CompositeEntry otherMedOutpatientComp = factory.createComposite("Other Medical Outpatient Details",
                "Details of Other Consultations (MEDICAL)");
        phs.addEntry(otherMedOutpatientComp);
        createOptionDependent(factory, otherOutpatientAttendanceYes, otherMedOutpatientComp);
        otherMedOutpatientComp.setEntryStatus(EntryStatus.DISABLED);
        otherMedOutpatientComp.setSection(sectionE);
        otherMedOutpatientComp.setLabel("E2");
        otherMedOutpatientComp.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");

        //Personnel
        OptionEntry otherMedConsPersonnel = factory.createOptionEntry("Other Medical Consultations Personnel",
                "Personnel, MEDICAL Staff", EntryStatus.DISABLED);
        otherMedOutpatientComp.addEntry(otherMedConsPersonnel);
        otherMedConsPersonnel.setSection(sectionE);
        otherMedConsPersonnel.addOption(factory.createOption("Psychiatrist", "Psychiatrist", 1));
        otherMedConsPersonnel.addOption(factory.createOption("Psychologist", "Psychologist", 2));
        otherMedConsPersonnel.addOption(factory.createOption("Neurologist", "Neurologist", 3));
        Option otherOtherConsPersonnel = factory.createOption("Other", "Other", 4);
        otherMedConsPersonnel.addOption(otherOtherConsPersonnel);
        otherOtherConsPersonnel.setTextEntryAllowed(true);

        TextEntry otherMedConshospital = factory.createTextEntry("Other Medical Consultations Hospital",
                "Name of Hospital", EntryStatus.DISABLED);
        otherMedOutpatientComp.addEntry(otherMedConshospital);
        otherMedConshospital.setSection(sectionE);

        TextEntry otherMedConsDepartment = factory.createTextEntry("Other Medical Consultations Ward",
                "Ward", EntryStatus.DISABLED);
        otherMedOutpatientComp.addEntry(otherMedConsDepartment);
        otherMedConsDepartment.setSection(sectionE);

        IntegerEntry numberMedOutCons = factory.createIntegerEntry("Number Medical Outpatient Consultations",
                "Number of Outpatient Consultations", EntryStatus.DISABLED);
        otherMedOutpatientComp.addEntry(numberMedOutCons);
        numberMedOutCons.setSection(sectionE);

        IntegerEntry numberMedDomVisits = factory.createIntegerEntry("Number of Medical Domiciliary Visits",
                "Number of Domiciliary Visits", EntryStatus.DISABLED);
        otherMedOutpatientComp.addEntry(numberMedDomVisits);
        numberMedDomVisits.setSection(sectionE);

        // Other Outpatient details
        CompositeEntry otherNonMedOutpatientComp = factory.createComposite("Other N0n-Medical Outpatient Details",
                "Details of Other Consultations (NON-MEDICAL)");
        phs.addEntry(otherNonMedOutpatientComp);
        createOptionDependent(factory, otherOutpatientAttendanceYes, otherNonMedOutpatientComp);
        otherNonMedOutpatientComp.setEntryStatus(EntryStatus.DISABLED);
        otherNonMedOutpatientComp.setSection(sectionE);
        otherNonMedOutpatientComp.setLabel("E3");
        otherNonMedOutpatientComp.setDescription("If you choose 'Data Not Known' please alert the project coordinator.");

        //Personnel
        OptionEntry otherNonMedConsPersonnel = factory.createOptionEntry("Other Non-Medical Consultations Personnel",
                "Personnel, MEDICAL Staff", EntryStatus.DISABLED);
        otherNonMedOutpatientComp.addEntry(otherNonMedConsPersonnel);
        otherNonMedConsPersonnel.setSection(sectionE);
        otherNonMedConsPersonnel.addOption(factory.createOption("Occupational Therapist", "Occupational Therapist", 1));
        otherNonMedConsPersonnel.addOption(factory.createOption("Social Worker", "Social Worker", 2));
        otherNonMedConsPersonnel.addOption(factory.createOption("CPM", "CPN", 3));
        otherNonMedConsPersonnel.addOption(factory.createOption("Nurse", "Nurse", 4));
        Option otherOtherNonMedConsPersonnel = factory.createOption("Other", "Other", 5);
        otherNonMedConsPersonnel.addOption(otherOtherNonMedConsPersonnel);
        otherOtherNonMedConsPersonnel.setTextEntryAllowed(true);

        TextEntry otherNonMedConshospital = factory.createTextEntry("Other Non-Medical Consultations Hospital",
                "Name of Hospital", EntryStatus.DISABLED);
        otherNonMedOutpatientComp.addEntry(otherNonMedConshospital);
        otherNonMedConshospital.setSection(sectionE);

        TextEntry otherNonMedConsDepartment = factory.createTextEntry("Other Non-Medical Consultations Ward",
                "Ward", EntryStatus.DISABLED);
        otherNonMedOutpatientComp.addEntry(otherNonMedConsDepartment);
        otherNonMedConsDepartment.setSection(sectionE);

        IntegerEntry numberNonMedOutCons = factory.createIntegerEntry("Number Non-Medical Outpatient Consultations",
                "Number of Outpatient Consultations", EntryStatus.DISABLED);
        otherNonMedOutpatientComp.addEntry(numberNonMedOutCons);
        numberNonMedOutCons.setSection(sectionE);

        IntegerEntry numberNonMedDomVisits = factory.createIntegerEntry("Number of Non-Medical Domiciliary Visits",
                "Number of Domiciliary Visits", EntryStatus.DISABLED);
        otherNonMedOutpatientComp.addEntry(numberNonMedDomVisits);
        numberNonMedDomVisits.setSection(sectionE);

        return phs;
    }

    static void buildYesNoOptions(Factory factory, OptionEntry q){
        Option op1 = factory.createOption("No", 0);
        q.addOption(op1);
        Option op2 = factory.createOption("Yes", 1);
        q.addOption(op2);
    }
}
