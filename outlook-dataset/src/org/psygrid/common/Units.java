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


package org.psygrid.common;

import java.util.HashMap;
import java.util.Map;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Unit;

/**
 * @author Rob Harper
 *
 */
public class Units {

    protected Map<String, Unit> unitsMap;

	protected Units() {
		unitsMap = new HashMap<String, Unit>();
	}

	static private Units _instance = null;

	static public Units instance() {
		if(null == _instance) {
			_instance = new Units();
		}
		return _instance;
	}

	public void init(Factory factory, DataSet dataSet){

	}

    public final Unit getUnit(String abbreviation) {
        return unitsMap.get(abbreviation);
    }

}
