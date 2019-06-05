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
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class FixedTableTest extends AbstractEntryTestCase   {

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
        compEntry = TableTestHelper.createFixedTable(factory, section);
        document.addEntry(compEntry);
        compEntry.setSection(section);
        compResponse = compEntry.generateInstance(sectionOcc);
        docInstance.addResponse(compResponse);
    }
    
    public static void main(String[] args) throws Exception {
        new FixedTableTest().testTableBiggerThanViewport();
    }
    
    public void testTableBiggerThanViewport() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                TextEntry textEntry = factory.createTextEntry("long text entry",
                        "Some very long text to make the table too wide");
                textEntry.setSection(section);
                compEntry.addEntry(textEntry);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
            }
        });
    }
}
