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

package org.psygrid.outlook.patches.v1_1_5;

import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.IValue;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch20 extends AbstractPatch {

    @Override
    public String getName() {
        return "Add the 'Date for onset of subthreshold treatment' question to DUP";
    }

    @Override
    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();

        ValidationRule after1900 = ds.getValidationRule(15);
        if ( !"After 1900".equals(after1900.getDescription()) ){
            throw new RuntimeException("This is not the After 1900 validation rule, it is "+after1900.getDescription());
        }

        Document doc = ds.getDocument(9);
        if ( !"DUP".equals(doc.getName()) ){
            throw new RuntimeException("This is not the DUP document, it is "+doc.getName());
        }

        Section sec = doc.getSection(0);

        DateEntry de = factory.createDateEntry("Onset subthreshold", "Onset of subthreshold treatment");
        de.setSection(sec);
        de.addValidationRule(after1900);

        int index = 5;
        Entry e = doc.getEntry(index);
        if ( !"Referral date".equals(e.getName())){
            throw new RuntimeException("This is not the Referral date entry, it is "+e.getName());
        }

        doc.insertEntry(de, index);

    }

    @Override
    public boolean isolated() {
        return true;
    }

    @Override
    public void postApplyPatch(DataSet ds, Object obj, RepositoryClient client, String saml) throws Exception {

        Document doc = ds.getDocument(9);
        if ( !"DUP".equals(doc.getName()) ){
            throw new RuntimeException("This is not the DUP document, it is "+doc.getName());
        }
        DocumentOccurrence docOcc = doc.getOccurrence(0);

        Section mainSec = doc.getSection(0);
        SectionOccurrence secOcc = mainSec.getOccurrence(0);

        DateEntry e = (DateEntry)doc.getEntry(5);
        if ( !"Onset subthreshold".equals(e.getName())){
            throw new RuntimeException("This is not the Onset subthreshold date entry, it is "+e.getName());
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

                //Apply standard code 999 to Onset Subthreshold entry for all existing
                //instances of DUP form
                BasicResponse br = (BasicResponse)e.generateInstance(secOcc);
                di.addResponse(br);
                IValue val = e.generateValue();
                br.setValue(val);
                val.setStandardCode(stdCodes.get(3));

                System.out.println("Saving record "+record.getIdentifier().getIdentifier());
                client.saveRecord(r, true, saml);
            }
        }
    }

}
