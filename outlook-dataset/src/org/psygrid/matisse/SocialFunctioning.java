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

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class SocialFunctioning extends AssessmentForm {

	public static Document createDocument(Factory factory){

        Document doc = factory.createDocument("Social Functioning",
                "Social Functioning");
        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main section occurrence");
        mainSec.addOccurrence(mainOcc);

        NarrativeEntry instructions1 = factory.createNarrativeEntry(
                "Instructions",
                "The next set of questions also asks about things that some people may have difficulty with." +
                        " Please look at each statement and for each one, select the one next to the reply that comes" +
                        " closest to how you have been over the past six months");

        doc.addEntry(instructions1);
        instructions1.setSection(mainSec);

        OptionEntry taskComplete = factory.createOptionEntry("Task Completion",
                "I complete my task at work and home satisfactorily.");
        doc.addEntry(taskComplete);
        taskComplete.setSection(mainSec);
        taskComplete.setOptionCodesDisplayed(true);
        taskComplete.setLabel("1");
        Option taskComplete1 = factory.createOption("Most of the time", 0);
        Option taskComplete2 = factory.createOption("Quite often", 1);
        Option taskComplete3 = factory.createOption("Sometimes", 2);
        Option taskComplete4 = factory.createOption("Not at all", 3);
        taskComplete.addOption(taskComplete1);
        taskComplete.addOption(taskComplete2);
        taskComplete.addOption(taskComplete3);
        taskComplete.addOption(taskComplete4);

        OptionEntry taskStressful = factory.createOptionEntry("Task Stressful",
                "I find my tasks at work and at home very stressful.");
		doc.addEntry(taskStressful);
		taskStressful.setSection(mainSec);
		taskStressful.setOptionCodesDisplayed(true);
		taskStressful.setLabel("2");
		Option taskStressful1 = factory.createOption("Most of the time", 3);
		Option taskStressful2 = factory.createOption("Quite often", 2);
		Option taskStressful3 = factory.createOption("Sometimes", 1);
		Option taskStressful4 = factory.createOption("Not at all", 0);
		taskStressful.addOption(taskStressful1);
		taskStressful.addOption(taskStressful2);
		taskStressful.addOption(taskStressful3);
		taskStressful.addOption(taskStressful4);

		OptionEntry moneyProblems = factory.createOptionEntry("Money Problems",
                "I have no money problems.");
		doc.addEntry(moneyProblems);
		moneyProblems.setSection(mainSec);
		moneyProblems.setOptionCodesDisplayed(true);
		moneyProblems.setLabel("3");
		Option moneyProblems1 = factory.createOption("No problems at all", 0);
		Option moneyProblems2 = factory.createOption("Slight worries only", 1);
		Option moneyProblems3 = factory.createOption("Definite problems", 2);
		Option moneyProblems4 = factory.createOption("Very severe problems", 3);
		moneyProblems.addOption(moneyProblems1);
		moneyProblems.addOption(moneyProblems2);
		moneyProblems.addOption(moneyProblems3);
		moneyProblems.addOption(moneyProblems4);

		OptionEntry relationshipProblems = factory.createOptionEntry("Relationship problems",
                "I have difficulties in getting and keeping close relationships.");
		doc.addEntry(relationshipProblems);
		relationshipProblems.setSection(mainSec);
		relationshipProblems.setOptionCodesDisplayed(true);
		relationshipProblems.setLabel("4");
		Option relationshipProblems1 = factory.createOption("Severe difficulties", 3);
		Option relationshipProblems2 = factory.createOption("Some problems", 2);
		Option relationshipProblems3 = factory.createOption("Occasional problems", 1);
		Option relationshipProblems4 = factory.createOption("No problems at all", 0);
		relationshipProblems.addOption(relationshipProblems1);
		relationshipProblems.addOption(relationshipProblems2);
		relationshipProblems.addOption(relationshipProblems3);
		relationshipProblems.addOption(relationshipProblems4);

		OptionEntry sexProblems = factory.createOptionEntry("Sex problems",
                "I have problems in my sex life");
		doc.addEntry(sexProblems);
		sexProblems.setSection(mainSec);
		sexProblems.setOptionCodesDisplayed(true);
		sexProblems.setLabel("5");
		Option sexProblems1 = factory.createOption("Severe difficulties", 3);
		Option sexProblems2 = factory.createOption("Moderate problems", 2);
		Option sexProblems3 = factory.createOption("Occasional problems", 1);
		Option sexProblems4 = factory.createOption("No problems at all", 0);
		sexProblems.addOption(sexProblems1);
		sexProblems.addOption(sexProblems2);
		sexProblems.addOption(sexProblems3);
		sexProblems.addOption(sexProblems4);

		OptionEntry familyProblems = factory.createOptionEntry("Family problems",
                "I get on well with my family and other relatives.");
		doc.addEntry(familyProblems);
		familyProblems.setSection(mainSec);
		familyProblems.setOptionCodesDisplayed(true);
		familyProblems.setLabel("6");
		Option familyProblems1 = factory.createOption("Yes, definitely", 0);
		Option familyProblems2 = factory.createOption("Yes, usually", 1);
		Option familyProblems3 = factory.createOption("No, some problems", 2);
		Option familyProblems4 = factory.createOption("No, severe problems", 3);
		familyProblems.addOption(familyProblems1);
		familyProblems.addOption(familyProblems2);
		familyProblems.addOption(familyProblems3);
		familyProblems.addOption(familyProblems4);

		OptionEntry lonelyProblems = factory.createOptionEntry("Lonely problems",
                "I feel lonely and isolated from other people.");
		doc.addEntry(lonelyProblems);
		lonelyProblems.setSection(mainSec);
		lonelyProblems.setOptionCodesDisplayed(true);
		lonelyProblems.setLabel("7");
		Option lonelyProblems1 = factory.createOption("Almost all the time", 3);
		Option lonelyProblems2 = factory.createOption("Much of the time", 2);
		Option lonelyProblems3 = factory.createOption("Not usually", 1);
		Option lonelyProblems4 = factory.createOption("Not at all", 0);
		lonelyProblems.addOption(lonelyProblems1);
		lonelyProblems.addOption(lonelyProblems2);
		lonelyProblems.addOption(lonelyProblems3);
		lonelyProblems.addOption(lonelyProblems4);

		OptionEntry enjoyTime = factory.createOptionEntry("Enjoy Time",
                "I enjoy my spare time");
		doc.addEntry(enjoyTime);
		enjoyTime.setSection(mainSec);
		enjoyTime.setOptionCodesDisplayed(true);
		enjoyTime.setLabel("8");
		Option enjoyTime1 = factory.createOption("Very much", 0);
		Option enjoyTime2 = factory.createOption("Sometimes", 1);
		Option enjoyTime3 = factory.createOption("Not often", 2);
		Option enjoyTime4 = factory.createOption("Not at all", 3);
		enjoyTime.addOption(enjoyTime1);
		enjoyTime.addOption(enjoyTime2);
		enjoyTime.addOption(enjoyTime3);
		enjoyTime.addOption(enjoyTime4);

        DerivedEntry totalScore = factory.createDerivedEntry(
                "Total Score", "Total Score");
        doc.addEntry(totalScore);
        totalScore.setSection(mainSec);
        totalScore.addVariable("a", taskComplete);
        totalScore.addVariable("b", taskStressful);
        totalScore.addVariable("c", moneyProblems);
        totalScore.addVariable("d", relationshipProblems);
        totalScore.addVariable("e", sexProblems);
        totalScore.addVariable("f", familyProblems);
        totalScore.addVariable("g", lonelyProblems);
        totalScore.addVariable("h", enjoyTime);
        totalScore.setFormula("a+b+c+d+e+f+g+h");


		return doc;

	}
}
