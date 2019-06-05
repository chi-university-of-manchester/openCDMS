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

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.ui.TextEntryField;
import org.psygrid.data.model.IIntegerValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.ITextValue;

import com.jgoodies.validation.view.ValidationResultViewFactory;

@SuppressWarnings("nls")
public class TextRendererTest2 extends AbstractEntryTestCase {

    private Section section;
    private Application application;
    private DocumentOccurrence docOccurrence;
    private DocumentInstance docInstance;
    private Document doc;
    private SectionOccurrence sectionOcc;
    private Factory factory;
    
    private void init() throws Exception {
        factory = getFactory();
        application = createApplication();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        docOccurrence = getDocumentOccurrence();
        doc = docOccurrence.getDocument();
        dataSet.addDocument(doc);
        sectionOcc = getSectionOccurrence();
        section = sectionOcc.getSection();
        doc.addSection(section);
        docInstance = doc.generateInstance(docOccurrence);
        record.addDocumentInstance(docInstance);
    }
    
    public static void main(String[] args) throws Exception {
        new TextRendererTest2().testStandardCodesAtStart();
    }
    
    public void testStandardCodesAtStart() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                TextEntry postcodeEntry = factory.createTextEntry("Postcode",
                        "Postcode");
                postcodeEntry.addUnit(factory.createUnit("mg"));
                postcodeEntry.addUnit(factory.createUnit("g"));
                doc.addEntry(postcodeEntry);
                postcodeEntry.setSection(section);
                
                BasicResponse response = postcodeEntry.generateInstance(sectionOcc);
                ITextValue value = postcodeEntry.generateValue();
                List<StandardCode> standardCodes = 
                    getStandardCodesGetter(application.getModel()).getStandardCodes();
                StandardCode selectedStdCode = standardCodes.get(0);
                value.setStandardCode(selectedStdCode);
                response.setValue(value);
                docInstance.addResponse(response);
                
                application.setVisible(true);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                TextEntryField field = 
                    (TextEntryField) getFieldAsEntryComponent(rendererHandler, postcodeEntry, 0);
                
                assertEquals(false, field.getTextComponent().isEnabled());
                assertEquals(true, field.getPopupButton().isEnabled());
                String stdCodeText = RendererHelper.getInstance().getStandardCodeText(selectedStdCode);
                
