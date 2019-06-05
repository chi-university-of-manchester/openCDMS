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


package org.psygrid.neden.patches.v1_1_15;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.neden.RelapseAndRecovery;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch25 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document relapseRating = ds.getDocument(23);
		if ( !"Relapse Rating".equals(relapseRating.getName())){
			throw new RuntimeException("This is not the Relapse Rating document, it is "+relapseRating.getName());
		}
		DocumentOccurrence relapseRatingOcc = relapseRating.getOccurrence(0);
		relapseRatingOcc.setLocked(true);

		Factory factory = new HibernateFactory();
		Document relapseAndRecovery = RelapseAndRecovery.createDocument(factory);

		ds.addDocument(relapseAndRecovery);

		ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
		if ( !"Main client consent".equals(cfg.getDescription()) ){
			throw new RuntimeException("This is not the 'Main client consent' CFG, it is "+cfg.getDescription());
		}
		relapseAndRecovery.addConsentFormGroup(cfg);
        DocumentOccurrence rrb = factory.createDocumentOccurrence("Shared");
        rrb.setDisplayText("Shared");
        rrb.setSecondaryOccIndex(0L);
        relapseAndRecovery.addOccurrence(rrb);
        DocumentGroup shared = ds.getDocumentGroup(3);
        if ( !"Shared".equals(shared.getName()) ){
        	throw new RuntimeException("This is not the Shared doc group, it is "+shared.getName());
        }
        rrb.setDocumentGroup(shared);

	}

	@Override
	public String getName() {
		return "Add Relapse and Recovery form; lock Relapse Rating form";
	}

}
