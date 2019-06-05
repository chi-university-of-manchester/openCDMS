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


package org.psygrid.outlook.patches.v1_1_10;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch38 extends AbstractPatch {

	@Override
	public String getName() {
		return "lock the following documents; CSSRI, baseline Time Use";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document cssri1 = ds.getDocument(22);
		if ( !"CSSRI".equals(cssri1.getName())){
			throw new RuntimeException("This is not the CSSRI (1) document - it is "+cssri1.getName());
		}
		for ( int i=0, c=cssri1.numOccurrences(); i<c; i++){
			cssri1.getOccurrence(i).setLocked(true);
		}
		Document cssri2 = ds.getDocument(27);
		if ( !"CSSRI".equals(cssri2.getName())){
			throw new RuntimeException("This is not the CSSRI (2) document - it is "+cssri2.getName());
		}
		for ( int i=0, c=cssri2.numOccurrences(); i<c; i++){
			cssri2.getOccurrence(i).setLocked(true);
		}
		Document timeUse = ds.getDocument(23);
		if ( !"Time Use Interview Score Sheet".equals(timeUse.getName())){
			throw new RuntimeException("This is not the Time Use Interview Score Sheet document - it is "+timeUse.getName());
		}
		DocumentOccurrence timeUseBaseline = timeUse.getOccurrence(0);
		if ( !"Baseline".equals(timeUseBaseline.getName())){
			throw new RuntimeException("This is not the Time Use Interview Score Sheet - Baseline occurrence, it is "+timeUseBaseline.getName());
		}
		timeUseBaseline.setLocked(true);
	}

}
