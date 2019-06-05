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

import java.net.MalformedURLException;
import java.net.URL;

import org.psygrid.common.TransformersWrapper;
import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.security.RBACAction;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.repository.client.RepositoryClient;

/**
 * @author Rob Harper
 *
 */
public class AddressDataset {

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

			AddressDataset addDs = new AddressDataset();
			DataSet ds = addDs.createDataset();
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

			client.removePublishedDataSet(id, "ED2", saml);

			System.out.println("Successfully deleted dataset");
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public DataSet createDataset(){

		Factory factory = new HibernateFactory();

		DataSet dataSet = factory.createDataset(getName(), getDisplayText());
		UnitWrapper.setFactory(new UnitFactory());
		UnitWrapper.instance().init(factory, dataSet);
		TransformersWrapper.setFactory(new TransformersFactory());
		TransformersWrapper.instance().init(factory, dataSet);
		ValidationRulesWrapper.setFactory(new ValidationRulesFactory());
		ValidationRulesWrapper.instance().init(factory, dataSet);

		dataSet.setProjectCode(getCode());
		dataSet.setVersionNo("1.0.0");
		dataSet.setEslUsed(true);
		dataSet.setRandomizationRequired(false);
		dataSet.setScheduleStartQuestion("Date of diagnosis");
		dataSet.setReviewReminderCount(0);

		//groups
		configureGroups(dataSet, factory);

		//consent
		ConsentFormGroup cfg = factory.createConsentFormGroup();
		dataSet.addAllConsentFormGroup(cfg);
		cfg.setDescription("General consent");
		PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
		pcf.setQuestion("Has the client given consent to take part in the study?");
		cfg.addConsentForm(pcf);
		PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
		pcf2.setQuestion("Has the client (under 16) given assent to take part in the study?");
		cfg.addConsentForm(pcf2);
		AssociatedConsentForm acf2 = factory.createAssociatedConsentForm();
		acf2.setQuestion("Has the client's parent/guardian given consent for them to take part in the study?");
		pcf2.addAssociatedConsentForm(acf2);

		ConsentFormGroup cfg2 = factory.createConsentFormGroup();
		dataSet.addAllConsentFormGroup(cfg2);
		cfg2.setDescription("Identifiable data consent");
		cfg2.setEslTrigger(true);
		PrimaryConsentForm pcf3 = factory.createPrimaryConsentForm();
		pcf3.setQuestion("Has the client agreed for their identifiable data to be stored?");
		cfg2.addConsentForm(pcf3);

		Status statReferred = factory.createStatus("Referred", "Referred", 0);
		statReferred.setGenericState(GenericState.REFERRED);
		Status statConsented = factory.createStatus("Consented", "Consented", 1);
		statConsented.setGenericState(GenericState.ACTIVE);
		Status statBaseline = factory.createStatus("Baseline", "Baseline completed", 2);
		statBaseline.setGenericState(GenericState.ACTIVE);
		Status stat6M = factory.createStatus("6Month", "6 Month Follow-up completed", 3);
		stat6M.setGenericState(GenericState.ACTIVE);
		Status stat1Y = factory.createStatus("1Year", "1 Year Follow-up completed", 4);
		stat1Y.setGenericState(GenericState.ACTIVE);
		Status stat2Y = factory.createStatus("2Year", "2 Year Follow-up completed", 5);
		stat2Y.setGenericState(GenericState.ACTIVE);
		Status stat3Y = factory.createStatus("3Year", "3 Year Follow-up completed", 6);
		stat3Y.setGenericState(GenericState.ACTIVE);
		Status stat4Y = factory.createStatus("4Year", "4 Year Follow-up completed", 7);
		stat4Y.setGenericState(GenericState.ACTIVE);
		Status stat5Y = factory.createStatus("5Year", "5 Year Follow-up completed", 8);
		stat5Y.setGenericState(GenericState.ACTIVE);
		Status statComplete = factory.createStatus("Complete", "Complete", 9);
		statComplete.setGenericState(GenericState.COMPLETED);
		statComplete.setInactive(true);
		Status statDeceased = factory.createStatus("Deceased", "Deceased", 10);
		statDeceased.setInactive(true);
		statDeceased.setGenericState(GenericState.LEFT);
		Status statWithdrew = factory.createStatus("Withdrew", "Withdrew", 11);
		statWithdrew.setInactive(true);
		statWithdrew.setGenericState(GenericState.INACTIVE);
		Status statInvalid = factory.createStatus("Invalid", "Invalid", 12);	//Record was added by mistake and shouldn't exist
		statInvalid.setInactive(true);
		statInvalid.setGenericState(GenericState.INVALID);

		Status statTempWithdrawn = factory.createStatus("TempWithdrawn", "Temporarily Withdrawn", 13);
		statTempWithdrawn.setInactive(true);
		statTempWithdrawn.setGenericState(GenericState.LEFT);

		Status statLeft = factory.createStatus("Left", "Left Study", 14);
		statLeft.setGenericState(GenericState.LEFT);
		statLeft.setInactive(true);


		statReferred.addStatusTransition(statConsented);            //referred -> consented
		statReferred.addStatusTransition(statDeceased);             //referred -> deceased
		statReferred.addStatusTransition(statWithdrew);             //referred -> withdrew
		statReferred.addStatusTransition(statInvalid);              //referred -> invalid
		//statReferred.addStatusTransition(statTempWithdrawn);        //referred -> temp withdrawn

		statConsented.addStatusTransition(statBaseline);          	//consented -> Baseline completed
		statConsented.addStatusTransition(stat6M);          		//consented -> 6M FU completed
		statConsented.addStatusTransition(stat1Y);          		//consented -> 1Y FU completed
		statConsented.addStatusTransition(stat2Y);              	//consented -> 2Y FU completed
		statConsented.addStatusTransition(stat3Y);             		//consented -> 3Y FU completed
		statConsented.addStatusTransition(stat4Y);             		//consented -> 4Y FU completed
		statConsented.addStatusTransition(stat5Y);             		//consented -> 5Y FU completed
		statConsented.addStatusTransition(statComplete);            //consented -> complete
		statConsented.addStatusTransition(statDeceased);            //consented -> deceased
		statConsented.addStatusTransition(statWithdrew);            //consented -> withdrew
		statConsented.addStatusTransition(statInvalid);             //consented -> invalid
		//statConsented.addStatusTransition(statTempWithdrawn);       //consented -> temp withdrawn
		statConsented.addStatusTransition(statLeft);             //consented -> left study

		statBaseline.addStatusTransition(stat6M);         			//Baseline completed -> 6M FU completed
		statBaseline.addStatusTransition(stat1Y);         			//Baseline completed -> 1Y FU completed
		statBaseline.addStatusTransition(stat2Y);             		//Baseline completed -> 2Y FU completed
		statBaseline.addStatusTransition(stat3Y);            		//Baseline completed -> 3Y FU completed
		statBaseline.addStatusTransition(stat4Y);            		//Baseline completed -> 4Y FU completed
		statBaseline.addStatusTransition(stat5Y);            		//Baseline completed -> 5Y FU completed
		statBaseline.addStatusTransition(statComplete);            	//Baseline completed -> complete
		statBaseline.addStatusTransition(statDeceased);           	//Baseline completed -> deceased
		statBaseline.addStatusTransition(statWithdrew);           	//Baseline completed -> withdrew
		statBaseline.addStatusTransition(statInvalid);            	//Baseline completed -> invalid
		//statBaseline.addStatusTransition(statTempWithdrawn);        //Baseline completed -> temp withdrawn
		statBaseline.addStatusTransition(statLeft);            	//Baseline completed -> left study

		stat6M.addStatusTransition(stat1Y);         				//6M FU completed -> 1Y FU completed
		stat6M.addStatusTransition(stat2Y);             			//6M FU completed -> 2Y FU completed
		stat6M.addStatusTransition(stat3Y);            				//6M FU completed -> 3Y FU completed
		stat6M.addStatusTransition(stat4Y);            				//6M FU completed -> 4Y FU completed
		stat6M.addStatusTransition(stat5Y);            				//6M FU completed -> 5Y FU completed
		stat6M.addStatusTransition(statComplete);            		//6M FU completed -> complete
		stat6M.addStatusTransition(statDeceased);           		//6M FU completed -> deceased
		stat6M.addStatusTransition(statWithdrew);           		//6M FU completed -> withdrew
		stat6M.addStatusTransition(statInvalid);            		//6M FU completed -> invalid
		//stat6M.addStatusTransition(statTempWithdrawn);        		//6M FU completed -> temp withdrawn
		stat6M.addStatusTransition(statLeft);            		//6M FU completed -> left study

		stat1Y.addStatusTransition(stat2Y);             			//1Y FU completed -> 2Y FU completed
		stat1Y.addStatusTransition(stat3Y);            				//1Y FU completed -> 3Y FU completed
		stat1Y.addStatusTransition(stat4Y);            				//1Y FU completed -> 4Y FU completed
		stat1Y.addStatusTransition(stat5Y);            				//1Y FU completed -> 5Y FU completed
		stat1Y.addStatusTransition(statComplete);            		//1Y FU completed -> complete
		stat1Y.addStatusTransition(statDeceased);           		//1Y FU completed -> deceased
		stat1Y.addStatusTransition(statWithdrew);           		//1Y FU completed -> withdrew
		stat1Y.addStatusTransition(statInvalid);            		//1Y FU completed -> invalid
		//stat1Y.addStatusTransition(statTempWithdrawn);        		//1Y FU completed -> temp withdrawn
		stat1Y.addStatusTransition(statLeft);            		//1Y FU completed -> left study

		stat2Y.addStatusTransition(stat3Y);            				//2Y FU completed -> 3Y FU completed
		stat2Y.addStatusTransition(stat4Y);            				//2Y FU completed -> 4Y FU completed
		stat2Y.addStatusTransition(stat5Y);            				//2Y FU completed -> 5Y FU completed
		stat2Y.addStatusTransition(statComplete);            		//2Y FU completed -> complete
		stat2Y.addStatusTransition(statDeceased);           		//2Y FU completed -> deceased
		stat2Y.addStatusTransition(statWithdrew);           		//2Y FU completed -> withdrew
		stat2Y.addStatusTransition(statInvalid);            		//2Y FU completed -> invalid
		//stat2Y.addStatusTransition(statTempWithdrawn);        		//2Y FU completed -> temp withdrawn
		stat2Y.addStatusTransition(statLeft);            		//2Y FU completed -> left study

		stat3Y.addStatusTransition(stat4Y);            				//3Y FU completed -> 4Y FU completed
		stat3Y.addStatusTransition(stat5Y);            				//3Y FU completed -> 5Y FU completed
		stat3Y.addStatusTransition(statComplete);            		//3Y FU completed -> complete
		stat3Y.addStatusTransition(statDeceased);           		//3Y FU completed -> deceased
		stat3Y.addStatusTransition(statWithdrew);           		//3Y FU completed -> withdrew
		stat3Y.addStatusTransition(statInvalid);            		//3Y FU completed -> invalid
		//stat3Y.addStatusTransition(statTempWithdrawn);       		//3Y FU completed -> temp withdrawn
		stat3Y.addStatusTransition(statLeft);            		//3Y FU completed -> left study

		stat4Y.addStatusTransition(stat5Y);            				//4Y FU completed -> 5Y FU completed
		stat4Y.addStatusTransition(statComplete);            		//4Y FU completed -> complete
		stat4Y.addStatusTransition(statDeceased);           		//4Y FU completed -> deceased
		stat4Y.addStatusTransition(statWithdrew);           		//4Y FU completed -> withdrew
		stat4Y.addStatusTransition(statInvalid);            		//4Y FU completed -> invalid
		//stat4Y.addStatusTransition(statTempWithdrawn);        		//4Y FU completed -> temp withdrawn
		stat4Y.addStatusTransition(statLeft);            		//4Y FU completed -> left study

		stat5Y.addStatusTransition(statComplete);            		//5Y FU completed -> complete
		stat5Y.addStatusTransition(statDeceased);           		//5Y FU completed -> deceased
		stat5Y.addStatusTransition(statWithdrew);           		//5Y FU completed -> withdrew
		stat5Y.addStatusTransition(statInvalid);            		//5Y FU completed -> invalid
		//stat5Y.addStatusTransition(statTempWithdrawn);        		//5Y FU completed -> temp withdrawn
		stat5Y.addStatusTransition(statLeft);            		//5Y FU completed -> Left Study

		statTempWithdrawn.addStatusTransition(statReferred);
		statTempWithdrawn.addStatusTransition(statConsented);
		statTempWithdrawn.addStatusTransition(statBaseline);
		statTempWithdrawn.addStatusTransition(stat6M);
		statTempWithdrawn.addStatusTransition(stat1Y);
		statTempWithdrawn.addStatusTransition(stat2Y);
		statTempWithdrawn.addStatusTransition(stat3Y);
		statTempWithdrawn.addStatusTransition(stat4Y);
		statTempWithdrawn.addStatusTransition(stat5Y);
		statTempWithdrawn.addStatusTransition(statWithdrew);
		statTempWithdrawn.addStatusTransition(statDeceased);
		statTempWithdrawn.addStatusTransition(statInvalid);
		statTempWithdrawn.addStatusTransition(statLeft);

		statLeft.addStatusTransition(statWithdrew);					//Left Study -> Withdrew
		statLeft.addStatusTransition(statDeceased);					//Left Study -> Deceased
		statLeft.addStatusTransition(statInvalid);					//Left Study -> Invalid


		dataSet.addStatus(statReferred);
		dataSet.addStatus(statConsented);
		dataSet.addStatus(statBaseline);
		dataSet.addStatus(stat6M);
		dataSet.addStatus(stat1Y);
		dataSet.addStatus(stat2Y);
		dataSet.addStatus(stat3Y);
		dataSet.addStatus(stat4Y);
		dataSet.addStatus(stat5Y);
		dataSet.addStatus(statComplete);
		dataSet.addStatus(statDeceased);
		dataSet.addStatus(statWithdrew);
		dataSet.addStatus(statInvalid);
		dataSet.addStatus(statTempWithdrawn);
		dataSet.addStatus(statLeft);


		//document groups
		DocumentGroup baseline = factory.createDocumentGroup("Baseline Group");
		baseline.setDisplayText("Baseline");
		baseline.addAllowedRecordStatus(statConsented);
		baseline.addAllowedRecordStatus(statBaseline);
		baseline.addAllowedRecordStatus(stat6M);
		baseline.addAllowedRecordStatus(stat1Y);
		baseline.addAllowedRecordStatus(stat2Y);
		baseline.addAllowedRecordStatus(stat3Y);
		baseline.addAllowedRecordStatus(stat4Y);
		baseline.addAllowedRecordStatus(stat5Y);
		baseline.setUpdateStatus(statBaseline);

		DocumentGroup sixMonths = factory.createDocumentGroup("6 Month Follow Up Group");
		sixMonths.setDisplayText("6 Month Follow Up");
		sixMonths.addAllowedRecordStatus(statBaseline);
		sixMonths.addAllowedRecordStatus(stat6M);
		sixMonths.addAllowedRecordStatus(stat1Y);
		sixMonths.addAllowedRecordStatus(stat2Y);
		sixMonths.addAllowedRecordStatus(stat3Y);
		sixMonths.addAllowedRecordStatus(stat4Y);
		sixMonths.addAllowedRecordStatus(stat5Y);
		sixMonths.addPrerequisiteGroup(baseline);
		sixMonths.setUpdateStatus(stat6M);

		DocumentGroup oneYear = factory.createDocumentGroup("Year 1 Follow Up Group");
		oneYear.setDisplayText("1 Year Follow Up");
		oneYear.addAllowedRecordStatus(stat6M);
		oneYear.addAllowedRecordStatus(stat1Y);
		oneYear.addAllowedRecordStatus(stat2Y);
		oneYear.addAllowedRecordStatus(stat3Y);
		oneYear.addAllowedRecordStatus(stat4Y);
		oneYear.addAllowedRecordStatus(stat5Y);
		oneYear.addPrerequisiteGroup(sixMonths);
		oneYear.setUpdateStatus(stat1Y);

		DocumentGroup twoYears = factory.createDocumentGroup("Year 2 Follow Up Group");
		twoYears.setDisplayText("2 Year Follow Up");
		twoYears.addAllowedRecordStatus(stat1Y);
		twoYears.addAllowedRecordStatus(stat2Y);
		twoYears.addAllowedRecordStatus(stat3Y);
		twoYears.addAllowedRecordStatus(stat4Y);
		twoYears.addAllowedRecordStatus(stat5Y);
		twoYears.addPrerequisiteGroup(oneYear);
		twoYears.setUpdateStatus(stat2Y);

		DocumentGroup threeYears = factory.createDocumentGroup("Year 3 Follow Up Group");
		threeYears.setDisplayText("3 Year Follow Up");
		threeYears.addAllowedRecordStatus(stat2Y);
		threeYears.addAllowedRecordStatus(stat3Y);
		threeYears.addAllowedRecordStatus(stat4Y);
		threeYears.addAllowedRecordStatus(stat5Y);
		threeYears.addPrerequisiteGroup(twoYears);
		threeYears.setUpdateStatus(stat3Y);

		DocumentGroup fourYears = factory.createDocumentGroup("Year 4 Follow Up Group");
		fourYears.setDisplayText("4 Year Follow Up");
		fourYears.addAllowedRecordStatus(stat3Y);
		fourYears.addAllowedRecordStatus(stat4Y);
		fourYears.addAllowedRecordStatus(stat5Y);
		fourYears.addPrerequisiteGroup(threeYears);
		fourYears.setUpdateStatus(stat4Y);

		DocumentGroup fiveYears = factory.createDocumentGroup("Year 5 Follow Up Group");
		fiveYears.setDisplayText("5 Year Follow Up");
		fiveYears.addAllowedRecordStatus(stat4Y);
		fiveYears.addAllowedRecordStatus(stat5Y);
		fiveYears.addPrerequisiteGroup(fourYears);
		fiveYears.setUpdateStatus(stat5Y);

		DocumentGroup shared = factory.createDocumentGroup("Shared Group");
		shared.setDisplayText("Shared");
		shared.addAllowedRecordStatus(statConsented);
		shared.addAllowedRecordStatus(statBaseline);
		shared.addAllowedRecordStatus(stat6M);
		shared.addAllowedRecordStatus(stat1Y);
		shared.addAllowedRecordStatus(stat2Y);
		shared.addAllowedRecordStatus(stat3Y);
		shared.addAllowedRecordStatus(stat4Y);
		shared.addAllowedRecordStatus(stat5Y);

		dataSet.addDocumentGroup(baseline);
		dataSet.addDocumentGroup(sixMonths);
		dataSet.addDocumentGroup(oneYear);
		dataSet.addDocumentGroup(twoYears);
		dataSet.addDocumentGroup(threeYears);
		dataSet.addDocumentGroup(fourYears);
		dataSet.addDocumentGroup(fiveYears);
		dataSet.addDocumentGroup(shared);

		/*
		 * Baseline documents
		 */
		Document demographics = Demographics.createDocument(factory);
		dataSet.addDocument(demographics);
		demographics.addConsentFormGroup(cfg);
		DocumentOccurrence demographicsBaseline = factory.createDocumentOccurrence("Baseline");
		demographicsBaseline.setDisplayText("Baseline");
		demographics.addOccurrence(demographicsBaseline);
		demographicsBaseline.setDocumentGroup(baseline);

		Document medicalDetails = MedicalDetails.createDocument(factory);
		dataSet.addDocument(medicalDetails);
		medicalDetails.addConsentFormGroup(cfg);
		DocumentOccurrence medicalDetailsBaseline = factory.createDocumentOccurrence("Baseline");
		medicalDetailsBaseline.setDisplayText("Baseline");
		medicalDetails.addOccurrence(medicalDetailsBaseline);
		medicalDetailsBaseline.setDocumentGroup(baseline);

		Document familyHistory = DiabetesFamilyHistory.createDocument(factory);
		dataSet.addDocument(familyHistory);
		familyHistory.addConsentFormGroup(cfg);
		DocumentOccurrence familyHistoryBaseline = factory.createDocumentOccurrence("Baseline");
		familyHistoryBaseline.setDisplayText("Baseline");
		familyHistory.addOccurrence(familyHistoryBaseline);
		familyHistoryBaseline.setDocumentGroup(baseline);

		Document clinicalMeasurements = ClinicalMeasurements.createDocument(factory);
		dataSet.addDocument(clinicalMeasurements);
		clinicalMeasurements.addConsentFormGroup(cfg);
		DocumentOccurrence clinicalMeasurementsBaseline = factory.createDocumentOccurrence("Baseline");
		clinicalMeasurementsBaseline.setDisplayText("Baseline");
		clinicalMeasurements.addOccurrence(clinicalMeasurementsBaseline);
		clinicalMeasurementsBaseline.setDocumentGroup(baseline);

		Document biochemistry = Biochemistry.createDocument(factory);
		dataSet.addDocument(biochemistry);
		biochemistry.addConsentFormGroup(cfg);
		DocumentOccurrence biochemistryBaseline = factory.createDocumentOccurrence("Baseline");
		biochemistryBaseline.setDisplayText("Baseline");
		biochemistry.addOccurrence(biochemistryBaseline);
		biochemistryBaseline.setDocumentGroup(baseline);


		/*
		 * Follow up documents
		 */
		Document medAndClinicalMeas = MedicationAndClinicalDetails.createDocument(factory);
		dataSet.addDocument(medAndClinicalMeas);
		medAndClinicalMeas.addConsentFormGroup(cfg);

		Document biochemistryFollowUp = BiochemistryFollowUp.createDocument(factory);
		dataSet.addDocument(biochemistryFollowUp);
		biochemistryFollowUp.addConsentFormGroup(cfg);

		Document diabetesComplications = DiabetesComplications.createDocument(factory);
		dataSet.addDocument(diabetesComplications);
		diabetesComplications.addConsentFormGroup(cfg);

		String[] followUpNames = new String[]{"6 Months", "1 Year", "2 Years", "3 Years", "4 Years", "5 Years"};
		DocumentGroup[] followUpDocGroups = new DocumentGroup[]{sixMonths, oneYear, twoYears, threeYears, fourYears, fiveYears};
		for ( int i=0, c=followUpNames.length; i<c; i++ ){

			DocumentOccurrence medAndClinicalMeasOcc = factory.createDocumentOccurrence(followUpNames[i]);
			medAndClinicalMeasOcc.setDisplayText(followUpNames[i]);
			medAndClinicalMeas.addOccurrence(medAndClinicalMeasOcc);
			medAndClinicalMeasOcc.setDocumentGroup(followUpDocGroups[i]);

			DocumentOccurrence biochemistryFollowUpOcc = factory.createDocumentOccurrence(followUpNames[i]);
			biochemistryFollowUpOcc.setDisplayText(followUpNames[i]);
			biochemistryFollowUp.addOccurrence(biochemistryFollowUpOcc);
			biochemistryFollowUpOcc.setDocumentGroup(followUpDocGroups[i]);

			DocumentOccurrence diabetesComplicationsOcc = factory.createDocumentOccurrence(followUpNames[i]);
			diabetesComplicationsOcc.setDisplayText(followUpNames[i]);
			diabetesComplications.addOccurrence(diabetesComplicationsOcc);
			diabetesComplicationsOcc.setDocumentGroup(followUpDocGroups[i]);

			if ( "6 Months".equals(followUpNames[i]) ){
				createSixMonthReminders(medAndClinicalMeasOcc, factory);
				createSixMonthReminders(biochemistryFollowUpOcc, factory);
				createSixMonthReminders(diabetesComplicationsOcc, factory);
			}
			else if ( "1 Year".equals(followUpNames[i]) ){
				createOneYearReminders(medAndClinicalMeasOcc, factory);
				createOneYearReminders(biochemistryFollowUpOcc, factory);
				createOneYearReminders(diabetesComplicationsOcc, factory);
			}
			else if ( "2 Years".equals(followUpNames[i]) ){
				createTwoYearReminders(medAndClinicalMeasOcc, factory);
				createTwoYearReminders(biochemistryFollowUpOcc, factory);
				createTwoYearReminders(diabetesComplicationsOcc, factory);
			}
			else if ( "3 Years".equals(followUpNames[i]) ){
				createThreeYearReminders(medAndClinicalMeasOcc, factory);
				createThreeYearReminders(biochemistryFollowUpOcc, factory);
				createThreeYearReminders(diabetesComplicationsOcc, factory);
			}
			else if ( "4 Years".equals(followUpNames[i]) ){
				createFourYearReminders(medAndClinicalMeasOcc, factory);
				createFourYearReminders(biochemistryFollowUpOcc, factory);
				createFourYearReminders(diabetesComplicationsOcc, factory);
			}
			else if ( "5 Years".equals(followUpNames[i]) ){
				createFiveYearReminders(medAndClinicalMeasOcc, factory);
				createFiveYearReminders(biochemistryFollowUpOcc, factory);
				createFiveYearReminders(diabetesComplicationsOcc, factory);
			}
			else{
				throw new RuntimeException("Unknown follow up name when adding reminders");
			}

		}

		/*
		 * Shared documents
		 */
		Document gpDetails = GpDetails.createDocument(factory);
		dataSet.addDocument(gpDetails);
		gpDetails.addConsentFormGroup(cfg);
		DocumentOccurrence gpDetailsShared = factory.createDocumentOccurrence("Shared");
		gpDetailsShared.setDisplayText("Shared");
		gpDetails.addOccurrence(gpDetailsShared);
		gpDetailsShared.setDocumentGroup(shared);

		Document participantContacted = ParticipantContacted.createDocument(factory);
		dataSet.addDocument(participantContacted);
		participantContacted.addConsentFormGroup(cfg);
		DocumentOccurrence participantContactedShared = factory.createDocumentOccurrence("Shared");
		participantContactedShared.setDisplayText("Shared");
		participantContacted.addOccurrence(participantContactedShared);
		participantContactedShared.setDocumentGroup(shared);

		Document siteTransfer = SiteTransfer.createDocument(factory);
		dataSet.addDocument(siteTransfer);
		siteTransfer.addConsentFormGroup(cfg);
		DocumentOccurrence siteTransferShared = factory.createDocumentOccurrence("Shared");
		siteTransferShared.setDisplayText("Shared");
		siteTransfer.addOccurrence(siteTransferShared);
		siteTransferShared.setDocumentGroup(shared);

		Document termination = Termination.createDocument(factory);
		dataSet.addDocument(termination);
		termination.addConsentFormGroup(cfg);
		DocumentOccurrence terminationShared = factory.createDocumentOccurrence("Shared");
		terminationShared.setDisplayText("Shared");
		termination.addOccurrence(terminationShared);
		terminationShared.setDocumentGroup(shared);


		/*
		 * Add an RBACAction to each document
		 */
		addDocumentActions(dataSet);

		return dataSet;
	}

