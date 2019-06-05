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

import java.awt.Font;

import javax.swing.UIManager;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ApplicationModel;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.Fonts;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class NarrativeRendererTest extends AbstractEntryTestCase    {

    private Application app;
    private NarrativeEntry header;
    private NarrativeEntry normal;
    private NarrativeEntry small;
    private ApplicationModel model;
    private DocumentInstance docInstance;
    private Document doc;
    private Section section;
    private Entry entry;
    private Factory factory;
    private SectionOccurrence sectionOcc;
    
    private void init() throws Exception {
        factory = getFactory();
        Record record = getRecord();
        DataSet dataSet = record.getDataSet();
        doc = factory.createDocument("PersonalDetails");
        doc.addStatus(factory.createStatus("Incomplete", 0));
        DocumentOccurrence docOccurrence = factory
                .createDocumentOccurrence("Baseline");
        doc.addOccurrence(docOccurrence);
        dataSet.addDocument(doc);
        docInstance = doc.generateInstance(docOccurrence);
        setStatus(docInstance, docOccurrence.getDocument().getStatus(0));
        record.addDocumentInstance(docInstance);
        sectionOcc = getSectionOccurrence();
        section = sectionOcc.getSection();
        doc.addSection(section);
        app = createApplication();
        header = factory.createNarrativeEntry("Header");
        header.setDisplayText("header");
        header.setStyle(NarrativeStyle.HEADER);
        normal = factory.createNarrativeEntry("Normal");
        normal.setDisplayText("header");
        small = factory.createNarrativeEntry("Small");
        small.setDisplayText("small");
        small.setStyle(NarrativeStyle.SMALL);
        model = app.getModel();
        
        entry = factory.createNarrativeEntry("Name");
        entry.setDisplayText("Name");
        entry.setSection(section);
        doc.addEntry(entry);
    }
    
    public void testGetRenderer() throws Exception {
        invokeAndWait(new Executable()    {
            public void execute() throws Exception {
                init();
                BuilderHandler builderHandler = getBuilderHandler(docInstance, 
                        sectionOcc);
                RendererHandler rendererHandler = 
                    builderHandler.getCurrentRendererHandler();
                Renderer renderer = rendererHandler.getRenderer(entry, null);
                EntryLabel label = (EntryLabel) renderer.getLabel();
                assertEquals(entry.getDisplayText(), label.getText());
                assertEquals(1, renderer.getComponents().size());
                assertEquals(true, label.isEnabled());
            }
        });
    }
    
    public void testGetRendererSPI() throws Exception {
        invokeAndWait(new Executable()    {
            public void execute() throws Exception {
                init();
                BuilderHandler builderHandler = getBuilderHandler(docInstance, 
                        sectionOcc);
                RendererHandler rendererHandler = 
                    builderHandler.getCurrentRendererHandler();
                RendererSPI rspi = rendererHandler.getRendererSPI(entry, null);
                assertTrue(rspi instanceof NarrativeRendererSPI);
            }
        });
    }
    
    public void testHeaderFont() throws Exception {
        invokeAndWait(new Executable()    {
            public void execute() throws Exception {
                init();
                doc.addEntry(header);
                header.setSection(section);
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                Renderer r = model.getBuilderHandler().getRenderer(header, 0);
                Font expectedFont = Fonts.getInstance().getHeaderFont();
                assertTrue("Narrative with style header does not have the " +
                        "correct font",
                         expectedFont == r.getLabel().getFont());
            }
        });
    }
    
    public void testNormalFont() throws Exception {
        invokeAndWait(new Executable()    {
            public void execute() throws Exception {
                init();
                doc.addEntry(normal);
                normal.setSection(section);
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                Renderer r = model.getBuilderHandler().getRenderer(normal, 0);
                Font expectedFont = UIManager.getFont("Label.font");
                assertTrue("Narrative with style normal does not have the " +
                        "correct font",
                         expectedFont == r.getLabel().getFont());
            }
        });
    }
    
    public void testSmallFont() throws Exception {
        invokeAndWait(new Executable()    {
            public void execute() throws Exception {
                init();
                doc.addEntry(small);
                small.setSection(section);
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                Renderer r = model.getBuilderHandler().getRenderer(small, 0);
                Font expectedFont = Fonts.getInstance().getSmallFont();
                assertTrue("Narrative with style small does not have the " +
                        "correct font",
                         expectedFont == r.getLabel().getFont());
            }
        });
    }
    
    /**
     * This tests that INarrativeEntries can be enabled and disabled
     * like any other entry, see bug #545.
     * 
     * @throws Exception
     */
    public void testDisabledEntry() throws Exception    {
        invokeAndWait(new Executable()    {
            public void execute() throws Exception {
                init();
                OptionEntry conditionalEntry = factory.createOptionEntry(
                        "Conditional question", "Conditional question");
                Option yesOption = factory.createOption("Enable narrative entry");
                Option noOption = factory.createOption("Disable narrative entry");
                conditionalEntry.addOption(yesOption);
                conditionalEntry.addOption(noOption);
                doc.addEntry(conditionalEntry);
                conditionalEntry.setSection(section);
                createOptionDependent(factory, yesOption, entry, EntryStatus.MANDATORY);
                createOptionDependent(factory, noOption, entry, EntryStatus.DISABLED);

                entry.setEntryStatus(EntryStatus.DISABLED);
                app.setSelectedDocOccurrenceInstance(docInstance, 0);
                app.setVisible(true);
                Renderer r = model.getBuilderHandler().getRenderer(entry, 0);
                
                assertEquals(false, r.getLabel().isEnabled());
                
                RendererHandler rendererHandler = 
                    app.getModel().getCurrentRendererHandler();
                setOption(rendererHandler, conditionalEntry, 0, yesOption);
                assertEquals(true, r.getLabel().isEnabled());
                
                setOption(rendererHandler, conditionalEntry, 0, noOption);
                assertEquals(false, r.getLabel().isEnabled());
            }
        }); 
    }
    
    public static void main(String[] args) throws Exception {
        new NarrativeRendererTest().testDisabledEntry();
    }
}