                assertEquals(stdCodeText, field.getTextComponent().getText());
                             
            }
        });
    }
    
    /**
     * Verifies that an entry with a standard code as a response has its field
     * disabled, but the popup enabled in the case where it it started as 
     * disabled, but got enabled due to an option dependent.
     * 
     * See bug #467 : Item with standard code should not have text field enabled.
     * @throws Exception 
     */
    public void testStandardCodesAtStartWithEntryThatGetsEnabled() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry condEntry = factory.createOptionEntry("Conditional " +
                        "entry");
                doc.addEntry(condEntry);
                condEntry.setSection(section);
                Option yesOption = factory.createOption("Yes");
                condEntry.addOption(yesOption);
                Option noOption = factory.createOption("No");
                condEntry.addOption(noOption);
                BasicResponse condResponse = condEntry.generateInstance(sectionOcc);
                IOptionValue condValue = condEntry.generateValue();
                condValue.setValue(yesOption);
                condResponse.setValue(condValue);
                docInstance.addResponse(condResponse);
                
                TextEntry postcodeEntry = factory.createTextEntry("Postcode",
                        "Postcode", EntryStatus.DISABLED);
                doc.addEntry(postcodeEntry);
                postcodeEntry.setSection(section);
                createOptionDependent(factory, yesOption, postcodeEntry, EntryStatus.MANDATORY);
                
                BasicResponse response = postcodeEntry.generateInstance(sectionOcc);
                ITextValue value = postcodeEntry.generateValue();
                List<StandardCode> standardCodes = 
                    getStandardCodesGetter(application.getModel()).getStandardCodes();
                StandardCode selectedStdCode = standardCodes.get(0);
                value.setStandardCode(selectedStdCode);
                response.setValue(value);
                docInstance.addResponse(response);
                
                application.setVisible(true);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                TextEntryField field = 
                    (TextEntryField) getFieldAsEntryComponent(rendererHandler, postcodeEntry, 0);
                
                assertEquals(false, field.getTextComponent().isEnabled());
                assertEquals(true, field.getPopupButton().isEnabled());
                String stdCodeText = RendererHelper.getInstance().getStandardCodeText(selectedStdCode);
                
                assertEquals(stdCodeText, field.getTextComponent().getText());
                             
            }
        });
    }
    
    /**
     * Verifies that an entry with a standard code as a response has its field
     * and popup disabled in the case where it is disabled at the start.
     * @throws Exception 
     */
    public void testStandardCodesAtStartWithEntryThatIsDisabled() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                TextEntry postcodeEntry = factory.createTextEntry("Postcode",
                        "Postcode", EntryStatus.DISABLED);
                doc.addEntry(postcodeEntry);
                postcodeEntry.setSection(section);
                
                BasicResponse response = postcodeEntry.generateInstance(sectionOcc);
                ITextValue value = postcodeEntry.generateValue();
                List<StandardCode> standardCodes = 
                    getStandardCodesGetter(application.getModel()).getStandardCodes();
                StandardCode selectedStdCode = standardCodes.get(0);
                value.setStandardCode(selectedStdCode);
                response.setValue(value);
                docInstance.addResponse(response);
                
                application.setVisible(true);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                TextEntryField field = 
                    (TextEntryField) getFieldAsEntryComponent(rendererHandler, postcodeEntry, 0);
                
                assertEquals(false, field.getTextComponent().isEnabled());
                assertEquals(false, field.getPopupButton().isEnabled());
                String stdCodeText = RendererHelper.getInstance().getStandardCodeText(selectedStdCode);
                
                assertEquals(stdCodeText, field.getTextComponent().getText());
                             
            }
        });
    }
    
    /**
     * Verifies that an entry with a standard code as a response has its field
     * and popup disabled in the case where it it started as enabled, but got 
     * disabled due to an option dependent.
     * @throws Exception 
     */
    public void testStandardCodesAtStartWithEntryThatGetsDisabled() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry condEntry = factory.createOptionEntry("Conditional " +
                        "entry");
                doc.addEntry(condEntry);
                condEntry.setSection(section);
                Option yesOption = factory.createOption("Yes");
                condEntry.addOption(yesOption);
                Option noOption = factory.createOption("No");
                condEntry.addOption(noOption);
                BasicResponse condResponse = condEntry.generateInstance(sectionOcc);
                IOptionValue condValue = condEntry.generateValue();
                condValue.setValue(yesOption);
                condResponse.setValue(condValue);
                docInstance.addResponse(condResponse);
                
                TextEntry postcodeEntry = factory.createTextEntry("Postcode",
                        "Postcode", EntryStatus.MANDATORY);
                doc.addEntry(postcodeEntry);
                postcodeEntry.setSection(section);
                createOptionDependent(factory, yesOption, postcodeEntry, EntryStatus.DISABLED);
                
                BasicResponse response = postcodeEntry.generateInstance(sectionOcc);
                ITextValue value = postcodeEntry.generateValue();
                List<StandardCode> standardCodes = 
                    getStandardCodesGetter(application.getModel()).getStandardCodes();
                StandardCode selectedStdCode = standardCodes.get(0);
                value.setStandardCode(selectedStdCode);
                response.setValue(value);
                docInstance.addResponse(response);
                
                application.setVisible(true);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                TextEntryField field = 
                    (TextEntryField) getFieldAsEntryComponent(rendererHandler, postcodeEntry, 0);
                
                assertEquals(false, field.getTextComponent().isEnabled());
                assertEquals(false, field.getPopupButton().isEnabled());
                String stdCodeText = RendererHelper.getInstance().getStandardCodeText(selectedStdCode);
                
                assertEquals(stdCodeText, field.getTextComponent().getText());
                             
            }
        });
    }
    
    public void testDisabledItemWithValidationIcon() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry condEntry = factory
                        .createOptionEntry("Has abode",
                                "Has abode. If yes, answer question 2.");
                Option yesOption = factory.createOption("Yes");
                Option noOption = factory.createOption("No");
                condEntry.addOption(yesOption);
                condEntry.addOption(noOption);
                condEntry.setDefaultValue(noOption);
                doc.addEntry(condEntry);
                condEntry.setSection(section);
                
                TextEntry postcodeEntry = factory.createTextEntry("Postcode",
                        "Postcode");
                postcodeEntry.setEntryStatus(EntryStatus.DISABLED);
                doc.addEntry(postcodeEntry);
                postcodeEntry.setSection(section);

                OptionDependent postcodeDependent = factory
                        .createOptionDependent();
                postcodeDependent.setDependentEntry(postcodeEntry);
                postcodeDependent.setEntryStatus(EntryStatus.MANDATORY);
                yesOption.addOptionDependent(postcodeDependent);
                
                application.setVisible(true);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                BasicRenderer<?> renderer = (BasicRenderer<?>) rendererHandler.getExistingRenderer(
                        postcodeEntry, 0);
                
                assertEquals(false, renderer.getLabel().isEnabled());
                assertEquals(false, renderer.getField().isEnabled());
                assertEquals(null, renderer.getValidationLabel().getIcon());
                
                setOption(rendererHandler, condEntry, 0, yesOption);
                
                assertEquals(true, renderer.getLabel().isEnabled());
                assertEquals(true, renderer.getField().isEnabled());
                assertEquals(null, renderer.getValidationLabel().getIcon());
                
                renderer.getPresModel().performValidation(false);
                
                assertEquals(true, renderer.getLabel().isEnabled());
                assertEquals(true, renderer.getField().isEnabled());
                assertEquals(ValidationResultViewFactory.getErrorIcon(), 
                        renderer.getValidationLabel().getIcon());
                
                setOption(rendererHandler, condEntry, 0, noOption);

                assertEquals(false, renderer.getLabel().isEnabled());
                assertEquals(false, renderer.getField().isEnabled());
                assertEquals(null, renderer.getValidationLabel().getIcon());
            }
        });
    }
    
    public void testEnableItemWithValidationError() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry condEntry = factory
                        .createOptionEntry("Has abode",
                                "Has abode. If yes, answer question 2.");
                Option yesOption = factory.createOption("Yes");
                Option noOption = factory.createOption("No");
                condEntry.addOption(yesOption);
                condEntry.addOption(noOption);
                condEntry.setDefaultValue(noOption);
                doc.addEntry(condEntry);
                condEntry.setSection(section);
                
                IntegerEntry numberEntry = factory.createIntegerEntry("Number",
                        "Number");
                numberEntry.setEntryStatus(EntryStatus.DISABLED);
                doc.addEntry(numberEntry);
                numberEntry.setSection(section);
                IntegerValidationRule rule = factory.createIntegerValidationRule();
                rule.setLowerLimit(Integer.valueOf(5));
                numberEntry.addValidationRule(rule);                

                BasicResponse response = numberEntry.generateInstance(sectionOcc);
                IIntegerValue value = numberEntry.generateValue();
                value.setValue(Integer.valueOf(3));
                response.setValue(value);
                docInstance.addResponse(response);
                
                OptionDependent optDependent = factory
                        .createOptionDependent();
                optDependent.setDependentEntry(numberEntry);
                optDependent.setEntryStatus(EntryStatus.MANDATORY);
                yesOption.addOptionDependent(optDependent);
                
                application.setVisible(true);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                BasicRenderer<?> renderer = (BasicRenderer<?>) rendererHandler.getExistingRenderer(
                        numberEntry, 0);
                
                assertEquals(false, renderer.getLabel().isEnabled());
                assertEquals(false, renderer.getField().isEnabled());
                assertEquals(null, renderer.getValidationLabel().getIcon());
                
                setOption(rendererHandler, condEntry, 0, yesOption);
                
                assertEquals(true, renderer.getLabel().isEnabled());
                assertEquals(true, renderer.getField().isEnabled());
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        renderer.getValidationLabel().getIcon());
                
                renderer.getPresModel().performValidation(false);
                
                assertEquals(true, renderer.getLabel().isEnabled());
                assertEquals(true, renderer.getField().isEnabled());
                assertEquals(ValidationResultViewFactory.getErrorIcon(), 
                        renderer.getValidationLabel().getIcon());
                
                setOption(rendererHandler, condEntry, 0, noOption);

                assertEquals(false, renderer.getLabel().isEnabled());
                assertEquals(false, renderer.getField().isEnabled());
                assertEquals(null, renderer.getValidationLabel().getIcon());
            }
        });
    }
}
