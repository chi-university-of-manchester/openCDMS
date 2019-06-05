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


package org.psygrid.neden.patches.v1_1_13;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch24 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document opcrit = ds.getDocument(24);
		if ( !"Opcrit Data Entry Sheet".equals(opcrit.getName())){
			throw new RuntimeException("This is not the Opcrit document, it is "+opcrit.getName());
		}

		DocumentOccurrence opcritOcc = opcrit.getOccurrence(0);
		opcritOcc.setName("Baseline");
		opcritOcc.setDisplayText("Baseline");

		DocumentGroup baseline = ds.getDocumentGroup(0);
		if ( !"Baseline Group".equals(baseline.getName())){
			throw new RuntimeException("This is not the Baseline group, it is "+baseline.getName());
		}

		opcritOcc.setDocumentGroup(baseline);

	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Move Opcrit from 12 Months to Baseline";
	}

}
