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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.psygrid.common.TransformersWrapper;
import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.security.RBACAction;
import org.psygrid.www.xml.security.core.types.GroupType;

/**
 * @author Rob Harper
 *
 */
public class CommandDataset {

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

			CommandDataset command = new CommandDataset();
			DataSet ds = command.createDataset();

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

	public CommandDataset(){

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

			client.removePublishedDataSet(id, "COM", saml);

			System.out.println("Successfully deleted dataset");
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	protected String getName(){
		return "COMMAND";
	}

	protected String getDisplayText(){
		return "COMMAND";
	}

	protected String getCode(){
		return "COM";
	}


	public DataSet createDataset() throws Exception{

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
		dataSet.setSendMonthlySummaries(true);
		dataSet.setEslUsed(true);
		dataSet.setRandomizationRequired(true);
		dataSet.setReviewReminderCount(0);

		configureGroups(factory, dataSet);

		//consent
		ConsentFormGroup cfg = factory.createConsentFormGroup();
		cfg.setDescription("Main client consent");
		cfg.setEslTrigger(true);
		PrimaryConsentForm pcf1 = factory.createPrimaryConsentForm();
		pcf1.setQuestion("The client confirms that he/she has read and understood the information sheet dated December 2006" +
				" (version 3) for the study and has had the opportunity to ask questions.\n" );
		AssociatedConsentForm pcf2 = factory.createAssociatedConsentForm();
		pcf2.setQuestion("The client understands that their participation is voluntary and that they are free to withdraw at any time, " +
				"without giving any reason and without their medical care or legal rights being affected.\n");
		AssociatedConsentForm pcf3 = factory.createAssociatedConsentForm();
		pcf3.setQuestion("The client understands that responsible individuals may look at sections of his/her medical notes" +
				" where it is relevant to their taking part in this research. The client gives permission for these individuals" +
				" to have access to his/her records.");

		cfg.addConsentForm(pcf1);
		pcf1.addAssociatedConsentForm(pcf2);
		pcf1.addAssociatedConsentForm(pcf3);
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
		statActive.addStatusTransition(statLeft); //active -> invalid

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
		DocumentGroup baseline = factory.createDocumentGroup("Baseline Group");
		baseline.setDisplayText("Baseline");
		baseline.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		baseline.setUpdateStatus(statActive);
		dataSet.addDocumentGroup(baseline);

		DocumentGroup nineMonths = factory.createDocumentGroup("9 Month Follow Up Group");
		nineMonths.setDisplayText("9 Month Follow Up");
		nineMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		nineMonths.addPrerequisiteGroup(baseline);
		dataSet.addDocumentGroup(nineMonths);

		DocumentGroup eighteenMonths = factory.createDocumentGroup("18 Month Follow Up Group");
		eighteenMonths.setDisplayText("18 Month Follow Up");
		eighteenMonths.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		eighteenMonths.addPrerequisiteGroup(nineMonths);
		eighteenMonths.setUpdateStatus(statComplete);
		dataSet.addDocumentGroup(eighteenMonths);

		//Baseline
		Document personalDetails = PersonalDetails.createDocument(factory);
		dataSet.addDocument(personalDetails);
		personalDetails.addConsentFormGroup(cfg);
		DocumentOccurrence personalDetailsBaseline = factory.createDocumentOccurrence("Baseline");
		personalDetailsBaseline.setRandomizationTrigger(true);
		personalDetailsBaseline.setDisplayText("Baseline");
		personalDetailsBaseline.setDocumentGroup(baseline);
		personalDetails.addOccurrence(personalDetailsBaseline);

		Document cav = CAV.createDocument(factory);
		dataSet.addDocument(cav);
		cav.addConsentFormGroup(cfg);
		DocumentOccurrence cavBaseline = factory.createDocumentOccurrence("Baseline");
		cavBaseline.setDisplayText("Baseline");
		cavBaseline.setDocumentGroup(baseline);
		cav.addOccurrence(cavBaseline);

		Document compliance = Compliance.createDocument(factory);
		dataSet.addDocument(compliance);
		compliance.addConsentFormGroup(cfg);
		DocumentOccurrence complianceBaseline = factory.createDocumentOccurrence("Baseline");
		complianceBaseline.setDisplayText("Baseline");
		complianceBaseline.setDocumentGroup(baseline);
		compliance.addOccurrence(complianceBaseline);

		Document pkq = PKQ.createDocument(factory);
		dataSet.addDocument(pkq);
		pkq.addConsentFormGroup(cfg);
		DocumentOccurrence pkqBaseline = factory.createDocumentOccurrence("Baseline");
		pkqBaseline.setDisplayText("Baseline");
		pkqBaseline.setDocumentGroup(baseline);
		pkq.addOccurrence(pkqBaseline);

		Document vpd = VPD.createDocument(factory);
		dataSet.addDocument(vpd);
		vpd.addConsentFormGroup(cfg);
		DocumentOccurrence vpdBaseline = factory.createDocumentOccurrence("Baseline");
		vpdBaseline.setDisplayText("Baseline");
		vpdBaseline.setDocumentGroup(baseline);
		vpd.addOccurrence(vpdBaseline);

		Document bavqr = BAVQR.createDocument(factory);
		dataSet.addDocument(bavqr);
		bavqr.addConsentFormGroup(cfg);
		DocumentOccurrence bavqrBaseline = factory.createDocumentOccurrence("Baseline");
		bavqrBaseline.setDisplayText("Baseline");
		bavqrBaseline.setDocumentGroup(baseline);
		bavqr.addOccurrence(bavqrBaseline);

		Document psyrats = Psyrats.createDocument(factory);
		dataSet.addDocument(psyrats);
		psyrats.addConsentFormGroup(cfg);
		DocumentOccurrence psyratsBaseline = factory.createDocumentOccurrence("Baseline");
		psyratsBaseline.setDisplayText("Baseline");
		psyratsBaseline.setDocumentGroup(baseline);
		psyrats.addOccurrence(psyratsBaseline);

		Document panss = Panss.createDocument(factory);
		dataSet.addDocument(panss);
		panss.addConsentFormGroup(cfg);
		DocumentOccurrence panssBaseline = factory.createDocumentOccurrence("Baseline");
		panssBaseline.setDisplayText("Baseline");
		panssBaseline.setDocumentGroup(baseline);
		panss.addOccurrence(panssBaseline);

		Document calgary = Calgary.createDocument(factory);
		dataSet.addDocument(calgary);
		calgary.addConsentFormGroup(cfg);
		DocumentOccurrence calgaryBaseline = factory.createDocumentOccurrence("Baseline");
		calgaryBaseline.setDisplayText("Baseline");
		calgaryBaseline.setDocumentGroup(baseline);
		calgary.addOccurrence(calgaryBaseline);

		Document bhs = BHS.createDocument(factory);
		dataSet.addDocument(bhs);
		bhs.addConsentFormGroup(cfg);
		DocumentOccurrence bhsBaseline = factory.createDocumentOccurrence("Baseline");
		bhsBaseline.setDisplayText("Baseline");
		bhsBaseline.setDocumentGroup(baseline);
		bhs.addOccurrence(bhsBaseline);

		Document bsi = BSI.createDocument(factory);
		dataSet.addDocument(bsi);
		bsi.addConsentFormGroup(cfg);
		DocumentOccurrence bsiBaseline = factory.createDocumentOccurrence("Baseline");
		bsiBaseline.setDisplayText("Baseline");
		bsiBaseline.setDocumentGroup(baseline);
		bsi.addOccurrence(bsiBaseline);

		Document suicideHistory = HistoryOfSuicide.createDocument(factory);
		dataSet.addDocument(suicideHistory);
		suicideHistory.addConsentFormGroup(cfg);
		DocumentOccurrence suicideHistoryBaseline = factory.createDocumentOccurrence("Baseline");
		suicideHistoryBaseline.setDisplayText("Baseline");
		suicideHistoryBaseline.setDocumentGroup(baseline);
		suicideHistoryBaseline.setLocked(true);
		suicideHistory.addOccurrence(suicideHistoryBaseline);

		Document risk = RiskRating.createDocument(factory);
		dataSet.addDocument(risk);
		risk.addConsentFormGroup(cfg);
		DocumentOccurrence riskBaseline = factory.createDocumentOccurrence("Baseline");
		riskBaseline.setDisplayText("Baseline");
		riskBaseline.setDocumentGroup(baseline);
		risk.addOccurrence(riskBaseline);

		Document ctq = CTQ.createDocument(factory);
		dataSet.addDocument(ctq);
		ctq.addConsentFormGroup(cfg);
		DocumentOccurrence ctqBaseline = factory.createDocumentOccurrence("Baseline");
		ctqBaseline.setDisplayText("Baseline");
		ctqBaseline.setDocumentGroup(baseline);
		ctq.addOccurrence(ctqBaseline);

		Document eq5d = EQ5D.createDocument(factory);
		dataSet.addDocument(eq5d);
		eq5d.addConsentFormGroup(cfg);
		DocumentOccurrence eq5dBaseline = factory.createDocumentOccurrence("Baseline");
		eq5dBaseline.setDisplayText("Baseline");
		eq5dBaseline.setDocumentGroup(baseline);
		eq5d.addOccurrence(eq5dBaseline);

		Document epqv2 = EPQv2.createDocument(factory);
		dataSet.addDocument(epqv2);
		epqv2.addConsentFormGroup(cfg);
		DocumentOccurrence epqv2Baseline = factory.createDocumentOccurrence("Baseline");
		epqv2Baseline.setDisplayText("Baseline");
		epqv2Baseline.setDocumentGroup(baseline);
		epqv2.addOccurrence(epqv2Baseline);

		Document npHospitalV1Baseline = NPhospitalV1Baseline.createDocument(factory, true);
		dataSet.addDocument(npHospitalV1Baseline);
		npHospitalV1Baseline.addConsentFormGroup(cfg);
		DocumentOccurrence npHospitalV1BaselineOcc = factory.createDocumentOccurrence("Baseline");
		npHospitalV1BaselineOcc.setDisplayText("Baseline");
		npHospitalV1BaselineOcc.setDocumentGroup(baseline);
		npHospitalV1Baseline.addOccurrence(npHospitalV1BaselineOcc);

		Document pHospitalV1Baseline = PhospitalV1Baseline.createDocument(factory, true);
		dataSet.addDocument(pHospitalV1Baseline);
		pHospitalV1Baseline.addConsentFormGroup(cfg);
		DocumentOccurrence pHospitalV1BaselineOcc = factory.createDocumentOccurrence("Baseline");
		pHospitalV1BaselineOcc.setDisplayText("Baseline");
		pHospitalV1BaselineOcc.setDocumentGroup(baseline);
		pHospitalV1Baseline.addOccurrence(pHospitalV1BaselineOcc);

		Document ctch = CTCH.createDocument(factory);
		dataSet.addDocument(ctch);
		ctch.addConsentFormGroup(cfg);
		DocumentOccurrence ctchBaseline = factory.createDocumentOccurrence("Baseline");
		ctchBaseline.setDisplayText("Baseline");
		ctchBaseline.setDocumentGroup(baseline);
		ctch.addOccurrence(ctchBaseline);
		//Document should only be available to RAs and CSO(?)s
		ctch.setAction(RBACAction.ACTION_DR_DOC_BLIND);
		ctch.setInstanceAction(RBACAction.ACTION_DR_DOC_BLIND_INST);

		//9 Months
		DocumentOccurrence cavNineMonths = factory.createDocumentOccurrence("9 Months");
		cavNineMonths.setDisplayText("9 Months");
		cavNineMonths.setDocumentGroup(nineMonths);
		cav.addOccurrence(cavNineMonths);
		forNineMonthDocOcc(cavNineMonths);

		DocumentOccurrence complianceNineMonths = factory.createDocumentOccurrence("9 Months");
		complianceNineMonths.setDisplayText("9 Months");
		complianceNineMonths.setDocumentGroup(nineMonths);
		compliance.addOccurrence(complianceNineMonths);
		forNineMonthDocOcc(complianceNineMonths);

		DocumentOccurrence pkqNineMonths = factory.createDocumentOccurrence("9 Months");
		pkqNineMonths.setDisplayText("9 Months");
		pkqNineMonths.setDocumentGroup(nineMonths);
		pkq.addOccurrence(pkqNineMonths);
		forNineMonthDocOcc(pkqNineMonths);

		DocumentOccurrence vpdNineMonths = factory.createDocumentOccurrence("9 Months");
		vpdNineMonths.setDisplayText("9 Months");
		vpdNineMonths.setDocumentGroup(nineMonths);
		vpd.addOccurrence(vpdNineMonths);
		forNineMonthDocOcc(vpdNineMonths);

		DocumentOccurrence bavqrNineMonths = factory.createDocumentOccurrence("9 Months");
		bavqrNineMonths.setDisplayText("9 Months");
		bavqrNineMonths.setDocumentGroup(nineMonths);
		bavqr.addOccurrence(bavqrNineMonths);
		forNineMonthDocOcc(bavqrNineMonths);

		DocumentOccurrence psyratsNineMonths = factory.createDocumentOccurrence("9 Months");
		psyratsNineMonths.setDisplayText("9 Months");
		psyratsNineMonths.setDocumentGroup(nineMonths);
		psyrats.addOccurrence(psyratsNineMonths);
		forNineMonthDocOcc(psyratsNineMonths);

		DocumentOccurrence panssNineMonths = factory.createDocumentOccurrence("9 Months");
		panssNineMonths.setDisplayText("9 Months");
		panssNineMonths.setDocumentGroup(nineMonths);
		panss.addOccurrence(panssNineMonths);
		forNineMonthDocOcc(panssNineMonths);

		DocumentOccurrence calgaryNineMonths = factory.createDocumentOccurrence("9 Months");
		calgaryNineMonths.setDisplayText("9 Months");
		calgaryNineMonths.setDocumentGroup(nineMonths);
		calgary.addOccurrence(calgaryNineMonths);
		forNineMonthDocOcc(calgaryNineMonths);

		DocumentOccurrence bhsNineMonths = factory.createDocumentOccurrence("9 Months");
		bhsNineMonths.setDisplayText("9 Months");
		bhsNineMonths.setDocumentGroup(nineMonths);
		bhs.addOccurrence(bhsNineMonths);
		forNineMonthDocOcc(bhsNineMonths);

		DocumentOccurrence bsiNineMonths = factory.createDocumentOccurrence("9 Months");
		bsiNineMonths.setDisplayText("9 Months");
		bsiNineMonths.setDocumentGroup(nineMonths);
		bsi.addOccurrence(bsiNineMonths);
		forNineMonthDocOcc(bsiNineMonths);

		DocumentOccurrence ctchNineMonths = factory.createDocumentOccurrence("9 Months");
		ctchNineMonths.setDisplayText("9 Months");
		ctchNineMonths.setDocumentGroup(nineMonths);
		ctch.addOccurrence(ctchNineMonths);
		forNineMonthDocOcc(ctchNineMonths);


		//18 Months
		DocumentOccurrence cavEighteenMonths = factory.createDocumentOccurrence("18 Months");
		cavEighteenMonths.setDisplayText("18 Months");
		cavEighteenMonths.setDocumentGroup(eighteenMonths);
		cav.addOccurrence(cavEighteenMonths);
		forEighteenMonthDocOcc(cavEighteenMonths);

		DocumentOccurrence complianceEighteenMonths = factory.createDocumentOccurrence("18 Months");
		complianceEighteenMonths.setDisplayText("18 Months");
		complianceEighteenMonths.setDocumentGroup(eighteenMonths);
		compliance.addOccurrence(complianceEighteenMonths);
		forEighteenMonthDocOcc(complianceEighteenMonths);

		DocumentOccurrence pkqEighteenMonths = factory.createDocumentOccurrence("18 Months");
		pkqEighteenMonths.setDisplayText("18 Months");
		pkqEighteenMonths.setDocumentGroup(eighteenMonths);
		pkq.addOccurrence(pkqEighteenMonths);
		forEighteenMonthDocOcc(pkqEighteenMonths);

		DocumentOccurrence vpdEighteenMonths = factory.createDocumentOccurrence("18 Months");
		vpdEighteenMonths.setDisplayText("18 Months");
		vpdEighteenMonths.setDocumentGroup(eighteenMonths);
		vpd.addOccurrence(vpdEighteenMonths);
		forEighteenMonthDocOcc(vpdEighteenMonths);

		DocumentOccurrence bavqrEighteenMonths = factory.createDocumentOccurrence("18 Months");
		bavqrEighteenMonths.setDisplayText("18 Months");
		bavqrEighteenMonths.setDocumentGroup(eighteenMonths);
		bavqr.addOccurrence(bavqrEighteenMonths);
		forEighteenMonthDocOcc(bavqrEighteenMonths);

		DocumentOccurrence psyratsEighteenMonths = factory.createDocumentOccurrence("18 Months");
		psyratsEighteenMonths.setDisplayText("18 Months");
		psyratsEighteenMonths.setDocumentGroup(eighteenMonths);
		psyrats.addOccurrence(psyratsEighteenMonths);
		forEighteenMonthDocOcc(psyratsEighteenMonths);

		DocumentOccurrence panssEighteenMonths = factory.createDocumentOccurrence("18 Months");
		panssEighteenMonths.setDisplayText("18 Months");
		panssEighteenMonths.setDocumentGroup(eighteenMonths);
		panss.addOccurrence(panssEighteenMonths);
		forEighteenMonthDocOcc(panssEighteenMonths);

		DocumentOccurrence calgaryEighteenMonths = factory.createDocumentOccurrence("18 Months");
		calgaryEighteenMonths.setDisplayText("18 Months");
		calgaryEighteenMonths.setDocumentGroup(eighteenMonths);
		calgary.addOccurrence(calgaryEighteenMonths);
		forEighteenMonthDocOcc(calgaryEighteenMonths);

		DocumentOccurrence bhsEighteenMonths = factory.createDocumentOccurrence("18 Months");
		bhsEighteenMonths.setDisplayText("18 Months");
		bhsEighteenMonths.setDocumentGroup(eighteenMonths);
		bhs.addOccurrence(bhsEighteenMonths);
		forEighteenMonthDocOcc(bhsEighteenMonths);

		DocumentOccurrence bsiEighteenMonths = factory.createDocumentOccurrence("18 Months");
		bsiEighteenMonths.setDisplayText("18 Months");
		bsiEighteenMonths.setDocumentGroup(eighteenMonths);
		bsi.addOccurrence(bsiEighteenMonths);
		forEighteenMonthDocOcc(bsiEighteenMonths);

		DocumentOccurrence eq5dEighteenMonths = factory.createDocumentOccurrence("18 Months");
		eq5dEighteenMonths.setDisplayText("18 Months");
		eq5dEighteenMonths.setDocumentGroup(eighteenMonths);
		eq5d.addOccurrence(eq5dEighteenMonths);
		forEighteenMonthDocOcc(eq5dEighteenMonths);

		DocumentOccurrence epqv2EighteenMonths = factory.createDocumentOccurrence("18 Months");
		epqv2EighteenMonths.setDisplayText("18 Months");
		epqv2EighteenMonths.setDocumentGroup(eighteenMonths);
		epqv2.addOccurrence(epqv2EighteenMonths);
		forEighteenMonthDocOcc(epqv2EighteenMonths);

		//Question A1 has different phrasing between baseline and 18 months
		Document npHospitalV1EighteenMonths = NPhospitalV1Baseline.createDocument(factory, false);
		dataSet.addDocument(npHospitalV1EighteenMonths);
		npHospitalV1EighteenMonths.addConsentFormGroup(cfg);
		DocumentOccurrence npHospitalV1EighteenMonthsOcc = factory.createDocumentOccurrence("18 Months");
		npHospitalV1EighteenMonthsOcc.setDisplayText("18 Months");
		npHospitalV1EighteenMonthsOcc.setDocumentGroup(eighteenMonths);
		npHospitalV1EighteenMonths.addOccurrence(npHospitalV1EighteenMonthsOcc);
		forEighteenMonthDocOcc(npHospitalV1EighteenMonthsOcc);

		//Question A1 has different phrasing between baseline and 18 months
		Document pHospitalV1EighteenMonths = PhospitalV1Baseline.createDocument(factory, false);
		dataSet.addDocument(pHospitalV1EighteenMonths);
		pHospitalV1EighteenMonths.addConsentFormGroup(cfg);
		DocumentOccurrence pHospitalV1EighteenMonthsOcc = factory.createDocumentOccurrence("18 Months");
		pHospitalV1EighteenMonthsOcc.setDisplayText("18 Months");
		pHospitalV1EighteenMonthsOcc.setDocumentGroup(eighteenMonths);
		pHospitalV1EighteenMonths.addOccurrence(pHospitalV1EighteenMonthsOcc);
		forEighteenMonthDocOcc(pHospitalV1EighteenMonthsOcc);

		DocumentOccurrence ctchEighteenMonths = factory.createDocumentOccurrence("18 Months");
		ctchEighteenMonths.setDisplayText("18 Months");
		ctchEighteenMonths.setDocumentGroup(eighteenMonths);
		ctch.addOccurrence(ctchEighteenMonths);
		forEighteenMonthDocOcc(ctchEighteenMonths);

		/*
		 * Documents added after first deployment
		 */
		Document suicideHistoryV2 = HistoryOfSuicideV2.createDocument(factory);
		dataSet.addDocument(suicideHistoryV2);
		suicideHistoryV2.addConsentFormGroup(cfg);
		DocumentOccurrence suicideHistoryV2Baseline = factory.createDocumentOccurrence("Baseline");
		suicideHistoryV2Baseline.setDisplayText("Baseline");
		suicideHistoryV2Baseline.setDocumentGroup(baseline);
		suicideHistoryV2.addOccurrence(suicideHistoryV2Baseline);


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
		List<IReport> piReports = new ArrayList<IReport>();
		for ( GroupType gt: COMGroups.allGroups() ){
			piReports.add(Reports.piMgmtReport(ds, gt));
		}
		IReport ukCRNReport= Reports.ukCRNReport(ds);
		IReport ciRecruitment  = Reports.recruitmentReport(ds);
		IReport cpmRecruitment = Reports.cpmRecruitmentReport(ds);
		List<IReport> rcrtReports = new ArrayList<IReport>();
		for ( GroupType gt: COMGroups.allGroups() ){
			rcrtReports.add(Reports.recruitmentInGroupReport(ds, gt));
		}
		IReport ciReceivingTreatment    = Reports.ciReceivingTreatmentReport(ds);
		IReport cpmReceivingTreatment   = Reports.cpmReceivingTreatmentReport(ds);
		IReport recordStatusReport = Reports.recordStatusReport(ds, null, "");
		IReport documentStatusReport = Reports.documentStatusReport(ds, null, "");
		IReport collectionDateReport = Reports.collectionDateReport(ds, null, "");
		IReport stdCodeStatusReport  = Reports.stdCodeStatusReport(ds);
		IReport basicStatsReport = Reports.basicStatisticsReport(ds);

		//save the reports
		ReportsClient client = new ReportsClient();
		client.saveReport(cpmReport, saml);
		client.saveReport(ciReport, saml);
		for ( IReport r: piReports ){
			client.saveReport(r, saml);
		}
		client.saveReport(ukCRNReport, saml);
		client.saveReport(ciRecruitment, saml);
		client.saveReport(cpmRecruitment, saml);
		for ( IReport r: rcrtReports ){
			client.saveReport(r, saml);
		}
		client.saveReport(ciReceivingTreatment, saml);
		client.saveReport(cpmReceivingTreatment, saml);

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
	 public static List<Status> getStatuses(DataSet dataSet, GenericState genericState) {
		 List<Status> statuses = new ArrayList<Status>();
		 String state = genericState.toString();
		 for (Status status: ((DataSet)dataSet).getStatuses()) {
			 if (status.getGenericState() != null && state.equals(status.getGenericState().toString())) {
				 statuses.add(status);
			 }
		 }
		 return statuses;
	 }

	private static void createNineMonthReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(9));
		occurrence.setScheduleUnits(TimeUnits.MONTHS);
		Reminder rem1 = factory.createReminder(267, TimeUnits.DAYS, ReminderLevel.MILD);
		occurrence.addReminder(rem1);
	}

