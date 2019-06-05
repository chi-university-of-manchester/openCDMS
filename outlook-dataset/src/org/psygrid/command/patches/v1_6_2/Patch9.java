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

package org.psygrid.command.patches.v1_6_2;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.TimeUnits;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch9 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		DocumentGroup nineMonthGrp = ds.getDocumentGroup(1);
		if ( !"9 Month Follow Up".equals(nineMonthGrp.getDisplayText())){
			throw new RuntimeException("This is not the 9 Month Follow Up group, it is "+nineMonthGrp.getDisplayText());
		}
		DocumentGroup eighteenMonthGrp = ds.getDocumentGroup(2);
		if ( !"18 Month Follow Up".equals(eighteenMonthGrp.getDisplayText())){
			throw new RuntimeException("This is not the 18 Month Follow Up group, it is "+eighteenMonthGrp.getDisplayText());
		}

		for ( int i=0, c=ds.numDocuments(); i<c; i++ ){
			Document doc = ds.getDocument(i);
			for ( int j=0, d=doc.numOccurrences(); j<d; j++ ){
				DocumentOccurrence occ = doc.getOccurrence(j);
				if ( nineMonthGrp.equals(occ.getDocumentGroup()) ){
					occ.setScheduleTime(Integer.valueOf(274));
					occ.setScheduleUnits(TimeUnits.DAYS);
				}
				if ( eighteenMonthGrp.equals(occ.getDocumentGroup()) ){
					occ.setScheduleTime(Integer.valueOf(548));
					occ.setScheduleUnits(TimeUnits.DAYS);
				}
			}
		}

		ds.setSendMonthlySummaries(true);

	}

	@Override
	public String getName() {
		return "Add monthly summaries to Command";
	}

}
