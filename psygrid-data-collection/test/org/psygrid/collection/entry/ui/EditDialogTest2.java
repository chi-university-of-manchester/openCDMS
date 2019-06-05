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

import java.util.Date;

import javax.swing.JTextField;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.renderer.BasicRenderer;
import org.psygrid.collection.entry.renderer.RendererHandler;

import com.jgoodies.validation.view.ValidationResultViewFactory;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class EditDialogTest2 extends AbstractEntryTestCase {
    
    private Application app;
    private Factory factory;
    private Document doc;
    private DocumentInstance docInstance;
    private RendererHandler rendererHandler;
    private TextEntry textEntry;
    private DateEntry dateEntry;
    private DateEntry partialDateEntry;
    
    public static void main(String[] args) throws Exception {
        new EditDialogTest2().testValidationEmptyTextRenderer();
    }
    
    public EditDialogTest2() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                initLauncher();
            }
        });
    }
    
    private void init() throws Exception    {
        factory = getFactory();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        createDocument();
        dataSet.addDocument(doc);
        setStatus(docInstance, DocumentStatus.toIStatus(doc, DocumentStatus.REJECTED));
        record.addDocumentInstance(docInstance);

        app = createApplication();
        app.setSelectedDocOccurrenceInstance(docInstance, 0);
        app.setVisible(true);
        rendererHandler = app.getModel().getCurrentRendererHandler();
    }
    
    public void testValidationEmptyTextRenderer() throws Exception {
        invokeInitAndWait();
        launchEditDialogBox(getEditDialogLauncherHolder(rendererHandler, textEntry),
                new Executable() {
            public void execute() throws Exception {
                EditDialog editDialog = (EditDialog) getOwnedWindowsShowing(app).get(0);
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
    
    public void testValidationEmptyYearPartialDate() throws Exception   {
        invokeInitAndWait();
        launchEditDialogBox(getEditDialogLauncherHolder(rendererHandler, partialDateEntry),
                new Executable() {
            public void execute() throws Exception {
                EditDialog editDialog = (EditDialog) getOwnedWindowsShowing(app).get(0);
                DatePicker datePicker = getComponent(DatePicker.class, editDialog,
                        null, null);
                BasicRenderer<?> renderer = editDialog.getCopyRenderer();
                assertNull(renderer.getValidationLabel().getIcon());
                datePicker.getYearTextField().setText("");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        renderer.getValidationLabel().getIcon());
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
    
    public void testValidationEmptyDateRenderer() throws Exception  {
        invokeInitAndWait();
        launchEditDialogBox(getEditDialogLauncherHolder(rendererHandler,dateEntry),
                new Executable() {
            public void execute() throws Exception {
                EditDialog editDialog = (EditDialog) getOwnedWindowsShowing(app).get(0);
                DatePicker datePicker = getComponent(DatePicker.class, editDialog,
                        null, null);
                BasicRenderer<?> renderer = editDialog.getCopyRenderer();
                assertNull(renderer.getValidationLabel().getIcon());
                datePicker.getTextComponent().setText("");
                assertEquals(ValidationResultViewFactory.getErrorIcon(),
                        renderer.getValidationLabel().getIcon());
            }
        });
    }
    
    private void createOptionDependent(Option option, Entry dependentEntry) {
        createOptionDependent(option, 
                dependentEntry, EntryStatus.MANDATORY);
    }
    
    private void createOptionDependent(Option option,
            Entry dependentEntry,
            EntryStatus status) {
        
        OptionDependent optDep = factory.createOptionDependent();
        optDep.setEntryStatus(status);
        option.addOptionDependent(optDep);
        optDep.setDependentEntry(dependentEntry);
    }

    private void createDocument() {
        
        doc = factory.createDocument("Baseline Audit", 
                "Baseline Audit Form (Information obtained from case notes)");
        
        createDocumentStatuses(doc);
        
        DocumentOccurrence docOcc = factory.createDocumentOccurrence("Occurrence");
        doc.addOccurrence(docOcc);
        docInstance = doc.generateInstance(docOcc);
        
        Section mainSec = factory.createSection("Main", "Main");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence sectionOcc = factory.createSectionOccurrence("Main");
        mainSec.addOccurrence(sectionOcc);
        
        OptionEntry optionEntry = factory.createOptionEntry("Question", "Question");
        doc.addEntry(optionEntry);
        optionEntry.setSection(mainSec);
        Option  notContinueNo = factory.createOption("No",  "No (Continue to the next question)", 0 );
        optionEntry.addOption(notContinueNo);
        Option  notContinueYes = factory.createOption("Yes",  "Yes (Please do not continue)", 1 );
        optionEntry.addOption(notContinueYes);

        addOptionResponse(docInstance, sectionOcc, optionEntry, notContinueNo);
        
        partialDateEntry = factory.createDateEntry("Date first " +
                "contact with Mental Health Team", "Date first contact with " +
                "Mental Health Team", EntryStatus.DISABLED);
        doc.addEntry(partialDateEntry);
        partialDateEntry.setSection(mainSec);
        createOptionDependent(notContinueNo, partialDateEntry);
        
        addDateResponse(docInstance, sectionOcc, partialDateEntry,
                Integer.valueOf(10), Integer.valueOf(2002));
        
        dateEntry = factory.createDateEntry("Date screened", 
                "Date screened", EntryStatus.DISABLED);
        doc.addEntry(dateEntry);
        dateEntry.setSection(mainSec);
        createOptionDependent(notContinueNo, dateEntry);

        addDateResponse(docInstance, sectionOcc, dateEntry, new Date());
        
        textEntry = factory.createTextEntry("Name of Consultant", 
                "Name of Consultant", EntryStatus.DISABLED);
        doc.addEntry(textEntry);
        textEntry.setSection(mainSec);
        createOptionDependent(notContinueNo, textEntry);
        
        addTextResponse(docInstance, sectionOcc, textEntry, "John");
        
    }
}
