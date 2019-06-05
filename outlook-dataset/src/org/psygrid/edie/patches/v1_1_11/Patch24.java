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

package org.psygrid.edie.patches.v1_1_11;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch24 extends AbstractPatch {

	@Override
	public String getName() {
		return "Change optionality on EPQv3";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		Document doc = ds.getDocument(11);
		if (!"EPQv3".equals(doc.getName())) {
			throw new RuntimeException(
					"This is not the 'EPQv3' form, it is "
							+ doc.getName());
		}
		Entry c2 = doc.getEntry(11);
		if(!c2.getName().equals("QC2")){
			throw new RuntimeException(
					"This is not the 'QC2' entry, it is "
							+ c2.getDisplayText());
		}

		Option c2Yes = ((OptionEntry)c2).getOption(0);
		if(!c2Yes.getDisplayText().equals("Yes (Go to C.3)")){
			throw new RuntimeException(
					"This is not the 'Yes (Go to C.3)' option, it is "
							+ c2Yes.getDisplayText());
		}
		if(!c2Yes.getOptionDependent(4).getDependentEntry().getName().equals("QC9")){
			throw new RuntimeException(
					"This is not the 'QC9' dependent entry, it is "
							+ c2Yes.getOptionDependent(4).getDependentEntry().getName());
		}
		c2Yes.getOptionDependent(4).getDependentEntry().setEntryStatus(EntryStatus.OPTIONAL);
		c2Yes.removeOptionDependent(4);
		if(!c2Yes.getOptionDependent(3).getDependentEntry().getName().equals("QC6")){
			throw new RuntimeException(
					"This is not the 'QC6' dependent entry, it is "
							+ c2Yes.getOptionDependent(3).getDependentEntry().getName());
		}
		c2Yes.getOptionDependent(3).getDependentEntry().setEntryStatus(EntryStatus.OPTIONAL);
		c2Yes.removeOptionDependent(3);
		if(!c2Yes.getOptionDependent(2).getDependentEntry().getName().equals("QC5")){
			throw new RuntimeException(
					"This is not the 'QC5' dependent entry, it is "
							+ c2Yes.getOptionDependent(2).getDependentEntry().getName());
		}
		c2Yes.getOptionDependent(2).getDependentEntry().setEntryStatus(EntryStatus.OPTIONAL);
		c2Yes.removeOptionDependent(2);
		if(!c2Yes.getOptionDependent(1).getDependentEntry().getName().equals("QC4")){
			throw new RuntimeException(
					"This is not the 'QC4' dependent entry, it is "
							+ c2Yes.getOptionDependent(1).getDependentEntry().getName());
		}
		c2Yes.getOptionDependent(1).getDependentEntry().setEntryStatus(EntryStatus.OPTIONAL);
		c2Yes.removeOptionDependent(1);
	}

}
