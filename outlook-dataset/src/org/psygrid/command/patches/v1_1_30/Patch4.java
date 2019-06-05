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

package org.psygrid.command.patches.v1_1_30;

import org.psygrid.command.HistoryOfSuicideV2;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch4 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document hos = ds.getDocument(11);
		if ( !"History of Suicide".equals(hos.getName()) ){
			throw new RuntimeException("This is not the History of Suicide document, it is "+hos.getName());
		}
		hos.getOccurrence(0).setLocked(true);

		ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
		DocumentGroup baseline = ds.getDocumentGroup(0);
		if ( !"Baseline Group".equals(baseline.getName())){
			throw new RuntimeException("This is not the Baseline Group, it is "+baseline.getName());
		}

		Factory factory = new HibernateFactory();
		Document suicideHistoryV2 = HistoryOfSuicideV2.createDocument(factory);
		ds.addDocument(suicideHistoryV2);
		suicideHistoryV2.addConsentFormGroup(cfg);
		DocumentOccurrence suicideHistoryV2Baseline = factory.createDocumentOccurrence("Baseline");
		suicideHistoryV2Baseline.setDisplayText("Baseline");
		suicideHistoryV2Baseline.setDocumentGroup(baseline);
		suicideHistoryV2.addOccurrence(suicideHistoryV2Baseline);

	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Lock Suicide History form, add Suicide History V2";
	}

}
