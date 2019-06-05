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


import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.ui.EntryTable;
import org.psygrid.collection.entry.ui.ValidationLabelsGroup;
import org.psygrid.data.model.IOptionValue;

import com.jgoodies.validation.view.ValidationResultViewFactory;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class CompositeRendererFixedTableTest extends AbstractEntryTestCase {

    private Section section;

    private Factory factory;

    private Application application;

    private DocumentOccurrence docOccurrence;

    private DocumentInstance docInstance;

    private Document document;

    private SectionOccurrence sectionOcc;
    
    private CompositeEntry compEntry;

    private CompositeResponse compResponse;
    
    private void init() throws Exception {
        application = createApplication();
        Record record = getRecord();
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
        compEntry = factory.createComposite("Main composite", "Test composite");
        document.addEntry(compEntry);
        compEntry.setSection(section);
    }

    public void testRestoreOptionAndText() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                compResponse = compEntry.generateInstance(sectionOcc);
                docInstance.addResponse(compResponse);
                compEntry.addRowLabel("3 months ago");
                compEntry.addRowLabel("6 months ago");
                
                TextEntry periodText = factory.createTextEntry("Period");
                compEntry.addEntry(periodText);
                periodText.setSection(section);
                
                OptionEntry jobOptionEntry = factory.createOptionEntry(
                        "Job Option", "Job");
                compEntry.addEntry(jobOptionEntry);
                jobOptionEntry.setSection(section);
                Option skilledOption = factory.createOption("Skilled");
                Option unskilledOption = factory.createOption("Unskilled");
                Option unemployedOption = factory.createOption("Unemployed");
                jobOptionEntry.addOption(skilledOption);
                jobOptionEntry.addOption(unskilledOption);
                jobOptionEntry.addOption(unemployedOption);
                Option otherJobOption = factory
                        .createOption("Other (specify)");
                jobOptionEntry.addOption(otherJobOption);
                otherJobOption.setTextEntryAllowed(true);

                OptionEntry religion = factory.createOptionEntry(
                        "Religion Option", "Religious Cultural Tradition");
                compEntry.addEntry(religion);
                religion.setSection(section);
                religion.addOption(factory.createOption("Muslim", 1));
                Option christianOption = factory.createOption("Christian", 2);
                religion.addOption(christianOption);
                religion.addOption(factory.createOption("Hindu", 3));
                Option religionOther = factory.createOption("Other (specify)",
                        4);
                religion.addOption(religionOther);
                religionOther.setTextEntryAllowed(true);

                CompositeRow compRow = compResponse.createCompositeRow();
                
                BasicResponse jobResponse = jobOptionEntry
                        .generateInstance(sectionOcc);
                compRow.addResponse(jobResponse);

                IOptionValue jobValue = jobOptionEntry.generateValue();
                jobValue.setValue(otherJobOption);
                jobResponse.setValue(jobValue);
                String textString = "Very skilled";
                jobValue.setTextValue(textString);

                BasicResponse religionResponse = religion
                        .generateInstance(sectionOcc);
                compRow.addResponse(religionResponse);
                IOptionValue religionValue = religion.generateValue();
                religionValue.setValue(christianOption);
                religionResponse.setValue(religionValue);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                assertEquals(7, rendererHandler.getNumExistingRenderers());

                String jobSelectedOption = getSelectedComboBoxOption(rendererHandler, 
                        jobOptionEntry, 0);
                assertEquals(otherJobOption.getDisplayText(), jobSelectedOption);
                
                String jobFieldText = getSelectedComboBoxOptionText(rendererHandler, 
                        jobOptionEntry, 0);
                assertEquals(textString, jobFieldText);

                String religionSelectedOption = getSelectedComboBoxOption(
                        rendererHandler, religion, 0);
                String christianTextExpected = christianOption.getCode() + ". "
                        + christianOption.getDisplayText();
                assertEquals(christianTextExpected, religionSelectedOption);
                
                String religionFieldText = getSelectedComboBoxOptionText(rendererHandler, 
                        religion, 0);
                assertTrue(religionFieldText == null);
            }
        });
    }
    
    public void testRenderWithoutResponse() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                compEntry.addRowLabel("3 months ago");
                compEntry.addRowLabel("6 months ago");
                
                TextEntry periodText = factory.createTextEntry("Period");
                compEntry.addEntry(periodText);
                periodText.setSection(section);
                
                OptionEntry jobOptionEntry = factory.createOptionEntry(
                        "Job Option", "Job");
                compEntry.addEntry(jobOptionEntry);
                jobOptionEntry.setSection(section);
                Option skilledOption = factory.createOption("Skilled");
                Option unskilledOption = factory.createOption("Unskilled");
                Option unemployedOption = factory.createOption("Unemployed");
                jobOptionEntry.addOption(skilledOption);
                jobOptionEntry.addOption(unskilledOption);
                jobOptionEntry.addOption(unemployedOption);
                Option otherJobOption = factory
                        .createOption("Other (specify)");
                jobOptionEntry.addOption(otherJobOption);
                otherJobOption.setTextEntryAllowed(true);

                OptionEntry religion = factory.createOptionEntry(
                        "Religion Option", "Religious Cultural Tradition");
                compEntry.addEntry(religion);
                religion.setSection(section);
                religion.addOption(factory.createOption("Muslim", 1));
                Option christianOption = factory.createOption("Christian", 2);
                religion.addOption(christianOption);
                religion.addOption(factory.createOption("Hindu", 3));
                Option religionOther = factory.createOption("Other (specify)",
                        4);
                religion.addOption(religionOther);
                religionOther.setTextEntryAllowed(true);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                assertEquals(7, rendererHandler.getNumExistingRenderers());

                String jobSelectedOption = getSelectedComboBoxOption(rendererHandler, 
                        jobOptionEntry, 0);
                assertTrue(jobSelectedOption == null);
                
                String jobFieldText = getSelectedComboBoxOptionText(rendererHandler, 
                        jobOptionEntry, 0);
                assertTrue(jobFieldText == null);

                String religionSelectedOption = getSelectedComboBoxOption(
                        rendererHandler, religion, 0);
                assertTrue(religionSelectedOption == null);
                
                String religionFieldText = getSelectedComboBoxOptionText(rendererHandler, 
                        religion, 0);
                assertTrue(religionFieldText == null);
            }
        });
    }
    
    public void testHeaderNamesWithNullHeader() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                compEntry.addRowLabel("3 months ago");
                compEntry.addRowLabel("6 months ago");
                
                TextEntry periodText = factory.createTextEntry("Period", "Period");
                compEntry.addEntry(periodText);
                periodText.setSection(section);
                
                NumericEntry numericEntry = factory.createNumericEntry("Numeric");
                compEntry.addEntry(numericEntry);
                numericEntry.setSection(section);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rendererHandler
                        .getExistingRenderer(compEntry, 0);

                EntryTable entryTable = (EntryTable) compRenderer
                        .getComposite();
                
                assertHeadings(entryTable, periodText.getDisplayText(),
                        "");
            }
        });
    }
    
    public void testDisableEnableEntryTable() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                compEntry.addRowLabel("3 months ago");
                compEntry.addRowLabel("6 months ago");

                TextEntry periodText = factory.createTextEntry("Period",
                        "Period");
                compEntry.addEntry(periodText);
                periodText.setSection(section);

                NumericEntry numericEntry = factory
                        .createNumericEntry("Numeric", "Numeric");
                compEntry.addEntry(numericEntry);
                numericEntry.setSection(section);

                OptionEntry conditionalEntry = factory
                        .createOptionEntry("Enable composite");
                document.addEntry(conditionalEntry);
                conditionalEntry.setSection(section);
                Option yesOption = factory.createOption("Yes", "Yes");
                conditionalEntry.addOption(yesOption);
                Option noOption = factory.createOption("No", "No");
                conditionalEntry.addOption(noOption);

                OptionDependent noOptDep = factory.createOptionDependent();
                noOptDep.setEntryStatus(EntryStatus.DISABLED);
                noOptDep.setDependentEntry(compEntry);
                noOption.addOptionDependent(noOptDep);

                OptionDependent yesOptDep = factory.createOptionDependent();
                yesOptDep.setEntryStatus(EntryStatus.MANDATORY);
                yesOptDep.setDependentEntry(compEntry);
                yesOption.addOptionDependent(yesOptDep);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                BasicRenderer<?> periodTextRenderer0 = (BasicRenderer<?>) rendererHandler
                        .getExistingRenderer(periodText, 0);

                BasicRenderer<?> periodTextRenderer1 = (BasicRenderer<?>) rendererHandler
                        .getExistingRenderer(periodText, 1);

                BasicRenderer<?> numericEntryRenderer0 = (BasicRenderer<?>) rendererHandler
                        .getExistingRenderer(numericEntry, 0);
                BasicRenderer<?> numericEntryRenderer1 = (BasicRenderer<?>) rendererHandler
                        .getExistingRenderer(numericEntry, 1);

                JTextComponent textComponent0 = ((EntryComponent) periodTextRenderer0
                        .getField()).getTextComponent();
                assertEquals(true, textComponent0.isEnabled());
                assertEquals(false, textComponent0.isEditable());

                JTextComponent textComponent1 = ((EntryComponent) periodTextRenderer1
                        .getField()).getTextComponent();
                assertEquals(true, textComponent1.isEnabled());
                assertEquals(false, textComponent1.isEditable());

                JTextComponent numericComponent0 = ((EntryComponent) numericEntryRenderer0
                        .getField()).getTextComponent();
                assertEquals(true, numericComponent0.isEnabled());
                assertEquals(true, numericComponent0.isEditable());

                JTextComponent numericComponent1 = ((EntryComponent) numericEntryRenderer1
                        .getField()).getTextComponent();
                assertEquals(true, numericComponent1.isEnabled());
                assertEquals(true, numericComponent1.isEditable());

                setOption(rendererHandler, conditionalEntry, 0, noOption);

                assertEquals(false, textComponent0.isEnabled());
                assertEquals(false, textComponent0.isEditable());

                assertEquals(false, textComponent1.isEnabled());
                assertEquals(false, textComponent1.isEditable());

                assertEquals(false, numericComponent0.isEnabled());
                assertEquals(true, numericComponent0.isEditable());

                assertEquals(false, numericComponent1.isEnabled());
                assertEquals(true, numericComponent1.isEditable());

                setOption(rendererHandler, conditionalEntry, 0, yesOption);

                assertEquals(true, textComponent0.isEnabled());
                assertEquals(false, textComponent0.isEditable());

                assertEquals(true, textComponent1.isEnabled());
                assertEquals(false, textComponent1.isEditable());

                assertEquals(true, numericComponent0.isEnabled());
                assertEquals(true, numericComponent0.isEditable());

                assertEquals(true, numericComponent1.isEnabled());
                assertEquals(true, numericComponent1.isEditable());
            }
        });

    }
    
    public void testHeaderNames() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                compEntry.addRowLabel("3 months ago");
                compEntry.addRowLabel("6 months ago");
                
                TextEntry periodText = factory.createTextEntry("Period", "Period");
                compEntry.addEntry(periodText);
                periodText.setSection(section);
                
                NumericEntry numericEntry = factory.createNumericEntry("Numeric",
                        "Numeric");
                compEntry.addEntry(numericEntry);
                numericEntry.setSection(section);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rendererHandler
                        .getExistingRenderer(compEntry, 0);

                EntryTable entryTable = (EntryTable) compRenderer
                        .getComposite();
                
                assertHeadings(entryTable, periodText.getDisplayText(),
                        numericEntry.getDisplayText());
            }
        });
    }
    
    public void testValidationIconDisplay() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                compEntry.addRowLabel("3 months ago");
                compEntry.addRowLabel("6 months ago");
                
                TextEntry periodText = factory.createTextEntry("Period", "Period");
                compEntry.addEntry(periodText);
                periodText.setSection(section);
                
                OptionEntry jobOptionEntry = factory.createOptionEntry(
                        "Job Option", "Job");
                compEntry.addEntry(jobOptionEntry);
                jobOptionEntry.setSection(section);
                Option skilledOption = factory.createOption("Skilled");
                Option unskilledOption = factory.createOption("Unskilled");
                Option unemployedOption = factory.createOption("Unemployed");
                jobOptionEntry.addOption(skilledOption);
                jobOptionEntry.addOption(unskilledOption);
                jobOptionEntry.addOption(unemployedOption);
                Option otherJobOption = factory
                        .createOption("Other (specify)");
                jobOptionEntry.addOption(otherJobOption);
                otherJobOption.setTextEntryAllowed(true);

                OptionEntry religion = factory.createOptionEntry(
                        "Religion Option", "Religious Cultural Tradition");
                compEntry.addEntry(religion);
                religion.setSection(section);
                religion.addOption(factory.createOption("Muslim", 1));
                Option christianOption = factory.createOption("Christian", 2);
                religion.addOption(christianOption);
                religion.addOption(factory.createOption("Hindu", 3));
                Option religionOther = factory.createOption("Other (specify)",
                        4);
                religion.addOption(religionOther);
                religionOther.setTextEntryAllowed(true);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                                
                CompositeRenderer<?> compRenderer = 
                    (CompositeRenderer<?>) rendererHandler.getExistingRenderer(compEntry, 0);
                
                EntryTable entryTable = (EntryTable) compRenderer.getComposite();
                ValidationLabelsGroup validationLabel0 = entryTable.getValidationLabel(0);
                assertNull(validationLabel0.getIcon());
                
                ValidationLabelsGroup validationLabel1 = entryTable.getValidationLabel(1);
                assertNull(validationLabel1.getIcon());

                application.getModel().validateSection(false);
                Icon errorIcon = ValidationResultViewFactory.getErrorIcon();
                assertEquals(errorIcon, validationLabel0.getIcon());
                assertEquals(errorIcon, validationLabel1.getIcon());
            }
        });
    }
    
    public static void main(String[] args) throws Exception {
        new CompositeRendererFixedTableTest().testDisableEnableEntryTable();
    }
}
