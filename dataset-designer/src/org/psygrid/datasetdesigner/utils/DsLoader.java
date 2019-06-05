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
package org.psygrid.datasetdesigner.utils;

import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.data.model.hibernate.DataSet;

public class DsLoader {

	private static boolean canPatchDataset = false;

	public static boolean isCanPatchDataset() {
		return canPatchDataset;
	}

	protected static void setCanPatchDataset(DataSet ds) {
		try {
			canPatchDataset = SecurityManager.getInstance().canPatchStudy(ds);
		} catch (Exception e) {
			//Do nothing - canPatchDataset will not be changed.
		} 
	}
	
	
	
}
