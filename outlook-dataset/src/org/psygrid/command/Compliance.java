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
public class Compliance extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "Compliance",
                "Compliance Behaviour Rating Form");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Sec Occ");
        mainSec.addOccurrence(mainSecOcc);

        CompositeEntry hallucinations = factory.createComposite("Command Hallucinations", "Command Hallucinations");
        doc.addEntry(hallucinations);
        hallucinations.setSection(mainSec);

        TextEntry hList = factory.createTextEntry("Hallucinations", "List of Command Hallucinations");
        hList.setDescription("Record examples from the CAV interview");
        hallucinations.addEntry(hList);
        hList.setSection(mainSec);

        TextEntry hAB = factory.createTextEntry("Associated Behaviour", "Associated Behaviour");
        hallucinations.addEntry(hAB);
        hAB.setSection(mainSec);

        CompositeEntry otherContent = factory.createComposite("Voices with other content", "Voices with other content");
        doc.addEntry(otherContent);
        otherContent.setSection(mainSec);

        TextEntry content = factory.createTextEntry("Voices with other content: critical, hostility, advice, commentary",
        		"Voices with other content: critical, hostility, advice, commentary");
        otherContent.addEntry(content);
        content.setSection(mainSec);
        content.setDescription("record examples from the CAV interview");

        OptionEntry intAsCom = factory.createOptionEntry("Voices possibly interpreted as commands", "Voices possibly interpreted as commands");
        intAsCom.setDescription("based on client's interpretation");
        otherContent.addEntry(intAsCom);
        intAsCom.setSection(mainSec);
        createOptions(factory, intAsCom, new String[]{"Yes", "No"}, new int[]{1, 0});

        TextEntry conAB = factory.createTextEntry("Associated Behaviour", "Associated Behaviour");
        otherContent.addEntry(conAB);
        conAB.setSection(mainSec);


        CompositeEntry voiceContent = factory.createComposite("Target voices and behaviour", "");
        doc.addEntry(voiceContent);
        voiceContent.setSection(mainSec);

        TextEntry targetVoice = factory.createTextEntry("Target voice", "Target voice");
        targetVoice.setDescription("from the CAV interview");
        voiceContent.addEntry(targetVoice);
        targetVoice.setSection(mainSec);

        TextEntry tvAssocBeh = factory.createTextEntry("Associated Behaviour", "Associated Behaviour");
        voiceContent.addEntry(tvAssocBeh);
        tvAssocBeh.setSection(mainSec);

        LongTextEntry history = factory.createLongTextEntry("History of harmful compliance/acting on voices", "History of harmful compliance/acting on voices");
        doc.addEntry(history);
        history.setSection(mainSec);

        LongTextEntry addInfoCarer = factory.createLongTextEntry("Additional information from carer", "Additional information from carer");
        doc.addEntry(addInfoCarer);
        addInfoCarer.setSection(mainSec);

        LongTextEntry addInfoPro = factory.createLongTextEntry("Additional information from professional", "Additional information from professional");
        doc.addEntry(addInfoPro);
        addInfoPro.setSection(mainSec);

        LongTextEntry vignette = factory.createLongTextEntry("Vignette", "Vignette");
        doc.addEntry(vignette);
        vignette.setSection(mainSec);

        OptionEntry cbr = factory.createOptionEntry("Compliance Behaviour Rating", "Compliance Behaviour Rating");
        doc.addEntry(cbr);
        cbr.setSection(mainSec);
        Option level1 = factory.createOption("Level 1", "Level 1: Neither appeasement or compliant");
        cbr.addOption(level1);
        Option level2 = factory.createOption("Level 2", "Level 2: Symbolic appeasement, i.e. compliant with innocuous and/or harmless Commands");
        cbr.addOption(level2);
        Option level3 = factory.createOption("Level 3", "Level 3: Appeasement. Preparatory acts or gestures");
        cbr.addOption(level3);
        Option level4 = factory.createOption("Level 4", "Level 4: Partial compliance with at least one severe command");
        cbr.addOption(level4);
        Option level5 = factory.createOption("Level 5", "Level 5: Full compliance with at least one severe command");
        cbr.addOption(level5);

        return doc;
    }
}
