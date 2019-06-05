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


package org.psygrid.neden.patches.v1_1_28;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch40 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document relapse = ds.getDocument(26);
		if ( !"Relapse And Recovery".equals(relapse.getName())){
			throw new RuntimeException("This is not the Relapse And Recovery - it is "+relapse.getName());
		}
		relapse.setLongRunning(true);

		Document treatDoc = ds.getDocument(25);
		if ( !"TreatmentDocumentationV2".equals(treatDoc.getName())){
			throw new RuntimeException("This is not the TreatmentDocumentationV2 - it is "+treatDoc.getName());
		}
		treatDoc.setLongRunning(true);
	}

	@Override
	public String getName() {
		return "Set long running flags on documents";
	}

}
