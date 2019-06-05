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
package org.psygrid.collection.entry;

import java.awt.Font;
import java.awt.Window;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.renderer.BasicRenderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.data.model.ITextValue;

import com.jgoodies.validation.view.ValidationResultViewFactory;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class ApplicationTest extends AbstractEntryTestCase    {

    private Application app;
    private Factory factory;
    private Section firstSection;
    private SectionOccurrence firstSectionOcc;
    private Document doc;
    private DocumentInstance docInstance;
    private Section secondSection;
    private SectionOccurrence secondSectionOcc;
    private ApplicationModel model;
    private BuilderHandler builderHandler;
    
    private void init() throws Exception {
        factory = getFactory();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        DocumentOccurrence docOccurrence = getDocumentOccurrence();
        doc = docOccurrence.getDocument();
        doc.addOccurrence(docOccurrence);
        dataSet.addDocument(doc);
        docInstance = doc.generateInstance(docOccurrence);
        setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.INCOMPLETE));
        record.addDocumentInstance(docInstance);
        firstSection = factory.createSection("Initial section", "Initial section");
        firstSectionOcc = factory.createSectionOccurrence("Initial Section Occ");
        firstSection.addOccurrence(firstSectionOcc);
        secondSection = factory.createSection("Last section", "Last section");
        secondSectionOcc = factory.createSectionOccurrence("Last section - Section Occ 1");
        secondSection.addOccurrence(secondSectionOcc);
        SectionOccurrence thirdSectionOcc =
            factory.createSectionOccurrence("Last section - Section Occ 2");
        secondSection.addOccurrence(thirdSectionOcc);
        doc.addSection(firstSection);
        doc.addSection(secondSection);
        app = createApplication();
        model = app.getModel();
    }
    
