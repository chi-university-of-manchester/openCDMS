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

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class RelapseAndRecovery extends AssessmentForm{

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "Relapse And Recovery",
                "Relapse And Recovery");

        createDocumentStatuses(factory, doc);

        doc.setLongRunning(true);

        Section mainSec = factory.createSection("Main", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainOcc = factory.createSectionOccurrence("Main section occurrence");
        mainSec.addOccurrence(mainOcc);

        CompositeEntry relapseRecovery = factory.createComposite("Relapse and recovery", "Relapse and recovery");
        relapseRecovery.setSection(mainSec);
        doc.addEntry(relapseRecovery);
        relapseRecovery.addRowLabel("1");
        relapseRecovery.addRowLabel("2");
        relapseRecovery.addRowLabel("3");
        relapseRecovery.addRowLabel("4");
        relapseRecovery.addRowLabel("5");
        relapseRecovery.addRowLabel("6");
        relapseRecovery.addRowLabel("7");
        relapseRecovery.addRowLabel("8");
        relapseRecovery.addRowLabel("9");
        relapseRecovery.addRowLabel("10");
        relapseRecovery.addRowLabel("11");
        relapseRecovery.addRowLabel("12");

        TextEntry month = factory.createTextEntry("Month", "Month");
        relapseRecovery.addEntry(month);
        month.setSection(mainSec);

        DateEntry from = factory.createDateEntry("FromDate", "From");
        relapseRecovery.addEntry(from);
        from.setSection(mainSec);

        DateEntry to = factory.createDateEntry("ToDate", "To");
        relapseRecovery.addEntry(to);
        to.setSection(mainSec);

        OptionEntry recovery = factory.createOptionEntry("Recovery", "Recovery");
        relapseRecovery.addEntry(recovery);
        recovery.setSection(mainSec);
        recovery.setDescription("IE = Inadequate Evidence");
        Option recoveryFull = factory.createOption("Recovery Full", "Full");
        recovery.addOption(recoveryFull);
        Option recoveryPartial = factory.createOption("Recovery Partial", "Partial");
        recovery.addOption(recoveryPartial);
        Option recoveryNon = factory.createOption("Recovery Non", "Non");
        recovery.addOption(recoveryNon);
        Option recoveryIE = factory.createOption("Recovery IE", "IE");
        recovery.addOption(recoveryIE);

        OptionEntry relapse = factory.createOptionEntry("Relapse", "Relapse");
        relapseRecovery.addEntry(relapse);
        relapse.setSection(mainSec);
        relapse.setDescription("IE = Inadequate Evidence");
        Option relapseType1 = factory.createOption("Relapse Type 1", "Type 1 (true)");
        relapse.addOption(relapseType1);
        Option relapseType2 = factory.createOption("Relapse Type 2", "Type 2 (exacerbation)");
        relapse.addOption(relapseType2);
        Option relapseNon = factory.createOption("Relapse Non", "Non");
        relapse.addOption(relapseNon);
        Option relapseIE = factory.createOption("Relapse IE", "IE");
        relapse.addOption(relapseIE);

        return doc;
    }
}
