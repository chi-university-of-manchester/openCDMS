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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.PersistenceManagerTestHelper;
import org.psygrid.collection.entry.persistence.UnfinishedDocInstance;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.collection.entry.ui.EditableComboBox;
import org.psygrid.collection.entry.ui.EntryTable;
import org.psygrid.collection.entry.ui.TableTestHelper;
import org.psygrid.collection.entry.ui.TextEntryField;
import org.psygrid.collection.entry.ui.VariableTable;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class CompositeRendererWithPersistenceTest extends AbstractEntryTestCase   {

    private Section section;
    private Application application;
    private DocumentOccurrence docOccurrence;
    private DocumentInstance docInstance;
    private Factory factory;
    private Document doc;
    private SectionOccurrence sectionOcc;
    
    private void initSecurityAndPersistence() throws Exception {
        PersistenceManagerTestHelper.initPersistenceManager("0", false);
        PersistenceManager.getInstance().saveUsers(getUsers());

        SecurityManager.getInstance().login("SomeUser", 
                "SomePassword".toCharArray(), true);
    }
    
    private void init() throws Exception {
        try {
            initSecurityAndPersistence();
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
        application = createApplication();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        docOccurrence = getDocumentOccurrence();
        doc = docOccurrence.getDocument();
        dataSet.addDocument(doc);
        factory = getFactory();
        sectionOcc = getSectionOccurrence();
        section = sectionOcc.getSection();
        doc.addSection(section);
        docInstance = doc.generateInstance(docOccurrence);
        record.addDocumentInstance(docInstance);
    }
    
    public void testRestoreMultipleRows() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                int selectedOptionIndex1 = 1;
                String selectedOption1 = "red";
                String selectedText1 = "one medication";
                Double selectedNumber1 = Double.valueOf(20);
                
                CompositeEntry table = TableTestHelper.createVariableTable(factory, section);
                table = setId(table);
                doc.addEntry(table);
                table.setSection(section);
                CompositeResponse tableResponse = table.generateInstance(sectionOcc);
                docInstance.addResponse(tableResponse);
                
                fillTableInstance(table, tableResponse,
                        selectedOptionIndex1, selectedText1, selectedNumber1);
                
                int selectedOptionIndex2 = 0;
                String selectedOption2 = "blue";
                String selectedText2 = "another medication";
                Double selectedNumber2 = Double.valueOf(30);
                
                fillTableInstance(table, tableResponse,
                        selectedOptionIndex2, selectedText2, selectedNumber2);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                RendererHandler rendererHandler = 
                    application.getModel().getCurrentRendererHandler();

                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>)
                        rendererHandler.getExistingRenderer(table, 0);
                EntryTable entryTable = (EntryTable) compRenderer.getComposite();
                
                List<Map<JComponent,Boolean>> comps = TableTestHelper.getComps(entryTable);
                assertEquals(2, comps.size());
                
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
                
                Map<JComponent,Boolean> secondRow = comps.get(1);
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
                String optionText2 = (String) comboBox2.getSelectedItem();
                assertEquals(selectedOption2, optionText2);
                assertEquals(selectedText2, textEntryField2.getTextComponent().getText());
                Double numericValue2 = Double.valueOf(numericEntryField2.getTextComponent().getText());
                assertEquals(selectedNumber2, numericValue2);
                
                String newSelectedOption1 = "blue";
                int newSelectedOptionIndex1 = 0;
                String newSelectedText1 = "new one medication";
                String newSelectedNumber1Text = "40";
                Double newSelectedNumber1 = Double.valueOf(newSelectedNumber1Text);
                comboBox1.setSelectedIndex(newSelectedOptionIndex1);
                CompositeRow firstCompRow = tableResponse.getCompositeRow(0);
                BasicResponse textResponse1 = firstCompRow.getResponse(table.getEntry(1));
                textEntryField1.getTextComponent().setText(newSelectedText1);
                ITextValue textValue1 = (ITextValue) textResponse1.getValue();
                assertEquals("Text entry incorrect.", newSelectedText1, 
                        textValue1.getValue());
                numericEntryField1.getTextComponent().setText(newSelectedNumber1Text);
                
                String newSelectedOption2 = "yellow";
                int newSelectedOptionIndex2 = 2;
                String newSelectedText2 = "new two medication";
                String newSelectedNumber2Text = "30";
                Double newSelectedNumber2 = Double.valueOf(newSelectedNumber2Text);
                comboBox2.setSelectedIndex(newSelectedOptionIndex2);
                textEntryField2.getTextComponent().setText(newSelectedText2);
                numericEntryField2.getTextComponent().setText(newSelectedNumber2Text);
                File savedFile = null;
                try {
                    savedFile = File.createTempFile("unfDoc", "xml");
                    PersistenceManagerTestHelper.save(new UnfinishedDocInstance(
                            docInstance, 0, DocumentStatus.READY_TO_SUBMIT), savedFile);
                    application.getModel().tearDown(true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                
                application.dispose();
                init();
                
                UnfinishedDocInstance unfDoc;
                try {
                    unfDoc = (UnfinishedDocInstance) 
                            PersistenceManagerTestHelper.load(savedFile);
                } catch (IOException e) {
                   throw new RuntimeException(e);
                }
                docInstance = unfDoc.getDocOccurrenceInstance();
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                rendererHandler = 
                    application.getModel().getCurrentRendererHandler();
                
                compRenderer = (CompositeRenderer<?>)
                        rendererHandler.getExistingRenderer(table, 0);
                entryTable = (EntryTable) compRenderer.getComposite();
                
                comps = TableTestHelper.getComps(entryTable);

                assertEquals(2, comps.size());
                firstRow = comps.get(0);

                counter = 0;
                //Components should be in order.
                for (JComponent comp: firstRow.keySet()) {
                	if (counter == 0) {
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
                optionText1 = (String) comboBox1.getSelectedItem();
                assertEquals("Option entry incorrect.", newSelectedOption1, 
                        optionText1);
                assertEquals("Text entry incorrect.", newSelectedText1, 
                        textEntryField1.getTextComponent().getText());
                numericValue1 = Double.valueOf(numericEntryField1.getTextComponent().getText());
                assertEquals("Numeric entry incorrect.", newSelectedNumber1, 
                        numericValue1);
                
                secondRow = comps.get(1);
                counter = 0;
                //Components should be in order.
                for (JComponent comp: secondRow.keySet()) {
                	if (counter == 0) {
                		comboBox2 = (EditableComboBox) comp;
                	}
                	else if (counter == 1) {
                		textEntryField2 = (TextEntryField) comp;
                	}
                	else if (counter == 2) {
                		numericEntryField2 = (TextEntryField) comp;
                	}
                	counter ++;
                }                optionText2 = (String) comboBox2.getSelectedItem();
                assertEquals(newSelectedOption2, optionText2);
                assertEquals(newSelectedText2, textEntryField2.getTextComponent().getText());
                numericValue2 = Double.valueOf(numericEntryField2.getTextComponent().getText());
                assertEquals(newSelectedNumber2, numericValue2);
                
            }
        });
    }
    
    /**
     * Restores multiple rows, then invokes the addRow button. Persists the
     * information and restores it again.
     * @throws Exception 
     */
    public void testAddAndRestoreRows() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                int selectedOptionIndex1 = 1;
                String selectedOption1 = "red";
                String selectedText1 = "one medication";
                Double selectedNumber1 = Double.valueOf(20);
                
                CompositeEntry table = TableTestHelper.createVariableTable(factory, section);
                table = setId(table);
                docOccurrence.getDocument().addEntry(table);
                table.setSection(section);
                CompositeResponse tableResponse = table.generateInstance(sectionOcc);
                docInstance.addResponse(tableResponse);
                
                fillTableInstance(table, tableResponse,
                        selectedOptionIndex1, selectedText1, selectedNumber1);
                
                int selectedOptionIndex2 = 0;
                String selectedOption2 = "blue";
                String selectedText2 = "another medication";
                Double selectedNumber2 = Double.valueOf(30);
                
                fillTableInstance(table, tableResponse,
                        selectedOptionIndex2, selectedText2, selectedNumber2);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                RendererHandler rendererHandler = 
                    application.getModel().getCurrentRendererHandler();
                
                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rendererHandler
                        .getExistingRenderer(table, 0);
                VariableTable entryTable = (VariableTable) compRenderer
                        .getComposite();
                
                List<Map<JComponent,Boolean>> comps = TableTestHelper.getComps(entryTable);
                assertEquals(2, comps.size());
                
                Map<JComponent,Boolean> firstRow = comps.get(0);
                EditableComboBox comboBox1 = null;
                TextEntryField textEntryField1 = null;
                TextEntryField numericEntryField1 = null;
                int counter = 0;
                //Components should be in order.
                for (JComponent comp: firstRow.keySet()) {
                	if (counter == 0) {
                		comboBox1 = (EditableComboBox) comp;
                	}
                	else if (counter == 1) {
                		textEntryField1 = (TextEntryField) comp;
                	}
                	else if (counter == 2) {
                		numericEntryField1 = (TextEntryField) comp;
                	}
                	counter ++;
                }                String optionText1 = (String) comboBox1.getSelectedItem();
                assertEquals(selectedOption1, optionText1);
                assertEquals(selectedText1, textEntryField1.getTextComponent().getText());
                Double doubleValue1 = Double.valueOf(numericEntryField1.getTextComponent().getText());
                assertEquals(selectedNumber1, doubleValue1);
                
                Map<JComponent,Boolean> secondRow = comps.get(1);
                EditableComboBox comboBox2 = null;
                TextEntryField textEntryField2 = null;
                TextEntryField numericEntryField2 = null;
                counter = 0;
                //Components should be in order.
                for (JComponent comp: firstRow.keySet()) {
                	if (counter == 0) {
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
                String optionText2 = (String) comboBox2.getSelectedItem();
                assertEquals(selectedOption2, optionText2);
                assertEquals(selectedText2, textEntryField2.getTextComponent().getText());
                Double doubleValue2 = Double.valueOf(numericEntryField2.getTextComponent().getText());
                assertEquals(selectedNumber2, doubleValue2);
                
                rendererHandler = 
                    application.getModel().getCurrentRendererHandler();
                
                compRenderer = (CompositeRenderer<?>)
                        rendererHandler.getExistingRenderer(table, 0);
                entryTable = (VariableTable) compRenderer.getComposite();
                
                comps = TableTestHelper.getComps(entryTable);

                // Two rows
                assertEquals(2, comps.size());
                firstRow = comps.get(0);
                counter = 0;
                //Components should be in order.
                for (JComponent comp: firstRow.keySet()) {
                	if (counter == 0) {
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
                optionText1 = (String) comboBox1.getSelectedItem();
                assertEquals(selectedOption1, optionText1);
                assertEquals(selectedText1, textEntryField1.getTextComponent().getText());
                doubleValue1 = Double.valueOf(numericEntryField1.getTextComponent().getText());
                assertEquals(selectedNumber1, doubleValue1);
                
                secondRow = comps.get(1);
                counter = 0;
                //Components should be in order.
                for (JComponent comp: secondRow.keySet()) {
                	if (counter == 0) {
                		comboBox2 = (EditableComboBox) comp;
                	}
                	else if (counter == 1) {
                		textEntryField2 = (TextEntryField) comp;
                	}
                	else if (counter == 2) {
                		numericEntryField2 = (TextEntryField) comp;
                	}
                	counter ++;
                }                optionText2 = (String) comboBox2.getSelectedItem();
                assertEquals(selectedOption2, optionText2);
                assertEquals(selectedText2, textEntryField2.getTextComponent().getText());
                doubleValue2 = Double.valueOf(numericEntryField2.getTextComponent().getText());
                assertEquals(selectedNumber2, doubleValue2);
                
                // Add third row
                entryTable.getAddRowButton().doClick();
                
                //Composite Rows
                CompositeRow firstCompRow = tableResponse.getCompositeRow(0);
                CompositeRow secondCompRow = tableResponse.getCompositeRow(1);
                CompositeRow thirdCompRow = tableResponse.getCompositeRow(2);
                
                // Option Responses
                OptionEntry optionEntry = (OptionEntry) table.getEntry(0);
                
                BasicResponse optionResponse1 = firstCompRow.getResponse(optionEntry);
                IOptionValue optionValue1 = (IOptionValue) optionResponse1.getValue();
                assertEquals(selectedOption1, optionValue1.getValue().getDisplayText());
                
                BasicResponse optionResponse2 = secondCompRow.getResponse(optionEntry);
                IOptionValue optionValue2 = (IOptionValue) optionResponse2.getValue();
                assertEquals(selectedOption2, optionValue2.getValue().getDisplayText());
                
                BasicResponse optionResponse3 = thirdCompRow.getResponse(optionEntry);
                IOptionValue optionValue3 = (IOptionValue) optionResponse3.getValue();
                assertNull(optionValue3.getValue());
                
                // Text Responses
                TextEntry textEntry = (TextEntry) table.getEntry(1);
                
                BasicResponse textResponse1 = firstCompRow.getResponse(textEntry);
                ITextValue textValue1 = (ITextValue) textResponse1.getValue();
                assertEquals(selectedText1, textValue1.getValue());
                
                BasicResponse textResponse2 = secondCompRow.getResponse(textEntry);
                ITextValue textValue2 = (ITextValue) textResponse2.getValue();
                assertEquals(selectedText2, textValue2.getValue());

                BasicResponse textResponse3 = thirdCompRow.getResponse(textEntry);
                ITextValue textValue3 = (ITextValue) textResponse3.getValue();
                assertNull(textValue3.getValue());
                
                // Numeric Responses
                NumericEntry numericEntry = (NumericEntry) table.getEntry(2);
                
                BasicResponse numericResponse1 = firstCompRow.getResponse(numericEntry);
                INumericValue numericValue1 = (INumericValue) numericResponse1.getValue();
                assertEquals(selectedNumber1, numericValue1.getValue());
                
                BasicResponse numericResponse2 = secondCompRow.getResponse(numericEntry);
                INumericValue numericValue2 = (INumericValue) numericResponse2.getValue();
                assertEquals(selectedNumber2, numericValue2.getValue());

                BasicResponse numericResponse3 = thirdCompRow.getResponse(numericEntry);
                INumericValue numericValue3 = (INumericValue) numericResponse3.getValue();
                assertNull(numericValue3.getValue());
                
                
                // Add fourth row
                entryTable.getAddRowButton().doClick();
                CompositeRow fourthCompRow = tableResponse.getCompositeRow(3);
                              
                comps = TableTestHelper.getComps(entryTable);
                Map<JComponent,Boolean> fourthRow = comps.get(3);
                EditableComboBox comboBox4 = null;
                TextEntryField textEntryField4 = null;
                TextEntryField numericEntryField4 = null;
                counter = 0;
                //Components should be in order.
                for (JComponent comp: fourthRow.keySet()) {
                	if (counter ==0) {
                		comboBox4 = (EditableComboBox) comp;
                	}
                	else if (counter == 1) {
                		textEntryField4 = (TextEntryField) comp;
                	}
                	else if (counter == 2) {
                		numericEntryField4 = (TextEntryField) comp;
                	}
                	counter ++;
                }
                int selectedOptionIndex4 = 1;
                String selectedOption4 = "red";
                comboBox4.setSelectedIndex(selectedOptionIndex4);
                String selectedText4 = "one medication";
                textEntryField4.getTextComponent().setText(selectedText4);
                String selectedNumber4Text = "50";
                Double selectedNumber4 = Double.valueOf(selectedNumber4Text);
                numericEntryField4.getTextComponent().setText(
                        selectedNumber4Text);

                // Option Responses
                optionResponse1 = firstCompRow.getResponse(optionEntry);
                optionValue1 = (IOptionValue) optionResponse1.getValue();
                assertEquals(selectedOption1, optionValue1.getValue().getDisplayText());
                
                optionResponse2 = secondCompRow.getResponse(optionEntry);
                optionValue2 = (IOptionValue) optionResponse2.getValue();
                assertEquals(selectedOption2, optionValue2.getValue().getDisplayText());
                
                optionResponse3 = thirdCompRow.getResponse(optionEntry);
                optionValue3 = (IOptionValue) optionResponse3.getValue();
                assertNull(optionValue3.getValue());
                
                BasicResponse optionResponse4 = fourthCompRow.getResponse(optionEntry);
                IOptionValue optionValue4 = (IOptionValue) optionResponse4.getValue();
                assertEquals(selectedOption4, optionValue4.getValue().getDisplayText());
                
                // Text Responses
                textResponse1 = firstCompRow.getResponse(textEntry);
                textValue1 = (ITextValue) textResponse1.getValue();
                assertEquals(selectedText1, textValue1.getValue());
                
                textResponse2 = secondCompRow.getResponse(textEntry);
                textValue2 = (ITextValue) textResponse2.getValue();
                assertEquals(selectedText2, textValue2.getValue());

                textResponse3 = thirdCompRow.getResponse(textEntry);
                textValue3 = (ITextValue) textResponse3.getValue();
                assertNull(textValue3.getValue());
                
                BasicResponse textResponse4 = fourthCompRow.getResponse(textEntry);
                ITextValue textValue4 = (ITextValue) textResponse4.getValue();
                assertEquals(selectedText4, textValue4.getValue());
                
                // Numeric Responses
                numericResponse1 = firstCompRow.getResponse(numericEntry);
                numericValue1 = (INumericValue) numericResponse1.getValue();
                assertEquals(selectedNumber1, numericValue1.getValue());
                
                numericResponse2 = secondCompRow.getResponse(numericEntry);
                numericValue2 = (INumericValue) numericResponse2.getValue();
                assertEquals(selectedNumber2, numericValue2.getValue());

                numericResponse3 = thirdCompRow.getResponse(numericEntry);
                numericValue3 = (INumericValue) numericResponse3.getValue();
                assertNull(numericValue3.getValue());
                
                BasicResponse numericResponse4 = fourthCompRow.getResponse(numericEntry);
                INumericValue numericValue4 = (INumericValue) numericResponse4.getValue();
                assertEquals(selectedNumber4, numericValue4.getValue());
                
                File savedFile = null;
                try {
                    savedFile = File.createTempFile("unfDoc", "xml");
                    PersistenceManagerTestHelper.save(
                            new UnfinishedDocInstance(docInstance, 0,
                                    DocumentStatus.READY_TO_SUBMIT),
                            savedFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    application.getModel().tearDown(false);
                } catch (IOException e1) {
                    fail(e1.getMessage());
                }
                application.dispose();
                init();

                UnfinishedDocInstance unfDoc;
                try {
                    unfDoc = (UnfinishedDocInstance) PersistenceManagerTestHelper
                            .load(savedFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                docInstance = unfDoc.getDocOccurrenceInstance();
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                rendererHandler = application.getModel().getCurrentRendererHandler();

                compRenderer = (CompositeRenderer<?>)
                        rendererHandler.getExistingRenderer(table, 0);
                entryTable = (VariableTable) compRenderer.getComposite();

                comps = TableTestHelper.getComps(entryTable);
                
                assertEquals("Number of rows incorrect", 4, comps.size());
                firstRow = comps.get(0);
                counter = 0;
                //Components should be in order.
                for (JComponent comp: firstRow.keySet()) {
                	if (counter == 0) {
                		comboBox1 = (EditableComboBox) comp;
                	}
                	else if (counter == 1) {
                		textEntryField1 = (TextEntryField) comp;
                	}
                	else if (counter == 2) {
                		numericEntryField1 = (TextEntryField) comp;
                	}
                	counter ++;
                }                optionText1 = (String) comboBox1.getSelectedItem();
                assertEquals(selectedOption1, optionText1);
                assertEquals(selectedText1, textEntryField1.getTextComponent().getText());
                doubleValue1 = Double.valueOf(numericEntryField1.getTextComponent().getText());
                assertEquals(selectedNumber1, doubleValue1);
                
                secondRow = comps.get(1);
                counter = 0;
                //Components should be in order.
                for (JComponent comp: firstRow.keySet()) {
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
                optionText2 = (String) comboBox2.getSelectedItem();
                assertEquals(selectedOption2, optionText2);
                assertEquals(selectedText2, textEntryField2.getTextComponent().getText());
                doubleValue2 = Double.valueOf(numericEntryField2.getTextComponent().getText());
                assertEquals(selectedNumber2, doubleValue2);
                
                Map<JComponent,Boolean> thirdRow = comps.get(2);
                EditableComboBox comboBox3 = null;
                TextEntryField textEntryField3 = null;
                TextEntryField numericEntryField3 = null;
                counter = 0;
                //Components should be in order.
                for (JComponent comp: thirdRow.keySet()) {
                	if (counter == 0) {
                		comboBox3 = (EditableComboBox) comp;
                	}
                	else if (counter == 1) {
                		textEntryField3 = (TextEntryField) comp;
                	}
                	else if (counter == 2) {
                		numericEntryField3 = (TextEntryField) comp;
                	}
                	counter ++;
                }
                String optionText3 = (String) comboBox3.getSelectedItem();
                assertNull(optionText3);
                String stringValue3 = textEntryField3.getTextComponent().getText();
                assertTrue("Third row text column is not empty", stringValue3 == null || 
                        stringValue3.equals(""));
                String numericString3 = numericEntryField3.getTextComponent().getText();
                assertTrue("Third row numeric column is not empty", 
                        numericString3 == null || numericString3.equals(""));
                
                fourthRow = comps.get(3);
                counter = 0;
                //Components should be in order.
                for (JComponent comp: fourthRow.keySet()) {
                	if (counter ==0) {
                		comboBox4 = (EditableComboBox) comp;
                	}
                	else if (counter == 1) {
                		textEntryField4 = (TextEntryField) comp;
                	}
                	else if (counter == 2) {
                		numericEntryField4 = (TextEntryField) comp;
                	}
                	counter ++;
                }
                String optionText4 = (String) comboBox4.getSelectedItem();
                assertEquals(selectedOption4, optionText4);
                assertEquals(selectedText4, textEntryField4.getTextComponent().getText());
                Double doubleValue4 = Double.valueOf(numericEntryField4.getTextComponent().getText());
                assertEquals(selectedNumber4, doubleValue4);
            }
        });
    }
    
    private ElementInstance fillTableInstance(CompositeEntry table,
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
