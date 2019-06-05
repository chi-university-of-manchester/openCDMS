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

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.IValue;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch15 extends AbstractPatch {

    @Override
    public String getName() {
        return "Companion patch for Patch9 - add responses for 'date of assessment' entries";
    }

    @Override
    public void applyPatch(DataSet ds, String saml) throws Exception {
        //do nothing
    }

    @Override
    public void postApplyPatch(DataSet ds, Object obj, RepositoryClient client, String saml) throws Exception {

        List<Document> docs = new ArrayList<Document>();
        Document panss = ds.getDocument(6);
        if ( !"PANSS".equals(panss.getName())){
            throw new RuntimeException("This is not the PANSS document, it is "+panss.getName());
        }
        docs.add(panss);
        Section panssSec = panss.getSection(0);
        if ( !"Header".equals(panssSec.getName())){
            throw new RuntimeException("This is not the PANSS Header Section, it is "+panssSec.getName());
        }
        SectionOccurrence panssSecOcc = panssSec.getOccurrence(0);
        DateEntry panssDate = (DateEntry)panss.getEntry(36);
        if ( !"Date of assessment".equals(panssDate.getName())){
            throw new RuntimeException("This is not the PANSS Date of assessment entry, it is "+panssDate.getName());
        }

        Document ym = ds.getDocument(7);
        if ( !"Young Mania".equals(ym.getName())){
            throw new RuntimeException("This is not the Young Mania document, it is "+ym.getName());
        }
        docs.add(ym);
        Section ymSec = ym.getSection(0);
        if ( !"Main section".equals(ymSec.getName())){
            throw new RuntimeException("This is not the Young Mania Main section, it is "+ymSec.getName());
        }
        SectionOccurrence ymSecOcc = ymSec.getOccurrence(0);
        DateEntry ymDate = (DateEntry)ym.getEntry(13);
        if ( !"Date of assessment".equals(ymDate.getName())){
            throw new RuntimeException("This is not the Young Mania Date of assessment entry, it is "+ymDate.getName());
        }

        Document gaf = ds.getDocument(8);
        if ( !"GAF Data Entry Sheet".equals(gaf.getName())){
            throw new RuntimeException("This is not the GAF Data Entry Sheet document, it is "+gaf.getName());
        }
        docs.add(gaf);
        Section gafSec = gaf.getSection(0);
        if ( !"Main section".equals(gafSec.getName())){
            throw new RuntimeException("This is not the GAF Main section, it is "+gafSec.getName());
        }
        SectionOccurrence gafSecOcc = gafSec.getOccurrence(0);
        DateEntry gafDate = (DateEntry)gaf.getEntry(4);
        if ( !"Date of assessment".equals(gafDate.getName())){
            throw new RuntimeException("This is not the GAF Date of assessment entry, it is "+gafDate.getName());
        }

        Document calgary = ds.getDocument(13);
        if ( !"Calgary".equals(calgary.getName())){
            throw new RuntimeException("This is not the Calgary document, it is "+calgary.getName());
        }
        docs.add(calgary);
        Section calgarySec = calgary.getSection(0);
        if ( !"Main".equals(calgarySec.getName())){
            throw new RuntimeException("This is not the Calgary Main section, it is "+calgarySec.getName());
        }
        SectionOccurrence calgarySecOcc = calgarySec.getOccurrence(0);
        DateEntry calgaryDate = (DateEntry)calgary.getEntry(11);
        if ( !"Date of assessment".equals(calgaryDate.getName())){
            throw new RuntimeException("This is not the Calgary Date of assessment entry, it is "+calgaryDate.getName());
        }

        Document insight = ds.getDocument(18);
        if ( !"Insight Scale Scoring".equals(insight.getName())){
            throw new RuntimeException("This is not the Insight Scale Scoring document, it is "+insight.getName());
        }
        docs.add(insight);
        Section insightSec = insight.getSection(0);
        if ( !"Main Section".equals(insightSec.getName())){
            throw new RuntimeException("This is not the Insight Main section, it is "+insightSec.getName());
        }
        SectionOccurrence insightSecOcc = insightSec.getOccurrence(0);
        DateEntry insightDate = (DateEntry)insight.getEntry(14);
        if ( !"Date of assessment".equals(insightDate.getName())){
            throw new RuntimeException("This is not the Insight Date of assessment entry, it is "+insightDate.getName());
        }

        //get standard codes
        List<StandardCode> stdCodes = client.getStandardCodes(saml);

        List<Record> records = client.getRecords(ds.getId(), saml);
        for ( Record record: records ){
            System.out.println("Processing record "+record.getIdentifier().getIdentifier());
            record.attach(ds);
            //see if we need to make modifications to this record
            boolean download = false;
            for ( Document doc: docs ){
                for ( int i=0; i<doc.numOccurrences(); i++ ){
                    if ( null != record.getDocumentInstance(doc.getOccurrence(i)) ){
                        download = true;
                    }
                }
            }
            if ( download ){
                System.out.println("Retrieving record "+record.getIdentifier().getIdentifier());
                Record r = client.getRecord(ds, record.getId(), saml);
                //PANSS
                for ( int i=0; i<panss.numOccurrences(); i++ ){
                    DocumentOccurrence docOcc = panss.getOccurrence(i);
                    DocumentInstance docInst = r.getDocumentInstance(docOcc);
                    if ( null != docInst ){
                        if ( null == docInst.getResponse(panssDate, panssSecOcc) ){
                            BasicResponse dateResp = panssDate.generateInstance(panssSecOcc);
                            docInst.addResponse(dateResp);
                            IValue dateVal = panssDate.generateValue();
                            dateResp.setValue(dateVal);
                            dateVal.setStandardCode(stdCodes.get(3));
                        }
                    }
                }
                //Young Mania
                for ( int i=0; i<ym.numOccurrences(); i++ ){
                    DocumentOccurrence docOcc = ym.getOccurrence(i);
                    DocumentInstance docInst = r.getDocumentInstance(docOcc);
                    if ( null != docInst ){
                        if ( null == docInst.getResponse(ymDate, panssSecOcc) ){
                            BasicResponse dateResp = ymDate.generateInstance(ymSecOcc);
                            docInst.addResponse(dateResp);
                            IValue dateVal = ymDate.generateValue();
                            dateResp.setValue(dateVal);
                            dateVal.setStandardCode(stdCodes.get(3));
                        }
                    }
                }
                //GAF
                for ( int i=0; i<gaf.numOccurrences(); i++ ){
                    DocumentOccurrence docOcc = gaf.getOccurrence(i);
                    DocumentInstance docInst = r.getDocumentInstance(docOcc);
                    if ( null != docInst ){
                        if ( null == docInst.getResponse(gafDate, gafSecOcc) ){
                            BasicResponse dateResp = gafDate.generateInstance(gafSecOcc);
                            docInst.addResponse(dateResp);
                            IValue dateVal = gafDate.generateValue();
                            dateResp.setValue(dateVal);
                            dateVal.setStandardCode(stdCodes.get(3));
                        }
                    }
                }
                //Calgary
                for ( int i=0; i<calgary.numOccurrences(); i++ ){
                    DocumentOccurrence docOcc = calgary.getOccurrence(i);
                    DocumentInstance docInst = r.getDocumentInstance(docOcc);
                    if ( null != docInst ){
                        if ( null == docInst.getResponse(calgaryDate, calgarySecOcc) ){
                            BasicResponse dateResp = calgaryDate.generateInstance(calgarySecOcc);
                            docInst.addResponse(dateResp);
                            IValue dateVal = calgaryDate.generateValue();
                            dateResp.setValue(dateVal);
                            dateVal.setStandardCode(stdCodes.get(3));
                        }
                    }
                }
                //Insight
                for ( int i=0; i<insight.numOccurrences(); i++ ){
                    DocumentOccurrence docOcc = insight.getOccurrence(i);
                    DocumentInstance docInst = r.getDocumentInstance(docOcc);
                    if ( null != docInst ){
                        if ( null == docInst.getResponse(insightDate, insightSecOcc) ){
                            BasicResponse dateResp = insightDate.generateInstance(insightSecOcc);
                            docInst.addResponse(dateResp);
                            IValue dateVal = insightDate.generateValue();
                            dateResp.setValue(dateVal);
                            dateVal.setStandardCode(stdCodes.get(3));
                        }
                    }
                }
                System.out.println("Saving record "+record.getIdentifier().getIdentifier());
                client.saveRecord(r, true, saml);
            }
        }

    }

}
