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
package org.psygrid.collection.entry;

import java.util.List;

import javax.swing.Action;

import org.psygrid.collection.entry.util.StandardCodesGetter;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class ApplicationModelTest extends AbstractEntryTestCase    {
    
    private ApplicationModel model;
    
    @Override
    protected void setUp() throws Exception {
        Factory factory = getFactory();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        DocumentOccurrence docOccurrence = getDocumentOccurrence();
        Document doc = docOccurrence.getDocument();
        doc.addOccurrence(docOccurrence);
        dataSet.addDocument(doc);
        DocumentInstance docInstance = doc.generateInstance(docOccurrence);
        record.addDocumentInstance(docInstance);
        Section firstSection = factory.createSection("Initial section");
        SectionOccurrence firstSectionOcc =
            factory.createSectionOccurrence("Initial Section Occ");
        firstSection.addOccurrence(firstSectionOcc);
        Section secondSection = factory.createSection("Last section");
        SectionOccurrence secondSectionOcc =
            factory.createSectionOccurrence("Last Section Occ");
        secondSection.addOccurrence(secondSectionOcc);
        doc.addSection(firstSection);
        doc.addSection(secondSection);
        model = new ApplicationModel();
        model.setStandardCodesGetter(new StandardCodesGetter() {
           public List<StandardCode> getStandardCodes() {
                return AbstractEntryTestCase.getStandardCodes();
            } 
        });
        model.setCurrentRecord(docInstance.getRecord());
        model.setSelectedDocOccurrenceInstance(docInstance, DocumentStatus.NOT_STARTED);
    }
    
    public void testBackAction() {
        Action backAction = model.getBackAction();
        backAction.actionPerformed(null);
        assertEquals(0, model.getCurrentSectionIndex());
    }
    
    public void testBackAndForwardActions() {
        Action backAction = model.getBackAction();
        Action forwardAction = model.getForwardAction();
        forwardAction.actionPerformed(null);
        assertEquals(1, model.getCurrentSectionIndex());
        backAction.actionPerformed(null);
        assertEquals(0, model.getCurrentSectionIndex());
    }
    
    public void testForwardAction() {
        Action forwardAction = model.getForwardAction();
        forwardAction.actionPerformed(null);
        assertEquals(1, model.getCurrentSectionIndex());
        forwardAction.actionPerformed(null);
        assertEquals(1, model.getCurrentSectionIndex());
    }
    
    public static void main(String[] args) throws Exception {
        ApplicationModelTest m = new ApplicationModelTest();
        m.setUp();
        m.testBackAndForwardActions();
    }
}
