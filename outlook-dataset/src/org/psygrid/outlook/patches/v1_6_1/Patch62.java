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

package org.psygrid.outlook.patches.v1_6_1;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch62 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();
		for ( int i=0, c=ds.numDocuments(); i<c; i++ ){
			Document doc = ds.getDocument(i);
			Status incomplete = doc.getStatus(0);
			if ( !"Incomplete".equals(incomplete.getShortName()) ){
				throw new RuntimeException("This is not the Incomplete status - it is "+incomplete.getShortName());
			}
			Status pending = doc.getStatus(1);
			if ( !"Pending".equals(pending.getShortName()) ){
				throw new RuntimeException("This is not the Pending status - it is "+pending.getShortName());
			}
			Status complete = factory.createStatus(Status.DOC_STATUS_COMPLETE, "Complete", 4);
			doc.addStatus(complete);
			incomplete.addStatusTransition(complete);
			complete.addStatusTransition(incomplete);
			complete.addStatusTransition(pending);
		}
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Add the Complete status to all documents";
	}

}
