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
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch60 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		Group notts = (Group)ds.getGroup(3);
		if ( !"East Midlands-Nottinghamshire Healthcare NHS Trust".equals(notts.getLongName())){
			throw new RuntimeException("This is not the East Midlands-Nottinghamshire Healthcare NHS Trust, it is "+notts.getLongName());
		}

		Group lincs = (Group)ds.getGroup(4);
		if ( !"East Midlands-Lincolnshire Partnership Trust".equals(lincs.getLongName())){
			throw new RuntimeException("This is not the East Midlands-Lincolnshire Partnership Trust, it is "+lincs.getLongName());
		}
		lincs.setLongName("East Midlands-Lincolnshire Partnership NHS Foundation Trust");

		notts.getSites().remove(0);
		lincs.getSites().remove(0);

		notts.addSite(new Site("Notts City and South EIP", "N0003742", "NG3 6AA", notts));
		notts.addSite(new Site("Mansfield and Ashfield EIP", "N0003743", "NG17 4HJ", notts));
		notts.addSite(new Site("Newark and Sherwood EIP", "N0003744", "NG24 4DE", notts));
		for ( Site s: notts.getSites()){
			s.addConsultant("Peter Liddle");
		}

		lincs.addSite(new Site("Grantham STEP", "N0003745", "NG31 9DF", lincs));
		lincs.addSite(new Site("Boston STEP", "N0003746", "PE21 0AX", lincs));
		lincs.addSite(new Site("Lincoln STEP", "N0003747", "LN1 1PB", lincs));
		for ( Site s: lincs.getSites()){
			s.addConsultant("Peter Ellwood");
		}
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Outlook site corrections for East Mids";
	}

}
