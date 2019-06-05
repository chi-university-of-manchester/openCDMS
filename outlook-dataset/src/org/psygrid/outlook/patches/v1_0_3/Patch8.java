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

package org.psygrid.outlook.patches.v1_0_3;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch8 extends AbstractPatch {

    public String getName() {
        return "ANNSERS - fix for M/F specific questions to allow calculations to proceed";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();

        Document doc = ds.getDocument(17);
        if ( !"ANNSERS".equals(doc.getName()) ){
            throw new RuntimeException("This is not the ANNSERS document, it is "+doc.getName());
        }

        OptionEntry e1 = (OptionEntry)doc.getEntry(137);
        if ( !"Problems of sexual arousal - Severity".equals(e1.getName()) ){
            throw new RuntimeException("This is not the 'Problems of sexual arousal - Severity' entry, it is "+e1.getName());
        }
        e1.addOption(factory.createOption("Male patient - not applicable",-1));

        OptionEntry e2 = (OptionEntry)doc.getEntry(141);
        if ( !"Erectile difficulties - Severity".equals(e2.getName()) ){
            throw new RuntimeException("This is not the 'Erectile difficulties - Severity' entry, it is "+e2.getName());
        }
        e2.addOption(factory.createOption("Female patient - not applicable",-1));

        OptionEntry e3 = (OptionEntry)doc.getEntry(145);
        if ( !"Orgasmic difficulties - Severity".equals(e3.getName()) ){
            throw new RuntimeException("This is not the 'Orgasmic difficulties - Severity' entry, it is "+e3.getName());
        }
        e3.addOption(factory.createOption("Male patient - not applicable",-1));

        OptionEntry e4 = (OptionEntry)doc.getEntry(149);
        if ( !"Delayed ejaculation - Severity".equals(e4.getName()) ){
            throw new RuntimeException("This is not the 'Delayed ejaculation - Severity' entry, it is "+e4.getName());
        }
        e4.addOption(factory.createOption("Female patient - not applicable",-1));

        OptionEntry e5 = (OptionEntry)doc.getEntry(153);
        if ( !"Change in menstruation - Severity".equals(e5.getName()) ){
            throw new RuntimeException("This is not the 'Change in menstruation - Severity' entry, it is "+e5.getName());
        }
        e5.addOption(factory.createOption("Male patient - not applicable",-1));

        OptionEntry e6 = (OptionEntry)doc.getEntry(157);
        if ( !"Reduction in ejaculation volume/intensity - Severity".equals(e6.getName()) ){
            throw new RuntimeException("This is not the 'Reduction in ejaculation volume/intensity - Severity' entry, it is "+e6.getName());
        }
        e6.addOption(factory.createOption("Female patient - not applicable",-1));

        DerivedEntry de1 = (DerivedEntry)doc.getEntry(201);
        if ( !"Number of severe side effects".equals(de1.getName()) ){
            throw new RuntimeException("This is not the 'Number of severe side effects' entry, it is "+de1.getName());
        }
        de1.setFormula("if(a0==3,1,0)+if(a1==3,1,0)+if(a2==3,1,0)+if(a3==3,1,0)+if(a4==3,1,0)+if(a5==3,1,0)+if(a6==3,1,0)+"+
                       "if(a7==3,1,0)+if(a8==3,1,0)+if(a9==3,1,0)+if(a10==3,1,0)+if(a11==3,1,0)+if(a12==3,1,0)+if(a13==3,1,0)+"+
                       "if(a14==3,1,0)+if(a15==3,1,0)+if(a16==3,1,0)+if(a17==3,1,0)+if(a18==3,1,0)+if(a19==3,1,0)+if(a20==3,1,0)+"+
                       "if(a21==3,1,0)+if(a22==3,1,0)+if(a23==3,1,0)+if(a24==3,1,0)+if(a25==3,1,0)+if(a26==3,1,0)+if(a27==3,1,0)+"+
                       "if(a28==3,1,0)+if(a29==3,1,0)+if(a30==3,1,0)+if(a31==3,1,0)+if(a32==3,1,0)+if(a33==3,1,0)+if(a34==3,1,0)+"+
                       "if(a35==3,1,0)+if(a36==3,1,0)+if(a37==3,1,0)+if(a38==3,1,0)+if(a39==3,1,0)+if(a40==3,1,0)+if(a41==3,1,0)+"+
                       "if(a42==3,1,0)+if(a43==3,1,0)+if(a44==3,1,0)+if(a45==3,1,0)+if(a46==3,1,0)");

        DerivedEntry de = (DerivedEntry)doc.getEntry(202);
        if ( !"Total score".equals(de.getName()) ){
            throw new RuntimeException("This is not the 'Total score' entry, it is "+de.getName());
        }
        de.setFormula("if(a0,a0,0)+if(a1,a1,0)+if(a2,a2,0)+if(a3,a3,0)+if(a4,a4,0)+if(a5,a5,0)+if(a6,a6,0)+if(a7,a7,0)+"+
                      "if(a8,a8,0)+if(a9,a9,0)+if(a10,a10,0)+if(a11,a11,0)+if(a12,a12,0)+if(a13,a13,0)+if(a14,a14,0)+"+
                      "if(a15,a15,0)+if(a16,a16,0)+if(a17,a17,0)+if(a18,a18,0)+if(a19,a19,0)+if(a20,a20,0)+if(a21,a21,0)+"+
                      "if(a22,a22,0)+if(a23,a23,0)+if(a24,a24,0)+if(a25,a25,0)+if(a26,a26,0)+if(a27,a27,0)+if(a28,a28,0)+"+
                      "if(a29,a29,0)+if(a30,a30,0)+if(a31,a31,0)+if(a32,a32,0)+if(a33,a33,0)+if(a34,a34,0)+if(a35,a35,0)+"+
                      "if(a36,a36,0)+if(a37,a37,0)+if(a38,a38,0)+if(a39,a39,0)+if(a40,a40,0)+if(a41,a41,0)+if(a42,a42,0)+"+
                      "if(a43,a43,0)+if(a44,a44,0)+if(a45,a45,0)+if(a46,a46,0)");
    }

}
