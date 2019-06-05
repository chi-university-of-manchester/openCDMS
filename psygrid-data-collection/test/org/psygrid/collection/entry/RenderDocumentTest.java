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
package org.psygrid.collection.entry;

import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class RenderDocumentTest extends AbstractEntryTestCase {
    
    private Application app;
    private Factory factory;
    private Document doc;
    private DocumentInstance docInstance;
    
    public static void main(String[] args) throws Exception {
        new RenderDocumentTest().testRenderDocument();
    }
    
    public RenderDocumentTest() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                initLauncher();
            }
        });
    }
    
    private void init() throws Exception    {
        factory = getFactory();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        createDocument();
        DocumentOccurrence docOccurrence = factory.createDocumentOccurrence("Some occurrence");
        doc.addOccurrence(docOccurrence);
        dataSet.addDocument(doc);
        docInstance = doc.generateInstance(docOccurrence);
        setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.INCOMPLETE));
        record.addDocumentInstance(docInstance);

        app = createApplication();
    }
    
    public void testRenderDocument() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
            }
        });
    }
    
    public void testApplyStdCodeAction() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                app.getModel().applyStdCodeToSection(
                        getStandardCodes(app.getModel()).get(0), false);
            }
        });
    }
    
    private void createOptionDependent(Option option, Entry dependentEntry) {
        createOptionDependent(option, 
                dependentEntry, EntryStatus.MANDATORY);
    }
    
    private void createOptionDependent(Option option,
            Entry dependentEntry,
            EntryStatus status) {
        
        OptionDependent optDep = factory.createOptionDependent();
        optDep.setEntryStatus(status);
        option.addOptionDependent(optDep);
        optDep.setDependentEntry(dependentEntry);
    }

    private void createDocument() {
        
        doc = factory.createDocument("Baseline Audit", 
                "Baseline Audit Form (Information obtained from case notes)");
        
        createDocumentStatuses(doc);
        
        Section mainSec = factory.createSection("Main", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence sectionOcc = factory.createSectionOccurrence("Main");
        mainSec.addOccurrence(sectionOcc);
        
        OptionEntry criteria = factory.createOptionEntry("Meets diagnostic "
                + "criteria", "Meets diagnostic criteria for psychosis?");
        doc.addEntry(criteria);
        criteria.setSection(mainSec);
        Option  criteriaNo = factory.createOption("No",0);
        criteria.addOption(criteriaNo);
        criteria.addOption(factory.createOption("Yes", 1));
        
        OptionEntry firstEpisode = factory.createOptionEntry("Patients first " +
                "episode", "Patients first episode?");
        doc.addEntry(firstEpisode);
        firstEpisode.setSection(mainSec);
        Option  firstEpisodeNo = factory.createOption("No",0 );
        firstEpisode.addOption(firstEpisodeNo);
        firstEpisode.addOption(factory.createOption("Yes", 1));
        
        OptionEntry age = factory.createOptionEntry("Age 16-35",
                "Age 16-35?");
        doc.addEntry(age);
        age.setSection(mainSec);
        Option  ageNo = factory.createOption("No",0 );
        age.addOption(ageNo);
        age.addOption(factory.createOption("Yes", 1));
        
        OptionEntry notContinue = factory.createOptionEntry("If No do not " +
                "continue",
                "Did you answer No in any of the previous questions?");
        doc.addEntry(notContinue);
        notContinue.setSection(mainSec);
        Option  notContinueNo = factory.createOption("No",  "No (Continue to the next question)", 0 );
        notContinue.addOption(notContinueNo);
        Option  notContinueYes = factory.createOption("Yes",  "Yes (Please do not continue)", 1 );
        notContinue.addOption(notContinueYes);

        DateEntry dateFirstContact = factory.createDateEntry("Date first " +
                "contact with Mental Health Team", "Date first contact with " +
                "Mental Health Team", EntryStatus.DISABLED);
        doc.addEntry(dateFirstContact);
        dateFirstContact.setSection(mainSec);
        createOptionDependent(notContinueNo, dateFirstContact);
        
        DateEntry dateScreened = factory.createDateEntry("Date screened",
                "Date screened", EntryStatus.DISABLED);
        doc.addEntry(dateScreened);
        dateScreened.setSection(mainSec);
        createOptionDependent(notContinueNo, dateScreened);
        
        TextEntry patientInitials = factory.createTextEntry("Patient initials",
                "Patient initials", EntryStatus.DISABLED);
        doc.addEntry(patientInitials);
        patientInitials.setSection(mainSec);
        createOptionDependent(notContinueNo, patientInitials);
        
        TextEntry nhsNumber = factory.createTextEntry("NHS Number", "NHS Number",
                EntryStatus.DISABLED);
        doc.addEntry(nhsNumber);
        nhsNumber.setSection(mainSec);
        createOptionDependent(notContinueNo, nhsNumber);
        
        TextEntry nameCons = factory.createTextEntry("Name of Consultant",
                "Name of Consultant", EntryStatus.DISABLED);
        doc.addEntry(nameCons);
        nameCons.setSection(mainSec);
        createOptionDependent(notContinueNo, nameCons);
        
        TextEntry nameKey = factory.createTextEntry("Name of keyworker/care " +
                "coordinator", "Name of keyworker/care coordinator", 
                EntryStatus.DISABLED);
        doc.addEntry(nameKey);
        nameKey.setSection(mainSec);
        createOptionDependent(notContinueNo, nameKey);
        
        TextEntry nameTrust = factory.createTextEntry("Name of Trust and code",
                "Name of Trust and code", EntryStatus.DISABLED);
        doc.addEntry(nameTrust);
        nameTrust.setSection(mainSec);
        createOptionDependent(notContinueNo, nameTrust);
        
        TextEntry nameOfHospital = factory.createTextEntry("Name of " +
                "Hospital/CMHT team", "Name of Hospital/CMHT team (if helpful)", 
                EntryStatus.DISABLED);
        doc.addEntry(nameOfHospital);
        nameOfHospital.setSection(mainSec);
        createOptionDependent(notContinueNo, nameOfHospital);
        
        OptionEntry statusOption = factory.createOptionEntry("Status of Patient",
                "Status of Patient at screening date", EntryStatus.DISABLED);
        doc.addEntry(statusOption);
        statusOption.setSection(mainSec);
        statusOption.addOption(factory.createOption("in patient", 1));
        statusOption.addOption(factory.createOption("day patient", 2));
        statusOption.addOption(factory.createOption("out patient", 3));
        createOptionDependent(notContinueNo, statusOption);
        
        DateEntry dob = factory.createDateEntry("DOB", "Date of Birth",
                EntryStatus.DISABLED);
        doc.addEntry(dob);
        dob.setSection(mainSec);
        createOptionDependent(notContinueNo, dob);
        
        TextEntry legalStatus = factory.createTextEntry("Legal Status",
                "Legal Status (MHA) on screening date", EntryStatus.DISABLED);
        doc.addEntry(legalStatus);
        legalStatus.setSection(mainSec);
        createOptionDependent(notContinueNo, legalStatus);
        
        OptionEntry sex = factory.createOptionEntry("Sex", "Sex",
                EntryStatus.DISABLED);
        sex.addOption(factory.createOption("Male",1));
        sex.addOption(factory.createOption("Female", 2));
        doc.addEntry(sex);
        sex.setSection(mainSec);
        createOptionDependent(notContinueNo, sex);
        
        OptionEntry hasFixedAbode = factory.createOptionEntry("Has Fixed Abode",
                "Has Fixed Abode?", EntryStatus.DISABLED);
        doc.addEntry(hasFixedAbode);
        hasFixedAbode.setSection(mainSec);
        createOptionDependent(notContinueNo, hasFixedAbode);
        
        hasFixedAbode.addOption(factory.createOption("No", 0));
        Option  hasFixedAbodeYes = factory.createOption("Yes", 1 );
        hasFixedAbode.addOption(hasFixedAbodeYes);
        OptionDependent fixedAbodeOptDep = factory.createOptionDependent();
        hasFixedAbodeYes.addOptionDependent(fixedAbodeOptDep);
        fixedAbodeOptDep.setEntryStatus(EntryStatus.MANDATORY);
        
        TextEntry postCode = factory.createTextEntry("Postcode", "Postcode of " +
                "residential address");
        doc.addEntry(postCode);
        postCode.setSection(mainSec);
        postCode.setEntryStatus(EntryStatus.DISABLED);
        fixedAbodeOptDep.setDependentEntry(postCode);
        
        OptionEntry status = factory.createOptionEntry("Status on current " +
                "admission", "Status on current/first Admission - clarification " +
                "found in guidelines.", EntryStatus.DISABLED);
        doc.addEntry(status);
        status.setSection(mainSec);
        createOptionDependent(notContinueNo, status);
        {
            status.addOption(factory.createOption("Voluntary", "Voluntary", 1));
            status.addOption(factory.createOption("Compulsory", "Compulsory", 2));
            status.addOption(factory.createOption("Voluntary then sectioned", 
                    "Voluntary then sectioned", 3));
        }
        
        OptionEntry streetDrugOption = factory.createOptionEntry("Street Drug " +
                "Use", "Street Drug Use in the past (month prior to onset)",
                EntryStatus.DISABLED);
        doc.addEntry(streetDrugOption);
        streetDrugOption.setSection(mainSec);
        streetDrugOption.addOption(factory.createOption("No", 0));
        Option  streetYesOption = factory.createOption("Yes",  1);
        streetDrugOption.addOption(streetYesOption);
        createOptionDependent(notContinueNo, streetDrugOption);
        
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
        drug.addOption(factory.createOption("Cannabis"));
        drug.addOption(factory.createOption("Amphetamines"));
        drug.addOption(factory.createOption("Ecstasy (MDMA)"));
        drug.addOption(factory.createOption("LSD"));
        drug.addOption(factory.createOption("Hallucinogenic Mushrooms"));
        drug.addOption(factory.createOption("Cocaine/Crack"));
        drug.addOption(factory.createOption("Heroin/Opiates"));
        drug.addOption(factory.createOption("Amyl/Butyl Nitrates"));
        drug.addOption(factory.createOption("Solvents"));
        drug.addOption(factory.createOption("Khat"));
        drug.addOption(factory.createOption("Ketamine"));
        drug.addOption(factory.createOption("GHB"));
        drug.addOption(factory.createOption("Barbituates"));
        drug.addOption(factory.createOption("Over Counter Medication"));
        drug.addOption(factory.createOption("Benzodiazepines"));
        Option  otherOption = factory.createOption("Other Drugs Specify");
        drug.addOption(otherOption);
        otherOption.setTextEntryAllowed(true);
        
        OptionEntry freqOfUse = factory.createOptionEntry("Frequency",
                "Previous Freq. of Use", EntryStatus.DISABLED);
        drugUse.addEntry(freqOfUse);
        freqOfUse.setSection(mainSec);
        freqOfUse.addOption(factory.createOption("None",0));
        freqOfUse.addOption(factory.createOption("Occasional user (less than weekly)",1));
        freqOfUse.addOption(factory.createOption("Regular user (1-3 times weekly)",2));
        freqOfUse.addOption(factory.createOption("Frequent user (almost everyday)",3));
        
        OptionEntry durPrevUse =
            factory.createOptionEntry("Duration","Duration of Previous Use", 
                    EntryStatus.DISABLED);
        drugUse.addEntry(durPrevUse);
        durPrevUse.setSection(mainSec);
        durPrevUse.addOption(factory.createOption("less than 2 weeks", 0));
        durPrevUse.addOption(factory.createOption("more than 4 weeks", 1));
        durPrevUse.addOption(factory.createOption("more than 3 months", 2));
        durPrevUse.addOption(factory.createOption("more than 6 months", 3));

        NarrativeEntry inclCriteria = factory.createNarrativeEntry(
                "Inclusion criteria", "Inclusion criteria are present " +
                "(see protocol)");
        doc.addEntry(inclCriteria);
        inclCriteria.setSection(mainSec);
        
        OptionEntry firstTreatedOption = factory.createOptionEntry("First " +
                "treated episode", "First treated episode", EntryStatus.DISABLED);
        doc.addEntry(firstTreatedOption);
        firstTreatedOption.setSection(mainSec);
        firstTreatedOption.addOption(factory.createOption("No", 0));
        firstTreatedOption.addOption(factory.createOption("Yes", 1));
        createOptionDependent(notContinueNo, firstTreatedOption);
        
        OptionEntry psychCriteriaOption = factory.createOptionEntry("Meets " +
                "psychosis criteria", "Meets psychosis criteria", 
                EntryStatus.DISABLED);
        doc.addEntry(psychCriteriaOption);
        psychCriteriaOption.setSection(mainSec);
        psychCriteriaOption.addOption(factory.createOption("No", 0));
        psychCriteriaOption.addOption(factory.createOption("Yes", 1));
        createOptionDependent(notContinueNo, psychCriteriaOption);
        
        OptionEntry ageCriteriaOption = factory.createOptionEntry("Age 16-35",
                "Age 16-35", EntryStatus.DISABLED);
        doc.addEntry(ageCriteriaOption);
        ageCriteriaOption.setSection(mainSec);
        ageCriteriaOption.addOption(factory.createOption("No", 0));
        ageCriteriaOption.addOption(factory.createOption("Yes", 1));
        createOptionDependent(notContinueNo, ageCriteriaOption);
        
        OptionEntry eligible = factory.createOptionEntry("Eligible for PsyGrid",
                "Eligible for PsyGrid?", EntryStatus.DISABLED);
        doc.addEntry(eligible);
        eligible.setSection(mainSec);
        eligible.addOption(factory.createOption("No", "No", 0));
        eligible.addOption(factory.createOption("Yes", "Yes", 1));
        createOptionDependent(notContinueNo, eligible);
        
        NarrativeEntry end = factory.createNarrativeEntry("End of form " +
                "instructions", "End of Baseline Audit Form, Screening number " +
                "will now be named 'Study Number' (move on to interview with " +
                "client)");
        doc.addEntry(end);
        end.setSection(mainSec);
    }
}