//    public void testProgressPanel() throws Exception {
//        invokeAndWait(new Executable() {
//            public void execute() throws Exception {
//                init();
//                app.setSelectedDocOccurrenceInstance(docInstance, 0);
//                SectionOccurrencesView panel = app.getSectionsPanel();
//                List<JComponent> sectionLabels = panel.getSectionLabels();
//                List<JRadioButton> numberLabels = panel.getNumberRadioButtons();
//                int size = sectionLabels.size();
//                assertEquals(size, numberLabels.size());
//                checkFontsWhenIndexIs0(sectionLabels, numberLabels, size);
//                Action forwardAction = model.getForwardAction();
//                forwardAction.actionPerformed(null);
//                assertTrue(isFontBold(sectionLabels.subList(1, 2)));
//                assertTrue(isFontBold(numberLabels.subList(1, 2)));
//                assertTrue(isFontNormal(sectionLabels.subList(2, size)));
//                assertTrue(isFontNormal(numberLabels.subList(2, size)));
//                assertTrue(isFontNormal(sectionLabels.subList(0, 1)));
//                assertTrue(isFontNormal(numberLabels.subList(0, 1)));
//                Action backAction = model.getBackAction();
//                backAction.actionPerformed(null);
//                checkFontsWhenIndexIs0(sectionLabels, numberLabels, size);
//            }
//        });
//    }
    
    private void checkFontsWhenIndexIs0(List<? extends JComponent> sectionLabels,
            List<? extends JComponent> numberLabels, int size) {
        assertTrue(isFontBold(sectionLabels.subList(0, 1)));
        assertTrue(isFontBold(numberLabels.subList(0, 1)));
        assertTrue(isFontNormal(sectionLabels.subList(1, size)));
        assertTrue(isFontNormal(numberLabels.subList(1, size)));
    }
    
    private boolean isFontBold(List<? extends JComponent> components) {
        Font font = Fonts.getInstance().getBoldLabelFont();
        return fontEquals(font, components);
        
    }
    
    private boolean fontEquals(Font font, List<? extends JComponent> components) {
        for (JComponent comp : components) {
            if (comp.getFont().equals(font) == false) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isFontNormal(List<? extends JComponent> components) {
        Font font = UIManager.getFont("Label.font");
        return fontEquals(font, components);
    }
    
    private void assertRadioButtonsState(List<JRadioButton> radioButtons,
            int currentSection) {
        int index = 0;
        for (JRadioButton button : radioButtons) {
            if (index == currentSection) {
                assertEquals(true, button.isSelected());
            }
            else {
                assertEquals(false, button.isSelected());
            }
            if (index <= currentSection) {
                assertEquals(true, button.isEnabled());
            }
            else {
                assertEquals(false, button.isEnabled());
            }
            ++index;
        }
    }
    
//    public void testSkipToSection() throws Exception    {
//        final INumericEntry numericEntry = getFactory().createNumericEntry("Numeric", "Numeric");
//        invokeAndWait(new Executable() {
//            public void execute() throws Exception {
//                init();
//                ISection thirdSection = factory.createSection("Third section",
//                        "Third section");
//                ISectionOccurrence fourthSectionOcc = factory.createSectionOccurrence("Fourth Section Occ");
//                thirdSection.addOccurrence(fourthSectionOcc);
//                doc.addSection(thirdSection);
//                
//                numericEntry.setSection(thirdSection);
//                doc.addEntry(numericEntry);
//                
//                ISection fourthSection = factory.createSection("Fourth section",
//                        "Fourth section");
//                ISectionOccurrence fifthSectionOcc = factory
//                        .createSectionOccurrence("Fifth Section Occ");
//                fourthSection.addOccurrence(fifthSectionOcc);
//                doc.addSection(fourthSection);
//
//                secondSectionOcc.setMultipleAllowed(true);
//                app.setSelectedDocOccurrenceInstance(docInstance, 0);
//                app.setVisible(true);
//                builderHandler = model.getBuilderHandler();
//                List<JRadioButton> radioButtons =
//                    app.getSectionsPanel().getNumberRadioButtons();
//                assertEquals(5, radioButtons.size());
//                assertRadioButtonsState(radioButtons, 0);
//                
//                /* Move to second SectionPresModel */
//                model.nextSection();
//                assertEquals(5, radioButtons.size());
//                assertRadioButtonsState(radioButtons, 1);
//            }
//        });
//        
//        /* Add one more SectionPresModel and move to third SectionPresModel */
//        moveToNextSecOccInstanceAndAdd();
//        
//        invokeAndWait(new Executable() {
//            public void execute() throws Exception {
//                List<JRadioButton> radioButtons =
//                    app.getSectionsPanel().getNumberRadioButtons();
//                assertEquals(6, radioButtons.size());
//                assertRadioButtonsState(radioButtons, 2);
//            }
//        });
//        
//        /* Move to fourth sectionPresModel */
//        moveToNextSecOccInstance();
//        
//        invokeAndWait(new Executable() {
//            public void execute() throws Exception {
//                List<JRadioButton> radioButtons =
//                    app.getSectionsPanel().getNumberRadioButtons();
//                assertEquals(6, radioButtons.size());
//                assertRadioButtonsState(radioButtons, 3);
//                
//                /* Move to fifth sectionPresModel */
//                model.nextSection();
//                assertEquals(6, radioButtons.size());
//                assertRadioButtonsState(radioButtons, 4);
//                TextEntryField field = (TextEntryField) getField(app.getModel()
//                        .getCurrentRendererHandler(), numericEntry, 0);
//                field.getTextComponent().setText("3");
//
//                /* Move to sixth sectionPresModel */
//                model.nextSection();
//                assertEquals(6, radioButtons.size());
//                assertRadioButtonsState(radioButtons, 5);
//                
//                /* Move to fifth sectionPresModel */
//                model.previousSection();
//                int index = 0;
//                for (JRadioButton button : radioButtons) {
//                    if (index == 4) {
//                        assertEquals(true, button.isSelected());
//                    }
//                    else {
//                        assertEquals(false, button.isSelected());
//                    }
//                    assertEquals(true, button.isEnabled());
//                    ++index;
//                }
//                field = (TextEntryField) getField(app.getModel()
//                        .getCurrentRendererHandler(), numericEntry, 0);
//                /* Disables sixth SectionPresModel */
//                field.getTextComponent().setText("");
//                assertRadioButtonsState(radioButtons, 4);
//                
//                /* Enables sixth SectionPresModel */
//                field.getTextComponent().setText("4");
//                index = 0;
//                for (JRadioButton button : radioButtons) {
//                    if (index == 4) {
//                        assertEquals(true, button.isSelected());
//                    }
//                    else {
//                        assertEquals(false, button.isSelected());
//                    }
//                    assertEquals(true, button.isEnabled());
//                    ++index;
//                }
//                
//                /* Disables sixth SectionPresModel */
//                field.getTextComponent().setText("ee");
//                assertRadioButtonsState(radioButtons, 4);
//                JLabel validationLabel = app.getInformationPanel().getValidationLabel();
//                assertEquals(ValidationResultViewFactory.getErrorIcon(),
//                        validationLabel.getIcon());
//                
//                /* Move to fourth sectionPresModel */
//                model.previousSection();
//                assertNull(validationLabel.getIcon());
//                
//                /* Move to firstSectionPresModel */
//                radioButtons.get(0).doClick();
//                assertEquals(0, model.getCurrentSectionIndex());
//                index = 0;
//                for (JRadioButton button : radioButtons) {
//                    if (index == 0) {
//                        assertEquals(true, button.isSelected());
//                    }
//                    else {
//                        assertEquals(false, button.isSelected());
//                    }
//                    if (index == 5) {
//                        assertEquals(false, button.isEnabled());
//                    }
//                    else {
//                        assertEquals(true, button.isEnabled());
//                    }
//                    ++index;
//                }
//                
//                radioButtons.get(4).doClick();
//                assertEquals(4, model.getCurrentSectionIndex());
//                assertRadioButtonsState(radioButtons, 4);
//            }
//        });
//    }
    
    public void testNextSectionWithMultipleAllowed() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                secondSectionOcc.setMultipleAllowed(true);
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                builderHandler = model.getBuilderHandler();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(1, builderHandler.getRendererHandlers().size());
                assertEquals(0, builderHandler.getRendererHandlerIndex());
                
                model.nextSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(2, builderHandler.getRendererHandlers().size());
                assertEquals(1, builderHandler.getRendererHandlerIndex());
                
            }
        });
        
        moveToNextSecOccInstance();
        
        invokeAndWait(new Executable() {
            public void execute() {
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(3, builderHandler.getRendererHandlers().size());
                assertEquals(2, builderHandler.getRendererHandlerIndex());
                
                model.previousSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(3, builderHandler.getRendererHandlers().size());
                assertEquals(1, builderHandler.getRendererHandlerIndex());
            }
        });
        
        moveToNextSecOccInstanceAndAdd();
        
        invokeAndWait(new Executable() {
            public void execute() {
                assertEquals(4, builderHandler.getSectionPresModels().size());
                assertEquals(4, builderHandler.getRendererHandlers().size());
                assertEquals(2, builderHandler.getRendererHandlerIndex());
                
                model.previousSection();
                assertEquals(4, builderHandler.getSectionPresModels().size());
                assertEquals(4, builderHandler.getRendererHandlers().size());
                assertEquals(1, builderHandler.getRendererHandlerIndex());
            }
        });
        
        nextSectionAndInvoke(new Executable() {
            public void execute() {
                List<Window> windows = getOwnedWindowsShowing(app);
                assertEquals(0, windows.size());
                assertEquals(4, builderHandler.getSectionPresModels().size());
                assertEquals(4, builderHandler.getRendererHandlers().size());
                assertEquals(2, builderHandler.getRendererHandlerIndex());
            }
        });
        
        nextSectionAndInvoke(new Executable() {
            public void execute() throws Exception {
                Window window = getAndCheckOwnedWindow();
                
                JButton noButton = getJButton(window, "No");
                assertNotNull(noButton);
                noButton.doClick();
            }
        });
        
        invokeAndWait(new Executable() {
            public void execute() {
                assertEquals(4, builderHandler.getSectionPresModels().size());
                assertEquals(4, builderHandler.getRendererHandlers().size());
                assertEquals(3, builderHandler.getRendererHandlerIndex());

                model.previousSection();
                assertEquals(4, builderHandler.getSectionPresModels().size());
                assertEquals(4, builderHandler.getRendererHandlers().size());
                assertEquals(2, builderHandler.getRendererHandlerIndex());

                model.previousSection();
                assertEquals(4, builderHandler.getSectionPresModels().size());
                assertEquals(4, builderHandler.getRendererHandlers().size());
                assertEquals(1, builderHandler.getRendererHandlerIndex());

                model.previousSection();
                assertEquals(4, builderHandler.getSectionPresModels().size());
                assertEquals(4, builderHandler.getRendererHandlers().size());
                assertEquals(0, builderHandler.getRendererHandlerIndex());

                model.nextSection();
                assertEquals(4, builderHandler.getSectionPresModels().size());
                assertEquals(4, builderHandler.getRendererHandlers().size());
                assertEquals(1, builderHandler.getRendererHandlerIndex());
            }
        });
    }

    /**
     * Use this method when the current section is a ISecOccInstance and 
     * the caller wants the "No" option to be selected when the dialog box
     * asking if a new ISecOccInstance should be created shows up. 
     * @throws Exception
     */
    private void moveToNextSecOccInstance() throws Exception {
        nextSectionAndInvoke(new Executable() {
            public void execute() throws Exception {
                Window window = getAndCheckOwnedWindow();
                
                JButton noButton = getJButton(window, "No");
                assertNotNull(noButton);
                noButton.doClick();
            }
        });
    }

    /**
     * Use this method when the current section is a ISecOccInstance and 
     * the caller wants the "Yes" option to be selected when the dialog box
     * asking if a new ISecOccInstance should be created shows up. 
     * @throws Exception
     */
    private void moveToNextSecOccInstanceAndAdd() throws Exception {
        nextSectionAndInvoke(new Executable() {
            public void execute() throws Exception {
                Window window = getAndCheckOwnedWindow();
                
                JButton yesButton = getJButton(window, "Yes");
                assertNotNull(yesButton);
                yesButton.doClick();
            }
        });
    }
    
    private Window getAndCheckOwnedWindow() {
        List<Window> showingWindows = getOwnedWindowsShowing(app);
        assertEquals(1, showingWindows.size());
        return showingWindows.get(0);
    }
    
    private void nextSectionAndInvoke(Executable executable) throws Exception {
        invokeLaterAndWait(new Executable() {
            public void execute() {
                model.nextSection();
            }
        }, executable);
    }
    
    public void testNextSection() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                builderHandler = model.getBuilderHandler();
                
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(1, builderHandler.getRendererHandlers().size());
                assertEquals(0, builderHandler.getRendererHandlerIndex());
                
                model.nextSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(2, builderHandler.getRendererHandlers().size());
                assertEquals(1, builderHandler.getRendererHandlerIndex());
                
                model.nextSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(3, builderHandler.getRendererHandlers().size());
                assertEquals(2, builderHandler.getRendererHandlerIndex());
                
                model.previousSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(3, builderHandler.getRendererHandlers().size());
                assertEquals(1, builderHandler.getRendererHandlerIndex());
                
                model.nextSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(3, builderHandler.getRendererHandlers().size());
                assertEquals(2, builderHandler.getRendererHandlerIndex());
                
                model.previousSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(3, builderHandler.getRendererHandlers().size());
                assertEquals(1, builderHandler.getRendererHandlerIndex());
                
                model.previousSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(3, builderHandler.getRendererHandlers().size());
                assertEquals(0, builderHandler.getRendererHandlerIndex());
                
                model.nextSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(3, builderHandler.getRendererHandlers().size());
                assertEquals(1, builderHandler.getRendererHandlerIndex());
                
                model.previousSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(3, builderHandler.getRendererHandlers().size());
                assertEquals(0, builderHandler.getRendererHandlerIndex());
                
                model.nextSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(3, builderHandler.getRendererHandlers().size());
                assertEquals(1, builderHandler.getRendererHandlerIndex());
                
                model.nextSection();
                assertEquals(3, builderHandler.getSectionPresModels().size());
                assertEquals(3, builderHandler.getRendererHandlers().size());
                assertEquals(2, builderHandler.getRendererHandlerIndex());
            }
        });
    }
    
    public void testNextSectionWithMessagesAndPendingDocStatus() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                TextEntry entry = factory.createTextEntry("Entry");
                entry.setSection(firstSection);
                doc.addEntry(entry);
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.PENDING));
                BasicResponse response = entry.generateInstance(firstSectionOcc);
                ITextValue value = entry.generateValue();
                value.setValue("SomeText");
                response.setValue(value);
                docInstance.addResponse(response);
                response.setStatus(ResponseStatus.FLAGGED_INVALID);
                response.setAnnotation("Some error");
                ITextValue anotherValue = entry.generateValue();
                anotherValue.setValue("SomeText2");
                response.setValue(anotherValue, "Fix");
                response.setStatus(ResponseStatus.FLAGGED_EDITED);
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                builderHandler = app.getModel().getBuilderHandler();
                RendererHandler rHandler = builderHandler.getCurrentRendererHandler();
                
                BasicRenderer<?> renderer = (BasicRenderer<?>) rHandler.getExistingRenderer(entry, 0);
                assertEquals(ValidationResultViewFactory.getInfoIcon(), renderer.getValidationLabel().getIcon());
                
                assertEquals(0, builderHandler.getRendererHandlerIndex());
                boolean valid = app.getModel().validateSection(false).isEmpty();
                
                assertEquals(false, valid);
                
            }
        });
    }
    
    public void testNextSectionWithMessagesAndRejectedDocStatus() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                TextEntry entry = factory.createTextEntry("Entry");
                entry.setSection(firstSection);
                doc.addEntry(entry);
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.REJECTED));
                BasicResponse response = entry.generateInstance(firstSectionOcc);
                ITextValue value = entry.generateValue();
                value.setValue("SomeText");
                response.setValue(value);
                docInstance.addResponse(response);
                response.setStatus(ResponseStatus.FLAGGED_INVALID);
                response.setAnnotation("Some error");
                ITextValue anotherValue = entry.generateValue();
                anotherValue.setValue("SomeText2");
                response.setValue(anotherValue, "Fix");
                response.setStatus(ResponseStatus.FLAGGED_EDITED);
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                builderHandler = app.getModel().getBuilderHandler();
                RendererHandler rHandler = builderHandler.getCurrentRendererHandler();
                
                BasicRenderer<?> renderer = (BasicRenderer<?>) rHandler.getExistingRenderer(entry, 0);
                assertEquals(ValidationResultViewFactory.getInfoIcon(), renderer.getValidationLabel().getIcon());
                
                assertEquals(0, builderHandler.getRendererHandlerIndex());
                boolean valid = app.getModel().validateSection(false).isEmpty();
                
                assertEquals(true, valid);
                
            }
        });
    }
    
    public void testNextSectionWithWarningsAndPendingDocStatus() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                TextEntry entry = factory.createTextEntry("Entry");
                entry.setSection(firstSection);
                doc.addEntry(entry);
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.PENDING));
                BasicResponse response = entry.generateInstance(firstSectionOcc);
                ITextValue value = entry.generateValue();
                value.setValue("SomeText");
                response.setValue(value);
                docInstance.addResponse(response);
                response.setStatus(ResponseStatus.FLAGGED_INVALID);
                response.setAnnotation("Some error");
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                builderHandler = app.getModel().getBuilderHandler();
                RendererHandler rHandler = builderHandler.getCurrentRendererHandler();
                
                BasicRenderer<?> renderer = (BasicRenderer<?>) rHandler.getExistingRenderer(entry, 0);
                assertEquals(ValidationResultViewFactory.getWarningIcon(), renderer.getValidationLabel().getIcon());
                
                assertEquals(0, builderHandler.getRendererHandlerIndex());
                app.getModel().nextSection();
                
                assertEquals(1, builderHandler.getRendererHandlerIndex());
                
            }
        });
    }
    
    public void testNextSectionWithWarningsAndRejectedDocStatus() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                TextEntry entry = factory.createTextEntry("Entry");
                entry.setSection(firstSection);
                doc.addEntry(entry);
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.REJECTED));
                BasicResponse response = entry.generateInstance(firstSectionOcc);
                ITextValue value = entry.generateValue();
                value.setValue("SomeText");
                response.setValue(value);
                docInstance.addResponse(response);
                response.setStatus(ResponseStatus.FLAGGED_INVALID);
                response.setAnnotation("Some error");
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                builderHandler = app.getModel().getBuilderHandler();
                RendererHandler rHandler = builderHandler.getCurrentRendererHandler();
                
                BasicRenderer<?> renderer = (BasicRenderer<?>) rHandler.getExistingRenderer(entry, 0);
                assertEquals(ValidationResultViewFactory.getWarningIcon(), renderer.getValidationLabel().getIcon());
                
                assertEquals(0, builderHandler.getRendererHandlerIndex());
                boolean valid = app.getModel().validateSection(false).isEmpty();
                
                assertEquals(false, valid);
                
            }
        });
    }
    
    public void testNextSectionWithWarningsAndApprovedDocStatus() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                TextEntry entry = factory.createTextEntry("Entry");
                entry.setSection(firstSection);
                doc.addEntry(entry);
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.APPROVED));
                BasicResponse response = entry.generateInstance(firstSectionOcc);
                ITextValue value = entry.generateValue();
                value.setValue("SomeText");
                response.setValue(value);
                docInstance.addResponse(response);
                response.setStatus(ResponseStatus.FLAGGED_INVALID);
                response.setAnnotation("Some error");
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                builderHandler = app.getModel().getBuilderHandler();
                RendererHandler rHandler = builderHandler.getCurrentRendererHandler();
                
                BasicRenderer<?> renderer = (BasicRenderer<?>) rHandler.getExistingRenderer(entry, 0);
                assertEquals(ValidationResultViewFactory.getWarningIcon(), renderer.getValidationLabel().getIcon());
                
                assertEquals(0, builderHandler.getRendererHandlerIndex());
                boolean valid = app.getModel().validateSection(false).isEmpty();
                
                assertEquals(false, valid);
                
            }
        });
    }
    
    public void testNextSectionWithWarningsAndIncompleteDocStatus() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                TextEntry entry = factory.createTextEntry("Entry");
                entry.setSection(firstSection);
                doc.addEntry(entry);
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.INCOMPLETE));
                BasicResponse response = entry.generateInstance(firstSectionOcc);
                ITextValue value = entry.generateValue();
                value.setValue("SomeText");
                response.setValue(value);
                docInstance.addResponse(response);
                response.setStatus(ResponseStatus.FLAGGED_INVALID);
                response.setAnnotation("Some error");
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                builderHandler = app.getModel().getBuilderHandler();
                RendererHandler rHandler = builderHandler.getCurrentRendererHandler();
                
                BasicRenderer<?> renderer = (BasicRenderer<?>) rHandler.getExistingRenderer(entry, 0);
                assertEquals(ValidationResultViewFactory.getWarningIcon(), renderer.getValidationLabel().getIcon());
                
                assertEquals(0, builderHandler.getRendererHandlerIndex());
                boolean valid = app.getModel().validateSection(false).isEmpty();
                
                assertEquals(false, valid);
                
            }
        });
    }
    
    public void testUnsetDisabledPresModelsFromDocInstance() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                TextEntry enabledEntry1 = factory.createTextEntry("Enabled Entry 1");
                enabledEntry1.setSection(firstSection);
                doc.addEntry(enabledEntry1);
                
                TextEntry disabledEntry1 = factory.createTextEntry("Disabled Entry 1",
                        EntryStatus.DISABLED);
                disabledEntry1.setSection(firstSection);
                doc.addEntry(disabledEntry1);
                
                TextEntry enabledEntry2 = factory.createTextEntry("Enabled Entry 2");
                enabledEntry2.setSection(secondSection);
                doc.addEntry(enabledEntry2);
                
                TextEntry disabledEntry2 = factory.createTextEntry("Disabled Entry 2",
                        EntryStatus.DISABLED);
                disabledEntry2.setSection(secondSection);
                doc.addEntry(disabledEntry2);
                
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                
                RendererHandler rendererHandler1 = app.getModel().getCurrentRendererHandler();
                
                EntryComponent enabledField1 = getFieldAsEntryComponent(rendererHandler1, enabledEntry1, 0);
                String enabledText1 = "enabledField1";
                enabledField1.getTextComponent().setText(enabledText1);
                
                EntryComponent disabledField1 = getFieldAsEntryComponent(rendererHandler1, disabledEntry1, 0);
                String disabledText1 = "disabledField1";
                disabledField1.getTextComponent().setText(disabledText1);
                
                app.getModel().nextSection();
                RendererHandler rendererHandler2 = app.getModel().getCurrentRendererHandler();
                
                EntryComponent enabledField2 = getFieldAsEntryComponent(rendererHandler2, enabledEntry2, 0);
                String enabledText2 = "enabledField2";
                enabledField2.getTextComponent().setText(enabledText2);
                
                EntryComponent disabledField2 = getFieldAsEntryComponent(rendererHandler2, disabledEntry2, 0);
                String disabledText2 = "disabledField2";
                disabledField2.getTextComponent().setText(disabledText2);
                
                app.getModel().unsetDisabledPresModelsFromDocInstance();
                
                BasicRenderer<?> enabledRenderer1 = getExistingBasicRenderer(
                        rendererHandler1, enabledEntry1);
                Object enabledValue1 = enabledRenderer1.getPresModel().getValueModel().getValue();
                assertEquals(enabledText1, enabledValue1);
                
                BasicRenderer<?> disabledRenderer1 = getExistingBasicRenderer(
                        rendererHandler1, disabledEntry1);
                Object disabledValue1 = disabledRenderer1.getPresModel().getValueModel().getValue();
                assertNull("Disabled value 1 must be null", disabledValue1);
                
                BasicRenderer<?> enabledRenderer2 = getExistingBasicRenderer(
                        rendererHandler2, enabledEntry2);
                Object enabledValue2 = enabledRenderer2.getPresModel().getValueModel().getValue();
                assertEquals(enabledText2, enabledValue2);
                
                BasicRenderer<?> disabledRenderer2 = getExistingBasicRenderer(
                        rendererHandler2, disabledEntry2);
                Object disabledValue2 = disabledRenderer2.getPresModel().getValueModel().getValue();
                assertNull("Disabled value 2 must be null", disabledValue2);
                
            }
        });
    }
    
    private BasicRenderer<?> getExistingBasicRenderer(RendererHandler rendererHandler,
            BasicEntry entry)  {
        return BasicRenderer.class.cast(rendererHandler.getExistingRenderer(entry, 0));
    }
}
