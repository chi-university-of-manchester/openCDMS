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
import org.psygrid.neden.RelapseRating;
import org.psygrid.security.RBACAction;

public class OutlookDataset {

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

	public OutlookDataset(){

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

	public static DataSet createDataset(){

		Factory factory = new HibernateFactory();

		DataSet dataSet = factory.createDataset("Outlook", "Outlook");
		UnitWrapper.setFactory(new UnitsFactory());
		UnitWrapper.instance().init(factory, dataSet);
		TransformersWrapper.setFactory(new TransformersFactory());
		TransformersWrapper.instance().init(factory, dataSet);
		ValidationRulesWrapper.setFactory(new ValidationRulesFactory());
		ValidationRulesWrapper.instance().init(factory, dataSet);

		dataSet.setProjectCode("OLK");
		dataSet.setVersionNo("1.0.0");
		dataSet.setScheduleStartQuestion("Please provide the date of the 1st contact with Mental Health services for this client:");
		dataSet.setReviewReminderCount(20);
		dataSet.setPrimaryProjectCode("NED");

		//groups and sites
		Group grp1 = (Group)factory.createGroup("001001");
		grp1.setLongName("Bristol Avon-Avon and Wiltshire Mental Health Partnership");
		Site site1 = new Site("Bath NHS House", "N0000688", "BA1 3QE", grp1);
		site1.addConsultant("Sarah Sullivan");
		grp1.addSite(site1);
		Group grp2 = (Group)factory.createGroup("002001");
		grp2.setLongName("East Anglia-Norfolk and Waveney Mental Health Partnership Trust");
		Site site2 = new Site("S1","N0000696", "NR6 5BE", grp2);
		site2.addConsultant("C1");
		site2.addConsultant("C2");
		grp2.addSite(site2);
		Site site2a = new Site("S2","N0000696", "NR6 5BE", grp2);
		site2a.addConsultant("D1");
		site2a.addConsultant("D2");
		grp2.addSite(site2a);
		Group grp3 = (Group)factory.createGroup("002002");
		grp3.setLongName("East Anglia-Cambridge CAMEO");
		Site site3 = new Site("T1", "N0000683", "CB1 5EE", grp3);
		site3.addConsultant("E1");
		site3.addConsultant("E2");
		grp3.addSite(site3);
		Site site3a = new Site("T2", "N0000683", "CB1 5EE", grp3);
		site3a.addConsultant("F1");
		site3a.addConsultant("F2");
		grp3.addSite(site3a);
		Group grp4 = (Group)factory.createGroup("003001");
		grp4.setLongName("East Midlands-Nottinghamshire Healthcare NHS Trust");
		Site site4a = new Site("Notts City and South EIP", "N0003742", "NG3 6AA", grp4);
		site4a.addConsultant("Peter Liddle");
		Site site4b = new Site("Mansfield and Ashfield EIP", "N0003743", "NG17 4HJ", grp4);
		site4b.addConsultant("Peter Liddle");
		Site site4c = new Site("Newark and Sherwood EIP", "N0003744", "NG24 4DE", grp4);
		site4c.addConsultant("Peter Liddle");
		grp4.addSite(site4a);
		grp4.addSite(site4b);
		grp4.addSite(site4c);
		Group grp5 = (Group)factory.createGroup("003002");
		grp5.setLongName("East Midlands-Lincolnshire Partnership NHS Foundation Trust");
		Site site5a = new Site("Grantham STEP", "N0003745", "NG31 9DF", grp5);
		site5a.addConsultant("Peter Ellwood");
		Site site5b = new Site("Boston STEP", "N0003746", "PE21 0AX", grp5);
		site5b.addConsultant("Peter Ellwood");
		Site site5c = new Site("Lincoln STEP", "N0003747", "LN1 1PB", grp5);
		site5c.addConsultant("Peter Ellwood");
		grp5.addSite(site5a);
		grp5.addSite(site5b);
		grp5.addSite(site5c);
		Group grp6 = (Group)factory.createGroup("004001");
		grp6.setLongName("North East-Newcastle, Northumberland and North Tyneside Mental Health Trust");
		Site site6 = new Site("St Nicholas Hospital", "N0000691", "NE3 3XT", grp6);
		//????
		grp6.addSite(site6);
		Group grp7 = (Group)factory.createGroup("005001");
		grp7.setLongName("North London-South West London and St. Georges Trust");
		Site site7 = new Site("Springfield University Hospital", "N0000687", "SW17 7DJ", grp7);
		site7.addConsultant("Tom Barnes");
		grp7.addSite(site7);
		Group grp8 = (Group)factory.createGroup("005002");
		grp8.setLongName("North London-Central and West London Trust");
		Site site8 = new Site("Trust HQ, Central & North West London", "N0000692", "W2 6LA", grp8);
		site8.addConsultant("Tom Barnes");
		grp8.addSite(site8);
		Group grp9 = (Group)factory.createGroup("006001");
		grp9.setLongName("North West-Manchester Mental Health and Social Care Trust");
		Site site9 = new Site("Chorlton House", "N0000693", "M21 9UN", grp9);
		site9.addConsultant("Max Marshall");
		site9.addConsultant("Shôn Lewis");
		grp9.addSite(site9);
		Group grp10 = (Group)factory.createGroup("007001");
		grp10.setLongName("South London-South London and Maudsely Trust");
		Site site10 = new Site("Bethlem Royal Hospital", "N0000694", "SE5 8AZ", grp10);
		site10.addConsultant("Robin Murray");
		site10.addConsultant("Til Wykes");
		grp10.addSite(site10);
		Group grp11 = (Group)factory.createGroup("008001");
		grp11.setLongName("West Midlands-Birmingham and Solihull Mental Health Trust");
		Site site11 = new Site("Trust HQ, Birmingham and Solihull", "N0000695", "B1 3RB", grp11);
		grp11.addSite(site11);
		Group grp12 = (Group)factory.createGroup("002003");
		grp12.setLongName("East Anglia-Peterbrough NHS Trust");
		Site site12 = new Site("Kingfisher House", "N0000697", "PE29 6FH", grp12);
		site12.addConsultant("Peter Jones");
		grp12.addSite(site12);
		Group grp13 = (Group)factory.createGroup("006002");
		grp13.setLongName("North West-Bolton, Salford and Trafford Mental Health Trust");
		Site site13 = new Site("Prestwich Hospital", "N0000686", "M25 3BL", grp13);
		site13.addConsultant("Max Marshall");
		site13.addConsultant("Shôn Lewis");
		grp13.addSite(site13);
		Group grp14 = (Group)factory.createGroup("006003");
		grp14.setLongName("North West-Lancashire Care Trust");
		Site site14a = new Site("Blackpool and Morecambe EIS", "N0000681", "BL1 0AA", grp14);
		site14a.addConsultant("Max Marshall");
		site14a.addConsultant("Shôn Lewis");
		Site site14b = new Site("Preston EIS", "N0000679", "PR1 7LY", grp14);
		site14b.addConsultant("Max Marshall");
		site14b.addConsultant("Shôn Lewis");
		Site site14c = new Site("Blackburn EIS", "N0000680", "BB6 7DD", grp14);
		site14c.addConsultant("Max Marshall");
		site14c.addConsultant("Shôn Lewis");
		grp14.addSite(site14a);
		grp14.addSite(site14b);
		grp14.addSite(site14c);

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
		dataSet.addGroup(grp12);
		dataSet.addGroup(grp13);
		dataSet.addGroup(grp14);

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
		acf.setQuestion("Has the client's relative/legal guardian agreed to the client taking part in the study?");
		pcf2.addAssociatedConsentForm(acf);
		dataSet.addAllConsentFormGroup(cfg);
		ConsentFormGroup cfg2 = factory.createConsentFormGroup();
		cfg2.setDescription("Consent to contact relatives");
		PrimaryConsentForm pcf3 = factory.createPrimaryConsentForm();
		pcf3.setQuestion("Has the client agreed to their relative being approached and asked "+
				"to complete questionnaires/short interview about their experiences as a "+
		"relative of someone experiencing mental health problems?");
		cfg2.addConsentForm(pcf3);
		dataSet.addAllConsentFormGroup(cfg2);

		//dataset statuses and their transitions
		Status statReferred = factory.createStatus("Referred", "Referred", 0);
		statReferred.setGenericState(GenericState.REFERRED);
		Status statConsented = factory.createStatus("Consented", "Consented", 1);
		statConsented.setGenericState(GenericState.ACTIVE);
		Status statInterview1 = factory.createStatus("Interview1", "Interview 1 completed", 2);
		statInterview1.setGenericState(GenericState.ACTIVE);
		Status statInterview2 = factory.createStatus("Interview2", "Interview 2 completed", 3);
		statInterview2.setGenericState(GenericState.ACTIVE);
		Status statInterview3 = factory.createStatus("Interview3", "Interview 3 completed", 4);
		statInterview3.setGenericState(GenericState.ACTIVE);
		Status stat6Month = factory.createStatus("6Month", "6 month follow-up completed", 5);
		stat6Month.setGenericState(GenericState.ACTIVE);
		Status stat12Month = factory.createStatus("12Month", "12 month follow-up completed", 6);
		stat12Month.setGenericState(GenericState.ACTIVE);
		Status statDeceased = factory.createStatus("Deceased", "Deceased", 7);
		statDeceased.setInactive(true);
		statDeceased.setGenericState(GenericState.LEFT);
		Status statWithdrew = factory.createStatus("Withdrew", "Withdrew", 8);
		statWithdrew.setInactive(true);
		statWithdrew.setGenericState(GenericState.INACTIVE);
		Status statInvalid = factory.createStatus("Invalid", "Invalid", 9);	//Record was added by mistake and shouldn't exist
		statInvalid.setInactive(true);
		statInvalid.setGenericState(GenericState.INVALID);
		Status statComplete = factory.createStatus("Complete", "Complete", 10);
		statComplete.setGenericState(GenericState.COMPLETED);
		statComplete.setInactive(true);
		Status statLeft = factory.createStatus("Left", "Left Study", 11);
		statLeft.setGenericState(GenericState.LEFT);
		statLeft.setInactive(true);

		statReferred.addStatusTransition(statConsented);            //referred -> consented
		statReferred.addStatusTransition(statDeceased);             //referred -> deceased
		statReferred.addStatusTransition(statWithdrew);             //referred -> withdrew
		statReferred.addStatusTransition(statInvalid);              //referred -> invalid

		statConsented.addStatusTransition(statInterview1);          //consented -> interview 1 completed
		statConsented.addStatusTransition(statInterview2);          //consented -> interview 2 completed
		statConsented.addStatusTransition(statInterview3);          //consented -> interview 3 completed
		statConsented.addStatusTransition(stat6Month);              //consented -> 6 month follow up completed
		statConsented.addStatusTransition(stat12Month);             //consented -> 12 month follow up completed
		statConsented.addStatusTransition(statDeceased);            //consented -> deceased
		statConsented.addStatusTransition(statWithdrew);            //consented -> withdrew
		statConsented.addStatusTransition(statInvalid);             //consented -> invalid
		statConsented.addStatusTransition(statLeft);             //consented -> left study

		statInterview1.addStatusTransition(statInterview2);         //interview 1 completed -> interview 2 completed
		statInterview1.addStatusTransition(statInterview3);         //interview 1 completed -> interview 3 completed
		statInterview1.addStatusTransition(stat6Month);             //interview 1 completed -> 6 month follow up completed
		statInterview1.addStatusTransition(stat12Month);            //interview 1 completed -> 12 month follow up completed
		statInterview1.addStatusTransition(statDeceased);           //interview 1 completed -> deceased
		statInterview1.addStatusTransition(statWithdrew);           //interview 1 completed -> withdrew
		statInterview1.addStatusTransition(statInvalid);            //interview 1 completed -> invalid
		statInterview1.addStatusTransition(statLeft);            //interview 1 completed -> left study

		statInterview2.addStatusTransition(statInterview3);         //interview 2 completed -> interview 3 completed
		statInterview2.addStatusTransition(stat6Month);             //interview 2 completed -> 6 month follow up completed
		statInterview2.addStatusTransition(stat12Month);            //interview 2 completed -> 12 month follow up completed
		statInterview2.addStatusTransition(statDeceased);           //interview 2 completed -> deceased
		statInterview2.addStatusTransition(statWithdrew);           //interview 2 completed -> withdrew
		statInterview2.addStatusTransition(statInvalid);            //interview 2 completed -> invalid
		statInterview2.addStatusTransition(statLeft);            //interview 2 completed -> left study

		statInterview3.addStatusTransition(stat6Month);             //interview 3 completed -> 6 month follow up completed
		statInterview3.addStatusTransition(stat12Month);            //interview 3 completed -> 12 month follow up completed
		statInterview3.addStatusTransition(statDeceased);           //interview 3 completed -> deceased
		statInterview3.addStatusTransition(statWithdrew);           //interview 3 completed -> withdrew
		statInterview3.addStatusTransition(statInvalid);            //interview 3 completed -> invalid
		statInterview3.addStatusTransition(statLeft);            //interview 3 completed -> left study

		stat6Month.addStatusTransition(stat12Month);                //6 month follow-up completed -> 12 month follow up completed
		stat6Month.addStatusTransition(statDeceased);               //6 month follow-up completed -> deceased
		stat6Month.addStatusTransition(statWithdrew);               //6 month follow-up completed -> withdrew
		stat6Month.addStatusTransition(statInvalid);                //6 month follow-up completed -> invalid
		stat6Month.addStatusTransition(statLeft);                //6 month follow-up completed -> left study

		stat12Month.addStatusTransition(statComplete);				//12 month follow-up completed -> complete
		stat12Month.addStatusTransition(statDeceased);              //12 month follow-up completed -> deceased
		stat12Month.addStatusTransition(statWithdrew);              //12 month follow-up completed -> withdrew
		stat12Month.addStatusTransition(statInvalid);               //12 month follow-up completed -> invalid

		statLeft.addStatusTransition(statWithdrew);					//Left Study -> Withdrew
		statLeft.addStatusTransition(statDeceased);					//Left Study -> Deceased
		statLeft.addStatusTransition(statInvalid);					//Left Study -> Invalid

		dataSet.addStatus(statReferred);
		dataSet.addStatus(statConsented);
		dataSet.addStatus(statInterview1);
		dataSet.addStatus(statInterview2);
		dataSet.addStatus(statInterview3);
		dataSet.addStatus(stat6Month);
		dataSet.addStatus(stat12Month);
		dataSet.addStatus(statDeceased);
		dataSet.addStatus(statWithdrew);
		dataSet.addStatus(statInvalid);
		dataSet.addStatus(statComplete);
		dataSet.addStatus(statLeft);

		//document groups
		DocumentGroup baselineA = factory.createDocumentGroup("Baseline Sec A Group");
		baselineA.setDisplayText("Baseline - Section A (Core assessments)");
		baselineA.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		baselineA.setUpdateStatus(statInterview1);	//interview 1 completed

		DocumentGroup baselineB = factory.createDocumentGroup("Baseline Sec B Group");
		baselineB.setDisplayText("Baseline - Section B");
		//any state from completion of baselineA (inteview1), inc 12Month completed, can view documents in this group
		baselineB.addAllowedRecordStatus(statConsented);
		baselineB.addAllowedRecordStatus(statInterview1);
		baselineB.addAllowedRecordStatus(statInterview2);
		baselineB.addAllowedRecordStatus(statInterview3);
		baselineB.addAllowedRecordStatus(stat6Month);
		baselineB.addAllowedRecordStatus(stat12Month);
		baselineB.setUpdateStatus(statInterview2);

		DocumentGroup baselineC = factory.createDocumentGroup("Baseline Sec C Group");
		baselineC.setDisplayText("Baseline - Section C");
		baselineC.addAllowedRecordStatus(statConsented);
		baselineC.addAllowedRecordStatus(statInterview1);
		baselineC.addAllowedRecordStatus(statInterview2);
		baselineC.addAllowedRecordStatus(statInterview3);
		baselineC.addAllowedRecordStatus(stat6Month);
		baselineC.addAllowedRecordStatus(stat12Month);
		baselineC.setUpdateStatus(statInterview3);

		DocumentGroup sixMonths = factory.createDocumentGroup("6 months Group");
		sixMonths.setDisplayText("6 months");
		sixMonths.addAllowedRecordStatus(statInterview3);
		sixMonths.addAllowedRecordStatus(stat6Month);
		sixMonths.addAllowedRecordStatus(stat12Month);
		sixMonths.addPrerequisiteGroup(baselineA);
		sixMonths.addPrerequisiteGroup(baselineB);
		sixMonths.addPrerequisiteGroup(baselineC);
		sixMonths.setUpdateStatus(stat6Month); //auto record update

		DocumentGroup twelveMonths = factory.createDocumentGroup("12 months");
		twelveMonths.setDisplayText("12 months");
		twelveMonths.addAllowedRecordStatus(stat6Month);
		twelveMonths.addAllowedRecordStatus(stat12Month);
		twelveMonths.addPrerequisiteGroup(sixMonths);
		twelveMonths.setUpdateStatus(stat12Month);

		DocumentGroup studyTermination = factory.createDocumentGroup("Study termination");
		studyTermination.setDisplayText("Study termination");
		studyTermination.setAllowedRecordStatus(getStatuses(dataSet, GenericState.INACTIVE));
		studyTermination.addAllowedRecordStatus(statDeceased);

		DocumentGroup shared = factory.createDocumentGroup("Shared");
		shared.setDisplayText("Shared");
		shared.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));

