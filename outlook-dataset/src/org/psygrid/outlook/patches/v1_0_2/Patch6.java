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

package org.psygrid.outlook.patches.v1_0_2;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch6 extends AbstractPatch {

    public String getName() {
        return "Add 'Unclear' options to group C and D questions and change Inclusion criteria formula";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {
        Factory factory = new HibernateFactory();
        Document doc = ds.getDocument(3);
        if ( !"WHO Screening Schedule".equals(doc.getName()) ){
            throw new RuntimeException("This is not the WHO Screening Schedule, it is "+doc.getName());
        }

        int[] indices = new int[]{7, 8, 9, 10, 11, 13, 14, 15, 16, 17};
        String[] names = new String[]{"Hallucinations",
                                      "Delusions",
                                      "Thought and Speech Disorder",
                                      "Psychomotor disorder",
                                      "Inappropriate Behaviour",
                                      "Loss of interests",
                                      "Social withdrawl",
                                      "Aggression",
                                      "Anxiety",
                                      "Self neglect"};

        for ( int i=0; i<indices.length; i++ ){
            OptionEntry oe = (OptionEntry)doc.getEntry(indices[i]);
            if ( !names[i].equals(oe.getName())){
                throw new RuntimeException("This is not the "+names[i]+" entry, it is "+oe.getName());
            }
            oe.addOption(factory.createOption("Unclear", 2));
        }

        DerivedEntry de = (DerivedEntry)doc.getEntry(19);
        if ( !"Satisfies criteria".equals(de.getName())){
            throw new RuntimeException("This is not the Satisfies criteria entry, it is "+de.getName());
        }
        de.setFormula(
            "if(((a1+a2+b1+b2+e)==0)&&"+
            "(((if(c1==1,1,0)+if(c2==1,1,0)+if(c3==1,1,0)+if(c4==1,1,0)+if(c5==1,1,0))>=1)||"+
            "((if(d1==1,1,0)+if(d2==1,1,0)+if(d3==1,1,0)+if(d4==1,1,0)+if(d5==1,1,0))>=2)), 1, 0)");

    }

}
