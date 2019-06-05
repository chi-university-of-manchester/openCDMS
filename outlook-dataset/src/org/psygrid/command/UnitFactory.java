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


package org.psygrid.command;

import org.psygrid.common.IUnitFactory;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Unit;

/**
 * @author Rob Harper
 *
 */
public class UnitFactory implements IUnitFactory {

	public Units makeInstance() {
		return new Units();
	}

	private class Units extends org.psygrid.common.Units {
		private Units(){
			super();
		}

	    public void init(Factory factory, DataSet dataSet) {

	    	unitsMap.put("%", factory.createUnit("%"));
	        unitsMap.put("mg", factory.createUnit("mg"));
	        unitsMap.put("ml", factory.createUnit("ml"));
	        unitsMap.put("units", factory.createUnit("units"));
	        unitsMap.put("tablets", factory.createUnit("tablets"));
	        unitsMap.put("ft", factory.createUnit("ft"));
	        unitsMap.put("hrs", factory.createUnit("hrs"));
	        unitsMap.put("months", factory.createUnit("months"));
	        unitsMap.put("gbp", factory.createUnit("gbp"));
	        unitsMap.put("mins", factory.createUnit("mins"));
	        unitsMap.put("weeks", factory.createUnit("weeks"));

	        for (Unit unit : unitsMap.values()) {
	            dataSet.addUnit(unit);
	        }
	    }
	}

}
