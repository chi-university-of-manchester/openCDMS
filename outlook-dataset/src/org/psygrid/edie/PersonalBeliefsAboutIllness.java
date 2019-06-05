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

public class PersonalBeliefsAboutIllness extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document pbaiq = factory.createDocument("PBIQ-R",
                "Personal Beliefs About Illness Questionnaire");

        createDocumentStatuses(factory, pbaiq);

        // main section
        Section mainSection = factory.createSection("Main section occurrence");
        pbaiq.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        NarrativeEntry pbaiqInstructions = factory.createNarrativeEntry("PBAIQ-R instructions");
        pbaiq.addEntry(pbaiqInstructions);
        pbaiqInstructions.setSection(mainSection);
        pbaiqInstructions.setDisplayText("Instructions: We are interested in your feelings about illness. " +
        		"Please take a few minutes to complete the questions below by circling your answer.");

        List<String> questions = new ArrayList<String>();
        questions.add("My experiences frighten me...");
        questions.add("There must always have been something wrong with me as a person (to have caused this experiences)...");
        questions.add("I am embarrassed to talk about my experiences...");
        questions.add("My experiences may mean that I should be kept away from others...");
        questions.add("I find it difficult to cope with my current experiences...");
        questions.add("I am fundamentally normal, my experiences are like any other...");
        questions.add("I am capable of very little as a result of my experiences...");
        questions.add("My experiences are a judgement on me...");
        questions.add("I am powerless to influence or control my experiences...");
        questions.add("There is something about my personality that causes these experiences...");
        questions.add("It is hard for me to work or keep a job because of my experiences...");
        questions.add("I can talk to most people about my experiences...");
        questions.add("There is something strange about me which is responsible for these experiences...");

        int count = 1;
        for (String text : questions) {
			OptionEntry question = factory
					.createOptionEntry("Question " + count, text);
			pbaiq.addEntry(question);
			question.setSection(mainSection);
			question.setLabel(count+".");
			buildFourOptions(factory, question);
			count++;
		}

        return pbaiq;
    }
    static void buildFourOptions(Factory factory, OptionEntry q){
        Option op0 = factory.createOption("Strongly Disagree", 1);
        q.addOption(op0);
        Option op1 = factory.createOption("Disagree", 2);
        q.addOption(op1);
        Option op2 = factory.createOption("Agree", 3);
        q.addOption(op2);
        Option op3 =  factory.createOption("Strongly Agree", 4);
        q.addOption(op3);
    }
    static void buildThreeOptions(Factory factory, OptionEntry q){
        Option op0 = factory.createOption("Disagree", 1);
        q.addOption(op0);
        Option op1 = factory.createOption("Unsure", 2);
        q.addOption(op1);
        Option op2 = factory.createOption("Agree", 3);
        q.addOption(op2);

    }
}