	public static void createReports(DataSet ds, String saml) throws Exception {
		ReportsClient client = new ReportsClient();
		client.saveReport(Reports.baselineReport(ds), saml);
		client.saveReport(Reports.sixMonthFollowUp(ds), saml);
		client.saveReport(Reports.oneYearFollowUp(ds), saml);
		client.saveReport(Reports.twoYearFollowUp(ds), saml);
		client.saveReport(Reports.threeYearFollowUp(ds), saml);
		client.saveReport(Reports.fourYearFollowUp(ds), saml);
		client.saveReport(Reports.fiveYearFollowUp(ds), saml);
		client.saveReport(Reports.contactInfo(ds), saml);
		client.saveReport(Reports.gpDetails(ds), saml);
		client.saveReport(Reports.transfers(ds), saml);
		client.saveReport(Reports.withdrawals(ds), saml);
		client.saveReport(Reports.participantsContacted(ds), saml);

		//Management reports
		IReport cpmReport = Reports.cpmMgmtReport(ds);
		IReport ciReport = Reports.ciMgmtReport(ds);
		IReport ukCRNReport= Reports.ukCRNReport(ds);
		IReport ciRecruitment  = Reports.recruitmentReport(ds);
		IReport cpmRecruitment = Reports.cpmRecruitmentReport(ds);
		IReport recordStatusReport = Reports.recordStatusReport(ds, null, "");
		IReport documentStatusReport = Reports.documentStatusReport(ds, null, "");
		IReport collectionDateReport = Reports.collectionDateReport(ds, null, "");
		IReport basicStatsReport = Reports.basicStatisticsReport(ds);
		IReport piNorthEastCumbriaMgmtReport = Reports.piNorthEastCumbriaMgmtReport(ds);
		IReport piNorthWestMgmtReport = Reports.piNorthWestMgmtReport(ds);
		IReport piNorthWestLondonMgmtReport = Reports.piNorthWestLondonMgmtReport(ds);
		IReport piSouthWestMgmtReport = Reports.piSouthWestMgmtReport(ds);
		IReport piThamesValleyMgmtReport = Reports.piThamesValleyMgmtReport(ds);
		IReport piEasternEnglandMgmtReport = Reports.piEasternEnglandMgmtReport(ds);
		IReport piNorthEastLondonMgmtReport = Reports.piNorthEastLondonMgmtReport(ds);
		IReport piSouthEastMidlandsMgmtReport = Reports.piSouthEastMidlandsMgmtReport(ds);
		IReport recruitmentInNorthEastAndCumbriaReport = Reports.recruitmentInNorthEastAndCumbriaReport(ds);
		IReport recruitmentInNorthWestReport = Reports.recruitmentInNorthWestReport(ds);
		IReport recruitmentInNorthWestLondonReport = Reports.recruitmentInNorthWestLondonReport(ds);
		IReport recruitmentInSouthWestReport = Reports.recruitmentInSouthWestReport(ds);
		IReport recruitmentInThamesValleyReport = Reports.recruitmentInThamesValleyReport(ds);
		IReport recruitmentInEasternEnglandReport = Reports.recruitmentInEasternEnglandReport(ds);
		IReport recruitmentInNorthEastLondonReport = Reports.recruitmentInNorthEastLondonReport(ds);
		IReport recruitmentInSouthEastMidlandsReport = Reports.recruitmentInSouthEastMidlandsReport(ds);

		//save the reports
		client.saveReport(cpmReport, saml);
		client.saveReport(ciReport, saml);
		client.saveReport(ukCRNReport, saml);
		client.saveReport(ciRecruitment, saml);
		client.saveReport(cpmRecruitment, saml);
		client.saveReport(recordStatusReport, saml);
		client.saveReport(documentStatusReport, saml);
		client.saveReport(collectionDateReport, saml);
		client.saveReport(basicStatsReport, saml);

		client.saveReport(piNorthEastCumbriaMgmtReport, saml);
		client.saveReport(piNorthWestMgmtReport, saml);
		client.saveReport(piNorthWestLondonMgmtReport, saml);
		client.saveReport(piSouthWestMgmtReport, saml);
		client.saveReport(piThamesValleyMgmtReport, saml);
		client.saveReport(piEasternEnglandMgmtReport, saml);
		client.saveReport(piNorthEastLondonMgmtReport, saml);
		client.saveReport(piSouthEastMidlandsMgmtReport, saml);
		client.saveReport(recruitmentInNorthEastAndCumbriaReport, saml);
		client.saveReport(recruitmentInNorthWestReport, saml);
		client.saveReport(recruitmentInNorthWestLondonReport, saml);
		client.saveReport(recruitmentInSouthWestReport, saml);
		client.saveReport(recruitmentInThamesValleyReport, saml);
		client.saveReport(recruitmentInEasternEnglandReport, saml);
		client.saveReport(recruitmentInNorthEastLondonReport, saml);
		client.saveReport(recruitmentInSouthEastMidlandsReport, saml);

	}


