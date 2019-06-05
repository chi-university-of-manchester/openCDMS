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

package org.psygrid.outlook.test.patches.v1_1_6;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch3 extends AbstractPatch {

	@Override
	public boolean isolated(){
		return true;
	}

	@Override
	public String getName() {
		return "Update records with sites";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		return;
	}

    @Override
	public void postApplyPatch(DataSet ds, Object obj,
			RepositoryClient client, String saml) throws Exception {

		System.out.println("Entering postApplyPatch...");

		List<Record> records = client.getRecords(ds.getId(), saml);
		for (Record record : records) {
			System.out.println("Processing record "
					+ record.getIdentifier().getIdentifier());
			record.attach(ds);
			record.setSite(findDefaultSite(ds, record.getIdentifier().getGroupPrefix()));
			System.out.println("Saving record "
					+ record.getIdentifier().getIdentifier());
			client.saveRecord(record, true, saml);
		}
		System.out.println("Exiting postApplyPatch.");
	}

    public Site findDefaultSite(DataSet ds, String g) {
		//Just pick the first (only) site attached to the group
		int numGroups = ds.numGroups();
		for (int i = 0; i < numGroups; i++) {
			if (ds.getGroup(i).getName().equals(g)) {
				return ds.getGroup(i).getSite(0);
			}
		}
		return null;
	}

}
