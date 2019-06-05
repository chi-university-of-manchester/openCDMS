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


import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.model.OptionPresModel;
import org.psygrid.collection.entry.ui.BasicTextEntryField;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;

/**
 * Tests the OptionEntryRenderer for IOptionEntry with a non null otherOption.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 *
 */
@SuppressWarnings("nls")
public class OptionRendererTest2 extends AbstractEntryTestCase    {

    private Section section;
    private Factory factory;
    private Application application;
    private DocumentOccurrence docOccurrence;
    private DocumentInstance docInstance;
    private Document document;
    private SectionOccurrence sectionOcc;
    
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
    }
    
    public void testNestedConditionalLogic() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry conditionalEntry1 = factory.createOptionEntry(
                        "Conditional Entry 1",
                        "Enable question 4 if yes, otherwise question 2");
                document.addEntry(conditionalEntry1);
                conditionalEntry1.setSection(section);
                Option yesOption1 = factory.createOption("Yes");
                Option noOption1 = factory.createOption("No");
                conditionalEntry1.addOption(yesOption1);
                conditionalEntry1.addOption(noOption1);
                
                OptionEntry conditionalEntry2 = factory.createOptionEntry(
                        "Conditional Entry 2",
                        "Enable question 3", EntryStatus.DISABLED);
                document.addEntry(conditionalEntry2);
                conditionalEntry2.setSection(section);
                Option yesOption2 = factory.createOption("Yes");
                Option noOption2 = factory.createOption("No");
                conditionalEntry2.addOption(yesOption2);
                conditionalEntry2.addOption(noOption2);
                createOptionDependent(factory, noOption1, conditionalEntry2,
                        EntryStatus.MANDATORY);

                OptionEntry conditionalEntry3 = factory.createOptionEntry(
                        "Question 3",
                        "Question 3", EntryStatus.DISABLED);
                document.addEntry(conditionalEntry3);
                conditionalEntry3.setSection(section);
                Option yesOption3 = factory.createOption("Yes");
                Option noOption3 = factory.createOption("No");
                conditionalEntry3.addOption(yesOption3);
                conditionalEntry3.addOption(noOption3);
                createOptionDependent(factory, yesOption2, conditionalEntry3,
                        EntryStatus.MANDATORY);
                
                OptionEntry conditionalEntry4 = factory.createOptionEntry(
                        "Question 4",
                        "Question 4", EntryStatus.DISABLED);
                document.addEntry(conditionalEntry4);
                conditionalEntry4.setSection(section);
                Option yesOption4 = factory.createOption("Yes");
                Option noOption4 = factory.createOption("No");
                conditionalEntry4.addOption(yesOption4);
                conditionalEntry4.addOption(noOption4);
                createOptionDependent(factory, yesOption1, conditionalEntry4,
                        EntryStatus.MANDATORY);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                RendererHandler rendererHandler = application.getModel()
                    .getCurrentRendererHandler();
                
                //Initial status
                
                //Conditional entry 1 status
                JComponent yesOption1RButton = getComponentFromOption(
                        rendererHandler, conditionalEntry1, 0, yesOption1, true);
                assertEquals(true, yesOption1RButton.isEnabled());

                JComponent noOption1RButton = getComponentFromOption(
                        rendererHandler, conditionalEntry1, 0, noOption1, true);
                assertEquals(true, noOption1RButton.isEnabled());
                
                //Conditional entry 2 status
                JComponent yesOption2RButton = getComponentFromOption(
                        rendererHandler, conditionalEntry2, 0, yesOption2, true);
                assertEquals(false, yesOption2RButton.isEnabled());

                JComponent noOption2RButton = getComponentFromOption(
                        rendererHandler, conditionalEntry2, 0, noOption2, true);
                assertEquals(false, noOption2RButton.isEnabled());
                
                //Conditional entry 3 status
                JComponent yesOption3RButton = getComponentFromOption(
                        rendererHandler, conditionalEntry3, 0, yesOption3, true);
                assertEquals(false, yesOption3RButton.isEnabled());

                JComponent noOption3RButton = getComponentFromOption(
                        rendererHandler, conditionalEntry3, 0, noOption3, true);
                assertEquals(false, noOption3RButton.isEnabled());
                
                //Conditional entry 4 status
                JComponent yesOption4RButton = getComponentFromOption(
                        rendererHandler, conditionalEntry4, 0, yesOption4, true);
                assertEquals(false, yesOption4RButton.isEnabled());

                JComponent noOption4RButton = getComponentFromOption(
                        rendererHandler, conditionalEntry4, 0, noOption4, true);
                assertEquals(false, noOption4RButton.isEnabled());
                
                //Select no in first question
                setOption(rendererHandler, conditionalEntry1, 0, noOption1);
                
                //Conditional entry 1 status;
                assertEquals(true, yesOption1RButton.isEnabled());
                assertEquals(true, noOption1RButton.isEnabled());
                
                //Conditional entry 2 status
                assertEquals(true, yesOption2RButton.isEnabled());
                assertEquals(true, noOption2RButton.isEnabled());
                
                //Conditional entry 3 status
                assertEquals(false, yesOption3RButton.isEnabled());
                assertEquals(false, noOption3RButton.isEnabled());
                
                //Conditional entry 4 status
                assertEquals(false, yesOption4RButton.isEnabled());
                assertEquals(false, noOption4RButton.isEnabled());
                
                //Select yes in second question
                setOption(rendererHandler, conditionalEntry2, 0, yesOption2);
                
                //Conditional entry 1 status;
                assertEquals(true, yesOption1RButton.isEnabled());
                assertEquals(true, noOption1RButton.isEnabled());
                
                //Conditional entry 2 status
                assertEquals(true, yesOption2RButton.isEnabled());
                assertEquals(true, noOption2RButton.isEnabled());
                
                //Conditional entry 3 status
                assertEquals(true, yesOption3RButton.isEnabled());
                assertEquals(true, noOption3RButton.isEnabled());
                
                //Conditional entry 4 status
                assertEquals(false, yesOption4RButton.isEnabled());
                assertEquals(false, noOption4RButton.isEnabled());
                
                //Select yes in first question
                setOption(rendererHandler, conditionalEntry1, 0, yesOption1);
                
                //Conditional entry 1 status;
                assertEquals(true, yesOption1RButton.isEnabled());
                assertEquals(true, noOption1RButton.isEnabled());
                
                //Conditional entry 2 status
                assertEquals(false, yesOption2RButton.isEnabled());
                assertEquals(false, noOption2RButton.isEnabled());
                
                //Conditional entry 3 status
                assertEquals(false, yesOption3RButton.isEnabled());
                assertEquals(false, noOption3RButton.isEnabled());
                
                //Conditional entry 4 status
                assertEquals(true, yesOption4RButton.isEnabled());
                assertEquals(true, noOption4RButton.isEnabled());
            }
        });
    }
    
    public static void main(String[] args) throws Exception {
        new OptionRendererTest2().testOptionAndTwoTextEntryAllowed();
    }
    
    public void testDisabledEntryWithValue() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry conditionalEntry1 = factory.createOptionEntry(
                        "Conditional Entry 1",
                        "Enable question 2 if yes");
                document.addEntry(conditionalEntry1);
                conditionalEntry1.setSection(section);
                Option yesOption1 = factory.createOption("Yes");
                Option noOption1 = factory.createOption("No");
                conditionalEntry1.addOption(yesOption1);
                conditionalEntry1.addOption(noOption1);
                BasicResponse response1 = conditionalEntry1.generateInstance(sectionOcc);
                IOptionValue value1 = conditionalEntry1.generateValue();
                value1.setValue(yesOption1);
                response1.setValue(value1);
                docInstance.addResponse(response1);
                
                OptionEntry conditionalEntry2 = factory.createOptionEntry(
                        "Question 2",
                        "Question 2", EntryStatus.DISABLED);
                document.addEntry(conditionalEntry2);
                conditionalEntry2.setSection(section);
                Option yesOption2 = factory.createOption("Yes");
                Option noOption2 = factory.createOption("No");
                conditionalEntry2.addOption(yesOption2);
                conditionalEntry2.addOption(noOption2);
                BasicResponse response2 = conditionalEntry2.generateInstance(sectionOcc);
                IOptionValue value2 = conditionalEntry2.generateValue();
                value2.setValue(noOption2);
                response2.setValue(value2);
                docInstance.addResponse(response2);
                createOptionDependent(factory, yesOption1, conditionalEntry2,
                        EntryStatus.MANDATORY);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                RendererHandler rendererHandler = 
                    application.getModel().getCurrentRendererHandler();
                
                String selectedOption1 = getSelectedRadioButtonOption(
                        rendererHandler, conditionalEntry1, 0);
                String expected1 = RendererHelper.getInstance().getOptionText(conditionalEntry1,
                        yesOption1);
                assertEquals(expected1, selectedOption1);
                
                String selectedOption2 = getSelectedRadioButtonOption(
                        rendererHandler, conditionalEntry2, 0);
                String expected2 = RendererHelper.getInstance().getOptionText(conditionalEntry2,
                        noOption2);
                assertEquals(expected2, selectedOption2);
            }
        });
    }
    
    /**
     * Tests that setting the text in one text entry for an IOption does not
     * affect the text entry in another IOption (in the same IOptionEntry).
     * See bug #537 for more information.
     * 
     * @throws Exception
     */
    public void testOptionAndTwoTextEntryAllowed() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry optionEntry = factory.createOptionEntry(
                        "Job Option", "Job");
                document.addEntry(optionEntry);
                optionEntry.setSection(section);
                Option skilledOption = factory.createOption("Skilled");
                Option unskilledOption = factory.createOption("Unskilled");
                optionEntry.addOption(skilledOption);
                optionEntry.addOption(unskilledOption);
                Option otherJobOption = factory
                        .createOption("Other (specify)");
                optionEntry.addOption(otherJobOption);
                otherJobOption.setTextEntryAllowed(true);
                Option studentJobOption = factory
                        .createOption("Student (specify)");
                optionEntry.addOption(studentJobOption);
                studentJobOption.setTextEntryAllowed(true);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                assertEquals(1, rendererHandler.getNumExistingRenderers());
                setOption(rendererHandler, optionEntry, 0, otherJobOption);
                JPanel otherPanel = (JPanel) getComponentFromOption(rendererHandler,
                        optionEntry, 0, otherJobOption, false);
                JPanel studentPanel = (JPanel) getComponentFromOption(
                        rendererHandler, optionEntry, 0, studentJobOption,
                        false);
                JTextComponent otherField = 
                    ((BasicTextEntryField) otherPanel.getComponent(1)).getTextComponent();
                JTextComponent studentField = 
                    ((BasicTextEntryField) studentPanel.getComponent(1)).getTextComponent();
                
                assertEquals("", otherField.getText());
                assertEquals("", studentField.getText());
                
                otherField.setText("some new text");
                assertEquals("", studentField.getText());
                
                setOption(rendererHandler, optionEntry, 0, skilledOption);
                assertEquals("", otherField.getText());
                assertEquals("", studentField.getText());
                OptionPresModel presModel = (OptionPresModel) ((PresModelRenderer<?>)
                        rendererHandler.getExistingRenderer(optionEntry, 0)).getPresModel();
                assertEquals("", presModel.getTextValueModel().getValue());
                
                setOption(rendererHandler, optionEntry, 0, otherJobOption);
                assertEquals("", otherField.getText());
                assertEquals("", studentField.getText());
            }
        });
    }
    
    public void testRestoreOptionAndText() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry jobOptionEntry = factory.createOptionEntry(
                        "Job Option", "Job");
                document.addEntry(jobOptionEntry);
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
                        "Religion Option",
                        "Religious Cultural Tradition");
                document.addEntry(religion);
                religion.setSection(section);
                religion.addOption(factory.createOption("Muslim", 1));
                Option christianOption = factory.createOption("Christian", 2);
                religion.addOption(christianOption);
                religion.addOption(factory.createOption("Hindu", 3));
                Option religionOther = factory.createOption("Other (specify)",
                        4);
                religion.addOption(religionOther);
                religionOther.setTextEntryAllowed(true);

                BasicResponse jobResponse = jobOptionEntry.generateInstance(sectionOcc);
                docInstance.addResponse(jobResponse);

                IOptionValue jobValue = jobOptionEntry.generateValue();
                jobValue.setValue(otherJobOption);
                jobResponse.setValue(jobValue);
                String textString = "Very skilled";
                jobValue.setTextValue(textString);

                BasicResponse religionResponse = religion.generateInstance(sectionOcc);
                docInstance.addResponse(religionResponse);
                IOptionValue religionValue = religion
                        .generateValue();
                religionValue.setValue(christianOption);
                religionResponse.setValue(religionValue);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                assertEquals(2, rendererHandler.getNumExistingRenderers());

                String jobSelectedOption = getSelectedRadioButtonOption(rendererHandler, 
                        jobOptionEntry, 0);
                assertEquals(otherJobOption.getDisplayText(), jobSelectedOption);
                JPanel jobTextPanel = (JPanel) getComponentFromOption(rendererHandler, 
                        jobOptionEntry, 0, otherJobOption, false);
                assertNotNull("The job JPanel was not found", jobTextPanel);
                BasicTextEntryField jobTextField = (BasicTextEntryField) jobTextPanel.getComponent(1);
                assertEquals(textString, jobTextField.getTextComponent()
                        .getText());

                assertEquals("jobTextField should be enabled", true, 
                        jobTextField.isEnabled());
                String religionSelectedOption = getSelectedRadioButtonOption(
                        rendererHandler, religion, 0);
                String christianTextExpected = christianOption.getCode() 
                        + ". " + christianOption.getDisplayText();
                assertEquals(christianTextExpected, religionSelectedOption);
                
                JPanel religionTextPanel = (JPanel) getComponentFromOption(rendererHandler,
                        religion, 0, religionOther, false);
                assertNotNull("The religion JPanel was not found", religionTextPanel);
                BasicTextEntryField religionTextField = 
                    (BasicTextEntryField) religionTextPanel.getComponent(1);
                assertEquals("", religionTextField.getTextComponent().getText());
                assertEquals(false, religionTextField.isEnabled());
            }
        });
    }
}