	private static void addDocumentActions(DataSet dataSet) {
		for (Document document: ((DataSet)dataSet).getDocuments()) {
			document.setAction(RBACAction.ACTION_DR_DOC_STANDARD);
			document.setInstanceAction(RBACAction.ACTION_DR_DOC_STANDARD_INST);
		}
	}
	private static void createSixMonthReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(6));
		occurrence.setScheduleUnits(TimeUnits.MONTHS);
		Reminder rem1 = factory.createReminder(168, TimeUnits.DAYS, ReminderLevel.MILD);
		occurrence.addReminder(rem1);
	}

	private static void createOneYearReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(1));
		occurrence.setScheduleUnits(TimeUnits.YEARS);
		Reminder rem1 = factory.createReminder(351, TimeUnits.DAYS, ReminderLevel.MILD);
		occurrence.addReminder(rem1);
	}

	private static void createTwoYearReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(2));
		occurrence.setScheduleUnits(TimeUnits.YEARS);
		Reminder rem1 = factory.createReminder(726, TimeUnits.DAYS, ReminderLevel.MILD);
		occurrence.addReminder(rem1);
	}

	private static void createThreeYearReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(3));
		occurrence.setScheduleUnits(TimeUnits.YEARS);
		Reminder rem1 = factory.createReminder(1081, TimeUnits.DAYS, ReminderLevel.MILD);
		occurrence.addReminder(rem1);
	}

	private static void createFourYearReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(4));
		occurrence.setScheduleUnits(TimeUnits.YEARS);
		Reminder rem1 = factory.createReminder(1446, TimeUnits.DAYS, ReminderLevel.MILD);
		occurrence.addReminder(rem1);
	}

	private static void createFiveYearReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(5));
		occurrence.setScheduleUnits(TimeUnits.YEARS);
		Reminder rem1 = factory.createReminder(1811, TimeUnits.DAYS, ReminderLevel.MILD);
		occurrence.addReminder(rem1);
	}

	protected String getName(){
		return "ADDRESS";
	}

	protected String getDisplayText(){
		return "After Diagnosis Diabetes REsearch Support System";
	}

	protected String getCode(){
		return "ADD";
	}

	protected void configureGroups(DataSet dataSet, Factory factory){
		Group grp1 = (Group)factory.createGroup("640001");
		grp1.setLongName("North Cumbria Acute Hospitals NHS Trust");
		Site site1a = new Site("West Cumberland Hospital", "N0000059", "CA28 8JG", grp1);
		site1a.addConsultant("Dr Stewart Sawers");
		grp1.addSite(site1a);
		Site site1b = new Site("Cumberland Infirmary", "N000002", "CA2 7HY", grp1);
		site1b.addConsultant("Dr Stewart Sawers");
		grp1.addSite(site1b);
		Group grp2 = (Group)factory.createGroup("640002");
		grp2.setLongName("South Tees Hospital NHS Trust");
		Site site2a = new Site("James Cook University Hospital: CTU, Diabetes Care Centre", "N0000006", "TS4 3BW", grp2);
		site2a.addConsultant("Prof Rudy Bilous");
		grp2.addSite(site2a);
		Group grp3 = (Group)factory.createGroup("640003");
		grp3.setLongName("City Hospitals Sunderland NHS Foundation Trust");
		Site site3a = new Site("Sunderland Royal Hospital: Diabetes Unit", "N0000173", "SR4 7TP", grp3);
		site3a.addConsultant("Dr John Chapman");
		grp3.addSite(site3a);
		Site site3b = new Site("Sunderland Royal Hospital: Niall Quinn Childrens Centre", "N0000732", "SR4 7TP", grp3);
		site3b.addConsultant("Dr John Chapman");
		grp3.addSite(site3b);
		Group grp4 = (Group)factory.createGroup("640004");
		grp4.setLongName("Gateshead Hospitals NHS Foundation Trust");
		Site site4a = new Site("Queen Elizabeth Hospital: Childrens Unit", "N0000969", "NE9 6SX", grp4);
		site4a.addConsultant("Dr Jola Weaver");
		grp4.addSite(site4a);
		Site site4b = new Site("Bensham Hospital: Diabetes Centre", "N0000936", "NE8 4YL", grp4);
		site4b.addConsultant("Dr Jola Weaver");
		grp4.addSite(site4b);
		Group grp5 = (Group)factory.createGroup("640005");
		grp5.setLongName("Newcastle upon Tyne Hospitals NHS Foundation Trust");
		Site site5a = new Site("Royal Victoria Infirmary: Diabetes OPD Childrens Unit", "N0000072", "NE1 4LP", grp5);
		site5a.addConsultant("Prof Mark Walker");
		grp5.addSite(site5a);
		Site site5b = new Site("Newcastle General Hospital: Diabetes OPD", "N0000004", "NE4 6BE", grp5);
		site5b.addConsultant("Prof Mark Walker");
		grp5.addSite(site5b);
		Group grp6 = (Group)factory.createGroup("651001");
		grp6.setLongName("Salford Royal Hospitals NHS Foundation Trust");
		Site site6a = new Site("Hope Hospital", "N0000079", "M6 8HD", grp6);
		site6a.addConsultant("Dr Martin Gibson");
		grp6.addSite(site6a);
		Group grp7 = (Group)factory.createGroup("650001");
		grp7.setLongName("NW London Hospitals NHS Trust");
		Site site7a = new Site("Central Middlesex Hospital", "N0000127", "NW10 7NS", grp7);
		site7a.addConsultant("Dr Daniel Darko");
		grp7.addSite(site7a);
		Site site7b = new Site("Northwick Park Hospital", "N0000132", "HA1 3UJ", grp7);
		site7b.addConsultant("Dr Daniel Darko");
		grp7.addSite(site7b);
		Group grp8 = (Group)factory.createGroup("650002");
		grp8.setLongName("Hillingdon Hospital NHS Trust");
		Site site8a = new Site("Hillingdon Hospital", "N0000130", "UB8 3NN", grp8);
		site8a.addConsultant("Dr Mark Edwards");
		grp8.addSite(site8a);
		Group grp9 = (Group)factory.createGroup("652001");
		grp9.setLongName("Royal Cornwall Healthcare NHS Trust");
		Site site9a = new Site("Royal Cornwall Hospital (Treliske)", "N0000121", "TR1 3LJ", grp9);
		site9a.addConsultant("Dr Jonathan Pinkney");
		grp9.addSite(site9a);
		Site site9b = new Site("West Cornwall Hospital", "N0000485", "TR18 2PF", grp9);
		site9b.addConsultant("Dr Jonathan Pinkney");
		grp9.addSite(site9b);
		Group grp10 = (Group)factory.createGroup("652002");
		grp10.setLongName("Plymouth Hospitals NHS Trust");
		Site site10a = new Site("Derriford Hospital", "N0000118", "PL6 8DH", grp10);
		site10a.addConsultant("Dr Ann Millward");
		grp10.addSite(site10a);
		Group grp11 = (Group)factory.createGroup("652003");
		grp11.setLongName("Royal Devon and Exeter NHS Foundation Trust");
		Site site11a = new Site("Royal Devon and Exeter Hospital", "N0000002", "EX2 5DW", grp11);
		site11a.addConsultant("Prof Andrew Hattersley");
		grp11.addSite(site11a);
		Group grp12 = (Group)factory.createGroup("652004");
		grp12.setLongName("South Devon Healthcare NHS Foundation Trust");
		Site site12a = new Site("Torbay Hospital", "N0000124", "TQ2 7AA", grp12);
		site12a.addConsultant("Dr Jamie Smith");
		grp12.addSite(site12a);
		Group grp13 = (Group)factory.createGroup("649001");
		grp13.setLongName("Oxford Radcliffe Hospitals NHS Trust");
		Site site13a = new Site("John Radcliffe Hospital", "N0000227", "OX3 9DU", grp13);
		site13a.addConsultant("Dr Julie Edge");
		grp13.addSite(site13a);
		Site site13b = new Site("Churchill Hospital", "N0000153", "OX3 7LJ", grp13);
		site13b.addConsultant("Dr Julie Edge");
		grp13.addSite(site13b);
		Site site13c = new Site("Horton General Hospital", "N0000190", "OX16 9AL", grp13);
		site13c.addConsultant("Dr Julie Edge");
		grp13.addSite(site13c);
		Group grp14 = (Group)factory.createGroup("654001");
		grp14.setLongName("The Ipswich Hospital NHS Trust");
		Site site14a = new Site("Ipswich Hospital", "N0000148", "IP4 5PD", grp14);
		site14a.addConsultant("Dr Gerry Rayman");
		grp14.addSite(site14a);
		Group grp15 = (Group)factory.createGroup("653001");
		grp15.setLongName("Barts and the London NHS Trust");
		Site site15a = new Site("Royal London Hospital", "N0000134", "E1 1BB", grp15);
		site15a.addConsultant("Prof Graham Hitman");
		grp15.addSite(site15a);
		Site site15b = new Site("St Bart's Hospital", "N0000137", "EC1A 7BE", grp15);
		site15b.addConsultant("Prof Graham Hitman");
		grp15.addSite(site15b);
		Group grp16 = (Group)factory.createGroup("653002");
		grp16.setLongName("Whipps Cross University Hospital Trust");
		Site site16a = new Site("Whipps Cross University Hospital", "N0000215", "E11 1NR", grp16);
		site16a.addConsultant("Dr David Levy");
		grp16.addSite(site16a);
		Group grp17 = (Group)factory.createGroup("653004");
		grp17.setLongName("Newham University Hospital NHS Trust");
		Site site17a = new Site("Newham General Hospital", "N0000237", "E13 8SL", grp17);
		site17a.addConsultant("Dr Shanti Vijayaraghavan");
		grp17.addSite(site17a);
		Group grp18 = (Group)factory.createGroup("653003");
		grp18.setLongName("Southend University Hospital NHS Foundation");
		Site site18a = new Site("Southend Hospital", "N0000049", "SS0 0RY", grp18);
		site18a.addConsultant("Dr Karl Metcalfe");
		grp18.addSite(site18a);
		Group grp19 = (Group)factory.createGroup("655001");
		grp19.setLongName("University Hospitals of Leicester NHS Trust");
		Site site19a = new Site("Leicester Royal Infirmary", "N0000031", "LE1 5WW", grp19);
		site19a.addConsultant("Prof Melanie Davies");
		grp19.addSite(site19a);
		Site site19b = new Site("Leicester General Hospital", "N0000135", "LE5 4PW", grp19);
		site19b.addConsultant("Prof Melanie Davies");
		grp19.addSite(site19b);

		Group grp20 = (Group)factory.createGroup("650003");
		grp20.setLongName("Ealing Hospital");
		Site site20a = new Site("Ealing Hospital", "N0000128", "UB1 3HW", grp20);
		site20a.addConsultant("Dr Kevin Baynes");
		grp20.addSite(site20a);
		Group grp21 = (Group)factory.createGroup("650004");
		grp21.setLongName("Imperial College Healthcare NHS trust");
		Site site21a = new Site("Hammersmith Hospital", "N0000129", "W12 0HS", grp21);
		site21a.addConsultant("Dr Ann Dornhorst");
		grp21.addSite(site21a);
		Site site21b = new Site("St Mary's Hospital", "N0000214", "W2 1NY", grp21);
		site21b.addConsultant("Prof Robert Elkeles");
		grp21.addSite(site21b);
		Site site21c = new Site("Charing Cross Hospital", "N0000015", "W6 8RF", grp21);
		site21c.addConsultant("Dr Ann Dornhorst");
		grp21.addSite(site21c);
		Group grp22 = (Group)factory.createGroup("650005");
		grp22.setLongName("Chelsea and Westminister Hospital");
		Site site22a = new Site("Chelsea and Westminister Hospital", "N0000016", " SW10 9NH", grp22);
		site22a.addConsultant("Dr Nicola Bridges");
		grp22.addSite(site22a);
		Group grp23 = (Group)factory.createGroup("650006");
		grp23.setLongName("West Middlesex University Hospitals");
		Site site23a = new Site("West Middlesex Hospitals NHS trust", "N0000060", "TW7 6AF", grp23);
		site23a.addConsultant("Dr Rashmi Kaushal");
		grp23.addSite(site23a);
		Group grp24 = (Group)factory.createGroup("650007");
		grp24.setLongName("Barnet and Chase Farm Hospitals");
		Site site24a = new Site("Barnet Hospital", "N0000126", "EN5 3DJ", grp24);
		site24a.addConsultant("Dr Vaseem Hakeem");
		grp24.addSite(site24a);
		Site site24b = new Site("Chase Farm Hospital", "N0000181", "EN2 8JL", grp24);
		site24b.addConsultant("Dr Vaseem Hakeem");
		grp24.addSite(site24b);

		Group grp25 = (Group)factory.createGroup("651002");
		grp25.setLongName("Central Manchester and Manchester Childrens Hospital");
		Site site25a = new Site("Manchester Royal Infirmary", "N0000080", "M13 9WL", grp25);
		site25a.addConsultant("Prof Rob Davies");
		grp25.addSite(site25a);
		Group grp26 = (Group)factory.createGroup("649005");
		grp26.setLongName("The Royal Berkshire NHS foundation trust");
		Site site26a = new Site("Royal Berkshire Hospital", "N0000139", "RG1 5AN", grp26);
		site26a.addConsultant("Dr Hugh Simpson");
		grp26.addSite(site26a);
		Group grp27 = (Group)factory.createGroup("649006");
		grp27.setLongName("Buckinghamshire Hospitals NHS Trust");
		Site site27a = new Site("Wycombe Hospital", "N0000106", "HP11 2TT", grp27);
		site27a.addConsultant("Dr Ian Gallen");
		grp27.addSite(site27a);
		Site site27b = new Site("Amersham Hospital", "N0000280", "HP7 0JD", grp27);
		site27b.addConsultant("Dr Ian Gallen");
		grp27.addSite(site27b);
		Group grp28 = (Group)factory.createGroup("653005");
		grp28.setLongName("Homerton University Hospital NHS Foundation Trust");
		Site site28a = new Site("Homerton Hospital", "N0000292", "E9 6SR", grp28);
		site28a.addConsultant("Dr John Anderson");
		grp28.addSite(site28a);
		Group grp29 = (Group)factory.createGroup("653006");
		grp29.setLongName("Broomfield Hospital");
		Site site29a = new Site("Broomfield Hospital", "N0000013", "CM1 7ET", grp29);
		site29a.addConsultant("Dr Alan Jackson");
		grp29.addSite(site29a);
		Group grp30 = (Group)factory.createGroup("651003");
		grp30.setLongName("East Lancashire NHS trust");
		Site site30a = new Site("Royal Blackburn Hospital", "N0001527", "BB2 3HH", grp30);
		site30a.addConsultant("Claire Smith");
		site30a.addConsultant("Dr Ramatoola");
		grp30.addSite(site30a);
		Site site30b = new Site("Burnley General Hospital", "N0000287", "BB10 2PQ", grp30);
		site30b.addConsultant("Miles Riddle");
		grp30.addSite(site30b);
		Group grp31 = (Group)factory.createGroup("651004");
		grp31.setLongName("DEPRECATED");
		Site site31a = new Site("Burnley General Hospital", "N0000287", "BB10 2PQ", grp31);
		site31a.addConsultant("Miles Riddle");
		grp31.addSite(site31a);
		Group grp32 = (Group)factory.createGroup("649007");
		grp32.setLongName("South Warwickshire General Hospital NHS Trust");
		Site site32a = new Site("Warwick Hospital", "?", "CV34 5BW", grp32);
		site32a.addConsultant("Dr Jyoti Sidhu");
		grp32.addSite(site32a);
		Group grp33 = (Group)factory.createGroup("654004");
		grp33.setLongName("Norfolk and Norwich University Hospitals NHS Foundation Trust");
		Site site33a = new Site("Norfolk and Norwich Hospital", "N0000036", "NR4 7UY", grp33);
		site33a.addConsultant("Dr Nandu Thalange");
		grp33.addSite(site33a);

		Group grp34 = (Group)factory.createGroup("651005");
		grp34.setLongName("Aintree University Hopsital NHS Foundation Trust");
		Site site34a = new Site("University Hospital Aintree", "N0000085", "L9 7AL", grp34);
		site34a.addConsultant("Professor John Wilding");
		grp34.addSite(site34a);

		Group grp35 = (Group)factory.createGroup("651006");
		grp35.setLongName("DEPRECATED");
		Site site35a = new Site("Burnley General Hospital", "N0000287", "?", grp35);
		site35a.addConsultant("Dr Claire Smith");
		grp35.addSite(site35a);

		Group grp36 = (Group)factory.createGroup("651007");
		grp36.setLongName("St Helens and Knowsley Hospitals NHS");
		Site site36a = new Site("St Helens and Knowsley Hospitals", "N0000287", "?", grp36);
		site36a.addConsultant("Dr Kevin Hardy");
		grp36.addSite(site36a);

		Group grp37 = (Group)factory.createGroup("651008");
		grp37.setLongName("University Hospital of South Manchester NHS Foundation Trust");
		Site site37a = new Site("Wythenshawe Hospital", "N0000172", "?", grp37);
		site37a.addConsultant("Dr Andrew Bradbury");
		grp37.addSite(site37a);

		Group grp38 = (Group)factory.createGroup("651009");
		grp38.setLongName("Wirral University Teaching Hospital NHS Foundation Trust");
		Site site38a = new Site("Arrowe Park Hospital", "N0000009", "?", grp38);
		site38a.addConsultant("Dr King Sun Leong");
		grp38.addSite(site38a);

		Group grp39 = (Group)factory.createGroup("650008");
		grp39.setLongName("West Hertfordshire hospital NHS trust");
		Site site39a = new Site("Hemel Hempstead General Hospital", "N0000189", "?", grp39);
		site39a.addConsultant("Dr Colin Johnson");
		grp39.addSite(site39a);

		Group grp40 = (Group)factory.createGroup("649002");
		grp40.setLongName("Wycombe Hospital");
		Site site40a = new Site("Wycombe Hospital", "N0000106", "?", grp40);
		site40a.addConsultant("Dr Ian Gallen");
		grp40.addSite(site40a);

		Group grp41 = (Group)factory.createGroup("649003");
		grp41.setLongName("George Eliot Hospital");
		Site site41a = new Site("George Eliot Hospital", "N0000187", "?", grp41);
		site41a.addConsultant("Dr Vinod Patel");
		grp41.addSite(site41a);

		Group grp42 = (Group)factory.createGroup("651010");
		grp42.setLongName("Countess of Chester Hospital NHS Foundation Trust");
		Site site42a = new Site("Countess of Chester Hospital", "N0000083", "?", grp42);
		site42a.addConsultant("Dr Niru Goenka");
		grp42.addSite(site42a);

		Group grp43 = (Group)factory.createGroup("654005");
		grp43.setLongName("James Paget University Hospitals NHS Foundation Trust");
		Site site43a = new Site("Northgate hospital", "N0000749", "?", grp43);
		site43a.addConsultant("Dr Sangeeta Garg");
		grp43.addSite(site43a);

		Group grp44 = (Group)factory.createGroup("655002");
		grp44.setLongName("United Lincolnshire Hospitals NHS Trust");
		Site site44a = new Site("Lincoln County Hospital", "N0000089", "?", grp44);
		site44a.addConsultant("Dr Keith Sands");
		grp44.addSite(site44a);

		Group grp45 = (Group)factory.createGroup("640006");
		grp45.setLongName("Queen Elizabeth Hospital NHS Trust");
		Site site45a = new Site("Queen Elizabeth Hospital Woolwich", "0000255", "?", grp45);
		site45a.addConsultant("Dr Jola Weaver");
		grp45.addSite(site45a);

		Group grp46 = (Group)factory.createGroup("640008");
		grp46.setLongName("Newcastle Diabetes Centre");
		Site site46a = new Site("Newcastle General Hospital: Diabetes OPD", "N0000004", "?", grp46);
		site46a.addConsultant("Prof Mark Walker");
		grp46.addSite(site46a);

		Group grp47 = (Group)factory.createGroup("640009");
		grp47.setLongName("South Tyneside Healthcare NHS Trust");
		Site site47a = new Site("South Tyneside District General Hospital", "N0000073", "?", grp47);
		site47a.addConsultant("Dr John Parr");
		grp47.addSite(site47a);

		Group grp48 = (Group)factory.createGroup("640010");
		grp48.setLongName("Northumbria Healthcare NHS Trust");
		Site site48a = new Site("North Tyneside General Hospital", "N0000279", "?", grp48);
		site48a.addConsultant("Dr Simon Eaton");
		grp48.addSite(site48a);

		Group grp49 = (Group)factory.createGroup("640011");
		grp49.setLongName("North Tees and Hartlepool NHS Foundation Trust");
		Site site49a = new Site("North Tees and Hartlepool hospital", "N0000390", "?", grp49);
		site49a.addConsultant("Dr Steve Jones");
		grp49.addSite(site49a);

		Group grp50 = (Group)factory.createGroup("649008");
		grp50.setLongName("Milton Keynes Hospital");
		Site site50a = new Site("Milton Keynes Hospital", "N0000314", "?", grp50);
		site50a.addConsultant("Dr Shanthi Chandran");
		grp50.addSite(site50a);

		Group grp51 = (Group)factory.createGroup("651011");
		grp51.setLongName("Pennine Acute Trust");
		Site site51a = new Site("North Manchester General Hospital", "N0000260", "?", grp51);
		site51a.addConsultant("Phil Wiles");
		grp51.addSite(site51a);

		//This will be group 51 when accessed by index.
		Group grp52 = (Group)factory.createGroup("640007");
		grp52.setLongName("Durham and Darlington NHS Foundation Trust");
		Site site52a = new Site("Darlington Memorial Hospital", "N0000068", "?", grp52);
		//Does this site have a consultant?
		grp52.addSite(site52a);

		//This will be group 52 when accessed by index.
		Group grp53 = (Group)factory.createGroup("654006");
		grp53.setLongName("The Queen Elizabeth Hospital King's Lynn");
		Site site53a = new Site("The Queen Elizabeth Hospital King's Lynn NHS Foundation Trust", "N0000163", "?", grp53);
		//Does this site have a consultant?
		grp53.addSite(site53a);

		//This will be group 53 when accessed by index.
		Group grp54 = (Group)factory.createGroup("649004");
		grp54.setLongName("University Hospitals Coventry");
		Site site54a = new Site("University Hospitals Coventry and Warwickshire NHS Trust", "N0000511", "?", grp54);
		site54a.addConsultant("Dr. Sailesh Sankar");
		grp54.addSite(site54a);

		//This will be group 54 when accessed by index.
		Group grp55 = (Group)factory.createGroup("653007");
		grp55.setLongName("Barking, Havering and Redbridge Hospitals NHS Trust");
		Site site55a = new Site("Barking Hospital", "N0008765", "?", grp55);
		site55a.addConsultant("Dr. Kash Nikookam");
		grp55.addSite(site55a);

		Group grp56 = (Group)factory.createGroup("651012");
		grp56.setLongName("Pennine Acute Trust - Oldham");
		Site royalOldham = new Site("Royal Oldham Hospital", "N0000488", "OL1 2JH", grp56);
		royalOldham.addConsultant("Dr Egware Odeka");
		grp56.addSite(royalOldham);


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
		dataSet.addGroup(grp15);
		dataSet.addGroup(grp16);
		dataSet.addGroup(grp17);
		dataSet.addGroup(grp18);
		dataSet.addGroup(grp19);
		dataSet.addGroup(grp20);
		dataSet.addGroup(grp21);
		dataSet.addGroup(grp22);
		dataSet.addGroup(grp23);
		dataSet.addGroup(grp24);
		dataSet.addGroup(grp25);
		dataSet.addGroup(grp26);
		dataSet.addGroup(grp27);
		dataSet.addGroup(grp28);
		dataSet.addGroup(grp29);
		dataSet.addGroup(grp30);
		dataSet.addGroup(grp31);
		dataSet.addGroup(grp32);
		dataSet.addGroup(grp33);
		dataSet.addGroup(grp34);
		dataSet.addGroup(grp35);
		dataSet.addGroup(grp36);
		dataSet.addGroup(grp37);
		dataSet.addGroup(grp38);
		dataSet.addGroup(grp39);
		dataSet.addGroup(grp40);
		dataSet.addGroup(grp41);
		dataSet.addGroup(grp42);
		dataSet.addGroup(grp43);
		dataSet.addGroup(grp44);
		dataSet.addGroup(grp45);
		dataSet.addGroup(grp46);
		dataSet.addGroup(grp47);
		dataSet.addGroup(grp48);
		dataSet.addGroup(grp49);
		dataSet.addGroup(grp50);
		dataSet.addGroup(grp51);
		dataSet.addGroup(grp52);
		dataSet.addGroup(grp53);
		dataSet.addGroup(grp54);
		dataSet.addGroup(grp55);
		dataSet.addGroup(grp56);
	}
}
