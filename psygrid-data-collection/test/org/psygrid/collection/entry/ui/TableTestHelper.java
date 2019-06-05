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
package org.psygrid.collection.entry.ui;

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class TableTestHelper {

    public static CompositeEntry createVariableTable(Factory factory, Section section) {
        CompositeEntry currentMedication = factory.createComposite("Current Medication",
                "Current Medication");

        OptionEntry colorOption = factory.createOptionEntry("Favourite colour", "Favourite colour");
        currentMedication.addEntry(colorOption);
        colorOption.setSection(section);
        
        Option blueOption = factory.createOption("blue");
        colorOption.addOption(blueOption);
        Option redOption = factory.createOption("red");
        colorOption.addOption(redOption);
        Option yellowOption = factory.createOption("yellow");
        colorOption.addOption(yellowOption);
        
        TextEntry medicationC = factory.createTextEntry("Medication",
                "Medication");
        currentMedication.addEntry(medicationC);
        medicationC.setSection(section);
        
        NumericEntry freqC = factory.createNumericEntry("Freq", "Freq");
        currentMedication.addEntry(freqC);
        freqC.setSection(section);
        
        return currentMedication;
    }
    
    public static CompositeEntry createFixedTable(Factory factory, Section section) {
        CompositeEntry qualifications = factory.createComposite(
                "Qualifications", "Qualifications");
        qualifications.addRowLabel("Degree level");
        qualifications.addRowLabel("Diploma in higher education");
        qualifications.addRowLabel("HNC/HND");
        qualifications.addRowLabel("ONC/OND");
        qualifications.setSection(section);
        
        TextEntry qualificationsText = factory.createTextEntry("Qualification", "Qualification");
        qualifications.addEntry(qualificationsText);
        qualificationsText.setSection(section);
        
        NumericEntry qualificationsNumber = factory.createNumericEntry("Qualification number", "Number");
        qualifications.addEntry(qualificationsNumber);
        qualificationsNumber.setSection(section);
        
        return qualifications;
    }
    
    public static List<Map<JComponent,Boolean>> getComps(EntryTable entryTable){
        return entryTable.getComps();
    }
}
