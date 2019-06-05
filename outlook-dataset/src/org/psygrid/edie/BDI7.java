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

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class BDI7 extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document bdi7 = factory.createDocument("BDI-7",
                "BDI-7");

        createDocumentStatuses(factory, bdi7);

        // main section
        Section mainSection = factory.createSection("Main section occurrence");
        bdi7.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        NarrativeEntry bdi7Instructions = factory.createNarrativeEntry("BDI-7 instructions");
        bdi7.addEntry(bdi7Instructions);
        bdi7Instructions.setSection(mainSection);
        bdi7Instructions.setDisplayText("This questionnaire consists of a group of statements. " +
        		"Please read each group of statements carefully, then pick out the one statement " +
        		"in each group which best describes the way you have been feeling during the past 2 " +
        		"weeks, including today! Circle the number beside the statement you picked. If several " +
        		"statements in the group seem to apply equally well, circle the statement which has the " +
        		"largest number.");


        // Question 1
        OptionEntry question1 = factory.createOptionEntry("", "");
        question1.setOptionCodesDisplayed(true);
        bdi7.addEntry(question1);
        question1.setSection(mainSection);
        question1.setLabel("1");
        Option sad0 = factory.createOption("I do not feel sad.", 0);
        question1.addOption(sad0);
        Option sad1 = factory.createOption("I feel sad much of the time.", 1);
        question1.addOption(sad1);
        Option sad2 = factory.createOption("I am sad all the time.", 2);
        question1.addOption(sad2);
        Option sad3 =  factory.createOption("I am so sad or unhappy that I can't stand it.", 3);
        question1.addOption(sad3);

        // Question 2
        OptionEntry question2 = factory.createOptionEntry("", "");
        question2.setOptionCodesDisplayed(true);
        bdi7.addEntry(question2);
        question2.setSection(mainSection);
        question2.setLabel("2");
        Option future0 = factory.createOption("I am not discouraged about my future.", 0);
        question2.addOption(future0);
        Option future1 = factory.createOption("I feel more discouraged about my future than I used to be.", 1);
        question2.addOption(future1);
        Option future2 = factory.createOption("I do not expect things to work out for me.", 2);
        question2.addOption(future2);
        Option future3 =  factory.createOption("I feel my future is hopeless and will only get worse.", 3);
        question2.addOption(future3);

        // Question 3
        OptionEntry question3 = factory.createOptionEntry("", "");
        question3.setOptionCodesDisplayed(true);
        bdi7.addEntry(question3);
        question3.setSection(mainSection);
        question3.setLabel("3");
        Option failure0 = factory.createOption("I do not feel like a failure.", 0);
        question3.addOption(failure0);
        Option failure1 = factory.createOption("I have failed more than I should have.", 1);
        question3.addOption(failure1);
        Option failure2 = factory.createOption("As I look back, I see a lot of failures.", 2);
        question3.addOption(failure2);
        Option failure3 =  factory.createOption("I feel I am a total failure as a person.", 3);
        question3.addOption(failure3);

        // Question 4
        OptionEntry question4 = factory.createOptionEntry("", "");
        question4.setOptionCodesDisplayed(true);
        bdi7.addEntry(question4);
        question4.setSection(mainSection);
        question4.setLabel("4");
        Option enjoy0 = factory.createOption("I get as much pleasure as I ever did from the things I enjoy.", 0);
        question4.addOption(enjoy0);
        Option enjoy1 = factory.createOption("I don't enjoy things as much as I used to.", 1);
        question4.addOption(enjoy1);
        Option enjoy2 = factory.createOption("I get very little pleasure from the things I used to enjoy.", 2);
        question4.addOption(enjoy2);
        Option enjoy3 =  factory.createOption("I can't get any pleasure from the things I used to enjoy.", 3);
        question4.addOption(enjoy3);

        // Question 5
        OptionEntry question5 = factory.createOptionEntry("", "");
        question5.setOptionCodesDisplayed(true);
        bdi7.addEntry(question5);
        question5.setSection(mainSection);
        question5.setLabel("5");
        Option selfWorth0 = factory.createOption("I feel the same about myself as ever.", 0);
        question5.addOption(selfWorth0);
        Option selfWorth1 = factory.createOption("I have lost confidence in myself.", 1);
        question5.addOption(selfWorth1);
        Option selfWorth2 = factory.createOption("I am disappointed in myself.", 2);
        question5.addOption(selfWorth2);
        Option selfWorth3 =  factory.createOption("I dislike myself.", 3);
        question5.addOption(selfWorth3);

        // Question 6
        OptionEntry question6 = factory.createOptionEntry("", "");
        question6.setOptionCodesDisplayed(true);
        bdi7.addEntry(question6);
        question6.setSection(mainSection);
        question6.setLabel("6");
        Option criticism0 = factory.createOption("I don't criticise or blame myself more than usual.", 0);
        question6.addOption(criticism0);
        Option criticism1 = factory.createOption("I am more critical of myself than I used to be.", 1);
        question6.addOption(criticism1);
        Option criticism2 = factory.createOption("I criticize myself for all of my faults.", 2);
        question6.addOption(criticism2);
        Option criticism3 =  factory.createOption("I blame myself for everything bad that happens.", 3);
        question6.addOption(criticism3);

        // Question 7
        OptionEntry question7 = factory.createOptionEntry("", "");
        question7.setOptionCodesDisplayed(true);
        bdi7.addEntry(question7);
        question7.setSection(mainSection);
        question7.setLabel("7");
        Option suicide0 = factory.createOption("I don't have any thoughts of killing myself.", 0);
        question7.addOption(suicide0);
        Option suicide1 = factory.createOption("I have thoughts of killing myself, but I would not carry them out.", 1);
        question7.addOption(suicide1);
        Option suicide2 = factory.createOption("I would like to kill myself.", 2);
        question7.addOption(suicide2);
        Option suicide3 =  factory.createOption("I would kill myself if I had the chance.", 3);
        question7.addOption(suicide3);

        // scoring section
        Section scoringSection = factory.createSection("Scoring section");
        bdi7.addSection(scoringSection);
        scoringSection.setDisplayText("Scoring");
        SectionOccurrence scoringSectionOcc = factory.createSectionOccurrence("Scoring Section Occurrence");
        scoringSection.addOccurrence(scoringSectionOcc);

        DerivedEntry bdi7Scoring = factory.createDerivedEntry(
                "BDI7 Scoring", "BDI7 Summary Score");
        bdi7.addEntry(bdi7Scoring);
        bdi7Scoring.setSection(scoringSection);
        bdi7Scoring.addVariable("a", question1);
        bdi7Scoring.addVariable("b", question2);
        bdi7Scoring.addVariable("c", question3);
        bdi7Scoring.addVariable("d", question4);
        bdi7Scoring.addVariable("e", question5);
        bdi7Scoring.addVariable("f", question6);
        bdi7Scoring.addVariable("g", question7);
        bdi7Scoring.setFormula("a+b+c+d+e+f+g");
        return bdi7;
    }
}