		dataSet.addDocumentGroup(baselineA);
		dataSet.addDocumentGroup(baselineB);
		dataSet.addDocumentGroup(baselineC);
		dataSet.addDocumentGroup(sixMonths);
		dataSet.addDocumentGroup(twelveMonths);
		dataSet.addDocumentGroup(studyTermination);
		dataSet.addDocumentGroup(shared);

		//NOTE this document is now locked and superseded by the V2 treatment documentation
		//document below
		Document treatmentDocumentation = TreatmentDocumentation.createDocument(factory);
		dataSet.addDocument(treatmentDocumentation);
		treatmentDocumentation.addConsentFormGroup(cfg);
		treatmentDocumentation.setPrimaryDocIndex(22L);
		DocumentOccurrence tdb = factory.createDocumentOccurrence("Shared");
		tdb.setDisplayText("Shared");
		tdb.setPrimaryOccIndex(0L);
		tdb.setLocked(true);
		treatmentDocumentation.addOccurrence(tdb);
		tdb.setDocumentGroup(shared);

		Document fileNoteLog = FileNoteLog.createDocument(factory);
		dataSet.addDocument(fileNoteLog);
		fileNoteLog.addConsentFormGroup(cfg);
		DocumentOccurrence fileNoteLogBaseline = factory.createDocumentOccurrence("Shared");
		fileNoteLogBaseline.setDisplayText("Shared");
		fileNoteLogBaseline.setDocumentGroup(shared);
		fileNoteLog.addOccurrence(fileNoteLogBaseline);


