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

package org.psygrid.outlook.patches.v0_9_9;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.TreatmentDocumentation;
import org.psygrid.outlook.patches.AbstractPatch;


public class Patch11 extends AbstractPatch {

    public String getName() {
        return "Add Treatment documents";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();
        //TreatmentDocumentation treatDoc = new TreatmentDocumentation();
        Document td = TreatmentDocumentation.createDocument(factory);

        ds.addDocument(td);
        ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
        if ( !"Main client consent".equals(cfg.getDescription())){
            throw new RuntimeException("This is not the Main Client Consent consent form group - it is "+cfg.getDescription());
        }
        td.addConsentFormGroup(cfg);
        DocumentOccurrence td6 = factory.createDocumentOccurrence("6 months");
        td6.setDisplayText("6 months");
        DocumentGroup grp6 = ds.getDocumentGroup(3);
        if ( !"6 months Group".equals(grp6.getName())){
            throw new RuntimeException("This is not the 6 months doc group - it is "+grp6.getName());
        }
        td6.setDocumentGroup(grp6);
        td.addOccurrence(td6);

        DocumentOccurrence td12 = factory.createDocumentOccurrence("12 months");
        td12.setDisplayText("12 months");
        DocumentGroup grp12 = ds.getDocumentGroup(4);
        if ( !"12 months".equals(grp12.getName())){
            throw new RuntimeException("This is not the 12 months doc group - it is "+grp12.getName());
        }
        td12.setDocumentGroup(grp12);
        td.addOccurrence(td12);
    }

}
