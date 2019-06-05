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

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.renderer.CompositeRenderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class VariableTableTest extends AbstractEntryTestCase	{
    
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
        compEntry = TableTestHelper.createVariableTable(factory, section);
        document.addEntry(compEntry);
        compEntry.setSection(section);
        compResponse = compEntry.generateInstance(sectionOcc);
        docInstance.addResponse(compResponse);
    }
    //TODO Add test that also tests the children
    /**
     * Only tests the table components. Doesn't test the children.
     * @throws Exception 
     *
     */
    public void testSetEnabled() throws Exception {
        invokeAndWait(new Executable() {
           public void execute() throws Exception {
               init();
               OptionEntry conditionalEntry = factory.createOptionEntry("Has abode",
                       "Has abode.");
               Option yesOption = factory.createOption("Yes");
               Option noOption = factory.createOption("No");
               conditionalEntry.addOption(yesOption);
               conditionalEntry.addOption(noOption);
               document.addEntry(conditionalEntry);
               conditionalEntry.setSection(section);
               
               OptionDependent optDep = factory.createOptionDependent();
               optDep.setEntryStatus(EntryStatus.DISABLED);
               optDep.setDependentEntry(compEntry);
               yesOption.addOptionDependent(optDep);
               
               application.setSelectedDocOccurrenceInstance(docInstance, 0);
               application.setVisible(true);

               RendererHandler rendererHandler = application.getModel()
                       .getCurrentRendererHandler();
               
               CompositeRenderer<?> compRenderer = (CompositeRenderer<?>)
                   rendererHandler.getExistingRenderer(compEntry, 0);
               
               VariableTable entryTable = (VariableTable) compRenderer.getComposite();
               
               assertTrue(entryTable.isEnabled());
               assertTrue(entryTable.getAddRowButton().isEnabled());
               assertFalse(entryTable.getDeleteRowButton(0).isEnabled());
               
               setOption(rendererHandler, conditionalEntry, 0, yesOption);
               
               assertFalse(entryTable.isEnabled());
               assertFalse(entryTable.getAddRowButton().isEnabled());
               assertFalse(entryTable.getDeleteRowButton(0).isEnabled());
                              
               setOption(rendererHandler, conditionalEntry, 0, noOption);
               
               assertTrue(entryTable.isEnabled());
               assertTrue(entryTable.getAddRowButton().isEnabled());
               assertFalse(entryTable.getDeleteRowButton(0).isEnabled());
               
               entryTable.getAddRowButton().doClick();
               
               assertTrue(entryTable.isEnabled());
               assertTrue(entryTable.getAddRowButton().isEnabled());
               assertTrue(entryTable.getDeleteRowButton(0).isEnabled());
               assertTrue(entryTable.getDeleteRowButton(1).isEnabled());
               
               entryTable.getDeleteRowButton(0).doClick();
               
               assertTrue(entryTable.isEnabled());
               assertTrue(entryTable.getAddRowButton().isEnabled());
               assertFalse(entryTable.getDeleteRowButton(0).isEnabled());
            }
        });
    }
    
    public void testRowRemoval() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rendererHandler
                        .getExistingRenderer(compEntry, 0);

                VariableTable entryTable = (VariableTable) compRenderer
                        .getComposite();

                int numRenderers = 4;
                int changePerRow = 3;
                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(1, compResponse.numCompositeRows());

                entryTable.getAddRowButton().doClick();
                numRenderers = numRenderers + changePerRow;

                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(2, compResponse.numCompositeRows());

                entryTable.getAddRowButton().doClick();
                numRenderers = numRenderers + changePerRow;

                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(3, compResponse.numCompositeRows());

                entryTable.getDeleteRowButton(2).doClick();
                numRenderers = numRenderers - changePerRow;

                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(2, compResponse.numCompositeRows());

                entryTable.getAddRowButton().doClick();
                numRenderers = numRenderers + changePerRow;

                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(3, compResponse.numCompositeRows());

                entryTable.getDeleteRowButton(1).doClick();
                numRenderers = numRenderers - changePerRow;

                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(2, compResponse.numCompositeRows());

                entryTable.getDeleteRowButton(0).doClick();
                numRenderers = numRenderers - changePerRow;

                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(1, compResponse.numCompositeRows());

                assertEquals(false, entryTable.getDeleteRowButton(0)
                        .isEnabled());

                entryTable.getAddRowButton().doClick();
                numRenderers = numRenderers + changePerRow;

                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(2, compResponse.numCompositeRows());

                entryTable.getDeleteRowButton(0).doClick();
                numRenderers = numRenderers - changePerRow;

                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(1, compResponse.numCompositeRows());

                assertEquals(false, entryTable.getDeleteRowButton(0)
                        .isEnabled());

            }
        });
    }
    
    public void testValidateAfterRemoval() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                CompositeRow compositeRow = compResponse.createCompositeRow();
                
                OptionEntry optionEntry = (OptionEntry) compEntry.getEntry(0);
                BasicResponse optionResponse = optionEntry.generateInstance(sectionOcc);
                IOptionValue optionValue = optionEntry.generateValue();
                optionValue.setValue(optionEntry.getOption(0));
                optionResponse.setValue(optionValue);
                compositeRow.addResponse(optionResponse);
                
                TextEntry textEntry = (TextEntry) compEntry.getEntry(1);
                BasicResponse textResponse = textEntry.generateInstance(sectionOcc);
                ITextValue textValue = textEntry.generateValue();
                textValue.setValue("someText");
                textResponse.setValue(textValue);
                compositeRow.addResponse(textResponse);
                
                NumericEntry numericEntry = (NumericEntry) compEntry.getEntry(2);
                BasicResponse numericResponse = numericEntry.generateInstance(sectionOcc);
                INumericValue numericValue = numericEntry.generateValue();
                numericValue.setValue(Double.valueOf(5.0));
                numericResponse.setValue(numericValue);
                compositeRow.addResponse(numericResponse);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);

                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rendererHandler
                        .getExistingRenderer(compEntry, 0);

                VariableTable entryTable = (VariableTable) compRenderer
                        .getComposite();

                int numRenderers = 4;
                int changePerRow = 3;
                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(1, compResponse.numCompositeRows());
                boolean valid = application.getModel().validateSection(false).isEmpty();
                assertEquals(true, valid);
                
                entryTable.getAddRowButton().doClick();
                numRenderers = numRenderers + changePerRow;

                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(2, compResponse.numCompositeRows());
                valid = application.getModel().validateSection(false).isEmpty();
                assertEquals(false, valid);
                
                entryTable.getDeleteRowButton(1).doClick();
                numRenderers = numRenderers - changePerRow;

                assertEquals(numRenderers, rendererHandler
                        .getNumExistingRenderers());
                assertEquals(1, compResponse.numCompositeRows());
                valid = application.getModel().validateSection(false).isEmpty();
                assertEquals(true, valid);
            }
        });
    }

    
}
