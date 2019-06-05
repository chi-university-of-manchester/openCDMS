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
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.NarrativeEntry;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch27 extends AbstractPatch {

	@Override
	public String getName() {
		return "Update entry names for export";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		//Drug Check
		{
			Document doc = ds.getDocument(10);
	        if ( !"Drug Check".equals(doc.getName()) ){
	            throw new RuntimeException("This is not the Drug Check document - it is "+doc.getName());
	        }

	        Entry e1 = doc.getEntry(1);
	        if ( !"Sleeping tablets or sedatives? (like valium or normison)".equals(e1.getDisplayText())){
	        	throw new RuntimeException("This is not the 'Sleeping tablets or sedatives? (like valium or normison)' entry, it is '"+e1.getDisplayText()+"'");
	        }
	        e1.setName("Had sedatives");

	        Entry e2 = doc.getEntry(2);
	        if ( !"Frequency".equals(e2.getName())){
	        	throw new RuntimeException("This is not the 'Frequency' entry (sedatives), it is '"+e2.getName()+"'");
	        }
	        e2.setName("Sedatives how often");

	        Entry e3 = doc.getEntry(3);
	        if ( !"Amount per week".equals(e3.getName())){
	        	throw new RuntimeException("This is not the 'Amount per week' entry (sedatives), it is '"+e3.getName()+"'");
	        }
	        e3.setName("Sedatives cost");

	        Entry e4 = doc.getEntry(4);
	        if ( !"Daily frequency sedatives".equals(e4.getName())){
	        	throw new RuntimeException("This is not the 'Daily frequency sedatives' entry (sedatives), it is '"+e4.getName()+"'");
	        }
	        e4.setName("Sedatives daily frequency");

	        Entry e5 = doc.getEntry(5);
	        if ( !"Amount per week".equals(e5.getName())){
	        	throw new RuntimeException("This is not the 'Amount per week' entry (sedatives), it is '"+e5.getName()+"'");
	        }
	        e5.setName("Sedatives quantity");

	        Entry e7 = doc.getEntry(7);
	        if ( !"Marijuana, cannabis, or hash?".equals(e7.getDisplayText())){
	        	throw new RuntimeException("This is not the 'Marijuana, cannabis, or hash?' entry, it is '"+e7.getDisplayText()+"'");
	        }
	        e7.setName("Had marijuana");

	        Entry e8 = doc.getEntry(8);
	        if ( !"Frequency".equals(e8.getName())){
	        	throw new RuntimeException("This is not the 'Frequency' entry (marijuana), it is '"+e8.getName()+"'");
	        }
	        e8.setName("Marijuana how often");

	        Entry e9 = doc.getEntry(9);
	        if ( !"Amount per week".equals(e9.getName())){
	        	throw new RuntimeException("This is not the 'Amount per week' entry (marijuana), it is '"+e9.getName()+"'");
	        }
	        e9.setName("Marijuana cost");

	        Entry e10 = doc.getEntry(10);
	        if ( !"Quantity".equals(e10.getName())){
	        	throw new RuntimeException("This is not the 'Quantity' entry (marijuana), it is '"+e10.getName()+"'");
	        }
	        e10.setName("Marijuana quantity");

	        Entry e11 = doc.getEntry(11);
	        if ( !"Drugs you sniff, like petrol/glue?".equals(e11.getDisplayText())){
	        	throw new RuntimeException("This is not the 'Drugs you sniff, like petrol/glue?' entry, it is '"+e11.getDisplayText()+"'");
	        }
	        e11.setName("Had drugs you sniff");

	        Entry e12 = doc.getEntry(12);
	        if ( !"Frequency".equals(e12.getName())){
	        	throw new RuntimeException("This is not the 'Frequency' entry (sniff), it is '"+e12.getName()+"'");
	        }
	        e12.setName("Drugs you sniff how often");

	        Entry e13 = doc.getEntry(13);
	        if ( !"Amount per week".equals(e13.getName())){
	        	throw new RuntimeException("This is not the 'Amount per week' entry (sniff), it is '"+e13.getName()+"'");
	        }
	        e13.setName("Drugs you sniff cost");

	        Entry e14 = doc.getEntry(14);
	        if ( !"Type".equals(e14.getName())){
	        	throw new RuntimeException("This is not the 'Type' entry (sniff), it is '"+e14.getName()+"'");
	        }
	        e14.setName("Drugs you sniff type");

	        Entry e15 = doc.getEntry(15);
	        if ( !"Drugs like LSD?".equals(e15.getDisplayText())){
	        	throw new RuntimeException("This is not the 'Drugs like LSD?' entry, it is '"+e15.getDisplayText()+"'");
	        }
	        e15.setName("Had LSD");

	        Entry e16 = doc.getEntry(16);
	        if ( !"Frequency".equals(e16.getName())){
	        	throw new RuntimeException("This is not the 'Frequency' entry (LSD), it is '"+e16.getName()+"'");
	        }
	        e16.setName("LSD how often");

	        Entry e17 = doc.getEntry(17);
	        if ( !"Amount per week".equals(e17.getName())){
	        	throw new RuntimeException("This is not the 'Amount per week' entry (LSD), it is '"+e17.getName()+"'");
	        }
	        e17.setName("LSD cost");

	        Entry e18 = doc.getEntry(18);
	        if ( !"Quantity".equals(e18.getName())){
	        	throw new RuntimeException("This is not the 'Quantity' entry (LSD), it is '"+e18.getName()+"'");
	        }
	        e18.setName("LSD quantity");

	        Entry e19 = doc.getEntry(19);
	        if ( !"Speed, ecstasy, crack or cocaine?".equals(e19.getDisplayText())){
	        	throw new RuntimeException("This is not the 'Speed, ecstasy, crack or cocaine?' entry, it is '"+e19.getDisplayText()+"'");
	        }
	        e19.setName("Had speed");

	        Entry e20 = doc.getEntry(20);
	        if ( !"Frequency".equals(e20.getName())){
	        	throw new RuntimeException("This is not the 'Frequency' entry (speed), it is '"+e20.getName()+"'");
	        }
	        e20.setName("Speed how often");

	        Entry e21 = doc.getEntry(21);
	        if ( !"Amount per week".equals(e21.getName())){
	        	throw new RuntimeException("This is not the 'Amount per week' entry (speed), it is '"+e21.getName()+"'");
	        }
	        e21.setName("Speed cost");

	        Entry e22 = doc.getEntry(22);
	        if ( !"Quantity".equals(e22.getName())){
	        	throw new RuntimeException("This is not the 'Quantity' entry (speed), it is '"+e22.getName()+"'");
	        }
	        e22.setName("Speed quantity");

	        Entry e23 = doc.getEntry(23);
	        if ( !"Type".equals(e23.getName())){
	        	throw new RuntimeException("This is not the 'Type' entry (speed), it is '"+e23.getName()+"'");
	        }
	        e23.setName("Speed type");

	        Entry e24 = doc.getEntry(24);
	        if ( !"Heroin, morphine or methadone?".equals(e24.getDisplayText())){
	        	throw new RuntimeException("This is not the 'Heroin, morphine or methadone?' entry, it is '"+e24.getDisplayText()+"'");
	        }
	        e24.setName("Had heroin");

	        Entry e25 = doc.getEntry(25);
	        if ( !"Frequency".equals(e25.getName())){
	        	throw new RuntimeException("This is not the 'Frequency' entry (heroin), it is '"+e25.getName()+"'");
	        }
	        e25.setName("Heroin how often");

	        Entry e26 = doc.getEntry(26);
	        if ( !"Amount per week".equals(e26.getName())){
	        	throw new RuntimeException("This is not the 'Amount per week' entry (heroin), it is '"+e26.getName()+"'");
	        }
	        e26.setName("Heroin cost");

	        Entry e27 = doc.getEntry(27);
	        if ( !"Quantity".equals(e27.getName())){
	        	throw new RuntimeException("This is not the 'Quantity' entry (heroin), it is '"+e27.getName()+"'");
	        }
	        e27.setName("Heroin quantity");

	        Entry e28 = doc.getEntry(28);
	        if ( !"Type".equals(e28.getName())){
	        	throw new RuntimeException("This is not the 'Type' entry (heroin), it is '"+e28.getName()+"'");
	        }
	        e28.setName("Heroin type");

	        Entry e54 = doc.getEntry(54);
	        if ( !"Do you want to change your use of (substance) right now?".equals(e54.getDisplayText())){
	        	throw new RuntimeException("This is not the 'Do you want to change your use of (substance) right now?' entry, it is '"+e54.getDisplayText()+"'");
	        }
			e54.setName("Readiness to change");

	        Entry e55 = doc.getEntry(55);
	        if ( !"Do you think you could change your use of (substance) now if you wanted to?".equals(e55.getDisplayText())){
	        	throw new RuntimeException("This is not the 'Do you think you could change your use of (substance) now if you wanted to?' entry, it is '"+e55.getDisplayText()+"'");
	        }
			e55.setName("Confidence to change");
		}

		//PreMorbid
		{
			Document doc = ds.getDocument(12);
			if ( !"Premorbid Summary Sheet".equals(doc.getName())){
				throw new RuntimeException("This is not the Premorbid Summary Sheet document - it is "+doc.getName());
			}

			//"Sub sections" are denoted by Narrative Entries - for all sub sections other
			//then "General" append the name of the sub section to the end of the entry name
			String suffix = null;
			for ( int i=0; i<doc.numEntries(); i++ ){
				Entry e = doc.getEntry(i);
				if ( e instanceof NarrativeEntry){
					suffix = e.getName();
				}
				else{
					if ( !suffix.equals("General")){
						e.setName(e.getName()+" ("+suffix+")");
					}
				}
			}
		}

		//Insight
		{
			Document doc = ds.getDocument(18);
			if ( !"Insight Scale Scoring".equals(doc.getName())){
				throw new RuntimeException("This is not the Insight Scale Scoring document - it is "+doc.getName());
			}

	        Entry e1 = doc.getEntry(7);
	        if ( !"None of the unusual things I experienced are due to an illness".equals(e1.getDisplayText())){
	        	throw new RuntimeException("This is not the 'None of the unusual things I experienced are due to an illness' entry, it is '"+e1.getDisplayText()+"'");
	        }
			e1.setName("Not due to illness");
		}

	}

}
