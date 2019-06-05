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

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class SIAS extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document sias = factory.createDocument("SIAS",
                "SIAS - Scoring");

        createDocumentStatuses(factory, sias);

        // main section
        Section mainSection = factory.createSection("Main section occurrence");
        sias.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        NarrativeEntry siasInstructions = factory.createNarrativeEntry("SIAS instructions");
        sias.addEntry(siasInstructions);
        siasInstructions.setSection(mainSection);
        siasInstructions.setDisplayText("For each question, please select a number to indicate the degree to which you feel " +
        		"the statement is characteristic or true of you. The rating scale is as follows:");

        NarrativeEntry siasInstructions2 = factory.createNarrativeEntry("SIAS instructions 2");
        sias.addEntry(siasInstructions2);
        siasInstructions2.setSection(mainSection);
        siasInstructions2.setDisplayText("\t0 = Not at all characteristic or true of me");
        NarrativeEntry siasInstructions3 = factory.createNarrativeEntry("SIAS instructions 3");
        sias.addEntry(siasInstructions3);
        siasInstructions3.setSection(mainSection);
        siasInstructions3.setDisplayText("\t1 = Slightly characterisitic or true of me");
        NarrativeEntry siasInstructions4 = factory.createNarrativeEntry("SIAS instructions 4");
        sias.addEntry(siasInstructions4);
        siasInstructions4.setSection(mainSection);
        siasInstructions4.setDisplayText("\t2 = Moderately characteristic or true of me");
        NarrativeEntry siasInstructions5 = factory.createNarrativeEntry("SIAS instructions 5");
        sias.addEntry(siasInstructions5);
        siasInstructions5.setSection(mainSection);
        siasInstructions5.setDisplayText("\t3 = Very characteristic or true of me");
        NarrativeEntry siasInstructions6 = factory.createNarrativeEntry("SIAS instructions 6");
        sias.addEntry(siasInstructions6);
        siasInstructions6.setSection(mainSection);
        siasInstructions6.setDisplayText("\t4 = Extremely characteristic or true of me");

        List<String> questions = new ArrayList<String>();
        questions.add("I get nervous if I have to speak to someone in authority (teacher, boss).");
        questions.add("I have difficulty making eye contact with others.");
        questions.add("I become tense if I have to talk about myself or my feelings.");
        questions.add("I find it difficult mixing comfortably with the people I work with.");
        questions.add("I find it easy to make friends of my own age.");
        questions.add("I tense up if I meet an acquaintance in the street.");
        questions.add("When mixing socially, I am uncomfortable.");
        questions.add("I feel tense if I am alone with just one person.");
        questions.add("I am at ease meeting people at parties, etc.");
        questions.add("I have difficulty talking with other people.");
        questions.add("I find it easy to think of things to talk about.");
        questions.add("I worry about expressing myself in case I feel awkward.");
        questions.add("I find it difficult to disagree with another's point of view.");
        questions.add("I have difficulty talking to attractive persons of the opposite sex.");
        questions.add("I find myself worrying that I won't know what to say in social situations.");
        questions.add("I am nervous mixing with people I don't know well.");
        questions.add("I feel I'll say something embarrassing when talking.");
        questions.add("When mixing in a group I find myself worrying I will be ignored.");
        questions.add("I am tense mixing in a group.");
        questions.add("I am unsure whether to greet someone I know only slightly.");

        int count = 1;
        for (String text : questions) {
			OptionEntry question = factory
					.createOptionEntry("Question " + count, text);
			question.setOptionCodesDisplayed(true);
			sias.addEntry(question);
			question.setSection(mainSection);
			question.setLabel(count+".");
			buildOptions(factory, question);
			count++;
		}
        // scoring section
        Section scoringSection = factory.createSection("Scoring section");
        sias.addSection(scoringSection);
        scoringSection.setDisplayText("Scoring");
        SectionOccurrence scoringSectionOcc = factory.createSectionOccurrence("Scoring Section Occurrence");
        scoringSection.addOccurrence(scoringSectionOcc);

        DerivedEntry siasScoring = factory.createDerivedEntry(
                "SIAS Scoring", "SIAS Summary Score");
        sias.addEntry(siasScoring);
        siasScoring.setSection(scoringSection);
        siasScoring.addVariable("a", (BasicEntry)sias.getEntry(6));
        siasScoring.addVariable("b", (BasicEntry)sias.getEntry(7));
        siasScoring.addVariable("c", (BasicEntry)sias.getEntry(8));
        siasScoring.addVariable("d", (BasicEntry)sias.getEntry(9));
        siasScoring.addVariable("e", (BasicEntry)sias.getEntry(10));
        siasScoring.addVariable("f", (BasicEntry)sias.getEntry(11));
        siasScoring.addVariable("g", (BasicEntry)sias.getEntry(12));
        siasScoring.addVariable("h", (BasicEntry)sias.getEntry(13));
        siasScoring.addVariable("i", (BasicEntry)sias.getEntry(14));
        siasScoring.addVariable("j", (BasicEntry)sias.getEntry(15));
        siasScoring.addVariable("k", (BasicEntry)sias.getEntry(16));
        siasScoring.addVariable("l", (BasicEntry)sias.getEntry(17));
        siasScoring.addVariable("m", (BasicEntry)sias.getEntry(18));
        siasScoring.addVariable("n", (BasicEntry)sias.getEntry(19));
        siasScoring.addVariable("o", (BasicEntry)sias.getEntry(20));
        siasScoring.addVariable("p", (BasicEntry)sias.getEntry(21));
        siasScoring.addVariable("q", (BasicEntry)sias.getEntry(22));
        siasScoring.addVariable("r", (BasicEntry)sias.getEntry(23));
        siasScoring.addVariable("s", (BasicEntry)sias.getEntry(24));
        siasScoring.addVariable("t", (BasicEntry)sias.getEntry(25));

        siasScoring.setFormula("a+b+c+d+(4-e)+f+g+h+(4-i)+j+(4-k)+l+m+n+o+p+q+r+s+t");
        return sias;
    }
    static void buildOptions(Factory factory, OptionEntry q){
        Option op0 = factory.createOption("Not at all", 0);
        q.addOption(op0);
        Option op1 = factory.createOption("Slightly", 1);
        q.addOption(op1);
        Option op2 = factory.createOption("Moderately", 2);
        q.addOption(op2);
        Option op3 =  factory.createOption("Very", 3);
        q.addOption(op3);
        Option op4 =  factory.createOption("Extremely", 4);
        q.addOption(op4);
    }
}
