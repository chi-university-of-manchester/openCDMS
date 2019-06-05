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

import javax.swing.JButton;
import javax.swing.JTextField;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.renderer.BasicRenderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.ITextValue;

import com.jgoodies.validation.view.ValidationResultViewFactory;

@SuppressWarnings("nls")
public class EditDialogTest extends AbstractEntryTestCase   {

    private RendererHandler rendererHandler;
    private Section section;
    private SectionOccurrence sectionOcc;
    private DocumentOccurrence docOcc;
    
    private TextEntry textEntry;

    private DateEntry dateEntry;
    
    private NumericEntry numericEntry;
    
    private EditDialog editDialog;
    private DocumentInstance docInstance;
    private StandardCode standardCode;
    private Application app;
    
    protected void init() throws Exception {
        Factory factory = getFactory();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        docOcc = getDocumentOccurrence();
        Document doc = docOcc.getDocument();
        docInstance = doc.generateInstance(docOcc);
        setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.REJECTED));
        dataSet.addDocument(doc);
        record.addDocumentInstance(docInstance);
        sectionOcc = getSectionOccurrence();
        section = sectionOcc.getSection();
        doc.addSection(section);
        
        textEntry = factory.createTextEntry("Name", "Name");
        textEntry.setSection(section);
        doc.addEntry(textEntry);
        BasicResponse textResponse = textEntry.generateInstance(sectionOcc);
        ITextValue textValue = textEntry.generateValue();
        textValue.setValue("Some value");
        textResponse.setValue(textValue);
        docInstance.addResponse(textResponse);
        
        dateEntry = factory.createDateEntry("Date", "Date");
        dateEntry.setSection(section);
        doc.addEntry(dateEntry);
        BasicResponse dateResponse = dateEntry.generateInstance(sectionOcc);
        IDateValue dateValue = dateEntry.generateValue();
        dateValue.setMonth(Integer.valueOf(10));
        dateValue.setYear(Integer.valueOf(2000));
        dateResponse.setValue(dateValue);
        docInstance.addResponse(dateResponse);
        
        numericEntry = factory.createNumericEntry("Numeric", "Numeric");
        numericEntry.setSection(section);
        doc.addEntry(numericEntry);
        BasicResponse numericResponse = numericEntry.generateInstance(sectionOcc);
        INumericValue numericValue = numericEntry.generateValue();
        numericValue.setValue(Double.valueOf(40.0));
        numericResponse.setValue(numericValue);
        docInstance.addResponse(numericResponse);
        
        app = createApplication();
        app.setSelectedDocOccurrenceInstance(docInstance, 0);
        standardCode = app.getModel().getBuilderHandler().getStandardCodes().get(0);
        textValue.setStandardCode(standardCode);
        rendererHandler = app.getModel().getCurrentRendererHandler();
        app.setVisible(true);
    }
    
    protected void setEditDialogVisibleAndInvoke(final Entry entry,
            Executable executable) throws Exception {
        launchEditDialogBox(getEditDialogLauncherHolder(rendererHandler, entry),
                new Executable() {
            public void execute() throws Exception {
                editDialog = (EditDialog) getOwnedWindowsShowing(app).get(0);
            }
        });
        invokeAndWait(executable);
    }

    /**
     * Tests that starting the EditDialog containing a TextRenderer where the
     * TextValue has a StandardCode behaves correctly.
     * 
     * @throws Exception
     */
    public void testStandardCodeWithTextRenderer() throws Exception {
        invokeInitAndWait();
        setEditDialogVisibleAndInvoke(textEntry, new Executable() {
            public void execute() throws Exception {
                JTextField textField = getComponent(JTextField.class, editDialog,
                        null, null);
                assertEquals(false, textField.isEnabled());
                String expectedText = RendererHelper.getInstance().getStandardCodeText(
                        standardCode);
                assertEquals(expectedText, textField.getText());
                JButton button = getJButton(editDialog, "");
                assertEquals(true, button.isEnabled());
            }
        });
    }
    
    private void invokeInitAndWait() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
            }
        });
    }
    
    public void testPartialDate() throws Exception {
        invokeInitAndWait();
        setEditDialogVisibleAndInvoke(dateEntry, new Executable() {
            public void execute() throws Exception {
                DatePicker datePicker = getComponent(DatePicker.class, editDialog,
                        null, null);
                assertEquals(true, datePicker.isPartialDateMode());
            }
        });
    }
    
    public void testValidationMandatoryEntry() throws Exception {
        invokeInitAndWait();
        setEditDialogVisibleAndInvoke(numericEntry, new Executable() {
            public void execute() throws Exception {
                JTextField numericField = getComponent(JTextField.class, editDialog,
                        null, null);
                BasicRenderer<?> renderer = editDialog.getCopyRenderer();
                assertNull(renderer.getValidationLabel().getIcon());
                numericField.setText("");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        renderer.getValidationLabel().getIcon());
            }
        });
    }
    
    public static void main(String[] args) throws Exception {
        new EditDialogTest().testValidationMandatoryEntry();
    }
}