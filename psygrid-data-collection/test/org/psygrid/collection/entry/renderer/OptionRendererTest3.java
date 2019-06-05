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
package org.psygrid.collection.entry.renderer;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class OptionRendererTest3 extends AbstractEntryTestCase {
    private OptionEntry optionEntry;
    private Option skilledOption;
    private Option unskilledOption;
    private Option unemployedOption;
    private Section initialSection;
    private Section section;
    private Factory factory;
    private Application application;
    private DocumentOccurrence docOccurrence;
    private DocumentInstance docInstance;
    private Document document;
    private Record record;
    
    protected void init() throws Exception {
        application = createApplication();
        record = getRecord();
        DataSet dataSet = record.getDataSet();
        docOccurrence = getDocumentOccurrence();
        document = docOccurrence.getDocument();
        
        dataSet.addDocument(document);
        factory = getFactory();
        SectionOccurrence initialSectionOcc = getSectionOccurrence();
        initialSection = initialSectionOcc.getSection();
        document.addSection(initialSection);
        section = factory.createSection("Test section");
        section.addOccurrence(factory.createSectionOccurrence("Test section occ 1"));
        section.addOccurrence(factory.createSectionOccurrence("Test section occ 2"));
        document.addSection(section);
        docInstance = document.generateInstance(docOccurrence);
        record.addDocumentInstance(docInstance);
        optionEntry = factory.createOptionEntry("Job", "Job");
        skilledOption = factory.createOption("Skilled");
        unskilledOption = factory.createOption("Unskilled");
        unemployedOption = factory.createOption("Unemployed");
        optionEntry.addOption(skilledOption);
        optionEntry.addOption(unskilledOption);
        optionEntry.addOption(unemployedOption);
        document.addEntry(optionEntry);
        optionEntry.setSection(initialSection);
    }

    public void testEnableConditionalSectionOccurrence() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                SectionOccurrence disabledSecOcc = section.getOccurrence(1);
                disabledSecOcc.setEntryStatus(EntryStatus.DISABLED);
                
                OptionEntry conditionalEntry = factory
                        .createOptionEntry("Enable second sectionOcc",
                                "Enable second section occurrence?");
                Option yesOption = factory.createOption("Yes");
                Option noOption = factory.createOption("No");
                conditionalEntry.addOption(yesOption);
                conditionalEntry.addOption(noOption);

                document.addEntry(conditionalEntry);
                conditionalEntry.setSection(initialSection);
                TextEntry postcodeEntry = factory.createTextEntry("Postcode",
                        "Postcode");
                
                OptionDependent secDependent = factory
                        .createOptionDependent();
                secDependent.setDependentSecOcc(disabledSecOcc);
                secDependent.setEntryStatus(EntryStatus.MANDATORY);
                yesOption.addOptionDependent(secDependent);

                document.addEntry(postcodeEntry);
                postcodeEntry.setSection(section);
                NumericEntry ageEntry = factory.createNumericEntry("Age",
                        "Age");
                ageEntry.setEntryStatus(EntryStatus.DISABLED);

                document.addEntry(ageEntry);
                ageEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                String postcodeIncorrectMsg = "Postcode field status incorrect:";
                String ageIncorrectMsg = "Age field incorrect: ";

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                setOption(rendererHandler, optionEntry, 0, skilledOption);
                setOption(rendererHandler, conditionalEntry, 0, noOption);
                
                application.getModel().nextSection();
                
                // First section occurrence of section
                rendererHandler = application.getModel().getCurrentRendererHandler();
                
                EntryComponent postcodeField = getFieldAsEntryComponent(rendererHandler,
                        postcodeEntry, 0);
                postcodeField.getTextComponent().setText("sometext");
                EntryComponent ageField = getFieldAsEntryComponent(rendererHandler, ageEntry, 0);
                
                assertEquals(postcodeIncorrectMsg, true, postcodeField
                        .isEnabled());
                assertEquals(ageIncorrectMsg, false, ageField.isEnabled());

                application.getModel().nextSection();
                
                // Second section occurrence of section
                rendererHandler = application.getModel().getCurrentRendererHandler();
                
                postcodeField = getFieldAsEntryComponent(rendererHandler,
                        postcodeEntry, 0);
                ageField = getFieldAsEntryComponent(rendererHandler, ageEntry, 0);

                assertEquals(postcodeIncorrectMsg, false, postcodeField
                        .isEnabled());
                assertEquals(ageIncorrectMsg, false, ageField.isEnabled());
                
                application.getModel().previousSection();
                application.getModel().previousSection();
                
                // Back to initialSection
                rendererHandler = application.getModel().getCurrentRendererHandler();
                setOption(rendererHandler, conditionalEntry, 0, yesOption);

                application.getModel().nextSection();
                
                // First section occurrence of section
                rendererHandler = application.getModel().getCurrentRendererHandler();
                
                postcodeField = getFieldAsEntryComponent(rendererHandler,
                        postcodeEntry, 0);
                ageField = getFieldAsEntryComponent(rendererHandler, ageEntry, 0);

                assertEquals(postcodeIncorrectMsg, true, postcodeField
                        .isEnabled());
                assertEquals(ageIncorrectMsg, false, ageField.isEnabled());

                application.getModel().nextSection();
                
                // Second section occurrence of section
                rendererHandler = application.getModel().getCurrentRendererHandler();
                
                postcodeField = getFieldAsEntryComponent(rendererHandler,
                        postcodeEntry, 0);
                ageField = getFieldAsEntryComponent(rendererHandler, ageEntry, 0);

                assertEquals(postcodeIncorrectMsg, true, postcodeField
                        .isEnabled());
                assertEquals(ageIncorrectMsg, false, ageField.isEnabled());
                
                application.getModel().previousSection();
                application.getModel().previousSection();
                
                //Back to initialSection
                rendererHandler = application.getModel().getCurrentRendererHandler();
                setOption(rendererHandler, conditionalEntry, 0, noOption);

                application.getModel().nextSection();
                
                // First section occurrence of section
                rendererHandler = application.getModel().getCurrentRendererHandler();
                
                postcodeField = getFieldAsEntryComponent(rendererHandler,
                        postcodeEntry, 0);
                ageField = getFieldAsEntryComponent(rendererHandler, ageEntry, 0);

                assertEquals(postcodeIncorrectMsg, true, postcodeField
                        .isEnabled());
                assertEquals(ageIncorrectMsg, false, ageField.isEnabled());

                application.getModel().nextSection();
                
                // Second section occurrence of section
                rendererHandler = application.getModel().getCurrentRendererHandler();
                
                postcodeField = getFieldAsEntryComponent(rendererHandler,
                        postcodeEntry, 0);
                ageField = getFieldAsEntryComponent(rendererHandler, ageEntry, 0);

                assertEquals(postcodeIncorrectMsg, false, postcodeField
                        .isEnabled());
                assertEquals(ageIncorrectMsg, false, ageField.isEnabled());
            }
        });
    }
}
