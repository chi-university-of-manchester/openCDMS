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
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch37 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		//Note: commented-out until the required repository changes have been committed
		//to trunk.
		//ds.setExportSecurityActive(false);
	}

	@Override
	public String getName() {
		return "Set ExportSecurityActive to false for OUTLOOK";
	}

}
