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

import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch14 extends AbstractPatch {

    @Override
    public String getName() {
        return "Multiple changes to Pathways to Care";
    }

    @Override
    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();

        Document doc = ds.getDocument(11);
        if ( !"PathwaysToCare".equals(doc.getName()) ){
            throw new RuntimeException("This is not the PathwaysToCare document, it is "+doc.getName());
        }

        Section pathwaySec = doc.getSection(1);
        if ( !"Pathway Section".equals(pathwaySec.getName())){
            throw new RuntimeException("This is not the Pathway Section, it is "+pathwaySec.getName());
        }

        //1. Change display text of "Who was seen" entry
        Entry whoSeen = doc.getEntry(3);
        if (!"Who was seen option".equals(whoSeen.getName())){
            throw new RuntimeException("This is not the Who was seen option entry, it is "+whoSeen.getName());
        }
        whoSeen.setDisplayText("Who was contacted?");

        //2. Change display text of "How seen" entry
        Entry howSeen = doc.getEntry(4);
        if ( !"How seen option".equals(howSeen.getName())){
            throw new RuntimeException("This is not the How seen option entry, it is "+howSeen.getName());
        }
        howSeen.setDisplayText("How contacted?");

        //3. Remove "Unknown" option from "Who suggested" entry
        OptionEntry whoSuggested = (OptionEntry)doc.getEntry(7);
        if ( !"Who suggested care".equals(whoSuggested.getName())){
            throw new RuntimeException("This is not the Who suggested care entry, it is "+whoSuggested.getName());
        }
        int optionIndex = 10;
        Option unknown = whoSuggested.getOption(optionIndex);
        if ( !"Unknown".equals(unknown.getDisplayText())){
            throw new RuntimeException("This is not the Unknown option, it is "+unknown.getDisplayText());
        }
        whoSuggested.removeOption(10);

        //4. Insert source option entry at pos 3
        OptionEntry sourceOption = factory.createOptionEntry("Source", "Source");
        doc.insertEntry(sourceOption, 3);
        sourceOption.setSection(pathwaySec);
        Option sourceClientOption = factory.createOption("Client", 0);
        sourceOption.addOption(sourceClientOption);
        Option sourceCarerOption = factory.createOption("Carer/Family", 1);
        sourceOption.addOption(sourceCarerOption);
        Option sourceNotesOption = factory.createOption("Notes", 2);
        sourceOption.addOption(sourceNotesOption);
        Option sourceClientCarerOption = factory.createOption("Client and Carer", 3);
        sourceOption.addOption(sourceClientCarerOption);
        Option sourceOtherOption = factory.createOption("Other (Specify)", 4);
        sourceOption.addOption(sourceOtherOption);
        sourceOtherOption.setTextEntryAllowed(true);

    }

    @Override
    public boolean isolated() {
        return true;
    }

    @Override
    public void postApplyPatch(DataSet ds, Object obj, RepositoryClient client, String saml) throws Exception {

        /*
         * For any record where the "Unknown" option of the "Who
         * suggested care" option entry was referenced, we replace this
         * with the 960 standard code.
         * Also add a response for the new Source entry, with the standard
         * code 999.
         */

        Document doc = ds.getDocument(11);
        if ( !"PathwaysToCare".equals(doc.getName()) ){
            throw new RuntimeException("This is not the PathwaysToCare document, it is "+doc.getName());
        }

        Section pathwaySec = doc.getSection(1);
        if ( !"Pathway Section".equals(pathwaySec.getName())){
            throw new RuntimeException("This is not the Pathway Section, it is "+pathwaySec.getName());
        }

        OptionEntry source = (OptionEntry)doc.getEntry(3);
        if ( !"Source".equals(source.getName())){
            throw new RuntimeException("This is not the Source entry, it is "+source.getName());
        }

        OptionEntry whoSuggested = (OptionEntry)doc.getEntry(8);
        if ( !"Who suggested care".equals(whoSuggested.getName())){
            throw new RuntimeException("This is not the Who suggested care entry, it is "+whoSuggested.getName());
        }

        DocumentOccurrence docOcc = doc.getOccurrence(0);
        SectionOccurrence secOcc = pathwaySec.getOccurrence(0);

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

                for ( SecOccInstance soi: di.getSecOccInstances(secOcc) ){

                    //for all instances, add a response for the new "Source" entry, the
                    //value of which is the 999. std code.
                    BasicResponse sourceResp = source.generateInstance(soi);
                    di.addResponse(sourceResp);
                    IOptionValue sourceVal = source.generateValue();
                    sourceResp.setValue(sourceVal);
                    sourceVal.setStandardCode(stdCodes.get(3));
                }

                System.out.println("Saving record "+record.getIdentifier().getIdentifier());
                client.saveRecord(r, true, saml);

            }
        }

    }

    @Override
    public Object preApplyPatch(DataSet ds, RepositoryClient client, String saml) throws Exception {

        /*
         * Remove any reference to the "Unknown" option of the "Who
         * suggested care" option entry, and replace it with the std
         * code 960 Data not known.
         */

        Document doc = ds.getDocument(11);
        if ( !"PathwaysToCare".equals(doc.getName()) ){
            throw new RuntimeException("This is not the PathwaysToCare document, it is "+doc.getName());
        }

        Section pathwaySec = doc.getSection(1);
        if ( !"Pathway Section".equals(pathwaySec.getName())){
            throw new RuntimeException("This is not the Pathway Section, it is "+pathwaySec.getName());
        }

        OptionEntry whoSuggested = (OptionEntry)doc.getEntry(7);
        if ( !"Who suggested care".equals(whoSuggested.getName())){
            throw new RuntimeException("This is not the Who suggested care entry, it is "+whoSuggested.getName());
        }

        DocumentOccurrence docOcc = doc.getOccurrence(0);
        SectionOccurrence secOcc = pathwaySec.getOccurrence(0);

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

                for ( SecOccInstance soi: di.getSecOccInstances(secOcc) ){

                    BasicResponse br = (BasicResponse)di.getResponse(whoSuggested, soi);
                    IOptionValue ov = (IOptionValue)br.getValue();
                    if ( null != ov.getValue() ){
                        String optionText = ov.getValue().getDisplayText();
                        if ( "Unknown".equals(optionText) ){
                            ov.setValue(null);
                            ov.setStandardCode(stdCodes.get(0));
                        }
                    }

                    //Remember to do "old" values as well...
                    for ( Value v: br.getOldValues() ){
                        OptionValue oldOv = (OptionValue)v;
                        if ( null != oldOv.getValue() ){
                            String optionText = oldOv.getValue().getDisplayText();
                            if ( "Unknown".equals(optionText) ){
                                oldOv.setValue(null);
                                oldOv.setStandardCode(stdCodes.get(0));
                            }
                        }
                    }

                }

                System.out.println("Saving record "+record.getIdentifier().getIdentifier());
                client.saveRecord(r, true, saml);
            }
        }

        return null;

    }

}
