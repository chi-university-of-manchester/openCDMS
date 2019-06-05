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
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.ValueHolder;
import org.psygrid.collection.entry.model.OptionComboBoxModel;
import org.psygrid.collection.entry.model.OptionEditableComboBoxModel;
import org.psygrid.collection.entry.ui.DualTextField;
import org.psygrid.collection.entry.ui.EditDialog;
import org.psygrid.collection.entry.ui.EditableComboBox;
import org.psygrid.collection.entry.ui.TableTestHelper;
import org.psygrid.collection.entry.ui.VariableTable;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class CompositeOptionRendererTest extends AbstractEntryTestCase {

    private Section section;

    private Factory factory;

    private Application application;

    private DocumentOccurrence docOccurrence;

    private DocumentInstance docInstance;

    private Document document;

    private SectionOccurrence sectionOcc;
    
    private CompositeEntry compEntry;

    private CompositeResponse compResponse;
    
    public CompositeOptionRendererTest() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                initLauncher();
            }
        });
    }
    
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
        compEntry = factory.createComposite("Some composite", "Some composite");
        document.addEntry(compEntry);
        compEntry.setSection(section);
        compResponse = compEntry.generateInstance(sectionOcc);
        docInstance.addResponse(compResponse);
    }
    
    public void testSettingAndGetting() throws Exception {
        invokeAndWait(new Executable() {
           public void execute() throws Exception {
               init();
               OptionEntry optionEntry = factory.createOptionEntry("Skills",
                       "Skills");
               Option highOption = factory.createOption("High", "High");
               Option lowOption = factory.createOption("Low", "Low");
               Option otherOption = factory.createOption("Other", "Other");
               optionEntry.addOption(highOption);
               optionEntry.addOption(lowOption);
               optionEntry.addOption(otherOption);
               compEntry.addEntry(optionEntry);
               optionEntry.setSection(section);
               
               application.setSelectedDocOccurrenceInstance(docInstance, 0);
               application.setVisible(true);

               RendererHandler rendererHandler = application.getModel()
                       .getCurrentRendererHandler();
               
               CompositeRenderer<?> compRenderer = (CompositeRenderer<?>)
                   rendererHandler.getExistingRenderer(compEntry, 0);
               
               VariableTable entryTable = (VariableTable) compRenderer.getComposite();
               
               List<Map<JComponent,Boolean>> comps = TableTestHelper.getComps(entryTable);
               EditableComboBox cBox = (EditableComboBox) comps.get(0);
               cBox.setSelectedItem(highOption.getDisplayText());
               assertEquals(highOption.getDisplayText(), cBox.getSelectedItem());
               OptionComboBoxModel model = 
                   (OptionComboBoxModel) cBox.getModel();
               
               OptionComboBoxRenderer editor = 
                   (OptionComboBoxRenderer) cBox.getEditor();
               cBox.configureEditor(editor, cBox.getSelectedItem());
               JTextField field = 
                   (JTextField) editor.getEditorComponent();
               assertEquals(highOption.getDisplayText(), field.getText());
               assertEquals(highOption.getDisplayText(), model.getSelectedItem());
               
               cBox.setSelectedItem(otherOption.getDisplayText());
               cBox.configureEditor(editor, cBox.getSelectedItem());
               
               String selectedText = otherOption.getDisplayText();
               assertEquals(selectedText, field.getText());
               assertEquals(selectedText, model.getSelectedItem());
            }
        });
    }
    
    public void testWithDefaultValue() throws Exception {
        invokeAndWait(new Executable() {
           public void execute() throws Exception {
               init();
               OptionEntry optionEntry = factory.createOptionEntry("Skills",
                       "Skills");
               Option highOption = factory.createOption("High", "High");
               Option lowOption = factory.createOption("Low", "Low");
               Option otherOption = factory.createOption("Other", "Other");
               optionEntry.addOption(highOption);
               optionEntry.addOption(lowOption);
               optionEntry.addOption(otherOption);
               compEntry.addEntry(optionEntry);
               optionEntry.setSection(section);
               optionEntry.setDefaultValue(lowOption);
               
               application.setSelectedDocOccurrenceInstance(docInstance, 0);
               application.setVisible(true);

               RendererHandler rendererHandler = application.getModel()
                       .getCurrentRendererHandler();
               
               CompositeRenderer<?> compRenderer = (CompositeRenderer<?>)
                   rendererHandler.getExistingRenderer(compEntry, 0);
               
               VariableTable entryTable = (VariableTable) compRenderer.getComposite();
               
               List<Map<JComponent,Boolean>> comps = TableTestHelper.getComps(entryTable);
               EditableComboBox cBox = (EditableComboBox) comps.get(0);
               OptionComboBoxModel model = 
                   (OptionComboBoxModel) cBox.getModel();
               
               OptionComboBoxRenderer editor = 
                   (OptionComboBoxRenderer) cBox.getEditor();

               JTextField field = 
                   (JTextField) editor.getEditorComponent();

               assertEquals(lowOption.getDisplayText(), field.getText());
               assertEquals(lowOption.getDisplayText(), model.getSelectedItem());
               
               cBox.configureEditor(editor, cBox.getSelectedItem());
               
               cBox.setSelectedItem(otherOption.getDisplayText());
               cBox.configureEditor(editor, cBox.getSelectedItem());
               
               String selectedText = otherOption.getDisplayText();
               assertEquals(selectedText, field.getText());
               assertEquals(selectedText, model.getSelectedItem());
            }
        });
    }
    
    public void testAddingRow() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                OptionEntry optionEntry = factory.createOptionEntry("Skills",
                        "Skills");
                Option highOption = factory.createOption("High", "High");
                Option lowOption = factory.createOption("Low", "Low");
                Option otherOption = factory.createOption("Other", "Other");
                optionEntry.addOption(highOption);
                optionEntry.addOption(lowOption);
                optionEntry.addOption(otherOption);
                compEntry.addEntry(optionEntry);
                optionEntry.setSection(section);
                
                OptionEntry optionEntry2 = factory.createOptionEntry("Frequency",
                        "Frequency");
                Option oftenOption = factory.createOption("often", "often");
                Option rareOption = factory.createOption("rare", "rare");
                optionEntry2.addOption(oftenOption);
                optionEntry2.addOption(rareOption);
                compEntry.addEntry(optionEntry2);
                optionEntry2.setSection(section);
                
                NumericEntry numericEntry = factory.createNumericEntry("Numeric entry",
                        "Numeric entry");
                compEntry.addEntry(numericEntry);
                numericEntry.setSection(section);
                

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rendererHandler
                        .getExistingRenderer(compEntry, 0);

                VariableTable entryTable = (VariableTable) compRenderer
                        .getComposite();
                entryTable.getAddRowButton().doClick();
                entryTable.getAddRowButton().doClick();
            }
        });
    }
    
    public void testSettingAndGettingWithTextAllowed() throws Exception {
        invokeAndWait(new Executable() {
           public void execute() throws Exception {
               init();
               OptionEntry optionEntry = factory.createOptionEntry("Skills",
                       "Skills");
               optionEntry.setDescription("Choose your skill level");
               Option highOption = factory.createOption("High", "High");
               Option lowOption = factory.createOption("Low", "Low");
               Option otherOption = factory.createOption("Other", "Other");
               optionEntry.addOption(highOption);
               optionEntry.addOption(lowOption);
               optionEntry.addOption(otherOption);
               otherOption.setTextEntryAllowed(true);
               compEntry.addEntry(optionEntry);
               optionEntry.setSection(section);
               
               application.setSelectedDocOccurrenceInstance(docInstance, 0);
               application.setVisible(true);

               RendererHandler rendererHandler = application.getModel()
                       .getCurrentRendererHandler();
               
               CompositeRenderer<?> compRenderer = (CompositeRenderer<?>)
                   rendererHandler.getExistingRenderer(compEntry, 0);
               
               VariableTable entryTable = (VariableTable) compRenderer.getComposite();
               
               List<Map<JComponent,Boolean>> comps = TableTestHelper.getComps(entryTable);
               EditableComboBox cBox = (EditableComboBox) comps.get(0);
               cBox.setSelectedItem(highOption.getDisplayText());
               
               assertEquals(highOption.getDisplayText(), cBox.getSelectedItem());
               OptionEditableComboBoxModel model = 
                   (OptionEditableComboBoxModel) cBox.getModel();
               assertNull(model.getTextValueModel().getValue());
               
               OptionComboBoxEditor editor = 
                   (OptionComboBoxEditor) cBox.getEditor();
               cBox.configureEditor(editor, cBox.getSelectedItem());
               DualTextField field = 
                   (DualTextField) editor.getEditorComponent();
               assertEquals("", field.getRightFieldText());
               assertEquals(highOption.getDisplayText(), field.getLeftFieldText());
               
               cBox.setSelectedItem(otherOption.getDisplayText());
               cBox.configureEditor(editor, cBox.getSelectedItem());
               
               String selectedText = otherOption.getDisplayText();
               assertEquals(selectedText, cBox.getSelectedItem());
               assertNull(model.getTextValueModel().getValue());
               assertEquals("", field.getRightFieldText());
               assertEquals(selectedText, field.getLeftFieldText());
               
               String otherTextValue = "Somevalue";
               model.getTextValueModel().setValue(otherTextValue);
               
               assertEquals(selectedText, cBox.getSelectedItem());
               assertEquals(otherTextValue, field.getRightFieldText());
               assertEquals(selectedText, field.getLeftFieldText());
               
               String newOtherTextValue = "new";
               field.getRightField().setText(newOtherTextValue);
               
               assertEquals(selectedText, cBox.getSelectedItem());
               assertEquals(newOtherTextValue, model.getTextValueModel().getValue());
               assertEquals(selectedText, field.getLeftFieldText());
            }
        });
    }
    
    /**
     * Shows that a non-editable option where textEntryAllowed = true shows
     * the text associated with it.
     */
    public void testNonEditableTextAllowed() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                setStatus(docInstance, DocumentStatus.toIStatus(document,
                        DocumentStatus.PENDING));
                OptionEntry optionEntry = factory.createOptionEntry("Skills",
                        "Skills");
                optionEntry.setDescription("Choose your skill level");
                Option highOption = factory.createOption("High", "High");
                Option lowOption = factory.createOption("Low", "Low");
                Option otherOption = factory.createOption("Other", "Other");
                optionEntry.addOption(highOption);
                optionEntry.addOption(lowOption);
                optionEntry.addOption(otherOption);
                otherOption.setTextEntryAllowed(true);
                compEntry.addEntry(optionEntry);
                optionEntry.setSection(section);

                CompositeRow compRow = compResponse.createCompositeRow();
                BasicResponse response = optionEntry
                        .generateInstance(sectionOcc);
                compRow.addResponse(response);
                IOptionValue value = optionEntry.generateValue();
                value.setValue(otherOption);
                value.setTextValue("new");
                response.setValue(value);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rendererHandler
                        .getExistingRenderer(compEntry, 0);

                VariableTable entryTable = (VariableTable) compRenderer
                        .getComposite();

                List<Map<JComponent,Boolean>> comps = TableTestHelper
                        .getComps(entryTable);
                EditableComboBox cBox = (EditableComboBox) comps.get(0);
                assertEquals(false, cBox.isEditable());
                assertEquals(true, cBox.isEnabled());
                String expectedText = otherOption.getDisplayText() + " : "
                        + value.getTextValue();
                JTextField activeComp = (JTextField) cBox.getActiveComponent();
                assertEquals(expectedText, activeComp.getText());
            }
        });
    }
    
    /**
     * Tests that a JComboBox in the EditDialog will size itself
     * correctly (see bug #714).
     * @throws Exception
     */
    public void testEditOptionEntryWithLongTextInRejectedDocument() throws Exception {
        final ValueHolder<OptionEntry> optionEntryHolder = ValueHolder.create(null);
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                setStatus(docInstance, DocumentStatus.toIStatus(document,
                        DocumentStatus.REJECTED));
                OptionEntry optionEntry = factory.createOptionEntry("Skills",
                        "Skills");
                optionEntryHolder.setValue(optionEntry);
                optionEntry.setDescription("Choose your skill level");
                Option highOption = factory.createOption("High", "High");
                Option lowOption = factory.createOption("Low", "Low");
                Option otherOption = factory.createOption("Other", "Very Long Other Text --------------------------------->");
                optionEntry.addOption(highOption);
                optionEntry.addOption(lowOption);
                optionEntry.addOption(otherOption);
                otherOption.setTextEntryAllowed(true);
                compEntry.addEntry(optionEntry);
                optionEntry.setSection(section);

                CompositeRow compRow = compResponse.createCompositeRow();
                BasicResponse response = optionEntry
                        .generateInstance(sectionOcc);
                compRow.addResponse(response);
                IOptionValue value = optionEntry.generateValue();
                value.setValue(otherOption);
                value.setTextValue("new");
                response.setValue(value);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
            }
        });
        
        RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
        launchEditDialogBox(getEditDialogLauncherHolder(rendererHandler,
                optionEntryHolder.getValue()), new Executable() {
            public void execute() throws Exception {
                EditDialog editDialog = (EditDialog) getOwnedWindowsShowing(
                        application).get(0);
                JComboBox comboBox = getComponent(JComboBox.class, editDialog,
                        null, null);
                assertEquals(comboBox.getPreferredSize(), comboBox.getSize());
                editDialog.dispose();
            }
        });
    }
    
    
    public static void main(String[] args) throws Exception {
        new CompositeOptionRendererTest().testEditOptionEntryWithLongTextInRejectedDocument();
    }
}
