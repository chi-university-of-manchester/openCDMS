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


package org.psygrid.neden.patches.v1_1_6;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch11 extends AbstractPatch {

	public String getName() {
		return "Correct Adverse Outcomes occurrence names";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document d1 = ds.getDocument(12);
        if ( !"Adverse Outcomes Detailed Questionnaire (Self-Harm)".equals(d1.getName()) ){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Self-Harm) document, it is "+d1.getName());
        }

        DocumentOccurrence d1o1 = d1.getOccurrence(0);
        if ( !"Client, Most serious, Baseline".equals(d1o1.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Self-Harm) - Client, Most serious, Baseline occurrence, it is "+d1o1.getDisplayText());
        }
        d1o1.setName("Client, Most serious, Baseline");

        DocumentOccurrence d1o2 = d1.getOccurrence(1);
        if ( !"Client, Closest to contact, Baseline".equals(d1o2.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Self-Harm) - Client, Closest to contact, Baseline occurrence, it is "+d1o2.getDisplayText());
        }
        d1o2.setName("Client, Closest to contact, Baseline");

		Document d2 = ds.getDocument(13);
        if ( !"Adverse Outcomes Detailed Questionnaire (Violence)".equals(d2.getName()) ){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Violence) document, it is "+d2.getName());
        }

        DocumentOccurrence d2o1 = d2.getOccurrence(0);
        if ( !"Client, Most serious, Baseline".equals(d2o1.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Violence) - Client, Most serious, Baseline occurrence, it is "+d2o1.getDisplayText());
        }
        d2o1.setName("Client, Most serious, Baseline");

        DocumentOccurrence d2o2 = d2.getOccurrence(1);
        if ( !"Client, Closest to contact, Baseline".equals(d2o2.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Violence) - Client, Closest to contact, Baseline occurrence, it is "+d2o2.getDisplayText());
        }
        d2o2.setName("Client, Closest to contact, Baseline");

		Document d3 = ds.getDocument(14);
        if ( !"Adverse Outcomes Detailed Questionnaire (Carer, Self-Harm)".equals(d3.getName()) ){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Carer, Self-Harm) document, it is "+d3.getName());
        }

        DocumentOccurrence d3o1 = d3.getOccurrence(0);
        if ( !"Carer, Most serious, Baseline".equals(d3o1.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Self-Harm) - Carer, Most serious, Baseline occurrence, it is "+d3o1.getDisplayText());
        }
        d3o1.setName("Carer, Most serious, Baseline");

        DocumentOccurrence d3o2 = d3.getOccurrence(1);
        if ( !"Carer, Closest to contact, Baseline".equals(d3o2.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Self-Harm) - Carer, Closest to contact, Baseline occurrence, it is "+d3o2.getDisplayText());
        }
        d3o2.setName("Carer, Closest to contact, Baseline");

		Document d4 = ds.getDocument(15);
        if ( !"Adverse Outcomes Detailed Questionnaire (Carer, Violence)".equals(d4.getName()) ){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Carer, Violence) document, it is "+d4.getName());
        }

        DocumentOccurrence d4o1 = d4.getOccurrence(0);
        if ( !"Carer, Most serious, Baseline".equals(d4o1.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Violence) - Carer, Most serious, Baseline occurrence, it is "+d4o1.getDisplayText());
        }
        d4o1.setName("Carer, Most serious, Baseline");

        DocumentOccurrence d4o2 = d4.getOccurrence(1);
        if ( !"Carer, Closest to contact, Baseline".equals(d4o2.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Violence) - Carer, Closest to contact, Baseline occurrence, it is "+d4o2.getDisplayText());
        }
        d4o2.setName("Carer, Closest to contact, Baseline");

	}

}
