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

package org.psygrid.outlook.patches.v0_9_4;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch5 extends AbstractPatch {

    public String getName() {
        return "Change inclusion criteria to 14-35; add new consent/assent forms";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        {
            Document baf = ds.getDocument(0);
            if ( !"Baseline Audit".equals(baf.getName())){
                throw new RuntimeException("This is not the Baseline Audit form - it is "+baf.getName());
            }

            Entry e1 = baf.getEntry(2);
            if ( !"Age 16-35".equals(e1.getName())){
                throw new RuntimeException("This is not the Age 16-35 entry - it is "+e1.getName());
            }
            e1.setName("Age 14-35");
            e1.setDisplayText("Age 14-35?");

            Entry e2 = baf.getEntry(24);
            if ( !"Age 16-35".equals(e2.getName())){
                throw new RuntimeException("This is not the Age 16-35 entry - it is "+e2.getName());
            }
            e2.setName("Age 14-35");
            e2.setDisplayText("Age 14-35");
        }

        {
            Document who = ds.getDocument(1);
            if ( !"WHO Screening Schedule".equals(who.getName())){
                throw new RuntimeException("This is not the Baseline Audit form - it is "+who.getName());
            }

            Entry e3 = who.getEntry(1);
            if ( !"Patient's age".equals(e3.getName())){
                throw new RuntimeException("This is not the Patient's age entry - it is "+e3.getName());
            }
            e3.setDisplayText("Is this patient's age below 14 or above 35?");
        }

        ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
        if ( !"Main client consent".equals(cfg.getDescription())){
            throw new RuntimeException("This is not the Main Client Consent consent form group - it is "+cfg.getDescription());
        }
        PrimaryConsentForm pcf = cfg.getConsentForm(0);
        if ( !"Has the client agreed to take part in the study?".equals(pcf.getQuestion())){
            throw new RuntimeException("This is not the 'Has the client agreed to take part in the study?' pcf - it is "+pcf.getQuestion());
        }
        pcf.setQuestion("Has the client (aged over 16 years) agreed to take part in the study?");

        Factory factory = new HibernateFactory();
        PrimaryConsentForm pcf2 = factory.createPrimaryConsentForm();
        pcf2.setQuestion("Has the client (aged 16 years or under) agreed to take part in the study?");
        cfg.addConsentForm(pcf2);
        AssociatedConsentForm acf = factory.createAssociatedConsentForm();
        acf.setQuestion("Has the client's relative/legal guardian agreed to the client taking part in the study?");
        pcf2.addAssociatedConsentForm(acf);

    }

}
