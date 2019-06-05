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
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.action.PartialDateModeAction;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.MonthsComboBoxModel;
import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;
import org.psygrid.collection.entry.ui.DatePicker;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.collection.entry.ui.EntryWithButton;
import org.psygrid.collection.entry.validation.Messages;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.view.ValidationResultViewFactory;

@SuppressWarnings("nls")
public class DateRendererTest extends AbstractEntryTestCase {

    private JLabel validationLabel;
    private DatePicker datePicker;
    private DateEntry entry;
    private RendererHandler rendererHandler;
    private Section section;
    private SectionOccurrence sectionOcc;
    private Application application;
    private DocumentOccurrence docOcc;
    private DocumentInstance docInstance;
    
    private void init() throws Exception {
        Factory factory = getFactory();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        docOcc = getDocumentOccurrence();
        Document doc = docOcc.getDocument();
        docInstance = doc.generateInstance(docOcc);
        dataSet.addDocument(doc);
        record.addDocumentInstance(docInstance);
        sectionOcc = getSectionOccurrence();
        section = sectionOcc.getSection();
        doc.addSection(section);
        BuilderHandler builderHandler = getBuilderHandler(docInstance, 
                sectionOcc);
        rendererHandler = builderHandler.getCurrentRendererHandler();
        entry = factory.createDateEntry("Name", "Name");
        entry.setSection(section);
        doc.addEntry(entry);
        application = createApplication();
    }
    
    private void init2() {
        DateRendererSPI tRenderer = new DateRendererSPI();
        BasicRenderer<?> renderer = (BasicRenderer<?>) tRenderer.getRenderer(
                new RendererData(rendererHandler, entry, null, 0, null, false,
                        EditableStatus.DEFAULT));
        datePicker = (DatePicker) renderer.getField();
        validationLabel = renderer.getValidationLabel();
    }
    
