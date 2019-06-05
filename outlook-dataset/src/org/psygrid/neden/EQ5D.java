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

package org.psygrid.neden;

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class EQ5D extends AssessmentForm {

    public static Document createDocument(Factory factory){

        ValidationRule healthThermRule = ValidationRulesWrapper.instance().getRule("Health thermometer validation rule");

        Document eq5d = factory.createDocument("EQ5D", "Health Questionnaire - EQ-5D Record");

        createDocumentStatuses(factory, eq5d);

        Section mainSec = factory.createSection("Main", "Main");
        eq5d.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main section occurrence");
        mainSec.addOccurrence(mainOcc);

        NarrativeEntry instructions1 = factory.createNarrativeEntry(
                "Instructions 1",
                "Instructions: Enter code for each of the groups below:");
        eq5d.addEntry(instructions1);
        instructions1.setSection(mainSec);

        OptionEntry mobility = factory.createOptionEntry("Mobility", "Mobility");
        eq5d.addEntry(mobility);
        mobility.setSection(mainSec);
        Option mob1 = factory.createOption("I have no problem in walking about", 1);
        Option mob2 = factory.createOption("I have some problems in walking about", 2);
        Option mob3 = factory.createOption("I am confined to bed", 3);
        mobility.addOption(mob1);
        mobility.addOption(mob2);
        mobility.addOption(mob3);

        OptionEntry selfCare = factory.createOptionEntry("Self-care", "Self-care");
        eq5d.addEntry(selfCare);
        selfCare.setSection(mainSec);
        Option sc1 = factory.createOption("I have no problems with self-care", 1);
        Option sc2 = factory.createOption("I have some problems washing or dressing myself", 2);
        Option sc3 = factory.createOption("I am unable to wash or dress myself", 3);
        selfCare.addOption(sc1);
        selfCare.addOption(sc2);
        selfCare.addOption(sc3);

        OptionEntry usualActivities = factory.createOptionEntry("Usual Activities", "Usual Activities (e.g. work, study, housework, family or leisure activities)");
        eq5d.addEntry(usualActivities);
        usualActivities.setSection(mainSec);
        Option ua1 = factory.createOption("I have no problem with performing my usual activities", 1);
        Option ua2 = factory.createOption("I have some problems with performing my usual activities", 2);
        Option ua3 = factory.createOption("I am unable to perform my usual activities", 3);
        usualActivities.addOption(ua1);
        usualActivities.addOption(ua2);
        usualActivities.addOption(ua3);

        OptionEntry painDiscomfort = factory.createOptionEntry("Pain/Discomfort", "Pain/Discomfort");
        eq5d.addEntry(painDiscomfort);
        painDiscomfort.setSection(mainSec);
        Option pd1 = factory.createOption("I have no pain or discomfort", 1);
        Option pd2 = factory.createOption("I have moderate pain or discomfort", 2);
        Option pd3 = factory.createOption("I have extreme pain or discomfort", 3);
        painDiscomfort.addOption(pd1);
        painDiscomfort.addOption(pd2);
        painDiscomfort.addOption(pd3);

        OptionEntry anxietyDepression = factory.createOptionEntry("Anxiety/Depression", "Anxiety/Depression");
        eq5d.addEntry(anxietyDepression);
        anxietyDepression.setSection(mainSec);
        Option ad1 = factory.createOption("I am not anxious or depressed", 1);
        Option ad2 = factory.createOption("I am moderately anxious or depressed", 2);
        Option ad3 = factory.createOption("I am extremely anxious or depressed", 3);
        anxietyDepression.addOption(ad1);
        anxietyDepression.addOption(ad2);
        anxietyDepression.addOption(ad3);

        NarrativeEntry htInstructions = factory.createNarrativeEntry("Health Thermometer instructions");
        eq5d.addEntry(htInstructions);
        htInstructions.setSection(mainSec);
        htInstructions.setDisplayText("To help people say how good or bad a health state is, we have drawn a scale "+
                                      "(rather like a thermometer) on which the best state you can imagine is marked 100 "+
                                      "and the worst state you can imagine is marked 0. We would like you to indicate on "+
                                      "this scale how good or bad your own health is today, in your opinion. "+
                                      "Please do this by drawing a line from the box below to whichever point on the scale "+
                                      "indicates how good or bad your health state is today.");

        NumericEntry healthThermometer = factory.createNumericEntry("Health Thermometer", "Health Thermometer (please enter the point indicated on the Health Thermometer)");
        eq5d.addEntry(healthThermometer);
        healthThermometer.setSection(mainSec);
        healthThermometer.addValidationRule(healthThermRule);

        return eq5d;
    }
}
