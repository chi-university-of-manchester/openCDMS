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

public class MCQ30 extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document mcq30 = factory.createDocument("MCQ30",
                "Meta-Cognitions Questionnaire");

        createDocumentStatuses(factory, mcq30);

        // main section
        Section mainSection = factory.createSection("Main section occurrence");
        mcq30.addSection(mainSection);
        mainSection.setDisplayText("Main");
        SectionOccurrence mainSectionOcc = factory.createSectionOccurrence("Main Section Occurrence");
        mainSection.addOccurrence(mainSectionOcc);

        NarrativeEntry mcq30Instructions = factory.createNarrativeEntry("mcq30instructions");
        mcq30.addEntry(mcq30Instructions);
        mcq30Instructions.setSection(mainSection);
        mcq30Instructions.setDisplayText("This questionnaire is concerned with beliefs people " +
        		"have baout their thinking. " +
        		"Lsited below are a number of beliefs that people have expressed. " +
        		"Please read each item and say how much you generally agress with " +
        		"it by selecting the appropriate number." +
        		"Please respond to all the items, there are no right or wrong answers.");

        List<String> questions = new ArrayList<String>();
        questions.add("Worrying helps me to avoid problems in the future");
        questions.add("My worrying is dangerous for me");
        questions.add("I think a lot about my thoughts");
        questions.add("I could make myself sick with worrying");
        questions.add("I am aware of the way my mind works when I am thinking through a problem");
        questions.add("If I did not control a worrying thought, and then it happened, it would be my fault");
        questions.add("I need to worry in order to remain organised");
        questions.add("I have little confidence in my memory for words or names");
        questions.add("My worrying thoughts persist, no matter how I try to stop them");
        questions.add("Worrying helps me to get things sorted out in my mind");
        questions.add("I cannot ignore my worrying thoughts");
        questions.add("I monitor my thoughts");
        questions.add("I should be in control of my thoughts all of the time");
        questions.add("My memory can mislead me at times");
        questions.add("My worrying could make me go mad");
        questions.add("I am constantly aware of my thinking");
        questions.add("I have a poor memory");
        questions.add("I pay close attention to the way my mind works");
        questions.add("Worrying helps me cope");
        questions.add("Not being able to control my thoughts is a sign of weakness");
        questions.add("When I start worrying, I cannot stop");
        questions.add("I will be punished for not controlling certain thoughts");
        questions.add("Worrying help me to solve problems");
        questions.add("I have little confidence in my memory for places");
        questions.add("It is bad to think certain thoughts");
        questions.add("I do not trust my memory");
        questions.add("If I could not control my thoughts, I would not be able to function");
        questions.add("I need to worry in order to work well");
        questions.add("I have little confidence in my memory for actions");
        questions.add("I constantly examine my thoughts");

        int count = 1;
        for (String text : questions) {
			OptionEntry question = factory
					.createOptionEntry("Question " + count, text);
			mcq30.addEntry(question);
			question.setSection(mainSection);
			question.setLabel(count+".");
			buildFourOptions(factory, question);
			count++;
		}
        return mcq30;
    }
    static void buildFourOptions(Factory factory, OptionEntry q){
        Option op0 = factory.createOption("Do not agree", 1);
        q.addOption(op0);
        Option op1 = factory.createOption("Agree slightly", 2);
        q.addOption(op1);
        Option op2 = factory.createOption("Agree moderately", 3);
        q.addOption(op2);
        Option op3 =  factory.createOption("Agree very much", 4);
        q.addOption(op3);
    }

}
