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


package org.psygrid.outlook.patches.v1_1_6;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch21 extends AbstractPatch {

	public String getName() {
		return "Correct Adverse Outcomes occurrence names";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {
		Document d1 = ds.getDocument(19);
        if ( !"Adverse Outcomes Detailed Questionnaire (Self-Harm)".equals(d1.getName()) ){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Self-Harm) document, it is "+d1.getName());
        }

        DocumentOccurrence d1o1 = d1.getOccurrence(0);
        if ( !"Most serious, Baseline".equals(d1o1.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Self-Harm) - Most serious, Baseline occurrence, it is "+d1o1.getDisplayText());
        }
        d1o1.setName("Most serious, Baseline");

        DocumentOccurrence d1o2 = d1.getOccurrence(1);
        if ( !"Closest to contact, Baseline".equals(d1o2.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Self-Harm) - Closest to contact, Baseline occurrence, it is "+d1o2.getDisplayText());
        }
        d1o2.setName("Closest to contact, Baseline");

		Document d2 = ds.getDocument(20);
        if ( !"Adverse Outcomes Detailed Questionnaire (Violence)".equals(d2.getName()) ){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Violence) document, it is "+d2.getName());
        }

        DocumentOccurrence d2o1 = d2.getOccurrence(0);
        if ( !"Most serious, Baseline".equals(d2o1.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Violence) - Most serious, Baseline occurrence, it is "+d2o1.getDisplayText());
        }
        d2o1.setName("Most serious, Baseline");

        DocumentOccurrence d2o2 = d2.getOccurrence(1);
        if ( !"Closest to contact, Baseline".equals(d2o2.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Violence) - Closest to contact, Baseline occurrence, it is "+d2o2.getDisplayText());
        }
        d2o2.setName("Closest to contact, Baseline");

		Document d3 = ds.getDocument(21);
        if ( !"Adverse Outcomes Screening Questionnaire".equals(d3.getName()) ){
            throw new RuntimeException("This is not the Adverse Outcomes Screening Questionnaire document, it is "+d3.getName());
        }

        DocumentOccurrence d3o1 = d3.getOccurrence(0);
        if ( !"Closet to Contact, Baseline".equals(d3o1.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Screening Questionnaire - Closet to Contact, Baseline occurrence, it is "+d3o1.getDisplayText());
        }
        d3o1.setName("Closest to contact, Baseline");
        d3o1.setDisplayText("Closest to contact, Baseline");

        DocumentOccurrence d3o2 = d3.getOccurrence(1);
        if ( !"Most Serious, Baseline".equals(d3o2.getDisplayText())){
            throw new RuntimeException("This is not the Adverse Outcomes Screening Questionnaire - Most Serious, Baseline occurrence, it is "+d3o2.getDisplayText());
        }
        d3o2.setName("Most Serious, Baseline");

	}

}
