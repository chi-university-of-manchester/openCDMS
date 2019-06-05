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
import org.psygrid.outlook.AdverseOutcomesCarer;
import org.psygrid.outlook.AdverseOutcomesClient;
import org.psygrid.outlook.AdverseOutcomesClientSelfHarm;
import org.psygrid.outlook.AdverseOutcomesClientViolent;
import org.psygrid.outlook.CSSRI_Followup;
import org.psygrid.outlook.Calgary;
import org.psygrid.outlook.DUP;
import org.psygrid.outlook.DrugCheck;
import org.psygrid.outlook.EisFamilyHistory;
import org.psygrid.outlook.FileNoteLog;
import org.psygrid.outlook.GlobalAssessmentFunctioning;
import org.psygrid.outlook.InsightScale;
import org.psygrid.outlook.Opcrit;
import org.psygrid.outlook.Panss;
import org.psygrid.outlook.PathwaysToCare;
import org.psygrid.outlook.PreMorbidAdjustmentScale;
import org.psygrid.outlook.TimeUseScoreSheet;
import org.psygrid.outlook.TransformersFactory;
import org.psygrid.outlook.TreatmentDocumentation;
import org.psygrid.outlook.TreatmentDocumentationV2;
import org.psygrid.outlook.UnitsFactory;
import org.psygrid.outlook.ValidationRulesFactory;
import org.psygrid.outlook.YoungMania;
import org.psygrid.security.RBACAction;

public class NEDENDataset {

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

	public NEDENDataset(){

	}

