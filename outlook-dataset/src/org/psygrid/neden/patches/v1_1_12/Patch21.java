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
package org.psygrid.neden.patches.v1_1_12;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.TreatmentDocumentationV2;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch21 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document treatDoc1 = ds.getDocument(22);
		if ( !"TreatmentDocumentation".equals(treatDoc1.getName()) ){
			throw new RuntimeException("This is not the TreatmentDocumentation document, it is "+treatDoc1.getName());
		}
		treatDoc1.getOccurrence(0).setLocked(true);

		Factory factory = new HibernateFactory();
		Document treatDoc2 = TreatmentDocumentationV2.createDocument(factory);

		ds.addDocument(treatDoc2);

		ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
		if ( !"Main client consent".equals(cfg.getDescription()) ){
			throw new RuntimeException("This is not the 'Main client consent' CFG, it is "+cfg.getDescription());
		}
		treatDoc2.addConsentFormGroup(cfg);
		treatDoc2.setSecondaryDocIndex(31L);
        DocumentOccurrence tdb = factory.createDocumentOccurrence("Shared");
        tdb.setDisplayText("Shared");
        tdb.setSecondaryOccIndex(0L);
        treatDoc2.addOccurrence(tdb);
        DocumentGroup shared = ds.getDocumentGroup(3);
        if ( !"Shared".equals(shared.getName()) ){
        	throw new RuntimeException("This is not the Shared doc group, it is "+shared.getName());
        }
        tdb.setDocumentGroup(shared);

	}

	@Override
	public String getName() {
		return "Add v2 of Treatment Documentation form and lock original version";
	}

}
