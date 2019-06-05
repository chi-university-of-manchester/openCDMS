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

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;

public class TimeUseScoreSheet extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        ValidationRule positiveRule = ValidationRulesWrapper.instance().getRule("Positive");

        Document doc = factory.createDocument("Time Use Interview Score Sheet",
                "Time Use Interview Score Sheet");

        createDocumentStatuses(factory, doc);

        //Score sheet employment section
        Section scoreEmpSec = factory.createSection("Score Sheet Employment section");
        doc.addSection(scoreEmpSec);
        scoreEmpSec.setDisplayText("Employment");
        SectionOccurrence scoreEmpSecOcc = factory.createSectionOccurrence("Score Sheet Employment section occurrence");
        scoreEmpSec.addOccurrence(scoreEmpSecOcc);

        OptionEntry scorePaidWork = factory.createOptionEntry("Paid Work", "Is paid work in the last month present or absent?");
        doc.addEntry(scorePaidWork);
        scorePaidWork.setSection(scoreEmpSec);
        scorePaidWork.setDescription("Present = 'YES' response to Question 1 (a), 1 (b), or Question 2; Absent = ‘NO’ response to Question 1 or 2");
        scorePaidWork.addOption(factory.createOption("Present", 1));
        scorePaidWork.addOption(factory.createOption("Absent", 2));

        TextEntry scoreTypeWork = factory.createTextEntry("Type of Work", "Type of work/job title");
        doc.addEntry(scoreTypeWork);
        scoreTypeWork.setSection(scoreEmpSec);
        scoreTypeWork.setDescription("From Question 4");

        OptionEntry scoreSalaryBand = factory.createOptionEntry("Score - Salary band",
                "Salary band");
        doc.addEntry(scoreSalaryBand);
        scoreSalaryBand.setSection(scoreEmpSec);
        scoreSalaryBand.setDescription("From Question 6");
        scoreSalaryBand.addOption(factory.createOption("Less than £215", "Less than £215", 1));
        scoreSalaryBand.addOption(factory.createOption("£215 to less than £435", "£215 to less than £435", 2));
        scoreSalaryBand.addOption(factory.createOption("£435 to less than £870", "£435 to less than £870", 3));
        scoreSalaryBand.addOption(factory.createOption("£870 to less than £1305", "£870 to less than £1305", 4));
        scoreSalaryBand.addOption(factory.createOption("£1305 to less than £1740", "£1305 to less than £1740", 5));
        scoreSalaryBand.addOption(factory.createOption("£1740 to less than £2820", "£1740 to less than £2820", 6));
        scoreSalaryBand.addOption(factory.createOption("£2820 to less than £3420", "£2820 to less than £3420", 7));
        scoreSalaryBand.addOption(factory.createOption("£3420 to less than £3830", "£3420 to less than £3830", 8));
        scoreSalaryBand.addOption(factory.createOption("£3830 to less than £4580", "£3830 to less than £4580", 9));
        scoreSalaryBand.addOption(factory.createOption("£4580 to less than £6670", "£4580 to less than £6670", 10));
        scoreSalaryBand.addOption(factory.createOption("£6670 or more", "£6670 or more", 11));

        NumericEntry scoreHoursPerWeek = factory.createNumericEntry("Hours per week", "Hours per week in paid employment over the last month");
        doc.addEntry(scoreHoursPerWeek);
        scoreHoursPerWeek.setSection(scoreEmpSec);
        scoreHoursPerWeek.setDescription("This should be calculated by adding all hours paid employment (from Questions 5 and 7) "
                                        +"and dividing by 4 to get a weekly average.  This includes time spent on government "
                                        +"training schemes. e.g. if someone generally gets one paid day of work per month, this is "
                                        +"taken as 2 hours per week");
        scoreHoursPerWeek.addValidationRule(positiveRule);

        OptionEntry scoreActiveSearchWork = factory.createOptionEntry("Active Search Work", "Active searching for work?");
        doc.addEntry(scoreActiveSearchWork);
        scoreActiveSearchWork.setSection(scoreEmpSec);
        scoreActiveSearchWork.setDescription("Present = 'YES' response to Question 8; Absent = 'NO' response to Question 8");
        scoreActiveSearchWork.addOption(factory.createOption("Present", 1));
        scoreActiveSearchWork.addOption(factory.createOption("Absent", 2));

        NumericEntry scoreNumWorkSearch = factory.createNumericEntry("Number Work Search", "Number of different work searching activities");
        doc.addEntry(scoreNumWorkSearch);
        scoreNumWorkSearch.setSection(scoreEmpSec);
        scoreNumWorkSearch.setDescription("Taken from Question 9");
        scoreNumWorkSearch.addValidationRule(positiveRule);

        OptionEntry scorePaidWorkEver = factory.createOptionEntry("Paid Work Ever", "Has paid work ever been present? (NB: Only code these items if no current paid work)");
        doc.addEntry(scorePaidWorkEver);
        scorePaidWorkEver.setSection(scoreEmpSec);
        scorePaidWorkEver.setDescription("Present = ‘YES’ response to Question 3b; Absent = ‘NO’ response to Question 3b");
        Option scorePaidWorkEverPresent = factory.createOption("Present", 1);
        scorePaidWorkEver.addOption(scorePaidWorkEverPresent);
        scorePaidWorkEver.addOption(factory.createOption("Absent", 2));

        NumericEntry scoreWeeksSinceLastWorked = factory.createNumericEntry("Weeks Since Last Worked",
                "Number of weeks since last worked",
                EntryStatus.DISABLED);
        doc.addEntry(scoreWeeksSinceLastWorked);
        scoreWeeksSinceLastWorked.setSection(scoreEmpSec);
        scoreWeeksSinceLastWorked.setDescription("Response to Question 3c");
        scoreWeeksSinceLastWorked.addValidationRule(positiveRule);
        createOptionDependent(factory, scorePaidWorkEverPresent, scoreWeeksSinceLastWorked);

        NumericEntry scoreHoursLastJob = factory.createNumericEntry("Hours Last Job",
                "Number of hours per week worked in last job",
                EntryStatus.DISABLED);
        doc.addEntry(scoreHoursLastJob);
        scoreHoursLastJob.setSection(scoreEmpSec);
        scoreHoursLastJob.setDescription("Response to Question 5");
        scoreHoursLastJob.addValidationRule(positiveRule);
        createOptionDependent(factory, scorePaidWorkEverPresent, scoreHoursLastJob);

        TextEntry scoreTypeLastJob = factory.createTextEntry("Type Last Job",
                "What was the last paid job?",
                EntryStatus.DISABLED);
        doc.addEntry(scoreTypeLastJob);
        scoreTypeLastJob.setSection(scoreEmpSec);
        scoreTypeLastJob.setDescription("From Question 4");
        createOptionDependent(factory, scorePaidWorkEverPresent, scoreTypeLastJob);

        OptionEntry scoreSalaryBandLast = factory.createOptionEntry("Salary band last job",
                "Salary band?",
                EntryStatus.DISABLED);
        doc.addEntry(scoreSalaryBandLast);
        scoreSalaryBandLast.setSection(scoreEmpSec);
        scoreSalaryBandLast.setDescription("From Question 6");
        scoreSalaryBandLast.addOption(factory.createOption("Less than £215", "Less than £215", 1));
        scoreSalaryBandLast.addOption(factory.createOption("£215 to less than £435", "£215 to less than £435", 2));
        scoreSalaryBandLast.addOption(factory.createOption("£435 to less than £870", "£435 to less than £870", 3));
        scoreSalaryBandLast.addOption(factory.createOption("£870 to less than £1305", "£870 to less than £1305", 4));
        scoreSalaryBandLast.addOption(factory.createOption("£1305 to less than £1740", "£1305 to less than £1740", 5));
        scoreSalaryBandLast.addOption(factory.createOption("£1740 to less than £2820", "£1740 to less than £2820", 6));
        scoreSalaryBandLast.addOption(factory.createOption("£2820 to less than £3420", "£2820 to less than £3420", 7));
        scoreSalaryBandLast.addOption(factory.createOption("£3420 to less than £3830", "£3420 to less than £3830", 8));
        scoreSalaryBandLast.addOption(factory.createOption("£3830 to less than £4580", "£3830 to less than £4580", 9));
        scoreSalaryBandLast.addOption(factory.createOption("£4580 to less than £6670", "£4580 to less than £6670", 10));
        scoreSalaryBandLast.addOption(factory.createOption("£6670 or more", "£6670 or more", 11));
        createOptionDependent(factory, scorePaidWorkEverPresent, scoreSalaryBandLast);

        //Score sheet education section
        Section scoreEduSec = factory.createSection("Score Sheet Education section");
        doc.addSection(scoreEduSec);
        scoreEduSec.setDisplayText("Education");
        SectionOccurrence scoreEduSecOcc = factory.createSectionOccurrence("Score Sheet Education section occurrence");
        scoreEduSec.addOccurrence(scoreEduSecOcc);

        OptionEntry scoreHighLevelEdu = factory.createOptionEntry("Highest educational qualification",
                "Highest level of educational qualification already achieved");
        doc.addEntry(scoreHighLevelEdu);
        scoreHighLevelEdu.setSection(scoreEduSec);
        scoreHighLevelEdu.setDescription("See Question 1b");
        scoreHighLevelEdu.addOption(factory.createOption("Degree level qualification including graduate membership of a professional institute or PGCE or higher (include undergraduate and postgraduate degrees)", 1));
        scoreHighLevelEdu.addOption(factory.createOption("Diploma in higher education", 2));
        scoreHighLevelEdu.addOption(factory.createOption("HNC/HND", 3));
        scoreHighLevelEdu.addOption(factory.createOption("ONC/OND", 4));
        scoreHighLevelEdu.addOption(factory.createOption("BTEC, BEC or TEC", 5));
        scoreHighLevelEdu.addOption(factory.createOption("SCOTVEC, SCOTEC or SCOTBEC", 6));
        scoreHighLevelEdu.addOption(factory.createOption("Teaching qualification excluding PGCE", 7));
        scoreHighLevelEdu.addOption(factory.createOption("Nursing or other medical qualification not yet mentioned?", 8));
        scoreHighLevelEdu.addOption(factory.createOption("Other higher education qualification below degree level", 9));
        scoreHighLevelEdu.addOption(factory.createOption("A-level or equivalent", 10));
        scoreHighLevelEdu.addOption(factory.createOption("SCE highers", 11));
        scoreHighLevelEdu.addOption(factory.createOption("NVQ/SVQ", 12));
        scoreHighLevelEdu.addOption(factory.createOption("GNVQ/GSVQ", 13));
        scoreHighLevelEdu.addOption(factory.createOption("AS-level", 14));
        scoreHighLevelEdu.addOption(factory.createOption("Certificate of sixth year studies (CSYS) or equivalent", 15));
        scoreHighLevelEdu.addOption(factory.createOption("O-Level or equivalent", 16));
        scoreHighLevelEdu.addOption(factory.createOption("SCE Standard or Ordinary (O) grade", 17));
        scoreHighLevelEdu.addOption(factory.createOption("GCSE", 18));
        scoreHighLevelEdu.addOption(factory.createOption("CSE", 19));
        scoreHighLevelEdu.addOption(factory.createOption("RSA", 20));
        scoreHighLevelEdu.addOption(factory.createOption("City and Guilds", 21));
        scoreHighLevelEdu.addOption(factory.createOption("YT certificate/YTP", 22));
        scoreHighLevelEdu.addOption(factory.createOption("Any other professional or vocational qualification or foreign qualifications (e.g. apprenticeship)", 23));


        TextEntry scoreOtherEdu = factory.createTextEntry("Other qualification",
                "Other educational or vocational qualifications already achieved");
        doc.addEntry(scoreOtherEdu);
        scoreOtherEdu.setSection(scoreEduSec);
        scoreOtherEdu.setDescription("See Question 1b");

        OptionEntry scoreCurrEdu = factory.createOptionEntry("Current education",
                "Is current education present or absent?");
        doc.addEntry(scoreCurrEdu);
        scoreCurrEdu.setSection(scoreEduSec);
        scoreCurrEdu.setDescription("Present = any 'YES' response to Questions 2, 3 or 4; Absent = 'NO' responses to Questions 2, 3 and 4");
        scoreCurrEdu.addOption(factory.createOption("Present", 1));
        scoreCurrEdu.addOption(factory.createOption("Absent", 2));

        NumericEntry scoreHoursPerWeekEdu = factory.createNumericEntry("Hours per week education",
                "Hours per week in education over the last month");
        doc.addEntry(scoreHoursPerWeekEdu);
        scoreHoursPerWeekEdu.setSection(scoreEduSec);
        scoreHoursPerWeekEdu.setDescription("This should be calculated by adding all hours spent in education "
                +"(from Questions 2, 3 4 and 5) and dividing by 4 to get a weekly average.");
        scoreHoursPerWeekEdu.addValidationRule(positiveRule);

        NumericEntry scoreNumDiffCourses = factory.createNumericEntry("Num Different Courses",
                "Number of different courses taken part in over last month");
        doc.addEntry(scoreNumDiffCourses);
        scoreNumDiffCourses.setSection(scoreEduSec);
        scoreNumDiffCourses.setDescription("Taken from Questions 2,3,4,5");
        scoreNumDiffCourses.addValidationRule(positiveRule);

        OptionEntry scoreActiveSearchEdu = factory.createOptionEntry("Active Search Edu",
                "Active searching for education?");
        doc.addEntry(scoreActiveSearchEdu);
        scoreActiveSearchEdu.setSection(scoreEduSec);
        scoreActiveSearchEdu.setDescription("Present = 'YES' response to Question 6; Absent = 'NO' response to Question 6");
        scoreActiveSearchEdu.addOption(factory.createOption("Present", 1));
        scoreActiveSearchEdu.addOption(factory.createOption("Absent", 2));

        //Score sheet voluntary work section
        Section scoreVoluntSec = factory.createSection("Score Voluntary Work section");
        doc.addSection(scoreVoluntSec);
        scoreVoluntSec.setDisplayText("Voluntary Work");
        SectionOccurrence scoreVoluntSecOcc = factory.createSectionOccurrence("Score Voluntary Work section occurrence");
        scoreVoluntSec.addOccurrence(scoreVoluntSecOcc);

        OptionEntry scoreVoluntWork = factory.createOptionEntry("Voluntary Work",
                "Is voluntary work present or absent?");
        doc.addEntry(scoreVoluntWork);
        scoreVoluntWork.setSection(scoreVoluntSec);
        scoreVoluntWork.setDescription("Present = 'YES' response to Question 1 or Question 3 (a) from Employment section; "
                +"Absent = 'NO' response to Question 1");
        scoreVoluntWork.addOption(factory.createOption("Present", 1));
        scoreVoluntWork.addOption(factory.createOption("Absent", 2));

        NumericEntry scoreVoluntHours = factory.createNumericEntry("Voluntary work hours per week",
                "Hours per week spent in voluntary work over the last month");
        doc.addEntry(scoreVoluntHours);
        scoreVoluntHours.setSection(scoreVoluntSec);
        scoreVoluntHours.setDescription("This should be calculated by multiplying number of times "
                +"(Question 2) by average length of time (Question 3) and dividing the result by 4 "
                +"to get a weekly average.");
        scoreVoluntHours.addValidationRule(positiveRule);

        //Score sheet leisure activities section
        Section scoreLeisSec = factory.createSection("Score Leisure section");
        doc.addSection(scoreLeisSec);
        scoreLeisSec.setDisplayText("Leisure Activities");
        SectionOccurrence scoreLeisSecOcc = factory.createSectionOccurrence("Score Leisure section occurrence");
        scoreLeisSec.addOccurrence(scoreLeisSecOcc);

        OptionEntry scoreLeisure = factory.createOptionEntry("Leisure",
                "Are leisure activities present or absent?");
        doc.addEntry(scoreLeisure);
        scoreLeisure.setSection(scoreLeisSec);
        scoreLeisure.setDescription("Taken from Question 1");
        scoreLeisure.addOption(factory.createOption("Present", 1));
        scoreLeisure.addOption(factory.createOption("Absent", 2));

        NumericEntry scoreLeisureHours = factory.createNumericEntry("Leisure Hours Per Week",
                "Hours per week spent in leisure activities over the last month");
        doc.addEntry(scoreLeisureHours);
        scoreLeisureHours.setSection(scoreLeisSec);
        scoreLeisureHours.setDescription("This should be calculated by multiplying number of times "
                +"by average length of time for each activity.  Then sum all of these and divide "
                +"the result by 4 to get a weekly average.");
        scoreLeisureHours.addValidationRule(positiveRule);

        NumericEntry scoreLeisureNum = factory.createNumericEntry("Number Leisure Activities",
                "Number of leisure activities taken part in over last month");
        doc.addEntry(scoreLeisureNum);
        scoreLeisureNum.setSection(scoreLeisSec);
        scoreLeisureNum.setDescription("Taken from Question 1");
        scoreLeisureNum.addValidationRule(positiveRule);

        OptionEntry scoreSport = factory.createOptionEntry("Sport",
                "Are sport/physical activities present or absent?");
        doc.addEntry(scoreSport);
        scoreSport.setSection(scoreLeisSec);
        scoreSport.setDescription("Taken from Question 2");
        scoreSport.addOption(factory.createOption("Present", 1));
        scoreSport.addOption(factory.createOption("Absent", 2));

        NumericEntry scoreSportHours = factory.createNumericEntry("Sport Hours Per Week",
                "Hours per week spent in sport/physical activities over the last month");
        doc.addEntry(scoreSportHours);
        scoreSportHours.setSection(scoreLeisSec);
        scoreSportHours.setDescription("This should be calculated by multiplying number of times "
                +"by average length of time for each activity.  Then sum all of these and divide "
                +"the result by 4 to get a weekly average.");
        scoreSportHours.addValidationRule(positiveRule);

        NumericEntry scoreSportNum = factory.createNumericEntry("Number Sport Activities",
                "Number of sport/physical activities taken part in over last month");
        doc.addEntry(scoreSportNum);
        scoreSportNum.setSection(scoreLeisSec);
        scoreSportNum.setDescription("Taken from Question 2");
        scoreSportNum.addValidationRule(positiveRule);

        NumericEntry scoreSocialising = factory.createNumericEntry("Hours per week socialising",
                "Hours per week over last month spent socialising");
        doc.addEntry(scoreSocialising);
        scoreSocialising.setSection(scoreLeisSec);
        scoreSocialising.addValidationRule(positiveRule);

        NumericEntry scoreResting = factory.createNumericEntry("Hours per week resting",
                "Hours per week over last month spent resting");
        doc.addEntry(scoreResting);
        scoreResting.setSection(scoreLeisSec);
        scoreResting.addValidationRule(positiveRule);

        //Score sheet hobbies section
        Section scoreHobbiesSec = factory.createSection("Score Hobbies section");
        doc.addSection(scoreHobbiesSec);
        scoreHobbiesSec.setDisplayText("Hobbies");
        SectionOccurrence scoreHobbiesSecOcc = factory.createSectionOccurrence("Score Hobbies section occurrence");
        scoreHobbiesSec.addOccurrence(scoreHobbiesSecOcc);

        OptionEntry scoreHobbies = factory.createOptionEntry("Hobbies",
                "Are hobbies present or absent?");
        doc.addEntry(scoreHobbies);
        scoreHobbies.setSection(scoreHobbiesSec);
        scoreHobbies.addOption(factory.createOption("Present", 1));
        scoreHobbies.addOption(factory.createOption("Absent", 2));

        NumericEntry scoreHobbiesHours = factory.createNumericEntry("Score - Hobbies Hours per week",
                "Hours per week spent on hobbies over the last month");
        doc.addEntry(scoreHobbiesHours);
        scoreHobbiesHours.setSection(scoreHobbiesSec);
        scoreHobbiesHours.setDescription("This should be calculated by multiplying number of times by average "+
                "length of time for each activity. Then sum all of these and divide the result by 4 to get a weekly average.");
        scoreHobbiesHours.addValidationRule(positiveRule);

        NumericEntry scoreHobbiesNumber = factory.createNumericEntry("Score - Number of Hobbies",
                "Number of hobbies taken part in over last month");
        doc.addEntry(scoreHobbiesNumber);
        scoreHobbiesNumber.setSection(scoreHobbiesSec);
        scoreHobbiesNumber.addValidationRule(positiveRule);

        //Score sheet childcare section
        Section scoreChildcareSec = factory.createSection("Score Childcare section");
        doc.addSection(scoreChildcareSec);
        scoreChildcareSec.setDisplayText("Childcare");
        SectionOccurrence scoreChildcareSecOcc = factory.createSectionOccurrence("Score Childcare section occurrence");
        scoreChildcareSec.addOccurrence(scoreChildcareSecOcc);

        OptionEntry scoreChildcare = factory.createOptionEntry("Childcare",
                "Childcare");
        doc.addEntry(scoreChildcare);
        scoreChildcare.setSection(scoreChildcareSec);
        scoreChildcare.addOption(factory.createOption("Applicable", 1));
        scoreChildcare.addOption(factory.createOption("Non-applicable", 2));

        NumericEntry scoreChildcareHours = factory.createNumericEntry("Childcare Hours",
                "Hours per week spent on childcare");
        doc.addEntry(scoreChildcareHours);
        scoreChildcareHours.setSection(scoreChildcareSec);
        scoreChildcareHours.setDescription("Taken from Question 3");
        scoreChildcareHours.addValidationRule(positiveRule);

        //Score sheet housework and chores section
        Section scoreHouseworkSec = factory.createSection("Score Housework and Chores section");
        doc.addSection(scoreHouseworkSec);
        scoreHouseworkSec.setDisplayText("Housework and Chores");
        SectionOccurrence scoreHouseworkSecOcc = factory.createSectionOccurrence("Score Housework and Chores section occurrence");
        scoreHouseworkSec.addOccurrence(scoreHouseworkSecOcc);

        NumericEntry scoreHouseworkHours = factory.createNumericEntry("Housework Hours",
                "Hours per week spent on housework and chores ");
        doc.addEntry(scoreHouseworkHours);
        scoreHouseworkHours.setSection(scoreHouseworkSec);
        scoreHouseworkHours.setDescription("Taken from estimate of average time including items from checklist in estimate");
        scoreHouseworkHours.addValidationRule(positiveRule);

        //Score sheet other activities section
        Section scoreOtherSec = factory.createSection("Score Other Activities section");
        doc.addSection(scoreOtherSec);
        scoreOtherSec.setDisplayText("Other Activities");
        SectionOccurrence scoreOtherSecOcc = factory.createSectionOccurrence("Score Other Activities section occurrence");
        scoreOtherSec.addOccurrence(scoreOtherSecOcc);

        NumericEntry scoreHoursSleeping = factory.createNumericEntry("Hours Sleeping",
                "Hours per day spent sleeping");
        doc.addEntry(scoreHoursSleeping);
        scoreHoursSleeping.setSection(scoreOtherSec);
        scoreHoursSleeping.setDescription("From Question 1");
        scoreHoursSleeping.addValidationRule(positiveRule);

        NumericEntry scoreHoursOther = factory.createNumericEntry("Hours Other Activities",
                "Hours per week spent on other activities over the last month");
        doc.addEntry(scoreHoursOther);
        scoreHoursOther.setSection(scoreOtherSec);
        scoreHoursOther.setDescription("From Question 2");
        scoreHoursOther.addValidationRule(positiveRule);

        NumericEntry scoreNumberOther = factory.createNumericEntry("Number Other Activities",
                "Number of other activities taken part in over last month");
        doc.addEntry(scoreNumberOther);
        scoreNumberOther.setSection(scoreOtherSec);
        scoreNumberOther.setDescription("From Question 2");
        scoreNumberOther.addValidationRule(positiveRule);

        return doc;

    }

}