		//-------------------------------------------------------------------
		//Documents/occurrences in the Baseline A (Core assessments) group
		//-------------------------------------------------------------------

		//Baseline Audit form
		Document audit = BaselineAudit.createDocument(factory);
		dataSet.addDocument(audit);
		audit.addConsentFormGroup(cfg);
		DocumentOccurrence auditBaseline =
			factory.createDocumentOccurrence("Baseline");
		auditBaseline.setDisplayText("Baseline");
		audit.addOccurrence(auditBaseline);
		auditBaseline.setDocumentGroup(baselineA);

		Document whoScreening = WHOScreeningSchedule.createDocument(factory);
		dataSet.addDocument(whoScreening);
		whoScreening.addConsentFormGroup(cfg);
		DocumentOccurrence whoBaseline =
			factory.createDocumentOccurrence("Baseline");
		whoBaseline.setDisplayText("Baseline");
		whoScreening.addOccurrence(whoBaseline);
		whoBaseline.setDocumentGroup(baselineA);

		Document personalDetails =
			PersonalDetails.createDocument(factory);
		dataSet.addDocument(personalDetails);
		personalDetails.addConsentFormGroup(cfg);
		DocumentOccurrence personalDetailsBaseline =
			factory.createDocumentOccurrence("Baseline");
		personalDetailsBaseline.setLabel("1.a");
		personalDetailsBaseline.setDisplayText("Baseline");
		personalDetailsBaseline.setDocumentGroup(baselineA);
		personalDetails.addOccurrence(personalDetailsBaseline);

