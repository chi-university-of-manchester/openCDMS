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

package org.psygrid.edie.patches.v1_0_1;

import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch3 extends AbstractPatch {

	@Override
	public String getName() {
		return "Change the eligibility criteria on the CAARMS";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		Document doc = ds.getDocument(1);
		if (!"CAARMS with GAF".equals(doc.getName())) {
			throw new RuntimeException(
					"This is not the 'CAARMS with GAF' form, it is "
							+ doc.getName());
		}
		Entry e6 = doc.getEntry(56);
		if (!"Attenuated Psychosis Group Criterion".equals(e6.getName())) {
			throw new RuntimeException(
					"This is not the 'Attenuated Psychosis Group Criterion' entry, it is " + e6.getName());
		} else {
			DerivedEntry de1 = (DerivedEntry)e6;
			de1.setFormula("if(((((a==1)&&(a1==1))||(c==1))&&(d==1)),1,0)");
			de1.removeVariable("b");
		}

        Entry e10 = doc.getEntry(3);
        if ( !"Unusual Thought Content Global Rating Scale".equals(e10.getName())) {
            throw new RuntimeException("This is not the 'Unusual Thought Content Global Rating Scale' entry, it is "+e10.getName());
        }
        OptionEntry oe10 = (OptionEntry)e10;
        oe10.getOption(1).removeOptionDependent(3);
        oe10.getOption(2).removeOptionDependent(3);
        oe10.getOption(3).removeOptionDependent(3);
        oe10.getOption(4).removeOptionDependent(3);
        oe10.getOption(5).removeOptionDependent(3);
        oe10.getOption(6).removeOptionDependent(3);

        Entry e9 = doc.getEntry(10);
        if ( !"Non-Bizarre Ideas Global Rating Scale".equals(e9.getName())) {
            throw new RuntimeException("This is not the 'Non-Bizarre Ideas Global Rating Scale' entry, it is "+e9.getName());
        }
        OptionEntry oe9 = (OptionEntry)e9;
        oe9.getOption(1).removeOptionDependent(3);
        oe9.getOption(2).removeOptionDependent(3);
        oe9.getOption(3).removeOptionDependent(3);
        oe9.getOption(4).removeOptionDependent(3);
        oe9.getOption(5).removeOptionDependent(3);
        oe9.getOption(6).removeOptionDependent(3);

        Entry e8 = doc.getEntry(17);
        if ( !"Perceptual Abnormalities Global Rating Scale".equals(e8.getName())) {
            throw new RuntimeException("This is not the 'Perceptual Abnormalities Global Rating Scale' entry, it is "+e8.getName());
        }
        OptionEntry oe8 = (OptionEntry)e8;
        oe8.getOption(1).removeOptionDependent(3);
        oe8.getOption(2).removeOptionDependent(3);
        oe8.getOption(3).removeOptionDependent(3);
        oe8.getOption(4).removeOptionDependent(3);
        oe8.getOption(5).removeOptionDependent(3);
        oe8.getOption(6).removeOptionDependent(3);

        Entry e7 = doc.getEntry(24);
        if ( !"Disorganised Speech Global Rating Scale".equals(e7.getName())) {
            throw new RuntimeException("This is not the 'Disorganised Speech Global Rating Scale' entry, it is "+e7.getName());
        }
        OptionEntry oe7 = (OptionEntry)e7;
        oe7.getOption(1).removeOptionDependent(3);
        oe7.getOption(2).removeOptionDependent(3);
        oe7.getOption(3).removeOptionDependent(3);
        oe7.getOption(4).removeOptionDependent(3);
        oe7.getOption(5).removeOptionDependent(3);
        oe7.getOption(6).removeOptionDependent(3);

		Entry e5 = doc.getEntry(52);
		if (!"Group 2a2".equals(e5.getName())) {
			throw new RuntimeException(
					"This is not the 'Group 2a2' entry, it is " + e5.getName());
		} else {
			doc.removeEntry(52);
		}
		Entry e4 = doc.getEntry(28);
		if (!"dissp Four Times".equals(e4.getName())) {
			throw new RuntimeException(
					"This is not the 'dissp Four Times' entry, it is "
							+ e4.getName());
		} else {
			doc.removeEntry(28);
		}
		Entry e3 = doc.getEntry(21);
		if (!"perab Four Times".equals(e3.getName())) {
			throw new RuntimeException(
					"This is not the 'perab Four Times' entry, it is "
							+ e3.getName());
		} else {
			doc.removeEntry(21);
		}
		Entry e2 = doc.getEntry(14);
		if (!"NBI Four Times".equals(e2.getName())) {
			throw new RuntimeException(
					"This is not the 'NBI Four Times' entry, it is "
							+ e2.getName());
		} else {
			doc.removeEntry(14);
		}
		Entry e1 = doc.getEntry(7);
		if (!"UTC Four Times".equals(e1.getName())) {
			throw new RuntimeException(
					"This is not the 'UTC Four Times' entry, it is "
							+ e1.getName());
		} else {
			doc.removeEntry(7);
		}
	}

    @Override
	public Object preApplyPatch(DataSet ds, RepositoryClient client,
			String saml) throws Exception {

		System.out.println("Entering preApplyPatch...");

		Document doc = ds.getDocument(1);
		if (!"CAARMS with GAF".equals(doc.getName())) {
			throw new RuntimeException(
					"This is not the 'CAARMS with GAF' form, it is "
							+ doc.getName());
		}

		Entry e1 = doc.getEntry(7);
		if (!"UTC Four Times".equals(e1.getName())) {
			throw new RuntimeException(
					"This is not the 'UTC Four Times' entry, it is "
							+ e1.getName());
		}

		Entry e2 = doc.getEntry(14);
		if (!"NBI Four Times".equals(e2.getName())) {
			throw new RuntimeException(
					"This is not the 'NBI Four Times' entry, it is "
							+ e2.getName());
		}

		Entry e3 = doc.getEntry(21);
		if (!"perab Four Times".equals(e3.getName())) {
			throw new RuntimeException(
					"This is not the 'perab Four Times' entry, it is "
							+ e3.getName());
		}

		Entry e4 = doc.getEntry(28);
		if (!"dissp Four Times".equals(e4.getName())) {
			throw new RuntimeException(
					"This is not the 'dissp Four Times' entry, it is "
							+ e4.getName());
		}

		Entry e5 = doc.getEntry(52);
		if (!"Group 2a2".equals(e5.getName())) {
			throw new RuntimeException(
					"This is not the 'Group 2a2' entry, it is " + e5.getName());
		}

		//Sections 1,2,3,4,8
		SectionOccurrence secOcc1 = doc.getSection(1).getOccurrence(0);
		SectionOccurrence secOcc2 = doc.getSection(2).getOccurrence(0);
		SectionOccurrence secOcc3 = doc.getSection(3).getOccurrence(0);
		SectionOccurrence secOcc4 = doc.getSection(4).getOccurrence(0);
		SectionOccurrence secOcc8 = doc.getSection(8).getOccurrence(0);

		List<Record> records = client.getRecords(ds.getId(), saml);

		//There are multiple occurences of the CAARMS, and this change affects
		//Sections 1,2,3,4,8

		for (Record record : records) {
			// There are 15 CAARMS document occurences in the EDIE data set
			for (int i = 0; i < 15; i++) {
				DocumentOccurrence docOcc = doc.getOccurrence(i);
				System.out.println("Processing record "
						+ record.getIdentifier().getIdentifier());
				record.attach(ds);
				DocumentInstance docInst = record.getDocumentInstance(docOcc);
				if (docInst != null) {
					System.out.println("Retrieving data for record "
							+ record.getIdentifier().getIdentifier());
					Record r = client.getRecordSingleDocument(record.getId(),
                            docInst.getId(), ds, saml);
					DocumentInstance di = r.getDocumentInstance(docOcc);
					if (di != null) {
                        di.removeResponse(e1, secOcc1);
                        di.removeResponse(e2, secOcc2);
                        di.removeResponse(e3, secOcc3);
                        di.removeResponse(e4, secOcc4);
                        di.removeResponse(e5, secOcc8);
						System.out.println("Saving record "
								+ record.getIdentifier().getIdentifier());
						client.saveRecord(r, true, saml);
					}
				}
			}
		}

		System.out.println("Exiting preApplyPatch.");
		return null;
	}

}
