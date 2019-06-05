/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.outlook.patches.v1_6_3;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch66 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		
		Factory factory = new HibernateFactory();
		Unit ml = factory.createUnit("ml");
		ds.addUnit(ml);
		
		{
			//Baseline
			Document cssri = ds.getDocument(22);
			if ( !"CSSRI".equals(cssri.getName())){
				throw new RuntimeException("This is not the Baseline CSSRI document, it is "+cssri.getName());
			}
			
			CompositeEntry drugs = (CompositeEntry)cssri.getEntry(41);
			if ( !"Drugs".equals(drugs.getName())){
				throw new RuntimeException("This is not the Baseline Drugs composite, it is "+drugs.getName());
			}
			
			BasicEntry dosage = (BasicEntry)drugs.getEntry(1);
			if ( !"Dosage".equals(dosage.getName())){
				throw new RuntimeException("This is not the Baseline Dosage entry, it is "+dosage.getName());
			}
			
			dosage.addUnit(ml);
		}
		
		{
			//Follow up
			Document cssri = ds.getDocument(27);
			if ( !"CSSRI".equals(cssri.getName())){
				throw new RuntimeException("This is not the Follow Up CSSRI document, it is "+cssri.getName());
			}
			
			CompositeEntry drugs = (CompositeEntry)cssri.getEntry(50);
			if ( !"Drugs".equals(drugs.getName())){
				throw new RuntimeException("This is not the Follow Up Drugs composite, it is "+drugs.getName());
			}
			
			BasicEntry dosage = (BasicEntry)drugs.getEntry(1);
			if ( !"Dosage".equals(dosage.getName())){
				throw new RuntimeException("This is not the Follow Up Dosage entry, it is "+dosage.getName());
			}
			
			dosage.addUnit(ml);
		}
	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Add ml unit to CSSRI Follow Up - Drugs - Dosage";
	}

}
