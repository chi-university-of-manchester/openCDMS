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

package org.psygrid.outlook.patches.v1_0_5;

import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.IValue;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch11 extends AbstractPatch {

    @Override
    public String getName() {
        return "Study Termination Record - fix status of Primary Reason and Any Other Information";
    }

    @Override
    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();

        Document doc = ds.getDocument(28);
        if ( !"Study Termination Record".equals(doc.getName()) ){
            throw new RuntimeException("This is not the Study Termination Record document, it is "+doc.getName());
        }

        Section mainSec = doc.getSection(0);

        OptionEntry baseline = (OptionEntry)doc.getEntry(1);
        if ( !"Baseline".equals(baseline.getName())){
            throw new RuntimeException("This is not the Baseline entry, it is "+baseline.getName());
        }
        Option blYes = baseline.getOption(1);
        if ( !"Yes".equals(blYes.getName())){
            throw new RuntimeException("This is not the Yes option, it is "+blYes.getName());
        }
        blYes.removeOptionDependent(2);
        blYes.removeOptionDependent(1);
        blYes.removeOptionDependent(0);

        Entry secA = doc.getEntry(2);
        if ( !"Section A".equals(secA.getName())){
            throw new RuntimeException("This is not the Section A entry, it is "+secA.getName());
        }
        secA.setEntryStatus(EntryStatus.MANDATORY);

        Entry secB = doc.getEntry(3);
        if ( !"Section B".equals(secB.getName())){
            throw new RuntimeException("This is not the Section B entry, it is "+secB.getName());
        }
        secB.setEntryStatus(EntryStatus.MANDATORY);

        Entry secC = doc.getEntry(4);
        if ( !"Section C".equals(secC.getName())){
            throw new RuntimeException("This is not the Section C entry, it is "+secC.getName());
        }
        secC.setEntryStatus(EntryStatus.MANDATORY);

        Entry reason = doc.getEntry(7);
        if ( !"Primary reason".equals(reason.getName())){
            throw new RuntimeException("This is not the Primary reason entry, it is "+reason.getName());
        }

        reason.setEntryStatus(EntryStatus.MANDATORY);

        LongTextEntry otherInfo = factory.createLongTextEntry("Other Info", "Any other information");
        doc.addEntry(otherInfo);
        otherInfo.setSection(mainSec);

    }

    @Override
    public boolean isolated() {
        return true;
    }

    @Override
    public void postApplyPatch(DataSet ds, Object obj, RepositoryClient client, String saml) throws Exception {

        Document doc = ds.getDocument(28);
        if ( !"Study Termination Record".equals(doc.getName()) ){
            throw new RuntimeException("This is not the Study Termination Record document, it is "+doc.getName());
        }
        DocumentOccurrence docOcc = doc.getOccurrence(0);

        Section mainSec = doc.getSection(0);
        SectionOccurrence secOcc = mainSec.getOccurrence(0);

        LongTextEntry otherInfo = (LongTextEntry)doc.getEntry(8);
        if ( !"Other Info".equals(otherInfo.getName())){
            throw new RuntimeException("This is not the Other Info entry, it is "+otherInfo.getName());
        }

        //get standard codes
        List<StandardCode> stdCodes = client.getStandardCodes(saml);

        List<Record> records = client.getRecords(ds.getId(), saml);
        for ( Record record: records ){
            System.out.println("Processing record "+record.getIdentifier().getIdentifier());
            record.attach(ds);
            DocumentInstance docInst = record.getDocumentInstance(docOcc);
            if ( null != docInst ){

                System.out.println("Retrieving data for record "+record.getIdentifier().getIdentifier());
                Record r = client.getRecordSingleDocument(record.getId(), docInst.getId(), ds, saml);
                DocumentInstance di = r.getDocumentInstance(docOcc);

                //Apply standard code 999 to Other Info entry for all existing
                //instances of Study Termination Record
                BasicResponse br = (BasicResponse)otherInfo.generateInstance(secOcc);
                di.addResponse(br);
                IValue val = otherInfo.generateValue();
                br.setValue(val);
                val.setStandardCode(stdCodes.get(3));

                System.out.println("Saving record "+record.getIdentifier().getIdentifier());
                client.saveRecord(r, true, saml);
            }
        }
    }

}
