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

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JMenuItem;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.action.EnableEditingAction;
import org.psygrid.collection.entry.ui.AbstractEntryField;
import org.psygrid.collection.entry.ui.EntryTable;
import org.psygrid.collection.entry.ui.ValidationLabelsGroup;
import org.psygrid.collection.entry.ui.VariableTable;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class CompositeRendererTest extends AbstractEntryTestCase {

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
        compResponse = compEntry.generateInstance(sectionOcc);
        docInstance.addResponse(compResponse);
    }
    
    public void testValidationIconWithOneRow() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                OptionEntry drug = factory.createOptionEntry("Drug Option", "Drug",
                        EntryStatus.DISABLED);
                compEntry.addEntry(drug);
                drug.addOption(factory.createOption("Cannabis"));
                Option otherOption = factory.createOption("Other Drugs Specify");
                drug.addOption(otherOption);
                otherOption.setTextEntryAllowed(true);
                
                OptionEntry freqOfUse = factory.createOptionEntry("Frequency",
                        "Previous Freq. of Use");
                compEntry.addEntry(freqOfUse);
                freqOfUse.addOption(factory.createOption("None",0));
                freqOfUse.addOption(factory.createOption("Occasional user (less than weekly)",1));
                
                OptionEntry durPrevUse =
                    factory.createOptionEntry("Duration","Duration of Previous Use");
                compEntry.addEntry(durPrevUse);
                durPrevUse.addOption(factory.createOption("less than 2 weeks", 0));
                durPrevUse.addOption(factory.createOption("more than 4 weeks", 1));
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rendererHandler
                        .getExistingRenderer(compEntry, 0);

                VariableTable entryTable = (VariableTable) compRenderer
                        .getComposite();

                ValidationLabelsGroup label = entryTable.getValidationLabel(0);
                assertNull(label.getIcon());
                
                boolean valid = application.getModel().validateSection(false).isEmpty();
                assertEquals(false, valid);
                
                assertNotNull(label.getIcon());
            }
        });
    }
    
    public void testAddRowWithDerivedEntry() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                OptionEntry drug = factory.createOptionEntry("Drug Option", "Drug",
                        EntryStatus.DISABLED);
                
                compEntry.addEntry(drug);
                CompositeEntry houseWorkOthers = factory.createComposite(
                        "Other", "Other (please state)");
                document.addEntry(houseWorkOthers);
                houseWorkOthers.setSection(section);
                TextEntry otherChore = factory.createTextEntry("Chore",
                        "Chore");
                houseWorkOthers.addEntry(otherChore);
                otherChore.setSection(section);
                NumericEntry otherTime = factory.createNumericEntry("Time",
                        "Time");
                houseWorkOthers.addEntry(otherTime);
                otherTime.setSection(section);

                DerivedEntry otherTimeTotal = factory.createDerivedEntry(
                        "Total time",
                        "Total time doing other housework activities");
                document.addEntry(otherTimeTotal);
                otherTimeTotal.setSection(section);
                otherTimeTotal.setAggregateOperator("+");
                otherTimeTotal.setComposite(houseWorkOthers);
                otherTimeTotal.addVariable("a", otherTime);
                otherTimeTotal.setFormula("a");
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rendererHandler
                        .getExistingRenderer(houseWorkOthers, 0);

                VariableTable entryTable = (VariableTable) compRenderer
                        .getComposite();
                
                entryTable.getAddRowButton().doClick();                
            }
        });
    }
    
    public void testValidationIconWithTwoRows() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                OptionEntry drug = factory.createOptionEntry("Drug Option", "Drug",
                        EntryStatus.DISABLED);
                compEntry.addEntry(drug);
                Option cannabis = factory.createOption("Cannabis");
                drug.addOption(cannabis);
                Option otherOption = factory.createOption("Other Drugs Specify");
                drug.addOption(otherOption);
                otherOption.setTextEntryAllowed(true);
                
                OptionEntry freqOfUse = factory.createOptionEntry("Frequency",
                        "Previous Freq. of Use");
                compEntry.addEntry(freqOfUse);
                Option noneOption = factory.createOption("None", 0);
                freqOfUse.addOption(noneOption);
                freqOfUse.addOption(factory.createOption("Occasional user (less than weekly)",1));
                
                NumericEntry duration =
                    factory.createNumericEntry("Duration", "Duration of Previous Use");
                compEntry.addEntry(duration);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rendererHandler
                        .getExistingRenderer(compEntry, 0);

                VariableTable entryTable = (VariableTable) compRenderer
                        .getComposite();
                
                entryTable.getAddRowButton().doClick();
                setComboBoxOption(rendererHandler, drug, 0, cannabis);
                setComboBoxOption(rendererHandler, freqOfUse, 0, noneOption);
                getFieldAsEntryComponent(rendererHandler, duration, 0).getTextComponent().setText("1");
                
                ValidationLabelsGroup label0 = entryTable.getValidationLabel(0);
                ValidationLabelsGroup label1 = entryTable.getValidationLabel(1);
                assertNull(label0.getIcon());
                assertNull(label1.getIcon());
                
                boolean valid = application.getModel().validateSection(false).isEmpty();
                assertEquals(false, valid);
                
                assertNull("label0 doesn't have a null icon", label0.getIcon());
                assertNotNull("label1 has null icon", label1.getIcon());
            }
        });
    }
    
    public void testApplyStdCodeToRow() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                TextEntry drug = factory.createTextEntry("Drug", "Drug");
                compEntry.addEntry(drug);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rendererHandler
                        .getExistingRenderer(compEntry, 0);

                VariableTable entryTable = (VariableTable) compRenderer
                        .getComposite();
                StandardCode stdCode = rendererHandler.getStdCodes().get(0);
                AbstractEntryField field = getComponent(AbstractEntryField.class,
                        application, null, null);
                entryTable.getModel().applyStdCodeToRow(0, stdCode);
                
                /* Verify that the EnableEditingAction is enabled */
                boolean found = false;
                for (Component comp : field.getPopup().getComponents()) {
                    if (comp instanceof JMenuItem) {
                        JMenuItem menuItem = (JMenuItem) comp;
                        Action action = menuItem.getAction();
                        if (action instanceof EnableEditingAction) {
                            found = true;
                            assertEquals(true, action.isEnabled());
                        }
                    }
                }
                assertEquals(true, found);
            }
        });
    }
    
    public static void main(String[] args) throws Exception {
        new CompositeRendererTest().testApplyStdCodeToRow();
    }
    
    public void testHeaderNamesWithNullHeader() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
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
    
    public void testHeaderNames() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
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

    public void testRestoreOptionAndText() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
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

                assertEquals(3, rendererHandler.getNumExistingRenderers());

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
    
    public void testRestoreOption() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
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

                OptionEntry religion = factory.createOptionEntry(
                        "Religion Option", "Religious Cultural Tradition");
                compEntry.addEntry(religion);
                religion.setSection(section);
                religion.addOption(factory.createOption("Muslim", 1));
                Option christianOption = factory.createOption("Christian", 2);
                religion.addOption(christianOption);
                religion.addOption(factory.createOption("Hindu", 3));


                CompositeRow compRow = compResponse.createCompositeRow();
                
                BasicResponse jobResponse = jobOptionEntry
                        .generateInstance(sectionOcc);
                compRow.addResponse(jobResponse);

                IOptionValue jobValue = jobOptionEntry.generateValue();
                jobValue.setValue(unskilledOption);
                jobResponse.setValue(jobValue);

                BasicResponse religionResponse = religion
                        .generateInstance(sectionOcc);
                compRow.addResponse(religionResponse);
                IOptionValue religionValue = religion
                        .generateValue();
                religionValue.setValue(christianOption);
                religionResponse.setValue(religionValue);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                assertEquals(3, rendererHandler.getNumExistingRenderers());

                String jobSelectedOption = getSelectedComboBoxOption(rendererHandler, 
                        jobOptionEntry, 0);
                assertEquals(unskilledOption.getDisplayText(), jobSelectedOption);

                String religionSelectedOption = getSelectedComboBoxOption(
                        rendererHandler, religion, 0);
                String christianTextExpected = christianOption.getCode() + ". "
                        + christianOption.getDisplayText();
                assertEquals(christianTextExpected, religionSelectedOption);
            }
        });
    }
}