		Document personalDetailsWithConsent =
			PersonalDetailsWithConsent.createDocument(factory);
		dataSet.addDocument(personalDetailsWithConsent);
		personalDetailsWithConsent.addConsentFormGroup(cfg);
		DocumentOccurrence personalDetailsWithConsentBaseline =
			factory.createDocumentOccurrence("Baseline");
		personalDetailsWithConsentBaseline.setDisplayText("Baseline");
		personalDetailsWithConsentBaseline.setDocumentGroup(baselineA);
		personalDetailsWithConsentBaseline.setLabel("1.b");
		personalDetailsWithConsent.addOccurrence(personalDetailsWithConsentBaseline);

		Document panss = Panss.createDocument(factory);
		dataSet.addDocument(panss);
		panss.addConsentFormGroup(cfg);
		panss.setPrimaryDocIndex(1L);
		DocumentOccurrence panssBaseline = factory.createDocumentOccurrence("Baseline");
		panssBaseline.setDisplayText("Baseline");
		panssBaseline.setDocumentGroup(baselineA);
		panssBaseline.setLabel("2");
		panssBaseline.setPrimaryOccIndex(0L);
		panss.addOccurrence(panssBaseline);

		Document youngMania = YoungMania.createDocument(factory);
		dataSet.addDocument(youngMania);
		youngMania.addConsentFormGroup(cfg);
		youngMania.setPrimaryDocIndex(2L);
		DocumentOccurrence ymBaseline = factory.createDocumentOccurrence("Baseline");
		ymBaseline.setLabel("3");
		ymBaseline.setDisplayText("Baseline");
		ymBaseline.setDocumentGroup(baselineA);
		ymBaseline.setPrimaryOccIndex(0L);
		youngMania.addOccurrence(ymBaseline);

		Document gaf = GlobalAssessmentFunctioning.createDocument(factory);
		dataSet.addDocument(gaf);
		gaf.addConsentFormGroup(cfg);
		gaf.setPrimaryDocIndex(7L);
		DocumentOccurrence gafBaseline = factory.createDocumentOccurrence("Baseline");
		gafBaseline.setDisplayText("Baseline");
		gafBaseline.setDocumentGroup(baselineA);
		gafBaseline.setLabel("4");
		gafBaseline.setPrimaryOccIndex(0L);
		gaf.addOccurrence(gafBaseline);

		Document dup = DUP.createDocument(factory);
		dataSet.addDocument(dup);
		dup.addConsentFormGroup(cfg);
		dup.setPrimaryDocIndex(9L);
		DocumentOccurrence dupBaseline = factory.createDocumentOccurrence("Baseline");
		dupBaseline.setDisplayText("Baseline");
		dupBaseline.setDocumentGroup(baselineA);
		dupBaseline.setLabel("5");
		dupBaseline.setPrimaryOccIndex(0L);
		dup.addOccurrence(dupBaseline);

		Document drugCheck = DrugCheck.createDocument(factory);
		dataSet.addDocument(drugCheck);
		drugCheck.addConsentFormGroup(cfg);
		drugCheck.setPrimaryDocIndex(6L);
		DocumentOccurrence drugCheckBaseline = factory.createDocumentOccurrence("Baseline");
		drugCheckBaseline.setDisplayText("Baseline");
		drugCheckBaseline.setDocumentGroup(baselineA);
		drugCheckBaseline.setLabel("6");
		drugCheckBaseline.setPrimaryOccIndex(0L);
		drugCheck.addOccurrence(drugCheckBaseline);

		//-------------------------------------------------------------------
		//Documents/occurrences in the Baseline B group
		//-------------------------------------------------------------------
		Document pathwaysToCare = PathwaysToCare.createDocument(factory);
		dataSet.addDocument(pathwaysToCare);
		pathwaysToCare.addConsentFormGroup(cfg);
		pathwaysToCare.setPrimaryDocIndex(10L);
		DocumentOccurrence pathwaysBaseline = factory.createDocumentOccurrence("Baseline");
		pathwaysBaseline.setDisplayText("Baseline");
		pathwaysBaseline.setDocumentGroup(baselineB);
		pathwaysBaseline.setPrimaryOccIndex(0L);
		pathwaysToCare.addOccurrence(pathwaysBaseline);

		Document premorbidAdjScale = PreMorbidAdjustmentScale.createDocument(factory);
		dataSet.addDocument(premorbidAdjScale);
		premorbidAdjScale.addConsentFormGroup(cfg);
		premorbidAdjScale.setPrimaryDocIndex(11L);
		DocumentOccurrence premorbidBaseline = factory.createDocumentOccurrence("Baseline");
		premorbidBaseline.setDisplayText("Baseline");
		premorbidBaseline.setDocumentGroup(baselineB);
		premorbidBaseline.setPrimaryOccIndex(0L);
		premorbidAdjScale.addOccurrence(premorbidBaseline);

		Document calgary = Calgary.createDocument(factory);
		dataSet.addDocument(calgary);
		calgary.addConsentFormGroup(cfg);
		calgary.setPrimaryDocIndex(4L);
		DocumentOccurrence calgBaseline = factory.createDocumentOccurrence("Baseline");
		calgBaseline.setDisplayText("Baseline");
		calgBaseline.setDocumentGroup(baselineB);
		calgBaseline.setPrimaryOccIndex(0L);
		calgary.addOccurrence(calgBaseline);

		Document eis = EisFamilyHistory.createDocument(factory);
		dataSet.addDocument(eis);
		eis.addConsentFormGroup(cfg);
		eis.setPrimaryDocIndex(5L);
		DocumentOccurrence eisBaseline = factory.createDocumentOccurrence("Baseline");
		eisBaseline.setDisplayText("Baseline");
		eisBaseline.setDocumentGroup(baselineB);
		eisBaseline.setPrimaryOccIndex(0L);
		eis.addOccurrence(eisBaseline);

		Document eq5d = EQ5D.createDocument(factory);
		dataSet.addDocument(eq5d);
		eq5d.addConsentFormGroup(cfg);
		DocumentOccurrence eq5dBaseline = factory.createDocumentOccurrence("Baseline");
		eq5dBaseline.setDisplayText("Baseline");
		eq5dBaseline.setDocumentGroup(baselineB);
		eq5d.addOccurrence(eq5dBaseline);

