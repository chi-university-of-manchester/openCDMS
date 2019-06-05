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

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class AdultServiceUseSchedule extends AssessmentForm {

	public static Document createDocument(Factory factory){
		

        Document doc = factory.createDocument("Adult Service Use Schedule",
                "Adult Service Use Schedule");
        createDocumentStatuses(factory, doc);
        
        addAccommodationSection(doc, factory, 1);
        addEmploymentSection(doc, factory, 2);
        addHospitalSection(doc, factory, 3);
        addCommunityServices(doc, factory, 4);
        addPsychotropicMedication(doc, factory, 5);
        addCriminalJustice(doc, factory, 6);
        
        return doc;
        
	}
	
	public static void addAccommodationSection(Document  doc, Factory factory, int sectionNumber){

        ValidationRule positiveNumber = ValidationRulesWrapper.instance().getRule("Positive");
        ValidationRule postCodeRule = ValidationRulesWrapper.instance().getRule("Validation of UK postcodes");

        Section accommodationSec = factory.createSection("Accommodation", "Accommodation");
        doc.addSection(accommodationSec);
        accommodationSec.setDisplayText("Accommodation");
        SectionOccurrence accommodationOcc = factory.createSectionOccurrence("Accommodation section occurrence");
        accommodationSec.addOccurrence(accommodationOcc);
        
        CompositeEntry accommodation = factory.createComposite("Accommodation", "In the last 12 months (or since last"
                + " interivew if follow-up), what type of accommodation have you lived in?");
        doc.addEntry(accommodation);
        accommodation.setSection(accommodationSec);
        
        EntryStatus defaultStatus = EntryStatus.MANDATORY;
        accommodation.setEntryStatus(defaultStatus);
        
        OptionEntry accommodationType = factory.createOptionEntry("Accommodation type", "Type of accommodation");
        accommodation.addEntry(accommodationType);
        accommodationType.setSection(accommodationSec);
        
        Option ownerOccupied = factory.createOption("Owner occupied", "Owner occupied", 1);
        accommodationType.addOption(ownerOccupied);

        Option privatelyOwned = factory.createOption("Privately rented", "Privately rented", 2);
        accommodationType.addOption(privatelyOwned);
        
        Option localAuthority = factory.createOption("Local authority/housing association rented", "Local authority/housing association rented", 3);
        accommodationType.addOption(localAuthority);

        Option bedBreakfast = factory.createOption("Bed & breakfast, boarding house, hotel", "Bed & breakfast, boarding house, hotel", 4);
        accommodationType.addOption(bedBreakfast);
        
        Option homelessHostel = factory.createOption("Homeless: hostel shelter or refuge", "Homeless: hostel shelter or refuge", 5);
        accommodationType.addOption(homelessHostel);
        
        Option homelessStreets = factory.createOption("Homeless: living on the streets", "Homeless: living on the streets", 6);
        accommodationType.addOption(homelessStreets);

        Option staffedAccomm = factory.createOption("Staffed accommodation", "Staffed accommodation", 7);
        accommodationType.addOption(staffedAccomm);

        Option other = factory.createOption("Other - specify", "Other - specify", 8);
        other.setTextEntryAllowed(true);
        accommodationType.addOption(other);
        
        NumericEntry numWeeks = factory.createNumericEntry(
                "Number of weeks",
                "Number of weeks"); 
        accommodation.addEntry(numWeeks);
        numWeeks.addUnit(UnitWrapper.instance().getUnit("weeks"));
        numWeeks.setSection(accommodationSec);
        numWeeks.addValidationRule(positiveNumber);
        
        TextEntry details = factory.createTextEntry("Details", "Details");
        accommodation.addEntry(details);
        details.setSection(accommodationSec);
        
        NumericEntry rentAmount = factory.createNumericEntry("Rent",
                "If living in domestic accommodation (1-3) above, how much is the total"
                        + " rent/mortgage per month (irrespective of who pays for it) (�) ", EntryStatus.OPTIONAL);
        doc.addEntry(rentAmount);
        rentAmount.setSection(accommodationSec);
        createOptionDependent(factory, ownerOccupied, rentAmount);
        createOptionDependent(factory, privatelyOwned, rentAmount);
        createOptionDependent(factory, localAuthority, rentAmount);
        
        TextEntry postCode = factory.createTextEntry("Postcode", "Postcode of current accommodation");
        doc.addEntry(postCode);
        postCode.setEntryStatus(EntryStatus.OPTIONAL);
        postCode.setSection(accommodationSec);
        postCode.addValidationRule(postCodeRule);
        createOptionDependent(factory, ownerOccupied, postCode);
        createOptionDependent(factory, privatelyOwned, postCode);
        createOptionDependent(factory, localAuthority, postCode);
        
	}
	
	 public static void addEmploymentSection(Document  doc, Factory factory, int sectionNumber){
        Section employmentSec = factory.createSection("Employment", "Employment");
        doc.addSection(employmentSec);
        SectionOccurrence employmentOcc = factory.createSectionOccurrence("Employment section occurrence");
        employmentSec.addOccurrence(employmentOcc);
        
        OptionEntry employment = factory.createOptionEntry("Occupational status", "Current occupational status:");
        doc.addEntry(employment);
        employment.setSection(employmentSec);
        
        Option employment1 = factory.createOption("Full-time employment (30+ hours p.w.)", 1);
        Option employment2 = factory.createOption("Part-time employment (<30 hours p.w.)", 2);
        
        employment.addOption(employment1);
        employment.addOption(employment2);
        employment.addOption(factory.createOption("Full-time student", 3));
        employment.addOption(factory.createOption("Voluntary worker", 4));
        employment.addOption(factory.createOption("Unemployed & looking for work", 5));
        employment.addOption(factory.createOption("Unemployed & not looking for work (e.g. housewife/husband)", 6));
        employment.addOption(factory.createOption("Unemployed & unable to work for medical reasons", 7));
        employment.addOption(factory.createOption("Medically retired", 8));
        employment.addOption(factory.createOption("Retired", 9));
        Option employment10 = factory.createOption("Employed & currently unable to work", 10);
        employment.addOption(employment10);
        
        TextEntry jobTitle = factory.createTextEntry("Job Title", "If employed: What is your job title?");
        doc.addEntry(jobTitle);
        jobTitle.setSection(employmentSec);
        jobTitle.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, employment1, jobTitle);
        createOptionDependent(factory, employment2, jobTitle);
        createOptionDependent(factory, employment10, jobTitle);
        
        NumericEntry numHoursWeek = factory.createNumericEntry("Num hours", "Number of hours worked per week");
        doc.addEntry(numHoursWeek);
        numHoursWeek.setSection(employmentSec);
        numHoursWeek.addUnit(UnitWrapper.instance().getUnit("hours"));
        numHoursWeek.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, employment1, numHoursWeek);
        createOptionDependent(factory, employment2, numHoursWeek);
        createOptionDependent(factory, employment10, numHoursWeek);
        
        NumericEntry absenceFromWork = factory.createNumericEntry("Work Absence", "Time absent from work due " +
                "to illness over last twelve months (or since last interview if follow-up)");
        doc.addEntry(absenceFromWork);
        absenceFromWork.setSection(employmentSec);
        absenceFromWork.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, employment1, absenceFromWork);
        createOptionDependent(factory, employment2, absenceFromWork);
        createOptionDependent(factory, employment10, absenceFromWork);
        absenceFromWork.addUnit(UnitWrapper.instance().getUnit("days"));
        absenceFromWork.addUnit(UnitWrapper.instance().getUnit("hours"));
		
		OptionEntry grossPay = factory.createOptionEntry("Gross Pay", "Gross pay per year (before-tax)");
		doc.addEntry(grossPay);
		grossPay.setEntryStatus(EntryStatus.DISABLED);
		grossPay.setSection(employmentSec);
		grossPay.setOptionCodesDisplayed(true);
		createOptionDependent(factory, employment1, grossPay);
		createOptionDependent(factory, employment2, grossPay);
		createOptionDependent(factory, employment10, grossPay);

		Option amountOption = factory.createOption("Enter amount", "Enter amount (£)");
		amountOption.setTextEntryAllowed(true);
		Option fiveKOption = factory.createOption("Under £5000", "Under £5000");
		Option  fiveToTenKOption = factory.createOption("£5001-£10000",  "£5001-£10000");
		Option  tenToFifteenKOption = factory.createOption("£10001-£15000",  "£10001-£15000");
		Option  fifteenToTwentyKOption = factory.createOption("£15001-£20000",  "£15001-£20000");
		Option  twentyToTwentyFiveKOption = factory.createOption("£20001-£25000",  "£20001-£25000");
		Option  twentyFiveToThirtyKOption = factory.createOption("£25001-£30000",  "£25001-£30000");
		Option  thirtyPlusKOption = factory.createOption("£30000+",  "£30000+");
		grossPay.addOption(amountOption);
		grossPay.addOption(fiveKOption);
		grossPay.addOption(fiveToTenKOption);
		grossPay.addOption(tenToFifteenKOption);
		grossPay.addOption(fifteenToTwentyKOption);
		grossPay.addOption(twentyToTwentyFiveKOption);
		grossPay.addOption(twentyFiveToThirtyKOption);
		grossPay.addOption(thirtyPlusKOption);

	 }
	 
	 public static void addHospitalSection(Document  doc, Factory factory, int sectionNumber){
		Section hospitalSec = factory.createSection("Hospital", "Hospital Services");
		doc.addSection(hospitalSec);
		SectionOccurrence hospitalOcc = factory.createSectionOccurrence("Hospital Services Occurrence");
		hospitalSec.addOccurrence(hospitalOcc);
	 
        CompositeEntry hospitalEntried = factory.createComposite("Inpatient stays - FROM RECORDS");
        hospitalEntried.setDisplayText("Inpatient stays - FROM RECORDS");
        doc.addEntry(hospitalEntried);
		hospitalEntried.setSection(hospitalSec);
		
		TextEntry hospitalName = factory.createTextEntry("Name of hospital", "Name of hospital");
		hospitalEntried.addEntry(hospitalName);
		hospitalName.setSection(hospitalSec);

		TextEntry specialty = factory.createTextEntry("Specialty / Reason", "Specialty / Reason");
		hospitalEntried.addEntry(specialty);
		specialty.setSection(hospitalSec);
		
		NumericEntry numNights = factory.createNumericEntry("Number of nights", "Number of nights");
		numNights.addUnit(UnitWrapper.instance().getUnit("nights"));
		hospitalEntried.addEntry(numNights);
		numNights.setSection(hospitalSec);
		
        CompositeEntry hospitalInpatientFromPatientReport = factory.createComposite("Inpatient stays - FROM PATIENT REPORT");
		hospitalInpatientFromPatientReport.setDisplayText("Inpatient stays - FROM PATIENT REPORT");
        doc.addEntry(hospitalInpatientFromPatientReport);
		hospitalInpatientFromPatientReport.setSection(hospitalSec);
		
		TextEntry hospitalInpatientFromPatientReportName = factory.createTextEntry("Name of hospital", "Name of hospital");
		hospitalInpatientFromPatientReport.addEntry(hospitalInpatientFromPatientReportName);
		hospitalInpatientFromPatientReportName.setSection(hospitalSec);

		TextEntry specialtyInpatientFromPatientReport = factory.createTextEntry("Specialty / Reason", "Specialty / Reason");
		hospitalInpatientFromPatientReport.addEntry(specialtyInpatientFromPatientReport);
		specialtyInpatientFromPatientReport.setSection(hospitalSec);
		
		NumericEntry numNightsInpatientFromPatientReport = factory.createNumericEntry("Number of nights", "Number of nights");
		numNightsInpatientFromPatientReport.addUnit(UnitWrapper.instance().getUnit("nights"));
		hospitalInpatientFromPatientReport.addEntry(numNightsInpatientFromPatientReport);
		numNightsInpatientFromPatientReport.setSection(hospitalSec);

        CompositeEntry hospitalOutpatientFromPatientReport = factory.createComposite("Outpatient/Day patient appointments - FROM PATIENT REPORT");
		hospitalOutpatientFromPatientReport.setDisplayText("Outpatient/Day patient appointments - FROM PATIENT REPORT");
		doc.addEntry(hospitalOutpatientFromPatientReport);
		hospitalOutpatientFromPatientReport.setSection(hospitalSec);
		
		TextEntry hospitalOutpatientFromPatientReportName = factory.createTextEntry("Name of hospital", "Name of hospital");
		hospitalOutpatientFromPatientReport.addEntry(hospitalOutpatientFromPatientReportName);
		hospitalOutpatientFromPatientReportName.setSection(hospitalSec);

		TextEntry specialtyOutpatientFromPatientReport = factory.createTextEntry("Specialty / Reason", "Specialty / Reason");
		hospitalOutpatientFromPatientReport.addEntry(specialtyOutpatientFromPatientReport);
		specialtyOutpatientFromPatientReport.setSection(hospitalSec);
		
		NumericEntry numNightsOutpatientFromPatientReport = factory.createNumericEntry("Number of appointments", "Number of appointments");
		numNightsOutpatientFromPatientReport.addUnit(UnitWrapper.instance().getUnit("nights"));
		hospitalOutpatientFromPatientReport.addEntry(numNightsOutpatientFromPatientReport);
		numNightsOutpatientFromPatientReport.setSection(hospitalSec);
		
        CompositeEntry aAndEFromPatientReport =
        	factory.createComposite("Accident and Emergency Attendances - FROM PATIENT REPORT");
        aAndEFromPatientReport.setDisplayText("Accident and Emergency Attendances - FROM PATIENT REPORT");
		doc.addEntry(aAndEFromPatientReport);
		aAndEFromPatientReport.setSection(hospitalSec);
		
		TextEntry aAndEFromPatientReportName =
				factory.createTextEntry("Name of hospital", "Name of hospital");
		aAndEFromPatientReport.addEntry(aAndEFromPatientReportName);
		aAndEFromPatientReportName.setSection(hospitalSec);

		TextEntry aandESpecialtyFromPatientReport =
			factory.createTextEntry("Specialty / Reason", "Reason");
		aAndEFromPatientReport.addEntry(aandESpecialtyFromPatientReport);
		aandESpecialtyFromPatientReport.setSection(hospitalSec);
		
		OptionEntry yesNoOption = factory.createOptionEntry("Ambulance", "Ambulance yes/no");
		yesNoOption.addOption(factory.createOption("Yes", "Yes"));
		yesNoOption.addOption(factory.createOption("No", "No"));
		aAndEFromPatientReport.addEntry(yesNoOption);
		aAndEFromPatientReportName.setSection(hospitalSec);
		
		NumericEntry aAndENumNightsOutpatientFromPatientReport =
			factory.createNumericEntry("Number of contacts", "Number of contacts");
		aAndEFromPatientReport.addEntry(aAndENumNightsOutpatientFromPatientReport);
		aAndENumNightsOutpatientFromPatientReport.setSection(hospitalSec);
		
	 }
	
	public static void addCommunityServices(Document  doc, Factory factory, int sectionNumber){
		Section communitySec = factory.createSection("Community Services", "Community Services");
		doc.addSection(communitySec);
		SectionOccurrence hospitalOcc = factory.createSectionOccurrence("Community services");
		communitySec.addOccurrence(hospitalOcc);
	 
		CompositeEntry  communityServices = factory.createComposite("Community Services");
		communityServices.setDisplayText("Which of the following community based professionals or services have you" +
				" had contact with over the last 12 months (or since last interview if follow-up)?"); 
		doc.addEntry(communityServices);
		communityServices.setSection(communitySec);
		
		OptionEntry optionEntry = factory.createOptionEntry("Service", "Service");
		Option option1 = factory.createOption("GP", "General Practitioner - surgery");
		Option option2 = factory.createOption("GP", "General Practitioner - home");
		Option option3 = factory.createOption("GP", "General Practitioner - telephone");
		Option option4 = factory.createOption("Practice nurse", "Practice nurse i.e. nurse in GP surgery");
		Option option5 = factory.createOption("Case manager/care coordinator", "Case manager/care coordinator");
		Option option6 = factory.createOption("Community psychiatric nurse", "Community psychiatric nurse");
		Option option7 = factory.createOption("Community psychiatrist", "Community psychiatrist");
		Option option8 = factory.createOption("Clincial psychologist", "Clinical psychologist");
		Option option9 = factory.createOption("Home treatment team", "Home treatment team");
		Option option10 = factory.createOption("Crisis resolution team", "Crisis resolution team");
		Option option11 = factory.createOption("Health visitor/district nurse", "Health visitor/district nurse");
		Option option12 = factory.createOption("Occupational therapist", "Occupational therapist");
		Option option13 = factory.createOption("Counsellor", "Counsellor");
		Option option14 = factory.createOption("Family therapist", "Family therapist");
		Option option15 = factory.createOption("Social worker", "Social worker");
		Option option16 = factory.createOption("Home help/support worker", "Home help/support worker");
		Option option17 = factory.createOption("Day centre", "Day centre");
		Option option18 = factory.createOption("Drop in centre", "Drop in centre");
		Option option19 = factory.createOption("Drug and alcohol counsellor/support worker", "Drug and alcohol counsellor/support worker");
		Option option20 = factory.createOption("Advice service", "Advice service e.g. citizen's advice bureau");
		Option option21 = factory.createOption("Helpline", "Helpline e.g. Samaritans");
		Option option22 = factory.createOption("Self-help group", "Self-help group e.g. AA");
		Option option23 = factory.createOption("Other", "Other community service - please specify");
		option23.setTextEntryAllowed(true);
		
		optionEntry.addOption(option1);
		optionEntry.addOption(option2);
		optionEntry.addOption(option3);
		optionEntry.addOption(option4);
		optionEntry.addOption(option5);
		optionEntry.addOption(option6);
		optionEntry.addOption(option7);
		optionEntry.addOption(option8);
		optionEntry.addOption(option9);
		optionEntry.addOption(option10);
		optionEntry.addOption(option11);
		optionEntry.addOption(option12);
		optionEntry.addOption(option13);
		optionEntry.addOption(option14);
		optionEntry.addOption(option15);
		optionEntry.addOption(option16);
		optionEntry.addOption(option17);
		optionEntry.addOption(option18);
		optionEntry.addOption(option19);
		optionEntry.addOption(option20);
		optionEntry.addOption(option21);
		optionEntry.addOption(option22);
		optionEntry.addOption(option23);

		communityServices.addEntry(optionEntry);
		
		NumericEntry numSessions = factory.createNumericEntry("Number of Contacts", "Number of Contacts");
		communityServices.addEntry(numSessions);

		NumericEntry avgDuration = factory.createNumericEntry("Average duration", "Average duration");
		avgDuration.addUnit(UnitWrapper.instance().getUnit("mins"));
		communityServices.addEntry(avgDuration);
	}
	
	public static void addPsychotropicMedication(Document  doc, Factory factory, int sectionNumber){
		Section medicationSec = factory.createSection("Psychotropic Medication", "Psychotropic Medication");
		doc.addSection(medicationSec);
		SectionOccurrence hospitalOcc = factory.createSectionOccurrence("Community services");
		medicationSec.addOccurrence(hospitalOcc);
	 
		CompositeEntry  psychotropicMedication = factory.createComposite("Psychotropic Medication");
		psychotropicMedication.setDisplayText("Have you been prescribed any medication for your mental health problems in the last" +
				" twelve months (or since last interview if follow-up), e.g. antidepressants, antispsychotics etc?");
		doc.addEntry(psychotropicMedication);
		psychotropicMedication.setSection(medicationSec);
		
		TextEntry medicationName =
			factory.createTextEntry("Medication Name", "Name of Medication");
		psychotropicMedication.addEntry(medicationName);

		TextEntry dose =
			factory.createTextEntry("Dose", "Dose");
		psychotropicMedication.addEntry(dose);
		
		NumericEntry numWeeks = factory.createNumericEntry("No. of weeks", "No. of weeks taken");
		psychotropicMedication.addEntry(numWeeks);
		
		OptionEntry optionEntry = factory.createOptionEntry("Depot", "Depot?");
		optionEntry.addOption(factory.createOption("Yes", "Yes"));
		optionEntry.addOption(factory.createOption("No", "No"));
		psychotropicMedication.addEntry(optionEntry);
	}
	
	public static void addCriminalJustice(Document  doc, Factory factory, int sectionNumber){
		Section criminalSec = factory.createSection("Criminal Justice", "Criminal Justice");
		doc.addSection(criminalSec);
		SectionOccurrence hospitalOcc = factory.createSectionOccurrence("Criminal Justice");
		criminalSec.addOccurrence(hospitalOcc);
		
		CompositeEntry  custody = factory.createComposite("Custody");
		custody.setDisplayText("Have you spent any time in prison or police custody in the last 12 months " +
				" (or since last interview if follow-up)?");
		doc.addEntry(custody);
		custody.setSection(criminalSec);
		
		OptionEntry custodyEntry = factory.createOptionEntry("Custody", "Custody");
		Option  police = factory.createOption("Police Custody",  "Police Custody");
		Option  prison = factory.createOption("Prison",  "Prison");
		custodyEntry.addOption(police);
		custodyEntry.addOption(prison);
		custody.addEntry(custodyEntry);
		
		TextEntry nameOfPrison =
			factory.createTextEntry("Name of prison", "Name of prison");
		custody.addEntry(nameOfPrison);

		NumericEntry numDays = factory.createNumericEntry("Days in Custody", "Days in Custody");
		numDays.addUnit(UnitWrapper.instance().getUnit("days"));
		custody.addEntry(numDays);

		CompositeEntry  service = factory.createComposite("Service");
		service.setDisplayText("Have you had contact with the following professionals in the last 12 months (or since" +
				" last interview if follow-up)?");
		doc.addEntry(service);
		service.setSection(criminalSec);
		
		OptionEntry serviceEntry = factory.createOptionEntry("Service", "Service");
		Option  probationOfficer = factory.createOption("Probation Officer",  "Probation Officer");
		Option policeService = factory.createOption("Police",  "Police (as either victim or perpetrator of crime)");
		Option  solicitor = factory.createOption("Solicitor",  "Solicitor or other legal representative " +"(for any reason, e.g. MH, criminal or civil?");
		serviceEntry.addOption(probationOfficer);
		serviceEntry.addOption(policeService);
		serviceEntry.addOption(solicitor);
		service.addEntry(serviceEntry);
	
		NumericEntry numContacts =
			factory.createNumericEntry("No of contacts", "No of contacts");
		service.addEntry(numContacts);

		NumericEntry avgDurations =
			factory.createNumericEntry("Average duration", "Average duration");
		avgDurations.addUnit(UnitWrapper.instance().getUnit("mins"));
		service.addEntry(avgDurations);
		
		OptionEntry legalAid = factory.createOptionEntry("Legal aid", "Legal aid");
		legalAid.addOption(factory.createOption("Yes", "Yes"));
		legalAid.addOption(factory.createOption("No", "No"));
		legalAid.setSection(criminalSec);
		doc.addEntry(legalAid);
		
		CompositeEntry  victim12Months = factory.createComposite("Victim last 12 months");
		victim12Months.setDisplayText("Have you been the victim of any crimes in the last 12 months (or since " +
				"last interviewed if follow-up)?");
		doc.addEntry(victim12Months);
		victim12Months.setSection(criminalSec);

		TextEntry specifyCrime =
			factory.createTextEntry("Specify Crime", "Specify Crime");
		victim12Months.addEntry(specifyCrime);

		NumericEntry numOffences = factory.createNumericEntry("No. of offences", "No. of offences");
		victim12Months.addEntry(numOffences);

		CompositeEntry  crimes12Months = factory.createComposite("Crimes last 12 months");
		crimes12Months.setDisplayText("Have you committed any crimes in the last 12 months (or since last interviewed" +
				" if follow-up)?");
		doc.addEntry(crimes12Months);
		crimes12Months.setSection(criminalSec);
		
		TextEntry specifyCrime12Months =
			factory.createTextEntry("Specify Crime", "Specify Crime");
		crimes12Months.addEntry(specifyCrime12Months);

		NumericEntry numOffences12Months = factory.createNumericEntry("No. of offences", "No. of offences");
		crimes12Months.addEntry(numOffences12Months);
		
	}
}
