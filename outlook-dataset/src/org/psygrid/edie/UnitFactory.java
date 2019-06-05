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


package org.psygrid.edie;

import org.psygrid.common.IUnitFactory;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Unit;

/**
 * @author Rob Harper
 *
 */
public class UnitFactory implements IUnitFactory {

	/* (non-Javadoc)
	 * @see org.psygrid.common.IUnitFactory#makeInstance()
	 */
	public Units makeInstance() {
		// TODO Auto-generated method stub
		return new Units();
	}

	private class Units extends org.psygrid.common.Units {
		private Units(){
			super();
		}

	    public void init(Factory factory, DataSet dataSet) {

	        unitsMap.put("days", factory.createUnit("days"));
	        unitsMap.put("mg", factory.createUnit("mg"));
	        unitsMap.put("weeks", factory.createUnit("weeks"));
	        unitsMap.put("months", factory.createUnit("months"));
	        unitsMap.put("years", factory.createUnit("years"));
	        unitsMap.put("hours", factory.createUnit("hours"));
	        unitsMap.put("mins", factory.createUnit("mins"));
	        unitsMap.put("sec", factory.createUnit("sec"));
	        unitsMap.put("kg", factory.createUnit("kg"));
	        unitsMap.put("Appointment", factory.createUnit("Appointment"));
	        unitsMap.put("Day attendance", factory.createUnit("Day attendance"));
	        unitsMap.put("cones per day", factory.createUnit("cones per day"));
	        unitsMap.put("spliffs per day", factory.createUnit("spliffs per day"));
	        unitsMap.put("joints per day", factory.createUnit("joints per day"));
	        unitsMap.put("trips per week", factory.createUnit("trips per week"));
	        unitsMap.put("mg per day", factory.createUnit("mg per day"));
	        unitsMap.put("tabs per day", factory.createUnit("tabs per day"));
	        unitsMap.put("g per week", factory.createUnit("g per day"));
	        unitsMap.put("ml per day", factory.createUnit("ml per day"));
	        unitsMap.put("hits per day", factory.createUnit("hits per day"));
	        unitsMap.put("tabs per week", factory.createUnit("tabs per week"));
	        unitsMap.put("g per day", factory.createUnit("g per day"));
	        unitsMap.put("hits per week", factory.createUnit("hits per week"));
	        unitsMap.put("gbp", factory.createUnit("GBP"));
	        unitsMap.put("mg/day", factory.createUnit("mg/day"));
	        unitsMap.put("mg/week", factory.createUnit("mg/week"));
	        unitsMap.put("times a day", factory.createUnit("times a day"));
	        unitsMap.put("times a week", factory.createUnit("times a week"));
	        unitsMap.put("tabs", factory.createUnit("tabs"));
	        unitsMap.put("ml", factory.createUnit("ml"));
	        unitsMap.put("cans per day", factory.createUnit("cans per day"));
	        unitsMap.put("cups per day", factory.createUnit("cups per day"));
	        unitsMap.put("cigs per day", factory.createUnit("cigs per day"));

	        for (Unit unit : unitsMap.values()) {
	            dataSet.addUnit(unit);
	        }
	    }
	}

}
