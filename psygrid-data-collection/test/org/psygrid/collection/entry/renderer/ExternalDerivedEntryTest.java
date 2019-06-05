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
import org.psygrid.collection.entry.Application;
import org.psygrid.data.model.hibernate.*;

public class ExternalDerivedEntryTest extends AbstractEntryTestCase {
	    
    private DataSet dataSet;
    private Document document;
    private DocumentOccurrence docOccurrence;
    private Application application;
    private SectionOccurrence sectionOcc;
    private DocumentInstance docInstance;
    private Record record;
    private Factory factory;
    

    
    private void init() throws Exception {
        application = createApplication();
        record = getRecord();
        dataSet = record.getDataSet();
        factory = getFactory();
        
        document = createDocument(factory);
        
        docInstance = document.generateInstance(docOccurrence);
        dataSet.addDocument(document);
        record.addDocumentInstance(docInstance);
        dataSet.addDocument(document);

    }
	
	    public static Document createDocument(Factory factory) {
	        
	        Document doc = factory.createDocument("Opcrit Test Data Entry Sheet",
                    "Opcrit Test Data Entry Record");
	       
	        //TODO createDocumentStatuses(factory, doc);
	        
	        Section mainSec = factory.createSection("Main section");
	        doc.addSection(mainSec);
	        mainSec.setDisplayText("Main");
	        SectionOccurrence mainSecOcc = factory.createSectionOccurrence(
                    "Main Section Occurrence");
	        mainSec.addOccurrence(mainSecOcc);
	         
	        NarrativeEntry instructions = factory.createNarrativeEntry("Instructions",
                    "Instructions: Select an answer for each question");
	        doc.addEntry(instructions);
	        instructions.setSection(mainSec);
	        
	        String description = "Some example questions.";
	        
	        OptionEntry q1 = factory.createOptionEntry("Q1", "Question 1");
	        q1.addOption(factory.createOption("Foo", 1));
	        q1.addOption(factory.createOption("Bar", 2));
	        doc.addEntry(q1);
	        q1.setSection(mainSec);
	        q1.setLabel("1");
	        q1.setDescription(description);
	        
	        OptionEntry q2 = factory.createOptionEntry("Q2", "Question 2");
	        q2.addOption(factory.createOption("One", 1));
	        q2.addOption(factory.createOption("Two", 2));
	        doc.addEntry(q2);
	        q2.setSection(mainSec);
	        q2.setLabel("2");
	        q2.setDescription(description);
	        
	        ExternalDerivedEntry exDE = factory.createExternalDerivedEntry("opcrit", "Opcrit's diagnosis");
	       // exDE.setExternalClient("Opcrit");
	        //ITransformer transformer = factory.createTransformer(wsUrl, wsNamespace, wsOperation, resultClass, viewableOutput);
	        //exDE.addTransformer(transformer);
	        doc.addEntry(exDE);
	        exDE.setSection(mainSec);
	        exDE.setLabel("3");
	        //exDE.setFormula("a+b");
	        exDE.addVariable("a", q1);
	        exDE.addVariable("b", q2);
	 
	        final String formula = "(a+b)/2";
	        DerivedEntry socialAdulthood3 = factory.createDerivedEntry(
                    "Derived Entry Test", "Derived Entry Test");
	        doc.addEntry(socialAdulthood3);
	        socialAdulthood3.setSection(mainSec);
	        socialAdulthood3.addVariable("a", q1);
	        socialAdulthood3.addVariable("b", q2);
	        socialAdulthood3.setFormula(formula);
	        socialAdulthood3.setLabel("4");
	        
	        ExternalDerivedEntry fred = factory.createExternalDerivedEntry("fred", "Test ExternalDerivedEntry");
	        doc.addEntry(fred);
	        fred.setSection(mainSec);
	        fred.setLabel("5");
	        fred.setDescription(description);

	        return doc;       
	    }

}
