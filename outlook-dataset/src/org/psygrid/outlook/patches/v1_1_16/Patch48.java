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


package org.psygrid.outlook.patches.v1_1_16;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
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
		Document aosqCarer = ds.getDocument(21);
		if ( !"Adverse Outcomes Screening Questionnaire (Carer)".equals(aosqCarer.getDisplayText())){
			throw new RuntimeException("This is not the Adverse Outcomes Screening Questionnaire (Carer) document, it is "+aosqCarer.getDisplayText());
		}
		aosqCarer.setName("Adverse Outcomes Screening Questionnaire (Carer)");

		Document aosqClient = ds.getDocument(26);
		if ( !"Adverse Outcomes Screening Questionnaire (Client)".equals(aosqClient.getDisplayText())){
			throw new RuntimeException("This is not the Adverse Outcomes Screening Questionnaire (Client) document, it is "+aosqClient.getDisplayText());
		}
		aosqClient.setName("Adverse Outcomes Screening Questionnaire (Client)");

	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Rename Adverse Outcomes Screening Questionnaire documents";
	}

}