		Document drugSideEffects  = DrugSideEffects.createDocument(factory);
		dataSet.addDocument(drugSideEffects);
		drugSideEffects.addConsentFormGroup(cfg);
		DocumentOccurrence dseBaseline = factory.createDocumentOccurrence("Baseline");
		dseBaseline.setDisplayText("Baseline");
		dseBaseline.setDocumentGroup(baselineB);
		dseBaseline.setLabel("12");
		drugSideEffects.addOccurrence(dseBaseline);

		Document drugSideEffectsAnnsers  = DrugSideEffectsAnnsers.createDocument(factory);
		dataSet.addDocument(drugSideEffectsAnnsers);
		drugSideEffectsAnnsers.addConsentFormGroup(cfg);
		DocumentOccurrence dseAnnsersBaseline = factory.createDocumentOccurrence("Baseline");
		dseAnnsersBaseline.setDisplayText("Baseline");
		dseAnnsersBaseline.setDocumentGroup(baselineB);
		drugSideEffectsAnnsers.addOccurrence(dseAnnsersBaseline);


		//-------------------------------------------------------------------
		//Documents/occurrences in the Baseline C group
		//-------------------------------------------------------------------
		Document insight = InsightScale.createDocument(factory);
		dataSet.addDocument(insight);
		insight.addConsentFormGroup(cfg);
		insight.setPrimaryDocIndex(3L);
		DocumentOccurrence insightBaseline = factory.createDocumentOccurrence("Baseline");
		insightBaseline.setDisplayText("Baseline");
		insightBaseline.setDocumentGroup(baselineC);
		insightBaseline.setPrimaryOccIndex(0L);
		insight.addOccurrence(insightBaseline);

		Document adverseOutcomesSelfHarm = AdverseOutcomesClientSelfHarm.createDocument(factory);
		dataSet.addDocument(adverseOutcomesSelfHarm);
		adverseOutcomesSelfHarm.addConsentFormGroup(cfg);
		DocumentOccurrence addOutSelfHarmSeriousBaseline = factory.createDocumentOccurrence("Most serious, Baseline");
		addOutSelfHarmSeriousBaseline.setDisplayText("Most serious, Baseline");
		addOutSelfHarmSeriousBaseline.setDocumentGroup(baselineC);
		adverseOutcomesSelfHarm.addOccurrence(addOutSelfHarmSeriousBaseline);

		DocumentOccurrence addOutSelfHarmContactBaseline = factory.createDocumentOccurrence("Closest to contact, Baseline");
		addOutSelfHarmContactBaseline.setDisplayText("Closest to contact, Baseline");
		addOutSelfHarmContactBaseline.setDocumentGroup(baselineC);
		adverseOutcomesSelfHarm.addOccurrence(addOutSelfHarmContactBaseline);

		Document adverseOutcomesViolence = AdverseOutcomesClientViolent.createDocument(factory);
		dataSet.addDocument(adverseOutcomesViolence);
		adverseOutcomesViolence.addConsentFormGroup(cfg);
		DocumentOccurrence addOutViolenceSeriousBaseline = factory.createDocumentOccurrence("Most serious, Baseline");
		addOutViolenceSeriousBaseline.setDisplayText("Most serious, Baseline");
		addOutViolenceSeriousBaseline.setDocumentGroup(baselineC);
		adverseOutcomesViolence.addOccurrence(addOutViolenceSeriousBaseline);

		DocumentOccurrence addOutViolenceContactBaseline = factory.createDocumentOccurrence("Closest to contact, Baseline");
		addOutViolenceContactBaseline.setDisplayText("Closest to contact, Baseline");
		addOutViolenceContactBaseline.setDocumentGroup(baselineC);
		adverseOutcomesViolence.addOccurrence(addOutViolenceContactBaseline);

		Document adverseOutcomesCarer = AdverseOutcomesCarer.createDocument(factory);
		dataSet.addDocument(adverseOutcomesCarer);
		adverseOutcomesCarer.addConsentFormGroup(cfg);
		DocumentOccurrence addOutCarerClosestBaseline = factory.createDocumentOccurrence("Closest to Contact, Baseline");
		addOutCarerClosestBaseline.setDisplayText("Closest to Contact, Baseline");
		addOutCarerClosestBaseline.setDocumentGroup(baselineC);
		adverseOutcomesCarer.addOccurrence(addOutCarerClosestBaseline);

		DocumentOccurrence addOutCarerMostSeriousBaseline = factory.createDocumentOccurrence("Most Serious, Baseline");
		addOutCarerMostSeriousBaseline.setDisplayText("Most Serious, Baseline");
		addOutCarerMostSeriousBaseline.setDocumentGroup(baselineC);
		adverseOutcomesCarer.addOccurrence(addOutCarerMostSeriousBaseline);

		Document cssri = CSSRI.createDocument(factory);
		dataSet.addDocument(cssri);
		cssri.addConsentFormGroup(cfg);
		DocumentOccurrence cssriBaseline = factory.createDocumentOccurrence("Baseline");
		cssriBaseline.setDisplayText("Baseline");
		cssriBaseline.setDocumentGroup(baselineC);
		cssriBaseline.setLocked(true);
		cssri.addOccurrence(cssriBaseline);

		Document timeUse = TimeUseScoreSheet.createDocument(factory);
		dataSet.addDocument(timeUse);
		timeUse.addConsentFormGroup(cfg);
		timeUse.setPrimaryDocIndex(18L);
		DocumentOccurrence timeUseBaseline =
			factory.createDocumentOccurrence("Baseline");
		timeUseBaseline.setDisplayText("Baseline");
		timeUseBaseline.setDocumentGroup(baselineC);
		timeUseBaseline.setLocked(true);
		timeUseBaseline.setPrimaryOccIndex(0L);
		timeUse.addOccurrence(timeUseBaseline);

		Document complianceScale = ComplianceScaleRecord.createDocument(factory);
		dataSet.addDocument(complianceScale);
		complianceScale.addConsentFormGroup(cfg);
		DocumentOccurrence complScaleBase =
			factory.createDocumentOccurrence("Baseline");
		complScaleBase.setDisplayText("Baseline");
		complScaleBase.setDocumentGroup(baselineC);
		complianceScale.addOccurrence(complScaleBase);


		//-------------------------------------------------------------------
		//Documents/occurrences in the 6 Month follow up  group
		//-------------------------------------------------------------------
		Document personalDetailsFollowUp = PersonalDetailsFollowUp.createDocument(factory);
		dataSet.addDocument(personalDetailsFollowUp);
		personalDetailsFollowUp.addConsentFormGroup(cfg);
		DocumentOccurrence pdfuSixMonths =
			factory.createDocumentOccurrence("6 Months");
		pdfuSixMonths.setDisplayText("6 Months");
		pdfuSixMonths.setDocumentGroup(sixMonths);
		createSixMonthReminders(pdfuSixMonths, factory);
		personalDetailsFollowUp.addOccurrence(pdfuSixMonths);

