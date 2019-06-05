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

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;

//TODO In some places the units are specified in the text. This could be
//improved by removing them from the text and adding them as real units
public class TimeUseInterview extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");
        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");
        Unit hours = UnitWrapper.instance().getUnit("hours");

        Document doc = factory.createDocument("Time Use Interview",
                "Time Use Interview");

        createDocumentStatuses(factory, doc);

        //Employment section
        Section employmentSec = factory.createSection("Employment section");
        doc.addSection(employmentSec);
        employmentSec.setDisplayText("Employment");
        SectionOccurrence employmentSecOcc = factory.createSectionOccurrence(
                "Employment section occurrence");
        employmentSec.addOccurrence(employmentSecOcc);

        OptionEntry paidWorkOption = factory.createOptionEntry("Paid work last " +
                "week", "Did you do any paid work in the last month, either as " +
                "an employee or self-employed?");
        doc.addEntry(paidWorkOption);
        paidWorkOption.setSection(employmentSec);
        paidWorkOption.setLabel("1.a");
        Option paidWorkYes = factory.createOption("Yes (Go to Qu 4)", 1);
        paidWorkOption.addOption(paidWorkYes);
        Option paidWorkNo = factory.createOption("No (Ask 1.b)", 0);
        paidWorkOption.addOption(paidWorkNo);
        paidWorkOption.setOptionCodesDisplayed(false);

        OptionEntry govSchemeOption = factory.createOptionEntry("Government " +
                "scheme", "Were you on a government scheme for employment " +
                "training?", EntryStatus.DISABLED);
        doc.addEntry(govSchemeOption);
        govSchemeOption.setSection(employmentSec);
        govSchemeOption.setLabel("1.b");
        Option govSchemeYes = factory.createOption("Yes (Please give details below)", 1);
        govSchemeOption.addOption(govSchemeYes);
        Option govSchemeNo = factory.createOption("No (Go to Qu 2)", 0);
        govSchemeOption.addOption(govSchemeNo);
        govSchemeOption.setOptionCodesDisplayed(false);
        createOptionDependent(factory, paidWorkNo, govSchemeOption);

        LongTextEntry govSchemeDetails = factory.createLongTextEntry("Details",
                "Details", EntryStatus.DISABLED);
        doc.addEntry(govSchemeDetails);
        govSchemeDetails.setSection(employmentSec);
        createOptionDependent(factory, govSchemeYes, govSchemeDetails);

        OptionEntry jobOption = factory.createOptionEntry(
                "Job or Business Option",
                "Did you have a job or business you were away from?",
                EntryStatus.DISABLED);
        doc.addEntry(jobOption);
        jobOption.setSection(employmentSec);
        jobOption.setLabel("2.a");
        Option jobYesOption = factory.createOption("Yes (Ask 2.b)", 1);
        jobOption.addOption(jobYesOption);
        Option jobNoOption = factory.createOption("No (Go to Qu 3)", 0);
        jobOption.addOption(jobNoOption);
        jobOption.setOptionCodesDisplayed(false);
        createOptionDependent(factory, govSchemeYes, jobOption);
        createOptionDependent(factory, govSchemeNo, jobOption);

        OptionEntry whyAwayOption = factory.createOptionEntry("Why away option",
                "Why were you away? (Then ask Qu 4 for typical work pattern " +
                        "when not away)");
        doc.addEntry(whyAwayOption);
        whyAwayOption.setSection(employmentSec);
        whyAwayOption.setLabel("2.b");
        whyAwayOption.setEntryStatus(EntryStatus.DISABLED);
        whyAwayOption.addOption(factory.createOption("Holiday"));
        whyAwayOption.addOption(factory.createOption("Sickness"));
        whyAwayOption.addOption(factory.createOption("Studying"));
        whyAwayOption.addOption(factory.createOption("Maternity/paternity leave"));
        Option whyAwayOther = factory.createOption("Other reason (plese state)");
        whyAwayOther.setTextEntryAllowed(true);
        whyAwayOption.addOption(whyAwayOther);
        createOptionDependent(factory, jobYesOption, whyAwayOption);

        OptionEntry unpaidWorkOption = factory.createOptionEntry(
                "Unpaid work for business you own",
                "Did you do any unpaid work for any business that you or a relative own?");
        doc.addEntry(unpaidWorkOption);
        unpaidWorkOption.setSection(employmentSec);
        unpaidWorkOption.setLabel("3.a");
        unpaidWorkOption.setEntryStatus(EntryStatus.DISABLED);
        Option unpaidWorkYesOption = factory.createOption(
                "Yes (Go to Qu 4 onwards)", 1);
        unpaidWorkOption.addOption(unpaidWorkYesOption);
        Option unpaidWorkNoOption = factory.createOption("No (Ask 3.b)", 0);
        unpaidWorkOption.addOption(unpaidWorkNoOption);
        unpaidWorkOption.setOptionCodesDisplayed(false);
        createOptionDependent(factory, jobNoOption, unpaidWorkOption);

        OptionEntry everPaidJobOption = factory.createOptionEntry("Ever had a " +
                "paid job option", "Have you ever had a paid job?");
        doc.addEntry(everPaidJobOption);
        everPaidJobOption.setSection(employmentSec);
        everPaidJobOption.setLabel("3.b");
        everPaidJobOption.setEntryStatus(EntryStatus.DISABLED);
        Option paidJobYesOption = factory.createOption("Yes (ask 3.c)");
        everPaidJobOption.addOption(paidJobYesOption);
        Option paidJobNoOption = factory.createOption("No (Go to Qu 8)");
        everPaidJobOption.addOption(paidJobNoOption);
        createOptionDependent(factory, unpaidWorkNoOption, everPaidJobOption);

        DateEntry leaveJobDate = factory.createDateEntry("Last paid job leave " +
                "date", "When did you leave your last paid job?");
        doc.addEntry(leaveJobDate);
        leaveJobDate.setSection(employmentSec);
        leaveJobDate.setLabel("3.c");
        leaveJobDate.setEntryStatus(EntryStatus.DISABLED);
        leaveJobDate.addValidationRule(after1900);
        createOptionDependent(factory, paidJobYesOption, leaveJobDate);

        LongTextEntry jobLastWeekText = factory.createLongTextEntry(
                "Job last month text",
                "What was your main job in the last month/most " +
                "recent period of paid work?");
        doc.addEntry(jobLastWeekText);
        jobLastWeekText.setSection(employmentSec);
        jobLastWeekText.setEntryStatus(EntryStatus.DISABLED);
        jobLastWeekText.setLabel("4");
        createOptionDependent(factory, paidWorkYes, jobLastWeekText);
        // jobYesOption (Q2a) enables Q2b which enables this question no matter
        // what the answer is. So instead of creating an OptionDependent for
        // answer in 2b, just use the relevant 2a option
        createOptionDependent(factory, jobYesOption, jobLastWeekText);
        createOptionDependent(factory, unpaidWorkYesOption, jobLastWeekText);
        createOptionDependent(factory, paidJobYesOption, jobLastWeekText);

        NumericEntry hoursMainJob = factory.createNumericEntry(
                "Hours a week usually work in main job",
                "How many hours a week do you usually work in your main job "+
                "or business?  Include any overtime.",
                EntryStatus.DISABLED);
        doc.addEntry(hoursMainJob);
        hoursMainJob.setSection(employmentSec);
        hoursMainJob.setLabel("5");
        hoursMainJob.addValidationRule(positive);
        createOptionDependent(factory, paidWorkYes, hoursMainJob);
        createOptionDependent(factory, jobYesOption, hoursMainJob);
        createOptionDependent(factory, unpaidWorkYesOption, hoursMainJob);
        createOptionDependent(factory, paidJobYesOption, hoursMainJob);

        NumericEntry workedLastMonth = factory.createNumericEntry(
                "Hours worked last month",
                "How many hours have you worked in the last month?",
                EntryStatus.DISABLED);
        doc.addEntry(workedLastMonth);
        workedLastMonth.setSection(employmentSec);
        workedLastMonth.addValidationRule(positive);
        createOptionDependent(factory, paidWorkYes, workedLastMonth);
        createOptionDependent(factory, jobYesOption, workedLastMonth);
        createOptionDependent(factory, unpaidWorkYesOption, workedLastMonth);
        createOptionDependent(factory, paidJobYesOption, workedLastMonth);

        OptionEntry takeHomePay = factory.createOptionEntry("Take-home pay",
                "What was your take-home montlhy pay after all deductions the last time " +
                        "you were paid?",
                EntryStatus.DISABLED);
        doc.addEntry(takeHomePay);
        takeHomePay.setSection(employmentSec);
        takeHomePay.setLabel("6");
        takeHomePay.addOption(factory.createOption("Less than £215", "Less than £215"));
        takeHomePay.addOption(factory.createOption("£215 to less than £435", "£215 to less than £435"));
        takeHomePay.addOption(factory.createOption("£435 to less than £870", "£435 to less than £870"));
        takeHomePay.addOption(factory.createOption("£870 to less than £1305", "£870 to less than £1305"));
        takeHomePay.addOption(factory.createOption("£1305 to less than £1740", "£1305 to less than £1740"));
        takeHomePay.addOption(factory.createOption("£1740 to less than £2820", "£1740 to less than £2820"));
        takeHomePay.addOption(factory.createOption("£2820 to less than £3420", "£2820 to less than £3420"));
        takeHomePay.addOption(factory.createOption("£3420 to less than £3830", "£3420 to less than £3830"));
        takeHomePay.addOption(factory.createOption("£3830 to less than £4580", "£3830 to less than £4580"));
        takeHomePay.addOption(factory.createOption("£4580 to less than £6670", "£4580 to less than £6670"));
        takeHomePay.addOption(factory.createOption("£6670 or more", "£6670 or more"));
        createOptionDependent(factory, paidWorkYes, takeHomePay);
        createOptionDependent(factory, jobYesOption, takeHomePay);
        createOptionDependent(factory, unpaidWorkYesOption, takeHomePay);
        createOptionDependent(factory, paidJobYesOption, takeHomePay);

        OptionEntry otherPaidWork = factory.createOptionEntry("Last week other paid work",
                "In the last month, did you do any other paid work or have any " +
                        "other paid job or business, in addition to the one you have " +
                        "just told me about?", EntryStatus.DISABLED);
        doc.addEntry(otherPaidWork);
        otherPaidWork.setSection(employmentSec);
        otherPaidWork.setLabel("7");
        Option otherPaidWorkYes = factory.createOption("Yes", "Yes", 1);
        otherPaidWork.addOption(otherPaidWorkYes);
        otherPaidWork.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, paidWorkYes, otherPaidWork);
        createOptionDependent(factory, jobYesOption, jobLastWeekText);
        createOptionDependent(factory, unpaidWorkYesOption, otherPaidWork);
        createOptionDependent(factory, paidJobYesOption, otherPaidWork);

        LongTextEntry otherPaidWorkDetails = factory.createLongTextEntry(
                "Other work details",
                "Details (e.g. how many, number of hours, type of job, wages)",
                EntryStatus.DISABLED);
        doc.addEntry(otherPaidWorkDetails);
        otherPaidWorkDetails.setSection(employmentSec);
        createOptionDependent(factory, otherPaidWorkYes, otherPaidWorkDetails);

        NumericEntry otherPaidWorkHours = factory.createNumericEntry(
                "Other work hours",
                "How many hours did you spend doing other work in the last month?",
                EntryStatus.DISABLED);
        doc.addEntry(otherPaidWorkHours);
        otherPaidWorkHours.setSection(employmentSec);
        otherPaidWorkHours.addValidationRule(positive);
        createOptionDependent(factory, otherPaidWorkYes, otherPaidWorkHours);

        NarrativeEntry instructions = factory.createNarrativeEntry("",
                "If no paid work at all in the last month, go to Qu 8. If " +
                "currently working, go to Qu 11.");
        doc.addEntry(instructions);
        instructions.setSection(employmentSec);

        OptionEntry lookingWork = factory.createOptionEntry("Looking for paid work",
                "Thinking of the last month, have you been " +
                        "looking for any kind of paid work or government training schemes?",
                EntryStatus.DISABLED);
        doc.addEntry(lookingWork);
        lookingWork.setSection(employmentSec);
        lookingWork.setLabel("8");
        Option lookingWorkYes = factory.createOption("Yes", "Yes (Go to Qu 9)", 1);
        lookingWork.addOption(lookingWorkYes);
        Option lookingWorkNo = factory.createOption("No", "No (Go to Qu 10)", 2);
        lookingWork.addOption(lookingWorkNo);
        createOptionDependent(factory, unpaidWorkNoOption, lookingWork);

        NarrativeEntry doThings = factory.createNarrativeEntry("Did any of these things",
                "In the last month, did you do any of these things?");
        doc.addEntry(doThings);
        doThings.setSection(employmentSec);
        doThings.setLabel("9");

        //TODO render as a table?
        OptionEntry jobCentre = factory.createOptionEntry("Jobcentre/Jobmarket",
                "Visited a Jobcentre/Jobmarket or Training and Employment Agency Office?",
                EntryStatus.DISABLED);
        doc.addEntry(jobCentre);
        jobCentre.setSection(employmentSec);
        jobCentre.setOptionCodesDisplayed(false);
        Option jobCentreYes = factory.createOption("Yes", "Yes", 1);
        jobCentre.addOption(jobCentreYes);
        jobCentre.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, jobCentre);

        OptionEntry careersOffice = factory.createOptionEntry("Visited a " +
                "careers office", "Visited a careers office?",
                EntryStatus.DISABLED);
        doc.addEntry(careersOffice);
        careersOffice.setSection(employmentSec);
        careersOffice.setOptionCodesDisplayed(false);
        careersOffice.addOption(factory.createOption("Yes", "Yes", 1));
        careersOffice.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, careersOffice);

        OptionEntry jobClub = factory.createOptionEntry("Visited a Jobclub",
                "Visited a Jobclub?", EntryStatus.DISABLED);
        doc.addEntry(jobClub);
        jobClub.setSection(employmentSec);
        jobClub.setOptionCodesDisplayed(false);
        jobClub.addOption(factory.createOption("Yes", "Yes", 1));
        jobClub.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, jobClub);

        OptionEntry agency = factory.createOptionEntry("Name on the books of " +
                "employment agency?", "Had your name on the books of an " +
                "employment agency?", EntryStatus.DISABLED);
        doc.addEntry(agency);
        agency.setSection(employmentSec);
        agency.setOptionCodesDisplayed(false);
        agency.addOption(factory.createOption("Yes", "Yes", 1));
        agency.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, agency);

        OptionEntry advertised = factory.createOptionEntry("Advertised for jobs " +
                "in newspapers", "Advertised for jobs in newspapers, etc?",
                EntryStatus.DISABLED);
        doc.addEntry(advertised);
        advertised.setSection(employmentSec);
        advertised.setOptionCodesDisplayed(false);
        advertised.addOption(factory.createOption("Yes", "Yes", 1));
        advertised.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, advertised);

        OptionEntry lookedAd = factory.createOptionEntry("Looked for " +
                "advertisements in newspapers", "Looked for advertisements in " +
                "newspapers, etc?", EntryStatus.DISABLED);
        doc.addEntry(lookedAd);
        lookedAd.setSection(employmentSec);
        lookedAd.setOptionCodesDisplayed(false);
        lookedAd.addOption(factory.createOption("Yes", "Yes", 1));
        lookedAd.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, lookedAd);

        OptionEntry answeredAd = factory.createOptionEntry("Answered advertisements",
                "Answered advertisements in newspapers, etc?",
                EntryStatus.DISABLED);
        doc.addEntry(answeredAd);
        answeredAd.setSection(employmentSec);
        answeredAd.setOptionCodesDisplayed(false);
        answeredAd.addOption(factory.createOption("Yes", "Yes", 1));
        answeredAd.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, answeredAd);

        OptionEntry appliedDirectly = factory.createOptionEntry("Applied directly to employers",
                "Applied directly to employers?", EntryStatus.DISABLED);
        doc.addEntry(appliedDirectly);
        appliedDirectly.setSection(employmentSec);
        appliedDirectly.setOptionCodesDisplayed(false);
        appliedDirectly.addOption(factory.createOption("Yes", "Yes", 1));
        appliedDirectly.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, appliedDirectly);

        OptionEntry askedFriends = factory.createOptionEntry("Asked friends " +
                "about jobs", "Asked friends, relatives, colleagues or trade " +
                "unions about jobs?", EntryStatus.DISABLED);
        doc.addEntry(askedFriends);
        askedFriends.setSection(employmentSec);
        askedFriends.setOptionCodesDisplayed(false);
        askedFriends.addOption(factory.createOption("Yes", "Yes", 1));
        askedFriends.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, askedFriends);

        OptionEntry waitedResults = factory.createOptionEntry("Waited for the " +
                "results of a job application",
                "Waited for the results of a job application?", EntryStatus.DISABLED);
        doc.addEntry(waitedResults);
        waitedResults.setSection(employmentSec);
        waitedResults.setOptionCodesDisplayed(false);
        waitedResults.addOption(factory.createOption("Yes", "Yes", 1));
        waitedResults.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, waitedResults);

        OptionEntry interview = factory.createOptionEntry("Been to an interview?",
                "Been to an interview?", EntryStatus.DISABLED);
        doc.addEntry(interview);
        interview.setSection(employmentSec);
        interview.setOptionCodesDisplayed(false);
        interview.addOption(factory.createOption("Yes", "Yes", 1));
        interview.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, interview);

        OptionEntry findWorkElse = factory.createOptionEntry("Anything else to " +
                "find work", "Anything else to find work? Please state.",
                EntryStatus.DISABLED);
        doc.addEntry(findWorkElse);
        findWorkElse.setSection(employmentSec);
        findWorkElse.setOptionCodesDisplayed(false);
        Option findWorkYes = factory.createOption("Yes", "Yes", 1);
        findWorkYes.setTextEntryAllowed(true);
        findWorkElse.addOption(findWorkYes);
        findWorkElse.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, lookingWorkYes, findWorkElse);

        NumericEntry findWorkElseTime = factory.createNumericEntry("Time spent " +
                "looking for work",
                "How much time did you spend doing this?", EntryStatus.DISABLED);
        doc.addEntry(findWorkElseTime);
        findWorkElseTime.setSection(employmentSec);
        findWorkElseTime.addValidationRule(positive);
        findWorkElseTime.addUnit(UnitWrapper.instance().getUnit("mins"));
        findWorkElseTime.addUnit(UnitWrapper.instance().getUnit("hours"));
        findWorkElseTime.addUnit(UnitWrapper.instance().getUnit("days"));
        createOptionDependent(factory, lookingWorkYes, findWorkElseTime);

        OptionEntry reasonNoLook = factory.createOptionEntry(
                "Main reason did not look for work",
                "May I just check, what was the main reason " +
                        "you did not look for work in the last month?",
                EntryStatus.DISABLED);
        doc.addEntry(reasonNoLook);
        reasonNoLook.setSection(employmentSec);
        reasonNoLook.setLabel("10");
        reasonNoLook.addOption(factory.createOption(
                                "Waiting for job application",
                                "Waiting for the results of a job " +
                                "application/being assessed by training agent?"));
        reasonNoLook.addOption(factory.createOption("Student", "Student?"));
        reasonNoLook.addOption(factory.createOption(
                "Looking after the family home",
                "Looking after the family home?"));
        reasonNoLook.addOption(factory.createOption(
                "Temporarily sick or injured", "Temporarily sick or injured?"));
        reasonNoLook.addOption(factory.createOption(
                "Long-term sick or disabled", "Long-term sick or disabled?"));
        reasonNoLook.addOption(factory.createOption(
                "Believe no jobs available", "Believe no jobs available?"));
        reasonNoLook.addOption(factory.createOption("Not yet started looking",
                "Not yet started looking?"));
        Option reasonNoLookOther = factory.createOption("Any other reason?",
                "Any other reason?  Please state");
        reasonNoLookOther.setTextEntryAllowed(true);
        reasonNoLook.addOption(reasonNoLookOther);
        createOptionDependent(factory, lookingWorkNo, reasonNoLook);

        OptionEntry stateBenefits = factory.createOptionEntry(
                "State benefits",
                "Are you at present receiving any of these state benefits in " +
                        "your own right?");
        doc.addEntry(stateBenefits);
        stateBenefits.setSection(employmentSec);
        stateBenefits.setLabel("11");
        Option stateBenefitsYes = factory.createOption("Yes", "Yes");
        stateBenefits.addOption(stateBenefitsYes);
        stateBenefits.addOption(factory.createOption("No", "No"));

        NarrativeEntry stateBenefitsN = factory.createNarrativeEntry("State benefits narrative",
                "If so, which ones?");
        doc.addEntry(stateBenefitsN);
        stateBenefitsN.setSection(employmentSec);

        //TODO render as a table?
        OptionEntry childBenefit = factory.createOptionEntry(
                "Child Benefit",
                "Child Benefit",
                EntryStatus.DISABLED);
        doc.addEntry(childBenefit);
        childBenefit.setSection(employmentSec);
        childBenefit.addOption(factory.createOption("Yes", "Yes"));
        childBenefit.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, childBenefit);

        OptionEntry guardianAllowance = factory.createOptionEntry("Guardian's Allowance",
                "Guardian's Allowance",
                EntryStatus.DISABLED);
        doc.addEntry(guardianAllowance);
        guardianAllowance.setSection(employmentSec);
        guardianAllowance.addOption(factory.createOption("Yes", "Yes"));
        guardianAllowance.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, guardianAllowance);

        OptionEntry invalidAllowance = factory.createOptionEntry("Invalid Care " +
                "Allowance", "Invalid Care Allowance",
                EntryStatus.DISABLED);
        doc.addEntry(invalidAllowance);
        invalidAllowance.setSection(employmentSec);
        invalidAllowance.addOption(factory.createOption("Yes", "Yes"));
        invalidAllowance.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, invalidAllowance);

        OptionEntry pension = factory.createOptionEntry("Pension",
                "Pension (of any kind, i.e. retirement, widow's, etc)",
                EntryStatus.DISABLED);
        doc.addEntry(pension);
        pension.setSection(employmentSec);
        pension.addOption(factory.createOption("Yes", "Yes"));
        pension.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, pension);

        OptionEntry severeDisability = factory.createOptionEntry("Severe " +
                "Disability Allowance", "Severe Disability Allowance",
                EntryStatus.DISABLED);
        doc.addEntry(severeDisability);
        severeDisability.setSection(employmentSec);
        severeDisability.addOption(factory.createOption("Yes", "Yes"));
        severeDisability.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, severeDisability);

        OptionEntry disability = factory.createOptionEntry("Disability " +
                "Living Allowance", "Disability Living Allowance",
                EntryStatus.DISABLED);
        doc.addEntry(disability);
        disability.setSection(employmentSec);
        disability.addOption(factory.createOption("Yes", "Yes"));
        disability.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, disability);

        OptionEntry jobSeeker = factory.createOptionEntry("Job Seekers' " +
                "Allowance", "Job Seekers' Allowance",
                EntryStatus.DISABLED);
        doc.addEntry(jobSeeker);
        jobSeeker.setSection(employmentSec);
        jobSeeker.addOption(factory.createOption("Yes", "Yes"));
        jobSeeker.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, jobSeeker);

        OptionEntry incomeSupport = factory.createOptionEntry("Income Support",
                "Income Support",
                EntryStatus.DISABLED);
        doc.addEntry(incomeSupport);
        incomeSupport.setSection(employmentSec);
        incomeSupport.addOption(factory.createOption("Yes", "Yes"));
        incomeSupport.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, incomeSupport);

        OptionEntry incapacityBenefit = factory.createOptionEntry("Incapacity " +
                "Benefit", "Incapacity Benefit",
                EntryStatus.DISABLED);
        doc.addEntry(incapacityBenefit);
        incapacityBenefit.setSection(employmentSec);
        incapacityBenefit.addOption(factory.createOption("Yes", "Yes"));
        incapacityBenefit.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, incapacityBenefit);

        OptionEntry sickPay = factory.createOptionEntry("Statutory sick pay",
                "Statutory sick pay",
                EntryStatus.DISABLED);
        doc.addEntry(sickPay);
        sickPay.setSection(employmentSec);
        sickPay.addOption(factory.createOption("Yes", "Yes"));
        sickPay.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, sickPay);

        OptionEntry disablementBenefit = factory.createOptionEntry("Industrial " +
                "Injury Disablement Benefit", "Industrial Injury Disablement " +
                "Benefit",
                EntryStatus.DISABLED);
        doc.addEntry(disablementBenefit);
        disablementBenefit.setSection(employmentSec);
        disablementBenefit.addOption(factory.createOption("Yes", "Yes"));
        disablementBenefit.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, disablementBenefit);

        OptionEntry maternityAllowance = factory.createOptionEntry("Maternity " +
                "Allowance", "Maternity Allowance",
                EntryStatus.DISABLED);
        doc.addEntry(maternityAllowance);
        maternityAllowance.setSection(employmentSec);
        maternityAllowance.addOption(factory.createOption("Yes", "Yes"));
        maternityAllowance.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, maternityAllowance);

        OptionEntry workingCredit = factory.createOptionEntry("Working " +
                "Families' Tax Credit", "Working Families' Tax Credit",
                EntryStatus.DISABLED);
        doc.addEntry(workingCredit);
        workingCredit.setSection(employmentSec);
        workingCredit.addOption(factory.createOption("Yes", "Yes"));
        workingCredit.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, workingCredit);

        OptionEntry disabledCredit = factory.createOptionEntry("Disabled " +
                "Persons' Tax Credit", "Disabled Persons' Tax Credit",
                EntryStatus.DISABLED);
        doc.addEntry(disabledCredit);
        disabledCredit.setSection(employmentSec);
        disabledCredit.addOption(factory.createOption("Yes", "Yes"));
        disabledCredit.addOption(factory.createOption("No", "No"));
        createOptionDependent(factory, stateBenefitsYes, disabledCredit);

        OptionEntry housingBenefit = factory.createOptionEntry("Disability Living Allowance",
                "Disability Living Allowance",
                EntryStatus.DISABLED);
        doc.addEntry(housingBenefit);
        housingBenefit.setSection(employmentSec);
        housingBenefit.addOption(factory.createOption("Yes", "Yes", 1));
        housingBenefit.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, stateBenefitsYes, housingBenefit);

        OptionEntry careAllowance = factory.createOptionEntry(
                "Care component of Disability Living Allowance",
                "Care component of Disability Living Allowance",
                EntryStatus.DISABLED);
        doc.addEntry(careAllowance);
        careAllowance.setSection(employmentSec);
        careAllowance.addOption(factory.createOption("Yes", "Yes", 1));
        careAllowance.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, stateBenefitsYes, careAllowance);

        OptionEntry mobilityAllowance = factory.createOptionEntry(
                "Mobility component of Disability Living Allowance",
                "Mobility component of Disability Living Allowance",
                EntryStatus.DISABLED);
        doc.addEntry(mobilityAllowance);
        mobilityAllowance.setSection(employmentSec);
        mobilityAllowance.addOption(factory.createOption("Yes", "Yes", 1));
        mobilityAllowance.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, stateBenefitsYes, mobilityAllowance);

        OptionEntry attendanceAllowance = factory.createOptionEntry(
                "Attendance Allowance", "Attendance Allowance",
                EntryStatus.DISABLED);
        doc.addEntry(attendanceAllowance);
        attendanceAllowance.setSection(employmentSec);
        attendanceAllowance.addOption(factory.createOption("Yes", "Yes", 1));
        attendanceAllowance.addOption(factory.createOption("No", "No", 2));
        createOptionDependent(factory, stateBenefitsYes, attendanceAllowance);

        //Score sheet - employment
        NarrativeEntry employScoreHeading = factory.createNarrativeEntry(
                "Employment Score Heading",
                "Score Sheet");
        doc.addEntry(employScoreHeading);
        employScoreHeading.setSection(employmentSec);
        employScoreHeading.setStyle(NarrativeStyle.HEADER);

        DerivedEntry scorePaidWork = factory.createDerivedEntry("Score - Paid Work", "Is paid work in the last month present (1) or absent (0)?");
        doc.addEntry(scorePaidWork);
        scorePaidWork.setSection(employmentSec);
        scorePaidWork.setDescription("Present = 'YES' response to Question 1 (a), 1 (b), or Question 2; Absent = 'NO' response to Question 1 or 2");
        scorePaidWork.setFormula("if((a==1||b==1||c==1),1,0)");
        scorePaidWork.addVariable("a",paidWorkOption);
        scorePaidWork.addVariable("b",govSchemeOption);
        scorePaidWork.addVariable("c",jobOption);

        DerivedEntry scoreHoursPerWeek = factory.createDerivedEntry(
                "Score - Hours per week",
                "Hours per week in paid employment over the last month");
        doc.addEntry(scoreHoursPerWeek);
        scoreHoursPerWeek.setSection(employmentSec);
        scoreHoursPerWeek.setDescription("This should be calculated by adding all hours paid employment (from Questions 5 and 7) "
                                        +"and dividing by 4 to get a weekly average.  This includes time spent on government "
                                        +"training schemes. e.g. if someone generally gets one paid day of work per month, this is "
                                        +"taken as 2 hours per week");
        scoreHoursPerWeek.setFormula("a+(b/4)");
        scoreHoursPerWeek.addVariable("a",hoursMainJob);
        scoreHoursPerWeek.addVariable("b",otherPaidWorkHours);

        DerivedEntry scoreNumWorkSearch = factory.createDerivedEntry("Score - Number Work Search", "Number of different work searching activities");
        doc.addEntry(scoreNumWorkSearch);
        scoreNumWorkSearch.setSection(employmentSec);
        scoreNumWorkSearch.setDescription("Taken from Question 9");
        scoreNumWorkSearch.setFormula("a+b+c+d+e+f+g+h+i+j+k+l");
        scoreNumWorkSearch.addVariable("a",jobCentre);
        scoreNumWorkSearch.addVariable("b",careersOffice);
        scoreNumWorkSearch.addVariable("c",jobClub);
        scoreNumWorkSearch.addVariable("d",agency);
        scoreNumWorkSearch.addVariable("e",advertised);
        scoreNumWorkSearch.addVariable("f",lookedAd);
        scoreNumWorkSearch.addVariable("g",answeredAd);
        scoreNumWorkSearch.addVariable("h",appliedDirectly);
        scoreNumWorkSearch.addVariable("i",askedFriends);
        scoreNumWorkSearch.addVariable("j",waitedResults);
        scoreNumWorkSearch.addVariable("k",interview);
        scoreNumWorkSearch.addVariable("l",findWorkElse);

        //TODO can this be done with a derived entry?
        NumericEntry scoreWeeksSinceLastWorked = factory.createNumericEntry("Score - Weeks Since Last Worked",
                "Number of weeks since last worked",
                EntryStatus.MANDATORY);
        doc.addEntry(scoreWeeksSinceLastWorked);
        scoreWeeksSinceLastWorked.setSection(employmentSec);
        scoreWeeksSinceLastWorked.setDescription("Calculate this as the number of weeks between the date "+
                "entered in question 3c and today's date");
        scoreWeeksSinceLastWorked.setDefaultValue(new Double(0));
        createOptionDependent(factory, paidJobNoOption, scoreWeeksSinceLastWorked, EntryStatus.DISABLED);

        DerivedEntry scoreHoursLastJob = factory.createDerivedEntry("Score - Hours Last Job",
                "Number of hours per week worked in last job");
        doc.addEntry(scoreHoursLastJob);
        scoreHoursLastJob.setSection(employmentSec);
        scoreHoursLastJob.setDescription("Response to Question 5");
        scoreHoursLastJob.setFormula("a");
        scoreHoursLastJob.addVariable("a", hoursMainJob);
        createOptionDependent(factory, paidJobNoOption, scoreHoursLastJob, EntryStatus.DISABLED);


        //Education and Training section
        Section educationSec = factory.createSection("Education and Training section");
        doc.addSection(educationSec);
        educationSec.setDisplayText("Education and Training");
        SectionOccurrence educationSecOcc = factory.createSectionOccurrence(
                "Education and Training section occurrence");
        educationSec.addOccurrence(educationSecOcc);

        OptionEntry qualifications = factory.createOptionEntry(
                "Qualifications connected with work",
                "Do you have any qualifications from school, college or " +
                        "university, connected with work or from government schemes?");
        doc.addEntry(qualifications);
        qualifications.setSection(educationSec);
        qualifications.setLabel("1.a");
        Option qualificationsYes = factory.createOption("Yes", "Yes (Ask b onwards)", 1);
        qualifications.addOption(qualificationsYes);
        qualifications.addOption(factory.createOption("No", "No (Go to Qu 2", 2));

        LongTextEntry qualificationsHighest = factory.createLongTextEntry(
                "Highest qualification",
                "What is the highest qualification that you have?",
                EntryStatus.DISABLED);
        doc.addEntry(qualificationsHighest);
        qualificationsHighest.setSection(educationSec);
        qualificationsHighest.setLabel("1.b");
        createOptionDependent(factory, qualificationsYes, qualificationsHighest);

        LongTextEntry qualificationsOther = factory.createLongTextEntry(
                "Other qualifications",
                "Which other qualifications do you have?",
                EntryStatus.DISABLED);
        doc.addEntry(qualificationsOther);
        qualificationsOther.setSection(educationSec);
        createOptionDependent(factory, qualificationsYes, qualificationsOther);

        DateEntry whenLastStudy = factory.createDateEntry(
                "When last studied for qualifications",
                "When did you last study for any qualifications?",
                EntryStatus.DISABLED);
        doc.addEntry(whenLastStudy);
        whenLastStudy.setSection(educationSec);
        whenLastStudy.setLabel("1.c");
        whenLastStudy.addValidationRule(after1900);
        createOptionDependent(factory, qualificationsYes, whenLastStudy);

        OptionEntry studyingQualifications = factory.createOptionEntry("Studying " +
                "for qualifications at the moment",
                "Are you studying for any qualifications at the moment?");
        doc.addEntry(studyingQualifications);
        studyingQualifications.setSection(educationSec);
        studyingQualifications.setLabel("2");
        Option studyingQualificationsYes = factory.createOption("Yes", "Yes", 1);
        studyingQualifications.addOption(studyingQualificationsYes);
        Option studyingQualificationsNo = factory.createOption("No",
                "No (Go to Qu 3)", 0);
        studyingQualifications.addOption(studyingQualificationsNo);
        studyingQualifications.setOptionCodesDisplayed(false);

        CompositeEntry qualificationsDetail = factory.createComposite(
                "Qualifications details", "If yes, please select the qualifications.");
        doc.addEntry(qualificationsDetail);
        qualificationsDetail.setSection(educationSec);
        qualificationsDetail.setEntryStatus(EntryStatus.DISABLED);
        qualificationsDetail.addRowLabel("Degree level qualification including graduate membership of a " +
                "professional institute or PGCE or higher (include undergraduate " +
                "and postgraduate degrees)");
        qualificationsDetail.addRowLabel("Diploma in higher education");
        qualificationsDetail.addRowLabel("HNC/HND");
        qualificationsDetail.addRowLabel("ONC/OND");
        qualificationsDetail.addRowLabel("BTEC, BEC or TEC");
        qualificationsDetail.addRowLabel("SCOTVEC, SCOTEC or SCOTBEC");
        qualificationsDetail.addRowLabel("Teaching qualification excluding PGCE");
        qualificationsDetail.addRowLabel("Nursing or other " +
                "medical qualification not yet mentioned");
        qualificationsDetail.addRowLabel("Other higher education " +
                "qualification below degree level");
        qualificationsDetail.addRowLabel("A-level or equivalent");
        qualificationsDetail.addRowLabel("SCE highers");
        qualificationsDetail.addRowLabel("NVQ/SVQ");
        qualificationsDetail.addRowLabel("GNVQ/GSVQ");
        qualificationsDetail.addRowLabel("AS-level");
        qualificationsDetail.addRowLabel("Certificate of sixth year " +
                "studies (CSYS) or equivalent");
        qualificationsDetail.addRowLabel("O-Level or equivalent");
        qualificationsDetail.addRowLabel("SCE Standard or Ordinary (O) grade");
        qualificationsDetail.addRowLabel("GCSE");
        qualificationsDetail.addRowLabel("CSE");
        qualificationsDetail.addRowLabel("RSA");
        qualificationsDetail.addRowLabel("City and Guilds");
        qualificationsDetail.addRowLabel("YT certificate/YTP");
        qualificationsDetail.addRowLabel("Any other professional or vocational " +
                "qualification or foreign qualifications (e.g. apprenticeship)");
        createOptionDependent(factory, studyingQualificationsYes, qualificationsDetail);

        TextEntry qualificationsText = factory.createTextEntry("Qualification", "Qualification");
        qualificationsDetail.addEntry(qualificationsText);
        qualificationsText.setSection(educationSec);

        NumericEntry qualificationsNumber = factory.createNumericEntry("Qualification number", "Number");
        qualificationsDetail.addEntry(qualificationsNumber);
        qualificationsNumber.setSection(educationSec);
        qualificationsNumber.setDefaultValue(new Double(0));
        qualificationsNumber.addValidationRule(positive);

        DerivedEntry qualificationsTotalNumber = factory.createDerivedEntry("Qualifications total number",
                "Total number of qualifications", EntryStatus.DISABLED);
        doc.addEntry(qualificationsTotalNumber);
        qualificationsTotalNumber.setSection(educationSec);
        qualificationsTotalNumber.setFormula("a");
        qualificationsTotalNumber.addVariable("a", qualificationsNumber);
        qualificationsTotalNumber.setAggregateOperator("+");
        qualificationsTotalNumber.setComposite(qualificationsDetail);
        createOptionDependent(factory, studyingQualificationsYes, qualificationsTotalNumber);

        LongTextEntry qualificationDetails = factory.createLongTextEntry(
                "Qualifications details long text entry", "Please give details " +
                "of these qualifications (e.g. what, where, full/part time, " +
                "hours, etc)", EntryStatus.DISABLED);
        doc.addEntry(qualificationDetails);
        qualificationDetails.setSection(educationSec);
        createOptionDependent(factory, studyingQualificationsYes, qualificationDetails);

        NumericEntry qualificationsHours = factory.createNumericEntry(
                "Qualifications time",
                "How much time do you spend studying for these qualifications each month?",
                EntryStatus.DISABLED);
        doc.addEntry(qualificationsHours);
        qualificationsHours.setSection(educationSec);
        qualificationsHours.addValidationRule(positive);
        qualificationsHours.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, studyingQualificationsYes, qualificationsHours);

        NarrativeEntry coursesNarrative = factory.createNarrativeEntry(
                "Taught courses",
                "In the last month, have you been on any taught courses or undertaken " +
                "learning of any of the following sorts");
        doc.addEntry(coursesNarrative);
        coursesNarrative.setSection(educationSec);
        coursesNarrative.setLabel("3.a");

        OptionEntry coursesQual = factory.createOptionEntry(
                "Taught courses meant to lead to qualifications",
                "Taught courses meant to lead to qualifications (even if you did not obtain them)");
        doc.addEntry(coursesQual);
        coursesQual.setSection(educationSec);
        coursesQual.addOption(factory.createOption("Yes", "Yes"));
        coursesQual.addOption(factory.createOption("No", "No"));

        OptionEntry coursesSkills = factory.createOptionEntry(
                "Taught courses designed to help you develop skills",
                "Taught courses designed to help you develop skills that you might use in a job");
        doc.addEntry(coursesSkills);
        coursesSkills.setSection(educationSec);
        coursesSkills.addOption(factory.createOption("Yes", "Yes"));
        coursesSkills.addOption(factory.createOption("No", "No"));

        OptionEntry coursesPractical = factory.createOptionEntry(
                "Courses any practical skill",
                "Courses or instruction or tuition in driving, in playing a " +
                        "musical instrument, in an art or craft, in a sport or in any " +
                        "practical skill");
        doc.addEntry(coursesPractical);
        coursesPractical.setSection(educationSec);
        coursesPractical.addOption(factory.createOption("Yes", "Yes"));
        coursesPractical.addOption(factory.createOption("No", "No"));

        OptionEntry eveningClasses = factory.createOptionEntry(
                "Evening classes",
                "Evening classes");
        doc.addEntry(eveningClasses);
        eveningClasses.setSection(educationSec);
        eveningClasses.addOption(factory.createOption("Yes", "Yes"));
        eveningClasses.addOption(factory.createOption("No", "No"));

        OptionEntry learningOwn = factory.createOptionEntry(
                "Learning which involved working on your own",
                "Learning which involved working on your own from a package of materials provided by an employer, college, commercial organisation or other training provider");
        doc.addEntry(learningOwn);
        learningOwn.setSection(educationSec);
        learningOwn.addOption(factory.createOption("Yes", "Yes"));
        learningOwn.addOption(factory.createOption("No", "No"));

        OptionEntry coursesAnyYes = factory.createOptionEntry(
                "Answer yes to any of the above",
                "Did you answer yes to any of the above?");
        doc.addEntry(coursesAnyYes);
        coursesAnyYes.setSection(educationSec);
        Option coursesYes = factory.createOption("Yes", "Yes (Ask 3.b)", 1);
        coursesAnyYes.addOption(coursesYes);
        Option coursesNo = factory.createOption("No", "No (Go to Qu 4)", 0);
        coursesAnyYes.addOption(coursesNo);
        coursesAnyYes.setOptionCodesDisplayed(false);

        NumericEntry numberCourses = factory.createNumericEntry(
                "Number taught courses last month full-time education",
                "How many taught courses have you been involved on in the last month?",
                EntryStatus.DISABLED);
        doc.addEntry(numberCourses);
        numberCourses.setSection(educationSec);
        numberCourses.setLabel("3.b");
        numberCourses.addValidationRule(positive);
        createOptionDependent(factory, coursesYes, numberCourses);

        NumericEntry hoursCourses = factory.createNumericEntry(
                "Qualifications time",
                "How much time do you spend on taught courses each month?",
                EntryStatus.DISABLED);
        doc.addEntry(hoursCourses);
        hoursCourses.setSection(educationSec);
        hoursCourses.addValidationRule(positive);
        hoursCourses.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, coursesYes, hoursCourses);

        NarrativeEntry study = factory.createNarrativeEntry("In the last month, have " +
                "you studied or received training in any of these ways",
                "In the last month, have you studied or received training in " +
                "any of these ways:");
        doc.addEntry(study);
        study.setSection(educationSec);
        study.setLabel("4");

        OptionEntry qualNoCourse = factory.createOptionEntry("Studied for a " +
                "qualification", "Studied for a qualification without taking " +
                "part in a taught course");
        doc.addEntry(qualNoCourse);
        qualNoCourse.setSection(educationSec);
        qualNoCourse.addOption(factory.createOption("Yes", "Yes"));
        qualNoCourse.addOption(factory.createOption("No", "No"));

        OptionEntry supervTraining = factory.createOptionEntry("Received supervised " +
                "training", "Received supervised training while you were " +
                "actually doing a job");
        doc.addEntry(supervTraining);
        supervTraining.setSection(educationSec);
        supervTraining.addOption(factory.createOption("Yes", "Yes"));
        supervTraining.addOption(factory.createOption("No", "No"));

        OptionEntry upToDateDev = factory.createOptionEntry("Keeping " +
                "up-to-date with developments", "Spent time keeping up-to-date " +
                "with developments in the type of work you do without " +
                "taking part in a taught course (e.g. by reading books, manuals " +
                "journals, or attending seminars)");
        doc.addEntry(upToDateDev);
        upToDateDev.setSection(educationSec);
        upToDateDev.addOption(factory.createOption("Yes", "Yes"));
        upToDateDev.addOption(factory.createOption("No", "No"));

        OptionEntry improveKnowledge = factory.createOptionEntry("Trying to " +
                "improve your knowledge", "Spent time deliberately trying to " +
                "improve your knowledge about anything or teach yourself a new " +
                "skill without taking part in a taught course");
        doc.addEntry(improveKnowledge);
        improveKnowledge.setSection(educationSec);
        improveKnowledge.addOption(factory.createOption("Yes", "Yes"));
        improveKnowledge.addOption(factory.createOption("No", "No"));

        OptionEntry trainingAnyYes = factory.createOptionEntry("Yes to any " +
                "training question", "Did you answer yes to any of the above " +
                "questions?");
        doc.addEntry(trainingAnyYes);
        trainingAnyYes.setSection(educationSec);
        Option trainingYes = factory.createOption("Yes", "Yes", 1);
        trainingAnyYes.addOption(trainingYes);
        trainingAnyYes.addOption(factory.createOption("No", "No (Go to Qu 6)", 0));
        trainingAnyYes.setOptionCodesDisplayed(false);

        LongTextEntry trainingDetails = factory.createLongTextEntry("Training details",
                "Details (e.g. what, number of occasions in last month, length " +
                "of time, etc)", EntryStatus.DISABLED);
        doc.addEntry(trainingDetails);
        trainingDetails.setSection(educationSec);
        createOptionDependent(factory, trainingYes, trainingDetails);

        NumericEntry trainingHours = factory.createNumericEntry(
                "Training time",
                "How much time do you spend studying or training in these ways each month?",
                EntryStatus.DISABLED);
        doc.addEntry(trainingHours);
        trainingHours.setSection(educationSec);
        trainingHours.addValidationRule(positive);
        trainingHours.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, trainingYes, trainingHours);

        NumericEntry studyingAtHome = factory.createNumericEntry(
                "Time studying at home",
                "On how many occasions in the last month did you spend time " +
                "studying at home outside of teaching sessions?",
                EntryStatus.DISABLED);
        doc.addEntry(studyingAtHome);
        studyingAtHome.setSection(educationSec);
        studyingAtHome.setLabel("5");
        studyingAtHome.addValidationRule(positive);
        createOptionDependent(factory, trainingYes, studyingAtHome);

        NumericEntry studyLastTime = factory.createNumericEntry(
                "Time studied for last time",
                "How long did you study for the last time you did any?",
                EntryStatus.DISABLED);
        doc.addEntry(studyLastTime);
        studyLastTime.setSection(educationSec);
        studyLastTime.addValidationRule(positive);
        studyLastTime.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, trainingYes, studyLastTime);

        NumericEntry normallyStudy = factory.createNumericEntry(
                "Average time study for",
                "How long on average do you normally study for?",
                EntryStatus.DISABLED);
        doc.addEntry(normallyStudy);
        normallyStudy.setSection(educationSec);
        normallyStudy.addValidationRule(positive);
        normallyStudy.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, trainingYes, normallyStudy);

        OptionEntry lookingForEdu = factory.createOptionEntry("Looking for any " +
                "kind of education", "Thinking of the last month, have you been " +
                "looking for any kind of education/course?");
        doc.addEntry(lookingForEdu);
        lookingForEdu.setSection(educationSec);
        lookingForEdu.setLabel("6");
        Option lookingForEduYes = factory.createOption("Yes", "Yes");
        lookingForEdu.addOption(lookingForEduYes);
        Option lookingForEduNo = factory.createOption("No", "No");
        lookingForEdu.addOption(lookingForEduNo);

        LongTextEntry lookingForEduDetails = factory.createLongTextEntry(
                "Looking for any kind of education - Details)",
                "Details (what, how much time, etc)", EntryStatus.DISABLED);
        doc.addEntry(lookingForEduDetails);
        lookingForEduDetails.setSection(educationSec);
        createOptionDependent(factory, lookingForEduYes, lookingForEduDetails);

        //Score sheet - education
        NarrativeEntry educScoreHeading = factory.createNarrativeEntry(
                "Education Score Heading",
                "Score Sheet");
        doc.addEntry(educScoreHeading);
        educScoreHeading.setSection(educationSec);
        educScoreHeading.setStyle(NarrativeStyle.HEADER);

        DerivedEntry scoreCurrEdu = factory.createDerivedEntry(
                "Score - current education",
                "Is current education present (1) or absent (0)?");
        doc.addEntry(scoreCurrEdu);
        scoreCurrEdu.setSection(educationSec);
        scoreCurrEdu.setDescription("Present = any 'YES' response to Questions 2, 3 or 4; Absent = 'NO' responses to Questions 2, 3 and 4");
        scoreCurrEdu.setFormula("if((a==1||b==1||c==1),1,0)");
        scoreCurrEdu.addVariable("a", studyingQualifications);
        scoreCurrEdu.addVariable("b", coursesAnyYes);
        scoreCurrEdu.addVariable("c", trainingAnyYes);

        DerivedEntry scoreHoursPerWeekEdu = factory.createDerivedEntry(
                "Score - Hours per week education",
                "Hours per week in education over the last month");
        doc.addEntry(scoreHoursPerWeekEdu);
        scoreHoursPerWeekEdu.setSection(educationSec);
        scoreHoursPerWeekEdu.setDescription("This should be calculated by adding all hours spent in education "
                +"(from Questions 2, 3 4 and 5) and dividing by 4 to get a weekly average.");
        scoreHoursPerWeekEdu.setFormula("(a+b+c+(d*e))/4");
        scoreHoursPerWeekEdu.addVariable("a", qualificationsHours);
        scoreHoursPerWeekEdu.addVariable("b", hoursCourses);
        scoreHoursPerWeekEdu.addVariable("c", trainingHours);
        scoreHoursPerWeekEdu.addVariable("d", studyingAtHome);
        scoreHoursPerWeekEdu.addVariable("e", normallyStudy);

        DerivedEntry scoreNumDiffCourses = factory.createDerivedEntry(
                "Score - Num Different Courses",
                "Number of different courses taken part in over last month");
        doc.addEntry(scoreNumDiffCourses);
        scoreNumDiffCourses.setSection(educationSec);
        scoreNumDiffCourses.setDescription("Taken from Questions 2, 3 and 4");
        scoreNumDiffCourses.setFormula("a+b");
        scoreNumDiffCourses.addVariable("a", qualificationsTotalNumber);
        scoreNumDiffCourses.addVariable("b", numberCourses);

        // Voluntary Work section
        Section volWorkSec = factory.createSection("Voluntary Work section");
        doc.addSection(volWorkSec);
        volWorkSec.setDisplayText("Voluntary Work");
        SectionOccurrence volWorkSecOcc = factory
                .createSectionOccurrence("Voluntary Work section occurrence");
        volWorkSec.addOccurrence(volWorkSecOcc);

        NarrativeEntry volWorkNarrative = factory.createNarrativeEntry("Voluntary work narrative",
                "Voluntary work is " +
                "work that people may do for which they are not paid, except " +
                "perhaps for expenses.");
        doc.addEntry(volWorkNarrative);
        volWorkNarrative.setSection(volWorkSec);

        OptionEntry volWork = factory.createOptionEntry("Voluntary work option",
                "Have you done any voluntary work through a group or on behalf " +
                        "of an organisation at any time during the last month?");
        doc.addEntry(volWork);
        volWork.setSection(volWorkSec);
        volWork.setLabel("1");
        Option volWorkYes = factory.createOption("Yes", "Yes", 1);
        volWork.addOption(volWorkYes);
        volWork.addOption(factory.createOption("No  ", "No", 0));
        volWork.setOptionCodesDisplayed(false);

        LongTextEntry volWorkDetails = factory.createLongTextEntry("Voluntary " +
                "work details", "Details", EntryStatus.DISABLED);
        doc.addEntry(volWorkDetails);
        volWorkDetails.setSection(volWorkSec);
        createOptionDependent(factory, volWorkYes, volWorkDetails);

        NumericEntry timesWork = factory.createNumericEntry("Different times " +
                "did this work",
                "How many different times did you do this work during the last " +
                "month?", EntryStatus.DISABLED);
        doc.addEntry(timesWork);
        timesWork.setSection(volWorkSec);
        timesWork.setLabel("2");
        timesWork.addValidationRule(positive);
        createOptionDependent(factory, volWorkYes, timesWork);

        NumericEntry longWork = factory.createNumericEntry(
                "How long did you work for, the last time you did this?",
                "How long did you work for, the last time you did this?",
                EntryStatus.DISABLED);
        doc.addEntry(longWork);
        longWork.setSection(volWorkSec);
        longWork.setLabel("3");
        longWork.addValidationRule(positive);
        longWork.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, volWorkYes, longWork);

        NumericEntry longWorkAverage = factory.createNumericEntry(
                "How long do you normally spend doing this?",
                "How long do you normally spend doing this?",
                EntryStatus.DISABLED);
        doc.addEntry(longWorkAverage);
        longWorkAverage.setSection(volWorkSec);
        longWorkAverage.addValidationRule(positive);
        longWorkAverage.addUnit(UnitWrapper.instance().getUnit("hours"));
            createOptionDependent(factory, volWorkYes, longWorkAverage);

        //score sheet - voluntary work
        NarrativeEntry volworkScoreHeading = factory.createNarrativeEntry(
                "Voluntary Work Score Heading",
                "Score Sheet");
        doc.addEntry(volworkScoreHeading);
        volworkScoreHeading.setSection(volWorkSec);
        volworkScoreHeading.setStyle(NarrativeStyle.HEADER);

        DerivedEntry scoreVoluntWork = factory.createDerivedEntry(
                "Score - Voluntary Work",
                "Is voluntary work present (1) or absent (0)?");
        doc.addEntry(scoreVoluntWork);
        scoreVoluntWork.setSection(volWorkSec);
        scoreVoluntWork.setDescription("Present = 'YES' response to Question 1 or Question 3 (a) from Employment section; "
                +"Absent = 'NO' response to Question 1");
        scoreVoluntWork.setFormula("if((a==1||b==1),1,0)");
        scoreVoluntWork.addVariable("a", unpaidWorkOption);
        scoreVoluntWork.addVariable("b", volWork);

        DerivedEntry scoreVoluntHours = factory.createDerivedEntry("Score - Voluntary work hours per week",
                "Hours per week spent in voluntary work over the last month");
        doc.addEntry(scoreVoluntHours);
        scoreVoluntHours.setSection(volWorkSec);
        scoreVoluntHours.setDescription("This should be calculated by multiplying number of times "
                +"(Question 2) by average length of time (Question 3) and dividing the result by 4 "
                +"to get a weekly average.");
        scoreVoluntHours.setFormula("a*b/4");
        scoreVoluntHours.addVariable("a",timesWork);
        scoreVoluntHours.addVariable("b",longWorkAverage);

        // Leisure Activities section
        Section leisureSec = factory.createSection("Leisure Activities section");
        doc.addSection(leisureSec);
        leisureSec.setDisplayText("Leisure Activities");
        SectionOccurrence leisureSecOcc = factory
                .createSectionOccurrence("Leisure Activities section occurrence");
        leisureSec.addOccurrence(leisureSecOcc);

        CompositeEntry leisureActivities = factory.createComposite("Leisure activities",
                "I am now going to ask some questions about things that some " +
                        "people do in their spare time.  For each activity that I " +
                        "mention could you please tell me whether of not you have done " +
                        "this in the last month, AND how often you normally do this.");
        doc.addEntry(leisureActivities);
        leisureActivities.setSection(leisureSec);
        leisureActivities.setLabel("1");
        leisureActivities.addRowLabel("Been to cinema, film society or club");
        leisureActivities.addRowLabel("Been to a sports event as a spectator");
        leisureActivities.addRowLabel("Been to a play, musical or pantomime");
        leisureActivities.addRowLabel("Been to the opera");
        leisureActivities.addRowLabel("Been to a concert or performance of " +
                "classical music of any kind");
        leisureActivities.addRowLabel("Been to any other gig or live music " +
                "performance (e.g. pop, rock or jazz concert, blues or folk club)");
        leisureActivities.addRowLabel("Been to the ballet or to a " +
                "modern/contemporary dance performance");
        leisureActivities.addRowLabel("Been to a museum or art gallery");
        leisureActivities.addRowLabel("Been to an historic house, castle or " +
                "other heritage site or building");
        leisureActivities.addRowLabel("Been to a library");
        leisureActivities.addRowLabel("Been out to eat or drink at a café, " +
                "restaurant, pub or wine bar");
        leisureActivities.addRowLabel("Been to a shopping centre, or mall, " +
                "apart from regular shopping for food and household items");
        leisureActivities.addRowLabel("Been to a car boot sale, antiques fair " +
                "or craft market or similar apart from regular shopping for " +
                "food and household items");
        leisureActivities.addRowLabel("Been to a theme park, fairground, fair or " +
                "carnival");
        leisureActivities.addRowLabel("Been to a zoo, wildlife reserve, aquarium " +
                "or farm park");
        leisureActivities.addRowLabel("Been to some other place of entertainment " +
                "(e.g. dance, club, bingo, casino)");
        leisureActivities.addRowLabel("Been on any other outdoor trips " +
                "(including going to places of natural beauty, picnics, going " +
                "for a drive or going to the beach)");
        leisureActivities.addRowLabel("Other");

        TextEntry leisureActivitiesLabel = factory.createTextEntry(
                "Lesirue Activity", "Activity");
        leisureActivities.addEntry(leisureActivitiesLabel);
        leisureActivitiesLabel.setSection(leisureSec);

        NumericEntry leisureActivitiesOption = factory.createNumericEntry(
                "Leisure activity number of times", "Number of times");
        leisureActivities.addEntry(leisureActivitiesOption);
        leisureActivitiesOption.setSection(leisureSec);
        leisureActivitiesOption.setDefaultValue(new Double(0.0));

        NumericEntry leisureActivitiesFreq = factory.createNumericEntry("Amount of time",
                "Amount of time");
        leisureActivities.addEntry(leisureActivitiesFreq);
        leisureActivitiesFreq.setSection(leisureSec);
        leisureActivitiesFreq.setDefaultValue(new Double(0.0));
        leisureActivitiesFreq.addUnit(hours);

        TextEntry timeLeisureOther = factory.createTextEntry(
                "Time leisure activities other",
                "If you chose other in the previous question, please specify.",
                EntryStatus.OPTIONAL);
        doc.addEntry(timeLeisureOther);
        timeLeisureOther.setSection(leisureSec);

        CompositeEntry physical = factory.createComposite("Physical " +
                "activities", "On these cards is a list of sports and " +
                "physical activities.  Could you please tell me whether or not " +
                "you took part in any of them in the last month AND how often.");
        doc.addEntry(physical);
        physical.setSection(leisureSec);
        physical.setLabel("2");
        physical.addRowLabel("Swimming or diving");
        physical.addRowLabel("Cycling");
        physical.addRowLabel("Indoor or outdoor bowls");
        physical.addRowLabel("Tenpin bowling");
        physical.addRowLabel("Keep fit, aerobics, yoga, dance exercise");
        physical.addRowLabel("Martial arts");
        physical.addRowLabel("Weight training or weight lifting");
        physical.addRowLabel("Gymnastics");
        physical.addRowLabel("Snooker, pool or billiards");
        physical.addRowLabel("Darts");
        physical.addRowLabel("Rugby");
        physical.addRowLabel("Football");
        physical.addRowLabel("Gaelic sports");
        physical.addRowLabel("Cricket");
        physical.addRowLabel("Hockey");
        physical.addRowLabel("Netball");
        physical.addRowLabel("Tennis");
        physical.addRowLabel("Badminton");
        physical.addRowLabel("Squash");
        physical.addRowLabel("Basketball");
        physical.addRowLabel("Table tennis");
        physical.addRowLabel("Track and field athletics");
        physical.addRowLabel("Jogging, cross country, road running");
        physical.addRowLabel("Angling/fishing");
        physical.addRowLabel("Yachting or dinghy sailing");
        physical.addRowLabel("Canoeing");
        physical.addRowLabel("Windsurfing/board sailing");
        physical.addRowLabel("Ice-skating");
        physical.addRowLabel("Curling");
        physical.addRowLabel("Golf");
        physical.addRowLabel("Skiing");
        physical.addRowLabel("Horse riding");
        physical.addRowLabel("Climbing/mountaineering");
        physical.addRowLabel("Motor sports");
        physical.addRowLabel("Shooting");
        physical.addRowLabel("Walking or hiking for 2 miles or more (recreationally)");
        physical.addRowLabel("Volleyball");
        physical.addRowLabel("Other");

        TextEntry physicalLabel = factory.createTextEntry(
                "Physical Activity", "Activity");
        physical.addEntry(physicalLabel);
        physicalLabel.setSection(leisureSec);

        NumericEntry physicalOption = factory.createNumericEntry(
                "Physical activity number of times", "Number of times");
        physical.addEntry(physicalOption);
        physicalOption.setSection(leisureSec);
        physicalOption.setDefaultValue(new Double(0.0));

        NumericEntry physicalFreq = factory.createNumericEntry("Amount of time",
                "Amount of time");
        physical.addEntry(physicalFreq);
        physicalFreq.setSection(leisureSec);
        physicalFreq.setDefaultValue(new Double(0.0));
        physicalFreq.addUnit(hours);

        TextEntry physicalOther = factory.createTextEntry(
                "Physical Other",
                "If 'Other' was selected in the previous table, please specify",
                EntryStatus.OPTIONAL);
        doc.addEntry(physicalOther);
        physicalOther.setSection(leisureSec);

        NumericEntry socialising = factory.createNumericEntry(
                "Time spent socialising",
                "How much time do you spend socialising (each month)?");
        doc.addEntry(socialising);
        socialising.setSection(leisureSec);
        socialising.setLabel("3");
        socialising.addValidationRule(positive);
        socialising.addUnit(UnitWrapper.instance().getUnit("hours"));

        NumericEntry freqFriends = factory.createNumericEntry(
                "How often see friends",
                "How often do you see friends, either visiting them or receiving visitors (each month)?");
        doc.addEntry(freqFriends);
        freqFriends.setSection(leisureSec);
        freqFriends.addValidationRule(positive);

        NumericEntry timeFriends = factory.createNumericEntry(
                "Time spent seeing friends",
                "How much time did you tend to spend socialising on each occasion on average?");
        doc.addEntry(timeFriends);
        timeFriends.setSection(leisureSec);
        timeFriends.addValidationRule(positive);
        timeFriends.addUnit(UnitWrapper.instance().getUnit("hours"));

        NarrativeEntry timeNarrative = factory.createNarrativeEntry(
                "How much time do you spend",
                "How much time do you spend:");
        doc.addEntry(timeNarrative);
        timeNarrative.setSection(leisureSec);
        timeNarrative.setLabel("4");

        NumericEntry resting = factory.createNumericEntry(
                "Time spent resting ",
                "Resting, i.e. taking time out and doing nothing (but not " +
                "sleeping)? Per month, on average.");
        doc.addEntry(resting);
        resting.setSection(leisureSec);
        resting.addValidationRule(positive);
        resting.addUnit(UnitWrapper.instance().getUnit("hours"));

        NumericEntry television = factory.createNumericEntry(
                "Time spent watching television",
                "Watching television or listening to the radio? Per month, on average.");
        doc.addEntry(television);
        television.setSection(leisureSec);
        television.addValidationRule(positive);
        television.addUnit(UnitWrapper.instance().getUnit("hours"));

        //Score sheet - leisure
        NarrativeEntry leisureScoreHeading = factory.createNarrativeEntry(
                "Leisure Score Heading",
                "Score Sheet");
        doc.addEntry(leisureScoreHeading);
        leisureScoreHeading.setSection(leisureSec);
        leisureScoreHeading.setStyle(NarrativeStyle.HEADER);

        DerivedEntry scoreLeisure = factory.createDerivedEntry(
                "Score - Leisure",
                "Are leisure activities present (1) or absent (0)?");
        doc.addEntry(scoreLeisure);
        scoreLeisure.setSection(leisureSec);
        scoreLeisure.setDescription("Taken from Question 1");
        scoreLeisure.setFormula("if(a>0,1,0)");
        scoreLeisure.addVariable("a", leisureActivitiesOption);
        scoreLeisure.setAggregateOperator("||");
        scoreLeisure.setComposite(leisureActivities);

        DerivedEntry scoreLeisureHours = factory.createDerivedEntry(
                "Score - Leisure Hours Per Week",
                "Hours per week spent in leisure activities over the last month");
        doc.addEntry(scoreLeisureHours);
        scoreLeisureHours.setSection(leisureSec);
        scoreLeisureHours.setDescription("This should be calculated by multiplying number of times "
                +"by average length of time for each activity.  Then sum all of these and divide "
                +"the result by 4 to get a weekly average.");
        scoreLeisureHours.setFormula("a*b/4");
        scoreLeisureHours.setAggregateOperator("+");
        scoreLeisureHours.setComposite(leisureActivities);
        scoreLeisureHours.addVariable("a",leisureActivitiesOption);
        scoreLeisureHours.addVariable("b",leisureActivitiesFreq);

        DerivedEntry scoreLeisureNum = factory.createDerivedEntry("Score - Number Leisure Activities",
                "Number of leisure activities taken part in over last month");
        doc.addEntry(scoreLeisureNum);
        scoreLeisureNum.setSection(leisureSec);
        scoreLeisureNum.setDescription("Taken from Question 1");
        scoreLeisureNum.setFormula("if(a>0,1,0)");
        scoreLeisureNum.setAggregateOperator("+");
        scoreLeisureNum.setComposite(leisureActivities);
        scoreLeisureNum.addVariable("a", leisureActivitiesOption);

        DerivedEntry scoreSport = factory.createDerivedEntry("Score - Sport",
                "Are sport/physical activities present (1) or absent (0)?");
        doc.addEntry(scoreSport);
        scoreSport.setSection(leisureSec);
        scoreSport.setDescription("Taken from Question 2");
        scoreSport.setFormula("if(a>0,1,0)");
        scoreSport.addVariable("a", physicalOption);
        scoreSport.setAggregateOperator("||");
        scoreSport.setComposite(physical);

        DerivedEntry scoreSportHours = factory.createDerivedEntry(
                "Score - Sport Hours Per Week",
                "Hours per week spent in sport/physical activities over the last month");
        doc.addEntry(scoreSportHours);
        scoreSportHours.setSection(leisureSec);
        scoreSportHours.setDescription("This should be calculated by multiplying number of times "
                +"by average length of time for each activity.  Then sum all of these and divide "
                +"the result by 4 to get a weekly average.");
        scoreSportHours.setFormula("(a*b)/4");
        scoreSportHours.setAggregateOperator("+");
        scoreSportHours.setComposite(physical);
        scoreSportHours.addVariable("a",physicalOption);
        scoreSportHours.addVariable("b",physicalFreq);

        DerivedEntry scoreSportNum = factory.createDerivedEntry("Score - Number Sport Activities",
                "Number of sport/physical activities taken part in over last month");
        doc.addEntry(scoreSportNum);
        scoreSportNum.setSection(leisureSec);
        scoreSportNum.setDescription("Taken from Question 2");
        scoreSportNum.setFormula("if(a>0,1,0)");
        scoreSportNum.setAggregateOperator("+");
        scoreSportNum.setComposite(physical);
        scoreSportNum.addVariable("a", physicalOption);

        DerivedEntry scoreSocialising = factory.createDerivedEntry(
                "Score - Hours per week socialising",
                "Hours per week over last month spent socialising");
        doc.addEntry(scoreSocialising);
        scoreSocialising.setSection(leisureSec);
        scoreSocialising.setFormula("a/4");
        scoreSocialising.addVariable("a", socialising);

        DerivedEntry scoreResting = factory.createDerivedEntry(
                "Score - Hours per week resting",
                "Hours per week over last month spent resting");
        doc.addEntry(scoreResting);
        scoreResting.setSection(leisureSec);
        scoreResting.setFormula("a/4");
        scoreResting.addVariable("a", resting);

        // Hobbies section
        Section hobbiesSec = factory.createSection("Hobbies section");
        doc.addSection(hobbiesSec);
        hobbiesSec.setDisplayText("Hobbies");
        SectionOccurrence hobbiesSecOcc = factory
                .createSectionOccurrence("Hobbies section occurrence");
        hobbiesSec.addOccurrence(hobbiesSecOcc);

        CompositeEntry hobbies = factory.createComposite("Hobbies list",
                "Do you have any hobbies?  Show list of examples.");
        doc.addEntry(hobbies);
        hobbies.setSection(hobbiesSec);
        hobbies.setLabel("1");
        hobbies.addRowLabel("Painting, drawing, etc");
        hobbies.addRowLabel("Pottery, sculpture");
        hobbies.addRowLabel("Photography, making videos");
        hobbies.addRowLabel("Working with textiles");
        hobbies.addRowLabel("Singing");
        hobbies.addRowLabel("Acting, role play, drama");
        hobbies.addRowLabel("Playing a musical instrument");
        hobbies.addRowLabel("Writing stories or poetry, keeping a personal diary");
        hobbies.addRowLabel("Collecting, e.g. stamps, coins, etc");
        hobbies.addRowLabel("Computing, e.g. programming, using the internet, email, etc");
        hobbies.addRowLabel("Writing letters to friends, pen pals, etc");
        hobbies.addRowLabel("Doing jigsaws or playing cards by self");
        hobbies.addRowLabel("Playing games with others, e.g. board games or computer games");
        hobbies.addRowLabel("Bingo");
        hobbies.addRowLabel("Other");

        TextEntry hobby = factory.createTextEntry("Hobby", "Hobby");
        hobbies.addEntry(hobby);
        hobby.setSection(hobbiesSec);

        OptionEntry hobbiesOption = factory.createOptionEntry("Hobbies option entry", "Yes/No");
        hobbies.addEntry(hobbiesOption);
        hobbiesOption.setSection(hobbiesSec);
        hobbiesOption.setOptionCodesDisplayed(false);
        Option hobbyNo = factory.createOption("No", 0);
        hobbiesOption.addOption(hobbyNo);
        hobbiesOption.addOption(factory.createOption("Yes", 1));
        hobbiesOption.setDefaultValue(hobbyNo);

        TextEntry hobbyOther = factory.createTextEntry(
                "Hobby Other",
                "If 'Other' was selected in the previous table, please specify",
                EntryStatus.OPTIONAL);
        doc.addEntry(hobbyOther);
        hobbyOther.setSection(hobbiesSec);

        NumericEntry timeHobbies = factory.createNumericEntry(
                "How much time in hobbies",
                "How much time do you spend on hobbies each week (on average)?");
        doc.addEntry(timeHobbies);
        timeHobbies.setSection(hobbiesSec);
        timeHobbies.setLabel("2");
        timeHobbies.addValidationRule(positive);
        timeHobbies.addUnit(UnitWrapper.instance().getUnit("hours"));

        //Score sheet - hobbies
        NarrativeEntry hobbiesScoreHeading = factory.createNarrativeEntry(
                "Hobbies Score Heading",
                "Score Sheet");
        doc.addEntry(hobbiesScoreHeading);
        hobbiesScoreHeading.setSection(hobbiesSec);
        hobbiesScoreHeading.setStyle(NarrativeStyle.HEADER);

        DerivedEntry scoreHobbies = factory.createDerivedEntry(
                "Score - Hobbies",
                "Are hobbies present (1) or absent (0)?");
        doc.addEntry(scoreHobbies);
        scoreHobbies.setSection(hobbiesSec);
        scoreHobbies.setFormula("a");
        scoreHobbies.addVariable("a", hobbiesOption);
        scoreHobbies.setAggregateOperator("||");
        scoreHobbies.setComposite(hobbies);

        DerivedEntry scoreHobbiesHours = factory.createDerivedEntry("Score - Hobbies Hours per week",
                "Hours per week spent on hobbies over the last month");
        doc.addEntry(scoreHobbiesHours);
        scoreHobbiesHours.setSection(hobbiesSec);
        scoreHobbiesHours.setFormula("a");
        scoreHobbiesHours.addVariable("a", timeHobbies);

        DerivedEntry scoreHobbiesNumber = factory.createDerivedEntry(
                "Score - Number of Hobbies",
                "Number of hobbies taken part in over last month");
        doc.addEntry(scoreHobbiesNumber);
        scoreHobbiesNumber.setSection(hobbiesSec);
        scoreHobbiesNumber.setFormula("a");
        scoreHobbiesNumber.setAggregateOperator("+");
        scoreHobbiesNumber.setComposite(hobbies);
        scoreHobbiesNumber.addVariable("a", hobbiesOption);

        // Child care section
        Section childCareSec = factory.createSection("Child care section");
        doc.addSection(childCareSec);
        childCareSec.setDisplayText("Child care");
        SectionOccurrence childCareSecOcc = factory
                .createSectionOccurrence("Child care section occurrence");
        childCareSec.addOccurrence(childCareSecOcc);

        OptionEntry responsibleChildren = factory.createOptionEntry(
                "Responsible for care of children",
                "Are you responsible for the care of any children?");
        doc.addEntry(responsibleChildren);
        responsibleChildren.setSection(childCareSec);
        responsibleChildren.setLabel("1");
        Option responsibleChildrenYes = factory.createOption("Yes", "Yes (Ask 2)", 1);
        responsibleChildren.addOption(responsibleChildrenYes);
        responsibleChildren.addOption(factory.createOption("No", "No", 0));
        responsibleChildren.setOptionCodesDisplayed(false);

        NumericEntry numberChildren = factory.createNumericEntry(
                "How many?",
                "How many?", EntryStatus.DISABLED);
        doc.addEntry(numberChildren);
        numberChildren.setSection(childCareSec);
        numberChildren.setLabel("2");
        numberChildren.addValidationRule(positive);
        createOptionDependent(factory, responsibleChildrenYes, numberChildren);

        CompositeEntry ageChildrenComp = factory.createComposite("How old are they?",
                "How old are they?");
        doc.addEntry(ageChildrenComp);
        ageChildrenComp.setSection(childCareSec);
        ageChildrenComp.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, responsibleChildrenYes, ageChildrenComp);
        NumericEntry ageChildren = factory.createNumericEntry(
                "How old are they?");
        ageChildrenComp.addEntry(ageChildren);
        ageChildren.setSection(childCareSec);
        ageChildren.addUnit(UnitWrapper.instance().getUnit("weeks"));
        ageChildren.addUnit(UnitWrapper.instance().getUnit("months"));
        ageChildren.addUnit(UnitWrapper.instance().getUnit("years"));
        ageChildren.addValidationRule(positive);

        CompositeEntry timeChildren = factory.createComposite(
                "Time spent with children",
                "How much time do you spend doing things with your " +
                        "children each week?");
        doc.addEntry(timeChildren);
        timeChildren.setSection(childCareSec);
        timeChildren.setEntryStatus(EntryStatus.DISABLED);
        timeChildren.setLabel("3");
        timeChildren.addRowLabel("Physical care (e.g. feeding, dressing, washing)");
        timeChildren.addRowLabel("Supervision (inside and outside)");
        timeChildren.addRowLabel("Teaching children (e.g. helping with homework)");
        timeChildren.addRowLabel("Reading, playing and talking with children");
        timeChildren.addRowLabel("Accompanying child (e.g. to school, doctor, friend’s house, etc)");
        createOptionDependent(factory, responsibleChildrenYes, timeChildren);

        TextEntry timeChildDesc = factory.createTextEntry(
                "Time with child - activity",
                "Activity");
        timeChildren.addEntry(timeChildDesc);
        timeChildDesc.setSection(childCareSec);

        NumericEntry timeChildHours = factory.createNumericEntry(
                "Time with children - Hours",
                "Time");
        timeChildren.addEntry(timeChildHours);
        timeChildHours.setSection(childCareSec);
        timeChildHours.addValidationRule(positive);
        timeChildHours.addUnit(UnitWrapper.instance().getUnit("hours"));
        timeChildHours.setDefaultValue(new Double(0));

        //Score sheet - childcare
        NarrativeEntry childcareScoreHeading = factory.createNarrativeEntry(
                "Childcare Score Heading",
                "Score Sheet");
        doc.addEntry(childcareScoreHeading);
        childcareScoreHeading.setSection(childCareSec);
        childcareScoreHeading.setStyle(NarrativeStyle.HEADER);

        DerivedEntry scoreChildcare = factory.createDerivedEntry(
                "Score - Childcare",
                "Is childcare applicable (1) or non-applicable (0)?");
        doc.addEntry(scoreChildcare);
        scoreChildcare.setSection(childCareSec);
        scoreChildcare.setFormula("a");
        scoreChildcare.addVariable("a", responsibleChildren);

        DerivedEntry scoreChildcareHours = factory.createDerivedEntry("Score - Childcare Hours",
                "Hours per week spent on childcare");
        doc.addEntry(scoreChildcareHours);
        scoreChildcareHours.setSection(childCareSec);
        scoreChildcareHours.setDescription("Taken from Question 3");
        scoreChildcareHours.setFormula("a");
        scoreChildcareHours.addVariable("a", timeChildHours);
        scoreChildcareHours.setAggregateOperator("+");
        scoreChildcareHours.setComposite(timeChildren);

        // Housework And Chores section
        Section houseSec = factory.createSection("Housework And Chores section");
        doc.addSection(houseSec);
        houseSec.setDisplayText("Housework And Chores");
        SectionOccurrence houseSecOcc = factory
                .createSectionOccurrence("Housework And Chores section occurrence");
        houseSec.addOccurrence(houseSecOcc);

        NarrativeEntry houseWorkNarrative = factory.createNarrativeEntry(
                "Housework time", "How much time do you spend doing housework " +
                "and chores per week? Ask individual to include checklist in " +
                "their estimate.");
        doc.addEntry(houseWorkNarrative);
        houseWorkNarrative.setSection(houseSec);

        CompositeEntry houseWork = factory.createComposite(
                "Housework time",
                "How much time do you spend doing housework " +
                        "and chores per week? Ask individual to include checklist in " +
                        "their estimate.");
        doc.addEntry(houseWork);
        houseWork.setSection(houseSec);
        houseWork.setLabel("1");
        houseWork.addRowLabel("Food management and preparation");
        houseWork.addRowLabel("Cleaning, dusting, vacuuming, washing dishes");
        houseWork.addRowLabel("Food shopping");
        houseWork.addRowLabel("Washing");
        houseWork.addRowLabel("Gardening");
        houseWork.addRowLabel("DIY and repairs");

        TextEntry houseWorkDesc = factory.createTextEntry(
                "Housework - activity",
                "Activity");
        houseWork.addEntry(houseWorkDesc);
        houseWorkDesc.setSection(houseSec);

        NumericEntry houseWorkHours = factory.createNumericEntry(
                "Housework - time",
                "Time");
        houseWork.addEntry(houseWorkHours);
        houseWorkHours.setSection(houseSec);
        houseWorkHours.addValidationRule(positive);
        houseWorkHours.addUnit(UnitWrapper.instance().getUnit("hours"));
        houseWorkHours.setDefaultValue(new Double(0));

        DerivedEntry timeTotal = factory.createDerivedEntry(
                "Total time",
                "Total time doing housework");
        doc.addEntry(timeTotal);
        timeTotal.setSection(houseSec);
        timeTotal.setAggregateOperator("+");
        timeTotal.setComposite(houseWork);
        timeTotal.addVariable("a", houseWorkHours);
        timeTotal.setFormula("a");

        CompositeEntry houseWorkOthers = factory.createComposite("Other",
                "Other (please state)");
        doc.addEntry(houseWorkOthers);
        houseWorkOthers.setSection(houseSec);
        houseWorkOthers.setEntryStatus(EntryStatus.OPTIONAL);
        TextEntry otherChore = factory.createTextEntry("Chore", "Chore");
        houseWorkOthers.addEntry(otherChore);
        otherChore.setSection(houseSec);
        NumericEntry otherTime = factory.createNumericEntry("Time", "Time");
        houseWorkOthers.addEntry(otherTime);
        otherTime.setSection(houseSec);
        otherTime.addValidationRule(positive);
        otherTime.addUnit(UnitWrapper.instance().getUnit("hours"));

        DerivedEntry otherTimeTotal = factory.createDerivedEntry(
                "Total time",
                "Total time doing other housework activities");
        doc.addEntry(otherTimeTotal);
        otherTimeTotal.setSection(houseSec);
        otherTimeTotal.setAggregateOperator("+");
        otherTimeTotal.setComposite(houseWorkOthers);
        otherTimeTotal.addVariable("a", otherTime);
        otherTimeTotal.setFormula("a");

        //Score sheet - housework
        NarrativeEntry houseworkScoreHeading = factory.createNarrativeEntry(
                "Housework Score Heading",
                "Score Sheet");
        doc.addEntry(houseworkScoreHeading);
        houseworkScoreHeading.setSection(houseSec);
        houseworkScoreHeading.setStyle(NarrativeStyle.HEADER);

        DerivedEntry scoreHouseworkHours = factory.createDerivedEntry(
                "Score - hours per week housework",
                "Hours per week spent on housework and chores");
        doc.addEntry(scoreHouseworkHours);
        scoreHouseworkHours.setSection(houseSec);
        scoreHouseworkHours.setDescription("Taken from estimate of average time including items from checklist in estimate");
        scoreHouseworkHours.setFormula("a+b");
        scoreHouseworkHours.addVariable("a", timeTotal);
        scoreHouseworkHours.addVariable("b", otherTimeTotal);


        // Other activities section
        Section otherSec = factory.createSection("Other Activities section");
        doc.addSection(otherSec);
        otherSec.setDisplayText("Other Activities");
        SectionOccurrence otherSecOcc = factory
                .createSectionOccurrence("Other Activities section occurrence");
        otherSec.addOccurrence(otherSecOcc);

        NumericEntry timeSleeping = factory.createNumericEntry("Time sleeping",
                "How much time do you spend sleeping per day (on average)?  " +
                "This includes sleep at night time and naps during the day.  " +
                "Ask about good and bad days.");
        doc.addEntry(timeSleeping);
        timeSleeping.setSection(otherSec);
        timeSleeping.addUnit(UnitWrapper.instance().getUnit("hours"));
        timeSleeping.setLabel("1");
        timeSleeping.addValidationRule(positive);

        LongTextEntry descOthers = factory.createLongTextEntry(
                "Activites not talked about",
                "Do you spend time doing any activities not already asked about?",
                EntryStatus.OPTIONAL);
        doc.addEntry(descOthers);
        descOthers.setSection(otherSec);
        descOthers.setLabel("2");

        NumericEntry timeOthers = factory.createNumericEntry(
                "Time doing activities not talked about",
                "How much time each week on average do you spend doing activities not already asked about?");
        doc.addEntry(timeOthers);
        timeOthers.setSection(otherSec);
        timeOthers.setLabel("2a");
        timeOthers.addUnit(UnitWrapper.instance().getUnit("hours"));
        timeOthers.addValidationRule(positive);

        NumericEntry numberOthers = factory.createNumericEntry(
                "Number activities not talked about",
                "How many activities not already asked about do you spend time doing?");
        doc.addEntry(numberOthers);
        numberOthers.setSection(otherSec);
        numberOthers.setLabel("2b");
        numberOthers.addValidationRule(positive);

        //Score sheet - other activities
        NarrativeEntry othersScoreHeading = factory.createNarrativeEntry(
                "Others Score Heading",
                "Score Sheet");
        doc.addEntry(othersScoreHeading);
        othersScoreHeading.setSection(otherSec);
        othersScoreHeading.setStyle(NarrativeStyle.HEADER);

        DerivedEntry scoreHoursSleeping = factory.createDerivedEntry("Score - Hours Sleeping",
                "Hours per day spent sleeping");
        doc.addEntry(scoreHoursSleeping);
        scoreHoursSleeping.setSection(otherSec);
        scoreHoursSleeping.setDescription("From Question 1");
        scoreHoursSleeping.setFormula("a");
        scoreHoursSleeping.addVariable("a", timeSleeping);

        DerivedEntry scoreHoursOther = factory.createDerivedEntry("Score - Hours Other Activities",
                "Hours per week spent on other activities over the last month");
        doc.addEntry(scoreHoursOther);
        scoreHoursOther.setSection(otherSec);
        scoreHoursOther.setDescription("From Question 2a");
        scoreHoursOther.setFormula("a");
        scoreHoursOther.addVariable("a", timeOthers);

        DerivedEntry scoreNumberOther = factory.createDerivedEntry(
                "Score - Number Other Activities",
                "Number of other activities taken part in over last month");
        doc.addEntry(scoreNumberOther);
        scoreNumberOther.setSection(otherSec);
        scoreNumberOther.setDescription("From Question 2b");
        scoreNumberOther.setFormula("a");
        scoreNumberOther.addVariable("a",numberOthers);

        return doc;
    }

}
