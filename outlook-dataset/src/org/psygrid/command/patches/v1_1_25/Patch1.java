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


package org.psygrid.command.patches.v1_1_25;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch1 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		Factory factory = new HibernateFactory();

		Document epq = ds.getDocument(15);
		if ( !"EPQv2".equals(epq.getName()) ){
			throw new RuntimeException("This is not the EPQ document, it is "+epq.getName());
		}

		Section employmentSec = epq.getSection(3);
		if ( !"Your employment section".equals(employmentSec.getName()) ){
			throw new RuntimeException("This is not the Your employment section, it is "+employmentSec.getName());
		}

		CompositeEntry qD1 = (CompositeEntry)epq.getEntry(35);
		if ( !"Which of the following describes how you have been employed in the last 18 months?".equals(qD1.getDisplayText()) ){
			throw new RuntimeException("This is not the 'Which of the following describes how you have been employed in the last 18 months?' entry, it is "+qD1.getDisplayText());
		}

        OptionEntry qD1Apply = factory.createOptionEntry(
                "Applicable", "Applicable");
        qD1Apply.addOption(factory.createOption("Yes", 1));
        qD1Apply.addOption(factory.createOption("No", 2));
        qD1Apply.setSection(employmentSec);

        qD1.insertEntry(qD1Apply, 1);

	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Correct EPQ for misplaced Applicable columns";
	}

}
