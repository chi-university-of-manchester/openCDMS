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

package org.psygrid.matisse;

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
import org.psygrid.outlook.TransformersFactory;

public class MatisseDataset {

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

	public MatisseDataset(){

	}

	public void remove(String saml, String repository)
	{
		try
		{
			RepositoryClient client = null;
			client = new RepositoryClient(new URL(repository));
			client.removePublishedDataSet(208201, "MTS", saml);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void getDataSet(String saml, String repository)
	{
		try
		{
			RepositoryClient client = null;
			client = new RepositoryClient(new URL(repository));
			DataSet dataset = client.getDataSet(40045L, saml);
		} catch (Exception ex)
		{
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

	public static DataSet createDataset()
	{
		Factory factory = new HibernateFactory();

		DataSet dataSet = factory.createDataset("Matisse", "Matisse");
		UnitWrapper.setFactory(new UnitFactory());
		UnitWrapper.instance().init(factory, dataSet);
		TransformersWrapper.setFactory(new TransformersFactory());
		TransformersWrapper.instance().init(factory, dataSet);
		ValidationRulesWrapper.setFactory(new ValidationRulesFactory());
		ValidationRulesWrapper.instance().init(factory, dataSet);

		dataSet.setScheduleStartQuestion("Please provide the date the client was recruited into the study:");

		dataSet.setExportSecurityActive(false);

		dataSet.setProjectCode("MTS");
		dataSet.setVersionNo("1.0.0");
		dataSet.setEslUsed(false);
		dataSet.setRandomizationRequired(false);
		dataSet.setSendMonthlySummaries(true);

		dataSet.setReviewReminderCount(0);

		//groups and sites - 4 sites per group
		Group grp1 = (Group)factory.createGroup("001001");
		grp1.addSite(new Site("Trust HQ, Central & North West London", "N0000692", "W9 2NW", grp1));
		grp1.addSite(new Site("Courtfield House", "N0000832", "W10 6DZ", grp1));
		grp1.addSite(new Site("Willesden Community Hospital", "N0000833", "NW10 3RY", grp1));
		grp1.addSite(new Site("The Gordon Hospital", "N0000834", "SW1V 2RH", grp1));

		//2 sites in this group had same address so only Royal Free Hospital represents 2 sites in reality
		Group grp2 = (Group)factory.createGroup("002001");
		grp2.addSite(new Site("Royal Free Hospital", "N0000155", "NW3 2QG", grp2));
		grp2.addSite(new Site("Highgate Centre Day Centre", "N0001556", "NW5 1JY", grp2));
		grp2.addSite(new Site("Hanley Road Day Centre", "N0001557", "N4 3DY", grp2));

		Group grp3 = (Group)factory.createGroup("003001");
		grp3.addSite(new Site("Callington Road Hospital", "N0000835", "BS4 5BJ", grp3));
		grp3.addSite(new Site("Hilview Lodge", "N0000336", "BA1 3NG", grp3));
		grp3.addSite(new Site("Sandalwood Court", "N0000337", "SN3 4WF", grp3));
		grp3.addSite(new Site("Green Lane Hospital", "N0000338", "SN10 5DS", grp3));

		Group grp4 = (Group)factory.createGroup("004001");
		grp4.addSite(new Site("Woodstock Link", "N0001054", "BT8 8BH", grp4));
		grp4.addSite(new Site("Centre for Psychotherapy", "N0001055", "BT8 8BH", grp4));

		//2 dummy sites for now, UKCRN does not have info ready
		grp4.addSite(new Site("Belfast-Site 3", "", "", grp4));
		grp4.addSite(new Site("Belfast-Site 4", "", "", grp4));

		dataSet.addGroup(grp1);
		dataSet.addGroup(grp2);
		dataSet.addGroup(grp3);
		dataSet.addGroup(grp4);

		ConsentFormGroup cfg = factory.createConsentFormGroup();
		cfg.setDescription("Main client consent");
		PrimaryConsentForm pcf = factory.createPrimaryConsentForm();
		pcf.setQuestion("Has the client (over 18) provided informed consent?");
		cfg.addConsentForm(pcf);
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
		Status statBaseline = factory.createStatus("Baseline", "Baseline completed", 6);
		statBaseline.setGenericState(GenericState.ACTIVE);
		Status stat12Month = factory.createStatus("12Month", "12 month follow-up completed", 7);
		stat12Month.setGenericState(GenericState.ACTIVE);
		Status stat24Month = factory.createStatus("24Month", "24 month follow-up completed", 8);
		stat24Month.setGenericState(GenericState.COMPLETED);
		Status statDeceased = factory.createStatus("Deceased", "Deceased", 9);
		statDeceased.setInactive(true);
		statDeceased.setGenericState(GenericState.LEFT);
		Status statWithdrew = factory.createStatus("Withdrew", "Withdrew", 10);
		statWithdrew.setInactive(true);
		statWithdrew.setGenericState(GenericState.INACTIVE);

		Status statInvalid = factory.createStatus("Invalid", "Invalid", 11);	//Record was added by mistake and shouldn't exist
		statInvalid.setInactive(true);
		statInvalid.setGenericState(GenericState.INVALID);

		statReferred.addStatusTransition(statScreenInelig);         //referred -> Screened; ineligible
		statReferred.addStatusTransition(statUnableToConsent);      //referred -> Unable to consent
		statReferred.addStatusTransition(statConsented);            //referred -> consented
		statReferred.addStatusTransition(statConsentRefused);       //referred -> consent refused
		statReferred.addStatusTransition(statClinicianWithdrew);    //referred -> clinician withdrew referral
		//statReferred.addStatusTransition(statBaseline);           //referred -> baseline completed
		//statReferred.addStatusTransition(stat12Month);           //referred -> 12 month completed
		//statReferred.addStatusTransition(stat24Month);           //referred -> 24 completed
		statReferred.addStatusTransition(statDeceased);             //referred -> deceased
		statReferred.addStatusTransition(statWithdrew);             //referred -> withdrew
		statReferred.addStatusTransition(statInvalid);             //referred -> invalid

		statConsented.addStatusTransition(statBaseline);          //consented -> baseline completed
		statConsented.addStatusTransition(stat12Month);          //consented -> 12 month completed
		statConsented.addStatusTransition(stat24Month);          //consented -> 24 month completed
		statConsented.addStatusTransition(statDeceased);            //consented -> deceased
		statConsented.addStatusTransition(statWithdrew);            //consented -> withdrew
		statConsented.addStatusTransition(statInvalid);            //consented -> invalid

		statBaseline.addStatusTransition(stat12Month);         //baseline completed -> 12 month completed
		statBaseline.addStatusTransition(stat24Month);         //baseline completed -> 24 month completed
		statBaseline.addStatusTransition(statDeceased);           //baseline completed -> deceased
		statBaseline.addStatusTransition(statWithdrew);           //baseline completed -> withdrew
		statBaseline.addStatusTransition(statInvalid);           //baseline completed -> invalid

		stat12Month.addStatusTransition(stat24Month);         //12 month completed -> 24 month completed
		stat12Month.addStatusTransition(statDeceased);           //12 month completed -> deceased
		stat12Month.addStatusTransition(statWithdrew);           //12 month completed -> withdrew
		stat12Month.addStatusTransition(statInvalid);           //12 month completed -> invalid

		stat24Month.addStatusTransition(statDeceased);             //24 month completed -> deceased
		stat24Month.addStatusTransition(statWithdrew);            //24 month completed -> withdrew
		stat24Month.addStatusTransition(statInvalid);            //24 month completed -> invalid

		dataSet.addStatus(statReferred);
		dataSet.addStatus(statScreenInelig);
		dataSet.addStatus(statUnableToConsent);
		dataSet.addStatus(statConsented);
		dataSet.addStatus(statConsentRefused);
		dataSet.addStatus(statClinicianWithdrew);
		dataSet.addStatus(statBaseline);
		dataSet.addStatus(stat12Month);
		dataSet.addStatus(stat24Month);
		dataSet.addStatus(statDeceased);
		dataSet.addStatus(statWithdrew);
		dataSet.addStatus(statInvalid);

		DocumentGroup baseline = factory.createDocumentGroup("Baseline");
		baseline.setDisplayText("Baseline - (Core assessments)");
		baseline.setAllowedRecordStatus(getStatuses(dataSet, GenericState.ACTIVE));
		//baseline.addAllowedRecordStatus(statReferred);
		baseline.setUpdateStatus(statBaseline);

		DocumentGroup twelveMonths = factory.createDocumentGroup("12 months");
		twelveMonths.setDisplayText("12 months");
		twelveMonths.addAllowedRecordStatus(statBaseline);
		twelveMonths.addAllowedRecordStatus(stat12Month);
		twelveMonths.addPrerequisiteGroup(baseline);
		twelveMonths.setUpdateStatus(stat12Month);

		DocumentGroup twentyFourMonths = factory.createDocumentGroup("24 months");
		twentyFourMonths.setDisplayText("24 months");
		twentyFourMonths.addAllowedRecordStatus(stat12Month);
		twentyFourMonths.addPrerequisiteGroup(twelveMonths);
		twentyFourMonths.setUpdateStatus(stat24Month);

		dataSet.addDocumentGroup(baseline);
		dataSet.addDocumentGroup(twelveMonths);
		dataSet.addDocumentGroup(twentyFourMonths);

		//-------------------------------------------------------------------
		//Documents/occurrences in the Baseline (Core assessments) group
		//-------------------------------------------------------------------

		//Baseline Audit form
		Document bas = BaselineAssessmentSchedule.createDocument(factory);
		dataSet.addDocument(bas);
		bas.addConsentFormGroup(cfg);
		DocumentOccurrence baselinePrimary =
			factory.createDocumentOccurrence("Baseline (Primary)");
		baselinePrimary.setDisplayText("Baseline (Primary)");
		bas.addOccurrence(baselinePrimary);
		baselinePrimary.setDocumentGroup(baseline);

		DocumentOccurrence baselineSecondary =
			factory.createDocumentOccurrence("Baseline (Secondary)");
		baselineSecondary.setDisplayText("Baseline (Secondary)");
		bas.addOccurrence(baselineSecondary);
		baselineSecondary.setDocumentGroup(baseline);

		//monthly email schedule times
		createBaselineReminders(baselinePrimary, factory);
		createBaselineReminders(baselineSecondary, factory);

		Document baselineQuestionnaire = BaselineQuestionnaire.createDocument(factory);
		dataSet.addDocument(baselineQuestionnaire);
		baselineQuestionnaire.addConsentFormGroup(cfg);
		DocumentOccurrence baselineQuestionnairePrimary =
			factory.createDocumentOccurrence("Baseline (Primary)");
		baselineQuestionnairePrimary.setDisplayText("Baseline (Primary)");
		baselineQuestionnaire.addOccurrence(baselineQuestionnairePrimary);
		baselineQuestionnairePrimary.setDocumentGroup(baseline);

		DocumentOccurrence baselineQuestionnaireSecondary =
			factory.createDocumentOccurrence("Baseline (Secondary)");
		baselineQuestionnaire.addOccurrence(baselineQuestionnaireSecondary);
		baselineQuestionnaireSecondary.setDisplayText("Baseline (Secondary)");
		baselineQuestionnaireSecondary.setDocumentGroup(baseline);

		//monthly email schedule times
		createBaselineReminders(baselineQuestionnairePrimary, factory);
		createBaselineReminders(baselineQuestionnaireSecondary, factory);

		Document qualityOfLife = QualityOfLife.createDocument(factory);
		dataSet.addDocument(qualityOfLife);
		qualityOfLife.addConsentFormGroup(cfg);
		DocumentOccurrence qualityOfLifePrimary =
			factory.createDocumentOccurrence("Baseline (Primary)");
		qualityOfLifePrimary.setDisplayText("Baseline (Primary)");
		qualityOfLife.addOccurrence(qualityOfLifePrimary);
		qualityOfLifePrimary.setDocumentGroup(baseline);

		DocumentOccurrence qualityOfLifeSecondary =
			factory.createDocumentOccurrence("Baseline (Secondary)");
		qualityOfLifeSecondary.setDisplayText("Baseline (Secondary)");
		qualityOfLife.addOccurrence(qualityOfLifeSecondary);
		qualityOfLifeSecondary.setDocumentGroup(baseline);

		//monthly baseline reminders
		createBaselineReminders(qualityOfLifePrimary, factory);
		createBaselineReminders(qualityOfLifeSecondary, factory);

		Document socialFunctioning = SocialFunctioning.createDocument(factory);
		dataSet.addDocument(socialFunctioning);
		socialFunctioning.addConsentFormGroup(cfg);
		DocumentOccurrence socialFunctioningPrimary =
			factory.createDocumentOccurrence("Baseline (Primary)");
		socialFunctioningPrimary.setDisplayText("Baseline (Primary)");
		socialFunctioning.addOccurrence(socialFunctioningPrimary);
		socialFunctioningPrimary.setDocumentGroup(baseline);

		DocumentOccurrence socialFunctioningSecondary =
			factory.createDocumentOccurrence("Baseline (Secondary)");
		socialFunctioningSecondary.setDisplayText("Baseline (Secondary)");
		socialFunctioning.addOccurrence(socialFunctioningSecondary);
		socialFunctioningSecondary.setDocumentGroup(baseline);

		//monthly baseline reminders
		createBaselineReminders(socialFunctioningPrimary, factory);
		createBaselineReminders(socialFunctioningSecondary, factory);

		Document generalWellBeing = GeneralWellBeing.createDocument(factory);
		dataSet.addDocument(generalWellBeing);
		generalWellBeing.addConsentFormGroup(cfg);
		DocumentOccurrence generalWellBeingPrimary =
			factory.createDocumentOccurrence("Baseline (Primary)");
		generalWellBeingPrimary.setDisplayText("Baseline (Primary)");
		generalWellBeing.addOccurrence(generalWellBeingPrimary);
		generalWellBeingPrimary.setDocumentGroup(baseline);
		DocumentOccurrence generalWellBeingSecondary =
			factory.createDocumentOccurrence("Baseline (Secondary)");
		generalWellBeingSecondary.setDisplayText("Baseline (Secondary)");
		generalWellBeing.addOccurrence(generalWellBeingSecondary);
		generalWellBeingSecondary.setDocumentGroup(baseline);

		//monthly baseline reminders
		createBaselineReminders(generalWellBeingPrimary, factory);
		createBaselineReminders(generalWellBeingSecondary, factory);

		Document processFactors = ProcessFactors.createDocument(factory);
		dataSet.addDocument(processFactors);
		processFactors.addConsentFormGroup(cfg);
		DocumentOccurrence processFactorsPrimary =
			factory.createDocumentOccurrence("Baseline (Primary)");
		processFactorsPrimary.setDisplayText("Baseline (Primary)");
		processFactors.addOccurrence(processFactorsPrimary);
		processFactorsPrimary.setDocumentGroup(baseline);
		DocumentOccurrence processFactorsSecondary =
			factory.createDocumentOccurrence("Baseline (Secondary)");
		processFactorsSecondary.setDisplayText("Baseline (Secondary)");
		processFactors.addOccurrence(processFactorsSecondary);
		processFactorsSecondary.setDocumentGroup(baseline);

		//monthly baseline reminders
		createBaselineReminders(processFactorsPrimary, factory);
		createBaselineReminders(processFactorsSecondary, factory);

		Document adultServiceUseSchedule = AdultServiceUseSchedule.createDocument(factory);
		dataSet.addDocument(adultServiceUseSchedule);
		adultServiceUseSchedule.addConsentFormGroup(cfg);
		DocumentOccurrence adultServiceUseSchedulePrimary =
			factory.createDocumentOccurrence("Baseline (Primary)");
		adultServiceUseSchedulePrimary.setDisplayText("Baseline (Primary)");
		adultServiceUseSchedule.addOccurrence(adultServiceUseSchedulePrimary);
		adultServiceUseSchedulePrimary.setDocumentGroup(baseline);
		DocumentOccurrence adultServiceUseScheduleSecondary =
			factory.createDocumentOccurrence("Baseline (Secondary)");
		adultServiceUseScheduleSecondary.setDisplayText("Baseline (Secondary)");
		adultServiceUseSchedule.addOccurrence(adultServiceUseScheduleSecondary);
		adultServiceUseScheduleSecondary.setDocumentGroup(baseline);

		//monthly baseline reminders
		createBaselineReminders(adultServiceUseSchedulePrimary, factory);
		createBaselineReminders(adultServiceUseScheduleSecondary, factory);

		Document panss = Panss.createDocument(factory);
		dataSet.addDocument(panss);
		panss.addConsentFormGroup(cfg);
		DocumentOccurrence panssPrimary =
			factory.createDocumentOccurrence("Baseline (Primary)");
		panssPrimary.setDisplayText("Baseline (Primary)");
		panss.addOccurrence(panssPrimary);
		panssPrimary.setDocumentGroup(baseline);
		DocumentOccurrence panssSecondary =
			factory.createDocumentOccurrence("Baseline (Secondary)");
		panssSecondary.setDisplayText("Baseline (Secondary)");
		panss.addOccurrence(panssSecondary);
		panssSecondary.setDocumentGroup(baseline);

		//monthly baseline reminders
		createBaselineReminders(panssPrimary, factory);
		createBaselineReminders(panssSecondary, factory);

		Document moriskyScale = MoriskyScale.createDocument(factory);
		dataSet.addDocument(moriskyScale);
		moriskyScale.addConsentFormGroup(cfg);
		DocumentOccurrence moriskyScalePrimary =
			factory.createDocumentOccurrence("Baseline (Primary)");
		moriskyScalePrimary.setDisplayText("Baseline (Primary)");
		moriskyScalePrimary.setDocumentGroup(baseline);
		moriskyScale.addOccurrence(moriskyScalePrimary);
		DocumentOccurrence moriskyScaleSecondary =
			factory.createDocumentOccurrence("Baseline (Secondary)");
		moriskyScaleSecondary.setDisplayText("Baseline (Secondary)");
		moriskyScaleSecondary.setDocumentGroup(baseline);
		moriskyScale.addOccurrence(moriskyScaleSecondary);

		//monthly baseline reminders
		createBaselineReminders(moriskyScalePrimary, factory);
		createBaselineReminders(moriskyScaleSecondary, factory);

		Document satisfactionWithCare = SatisfactionWithCare.createDocument(factory);
		dataSet.addDocument(satisfactionWithCare);
		satisfactionWithCare.addConsentFormGroup(cfg);
		DocumentOccurrence satisfactionWithCarePrimary =
			factory.createDocumentOccurrence("Baseline (Primary)");
		satisfactionWithCarePrimary.setDisplayText("Baseline (Primary)");
		satisfactionWithCarePrimary.setDocumentGroup(baseline);
		satisfactionWithCare.addOccurrence(satisfactionWithCarePrimary);
		DocumentOccurrence satisfactionWithCareSecondary =
			factory.createDocumentOccurrence("Baseline (Secondary)");
		satisfactionWithCareSecondary.setDisplayText("Baseline (Secondary)");
		satisfactionWithCare.addOccurrence(satisfactionWithCareSecondary);
		satisfactionWithCareSecondary.setDocumentGroup(baseline);

		//monthly baseline reminders
		createBaselineReminders(satisfactionWithCarePrimary, factory);
		createBaselineReminders(satisfactionWithCareSecondary, factory);

		Document gaf = GAF.createDocument(factory);
		dataSet.addDocument(gaf);
		gaf.addConsentFormGroup(cfg);
		DocumentOccurrence gafPrimary =
			factory.createDocumentOccurrence("Baseline (Primary)");
		gafPrimary.setDisplayText("Baseline (Primary)");
		gafPrimary.setDocumentGroup(baseline);
		gaf.addOccurrence(gafPrimary);
		DocumentOccurrence gafSecondary =
			factory.createDocumentOccurrence("Baseline (Secondary)");
		gafSecondary.setDisplayText("Baseline (Secondary)");
		gafSecondary.setDocumentGroup(baseline);
		gaf.addOccurrence(gafSecondary);

		//monthly baseline reminders
		createBaselineReminders(gafPrimary, factory);
		createBaselineReminders(gafSecondary, factory);


//		//-------------------------------------------------------------------
//		//Documents/occurrences in the 12 month group
//		//-------------------------------------------------------------------

		DocumentOccurrence qualityOfLife12MonthPrimary =
			factory.createDocumentOccurrence("12 Month (Primary)");
		qualityOfLife12MonthPrimary.setDisplayText("12 Month (Primary)");
		qualityOfLife.addOccurrence(qualityOfLife12MonthPrimary);
		qualityOfLife12MonthPrimary.setDocumentGroup(twelveMonths);
		DocumentOccurrence qualityOfLife12MonthSecondary =
			factory.createDocumentOccurrence("12 Month (Secondary)");
		qualityOfLife12MonthSecondary.setDisplayText("12 Month (Secondary)");
		qualityOfLife.addOccurrence(qualityOfLife12MonthSecondary);
		qualityOfLife12MonthSecondary.setDocumentGroup(twelveMonths);

		//email settings
		createTwelveMonthReminders(qualityOfLife12MonthPrimary, factory);
		createTwelveMonthReminders(qualityOfLife12MonthSecondary, factory);

		DocumentOccurrence socialFunctioning12MonthPrimary =
			factory.createDocumentOccurrence("12 Month (Primary)");
		socialFunctioning12MonthPrimary.setDisplayText("12 Month (Primary)");
		socialFunctioning.addOccurrence(socialFunctioning12MonthPrimary);
		socialFunctioning12MonthPrimary.setDocumentGroup(twelveMonths);

		DocumentOccurrence socialFunctioning12MonthSecondary =
			factory.createDocumentOccurrence("12 Month (Secondary)");
		socialFunctioning12MonthSecondary.setDisplayText("12 Month (Secondary)");
		socialFunctioning.addOccurrence(socialFunctioning12MonthSecondary);
		socialFunctioning12MonthSecondary.setDocumentGroup(twelveMonths);

		//eamil settings
		createTwelveMonthReminders(socialFunctioning12MonthPrimary, factory);
		createTwelveMonthReminders(socialFunctioning12MonthSecondary, factory);

		DocumentOccurrence generalWellBeing12MonthPrimary =
			factory.createDocumentOccurrence("12 Month (Primary)");
		generalWellBeing12MonthPrimary.setDisplayText("12 Month (Primary)");
		generalWellBeing.addOccurrence(generalWellBeing12MonthPrimary);
		generalWellBeing12MonthPrimary.setDocumentGroup(twelveMonths);
		DocumentOccurrence generalWellBeing12MonthSecondary =
			factory.createDocumentOccurrence("12 Month (Secondary)");
		generalWellBeing12MonthSecondary.setDisplayText("12 Month (Secondary)");
		generalWellBeing.addOccurrence(generalWellBeing12MonthSecondary);
		generalWellBeing12MonthSecondary.setDocumentGroup(twelveMonths);

		//email settings
		createTwelveMonthReminders(generalWellBeing12MonthPrimary, factory);
		createTwelveMonthReminders(generalWellBeing12MonthSecondary, factory);

		DocumentOccurrence adultServiceUseSchedule12MonthPrimary =
			factory.createDocumentOccurrence("12 Month (Primary)");
		adultServiceUseSchedule12MonthPrimary.setDisplayText("12 Month (Primary)");
		adultServiceUseSchedule.addOccurrence(adultServiceUseSchedule12MonthPrimary);
		adultServiceUseSchedule12MonthPrimary.setDocumentGroup(twelveMonths);
		DocumentOccurrence adultServiceUseSchedule12MonthSecondary =
			factory.createDocumentOccurrence("12 Month (Secondary)");
		adultServiceUseSchedule12MonthSecondary.setDisplayText("12 Month (Secondary)");
		adultServiceUseSchedule.addOccurrence(adultServiceUseSchedule12MonthSecondary);
		adultServiceUseSchedule12MonthSecondary.setDocumentGroup(twelveMonths);

		//email settings
		createTwelveMonthReminders(adultServiceUseSchedule12MonthPrimary, factory);
		createTwelveMonthReminders(adultServiceUseSchedule12MonthSecondary, factory);

		DocumentOccurrence panss12MonthPrimary =
			factory.createDocumentOccurrence("12 Month (Primary)");
		panss12MonthPrimary.setDisplayText("12 Month (Primary)");
		panss12MonthPrimary.setDocumentGroup(twelveMonths);
		panss.addOccurrence(panss12MonthPrimary);
		DocumentOccurrence panss12MonthSecondary =
			factory.createDocumentOccurrence("12 Month (Secondary)");
		panss12MonthSecondary.setDisplayText("12 Monday (Secondary)");
		panss.addOccurrence(panss12MonthSecondary);
		panss12MonthSecondary.setDocumentGroup(twelveMonths);

		//email settings
		createTwelveMonthReminders(panss12MonthPrimary, factory);
		createTwelveMonthReminders(panss12MonthSecondary, factory);

		DocumentOccurrence moriskyScale12MonthPrimary =
			factory.createDocumentOccurrence("12 Month (Primary)");
		moriskyScale12MonthPrimary.setDisplayText("12 Month (Primary)");
		moriskyScale12MonthPrimary.setDocumentGroup(twelveMonths);
		moriskyScale.addOccurrence(moriskyScale12MonthPrimary);
		DocumentOccurrence moriskyScale12MonthSecondary =
			factory.createDocumentOccurrence("12 Month (Secondary)");
		moriskyScale12MonthSecondary.setDisplayText("12 Month (Secondary)");
		moriskyScale.addOccurrence(moriskyScale12MonthSecondary);
		moriskyScale12MonthSecondary.setDocumentGroup(twelveMonths);

		//email settings
		createTwelveMonthReminders(moriskyScale12MonthPrimary, factory);
		createTwelveMonthReminders(moriskyScale12MonthSecondary, factory);

		DocumentOccurrence satisfactionWithCare12MonthPrimary =
			factory.createDocumentOccurrence("12 Month (Primary)");
		satisfactionWithCare12MonthPrimary.setDisplayText("12 Month (Primary)");
		satisfactionWithCare12MonthPrimary.setDocumentGroup(twelveMonths);
		satisfactionWithCare.addOccurrence(satisfactionWithCare12MonthPrimary);
		DocumentOccurrence satisfactionWithCare12MonthSecondary =
			factory.createDocumentOccurrence("12 Month (Secondary)");
		satisfactionWithCare12MonthSecondary.setDisplayText("12 Month (Secondary)");
		satisfactionWithCare.addOccurrence(satisfactionWithCare12MonthSecondary);
		satisfactionWithCare12MonthSecondary.setDocumentGroup(twelveMonths);

		//email settings
		createTwelveMonthReminders(satisfactionWithCare12MonthPrimary, factory);
		createTwelveMonthReminders(satisfactionWithCare12MonthSecondary, factory);

		DocumentOccurrence gaf12MonthPrimary =
			factory.createDocumentOccurrence("12 Month (Primary)");
		gaf12MonthPrimary.setDisplayText("12 Month (Primary)");
		gaf.addOccurrence(gaf12MonthPrimary);
		gaf12MonthPrimary.setDocumentGroup(twelveMonths);
		DocumentOccurrence gaf12MonthSecondary =
			factory.createDocumentOccurrence("12 Month (Secondary)");
		gaf12MonthSecondary.setDisplayText("12 Month (Secondary)");
		gaf.addOccurrence(gaf12MonthSecondary);
		gaf12MonthSecondary.setDocumentGroup(twelveMonths);

		//email settings
		createTwelveMonthReminders(gaf12MonthPrimary, factory);
		createTwelveMonthReminders(gaf12MonthSecondary, factory);

		Document followUpQuestionnaire = FollowUpQuestionnaire.createDocument(factory);
		dataSet.addDocument(followUpQuestionnaire);
		followUpQuestionnaire.addConsentFormGroup(cfg);
		DocumentOccurrence fuq12MonthPrimary =
			factory.createDocumentOccurrence("12 Month (Primary)");
		fuq12MonthPrimary.setDisplayText("12 Month (Primary)");
		followUpQuestionnaire.addOccurrence(fuq12MonthPrimary);
		fuq12MonthPrimary.setDocumentGroup(twelveMonths);
		DocumentOccurrence fuq12MonthSecondary =
			factory.createDocumentOccurrence("12 Month (Secondary)");
		fuq12MonthSecondary.setDisplayText("12 Month (Secondary)");
		followUpQuestionnaire.addOccurrence(fuq12MonthSecondary);
		fuq12MonthSecondary.setDocumentGroup(twelveMonths);

		//email settings
		createTwelveMonthReminders(fuq12MonthPrimary, factory);
		createTwelveMonthReminders(fuq12MonthSecondary, factory);


		//-------------------------------------------------------------------
		//Documents/occurrences in the 24 month group
		//-------------------------------------------------------------------

		DocumentOccurrence qualityOfLife24MonthPrimary =
			factory.createDocumentOccurrence("24 Month (Primary)");
		qualityOfLife24MonthPrimary.setDisplayText("24 Month (Primary)");
		qualityOfLife.addOccurrence(qualityOfLife24MonthPrimary);
		qualityOfLife24MonthPrimary.setDocumentGroup(twentyFourMonths);
		DocumentOccurrence qualityOfLife24MonthSecondary =
			factory.createDocumentOccurrence("24 Month (Secondary)");
		qualityOfLife24MonthSecondary.setDisplayText("24 Month (Secondary)");
		qualityOfLife.addOccurrence(qualityOfLife24MonthSecondary);
		qualityOfLife24MonthSecondary.setDocumentGroup(twentyFourMonths);

		//email settings
		createTwentyFourMonthReminders(qualityOfLife24MonthPrimary, factory);
		createTwentyFourMonthReminders(qualityOfLife24MonthSecondary, factory);

		DocumentOccurrence socialFunctioning24MonthPrimary =
			factory.createDocumentOccurrence("24 Month (Primary)");
		socialFunctioning24MonthPrimary.setDisplayText("24 Month (Primary)");
		socialFunctioning.addOccurrence(socialFunctioning24MonthPrimary);
		socialFunctioning24MonthPrimary.setDocumentGroup(twentyFourMonths);

		DocumentOccurrence socialFunctioning24MonthSecondary =
			factory.createDocumentOccurrence("24 Month (Secondary)");
		socialFunctioning24MonthSecondary.setDisplayText("24 Month (Secondary)");
		socialFunctioning.addOccurrence(socialFunctioning24MonthSecondary);
		socialFunctioning24MonthSecondary.setDocumentGroup(twentyFourMonths);

		//email settings
		createTwentyFourMonthReminders(socialFunctioning24MonthPrimary, factory);
		createTwentyFourMonthReminders(socialFunctioning24MonthSecondary, factory);

		DocumentOccurrence generalWellBeing24MonthPrimary =
			factory.createDocumentOccurrence("24 Month (Primary)");
		generalWellBeing24MonthPrimary.setDisplayText("24 Month (Primary)");
		generalWellBeing.addOccurrence(generalWellBeing24MonthPrimary);
		generalWellBeing24MonthPrimary.setDocumentGroup(twentyFourMonths);
		DocumentOccurrence generalWellBeing24MonthSecondary =
			factory.createDocumentOccurrence("24 Month (Secondary)");
		generalWellBeing24MonthSecondary.setDisplayText("24 Month (Secondary)");
		generalWellBeing.addOccurrence(generalWellBeing24MonthSecondary);
		generalWellBeing24MonthSecondary.setDocumentGroup(twentyFourMonths);

		//email settings
		createTwentyFourMonthReminders(generalWellBeing24MonthPrimary, factory);
		createTwentyFourMonthReminders(generalWellBeing24MonthSecondary, factory);

		DocumentOccurrence adultServiceUseSchedule24MonthPrimary =
			factory.createDocumentOccurrence("24 Month (Primary)");
		adultServiceUseSchedule24MonthPrimary.setDisplayText("24 Month (Primary)");
		adultServiceUseSchedule.addOccurrence(adultServiceUseSchedule24MonthPrimary);
		adultServiceUseSchedule24MonthPrimary.setDocumentGroup(twentyFourMonths);
		DocumentOccurrence adultServiceUseSchedule24MonthSecondary =
			factory.createDocumentOccurrence("24 Month (Secondary)");
		adultServiceUseSchedule24MonthSecondary.setDisplayText("24 Month (Secondary)");
		adultServiceUseSchedule.addOccurrence(adultServiceUseSchedule24MonthSecondary);
		adultServiceUseSchedule24MonthSecondary.setDocumentGroup(twentyFourMonths);

		//email settings
		createTwentyFourMonthReminders(adultServiceUseSchedule24MonthPrimary, factory);
		createTwentyFourMonthReminders(adultServiceUseSchedule24MonthSecondary, factory);

		DocumentOccurrence panss24MonthPrimary =
			factory.createDocumentOccurrence("24 Month (Primary)");
		panss24MonthPrimary.setDisplayText("24 Month (Primary)");
		panss.addOccurrence(panss24MonthPrimary);
		panss24MonthPrimary.setDocumentGroup(twentyFourMonths);
		DocumentOccurrence panss24MonthSecondary =
			factory.createDocumentOccurrence("24 Month (Secondary)");
		panss24MonthSecondary.setDisplayText("24 Month (Secondary)");
		panss.addOccurrence(panss24MonthSecondary);
		panss24MonthSecondary.setDocumentGroup(twentyFourMonths);

		//email settings
		createTwentyFourMonthReminders(panss24MonthPrimary, factory);
		createTwentyFourMonthReminders(panss24MonthSecondary, factory);

		DocumentOccurrence moriskyScale24MonthPrimary =
			factory.createDocumentOccurrence("24 Month (Primary)");
		moriskyScale24MonthPrimary.setDisplayText("24 Month (Primary)");
		moriskyScale24MonthPrimary.setDocumentGroup(twentyFourMonths);
		moriskyScale.addOccurrence(moriskyScale24MonthPrimary);
		DocumentOccurrence moriskyScale24MonthSecondary =
			factory.createDocumentOccurrence("24 Month (Secondary)");
		moriskyScale24MonthSecondary.setDisplayText("24 Month (Secondary)");
		moriskyScale.addOccurrence(moriskyScale24MonthSecondary);
		moriskyScale24MonthSecondary.setDocumentGroup(twentyFourMonths);

		//email settings
		createTwentyFourMonthReminders(moriskyScale24MonthPrimary, factory);
		createTwentyFourMonthReminders(moriskyScale24MonthSecondary, factory);

		DocumentOccurrence satisfactionWithCare24MonthPrimary =
			factory.createDocumentOccurrence("24 Month (Primary)");
		satisfactionWithCare24MonthPrimary.setDisplayText("24 Month (Primary)");
		satisfactionWithCare24MonthPrimary.setDocumentGroup(twentyFourMonths);
		satisfactionWithCare.addOccurrence(satisfactionWithCare24MonthPrimary);
		DocumentOccurrence satisfactionWithCare24MonthSecondary =
			factory.createDocumentOccurrence("24 Month (Secondary)");
		satisfactionWithCare24MonthSecondary.setDisplayText("24 Month (Secondary)");
		satisfactionWithCare.addOccurrence(satisfactionWithCare24MonthSecondary);
		satisfactionWithCare24MonthSecondary.setDocumentGroup(twentyFourMonths);

		//monthly emails
		createTwentyFourMonthReminders(satisfactionWithCare24MonthPrimary, factory);
		createTwentyFourMonthReminders(satisfactionWithCare24MonthSecondary, factory);

		DocumentOccurrence gaf24MonthPrimary =
			factory.createDocumentOccurrence("24 Month (Primary)");
		gaf24MonthPrimary.setDisplayText("24 Month (Primary)");
		gaf.addOccurrence(gaf24MonthPrimary);
		gaf24MonthPrimary.setDocumentGroup(twentyFourMonths);
		DocumentOccurrence gaf24MonthSecondary =
			factory.createDocumentOccurrence("24 Month (Secondary)");
		gaf24MonthSecondary.setDisplayText("24 Month (Secondary)");
		gaf.addOccurrence(gaf24MonthSecondary);
		gaf24MonthSecondary.setDocumentGroup(twentyFourMonths);

		//monthly emails
		createTwentyFourMonthReminders(gaf24MonthPrimary, factory);
		createTwentyFourMonthReminders(gaf24MonthSecondary, factory);

		DocumentOccurrence fuq24MonthPrimary =
			factory.createDocumentOccurrence("24 Month (Primary)");
		fuq24MonthPrimary.setDisplayText("24 Month (Primary)");
		followUpQuestionnaire.addOccurrence(fuq24MonthPrimary);
		fuq24MonthPrimary.setDocumentGroup(twentyFourMonths);
		DocumentOccurrence fuq24MonthSecondary =
			factory.createDocumentOccurrence("24 Month (Secondary)");
		fuq24MonthSecondary.setDisplayText("24 Month (Secondary)");
		followUpQuestionnaire.addOccurrence(fuq24MonthSecondary);
		fuq24MonthSecondary.setDocumentGroup(twentyFourMonths);

		//monthly emails
		createTwentyFourMonthReminders(fuq24MonthPrimary, factory);
		createTwentyFourMonthReminders(fuq24MonthSecondary, factory);

		return dataSet;
	}

	public static void createReports(DataSet ds, String saml) throws Exception {
		//Management reports
		IReport ciRecruitmentReport = Reports.ciRecruitmentReport(ds);
		IReport londonRecruitment = Reports.recruitmentInLondonReport(ds);
		IReport camdenRecruitment = Reports.recruitmentInCamdenReport(ds);
		IReport westEnglandRecruitment = Reports.recruitmentInWestEnglandReport(ds);
		IReport northernIrelandRecruitment = Reports.recruitmentInNorthernIrelandReport(ds);

		IReport ukCRNReport	= Reports.ukCRNReport(ds);
		IReport stdCodeStatusReport = Reports.stdCodeStatusReport(ds);
		IReport basicStatsReport = Reports.basicStatisticsReport(ds);

		ReportsClient client = new ReportsClient();
		client.saveReport(ciRecruitmentReport, saml);
		client.saveReport(londonRecruitment, saml);
		client.saveReport(camdenRecruitment, saml);
		client.saveReport(westEnglandRecruitment, saml);
		client.saveReport(northernIrelandRecruitment, saml);
		client.saveReport(ukCRNReport, saml);
		client.saveReport(stdCodeStatusReport, saml);
		client.saveReport(basicStatsReport, saml);
	}

	public static void createBaselineReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(30));
		occurrence.setScheduleUnits(TimeUnits.DAYS);
	}

	public static void createTwelveMonthReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(397));
		occurrence.setScheduleUnits(TimeUnits.DAYS);
	}

	public static void createTwentyFourMonthReminders(DocumentOccurrence occurrence, Factory factory){
		occurrence.setScheduleTime(new Integer(762));
		occurrence.setScheduleUnits(TimeUnits.DAYS);
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
}