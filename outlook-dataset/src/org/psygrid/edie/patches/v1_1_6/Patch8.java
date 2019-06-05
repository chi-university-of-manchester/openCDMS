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

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch8 extends AbstractPatch {

	@Override
	public String getName() {
		return "Remove weird chars; fix NPHS name for export";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document dc = ds.getDocument(7);
		if ( !"Drug Check".equals(dc.getDisplayText())){
			throw new RuntimeException("This is not the Drug Check document, it is "+dc.getDisplayText());
		}
		Entry e1 = dc.getEntry(70);
		if ( !"Cannabis cost".equals(e1.getName())){
			throw new RuntimeException("This is not the Cannabis cost entry, it is "+e1.getName());
		}
		e1.setDisplayText("How much money (UKP) do you spend on cannabis per day?");

		Document nphs = ds.getDocument(9);
		if ( !"Non-Psychiatric Hospital Record Record".equals(nphs.getDisplayText())){
			throw new RuntimeException("This is not the Non-Psychiatric Hospital Record Record document, it is "+nphs.getDisplayText());
		}
		nphs.setName("NPHS");
		nphs.setDisplayText("Non-Psychiatric Hospital Record");

		Section nps1 = nphs.getSection(0);
		if ( !"Section A".equals(nps1.getName())){
			throw new RuntimeException("This is not the NPHS Section A section, it is "+nps1.getName());
		}
		SectionOccurrence npso1 = nps1.getOccurrence(0);
		npso1.setName("Section A Occurrence");

		Section nps2 = nphs.getSection(1);
		if ( !"Section B".equals(nps2.getName())){
			throw new RuntimeException("This is not the NPHS Section B section, it is "+nps2.getName());
		}
		SectionOccurrence npso2 = nps2.getOccurrence(0);
		npso2.setName("Section B Occurrence");

		Section nps3 = nphs.getSection(2);
		if ( !"Section C".equals(nps3.getName())){
			throw new RuntimeException("This is not the NPHS Section C section, it is "+nps3.getName());
		}
		SectionOccurrence npso3 = nps3.getOccurrence(0);
		npso3.setName("Section C Occurrence");

		Section nps4 = nphs.getSection(3);
		if ( !"Section D".equals(nps4.getName())){
			throw new RuntimeException("This is not the NPHS Section D section, it is "+nps4.getName());
		}
		SectionOccurrence npso4 = nps4.getOccurrence(0);
		npso4.setName("Section D Occurrence");

		Document phs = ds.getDocument(10);
		if ( !"Psychiatric Hospital Record Record".equals(phs.getDisplayText())){
			throw new RuntimeException("This is not the Psychiatric Hospital Record Record document, it is "+phs.getDisplayText());
		}
		phs.setDisplayText("Psychiatric Hospital Record");

		Section ps1 = phs.getSection(0);
		if ( !"Section A".equals(ps1.getName())){
			throw new RuntimeException("This is not the PHS Section A section, it is "+ps1.getName());
		}
		SectionOccurrence pso1 = ps1.getOccurrence(0);
		pso1.setName("Section A Occurrence");

		Section ps2 = phs.getSection(1);
		if ( !"Section B".equals(ps2.getName())){
			throw new RuntimeException("This is not the PHS Section B section, it is "+ps2.getName());
		}
		SectionOccurrence pso2 = ps2.getOccurrence(0);
		pso2.setName("Section B Occurrence");

		Section ps3 = phs.getSection(2);
		if ( !"Section C".equals(ps3.getName())){
			throw new RuntimeException("This is not the PHS Section C section, it is "+ps3.getName());
		}
		SectionOccurrence pso3 = ps3.getOccurrence(0);
		pso3.setName("Section C Occurrence");

		Section ps4 = phs.getSection(3);
		if ( !"Section D".equals(ps4.getName())){
			throw new RuntimeException("This is not the PHS Section D section, it is "+ps4.getName());
		}
		SectionOccurrence pso4 = ps4.getOccurrence(0);
		pso4.setName("Section D Occurrence");

		Section ps5 = phs.getSection(4);
		if ( !"Section E".equals(ps5.getName())){
			throw new RuntimeException("This is not the PHS Section E section, it is "+ps5.getName());
		}
		SectionOccurrence pso5 = ps5.getOccurrence(0);
		pso5.setName("Section E Occurrence");

		Document epq = ds.getDocument(11);
		if ( !"EPQv3".equals(epq.getName())){
			throw new RuntimeException("This is not the EPQv3 document, it is "+epq.getName());
		}

		Entry e2 = epq.getEntry(4);
		if ( !"Medication Costs".equals(e2.getName())){
			throw new RuntimeException("This is not the Medication Costs entry, it is "+e2.getName());
		}
		e2.setDisplayText("Prescribed, and over-the-counter, medications (UKP)?");

		Entry e3 = epq.getEntry(5);
		if ( !"Travel Costs".equals(e3.getName())){
			throw new RuntimeException("This is not the Travel Costs entry, it is "+e3.getName());
		}
		e3.setDisplayText("Travel costs (e.g. parking fees to attend any hospital, GP, or day care appointments) (UKP)?");

		Entry e4 = epq.getEntry(6);
		if ( !"Private Care Costs".equals(e4.getName())){
			throw new RuntimeException("This is not the Private Care Costs entry, it is "+e4.getName());
		}
		e4.setDisplayText("Private health care (include use of alternative therapies and practitioners) (UKP)?");

		Entry e5 = epq.getEntry(7);
		if ( !"One off expenses".equals(e5.getName())){
			throw new RuntimeException("This is not the One off expenses entry, it is "+e5.getName());
		}
		e5.setDisplayText("Over the last 3 months, are there any other MAJOR (UKP50+) one-off expenses that you have had to meet?");

		CompositeEntry ce1 = (CompositeEntry)epq.getEntry(8);
		if ( !"One off cost items".equals(ce1.getName())){
			throw new RuntimeException("This is not the One off cost items entry, it is "+ce1.getName());
		}
		Entry e6 = ce1.getEntry(1);
		if ( !"Other Costs".equals(e6.getName())){
			throw new RuntimeException("This is not the Other Costs entry, it is "+e6.getName());
		}
		e6.setDisplayText("Amount spent (during last 3 months, UKP)");

	}

}
