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

package org.psygrid.neden.patches.v1_6_4;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.FileNoteLog;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch48 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		DocumentGroup shared = ds.getDocumentGroup(3);
		if ( !"Shared".equals(shared.getDisplayText())){
			throw new RuntimeException("This is not the Shared group, it is "+shared.getDisplayText());
		}
		
		ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
		
		Factory factory = new HibernateFactory();
		Document fnl = FileNoteLog.createDocument(factory);
		ds.addDocument(fnl);
		fnl.addConsentFormGroup(cfg);
		
		DocumentOccurrence fnlOcc = factory.createDocumentOccurrence("Shared");
		fnlOcc.setDisplayText("Shared");
		fnlOcc.setDocumentGroup(shared);
		fnl.addOccurrence(fnlOcc);
		
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Add File Note log to NED";
	}

}
