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

package org.psygrid.neden.patches.v1_1_9;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.Opcrit;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch16 extends AbstractPatch {

	@Override
	public String getName() {
		return "Add the new Opcrit document to the NEDEN dataset";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		Factory factory = new HibernateFactory();

		System.out.println("Creating new Opcrit document");
		Document opcrit = Opcrit.createDocument(factory);
        ds.addDocument(opcrit);

        ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
        if ( !"Main client consent".equals(cfg.getDescription())){
            throw new RuntimeException("This is not the Main Client Consent consent form group - it is "+cfg.getDescription());
        }

        //Explictly set the transformer
        Transformer transformer = ds.getTransformer(ds.numTransformers() - 1);
        if (!"opcrit".equals(transformer.getWsOperation())) {
        	throw new RuntimeException("This is not the Opcrit transformer - it is "+transformer.getWsOperation());
        }
        ((ExternalDerivedEntry)opcrit.getEntry(opcrit.numEntries()-1)).setExternalTransformer(transformer);

        opcrit.addConsentFormGroup(cfg);
        DocumentOccurrence opcritOcc = factory.createDocumentOccurrence("12 Months");
        opcritOcc.setDisplayText("12 Months");
        opcrit.addOccurrence(opcritOcc);

        DocumentGroup twelveMonths = ds.getDocumentGroup(2);
        if ( !"12 months Group".equals(twelveMonths.getName())){
            throw new RuntimeException("This is not the 12 months doc group - it is "+twelveMonths.getName());
        }
        opcritOcc.setDocumentGroup(twelveMonths);
	}

}