	private static void createEighteenMonthReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(18));
		occurrence.setScheduleUnits(TimeUnits.MONTHS);
		Reminder rem1 = factory.createReminder(541, TimeUnits.DAYS, ReminderLevel.MILD);
		occurrence.addReminder(rem1);
	}

    /**
     * Add an RBACAction to each document not already having an action. This specifies a default
     * policy for access to the document.
     *
     * @param dataSet
     */
	private static void addDocumentActions(DataSet dataSet) {
		for (Document document: ((DataSet)dataSet).getDocuments()) {
			if (document.getAction() == null) {
				document.setAction(RBACAction.ACTION_DR_DOC_STANDARD);
				document.setInstanceAction(RBACAction.ACTION_DR_DOC_STANDARD_INST);
			}
		}
	}

	private void addHoESites(Group group) throws Exception {
		System.out.println("Importing Sites from JANUARY 2008 Mapping - HoE.xls");
	    URL url = Thread.currentThread().getContextClassLoader().getResource("org/psygrid/command/resources/JANUARY 2008 Mapping - HoE.xls");
		Workbook book = Workbook.getWorkbook(new File(url.toURI()));
		Set<Integer> ignoreSheets = new HashSet<Integer>();
		ignoreSheets.add(new Integer(0));
		int[] postcodeCol = new int[]{0,3,2,3,5,6};
		for ( int i=0, c=book.getNumberOfSheets(); i<c; i++ ){
			if ( !ignoreSheets.contains(new Integer(i)) ){
				Sheet sheet = book.getSheet(i);
				System.out.println("Importing Sites from Sheet="+sheet.getName());
				for ( int j=1, d=sheet.getRows(); j<d; j++ ){
					Cell[] row = sheet.getRow(j);
					String colA = null;
					String colB = null;
					String postcode = null;
					try{
						colA = row[0].getContents().trim();
						colB = row[1].getContents().trim();
						postcode = row[postcodeCol[i]].getContents().trim();
					}
					catch(ArrayIndexOutOfBoundsException ex){
						//do nothing - caused by an empty row in the spreadsheet
						//and will be handled gracefully by the if clause below
					}
					if ( null != colA && colA.length()>0 && null != colB && colB.length()>0 ){
						//System.out.println("Row="+j+"; colA="+colA+"; colB="+colB+"; postcode="+postcode);
						String siteName = colA+" - "+colB;
						//check for duplicates
						boolean duplicate = false;
						for ( int k=0, e=group.numSites(); k<e ; k++ ){
							if ( group.getSite(k).getSiteName().equals(siteName)){
								duplicate = true;
								break;
							}
						}
						if ( !duplicate ){
							System.out.println(siteName);
							Site site1 = new Site(siteName, "??", postcode, group);
							site1.addConsultant("Max Birchwood");
							group.addSite(site1);
						}
					}
				}
			}
		}
	}

	private void addNWSites(Group group) throws Exception{
		{
			System.out.println("Importing Sites from BST.xls");
		    URL url = Thread.currentThread().getContextClassLoader().getResource("org/psygrid/command/resources/BST.xls");
			Workbook book = Workbook.getWorkbook(new File(url.toURI()));
			Set<Integer> ignoreSheets = new HashSet<Integer>();
			ignoreSheets.add(new Integer(1));
			int[] postcodeCol = new int[]{3,0};
			for ( int i=0, c=book.getNumberOfSheets(); i<c; i++ ){
				if ( !ignoreSheets.contains(new Integer(i)) ){
					Sheet sheet = book.getSheet(i);
					System.out.println("Importing Sites from Sheet="+sheet.getName());
					for ( int j=2, d=sheet.getRows(); j<d; j++ ){
						Cell[] row = sheet.getRow(j);
						String colC = null;
						String postcode = null;
						try{
							colC = row[2].getContents().trim();
							postcode = row[postcodeCol[i]].getContents().trim();
						}
						catch(ArrayIndexOutOfBoundsException ex){
							//do nothing - caused by an empty row in the spreadsheet
							//and will be handled gracefully by the if clause below
						}
						if ( null != colC && colC.length()>0 ){
							//System.out.println("Row="+j+"; colC="+colC+"; postcode="+postcode);
							String siteName = "BST - "+colC;
							//check for duplicates
							boolean duplicate = false;
							for ( int k=0, e=group.numSites(); k<e ; k++ ){
								if ( group.getSite(k).getSiteName().equals(siteName)){
									duplicate = true;
									break;
								}
							}
							if ( !duplicate ){
								System.out.println(siteName);
								Site site1 = new Site(siteName, "??", postcode, group);
								site1.addConsultant("Shôn Lewis");
								site1.addConsultant("Nick Tarrier");
								group.addSite(site1);
							}
						}
					}
				}
			}

		}

		{
			System.out.println("Importing Sites from LCT.xls");
		    URL url = Thread.currentThread().getContextClassLoader().getResource("org/psygrid/command/resources/LCT.xls");
			Workbook book = Workbook.getWorkbook(new File(url.toURI()));
			Set<Integer> ignoreSheets = new HashSet<Integer>();
			ignoreSheets.add(new Integer(1));
			int[] postcodeCol = new int[]{3,0};
			for ( int i=0, c=book.getNumberOfSheets(); i<c; i++ ){
				if ( !ignoreSheets.contains(new Integer(i)) ){
					Sheet sheet = book.getSheet(i);
					System.out.println("Importing Sites from Sheet="+sheet.getName());
					for ( int j=2, d=sheet.getRows(); j<d; j++ ){
						Cell[] row = sheet.getRow(j);
						String colC = null;
						String postcode = null;
						try{
							colC = row[2].getContents().trim();
							postcode = row[postcodeCol[i]].getContents().trim();
						}
						catch(ArrayIndexOutOfBoundsException ex){
							//do nothing - caused by an empty row in the spreadsheet
							//and will be handled gracefully by the if clause below
						}
						if ( null != colC && colC.length()>0 ){
							//System.out.println("Row="+j+"; colC="+colC+"; postcode="+postcode);
							String siteName = "Lancashire - "+colC;
							//check for duplicates
							boolean duplicate = false;
							for ( int k=0, e=group.numSites(); k<e ; k++ ){
								if ( group.getSite(k).getSiteName().equals(siteName)){
									duplicate = true;
									break;
								}
							}
							if ( !duplicate ){
								System.out.println(siteName);
								Site site1 = new Site(siteName, "??", postcode, group);
								site1.addConsultant("Shôn Lewis");
								site1.addConsultant("Nick Tarrier");
								group.addSite(site1);
							}
						}
					}
				}
			}

		}

		{
			System.out.println("Importing Sites from MMHSCNHSTReview 1.xls");
		    URL url = Thread.currentThread().getContextClassLoader().getResource("org/psygrid/command/resources/MMHSCNHSTReview 1.xls");
			Workbook book = Workbook.getWorkbook(new File(url.toURI()));
			Set<Integer> ignoreSheets = new HashSet<Integer>();
			ignoreSheets.add(new Integer(1));
			ignoreSheets.add(new Integer(2));
			int[] postcodeCol = new int[]{3,0};
			for ( int i=0, c=book.getNumberOfSheets(); i<c; i++ ){
				if ( !ignoreSheets.contains(new Integer(i)) ){
					Sheet sheet = book.getSheet(i);
					System.out.println("Importing Sites from Sheet="+sheet.getName());
					for ( int j=2, d=sheet.getRows(); j<d; j++ ){
						Cell[] row = sheet.getRow(j);
						String colC = null;
						String postcode = null;
						try{
							colC = row[2].getContents().trim();
							postcode = row[postcodeCol[i]].getContents().trim();
						}
						catch(ArrayIndexOutOfBoundsException ex){
							//do nothing - caused by an empty row in the spreadsheet
							//and will be handled gracefully by the if clause below
						}
						if ( null != colC && colC.length()>0 ){
							//System.out.println("Row="+j+"; colC="+colC+"; postcode="+postcode);
							String siteName = "Manchester - "+colC;
							//check for duplicates
							boolean duplicate = false;
							for ( int k=0, e=group.numSites(); k<e ; k++ ){
								if ( group.getSite(k).getSiteName().equals(siteName)){
									duplicate = true;
									break;
								}
							}
							if ( !duplicate ){
								System.out.println(siteName);
								Site site1 = new Site(siteName, "??", postcode, group);
								site1.addConsultant("Shôn Lewis");
								site1.addConsultant("Nick Tarrier");
								group.addSite(site1);
							}
						}
					}
				}
			}

		}

		{
			System.out.println("Importing Sites from Pennine Care NHS Trust.xls");
		    URL url = Thread.currentThread().getContextClassLoader().getResource("org/psygrid/command/resources/Pennine Care NHS Trust.xls");
			Workbook book = Workbook.getWorkbook(new File(url.toURI()));
			Set<Integer> ignoreSheets = new HashSet<Integer>();
			ignoreSheets.add(new Integer(1));
			int[] postcodeCol = new int[]{3,0};
			for ( int i=0, c=book.getNumberOfSheets(); i<c; i++ ){
				if ( !ignoreSheets.contains(new Integer(i)) ){
					Sheet sheet = book.getSheet(i);
					System.out.println("Importing Sites from Sheet="+sheet.getName());
					for ( int j=2, d=sheet.getRows(); j<d; j++ ){
						Cell[] row = sheet.getRow(j);
						String colC = null;
						String postcode = null;
						try{
							colC = row[2].getContents().trim();
							postcode = row[postcodeCol[i]].getContents().trim();
						}
						catch(ArrayIndexOutOfBoundsException ex){
							//do nothing - caused by an empty row in the spreadsheet
							//and will be handled gracefully by the if clause below
						}
						if ( null != colC && colC.length()>0 ){
							//System.out.println("Row="+j+"; colC="+colC+"; postcode="+postcode);
							String siteName = "Pennine Care - "+colC;
							//check for duplicates
							boolean duplicate = false;
							for ( int k=0, e=group.numSites(); k<e ; k++ ){
								if ( group.getSite(k).getSiteName().equals(siteName)){
									duplicate = true;
									break;
								}
							}
							if ( !duplicate ){
								System.out.println(siteName);
								Site site1 = new Site(siteName, "??", postcode, group);
								site1.addConsultant("Shôn Lewis");
								site1.addConsultant("Nick Tarrier");
								group.addSite(site1);
							}
						}
					}
				}
			}

		}

	}

	private void addSLSESites(Group group) throws Exception {
		System.out.println("Importing Sites from Command Mapping 29th Oct 2007 - London.xls");
	    URL url = Thread.currentThread().getContextClassLoader().getResource("org/psygrid/command/resources/Command Mapping 29th Oct 2007 - London.xls");
		Workbook book = Workbook.getWorkbook(new File(url.toURI()));
		Set<Integer> ignoreSheets = new HashSet<Integer>();
		ignoreSheets.add(new Integer(1));
		ignoreSheets.add(new Integer(2));
		int[] postcodeCol = new int[]{9,0,0,6};
		for ( int i=0, c=book.getNumberOfSheets(); i<c; i++ ){
			if ( !ignoreSheets.contains(new Integer(i)) ){
				Sheet sheet = book.getSheet(i);
				System.out.println("Importing Sites from Sheet="+sheet.getName());
				for ( int j=1, d=sheet.getRows(); j<d; j++ ){
					Cell[] row = sheet.getRow(j);
					String colA = null;
					String colC = null;
					String postcode = null;
					try{
						colA = row[0].getContents().trim();
						colC = row[2].getContents().trim();
						postcode = row[postcodeCol[i]].getContents().trim();
					}
					catch(ArrayIndexOutOfBoundsException ex){
						//do nothing - caused by an empty row in the spreadsheet
						//and will be handled gracefully by the if clause below
					}
					if ( null != colA && colA.length()>0 && null != colC && colC.length()>0 ){
						//System.out.println("Row="+j+"; colA="+colA+"; colC="+colC+"; postcode="+postcode);
						String siteName = colA+" - "+colC;
						//check for duplicates
						boolean duplicate = false;
						for ( int k=0, e=group.numSites(); k<e ; k++ ){
							if ( group.getSite(k).getSiteName().equals(siteName)){
								duplicate = true;
								break;
							}
						}
						if ( !duplicate ){
							System.out.println(siteName);
							Site site1 = new Site(siteName, "??", postcode, group);
							site1.addConsultant("Til Wykes");
							site1.addConsultant("Emmanuelle Peters");
							group.addSite(site1);
						}
					}
				}
			}
		}
	}

	private void forNineMonthDocOcc(DocumentOccurrence docOcc){
		docOcc.setScheduleTime(Integer.valueOf(274));
		docOcc.setScheduleUnits(TimeUnits.DAYS);
	}

	private void forEighteenMonthDocOcc(DocumentOccurrence docOcc){
		docOcc.setScheduleTime(Integer.valueOf(548));
		docOcc.setScheduleUnits(TimeUnits.DAYS);
	}

	protected void configureGroups(Factory factory, DataSet dataSet) throws Exception {
		//groups
		Group grp1 = (Group)factory.createGroup("001001");
		grp1.setLongName("Heart of England");
		addHoESites(grp1);

		Group grp2 = (Group)factory.createGroup("002001");
		grp2.setLongName("North West");
		addNWSites(grp2);

		Group grp3 = (Group)factory.createGroup("003001");
		grp3.setLongName("South London and South East");
		addSLSESites(grp3);

		dataSet.addGroup(grp1);
		dataSet.addGroup(grp2);
		dataSet.addGroup(grp3);

	}
}
