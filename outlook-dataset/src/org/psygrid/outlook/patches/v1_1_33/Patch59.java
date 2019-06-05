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
package org.psygrid.outlook.patches.v1_1_33;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch59 extends AbstractPatch {

    public String getName() {
        return "Update the accessibility of the study termination document group for Outlook dataset";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

    	//Get statuses to be used for study termination doc group
		Status statConsented = null;
		Status statInterview1 = null;
		Status statInterview2 = null;
		Status statInterview3 = null;
		Status stat6Month = null;

		for (Status status: ((DataSet)ds).getStatuses()) {
			if (status.getShortName().equals("Consented")) {
				statConsented = status;
			}
			if (status.getShortName().equals("Interview1")) {
				statInterview1 = status;
			}
			if (status.getShortName().equals("Interview2")) {
				statInterview2 = status;
			}
			if (status.getShortName().equals("Interview3")) {
				statInterview3 = status;
			}
			if (status.getShortName().equals("6Month")) {
				stat6Month = status;
			}
		}

		//Add the statuses to the study termination document group
		for (DocumentGroup docGroup: ((DataSet)ds).getDocumentGroups()) {
			if (docGroup.getDisplayText().equals("Study termination")) {
				docGroup.addAllowedRecordStatus(statConsented);
				docGroup.addAllowedRecordStatus(statInterview1);
				docGroup.addAllowedRecordStatus(statInterview2);
				docGroup.addAllowedRecordStatus(statInterview3);
				docGroup.addAllowedRecordStatus(stat6Month);
				break;
			}
		}


    }

}
