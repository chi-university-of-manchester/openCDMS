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

import javax.swing.JLabel;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.collection.entry.ui.TextEntryField;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.validation.view.ValidationResultViewFactory;

@SuppressWarnings("nls")
public class DoubleRendererTest extends AbstractEntryTestCase {

    private JLabel validationLabel;
    private TextEntryField entryField;
    private NumericValidationRule rule;
    private NumericEntry entry;
    private RendererHandler rendererHandler;
    private Section section;
    private SectionOccurrence sectionOcc;
    private DocumentOccurrence docOcc;
    private NumericEntry optionalEntry;
    private TextEntryField optionalEntryField;
    private JLabel optionalVLabel;
    
    @Override
    protected void setUp() throws Exception {
        Factory factory = getFactory();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        docOcc = getDocumentOccurrence();
        Document doc = docOcc.getDocument();
        DocumentInstance docInstance = doc.generateInstance(docOcc);
        dataSet.addDocument(doc);
        record.addDocumentInstance(docInstance);
        sectionOcc = getSectionOccurrence();
        section = sectionOcc.getSection();
        doc.addSection(section);
        BuilderHandler builderHandler = getBuilderHandler(docInstance, 
                sectionOcc);
        rendererHandler = builderHandler.getCurrentRendererHandler();
        entry = factory.createNumericEntry("Name", "Name");
        entry.setSection(section);
        doc.addEntry(entry);
        
        optionalEntry = factory.createNumericEntry("Optional entry", "optional entry");
        optionalEntry.setSection(section);
        optionalEntry.setEntryStatus(EntryStatus.OPTIONAL);
        doc.addEntry(optionalEntry);
        
        rule = factory.createNumericValidationRule();
        rule.setLowerLimit(new Double(5));
        entry.addValidationRule(rule);
        NumericRendererSPI tRenderer = new DoubleRendererSPI();
        NumericRendererSPI oRenderer = new DoubleRendererSPI();
        BasicRenderer<?> renderer = (BasicRenderer<?>) tRenderer.getRenderer(
                new RendererData(rendererHandler, entry, null, 0, null, false,
                        EditableStatus.DEFAULT));
        entryField = (TextEntryField) renderer.getField();
        BasicRenderer<?> optionalRenderer = (BasicRenderer<?>) oRenderer.getRenderer(
                new RendererData(rendererHandler, optionalEntry, null, 0, null, false,
                        EditableStatus.DEFAULT));
        optionalEntryField = (TextEntryField) optionalRenderer.getField();
        validationLabel = renderer.getValidationLabel();
        optionalVLabel = optionalRenderer.getValidationLabel();
    }
    
    public void testGetRendererSPI() {
        RendererSPI rspi = rendererHandler.getRendererSPI(entry, null);
        assertTrue(rspi instanceof DoubleRendererSPI);
    }
    
    public void testGetRenderer() {
        BasicRenderer<?> renderer = 
            (BasicRenderer<?>) rendererHandler.getRenderer(entry, null);
        assertTrue(renderer.getField() != null);
        EntryLabel label = (EntryLabel) renderer.getLabel();
        assertEquals(entry.getDisplayText(), label.getText());
        assertTrue(renderer.getPresModel() != null);
        assertEquals(NUM_STANDARD_RENDERER_COMPONENTS + 1,
                renderer.getComponents().size());
    }
    
    public void testInvalidInput(){
        entryField.getTextComponent().setText("four");
        assertEquals(ValidationResultViewFactory.getErrorIcon(), 
                validationLabel.getIcon());
    }
    
    public void testNullInputInMandatory(){
        entryField.getTextComponent().setText("1");
        entryField.getTextComponent().setText("");
        assertEquals(ValidationResultViewFactory.getErrorIcon(), 
                validationLabel.getIcon());

    }
    
    public void testNullInputInOptional() {
        optionalEntryField.getTextComponent().setText("1");
        optionalEntryField.getTextComponent().setText("");
        assertEquals("Should not validate empty string in optional entry.", null, 
                optionalVLabel.getIcon());
    }
    
    public void testNullInputInOptional2() {
        optionalEntryField.getTextComponent().setText("rr");
        optionalEntryField.getTextComponent().setText("");
        assertEquals("Should not validate empty string in optional entry.", null, 
                optionalVLabel.getIcon());
    }
}
