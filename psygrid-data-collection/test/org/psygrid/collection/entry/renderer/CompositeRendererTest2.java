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

import javax.swing.JComponent;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.ui.EditableComboBox;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.collection.entry.ui.EntryTable;
import org.psygrid.collection.entry.ui.TableTestHelper;
import org.psygrid.collection.entry.ui.TextEntryField;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class CompositeRendererTest2 extends AbstractEntryTestCase {

    private CompositeEntry medicationTable;
    private RendererHandler rendererHandler;
    private Section section;
    private Application application;
    private DocumentOccurrence docOccurrence;
    private DocumentInstance docInstance;
    private Document doc;
    private SectionOccurrence sectionOcc;
    
    private void init() throws Exception {
        application = createApplication();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        docOccurrence = getDocumentOccurrence();
        doc = docOccurrence.getDocument();
        dataSet.addDocument(doc);
        sectionOcc = getSectionOccurrence();
        section = sectionOcc.getSection();
        medicationTable = TableTestHelper.createVariableTable(getFactory(), section);
        doc.addSection(section);
        docInstance = doc.generateInstance(docOccurrence);
        record.addDocumentInstance(docInstance);
        BuilderHandler builderHandler = getBuilderHandler(docInstance, sectionOcc);
        rendererHandler = builderHandler.getCurrentRendererHandler();
    }
    
    public void testGetRendererSPI() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                RendererSPI rspi = rendererHandler.getRendererSPI(
                        medicationTable, null);
                assertTrue(rspi instanceof CompositeRendererSPI);
            }
        });
    }

    public void testGetRenderer() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                doc.addEntry(medicationTable);
                medicationTable.setSection(section);
                Renderer renderer = rendererHandler.getRenderer(medicationTable, 
                        null);
                assertTrue(renderer instanceof CompositeRenderer);
                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) renderer;
                EntryLabel label = (EntryLabel) renderer.getLabel();
                assertEquals(medicationTable.getDisplayText(), label.getText());
                assertTrue(compRenderer.getPresModel() != null);
                assertEquals(NUM_STANDARD_RENDERER_COMPONENTS + 1,
                        renderer.getComponents().size());
                assertTrue(compRenderer.getComposite() instanceof EntryTable);
            }
        });
    }
    
    public void testRestoreTableOptionEntry() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                int selectedOptionIndex = 2;
                String selectedOption = "yellow";
                String selectedText = "SomeMedication";
                Double selectedNumber = Double.valueOf(10);
                
                createTableInstance(medicationTable,
                        selectedOptionIndex, selectedText, selectedNumber);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                RendererHandler rHandler = 
                    application.getModel().getCurrentRendererHandler();
                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>)
                        rHandler.getExistingRenderer(medicationTable, 0);
                EntryTable entryTable = (EntryTable) compRenderer.getComposite();
                
                List<Map<JComponent,Boolean>> comps = TableTestHelper.getComps(entryTable);
                assertEquals(1, comps.size());
                Map<JComponent,Boolean> firstRow = comps.get(0);
                
                EditableComboBox comboBox = null;
                TextEntryField textEntryField = null;
                TextEntryField numericEntryField = null;
                int counter = 0;
                //Components should be in order.
                for (JComponent comp: firstRow.keySet()) {
                	if (counter ==0) {
                		comboBox = (EditableComboBox) comp;
                	}
                	else if (counter == 1) {
                		textEntryField = (TextEntryField) comp;
                	}
                	else if (counter == 2) {
                		numericEntryField = (TextEntryField) comp;
                	}
                	counter ++;
                }
                
                String optionText = (String) comboBox.getSelectedItem();
                assertEquals(selectedOption, optionText);
                assertEquals(selectedText, textEntryField.getTextComponent().getText());
                Double numericValue = Double.valueOf(numericEntryField.getTextComponent().getText());
                assertEquals(selectedNumber, numericValue);
            }
        });
    }
    
    public void testNullDisplayText() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                medicationTable.setDisplayText(null);
                medicationTable.setSection(section);
                doc.addEntry(medicationTable);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
            }
        });
    }
    
    public void testRestoreMultipleRows() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                int selectedOptionIndex1 = 1;
                String selectedOption1 = "red";
                String selectedText1 = "one medication";
                Double selectedNumber1 = Double.valueOf(20);
                
                CompositeEntry table = medicationTable;
                doc.addEntry(table);
                table.setSection(section);
                CompositeResponse tableResponse = table.generateInstance(sectionOcc);
                docInstance.addResponse(tableResponse);
                
                fillTableInstance(table, tableResponse,
                        selectedOptionIndex1, selectedText1, selectedNumber1);
                
                int selectedOptionIndex2 = 0;
                String selectedOption2 = "blue";
                String selectedText2 = "another medication";
                Double selectedNumber2 = Double.valueOf(28);
                
                fillTableInstance(table, tableResponse,
                        selectedOptionIndex2, selectedText2, selectedNumber2);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                RendererHandler rHandler = application.getModel()
                        .getCurrentRendererHandler();
                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rHandler
                        .getExistingRenderer(medicationTable, 0);
                EntryTable entryTable = (EntryTable) compRenderer
                        .getComposite();

                List<Map<JComponent,Boolean>> comps = TableTestHelper
                        .getComps(entryTable);

                assertEquals("Wrong number of rows found in table", 2, 
                        comps.size());
                Map<JComponent,Boolean> firstRow = comps.get(0);
                EditableComboBox comboBox1 = null;
                TextEntryField textEntryField1 = null;
                TextEntryField numericEntryField1 = null;
                int counter = 0;
                //Components should be in order.
                for (JComponent comp: firstRow.keySet()) {
                	if (counter ==0) {
                		comboBox1 = (EditableComboBox) comp;
                	}
                	else if (counter == 1) {
                		textEntryField1 = (TextEntryField) comp;
                	}
                	else if (counter == 2) {
                		numericEntryField1 = (TextEntryField) comp;
                	}
                	counter ++;
                }
                
                String optionText1 = (String) comboBox1.getSelectedItem();
                assertEquals(selectedOption1, optionText1);
                assertEquals(selectedText1, textEntryField1.getTextComponent().getText());
                Double numericValue1 = Double.valueOf(numericEntryField1.getTextComponent().getText());
                assertEquals(selectedNumber1, numericValue1);
                
                Map<JComponent,Boolean> secondRow = comps.get(0);
                EditableComboBox comboBox2 = null;
                TextEntryField textEntryField2 = null;
                TextEntryField numericEntryField2 = null;
                counter = 0;
                //Components should be in order.
                for (JComponent comp: secondRow.keySet()) {
                	if (counter ==0) {
                		comboBox2 = (EditableComboBox) comp;
                	}
                	else if (counter == 1) {
                		textEntryField2 = (TextEntryField) comp;
                	}
                	else if (counter == 2) {
                		numericEntryField2 = (TextEntryField) comp;
                	}
                	counter ++;
                }
                String optionText = (String) comboBox2.getSelectedItem();
                assertEquals(selectedOption2, optionText);
                assertEquals(selectedText2, textEntryField2.getTextComponent().getText());
                Double numericValue2 = Double.valueOf(numericEntryField2.getTextComponent().getText());
                assertEquals(selectedNumber2, numericValue2);
            }
        });
    }
    
    private CompositeResponse createTableInstance(CompositeEntry table,
                                                  int selectedOption,
                                                  String selectedText, Double selectedNumber) {
        table.setSection(section);
        doc.addEntry(table);
        CompositeResponse tableInstance = table.generateInstance(sectionOcc);
        docInstance.addResponse(tableInstance);
        return fillTableInstance(table, tableInstance, selectedOption, 
                selectedText, selectedNumber);
    }
    
    private CompositeResponse fillTableInstance(CompositeEntry table,
                                                CompositeResponse tableResponse, int selectedOption,
                                                String selectedText, Double selectedNumber) {
        CompositeRow compRow = tableResponse.createCompositeRow();
        OptionEntry colorOption = (OptionEntry) table.getEntry(0);
        BasicResponse colorResponse = colorOption.generateInstance(sectionOcc);
        IOptionValue colorValue = colorOption.generateValue();
        colorValue.setValue(colorOption.getOption(selectedOption));
        colorResponse.setValue(colorValue);
        compRow.addResponse(colorResponse);
        TextEntry medication = (TextEntry) table.getEntry(1);
        BasicResponse medResponse = medication.generateInstance(sectionOcc);
        ITextValue textValue = medication.generateValue();
        textValue.setValue(selectedText);
        medResponse.setValue(textValue);
        compRow.addResponse(medResponse);
        NumericEntry freq = (NumericEntry) table.getEntry(2);
        BasicResponse freqResponse = freq.generateInstance(sectionOcc);
        INumericValue numericValue = freq.generateValue();
        numericValue.setValue(selectedNumber);
        freqResponse.setValue(numericValue);
        compRow.addResponse(freqResponse);
        return tableResponse;
    }
}
