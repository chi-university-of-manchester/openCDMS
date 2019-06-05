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

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class GeneralWellBeing extends AssessmentForm {

	public static Document createDocument(Factory factory){

        ValidationRule zeroToTen = ValidationRulesWrapper.instance().getRule("ZeroTwoFourSixEightOrTen");

        Document doc = factory.createDocument("The General Well Being Scale",
                "The General Well Being Scale");
        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main section occurrence");
        mainSec.addOccurrence(mainOcc);

        NarrativeEntry instructions1 = factory.createNarrativeEntry(
                "Instructions",
                "For each question, choose the answer that best describes how you have felt and how things" +
                        " have been going for you during the past month.");

        doc.addEntry(instructions1);
        instructions1.setSection(mainSec);

        OptionEntry generalFeeling = factory.createOptionEntry("General feeling",
                "How have you been feeling in general?");
        doc.addEntry(generalFeeling);
        generalFeeling.setSection(mainSec);
        generalFeeling.setOptionCodesDisplayed(true);
        generalFeeling.setLabel("1");
		Option generalFeeling1 = factory.createOption("In excellent spirits", 5);
		Option generalFeeling2 = factory.createOption("In very good spirits", 4);
		Option generalFeeling3 = factory.createOption("In good spirits mostly", 3);
		Option generalFeeling4 = factory.createOption("I've been up and down in spirits a lot", 2);
		Option generalFeeling5 = factory.createOption("In low spirits mostly", 1);
		Option generalFeeling6 = factory.createOption("In very low spirits", 0);

		generalFeeling.addOption(generalFeeling1);
		generalFeeling.addOption(generalFeeling2);
		generalFeeling.addOption(generalFeeling3);
		generalFeeling.addOption(generalFeeling4);
		generalFeeling.addOption(generalFeeling5);
		generalFeeling.addOption(generalFeeling6);

        OptionEntry botheredByNerves = factory.createOptionEntry("Bothered by Nerves",
                "Have you been bothered by nervousness or your \"nerves\"?");
		doc.addEntry(botheredByNerves);
		botheredByNerves.setSection(mainSec);
		botheredByNerves.setOptionCodesDisplayed(true);
		botheredByNerves.setLabel("2");
		Option botheredByNerves1 = factory.createOption("Extremely so-to the point where I could not work or take care of things", 0);
		Option botheredByNerves2 = factory.createOption("Very much so", 1);
		Option botheredByNerves3 = factory.createOption("Quite a bit", 2);
		Option botheredByNerves4 = factory.createOption("Some-enough to bother me", 3);
		Option botheredByNerves5 = factory.createOption("A little", 4);
		Option botheredByNerves6 = factory.createOption("Not at all", 5);

		botheredByNerves.addOption(botheredByNerves1);
		botheredByNerves.addOption(botheredByNerves2);
		botheredByNerves.addOption(botheredByNerves3);
		botheredByNerves.addOption(botheredByNerves4);
		botheredByNerves.addOption(botheredByNerves5);
		botheredByNerves.addOption(botheredByNerves6);


        OptionEntry controlOfBehaviour = factory.createOptionEntry("Control of Behaviour",
                "Have you been in firm control of your behavior, thoughts, emotions or feelings?");
		doc.addEntry(controlOfBehaviour);
		controlOfBehaviour.setSection(mainSec);
		controlOfBehaviour.setOptionCodesDisplayed(true);
		controlOfBehaviour.setLabel("3");
		Option controlOfBehaviour1 = factory.createOption("Yes, definitely so", 5);
		Option controlOfBehaviour2 = factory.createOption("Yes, for the most part", 4);
		Option controlOfBehaviour3 = factory.createOption("Generally so", 3);
		Option controlOfBehaviour4 = factory.createOption("Not too well", 2);
		Option controlOfBehaviour5 = factory.createOption("No, and I am somewhat disturbed", 1);
		Option controlOfBehaviour6 = factory.createOption("No, and I am very disturbed", 0);

		controlOfBehaviour.addOption(controlOfBehaviour1);
		controlOfBehaviour.addOption(controlOfBehaviour2);
		controlOfBehaviour.addOption(controlOfBehaviour3);
		controlOfBehaviour.addOption(controlOfBehaviour4);
		controlOfBehaviour.addOption(controlOfBehaviour5);
		controlOfBehaviour.addOption(controlOfBehaviour6);

		OptionEntry sadDiscouraged = factory.createOptionEntry("Sad, Discouraged",
                "Have you felt so sad, discouraged, hopeless, or had so many problems that you" +
                        " wondered if anything was worthwhile?");
		doc.addEntry(sadDiscouraged);
		sadDiscouraged.setSection(mainSec);
		sadDiscouraged.setOptionCodesDisplayed(true);
		sadDiscouraged.setLabel("4");
		Option sadDiscouraged1 = factory.createOption("Extremely so-to the point I have just about given up", 0);
		Option sadDiscouraged2 = factory.createOption("Very much so", 1);
		Option sadDiscouraged3 = factory.createOption("Quite a bit", 2);
		Option sadDiscouraged4 = factory.createOption("Some-enough to bother me", 3);
		Option sadDiscouraged5 = factory.createOption("A little bit", 4);
		Option sadDiscouraged6 = factory.createOption("Not at all", 5);

		sadDiscouraged.addOption(sadDiscouraged1);
		sadDiscouraged.addOption(sadDiscouraged2);
		sadDiscouraged.addOption(sadDiscouraged3);
		sadDiscouraged.addOption(sadDiscouraged4);
		sadDiscouraged.addOption(sadDiscouraged5);
		sadDiscouraged.addOption(sadDiscouraged6);

		OptionEntry underStrain = factory.createOptionEntry("Under strain",
                "Have you been under or felt you were under any strain, stress, or pressure");
		doc.addEntry(underStrain);
		underStrain.setSection(mainSec);
		underStrain.setOptionCodesDisplayed(true);
		underStrain.setLabel("5");
		Option underStrain1 = factory.createOption("Yes-almost more than I could bear", 0);
		Option underStrain2 = factory.createOption("Yes-quite a bit of pressure", 1);
		Option underStrain3 = factory.createOption("Yes-some, more than usual", 2);
		Option underStrain4 = factory.createOption("Yes-some, but about usual", 3);
		Option underStrain5 = factory.createOption("Yes-a little", 4);
		Option underStrain6 = factory.createOption("Not at all", 5);

		underStrain.addOption(underStrain1);
		underStrain.addOption(underStrain2);
		underStrain.addOption(underStrain3);
		underStrain.addOption(underStrain4);
		underStrain.addOption(underStrain5);
		underStrain.addOption(underStrain6);

		OptionEntry happyPersonalLife = factory.createOptionEntry("Happy Personal Life",
                "How happy, satisfied, or pleased have you been with your personal life?");
		doc.addEntry(happyPersonalLife);
		happyPersonalLife.setSection(mainSec);
		happyPersonalLife.setOptionCodesDisplayed(true);
		happyPersonalLife.setLabel("6");
		Option happyPersonalLife1 = factory.createOption("Extremely happy-couldn't have been more satisfied or pleased", 5);
		Option happyPersonalLife2 = factory.createOption("Very happy", 4);
		Option happyPersonalLife3 = factory.createOption("Fairly happy", 3);
		Option happyPersonalLife4 = factory.createOption("Satisfied-pleased", 2);
		Option happyPersonalLife5 = factory.createOption("Somewhat dissatisfied", 1);
		Option happyPersonalLife6 = factory.createOption("Very dissatisfied", 0);

		happyPersonalLife.addOption(happyPersonalLife1);
		happyPersonalLife.addOption(happyPersonalLife2);
		happyPersonalLife.addOption(happyPersonalLife3);
		happyPersonalLife.addOption(happyPersonalLife4);
		happyPersonalLife.addOption(happyPersonalLife5);
		happyPersonalLife.addOption(happyPersonalLife6);

		OptionEntry losingMind = factory.createOptionEntry("Losing your Mind",
                "Have you had reason to wonder if you were losing your mind, or losing control"
                        + " over the way you act, talk, feel or of your memory?");
		doc.addEntry(losingMind);
		losingMind.setSection(mainSec);
		losingMind.setOptionCodesDisplayed(true);
		losingMind.setLabel("7");
		Option losingMind1 = factory.createOption("Not at all", 5);
		Option losingMind2 = factory.createOption("Only a little", 4);
		Option losingMind3 = factory.createOption("Some, but not enough to be concerned", 3);
		Option losingMind4 = factory.createOption("Some, and I've been a little concerned", 2);
		Option losingMind5 = factory.createOption("Some, and I am quite concerned", 1);
		Option losingMind6 = factory.createOption("Much, and I'm very concerned", 0);

		losingMind.addOption(losingMind1);
		losingMind.addOption(losingMind2);
		losingMind.addOption(losingMind3);
		losingMind.addOption(losingMind4);
		losingMind.addOption(losingMind5);
		losingMind.addOption(losingMind6);

		OptionEntry anxious = factory.createOptionEntry("Anxious",
                "Have you been anxious, worried or upset?");
		doc.addEntry(anxious);
		anxious.setSection(mainSec);
		anxious.setOptionCodesDisplayed(true);
		anxious.setLabel("8");
		Option anxious1 = factory.createOption("Extremely so-to the point of being sick or almost sick", 0);
		Option anxious2 = factory.createOption("Very much so", 1);
		Option anxious3 = factory.createOption("Quite a bit", 2);
		Option anxious4 = factory.createOption("Some-enough to bother me", 3);
		Option anxious5 = factory.createOption("A little bit", 4);
		Option anxious6 = factory.createOption("Not at all", 5);

		anxious.addOption(anxious1);
		anxious.addOption(anxious2);
		anxious.addOption(anxious3);
		anxious.addOption(anxious4);
		anxious.addOption(anxious5);
		anxious.addOption(anxious6);

		OptionEntry freshAndRested = factory.createOptionEntry("Waking up fresh and rested",
                "Have you been waking up fresh and rested?");
		doc.addEntry(freshAndRested);
		freshAndRested.setSection(mainSec);
		freshAndRested.setOptionCodesDisplayed(true);
		freshAndRested.setLabel("9");
		Option freshAndRested1 = factory.createOption("Every day", 5);
		Option freshAndRested2 = factory.createOption("Most every day", 4);
		Option freshAndRested3 = factory.createOption("Fairly often", 3);
		Option freshAndRested4 = factory.createOption("Less than half the time", 2);
		Option freshAndRested5 = factory.createOption("Rarely", 1);
		Option freshAndRested6 = factory.createOption("None of the time", 0);

		freshAndRested.addOption(freshAndRested1);
		freshAndRested.addOption(freshAndRested2);
		freshAndRested.addOption(freshAndRested3);
		freshAndRested.addOption(freshAndRested4);
		freshAndRested.addOption(freshAndRested5);
		freshAndRested.addOption(freshAndRested6);

		OptionEntry botheredByIllness = factory.createOptionEntry("Bothered by Illness",
                "Have you been bothered by illness, bodily disorder, pain or fears about your health?");
		doc.addEntry(botheredByIllness);
		botheredByIllness.setSection(mainSec);
		botheredByIllness.setOptionCodesDisplayed(true);
		botheredByIllness.setLabel("10");
		Option botheredByIllness1 = factory.createOption("All the time", 0);
		Option botheredByIllness2 = factory.createOption("Most of the time", 1);
		Option botheredByIllness3 = factory.createOption("A good bit of the time", 2);
		Option botheredByIllness4 = factory.createOption("Some of the time", 3);
		Option botheredByIllness5 = factory.createOption("A little of the time", 4);
		Option botheredByIllness6 = factory.createOption("None of the time", 5);

		botheredByIllness.addOption(botheredByIllness1);
		botheredByIllness.addOption(botheredByIllness2);
		botheredByIllness.addOption(botheredByIllness3);
		botheredByIllness.addOption(botheredByIllness4);
		botheredByIllness.addOption(botheredByIllness5);
		botheredByIllness.addOption(botheredByIllness6);

		OptionEntry interestingThings = factory.createOptionEntry("Interesting Things",
                "Has your daily life been full of things that are interesting to you?");
		doc.addEntry(interestingThings);
		interestingThings.setSection(mainSec);
		interestingThings.setOptionCodesDisplayed(true);
		interestingThings.setLabel("11");
		Option interestingThings1 = factory.createOption("All the time", 5);
		Option interestingThings2 = factory.createOption("Most of the time", 4);
		Option interestingThings3 = factory.createOption("A good bit of the time", 3);
		Option interestingThings4 = factory.createOption("Some of the time", 2);
		Option interestingThings5 = factory.createOption("A little of the time", 1);
		Option interestingThings6 = factory.createOption("None of the time", 0);

		interestingThings.addOption(interestingThings1);
		interestingThings.addOption(interestingThings2);
		interestingThings.addOption(interestingThings3);
		interestingThings.addOption(interestingThings4);
		interestingThings.addOption(interestingThings5);
		interestingThings.addOption(interestingThings6);

		OptionEntry downheartedBlue = factory.createOptionEntry("Downhearted Blue",
                "Have you felt downhearted and blue?");
		doc.addEntry(downheartedBlue);
		downheartedBlue.setSection(mainSec);
		downheartedBlue.setOptionCodesDisplayed(true);
		downheartedBlue.setLabel("12");
		Option downheartedBlue1 = factory.createOption("All the time", 0);
		Option downheartedBlue2 = factory.createOption("Most of the time", 1);
		Option downheartedBlue3 = factory.createOption("A good bit of the time", 2);
		Option downheartedBlue4 = factory.createOption("Some of the time", 3);
		Option downheartedBlue5 = factory.createOption("A little of the time", 4);
		Option downheartedBlue6 = factory.createOption("None of the time", 5);

		downheartedBlue.addOption(downheartedBlue1);
		downheartedBlue.addOption(downheartedBlue2);
		downheartedBlue.addOption(downheartedBlue3);
		downheartedBlue.addOption(downheartedBlue4);
		downheartedBlue.addOption(downheartedBlue5);
		downheartedBlue.addOption(downheartedBlue6);

		OptionEntry emotionallyStable = factory.createOptionEntry("Emotionally Stable",
                "Have you been feeling emotionally stable and sure of yourself?");
		doc.addEntry(emotionallyStable);
		emotionallyStable.setSection(mainSec);
		emotionallyStable.setOptionCodesDisplayed(true);
		emotionallyStable.setLabel("13");
		Option emotionallyStable1 = factory.createOption("All the time", 5);
		Option emotionallyStable2 = factory.createOption("Most of the time", 4);
		Option emotionallyStable3 = factory.createOption("A good bit of the time", 3);
		Option emotionallyStable4 = factory.createOption("Some of the time", 2);
		Option emotionallyStable5 = factory.createOption("A little of the time", 1);
		Option emotionallyStable6 = factory.createOption("None of the time", 0);

		emotionallyStable.addOption(emotionallyStable1);
		emotionallyStable.addOption(emotionallyStable2);
		emotionallyStable.addOption(emotionallyStable3);
		emotionallyStable.addOption(emotionallyStable4);
		emotionallyStable.addOption(emotionallyStable5);
		emotionallyStable.addOption(emotionallyStable6);

		OptionEntry tiredWornOut = factory.createOptionEntry("Tired or Worn Out",
                "Have you felt tired, worn out, used up, or exhausted?");
		doc.addEntry(tiredWornOut);
		tiredWornOut.setSection(mainSec);
		tiredWornOut.setOptionCodesDisplayed(true);
		tiredWornOut.setLabel("14");
		Option tiredWornOut1 = factory.createOption("All the time", 0);
		Option tiredWornOut2 = factory.createOption("Most of the time", 1);
		Option tiredWornOut3 = factory.createOption("A good bit of the time", 2);
		Option tiredWornOut4 = factory.createOption("Some of the time", 3);
		Option tiredWornOut5 = factory.createOption("A little of the time", 4);
		Option tiredWornOut6 = factory.createOption("None of the time", 5);

		tiredWornOut.addOption(tiredWornOut1);
		tiredWornOut.addOption(tiredWornOut2);
		tiredWornOut.addOption(tiredWornOut3);
		tiredWornOut.addOption(tiredWornOut4);
		tiredWornOut.addOption(tiredWornOut5);
		tiredWornOut.addOption(tiredWornOut6);

        NumericEntry worriedHealth = factory.createNumericEntry("Concerned about health",
                "How concerned or worried about your health have you been? 10=Not concerned at all, 0=Very concerned");
        worriedHealth.setDescription("Enter an even number from 0 - 10.  10=Not concerned at all, 0=Very concerned");
        worriedHealth.setLabel("15");
        worriedHealth.addValidationRule(zeroToTen);
        doc.addEntry(worriedHealth);
		worriedHealth.setSection(mainSec);

        NumericEntry relaxedOrTense = factory.createNumericEntry("Relaxed or tense",
                "How relaxed or tense have you been? 10=Very relaxed, 0=Very tense");
		relaxedOrTense.setDescription("Enter an even number from 0 - 10.  10=Very relaxed, 0=Very tense");
		relaxedOrTense.addValidationRule(zeroToTen);
		relaxedOrTense.setLabel("16");
		doc.addEntry(relaxedOrTense);
		relaxedOrTense.setSection(mainSec);

        NumericEntry energyPep = factory.createNumericEntry("Energy & Pep",
                "How much energy, pep and vitality have you felt? " +
                        "10=Very energetic,dynamic, 0=No energey at all, listless");
		energyPep.setDescription("Enter an even number from 0 - 10.  10=Very energetic, dynamic, 0=No energy at all, listless");
		energyPep.addValidationRule(zeroToTen);
		energyPep.setLabel("17");
		doc.addEntry(energyPep);
		energyPep.setSection(mainSec);

        NumericEntry depressedOrCheerful = factory.createNumericEntry("Depressed or cheerful",
                "How depressed or cheerful have you been? 10=Very cheerful, 0=Very depressed");
		depressedOrCheerful.setDescription("Enter an even number from 0 - 10.  10=Very cheerful, 0=Very depressed");
		depressedOrCheerful.addValidationRule(zeroToTen);
		depressedOrCheerful.setLabel("18");
		doc.addEntry(depressedOrCheerful);
		depressedOrCheerful.setSection(mainSec);

        DerivedEntry totalScore = factory.createDerivedEntry(
                "Total Score", "Total Score");
        doc.addEntry(totalScore);
        totalScore.setSection(mainSec);
        totalScore.addVariable("a", generalFeeling);
        totalScore.addVariable("b",  botheredByNerves);
        totalScore.addVariable("c", controlOfBehaviour);
        totalScore.addVariable("d", sadDiscouraged);
        totalScore.addVariable("e", underStrain);
        totalScore.addVariable("f", happyPersonalLife);
        totalScore.addVariable("g", losingMind);
        totalScore.addVariable("h", anxious);
        totalScore.addVariable("i", freshAndRested);
        totalScore.addVariable("j", botheredByIllness);
        totalScore.addVariable("k", interestingThings);
        totalScore.addVariable("l", downheartedBlue);
        totalScore.addVariable("m", emotionallyStable);
        totalScore.addVariable("n", tiredWornOut);
        totalScore.addVariable("o", worriedHealth);
        totalScore.addVariable("p", relaxedOrTense);
        totalScore.addVariable("q", energyPep);
        totalScore.addVariable("r", depressedOrCheerful);

        totalScore.setFormula("a+b+c+d+e+f+g+h+i+j+k+l+m+n+o+p+q+r");
        return doc;
	}
}
