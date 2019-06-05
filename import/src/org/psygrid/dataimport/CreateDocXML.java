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

package org.psygrid.dataimport;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.dataimport.jaxb.doc.Basicentrytype;
import org.psygrid.dataimport.jaxb.doc.Compositeentrytype;
import org.psygrid.dataimport.jaxb.doc.Documenttype;
import org.psygrid.dataimport.jaxb.doc.Importdoc;
import org.psygrid.dataimport.jaxb.doc.Inputtype;
import org.psygrid.dataimport.jaxb.doc.Occurrencetype;
import org.psygrid.dataimport.jaxb.doc.Sectionoccurrencetype;
import org.psygrid.dataimport.jaxb.doc.Sectiontype;
import org.psygrid.dataimport.jaxb.doc.Translationstype;
import org.psygrid.dataimport.jaxb.doc.Translationtype;

public class CreateDocXML {

    /**
     * @param args
     */
    public static void main(String[] args) {

        try{
            if ( args.length != 3 ){
                System.out.println("Usage: CreateXML <project code> <doc index> <output file>");
                return;
            }
            
            String projectCode = args[0];
            int docIndex = Integer.parseInt(args[1]);
            String outputFile = args[2];
            
            //get dataset
            RepositoryClient client = new RepositoryClient();
            DataSet ds = client.getDataSetSummary(projectCode, new Date(0), null);
            ds = client.getDataSet(ds.getId(), null);
            
            JAXBContext jc = JAXBContext.newInstance( "org.psygrid.dataimport.jaxb.doc" );
            Importdoc xmlImp = new Importdoc();
            
            Document doc = ds.getDocument(docIndex);
            
            Documenttype xmlDoc = new Documenttype();
            xmlImp.getDocument().add(xmlDoc);
            xmlDoc.setIndex(new BigInteger(Integer.toString(docIndex)));
            xmlDoc.setDescription(doc.getDisplayText());
            for ( int j=0; j<doc.numOccurrences(); j++ ){
                DocumentOccurrence occ = doc.getOccurrence(j);
                Occurrencetype xmlOcc = new Occurrencetype();
                xmlDoc.getOccurrence().add(xmlOcc);
                xmlOcc.setIndex(new BigInteger(Integer.toString(j)));
                xmlOcc.setDescription(occ.getDisplayText());
                for ( int k=0; k<doc.numSections(); k++ ){
                    Section sec = doc.getSection(k);
                    Sectiontype xmlSec = new Sectiontype();
                    xmlOcc.getSection().add(xmlSec);
                    xmlSec.setIndex(new BigInteger(Integer.toString(k)));
                    xmlSec.setDescription(sec.getDescription());
                    for ( int l=0; l<sec.numOccurrences(); l++ ){
                        SectionOccurrence secOcc = sec.getOccurrence(l);
                        Sectionoccurrencetype xmlSecOcc = new Sectionoccurrencetype();
                        xmlSec.getSectionoccurrence().add(xmlSecOcc);
                        xmlSecOcc.setIndex(new BigInteger(Integer.toString(l)));
                        xmlSecOcc.setMultipleRuntime(secOcc.isMultipleAllowed());
                        xmlSecOcc.setDescription(secOcc.getDescription());

                        for ( int m=0; m<doc.numEntries(); m++ ){
                            Entry e = doc.getEntry(m);
                            if ( sec.equals(e.getSection()) ){
                                if ( e instanceof BasicEntry){
                                    Basicentrytype xmlEntry = new Basicentrytype();
                                    xmlSecOcc.getBasicentry().add(xmlEntry);
                                    xmlEntry.setIndex(new BigInteger(Integer.toString(m)));
                                    xmlEntry.setDescription(e.getDisplayText());
                                    if ( e instanceof OptionEntry){
                                        handleOptionEntry((OptionEntry)e, xmlEntry);
                                    }
                                }
                                else if ( e instanceof CompositeEntry){
                                    CompositeEntry comp = (CompositeEntry)e;
                                    Compositeentrytype xmlComp = new Compositeentrytype();
                                    xmlSecOcc.getCompositeentry().add(xmlComp);
                                    xmlComp.setIndex(new BigInteger(Integer.toString(m)));
                                    xmlComp.setDescription(comp.getDisplayText());
                                    for ( int n=0; n<comp.numEntries(); n++ ){
                                        BasicEntry be = comp.getEntry(n);
                                        Basicentrytype xmlEntry = new Basicentrytype();
                                        xmlComp.getBasicentry().add(xmlEntry);
                                        xmlEntry.setIndex(new BigInteger(Integer.toString(n)));
                                        xmlEntry.setDescription(be.getDisplayText());
                                        if ( be instanceof OptionEntry){
                                            handleOptionEntry((OptionEntry)be, xmlEntry);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            FileOutputStream fos = new FileOutputStream(outputFile);
            m.marshal( xmlImp, fos );            
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private static void handleOptionEntry(OptionEntry oe, Basicentrytype xmlEntry){
        Translationstype xmlTranslations = new Translationstype();
        xmlEntry.setTranslations(xmlTranslations);
        for ( int i=0; i<oe.numOptions(); i++ ){
            Option opt = oe.getOption(i);
            Translationtype xmlTrans = new Translationtype();
            xmlTranslations.getTranslation().add(xmlTrans);
            StringBuilder builder = new StringBuilder();
            if ( null != opt.getCode() ){
                builder.append(opt.getCode());
                builder.append(". ");
            }
            builder.append(opt.getDisplayText());
            xmlTrans.setDescription("Translation for option "+builder.toString());
            if ( null != opt.getCode() ){
                Inputtype input = new Inputtype();
                input.setSpecial(false);
                input.setValue(opt.getCode().toString());
                xmlTrans.getInput().add(input);
            }
            xmlTrans.setOutput(Integer.toString(i));
        }
    }
    
}