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


package org.psygrid.drn.address.patches.v1_1_21;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch3 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document bioAndUrin = ds.getDocument(4);
		if ( !"Biochemistry and Urinalysis".equals(bioAndUrin.getName())){
			throw new RuntimeException("This is not the Biochemistry and Urinalysis document, it is "+bioAndUrin.getName());
		}
		Entry ldl1 = bioAndUrin.getEntry(24);
		if ( !"LDL".equals(ldl1.getName())){
			throw new RuntimeException("This is not the LDL entry, it is "+ldl1.getName());
		}
		ldl1.setDisplayText("Low density lipoprotein (LDL)");


		Document bio = ds.getDocument(6);
		if ( !"Biochemistry".equals(bio.getName())){
			throw new RuntimeException("This is not the Biochemistry document, it is "+bio.getName());
		}
		Entry ldl2 = bio.getEntry(15);
		if ( !"LDL".equals(ldl2.getName())){
			throw new RuntimeException("This is not the LDL entry, it is "+ldl2.getName());
		}
		ldl2.setDisplayText("Low density lipoprotein (LDL)");

	}

	@Override
	public String getName() {
		return "Correct LDL entry text";
	}

}
