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


package org.psygrid.command;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class HistoryOfSuicideV2 extends AssessmentForm {

    public static Document createDocument(Factory factory){

    	Document doc = factory.createDocument("History of Suicide V2", "History of Suicide, Self harm and Harm to others (V2)");
    	createDocumentStatuses(factory, doc);

        Section introSec = factory.createSection("Introduction Section", "Introduction");
        doc.addSection(introSec);
        SectionOccurrence introSecOcc = factory.createSectionOccurrence("Introduction Section Occ");
        introSec.addOccurrence(introSecOcc);

        NarrativeEntry intro1 = factory.createNarrativeEntry("Intro1",
        		"The History of Suicide, Self harm and Harm to others is part of the Command Assessments and will be completed by the RAs/CSOs at " +
        		"Baseline only. It will be used to collect information about suicide attempts, incidents of deliberate self-harm and " +
        		"harm to others that occurred in the past two years.");
        doc.addEntry(intro1);
        intro1.setSection(introSec);

        NarrativeEntry intro2 = factory.createNarrativeEntry("Intro2",
        		"The primary source of information will be the medical notes. However, the information recorded in the form will need " +
        		"to be confirmed and clarified by the client by asking them the relevant questions in the structured form below.");
        doc.addEntry(intro2);
        intro2.setSection(introSec);


        NarrativeEntry intro3 = factory.createNarrativeEntry("Intro3",
        		"In case of discrepancies between the medical notes and the clients’ version (e.g. incidents of self-harm in the medical " +
        		"records which client denies): a) request for more information and clarification from another source (e.g. care " +
        		"coordinator) and if there is still ambiguity b) refer the case to the Trial Manager who will discuss it with the coordinating " +
        		"team and will make an independent judgment.");
        doc.addEntry(intro3);
        intro3.setSection(introSec);


        Section mainSec = factory.createSection("Suicide Attempts Section", "Suicide Attempts");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Suicide Attempts Section Occ");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry pastTwoYears = factory.createNarrativeEntry("Past Two Years",
        		"Past Two Years");
        doc.addEntry(pastTwoYears);
        pastTwoYears.setSection(mainSec);
        pastTwoYears.setStyle(NarrativeStyle.HEADER);

        OptionEntry q1 = factory.createOptionEntry("Have you ever attempted to kill yourself?", "Have you ever attempted to kill yourself?");
        q1.setSection(mainSec);
        doc.addEntry(q1);
        createOptions(factory, q1, new String[]{"Yes", "No"}, new int[]{1,0});
        Option q1Yes = q1.getOption(0);

        OptionEntry q2 = factory.createOptionEntry("If YES: In the past 2 years?", "If YES: In the past 2 years?");
        q2.setEntryStatus(EntryStatus.DISABLED);
        q2.setSection(mainSec);
        doc.addEntry(q2);
        createOptions(factory, q2, new String[]{"Once?", "Twice?", ">Twice?"}, new int[]{1,2,3});
        createOptionDependent(factory, q1Yes, q2);

        Section selfHarmSec = factory.createSection("Self Harm Section", "Deliberate Self-Harm");
        doc.addSection(selfHarmSec);
        SectionOccurrence selfHarmSecOcc = factory.createSectionOccurrence("Self Harm Section Occ");
        selfHarmSec.addOccurrence(selfHarmSecOcc);

        NarrativeEntry pastTwoYears2 = factory.createNarrativeEntry("Past Two Years", "Past Two Years");
        doc.addEntry(pastTwoYears2);
        pastTwoYears2.setSection(selfHarmSec);
        pastTwoYears2.setStyle(NarrativeStyle.HEADER);

        OptionEntry q3 = factory.createOptionEntry("Intentional Self-Harm", "Have you intentionally harmed or injured yourself (without intending to kill yourself) in the past 2 years?");
        q3.setSection(selfHarmSec);
        doc.addEntry(q3);
        createOptions(factory, q3, new String[]{"Yes", "No"}, new int[]{1,0});
        Option q3Yes = q3.getOption(0);

        OptionEntry q4 = factory.createOptionEntry("If YES", "If YES:");
        q4.setEntryStatus(EntryStatus.DISABLED);
        q4.setSection(selfHarmSec);
        doc.addEntry(q4);
        createOptions(factory, q4, new String[]{"Once?", "Twice?", ">Twice?"}, new int[]{1,2,3});
        createOptionDependent(factory, q3Yes, q4);

        CompositeEntry q5 = factory.createComposite("Methods harm self", "Methods used to harm/injure self");
        q5.setEntryStatus(EntryStatus.DISABLED);
        q5.setSection(selfHarmSec);
        doc.addEntry(q5);
        createOptionDependent(factory, q3Yes, q5);

        OptionEntry q5a = factory.createOptionEntry("Method", "Method");
        q5a.setSection(selfHarmSec);
        q5.addEntry(q5a);
        createOptions(factory, q5a, new String[]{"Overdose", "Poisoning", "Burning", "Scratching/cutting", "Stabbing", "Jumping", "Other"}, new int[]{1,2,3,4,5,6,7});
        Option q5Other = q5a.getOption(6);
        q5Other.setTextEntryAllowed(true);


        Section harmToOthersSec = factory.createSection("Harm to Others Section", "Harm to Others");
        doc.addSection(harmToOthersSec);
        SectionOccurrence harmToOthersSecOcc = factory.createSectionOccurrence("Harm to Others Section Occ");
        harmToOthersSec.addOccurrence(harmToOthersSecOcc);

        NarrativeEntry pastTwoYears3 = factory.createNarrativeEntry("Past Two Years", "Past Two Years");
        doc.addEntry(pastTwoYears3);
        pastTwoYears3.setSection(harmToOthersSec);
        pastTwoYears3.setStyle(NarrativeStyle.HEADER);

        OptionEntry q6 = factory.createOptionEntry("Intentional harm", "Have you intentionally tried to harm or injure others in the past 2 years?");
        q6.setSection(harmToOthersSec);
        doc.addEntry(q6);
        createOptions(factory, q6, new String[]{"Yes", "No"}, new int[]{1,0});
        Option q6Yes = q6.getOption(0);

        OptionEntry q7 = factory.createOptionEntry("If YES", "If YES:");
        q7.setEntryStatus(EntryStatus.DISABLED);
        q7.setSection(harmToOthersSec);
        doc.addEntry(q7);
        createOptions(factory, q7, new String[]{"Once?", "Twice?", ">Twice?"}, new int[]{1,2,3});
        createOptionDependent(factory, q6Yes, q7);

        CompositeEntry q8 = factory.createComposite("Methods harm others", "Methods used to harm/injure others");
        q8.setEntryStatus(EntryStatus.DISABLED);
        q8.setSection(harmToOthersSec);
        doc.addEntry(q8);
        createOptionDependent(factory, q6Yes, q8);

        OptionEntry q8a = factory.createOptionEntry("Method", "Method");
        q8a.setSection(harmToOthersSec);
        q8.addEntry(q8a);
        createOptions(factory, q8a, new String[]{"Hitting", "Stabbing", "Shooting", "Strangling", "Poisoning", "Other"}, new int[]{1,2,3,4,5,6});
        Option q8Other = q8a.getOption(5);
        q8Other.setTextEntryAllowed(true);

        return doc;
    }
}
