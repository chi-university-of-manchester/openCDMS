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


package org.psygrid.drn.address;

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

	        unitsMap.put("mm/Hg", factory.createUnit("mm/Hg"));
	        unitsMap.put("kg", factory.createUnit("kg"));
	        unitsMap.put("m", factory.createUnit("m"));
	        unitsMap.put("cm", factory.createUnit("cm"));
	        unitsMap.put("cm", factory.createUnit("cm"));
	        unitsMap.put("umol/L", factory.createUnit("\u00B5mol/L"));
	        unitsMap.put("mmol/L", factory.createUnit("mmol/L"));
	        unitsMap.put("%", factory.createUnit("%"));
	        unitsMap.put("U/L", factory.createUnit("U/L"));
	        unitsMap.put("g/L", factory.createUnit("g/L"));
	        unitsMap.put("mlU/L", factory.createUnit("mlU/L"));
	        unitsMap.put("pmol/L", factory.createUnit("pmol/L"));
	        unitsMap.put("mg/L", factory.createUnit("mg/L"));
	        unitsMap.put("min", factory.createUnit("min"));
	        unitsMap.put("mg", factory.createUnit("mg"));
	        unitsMap.put("mg/dL", factory.createUnit("mg/dL"));

	        for (Unit unit : unitsMap.values()) {
	            dataSet.addUnit(unit);
	        }
	    }
	}

}