		DocumentOccurrence panssSixMonths = factory.createDocumentOccurrence("6 Months");
		panssSixMonths.setDocumentGroup(sixMonths);
		panssSixMonths.setDisplayText("6 Months");
		panssSixMonths.setPrimaryOccIndex(1L);
		createSixMonthReminders(panssSixMonths, factory);
		panss.addOccurrence(panssSixMonths);

		DocumentOccurrence ymSixMonths = factory.createDocumentOccurrence("6 Months");
		ymSixMonths.setDocumentGroup(sixMonths);
		ymSixMonths.setDisplayText("6 Months");
		ymSixMonths.setPrimaryOccIndex(1L);
		createSixMonthReminders(ymSixMonths, factory);
		youngMania.addOccurrence(ymSixMonths);

		DocumentOccurrence calgSixMonths = factory.createDocumentOccurrence("6 Months");
		calgSixMonths.setDocumentGroup(sixMonths);
		calgSixMonths.setDisplayText("6 Months");
		calgSixMonths.setPrimaryOccIndex(1L);
		createSixMonthReminders(calgSixMonths, factory);
		calgary.addOccurrence(calgSixMonths);

		Document adverseOutcomesClient = AdverseOutcomesClient.createDocument(factory);
		dataSet.addDocument(adverseOutcomesClient);
		adverseOutcomesClient.addConsentFormGroup(cfg);
		DocumentOccurrence addOutClientBaseline = factory.createDocumentOccurrence("6 Months");
		addOutClientBaseline.setDisplayText("6 Months");
		addOutClientBaseline.setDocumentGroup(sixMonths);
		createSixMonthReminders(addOutClientBaseline, factory);
		adverseOutcomesClient.addOccurrence(addOutClientBaseline);

		DocumentOccurrence timeUseSixMonths =
			factory.createDocumentOccurrence("6 Months");
		timeUseSixMonths.setDisplayText("6 Months");
		timeUseSixMonths.setDocumentGroup(sixMonths);
		timeUseSixMonths.setPrimaryOccIndex(1L);
		createSixMonthReminders(timeUseSixMonths, factory);
		timeUse.addOccurrence(timeUseSixMonths);

		DocumentOccurrence complScaleSixMonths =
			factory.createDocumentOccurrence("6 months");
		complScaleSixMonths.setDisplayText("6 Months");
		complScaleSixMonths.setDocumentGroup(sixMonths);
		createSixMonthReminders(complScaleSixMonths, factory);
		complianceScale.addOccurrence(complScaleSixMonths);

		DocumentOccurrence dseSixMonths= factory.createDocumentOccurrence("6 months");
		dseSixMonths.setDisplayText("6 Months");
		dseSixMonths.setDocumentGroup(sixMonths);
		createSixMonthReminders(dseSixMonths, factory);
		drugSideEffects.addOccurrence(dseSixMonths);

		DocumentOccurrence dseAnnsersSixMonths = factory.createDocumentOccurrence("6 months");
		dseAnnsersSixMonths.setDisplayText("6 Months");
		dseAnnsersSixMonths.setDocumentGroup(sixMonths);
		createSixMonthReminders(dseAnnsersSixMonths, factory);
		drugSideEffectsAnnsers.addOccurrence(dseAnnsersSixMonths);

		//-------------------------------------------------------------------
		//Documents/occurrences in the 12 Month follow up  group
		//-------------------------------------------------------------------
		DocumentOccurrence panssTwelveMonths = factory.createDocumentOccurrence("12 Months");
		panssTwelveMonths.setDisplayText("12 Months");
		panssTwelveMonths.setDocumentGroup(twelveMonths);
		panssTwelveMonths.setPrimaryOccIndex(2L);
		createTwelveMonthReminders(panssTwelveMonths, factory);
		panss.addOccurrence(panssTwelveMonths);

		DocumentOccurrence ymTwelveMonths = factory.createDocumentOccurrence("12 Months");
		ymTwelveMonths.setDisplayText("12 Months");
		ymTwelveMonths.setDocumentGroup(twelveMonths);
		ymTwelveMonths.setPrimaryOccIndex(2L);
		createTwelveMonthReminders(ymTwelveMonths, factory);
		youngMania.addOccurrence(ymTwelveMonths);

		DocumentOccurrence calgTwelveMonths = factory.createDocumentOccurrence("12 Months");
		calgTwelveMonths.setDisplayText("12 Months");
		calgTwelveMonths.setDocumentGroup(twelveMonths);
		calgTwelveMonths.setPrimaryOccIndex(2L);
		createTwelveMonthReminders(calgTwelveMonths, factory);
		calgary.addOccurrence(calgTwelveMonths);

		DocumentOccurrence gafTwelveMonths = factory.createDocumentOccurrence("12 Months");
		gafTwelveMonths.setDisplayText("12 Months");
		gafTwelveMonths.setDocumentGroup(twelveMonths);
		gafTwelveMonths.setPrimaryOccIndex(1L);
		createTwelveMonthReminders(gafTwelveMonths, factory);
		gaf.addOccurrence(gafTwelveMonths);

		DocumentOccurrence drugCheckTwelveMonths = factory.createDocumentOccurrence("12 Months");
		drugCheckTwelveMonths.setDisplayText("12 Months");
		drugCheckTwelveMonths.setDocumentGroup(twelveMonths);
		drugCheckTwelveMonths.setPrimaryOccIndex(1L);
		createTwelveMonthReminders(drugCheckTwelveMonths, factory);
		drugCheck.addOccurrence(drugCheckTwelveMonths);

		DocumentOccurrence eq5dTwelveMonths = factory.createDocumentOccurrence("12 Months");
		eq5dTwelveMonths.setDisplayText("12 Months");
		eq5dTwelveMonths.setDocumentGroup(twelveMonths);
		createTwelveMonthReminders(eq5dTwelveMonths, factory);
		eq5d.addOccurrence(eq5dTwelveMonths);

		DocumentOccurrence insightTwelveMonths = factory.createDocumentOccurrence("12 Months");
		insightTwelveMonths.setDisplayText("12 Months");
		insightTwelveMonths.setDocumentGroup(twelveMonths);
		insightTwelveMonths.setPrimaryOccIndex(1L);
		createTwelveMonthReminders(insightTwelveMonths, factory);
		insight.addOccurrence(insightTwelveMonths);

		DocumentOccurrence addOutClientTwelve = factory.createDocumentOccurrence("12 Months");
		addOutClientTwelve.setDisplayText("12 Months");
		addOutClientTwelve.setDocumentGroup(twelveMonths);
		createTwelveMonthReminders(addOutClientTwelve, factory);
		adverseOutcomesClient.addOccurrence(addOutClientTwelve);

