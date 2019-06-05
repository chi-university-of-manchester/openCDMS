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

public class QualityOfLife extends AssessmentForm {

	public static Document createDocument(Factory factory){

        Document doc = factory.createDocument("Quality of Life",
                "Quality of Life");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main section");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence(
                "Main Section Occurrence");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry instructions1 = factory.createNarrativeEntry(
                "Instructions",
                "Please select the option that best describes your own state of health today");
        doc.addEntry(instructions1);
        instructions1.setSection(mainSec);

        OptionEntry mobility = factory.createOptionEntry("Mobility", "Mobility");
        doc.addEntry(mobility);
        mobility.setSection(mainSec);
        Option mob1 = factory.createOption("I have no problems walking about", 1);
        Option mob2 = factory.createOption("I have some problems in walking about", 2);
        Option mob3 = factory.createOption("I am confined to bed", 3);
        mobility.addOption(mob1);
        mobility.addOption(mob2);
        mobility.addOption(mob3);

        OptionEntry selfCare = factory.createOptionEntry("Self-care", "Self-care");
        doc.addEntry(selfCare);
        selfCare.setSection(mainSec);
        Option selfCare1 = factory.createOption("I have no problems with self-care", 1);
        Option selfCare2 = factory.createOption("I have some problems washing or dressing myself", 2);
        Option selfCare3 = factory.createOption("I am unable to wash or dress myself", 3);
        selfCare.addOption(selfCare1);
        selfCare.addOption(selfCare2);
        selfCare.addOption(selfCare3);

        OptionEntry usualActivities = factory.createOptionEntry("Usual activities", "Usual activites(e.g. housework, studies, leisure activities)");
        doc.addEntry(usualActivities);
        usualActivities.setSection(mainSec);
        Option usualActivities1 = factory.createOption("I have no problems with performing my usual activities", 1);
        Option usualActivities2 = factory.createOption("I have some problems performing my usual activities", 2);
        Option usualActivities3 = factory.createOption("I am unable to perform my usual activities", 3);
        usualActivities.addOption(usualActivities1);
        usualActivities.addOption(usualActivities2);
        usualActivities.addOption(usualActivities3);

        OptionEntry painDiscomfort = factory.createOptionEntry("Pain/discomfort", "Pain/ discomfort");
        doc.addEntry(painDiscomfort);
        painDiscomfort.setSection(mainSec);
        Option pain1 = factory.createOption("I have no pain or discomfort", 1);
        Option pain2 = factory.createOption("I have some pain or discomfort", 2);
        Option pain3 = factory.createOption("I have extreme pain or discomfort", 3);
        painDiscomfort.addOption(pain1);
        painDiscomfort.addOption(pain2);
        painDiscomfort.addOption(pain3);

        OptionEntry anxious = factory.createOptionEntry("Anxiety/depression", "Anxiety/depression");
        doc.addEntry(anxious);
        anxious.setSection(mainSec);
        Option anxious1 = factory.createOption("I am not anxious or depressed", 1);
        Option anxious2 = factory.createOption("I am moderately not anxious or depressed", 2);
        Option anxious3 = factory.createOption("I am extremely not anxious or depressed", 3);
        anxious.addOption(anxious1);
        anxious.addOption(anxious2);
        anxious.addOption(anxious3);

        DerivedEntry totalScore = factory.createDerivedEntry(
                "Total Score", "Total Score");
        doc.addEntry(totalScore);
        totalScore.setSection(mainSec);
        totalScore.addVariable("a", mobility);
        totalScore.addVariable("b", selfCare);
        totalScore.addVariable("c", usualActivities);
        totalScore.addVariable("d", painDiscomfort);
        totalScore.addVariable("e", anxious);
        totalScore.setFormula("a+b+c+d+e");

        return doc;

	}

}