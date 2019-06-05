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

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.outlook.AssessmentForm;

public class MoriskyScale extends AssessmentForm {

	public static Document createDocument(Factory factory){

		Document doc = factory.createDocument("Morisky Scale",
                "Morisky Scale");

		createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main section occurrence");
        mainSec.addOccurrence(mainOcc);

        OptionEntry forgetMedication = factory.createOptionEntry("Forget medication", "Do you ever forget to take your medication?");
        doc.addEntry(forgetMedication);
        forgetMedication.setLabel("1");
        forgetMedication.setOptionCodesDisplayed(true);
        forgetMedication.setSection(mainSec);
        forgetMedication.addOption(factory.createOption("Yes", 1));
        forgetMedication.addOption(factory.createOption("No", 0));

        OptionEntry carelessMedication = factory.createOptionEntry("Careless about medication",
                "Are you careless at times about taking your medication?");
        doc.addEntry(carelessMedication);
        carelessMedication.setLabel("2");
        carelessMedication.setOptionCodesDisplayed(true);
        carelessMedication.setSection(mainSec);
        carelessMedication.addOption(factory.createOption("Yes", 1));
        carelessMedication.addOption(factory.createOption("No", 0));

        OptionEntry stopWhenBetterMedication = factory.createOptionEntry("Stop taking when better",
                "When you feel better, do you stop taking your medications?");
		doc.addEntry(stopWhenBetterMedication);
		stopWhenBetterMedication.setLabel("3");
		stopWhenBetterMedication.setOptionCodesDisplayed(true);
		stopWhenBetterMedication.setSection(mainSec);
		stopWhenBetterMedication.addOption(factory.createOption("Yes", 1));
		stopWhenBetterMedication.addOption(factory.createOption("No", 0));

        OptionEntry stopWhenWorseMedication = factory.createOptionEntry("Stop taking when worse",
                "Sometimes if you feel worse when you take your medication, do you stop taking it?");
        doc.addEntry(stopWhenWorseMedication);
        stopWhenWorseMedication.setLabel("4");
        stopWhenWorseMedication.setOptionCodesDisplayed(true);
        stopWhenWorseMedication.setSection(mainSec);
        stopWhenWorseMedication.addOption(factory.createOption("Yes", 1));
        stopWhenWorseMedication.addOption(factory.createOption("No", 0));

        return doc;


	}

}