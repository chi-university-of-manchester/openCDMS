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

import java.util.Calendar;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch9 extends AbstractPatch {

	public String getName() {
		return "Add 'date of assessment' entry to PANSS, Insight, Calgary (CDSS), GAF and YMS";
	}

	public void applyPatch(DataSet ds, String saml) throws Exception {

		Factory factory = new HibernateFactory();

        DateValidationRule after2000 = factory.createDateValidationRule();
        after2000.setDescription("After 2000");
        Calendar cal = Calendar.getInstance();
        cal.set(2000, 0, 1);
        cal.clear(Calendar.MILLISECOND);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.AM_PM);
        after2000.setAbsLowerLimit(cal.getTime());
        ds.addValidationRule(after2000);

        ValidationRule notInFuture = ds.getValidationRule(14);
        if ( !"Not in future".equals(notInFuture.getDescription())){
        	throw new RuntimeException("This is not the 'Not in future' validation rule, it is "+notInFuture.getDescription());
        }

		Document panss = ds.getDocument(6);
		if ( !"PANSS".equals(panss.getName())){
			throw new RuntimeException("This is not the PANSS document, it is "+panss.getName());
		}
		DateEntry doa1 = factory.createDateEntry("Date of assessment", "Date of assessment");
		panss.addEntry(doa1);
		doa1.setSection(panss.getSection(0));
		doa1.addValidationRule(after2000);
		doa1.addValidationRule(notInFuture);

		Document ym = ds.getDocument(7);
		if ( !"Young Mania".equals(ym.getName())){
			throw new RuntimeException("This is not the Young Mania document, it is "+ym.getName());
		}
		DateEntry doa2 = factory.createDateEntry("Date of assessment", "Date of assessment");
		ym.addEntry(doa2);
		doa2.setSection(ym.getSection(0));
		doa2.addValidationRule(after2000);
		doa2.addValidationRule(notInFuture);


		Document gaf = ds.getDocument(8);
		if ( !"GAF Data Entry Sheet".equals(gaf.getName())){
			throw new RuntimeException("This is not the GAF Data Entry Sheet document, it is "+gaf.getName());
		}
		DateEntry doa3 = factory.createDateEntry("Date of assessment", "Date of assessment");
		gaf.addEntry(doa3);
		doa3.setSection(gaf.getSection(0));
		doa3.addValidationRule(after2000);
		doa3.addValidationRule(notInFuture);


		Document calgary = ds.getDocument(13);
		if ( !"Calgary".equals(calgary.getName())){
			throw new RuntimeException("This is not the Calgary document, it is "+calgary.getName());
		}
		DateEntry doa4 = factory.createDateEntry("Date of assessment", "Date of assessment");
		calgary.addEntry(doa4);
		doa4.setSection(calgary.getSection(0));
		doa4.addValidationRule(after2000);
		doa4.addValidationRule(notInFuture);


		Document insight = ds.getDocument(18);
		if ( !"Insight Scale Scoring".equals(insight.getName())){
			throw new RuntimeException("This is not the Insight Scale Scoring document, it is "+insight.getName());
		}
		DateEntry doa5 = factory.createDateEntry("Date of assessment", "Date of assessment");
		insight.addEntry(doa5);
		doa5.setSection(insight.getSection(0));
		doa5.addValidationRule(after2000);
		doa5.addValidationRule(notInFuture);


	}

}