    public void testGetRendererSPI() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                init2();
                RendererSPI rspi = rendererHandler.getRendererSPI(entry, null);
                assertTrue(rspi instanceof DateRendererSPI);
            }
        });
    }

    public void testGetRenderer() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                init2();
                BasicRenderer<?> renderer = (BasicRenderer<?>) rendererHandler
                        .getRenderer(entry, null);
                assertTrue(renderer.getField() != null);
                EntryLabel label = (EntryLabel) renderer.getLabel();
                assertEquals(entry.getDisplayText(), label.getText());
                assertTrue(renderer.getPresModel() != null);
                assertEquals(NUM_STANDARD_RENDERER_COMPONENTS + 1,
                        renderer.getComponents().size());
                assertEquals(1, rendererHandler.getNumExistingRenderers());
            }
        });
    }

    public void testInvalidInput() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                init2();
                checkInvalidInput();
            }
        });
    }
    
    private void checkInvalidInput() {
        datePicker.getTextComponent().setText("12998");
        assertEquals(ValidationResultViewFactory.getErrorIcon(),
                validationLabel.getIcon());
        assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
    }
    
    private String getDateInvalidMessage() {
        return "<html>" + Messages.getString("DateEntryValidationHandler.valueNotDate") + "</html>"; //$NON-NLS-1$
    }

    public void testValidInput() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                init2();
                checkValidInput();
            }
        });
    }
    
    public void testYearWithWrongLength() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                init2();
                datePicker.getTextComponent().setText("01-JUN-80");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
                datePicker.getTextComponent().setText("01-JUN-1980");
                
                datePicker.getTextComponent().setText("01-JUN-80");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
                datePicker.getTextComponent().setText("01-Jun-1980");
                
                datePicker.getTextComponent().setText("01-Jun-8");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
            }
        });
    }
    
    public void testDayWithWrongLength() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                init2();
                datePicker.getTextComponent().setText("1-JUN-1980");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
                datePicker.getTextComponent().setText("01-JUN-1980");
                
                datePicker.getTextComponent().setText("101-JUN-1980");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
                datePicker.getTextComponent().setText("01-Jun-1980");
            }
        });
    }
    
    public void testMalformedDates() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                init2();
                datePicker.getTextComponent().setText("1/JUN/19");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
                datePicker.getTextComponent().setText("01-JUN-1980");
                
                datePicker.getTextComponent().setText("011980");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
                datePicker.getTextComponent().setText("01-JUN-1980");
                
                datePicker.getTextComponent().setText("--");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
                datePicker.getTextComponent().setText("01-JUN-1980");
                
                datePicker.getTextComponent().setText("01--1980");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
                datePicker.getTextComponent().setText("01-JUN-1980");
                
                datePicker.getTextComponent().setText("JUN-1980");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
                
                datePicker.getTextComponent().setText("19-JUN-");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
                
                datePicker.getTextComponent().setText("31-JUN-2005");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        validationLabel.getIcon());
                assertEquals(getDateInvalidMessage(), validationLabel.getToolTipText());
            }
        });
    }
    
    private void checkValidInput() {
        datePicker.getTextComponent().setText("12-JUN-1982");
        assertEquals(null, validationLabel.getToolTipText());
        assertEquals(null, validationLabel.getIcon());
    }

    public void testValidInputAfterInvalid() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                init2();
                checkValidInput();
                checkInvalidInput();
                checkValidInput();
            }
        });
    }
    
    public void testDateEntryStatusRejectedDocument() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                Document doc = docOcc.getDocument();
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.REJECTED));
                BasicResponse response = entry.generateInstance(sectionOcc);
                IDateValue value = entry.generateValue();
                value.setValue(new Date());
                response.setValue(value);
                docInstance.addResponse(response);
                response.setStatus(ResponseStatus.FLAGGED_INVALID);
                response.setAnnotation("Some error");
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                BuilderHandler bHandler = application.getModel().getBuilderHandler();
                RendererHandler rHandler = bHandler.getCurrentRendererHandler();
                DatePicker picker = (DatePicker) getFieldAsEntryComponent(rHandler, entry, 0);
                assertEquals(true, picker.isEnabled());
                assertEquals(false, picker.isEditable());
                
                assertEquals(true, picker.getMonthsComboBox().isEnabled());
                assertEquals(false, picker.getMonthsComboBox().isEditable());
                
                assertEquals(true, picker.getYearTextField().isEnabled());
                assertEquals(false, picker.getYearTextField().isEditable());
                
                assertEquals(false, picker.getPopupButton().isEnabled());
                
                assertEquals(true, picker.getTextComponent().isEnabled());
                assertEquals(false, picker.getTextComponent().isEditable());
                
                assertEquals(true, picker.getBasicDatePicker().isEnabled());
                assertEquals(false, picker.getBasicDatePicker().isEditable());
                assertEquals(false, picker.getBasicDatePicker().getPopupButton().isEnabled());
                
            }
        });               
    }
    
    public void testDateEntryStatusApprovedDocument() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                Document doc = docOcc.getDocument();
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.APPROVED));
                BasicResponse response = entry.generateInstance(sectionOcc);
                IDateValue value = entry.generateValue();
                value.setValue(new Date());
                response.setValue(value);
                docInstance.addResponse(response);
                response.setStatus(ResponseStatus.FLAGGED_INVALID);
                response.setAnnotation("Some error");
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                BuilderHandler bHandler = application.getModel().getBuilderHandler();
                RendererHandler rHandler = bHandler.getCurrentRendererHandler();
                DatePicker picker = (DatePicker) getFieldAsEntryComponent(rHandler, entry, 0);
                assertEquals(true, picker.isEnabled());
                assertEquals(false, picker.isEditable());
                
                assertEquals(true, picker.getMonthsComboBox().isEnabled());
                assertEquals(false, picker.getMonthsComboBox().isEditable());
                
                assertEquals(true, picker.getYearTextField().isEnabled());
                assertEquals(false, picker.getYearTextField().isEditable());
                
                assertEquals(false, picker.getPopupButton().isEnabled());
                
                assertEquals(true, picker.getTextComponent().isEnabled());
                assertEquals(false, picker.getTextComponent().isEditable());
                
                assertEquals(true, picker.getBasicDatePicker().isEnabled());
                assertEquals(false, picker.getBasicDatePicker().isEditable());
                assertEquals(false, picker.getBasicDatePicker().getPopupButton().isEnabled());
                
            }
        });               
    }
    
    public void testDateEntryStatusPendingDocument() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                Document doc = docOcc.getDocument();
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.PENDING));
                BasicResponse response = entry.generateInstance(sectionOcc);
                IDateValue value = entry.generateValue();
                value.setValue(new Date());
                response.setValue(value);
                docInstance.addResponse(response);
                response.setStatus(ResponseStatus.FLAGGED_INVALID);
                response.setAnnotation("Some error");
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                BuilderHandler bHandler = application.getModel().getBuilderHandler();
                RendererHandler rHandler = bHandler.getCurrentRendererHandler();
                DatePicker picker = (DatePicker) getFieldAsEntryComponent(rHandler, entry, 0);
                assertEquals(true, picker.isEnabled());
                assertEquals(false, picker.isEditable());
                
                assertEquals(true, picker.getMonthsComboBox().isEnabled());
                assertEquals(false, picker.getMonthsComboBox().isEditable());
                
                assertEquals(true, picker.getYearTextField().isEnabled());
                assertEquals(false, picker.getYearTextField().isEditable());
                
                assertEquals(false, picker.getPopupButton().isEnabled());
                
                assertEquals(true, picker.getTextComponent().isEnabled());
                assertEquals(false, picker.getTextComponent().isEditable());
                
                assertEquals(true, picker.getBasicDatePicker().isEnabled());
                assertEquals(false, picker.getBasicDatePicker().isEditable());
                assertEquals(false, picker.getBasicDatePicker().getPopupButton().isEnabled());
                
            }
        });               
    }
    
    public void testDateEntryStatusIncompleteDocument() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                Document doc = docOcc.getDocument();
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.INCOMPLETE));
                BasicResponse response = entry.generateInstance(sectionOcc);
                IDateValue value = entry.generateValue();
                value.setValue(new Date());
                response.setValue(value);
                docInstance.addResponse(response);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                BuilderHandler bHandler = application.getModel().getBuilderHandler();
                RendererHandler rHandler = bHandler.getCurrentRendererHandler();
                DatePicker picker = (DatePicker) getFieldAsEntryComponent(rHandler, entry, 0);
                assertEquals(true, picker.isEnabled());
                assertEquals(true, picker.isEditable());
                
                assertEquals(true, picker.getMonthsComboBox().isEnabled());
                assertEquals(true, picker.getMonthsComboBox().isEditable());
                
                assertEquals(true, picker.getYearTextField().isEnabled());
                assertEquals(true, picker.getYearTextField().isEditable());
                
                assertEquals(true, picker.getPopupButton().isEnabled());
                
                assertEquals(true, picker.getTextComponent().isEnabled());
                assertEquals(true, picker.getTextComponent().isEditable());
                
                assertEquals(true, picker.getBasicDatePicker().isEnabled());
                assertEquals(true, picker.getBasicDatePicker().isEditable());
                assertEquals(true, picker.getBasicDatePicker().getPopupButton().isEnabled());
                
            }
        });               
    }
    
    public void testTransformedDateEntryStatusIncompleteDocument() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                Document doc = docOcc.getDocument();
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.INCOMPLETE));
                BasicResponse response = entry.generateInstance(sectionOcc);
                IDateValue value = entry.generateValue();
                value.setValue(new Date());
                setTransformed(value, true);
                response.setValue(value);
                docInstance.addResponse(response);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                BuilderHandler bHandler = application.getModel().getBuilderHandler();
                RendererHandler rHandler = bHandler.getCurrentRendererHandler();
                DatePicker picker = (DatePicker) getFieldAsEntryComponent(rHandler, entry, 0);
                assertEquals(true, picker.isEnabled());
                assertEquals(false, picker.isEditable());
                
                assertEquals(true, picker.getMonthsComboBox().isEnabled());
                assertEquals(false, picker.getMonthsComboBox().isEditable());
                
                assertEquals(true, picker.getYearTextField().isEnabled());
                assertEquals(false, picker.getYearTextField().isEditable());
                
                assertEquals(false, picker.getPopupButton().isEnabled());
                
                assertEquals(true, picker.getTextComponent().isEnabled());
                assertEquals(false, picker.getTextComponent().isEditable());
                
                assertEquals(true, picker.getBasicDatePicker().isEnabled());
                assertEquals(false, picker.getBasicDatePicker().isEditable());
                assertEquals(false, picker.getBasicDatePicker().getPopupButton().isEnabled());
                
            }
        });               
    }
    
    public void testTransformedDateEntryWarningIcon() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                Document doc = docOcc.getDocument();
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.REJECTED));
                BasicResponse response = entry.generateInstance(sectionOcc);
                IDateValue value = entry.generateValue();
                value.setValue(new Date());
                setTransformed(value, true);
                response.setValue(value);
                response.setStatus(ResponseStatus.FLAGGED_INVALID);
                String errorMsg = "Some error";
                response.setAnnotation(errorMsg);
                docInstance.addResponse(response);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                BuilderHandler bHandler = application.getModel().getBuilderHandler();
                RendererHandler rHandler = bHandler.getCurrentRendererHandler();
                BasicRenderer<?> renderer = BasicRenderer.class.cast(
                        rHandler.getExistingRenderer(entry, 0));
                JLabel vLabel = renderer.getValidationLabel();
                assertEquals(ValidationResultViewFactory.getWarningIcon(), vLabel.getIcon());
                BasicPresModel presModel = renderer.getPresModel();
                ValidationResult vResult = presModel.getValidationModel().getResult();
                assertEquals(errorMsg, vResult.getMessagesText());
                presModel.performValidation(false);
                vLabel = renderer.getValidationLabel();
                assertEquals(ValidationResultViewFactory.getWarningIcon(), vLabel.getIcon());
                presModel = renderer.getPresModel();
                vResult = presModel.getValidationModel().getResult();
                assertEquals(errorMsg, vResult.getMessagesText());
            }
        });               
    }
    
    public void testStandardCodesAtStart() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                BasicResponse response = entry.generateInstance(sectionOcc);
                IDateValue value = entry.generateValue();
                List<StandardCode> standardCodes = 
                    getStandardCodesGetter(application.getModel()).getStandardCodes();
                StandardCode selectedStdCode = standardCodes.get(0);
                value.setStandardCode(selectedStdCode);
                response.setValue(value);
                docInstance.addResponse(response);
                
                application.setVisible(true);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                
                rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                datePicker = (DatePicker) getFieldAsEntryComponent(rendererHandler, entry, 0);
                
                assertEquals(false, datePicker.getTextComponent().isEnabled());
                assertEquals(true, datePicker.getPopupButton().isEnabled());
                assertEquals(false, datePicker.getBasicDatePicker().getPopupButton().isEnabled());
                String stdCodeText = RendererHelper.getInstance().getStandardCodeText(selectedStdCode);
                
                assertEquals(stdCodeText, datePicker.getTextComponent().getText());
                             
            }
        });
    }
    
    public void testPartialModeAtStart() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                BasicResponse response = entry.generateInstance(sectionOcc);
                IDateValue value = entry.generateValue();
                int monthIndex = 10;
                value.setMonth(Integer.valueOf(monthIndex));
                int year = 1995;
                value.setYear(Integer.valueOf(year));
                response.setValue(value);
                docInstance.addResponse(response);
                
                application.setVisible(true);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                
                rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                datePicker = (DatePicker) getFieldAsEntryComponent(rendererHandler, entry, 0);
                
                assertEquals(true, datePicker.isPartialDateMode());
                MonthsComboBoxModel cBoxModel = 
                    (MonthsComboBoxModel) datePicker.getMonthsComboBox().getModel();
                assertEquals(cBoxModel.getValues()[monthIndex], 
                        datePicker.getMonthsComboBox().getSelectedItem());
                assertEquals(String.valueOf(year), datePicker.getYearTextField().getText());
            }
        });
    }
    
    public void testPartialModeAction() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                application.setVisible(true);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                
                rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                datePicker = (DatePicker) getFieldAsEntryComponent(rendererHandler, entry, 0);
                
                assertEquals(false, datePicker.isPartialDateMode());
                performPartialModeAction(datePicker);
                assertEquals(true, datePicker.isPartialDateMode());
                performPartialModeAction(datePicker);
                assertEquals(false, datePicker.isPartialDateMode());
                performPartialModeAction(datePicker);
                assertEquals(true, datePicker.isPartialDateMode());
            }
        });
    }
    
    private void performPartialModeAction(EntryWithButton field) {
        for (Component comp : field.getPopup().getComponents()) {
            if (comp instanceof JMenuItem) {
                JMenuItem menuItem = (JMenuItem) comp;
                Action action = menuItem.getAction();
                if (action instanceof PartialDateModeAction) {
                    action.actionPerformed(new ActionEvent(this,
                                ActionEvent.ACTION_PERFORMED, ""));
                }
            }
        }
    }
    
    public void testPartialModeWithYearOnly() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                BasicResponse response = entry.generateInstance(sectionOcc);
                IDateValue value = entry.generateValue();
                int year = 1995;
                value.setYear(Integer.valueOf(year));
                response.setValue(value);
                docInstance.addResponse(response);
                
                application.setVisible(true);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                
                rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                datePicker = (DatePicker) getFieldAsEntryComponent(rendererHandler, entry, 0);
                
                assertEquals(true, datePicker.isPartialDateMode());
                MonthsComboBoxModel cBoxModel = 
                    (MonthsComboBoxModel) datePicker.getMonthsComboBox().getModel();
                assertNull(cBoxModel.getMonthValueModel().getValue());
                assertEquals(String.valueOf(year), datePicker.getYearTextField().getText());
                
                performPartialModeAction(datePicker);
                
                assertEquals(false, datePicker.isPartialDateMode());
                
                performPartialModeAction(datePicker);
                
                assertEquals(true, datePicker.isPartialDateMode());
                assertNull(cBoxModel.getMonthValueModel().getValue());
                assertEquals("", datePicker.getYearTextField().getText());
                
                performPartialModeAction(datePicker);
                
                assertEquals(false, datePicker.isPartialDateMode());
                datePicker.getTextComponent().setText("12-Jul-1995");
                assertEquals(false, datePicker.isPartialDateMode());
            }
        });
    }
    
    //TODO Implement the rest of the test
    public void testActions() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                Document doc = docOcc.getDocument();
                setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.REJECTED));
                BasicResponse response = entry.generateInstance(sectionOcc);
                IDateValue value = entry.generateValue();
                value.setValue(new Date());
                response.setValue(value);
                docInstance.addResponse(response);
                response.setStatus(ResponseStatus.FLAGGED_INVALID);
                response.setAnnotation("Some error");
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
            }
        });               
    }
    
    public static void main(String[] args) throws Exception {
        DateRendererTest test = new DateRendererTest();
        test.testPartialModeWithYearOnly();
    }
}
