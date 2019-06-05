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

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.ui.LongTextEntryField;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class LongTextRendererTest2 extends AbstractEntryTestCase  {
    private LongTextEntry entry;
    private Section section;
    private SectionOccurrence sectionOcc;
    private DocumentOccurrence docOcc;
    private Application app;
    private Factory factory;
    private DocumentInstance docInstance;
    private Document doc;
    
    private void init() throws Exception {
        factory = getFactory();
        app = createApplication();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        docOcc = getDocumentOccurrence();
        doc = docOcc.getDocument();
        docInstance = doc.generateInstance(docOcc);
        dataSet.addDocument(doc);
        record.addDocumentInstance(docInstance);
        sectionOcc = getSectionOccurrence();
        section = sectionOcc.getSection();
        doc.addSection(section);
        entry = factory.createLongTextEntry("Name", "Name");
        entry.setSection(section);
        doc.addEntry(entry);
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testScrollableLongTextEntry() throws Exception {
        invokeAndWait(new Executable() {
           public void execute() throws Exception {
               init();
               app.setVisible(true);
               app.setSelectedDocOccurrenceInstance(docInstance, 0);
               RendererHandler rendererHandler = 
                   app.getModel().getCurrentRendererHandler();
               LongTextEntryField field = (LongTextEntryField) getField(
                       rendererHandler, entry, 0);
               assertEquals(6, field.getRows());
               JScrollPane scrollPane = (JScrollPane) field.getDecoratedTextComponent();
               assertEquals(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER,
                       scrollPane.getHorizontalScrollBarPolicy());
               assertEquals(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                       scrollPane.getVerticalScrollBarPolicy());
            }
        });
    }
    
    public static void main(String[] args) throws Exception {
        new LongTextRendererTest2().testScrollableLongTextEntry();
    }
}
