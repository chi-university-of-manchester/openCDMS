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

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class LongTextRendererTest extends AbstractEntryTestCase {

    private LongTextEntry entry;
    private RendererHandler rendererHandler;
    private Section section;
    private SectionOccurrence sectionOcc;
    private DocumentOccurrence docOcc;
    
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
        entry = factory.createLongTextEntry("Name", "Name");
        entry.setSection(section);
        doc.addEntry(entry);
    }
    
    public void testGetRendererSPI() {
        RendererSPI rspi = rendererHandler.getRendererSPI(entry, null);
        assertTrue(rspi instanceof LongTextRendererSPI);
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
}
