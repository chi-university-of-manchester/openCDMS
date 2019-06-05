/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.drn.address.patches.v1_6_4;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DerivedEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.NumericEntry;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch35 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document doc = ds.getDocument(5);
		if ( !"Medication and Clinical Measurements".equals(doc.getName())){
			throw new RuntimeException("This is not the Medication and Clinical Measurements document, it is "+doc.getName());
		}
		
		NumericEntry hip = (NumericEntry)doc.getEntry(31);
		if ( !"Hip circumference".equals(hip.getName())){
			throw new RuntimeException("This is not the Hip circumference entry, it is "+hip.getName());
		}

		DerivedEntry waistHipRatio = (DerivedEntry)doc.getEntry(32);
		if ( !"Waist to hip ratio".equals(waistHipRatio.getName())){
			throw new RuntimeException("This is not the Waist to hip ratio entry, it is "+waistHipRatio.getName());
		}
		
		waistHipRatio.removeVariable("h");
		
		waistHipRatio.addVariable("h", hip);
		
	}

	@Override
	public String getName() {
		return "Correct waist to hip ratio derived entry";
	}

}
