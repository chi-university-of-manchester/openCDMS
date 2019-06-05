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

import org.psygrid.data.model.hibernate.AssociatedConsentForm;
import org.psygrid.data.model.hibernate.ConsentFormGroup;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.PrimaryConsentForm;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.dataimport.jaxb.imp.Consentformgroupstype;
import org.psygrid.dataimport.jaxb.imp.Consentformgrouptype;
import org.psygrid.dataimport.jaxb.imp.Consentformtype;
import org.psygrid.dataimport.jaxb.imp.Import;
import org.psygrid.dataimport.jaxb.imp.Primaryconsentformtype;

public class CreateXML {

    /**
     * @param args
     */
    public static void main(String[] args) {

        try{
            if ( args.length != 2 ){
                System.out.println("Usage: CreateXML <project code> <output file>");
                return;
            }
            
            String projectCode = args[0];
            String outputFile = args[1];
            
            //get dataset
            RepositoryClient client = new RepositoryClient();
            DataSet ds = client.getDataSetSummary(projectCode, new Date(0), null);
            ds = client.getDataSet(ds.getId(), null);
            
            JAXBContext jc = JAXBContext.newInstance( "org.psygrid.dataimport.jaxb.imp" );
            Import xmlImp = new Import();
            xmlImp.setProject(ds.getProjectCode());
            
            Consentformgroupstype xmlGroups = new Consentformgroupstype();
            xmlImp.setConsentformgroups(xmlGroups);
            for ( int i=0; i<ds.numAllConsentFormGroups(); i++){
                ConsentFormGroup cfg = ds.getAllConsentFormGroup(i);
                Consentformgrouptype xmlCfg = new Consentformgrouptype();
                xmlGroups.getConsentformgroup().add(xmlCfg);
                xmlCfg.setIndex(new BigInteger(Integer.toString(i)));
                xmlCfg.setDescription(cfg.getDescription());
                for ( int j=0; j<cfg.numConsentForms(); j++ ){
                    PrimaryConsentForm pcf = cfg.getConsentForm(j);
                    Primaryconsentformtype xmlPcf = new Primaryconsentformtype();
                    xmlCfg.getPrimaryconsentform().add(xmlPcf);
                    xmlPcf.setIndex(new BigInteger(Integer.toString(j)));
                    xmlPcf.setDescription(pcf.getQuestion());
                    for ( int k=0; k<pcf.numAssociatedConsentForms(); k++ ){
                        AssociatedConsentForm acf = pcf.getAssociatedConsentForm(k);
                        Consentformtype xmlAcf = new Consentformtype();
                        xmlPcf.getAssociatedconsentform().add(xmlAcf);
                        xmlAcf.setIndex(new BigInteger(Integer.toString(k)));
                        xmlAcf.setDescription(acf.getQuestion());
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

}