		Document cssriFollowUp = CSSRI_Followup.createDocument(factory);
		dataSet.addDocument(cssriFollowUp);
		cssriFollowUp.addConsentFormGroup(cfg);
		cssriFollowUp.setPrimaryDocIndex(20L);
		//Note that the occurrence indices for CSSRI Follow Up are transposed between NED and OLK:
		//6 months is index 0 in NED and 1 in OLK; 12 months is index 1 in NED and 0 in OLK
		DocumentOccurrence cssriTwelveMonths = factory.createDocumentOccurrence("12 Months");
		cssriTwelveMonths.setDisplayText("12 Months");
		cssriTwelveMonths.setDocumentGroup(twelveMonths);
		cssriTwelveMonths.setLocked(true);
		cssriTwelveMonths.setPrimaryOccIndex(1L);
		createTwelveMonthReminders(cssriTwelveMonths, factory);
		cssriFollowUp.addOccurrence(cssriTwelveMonths);

		DocumentOccurrence complScaleTwelveMonths =
			factory.createDocumentOccurrence("12 months");
		complScaleTwelveMonths.setDisplayText("12 Months");
		complScaleTwelveMonths.setDocumentGroup(twelveMonths);
		createTwelveMonthReminders(complScaleTwelveMonths, factory);
		complianceScale.addOccurrence(complScaleTwelveMonths);

		DocumentOccurrence dseTwelveMonths= factory.createDocumentOccurrence("12 months");
		dseTwelveMonths.setDisplayText("12 Months");
		dseTwelveMonths.setDocumentGroup(twelveMonths);
		createTwelveMonthReminders(dseTwelveMonths, factory);
		drugSideEffects.addOccurrence(dseTwelveMonths);


		DocumentOccurrence dseAnnsersTwelveMonths = factory.createDocumentOccurrence("12 months");
		dseAnnsersTwelveMonths.setDisplayText("12 Months");
		dseAnnsersTwelveMonths.setDocumentGroup(twelveMonths);
		createTwelveMonthReminders(dseAnnsersTwelveMonths, factory);
		drugSideEffectsAnnsers.addOccurrence(dseAnnsersTwelveMonths);


		//-------------------------------------------------------------------
		//Documents/occurrences in the study termination group
		//-------------------------------------------------------------------
		Document studyTermRecord = StudyTerminationRecord.createDocument(factory);
		dataSet.addDocument(studyTermRecord);
		studyTermRecord.addConsentFormGroup(cfg);
		DocumentOccurrence studyTermBase =
			factory.createDocumentOccurrence("Baseline");
		studyTermBase.setDocumentGroup(studyTermination);
		studyTermRecord.addOccurrence(studyTermBase);

		/**
		 *
		 * Documents patched in after deployment
		 *
		 */

		//Note that the occurrence indices for CSSRI Follow Up are transposed between NED and OLK:
		//6 months is index 0 in NED and 1 in OLK; 12 months is index 1 in NED and 0 in OLK
		DocumentOccurrence cssriSixMonths = factory.createDocumentOccurrence("6 Months");
		cssriSixMonths.setDisplayText("6 Months");
		cssriSixMonths.setDocumentGroup(sixMonths);
		cssriSixMonths.setLocked(true);
		cssriSixMonths.setPrimaryOccIndex(0L);
		createSixMonthReminders(cssriSixMonths, factory);
		cssriFollowUp.addOccurrence(cssriSixMonths);

		DocumentOccurrence timeUseTwelveMonths = factory.createDocumentOccurrence("12 Months");
		timeUseTwelveMonths.setDisplayText("12 Months");
		timeUseTwelveMonths.setDocumentGroup(twelveMonths);
		timeUseTwelveMonths.setPrimaryOccIndex(2L);
		createSixMonthReminders(timeUseTwelveMonths, factory);
		timeUse.addOccurrence(timeUseTwelveMonths);

		Document relapseRating = RelapseRating.createDocument(factory);
		dataSet.addDocument(relapseRating);
		relapseRating.addConsentFormGroup(cfg);
		DocumentOccurrence rrb = factory.createDocumentOccurrence("Shared");
		rrb.setDisplayText("Shared");
		relapseRating.addOccurrence(rrb);
		rrb.setDocumentGroup(shared);

		Document opcrit = Opcrit.createDocument(factory);
		dataSet.addDocument(opcrit);
		opcrit.addConsentFormGroup(cfg);
		opcrit.setPrimaryDocIndex(24L);
		DocumentOccurrence opcritOcc = factory.createDocumentOccurrence("12 Months");
		opcritOcc.setDisplayText("12 Months");
		opcritOcc.setPrimaryOccIndex(0L);
		opcrit.addOccurrence(opcritOcc);
		opcritOcc.setDocumentGroup(twelveMonths);

		Document treatDoc2 = TreatmentDocumentationV2.createDocument(factory);
		dataSet.addDocument(treatDoc2);
		treatDoc2.addConsentFormGroup(cfg);
		treatDoc2.setSecondaryDocIndex(25L);
		DocumentOccurrence tdbv2 = factory.createDocumentOccurrence("Shared");
		tdbv2.setDisplayText("Shared");
		tdbv2.setSecondaryOccIndex(0L);
		treatDoc2.addOccurrence(tdbv2);
		tdbv2.setDocumentGroup(shared);

		/*
		 * Add an RBACAction to each document
		 */
		addDocumentActions(dataSet);

