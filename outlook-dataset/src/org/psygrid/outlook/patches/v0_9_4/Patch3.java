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

import java.util.Calendar;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch3 extends AbstractPatch {

    public String getName() {
        return "Add 'after 1900' validation rule to all date entries";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        //create the new validation rule
        Factory factory = new HibernateFactory();
        DateValidationRule after1900 = factory.createDateValidationRule();
        after1900.setDescription("After 1900");
        Calendar cal = Calendar.getInstance();
        cal.set(1900, 0, 1);
        after1900.setAbsLowerLimit(cal.getTime());
        ds.addValidationRule(after1900);

        {
            Document baf = ds.getDocument(0);
            if ( !"Baseline Audit".equals(baf.getName().trim()) ){
                throw new RuntimeException("This is not the Baseline Audit document - it is "+baf.getName());
            }
            DateEntry bafDe1 = (DateEntry)baf.getEntry(4);
            bafDe1.addValidationRule(after1900);
            DateEntry bafDe2 = (DateEntry)baf.getEntry(5);
            bafDe2.addValidationRule(after1900);
            DateEntry bafDe3 = (DateEntry)baf.getEntry(13);
            bafDe3.addValidationRule(after1900);
        }

        {
            Document personalDetails = ds.getDocument(3);
            if ( !"Personal Details".equals(personalDetails.getName().trim()) ){
                throw new RuntimeException("This is not the Personal Details document - it is "+personalDetails.getName());
            }
            DateEntry pdDe1 = (DateEntry)personalDetails.getEntry(0);
            pdDe1.addValidationRule(after1900);
        }

        {
            Document adOutSelfHarm = ds.getDocument(17);
            if ( !"Adverse Outcomes Detailed Questionnaire (Self-Harm)".equals(adOutSelfHarm.getName().trim()) ){
                throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Self-Harm) document - it is "+adOutSelfHarm.getName());
            }
            DateEntry aoshDe1 = (DateEntry)adOutSelfHarm.getEntry(0);
            aoshDe1.addValidationRule(after1900);
        }

        {
            Document adOutViolence = ds.getDocument(18);
            if ( !"Adverse Outcomes Detailed Questionnaire (Violence)".equals(adOutViolence.getName().trim()) ){
                throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Violence) document - it is "+adOutViolence.getName());
            }
            DateEntry aovDe1 = (DateEntry)adOutViolence.getEntry(0);
            aovDe1.addValidationRule(after1900);
        }

        {
            Document dup = ds.getDocument(7);
            if ( !"DUP".equals(dup.getName().trim()) ){
                throw new RuntimeException("This is not the DUP document - it is "+dup.getName());
            }
            DateEntry dupDe1 = (DateEntry)dup.getEntry(0);
            dupDe1.addValidationRule(after1900);
            DateEntry dupDe2 = (DateEntry)dup.getEntry(1);
            dupDe2.addValidationRule(after1900);
            DateEntry dupDe3 = (DateEntry)dup.getEntry(2);
            dupDe3.addValidationRule(after1900);
            DateEntry dupDe4 = (DateEntry)dup.getEntry(3);
            dupDe4.addValidationRule(after1900);
            DateEntry dupDe5 = (DateEntry)dup.getEntry(4);
            dupDe5.addValidationRule(after1900);
            DateEntry dupDe6 = (DateEntry)dup.getEntry(5);
            dupDe6.addValidationRule(after1900);
        }

        {
            Document pathways = ds.getDocument(9);
            if ( !"PathwaysToCare".equals(pathways.getName().trim()) ){
                throw new RuntimeException("This is not the PathwaysToCare document - it is "+pathways.getName());
            }
            DateEntry pathDe1 = (DateEntry)pathways.getEntry(4);
            pathDe1.addValidationRule(after1900);
        }

        {
            Document consentInfoForm = ds.getDocument(2);
            if ( !"Interview and consent information form".equals(consentInfoForm.getName().trim()) ){
                throw new RuntimeException("This is not the Interview and consent information form document - it is "+consentInfoForm.getName());
            }
            DateEntry cifDe1 = (DateEntry)consentInfoForm.getEntry(0);
            cifDe1.addValidationRule(after1900);
            DateEntry cifDe2 = (DateEntry)consentInfoForm.getEntry(1);
            cifDe2.addValidationRule(after1900);
            DateEntry cifDe3 = (DateEntry)consentInfoForm.getEntry(2);
            cifDe3.addValidationRule(after1900);
            DateEntry cifDe4 = (DateEntry)consentInfoForm.getEntry(4);
            cifDe4.addValidationRule(after1900);
        }

        {
            Document persDetailsFU = ds.getDocument(22);
            if ( !"Personal Details Follow Up".equals(persDetailsFU.getName().trim()) ){
                throw new RuntimeException("This is not the Personal Details Follow Up document - it is "+persDetailsFU.getName());
            }
            DateEntry pdfuDe1 = (DateEntry)persDetailsFU.getEntry(3);
            pdfuDe1.addValidationRule(after1900);
        }

        {
            Document timeUseInterview = ds.getDocument(20);
            if ( !"Time Use Interview".equals(timeUseInterview.getName().trim()) ){
                throw new RuntimeException("This is not the Time Use Interview document - it is "+timeUseInterview.getName());
            }
            DateEntry tuiDe1 = (DateEntry)timeUseInterview.getEntry(7);
            tuiDe1.addValidationRule(after1900);
            DateEntry tuiDe2 = (DateEntry)timeUseInterview.getEntry(61);
            tuiDe2.addValidationRule(after1900);
        }
    }

}
