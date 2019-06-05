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

import java.util.List;

import javax.swing.JComponent;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.ui.EditableRadioButton;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class OptionRendererTest extends AbstractEntryTestCase  {
    private OptionEntry optionEntry;
    private Option skilledOption;
    private Option unskilledOption;
    private Option unemployedOption;
    private Section section;
    private Factory factory;
    private Application application;
    private DocumentOccurrence docOccurrence;
    private DocumentInstance docInstance;
    private Document document;
    private SectionOccurrence sectionOcc;
    private Record record;
    
    protected void init() throws Exception {
        application = createApplication();
        record = getRecord();
        DataSet dataSet = record.getDataSet();
        docOccurrence = getDocumentOccurrence();
        document = docOccurrence.getDocument();
        
        dataSet.addDocument(document);
        factory = getFactory();
        sectionOcc = getSectionOccurrence();
        section = sectionOcc.getSection();
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
        optionEntry.setSection(section);
    }
    
    public void testGetRendererSPI() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                RendererHandler rendererHandler = getRendererHandler(sectionOcc);
                RendererSPI rspi = rendererHandler.getRendererSPI(optionEntry,
                        null);
                assertTrue(rspi instanceof OptionRendererSPI);
            }
        });
    }
    
    public void testGetRenderer() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                BuilderHandler builderHandler = getBuilderHandler(docInstance,
                        sectionOcc);
                RendererHandler rendererHandler = builderHandler
                        .getCurrentRendererHandler();
                BasicRenderer<?> renderer = (BasicRenderer<?>) rendererHandler
                        .getRenderer(optionEntry, null);
                assertTrue(renderer.getField() == null);
                EntryLabel label = (EntryLabel) renderer.getLabel();
                assertEquals(optionEntry.getDisplayText(), label.getText());
                assertTrue(renderer.getPresModel() != null);
                int numComponents = rendererHandler.getStdCodes().size()
                        + optionEntry.numOptions() + NUM_STANDARD_RENDERER_COMPONENTS;
                assertEquals(numComponents, renderer.getComponents().size());
            }
        });
    }
    
    public void testEnableConditionalQuestions() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry conditionalEntry = factory.createOptionEntry("Has abode",
                        "Has abode. If yes, go to question 2, else go to question 3");
                Option yesOption = factory.createOption("Yes");
                Option noOption = factory.createOption("No");
                conditionalEntry.addOption(yesOption);
                conditionalEntry.addOption(noOption);
                
                document.addEntry(conditionalEntry);
                conditionalEntry.setSection(section);
                TextEntry postcodeEntry = factory.createTextEntry("Postcode", "Postcode");
                postcodeEntry.setEntryStatus(EntryStatus.DISABLED);
                OptionDependent postcodeDependent = factory.createOptionDependent();
                postcodeDependent.setDependentEntry(postcodeEntry);
                postcodeDependent.setEntryStatus(EntryStatus.MANDATORY);
                yesOption.addOptionDependent(postcodeDependent);
                
                document.addEntry(postcodeEntry);
                postcodeEntry.setSection(section);
                NumericEntry ageEntry = factory.createNumericEntry("Age", "Age");
                ageEntry.setEntryStatus(EntryStatus.DISABLED);
                
                OptionDependent ageDependent = factory.createOptionDependent();
                ageDependent.setDependentEntry(ageEntry);
                ageDependent.setEntryStatus(EntryStatus.MANDATORY);
                yesOption.addOptionDependent(ageDependent);
                noOption.addOptionDependent(ageDependent);
                
                document.addEntry(ageEntry);
                ageEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                String postcodeIncorrectMsg = "Postcode field status incorrect:";
                String ageIncorrectMsg = "Age field incorrect: ";
                
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                EntryComponent postcodeField = getFieldAsEntryComponent(rendererHandler, 
                        postcodeEntry, 0);
                JComponent postcodeLabel = getLabel(rendererHandler, 
                        postcodeEntry, 0);
                EntryComponent ageField = getFieldAsEntryComponent(rendererHandler, ageEntry, 0);
                JComponent ageLabel = getLabel(rendererHandler, ageEntry, 0);
                
                assertEquals(false, postcodeLabel.isEnabled());
                assertEquals(postcodeIncorrectMsg, false, postcodeField.isEnabled());
                assertEquals(false, ageLabel.isEnabled());
                assertEquals(ageIncorrectMsg, false, ageField.isEnabled());
                
                setOption(rendererHandler, conditionalEntry, 0, noOption);

                assertEquals(false, postcodeLabel.isEnabled());
                assertEquals(postcodeIncorrectMsg, false, postcodeField.isEnabled());
                assertEquals(true, ageLabel.isEnabled());
                assertEquals(ageIncorrectMsg, true, ageField.isEnabled());
                
                setOption(rendererHandler, conditionalEntry, 0, yesOption);

                assertEquals(true, postcodeLabel.isEnabled());
                assertEquals(postcodeIncorrectMsg, true, postcodeField.isEnabled());
                assertEquals(true, ageLabel.isEnabled());
                assertEquals(ageIncorrectMsg, true, ageField.isEnabled());
                
                setOption(rendererHandler, conditionalEntry, 0, noOption);

                assertEquals(false, postcodeLabel.isEnabled());
                assertEquals(postcodeIncorrectMsg, false, postcodeField.isEnabled());
                assertEquals(true, ageLabel.isEnabled());
                assertEquals(ageIncorrectMsg, true, ageField.isEnabled());
            }
        });
    }
    
    public void testConditionalQuestionsWithPresetValue() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry condEntry = factory.createOptionEntry("Has abode",
                        "Has abode. If yes, go to question 2, else go to question 3");
                condEntry.setSection(section);
                Option yesOption = factory.createOption("Yes");
                Option noOption = factory.createOption("No");
                condEntry.addOption(yesOption);
                condEntry.addOption(noOption);
                IOptionValue condValue = condEntry.generateValue();
                BasicResponse condResponse = condEntry.generateInstance(sectionOcc);
                condValue.setValue(noOption);
                condResponse.setValue(condValue);

                document.addEntry(condEntry);
                docInstance.addResponse(condResponse);
                
                TextEntry postcodeEntry = factory.createTextEntry("Postcode", "Postcode");
                postcodeEntry.setEntryStatus(EntryStatus.DISABLED);
                postcodeEntry.setSection(section);
                
                BasicResponse postcodeResponse =
                    postcodeEntry.generateInstance(sectionOcc);
                ITextValue postcodeValue = postcodeEntry.generateValue();
                postcodeValue.setValue("somePostcode");
                postcodeResponse.setValue(postcodeValue);
                document.addEntry(postcodeEntry);
                docInstance.addResponse(postcodeResponse);
                
                OptionDependent postcodeDependent = factory.createOptionDependent();
                postcodeDependent.setDependentEntry(postcodeEntry);
                postcodeDependent.setEntryStatus(EntryStatus.MANDATORY);
                yesOption.addOptionDependent(postcodeDependent);
                
                NumericEntry ageEntry = factory.createNumericEntry("Age", "Age");
                ageEntry.setEntryStatus(EntryStatus.DISABLED);
                ageEntry.setSection(section);
                
                BasicResponse  ageResponse = ageEntry.generateInstance(sectionOcc);
                INumericValue ageValue = ageEntry.generateValue();
                ageValue.setValue(Double.valueOf(20));
                ageResponse.setValue(ageValue);
                document.addEntry(ageEntry);
                docInstance.addResponse(ageResponse);
                
                OptionDependent ageDependent = factory.createOptionDependent();
                ageDependent.setDependentEntry(ageEntry);
                ageDependent.setEntryStatus(EntryStatus.MANDATORY);
                yesOption.addOptionDependent(ageDependent);
                noOption.addOptionDependent(ageDependent);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                String postcodeIncorrectMsg = "Postcode field status incorrect:";
                String ageIncorrectMsg = "Age field incorrect: ";
                
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                EntryComponent postcodeField = getFieldAsEntryComponent(rendererHandler, 
                        postcodeEntry, 0);
                EntryComponent ageField = getFieldAsEntryComponent(rendererHandler, ageEntry, 0);
                
                assertEquals(postcodeIncorrectMsg, false, postcodeField.isEnabled());
                assertEquals(ageIncorrectMsg, true, ageField.isEnabled());
            }
        });
    }
    
    public void testConditionalQuestionsWithDefaultValue() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry condEntry = factory.createOptionEntry("Has abode",
                        "Has abode. If yes, go to question 2, else go to question 3");
                Option yesOption = factory.createOption("Yes");
                Option noOption = factory.createOption("No");
                condEntry.addOption(yesOption);
                condEntry.addOption(noOption);
                condEntry.setDefaultValue(yesOption);
                
                document.addEntry(condEntry);
                condEntry.setSection(section);
                TextEntry postcodeEntry = factory.createTextEntry("Postcode", "Postcode");
                postcodeEntry.setEntryStatus(EntryStatus.DISABLED);
                document.addEntry(postcodeEntry);
                postcodeEntry.setSection(section);
                                
                OptionDependent postcodeDependent = factory.createOptionDependent();
                postcodeDependent.setDependentEntry(postcodeEntry);
                postcodeDependent.setEntryStatus(EntryStatus.MANDATORY);
                yesOption.addOptionDependent(postcodeDependent);
                
                NumericEntry ageEntry = factory.createNumericEntry("Age", "Age");
                ageEntry.setEntryStatus(EntryStatus.DISABLED);
                
                OptionDependent ageDependent = factory.createOptionDependent();
                ageDependent.setDependentEntry(ageEntry);
                ageDependent.setEntryStatus(EntryStatus.MANDATORY);
                yesOption.addOptionDependent(ageDependent);
                noOption.addOptionDependent(ageDependent);
                
                document.addEntry(ageEntry);
                ageEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                String postcodeIncorrectMsg = "Postcode field status incorrect:";
                String ageIncorrectMsg = "Age field incorrect: ";
                
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                
                EntryComponent postcodeField = getFieldAsEntryComponent(rendererHandler, 
                        postcodeEntry, 0);
                EntryComponent ageField = getFieldAsEntryComponent(rendererHandler, ageEntry, 0);
                
                assertEquals(postcodeIncorrectMsg, true, postcodeField.isEnabled());
                assertEquals(ageIncorrectMsg, true, ageField.isEnabled());
            }
        });
    }
    
    public void testRestoreOptionEntriesFromResponse() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry condEntry1 = factory.createOptionEntry("Favourite colour");
                Option blueOption = factory.createOption("blue");
                Option redOption = factory.createOption("red");
                Option yellowOption = factory.createOption("yellow");
                
                condEntry1.addOption(blueOption);
                condEntry1.addOption(redOption);
                condEntry1.addOption(yellowOption);
                IOptionValue condValue1 = condEntry1.generateValue();
                condEntry1.setSection(section);
                BasicResponse  condResponse1 = condEntry1.generateInstance(sectionOcc);
                condValue1.setValue(redOption);
                condResponse1.setValue(condValue1);

                document.addEntry(condEntry1);
                docInstance.addResponse(condResponse1);
                
                OptionEntry condEntry2 = factory.createOptionEntry("Favourite car");
                Option fordOption = factory.createOption("ford");
                Option mustangOption = factory.createOption("mustang");
                Option mercOption = factory.createOption("merc");
                
                condEntry2.addOption(fordOption);
                condEntry2.addOption(mustangOption);
                condEntry2.addOption(mercOption);
                IOptionValue condValue2 = condEntry2.generateValue();
                condEntry2.setSection(section);
                BasicResponse  condResponse2 = condEntry2.generateInstance(sectionOcc);
                condValue2.setValue(mercOption);
                condResponse2.setValue(condValue2);

                document.addEntry(condEntry2);
                docInstance.addResponse(condResponse2);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                String condEntry1IncorrectMsg = 
                    "condEntry1 selected option incorrect:";
                String condEntry2IncorrectMsg = 
                    "condEntry2 selected option incorrect: ";
                
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                String cond1SelectedOption = getSelectedOption(rendererHandler, 
                        condEntry1, 0);
                assertEquals(condEntry1IncorrectMsg, redOption.getDisplayText(), 
                        cond1SelectedOption);
                String cond2SelectedOption = getSelectedOption(rendererHandler, 
                        condEntry2, 0);
                assertEquals(condEntry2IncorrectMsg, mercOption.getDisplayText(), 
                        cond2SelectedOption);
            }
        });
    }
    
    public static void main(String[] args) throws Exception {
        new OptionRendererTest().testOptionEntryDocumentStatusRejected();
    }
    
    public void testOptionEntryDocumentStatusRejected() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                setStatus(docInstance, DocumentStatus.toIStatus(document,
                        DocumentStatus.REJECTED));
                OptionEntry condEntry1 = factory.createOptionEntry("Favourite colour");
                Option blueOption = factory.createOption("blue");
                Option redOption = factory.createOption("red");
                Option yellowOption = factory.createOption("yellow");
                
                condEntry1.addOption(blueOption);
                condEntry1.addOption(redOption);
                condEntry1.addOption(yellowOption);
                IOptionValue condValue1 = condEntry1.generateValue();
                condEntry1.setSection(section);
                BasicResponse  condResponse1 = condEntry1.generateInstance(sectionOcc);
                //condResponse1.setStatus()
                condValue1.setValue(redOption);
                condResponse1.setValue(condValue1);

                document.addEntry(condEntry1);
                docInstance.addResponse(condResponse1);
                                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
            }
        });
    }
            
    public void testDefaultValueInOptionEntries() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry condEntry1 = factory.createOptionEntry("Favourite colour");
                Option blueOption = factory.createOption("blue");
                Option redOption = factory.createOption("red");
                Option yellowOption = factory.createOption("yellow");
                
                condEntry1.addOption(blueOption);
                condEntry1.addOption(redOption);
                condEntry1.addOption(yellowOption);
                
                condEntry1.setDefaultValue(blueOption);
                document.addEntry(condEntry1);
                condEntry1.setSection(section);
                
                OptionEntry condEntry2 = factory.createOptionEntry("Favourite car");
                Option fordOption = factory.createOption("ford");
                Option mustangOption = factory.createOption("mustang");
                Option mercOption = factory.createOption("merc");
                
                condEntry2.addOption(fordOption);
                condEntry2.addOption(mustangOption);
                condEntry2.addOption(mercOption);
                condEntry2.setDefaultValue(mustangOption);
                
                document.addEntry(condEntry2);
                condEntry2.setSection(section);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                String condEntry1IncorrectMsg = 
                    "condEntry1 selected option incorrect:";
                String condEntry2IncorrectMsg = 
                    "condEntry2 selected option incorrect: ";
                
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                String cond1SelectedOption = getSelectedOption(rendererHandler, 
                        condEntry1, 0);
                assertEquals(condEntry1IncorrectMsg, blueOption.getDisplayText(), 
                        cond1SelectedOption);
                String cond2SelectedOption = getSelectedOption(rendererHandler,
                        condEntry2, 0);
                assertEquals(condEntry2IncorrectMsg, mustangOption.getDisplayText(), 
                        cond2SelectedOption);
            }
        });
    }
    
    private String getSelectedOption(RendererHandler rendererHandler, 
            OptionEntry entry, int rowIndex) {
        List<JComponent> comps = 
            rendererHandler.getExistingRenderer(entry, rowIndex).getComponents();
        for (int i = 0, c = comps.size(); i < c; ++i) {
            JComponent comp = comps.get(i);
            if (comp instanceof EditableRadioButton) {
                EditableRadioButton radioButton = (EditableRadioButton) comp;
                if (radioButton.isSelected()) {
                    return radioButton.getText();
                }
            }
        }
        return null;
    }
}
