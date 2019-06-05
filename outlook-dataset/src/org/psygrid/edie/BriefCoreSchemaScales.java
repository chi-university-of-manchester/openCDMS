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

public class BriefCoreSchemaScales extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document bcss = factory.createDocument("Brief Core Schema Scales",
                "The Brief Core Schema Scales");

        createDocumentStatuses(factory, bcss);

        // main section
        Section mainSection = factory.createSection("Main section occurrence");
        bcss.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        NarrativeEntry bcssInstructions = factory.createNarrativeEntry("bcssinstructions");
        bcss.addEntry(bcssInstructions);
        bcssInstructions.setSection(mainSection);
        bcssInstructions.setDisplayText("This questionnaire lists beliefs that people " +
        		"can hold about themselves and other people. Please indicate how strongly you " +
        		"hold each belief by selecting a number (0-4). Try to judge " +
        		"the beliefs on how you have generally, over time, viewed yourself " +
        		"and others. Do not spend too long on each belief. There are no right " +
        		"or wrong answers and the first response to each belief " +
        		"is often the most accurate.");

        List<String> questions = new ArrayList<String>();
        questions.add("I am unloved");
        questions.add("I am worthless");
        questions.add("I am weak");
        questions.add("I am vulnerable");
        questions.add("I am bad");
        questions.add("I am a failure");
        questions.add("I am respected");
        questions.add("I am valuable");
        questions.add("I am talented");
        questions.add("I am succesful");
        questions.add("I am good");
        questions.add("I am interesting");
        questions.add("Other people are hostile");
        questions.add("Other people are harsh");
        questions.add("Other people are unforgiving");
        questions.add("Other people are bad");
        questions.add("Other people are devious");
        questions.add("Other people are nasty");
        questions.add("Other people are fair");
        questions.add("Other people are good");
        questions.add("Other people are trustworthy");
        questions.add("Other people are accepting");
        questions.add("Other people are supportive");
        questions.add("Other people are truthful");

        int count = 1;
        for (String text : questions) {
			OptionEntry question = factory
					.createOptionEntry("Question " + count, text);
			bcss.addEntry(question);
			question.setSection(mainSection);
			question.setLabel(count+".");
			buildFiveOptions(factory, question);
			count++;
		}
        return bcss;
    }
    static void buildFiveOptions(Factory factory, OptionEntry q){
        Option op0 = factory.createOption("Do not believe it", 0);
        q.addOption(op0);
        Option op1 = factory.createOption("Believe it slightly", 1);
        q.addOption(op1);
        Option op2 = factory.createOption("Believe it moderately", 2);
        q.addOption(op2);
        Option op3 =  factory.createOption("Believe it very much", 3);
        q.addOption(op3);
        Option op4 =  factory.createOption("Believe it totally", 4);
        q.addOption(op4);
    }

}
