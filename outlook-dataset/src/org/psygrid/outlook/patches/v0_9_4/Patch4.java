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

package org.psygrid.outlook.patches.v0_9_4;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch4 extends AbstractPatch {

    public String getName() {
        return "Add the File Note Log document";
    }

    private class FileNoteLog extends AssessmentForm {
        public Document createDocument(Factory factory){
            Document log = factory.createDocument("File Note Log", "File Note Log");

            createDocumentStatuses(factory, log);

            // general section
            Section introSection = factory.createSection("Intro section");
            log.addSection(introSection);
            introSection.setDisplayText("Introduction");
            SectionOccurrence introSectionOcc = factory.createSectionOccurrence("Intro Section Occurrence");
            introSection.addOccurrence(introSectionOcc);

            NarrativeEntry instructions = factory.createNarrativeEntry("Instructions");
            instructions.setDisplayText("This document is intended to be used for recording general notes"+
                                        "about the client.");
            log.addEntry(instructions);
            instructions.setSection(introSection);

            NarrativeEntry instructions2 = factory.createNarrativeEntry("Instructions2");
            instructions2.setDisplayText("In order that you may add to this document throughout the course "+
                                         "of the study please ensure that you always save this as 'incomplete' by "+
                                         "using the 'Local - Save Incomplete Document' menu option.");
            log.addEntry(instructions2);
            instructions2.setSection(introSection);

            //notes section
            Section notesSec = factory.createSection("Notes Section");
            log.addSection(notesSec);
            notesSec.setDisplayText("Notes");
            SectionOccurrence notesOcc = factory.createSectionOccurrence("Notes Section Occurrence");
            notesSec.addOccurrence(notesOcc);
            notesOcc.setMultipleAllowed(true);

            DateEntry date = factory.createDateEntry("Date", "Date");
            log.addEntry(date);
            date.setSection(notesSec);

            LongTextEntry text = factory.createLongTextEntry("Notes", "Notes");
            log.addEntry(text);
            text.setSection(notesSec);

            return log;
        }

    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();
        FileNoteLog logCreator = new FileNoteLog();

        Document log = logCreator.createDocument(factory);

        ds.addDocument(log);
        ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
        if ( !"Main client consent".equals(cfg.getDescription())){
            throw new RuntimeException("This is not the Main Client Consent consent form group - it is "+cfg.getDescription());
        }
        log.addConsentFormGroup(cfg);
        DocumentOccurrence fileNoteLogBaseline = factory.createDocumentOccurrence("Baseline");
        fileNoteLogBaseline.setDisplayText("Baseline");
        DocumentGroup grp = ds.getDocumentGroup(0);
        if ( !"Baseline Sec A Group".equals(grp.getName())){
            throw new RuntimeException("This is not the Baseline Sec A doc group - it is "+grp.getName());
        }
        fileNoteLogBaseline.setDocumentGroup(grp);
        log.addOccurrence(fileNoteLogBaseline);

    }

}