		return dataSet;
	}

	private static void createSixMonthReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(182));
		occurrence.setScheduleUnits(TimeUnits.DAYS);
		Reminder rem1 = factory.createReminder(175, TimeUnits.DAYS, ReminderLevel.MILD);
		Reminder rem2 = factory.createReminder(192, TimeUnits.DAYS, ReminderLevel.NORMAL);
		Reminder rem3 = factory.createReminder(202, TimeUnits.DAYS, ReminderLevel.SEVERE);
		occurrence.addReminder(rem1);
		occurrence.addReminder(rem2);
		occurrence.addReminder(rem3);
	}

	private static void createTwelveMonthReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(365));
		occurrence.setScheduleUnits(TimeUnits.DAYS);
		Reminder rem1 = factory.createReminder(358, TimeUnits.DAYS, ReminderLevel.MILD);
		Reminder rem2 = factory.createReminder(375, TimeUnits.DAYS, ReminderLevel.NORMAL);
		Reminder rem3 = factory.createReminder(385, TimeUnits.DAYS, ReminderLevel.SEVERE);
		occurrence.addReminder(rem1);
		occurrence.addReminder(rem2);
		occurrence.addReminder(rem3);
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
		IReport drugSideEffectsReportBaseline = Reports.drugSideEffectsReportBaseline(ds);
		IReport drugSideEffectsReport6Months = Reports.drugSideEffectsReport6Months(ds);
		IReport drugSideEffectsReport12Months = Reports.drugSideEffectsReport12Months(ds);
		IReport premorbidAdjustmentReport = Reports.premorbidAdjustmentReport(ds);

		//Trend reports
		IReport dupTrendsReportBaseline  = Reports.dupTrendsReport(ds);
		IReport dupHighLowTrendsReportBaseline  = Reports.dupHighLowTrendsReport(ds);
		IReport panssTrendsReportBaseline = Reports.panssTrendsReportBaseline(ds);
		IReport panssTrendsReport6Months  = Reports.panssTrendsReport6Months(ds);
		IReport panssTrendsReport12Months = Reports.panssTrendsReport12Months(ds);
		IReport pathwaysTrendsReportBaseline  = Reports.pathwaysTrendsReport(ds);
		IReport premorbidTrendsReportBaseline = Reports.premorbidTrendsReport(ds);
		IReport gafTrendsReportBaseline  = Reports.gafTrendsReportBaseline(ds);
		IReport gafTrendsReport12Months  = Reports.gafTrendsReport12Months(ds);
		IReport eq5dTrendsReportBaseline  = Reports.eq5dTrendsReportBaseline(ds);
		IReport eq5dTrendsReport12Months  = Reports.eq5dTrendsReport12Months(ds);
		IReport calgaryScaleTrendsReportBaseline  = Reports.calgaryScaleTrendsReportBaseline(ds);
		IReport calgaryScaleTrendsReport6Months   = Reports.calgaryScaleTrendsReport6Months(ds);
		IReport calgaryScaleTrendsReport12Months  = Reports.calgaryScaleTrendsReport12Months(ds);
		IReport youngManiaTrendsReportBaseline  = Reports.youngManiaTrendsReportBaseline(ds);
		IReport youngManiaTrendsReport6Months   = Reports.youngManiaTrendsReport6Months(ds);
		IReport youngManiaTrendsReport12Months  = Reports.youngManiaTrendsReport12Months(ds);
		IReport insightScaleTrendsReportBaseline  = Reports.insightScaleTrendsReportBaseline(ds);
		IReport insightScaleTrendsReport12Months  = Reports.insightScaleTrendsReport12Months(ds);

		//Management reports
		IReport cpmReport = Reports.cpmMgmtReport(ds);
		IReport ciReport = Reports.ciMgmtReport(ds);
		IReport piBaReport = Reports.piBristolAvonMgmtReport(ds);
		IReport piEaReport = Reports.piEastAngliaMgmtReport(ds);
		IReport piEmReport = Reports.piEastMidlandsMgmtReport(ds);
		IReport piNeReport = Reports.piNorthEastMgmtReport(ds);
		IReport piNlReport = Reports.piNorthLondonMgmtReport(ds);
		IReport piNwReport = Reports.piNorthWestMgmtReport(ds);
		IReport piSlReport = Reports.piSouthLondonMgmtReport(ds);
		IReport piWmReport = Reports.piWestMidlandsMgmtReport(ds);
		IReport ciRecruitmentReport = Reports.ciRecruitmentReport(ds);
		IReport cpmRecruitmentReport  = Reports.cpmRecruitmentReport(ds);
		IReport piBaRecruitmentReport = Reports.recruitmentInBristolAvonReport(ds);
		IReport piEaRecruitmentReport = Reports.recruitmentInEastAngliaReport(ds);
		IReport piEmRecruitmentReport = Reports.recruitmentInEastMidlandsReport(ds);
		IReport piNeRecruitmentReport = Reports.recruitmentInNorthEastReport(ds);
		IReport piNlRecruitmentReport = Reports.recruitmentInNorthLondonReport(ds);
		IReport piNwRecruitmentReport = Reports.recruitmentInNorthWestReport(ds);
		IReport piSlRecruitmentReport = Reports.recruitmentInSouthLondonReport(ds);
		IReport piWmRecruitmentReport = Reports.recruitmentInWestMidlandsReport(ds);
		IReport ukCRNReport	= Reports.ukCRNReport(ds);

		IReport recordStatusReport = Reports.recordStatusReport(ds, null, "");
		IReport documentStatusReport = Reports.documentStatusReport(ds, null, "");
		IReport collectionDateReport = Reports.collectionDateReport(ds, null, "");
		IReport stdCodeStatusReport = Reports.stdCodeStatusReport(ds);
		IReport basicStatReport = Reports.basicStatisticsReport(ds);

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
		client.saveReport(ciReport, saml);
		client.saveReport(piBaReport, saml);
		client.saveReport(piEaReport, saml);
		client.saveReport(piEmReport, saml);
		client.saveReport(piNeReport, saml);
		client.saveReport(piNlReport, saml);
		client.saveReport(piNwReport, saml);
		client.saveReport(piSlReport, saml);
		client.saveReport(piWmReport, saml);
		client.saveReport(eq5dReportBaseline, saml);
		client.saveReport(eq5dReport12Months, saml);
		client.saveReport(drugSideEffectsReportBaseline, saml);
		client.saveReport(drugSideEffectsReport6Months, saml);
		client.saveReport(drugSideEffectsReport12Months, saml);
		client.saveReport(premorbidAdjustmentReport, saml);

		//trends reports
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
		client.saveReport(pathwaysTrendsReportBaseline, saml);
		client.saveReport(premorbidTrendsReportBaseline, saml);
		client.saveReport(dupHighLowTrendsReportBaseline, saml);
		client.saveReport(panssTrendsReportBaseline, saml);
		client.saveReport(panssTrendsReport6Months, saml);
		client.saveReport(panssTrendsReport12Months, saml);
		client.saveReport(dupTrendsReportBaseline, saml);

		//management reports
		client.saveReport(ciRecruitmentReport, saml);
		client.saveReport(cpmRecruitmentReport, saml);
		client.saveReport(piBaRecruitmentReport, saml);
		client.saveReport(piEaRecruitmentReport, saml);
		client.saveReport(piEmRecruitmentReport, saml);
		client.saveReport(piNeRecruitmentReport, saml);
		client.saveReport(piNlRecruitmentReport, saml);
		client.saveReport(piNwRecruitmentReport, saml);
		client.saveReport(piSlRecruitmentReport, saml);
		client.saveReport(piWmRecruitmentReport, saml);
		client.saveReport(ukCRNReport, saml);

		client.saveReport(recordStatusReport, saml);
		client.saveReport(documentStatusReport, saml);
		client.saveReport(collectionDateReport, saml);
		client.saveReport(stdCodeStatusReport, saml);
		client.saveReport(basicStatReport, saml);
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
			}    	}
		return statuses;
	}

	private static void addDocumentActions(DataSet dataSet) {
		for (Document document: ((DataSet)dataSet).getDocuments()) {
			document.setAction(RBACAction.ACTION_DR_DOC_STANDARD);
		}
	}
}