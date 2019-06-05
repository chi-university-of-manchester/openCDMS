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

package org.psygrid.edie.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.psygrid.common.TransformersWrapper;
import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.edie.BDI7;
import org.psygrid.edie.CAARMSWithGAF;
import org.psygrid.edie.Demographics;
import org.psygrid.edie.DrugCheck;
import org.psygrid.edie.EPQv3;
import org.psygrid.edie.EQ5D;
import org.psygrid.edie.MANSA;
import org.psygrid.edie.NonPsychiatricHospitalRecord;
import org.psygrid.edie.PersonalBeliefsAboutIllness;
import org.psygrid.edie.PsychiatricHospitalRecord;
import org.psygrid.edie.SCID;
import org.psygrid.edie.SCIDPsychDisorders;
import org.psygrid.edie.SIAS;
import org.psygrid.edie.StudyTermination;
import org.psygrid.edie.Transition;
import org.psygrid.edie.Treatment;
import org.psygrid.edie.UnitFactory;
import org.psygrid.edie.ValidationRulesFactory;
import org.psygrid.outlook.TransformersFactory;
import org.psygrid.security.RBACAction;


public class EDIETestDataset {

    public static void main(String[] args){
        try{
            RepositoryClient client = null;
            if (1==args.length){
                //use the argument as the location of the repository web-service
            	System.out.println(args[0]);
                client = new RepositoryClient(new URL(args[0]));
            }
            else{
                client = new RepositoryClient();
            }

            DataSet ds = createDataset();
            Long id = client.saveDataSet(ds, null);
            client.publishDataSet(id, null);
            System.out.println("DataSet successfully saved to the repository and assigned id="+id);

            ds = client.getDataSet(id, null);
            createReports(ds, null);
            System.out.println("Successfully saved reports");

        }
        catch(MalformedURLException ex){
            System.out.println("URL '"+args[0]+"' specified as the argument is not valid");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public EDIETestDataset(){

    }

    public void insert(String saml, String repository){
        try{
            RepositoryClient client = null;

            client = new RepositoryClient(new URL(repository));

            DataSet ds = createDataset();

            Long id = client.saveDataSet(ds, saml);
            client.publishDataSet(id, saml);
            System.out.println("DataSet successfully saved to the repository and assigned id="+id);

            ds = client.getDataSet(id, saml);
            createReports(ds, saml);
            System.out.println("Successfully saved reports");

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void delete(long id, String saml, String repository){
        try{
            RepositoryClient client = null;

            client = new RepositoryClient(new URL(repository));

            client.removePublishedDataSet(id, "EDT", saml);

            System.out.println("Successfully deleted dataset");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static DataSet createDataset(){

        Factory factory = new HibernateFactory();

        DataSet dataSet = factory.createDataset("EDIE Test", "EDIE Test");
		UnitWrapper.setFactory(new UnitFactory());
		UnitWrapper.instance().init(factory, dataSet);
		TransformersWrapper.setFactory(new TransformersFactory());
		TransformersWrapper.instance().init(factory, dataSet);
		ValidationRulesWrapper.setFactory(new ValidationRulesFactory());
		ValidationRulesWrapper.instance().init(factory, dataSet);

        dataSet.setProjectCode("EDT");
        dataSet.setVersionNo("1.0.0");
        dataSet.setEslUsed(true);
        dataSet.setRandomizationRequired(true);

        dataSet.setScheduleStartQuestion("Please provide the date the patient was recruited into the study:");

        //groups
        Group grp1 = (Group)factory.createGroup("001001");
        grp1.setLongName("Manchester");
		Site site1 = new Site("Faculty of Medical and Human Sciences", "N0000673", "M13 9PL", grp1);
		site1.addConsultant("Tony Morrison");
		grp1.addSite(site1);
		Group grp2 = (Group)factory.createGroup("002001");
		grp2.setLongName("Birmingham");
		Site site2 = new Site("School of Health Sciences", "N0000674", "B15 2TT", grp2);
		site2.addConsultant("Max Birchwood");
		grp2.addSite(site2);
		Group grp3 = (Group)factory.createGroup("003001");
		grp3.setLongName("Cambridge");
		Site site3 = new Site("School of Clinical Medicine", "N0000675", "CB2 2QQ", grp3);
		site3.addConsultant("Peter Jones");
		grp3.addSite(site3);
		Group grp4 = (Group)factory.createGroup("004001");
		grp4.setLongName("East Anglia");
		Site site4 = new Site("University of East Anglia", "N0000676", "NR4 7TJ", grp4);
		site4.addConsultant("David Fowler");
		grp4.addSite(site4);
		Group grp5 = (Group)factory.createGroup("005001");
		grp5.setLongName("Glasgow");
		Site site5 = new Site("Gartnavel Royal Hospital", "N0000677", "G12 0XH", grp5);
		site5.addConsultant("Andew Gumley");
		grp5.addSite(site5);

        dataSet.addGroup(grp1);
        dataSet.addGroup(grp2);
        dataSet.addGroup(grp3);
        dataSet.addGroup(grp4);
        dataSet.addGroup(grp5);

        //consent
        ConsentFormGroup cfg = factory.createConsentFormGroup();
        cfg.setDescription("Main client consent");
        cfg.setEslTrigger(true);
        PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
        pcf.setQuestion("Has the client (aged 16 years or over) agreed to take part in the study?");
        cfg.addConsentForm(pcf);
        PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
        pcf2.setQuestion("Has the client (aged under 16 years) agreed to take part in the study?");
        cfg.addConsentForm(pcf2);
        AssociatedConsentForm acf = factory.createAssociatedConsentForm();
        acf.setQuestion("Has the client's relative/legal guardian agreed to the client taking part in the study?");
        pcf2.addAssociatedConsentForm(acf);
        dataSet.addAllConsentFormGroup(cfg);

        //dataset statuses and their transitions
        Status statReferred = factory.createStatus("Referred", "Referred", 0);
        statReferred.setGenericState(GenericState.REFERRED);
        Status statScreenInelig = factory.createStatus("Ineligible",
                "Screened; ineligible", 1);
        statScreenInelig.setInactive(true);
        statScreenInelig.setGenericState(GenericState.INACTIVE);
        Status statUnableToConsent = factory.createStatus("Unable", "Unable to consent", 2);
        statUnableToConsent.setInactive(true);
        statUnableToConsent.setGenericState(GenericState.INACTIVE);
        Status statConsented = factory.createStatus("Consented",
                "Consented", 3);
        statConsented.setGenericState(GenericState.ACTIVE);
        Status statConsentRefused = factory.createStatus("Refused", "Consent refused", 4);
        statConsentRefused.setInactive(true);
        statConsentRefused.setGenericState(GenericState.INACTIVE);
        Status statClinicianWithdrew = factory.createStatus("Withdrawn",
                "Clinician withdrew referral", 5);
        statClinicianWithdrew.setInactive(true);
        statClinicianWithdrew.setGenericState(GenericState.INACTIVE);
        Status statActive = factory.createStatus("Active", "Active", 6);
        statActive.setGenericState(GenericState.ACTIVE);
        Status statComplete = factory.createStatus("Complete", "Complete", 7);
        statComplete.setInactive(true);
        statComplete.setGenericState(GenericState.COMPLETED);
        Status statDeceased = factory.createStatus("Deceased", "Deceased", 8);
        statDeceased.setInactive(true);
        statDeceased.setGenericState(GenericState.LEFT);
        Status statWithdrew = factory.createStatus("Withdrew", "Withdrew", 9);
        statWithdrew.setInactive(true);
        statWithdrew.setGenericState(GenericState.INACTIVE);
        Status statLost = factory.createStatus("Lost", "Lost", 10);
        statLost.setInactive(true);
        statLost.setGenericState(GenericState.LEFT);

        Status statInvalid = factory.createStatus("Invalid", "Invalid", 11);	//Record was added by mistake and shouldn't exist
		statInvalid.setInactive(true);
		statInvalid.setGenericState(GenericState.INVALID);

		Status statLeft = factory.createStatus("Left", "Left Study", 12);
		statLeft.setGenericState(GenericState.LEFT);
		statLeft.setInactive(true);

        statReferred.addStatusTransition(statScreenInelig); //referred -> Screened; ineligible
        statReferred.addStatusTransition(statUnableToConsent); //referred -> Unable to consent
        statReferred.addStatusTransition(statConsented); //referred ->	consented
        statReferred.addStatusTransition(statConsentRefused); //referred -> consent refused
        statReferred.addStatusTransition(statClinicianWithdrew); //referred -> clinician withdrew referral
       // statReferred.addStatusTransition(statActive); //referred -> active
        statReferred.addStatusTransition(statComplete); //referred -> completed
        statReferred.addStatusTransition(statDeceased); //referred -> deceased
        statReferred.addStatusTransition(statWithdrew); //referred -> withdrew
        statReferred.addStatusTransition(statLost); //referred -> lost
        statReferred.addStatusTransition(statInvalid); //referred -> invalid

        statConsented.addStatusTransition(statActive); //consented -> active
        statConsented.addStatusTransition(statComplete); //consented -> completed
        statConsented.addStatusTransition(statDeceased); //consented -> deceased
        statConsented.addStatusTransition(statWithdrew); //consented -> withdrew
        statConsented.addStatusTransition(statLost); //consented -> lost
        statConsented.addStatusTransition(statInvalid); //consented -> invalid
        statConsented.addStatusTransition(statLeft); //consented -> left study

        statActive.addStatusTransition(statComplete); //active -> completed
        statActive.addStatusTransition(statDeceased); //active -> deceased
        statActive.addStatusTransition(statWithdrew); //active -> withdrew
        statActive.addStatusTransition(statLost); //active -> lost
        statActive.addStatusTransition(statInvalid); //active -> invalid
		statActive.addStatusTransition(statLeft); //active -> left study

        statLeft.addStatusTransition(statWithdrew);					//Left Study -> Withdrew
		statLeft.addStatusTransition(statDeceased);					//Left Study -> Deceased
		statLeft.addStatusTransition(statInvalid);					//Left Study -> Invalid


        dataSet.addStatus(statReferred);
        dataSet.addStatus(statScreenInelig);
        dataSet.addStatus(statUnableToConsent);
        dataSet.addStatus(statConsented);
        dataSet.addStatus(statConsentRefused);
        dataSet.addStatus(statClinicianWithdrew);
        dataSet.addStatus(statActive);
        dataSet.addStatus(statComplete);
        dataSet.addStatus(statDeceased);
        dataSet.addStatus(statWithdrew);
        dataSet.addStatus(statLost);
        dataSet.addStatus(statInvalid);
        dataSet.addStatus(statLeft);

        //document groups
        DocumentGroup baselineMinus1 = factory.createDocumentGroup("Baseline -1 Group");
        baselineMinus1.setDisplayText("Baseline -1");
        baselineMinus1.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
       // baselineMinus1.addAllowedRecordStatus(statReferred);
        baselineMinus1.setUpdateStatus(statActive);

        DocumentGroup baseline0 = factory.createDocumentGroup("Baseline 0 Group");
        baseline0.setDisplayText("Baseline 0");
        baseline0.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        //baseline0.addPrerequisiteGroup(baselineMinus1);

        DocumentGroup oneMonth = factory.createDocumentGroup("1 month Group");
        oneMonth.setDisplayText("1 month");
        oneMonth.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        oneMonth.addPrerequisiteGroup(baseline0);

        DocumentGroup twoMonths = factory.createDocumentGroup("2 months Group");
        twoMonths.setDisplayText("2 months");
        twoMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        twoMonths.addPrerequisiteGroup(oneMonth);

        DocumentGroup threeMonths = factory.createDocumentGroup("3 months Group");
        threeMonths.setDisplayText("3 months");
        threeMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        threeMonths.addPrerequisiteGroup(twoMonths);

        DocumentGroup fourMonths = factory.createDocumentGroup("4 months Group");
        fourMonths.setDisplayText("4 months");
        fourMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        fourMonths.addPrerequisiteGroup(threeMonths);

        DocumentGroup fiveMonths = factory.createDocumentGroup("5 months Group");
        fiveMonths.setDisplayText("5 months");
        fiveMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        fiveMonths.addPrerequisiteGroup(fourMonths);

        DocumentGroup sixMonths = factory.createDocumentGroup("6 months Group");
        sixMonths.setDisplayText("6 months");
        sixMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        sixMonths.addPrerequisiteGroup(fiveMonths);

        DocumentGroup nineMonths = factory.createDocumentGroup("9 months Group");
        nineMonths.setDisplayText("9 months");
        nineMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        nineMonths.addPrerequisiteGroup(sixMonths);

        DocumentGroup twelveMonths = factory.createDocumentGroup("12 months");
        twelveMonths.setDisplayText("12 months");
        twelveMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        twelveMonths.addPrerequisiteGroup(sixMonths);

        DocumentGroup fifteenMonths = factory.createDocumentGroup("15 months Group");
        fifteenMonths.setDisplayText("15 months");
        fifteenMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        fifteenMonths.addPrerequisiteGroup(twelveMonths);

        DocumentGroup eighteenMonths = factory.createDocumentGroup("18 months Group");
        eighteenMonths.setDisplayText("18 months");
        eighteenMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        eighteenMonths.addPrerequisiteGroup(fifteenMonths);

        DocumentGroup twentyOneMonths = factory.createDocumentGroup("21 months Group");
        twentyOneMonths.setDisplayText("21 months");
        twentyOneMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        twentyOneMonths.addPrerequisiteGroup(eighteenMonths);

        DocumentGroup twentyFourMonths = factory.createDocumentGroup("24 months Group");
        twentyFourMonths.setDisplayText("24 months");
        twentyFourMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
        twentyFourMonths.addPrerequisiteGroup(twentyOneMonths);
        twentyFourMonths.setUpdateStatus(statComplete);

        DocumentGroup studyTermination = factory.createDocumentGroup("Study termination");
        studyTermination.setDisplayText("Study termination");
        studyTermination.setAllowedRecordStatus(getStatuses(dataSet, GenericState.INACTIVE));
        studyTermination.addAllowedRecordStatus(statLost);
        studyTermination.addAllowedRecordStatus(statDeceased);
		for (Status s: getStatuses(dataSet, GenericState.ACTIVE)) {
			studyTermination.addAllowedRecordStatus(s);
		}

        DocumentGroup transition = factory.createDocumentGroup("Transition");
        transition.setDisplayText("Transition to Psychosis");
        transition.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));

        dataSet.addDocumentGroup(baselineMinus1);
        dataSet.addDocumentGroup(baseline0);
        dataSet.addDocumentGroup(oneMonth);
        dataSet.addDocumentGroup(twoMonths);
        dataSet.addDocumentGroup(threeMonths);
        dataSet.addDocumentGroup(fourMonths);
        dataSet.addDocumentGroup(fiveMonths);
        dataSet.addDocumentGroup(sixMonths);
        dataSet.addDocumentGroup(nineMonths);
        dataSet.addDocumentGroup(twelveMonths);
        dataSet.addDocumentGroup(fifteenMonths);
        dataSet.addDocumentGroup(eighteenMonths);
        dataSet.addDocumentGroup(twentyOneMonths);
        dataSet.addDocumentGroup(twentyFourMonths);
        dataSet.addDocumentGroup(studyTermination);
        dataSet.addDocumentGroup(transition);

        // Baseline -1
        Document demogs = Demographics.createDocument(factory);
        dataSet.addDocument(demogs);
        demogs.addConsentFormGroup(cfg);
        DocumentOccurrence demogsbm1 = factory.createDocumentOccurrence("Baseline -1");
        demogsbm1.setDisplayText("Baseline -1");
        demogs.addOccurrence(demogsbm1);
        demogsbm1.setDocumentGroup(baselineMinus1);

        Document caarmsWithGAF = CAARMSWithGAF.createDocument(factory);
        dataSet.addDocument(caarmsWithGAF);
        caarmsWithGAF.addConsentFormGroup(cfg);
        DocumentOccurrence caarmsbm1 = factory.createDocumentOccurrence("Baseline -1");
        caarmsbm1.setDisplayText("Baseline -1");
        caarmsWithGAF.addOccurrence(caarmsbm1);
        caarmsbm1.setDocumentGroup(baselineMinus1);

        Document bdi7 = BDI7.createDocument(factory);
        dataSet.addDocument(bdi7);
        bdi7.addConsentFormGroup(cfg);
        DocumentOccurrence bdi7bm1 = factory.createDocumentOccurrence("Baseline -1");
        bdi7bm1.setDisplayText("Baseline -1");
        bdi7.addOccurrence(bdi7bm1);
        bdi7bm1.setDocumentGroup(baselineMinus1);

        Document sias = SIAS.createDocument(factory);
        dataSet.addDocument(sias);
        sias.addConsentFormGroup(cfg);
        DocumentOccurrence siasbm1 = factory.createDocumentOccurrence("Baseline -1");
        siasbm1.setDisplayText("Baseline -1");
        sias.addOccurrence(siasbm1);
        siasbm1.setDocumentGroup(baselineMinus1);

        Document eq5d = EQ5D.createDocument(factory);
        dataSet.addDocument(eq5d);
        eq5d.addConsentFormGroup(cfg);
        DocumentOccurrence eq5dbm1 = factory.createDocumentOccurrence("Baseline -1");
        eq5dbm1.setDisplayText("Baseline -1");
        eq5d.addOccurrence(eq5dbm1);
        eq5dbm1.setDocumentGroup(baselineMinus1);

        Document pbiq = PersonalBeliefsAboutIllness.createDocument(factory);
        dataSet.addDocument(pbiq);
        pbiq.addConsentFormGroup(cfg);
        DocumentOccurrence pbiqbm1 = factory.createDocumentOccurrence("Baseline -1");
        pbiqbm1.setDisplayText("Baseline -1");
        pbiq.addOccurrence(pbiqbm1);
        pbiqbm1.setDocumentGroup(baselineMinus1);

        Document mansa = MANSA.createDocument(factory);
        dataSet.addDocument(mansa);
        mansa.addConsentFormGroup(cfg);
        DocumentOccurrence mansabm1 = factory.createDocumentOccurrence("Baseline -1");
        mansabm1.setDisplayText("Baseline -1");
        mansa.addOccurrence(mansabm1);
        mansabm1.setDocumentGroup(baselineMinus1);

        Document drugCheck = DrugCheck.createDocument(factory);
        dataSet.addDocument(drugCheck);
        drugCheck.addConsentFormGroup(cfg);
        DocumentOccurrence drugCheckbm1 = factory.createDocumentOccurrence("Baseline -1");
        drugCheckbm1.setDisplayText("Baseline -1");
        drugCheck.addOccurrence(drugCheckbm1);
        drugCheckbm1.setDocumentGroup(baselineMinus1);

        Document treatment = Treatment.createDocument(factory);
        dataSet.addDocument(treatment);
        treatment.addConsentFormGroup(cfg);
        DocumentOccurrence treatmentbm1 = factory.createDocumentOccurrence("Baseline -1");
        treatmentbm1.setDisplayText("Baseline -1");
        treatment.addOccurrence(treatmentbm1);
        treatmentbm1.setDocumentGroup(baselineMinus1);

        Document nphr = NonPsychiatricHospitalRecord.createDocument(factory);
        dataSet.addDocument(nphr);
        nphr.addConsentFormGroup(cfg);
        DocumentOccurrence nphrbm1 = factory.createDocumentOccurrence("Baseline -1");
        nphrbm1.setDisplayText("Baseline -1");
        nphr.addOccurrence(nphrbm1);
        nphrbm1.setDocumentGroup(baselineMinus1);

        Document phr = PsychiatricHospitalRecord.createDocument(factory);
        dataSet.addDocument(phr);
        phr.addConsentFormGroup(cfg);
        DocumentOccurrence phrbm1 = factory.createDocumentOccurrence("Baseline -1");
        phrbm1.setDisplayText("Baseline -1");
        phr.addOccurrence(phrbm1);
        phrbm1.setDocumentGroup(baselineMinus1);

        Document epq = EPQv3.createDocument(factory);
        dataSet.addDocument(epq);
        epq.addConsentFormGroup(cfg);
        DocumentOccurrence epqbm1 = factory.createDocumentOccurrence("Baseline -1");
        epqbm1.setDisplayText("Baseline -1");
        epq.addOccurrence(epqbm1);
        epqbm1.setDocumentGroup(baselineMinus1);

        // Baseline 0
        DocumentOccurrence caarmsb0 = factory.createDocumentOccurrence("Baseline 0");
        caarmsb0.setDisplayText("Baseline 0");
        caarmsWithGAF.addOccurrence(caarmsb0);
        caarmsb0.setDocumentGroup(baseline0);
        caarmsb0.setRandomizationTrigger(true);

        Document scid = SCID.createDocument(factory);
        dataSet.addDocument(scid);
        scid.addConsentFormGroup(cfg);
        DocumentOccurrence scidb0 = factory.createDocumentOccurrence("Baseline 0");
        scidb0.setDisplayText("Baseline 0");
        scid.addOccurrence(scidb0);
        scidb0.setDocumentGroup(baseline0);

        DocumentOccurrence treatmentb0 = factory.createDocumentOccurrence("Baseline 0");
        treatmentb0.setDisplayText("Baseline 0");
        treatment.addOccurrence(treatmentb0);
        treatmentb0.setDocumentGroup(baseline0);

        // Month 1
        DocumentOccurrence caarmsm1 = factory.createDocumentOccurrence("1 Month");
        caarmsm1.setDisplayText("1 Month");
        caarmsWithGAF.addOccurrence(caarmsm1);
        caarmsm1.setDocumentGroup(oneMonth);
        createOneMonthReminders(caarmsm1, factory);

        DocumentOccurrence bdi7m1 = factory.createDocumentOccurrence("1 Month");
        bdi7m1.setDisplayText("1 Month");
        bdi7.addOccurrence(bdi7m1);
        bdi7m1.setDocumentGroup(oneMonth);
        //createOneMonthReminders(bdi7m1, factory);

        DocumentOccurrence eq5dm1 = factory.createDocumentOccurrence("1 Month");
        eq5dm1.setDisplayText("1 Month");
        eq5d.addOccurrence(eq5dm1);
        eq5dm1.setDocumentGroup(oneMonth);
        //createOneMonthReminders(eq5dm1, factory);

        DocumentOccurrence siasm1 = factory.createDocumentOccurrence("1 Month");
        siasm1.setDisplayText("1 Month");
        sias.addOccurrence(siasm1);
        siasm1.setDocumentGroup(oneMonth);
        //createOneMonthReminders(siasm1, factory);

        DocumentOccurrence treatmentm1 = factory.createDocumentOccurrence("1 Month");
        treatmentm1.setDisplayText("1 Month");
        treatment.addOccurrence(treatmentm1);
        treatmentm1.setDocumentGroup(oneMonth);
        //createOneMonthReminders(treatmentm1, factory);

        // Month 2
        DocumentOccurrence caarmsm2 = factory.createDocumentOccurrence("2 Months");
        caarmsm2.setDisplayText("2 Months");
        caarmsWithGAF.addOccurrence(caarmsm2);
        caarmsm2.setDocumentGroup(twoMonths);
        createTwoMonthReminders(caarmsm2, factory);

        DocumentOccurrence bdi7m2 = factory.createDocumentOccurrence("2 Months");
        bdi7m2.setDisplayText("2 Months");
        bdi7.addOccurrence(bdi7m2);
        bdi7m2.setDocumentGroup(twoMonths);
        //createTwoMonthReminders(bdi7m2, factory);

        DocumentOccurrence eq5dm2 = factory.createDocumentOccurrence("2 Months");
        eq5dm2.setDisplayText("2 Months");
        eq5d.addOccurrence(eq5dm2);
        eq5dm2.setDocumentGroup(twoMonths);
        //createTwoMonthReminders(eq5dm2, factory);

        DocumentOccurrence siasm2 = factory.createDocumentOccurrence("2 Months");
        siasm2.setDisplayText("2 Months");
        sias.addOccurrence(siasm2);
        siasm2.setDocumentGroup(twoMonths);
        //createTwoMonthReminders(siasm2, factory);

        DocumentOccurrence treatmentm2 = factory.createDocumentOccurrence("2 Months");
        treatmentm2.setDisplayText("2 Months");
        treatment.addOccurrence(treatmentm2);
        treatmentm2.setDocumentGroup(twoMonths);
        //createTwoMonthReminders(treatmentm2, factory);

        // Month 3
        DocumentOccurrence caarmsm3 = factory.createDocumentOccurrence("3 Months");
        caarmsm3.setDisplayText("3 Months");
        caarmsWithGAF.addOccurrence(caarmsm3);
        caarmsm3.setDocumentGroup(threeMonths);
        createThreeMonthReminders(caarmsm3, factory);

        DocumentOccurrence bdi7m3 = factory.createDocumentOccurrence("3 Months");
        bdi7m3.setDisplayText("3 Months");
        bdi7.addOccurrence(bdi7m3);
        bdi7m3.setDocumentGroup(threeMonths);
        //createThreeMonthReminders(bdi7m3, factory);

        DocumentOccurrence eq5dm3 = factory.createDocumentOccurrence("3 Months");
        eq5dm3.setDisplayText("3 Months");
        eq5d.addOccurrence(eq5dm3);
        eq5dm3.setDocumentGroup(threeMonths);
        //createThreeMonthReminders(eq5dm3, factory);

        DocumentOccurrence siasm3 = factory.createDocumentOccurrence("3 Months");
        siasm3.setDisplayText("3 Months");
        sias.addOccurrence(siasm3);
        siasm3.setDocumentGroup(threeMonths);
        //createThreeMonthReminders(siasm3, factory);

        DocumentOccurrence treatmentm3 = factory.createDocumentOccurrence("3 Months");
        treatmentm3.setDisplayText("3 Months");
        treatment.addOccurrence(treatmentm3);
        treatmentm3.setDocumentGroup(threeMonths);
        //createThreeMonthReminders(treatmentm3, factory);

        DocumentOccurrence nphrm3 = factory.createDocumentOccurrence("3 Months");
        nphrm3.setDisplayText("3 Months");
        nphr.addOccurrence(nphrm3);
        nphrm3.setDocumentGroup(threeMonths);
        //createThreeMonthReminders(nphrm3, factory);

        DocumentOccurrence phrm3 = factory.createDocumentOccurrence("3 Months");
        phrm3.setDisplayText("3 Months");
        phr.addOccurrence(phrm3);
        phrm3.setDocumentGroup(threeMonths);
        //createThreeMonthReminders(phrm3, factory);

        DocumentOccurrence epqm3 = factory.createDocumentOccurrence("3 Months");
        epqm3.setDisplayText("3 Months");
        epq.addOccurrence(epqm3);
        epqm3.setDocumentGroup(threeMonths);
        //createThreeMonthReminders(epqm3, factory);

        // Month 4
        DocumentOccurrence caarmsm4 = factory.createDocumentOccurrence("4 Months");
        caarmsm4.setDisplayText("4 Months");
        caarmsWithGAF.addOccurrence(caarmsm4);
        caarmsm4.setDocumentGroup(fourMonths);
        createFourMonthReminders(caarmsm4, factory);

        DocumentOccurrence bdi7m4 = factory.createDocumentOccurrence("4 Months");
        bdi7m4.setDisplayText("4 Months");
        bdi7.addOccurrence(bdi7m4);
        bdi7m4.setDocumentGroup(fourMonths);
        //createFourMonthReminders(bdi7m4, factory);

        DocumentOccurrence eq5dm4 = factory.createDocumentOccurrence("4 Months");
        eq5dm4.setDisplayText("4 Months");
        eq5d.addOccurrence(eq5dm4);
        eq5dm4.setDocumentGroup(fourMonths);
        //createFourMonthReminders(eq5dm4, factory);

        DocumentOccurrence siasm4 = factory.createDocumentOccurrence("4 Months");
        siasm4.setDisplayText("4 Months");
        sias.addOccurrence(siasm4);
        siasm4.setDocumentGroup(fourMonths);
        //createFourMonthReminders(siasm4, factory);

        DocumentOccurrence treatmentm4 = factory.createDocumentOccurrence("4 Months");
        treatmentm4.setDisplayText("4 Months");
        treatment.addOccurrence(treatmentm4);
        treatmentm4.setDocumentGroup(fourMonths);
        //createFourMonthReminders(treatmentm4, factory);

        // Month 5
        DocumentOccurrence caarmsm5 = factory.createDocumentOccurrence("5 Months");
        caarmsm5.setDisplayText("5 Months");
        caarmsWithGAF.addOccurrence(caarmsm5);
        caarmsm5.setDocumentGroup(fiveMonths);
        createFiveMonthReminders(caarmsm5, factory);

        DocumentOccurrence bdi7m5 = factory.createDocumentOccurrence("5 Months");
        bdi7m5.setDisplayText("5 Months");
        bdi7.addOccurrence(bdi7m5);
        bdi7m5.setDocumentGroup(fiveMonths);
        //createFiveMonthReminders(bdi7m5, factory);

        DocumentOccurrence eq5dm5 = factory.createDocumentOccurrence("5 Months");
        eq5dm5.setDisplayText("5 Months");
        eq5d.addOccurrence(eq5dm5);
        eq5dm5.setDocumentGroup(fiveMonths);
        //createFiveMonthReminders(eq5dm5, factory);

        DocumentOccurrence siasm5 = factory.createDocumentOccurrence("5 Months");
        siasm5.setDisplayText("5 Months");
        sias.addOccurrence(siasm5);
        siasm5.setDocumentGroup(fiveMonths);
        //createFiveMonthReminders(siasm5, factory);

        DocumentOccurrence treatmentm5 = factory.createDocumentOccurrence("5 Months");
        treatmentm5.setDisplayText("5 Months");
        treatment.addOccurrence(treatmentm5);
        treatmentm5.setDocumentGroup(fiveMonths);
        //createFiveMonthReminders(treatmentm5, factory);

        // Month 6
        DocumentOccurrence caarmsm6 = factory.createDocumentOccurrence("6 Months");
        caarmsm6.setDisplayText("6 Months");
        caarmsWithGAF.addOccurrence(caarmsm6);
        caarmsm6.setDocumentGroup(sixMonths);
        createSixMonthReminders(caarmsm6, factory);

        DocumentOccurrence bdi7m6 = factory.createDocumentOccurrence("6 Months");
        bdi7m6.setDisplayText("6 Months");
        bdi7.addOccurrence(bdi7m6);
        bdi7m6.setDocumentGroup(sixMonths);
        //createSixMonthReminders(bdi7m6, factory);

        DocumentOccurrence eq5dm6 = factory.createDocumentOccurrence("6 Months");
        eq5dm6.setDisplayText("6 Months");
        eq5d.addOccurrence(eq5dm6);
        eq5dm6.setDocumentGroup(sixMonths);
        //createSixMonthReminders(eq5dm6, factory);

        DocumentOccurrence siasm6 = factory.createDocumentOccurrence("6 Months");
        siasm6.setDisplayText("6 Months");
        sias.addOccurrence(siasm6);
        siasm6.setDocumentGroup(sixMonths);
        //createSixMonthReminders(siasm6, factory);

        DocumentOccurrence pbiqm6 = factory.createDocumentOccurrence("6 Months");
        pbiqm6.setDisplayText("6 Months");
        pbiq.addOccurrence(pbiqm6);
        pbiqm6.setDocumentGroup(sixMonths);
        //createSixMonthReminders(pbiqm6, factory);

        DocumentOccurrence drugCheckm6 = factory.createDocumentOccurrence("6 Months");
        drugCheckm6.setDisplayText("6 Months");
        drugCheck.addOccurrence(drugCheckm6);
        drugCheckm6.setDocumentGroup(sixMonths);
        //createSixMonthReminders(drugCheckm6, factory);

        DocumentOccurrence mansam6 = factory.createDocumentOccurrence("6 Months");
        mansam6.setDisplayText("6 Months");
        mansa.addOccurrence(mansam6);
        mansam6.setDocumentGroup(sixMonths);
        //createSixMonthReminders(mansam6, factory);

        DocumentOccurrence treatmentm6 = factory.createDocumentOccurrence("6 Months");
        treatmentm6.setDisplayText("6 Months");
        treatment.addOccurrence(treatmentm6);
        treatmentm6.setDocumentGroup(sixMonths);
        //createSixMonthReminders(treatmentm6, factory);

        DocumentOccurrence nphrm6 = factory.createDocumentOccurrence("6 Months");
        nphrm6.setDisplayText("6 Months");
        nphr.addOccurrence(nphrm6);
        nphrm6.setDocumentGroup(sixMonths);
        //createSixMonthReminders(nphrm6, factory);

        DocumentOccurrence phrm6 = factory.createDocumentOccurrence("6 Months");
        phrm6.setDisplayText("6 Months");
        phr.addOccurrence(phrm6);
        phrm6.setDocumentGroup(sixMonths);
        //createSixMonthReminders(phrm6, factory);

        DocumentOccurrence epqm6 = factory.createDocumentOccurrence("6 Months");
        epqm6.setDisplayText("6 Months");
        epq.addOccurrence(epqm6);
        epqm6.setDocumentGroup(sixMonths);
        //createSixMonthReminders(epqm6, factory);

        // Month 9
        DocumentOccurrence caarmsm9 = factory.createDocumentOccurrence("9 Months");
        caarmsm9.setDisplayText("9 Months");
        caarmsWithGAF.addOccurrence(caarmsm9);
        caarmsm9.setDocumentGroup(nineMonths);
        createNineMonthReminders(caarmsm9, factory);

        DocumentOccurrence bdi7m9 = factory.createDocumentOccurrence("9 Months");
        bdi7m9.setDisplayText("9 Months");
        bdi7.addOccurrence(bdi7m9);
        bdi7m9.setDocumentGroup(nineMonths);
        //createNineMonthReminders(bdi7m9, factory);

        DocumentOccurrence eq5dm9 = factory.createDocumentOccurrence("9 Months");
        eq5dm9.setDisplayText("9 Months");
        eq5d.addOccurrence(eq5dm9);
        eq5dm9.setDocumentGroup(nineMonths);
        //createNineMonthReminders(eq5dm9, factory);

        DocumentOccurrence siasm9 = factory.createDocumentOccurrence("9 Months");
        siasm9.setDisplayText("9 Months");
        sias.addOccurrence(siasm9);
        siasm9.setDocumentGroup(nineMonths);
        //createNineMonthReminders(siasm9, factory);

        DocumentOccurrence treatmentm9 = factory.createDocumentOccurrence("9 Months");
        treatmentm9.setDisplayText("9 Months");
        treatment.addOccurrence(treatmentm9);
        treatmentm9.setDocumentGroup(nineMonths);
        //createNineMonthReminders(treatmentm9, factory);

        DocumentOccurrence nphrm9 = factory.createDocumentOccurrence("9 Months");
        nphrm9.setDisplayText("9 Months");
        nphr.addOccurrence(nphrm9);
        nphrm9.setDocumentGroup(nineMonths);
        //createNineMonthReminders(nphrm9, factory);

        DocumentOccurrence phrm9 = factory.createDocumentOccurrence("9 Months");
        phrm9.setDisplayText("9 Months");
        phr.addOccurrence(phrm9);
        phrm9.setDocumentGroup(nineMonths);
        //createNineMonthReminders(phrm9, factory);

        DocumentOccurrence epqm9 = factory.createDocumentOccurrence("9 Months");
        epqm9.setDisplayText("9 Months");
        epq.addOccurrence(epqm9);
        epqm9.setDocumentGroup(nineMonths);
        //createNineMonthReminders(epqm9, factory);


        // Month 12
        DocumentOccurrence caarmsm12 = factory.createDocumentOccurrence("12 Months");
        caarmsm12.setDisplayText("12 Months");
        caarmsWithGAF.addOccurrence(caarmsm12);
        caarmsm12.setDocumentGroup(twelveMonths);
        createTwelveMonthReminders(caarmsm12, factory);

        DocumentOccurrence bdi7m12 = factory.createDocumentOccurrence("12 Months");
        bdi7m12.setDisplayText("12 Months");
        bdi7.addOccurrence(bdi7m12);
        bdi7m12.setDocumentGroup(twelveMonths);
        //createTwelveMonthReminders(bdi7m12, factory);

        DocumentOccurrence eq5dm12 = factory.createDocumentOccurrence("12 Months");
        eq5dm12.setDisplayText("12 Months");
        eq5d.addOccurrence(eq5dm12);
        eq5dm12.setDocumentGroup(twelveMonths);
        //createTwelveMonthReminders(eq5dm12, factory);

        DocumentOccurrence siasm12 = factory.createDocumentOccurrence("12 Months");
        siasm12.setDisplayText("12 Months");
        sias.addOccurrence(siasm12);
        siasm12.setDocumentGroup(twelveMonths);
        //createTwelveMonthReminders(siasm12, factory);

        DocumentOccurrence pbiqm12 = factory.createDocumentOccurrence("12 Months");
        pbiqm12.setDisplayText("12 Months");
        pbiq.addOccurrence(pbiqm12);
        pbiqm12.setDocumentGroup(twelveMonths);
        //createTwelveMonthReminders(pbiqm12, factory);

        DocumentOccurrence drugCheckm12 = factory.createDocumentOccurrence("12 Months");
        drugCheckm12.setDisplayText("12 Months");
        drugCheck.addOccurrence(drugCheckm12);
        drugCheckm12.setDocumentGroup(twelveMonths);
        //createTwelveMonthReminders(drugCheckm12, factory);

        DocumentOccurrence mansam12 = factory.createDocumentOccurrence("12 Months");
        mansam12.setDisplayText("12 Months");
        mansa.addOccurrence(mansam12);
        mansam12.setDocumentGroup(twelveMonths);
        //createTwelveMonthReminders(mansam12, factory);

        DocumentOccurrence treatmentm12 = factory.createDocumentOccurrence("12 Months");
        treatmentm12.setDisplayText("12 Months");
        treatment.addOccurrence(treatmentm12);
        treatmentm12.setDocumentGroup(twelveMonths);
        //createTwelveMonthReminders(treatmentm12, factory);

        DocumentOccurrence nphrm12 = factory.createDocumentOccurrence("12 Months");
        nphrm12.setDisplayText("12 Months");
        nphr.addOccurrence(nphrm12);
        nphrm12.setDocumentGroup(twelveMonths);
        //createTwelveMonthReminders(nphrm12, factory);

        DocumentOccurrence phrm12 = factory.createDocumentOccurrence("12 Months");
        phrm12.setDisplayText("12 Months");
        phr.addOccurrence(phrm12);
        phrm12.setDocumentGroup(twelveMonths);
        //createTwelveMonthReminders(phrm12, factory);

        DocumentOccurrence epqm12 = factory.createDocumentOccurrence("12 Months");
        epqm12.setDisplayText("12 Months");
        epq.addOccurrence(epqm12);
        epqm12.setDocumentGroup(twelveMonths);
        //createTwelveMonthReminders(epqm12, factory);

        // Month 15
        DocumentOccurrence caarmsm15 = factory.createDocumentOccurrence("15 Months");
        caarmsm15.setDisplayText("15 Months");
        caarmsWithGAF.addOccurrence(caarmsm15);
        caarmsm15.setDocumentGroup(fifteenMonths);
        createFifteenMonthReminders(caarmsm15, factory);

        DocumentOccurrence bdi7m15 = factory.createDocumentOccurrence("15 Months");
        bdi7m15.setDisplayText("15 Months");
        bdi7.addOccurrence(bdi7m15);
        bdi7m15.setDocumentGroup(fifteenMonths);
        //createFifteenMonthReminders(bdi7m15, factory);

        DocumentOccurrence eq5dm15 = factory.createDocumentOccurrence("15 Months");
        eq5dm15.setDisplayText("15 Months");
        eq5d.addOccurrence(eq5dm15);
        eq5dm15.setDocumentGroup(fifteenMonths);
        //createFifteenMonthReminders(eq5dm15, factory);

        DocumentOccurrence siasm15 = factory.createDocumentOccurrence("15 Months");
        siasm15.setDisplayText("15 Months");
        sias.addOccurrence(siasm15);
        siasm15.setDocumentGroup(fifteenMonths);
        //createFifteenMonthReminders(siasm15, factory);

        DocumentOccurrence treatmentm15 = factory.createDocumentOccurrence("15 Months");
        treatmentm15.setDisplayText("15 Months");
        treatment.addOccurrence(treatmentm15);
        treatmentm15.setDocumentGroup(fifteenMonths);
        //createFifteenMonthReminders(treatmentm15, factory);

        DocumentOccurrence nphrm15 = factory.createDocumentOccurrence("15 Months");
        nphrm15.setDisplayText("15 Months");
        nphr.addOccurrence(nphrm15);
        nphrm15.setDocumentGroup(fifteenMonths);
        //createFifteenMonthReminders(nphrm15, factory);

        DocumentOccurrence phrm15 = factory.createDocumentOccurrence("15 Months");
        phrm15.setDisplayText("15 Months");
        phr.addOccurrence(phrm15);
        phrm15.setDocumentGroup(fifteenMonths);
        //createFifteenMonthReminders(phrm15, factory);

        DocumentOccurrence epqm15 = factory.createDocumentOccurrence("15 Months");
        epqm15.setDisplayText("15 Months");
        epq.addOccurrence(epqm15);
        epqm15.setDocumentGroup(fifteenMonths);
        //createFifteenMonthReminders(epqm15, factory);

        // Month 18
        DocumentOccurrence caarmsm18 = factory.createDocumentOccurrence("18 Months");
        caarmsm18.setDisplayText("18 Months");
        caarmsWithGAF.addOccurrence(caarmsm18);
        caarmsm18.setDocumentGroup(eighteenMonths);
        createEighteenMonthReminders(caarmsm18, factory);

        DocumentOccurrence bdi7m18 = factory.createDocumentOccurrence("18 Months");
        bdi7m18.setDisplayText("18 Months");
        bdi7.addOccurrence(bdi7m18);
        bdi7m18.setDocumentGroup(eighteenMonths);
        //createEighteenMonthReminders(bdi7m18, factory);

        DocumentOccurrence eq5dm18 = factory.createDocumentOccurrence("18 Months");
        eq5dm18.setDisplayText("18 Months");
        eq5d.addOccurrence(eq5dm18);
        eq5dm18.setDocumentGroup(eighteenMonths);
        //createEighteenMonthReminders(eq5dm18, factory);

        DocumentOccurrence siasm18 = factory.createDocumentOccurrence("18 Months");
        siasm18.setDisplayText("18 Months");
        sias.addOccurrence(siasm18);
        siasm18.setDocumentGroup(eighteenMonths);
        //createEighteenMonthReminders(siasm18, factory);

        DocumentOccurrence pbiqm18 = factory.createDocumentOccurrence("18 Months");
        pbiqm18.setDisplayText("18 Months");
        pbiq.addOccurrence(pbiqm18);
        pbiqm18.setDocumentGroup(eighteenMonths);
        //createEighteenMonthReminders(pbiqm18, factory);

        DocumentOccurrence drugCheckm18 = factory.createDocumentOccurrence("18 Months");
        drugCheckm18.setDisplayText("18 Months");
        drugCheck.addOccurrence(drugCheckm18);
        drugCheckm18.setDocumentGroup(eighteenMonths);
        //createEighteenMonthReminders(drugCheckm18, factory);

        DocumentOccurrence mansam18 = factory.createDocumentOccurrence("18 Months");
        mansam18.setDisplayText("18 Months");
        mansa.addOccurrence(mansam18);
        mansam18.setDocumentGroup(eighteenMonths);
        //createEighteenMonthReminders(mansam18, factory);

        DocumentOccurrence treatmentm18 = factory.createDocumentOccurrence("18 Months");
        treatmentm18.setDisplayText("18 Months");
        treatment.addOccurrence(treatmentm18);
        treatmentm18.setDocumentGroup(eighteenMonths);
        //createEighteenMonthReminders(treatmentm18, factory);

        DocumentOccurrence nphrm18 = factory.createDocumentOccurrence("18 Months");
        nphrm18.setDisplayText("18 Months");
        nphr.addOccurrence(nphrm18);
        nphrm18.setDocumentGroup(eighteenMonths);
        //createEighteenMonthReminders(nphrm18, factory);

        DocumentOccurrence phrm18 = factory.createDocumentOccurrence("18 Months");
        phrm18.setDisplayText("18 Months");
        phr.addOccurrence(phrm18);
        phrm18.setDocumentGroup(eighteenMonths);
        //createEighteenMonthReminders(phrm18, factory);

        DocumentOccurrence epqm18 = factory.createDocumentOccurrence("18 Months");
        epqm18.setDisplayText("18 Months");
        epq.addOccurrence(epqm18);
        epqm18.setDocumentGroup(eighteenMonths);
        //createEighteenMonthReminders(epqm18, factory);

        // Month 21
        DocumentOccurrence caarmsm21 = factory.createDocumentOccurrence("21 Months");
        caarmsm21.setDisplayText("21 Months");
        caarmsWithGAF.addOccurrence(caarmsm21);
        caarmsm21.setDocumentGroup(twentyOneMonths);
        createTwentyOneMonthReminders(caarmsm21, factory);

        DocumentOccurrence bdi7m21 = factory.createDocumentOccurrence("21 Months");
        bdi7m21.setDisplayText("21 Months");
        bdi7.addOccurrence(bdi7m21);
        bdi7m21.setDocumentGroup(twentyOneMonths);
        //createTwentyOneMonthReminders(bdi7m21, factory);

        DocumentOccurrence eq5dm21 = factory.createDocumentOccurrence("21 Months");
        eq5dm21.setDisplayText("21 Months");
        eq5d.addOccurrence(eq5dm21);
        eq5dm21.setDocumentGroup(twentyOneMonths);
        //createTwentyOneMonthReminders(eq5dm21, factory);

        DocumentOccurrence siasm21 = factory.createDocumentOccurrence("21 Months");
        siasm21.setDisplayText("21 Months");
        sias.addOccurrence(siasm21);
        siasm21.setDocumentGroup(twentyOneMonths);
        //createTwentyOneMonthReminders(siasm21, factory);

        DocumentOccurrence treatmentm21 = factory.createDocumentOccurrence("21 Months");
        treatmentm21.setDisplayText("21 Months");
        treatment.addOccurrence(treatmentm21);
        treatmentm21.setDocumentGroup(twentyOneMonths);
        //createTwentyOneMonthReminders(treatmentm21, factory);

        DocumentOccurrence nphrm21 = factory.createDocumentOccurrence("21 Months");
        nphrm21.setDisplayText("21 Months");
        nphr.addOccurrence(nphrm21);
        nphrm21.setDocumentGroup(twentyOneMonths);
        //createTwentyOneMonthReminders(nphrm21, factory);

        DocumentOccurrence phrm21 = factory.createDocumentOccurrence("21 Months");
        phrm21.setDisplayText("21 Months");
        phr.addOccurrence(phrm21);
        phrm21.setDocumentGroup(twentyOneMonths);
        //createTwentyOneMonthReminders(phrm21, factory);

        DocumentOccurrence epqm21 = factory.createDocumentOccurrence("21 Months");
        epqm21.setDisplayText("21 Months");
        epq.addOccurrence(epqm21);
        epqm21.setDocumentGroup(twentyOneMonths);
        //createTwentyOneMonthReminders(epqm21, factory);

        // Month 24
        DocumentOccurrence caarmsm24 = factory.createDocumentOccurrence("24 Months");
        caarmsm24.setDisplayText("24 Months");
        caarmsWithGAF.addOccurrence(caarmsm24);
        caarmsm24.setDocumentGroup(twentyFourMonths);
        createTwentyFourMonthReminders(caarmsm24, factory);

        DocumentOccurrence bdi7m24 = factory.createDocumentOccurrence("24 Months");
        bdi7m24.setDisplayText("24 Months");
        bdi7.addOccurrence(bdi7m24);
        bdi7m24.setDocumentGroup(twentyFourMonths);
        //createTwentyFourMonthReminders(bdi7m24, factory);

        DocumentOccurrence eq5dm24 = factory.createDocumentOccurrence("24 Months");
        eq5dm24.setDisplayText("24 Months");
        eq5d.addOccurrence(eq5dm24);
        eq5dm24.setDocumentGroup(twentyFourMonths);
        //createTwentyFourMonthReminders(eq5dm24, factory);

        DocumentOccurrence siasm24 = factory.createDocumentOccurrence("24 Months");
        siasm24.setDisplayText("24 Months");
        sias.addOccurrence(siasm24);
        siasm24.setDocumentGroup(twentyFourMonths);
        createTwentyFourMonthReminders(siasm24, factory);

        DocumentOccurrence pbiqm24 = factory.createDocumentOccurrence("24 Months");
        pbiqm24.setDisplayText("24 Months");
        pbiq.addOccurrence(pbiqm24);
        pbiqm24.setDocumentGroup(twentyFourMonths);
        //createTwentyFourMonthReminders(pbiqm24, factory);

        DocumentOccurrence drugCheckm24 = factory.createDocumentOccurrence("24 Months");
        drugCheckm24.setDisplayText("24 Months");
        drugCheck.addOccurrence(drugCheckm24);
        drugCheckm24.setDocumentGroup(twentyFourMonths);
        //createTwentyFourMonthReminders(drugCheckm24, factory);

        DocumentOccurrence mansam24 = factory.createDocumentOccurrence("24 Months");
        mansam24.setDisplayText("24 Months");
        mansa.addOccurrence(mansam24);
        mansam24.setDocumentGroup(twentyFourMonths);
        //createTwentyFourMonthReminders(mansam24, factory);

        DocumentOccurrence treatmentm24 = factory.createDocumentOccurrence("24 Months");
        treatmentm24.setDisplayText("24 Months");
        treatment.addOccurrence(treatmentm24);
        treatmentm24.setDocumentGroup(twentyFourMonths);
        //createTwentyFourMonthReminders(treatmentm24, factory);

        DocumentOccurrence nphrm24 = factory.createDocumentOccurrence("24 Months");
        nphrm24.setDisplayText("24 Months");
        nphr.addOccurrence(nphrm24);
        nphrm24.setDocumentGroup(twentyFourMonths);
        //createTwentyFourMonthReminders(nphrm24, factory);

        DocumentOccurrence phrm24 = factory.createDocumentOccurrence("24 Months");
        phrm24.setDisplayText("24 Months");
        phr.addOccurrence(phrm24);
        phrm24.setDocumentGroup(twentyFourMonths);
        //createTwentyFourMonthReminders(phrm24, factory);

        DocumentOccurrence epqm24 = factory.createDocumentOccurrence("24 Months");
        epqm24.setDisplayText("24 Months");
        epq.addOccurrence(epqm24);
        epqm24.setDocumentGroup(twentyFourMonths);
        //createTwentyFourMonthReminders(epqm24, factory);

        // Transition
        Document tranDoc = Transition.createDocument(factory);
        dataSet.addDocument(tranDoc);
        tranDoc.addConsentFormGroup(cfg);
        DocumentOccurrence tranDocOcc = factory.createDocumentOccurrence("Transition");
        tranDocOcc.setDisplayText("Transition");
        tranDoc.addOccurrence(tranDocOcc);
        tranDocOcc.setDocumentGroup(transition);

        DocumentOccurrence caarmsTransition = factory.createDocumentOccurrence("Transition");
        caarmsTransition.setDisplayText("Transition");
        caarmsWithGAF.addOccurrence(caarmsTransition);
        caarmsTransition.setDocumentGroup(transition);

        Document scidpddoc = SCIDPsychDisorders.createDocument(factory);
        dataSet.addDocument(scidpddoc);
        scidpddoc.addConsentFormGroup(cfg);
        DocumentOccurrence scidTransition = factory.createDocumentOccurrence("Transition");
        scidTransition.setDisplayText("Transition");
        scidpddoc.addOccurrence(scidTransition);
        scidTransition.setDocumentGroup(transition);

        DocumentOccurrence bdi7Transition = factory.createDocumentOccurrence("Transition");
        bdi7Transition.setDisplayText("Transition");
        bdi7.addOccurrence(bdi7Transition);
        bdi7Transition.setDocumentGroup(transition);

        DocumentOccurrence eq5dTransition = factory.createDocumentOccurrence("Transition");
        eq5dTransition.setDisplayText("Transition");
        eq5d.addOccurrence(eq5dTransition);
        eq5dTransition.setDocumentGroup(transition);

        DocumentOccurrence siasTransition = factory.createDocumentOccurrence("Transition");
        siasTransition.setDisplayText("Transition");
        sias.addOccurrence(siasTransition);
        siasTransition.setDocumentGroup(transition);

        DocumentOccurrence pbiqTransition = factory.createDocumentOccurrence("Transition");
        pbiqTransition.setDisplayText("Transition");
        pbiq.addOccurrence(pbiqTransition);
        pbiqTransition.setDocumentGroup(transition);

        DocumentOccurrence drugCheckTransition = factory.createDocumentOccurrence("Transition");
        drugCheckTransition.setDisplayText("Transition");
        drugCheck.addOccurrence(drugCheckTransition);
        drugCheckTransition.setDocumentGroup(transition);

        DocumentOccurrence mansaTransition = factory.createDocumentOccurrence("Transition");
        mansaTransition.setDisplayText("Transition");
        mansa.addOccurrence(mansaTransition);
        mansaTransition.setDocumentGroup(transition);

        DocumentOccurrence treatmentTransition = factory.createDocumentOccurrence("Transition");
        treatmentTransition.setDisplayText("Transition");
        treatment.addOccurrence(treatmentTransition);
        treatmentTransition.setDocumentGroup(transition);

        // Termination
        Document termDoc = StudyTermination.createDocument(factory);
        dataSet.addDocument(termDoc);
        termDoc.addConsentFormGroup(cfg);
        DocumentOccurrence termDocOcc = factory.createDocumentOccurrence("Termination");
        termDocOcc.setDisplayText("Termination");
        termDoc.addOccurrence(termDocOcc);
        termDocOcc.setDocumentGroup(studyTermination);

        DocumentOccurrence scidTermination = factory.createDocumentOccurrence("Termination");
        scidTermination.setDisplayText("Termination");
        scidpddoc.addOccurrence(scidTermination);
        scidTermination.setDocumentGroup(studyTermination);



		/*
		 * Add an RBACAction to each document
		 */
		addDocumentActions(dataSet);

        return dataSet;
    }

    public static void createReports(DataSet ds, String saml) throws Exception {

        //Management reports
        IReport cpmReport = Reports.cpmMgmtReport(ds);
        IReport ciReport = Reports.ciMgmtReport(ds);
        IReport piMaReport = Reports.piManchesterMgmtReport(ds);
        IReport piEaReport = Reports.piEastAngliaMgmtReport(ds);
        IReport piGlReport = Reports.piGlasgowMgmtReport(ds);
        IReport piCaReport = Reports.piCambridgeMgmtReport(ds);
        IReport piBhReport = Reports.piBirminghamMgmtReport(ds);
        IReport ukCRNReport= Reports.ukCRNReport(ds);
        IReport ciRecruitment = Reports.recruitmentReport(ds);
        IReport cpmRecruitment = Reports.cpmRecruitmentReport(ds);
        IReport recruitmentCambridge    = Reports.recruitmentInCambridgeReport(ds);
        IReport recruitmentEastAnglia   = Reports.recruitmentInEastAngliaReport(ds);
        IReport recruitmentBirmingham	= Reports.recruitmentInBirminghamReport(ds);
        IReport recruitmentManchester   = Reports.recruitmentInManchesterReport(ds);
        IReport recruitmentEastMidlands = Reports.recruitmentInEastMidlandsReport(ds);
        IReport recruitmentGlasgow      = Reports.recruitmentInGlasgowReport(ds);
        IReport ciReceivingTreatment    = Reports.ciReceivingTreatmentReport(ds);
        IReport cpmReceivingTreatment   = Reports.cpmReceivingTreatmentReport(ds);
        IReport receivingTreatmentInCambridge = Reports.receivingTreatmentInCambridgeReport(ds);

        IReport recordStatusReport = Reports.recordStatusReport(ds, null, "");
        IReport documentStatusReport = Reports.documentStatusReport(ds, null, "");
        IReport collectionDateReport = Reports.collectionDateReport(ds, null, "");
        IReport stdCodeStatusReport  = Reports.stdCodeStatusReport(ds);
        IReport basicStatsReport = Reports.basicStatisticsReport(ds);

        //save the reports
        ReportsClient client = new ReportsClient();
        client.saveReport(cpmReport, saml);
        client.saveReport(ciReport, saml);
        client.saveReport(piMaReport, saml);
        client.saveReport(piEaReport, saml);
        client.saveReport(piGlReport, saml);
        client.saveReport(piCaReport, saml);
        client.saveReport(piBhReport, saml);
        client.saveReport(ukCRNReport, saml);
        client.saveReport(ciRecruitment, saml);
        client.saveReport(cpmRecruitment, saml);
        client.saveReport(recruitmentCambridge, saml);
        client.saveReport(recruitmentEastAnglia, saml);
        client.saveReport(recruitmentBirmingham, saml);
        client.saveReport(recruitmentManchester, saml);
        client.saveReport(recruitmentEastMidlands, saml);
        client.saveReport(recruitmentGlasgow, saml);
        client.saveReport(ciReceivingTreatment, saml);
        client.saveReport(cpmReceivingTreatment, saml);
        client.saveReport(receivingTreatmentInCambridge, saml);

        client.saveReport(recordStatusReport, saml);
        client.saveReport(documentStatusReport, saml);
        client.saveReport(collectionDateReport, saml);
        client.saveReport(stdCodeStatusReport, saml);
        client.saveReport(basicStatsReport, saml);
    }
    private static void createOneMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(30));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(23, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(37, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(51, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }
    private static void createTwoMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(60));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(53, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(67, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(81, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }
    private static void createThreeMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(90));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(83, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(97, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(111, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }
    private static void createFourMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(120));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(113, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(127, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(141, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }
    private static void createFiveMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(150));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(143, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(157, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(171, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }

    private static void createSixMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(182));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(175, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(212, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(242, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }

    private static void createNineMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(270));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(263, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(300, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(530, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }

    private static void createTwelveMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(365));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(358, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(395, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(425, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }
    private static void createFifteenMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(450));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(443, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(480, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(510, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }
    private static void createEighteenMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(540));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(537, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(570, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(600, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }
    private static void createTwentyOneMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(630));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(623, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(660, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(690, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }
    private static void createTwentyFourMonthReminders(DocumentOccurrence occurrence, Factory factory){
        occurrence.setScheduleTime(new Integer(730));
        occurrence.setScheduleUnits(TimeUnits.DAYS);
        Reminder rem1 = factory.createReminder(727, TimeUnits.DAYS, ReminderLevel.MILD);
        Reminder rem2 = factory.createReminder(760, TimeUnits.DAYS, ReminderLevel.NORMAL);
        Reminder rem3 = factory.createReminder(790, TimeUnits.DAYS, ReminderLevel.SEVERE);
        occurrence.addReminder(rem1);
        occurrence.addReminder(rem2);
        occurrence.addReminder(rem3);
    }

    /**
     * Get all DataSet statuses having the given generic state.
     *
     * @param dataSet
     * @param genericState
     * @return statuses
     */
    private static List<Status> getStatuses(DataSet dataSet, GenericState genericState) {
    	List<Status> statuses = new ArrayList<Status>();
    	String state = genericState.toString();
    	for (Status status: ((DataSet)dataSet).getStatuses()) {
    		if (status.getGenericState() != null && state.equals(status.getGenericState().toString())) {
    			statuses.add(status);
    		}
    	}
    	return statuses;
    }


	private static void addDocumentActions(DataSet dataSet) {
		for (Document document: ((DataSet)dataSet).getDocuments()) {
			document.setAction(RBACAction.ACTION_DR_DOC_STANDARD);
		}
	}
}
