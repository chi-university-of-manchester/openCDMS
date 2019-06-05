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

import org.psygrid.common.UnitWrapper;
import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class EPQv3 extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "EPQv3",
                "Economic Patient Questionnaire v3");

        createDocumentStatuses(factory, doc);

        //add section 1 (Service Receipt)
        addServiceReceiptSection(doc, factory, "A", false);

        //add section 2 (Extra Costs)
        addExtraCostsSection(doc, factory, "B", false);

        //add section 3 (Employment and Income)
        addEmploymentSection(doc, factory, "C", false);

        //add section 4 (Education)
        addEducationSection(doc, factory, "D", false);

        //add section 5 (Other Activities)
        addOtherActivitiesSection(doc, factory, "E", false);

        //add section 6 (Criminal Justice Service)
        addCriminalJusticeServiceSection(doc, factory, "F", false);

        //add section 7 (Interviewer Comments)
        addInterviewerCommentsSection(doc, factory, "G", false);

        return doc;
    }

    public static void addEmploymentSection(Document doc, Factory factory,
    		String sec, boolean followUp){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");
        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");


        //Employment and Income section
        Section employmentSec = factory.createSection("Your employment section");
        doc.addSection(employmentSec);
        employmentSec.setDisplayText("Your Employment");
        SectionOccurrence employmentSecOcc = factory.createSectionOccurrence(
                "Your Employment section occurrence");
        employmentSec.addOccurrence(employmentSecOcc);
        employmentSecOcc.setLabel(sec);

        CompositeEntry q1 = factory.createComposite("QC1",
                "How have you been employed in the last 3 months?");
        doc.addEntry(q1);
        q1.setSection(employmentSec);
        q1.setLabel(sec+".1");
        q1.addRowLabel("Employee, full time (more than 30 hours/week)");
        q1.addRowLabel("Employee, part time (less than 30 hours/week)");
        q1.addRowLabel("Self-employed (paid work)");
        q1.addRowLabel("Government-supported employment training");
        q1.addRowLabel("Non-government supported employment training or education");
        q1.addRowLabel("Employee on sick leave because of mental health problems");
        q1.addRowLabel("Employee on sick leave becuase of other health reasons");
        q1.addRowLabel("Not in paid employment because of mental health problems");
        q1.addRowLabel("Not in paid employment due to retirement");
        q1.addRowLabel("Not in paid employment for other reasons");
        q1.addRowLabel("Unpaid employment for a business owned by self, friend or relative");

        TextEntry q1Label = factory.createTextEntry(
                "CQ1a", "Employment Status");
        q1.addEntry(q1Label);
        q1Label.setSection(employmentSec);

        OptionEntry q1Apply = factory.createOptionEntry(
                "CQ1b", "Applicable");
        q1.addEntry(q1Apply);
        q1Apply.addOption(factory.createOption("Yes", 1));
        q1Apply.addOption(factory.createOption("No", 2));
        q1Apply.setSection(employmentSec);

        NumericEntry q1Freq = factory.createNumericEntry("QC1c",
                "Number of weeks spent in the last 3 months");
        q1.addEntry(q1Freq);
        q1Freq.setSection(employmentSec);
        q1Freq.setDefaultValue(new Double(0.0));
        q1Freq.addUnit(UnitWrapper.instance().getUnit("weeks"));

        OptionEntry q1d = factory.createOptionEntry(
                "QC1d",
                "Which category best describes your employment now?");
        doc.addEntry(q1d);
        q1d.setSection(employmentSec);
        q1d.addOption(factory.createOption("Employee, full time (more than 30 hours/week)", 1));
        q1d.addOption(factory.createOption("Employee, part time (less than 30 hours/week)", 2));
        q1d.addOption(factory.createOption("Self-employed (paid work)", 3));
        q1d.addOption(factory.createOption("Government-supported employment training", 4));
        q1d.addOption(factory.createOption("Non-government supported employment training or education", 5));
        q1d.addOption(factory.createOption("Employee on sick leave because of mental health problems", 6));
        q1d.addOption(factory.createOption("Employee on sick leave because of other health reasons", 7));
        q1d.addOption(factory.createOption("Not in paid employment because of mental health problems", 8));
        q1d.addOption(factory.createOption("Not in paid employment due to retirement", 9));
        q1d.addOption(factory.createOption("Not in paid employment for other reasons", 10));
        q1d.addOption(factory.createOption("Unpaid employment for a business owned by self, friend or relative", 11));

        //Begin QC2
        OptionEntry q2 = factory.createOptionEntry(
                "QC2",
                "In the last 3 months, did you have a job or business you were away from?");
        doc.addEntry(q2);
        q2.setSection(employmentSec);
        q2.setLabel(sec+".2");
        Option q2Yes = factory.createOption("Yes (Go to C.3)", 1);
        q2.addOption(q2Yes);
        Option q2No = factory.createOption("No (Go to C.7)", 0);
        q2.addOption(q2No);
        q2.setOptionCodesDisplayed(false);

        OptionEntry q3 = factory.createOptionEntry("QC3",
                "Why were you away from work?");
        doc.addEntry(q3);
        q3.setSection(employmentSec);
        q3.setLabel(sec+".3");
        q3.setEntryStatus(EntryStatus.DISABLED);
        q3.addOption(factory.createOption("Holiday", 1));
        q3.addOption(factory.createOption("Sickness", 2));
        q3.addOption(factory.createOption("Studying/training", 3));
        q3.addOption(factory.createOption("Maternity/paternity leave", 4));
        Option q3Other = factory.createOption("Other reason (plese state)", 5);
        q3Other.setTextEntryAllowed(true);
        q3.addOption(q3Other);
        createOptionDependent(factory, q2Yes, q3);

        CompositeEntry q4 = factory.createComposite("QC4",
                "Please give details of all paid jobs or business you have had in the last 3 months");
        doc.addEntry(q4);
        q4.setSection(employmentSec);
        q4.setEntryStatus(EntryStatus.OPTIONAL);
        q4.setLabel(sec+".4");

        TextEntry q4a = factory.createTextEntry("QC4a",
        		"Type of paid job or business in the last 3 months");
        q4.addEntry(q4a);
        q4a.setSection(employmentSec);

        NumericEntry q4b = factory.createNumericEntry("QC4b",
                "Hours per week");
        q4.addEntry(q4b);
        q4b.setSection(employmentSec);
        q4b.setDefaultValue(new Double(0.0));
        q4b.addUnit(UnitWrapper.instance().getUnit("hours"));

        NumericEntry q4c = factory.createNumericEntry("QC4c",
                "Wages after all deductions paid");
        q4.addEntry(q4c);
        q4c.setSection(employmentSec);
        q4c.setDefaultValue(new Double(0.0));
        q4c.addUnit(UnitWrapper.instance().getUnit("gbp"));

        OptionEntry q4d = factory.createOptionEntry("QC4d",
                "Was the wage per week or per month");
        q4.addEntry(q4d);
        q4d.setSection(employmentSec);
        q4d.addOption(factory.createOption("Week", 1));
        q4d.addOption(factory.createOption("Month", 2));

        LongTextEntry q5 = factory.createLongTextEntry(
                "QC5",
                "What was your main job in the last 3 months or most " +
                        "recent period of paid work?");
        doc.addEntry(q5);
        q5.setSection(employmentSec);
        q5.setEntryStatus(EntryStatus.OPTIONAL);
        q5.setLabel(sec+".5");


        LongTextEntry q6 = factory.createLongTextEntry(
                "QC6",
                "What do/did you mainly do in your main job? (check whether any special" +
                        " qualifications, managerial duties, etc). Now go to C9.");
        doc.addEntry(q6);
        q6.setSection(employmentSec);
        q6.setEntryStatus(EntryStatus.OPTIONAL);
        q6.setLabel(sec+".6");

        OptionEntry q7 = factory.createOptionEntry("QC7", "Have you ever had a paid job?");
        doc.addEntry(q7);
        q7.setSection(employmentSec);
        q7.setLabel(sec+".7");
        q7.setEntryStatus(EntryStatus.DISABLED);
        Option q7Yes = factory.createOption("Yes (ask C.8)", 1);
        q7.addOption(q7Yes);
        Option q7No = factory.createOption("No (Go to C.11)", 2);
        q7.addOption(q7No);
        createOptionDependent(factory, q2No, q7);

        DateEntry q8 = factory.createDateEntry("QC8",
                "When did you leave your last paid job? Now go to C11");
        doc.addEntry(q8);
        q8.setSection(employmentSec);
        q8.setLabel(sec+".8");
        q8.setEntryStatus(EntryStatus.DISABLED);
        q8.addValidationRule(after1900);
        createOptionDependent(factory, q7Yes, q8);

        OptionEntry q9 = factory.createOptionEntry(
                "QC9",
                "Have you lost any earnings in the last 3 months becuase of your mental health problems?");
        doc.addEntry(q9);
        q9.setSection(employmentSec);
        q9.setLabel(sec+".9");
        q9.setEntryStatus(EntryStatus.OPTIONAL);
        Option q9Yes = factory.createOption("Yes (Go to C.10)", 1);
        q9.addOption(q9Yes);
        Option q9No = factory.createOption("No (Go to C.11)", 2);
        q9.addOption(q9No);

        NumericEntry q10 = factory.createNumericEntry("QC10",
                "Have you lost earnings during the last 3 months because of your mental health problems?");
        doc.addEntry(q10);
        q10.setSection(employmentSec);
        q10.setLabel(sec+".10");
        q10.setEntryStatus(EntryStatus.DISABLED);
        q10.setDefaultValue(new Double(0.0));
        q10.addUnit(UnitWrapper.instance().getUnit("gbp"));
        createOptionDependent(factory, q9Yes, q10);

        OptionEntry q11 = factory.createOptionEntry(
                "QC11",
                "Over the last 3 months do you think that your employment an/or potential employment" +
                        " opportunities have been affected by your mental health problems?");
        doc.addEntry(q11);
        q11.setSection(employmentSec);
        q11.setLabel(sec+".11");
        Option q11Yes = factory.createOption("Yes (Go to C12)", 1);
        q11.addOption(q11Yes);
        q11.addOption(factory.createOption("No (Go to C13)", 2));

        CompositeEntry q12 = factory.createComposite("QC12",
                "Thinking about your career and your job, have you experienced any changes " +
                        "in the last 3 months because of your mental health problems?");
        doc.addEntry(q12);
        q12.setSection(employmentSec);
        q12.setLabel(sec+".12");
        q12.setEntryStatus(EntryStatus.DISABLED);
        q12.addRowLabel("Lost job and became unemployed");
        q12.addRowLabel("Had difficulty getting a job");
        q12.addRowLabel("Change type of work (e.g. lower paid job, but not change in hours worked.)");
        q12.addRowLabel("Changed hours worked");
        q12.addRowLabel("Promotion prospects/career development restricted");
        q12.addRowLabel("Opportunities for changing job reduced");
        q12.addRowLabel("Opportunities for overtime reduced");
        q12.addRowLabel("Distance can travel reduced");
        q12.addRowLabel("Attendance reduced");
        q12.addRowLabel("Other (please describe)");
        createOptionDependent(factory, q11Yes, q12);

        TextEntry q12Label = factory.createTextEntry(
                "CQ1a", "");
        q12.addEntry(q12Label);
        q12Label.setSection(employmentSec);

        OptionEntry q12Apply = factory.createOptionEntry(
                "CQ12b", "Applicable");
        q12.addEntry(q12Apply);
        q12Apply.addOption(factory.createOption("Yes", 1));
        q12Apply.addOption(factory.createOption("No", 2));
        q12Apply.setSection(employmentSec);

        TextEntry q12Other = factory.createTextEntry(
                "QC12 Other",
                "If you chose other in the previous question, please specify.",
                EntryStatus.OPTIONAL);
        doc.addEntry(q12Other);
        q12Other.setSection(employmentSec);


        OptionEntry q12c = factory.createOptionEntry(
                "QC12c",
                "Which category best describes your employment now?");
        doc.addEntry(q12c);
        q12c.setSection(employmentSec);
        q12c.setEntryStatus(EntryStatus.DISABLED);
        q12c.addOption(factory.createOption("Lost job and became unemployed", 1));
        q12c.addOption(factory.createOption("Had difficulty getting a job", 2));
        q12c.addOption(factory.createOption("Change type of work (e.g. lower paid job, but not change in hours worked.)", 3));
        q12c.addOption(factory.createOption("Change hours worked", 4));
        q12c.addOption(factory.createOption("Promotion prospects/career development restricted", 5));
        q12c.addOption(factory.createOption("Opportunities for changing job reduced", 6));
        q12c.addOption(factory.createOption("Opportunities for overtime reduced", 7));
        q12c.addOption(factory.createOption("Distance can travel reduced", 8));
        q12c.addOption(factory.createOption("Attendance reduced", 9));
        q12c.addOption(factory.createOption("Other", 10));
        createOptionDependent(factory, q11Yes, q12c);

        OptionEntry q13 = factory.createOptionEntry("QC13",
                "In the last 3 months have you been looking for any kind of paid " +
                        "work or government training schemes?");
        doc.addEntry(q13);
        q13.setSection(employmentSec);
        q13.setLabel(sec+".13");
        Option q13Yes = factory.createOption("Yes", "Yes (Go to C14)", 1);
        q13.addOption(q13Yes);
        Option q13No = factory.createOption("No", "No (Go to C16)", 2);
        q13.addOption(q13No);

        CompositeEntry q14 = factory.createComposite("QC14",
                "In the last 3 months, " +
                        "did you do anything to find a new job or government training scheme?");
        doc.addEntry(q14);
        q14.setSection(employmentSec);
        q14.setLabel(sec+".14");
        q14.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, q13Yes, q14);

        OptionEntry q14a = factory.createOptionEntry("QC14a",
                "Job seeking activities in the last 3 months (add all that are applicable)");
        q14.addEntry(q14a);
        q14a.setSection(employmentSec);
        q14a.setOptionCodesDisplayed(true);
        q14a.addOption(factory.createOption("Visited a Jobcentre/Jobmarket or " +
        		"Training and Employment Agency Office?", 1));
        q14a.addOption(factory.createOption("Visited a Jobclub?", 2));
        q14a.addOption(factory.createOption("Had your name on the books of an " +
                "employment agency?", 3));
        q14a.addOption(factory.createOption("Advertised for jobs in newspapers, etc?", 4));
        q14a.addOption(factory.createOption("Looked for advertisements in " +
                "newspapers, etc?", 5));
        q14a.addOption(factory.createOption("Answered advertisements in newspapers, etc?", 6));
        q14a.addOption(factory.createOption("Applied directly to employers?", 7));
        q14a.addOption(factory.createOption( "Asked friends, relatives, colleagues or trade " +
                "unions about jobs?", 8));
        q14a.addOption(factory.createOption("Waited for the results of a job application?", 9));
        q14a.addOption(factory.createOption("Been to an interview?", 10));
        Option q14aElse = factory.createOption("Anything else to find work? Please state.", 11);
        q14aElse.setTextEntryAllowed(true);
        q14a.addOption(q14aElse);
        Option q14aOther = factory.createOption("Other (please specify)", 12);
        q14aOther.setTextEntryAllowed(true);
        q14a.addOption(q14aOther);

        //QC15
        NumericEntry q15 = factory.createNumericEntry("QC15",
                "How much time have you spent looking for a new job?", EntryStatus.DISABLED);
        doc.addEntry(q15);
        q15.setLabel(sec+".15");
        q15.setSection(employmentSec);
        q15.addValidationRule(positive);
        q15.setEntryStatus(EntryStatus.DISABLED);
        q15.addUnit(UnitWrapper.instance().getUnit("mins"));
        q15.addUnit(UnitWrapper.instance().getUnit("hours"));
        q15.addUnit(UnitWrapper.instance().getUnit("days"));
        createOptionDependent(factory, q13Yes, q15);

        //QC16
        OptionEntry q16 = factory.createOptionEntry(
                "QC16",
                "What was the main reason you did not look for work in the last 3 months?");
        doc.addEntry(q16);
        q16.setSection(employmentSec);
        q16.setEntryStatus(EntryStatus.DISABLED);
        q16.setLabel(sec+".16");
        q16.addOption(factory.createOption("Happy", "Happy with current job", 1));
        q16.addOption(factory.createOption(
                                "Waiting for job application",
                                "Waiting for the results of a job " +
                                "application/being assessed by training agent", 2));
        q16.addOption(factory.createOption("Student", "Student", 3));
        q16.addOption(factory.createOption(
                "Looking after the family home",
                "Looking after the family home", 4));
        q16.addOption(factory.createOption(
                "Temporarily sick or injured", "Temporarily sick or injured", 5));
        q16.addOption(factory.createOption(
                "Long-term sick or disabled", "Long-term sick or disabled", 6));
        q16.addOption(factory.createOption(
                "Believe no jobs available", "Believe no jobs available", 7));
        q16.addOption(factory.createOption("Not yet started looking",
                "Not yet started looking", 8));
        Option reasonNoLookOther = factory.createOption("Any other reason",
                "Any other reason.  Please state", 9);
        reasonNoLookOther.setTextEntryAllowed(true);
        q16.addOption(reasonNoLookOther);
        createOptionDependent(factory, q13No, q16);

        OptionEntry q17 = factory.createOptionEntry(
                "QC17",
                "Do you currently receive any state benefits in your own right or on behalf of " +
                        "anyone in your household?");
        doc.addEntry(q17);
        q17.setSection(employmentSec);
        q17.setLabel(sec+".17");
        Option q17Yes = factory.createOption("Yes", "Yes (Go to C18)", 1);
        q17.addOption(q17Yes);
        q17.addOption(factory.createOption("No", "No (Go to Section D)", 2));

        NarrativeEntry q18 = factory.createNarrativeEntry("QC18",
                "On average how many hours per week have you spent in the last 3 months on the following?");
        doc.addEntry(q18);
        q18.setLabel(sec+".18");
        q18.setSection(employmentSec);

        NumericEntry q18a = factory.createNumericEntry("QC18a",
                "Doing paid work (employed or self employed)");
        doc.addEntry(q18a);
        q18a.setLabel("a");
        q18a.setSection(employmentSec);
        q18a.setEntryStatus(EntryStatus.DISABLED);
        q18a.addValidationRule(positive);
        q18a.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, q17Yes, q18a);

        NumericEntry q18b = factory.createNumericEntry("QC18b",
                "Looking for work");
        doc.addEntry(q18b);
        q18b.setLabel("b");
        q18b.setSection(employmentSec);
        q18b.addValidationRule(positive);
        q18b.setEntryStatus(EntryStatus.DISABLED);
        q18b.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, q17Yes, q18b);

        CompositeEntry q19 = factory.createComposite("QC14",
                "Which benefits do you receive?");
        doc.addEntry(q19);
        q19.setSection(employmentSec);
        q19.setLabel(sec+".19");
        q19.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, q17Yes, q19);

        OptionEntry q19a = factory.createOptionEntry("QC19a",
                "Benefits currently receiving (add all that are applicable)");
        q19.addEntry(q19a);
        q19a.setSection(employmentSec);
        q19a.setOptionCodesDisplayed(true);
        q19a.addOption(factory.createOption("Child Benefit", 1));
        q19a.addOption(factory.createOption("Guardian's Allowance", 2));
        q19a.addOption(factory.createOption("Invalid Care Allowance", 3));
        q19a.addOption(factory.createOption("Pension (of any kind, i.e. retirement, widow's, etc)", 4));
        q19a.addOption(factory.createOption("Severe Disability Allowance", 5));
        q19a.addOption(factory.createOption("Disability Working Allowance", 6));
        q19a.addOption(factory.createOption("Job Seekers' Allowance", 7));
        q19a.addOption(factory.createOption("Income Support", 8));
        q19a.addOption(factory.createOption("Incapacity Benefit", 9));
        q19a.addOption(factory.createOption("Statutory Sick Pay", 10));
        q19a.addOption(factory.createOption("Industrial Injury Disablement " +
                "Benefit", 11));
        q19a.addOption(factory.createOption("Maternity Allowance", 12));
        q19a.addOption(factory.createOption("Working Families' Tax Credit", 13));
        q19a.addOption(factory.createOption("Disabled Persons' Tax Credit", 14));
        q19a.addOption(factory.createOption("Disability Living Allowance", 15));
    }

    public static void addServiceReceiptSection(Document doc, Factory factory,
    		String sec, boolean followUp){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");

        //Service Receipt section
        Section serviceSec = factory.createSection("Service Receipt section");
        doc.addSection(serviceSec);
        serviceSec.setDisplayText("Service Receipt");
        SectionOccurrence serviceSecOcc = factory
                .createSectionOccurrence("Service Receipt section occurrence");
        serviceSec.addOccurrence(serviceSecOcc);
        serviceSecOcc.setLabel(sec);

        CompositeEntry inpatient = factory.createComposite(
                "Use of inpatient hospital services",
                "Have you used any of these inpatient hospital services during the " +
                        "last three months?");
        doc.addEntry(inpatient);
        inpatient.setSection(serviceSec);
        inpatient.setLabel(sec+".1");

        OptionEntry inpatientService = factory.createOptionEntry(
                "Inpatient service option", "Service");
        inpatient.addEntry(inpatientService);
        inpatientService.setSection(serviceSec);
        inpatientService.addOption(factory.createOption("Acute psychiatric ward",
                "Acute psychiatric ward", 1));
        inpatientService.addOption(factory.createOption("Psychiatric " +
                "rehabilitation ward", "Psychiatric rehabilitation ward", 2));
        inpatientService.addOption(factory.createOption("Long-stay psychiatric ward",
                "Long-stay psychiatric ward", 3));
        inpatientService.addOption(factory.createOption("Emergency / crisis centre",
                "Emergency / crisis centre", 4));
        inpatientService.addOption(factory.createOption("General medical ward",
                "General medical ward", 5));
        inpatientService.addOption(factory.createOption("Drug treatment ward", "Drug treatment ward", 6));
        inpatientService.addOption(factory.createOption("Alcohol tretament ward",
        "Alcohol treatment ward", 7));
        Option inpatientServiceOther = factory.createOption("Other", "Other", 8);
        inpatientServiceOther.setTextEntryAllowed(true);
        inpatientService.addOption(inpatientServiceOther);

        TextEntry hospitalName = factory.createTextEntry("Hospital Name",
        "Name of Hospital");
        inpatient.addEntry(hospitalName);
        hospitalName.setSection(serviceSec);

        NumericEntry admissions = factory.createNumericEntry("Admissions",
                "Total number of admissions (during last 3 months)");
        inpatient.addEntry(admissions);
        admissions.setSection(serviceSec);
        admissions.setDefaultValue(0.0);
        admissions.addValidationRule(positive);

        NumericEntry inpatientDays = factory.createNumericEntry("Total number " +
                "of inpatient days",
                "Total number of inpatient days (during last 3 months)");
        inpatient.addEntry(inpatientDays);
        inpatientDays.setSection(serviceSec);
        inpatientDays.setDefaultValue(0.0);
        inpatientDays.addValidationRule(positive);

        CompositeEntry outpatient1 = factory.createComposite("Use of outpatient " +
                "hospital services", "Have you used any hospital outpatient or day services during " +
                "the last three months?");
        doc.addEntry(outpatient1);
        outpatient1.setSection(serviceSec);
        outpatient1.setLabel(sec+".2");

        OptionEntry outpatientService1 = factory.createOptionEntry("Outpatient " +
                "service option", "Service");
        outpatient1.addEntry(outpatientService1);
        outpatientService1.setSection(serviceSec);
        outpatientService1.addOption(factory.createOption(
                "Psychiatric",
                "Psychiatric", 1));
        outpatientService1.addOption(factory.createOption(
                "Hospital alcohol service",
                "Hospital alcohol service", 2));
        outpatientService1.addOption(factory.createOption(
                "Hospital substance use service",
                "Hospital substance use service", 3));
        Option nonPsych = factory.createOption(
                "Non-psychiatric",
                "Non-psychiatric (specify)", 4);
        nonPsych.setTextEntryAllowed(true);
        outpatientService1.addOption(nonPsych);
        outpatientService1.addOption(factory.createOption(
                "Accident and Emergency",
                "Accident and Emergency", 5));
        outpatientService1.addOption(factory.createOption(
                "Day hospital",
                "Day hospital", 6));
        Option outpatientServiceOther = factory.createOption("Other", "Other", 7);
        outpatientServiceOther.setTextEntryAllowed(true);
        outpatientService1.addOption(outpatientServiceOther);

        TextEntry serviceName = factory.createTextEntry("Service Name",
        "Name of Service");
        outpatient1.addEntry(serviceName);
        serviceName.setSection(serviceSec);

        NumericEntry unitsReceived1 = factory.createNumericEntry("Number of outpatient attendances",
                "Total number of outpatient visits made (during last 3 months)");
        outpatient1.addEntry(unitsReceived1);
        unitsReceived1.setSection(serviceSec);
        unitsReceived1.setDefaultValue(0.0);
        unitsReceived1.addValidationRule(positive);

        NumericEntry unitsReceived2 = factory.createNumericEntry("Number of day attendances",
                "Total number of day attendances (during last 3 months)");
        outpatient1.addEntry(unitsReceived2);
        unitsReceived2.setSection(serviceSec);
        unitsReceived2.setDefaultValue(0.0);
        unitsReceived2.addValidationRule(positive);

        CompositeEntry communityServices = factory.createComposite(
                "Community-based day services", "Have you had any other primary and community-care " +
                "contacts during the last three months?");
        doc.addEntry(communityServices);
        communityServices.setSection(serviceSec);
        communityServices.setLabel(sec+".3");

        OptionEntry communityService = factory.createOptionEntry("Community Day " +
                "Services Option", "Type of Contact");
        communityServices.addEntry(communityService);
        communityService.setSection(serviceSec);
        communityService.addOption(factory.createOption("GP, surgery visit ",
        		"GP, surgery visit", 1));
        communityService.addOption(factory.createOption("GP, home visit ",
		"GP, home visit", 2));
        communityService.addOption(factory.createOption("Psychiatrist ",
		"Psychiatrist", 3));
        communityService.addOption(factory.createOption("Psychologist",
                "Psychologist", 4));
        communityService.addOption(factory.createOption("Alcohol treatment or rehab",
                "Alcohol treatment or rehabilitation service", 5));
        communityService.addOption(factory.createOption("Drug treatment or rehab",
                "Drug treatment or rehabilitation service", 6));
        communityService.addOption(factory.createOption("District Nurse",
                "District Nurse", 7));
        communityService.addOption(factory.createOption("Community psychiatric nurse",
        "Community psychiatric nurse / case manager", 8));
        communityService.addOption(factory.createOption("Social worker",
        "Social worker", 9));
        communityService.addOption(factory.createOption("Occupational therapist",
        "Occupational therapist", 10));
        communityService.addOption(factory.createOption("Voluntary counsellor",
        "Voluntary counsellor", 11));
        communityService.addOption(factory.createOption("Home help / care worker",
        "Home help / care worker", 12));
        Option communityServiceOther = factory.createOption("Other", "Other", 13);
        communityServiceOther.setTextEntryAllowed(true);
        communityService.addOption(communityServiceOther);

        NumericEntry numberAttendance = factory.createNumericEntry("Number of " +
                "attendances", "Total number of contacts");
        communityServices.addEntry(numberAttendance);
        numberAttendance.setSection(serviceSec);
        numberAttendance.setDefaultValue(0.0);
        numberAttendance.addValidationRule(positive);

        NumericEntry durationAttendance = factory.createNumericEntry("Average " +
                "duration of attendance", "Average time per contact");
        communityServices.addEntry(durationAttendance);
        durationAttendance.setSection(serviceSec);
        durationAttendance.addValidationRule(positive);
        durationAttendance.setDefaultValue(0.0);
        durationAttendance.addUnit(UnitWrapper.instance().getUnit("hours"));
    }


    public static void addCriminalJusticeServiceSection(Document doc, Factory factory,
    		String sec, boolean followUp){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");


        //Criminal justice section
        Section serviceSec = factory.createSection("Criminal Justice Service section");
        doc.addSection(serviceSec);
        serviceSec.setDisplayText("Criminal Justice Service");
        SectionOccurrence serviceSecOcc = factory
                .createSectionOccurrence("Criminal Justice Service section occurrence");
        serviceSec.addOccurrence(serviceSecOcc);
        serviceSecOcc.setLabel(sec);

        OptionEntry contactWithPolice = factory.createOptionEntry(
                "Been in contact with the just services",
                "Over the last 6 months, have you been in contact with the " +
                        "criminal justice services?");
        doc.addEntry(contactWithPolice);
        contactWithPolice.setSection(serviceSec);
        contactWithPolice.setLabel(sec+".1");
        contactWithPolice.addOption(factory.createOption("No", "No", 1));
        Option contactWithPoliceYes = factory.createOption("Yes", "Yes", 2);
        contactWithPolice.addOption(contactWithPoliceYes);

        NarrativeEntry contactPoliceYes = factory.createNarrativeEntry(
                "Contact with police yes",
                "If yes:");
        doc.addEntry(contactPoliceYes);
        contactPoliceYes.setSection(serviceSec);

        NumericEntry numberContactsPolice = factory.createNumericEntry(
                "Number of contacts with police",
                "How many contacts with the police during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(numberContactsPolice);
        numberContactsPolice.setSection(serviceSec);
        numberContactsPolice.setLabel(sec+".2");
        numberContactsPolice.setDescription(
                "Note: contact = interview or stay of some hours, but not overnight");
        numberContactsPolice.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, numberContactsPolice);

        NumericEntry numberContactsProbation = factory.createNumericEntry(
                "Number of contacts with probation",
                "How many contacts with a probation officer during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(numberContactsProbation);
        numberContactsProbation.setSection(serviceSec);
        numberContactsProbation.setLabel(sec+".3");
        numberContactsProbation.setDescription(
                "Note: contact = interview or stay of some hours, but not overnight");
        numberContactsProbation.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, numberContactsProbation);

        NumericEntry nightsPrison = factory.createNumericEntry(
                "Nights spent in prison",
                "How many nights spent in a police cell or prison during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(nightsPrison);
        nightsPrison.setSection(serviceSec);
        nightsPrison.setLabel(sec+".3");
        nightsPrison.addValidationRule(positive);
        createOptionDependent(factory, contactWithPoliceYes, nightsPrison);

        NumericEntry psychAssessments = factory.createNumericEntry(
                "Number of psychiatric assessments",
                "How many psychiatric assessments whilst in custody during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(psychAssessments);
        psychAssessments.setSection(serviceSec);
        psychAssessments.addValidationRule(positive);
        psychAssessments.setLabel(sec+".4");
        createOptionDependent(factory, contactWithPoliceYes, psychAssessments);

        NumericEntry criminalCourt = factory.createNumericEntry(
                "Number of criminal court appearances",
                "How many criminal court appearances during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(criminalCourt);
        criminalCourt.setSection(serviceSec);
        criminalCourt.addValidationRule(positive);
        criminalCourt.setLabel(sec+".5a");
        createOptionDependent(factory, contactWithPoliceYes, criminalCourt);

        NumericEntry civilCourt = factory.createNumericEntry(
                "Number of civil court appearances",
                "How many civil court appearances during the last 6 months?",
                EntryStatus.DISABLED);
        doc.addEntry(civilCourt);
        civilCourt.setSection(serviceSec);
        civilCourt.addValidationRule(positive);
        civilCourt.setLabel(sec+".5b");
        createOptionDependent(factory, contactWithPoliceYes, civilCourt);

        OptionEntry convictions = factory.createOptionEntry(
                "Number of convictions",
                "During the last 6 months, have you been convicted for any offences?",
                EntryStatus.DISABLED);
        doc.addEntry(convictions);
        convictions.setSection(serviceSec);
        convictions.setLabel(sec+".6");
        createOptionDependent(factory, contactWithPoliceYes, convictions);
        convictions.addOption(factory.createOption("No", "No", 1));
        Option convictionsYes = factory.createOption("Yes", "Yes", 2);
        convictions.addOption(convictionsYes);

        CompositeEntry offenceType = factory.createComposite(
                "Offence Type", "What offences have you been convicted of during the last 6 months?");
        doc.addEntry(offenceType);
        offenceType.setSection(serviceSec);
        offenceType.setLabel(sec+".7");
        offenceType.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, convictionsYes, offenceType);

        OptionEntry offenceCategory = factory.createOptionEntry("Category of offence",
                "Category of offence");
        offenceType.addEntry(offenceCategory);
        offenceCategory.setSection(serviceSec);
        offenceCategory.addOption(factory.createOption("Homocide/Manslaughter",
        		"Homocide/Manslaughter", 1));
        offenceCategory.addOption(factory.createOption("Serious assault leading to major injury",
		"Serious assault leading to major injury", 2));
        offenceCategory.addOption(factory.createOption("Less serious assault leading to minor or no injury",
		"Less serious assault leading to minor or no injury", 3));
        offenceCategory.addOption(factory.createOption("Sexual offences",
		"Sexual offences", 11));
        offenceCategory.addOption(factory.createOption("Robbery",
		"Robbery", 4));
        offenceCategory.addOption(factory.createOption("Burlgary",
		"Burlgary", 5));
        offenceCategory.addOption(factory.createOption("Theft and handling stolen goods",
		"Theft and handling stolen goods", 6));
        offenceCategory.addOption(factory.createOption("Fraud and forgery",
		"Fraud and forgery", 7));
        offenceCategory.addOption(factory.createOption("Criminal damage",
		"Criminal damage", 8));
        offenceCategory.addOption(factory.createOption("Drug offences",
		"Drug offences", 9));
        Option offenceCategoryOther = factory.createOption("Other", "Other", 10);
        offenceCategoryOther.setTextEntryAllowed(true);
        offenceCategory.addOption(offenceCategoryOther);

        OptionEntry convictedOfOffences = factory.createOptionEntry("Convicted of offence",
                "Convicted");
        offenceType.addEntry(convictedOfOffences);
        convictedOfOffences.setSection(serviceSec);
        convictedOfOffences.addOption(factory.createOption("No", 1));
        convictedOfOffences.addOption(factory.createOption("Yes", 2));

        TextEntry clientOffenceName = factory.createTextEntry("Client offence name",
        		"Name of offence (as given by respondent)");
        offenceType.addEntry(clientOffenceName);
        clientOffenceName.setSection(serviceSec);
    }

    public static void addInterviewerCommentsSection(Document doc, Factory factory,
    		String sec, boolean followup){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");


        //Interviewer Comments section
        Section interviewerCommentsSec = factory.createSection(
                "Interviewer Comments section");
        doc.addSection(interviewerCommentsSec);
        interviewerCommentsSec.setDisplayText("Interviewer Comments");
        SectionOccurrence interviewerCommentsSecOcc = factory.createSectionOccurrence(
                "Interviewer Comments section occurrence");
        interviewerCommentsSec.addOccurrence(interviewerCommentsSecOcc);
        interviewerCommentsSecOcc.setLabel(sec);

        NarrativeEntry note = factory.createNarrativeEntry(
                "Interviewer Comments section note",
                "Before filing this questionnaire or proceeding to the next interview " +
                        ", please complete the following section while you impressions of the" +
                        " patient's responses are still fresh in your memory");
        doc.addEntry(note);
        note.setSection(interviewerCommentsSec);

        NumericEntry interviewDuration = factory.createNumericEntry("Interview Duration",
                "How long did the interview take?");
        doc.addEntry(interviewDuration);
        interviewDuration.setSection(interviewerCommentsSec);
        interviewDuration.addValidationRule(positive);
        interviewDuration.addUnit(UnitWrapper.instance().getUnit("mins"));

        OptionEntry reliability = factory.createOptionEntry("Reliability",
                "How reliable or unreliable do you think the patient's responses were?");
        doc.addEntry(reliability);
        reliability.setSection(interviewerCommentsSec);
        reliability.addOption(factory.createOption("Very reliable", 1));
        reliability.addOption(factory.createOption("Generally reliable", 2));
        reliability.addOption(factory.createOption("Generally unreliable", 3));
        reliability.addOption(factory.createOption("Very unreliable", 4));

        LongTextEntry comments = factory.createLongTextEntry("Other comments", "Any other comments?");
        doc.addEntry(comments);
        comments.setSection(interviewerCommentsSec);
    }
    public static void addExtraCostsSection(Document doc, Factory factory,
    		String sec, boolean followup){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");



        //Extra Costs section
        Section extraCostsSec = factory.createSection(
                "Extra Costs section");
        doc.addSection(extraCostsSec);
        extraCostsSec.setDisplayText("Extra Costs");
        SectionOccurrence extraCostsSecOcc = factory.createSectionOccurrence(
                "Extra Costs section occurrence");
        extraCostsSec.addOccurrence(extraCostsSecOcc);
        extraCostsSecOcc.setLabel(sec);

        NarrativeEntry note = factory.createNarrativeEntry(
                "Spent on Care",
                "During the last 3 months, how much do you think you have spent on: " +
                        "Note 1: please enter '0' if service has not been used");
        doc.addEntry(note);
        note.setSection(extraCostsSec);
        note.setLabel(sec+".1");

        NumericEntry medicationCosts = factory.createNumericEntry("Medication Costs",
                "Prescribed, and over-the-counter, medications (UKP)?");
        doc.addEntry(medicationCosts);
        medicationCosts.setSection(extraCostsSec);
        medicationCosts.addValidationRule(positive);
        //medicationCosts.setLabel(sec+".1.1");
        medicationCosts.addUnit(UnitWrapper.instance().getUnit("gbp"));

        NumericEntry travelCosts = factory.createNumericEntry("Travel Costs",
                "Travel costs (e.g. parking fees to attend any hospital, GP, or day care appointments) (UKP)?");
        doc.addEntry(travelCosts);
        travelCosts.setSection(extraCostsSec);
        travelCosts.addValidationRule(positive);
        //travelCosts.setLabel(sec+".1.2");
        travelCosts.addUnit(UnitWrapper.instance().getUnit("gbp"));

        NumericEntry privateCareCosts = factory.createNumericEntry("Private Care Costs",
                "Private health care (include use of alternative therapies and practitioners) (UKP)?");
        doc.addEntry(privateCareCosts);
        privateCareCosts.setSection(extraCostsSec);
        privateCareCosts.addValidationRule(positive);
        //privateCareCosts.setLabel(sec+".1.3");
        privateCareCosts.addUnit(UnitWrapper.instance().getUnit("gbp"));

        OptionEntry oneOffExpenses = factory.createOptionEntry(
                "One off expenses",
                "Over the last 3 months, are there any other MAJOR (UKP50+) one-off " +
                        "expenses that you have had to meet?");
        doc.addEntry(oneOffExpenses);
        oneOffExpenses.setSection(extraCostsSec);
        oneOffExpenses.setLabel(sec+".2");
        oneOffExpenses.addOption(factory.createOption("No", "No", 0));
        Option oneOffExpensesYes = factory.createOption("Yes", "Yes", 1);
        oneOffExpenses.addOption(oneOffExpensesYes);

        CompositeEntry oneOffCostComp = factory.createComposite(
                "One off cost items",
                "Please enter details of one-off expenses");
        doc.addEntry(oneOffCostComp);
        oneOffCostComp.setSection(extraCostsSec);
        oneOffCostComp.setLabel(sec+".3");
        oneOffCostComp.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, oneOffExpensesYes, oneOffCostComp);

        TextEntry itemDescription = factory.createTextEntry("Description of item",
        		"Description of item");
        oneOffCostComp.addEntry(itemDescription);
        itemDescription.setSection(extraCostsSec);

        NumericEntry otherCareCosts = factory.createNumericEntry("Other Costs",
                "Amount spent (during last 3 months, UKP)");
        oneOffCostComp.addEntry(otherCareCosts);
        otherCareCosts.setSection(extraCostsSec);
        otherCareCosts.addValidationRule(positive);
        //otherCareCosts.setLabel(sec+".1.3");
        otherCareCosts.addUnit(UnitWrapper.instance().getUnit("gbp"));

        OptionEntry dueToMentalHealthProblem = factory.createOptionEntry("Incurred due to mental health problems?",
                "Incurred due to mental health problems?");
        oneOffCostComp.addEntry(dueToMentalHealthProblem);
        dueToMentalHealthProblem.setSection(extraCostsSec);
        dueToMentalHealthProblem.addOption(factory.createOption("No", 1));
        dueToMentalHealthProblem.addOption(factory.createOption("Yes", 2));

    }

    public static void addEducationSection(Document doc, Factory factory,
    		String sec, boolean followup) {

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");
        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");

        //Education and Training section
        Section educationSec = factory.createSection("Education and Training section");
        doc.addSection(educationSec);
        educationSec.setDisplayText("Education and Training");
        SectionOccurrence educationSecOcc = factory.createSectionOccurrence(
                "Education and Training section occurrence");
        educationSec.addOccurrence(educationSecOcc);
        educationSecOcc.setLabel(sec);

        OptionEntry qualifications = factory.createOptionEntry(
                "Qualifications connected with work",
                "Do you have any qualifications from school, college or " +
                        "university, connected with work or from government schemes?");
        doc.addEntry(qualifications);
        qualifications.setSection(educationSec);
        qualifications.setLabel(sec+".1");
        Option qualificationsYes = factory.createOption("Yes", "Yes (Go to D2)", 1);
        qualifications.addOption(qualificationsYes);
        qualifications.addOption(factory.createOption("No", "No (Go to D4)", 2));

        LongTextEntry qualificationsHighest = factory.createLongTextEntry(
                "Highest qualification",
                "What is the highest qualification that you have?",
                EntryStatus.DISABLED);
        doc.addEntry(qualificationsHighest);
        qualificationsHighest.setSection(educationSec);
        qualificationsHighest.setLabel(sec+".2");
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
        whenLastStudy.setLabel(sec+".3");
        whenLastStudy.addValidationRule(after1900);
        createOptionDependent(factory, qualificationsYes, whenLastStudy);

        OptionEntry q4 = factory.createOptionEntry("Studying " +
                "for qualifications at the moment",
                "Are you studying for any qualifications at the moment?");
        doc.addEntry(q4);
        q4.setSection(educationSec);
        q4.setLabel(sec+".4");
        Option q4Yes = factory.createOption("Yes", "Yes (Go to D5)", 1);
        q4.addOption(q4Yes);
        Option q4No = factory.createOption("No",
                "No (Go to D6)", 0);
        q4.addOption(q4No);
        q4.setOptionCodesDisplayed(true);

        CompositeEntry q5 = factory.createComposite(
                "QD5", "What qualifications are you studying for at the moment?");
        doc.addEntry(q5);
        q5.setLabel(sec+".5");
        q5.setSection(educationSec);
        q5.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, q4Yes, q5);

        OptionEntry q5a = factory.createOptionEntry("DQ5a",
                "Qualifications currently studying for");
        q5.addEntry(q5a);
        q5a.addOption(factory.createOption("Degree level qualification including graduate membership of a " +
                "professional institute or PGCE or higher (include undergraduate " +
                "and postgraduate degrees)", 1));
        q5a.addOption(factory.createOption("Diploma in higher education", 2));
        q5a.addOption(factory.createOption("HNC/HND", 3));
        q5a.addOption(factory.createOption("ONC/OND", 4));
        q5a.addOption(factory.createOption("BTEC, BEC or TEC", 5));
        q5a.addOption(factory.createOption("SCOTVEC, SCOTEC or SCOTBEC", 6));
        q5a.addOption(factory.createOption("Teaching qualification excluding PGCE", 7));
        q5a.addOption(factory.createOption("Nursing or other " +
                "medical qualification not yet mentioned", 8));
        q5a.addOption(factory.createOption("Other higher education " +
                "qualification below degree level", 9));
        q5a.addOption(factory.createOption("A-level or equivalent", 10));
        q5a.addOption(factory.createOption("SCE highers", 11));
        q5a.addOption(factory.createOption("NVQ/SVQ", 12));
        q5a.addOption(factory.createOption("GNVQ/GSVQ", 13));
        q5a.addOption(factory.createOption("AS-level", 14));
        q5a.addOption(factory.createOption("Certificate of sixth year " +
                "studies (CSYS) or equivalent", 15));
        q5a.addOption(factory.createOption("O-Level or equivalent", 16));
        q5a.addOption(factory.createOption("SCE Standard or Ordinary (O) grade", 17));
        q5a.addOption(factory.createOption("GCSE", 18));
        q5a.addOption(factory.createOption("CSE", 19));
        q5a.addOption(factory.createOption("RSA", 20));
        q5a.addOption(factory.createOption("City and Guilds", 21));
        q5a.addOption(factory.createOption("YT certificate/YTP", 22));
        q5a.addOption(factory.createOption("Any other professional or vocational " +
                "qualification or foreign qualifications (e.g. apprenticeship)", 23));
        q5a.addOption(factory.createOption("Don't know", 24));
        q5a.setSection(educationSec);

        OptionEntry ftOrPt = factory.createOptionEntry("FT or PT", "Are you studying full time or part time");
        q5.addEntry(ftOrPt);
        ftOrPt.setSection(educationSec);
        ftOrPt.addOption(factory.createOption("F/T", 1));
        ftOrPt.addOption(factory.createOption("P/T", 2));

        OptionEntry q6 = factory.createOptionEntry(
                "Taught courses",
                "In the last 3 months, have you been on any taught courses or undertaken " +
                        "other types of structured learning");
        doc.addEntry(q6);
        q6.setSection(educationSec);
        q6.setLabel(sec+".6");
        Option q6Yes = factory.createOption("Yes (Go to D7)", 1);
        q6.addOption(q6Yes);
        q6.addOption(factory.createOption("No (Go to D8)", 2));

        CompositeEntry q7 = factory.createComposite(
                "Taught Course details", "What courses/other types of learning have you done in the last 3 months?");
        doc.addEntry(q7);
        q7.setLabel(sec+".7");
        q7.setSection(educationSec);
        q7.setEntryStatus(EntryStatus.DISABLED);
           createOptionDependent(factory, q6Yes, q7);

        OptionEntry q7a = factory.createOptionEntry("Taught Course", "Taught course ");
        q7.addEntry(q7a);
        q7a.setSection(educationSec);
        q7a.addOption(factory.createOption("Taught courses meant to lead to qualifications " +
        		"(even if you did not obtain them)", 1));
        q7a.addOption(factory.createOption("Taught courses designed to help you develop skills " +
        		"that you might use in a job", 2));
        q7a.addOption(factory.createOption("Courses or instruction or tuition in driving, in playing a " +
                "musical instrument, in an art or craft, in a sport or in any " +
                "practical skill", 3));
        q7a.addOption(factory.createOption("Evening classes", 4));
        q7a.addOption(factory.createOption("Learning which involved working on your own " +
        		"from a package of materials provided", 5));

        IntegerEntry numCourses = factory.createIntegerEntry("Number of courses", "Number of courses");
        q7.addEntry(numCourses);
        numCourses.setSection(educationSec);


        OptionEntry q8 = factory.createOptionEntry(
                "QD8",
                "In the last 3 months, have you studied or received any other type of training");
        doc.addEntry(q8);
        q8.setSection(educationSec);
        q8.setLabel(sec+".8");
        Option q8Yes = factory.createOption("Yes (Go to D9)", 1);
        q8.addOption(q8Yes);
        Option q8No = factory.createOption("No (Go to D14)", 2);
        q8.addOption(q8No);

        NarrativeEntry q9 = factory.createNarrativeEntry("QD9",
                "In the last month, have you studied or received training in " +
                "any of these ways:");
        doc.addEntry(q9);
        q9.setSection(educationSec);
        q9.setLabel(sec+".9");

        OptionEntry q9a = factory.createOptionEntry("QD9a", "Studied for a qualification without taking " +
                "part in a taught course");
        doc.addEntry(q9a);
        q9a.setSection(educationSec);
        q9a.setEntryStatus(EntryStatus.DISABLED);
        Option q9aYes = factory.createOption("Yes", "Yes", 1);
        q9a.addOption(q9aYes);
        q9a.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, q8Yes, q9a);

        OptionEntry q9b = factory.createOptionEntry("QD9b", "Received supervised training while you were " +
                "actually doing a job");
        doc.addEntry(q9b);
        q9b.setSection(educationSec);
        q9b.setEntryStatus(EntryStatus.DISABLED);
        Option q9bYes = factory.createOption("Yes", "Yes", 2);
        q9b.addOption(q9bYes);
        q9b.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, q8Yes, q9b);

        OptionEntry q9c = factory.createOptionEntry("QD9c", "Spent time keeping up-to-date " +
                "with developments in the type of work you do without " +
                "taking part in a taught course (e.g. by reading books, manuals " +
                "journals, or attending seminars)");
        doc.addEntry(q9c);
        q9c.setSection(educationSec);
        q9c.setEntryStatus(EntryStatus.DISABLED);
        Option q9cYes = factory.createOption("Yes", "Yes", 3);
        q9c.addOption(q9cYes);
        q9c.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, q8Yes, q9c);

        OptionEntry q9d = factory.createOptionEntry("QD9d", "Spent time deliberately trying to " +
                "improve your knowledge about anything or teach yourself a new " +
                "skill without taking part in a taught course");
        doc.addEntry(q9d);
        q9d.setSection(educationSec);
        q9d.setEntryStatus(EntryStatus.DISABLED);
        Option q9dYes = factory.createOption("Yes", "Yes", 4);
        q9d.addOption(q9dYes);
        q9d.addOption(factory.createOption("No", "No", 0));
        createOptionDependent(factory, q8Yes, q9d);

        LongTextEntry q10 = factory.createLongTextEntry("QD10",
                "Please give details of any other types of learning " +
                        "you have been involved in over the last 3 months " +
                        "(e.g. what, number of occasions in last month, length " +
                        "of time, etc)", EntryStatus.DISABLED);
        doc.addEntry(q10);
        q10.setSection(educationSec);
        q10.setLabel(sec+".10");
        createOptionDependent(factory, q9aYes, q10);
        createOptionDependent(factory, q9bYes, q10);
        createOptionDependent(factory, q9cYes, q10);
        createOptionDependent(factory, q9dYes, q10);

        NarrativeEntry guidance1 = factory.createNarrativeEntry("If the participant is currently " +
        		"studying for a qualification, has had a taught courses or been involved " +
        		"in any other form of learning (i.e. answered yes to any of D4, D6 or D8) go to D11.");
        doc.addEntry(guidance1);
        guidance1.setSection(educationSec);

        NarrativeEntry guidance2 = factory.createNarrativeEntry("If the participant is not currently " +
        		"studying or involved in taught courses or other forms of learning go to D14");
        doc.addEntry(guidance2);
        guidance2.setSection(educationSec);

        NumericEntry q11 = factory.createNumericEntry(
                "Training time",
                "On average how many hours per week have you spent on taught courses " +
                        "or other forms of learning in the last 3 months?",
                EntryStatus.DISABLED);
        doc.addEntry(q11);
        q11.setSection(educationSec);
        q11.addValidationRule(positive);
        q11.setLabel(sec+".11");
        q11.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, q4Yes, q11);
        createOptionDependent(factory, q6Yes, q11);
        createOptionDependent(factory, q8Yes, q11);

        NumericEntry q12 = factory.createNumericEntry(
                "Time studying at home",
                "On how many occasions in the last 3 months did you spend time " +
                        "studying at home outside of teaching sessions?",
                EntryStatus.DISABLED);
        doc.addEntry(q12);
        q12.setSection(educationSec);
        q12.setLabel(sec+".12");
        q12.addValidationRule(positive);
        createOptionDependent(factory, q4Yes, q12);
        createOptionDependent(factory, q6Yes, q12);
        createOptionDependent(factory, q8Yes, q12);

        NumericEntry q13 = factory.createNumericEntry(
                "Time studied for last time",
                "During the last 3 months, how long did you study for the last time you did any?",
                EntryStatus.DISABLED);
        doc.addEntry(q13);
        q13.setSection(educationSec);
        q13.addValidationRule(positive);
        q13.setLabel(sec+".13");
        q13.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, q4Yes, q13);
        createOptionDependent(factory, q6Yes, q13);
        createOptionDependent(factory, q8Yes, q13);

        NumericEntry q13a = factory.createNumericEntry(
                "Average time study for",
                "How long on average do you normally study for?",
                EntryStatus.DISABLED);
        doc.addEntry(q13a);
        q13a.setSection(educationSec);
        q13a.addValidationRule(positive);
        q13a.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, q4Yes, q13a);
        createOptionDependent(factory, q6Yes, q13a);
        createOptionDependent(factory, q8Yes, q13a);

        OptionEntry q14 = factory.createOptionEntry("Looking for any " +
                "kind of education", "Thinking of the last 3 months, have you been " +
                "looking for any kind of education/course?");
        doc.addEntry(q14);
        q14.setSection(educationSec);
        q14.setLabel(sec+".14");
        Option q14Yes = factory.createOption("Yes", "Yes", 1);
        q14.addOption(q14Yes);
        Option q14No = factory.createOption("No", "No", 2);
        q14.addOption(q14No);

        LongTextEntry q15 = factory.createLongTextEntry(
                "Looking for any kind of education - Details)",
                "Details (what, how much time, etc)", EntryStatus.DISABLED);
        doc.addEntry(q15);
        q15.setLabel(sec+".15");
        q15.setSection(educationSec);
        createOptionDependent(factory, q14Yes, q15);
    }

    public static void addOtherActivitiesSection(Document doc, Factory factory,
    		String sec, boolean followup){

        ValidationRule positive = ValidationRulesWrapper.instance().getRule("Positive");
        Unit hours = UnitWrapper.instance().getUnit("hours");

        // Leisure Activities section
        Section leisureSec = factory.createSection("Other Activities section");
        doc.addSection(leisureSec);
        leisureSec.setDisplayText("Other Activities");
        SectionOccurrence leisureSecOcc = factory
                .createSectionOccurrence("Other Activities section occurrence");
        leisureSec.addOccurrence(leisureSecOcc);
        leisureSecOcc.setLabel(sec);

        NarrativeEntry guidance1 = factory.createNarrativeEntry("Voluntary work is work " +
        		"that people may do for which they are not paid, except perhaps expenses");
        doc.addEntry(guidance1);
        guidance1.setSection(leisureSec);

        OptionEntry q1 = factory.createOptionEntry(
                "QE1",
                "Have you done any voluntary work through a group or on behalf " +
                        "of an organisation at any time during the last 3 months?");
        doc.addEntry(q1);
        q1.setSection(leisureSec);
        q1.setLabel(sec+".1");
        Option q1Yes = factory.createOption("Yes (Go to E2)", 1);
        q1.addOption(q1Yes);
        q1.addOption(factory.createOption("No (Go to E5)", 2));

        LongTextEntry q2 = factory.createLongTextEntry(
                "QE2",
                "What types of voluntary work have you done in the last 3 months?",
                EntryStatus.DISABLED);
        doc.addEntry(q2);
        q2.setSection(leisureSec);
        q2.setLabel(sec+".2");
        createOptionDependent(factory, q1Yes, q2);

        NumericEntry q3 = factory.createNumericEntry(
                "QE3",
                "How many different times did you do this work during the last 3 months?",
                EntryStatus.DISABLED);
        doc.addEntry(q3);
        q3.setSection(leisureSec);
        q3.addValidationRule(positive);
        q3.setLabel(sec+".3");
        createOptionDependent(factory, q1Yes, q3);

        NumericEntry q4 = factory.createNumericEntry(
                "QE4",
                "How many hours did you work for, the last time you did this?",
                EntryStatus.DISABLED);
        doc.addEntry(q4);
        q4.setSection(leisureSec);
        q4.addValidationRule(positive);
        q4.setLabel(sec+".4");
        q4.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, q1Yes, q4);

        NumericEntry q4a = factory.createNumericEntry(
                "QE4a",
                "How many hours per week do you normally spend doing this?",
                EntryStatus.DISABLED);
        doc.addEntry(q4a);
        q4a.setSection(leisureSec);
        q4a.addValidationRule(positive);
        q4a.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, q1Yes, q4a);

        CompositeEntry q5 = factory.createComposite("QE5",
                "What activities do you do in your spare time? " +
                        "Do not include sports or other physical activity, which we will talk about " +
                        "afterwards. For each activity could you please tell me whether you have " +
                        "done this in the last 3 months, how often and the amount of time " +
                        "you spend each time you do the activity?");
        doc.addEntry(q5);
        q5.setSection(leisureSec);
        q5.setLabel(sec+".5");

        OptionEntry q5a = factory.createOptionEntry(
                "Leisure Activity", "Activities you have done in the last 3 months");
        q5.addEntry(q5a);
        q5a.setSection(leisureSec);
        q5a.addOption(factory.createOption("Been to a sports event as a spectator", 1));
        q5a.addOption(factory.createOption("Been to cinema, film society or club", 2));
        q5a.addOption(factory.createOption("Been to a play, musical, pantomime or the opera", 3));
        q5a.addOption(factory.createOption("Been to a concert, gig...", 4));
        q5a.addOption(factory.createOption("Been to the ballet or to a " +
                "modern/contemporary dance performance", 5));
        q5a.addOption(factory.createOption("Been to a museum or art gallery", 6));
        q5a.addOption(factory.createOption("Been to an historic house, castle or " +
                "other heritage site or building", 7));
        q5a.addOption(factory.createOption("Been to a library", 8));
        q5a.addOption(factory.createOption("Been out to eat or drink at a cafe, " +
                "restaurant, pub or wine bar", 9));
        q5a.addOption(factory.createOption("Been to a shopping centre, or mall, " +
                "apart from regular shopping for food and household items", 10));
        q5a.addOption(factory.createOption("Been to a car boot sale, antiques fair " +
                "or craft market or similar apart from regular shopping for " +
                "food and household items", 11));
        q5a.addOption(factory.createOption("Been to a theme park, fairground, fair or " +
                "carnival", 12));
        q5a.addOption(factory.createOption("Been to a zoo, wildlife reserve, aquarium " +
                "or farm park", 13));
        q5a.addOption(factory.createOption("Been to some other place of entertainment " +
                "(e.g. dance, club, bingo, casino)", 14));
        q5a.addOption(factory.createOption("Been on any other outdoor trips " +
                "(including going to places of natural beauty, picnics, going " +
                "for a drive or going to the beach)", 15));
        Option q5aOther = factory.createOption("Other (please state)", 16);
        q5aOther.setTextEntryAllowed(true);
        q5a.addOption(q5aOther);

        NumericEntry q5Option = factory.createNumericEntry(
                "Leisure activity number of times", "Number of times in last 3 months");
        q5.addEntry(q5Option);
        q5Option.setSection(leisureSec);
        q5Option.setDefaultValue(new Double(0.0));

        NumericEntry q5Freq = factory.createNumericEntry("Amount of time",
                "Amount of time");
        q5.addEntry(q5Freq);
        q5Freq.setSection(leisureSec);
        q5Freq.setDefaultValue(new Double(0.0));
        q5Freq.addUnit(hours);

        CompositeEntry q6 = factory.createComposite("QE6", "What sports or other physical activities do you do in your spare time " +
                "For each activity could you please tell me whether you have done this in the " +
                "last 3 months, how often and the asmount of time you spend each time you " +
                "do the activity?");
        doc.addEntry(q6);
        q6.setSection(leisureSec);
        q6.setLabel(sec+".6");

        OptionEntry q6a = factory.createOptionEntry(
                "Physical Activity", "Activity");
        q6.addEntry(q6a);
        q6a.setSection(leisureSec);
        q6a.addOption(factory.createOption("Swimming or diving", 1));
        q6a.addOption(factory.createOption("Cycling", 2));
        q6a.addOption(factory.createOption("Indoor or outdoor bowls", 3));
        q6a.addOption(factory.createOption("Tenpin bowling", 4));
        q6a.addOption(factory.createOption("Keep fit, aerobics, yoga, dance exercise", 5));
        q6a.addOption(factory.createOption("Martial arts", 6));
        q6a.addOption(factory.createOption("Weight training or weight lifting", 7));
        q6a.addOption(factory.createOption("Gymnastics", 8));
        q6a.addOption(factory.createOption("Snooker, pool or billiards", 9));
        q6a.addOption(factory.createOption("Darts", 10));
        q6a.addOption(factory.createOption("Rugby", 11));
        q6a.addOption(factory.createOption("Football", 12));
        q6a.addOption(factory.createOption("Gaelic sports", 13));
        q6a.addOption(factory.createOption("Cricket", 14));
        q6a.addOption(factory.createOption("Hockey", 15));
        q6a.addOption(factory.createOption("Netball", 16));
        q6a.addOption(factory.createOption("Tennis", 17));
        q6a.addOption(factory.createOption("Badminton", 18));
        q6a.addOption(factory.createOption("Squash", 19));
        q6a.addOption(factory.createOption("Basketball", 20));
        q6a.addOption(factory.createOption("Table tennis", 21));
        q6a.addOption(factory.createOption("Track and field athletics", 22));
        q6a.addOption(factory.createOption("Jogging, cross country, road running", 23));
        q6a.addOption(factory.createOption("Angling/fishing", 24));
        q6a.addOption(factory.createOption("Yachting or dinghy sailing", 25));
        q6a.addOption(factory.createOption("Canoeing", 26));
        q6a.addOption(factory.createOption("Windsurfing/board sailing", 27));
        q6a.addOption(factory.createOption("Ice-skating", 28));
        q6a.addOption(factory.createOption("Curling", 29));
        q6a.addOption(factory.createOption("Golf", 30));
        q6a.addOption(factory.createOption("Skiing", 31));
        q6a.addOption(factory.createOption("Horse riding", 32));
        q6a.addOption(factory.createOption("Climbing/mountaineering", 33));
        q6a.addOption(factory.createOption("Motor sports", 34));
        q6a.addOption(factory.createOption("Shooting", 35));
        q6a.addOption(factory.createOption("Walking or hiking for 2 miles or more (recreationally)", 36));
        q6a.addOption(factory.createOption("Volleyball", 37));
        Option q6aOther = factory.createOption("Other (please state)", 38);
        q6aOther.setTextEntryAllowed(true);
        q6a.addOption(q6aOther);

        NumericEntry q6Option = factory.createNumericEntry(
                "Physical activity number of times", "Number of times");
        q6.addEntry(q6Option);
        q6Option.setSection(leisureSec);
        q6Option.setDefaultValue(new Double(0.0));

        NumericEntry q6Freq = factory.createNumericEntry("Amount of time",
                "Amount of time");
        q6.addEntry(q6Freq);
        q6Freq.setSection(leisureSec);
        q6Freq.setDefaultValue(new Double(0.0));
        q6Freq.addUnit(hours);

        NumericEntry q7 = factory.createNumericEntry(
                "QE7",
                "How much time do you spend socialising?");
        doc.addEntry(q7);
        q7.setSection(leisureSec);
        q7.setLabel(sec+".7");
        q7.addValidationRule(positive);
        q7.addUnit(UnitWrapper.instance().getUnit("hours"));

        NumericEntry q8 = factory.createNumericEntry(
                "QE8",
                "On how many occasions in the last 3 months have you seen friends, either " +
                        "visiting them or receiving visitors?");
        doc.addEntry(q8);
        q8.setSection(leisureSec);
        q8.setLabel(sec+".8");
        q8.addValidationRule(positive);

        NumericEntry q9 = factory.createNumericEntry(
                "QE9",
                "How much time did you tend to spend socialising on each occasion on average?");
        doc.addEntry(q9);
        q9.setSection(leisureSec);
        q9.addValidationRule(positive);
        q9.setLabel(sec+".9");
        q9.addUnit(UnitWrapper.instance().getUnit("hours"));

        NumericEntry q10 = factory.createNumericEntry(
                "QE10",
                "How much time do you spend resting each day, i.e. taking time out and doing nothing (but not " +
                        "sleeping)? Average per day, last 3 months.");
        doc.addEntry(q10);
        q10.setSection(leisureSec);
        q10.addValidationRule(positive);
        q10.setLabel(sec+".10");
        q10.addUnit(UnitWrapper.instance().getUnit("hours"));

        NumericEntry q11 = factory.createNumericEntry(
                "QE11",
                "How much time do you spend watching television or listening to the radio " +
                        "each day? Average per day, last 3 months.");
        doc.addEntry(q11);
        q11.setSection(leisureSec);
        q11.addValidationRule(positive);
        q11.setLabel(sec+".11");
        q11.addUnit(UnitWrapper.instance().getUnit("hours"));

        OptionEntry q12 = factory.createOptionEntry(
                "E12",
                "Do you have any hobbies?");
        doc.addEntry(q12);
        q12.setSection(leisureSec);
        q12.setLabel(sec+".12");
        Option q12Yes = factory.createOption("Yes", 1);
        q12.addOption(q12Yes);
        q12.addOption(factory.createOption("No", 2));

        LongTextEntry q12response = factory.createLongTextEntry(
                "EQ12 Response",
                "What are they?",
                EntryStatus.DISABLED);
        doc.addEntry(q12response);
        q12response.setSection(leisureSec);
        createOptionDependent(factory, q12Yes, q12response);

        NumericEntry q13 = factory.createNumericEntry(
                "EQ13",
                "How much time do you spend on hobbies each week (on average)?",
                EntryStatus.DISABLED);
        doc.addEntry(q13);
        q13.setSection(leisureSec);
        q13.addValidationRule(positive);
        q13.setLabel(sec+".13");
        q13.addUnit(UnitWrapper.instance().getUnit("hours"));
        createOptionDependent(factory, q12Yes, q13);

        OptionEntry q14 = factory.createOptionEntry(
                "EQ14",
                "Are you responsible for the care of any children?");
        doc.addEntry(q14);
        q14.setSection(leisureSec);
        q14.setLabel(sec+".14");
        Option q14Yes = factory.createOption("Yes (Qo to E15)", 1);
        q14.addOption(q14Yes);
        q14.addOption(factory.createOption("No (Go to E17)", 2));

        IntegerEntry q15 = factory.createIntegerEntry(
                "EQ15",
                "How many?",
                EntryStatus.DISABLED);
        doc.addEntry(q15);
        q15.setSection(leisureSec);
        q15.addValidationRule(positive);
        q15.setLabel(sec+".15");
        createOptionDependent(factory, q14Yes, q15);

        CompositeEntry q15a = factory.createComposite("EQ15a", "How old are they?");
        doc.addEntry(q15a);
        q15a.setSection(leisureSec);
        q15a.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, q14Yes, q15a);

        IntegerEntry q15b = factory.createIntegerEntry(
                "EQ15b");
        q15a.addEntry(q15b);
        q15b.setSection(leisureSec);
        q15b.addValidationRule(positive);

        CompositeEntry q16 = factory.createComposite("EQ16",
                "How much time do you spend doing things with your children?");
        doc.addEntry(q16);
        q16.setSection(leisureSec);
        q16.setLabel(sec+".16");
        q16.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, q14Yes, q16);

        OptionEntry q16a = factory.createOptionEntry(
                "EQ16.1", "Time spent with children");
        q16.addEntry(q16a);
        q16a.addOption(factory.createOption("a. Physical care (e.g. feeding, dressing, washing)", 1));
        q16a.addOption(factory.createOption("b. Supervision (inside and outside)", 2));
        q16a.addOption(factory.createOption("c. Teaching children (e.g. helping with homework)", 3));
        q16a.addOption(factory.createOption("d. Reading, playing and talking with children", 4));
        q16a.addOption(factory.createOption("e. Accompanying child (e.g. to school, doctor, friend's house, etc)", 5));
        Option q16aOther = factory.createOption("Other (please state)", 6);
        q16aOther.setTextEntryAllowed(true);
        q16a.addOption(q16aOther);
        q16a.setSection(leisureSec);

        NumericEntry q16Option = factory.createNumericEntry(
                "EQ16.3", "Number of times per week");
        q16.addEntry(q16Option);
        q16Option.setSection(leisureSec);
        q16Option.setDefaultValue(new Double(0.0));

        NumericEntry q16Freq = factory.createNumericEntry("Q16.4",
                "Amount of time");
        q16.addEntry(q16Freq);
        q16Freq.setSection(leisureSec);
        q16Freq.setDefaultValue(new Double(0.0));
        q16Freq.addUnit(hours);

        CompositeEntry q17 = factory.createComposite("EQ17",
                "How much time do you spend doing housework and chores per week?");
        doc.addEntry(q17);
        q17.setSection(leisureSec);
        q17.setLabel(sec+".17");

        OptionEntry q17a = factory.createOptionEntry(
                "EQ17.1", "Housework and chores");
        q17.addEntry(q17a);
        q17a.addOption(factory.createOption("Food management and preparation", 1));
        q17a.addOption(factory.createOption("Cleaning, dusting, vacuuming, washing dishes", 2));
        q17a.addOption(factory.createOption("Food shopping", 3));
        q17a.addOption(factory.createOption("Washing and ironing", 4));
        q17a.addOption(factory.createOption("Gardening", 5));
        q17a.addOption(factory.createOption("DIY and repairs", 5));
        Option q17aOther = factory.createOption("Other (please state)", 6);
        q17aOther.setTextEntryAllowed(true);
        q17a.addOption(q17aOther);
        q17a.setSection(leisureSec);

        NumericEntry q17Option = factory.createNumericEntry(
                "EQ17.3", "Number of times per week");
        q17.addEntry(q17Option);
        q17Option.setSection(leisureSec);
        q17Option.setDefaultValue(new Double(0.0));

        NumericEntry q17Freq = factory.createNumericEntry("Q16.4",
                "Amount of time");
        q17.addEntry(q17Freq);
        q17Freq.setSection(leisureSec);
        q17Freq.setDefaultValue(new Double(0.0));
        q17Freq.addUnit(hours);

        NarrativeEntry q18 = factory.createNarrativeEntry("EQ18",
        		"How much time do you spend sleeping per day (on average)? " +
        		"This includes sleep at night time and naps during the day.");
        doc.addEntry(q18);
        q18.setSection(leisureSec);
        q18.setLabel(sec+".18");

        NumericEntry q18a = factory.createNumericEntry(
                "EQ18a",
                "Good Days");
        doc.addEntry(q18a);
        q18a.setSection(leisureSec);
        q18a.addValidationRule(positive);
        q18a.addUnit(UnitWrapper.instance().getUnit("hours"));

        NumericEntry q18b = factory.createNumericEntry(
                "EQ18b",
                "Bad Days");
        doc.addEntry(q18b);
        q18b.setSection(leisureSec);
        q18b.addValidationRule(positive);
        q18b.addUnit(UnitWrapper.instance().getUnit("hours"));

        NumericEntry q19 = factory.createNumericEntry(
                "EQ19",
                "Do you spend time doing any activities not already asked about? " +
                        "(Weekly Average.)");
        doc.addEntry(q19);
        q19.setLabel(sec+".19");
        q19.setSection(leisureSec);
        q19.addValidationRule(positive);
        q19.addUnit(UnitWrapper.instance().getUnit("hours"));

        OptionEntry q20 = factory.createOptionEntry(
                "QE20",
                "Over the last 3 months do you think that your usual activities " +
                        "and/or potential activities have been affected by your mental health problems?");
        doc.addEntry(q20);
        q20.setSection(leisureSec);
        q20.setLabel(sec+".20");
        Option q20Yes = factory.createOption("Yes", 1);
        q20.addOption(q20Yes);
        q20.addOption(factory.createOption("No", 2));

        //EQ21
        CompositeEntry q21 = factory.createComposite("QE21",
                "Thinking about your usual activities, have you made any changes " +
                        "in the last 3 months because of your mental health problems?");
        doc.addEntry(q21);
        q21.setSection(leisureSec);
        q21.setLabel(sec+".21");
        q21.setEntryStatus(EntryStatus.DISABLED);
        createOptionDependent(factory, q20Yes, q21);
        q21.addRowLabel("Change to less demanding or less intensive activities");
        q21.addRowLabel("Change to more demanding or more intensive activities");
        q21.addRowLabel("Spend less time on activities");
        q21.addRowLabel("Spend more time on activities");
        q21.addRowLabel("Opportunities for taking on new activities decreased");
        q21.addRowLabel("Opportunities for taking on new activities increased");
        q21.addRowLabel("Other (specify)");

        TextEntry q21Label = factory.createTextEntry(
                "QE21Label", "");
        q21.addEntry(q21Label);
        q21Label.setSection(leisureSec);

        OptionEntry q21a = factory.createOptionEntry(
                "QE21a",
                "(i)");
        q21.addEntry(q21a);
        q21a.setSection(leisureSec);
        q21a.setLabel(sec+".20");
        Option q21aYes = factory.createOption("Yes", 1);
        q21a.addOption(q21aYes);
        q21a.addOption(factory.createOption("No", 2));

        TextEntry q21Other = factory.createTextEntry(
                "QE21 Other",
                "If you chose other in the previous question, please specify.",
                EntryStatus.OPTIONAL);
        doc.addEntry(q21Other);
        q21Other.setSection(leisureSec);

        OptionEntry q21h = factory.createOptionEntry(
                "QE21h",
                "Which category best describes your usual activities now?",
                EntryStatus.DISABLED);
        doc.addEntry(q21h);
        q21h.setSection(leisureSec);
        q21h.addOption(factory.createOption("Change to less demanding or less intensive activities", 1));
        q21h.addOption(factory.createOption("Change to more demanding or more intensive activities", 2));
        q21h.addOption(factory.createOption("Spend less time on activities", 3));
        q21h.addOption(factory.createOption("Spend more time on activities", 4));
        q21h.addOption(factory.createOption("Opportunities for taking on new activities decreased", 5));
        q21h.addOption(factory.createOption("Opportunities for taking on new activities increased", 6));
        q21h.addOption(factory.createOption("Other", 7));
        createOptionDependent(factory, q20Yes, q21h);
    }
}