	public void insert(String saml, String repository){
		try{
			RepositoryClient client = null;

			client = new RepositoryClient(new URL(repository));

			DataSet ds = createDataset();

			System.out.println("save");
			Long id = client.saveDataSet(ds, saml);
			System.out.println("publish");
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

	public static DataSet createDataset(){

		Factory factory = new HibernateFactory();

		DataSet dataSet = factory.createDataset("National EDEN", "National EDEN");
		UnitWrapper.setFactory(new UnitsFactory());
		UnitWrapper.instance().init(factory, dataSet);
		TransformersWrapper.setFactory(new TransformersFactory());
		TransformersWrapper.instance().init(factory, dataSet);
		ValidationRulesWrapper.setFactory(new ValidationRulesFactory());
		ValidationRulesWrapper.instance().init(factory, dataSet);

		dataSet.setProjectCode("NED");
		dataSet.setScheduleStartQuestion("Date of first contact with EIS:");
		dataSet.setReviewReminderCount(0);
		dataSet.setSecondaryProjectCode("OLK");

		//groups
		Group grp1 = (Group)factory.createGroup("001001");
		grp1.setLongName("Heart of Birmingham - West EIS");
		grp1.addSite(new Site("Birmingham EIS", "N0000678", "B6 4NF", grp1));
		grp1.addSecondaryGroup("008001");
		Group grp2 = (Group)factory.createGroup("002001");
		grp2.setLongName("Heart of Birmingham - East EIS");
		grp2.addSite(new Site("Birmingham EIS", "N0000678", "B6 4NF", grp2));
		grp2.addSecondaryGroup("008001");
		Group grp3 = (Group)factory.createGroup("003001");
		grp3.setLongName("East PCT Birmingham");
		grp3.addSite(new Site("Birmingham EIS", "N0000678", "B6 4NF", grp3));
		grp3.addSecondaryGroup("008001");
		Group grp4 = (Group)factory.createGroup("004001");
		grp4.setLongName("Lancashire 001-400");
		grp4.addSite(new Site("Blackburn EIS", "N0000680", "BB6 7DD", grp4));
		//TODO secondary group if required
		Group grp5 = (Group)factory.createGroup("004002");
		grp5.setLongName("Lancashire 401-800");
		grp5.addSite(new Site("Preston EIS", "N0000679", "PR1 7LY", grp5));
		//TODO secondary group if required
		Group grp6 = (Group)factory.createGroup("005001");
		grp6.setLongName("Norfolk");
		grp6.addSite(new Site("East Anglia Norfolk EIS", "N0000682", "NR1 3RE", grp6));
		grp6.addSecondaryGroup("002001");
		Group grp7 = (Group)factory.createGroup("006001");
		grp7.setLongName("Cambridge CAMEO");
		grp7.addSite(new Site("CAMEO Cambridge", "N0000683", "CB1 5EE", grp7));
		grp7.addSecondaryGroup("002002");
		Group grp8 = (Group)factory.createGroup("007001");
		grp8.setLongName("Cornwall 001-500");
		grp8.addSite(new Site("Truro EIS", "N0000684", "TR15 2SP", grp8));
		grp8.addSecondaryGroup("001001");
		Group grp9 = (Group)factory.createGroup("007002");
		grp9.setLongName("Cornwall 501-1000");
		grp9.addSite(new Site("Plymouth EIS", "N0000685", "PL31 2QT", grp9));
		grp9.addSecondaryGroup("001001");
		Group grp10 = (Group)factory.createGroup("008001");
		grp10.setLongName("Birmingham South");
		grp10.addSite(new Site("Birmingham EIS", "N0000678", "B6 4NF", grp10));
		grp10.addSecondaryGroup("008001");
		Group grp11 = (Group)factory.createGroup("004003");
		grp11.setLongName("Lancashire-Blackpool and Morecambe");
		grp11.addSite(new Site("Blackpool and Morecambe EIS", "N0000681", "BL1 0AA", grp11));
		//TODO secondary group if required

		Group kingsLynn = (Group)factory.createGroup("009001");
		kingsLynn.setLongName("Kings Lynn");
		kingsLynn.addSite(new Site("Kings Lynn EIS", "N0002239", "PE30 5PD", kingsLynn));

		Group solihull = (Group)factory.createGroup("010001");
		solihull.setLongName("Solihull");
		solihull.addSite(new Site("Solihull EIS", "N0002243", "B37 7RW", solihull));
		solihull.addSecondaryGroup("008001");

		Group cheshireWirral = (Group)factory.createGroup("011001");
		cheshireWirral.setLongName("Cheshire and Wirral");
		cheshireWirral.addSite(new Site("Wirral EIT", "N0002240", "CH42 0LQ", cheshireWirral));
		cheshireWirral.addSite(new Site("West Cheshire EIT", "N0002241", "CH65 0BY", cheshireWirral));
		cheshireWirral.addSite(new Site("East Cheshire EIT", "N0002242", "CW1 4QJ", cheshireWirral));

		Group huntingdon = (Group)factory.createGroup("012001");
		huntingdon.setLongName("Huntingdon");
		huntingdon.addSite(new Site("Huntingdon EIS", "N0002244", "PE29 3RJ", huntingdon));

		Group peterborough = (Group)factory.createGroup("006002");
		peterborough.setLongName("Peterborough");
		peterborough.addSite(new Site("CAMEO North Early Intervention Service", "UKCRN ID", "PE3 6AN", peterborough));	//TODO UKCRN ID
		peterborough.addSecondaryGroup("002003");


		dataSet.addGroup(grp1);
		dataSet.addGroup(grp2);
		dataSet.addGroup(grp3);
		dataSet.addGroup(grp4);
		dataSet.addGroup(grp5);
		dataSet.addGroup(grp6);
		dataSet.addGroup(grp7);
		dataSet.addGroup(grp8);
		dataSet.addGroup(grp9);
		dataSet.addGroup(grp10);
		dataSet.addGroup(grp11);
		dataSet.addGroup(kingsLynn);
		dataSet.addGroup(solihull);
		dataSet.addGroup(cheshireWirral);
		dataSet.addGroup(huntingdon);
		dataSet.addGroup(peterborough);

		//consent
		ConsentFormGroup cfg = factory.createConsentFormGroup();
		cfg.setDescription("Main client consent");
		PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
		pcf.setQuestion("Has the client (aged over 16 years) agreed to take part in the study?");
		cfg.addConsentForm(pcf);
		PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
		pcf2.setQuestion("Has the client (aged 16 years or under) agreed to take part in the study?");
		cfg.addConsentForm(pcf2);
		AssociatedConsentForm acf = factory.createAssociatedConsentForm();
		acf.setQuestion("Has the client's legal guardian agreed to the client taking part in the study?");
		pcf2.addAssociatedConsentForm(acf);
		dataSet.addAllConsentFormGroup(cfg);

		//dataset statuses and their transitions
		Status statReferred = factory.createStatus("Referred", "Referred", 0);
		statReferred.setGenericState(GenericState.REFERRED);
		Status statScreenInelig = factory.createStatus("Ineligible", "Screened; ineligible", 1);
		statScreenInelig.setInactive(true);
		statScreenInelig.setGenericState(GenericState.INACTIVE);
		Status statUnableToConsent = factory.createStatus("Unable", "Unable to consent", 2);
		statUnableToConsent.setInactive(true);
		statUnableToConsent.setGenericState(GenericState.INACTIVE);
		Status statConsented = factory.createStatus("Consented", "Consented", 3);
		statConsented.setGenericState(GenericState.ACTIVE);
		Status statConsentRefused = factory.createStatus("Refused", "Consent refused", 4);
		statConsentRefused.setInactive(true);
		statConsentRefused.setGenericState(GenericState.INACTIVE);
		Status statClinicianWithdrew = factory.createStatus("Withdrawn", "Clinician withdrew referral", 5);
		statClinicianWithdrew.setInactive(true);
		statClinicianWithdrew.setGenericState(GenericState.INACTIVE);
		Status statInterview1 = factory.createStatus("Baseline", "Baseline completed", 6);
		statInterview1.setGenericState(GenericState.ACTIVE);
		Status stat6Month = factory.createStatus("6Month", "6 month follow-up completed", 7);
		stat6Month.setGenericState(GenericState.ACTIVE);
		Status stat12Month = factory.createStatus("12Month", "12 month follow-up completed", 8);
		stat12Month.setGenericState(GenericState.ACTIVE);
		Status statDeceased = factory.createStatus("Deceased", "Deceased", 9);
		statDeceased.setInactive(true);
		statDeceased.setGenericState(GenericState.LEFT);
		Status statWithdrew = factory.createStatus("Withdrew", "Withdrew", 10);
		statWithdrew.setInactive(true);
		statWithdrew.setGenericState(GenericState.INACTIVE);

		Status statInvalid = factory.createStatus("Invalid", "Invalid", 11);	//Record was added by mistake and shouldn't exist
		statInvalid.setInactive(true);
		statInvalid.setGenericState(GenericState.INVALID);

		Status statComplete = factory.createStatus("Complete", "Complete", 12);
		statComplete.setGenericState(GenericState.COMPLETED);
		statComplete.setInactive(true);

		Status statLeft = factory.createStatus("Left", "Left Study", 13);
		statLeft.setGenericState(GenericState.LEFT);
		statLeft.setInactive(true);

		statReferred.addStatusTransition(statScreenInelig);         //referred -> Screened; ineligible
		statReferred.addStatusTransition(statUnableToConsent);      //referred -> Unable to consent
		statReferred.addStatusTransition(statConsented);            //referred -> consented
		statReferred.addStatusTransition(statConsentRefused);       //referred -> consent refused
		statReferred.addStatusTransition(statClinicianWithdrew);    //referred -> clinician withdrew referral
		statReferred.addStatusTransition(statDeceased);             //referred -> deceased
		statReferred.addStatusTransition(statWithdrew);             //referred -> withdrew
		statReferred.addStatusTransition(statInvalid);              //referred -> invalid

		statConsented.addStatusTransition(statInterview1);          //consented -> interview 1 completed
		statConsented.addStatusTransition(stat6Month);         		//consented -> 6 month follow-up completed
		statConsented.addStatusTransition(stat12Month);             //consented -> 12 month follow up completed
		statConsented.addStatusTransition(statDeceased);            //consented -> deceased
		statConsented.addStatusTransition(statWithdrew);            //consented -> withdrew
		statConsented.addStatusTransition(statInvalid);             //consented -> invalid
		statConsented.addStatusTransition(statLeft);             	//consented -> left study

		statInterview1.addStatusTransition(stat6Month);         	//interview 1 completed -> 6 month follow-up completed
		statInterview1.addStatusTransition(stat12Month);            //interview 1 completed -> 12 month follow up completed
		statInterview1.addStatusTransition(statDeceased);           //interview 1 completed -> deceased
		statInterview1.addStatusTransition(statWithdrew);           //interview 1 completed -> withdrew
		statInterview1.addStatusTransition(statInvalid);            //interview 1 completed -> invalid
		statInterview1.addStatusTransition(statLeft);               //interview 1 completed -> left study

		stat6Month.addStatusTransition(stat12Month);                //6 month follow-up completed -> 12 month follow up completed
		stat6Month.addStatusTransition(statDeceased);               //6 month follow-up completed -> deceased
		stat6Month.addStatusTransition(statWithdrew);               //6 month follow-up completed -> withdrew
		stat6Month.addStatusTransition(statInvalid);                //6 month follow-up completed -> invalid
		stat6Month.addStatusTransition(statLeft);                //6 month follow-up completed -> invalid

		stat12Month.addStatusTransition(statComplete);				//12 month follow-up completed -> complete
		stat12Month.addStatusTransition(statDeceased);              //12 month follow-up completed -> deceased
		stat12Month.addStatusTransition(statWithdrew);              //12 month follow-up completed -> withdrew
		stat12Month.addStatusTransition(statInvalid);               //12 month follow-up completed -> invalid

		statLeft.addStatusTransition(statWithdrew);					//Left Study -> Withdrew
		statLeft.addStatusTransition(statDeceased);					//Left Study -> Deceased
		statLeft.addStatusTransition(statInvalid);					//Left Study -> Invalid

		dataSet.addStatus(statReferred);
		dataSet.addStatus(statScreenInelig);
		dataSet.addStatus(statUnableToConsent);
		dataSet.addStatus(statConsented);
		dataSet.addStatus(statConsentRefused);
		dataSet.addStatus(statClinicianWithdrew);
		dataSet.addStatus(statInterview1);
		dataSet.addStatus(stat6Month);
		dataSet.addStatus(stat12Month);
		dataSet.addStatus(statDeceased);
		dataSet.addStatus(statWithdrew);
		dataSet.addStatus(statInvalid);
		dataSet.addStatus(statComplete);
		dataSet.addStatus(statLeft);


		//document groups
		DocumentGroup baseline = factory.createDocumentGroup("Baseline Group");
		baseline.setDisplayText("Baseline");
		baseline.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		//baseline.addAllowedRecordStatus(statReferred);
		baseline.setUpdateStatus(statInterview1);

		DocumentGroup sixMonths = factory.createDocumentGroup("6 months Group");
		sixMonths.setDisplayText("6 months");
		sixMonths.addAllowedRecordStatus(statInterview1);
		sixMonths.addAllowedRecordStatus(stat6Month);
		sixMonths.addAllowedRecordStatus(stat12Month);
		sixMonths.setUpdateStatus(stat6Month);
		sixMonths.addPrerequisiteGroup(baseline);

		DocumentGroup twelveMonths = factory.createDocumentGroup("12 months Group");
		twelveMonths.setDisplayText("12 months");
		twelveMonths.addAllowedRecordStatus(stat6Month);
		twelveMonths.addAllowedRecordStatus(stat12Month);
		twelveMonths.setUpdateStatus(stat12Month);
		twelveMonths.addPrerequisiteGroup(sixMonths);

		DocumentGroup shared = factory.createDocumentGroup("Shared");
		shared.setDisplayText("Shared");
		shared.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		//shared.addAllowedRecordStatus(statReferred);

		dataSet.addDocumentGroup(baseline);
		dataSet.addDocumentGroup(sixMonths);
		dataSet.addDocumentGroup(twelveMonths);
		dataSet.addDocumentGroup(shared);

		//-------------------------------------------------------------------
		//Documents/occurrences in the Baseline
		//-------------------------------------------------------------------

		Document personalDetails =
			PersonalDetails.createDocument(factory);
		dataSet.addDocument(personalDetails);
		personalDetails.addConsentFormGroup(cfg);
		DocumentOccurrence personalDetailsBaseline =
			factory.createDocumentOccurrence("Baseline");
		personalDetailsBaseline.setLabel("1");
		personalDetailsBaseline.setDisplayText("Baseline");
		personalDetailsBaseline.setDocumentGroup(baseline);
		personalDetails.addOccurrence(personalDetailsBaseline);

		Document panss = Panss.createDocument(factory);
		dataSet.addDocument(panss);
		panss.addConsentFormGroup(cfg);
		panss.setSecondaryDocIndex(6L);
		DocumentOccurrence panssBaseline = factory.createDocumentOccurrence("Baseline");
		panssBaseline.setDisplayText("Baseline");
		panssBaseline.setDocumentGroup(baseline);
		panssBaseline.setLabel("2");
		panssBaseline.setSecondaryOccIndex(0L);
		panss.addOccurrence(panssBaseline);

		Document youngMania = YoungMania.createDocument(factory);
		dataSet.addDocument(youngMania);
		youngMania.addConsentFormGroup(cfg);
		youngMania.setSecondaryDocIndex(7L);
		DocumentOccurrence ymBaseline = factory.createDocumentOccurrence("Baseline");
		ymBaseline.setLabel("3");
		ymBaseline.setDisplayText("Baseline");
		ymBaseline.setDocumentGroup(baseline);
		ymBaseline.setSecondaryOccIndex(0L);
		youngMania.addOccurrence(ymBaseline);

		Document insight = InsightScale.createDocument(factory);
		dataSet.addDocument(insight);
		insight.addConsentFormGroup(cfg);
		insight.setSecondaryDocIndex(18L);
		DocumentOccurrence insightBaseline = factory.createDocumentOccurrence("Baseline");
		insightBaseline.setDisplayText("Baseline");
		insightBaseline.setDocumentGroup(baseline);
		insightBaseline.setSecondaryOccIndex(0L);
		insight.addOccurrence(insightBaseline);

		Document calgary = Calgary.createDocument(factory);
		dataSet.addDocument(calgary);
		calgary.addConsentFormGroup(cfg);
		calgary.setSecondaryDocIndex(13L);
		DocumentOccurrence calgBaseline = factory.createDocumentOccurrence("Baseline");
		calgBaseline.setDisplayText("Baseline");
		calgBaseline.setDocumentGroup(baseline);
		calgBaseline.setSecondaryOccIndex(0L);
		calgary.addOccurrence(calgBaseline);

		Document eis = EisFamilyHistory.createDocument(factory);
		dataSet.addDocument(eis);
		eis.addConsentFormGroup(cfg);
		eis.setSecondaryDocIndex(14L);
		DocumentOccurrence eisBaseline = factory.createDocumentOccurrence("Baseline");
		eisBaseline.setDisplayText("Baseline");
		eisBaseline.setDocumentGroup(baseline);
		eisBaseline.setSecondaryOccIndex(0L);
		eis.addOccurrence(eisBaseline);

		Document drugCheck = DrugCheck.createDocument(factory);
		dataSet.addDocument(drugCheck);
		drugCheck.addConsentFormGroup(cfg);
		drugCheck.setSecondaryDocIndex(10L);
		DocumentOccurrence drugCheckBaseline = factory.createDocumentOccurrence("Baseline");
		drugCheckBaseline.setDisplayText("Baseline");
		drugCheckBaseline.setDocumentGroup(baseline);
		drugCheckBaseline.setLabel("6");
		drugCheckBaseline.setSecondaryOccIndex(0L);
		drugCheck.addOccurrence(drugCheckBaseline);

		Document gaf = GlobalAssessmentFunctioning.createDocument(factory);
		dataSet.addDocument(gaf);
		gaf.addConsentFormGroup(cfg);
		gaf.setSecondaryDocIndex(8L);
		DocumentOccurrence gafBaseline = factory.createDocumentOccurrence("Baseline");
		gafBaseline.setDisplayText("Baseline");
		gafBaseline.setDocumentGroup(baseline);
		gafBaseline.setLabel("4");
		gafBaseline.setSecondaryOccIndex(0L);
		gaf.addOccurrence(gafBaseline);

		Document eq5d = EQ5D.createDocument(factory);
		dataSet.addDocument(eq5d);
		eq5d.addConsentFormGroup(cfg);
		DocumentOccurrence eq5dBaseline = factory.createDocumentOccurrence("Baseline");
		eq5dBaseline.setDisplayText("Baseline");
		eq5dBaseline.setDocumentGroup(baseline);
		eq5d.addOccurrence(eq5dBaseline);

		Document dup = DUP.createDocument(factory);
		dataSet.addDocument(dup);
		dup.addConsentFormGroup(cfg);
		dup.setSecondaryDocIndex(9L);
		DocumentOccurrence dupBaseline = factory.createDocumentOccurrence("Baseline");
		dupBaseline.setDisplayText("Baseline");
		dupBaseline.setDocumentGroup(baseline);
		dupBaseline.setLabel("5");
		dupBaseline.setSecondaryOccIndex(0L);
		dup.addOccurrence(dupBaseline);

		Document pathwaysToCare = PathwaysToCare.createDocument(factory);
		dataSet.addDocument(pathwaysToCare);
		pathwaysToCare.addConsentFormGroup(cfg);
		pathwaysToCare.setSecondaryDocIndex(11L);
		DocumentOccurrence pathwaysBaseline = factory.createDocumentOccurrence("Baseline");
		pathwaysBaseline.setDisplayText("Baseline");
		pathwaysBaseline.setDocumentGroup(baseline);
		pathwaysBaseline.setSecondaryOccIndex(0L);
		pathwaysToCare.addOccurrence(pathwaysBaseline);

		Document premorbidAdjScale = PreMorbidAdjustmentScale.createDocument(factory);
		dataSet.addDocument(premorbidAdjScale);
		premorbidAdjScale.addConsentFormGroup(cfg);
		premorbidAdjScale.setSecondaryDocIndex(12L);
		DocumentOccurrence premorbidBaseline = factory.createDocumentOccurrence("Baseline");
		premorbidBaseline.setDisplayText("Baseline");
		premorbidBaseline.setDocumentGroup(baseline);
		premorbidBaseline.setSecondaryOccIndex(0L);
		premorbidAdjScale.addOccurrence(premorbidBaseline);

		Document adverseOutcomesSelfHarm = AdverseOutcomesClientSelfHarm.createDocument(factory);
		dataSet.addDocument(adverseOutcomesSelfHarm);
		adverseOutcomesSelfHarm.addConsentFormGroup(cfg);
		adverseOutcomesSelfHarm.setSecondaryDocIndex(19L);
		DocumentOccurrence addOutSelfHarmSeriousBaseline = factory.createDocumentOccurrence("Client, Most serious, Baseline");
		addOutSelfHarmSeriousBaseline.setDisplayText("Client, Most serious, Baseline");
		addOutSelfHarmSeriousBaseline.setDocumentGroup(baseline);
		addOutSelfHarmSeriousBaseline.setSecondaryOccIndex(0L);
		adverseOutcomesSelfHarm.addOccurrence(addOutSelfHarmSeriousBaseline);

		DocumentOccurrence addOutSelfHarmContactBaseline = factory.createDocumentOccurrence("Client, Closest to contact, Baseline");
		addOutSelfHarmContactBaseline.setDisplayText("Client, Closest to contact, Baseline");
		addOutSelfHarmContactBaseline.setDocumentGroup(baseline);
		addOutSelfHarmContactBaseline.setSecondaryOccIndex(1L);
		adverseOutcomesSelfHarm.addOccurrence(addOutSelfHarmContactBaseline);

		Document adverseOutcomesViolence = AdverseOutcomesClientViolent.createDocument(factory);
		dataSet.addDocument(adverseOutcomesViolence);
		adverseOutcomesViolence.addConsentFormGroup(cfg);
		adverseOutcomesViolence.setSecondaryDocIndex(20L);
		DocumentOccurrence addOutViolenceSeriousBaseline = factory.createDocumentOccurrence("Client, Most serious, Baseline");
		addOutViolenceSeriousBaseline.setDisplayText("Client, Most serious, Baseline");
		addOutViolenceSeriousBaseline.setDocumentGroup(baseline);
		addOutViolenceSeriousBaseline.setSecondaryOccIndex(0L);
		adverseOutcomesViolence.addOccurrence(addOutViolenceSeriousBaseline);

		DocumentOccurrence addOutViolenceContactBaseline = factory.createDocumentOccurrence("Client, Closest to contact, Baseline");
		addOutViolenceContactBaseline.setDisplayText("Client, Closest to contact, Baseline");
		addOutViolenceContactBaseline.setDocumentGroup(baseline);
		addOutViolenceContactBaseline.setSecondaryOccIndex(1L);
		adverseOutcomesViolence.addOccurrence(addOutViolenceContactBaseline);

		Document adverseOutcomesCarerSelfHarm = AdverseOutcomesCarerSelfHarm.createDocument(factory);
		dataSet.addDocument(adverseOutcomesCarerSelfHarm);
		adverseOutcomesCarerSelfHarm.addConsentFormGroup(cfg);
		DocumentOccurrence addOutCarerSelfHarmSeriousBaseline = factory.createDocumentOccurrence("Carer, Most serious, Baseline");
		addOutCarerSelfHarmSeriousBaseline.setDisplayText("Carer, Most serious, Baseline");
		addOutCarerSelfHarmSeriousBaseline.setDocumentGroup(baseline);
		adverseOutcomesCarerSelfHarm.addOccurrence(addOutCarerSelfHarmSeriousBaseline);

		DocumentOccurrence addOutCarerSelfHarmContactBaseline = factory.createDocumentOccurrence("Carer, Closest to contact, Baseline");
		addOutCarerSelfHarmContactBaseline.setDisplayText("Carer, Closest to contact, Baseline");
		addOutCarerSelfHarmContactBaseline.setDocumentGroup(baseline);
		adverseOutcomesCarerSelfHarm.addOccurrence(addOutCarerSelfHarmContactBaseline);

		Document adverseOutcomesCarerViolence = AdverseOutcomesCarerViolent.createDocument(factory);
		dataSet.addDocument(adverseOutcomesCarerViolence);
		adverseOutcomesCarerViolence.addConsentFormGroup(cfg);
		DocumentOccurrence addOutCarerViolenceSeriousBaseline = factory.createDocumentOccurrence("Carer, Most serious, Baseline");
		addOutCarerViolenceSeriousBaseline.setDisplayText("Carer, Most serious, Baseline");
		addOutCarerViolenceSeriousBaseline.setDocumentGroup(baseline);
		adverseOutcomesCarerViolence.addOccurrence(addOutCarerViolenceSeriousBaseline);

		DocumentOccurrence addOutCarerViolenceContactBaseline = factory.createDocumentOccurrence("Carer, Closest to contact, Baseline");
		addOutCarerViolenceContactBaseline.setDisplayText("Carer, Closest to contact, Baseline");
		addOutCarerViolenceContactBaseline.setDocumentGroup(baseline);
		adverseOutcomesCarerViolence.addOccurrence(addOutCarerViolenceContactBaseline);

		Document adverseOutcomesCarer = AdverseOutcomesCarer.createDocument(factory);
		dataSet.addDocument(adverseOutcomesCarer);
		adverseOutcomesCarer.addConsentFormGroup(cfg);
		//Occurrence now added in 6 month section below

		Document cssri = CSSRI.createDocument(factory);
		dataSet.addDocument(cssri);
		cssri.addConsentFormGroup(cfg);
		DocumentOccurrence cssriBaseline = factory.createDocumentOccurrence("Baseline");
		cssriBaseline.setDisplayText("Baseline");
		cssriBaseline.setDocumentGroup(baseline);
		cssri.addOccurrence(cssriBaseline);

		Document timeUse = TimeUseScoreSheet.createDocument(factory);
		dataSet.addDocument(timeUse);
		timeUse.addConsentFormGroup(cfg);
		timeUse.setSecondaryDocIndex(23L);
		DocumentOccurrence timeUseBaseline =
			factory.createDocumentOccurrence("Baseline");
		timeUseBaseline.setDisplayText("Baseline");
		timeUseBaseline.setDocumentGroup(baseline);
		timeUseBaseline.setSecondaryOccIndex(0L);
		timeUse.addOccurrence(timeUseBaseline);

		Document serviceEngagementScale = ServiceEngagementScale.createDocument(factory);
		dataSet.addDocument(serviceEngagementScale);
		serviceEngagementScale.addConsentFormGroup(cfg);


		//-------------------------------------------------------------------
		//Documents/occurrences in the 6 Month follow up group
		//-------------------------------------------------------------------

		DocumentOccurrence panssSixMonths = factory.createDocumentOccurrence("6 Months");
		panssSixMonths.setDocumentGroup(sixMonths);
		panssSixMonths.setDisplayText("6 Months");
		panssSixMonths.setSecondaryOccIndex(1L);
		panss.addOccurrence(panssSixMonths);

		DocumentOccurrence ymSixMonths = factory.createDocumentOccurrence("6 Months");
		ymSixMonths.setDocumentGroup(sixMonths);
		ymSixMonths.setDisplayText("6 Months");
		ymSixMonths.setSecondaryOccIndex(1L);
		youngMania.addOccurrence(ymSixMonths);

		DocumentOccurrence calgSixMonths = factory.createDocumentOccurrence("6 Months");
		calgSixMonths.setDocumentGroup(sixMonths);
		calgSixMonths.setDisplayText("6 Months");
		calgSixMonths.setSecondaryOccIndex(1L);
		calgary.addOccurrence(calgSixMonths);

		DocumentOccurrence timeUseSixMonths =
			factory.createDocumentOccurrence("6 Months");
		timeUseSixMonths.setDisplayText("6 Months");
		timeUseSixMonths.setDocumentGroup(sixMonths);
		timeUseSixMonths.setSecondaryOccIndex(1L);
		timeUse.addOccurrence(timeUseSixMonths);

		Document cssriFollowUp = CSSRI_Followup.createDocument(factory);
		dataSet.addDocument(cssriFollowUp);
		cssriFollowUp.addConsentFormGroup(cfg);
		cssriFollowUp.setSecondaryDocIndex(27L);
		//Note that the occurrence indices for CSSRI Follow Up are transposed between NED and OLK:
		//6 months is index 0 in NED and 1 in OLK; 12 months is index 1 in NED and 0 in OLK
		DocumentOccurrence cssriSixMonths = factory.createDocumentOccurrence("6 Months");
		cssriSixMonths.setDisplayText("6 Months");
		cssriSixMonths.setDocumentGroup(sixMonths);
		cssriSixMonths.setSecondaryOccIndex(1L);
		cssriFollowUp.addOccurrence(cssriSixMonths);

		Document adverseOutcomesClient = AdverseOutcomesClient.createDocument(factory);
		dataSet.addDocument(adverseOutcomesClient);
		adverseOutcomesClient.addConsentFormGroup(cfg);
		DocumentOccurrence addOutClientBaseline = factory.createDocumentOccurrence("6 Months");
		addOutClientBaseline.setDisplayText("6 Months");
		addOutClientBaseline.setDocumentGroup(sixMonths);
		adverseOutcomesClient.addOccurrence(addOutClientBaseline);

		DocumentOccurrence sesSixMonths = factory
		.createDocumentOccurrence("6 Months");
		sesSixMonths.setDisplayText("6 Months");
		serviceEngagementScale.addOccurrence(sesSixMonths);
		sesSixMonths.setDocumentGroup(sixMonths);

		DocumentOccurrence addOutCarerMostSeriousSixMonths = factory.createDocumentOccurrence("6 Months");
		addOutCarerMostSeriousSixMonths.setDisplayText("6 Months");
		addOutCarerMostSeriousSixMonths.setDocumentGroup(sixMonths);
		adverseOutcomesCarer.addOccurrence(addOutCarerMostSeriousSixMonths);


		//-------------------------------------------------------------------
		//Documents/occurrences in the 12 Month follow up  group
		//-------------------------------------------------------------------
		DocumentOccurrence panssTwelveMonths = factory.createDocumentOccurrence("12 Months");
		panssTwelveMonths.setDisplayText("12 Months");
		panssTwelveMonths.setDocumentGroup(twelveMonths);
		panssTwelveMonths.setSecondaryOccIndex(2L);
		panss.addOccurrence(panssTwelveMonths);

		DocumentOccurrence ymTwelveMonths = factory.createDocumentOccurrence("12 Months");
		ymTwelveMonths.setDisplayText("12 Months");
		ymTwelveMonths.setDocumentGroup(twelveMonths);
		ymTwelveMonths.setSecondaryOccIndex(2L);
		youngMania.addOccurrence(ymTwelveMonths);

		DocumentOccurrence calgTwelveMonths = factory.createDocumentOccurrence("12 Months");
		calgTwelveMonths.setDisplayText("12 Months");
		calgTwelveMonths.setDocumentGroup(twelveMonths);
		calgTwelveMonths.setSecondaryOccIndex(2L);
		calgary.addOccurrence(calgTwelveMonths);

		DocumentOccurrence gafTwelveMonths = factory.createDocumentOccurrence("12 Months");
		gafTwelveMonths.setDisplayText("12 Months");
		gafTwelveMonths.setDocumentGroup(twelveMonths);
		gafTwelveMonths.setSecondaryOccIndex(1L);
		gaf.addOccurrence(gafTwelveMonths);

		DocumentOccurrence drugCheckTwelveMonths = factory.createDocumentOccurrence("12 Months");
		drugCheckTwelveMonths.setDisplayText("12 Months");
		drugCheckTwelveMonths.setDocumentGroup(twelveMonths);
		drugCheckTwelveMonths.setSecondaryOccIndex(1L);
		drugCheck.addOccurrence(drugCheckTwelveMonths);

		DocumentOccurrence insightTwelveMonths = factory.createDocumentOccurrence("12 Months");
		insightTwelveMonths.setDisplayText("12 Months");
		insightTwelveMonths.setDocumentGroup(twelveMonths);
		insightTwelveMonths.setSecondaryOccIndex(1L);
		insight.addOccurrence(insightTwelveMonths);

		//Note that the occurrence indices for CSSRI Follow Up are transposed between NED and OLK:
		//6 months is index 0 in NED and 1 in OLK; 12 months is index 1 in NED and 0 in OLK
		DocumentOccurrence cssriTwelveMonths = factory.createDocumentOccurrence("12 Months");
		cssriTwelveMonths.setDisplayText("12 Months");
		cssriTwelveMonths.setDocumentGroup(twelveMonths);
		cssriTwelveMonths.setSecondaryOccIndex(0L);
		cssriFollowUp.addOccurrence(cssriTwelveMonths);

		DocumentOccurrence eq5dTwelveMonths = factory.createDocumentOccurrence("12 Months");
		eq5dTwelveMonths.setDisplayText("12 Months");
		eq5dTwelveMonths.setDocumentGroup(twelveMonths);
		eq5d.addOccurrence(eq5dTwelveMonths);

		DocumentOccurrence timeUseTwelveMonths = factory.createDocumentOccurrence("12 Months");
		timeUseTwelveMonths.setDisplayText("12 Months");
		timeUseTwelveMonths.setDocumentGroup(twelveMonths);
		timeUseTwelveMonths.setSecondaryOccIndex(2L);
		timeUse.addOccurrence(timeUseTwelveMonths);

		DocumentOccurrence addOutClientTwelve = factory.createDocumentOccurrence("12 Months");
		addOutClientTwelve.setDisplayText("12 Months");
		addOutClientTwelve.setDocumentGroup(twelveMonths);
		adverseOutcomesClient.addOccurrence(addOutClientTwelve);

		DocumentOccurrence sesTwelveMonths = factory
		.createDocumentOccurrence("12 Months");
		sesTwelveMonths.setDisplayText("12 Months");
		serviceEngagementScale.addOccurrence(sesTwelveMonths);
		sesTwelveMonths.setDocumentGroup(twelveMonths);

		//-------------------------------------------------------------------
		//Documents/occurrences in the Shared Group
		//-------------------------------------------------------------------

		//NOTE this document is now locked and superseded by the V2 treatment documentation
		//document below
		Document treatmentDocumentation = TreatmentDocumentation.createDocument(factory);
		dataSet.addDocument(treatmentDocumentation);
		treatmentDocumentation.addConsentFormGroup(cfg);
		treatmentDocumentation.setSecondaryDocIndex(0L);
		DocumentOccurrence tdb = factory.createDocumentOccurrence("Shared");
		tdb.setDisplayText("Shared");
		tdb.setSecondaryOccIndex(0L);
		tdb.setLocked(true);
		treatmentDocumentation.addOccurrence(tdb);
		tdb.setDocumentGroup(shared);

		Document relapseRating = RelapseRating.createDocument(factory);
		dataSet.addDocument(relapseRating);
		relapseRating.addConsentFormGroup(cfg);
		DocumentOccurrence rrb = factory.createDocumentOccurrence("Shared");
		rrb.setDisplayText("Shared");
		rrb.setLocked(true);
		relapseRating.addOccurrence(rrb);
		rrb.setDocumentGroup(shared);

		/*
		 *
		 * Documents patched in after deployment
		 *
		 */
		Document opcrit = Opcrit.createDocument(factory);
		dataSet.addDocument(opcrit);
		opcrit.addConsentFormGroup(cfg);
		opcrit.setSecondaryDocIndex(30L);
		DocumentOccurrence opcritOcc = factory.createDocumentOccurrence("12 Months");
		opcritOcc.setDisplayText("12 Months");
		opcritOcc.setSecondaryOccIndex(0L);
		opcrit.addOccurrence(opcritOcc);
		opcritOcc.setDocumentGroup(twelveMonths);

		Document treatDoc2 = TreatmentDocumentationV2.createDocument(factory);
		dataSet.addDocument(treatDoc2);
		treatDoc2.addConsentFormGroup(cfg);
		treatDoc2.setSecondaryDocIndex(31L);
		DocumentOccurrence tdbv2 = factory.createDocumentOccurrence("Shared");
		tdbv2.setDisplayText("Shared");
		tdbv2.setSecondaryOccIndex(0L);
		treatDoc2.addOccurrence(tdbv2);
		tdbv2.setDocumentGroup(shared);

		Document relapseAndRecovery = RelapseAndRecovery.createDocument(factory);
		dataSet.addDocument(relapseAndRecovery);
		relapseAndRecovery.addConsentFormGroup(cfg);
		DocumentOccurrence rrs = factory.createDocumentOccurrence("Shared");
		rrs.setDisplayText("Shared");
		relapseAndRecovery.addOccurrence(rrs);
		rrs.setDocumentGroup(shared);

		Document fnl = FileNoteLog.createDocument(factory);
		dataSet.addDocument(fnl);
		fnl.addConsentFormGroup(cfg);
		DocumentOccurrence fnlOcc = factory.createDocumentOccurrence("Shared");
		fnlOcc.setDisplayText("Shared");
		fnlOcc.setDocumentGroup(shared);
		fnl.addOccurrence(fnlOcc);

		/*
		 * Add an RBACAction to each document
		 */
		addDocumentActions(dataSet);

		return dataSet;
	}

	public static void createReports(DataSet ds, String saml) throws Exception {

		//Clinical reports
		IReport panssReportBaseline = Reports.panssReportBaseline(ds);
		IReport panssReport6Months = Reports.panssReport6Months(ds);
		IReport panssReport12Months = Reports.panssReport12Months(ds);
		IReport youngManiaReportBaseline = Reports.youngManiaBaseline(ds);
		IReport youngManiaReport6Months = Reports.youngMania6Months(ds);
		IReport youngManiaReport12Months = Reports.youngMania12Months(ds);
		IReport gafReportBaseline = Reports.gafReportBaseline(ds);
		IReport gafReport12Months = Reports.gafReport12Months(ds);
		IReport drugCheckReportBaseline = Reports.drugCheckReportBaseline(ds);
		IReport drugCheckReport12Months = Reports.drugCheckReport12Months(ds);
		IReport calgaryScaleReportBaseline = Reports.calgaryScaleReportBaseline(ds);
		IReport calgaryScaleReport6Months = Reports.calgaryScaleReport6Months(ds);
		IReport calgaryScaleReport12Months = Reports.calgaryScaleReport12Months(ds);
		IReport insightReportBaseline = Reports.insightScaleReportBaseline(ds);
		IReport insightReport12Months = Reports.insightScaleReport12Months(ds);
		IReport dupReportBaseline = Reports.DUPReportBaseline(ds);
		IReport eq5dReportBaseline = Reports.eq5dReportBaseline(ds);
		IReport eq5dReport12Months = Reports.eq5dReport12Months(ds);
		IReport premorbidAdjustmentReport = Reports.premorbidAdjustmentReport(ds);

		//Trend reports
		IReport insightScaleTrendsReportBaseline = Reports.insightScaleTrendsReportBaseline(ds);
		IReport insightScaleTrendsReport12Months = Reports.insightScaleTrendsReport12Months(ds);
		IReport youngManiaTrendsReportBaseline = Reports.youngManiaTrendsReportBaseline(ds);
		IReport youngManiaTrendsReport6Months = Reports.youngManiaTrendsReport6Months(ds);
		IReport youngManiaTrendsReport12Months = Reports.youngManiaTrendsReport12Months(ds);
		IReport calgaryScaleTrendsReportBaseline = Reports.calgaryScaleTrendsReportBaseline(ds);
		IReport calgaryScaleTrendsReport6Months = Reports.calgaryScaleTrendsReport6Months(ds);
		IReport calgaryScaleTrendsReport12Months = Reports.calgaryScaleTrendsReport12Months(ds);
		IReport eq5dTrendsReportBaseline = Reports.eq5dTrendsReportBaseline(ds);
		IReport eq5dTrendsReport12Months = Reports.eq5dTrendsReport12Months(ds);
		IReport gafTrendsReportBaseline = Reports.gafTrendsReportBaseline(ds);
		IReport gafTrendsReport12Months = Reports.gafTrendsReport12Months(ds);
		IReport pathwaysTrendsReport = Reports.pathwaysTrendsReport(ds);
		IReport premorbidTrendsReport = Reports.premorbidTrendsReport(ds);
		IReport dupHighLowTrendsReport = Reports.dupHighLowTrendsReport(ds);
		IReport panssTrendsReportBaseline = Reports.panssTrendsReportBaseline(ds);
		IReport panssTrendsReport6Months = Reports.panssTrendsReport6Months(ds);
		IReport panssTrendsReport12Months = Reports.panssTrendsReport12Months(ds);
		IReport dupTrendsReportBaseline = Reports.dupTrendsReportBaseline(ds);


		//Management reports
		IReport cpmReport = Reports.cpmMgmtReport(ds);
		IReport rmReport  = Reports.rmMgmtReport(ds);
		IReport ciReport  = Reports.ciMgmtReport(ds);
		IReport ukCRNReport = Reports.ukCRNReport(ds);
		IReport ciRecruitmentReport   = Reports.ciRecruitmentReport(ds);
		IReport cpmRecruitmentReport  = Reports.cpmRecruitmentReport(ds);
		IReport piCaRecruitmentReport = Reports.recruitmentInCambridgeReport(ds);
		IReport piCoRecruitmentReport = Reports.recruitmentInCornwallReport(ds);
		IReport piBirRecruitmentReport = Reports.recruitmentInBirminghamReport(ds);
		IReport piLaRecruitmentReport = Reports.recruitmentInLancashireReport(ds);
		IReport piNoRecruitmentReport = Reports.recruitmentInNorfolkReport(ds);
		IReport piKlRecruitmentReport = Reports.recruitmentInKingsLynnReport(ds);
		IReport piSoRecruitmentReport = Reports.recruitmentInSolihullReport(ds);
		IReport piCwRecruitmentReport = Reports.recruitmentInCheshireWirralReport(ds);
		IReport piPeRecruitmentReport = Reports.recruitmentInPeterboroughReport(ds);

		IReport recordStatusReport = Reports.recordStatusReport(ds, null, "");
		IReport documentStatusReport = Reports.documentStatusReport(ds, null, "");
		IReport collectionDateReport = Reports.collectionDateReport(ds, null, "");
		IReport stdCodeStatusReport  = Reports.stdCodeStatusReport(ds);
		IReport basicStatsReport = Reports.basicStatisticsReport(ds);

		//save the reports
		ReportsClient client = new ReportsClient();
		client.saveReport(panssReportBaseline, saml);
		client.saveReport(panssReport6Months, saml);
		client.saveReport(panssReport12Months, saml);
		client.saveReport(youngManiaReportBaseline, saml);
		client.saveReport(youngManiaReport6Months, saml);
		client.saveReport(youngManiaReport12Months, saml);
		client.saveReport(gafReportBaseline, saml);
		client.saveReport(gafReport12Months, saml);
		client.saveReport(drugCheckReportBaseline, saml);
		client.saveReport(drugCheckReport12Months, saml);
		client.saveReport(calgaryScaleReportBaseline, saml);
		client.saveReport(calgaryScaleReport6Months, saml);
		client.saveReport(calgaryScaleReport12Months, saml);
		client.saveReport(insightReportBaseline, saml);
		client.saveReport(insightReport12Months, saml);
		client.saveReport(dupReportBaseline, saml);
		client.saveReport(cpmReport, saml);
		client.saveReport(rmReport, saml);
		client.saveReport(ciReport, saml);
		client.saveReport(eq5dReportBaseline, saml);
		client.saveReport(eq5dReport12Months, saml);
		client.saveReport(premorbidAdjustmentReport, saml);

		client.saveReport(ukCRNReport, saml);

		client.saveReport(ciRecruitmentReport, saml);
		client.saveReport(cpmRecruitmentReport, saml);
		client.saveReport(piCaRecruitmentReport, saml);
		client.saveReport(piCoRecruitmentReport, saml);
		client.saveReport(piBirRecruitmentReport, saml);
		client.saveReport(piLaRecruitmentReport, saml);
		client.saveReport(piNoRecruitmentReport, saml);
		client.saveReport(piKlRecruitmentReport, saml);
		client.saveReport(piSoRecruitmentReport, saml);
		client.saveReport(piCwRecruitmentReport, saml);
		client.saveReport(piPeRecruitmentReport, saml);

		client.saveReport(insightScaleTrendsReportBaseline, saml);
		client.saveReport(insightScaleTrendsReport12Months, saml);
		client.saveReport(youngManiaTrendsReportBaseline, saml);
		client.saveReport(youngManiaTrendsReport6Months, saml);
		client.saveReport(youngManiaTrendsReport12Months, saml);
		client.saveReport(calgaryScaleTrendsReportBaseline, saml);
		client.saveReport(calgaryScaleTrendsReport6Months, saml);
		client.saveReport(calgaryScaleTrendsReport12Months, saml);
		client.saveReport(eq5dTrendsReportBaseline, saml);
		client.saveReport(eq5dTrendsReport12Months, saml);
		client.saveReport(gafTrendsReportBaseline, saml);
		client.saveReport(gafTrendsReport12Months, saml);
		client.saveReport(pathwaysTrendsReport, saml);
		client.saveReport(premorbidTrendsReport, saml);
		client.saveReport(dupHighLowTrendsReport, saml);
		client.saveReport(panssTrendsReportBaseline, saml);
		client.saveReport(panssTrendsReport6Months, saml);
		client.saveReport(panssTrendsReport12Months, saml);
		client.saveReport(dupTrendsReportBaseline, saml);

		client.saveReport(recordStatusReport, saml);
		client.saveReport(documentStatusReport, saml);
		client.saveReport(collectionDateReport, saml);
		client.saveReport(stdCodeStatusReport, saml);
		client.saveReport(basicStatsReport, saml);
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
