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

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class MANSA extends AssessmentForm {

	public static Document createDocument(Factory factory) {

		ValidationRule zeroToTwentyFour = ValidationRulesWrapper.instance().getRule("ZeroToTwentyFour");
		ValidationRule zeroTo168 = ValidationRulesWrapper.instance().getRule(
				"ZeroTo168");
		Document mansa = factory.createDocument("MANSA V2u",
                "MANSA Quality of Life Assessment");

		createDocumentStatuses(factory, mansa);

		// main section
		Section mainSection = factory.createSection("Main section occurrence");
		mansa.addSection(mainSection);
		mainSection.setDisplayText("Main");
		SectionOccurrence mainSectionOcc = factory
				.createSectionOccurrence("Main Section Occurrence");
		mainSection.addOccurrence(mainSectionOcc);

		NarrativeEntry mi1 = factory
				.createNarrativeEntry("MANSA instructions");
		mansa.addEntry(mi1);
		mi1.setSection(mainSection);
		mi1
				.setDisplayText("The MANSA asks a number of questions about your "
						+ "quality of life. The style and content of these questions have been developed "
						+ "and agreed by service users. The form takes only a short time to complete "
						+ "(about 10 minutes), the questions are quite easy and there are no right or "
						+ "wrong answers. The information that you provide is confidential.");
		NarrativeEntry mi2 = factory
				.createNarrativeEntry("MANSA instructions");
		mansa.addEntry(mi2);
		mi2.setSection(mainSection);
		mi2.setDisplayText("\nWHAT YOU DO.");
		NarrativeEntry mi3 = factory
				.createNarrativeEntry("MANSA instructions");
		mansa.addEntry(mi3);
		mi3.setSection(mainSection);
		mi3.setDisplayText("1. Please read all of the questions.");
		NarrativeEntry mi4 = factory
				.createNarrativeEntry("MANSA instructions");
		mansa.addEntry(mi4);
		mi4.setSection(mainSection);
		mi4
				.setDisplayText("2. Most questions apply to everybody and should be completed by everyone. "
						+ "Some questions have instructions that tell you whether or not the question "
						+ "applies to you, for example there are some different questions for people "
						+ "who are working compared to those who are not working. ");
		NarrativeEntry mi5 = factory
				.createNarrativeEntry("MANSA instructions");
		mansa.addEntry(mi5);
		mi5.setSection(mainSection);
		mi5
				.setDisplayText("3. For most questions you just need to tick one box to answer the question. "
						+ "For some questions you may be able to tick more than one box, for example to "
						+ "show that you live with a parent and other family. Most questions have "
						+ "instructions that tell you whether you need to tick one box only or whether "
						+ "you can tick as many boxes as apply to you. Please follow these instructions "
						+ "carefully.");
		NarrativeEntry mi6 = factory
				.createNarrativeEntry("MANSA instructions");
		mansa.addEntry(mi6);
		mi6.setSection(mainSection);
		mi6
				.setDisplayText("4. Some of the questions ask how you feel about certain aspects of your "
						+ "life and look like the examples below. Each number on the scale describes how "
						+ "you feel, ranging from 1 for terrible to 7 for delighted. Here are some "
						+ "examples of how this scale should be used. If you think that a part of your "
						+ "life e.g. health is as bad as it could be you should tick box 1. If "
						+ "you think that your health couldn�t be any better you should tick box 7 ");

		DateEntry programmeEntry = factory.createDateEntry("Date", "Date");
		mansa.addEntry(programmeEntry);
		programmeEntry.setSection(mainSection);

		// Life In General Section
		Section lifeInGeneralSection = factory
				.createSection("Life In General Section");
		mansa.addSection(lifeInGeneralSection);
		lifeInGeneralSection.setDisplayText("Life In General");
		SectionOccurrence lifeInGeneralSectionOcc = factory
				.createSectionOccurrence("Life In General Section Occurrence");
		lifeInGeneralSection.addOccurrence(lifeInGeneralSectionOcc);

		// How do you feel today
		OptionEntry question1 = factory.createOptionEntry(
                "How do you feeel today",
                "How do you feel about your life as a whole today?");
		question1.setOptionCodesDisplayed(true);
		mansa.addEntry(question1);
		question1.setSection(lifeInGeneralSection);
		question1.setLabel("1.");
		buildSevenOptions(factory, question1);

		// Health
		Section healthSection = factory.createSection("Health Section");
		mansa.addSection(healthSection);
		healthSection.setDisplayText("Health");
		SectionOccurrence healthSectionOcc = factory
				.createSectionOccurrence("Health Section Occurrence");
		healthSection.addOccurrence(healthSectionOcc);

		// How do you about your health
		OptionEntry question2 = factory.createOptionEntry(
                "How do you feel about your helath?",
                "How do you feel about your health?");
		question2.setOptionCodesDisplayed(true);
		mansa.addEntry(question2);
		question2.setSection(healthSection);
		question2.setLabel("2.");
		buildSevenOptions(factory, question2);

		// How do you feel about current mental health
		OptionEntry question3 = factory.createOptionEntry(
                "Present Mental Health",
                "How do you feel about your present mental health?");
		question3.setOptionCodesDisplayed(true);
		mansa.addEntry(question3);
		question3.setSection(healthSection);
		question3.setLabel("3.");
		buildSevenOptions(factory, question3);

		// Question 4 - Improve health
		OptionEntry question4 = factory
				.createOptionEntry(
                        "Improve Health",
                        "In the past year"
                                + ", have there been times when you wanted to improve your health?");
		mansa.addEntry(question4);
		question4.setSection(healthSection);
		question4.setLabel("4.");
		buildYesNoOptions(factory, question4);

		// Question 5 - Improve health chances restricted
		OptionEntry question5 = factory
				.createOptionEntry(
                        "Improve Health Restrictions",
                        "In the past year"
                                + ", have the chances for you to improve your health been restricted in any way?");
		mansa.addEntry(question5);
		question5.setSection(healthSection);
		question5.setLabel("5.");
		buildYesNoOptions(factory, question5);

		// Work and education section
		Section workEducationSection = factory
				.createSection("Work and Education Section");
		mansa.addSection(workEducationSection);
		workEducationSection.setDisplayText("Work and Education");
		SectionOccurrence workEducationSectionOcc = factory
				.createSectionOccurrence("Work and Education Section Occurrence");
		workEducationSection.addOccurrence(workEducationSectionOcc);

		IntegerEntry question6 = factory
				.createIntegerEntry(
                        "Months Worked",
                        "How many "
                                + "months have you worked (part-time or full-time) in the past 2 years?");
		mansa.addEntry(question6);
		question6.setSection(workEducationSection);
		question6.setLabel("6.");
		question6.addValidationRule(zeroToTwentyFour);

		OptionEntry question7 = factory.createOptionEntry(
                "Current Employment", "What is your "
                + "current employment status?");
		mansa.addEntry(question7);
		question7.setSection(workEducationSection);
		question7.setLabel("7.");
		Option ces1 = factory.createOption("In paid work", 8);
		Option ces2 = factory.createOption("In sheltered work", 7);
		Option ces3 = factory.createOption("In training / education", 6);
		Option ces4 = factory.createOption(
                "Not working due to long-term illness or disability", 5);
		Option ces5 = factory.createOption("Looking after home", 4);
		Option ces6 = factory.createOption(
                "Unemployed and actively seeking employment", 3);
		Option ces7 = factory.createOption("Retired", 2);
		Option ces8 = factory.createOption("Other", 1);
		question7.addOption(ces1);
		question7.addOption(ces2);
		question7.addOption(ces3);
		question7.addOption(ces4);
		question7.addOption(ces5);
		question7.addOption(ces6);
		question7.addOption(ces7);
		question7.addOption(ces8);

		OptionEntry question8 = factory
				.createOptionEntry(
                        "Continuous work",
                        "If working:"
                                + " Have you worked continuously over the past 3 months?",
                        EntryStatus.DISABLED);
		mansa.addEntry(question8);
		question8.setSection(workEducationSection);
		question8.setLabel("8.");
		buildYesNoOptions(factory, question8);
		createOptionDependent(factory, ces1, question8);
		createOptionDependent(factory, ces2, question8);
		createOptionDependent(factory, ces5, question8);

		IntegerEntry question9 = factory.createIntegerEntry("Hours worked",
                "If working:"
                        + " On average, how many hours a week do you work?",
                EntryStatus.DISABLED);
		mansa.addEntry(question9);
		question9.setSection(workEducationSection);
		question9.setLabel("9.");
		question9.addValidationRule(zeroTo168);
		createOptionDependent(factory, ces1, question9);
		createOptionDependent(factory, ces2, question9);
		createOptionDependent(factory, ces5, question9);

		OptionEntry question10a = factory.createOptionEntry("Feel About Job",
                "If working:" + " How do you feel about your job?",
                EntryStatus.DISABLED);
		mansa.addEntry(question10a);
		question10a.setSection(workEducationSection);
		question10a.setLabel("10a.");
		buildSevenOptions(factory, question10a);
		createOptionDependent(factory, ces1, question10a);
		createOptionDependent(factory, ces2, question10a);
		createOptionDependent(factory, ces5, question10a);

		OptionEntry question10b = factory.createOptionEntry(
                "Feel About Not Working", "If not working:"
                + " How do you feel about not working?", EntryStatus.DISABLED);
		mansa.addEntry(question10b);
		question10b.setSection(workEducationSection);
		question10b.setLabel("10b.");
		buildSevenOptions(factory, question10b);
		createOptionDependent(factory, ces3, question10b);
		createOptionDependent(factory, ces4, question10b);
		createOptionDependent(factory, ces6, question10b);
		createOptionDependent(factory, ces7, question10b);
		createOptionDependent(factory, ces8, question10b);

		OptionEntry question11 = factory
				.createOptionEntry(
                        "Improve work situation",
                        "In the past year, "
                                + "have there been times when you wanted to improve your work situation?");
		mansa.addEntry(question11);
		question11.setSection(workEducationSection);
		question11.setLabel("11.");
		buildYesNoOptions(factory, question11);

		OptionEntry question12 = factory
				.createOptionEntry(
                        "Improve work situation retrictions",
                        "In the past year, "
                                + "have the chances for you to improve your work situation been restricted in any way?");
		mansa.addEntry(question12);
		question12.setSection(workEducationSection);
		question12.setLabel("12.");
		buildYesNoOptions(factory, question12);

		// Finance Section
		Section financeSection = factory.createSection("Finance Section");
		mansa.addSection(financeSection);
		financeSection.setDisplayText("Finance");
		SectionOccurrence financeSectionOcc = factory
				.createSectionOccurrence("Finance Section Occurrence");
		financeSection.addOccurrence(financeSectionOcc);

		OptionEntry question13 = factory
				.createOptionEntry(
                        "Bills",
                        "How frequently (if at all) "
                                + "do you find it difficult to meet the cost of household bills?");
		mansa.addEntry(question13);
		question13.setSection(financeSection);
		question13.setLabel("13.");
		Option bill1 = factory.createOption("All of the time", 1);
		Option bill2 = factory.createOption("Most of the time", 2);
		Option bill3 = factory.createOption("Some of the time", 3);
		Option bill4 = factory.createOption("Seldom", 4);
		Option bill5 = factory.createOption("Never", 5);
		question13.addOption(bill1);
		question13.addOption(bill2);
		question13.addOption(bill3);
		question13.addOption(bill4);
		question13.addOption(bill5);

		OptionEntry question14 = factory.createOptionEntry(
                "Feel About Financal Situation",
                "How do you feel about your financial situation?");
		mansa.addEntry(question14);
		question14.setSection(financeSection);
		question14.setLabel("14.");
		buildSevenOptions(factory, question14);

		OptionEntry question15 = factory
				.createOptionEntry(
                        "Improve financial situation",
                        "In the past year, "
                                + "have there been times when you wanted to improve your financial situation?");
		mansa.addEntry(question15);
		question15.setSection(financeSection);
		question15.setLabel("15.");
		buildYesNoOptions(factory, question15);

		OptionEntry question16 = factory
				.createOptionEntry(
                        "Improve financial situation retrictions",
                        "In the past year, "
                                + "have the chances for you to improve your financial situation been restricted in any way?");
		mansa.addEntry(question16);
		question16.setSection(financeSection);
		question16.setLabel("16.");
		buildYesNoOptions(factory, question16);

		// Leisure Section
		Section leisureSection = factory.createSection("Leisure Section");
		mansa.addSection(leisureSection);
		leisureSection.setDisplayText("Leisure");
		SectionOccurrence leisureSectionOcc = factory
				.createSectionOccurrence("Leisure Section Occurrence");
		leisureSection.addOccurrence(leisureSectionOcc);

		OptionEntry question17 = factory.createOptionEntry(
                "Number of activities", "How many leisure "
                + "activities do you do on a weekly basis (if any)?");
		mansa.addEntry(question17);
		question17.setSection(leisureSection);
		question17.setLabel("17.");
		Option actnum1 = factory.createOption("None", 1);
		Option actnum2 = factory.createOption("One", 2);
		Option actnum3 = factory.createOption("Two or three", 3);
		Option actnum4 = factory.createOption("Four or more", 4);
		question17.addOption(actnum1);
		question17.addOption(actnum2);
		question17.addOption(actnum3);
		question17.addOption(actnum4);

		OptionEntry question18 = factory.createOptionEntry(
                "Feel About Leisure Activities",
                "How do you feel about your lesiure activities?");
		mansa.addEntry(question18);
		question18.setSection(leisureSection);
		question18.setLabel("18.");
		buildSevenOptions(factory, question18);

		OptionEntry question19 = factory
				.createOptionEntry(
                        "Improve leisure situation",
                        "In the past year, "
                                + "have there been times when you wanted to improve your leisure?");
		mansa.addEntry(question19);
		question19.setSection(leisureSection);
		question19.setLabel("19.");
		buildYesNoOptions(factory, question19);

		OptionEntry question20 = factory
				.createOptionEntry(
                        "Improve leisure situation retrictions",
                        "In the past year, "
                                + "have the chances for you to improve your leisure been restricted in any way?");
		mansa.addEntry(question20);
		question20.setSection(leisureSection);
		question20.setLabel("20.");
		buildYesNoOptions(factory, question20);

		// Social Section
		Section socialSection = factory.createSection("Leisure Section");
		mansa.addSection(socialSection);
		socialSection.setDisplayText("Social");
		SectionOccurrence socialSectionOcc = factory
				.createSectionOccurrence("Social Section Occurrence");
		socialSection.addOccurrence(socialSectionOcc);

		OptionEntry question21 = factory.createOptionEntry("Close friend",
                "Do you have anyone who you would call a 'close friend'?");
		mansa.addEntry(question21);
		question21.setSection(socialSection);
		question21.setLabel("21.");
		buildYesNoOptions(factory, question21);

		OptionEntry question22 = factory
				.createOptionEntry(
                        "Seen friends",
                        "In the past week have you had contact with a friend (either face to face or by telephone)?");
		mansa.addEntry(question22);
		question22.setSection(socialSection);
		question22.setLabel("22.");
		buildYesNoOptions(factory, question22);

		OptionEntry question23 = factory.createOptionEntry(
                "Feel About Number Friends",
                "How do you feel about the number of friends you have?");
		mansa.addEntry(question23);
		question23.setSection(socialSection);
		question23.setLabel("23.");
		buildSevenOptions(factory, question23);

		OptionEntry question24 = factory
				.createOptionEntry("Feel About Relationship With Friends",
                        "How do you feel about the relationships you have with your friends?");
		mansa.addEntry(question24);
		question24.setSection(socialSection);
		question24.setLabel("24.");
		buildSevenOptions(factory, question24);

		OptionEntry question25 = factory
				.createOptionEntry(
                        "Improve social situation",
                        "In the past year, "
                                + "have there been times when you wanted to improve your social life?");
		mansa.addEntry(question25);
		question25.setSection(socialSection);
		question25.setLabel("25.");
		buildYesNoOptions(factory, question25);

		OptionEntry question26 = factory
				.createOptionEntry(
                        "Improve social situation retrictions",
                        "In the past year, "
                                + "have the chances for you to improve your social life been restricted in any way?");
		mansa.addEntry(question26);
		question26.setSection(socialSection);
		question26.setLabel("26.");
		buildYesNoOptions(factory, question26);

		// Safety Section
		Section safetySection = factory.createSection("Safety Section");
		mansa.addSection(safetySection);
		safetySection.setDisplayText("Safety");
		SectionOccurrence safetySectionOcc = factory
				.createSectionOccurrence("Leisure Section Occurrence");
		safetySection.addOccurrence(safetySectionOcc);

		OptionEntry question27 = factory.createOptionEntry(
                "Victim of Violence",
                "In the past year, have you been a victim of violence?");
		mansa.addEntry(question27);
		question27.setSection(safetySection);
		question27.setLabel("27.");
		buildYesNoOptions(factory, question27);

		OptionEntry question28 = factory.createOptionEntry(
                "Feel About Safety",
                "How do you feel about your personal safety?");
		mansa.addEntry(question28);
		question28.setSection(safetySection);
		question28.setLabel("28.");
		buildSevenOptions(factory, question28);

		OptionEntry question29 = factory
				.createOptionEntry(
                        "Improve safety situation",
                        "In the past year, "
                                + "have there been times when you wanted to improve your personal safety?");
		mansa.addEntry(question29);
		question29.setSection(safetySection);
		question29.setLabel("29.");
		buildYesNoOptions(factory, question29);

		OptionEntry question30 = factory
				.createOptionEntry(
                        "Improve safety situation retrictions",
                        "In the past year, "
                                + "have the chances for you to improve your personal safety been restricted in any way?");
		mansa.addEntry(question30);
		question30.setSection(safetySection);
		question30.setLabel("30.");
		buildYesNoOptions(factory, question30);

		// Living Situation section
		Section livingSituation = factory
				.createSection("Living Situation Section");
		mansa.addSection(livingSituation);
		livingSituation.setDisplayText("Living Situation");
		SectionOccurrence livingSituationOcc = factory
				.createSectionOccurrence("Living Situation Section Occurrence");
		livingSituation.addOccurrence(livingSituationOcc);

		OptionEntry question31 = factory.createOptionEntry(
                "Current Accommodation",
                "In which type of accommodation do you currently live?");
		mansa.addEntry(question31);
		question31.setSection(livingSituation);
		question31.setLabel("31.");
		Option at1 = factory.createOption("House or flat (owned)", 10);
		Option at2 = factory.createOption("House or flat (rented)", 9);
		Option at3 = factory.createOption("Boarding out (inc B&B)", 8);
		Option at4 = factory.createOption("Mobile Home", 7);
		Option at5 = factory.createOption("Hostel/Supported or Group Home", 6);
		Option at6 = factory.createOption("Sheltered housing", 5);
		Option at7 = factory.createOption("Residential home", 4);
		Option at8 = factory.createOption("Nursing home", 3);
		Option at9 = factory.createOption("Hospital ward", 2);
		Option at10 = factory.createOption("Homeless", 1);
		question31.addOption(at1);
		question31.addOption(at2);
		question31.addOption(at3);
		question31.addOption(at4);
		question31.addOption(at5);
		question31.addOption(at6);
		question31.addOption(at7);
		question31.addOption(at8);
		question31.addOption(at9);
		question31.addOption(at10);

		OptionEntry question32 = factory.createOptionEntry(
                "Feel About Accommodation",
                "How do you feel about your accommodation?");
		mansa.addEntry(question32);
		question32.setSection(livingSituation);
		question32.setLabel("32.");
		buildSevenOptions(factory, question32);

		OptionEntry question33 = factory.createOptionEntry(
                "Improve accommodation", "In the past year, "
                + "have you wanted to improve your accommodation?");
		mansa.addEntry(question33);
		question33.setSection(livingSituation);
		question33.setLabel("33.");
		buildYesNoOptions(factory, question33);

		OptionEntry question34 = factory
				.createOptionEntry(
                        "Improve accommodation retrictions",
                        "In the past year, "
                                + "have the chances for you to improve your accommodation been restricted in any way?");
		mansa.addEntry(question34);
		question34.setSection(livingSituation);
		question34.setLabel("34.");
		buildYesNoOptions(factory, question34);

        OptionEntry question35 = factory.createOptionEntry("Q35", "Do you live alone?");
        mansa.addEntry(question35);
        question35.setSection(livingSituation);
        question35.setLabel("35.");
        Option q35yes = factory.createOption("Q35Yes", "Yes", 1);
        question35.addOption(q35yes);
        Option q35no= factory.createOption("Q35No", "No", 0);
        question35.addOption(q35no);

        CompositeEntry question35a = factory.createComposite("Q35a",
                "Who do you live with (if anybody) in your current home?");
        question35a.setEntryStatus(EntryStatus.DISABLED);
        mansa.addEntry(question35a);
        question35a.setSection(livingSituation);
        question35a.setLabel("35a.");
        createOptionDependent(factory, q35no, question35a);

		OptionEntry question35a1 = factory.createOptionEntry("Q35a1", "");
		question35a.addEntry(question35a1);
		question35a1.setSection(livingSituation);
		Option lw2 = factory.createOption("Spouse / partner", 2);
		Option lw3 = factory.createOption("Parent(s)", 3);
		Option lw4 = factory.createOption("Children under 18", 4);
		Option lw5 = factory.createOption("Children over 18", 5);
		Option lw6 = factory.createOption("Other family", 6);
		Option lw7 = factory.createOption("Non-family", 7);
		question35a1.addOption(lw2);
		question35a1.addOption(lw3);
		question35a1.addOption(lw4);
		question35a1.addOption(lw5);
		question35a1.addOption(lw6);
		question35a1.addOption(lw7);

		OptionEntry question36a = factory
				.createOptionEntry(
                        "Feel About Living With",
                        "If living with other people:"
                                + " How do you feel about the people that you live with?",
                        EntryStatus.DISABLED);
		mansa.addEntry(question36a);
		question36a.setSection(livingSituation);
		question36a.setLabel("36a.");
		buildSevenOptions(factory, question36a);
		createOptionDependent(factory, q35no, question36a);

		OptionEntry question36b = factory.createOptionEntry(
                "Feel About Living Alone", "If living alone:"
                + " How do you feel about living alone?",
                EntryStatus.DISABLED);
		mansa.addEntry(question36b);
		question36b.setSection(livingSituation);
		question36b.setLabel("36b.");
		buildSevenOptions(factory, question36b);
		createOptionDependent(factory, q35yes, question36b);

		OptionEntry question37 = factory
				.createOptionEntry(
                        "Improve living situation",
                        "In the past year, "
                                + "have you wanted to change your living arrangements?");
		mansa.addEntry(question37);
		question37.setSection(livingSituation);
		question37.setLabel("37.");
		buildYesNoOptions(factory, question37);

		OptionEntry question38 = factory
				.createOptionEntry(
                        "Improve living situation retrictions",
                        "In the past year, "
                                + "have the chances for you to improve your living arrangements been restricted in any way?");
		mansa.addEntry(question38);
		question38.setSection(livingSituation);
		question38.setLabel("38.");
		buildYesNoOptions(factory, question38);

		// Family section
		Section familySection = factory.createSection("Family Section");
		mansa.addSection(familySection);
		familySection.setDisplayText("Family");
		SectionOccurrence familySectionOcc = factory
				.createSectionOccurrence("Family Section Occurrence");
		familySection.addOccurrence(familySectionOcc);

		OptionEntry question39 = factory
				.createOptionEntry(
                        "Frequency of Contact With Relatives",
                        "How often do you have contact with a relative (not including those who live with you) either face to face or by telephone?");
		mansa.addEntry(question39);
		question39.setLabel("39.");
		question39.setSection(familySection);
		Option rc1 = factory.createOption("Not at all", 1);
		Option rc2 = factory.createOption("Daily", 7);
		Option rc3 = factory.createOption("At least weekly", 6);
		Option rc4 = factory.createOption("At least monthly", 5);
		Option rc5 = factory.createOption("At least 3 monthly", 4);
		Option rc6 = factory.createOption("At least yearly", 3);
		Option rc7 = factory.createOption("Less than yearly", 2);
		question39.addOption(rc1);
		question39.addOption(rc2);
		question39.addOption(rc3);
		question39.addOption(rc4);
		question39.addOption(rc5);
		question39.addOption(rc6);
		question39.addOption(rc7);

		OptionEntry question40 = factory.createOptionEntry(
                "Feel About Family",
                "How do you feel about your relationship with your family?");
		mansa.addEntry(question40);
		question40.setSection(familySection);
		question40.setLabel("40.");
		buildSevenOptions(factory, question40);

		OptionEntry question41 = factory.createOptionEntry(
                "Improve family life", "In the past year, "
                + "have you wanted to improve your family life?");
		mansa.addEntry(question41);
		question41.setSection(familySection);
		question41.setLabel("41.");
		buildYesNoOptions(factory, question41);

		OptionEntry question42 = factory
				.createOptionEntry(
                        "Improve family life retrictions",
                        "In the past year, "
                                + "have the chances for you to improve your family life been restricted in any way?");
		mansa.addEntry(question42);
		question42.setSection(familySection);
		question42.setLabel("42.");
		buildYesNoOptions(factory, question42);

		// Life Overall Section
		Section lifeOverallSection = factory
				.createSection("Life Overall Section");
		mansa.addSection(lifeOverallSection);
		lifeOverallSection.setDisplayText("Life Overall");
		SectionOccurrence lifeOverallSectionOcc = factory
				.createSectionOccurrence("Life Overall Section Occurrence");
		lifeOverallSection.addOccurrence(lifeOverallSectionOcc);

		// How do you feel today
		OptionEntry question43 = factory.createOptionEntry(
                "How do you feeel about life",
                "How do you feel about your life as a whole?");
		question43.setOptionCodesDisplayed(true);
		mansa.addEntry(question43);
		question43.setSection(lifeOverallSection);
		question43.setLabel("43.");
		buildSevenOptions(factory, question43);

		return mansa;
	}

	static void buildSevenOptions(Factory factory, OptionEntry q) {
		Option op1 = factory.createOption("Terrible", 1);
		q.addOption(op1);
		Option op2 = factory.createOption("Displeased", 2);
		q.addOption(op2);
		Option op3 = factory.createOption("Mostly dissatisfied", 3);
		q.addOption(op3);
		Option op4 = factory.createOption("Mixed", 4);
		q.addOption(op4);
		Option op5 = factory.createOption("Mostly satisfied", 5);
		q.addOption(op5);
		Option op6 = factory.createOption("Pleased", 6);
		q.addOption(op6);
		Option op7 = factory.createOption("Delighted", 7);
		q.addOption(op7);
	}

	static void buildYesNoOptions(Factory factory, OptionEntry q) {
		Option op1 = factory.createOption("No", 0);
		q.addOption(op1);
		Option op2 = factory.createOption("Yes", 1);
		q.addOption(op2);
	}
}
