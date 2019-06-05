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


package org.psygrid.edie.patches.v1_1_6;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.TimeUnits;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch7 extends AbstractPatch {

	@Override
	public String getName() {
		return "Add units for occurrence scheduled times";
	}

    @Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ds.setSendMonthlySummaries(true);
		for(int i=0;i<ds.numDocuments();i++){
			for(int j=0;j<ds.getDocument(i).numOccurrences();j++){
				DocumentOccurrence docOcc = ds.getDocument(i).getOccurrence(j);
				if ( null != docOcc.getScheduleTime() && null == docOcc.getScheduleUnits() ){
					docOcc.setScheduleUnits(TimeUnits.DAYS);
				}
			}
		}
	}

}